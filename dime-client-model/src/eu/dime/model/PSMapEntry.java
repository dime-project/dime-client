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
