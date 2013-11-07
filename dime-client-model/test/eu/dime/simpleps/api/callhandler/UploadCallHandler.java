/*
* Copyright 2013 by the digital.me project (http:\\www.dime-project.eu).
*
* Licensed under the EUPL, Version 1.1 only (the "Licence");
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
*
* http://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and limitations under the Licence.
*/

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.dime.simpleps.api.callhandler;

import eu.dime.control.DummyLoadingViewHandler;
import eu.dime.model.CreateItemFailedException;
import eu.dime.model.ItemFactory;
import eu.dime.model.Model;
import eu.dime.model.ModelRequestContext;
import eu.dime.model.displayable.ResourceItem;
import eu.dime.model.specialitem.NotificationItem;
import eu.dime.simpleps.database.DatabaseAccess;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import sit.io.FileHelper;
import sit.web.HttpConstants;
import sit.web.MimeTypes;
import sit.web.WebRequest;
import sit.web.multipart.MPFileEntry;
import sit.web.multipart.MultipartContainer;
import sit.web.multipart.MultipartEntry;
import sit.web.multipart.MultipartParser;

/**
 *
 * @author simon
 */
public class UploadCallHandler extends CallHandler {

    public static final String RESOURCE_SUB_PATH = "blob/";
    public static final String RESOURCE_PATH = "resources/www/"+RESOURCE_SUB_PATH;
    public static final String THUMB_PREFIX = "thumb_";

    @Override
    public DIME_HANDLER_PARAMS[] getSignature() {
        //dime-communications/api/dime/rest/9702325/resource/crawler
        return new DIME_HANDLER_PARAMS[]{
                    DIME_HANDLER_PARAMS.HOSTER,
                    DIME_HANDLER_PARAMS.TYPE,
                    DIME_HANDLER_PARAMS.CRAWLER
                };
    }

    @Override
    public String handleCall(WebRequest request, ParamsMap params) {


        try {
            if (request.body != null) {
                new FileHelper().writeToFile("request_body", request.body);
            }

            if (request.contentType.isMultiPart()) {
                MultipartContainer result = MultipartParser.parse(request.contentType.boundary, HttpConstants.DEFAULT_CHARSET, request.body);
                for (MultipartEntry entry : result) {
                    if (entry.getType() == sit.web.multipart.TYPES.BINARY) {

                        MPFileEntry fEntry = (MPFileEntry) entry;
                        if (fEntry.getFileName() != null) {
                            
                            String mimeType = MimeTypes.getMimeTypeFromFileName(fEntry.getFileName());
                            UploadCallHandler.handleUploadedFile(getHoster(params), fEntry.getFileName(), fEntry.getFileContent(), mimeType);
                        }
                    }
                }
            }
            return "OK";
        } catch (IOException ex) {
            Logger.getLogger(UploadCallHandler.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return "fail";
            //return EndpointHelper.logAccessExceptionAndPrepareErrorMessage(UploadCallHandler.class, ex, request, "unknown", "unknown"); 
        }
    }
    
    private static boolean isImage(String mimeType){
        return (mimeType.equals("image/jpeg") || mimeType.equals("image/png"));
        
    }

    public static ResourceItem handleUploadedFile(String hoster, String filename, byte[] content, String mimeType) throws IOException {
        Logger.getLogger(UploadCallHandler.class.getName()).log(Level.INFO, "received file:" + filename);
        
        
        
        String internalFileName = UUID.randomUUID().toString()+"."+(new FileHelper()).getExtention(filename);        
        
        
        new FileHelper().writeToFile((new File(RESOURCE_PATH + internalFileName)).getAbsolutePath(), content)
                ;
        try {
            ResourceItem item = (ResourceItem) ItemFactory.createNewItemByType(eu.dime.model.TYPES.RESOURCE);
            item.setUserId(Model.ME_OWNER);
            ModelRequestContext mrc = new ModelRequestContext(hoster, Model.ME_OWNER, new DummyLoadingViewHandler());
            
        
            item.setDownloadUrl(RESOURCE_SUB_PATH + internalFileName);
            item.setMimeType(mimeType);
            
            if (isImage(mimeType)){
                BufferedImage thmbImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                ByteArrayInputStream bis = new ByteArrayInputStream(content);

                thmbImage.createGraphics().drawImage(ImageIO.read(bis).getScaledInstance(100, 100, Image.SCALE_SMOOTH),0,0,null);

                if (mimeType.equals("image/jpeg")){
                    item.setImageUrl(RESOURCE_SUB_PATH +THUMB_PREFIX+ internalFileName );
                    ImageIO.write(thmbImage, "jpg", new File(RESOURCE_PATH+THUMB_PREFIX+ internalFileName));
                }else if (mimeType.equals("image/png")){
                    item.setImageUrl(RESOURCE_SUB_PATH +THUMB_PREFIX+ internalFileName );
                    ImageIO.write(thmbImage, "png", new File(RESOURCE_PATH+THUMB_PREFIX+ internalFileName));
                }
            }//TODO set other thumbnail if no picture
            
            item.setName(filename);
            item.setFileSize(content.length);
            
            ResourceItem newItem = (ResourceItem) Model.getInstance().createItem(mrc, item);
            DatabaseAccess.sendNotification(mrc, NotificationItem.OPERATION_CREATE, newItem);
            return newItem;
        } catch (CreateItemFailedException e) {
            Logger.getLogger(UploadCallHandler.class.getName()).log(Level.SEVERE, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public String getName() {
        return this.getClass().getName();
    }
}
