package com.lostagain.Jam.InventoryItems;

import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.SceneAndPageSet;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.Interfaces.IsBamfImage;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.hasInventoryButtonFunctionality;
import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;
import com.lostagain.Jam.Interfaces.PopupTypes.IsPopupContents;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.InventoryObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.IconMode;

//all methods needed to create or clone objects
/**
 * setup has to be run for this to work.
 * You MUST supply a implemented subtype (such as GWTInventoryItemFactory) in the static setup(..) function
 * for the scene object  factory - and thus the games object creation functions 
 * to work  
 * @author darkflame
 *
 */
public abstract class InventoryItemFactory {

		
	static InventoryItemFactory instance;
	
	
	/**
	 * item types (not all may be supported by subtypes of this)
	 * **/
	public enum inventoryItemTypes {
		
		concept,
		picture,
		movie,
		youtube,
		embed,
		pngpicture,
		flash,
		toggleitemgroup,
		pngtoggleitemgroup,
		overlay,
		magnifyingglass,
		textscroll,
		dummy,
		
	}
	

	public static void setup(InventoryItemFactory subClass) {
		instance = subClass;
	}
	

	/**
	 * 
	 * @param sceneObjectData - should be a subclass of SceneObjectState with the data already loaded
	 * @param actions
	 * @param sceneObjectBelongsTo
	 * @return
	 */
	public static IsImageWithAlphaItem createNewImageWithAlphaItem(String ImagelocationSet, String NewImageDiscription){
		return instance.createNewImageWithAlphaItem_impl( ImagelocationSet,  NewImageDiscription);
	}
	

	public static IsPopupPanel createImageZoomPopup(
			IsInventoryItem zoomThis, 
			String ImageURL,
			int sizex,
			int sizey,
			String imageDiscription,
			String popupTitle)	
	{
		return instance.createImageZoomPopup_impl(zoomThis,  ImageURL,  sizex,  sizey,  imageDiscription,  popupTitle);		
	}
	
	/**
	 * creates the popup that normally triggers when the inventory item is clicked on
	 * 
	 * @param ItemsType
	 * @param ItemsName
	 * @param ItemsDiscription
	 * @param ItemsURL
	 * @param ItemsTitle
	 * @param size_x
	 * @param size_y
	 * @param is_magnifiable
	 * @param Embed
	 * @return
	 */
	static public IsInventoryItemPopupContent createInventoryItemsPopupContents(String ItemsTypeString, String ItemsName,
			String ItemsDiscription, String ItemsURL, String ItemsTitle, String size_x, String size_y,
			boolean is_magnifiable, String Embed){
		
		
		inventoryItemTypes itemsType = inventoryItemTypes.valueOf(ItemsTypeString.toLowerCase());
		
		return instance.createInventoryItemsPopupContents_impl(itemsType, ItemsName, ItemsDiscription, ItemsURL, ItemsTitle, size_x, size_y, is_magnifiable, Embed);
	}

	public static IsInventoryItem createInventoryItem(IsInventoryItemPopupContent itemPopUp, String title,
			InventoryPanelCore this_inventory, InventoryObjectState iconsstate, String itemDataString) {
		return instance.createInventoryItem_impl(itemPopUp,  title, this_inventory,iconsstate,itemDataString);
	}
	
	

	public abstract IsImageWithAlphaItem createNewImageWithAlphaItem_impl(String ImagelocationSet, String NewImageDiscription);

	
	public abstract IsPopupPanel createImageZoomPopup_impl(IsInventoryItem zoomThis, String ImageURL, int sizex, int sizey, String imageDiscription, String popupTitle);


	public abstract IsInventoryItem createInventoryItem_impl(IsInventoryItemPopupContent itemPopUp, String title,
			InventoryPanelCore this_inventory, InventoryObjectState iconsstate, String itemDataString);

	/**
	 * creates the popup that normally triggers when the inventory item is clicked on
	 *
	 */
	public abstract IsInventoryItemPopupContent createInventoryItemsPopupContents_impl(inventoryItemTypes ItemsType, String ItemsName,
			String ItemsDiscription, String ItemsURL, String ItemsTitle, String size_x, String size_y,
			boolean is_magnifiable, String Embed);


	
		

	
	
	
		
	
}
