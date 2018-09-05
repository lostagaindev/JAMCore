package com.lostagain.Jam.Interfaces.PopupTypes;

/**
 * A widget that implements this interface is an inventory item popup and has appropriate functions for that
 * */

public interface IsInventoryItemPopupContent extends IsPopupContents {


	 public String  getSourceURL();
	 public boolean MAGNIFYABLE();
	  
	 public int  sourcesizeX();
	 public int  sourcesizeY();
	 
	 /**
	  * when poped up, what zindex should this have by default?
	  * (-1 is default which just means natural ordering)
	  * @return
	  */
	 public int getExpectedZIndex();

	 
		/**
		 * Together with loadstate this should save the state to a string that can be loaded
		 * allowing the item to return to a previous state when loaded
		 */
		public String getState();
		/**
		 * Together with getState() this should save the state to a string that can be loaded
		 * allowing the item to return to a previous state when loaded
		 */
		
		public void loadState(String state);
		
		  
}
