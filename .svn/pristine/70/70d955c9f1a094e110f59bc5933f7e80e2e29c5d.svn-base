package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;
import com.lostagain.Jam.SceneObjects.InventoryObjectState;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.IconMode;

/**
 * A widget that implements this interface has a associated popup that can be triggered
 * as well as a native inventory panel it belongs too 
 * 
 * */
public interface IsInventoryItem extends IsSceneSpriteObject, hasUserActions {


	/** KeepHeld mode - normally a item is dropped back into the inventory after use.
	 * This mode determines if it should be kept held  on certain conditions **/
	public enum KeepHeldMode {
		never,onuse
	}


	public InventoryPanelCore getNativeInventoryPanel();


	public void setPopedUp(Boolean settothis);

	public boolean isPopedUp();


	public void setKeepHeldMode(KeepHeldMode mode);
	public KeepHeldMode getKeepHeldMode();




	/**
	 * returns the name of the inventory item associated with this icon
	 * this should be the same as .sceneobjectsstate().objectsname
	 * @return
	 */
	public String getName();

	/**
	 * should trigger a popup to pop if there is one
	 */
	public void triggerPopup();


	public IsInventoryItemPopupContent getPopup();


	/**
	 * This gets the widget that represents the visuals needed for a popup
	 * For GWT apps, this will be something that implements "asWidget()"
	 * @return
	 */
	//public Object getPopupImpl();


	public enum IconMode {		
		Image,Text,CaptionedImage		
	}


	public SceneObject getAssociatedSceneObject();

	void updateState(InventoryObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);


	//subclasses have to override these to provide their own more specific types
	@Override	
	public InventoryObjectState getObjectsCurrentState();

	@Override
	public InventoryObjectState getTempState();

	@Override
	public InventoryObjectState getInitialState();


	/**
	 * Setws the popup to appear at a specific zindex
	 * @param expectedZIndex
	 */
	public void setFixedZdepth(int expectedZIndex);


	public void setPopupsBackgroundToTransparent();


	/**
	 * In future inventorys might have two or more modes that visually represent them (ie, text or text and image).
	 * This might enable gameplay such as selecting a concept from a text list
	 * @param currentInventoryMode
	 */
	public void setIconMode(IconMode currentInventoryMode);


	/**
	 * should trigger the inventorys onItemAdded commands - first local then global
	 */
	public void fireOnItemAddedToInventoryCommands();


	/**
	 * fired if the item is picked up
	 * @param b
	 */
	public void triggerPickedUp(boolean b);


	public void triggerPutDown();
	


	




}
