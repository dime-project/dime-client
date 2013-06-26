/*
 *  Description of DefaultImages
 * 
 *  @author Simon Thiel
 *  @version $Revision: $
 *  @date 26.03.2012
 */
package eu.dime.mobile.view;


import eu.dime.mobile.R;
import eu.dime.model.TYPES;
import sit.sstl.SITEnumMap;
import sit.sstl.StrictSITEnumContainer;
import sit.sstl.StrictSITEnumMap;

/**
 *
 * @author Simon Thiel
 */
public class DefaultImages {
    
    //GROUP, PERSON, DATABOX, RESOURCE, LIVESTREAM, LIVEPOST, NOTIFICATION, 
    //PROFILE, PROFILEATTRIBUTE, SITUATION, EVENT, SERVICEADAPTER, DEVICE, CONTEXT, EVALUATION, PLACE;


    static final StrictSITEnumMap<TYPES, ImageIdContainer> DefaultImageMap = new StrictSITEnumMap(
            TYPES.class,
            new ImageIdContainer[]{
         new ImageIdContainer(TYPES.GROUP, R.drawable.group),
         new ImageIdContainer(TYPES.PERSON, R.drawable.person),
         new ImageIdContainer(TYPES.DATABOX, R.drawable.data_box),
         new ImageIdContainer(TYPES.RESOURCE, R.drawable.resource),
         new ImageIdContainer(TYPES.LIVESTREAM, R.drawable.resource),
         new ImageIdContainer(TYPES.LIVEPOST, R.drawable.resource),
         new ImageIdContainer(TYPES.NOTIFICATION, R.drawable.notification),
         new ImageIdContainer(TYPES.PROFILE, R.drawable.person),
         new ImageIdContainer(TYPES.PROFILEATTRIBUTE, R.drawable.person),
         new ImageIdContainer(TYPES.SITUATION, R.drawable.situation_general),
         new ImageIdContainer(TYPES.EVENT, R.drawable.resource),
         new ImageIdContainer(TYPES.SERVICEADAPTER, R.drawable.resource),
         new ImageIdContainer(TYPES.DEVICE, R.drawable.resource),
         new ImageIdContainer(TYPES.CONTEXT, R.drawable.resource),         
         new ImageIdContainer(TYPES.EVALUATION, R.drawable.resource),
         new ImageIdContainer(TYPES.PLACE, R.drawable.resource)
    });

    public static int getDefaultImageId(TYPES type){
        return DefaultImageMap.get(type).imageId;
    }
    
    private static class ImageIdContainer implements StrictSITEnumContainer<TYPES>{
        
        TYPES type;
        int imageId;

        public ImageIdContainer(TYPES type, int imageId) {
            this.type = type;
            this.imageId = imageId;
        }

        
        

        public TYPES getEnumType() {
            return type;
        }
        
    }

}
