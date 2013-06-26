package eu.dime.model;

import java.util.logging.Level;
import java.util.logging.Logger;
import sit.sstl.StrictSITEnumContainer;

public class PSMapEntry<TYPE extends GenItem> implements StrictSITEnumContainer<TYPES>{

    private final TYPES mType;
    public final String type;
    private final Class<TYPE> typeClass;
    public final TYPES parentType;
    public final TYPES childType;
    public final String path;
    public final boolean isShareable;

    public PSMapEntry(TYPES mType, String type, Class<TYPE> typeClass, TYPES parentType, TYPES childType, String path, boolean isShareable) {
        this.mType = mType;
        this.type = type;
        this.typeClass = typeClass;
        this.parentType = parentType;
        this.childType = childType;
        this.path = path;
        this.isShareable = isShareable;
        if (!type.equals(path)){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "On setting up the model: Type and path missmatch:(type/path) " + type+"/"+path);
        }
        if ((parentType!=null) && (childType!=null)){
            Logger.getLogger(getClass().getName()).log(Level.WARNING, "On setting up the model: both parentType and childType is set:(parent/child)"  +type+": " + parentType+"/"+childType);
        }
    }
    
    public TYPES getEnumType() {
        return mType;
    }

    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PSMapEntry<TYPE> other = (PSMapEntry<TYPE>) obj;
        if (this.mType != other.mType) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.mType != null ? this.mType.hashCode() : 0);
        return hash;
    }

    public Class<TYPE> getTypeClass() {
        return typeClass;
    }

    @SuppressWarnings("unchecked")
	public TYPE castToClassType(GenItem item){
        try {
            return (TYPE) item;
        } catch(ClassCastException ex){
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
