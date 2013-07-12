/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import eu.dime.restapi.DimeHelper;
import sit.json.JSONObject;
import sit.sstl.StringEnumMap;

/**
 *
 * @author Simon Thiel
 */
public final class ResourceItem extends DisplayableItem implements ShareableItem {

    public static enum RESOURCE_ITEM_FIELDS {
        PRIVACY_LEVEL_TAG, DOWNLOAD_URL, MIME_TYPE, ORIGIN_ID, ORIGINAL_PATH, 
        FILE_SIZE, WORD_COUNT, LINE_COUNT, PAGE_NUMBER, FILE_OWNER
    };
    public static final StringEnumMap<RESOURCE_ITEM_FIELDS> ResourceItemFieldMap =
            new StringEnumMap<RESOURCE_ITEM_FIELDS>(RESOURCE_ITEM_FIELDS.class, RESOURCE_ITEM_FIELDS.values(),
            new String[]{"nao:privacyLevel", "downloadUrl", "nie:mimeType", "originId", "originalPath", 
                        "nfo:fileSize", "nfo:lineCount", "nfo:wordCount", "nfo:pageNumber", "nfo:fileOwner"});
   
    private Double privacyLevel;
    private String downloadUrl;
    private String mimeType;
    private String originId;
    private String originalPath;
    private Integer fileSize;
    private Integer wordCount;
    private Integer lineCount;
    private Integer pageNumber;
    private String fileOwner;

    public ResourceItem() {
        wipeItemForDisplayItem();
    }

    public ResourceItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    @Override
    protected void wipeItemForDisplayItem() {
        privacyLevel = DimeHelper.DEFAULT_INITIAL_PRIVACY_LEVEL;
        downloadUrl = "";
        mimeType = "";
        originId = "";
        originalPath = "";
        fileSize = -1;
        wordCount = -1;
        lineCount = -1;
        pageNumber = -1;
        fileOwner = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        ResourceItem result = new ResourceItem();
        result.privacyLevel = this.privacyLevel;
        result.downloadUrl = this.downloadUrl;
        result.mimeType = this.mimeType;
        result.originId = this.originId;
        result.originalPath = this.originalPath;
        result.fileSize = this.fileSize;
        result.wordCount = this.wordCount;
        result.lineCount = this.lineCount;
        result.pageNumber = this.pageNumber;
        result.fileOwner = this.fileOwner;
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.privacyLevel = getDoubleValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.PRIVACY_LEVEL_TAG));
        this.downloadUrl =  getStringValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.DOWNLOAD_URL));
        this.mimeType =  getStringValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.MIME_TYPE));
        this.originId =  getStringValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.ORIGIN_ID));
        this.originalPath =  getStringValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.ORIGINAL_PATH));
        this.fileSize =  getIntegerValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.FILE_SIZE));
        this.wordCount =  getIntegerValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.WORD_COUNT));
        this.lineCount =  getIntegerValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.LINE_COUNT));
        this.pageNumber =  getIntegerValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.PAGE_NUMBER));
        this.fileOwner =  getStringValueOfJSONO(jsonObject, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.FILE_OWNER));
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(privacyLevel, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.PRIVACY_LEVEL_TAG)));   
        newJSONObject.addChild(getJSONValue(downloadUrl, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.DOWNLOAD_URL)));        
        newJSONObject.addChild(getJSONValue(mimeType, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.MIME_TYPE)));        
        newJSONObject.addChild(getJSONValue(originId, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.ORIGIN_ID)));        
        newJSONObject.addChild(getJSONValue(originalPath, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.ORIGINAL_PATH)));        
        newJSONObject.addChild(getJSONValue(fileSize, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.FILE_SIZE)));        
        newJSONObject.addChild(getJSONValue(wordCount, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.WORD_COUNT)));
        newJSONObject.addChild(getJSONValue(lineCount, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.LINE_COUNT)));
        newJSONObject.addChild(getJSONValue(pageNumber, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.PAGE_NUMBER)));
        newJSONObject.addChild(getJSONValue(fileOwner, ResourceItemFieldMap.get(RESOURCE_ITEM_FIELDS.FILE_OWNER)));        
        return newJSONObject;
    }

    /**
     * @return the privacyLevel
     */
    public Double getPrivacyLevel() {
        return privacyLevel;
    }

    /**
     * @return the downloadUrl
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }

    /**
     * @return the mimeType
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @return the originId
     */
    public String getOriginId() {
        return originId;
    }

    /**
     * @return the originalPath
     */
    public String getOriginalPath() {
        return originalPath;
    }

    /**
     * @return the fileSize
     */
    public Integer getFileSize() {
        return fileSize;
    }

    /**
     * @return the wordCount
     */
    public Integer getWordCount() {
        return wordCount;
    }

    /**
     * @return the pageNumber
     */
    public Integer getPageNumber() {
        return pageNumber;
    }

    /**
     * @return the fileOwner
     */
    public String getFileOwner() {
        return fileOwner;
    }

    /**
     * @param privacyLevel the privacyLevel to set
     */
    public void setPrivacyLevel(Double privacyLevel) {
        changed = true;
        this.privacyLevel = privacyLevel;
    }

    /**
     * @param downloadUrl the downloadUrl to set
     */
    public void setDownloadUrl(String downloadUrl) {
        changed = true;
        this.downloadUrl = downloadUrl;
    }

    /**
     * @param mimeType the mimeType to set
     */
    public void setMimeType(String mimeType) {
        changed = true;
        this.mimeType = mimeType;
    }

    /**
     * @param originId the originId to set
     */
    public void setOriginId(String originId) {
        changed = true;
        this.originId = originId;
    }

    /**
     * @param originalPath the originalPath to set
     */
    public void setOriginalPath(String originalPath) {
        changed = true;
        this.originalPath = originalPath;
    }

    /**
     * @param fileSize the fileSize to set
     */
    public void setFileSize(Integer fileSize) {
        changed = true;
        this.fileSize = fileSize;
    }

    /**
     * @param wordCount the wordCount to set
     */
    public void setWordCount(Integer wordCount) {
        changed = true;
        this.wordCount = wordCount;
    }

    /**
     * @param pageNumber the pageNumber to set
     */
    public void setPageNumber(Integer pageNumber) {
        changed = true;
        this.pageNumber = pageNumber;
    }

    /**
     * @param fileOwner the fileOwner to set
     */
    public void setFileOwner(String fileOwner) {
        changed = true;
        this.fileOwner = fileOwner;
    }

    /**
     * @return the lineCount
     */
    public Integer getLineCount() {
        return lineCount;
    }

    /**
     * @param lineCount the lineCount to set
     */
    public void setLineCount(Integer lineCount) {
        this.lineCount = lineCount;
    }

    
}
