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

import java.util.Comparator;
import java.util.List;

import eu.dime.model.specialitem.advisory.AdvisoryItem;
import eu.dime.model.specialitem.usernotification.UserNotificationItem;
import eu.dime.model.GenItem;
import eu.dime.model.displayable.DisplayableItem;
import eu.dime.model.displayable.LivePostItem;
import eu.dime.model.displayable.PlaceItem;
import eu.dime.model.displayable.SituationItem;

public class ComparatorHelper {
	
	public static class SituationComparator implements Comparator<SituationItem> {
		
		private final boolean trueLow;

		public SituationComparator(boolean trueLow) {
			this.trueLow = trueLow;
		}

		@Override
		public int compare(SituationItem si1, SituationItem si2) {
			int result = 0;
			result =  (si1.isActive() ^ si2.isActive()) ? ((si1.isActive() ^ this.trueLow) ? 1 : -1) : 0;
			if(result == 0) {
				if(si1.getScore() < si2.getScore()) result = -1;
				if(si1.getScore() > si2.getScore()) result = 1;
			}
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof SituationComparator)) {
				return false;
			}
			return (this.trueLow == ((SituationComparator) obj).trueLow);
		}

		public int hashCode() {
			return (this.trueLow ? -1 : 1) * getClass().hashCode();
		}
		
	}

	public static class LivePostComparator implements Comparator<DisplayableItem> {

		@Override
		public int compare(DisplayableItem o1, DisplayableItem o2) {
			if (o1 instanceof LivePostItem && o2 instanceof LivePostItem) {
				long t1 = ((LivePostItem) o1).getCreated();
				long t2 = ((LivePostItem) o2).getCreated();
				if(t2 > t1) {
		            return 1;
				} else if(t1 > t2) {
		            return -1;
				} else {
		            return 0;
				}
			}
			return 0;
		}
		
	}
	
	public static class UserNotificationComparator implements Comparator<UserNotificationItem> {
		
		@Override
		public int compare(UserNotificationItem un1, UserNotificationItem un2) {
			int result = 0;
			result =  (un1.isRead() ^ un2.isRead()) ? (un1.isRead() ? 1 : -1) : 0;
			if(result == 0) {
				result =  (int) (un2.getCreated() - un1.getCreated());
			}
			return result;
		}
		
	}
	
	public static class StandardComparator implements Comparator<DisplayableItem> {
		
		private List<String> selectedItems;
		
		public StandardComparator(List<String> selectedItems){
			this.selectedItems = selectedItems;
		}

		@Override
		public int compare(DisplayableItem o1, DisplayableItem o2) {
			if (o1 instanceof DisplayableItem && o2 instanceof DisplayableItem) {
				DisplayableItem item1 = (DisplayableItem) o1;
				DisplayableItem item2 = (DisplayableItem) o2;
				boolean isItem1selected = selectedItems.contains(item1.getGuid());
				boolean isItem2selected = selectedItems.contains(item2.getGuid());
				if((isItem1selected && isItem2selected) || (!isItem1selected && !isItem2selected)){
					return (int) (item1.getName().compareToIgnoreCase(item2.getName()));
				} else {
					if(isItem1selected){
						return (int) -1;
					} else {
						return (int) 1;
					}
				}
			}
			return 0;
		}
		
	}
	
	public static class NameComparator implements Comparator<DisplayableItem> {

		@Override
		public int compare(DisplayableItem o1, DisplayableItem o2) {
			if (o1 instanceof DisplayableItem && o2 instanceof DisplayableItem) {
				DisplayableItem item1 = (DisplayableItem) o1;
				DisplayableItem item2 = (DisplayableItem) o2;
				return (int) (item1.getName().compareToIgnoreCase(item2.getName()));
			}
			return 0;
		}
		
	}
	
	public static class WarningLevelComparator implements Comparator<AdvisoryItem> {

		@Override
		public int compare(AdvisoryItem a1, AdvisoryItem a2) {
			if(a1 instanceof AdvisoryItem && a2 instanceof AdvisoryItem) {
				AdvisoryItem w1 = a1;
				AdvisoryItem w2 = a2;
				if (w1.getWarningLevel() > w2.getWarningLevel()){
					return -1;
				} else if(w1.getWarningLevel() < w2.getWarningLevel()){
					return 1;
				}
			}
			return 0;
		}
		
	}
	
	public static class DummyComparator implements Comparator<DisplayableItem> {
		
		@Override
		public int compare(DisplayableItem o1, DisplayableItem o2) {
			return 0;
		}
		
	}
	
    public static class RatingComparator implements Comparator<DisplayableItem> {

        public int compare(DisplayableItem o1, DisplayableItem o2) {
        	if (o1 instanceof PlaceItem && o2 instanceof PlaceItem) {
                PlaceItem s1 = (PlaceItem) o1;
                PlaceItem s2 = (PlaceItem) o2;
                return (int) ((s2.getSocRating() - s1.getSocRating()) * 100.0);
            }
            return 0;
        }
        
    }

	public static class GUIDComparator implements Comparator<GenItem> {

		@Override
		public int compare(GenItem o1, GenItem o2) {
			if (o1 instanceof GenItem && o2 instanceof GenItem) {
				GenItem item1 = (GenItem) o1;
				GenItem item2 = (GenItem) o2;
				if (item1.getGuid() != null && item2.getGuid() != null) {
					return (int) (item1.getGuid().compareToIgnoreCase(item2.getGuid()));
				}
			}
			return 0;
		}
		
	}
}
