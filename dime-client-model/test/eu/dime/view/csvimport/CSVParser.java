/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.view.csvimport;

import eu.dime.control.LoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.AgentItem;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.simpleps.SaidRegistry;
import eu.dime.simpleps.SimplePSHelper;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import sit.parser.ParseContext;
import sit.parser.SimpleTextParser;
import sit.tools.StringFormat;

/**
 *
 * @author simon
 */
public class CSVParser {

    public enum HEADER_IDS {

        OWNER, NAME, TYPE, IMAGEURL
    };
    public static String[] HEADER_FIELDS = new String[]{"Owner", "Name", "TYPE", "ImageUrl"};

    public static class Hoster {

        private String userId;
        private String nameGiven;
        private String familyName;
        private String said;

        public Hoster(String userId, String nameGiven, String familyName, String said) {
            this.userId = userId;
            this.nameGiven = nameGiven;
            this.familyName = familyName;
            this.said = said;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 47 * hash + (this.userId != null ? this.userId.hashCode() : 0);
            hash = 47 * hash + (this.nameGiven != null ? this.nameGiven.hashCode() : 0);
            hash = 47 * hash + (this.familyName != null ? this.familyName.hashCode() : 0);
            hash = 47 * hash + (this.said != null ? this.said.hashCode() : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Hoster other = (Hoster) obj;
            if ((this.userId == null) ? (other.userId != null) : !this.userId.equals(other.userId)) {
                return false;
            }
            if ((this.nameGiven == null) ? (other.nameGiven != null) : !this.nameGiven.equals(other.nameGiven)) {
                return false;
            }
            if ((this.familyName == null) ? (other.familyName != null) : !this.familyName.equals(other.familyName)) {
                return false;
            }
            if ((this.said == null) ? (other.said != null) : !this.said.equals(other.said)) {
                return false;
            }
            return true;
        }

        /**
         * @return the userId
         */
        public String getUserId() {
            return userId;
        }

        /**
         * @return the nameGiven
         */
        public String getNameGiven() {
            return nameGiven;
        }

        /**
         * @return the familyName
         */
        public String getFamilyName() {
            return familyName;
        }

        /**
         * @param userId the userId to set
         */
        public void setUserId(String userId) {
            this.userId = userId;
        }

        /**
         * @param nameGiven the nameGiven to set
         */
        public void setNameGiven(String nameGiven) {
            this.nameGiven = nameGiven;
        }

        /**
         * @param familyName the familyName to set
         */
        public void setFamilyName(String familyName) {
            this.familyName = familyName;
        }

        /**
         * @return the said
         */
        public String getSaid() {
            return said;
        }

        /**
         * @param said the said to set
         */
        public void setSaid(String said) {
            this.said = said;
        }
    }

    public static class DimeParseContext extends ParseContext {

        private boolean foundValidData = false;
        private final ModelRequestContext mrc;
        private boolean initialLines = true;
        private Vector<Hoster> hosters = new Vector();
        private boolean interconnectionEstablished = false;

        public DimeParseContext(ModelRequestContext mrc) {
            this.mrc = mrc;
        }

        /**
         * @return the foundValidData
         */
        public boolean isFoundValidData() {
            return foundValidData;
        }

        /**
         * @param foundValidData the foundValidData to set
         */
        public void setFoundValidData(boolean foundValidData) {
            this.foundValidData = foundValidData;
        }

        /**
         * @return the mrc
         */
        public ModelRequestContext getMrc() {
            return mrc;
        }

        private boolean isInitialLines() {
            return this.initialLines;
        }

        private void setInitialLines(boolean initialLines) {
            this.initialLines = initialLines;
        }

        private void addHoster(String userId, String nameGiven, String familyName, String said) {
            hosters.add(new Hoster(userId, nameGiven, familyName, said));
        }

        private Iterator<Hoster> getHosterIter() {
            return hosters.iterator();
        }

        private Vector<Hoster> getHosters() {
            return hosters;
        }

        private boolean isInterconnectionEstablished() {
            return interconnectionEstablished;
        }

        /**
         * @param interconnectionEstablished the interconnectionEstablished to
         * set
         */
        public void setInterconnectionEstablished(boolean interconnectionEstablished) {
            this.interconnectionEstablished = interconnectionEstablished;
        }
    }

    public static class DimeDataLineProcessor extends SimpleTextParser.LineProcessor {

        private int index = -1; //start with -1 cause getNextValue increments in the first step
        private final ProfileItem defaultProfile;

        public DimeDataLineProcessor(ProfileItem defaultProfile) {
            this.defaultProfile = defaultProfile;
        }

        private String getNextValue(String line) {
            if (line.length() < index + 1) {
                return null;
            }
            index++; //now pointing on the first value in the field
            int nextSem = line.indexOf(';', index);

            nextSem = (nextSem > -1) ? nextSem : line.length(); //in case no ";" was found set nextSem to length of line

            String result = line.substring(index, nextSem);

            index = nextSem;

            return StringFormat.trimQuotes(result.trim());
        }

        @Override
        public void processLine(ParseContext context, String line) {
            DimeParseContext myContext = (DimeParseContext) context;
            index = -1;

            if (line == null || line.isEmpty()) {
                myContext.setInitialLines(false);
                return;
            }

            if (myContext.isInitialLines()) {

                //in the initial lines the different users==hosters to set up this content for can be specified
                //FIXME this is a bad hack and should be replaced by allowing to specify 
                // separate data import for specific hosters
                //we expect an entry like :
                // userId; given name; family name
                String[] values = line.split(";");
                if (values.length < 3) {
                    myContext.setInitialLines(false);
                    return;
                }

                String userId = values[0];
                String givenName = values[1];
                String familyName = values[2];

                ModelRequestContext meMrc = new ModelRequestContext(
                        userId,
                        Model.ME_OWNER,
                        myContext.getMrc().lvHandler);

                String senderSaid = ModelHelper.getDefaultSenderSaid(meMrc);
                if (senderSaid == null) {
                    senderSaid = UUID.randomUUID().toString();
                    try {
                        //create public profile for user
                        SimplePSHelper.createDefaultProfileForPerson(meMrc,
                                familyName + " " + givenName,
                                senderSaid);
                    } catch (CreateItemFailedException ex) {
                        Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
                        throw new RuntimeException(ex);
                    }
                }

                myContext.addHoster(userId, givenName, familyName, senderSaid);

                return;
            }



            if (!myContext.isFoundValidData()) { //we are in "search for headline" mode
                boolean foundHeadLine = true;
                for (int i = 0; i < 3; i++) {

                    String curVal = getNextValue(line);
                    if ((curVal == null)
                            || (!curVal.equals(CSVParser.HEADER_FIELDS[i]))) {
                        foundHeadLine = false;
                        break;
                    }
                }

                myContext.setFoundValidData(foundHeadLine);

                return;
            }// else

            if (myContext.getHosters().isEmpty()) {
                //in case we have no hoster specified in the csv file - we
                //take the hoster from the 
                myContext.addHoster(myContext.getMrc().hoster, myContext.getMrc().hoster, "", defaultProfile.getServiceAccountId());
            }

            //HACK in case we have more than one person init the interconnection
            if (myContext.getHosters().size() > 1
                    && (!myContext.isInterconnectionEstablished())) {
                myContext.setInterconnectionEstablished(true);
                try {
                    interConnectHosters(myContext.getMrc().lvHandler, myContext.getHosters());
                } catch (CreateItemFailedException ex) {
                    Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
                    throw new RuntimeException(ex);
                }

            }


            String[] values = line.split(";");

            //check whether we got at least a type (and name field)
            if (values.length <= CSVParser.HEADER_IDS.TYPE.ordinal()) {
                Logger.getLogger(CSVParser.class.getName()).log(Level.INFO, "no data found in dataset at line:\n" + line);
                myContext.setFoundValidData(false);
                return;
            }


            TYPES type = SimplePSHelper.guessType(values[CSVParser.HEADER_IDS.TYPE.ordinal()]);
            if (type != null) {



                //add owner
                String owner = values[CSVParser.HEADER_IDS.OWNER.ordinal()].trim();
                if (owner.length() == 0) {
                    owner = Model.ME_OWNER;
                } else {
                    //lookup guid of person
                    AgentItem agent = ModelHelper.getAgentByName(myContext.getMrc(), owner);
                    if (agent != null) {
                        owner = agent.getGuid();
                    } else {
                        Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.WARNING,
                                "unable to find Agent");
                        owner = Model.ME_OWNER;
                    }
                }

                GenItem item = ItemFactory.createNewItemByType(type);

                try {
                    DisplayableItem dItem = (DisplayableItem) item;
                    dItem.setName(values[CSVParser.HEADER_IDS.NAME.ordinal()].trim());
                    dItem.setUserId(owner);

                    //add optional imageurl
                    if (values.length > CSVParser.HEADER_IDS.IMAGEURL.ordinal()) {
                        if (type == TYPES.PROFILEATTRIBUTE) { //HACK for ProfileAttribute it is not an imageURL, but the category of the attribute
                            ProfileAttributeItem paItem = (ProfileAttributeItem) dItem;
                            paItem.setCategory(values[CSVParser.HEADER_IDS.IMAGEURL.ordinal()].trim());
                            if (paItem.getCategoryType() != null) {
                                paItem.updateCategoryRelatedFields(paItem.getCategoryType());
                                int j = CSVParser.HEADER_IDS.IMAGEURL.ordinal() + 1;
                                for (Map.Entry<String, String> entry : paItem.getValue().entrySet()) {
                                    if (j < values.length) {
                                        entry.setValue(values[j]);
                                    } else {
                                        break;
                                    }
                                    j++;
                                }
                                //set name again - since it was overridden by the updateCategoryRelatedFields call
                                //FIXME - check convention/specification
                                dItem.setName(values[CSVParser.HEADER_IDS.NAME.ordinal()].trim());
                            } else {
                                Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.SEVERE, "found unknown category for profileattribute:" + paItem.getCategory());
                            }
                        } else {
                            dItem.setImageUrl("/dime-communications/static/ui/dime/img_demo/" + values[CSVParser.HEADER_IDS.IMAGEURL.ordinal()].trim());
                        }
                    }
                } catch (ClassCastException ex) {
                }

                //handle some special cases:
                if (item.getMType() == TYPES.GROUP) {
                    ((GroupItem) item).setGroupType(GroupItem.VALID_GROUP_TYPE_VALUES[2]);
                }
                if (item.getMType() == TYPES.LIVEPOST) {
                    LivePostItem lpItem = (LivePostItem) item;
                    lpItem.setText(lpItem.getName());
                    lpItem.setName(lpItem.getName().substring(0, 10) + " ..");
                    lpItem.setTimeStamp(System.currentTimeMillis());
                }

                for (Hoster hoster : myContext.getHosters()) {
                    createItemForHoster(myContext.getMrc().lvHandler, hoster, owner, item, values);
                }



            } else { //no type identitfied
                Logger.getLogger(CSVParser.class.getName()).log(Level.INFO, "end of dataset");
                myContext.setFoundValidData(false);
            }

        }

        private void createItemForHoster(LoadingViewHandler lvHandler, Hoster hoster, String owner, GenItem item, String[] values) {
            GenItem myItem = item.getClone();

            try {
                //use special mrc with adapted owner set
                ModelRequestContext myMrc = new ModelRequestContext(
                        hoster.userId,
                        owner,
                        lvHandler);



                if (myItem.getMType() == TYPES.PROFILE) {
                    ProfileItem pItem = (ProfileItem) myItem;
                    Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.INFO, "adding profile\n" + pItem.getName());
                }

                //actually create the item                    
                myItem = Model.getInstance().createItem(myMrc, myItem);

                if (myItem.getMType() == TYPES.PERSON) {
                    if (!owner.equals(Model.ME_OWNER)) {
                        Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.WARNING,
                                "creating of shared persons is not supported!!");
                        return;
                    }

                    PersonItem person = (PersonItem) myItem;
              
                    //create default profile (fake) shared by other user
                    ModelRequestContext themMrc = new ModelRequestContext(myMrc.hoster, person.getGuid(), myMrc.lvHandler);
                    String remotePersonSaid = ModelHelper.getDefaultSenderSaid(themMrc);
                    if (remotePersonSaid == null) {
                        remotePersonSaid = UUID.randomUUID().toString();
                        SimplePSHelper.createDefaultProfileForPerson(
                                themMrc,
                                person.getName(), remotePersonSaid);
                    }
                }


                if (myItem.getMType() == TYPES.GROUP) {
                    //add optional references to groups if there are any
                    for (int i = CSVParser.HEADER_IDS.values().length; i < values.length; i++) {
                        //lookup guid of group
                        AgentItem agent = ModelHelper.getAgentByName(myMrc, values[i].trim());
                        if ((agent != null) && (agent.getMType() == TYPES.PERSON)) {
                            ModelHelper.addPersonToGroup(myMrc, ((PersonItem) agent), (GroupItem) myItem);
                        } else {
                            Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.WARNING, "cannot find person:"
                                    + values[i].trim() + " for group: "
                                    + ((GroupItem) myItem).getName());
                        }

                    }
                }
                if (myItem.getMType() == TYPES.DATABOX) {
                    //add optional references to databoxes if there are any
                    for (int i = CSVParser.HEADER_IDS.values().length; i < values.length; i++) {
                        //lookup guid of databox
                        ResourceItem resItem = (ResourceItem) ModelHelper.getDisplayableByName(
                                myMrc, values[i].trim(), TYPES.RESOURCE);
                        if (resItem != null) {
                            ModelHelper.addResourceToDatabox(myMrc,
                                    ((ResourceItem) resItem), (DataboxItem) myItem);
                        } else {
                            Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.WARNING, "cannot find resource:"
                                    + values[i].trim() + " for databox: "
                                    + ((DataboxItem) myItem).getName());
                        }

                    }
                }
                if (myItem.getMType() == TYPES.PROFILE) {
                    //add optional references to databoxes if there are any
                    for (int i = CSVParser.HEADER_IDS.values().length; i < values.length; i++) {
                        //lookup guid of profileattribute
                        ProfileAttributeItem profAttrItem = (ProfileAttributeItem) ModelHelper.getDisplayableByName(
                                myMrc, values[i].trim(), TYPES.PROFILEATTRIBUTE);
                        if (profAttrItem != null) {
                            ModelHelper.addProfileAttributeToProfile(myMrc,
                                    profAttrItem, (ProfileItem) myItem);
                        } else {
                            Logger.getLogger(DimeDataLineProcessor.class.getName()).log(Level.WARNING, "cannot find profile-attribute:"
                                    + values[i].trim() + " for profile: "
                                    + ((ProfileItem) myItem).getName());
                        }

                    }
                }
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(CSVParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        private void interConnectHosters(LoadingViewHandler lvHandler, Vector<Hoster> hosters) throws CreateItemFailedException {
            for (Hoster aHoster : hosters) {



                for (Hoster bHoster : hosters) {
                    if (aHoster.equals(bHoster)) {
                        continue;
                    }//else

                    SimplePSHelper.createContactEntryAndPublicProfileForRemotePerson(
                            lvHandler,
                            aHoster.familyName + " " + aHoster.nameGiven,
                            aHoster.said,
                            bHoster.userId);


                }
            }
        }
    }

    public void parseCSVFile(ModelRequestContext mrc, File file) throws FileNotFoundException, CreateItemFailedException {

        DimeParseContext context = new DimeParseContext(mrc);

        //make sure the default profile exists
        ProfileItem defaultProfile = SimplePSHelper.getAndCreateDefaultProfile(mrc);



        SimpleTextParser myTxtParser = new SimpleTextParser(file, new DimeDataLineProcessor(defaultProfile));
        myTxtParser.parse(context);

    }
}
