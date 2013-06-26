/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import eu.dime.model.JSONItem;
import java.util.List;
import java.util.Vector;
import sit.json.JSONObject;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public final class PlaceItem extends DisplayableItem {

    public static enum PLACE_ITEM_FIELDS {

        POSITION, DISTANCE, ADDRESS, TAGS, PHONE, URL, INFORMATION, YM_RATING, SOCIAL_RATING, USER_RATING, FAVORITE,
        FORMATTED, STREET_ADDRESS, LOCALITY, REGION, POSTAL_CODE, COUNTRY
    };
    public static final StringEnumMap<PLACE_ITEM_FIELDS> PlaceItemFieldMap =
            new StringEnumMap(PLACE_ITEM_FIELDS.class, PLACE_ITEM_FIELDS.values(),
            new String[]{"position", "distance", "address", "tags", "phone", "url", "information", "YMRating", "socialRecRating", "userRating", "favorite",
                "formatted", "streetAddress", "locality", "region", "postalCode", "country"
            });

    public final class PlaceAddress extends JSONItem {

        private String formatted;
        private String street;
        private String locality;
        private String region;
        private String postalCode;
        private String country;

        public PlaceAddress() {
            wipeItem();
        }

        public PlaceAddress(String formatted, String street, String locality, String region, String postalCode, String country) {
            this.formatted = formatted;
            this.street = street;
            this.locality = locality;
            this.region = region;
            this.postalCode = postalCode;
            this.country = country;
        }

        @Override
        public PlaceAddress getClone() {
            PlaceAddress result = new PlaceAddress();
            result.formatted = this.formatted;
            result.street = this.street;
            result.locality = this.locality;

            result.region = this.region;
            result.postalCode = this.postalCode;
            result.country = this.country;
            return result;
        }

        @Override
        protected void wipeItem() {
            formatted = "";
            street = "";
            locality = "";

            region = "";
            postalCode = "";
            country = "";
        }

        @Override
        public JSONObject createJSONObject() {
            JSONObject result = new JSONObject(PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.ADDRESS));
            
            result.addChild(getJSONValue(formatted, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.FORMATTED)));
            result.addChild(getJSONValue(street, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.STREET_ADDRESS)));            
            result.addChild(getJSONValue(locality, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.STREET_ADDRESS)));
            
            result.addChild(getJSONValue(region, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.REGION)));            
            result.addChild(getJSONValue(postalCode, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.POSTAL_CODE)));
            result.addChild(getJSONValue(country, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.COUNTRY)));
            return result;
        }

        @Override
        public void readJSONObject(JSONObject jsonObject) {
              this.formatted = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.FORMATTED));
              this.street = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.STREET_ADDRESS));
              this.locality = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.STREET_ADDRESS));
              
              this.region = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.REGION));
              this.postalCode = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.POSTAL_CODE));
              this.country = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.COUNTRY));

        }

        /**
         * @return the formatted
         */
        public String getFormatted() {
            return formatted;
        }

        /**
         * @return the street
         */
        public String getStreet() {
            return street;
        }

        /**
         * @return the locality
         */
        public String getLocality() {
            return locality;
        }

        /**
         * @return the region
         */
        public String getRegion() {
            return region;
        }

        /**
         * @return the postalCode
         */
        public String getPostalCode() {
            return postalCode;
        }

        /**
         * @return the country
         */
        public String getCountry() {
            return country;
        }

        /**
         * @param formatted the formatted to set
         */
        public void setFormatted(String formatted) {
            this.formatted = formatted;
        }

        /**
         * @param street the street to set
         */
        public void setStreet(String street) {
            this.street = street;
        }

        /**
         * @param locality the locality to set
         */
        public void setLocality(String locality) {
            this.locality = locality;
        }

        /**
         * @param region the region to set
         */
        public void setRegion(String region) {
            this.region = region;
        }

        /**
         * @param postalCode the postalCode to set
         */
        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        /**
         * @param country the country to set
         */
        public void setCountry(String country) {
            this.country = country;
        }
    }
    private String position;
    private Double distance;
    private PlaceAddress address;
    private List<String> tags;
    private String phone;
    private String url;
    private String information;
    private Double ymRating;
    private Double socRating;
    private Double userRating;
    private Boolean favorite;

    public PlaceItem() {
        wipeItemForDisplayItem();
    }

    public PlaceItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected final void wipeItemForDisplayItem() {
         this.position = "";
        this.distance = -1.0;
        this.address = new PlaceAddress();
        this.tags = new Vector();
        this.phone ="";
        
        this.url = "";
        this.information = "";
        this.ymRating = -1.0;
        this.socRating = -1.0;
        this.userRating = -1.0;
        
        this.favorite = false;

    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        PlaceItem result = new PlaceItem();
        
        result.position = this.position;
        result.distance = this.distance;
        result.address = this.address.getClone();
        result.tags = new Vector(this.tags);
        result.phone = this.phone;
        
        result.url = this.url;
        result.information = this.information;
        result.ymRating = this.ymRating;
        result.socRating = this.socRating;
        result.userRating = this.userRating;
        
        result.favorite = this.favorite;

        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {

        this.position = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.POSITION));
        this.distance = getDoubleValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.DISTANCE));
        JSONObject myAddress = getItemOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.ADDRESS));
        
        if (myAddress!=null){
            this.address.readJSONObject(myAddress);
        }else{
            this.address = new PlaceAddress("undefined", "", "", "", "", "");
        }
        
        this.tags = getStringListOfJSONObject(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.TAGS));
        this.phone = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.PHONE));
        
        this.url = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.URL));
        this.information = getStringValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.INFORMATION));
        this.ymRating = getDoubleValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.YM_RATING));
        this.socRating = getDoubleValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.SOCIAL_RATING));
        this.userRating = getDoubleValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.USER_RATING));
        
        this.favorite = getBooleanValueOfJSONO(jsonObject, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.FAVORITE));

    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {

        newJSONObject.addChild(getJSONValue(position, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.POSITION)));
        newJSONObject.addChild(getJSONValue(distance, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.DISTANCE)));
        newJSONObject.addChild(address.createJSONObject());
        newJSONObject.addChild(getJSONCollection(tags, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.TAGS)));
        newJSONObject.addChild(getJSONValue(phone, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.PHONE)));
        
        newJSONObject.addChild(getJSONValue(url, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.URL)));
        newJSONObject.addChild(getJSONValue(information, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.INFORMATION)));
        newJSONObject.addChild(getJSONValue(ymRating, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.YM_RATING)));
        newJSONObject.addChild(getJSONValue(socRating, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.SOCIAL_RATING)));
        newJSONObject.addChild(getJSONValue(userRating, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.USER_RATING)));
                
        newJSONObject.addChild(getJSONValue(favorite, PlaceItemFieldMap.get(PLACE_ITEM_FIELDS.FAVORITE)));


        return newJSONObject;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @return the distance
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * @return the address
     */
    public PlaceAddress getAddress() {
        return address;
    }

    /**
     * @return the tags
     */
    public List<String> getTags() {
        changed = true;
        return tags;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @return the information
     */
    public String getInformation() {
        return information;
    }

    /**
     * @return the ymRating
     */
    public Double getYmRating() {
        return ymRating;
    }

    /**
     * @return the socRating
     */
    public Double getSocRating() {
        return socRating;
    }

    /**
     * @return the userRating
     */
    public Double getUserRating() {
        return userRating;
    }

    /**
     * @return the favorite
     */
    public Boolean getFavorite() {
        return favorite;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(String position) {
        changed = true;
        this.position = position;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(Double distance) {
        changed = true;
        this.distance = distance;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(PlaceAddress address) {
        changed = true;
        this.address = address;
    }

    /**
     * @param tags the tags to set
     */
    public void setTags(List<String> tags) {
        changed = true;
        this.tags = tags;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        changed = true;
        this.phone = phone;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        changed = true;
        this.url = url;
    }

    /**
     * @param information the information to set
     */
    public void setInformation(String information) {
        changed = true;
        this.information = information;
    }

    /**
     * @param ymRating the ymRating to set
     */
    public void setYmRating(Double ymRating) {
        changed = true;
        this.ymRating = ymRating;
    }

    /**
     * @param socRating the socRating to set
     */
    public void setSocRating(Double socRating) {
        changed = true;
        this.socRating = socRating;
    }

    /**
     * @param userRating the userRating to set
     */
    public void setUserRating(Double userRating) {
        changed = true;
        this.userRating = userRating;
    }

    /**
     * @param favorite the favorite to set
     */
    public void setFavorite(Boolean favorite) {
        changed = true;
        this.favorite = favorite;
    }
}
