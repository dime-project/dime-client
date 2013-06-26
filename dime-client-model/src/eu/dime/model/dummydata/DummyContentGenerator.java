package eu.dime.model.dummydata;

import eu.dime.model.CreateItemFailedException;
import eu.dime.model.GenItem;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelHelper;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.SharingNotSupportedForSAIDException;
import eu.dime.model.TYPES;
import eu.dime.model.displayable.DataboxItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.GroupItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PersonItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.ProfileAttributeItem;
import eu.dime.model.displayable.ProfileItem;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.displayable.SituationItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DummyContentGenerator {

    private static String[] randomPersonNames = {"Chan Days", "Moira Burkhalter", "Keith Loudermilk", "Eliseo Manion", "Jen Russom",
        "Shaina Rowley", "Charmaine Mccalley", "Harley Harshaw", "Luanna Walch", "Arletha Riffe", "Adaline Venezia", "Violeta Patz",
        "Calvin Brite", "Kendra Cressey", "Leonia Krantz", "Frederic Colpitts", "Nidia Mckellar", "Phung Stockton", "Maire Shetley",
        "Keiko Kirch", "Cristi Rottman", "Cory Estabrook", "Siu Boyland", "Carlena Propst", "Kristina Ruley", "Sergio Rogan", "Eduardo Derrow",
        "Lindsey Loyd", "Stuart Barbera", "Laura Shehan"}; // 30
    // http://listofrandomnames.com/
    private static String[] randomGroupNames = {"Friends", "Business", "Summer Event", "Family", "Party Group", "Knitting Group"};
    private static String[] randomProfileNames = {"Default", "di.me Private", "Business", "LinkedIn", "Summer Event"};
    private static String[] randomMessageText = {"Politically enhanced and making lemonade from wealth.",
        "Snoringly feisty and having second thoughts about the singuarity.", "Gleefully brilliant and hiding comfy blankets.",
        "Snoringly goofy and cuckoo for good times.", "Assuredly broken and fretting over gloom.",
        "Near noxious and wishing there was more obesity.", "Lovingly transient and trippin' about clowns.",
        "Conveniently accepted and whining about shaving.", "Fundamentally stubborn and drunk with abject pity.",
        "Fundamentally optimistic and tripping about mail.", "Endearingly sleepy and strategizing bad karma.",
        "Horribly bewildered and trippin' about halibut.", "Nervously handled and muttering about laundry.",
        "Blessedly flattened and having second thoughts about nuances.", "Groovily enhanced and up on martinis.",
        "Confidently chilled and getting nervous about the golden rule.", "Assuredly philanthropic and cruising toast.",
        "Cosmically star struck and getting nervous about desire.", "Frantically hoodwinked and freakin' on comfy blankets.",
        "Drunkenly frenetic and giving up on subtleties."}; // 20 random
    // http://www.generatorland.com/glgenerator.aspx?id=53&rlx=y
    private static String[] randomFileNames = {"bike.jpg", "black_lodge.jpg", "calculation.xlsx", "cat.JPG", "hannover_fair.JPG",
        "project_pitch.docx"};
    private static String[] randomFileURLs = {"https://dl.dropbox.com/u/9349004/bike.jpg", "https://dl.dropbox.com/u/9349004/black_lodge.jpg",
        "https://dl.dropbox.com/u/9349004/calculation.xlsx", "https://dl.dropbox.com/u/9349004/cat.JPG",
        "https://dl.dropbox.com/u/9349004/hannover_fair.JPG", "https://dl.dropbox.com/u/9349004/project_pitch.docx"};
    private static String[] randomDataBoxNames = {"Summer school", "hiking pics", "all@iao"};
    private static String[] randomPositions = {"+27.1731476 +78.0420685", "+37.8074444 -122.4759721756", "-25.351085 +131.034536"};
    private static String[] randomPlaceNames = {"Taj Mahal", "Golden Gate", "Uluru"};
    private static String[] randomPlaceAddresses = {"Agra, Uttar Pradesh 282001, India", "Presidio, San Francisco, CA, USA",
        "Uluru Northern Territory 0872"};
    private static String[] randomPlaceInformation = {
        "The Taj Mahal is a white marble mausoleum located in Agra, India. It was built by Mughal emperor Shah Jahan in "
        + "memory of his third wife, Mumtaz Mahal. The Taj Mahal is widely recognized as \"the jewel of Muslim art in India and one of the universally admired "
        + "masterpieces of the world's heritage\". It coveres area of about 221 hectare (552 acres), which includes the 38 hectare taj mahal and the 183 hectare "
        + "taj protected forest area",
        "The Golden Gate Bridge is a suspension bridge spanning the Golden Gate, the opening of the San Francisco Bay into the Pacific Ocean. As part of both U.S. "
        + "Route 101 and California State Route 1, the structure links the city of San Francisco, on the northern tip of the San Francisco Peninsula, to Marin County. "
        + "It is one of the most internationally recognized symbols of San Francisco, California, and the United States. It has been declared one of the modern Wonders"
        + " of the World by the American Society of Civil Engineers. The Frommers travel guide considers the Golden Gate Bridge \"possibly the most beautiful, certainly "
        + "the most photographed, bridge in the world\".",
        "Uluru, also known as Ayers Rock, is a large sandstone rock formation in the southern part of the Northern Territory, "
        + "central Australia. It lies 335 km (208 mi) south west of the nearest large town, Alice Springs, 450 km (280 mi) by road. Kata Tjuta and Uluru are the two major "
        + "features of the Uluṟu-Kata Tjuṯa National Park. Uluru is sacred to the Anangu, the Aboriginal people of the area. The area around the formation is home to a plethora "
        + "of springs, waterholes, rock caves and ancient paintings. Uluru is listed as a World Heritage Site."};
    private static String[] randomSituationNames = {
        "@work", "@play", "@lunch", "@home", "@dinner", "@party", "@lunch", "@stressed", "@free", "@travelling"};

    public static void generateRandomSituations(ModelRequestContext mrContext, int num) {
        // CREATOR, ACTIVE, CONFIDENCE, PLACE_REFERENCE, EVENT_REFERENCE
        TYPES type = TYPES.SITUATION;

        Random rnd = new Random();
        int activeSituationIndex = rnd.nextInt(num);

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);
            result.setName(randomSituationNames[i % randomPersonNames.length]);
            result.setImageUrl("http://lorempixel.com/200/200/abstract/" + (i % 10) + "/");
            SituationItem situation = (SituationItem) result;
            if (i == activeSituationIndex) {
                situation.setActive(true);
            } else {
                situation.setActive(false);
            }
//            List<String> placeGUIDs = getExistingGUIDs(mrContext, TYPES.PLACE);
//            situation.setPlaceReference(placeGUIDs.get(rnd.nextInt(placeGUIDs.size())));
//            List<String> eventGUIDs = getExistingGUIDs(mrContext, TYPES.EVENT);
//            if (eventGUIDs != null && eventGUIDs.size() > 0) {
//                situation.setPlaceReference(eventGUIDs.get(rnd.nextInt(eventGUIDs.size())));
//            }
            situation.setCreator("urn:auto-generated");
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, situation);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    // POSITION, DISTANCE, ADDRESS, TAGS, PHONE, URL, INFORMATION, YM_RATING,
    // SOCIAL_RATING, USER_RATING, FAVORITE,
    // FORMATTED, STREET_ADDRESS, LOCALITY, REGION, POSTAL_CODE, COUNTRY //
    // Ratings' Range [0.0 .. 1.0] ; Favorite true & false
    public static void generateRandomPlaces(ModelRequestContext mrContext, int num) {
        TYPES type = TYPES.PLACE;

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);
            result.setName(randomPlaceNames[i % randomPlaceNames.length]);

            result.setImageUrl("http://lorempixel.com/200/200/city/" + (i % 10) + "/");

            PlaceItem place = (PlaceItem) result;

            place.setInformation(randomPlaceInformation[i % randomPlaceInformation.length]);

            // TODO: set address of place if available

            place.setPosition(randomPositions[i % randomPositions.length]);

            place.setSocRating(Math.random());
            place.setYmRating(Math.random());
            place.setUserRating(Math.random());

            place.setDistance((Math.random() * 1000));

            if (Math.random() <= 0.5) {
                place.setFavorite(true);
            } else {
                place.setFavorite(false);
            }
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, place);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static void generateRandomDataBoxes(ModelRequestContext mrContext, int num) {
        TYPES type = TYPES.DATABOX;

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);
            assignRandomExistingChildren(mrContext, type, result, 3, 7);
            result.setName(randomDataBoxNames[i % randomDataBoxNames.length]);

            result.setImageUrl("http://lorempixel.com/200/200/technics/" + (i % 10) + "/");

            DataboxItem dbi = (DataboxItem) result;
            dbi.setPrivacyLevel(Math.random());
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, dbi);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static void generateRandomResources(ModelRequestContext mrContext, int num, boolean createForeign) {
        TYPES type = TYPES.RESOURCE;
        List<String> userGUIDs = getExistingGUIDs(mrContext, TYPES.PERSON);

        Random rnd = new Random();
        long now = System.currentTimeMillis();

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);

            String randomUserID = userGUIDs.get(rnd.nextInt(userGUIDs.size() - 1));
            PersonItem randomPerson = (PersonItem) Model.getInstance().getItem(mrContext, TYPES.PERSON, randomUserID);

            if (i % 2 == 0 || !createForeign) {
                result.setUserId(Model.ME_OWNER);
            } else {
                result.setUserId(randomUserID);
            }

            ResourceItem ri = (ResourceItem) result;

            int randomNumber = rnd.nextInt(randomFileNames.length);
            ri.setPrivacyLevel(Math.random());
            ri.setName(randomFileNames[randomNumber]);
            ri.setDownloadUrl(randomFileURLs[randomNumber]);

            // TODO: set sharedItem to random person: res from me to that person
            if (i % 2 == 0 || !createForeign) {
                try {
                    // ModelHelper.shareItemToAgent(hoster, lpi, randomPerson);
                    // s
                    Model.getInstance().createItem(mrContext, ri);
                } catch (CreateItemFailedException ex) {
                    Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }

            } else {
                try {
                    Model.getInstance().createItem(new ModelRequestContext(mrContext.hoster, randomUserID, mrContext.lvHandler), ri);
                } catch (CreateItemFailedException ex) {
                    Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }

        }

    }

    public static void generateRandomLivepost(ModelRequestContext mrContext, int num, boolean createForeign) {

        List<PersonItem> allPersons = ModelHelper.getAllPersons(mrContext);
        if (allPersons.isEmpty()) {
            Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.WARNING, "no persons found - creating liveposts skipped!");
            return;
        }

        Random rnd = new Random();
        long now = System.currentTimeMillis();

        for (int i = 0; i < num; i++) {
            try {
                PersonItem randomPerson = allPersons.get(rnd.nextInt(allPersons.size()));

                String owner = (i % 2 == 0 || !createForeign) ? Model.ME_OWNER : randomPerson.getGuid();
                ModelRequestContext myMRContext = new ModelRequestContext(mrContext.hoster, owner, mrContext.lvHandler);

                LivePostItem lpi = (LivePostItem) generateRandomItem(mrContext, TYPES.LIVEPOST);
                lpi.setText(randomMessageText[rnd.nextInt(randomMessageText.length - 1)]);
                lpi.setTimeStamp(now + i);
                lpi.setUserId(owner);
                Model.getInstance().createItem(myMRContext, lpi);

                List<ProfileItem> profilesOfPerson = ModelHelper.getAllProfiles(mrContext);
                if (profilesOfPerson.size() < 1) {
                    Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.WARNING, "cannot create share - since no profiles available");
                    return;
                }
                if (owner.equals(Model.ME_OWNER)) {
                    try {
                        ModelHelper.shareItemToAgent(mrContext, lpi, randomPerson, profilesOfPerson.get(0));
                    } catch (SharingNotSupportedForSAIDException ex) {
                        Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }

        }

    }

    public static void generateOneNCOProfile(ModelRequestContext mrContext) {
        TYPES type = TYPES.PROFILE;
        Random rnd = new Random();
        List<String> userGUIDs = getExistingGUIDs(mrContext, TYPES.PERSON);


        DisplayableItem result = generateRandomItem(mrContext, type);


        result.setName("NCO-Profile");
        result.setImageUrl("http://lorempixel.com/200/200/abstract/");



        result.setUserId(Model.ME_OWNER);
        ProfileItem pi = (ProfileItem) result;

        pi.setItems(getExistingGUIDs(mrContext, TYPES.PROFILEATTRIBUTE));


        try {
            Model.getInstance().createItem(mrContext, pi);
        } catch (CreateItemFailedException ex) {
            Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }



    }

    public static void generateOneNCOProfileAtt(ModelRequestContext mrContext) {



        /*
         * type = TYPES.PROFILE;
         *
         * result = generateRandomItem(mrContext, type);
         *
         * result.setName("John Doe-NCO");
         * result.setImageUrl("http://lorempixel.com/200/200/abstract/");
         *
         * Vector<String> childs = getExistingGUIDs(mrContext,
         * TYPES.PROFILEATTRIBUTE);
         *
         * result.setItems(childs);
         *
         * // create profile of me
         *
         * result.setUserId(Model.ME_OWNER); try {
         * Model.getInstance().createItem(mrContext, result); } catch
         * (CreateItemFailedException ex) {
         * Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE,
         * ex.getMessage(), ex);
		}
         */



        TYPES type = TYPES.PROFILEATTRIBUTE;

        class InnerProfileAttributeItem {

            String category;
            HashMap<String, String> attributes;

            public InnerProfileAttributeItem() {
                this.category = "";
                this.attributes = new HashMap<String, String>();
            }
        }
        InnerProfileAttributeItem aProfileAtt = new InnerProfileAttributeItem();
        aProfileAtt.category = "nco:PersonName";
        aProfileAtt.attributes.put("nameFamily", "Doe");
        aProfileAtt.attributes.put("nameHonorificPrefix", "Dr.");
        aProfileAtt.attributes.put("nameAdditional", "Smirnov");
        aProfileAtt.attributes.put("nameGiven", "John");

        DisplayableItem result = generateRandomItem(mrContext, type);

        ProfileAttributeItem pai = (ProfileAttributeItem) result;
        pai.setName("John Doe Profile Cat");
        pai.setCategory(aProfileAtt.category);
        pai.setValue(aProfileAtt.attributes);
        try {
            // update one object a time!
            Model.getInstance().createItem(mrContext, pai);
        } catch (CreateItemFailedException ex) {
            Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public static void generateRandomProfileAttributes(ModelRequestContext mrContext, int num) {
        TYPES type = TYPES.PROFILEATTRIBUTE;
        Random rnd = new Random();

        class InnerProfileAttributeItem {

            String category;
            HashMap<String, String> attributes;

            public InnerProfileAttributeItem() {
                this.category = "";
                this.attributes = new HashMap<String, String>();
            }
        }

        ArrayList<InnerProfileAttributeItem> tmpList = new ArrayList<InnerProfileAttributeItem>();
        InnerProfileAttributeItem theLast = new InnerProfileAttributeItem();

        tmpList.add(new InnerProfileAttributeItem());
        theLast = tmpList.get(tmpList.size() - 1);
        theLast.category = "Address";
        theLast.attributes.put("Region", "Somewhere");
        theLast.attributes.put("StreetAddress", "Sesamestreet");
        theLast.attributes.put("Postalcode", "73613");
        theLast.attributes.put("Country", "Ukraine");

        tmpList.add(new InnerProfileAttributeItem());
        theLast = tmpList.get(tmpList.size() - 1);
        theLast.category = "IMAccount";
        theLast.attributes.put("imStatus", "lurk");
        theLast.attributes.put("imNickname", "stevie");
        theLast.attributes.put("imStautsmessage", "lol");

        tmpList.add(new InnerProfileAttributeItem());
        theLast = tmpList.get(tmpList.size() - 1);
        theLast.category = "CellphoneNumber";
        theLast.attributes.put("phoneNumber", "7864182643");

        tmpList.add(new InnerProfileAttributeItem());
        theLast = tmpList.get(tmpList.size() - 1);
        theLast.category = "Interests";
        theLast.attributes.put("Interest one", "reading");
        theLast.attributes.put("Interest two", "knitting");
        theLast.attributes.put("Interest three", "party");

        tmpList.add(new InnerProfileAttributeItem());
        theLast = tmpList.get(tmpList.size() - 1);
        theLast.category = "WorkphoneNumber";
        theLast.attributes.put("phoneNumber", "7864182643");

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);

            // InnerProfileAttributeItem randomItem = tmpList.get(rnd.nextInt(
            // tmpList.size()-1 ) );
            InnerProfileAttributeItem randomItem = tmpList.get(i % tmpList.size());

            ProfileAttributeItem pai = (ProfileAttributeItem) result;
            pai.setName(randomItem.category);
            pai.setCategory(randomItem.category);
            pai.setValue(randomItem.attributes);
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, pai);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void generateRandomProfiles(ModelRequestContext mrContext, int num, boolean createForeign) {
        TYPES type = TYPES.PROFILE;
        Random rnd = new Random();
        List<String> userGUIDs = getExistingGUIDs(mrContext, TYPES.PERSON);

        for (int i = 0; i < num; i++) {

            String randomUserID = userGUIDs.get(rnd.nextInt(userGUIDs.size() - 1));
            PersonItem randomPerson = (PersonItem) Model.getInstance().getItem(mrContext, TYPES.PERSON, randomUserID);

            DisplayableItem result = generateRandomItem(mrContext, type);
            assignRandomExistingChildren(mrContext, type, result, 2, 5);

            result.setName(randomProfileNames[rnd.nextInt(randomProfileNames.length - 1)]);
            result.setImageUrl("http://lorempixel.com/200/200/abstract/" + (i % 10) + "/");

            // create profile of me
            if (i % 2 == 0 || !createForeign) {
                result.setUserId(Model.ME_OWNER);
                try {
                    Model.getInstance().createItem(mrContext, result);
                } catch (CreateItemFailedException ex) {
                    Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            } // create profile for some random else
            else {
                result.setUserId(randomUserID);
                try {
                    Model.getInstance().createItem(new ModelRequestContext(mrContext.hoster, randomUserID, mrContext.lvHandler), result);
                } catch (CreateItemFailedException ex) {
                    Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
    }

    public static void generateRandomGroups(ModelRequestContext mrContext, int num) {
        TYPES type = TYPES.GROUP;

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);
            assignRandomExistingChildren(mrContext, type, result, 4, 12);

            result.setName(randomGroupNames[i % randomGroupNames.length]);
            result.setImageUrl("http://lorempixel.com/200/200/abstract/" + (i % 10) + "/");

            GroupItem gi = (GroupItem) result;
            gi.setTrustLevel(Math.random());
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, gi);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static void generateRandomPeople(ModelRequestContext mrContext, int num) {
        TYPES type = TYPES.PERSON;

        for (int i = 0; i < num; i++) {
            DisplayableItem result = generateRandomItem(mrContext, type);
            result.setName(randomPersonNames[i % randomPersonNames.length]);

            result.setImageUrl("http://lorempixel.com/200/200/people/" + (i % 10) + "/");

            PersonItem ps = (PersonItem) result;
            ps.setTrustLevel(Math.random());
            try {
                // update one object a time!
                Model.getInstance().createItem(mrContext, ps);
            } catch (CreateItemFailedException ex) {
                Logger.getLogger(DummyContentGenerator.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    public static DisplayableItem generateRandomItem(ModelRequestContext mrContext, TYPES type) {

        DisplayableItem result = (DisplayableItem) ItemFactory.createNewItemByType(UUID.randomUUID().toString(), type);

        result.setName("TestName");
        result.setMType(type);
        result.setUserId(mrContext.owner);

        return result;
    }

    public static DisplayableItem assignRandomExistingChildren(ModelRequestContext mrContext, TYPES type, DisplayableItem item, int minChildren,
            int maxChildren) {

        Random rnd = new Random();

        // in case child items exists for this type - add some random childs
        if (ModelHelper.getChildType(type) != null) {

            int numberOfChilds = rnd.nextInt(maxChildren - minChildren) + minChildren;

            Vector<String> childs = getExistingGUIDs(mrContext, ModelHelper.getChildType(type));

            Collections.shuffle(childs);
            childs.setSize(numberOfChilds);

            item.setItems(childs);
        }

        return item;
    }

    private static Vector<String> getExistingGUIDs(ModelRequestContext mrContext, TYPES type) {
        Vector<String> result = new Vector<String>();
        List<GenItem> allItems = Model.getInstance().getAllItems(mrContext, type);
        for (int i = 0; i < allItems.size(); i++) {

            result.add(allItems.get(i).getGuid());
        }
        return result;

    }

    public static void generateAllDummyContent(ModelRequestContext mrContext) {
        generateRandomPeople(mrContext, 12);
        generateRandomGroups(mrContext, 5);
        generateRandomLivepost(mrContext, 20, false);
        generateRandomProfileAttributes(mrContext, 5);
        generateRandomProfiles(mrContext, 3, false);
        generateRandomResources(mrContext, 15, false);
        generateRandomDataBoxes(mrContext, 4);
        generateRandomPlaces(mrContext, 3);
        generateRandomSituations(mrContext, 5);
    }
}
