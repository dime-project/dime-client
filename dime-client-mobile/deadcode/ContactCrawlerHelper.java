package eu.dime.mobile.datamining;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

import eu.dime.mobile.datamining.semantic.vocabulary.NCO;

public class ContactCrawlerHelper {

    private static final String TAG = "ContactCrawler";

    public static void crawlContact(ContentResolver cr, Model model, Resource contact, String contactId) {
        crawlName(cr, contact, model, contactId);
        crawlEmail(cr, contact, model, contactId);
        crawlPhoneNumbers(cr, contact, model, contactId);
        crawlPostalAddress(cr, contact, model, contactId);
    }

    private static void crawlName(ContentResolver cr, Resource contact, Model model, String contactId) {

        Cursor nameCursor = cr.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                    contactId}, null);
        if (nameCursor.moveToNext()) {
            Resource personName = model.createResource();

            personName.addProperty(RDF.type, NCO.PersonName);

            String displayName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME));
            if (displayName != null) {
                personName.addProperty(NCO.fullname, displayName);
            }

            String givenName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME));
            if (givenName != null) {
                personName.addProperty(NCO.nameGiven, givenName);
            }

            String familyName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME));
            if (familyName != null) {
                personName.addProperty(NCO.nameFamily, familyName);
            }

            String honorificPrefix = nameCursor.getString(nameCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.PREFIX));
            if (honorificPrefix != null) {
                personName.addProperty(NCO.nameHonorificPrefix, honorificPrefix);
            }

            String honorificSuffix = nameCursor.getString(nameCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.SUFFIX));
            if (honorificSuffix != null) {
                personName.addProperty(NCO.nameHonorificSuffix, honorificSuffix);
            }

            String middleName = nameCursor.getString(nameCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.MIDDLE_NAME));
            if (middleName != null) {
                personName.addProperty(NCO.nameAdditional, middleName);
            }

            // Only add the name resource if there's at least one bit of information
            if (displayName != null || givenName != null || familyName != null
                    || honorificPrefix != null || honorificSuffix != null || middleName != null) {
                contact.addProperty(NCO.hasPersonName, personName);
            }
        }
    }

    private static void crawlEmail(ContentResolver cr, Resource contact, Model model, String contactId) {
        Cursor emailCursor = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{
                    contactId}, null);
        while (emailCursor.moveToNext()) {
            String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
            int emailType = emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));

            Resource emailAddress = model.createResource().addProperty(RDF.type, NCO.EmailAddress);
            emailAddress.addProperty(NCO.emailAddress, email);

            if (email != null && email.trim().length() > 0) {
                switch (emailType) {
                    case Email.TYPE_HOME:
                        emailAddress.addProperty(NCO.contactMediumComment, "Home Email Address");
                        break;

                    case Email.TYPE_MOBILE:
                        emailAddress.addProperty(NCO.contactMediumComment, "Mobile Email Address");
                        break;

                    case Email.TYPE_WORK:
                        emailAddress.addProperty(NCO.contactMediumComment, "Home Email Address");
                        break;

                    case Email.TYPE_OTHER:
                    default:
                        emailAddress.addProperty(NCO.contactMediumComment, "Other Email Address");
                        break;


                }
                contact.addProperty(NCO.hasEmailAddress, emailAddress);
            }
        }
        emailCursor.close();
    }

    private static void crawlPhoneNumbers(ContentResolver cr, Resource contact, Model model, String contactId) {
        Cursor phoneCursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                    contactId}, null);
        while (phoneCursor.moveToNext()) {
            String phoneNumber = phoneCursor.getString(phoneCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            switch (phoneType) {
                case Phone.TYPE_MAIN:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Main Phone Number"));
                    break;

                case Phone.TYPE_MOBILE:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.CellPhoneNumber).
                            addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Mobile Phone Number"));

                    break;

                case Phone.TYPE_HOME:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Home Phone Number"));
                    break;

                case Phone.TYPE_WORK:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Work Phone Number"));
                    break;

                case Phone.TYPE_WORK_MOBILE:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.CellPhoneNumber).
                            addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Work Mobile Phone Number"));
                    break;

                case Phone.TYPE_COMPANY_MAIN:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Main Company Phone Number"));
                    break;

                case Phone.TYPE_FAX_HOME:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.FaxNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Home Fax Number"));
                    break;

                case Phone.TYPE_FAX_WORK:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.FaxNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Work Fax Number"));
                    break;

                case Phone.TYPE_OTHER_FAX:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.FaxNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Other Fax Number"));
                    break;

                case Phone.TYPE_CAR:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.CarPhoneNumber).
                            addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Car Phone Number"));
                    break;

                case Phone.TYPE_OTHER:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Other Phone Number"));
                    break;

                case Phone.TYPE_PAGER:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PagerNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Pager Phone Number"));
                    break;

                case Phone.TYPE_ISDN:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.IsdnNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "ISDN Phone Number"));
                    break;

                default:
                    contact.addProperty(NCO.hasPhoneNumber, model.createResource().
                            addProperty(RDF.type, NCO.PhoneNumber).addProperty(NCO.phoneNumber, phoneNumber).
                            addProperty(NCO.contactMediumComment, "Other Phone Number"));
                    break;
            }

        }
        phoneCursor.close();
    }

    private static void crawlPostalAddress(ContentResolver cr, Resource contact, Model model, String contactId) {
        Cursor addressCursor = cr.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI, null, ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID + " = ?", new String[]{
                    contactId}, null);


        while (addressCursor.moveToNext()) {
            Resource address = model.createResource().addProperty(RDF.type, NCO.PostalAddress);

            String street = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
            String neightbourhood = addressCursor.getString(addressCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.NEIGHBORHOOD));
            if (street != null) {
                address.addProperty(NCO.streetAddress, street + (neightbourhood != null ? neightbourhood : ""));
            }

            String city = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
            if (city != null) {
                address.addProperty(NCO.locality, city);
            }

            String region = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
            if (region != null) {
                address.addProperty(NCO.region, region);
            }

            String postcode = addressCursor.getString(addressCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
            if (postcode != null) {
                address.addProperty(NCO.postalcode, postcode);
            }

            String country = addressCursor.getString(addressCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
            if (country != null) {
                address.addProperty(NCO.country, country);
            }

            String pobox = addressCursor.getString(addressCursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
            if (pobox != null) {
                address.addProperty(NCO.pobox, pobox);
            }

            // TODO is this useful?
            String addressType = addressCursor.getString(addressCursor.
                    getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

            contact.addProperty(NCO.hasPostalAddress, address);
        }
        addressCursor.close();
    }
}
// Additional properties available
//ContactsContract.Contacts.CONTACT_STATUS // latest status update TEXT
//ContactsContract.Contacts.CONTACT_STATUS_TIMESTAMP // timestamp of the latest status update NUMBER
//ContactsContract.Contacts.LAST_TIME_CONTACTED // timestamp when the contact was last contacted INTEGER
//ContactsContract.Contacts.STARRED // is the contact starred? INTEGER (boolean)				
//ContactsContract.Contacts.TIMES_CONTACTED // number of times the contact has been contacted INTEGER
