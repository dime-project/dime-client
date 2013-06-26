package eu.dime.mobile.service;

import eu.dime.model.file.CrawlData;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.widget.Toast;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import eu.dime.mobile.datamining.ContactCrawlerHelper;
import eu.dime.mobile.datamining.FileCrawlerHelper;
import eu.dime.mobile.datamining.semantic.vocabulary.NCO;
import eu.dime.mobile.datamining.semantic.vocabulary.NIE;
import java.io.File;
import java.io.FilenameFilter;
import java.io.StringWriter;
import java.util.Timer;
import java.util.TimerTask;

public class DataMiningService extends Service {

    // Tag for logging
    private static final String TAG = "DataminingService";
    private static final String ROOT_FOLDER = "/";
    // Timer to schedule the crawling task
    Timer timer = null;
    // Filename filter to reject 
    FilenameFilter filenameFilter;
    // Is the service crawling files and contacts right now?
    static boolean isCrawling = false;
    // has an order to stop crawling been issued?
    static boolean stopCrawling = false;
    // Stats
    static int totalFilesCrawled = 0;
    static int totalFoldersCrawled = 0;

    @Override
    public void onCreate() {
        Toast.makeText(this, "Crawling Service Created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");
        Settings.readPreferences(getApplicationContext());
        // Using a contacts observer proved not useful, because every time a contact is 'contacted' (called, SMSed, etc), the observer gets called,
        // thus triggering lots of unnecessary contact list crawlings
        // contacts will get crawled at the same time as files
        //contactsObserver = new ContactsObserver( new Handler());
        //this.getApplicationContext().getContentResolver().registerContentObserver (ContactsContract.Contacts.CONTENT_URI, true, contactsObserver);
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Crawling Service Stopped", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onDestroy");
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        stopCrawling = true;



        Settings.savePreferences(getApplicationContext());
        //this.getApplicationContext().getContentResolver().unregisterContentObserver(contactsObserver);
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Crawling Service Started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");



        if (timer == null) {
            createTimer();
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void crawlContacts() {
        ContentResolver cr = getContentResolver();
        Cursor contactsCursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        StringWriter sw = new StringWriter();
        int count = 0;
        if (contactsCursor.getCount() > 0) {
            while (contactsCursor.moveToNext()) {
                Model model = ModelFactory.createDefaultModel();
                model.setNsPrefix("nco", NCO.NS_NCO);
                model.setNsPrefix("nie", NIE.NS_NIE);

                // Unique contact ID				
                String id = contactsCursor.getString(contactsCursor.getColumnIndex(ContactsContract.Contacts._ID));
                String contactUri = "content://" + android.provider.Settings.Secure.ANDROID_ID + "/" + Contacts.CONTENT_LOOKUP_URI + id;
                Resource contact = model.createResource(contactUri).addProperty(RDF.type, NCO.PersonContact);
                ContactCrawlerHelper.crawlContact(cr, model, contact, id);

                // contact contains all info; serialize it and send it to the server

                model.write(sw, "TURTLE");
                String serializedRDFContact = sw.toString();

                /**
                 * TODO: SEND CONTACT TO SERVER
                 * **************************************************************
                 */
//				restApi.sendContactData(serializedRDFContact);
                count++;
            }
            contactsCursor.close();
        }
    }

    private void doCrawl() {
        isCrawling = true;

        Log.d(TAG, "Let the crawl begin!");

        // Refresh preferences
        Settings.readPreferences(getApplicationContext());

        if (Settings.isCrawlContacts()) {
            //crawlContacts();
        }

        if (Settings.getExtensionsToCheck() != null && Settings.getExtensionsToCheck().size() > 0) {
            // Recreate timer to reflect changes in settings
            //createTimer();		

            long currentTimestamp = System.currentTimeMillis();

            // Recreate filename filter to reflect current settings
            filenameFilter = new FilenameFilter() {

                public boolean accept(File dir, String filename) {
                    boolean accept = false;
                    File sel = new File(dir, filename);
                    if (sel.isFile()) {
                        for (String extension : Settings.getExtensionsToCheck()) {
                            // check only files which have allowed extensions
                            if (filename.toLowerCase().endsWith(extension)) {
                                // Only take files which: we have the permission to read, are not hidden, are smaller than the maxFileSize and were modified since the last crawl
                                accept = sel.canRead() && !sel.isHidden() && sel.lastModified() > Settings.getLastCrawlTimestamp() && sel.length() < Settings.getMaxFileSize();
                                break;
                            }
                        }
                    } else {
                        accept = sel.canRead() && sel.isDirectory() && !sel.isHidden();
                    }
                    return accept;
                }
            };

            // Reset counters 
            totalFilesCrawled = 0;
            totalFoldersCrawled = 0;

            // Start from root		
            crawlFolder(ROOT_FOLDER);

            Settings.setLastCrawlTimestamp(currentTimestamp);

            Log.d(TAG, "Total folders crawled: " + totalFoldersCrawled);
            Log.d(TAG, "Total files crawled: " + totalFilesCrawled);
            Log.d(TAG, "Crawl finished!");
        }
        isCrawling = false;
    }

    private void crawlFolder(String currentFolder) {
        //Log.d(TAG, "Crawling folder: " + currentFolder);
        totalFoldersCrawled++;
        File currentPath = new File(currentFolder);
        String[] folderContents = currentPath.list(filenameFilter);
        for (int i = 0; i < folderContents.length; i++) {
            String filePath = "";
            if (currentFolder.equalsIgnoreCase(ROOT_FOLDER)) {
                filePath = currentFolder + folderContents[i];
            } else {
                filePath = currentFolder + File.separator + folderContents[i];
            }
            File currentFile = new File(filePath);
            if (currentFile != null) {
                if (currentFile.isDirectory()) {
                    if (!stopCrawling) // avoid /sys and /proc: don't have interesting data, and are full of symlinks!
                    {
                        if (!filePath.startsWith("/sys") && !filePath.startsWith("/proc")) {
                            crawlFolder(filePath);
                        }
                    }
                } else {
                    //Log.d(TAG, "Crawled " + currentFile.toURI().toString());
                    crawlFile(currentFile);
                    totalFilesCrawled++;
                }
            }
        }
        currentPath = null;
    }

    private void crawlFile(File file) {
        CrawlData data = FileCrawlerHelper.crawlFile(file);
        if (data != null) {
            //TODO send data to the server
//			restApi.sendCrawlData(data, file);
        }
    }

    private void createTimer() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {

                    public void run() {
                        // start the crawling only if we're not currently crawling,
                        // and an order to stop the process has not been issued
                        if (!isCrawling && !stopCrawling) {
                            doCrawl();
                        }
                    }
                }, 0, Settings.getCrawlInterval());
        Log.d(TAG, "Timer Started");
    }
//	private class ContactsObserver extends ContentObserver {
//
//
//		public ContactsObserver(Handler handler) {
//			super(handler);		
//		}
//		
//		/**
//		 * Every time there's a change to a contact (new contact added, existing contact modified, existing contact deleted),
//		 * this method gets called. Only problem is that whenever a contact is contacted (called, sent an sms, etc), its
//		 * LAST_CONTACTED_TIMESTAMP field is updated
//		 */
//		@Override
//		public void onChange(boolean selfChange) {		
//			super.onChange(selfChange);
//			Log.d(TAG, "********************************CAMBIO EN CONTACTOS****************");
//			//crawlContacts();
//		}
//
//
//		@Override
//		public boolean deliverSelfNotifications() {
//			return true;
//		}
//		
//	}
}
