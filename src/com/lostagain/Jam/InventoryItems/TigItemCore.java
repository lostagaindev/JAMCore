package com.lostagain.Jam.InventoryItems;

import java.util.logging.Logger;

import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;

/**
 * The core class for a Togglable Item Group
 * TIGs are almost like mini-scenes, designed for puzzles that can be put into the inventory.
 * 
 * They allow images that can be clicked on to change them between specific states, as well as the testing
 * of if they are all in the "correct" state
 * This lets many types of puzzles be implemented
 * 
 * @author darkflame
 *
 */
public abstract class TigItemCore implements IsInventoryItemPopupContent{
	
	static Logger Log = Logger.getLogger("JAMcore.TigItemCore");
	protected String CoreName = "";

	protected boolean disabled=false;
	public static TigItemCore lastclickedTig;
	
	public TigItemCore(String itemName) {

		CoreName = itemName;
	}


	public void disable() {

		disabled=true;


	}

	public abstract int getAbsoluteTopOfImage();
	
	public abstract int getAbsoluteLeftOfImage();
	

	/**
	 * adds a arbitary visual element to part of the tig.
	 * In GWT this must be a widget.
	 * 
	 * @param addThis
	 * @param left
	 * @param Top
	 */
	public abstract void add(Object addThis,int left,int Top);
	
	
	public abstract void setFeedbackText(String text);


	//TODO: This should be moved to core once TigItem does not relay on visual component
	public abstract void testCombination();


	//TODO: This should be moved to core once TigItem does not relay on visual component
	public abstract void setItemState(String itemnamesearch, String state);


	public abstract void triggerPopUpMessage(String currentProperty);
	
	
	
}
