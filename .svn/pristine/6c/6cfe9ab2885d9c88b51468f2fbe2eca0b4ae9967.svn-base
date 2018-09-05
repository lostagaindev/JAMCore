package com.lostagain.Jam.SceneObjects.Helpers;

import java.util.logging.Logger;

import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;

/**
 * Functions to help deal with inventory items
 * 
 * @author darkflame
 *
 */
public class InventoryObjectHelpers {
	public static Logger	Log								= Logger.getLogger("JAMCore.InventoryObjectHelpers");
	
	/**
	 * This helper should be run when a inventory item is clicked
	 * 
	 * @param thisWasClicked
	 */
	public static void onInventoryItemClick_HELPER(IsInventoryItem thisWasClicked) {
	
		int clickClientX = CurrentScenesVariables.lastclickedscreen_x;
		int clickClientY = CurrentScenesVariables.lastclickedscreen_y;
	
		// first we check if we are holding something
		// if so we do a mix action instead of the items normally actions
		// mix actions are controlled by the inventory global actions
		if (InventoryPanelCore.isItemBeingHeldOrDragged()) {
	
			Log.info("item clicked while holding a object");
	
			IsInventoryItem item1 = InventoryPanelCore.getCurrentItemBeingHeldOrDragged();
	
			Log.info("item held:" + item1.getName());
			Log.info("item clicked:" + thisWasClicked.getName());
	
			InventoryPanelCore.testForItemMix(item1, thisWasClicked);
	
			// unhold (should we only unhold if the above mix wa successful?
			InventoryPanelCore.unholdItem();
	
			return;
			
		} else {
			
			Log.info("inventory item clicked");
			
		}
	
		//
	
		SceneObject.iconclickedrecently = true;
	
		thisWasClicked.wasLastObjectClicked(); // newly added
	
		boolean actionsfound = false;
	
		if (thisWasClicked.getObjectsActions() != null) {
			
			CommandList actionsToRunForMouseClick = thisWasClicked.getObjectsActions()
					.getActionsForTrigger(TriggerType.MouseClickActions, null);
			
			Log.info("_________________Running inventory object actions "+actionsToRunForMouseClick.size());
			
			InventoryObjectHelpers.processActionsFromThisInventoryItem(thisWasClicked, actionsToRunForMouseClick, clickClientX, clickClientY);
			
			actionsfound = true;
			
		}
	
		if (InventoryPanelCore.globalInventoryActions != null) {
	
			CommandList defaultMouseClickActions = InventoryPanelCore.globalInventoryActions
					.getActionsForTrigger(TriggerType.MouseClickActions, null);
			// Log.info("_________________Event.BUTTON_Click inv2 ");
			
			Log.info("_________________Running global inventory object actions "+defaultMouseClickActions.size());
			
			InventoryObjectHelpers.processActionsFromThisInventoryItem(thisWasClicked, defaultMouseClickActions, clickClientX, clickClientY);
			actionsfound = true;
		}
		

	
		// we used to look if actions where found, and if not raise the
		// inventory to the front for any clicks
		if (!actionsfound) {
			// raise inventory panel to front
			// MyApplication.allInventoryFrames.get(NativeInventoryPanel.Title).setZIndexTop();
			//
		}
		
	}

	/**
	 * 
	 * @param inventoryItem
	 * @param clickClientX
	 *            - can be supplied but not really used for inventory items
	 * @param clickClientY
	 */
	public static void onInventoryItemRightClick_HELPER(IsInventoryItem inventoryItem) {
	
		int clickClientX = CurrentScenesVariables.lastclickedscreen_x;
		int clickClientY = CurrentScenesVariables.lastclickedscreen_y;
	
		if (inventoryItem.getObjectsActions() != null) {
			CommandList localRightClickActions = inventoryItem.getObjectsActions()
					.getActionsForTrigger(TriggerType.MouseRightClickActions, null);
	
			InventoryObjectHelpers.processActionsFromThisInventoryItem(inventoryItem, localRightClickActions, clickClientX, clickClientY);
		}
	
		if (InventoryPanelCore.globalInventoryActions != null) {
			CommandList defaultRightClickActions = InventoryPanelCore.globalInventoryActions
					.getActionsForTrigger(TriggerType.MouseRightClickActions, null);
			InventoryObjectHelpers.processActionsFromThisInventoryItem(inventoryItem, defaultRightClickActions, clickClientX, clickClientY);
		}
	}

	/**
	 * 
	 * @param item
	 * @param actions
	 * @param clientX
	 *            - where we were clicked, screen relative (note; not reallyused
	 *            for inventory items)
	 * @param clientY
	 */
	public static void processActionsFromThisInventoryItem(IsInventoryItem item, CommandList actions, int clientX,
			int clientY) {
		if (!actions.isEmpty()) {
	
			item.wasLastObjectUpdated();
	
			// if (Event.getCurrentEvent()!=null){
			int x = clientX;// Event.getCurrentEvent().getClientX();
			int y = clientY;// Event.getCurrentEvent().getClientY();
			// last click vals dont make sense for inventory objects anyway
			CurrentScenesVariables.lastclicked_x = x;// +super.getAbsoluteLeft();
			CurrentScenesVariables.lastclicked_y = y;// +super.getAbsoluteTop();
	
			CurrentScenesVariables.lastclickedscreen_x = x;
			CurrentScenesVariables.lastclickedscreen_y = y;
			// }
	
			InstructionProcessor.processInstructions(actions, "iitema_" + item.getObjectsCurrentState().ObjectsName,
					(SceneObject) item); // casting to scene object should be
			// safe, as ultimately it should
			// extend it
	
		}
	}

}
