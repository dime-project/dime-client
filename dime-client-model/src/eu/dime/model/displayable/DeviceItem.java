/*
 *  Description of PersonItem
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 07.05.2012
 */
package eu.dime.model.displayable;

import sit.json.JSONObject;

/**
 *
 * @author Simon Thiel
 */
public final class DeviceItem extends DisplayableItem{


    public static final String CLIENT_TYPE_TAG = "clientType";
    public static final String DEVICE_NAME_TAG = "ddo:deviceName";
    public static final String DEVICE_ID_TAG = "ddo:deviceIdentifier";
    public static final String VERSION_NR_TAG = "versionNumber";
    
    private String clientType;
    private String deviceName;
    private String deviceId;
    private String versionNr;
    
    public DeviceItem() {
        wipeItemForDisplayItem();
    }

    public DeviceItem(String guid) {
        super(guid);
        wipeItemForDisplayItem();
    }

    
    
    @Override
    protected final void wipeItemForDisplayItem() {
        clientType = "";
        deviceName = "";
        deviceId = "";
        versionNr = "";
    }

    @Override
    protected DisplayableItem getCloneForDisplayItem() {
        DeviceItem result = new DeviceItem();
        result.clientType = this.clientType;
        result.deviceName = this.deviceName;
        result.deviceId = this.deviceId;
        result.versionNr = this.versionNr;
        
        return result;
    }

    @Override
    public void readJSONObjectForDisplayItem(JSONObject jsonObject) {
        this.clientType = getStringValueOfJSONO(jsonObject, CLIENT_TYPE_TAG);
        this.deviceName = getStringValueOfJSONO(jsonObject, DEVICE_NAME_TAG);
        this.deviceId = getStringValueOfJSONO(jsonObject, DEVICE_ID_TAG);
        this.versionNr = getStringValueOfJSONO(jsonObject, VERSION_NR_TAG);
        
    }

    @Override
    protected JSONObject createJSONObjectForDisplayItem(JSONObject newJSONObject) {
        newJSONObject.addChild(getJSONValue(clientType, CLIENT_TYPE_TAG));
        newJSONObject.addChild(getJSONValue(deviceName, DEVICE_NAME_TAG));
        newJSONObject.addChild(getJSONValue(deviceId, DEVICE_ID_TAG));
        newJSONObject.addChild(getJSONValue(versionNr, VERSION_NR_TAG));
        return newJSONObject;
    }

    /**
     * @return the clientType
     */
    public String getClientType() {
        return clientType;
    }

    /**
     * @return the deviceName
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * @return the deviceId
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * @return the versionNr
     */
    public String getVersionNr() {
        return versionNr;
    }

    /**
     * @param clientType the clientType to set
     */
    public void setClientType(String clientType) {
        changed = true;
        this.clientType = clientType;
    }

    /**
     * @param deviceName the deviceName to set
     */
    public void setDeviceName(String deviceName) {
        changed = true;
        this.deviceName = deviceName;
    }

    /**
     * @param deviceId the deviceId to set
     */
    public void setDeviceId(String deviceId) {
        changed = true;
        this.deviceId = deviceId;
    }

    /**
     * @param versionNr the versionNr to set
     */
    public void setVersionNr(String versionNr) {
        changed = true;
        this.versionNr = versionNr;
    }

}
