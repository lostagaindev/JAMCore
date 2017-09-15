package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.InstructionProcessing.ActionSet;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.hasCloseDefault;
import com.lostagain.Jam.Interfaces.hasInventoryButtonFunctionality;
import com.lostagain.Jam.Interfaces.hasOpenDefault;
import com.lostagain.Jam.Interfaces.PopupTypes.IsPopupContents;
import com.lostagain.Jam.InventoryItems.InventoryItemFactory;
import com.lostagain.Jam.InventoryItems.ItemMixRequirement;
import com.lostagain.Jam.InventoryPanelCore.itemState;
import com.lostagain.Jam.Interfaces.PopupTypes.IsImagePopUpContents;
import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;
import com.lostagain.Jam.SceneObjects.InventoryObjectState;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.IconMode;

/**<br>
 * The JAM game engine supports unlimited inventory panels. The idea being each can be a different type.<br>
 * An inventory for items, An inventory for "Concepts", A inventory for people....etc.<br>
 * It supports adding, removing, and testing the presence of items in these panels as well as if one item is used on another.<br>
 * Items can also be triggered on their own, as they can have their own actions or popups.<br>
 * <br>
 * This class contains all of that management, as well as the process of creating new items.<br>
 * It will not, however, deal with the visual side or the dragging and dropping - it uses abstracts and expects things implementing it to
 * deal with that. Assuming they want a visual interface to the inventory at all that is.<br>
 * <br>
 * Inventorys themselves can also fire commands merely from being open/closed<br>
 * See oninventoryopen=###: used in globalitemactions<br>
 * (this is mostly used to hock up a open sound effect)<br>
 * <br>
 * @author darkflame
 *
 */
public abstract class InventoryPanelCore implements hasCloseDefault,hasOpenDefault,IsPopupContents {

	public static Logger Log = Logger.getLogger("JAMcore.InventoryPanelCore");

	
	/**
	 * This will be added to the start of all pocketed items to find its associated inventory name.
	 * ie
	 * LazerTape when picked up on the scene will look for INV_LazerTape as its inventory item
	 */
	public static final String PocketedPrefix = "INV_";

	/** if an inventory item has no supplied actions, these are the default ones **/
	protected static final ActionList defaultActions = new ActionList(
			" MouseClickActions: \n " + " - OpenItem = <LASTINVENTORYITEM> ");

	/**
	 * is an item currently being held?
	 */
	public static boolean isItemCurrentlyBeingHeld = false;
	public static boolean isItemCurrentlyBeingDragged = false; //should we even distinguish between held and dragged?

	/** stores the item currently being held **/
	public static IsInventoryItem currentlyHeldItem = null;
	public static IsInventoryItem currentlyDraggedItem = null;

	public enum itemState {
		Unknown,
		/**
		 * Item currently loading
		 */
		Loading,
		/**
		 * Same as loading, but item should be held after its finnished loading
		 */
		Loading_HoldAfter,
		/**
		 * Item loaded, and ready (not necessarily in inventory however - an item loaded to a inventory then removed remains loaded for instant re-adding)
		 */
		Loaded, 
		/**
		 * items file not found
		 */
		Failed		
		
	}
	
	/**
	 * Keeps track of all items requested to load and their current state
	 */
	public static HashMap<String,itemState> itemLoadStates = Maps.newHashMap();
	
	/**
	 * is this inventory currently open?
	 * @param ItemName
	 * @return
	 */
	protected boolean isOpen = false;
	public boolean isOpen() {
		return isOpen;
	}


	public static boolean playerHasItem(final String ItemName) {

		// loop over each inventory to check
		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys
				.values().iterator();


		while (inventorys.hasNext()) {

			InventoryPanelCore currentinventoryPanel = (InventoryPanelCore) inventorys
					.next();

			if (currentinventoryPanel.inventoryContainsItem(ItemName)) {
				return true;
			}
		}

		return false;

	}

	public static IsInventoryItem getItemFromName(final String ItemName) {

		// loop over each inventory to check
		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys.values().iterator();

		while (inventorys.hasNext()) {

			InventoryPanelCore currentinventoryPanel = (InventoryPanelCore) inventorys.next();

			//note; the following might be better combined into one function
			if (currentinventoryPanel.inventoryContainsItem(ItemName)) {
				return currentinventoryPanel.getItem(ItemName);
			}
			
			
			
		}

		return null;

	}

	/**
	 * The button associated with this inventory.
	 * It should allow the opening/closing of this inventory, and optionally represent that state. (ie, picture of a bag open, picture of a bag closed)
	 */
	private hasInventoryButtonFunctionality InventorysButton = null;

	/**
	 * Can this inventory contain duplicates?
	 * NOTE: currently there might be issues when removing duplicates.
	 * Check implementation of this class, HashMaps are 1<>1 relationships and probably not suitable to use
	 * as a method to associate items to drag controllers if there is identical items. (at least in terms of names)
	 */
	protected boolean noduplicates = true;


	//We now use the new system as its more reliable
	//public final boolean itemSpace[] = new boolean[100];

	/**
	 * We now, more robustly, store the items in slots directly
	 **/
	public final IsInventoryItem itemSpace_new[] = new IsInventoryItem[50];


	public String InventorysName = "Inventory default";
	protected IconMode CurrentInventoryMode = IconMode.Image;
	public final ArrayList<IsInventoryItem> inventorysCurrentItems = new ArrayList<IsInventoryItem>();

	/*** 
	 * Last position picked up from.<br>
	 * This is so after a drag is canceled we can return it to where it came from originally <br>
	 ***/
	public int old_LinerPos = -1;

	protected int iconSizeX = 100;

	protected int iconSizeY = 100;

	protected int ScreenSizeX = RequiredImplementations.getCurrentGameStageWidth();

	protected int ScreenSizeY = RequiredImplementations.getCurrentGameStageHeight();

	protected int InventorySizeX = (100 * (ScreenSizeX / 150));

	protected int InventorySizeY = (100 * (ScreenSizeY / 175));

	protected int roundToX = 100;

	protected int roundToY = 100;

	protected int RInventorySizeX = (int) ((InventorySizeX + (0.5 * roundToX)) / roundToX)   	  * roundToX;

	protected int RInventorySizeY = 20 + (int) ((InventorySizeY + (0.5 * roundToY)) / roundToY) * roundToY;

	/**
	 * We now use itemMixCommands
	 */
	@Deprecated
	public static String itemmixscript = "";

	static ArrayList<ItemMixRequirement> itemMixCommands;

	// change this to default inventory
	//This could be moved to the core...maybe havw a "setup inventorys" method?
	//We then refer to that defaultInventory in the core every time we can instead of this
	public static InventoryPanelCore defaultInventory;// = new InventoryPanel("Inventory Items", IconMode.Image);
	public static ActionList globalInventoryActions = new ActionList();


	/**
	 * Gets the inventory button associated with this inventory panel<br>	  
	 * @return the inventorysButton
	 **/
	public hasInventoryButtonFunctionality getInventorysButton() {
		return InventorysButton;
	}

	/**
	 * Sets the inventory button associated with this inventory panel
	 * 
	 * @param inventorysButton the inventorysButton to set
	 */
	public void setInventorysButton(hasInventoryButtonFunctionality inventorysButton) {
		InventorysButton = inventorysButton;
	}

	public String getPanelsMode() {
		return CurrentInventoryMode.toString();
	}

	public InventoryPanelCore(String inventoryName, IconMode mode) {		

		InventorysName   			 = inventoryName;
		CurrentInventoryMode = mode;

		Log.info(" making inventory:"+inventoryName);
		Log.info(" getting inventory:"+ JAMcore.inventory_url + "GlobalItemActions.ini" );

		if (globalInventoryActions.isEmpty()) {
			LoadGlobalInventoryActions();
		}



	}

	//abstracts to be implemented by subclasses
	//some of these abstracts functions can be brought inline to the Core once other refractoring is done

	public abstract void ClearInventory(); //should be possible to inline to this class

	//public abstract boolean RemoveItem(String currentProperty); //now inlined to this class

	/**
	 * removes the icon from the inventory panel in terms of visuals.
	 * Do whatever you need for the specific implementation here.
	 * @param currentProperty
	 */
	public abstract void physicallyRemoveItem(IsInventoryItem icon); 
	

	//public abstract String[] getArrayOfAllItemNames(); //should be possible to inline to this class

	//public abstract boolean inventoryContainsItem(String itemName); //should be possible to inline to this class

	/**
	 * after adding the item onto the page, you must run addAlreadyAttachItemLogically(
	 * @param runwhendone
	 * @param TriggerPopUpAfterLoading
	 * @param existing
	 */
	public abstract void attachPreparedInventoryItemToPanel(Runnable runwhendone,boolean TriggerPopUpAfterLoading, IsInventoryItem existing);
	
	
	/**
	 * resizes the inventory as the screen size just changed
	 */
	public abstract void ResizeAndReorder(); 

	public void AddItem(final String ItemName) {
		this.AddItem(ItemName, false,null); //defaults

	}

	/** adds an item **/
	public void AddItem(final String ItemName, 
						boolean TriggerPopUp,
						final SceneObject associatedObjectName) {
		
		//set the ItemNames state if it does not already have a state
		if (itemLoadStates.get(ItemName) == null ){ //NOTE: it might already be set to Loading_HoldAFter and we dont want to  remove that setting
			itemLoadStates.put(ItemName, itemState.Loading); 
		}

		Log.info("AddItem requested:"+ItemName);

		//we now always check for a hold action as a hold action can be set AFTER the initial load trigger
		//(for example if a "-pocketitem" was fun followed by a "-holditem" straight away
		final Runnable runwhendone = new Runnable(){
			@Override
			public void run() {				

				Log.info("testing hold state for;"+ItemName);
				if (InventoryPanelCore.itemLoadStates.get(ItemName) == itemState.Loading_HoldAfter){					
					Log.info("Inventory item "+ItemName+" loaded. Now running hold item");				
					holdItem(ItemName);
				}				
				InventoryPanelCore.itemLoadStates.put(ItemName, itemState.Loaded);				
			}			
		};
		
		if (runwhendone!=null){
			Log.info("runwhendone was requested");			
		}


		//Check for dups
		if (noduplicates){						
			if (inventoryContainsItem(ItemName.trim())){
				Log.info("already have item");		
				
				//fire run when done (if any)
				if (runwhendone!=null){ //without this null check runnables that are null seem to behave VERY strangly. Somehow the runwhendone gets the value of whats set in "AddItemAndHold" - despite the fact no logs for it are triggered and that function isnt triggered from anywhere
					runwhendone.run();
				}				
				return;
			}
		}

		

		final boolean TriggerPopUpAfterLoading = TriggerPopUp;
		
		//Test item already exists in database before triggering load
		//Maybe some use of genetics could remove the type casting here?
		Log.info("testing for existing item:"+ItemName);
		IsInventoryItem existing = (IsInventoryItem) SceneObjectDatabase.getSingleObjectOfType(ItemName, null, true, SceneObjectType.InventoryObject);
		//
		if (existing!=null){			
			Log.info("already know about item, so just attaching to this inventory");	
			existing.ObjectsLog("AddItem was requested again for this item, so reattaching to inventory");			
			attachPreparedInventoryItemToPanel(runwhendone, TriggerPopUpAfterLoading, existing);			
			return; //exit, job done already!
		}		
		

		/*
			Log.info("AddItem requested:"+ItemName);
	
			//we now always check for a hold action as a hold action can be set AFTER the initial load trigger
			//(for example if a "-pocketitem" was fun followed by a "-holditem" straight away
			final Runnable runwhendone = new Runnable(){
				@Override
				public void run() {				
					if (InventoryPanelCore.itemLoadStates.get(ItemName) == itemState.Loading_HoldAfter){					
						Log.info("Inventory item loaded. Now running hold item");				
						holdItem(ItemName);
					}				
					InventoryPanelCore.itemLoadStates.put(ItemName, itemState.Loaded);				
				}			
			};
			
			if (runwhendone!=null){
				Log.info("runwhendone was requested");			
			}
	
			final boolean TriggerPopUpAfterLoading = TriggerPopUp;
	
			//Check for dups
			if (noduplicates){						
				if (inventoryContainsItem(ItemName.trim())){
					Log.info("already have item");		
					
					//fire run when done (if any)
					if (runwhendone!=null){ //without this null check runnables that are null seem to behave VERY strangly. Somehow the runwhendone gets the value of whats set in "AddItemAndHold" - despite the fact no logs for it are triggered and that function isnt triggered from anywhere
						runwhendone.run();
					}				
					return;
				}
			}
			
			
	
			
			//Test item already exists in database before triggering load
			//Maybe some use of genetics could remove the type casting here?
			Log.info("testing for existing item:"+ItemName);
			InventoryItem existing = (InventoryItem) SceneObjectDatabase.getSingleObjectOfType(ItemName, null, true, SceneObjectType.InventoryObject);
			//
			if (existing!=null){			
				Log.info("already know about item, so just attaching to this inventory");	
				existing.ObjectsLog("AddItem was requested again for this item, so reattaching to inventory", "green");			
				attachPreparedInventoryItemToPanel(runwhendone, TriggerPopUpAfterLoading, existing);			
				return; //exit, job done already!
			}		
			//
			*/
	
			
			// get item ini location
			final String itemlocation = JAMcore.inventory_url + ItemName + "/" + ItemName	+ ".jam";
	
			
						
			//new method
			FileCallbackRunnable OnFileSuccess = new FileCallbackRunnable() {
	
				@Override
				public void run(String responseData, int responseCode) {
	
					
					String ItemDataString = "Type = Concept"; //default
					
					if (responseCode==FileResponseCodes.SC_OK  || responseCode==0){
	
						Log.info("Got data from inventory item:"+ItemName);
						ItemDataString = responseData;
	
					} else if (!JAMcore.DebugMode) {
	
						Log.warning("No JAM file found for:"+ItemName+" ResponseCode was:"+responseCode+" "
								+ "This can happen when something exists in the scene but theres no inventory equilivant when picked up."
								+ "somethings this is intentional (like shadows attached to a object dont need a inventory version)");
						JAMcore.itemsLeftToLoad--;						
						itemState oldstate = InventoryPanelCore.itemLoadStates.put(ItemName, itemState.Failed);						
						runwhendone.run(); //run post add requests straight away
						
						return;
	
					} else {
						Log.warning("No JAM file found for:"+ItemName+" ResponseCode was:"+responseCode+" "
								+ "This can happen when something exists in the scene but theres no inventory equilivant when picked up."
								+ "somethings this is intentional (like shadows attached to a object dont need a inventory version)");
					
						JAMcore.itemsLeftToLoad--;						
						itemState oldstate = InventoryPanelCore.itemLoadStates.put(ItemName, itemState.Failed);						
						runwhendone.run(); //run post add requests straight away
						
						return;
		
						
					}
	
				//	Log.info("res=" + ItemDataString);	
	
					
					Log.info("(Data recieved, making Item called:)"+ItemName);
					createInventoryItemFromData(ItemName, associatedObjectName, runwhendone, TriggerPopUpAfterLoading,ItemDataString);
				
					
				}
	
			};
			FileCallbackError OnFileError = new FileCallbackError() {
	
				@Override
				public void run(String errorData, Throwable exception) {
					
					Log.severe("could not find file for:"+ItemName+" searched at:"+itemlocation);
					Log.severe("errorData:\n"+errorData);
					
					JAMcore.itemsLeftToLoad--;
				}
			};
	
			
	
			JAMcore.itemsLeftToLoad++;
			RequiredImplementations.getFileManager().getText(itemlocation,false, OnFileSuccess, OnFileError, false);
	
	
		//TODO: when more of the loading process is refractored here, we can run checks not to load stuff already loading too		
		

	}


	/**
	 * searchs and returns either ItemName or PocketedPrefixItemName
	 * (ie a search for Books might return INV_Books if PocketedPrefix is "INV_")
	 * 
	 * @param ItemName
	 * @return
	 */
	public boolean inventoryContainsItem(final String ItemName) {

		boolean hasitem = false;

		for (Iterator<IsInventoryItem> it = inventorysCurrentItems.iterator(); it
				.hasNext();) {

			IsInventoryItem currentItem = it.next();
			
			if (currentItem.getName().equalsIgnoreCase(ItemName)
					|| currentItem.getName().equalsIgnoreCase(PocketedPrefix+ItemName)) {
				hasitem = true;
			}
		}



		// we test if the player has an item
		//		for (Iterator<ItemDropController> it = itemDropControllerList.iterator(); it
		//				.hasNext();) {
		//
		//			ItemDropController currentItem = it.next();
		//			if (currentItem.itemName.equalsIgnoreCase(ItemName)) {
		//				hasitem = true;
		//			}
		//		}

		return hasitem;
	}
	
	/**
	 * searchs and returns the item called either ItemName or PocketedPrefixItemName
	 * (ie a search for Books might return INV_Books if PocketedPrefix is "INV_")
	 * 
	 * @param ItemName
	 * @return
	 */
	public IsInventoryItem getItem(final String ItemName) {

		for (Iterator<IsInventoryItem> it = inventorysCurrentItems.iterator(); it
				.hasNext();) {


			IsInventoryItem currentItem = it.next();
			
			//if (currentItem.getName().compareTo(ItemName) == 0) {
			if (currentItem.getName().equalsIgnoreCase(ItemName)
						|| currentItem.getName().equalsIgnoreCase(PocketedPrefix+ItemName)) {
				return  currentItem;
			}
		}


		// loop to get item
		//		for (Iterator<ItemDropController> it = itemDropControllerList.iterator(); it
		//				.hasNext();) {
		//
		//			ItemDropController currentItem = it.next();
		//			if (currentItem.itemName.compareTo(ItemName) == 0) {
		//				// return it
		//				//return ((InventoryIcon) currentItem.getDropTarget());
		//				
		//				return  ((SceneObjectVisual.MyFocusPanel)currentItem.getDropTarget()).getParentSceneObjectAsInventoryIcon();
		//			}
		//		}

		return null;
	}

	public String[] getArrayOfAllItemNames() {

		String[] names = new String[inventorysCurrentItems.size()];
		int i=0;


		for (IsInventoryItem idc : inventorysCurrentItems) {

			names[i]=  idc.getName();

			i++;
		}


		//		for (ItemDropController idc : itemDropControllerList) {
		//			
		//			names[i]=  idc.itemName;
		//			
		//			i++;
		//		}


		return names;



	}


	public void LoadGlobalInventoryActions() {

		Log.info("________________________________GlobalItemActions loading");
		/*
		try {
			RequestBuilder requestBuilder = new RequestBuilder(
					RequestBuilder.GET, JAMcore.inventory_url + "GlobalItemActions.ini"); //$NON-NLS-1$

			requestBuilder.sendRequest("", new RequestCallback() { //$NON-NLS-1$
				public void onError(Request request, Throwable exception) {

					Log.info("http GlobalItemActions failed");
					// load default actions instead
					globalInventoryActions = defaultActions;

				}

				public void onResponseReceived(Request request,
						Response response) {

					if (response.getStatusCode() != Response.SC_OK && response.getStatusCode()!=0) {

						Log.info("no GlobalItemActions ini found");

						Log.info("response code was:"+response.getStatusCode() );

						// load default actions instead
						globalInventoryActions = defaultActions;
						return;
					}

					String responsetext = response.getText().trim();

					// update the global actions list for the inventory
					globalInventoryActions = new ActionList(
							responsetext);

				}
			});
		} catch (RequestException ex) {
			String responsetext = "can not connect to global item controll file at:"+JAMcore.inventory_url + "GlobalItemActions.ini"; //$NON-NLS-1$
			Log.info(responsetext);
			Log.info(ex.getMessage());
			// itemmixscript = responsetext;
		}
		 */
		//new method
		FileCallbackRunnable OnFileSuccess = new FileCallbackRunnable() {

			@Override
			public void run(String responseData, int responseCode) {

				if (responseCode != FileResponseCodes.SC_OK && responseCode!=0) {
					Log.info("no GlobalItemActions ini found");
					Log.info("response code was:"+responseCode );

					// load default actions instead
					globalInventoryActions = defaultActions;
					return;
				}

				String responsetext = responseData.trim();

				// update the global actions list for the inventory if there was contents, else null
				if (responsetext.length()>3){					
					globalInventoryActions = new ActionList(responsetext);			
				} else {
					globalInventoryActions = null;
				}
			}

		};
		
		FileCallbackError OnFileError = new FileCallbackError() {

			@Override
			public void run(String errorData, Throwable exception) {

				Log.info("http GlobalItemActions failed");
				// load default actions instead
				globalInventoryActions = defaultActions;
			}
		};


		RequiredImplementations.getFileManager().getText(JAMcore.inventory_url + "GlobalItemActions.ini",false, OnFileSuccess, OnFileError, false);


	}

	/**
	 * tries to remove an item with the specified name, returns true if it finds
	 * and removes it
	 **/
	public boolean RemoveItem(String ReItemName) {

		Log.info("removing:"+ReItemName+"       ("+inventorysCurrentItems.size()+" items in "+this.getName()+" inventory)");

		// find the item from the name
		for (Iterator<IsInventoryItem> it = inventorysCurrentItems.iterator(); it.hasNext();) {


			IsInventoryItem currentIcon = it.next();

			if (currentIcon.getName().compareTo(ReItemName) == 0) {
				

				Log.info("removing:::"+ReItemName);
				removeItem(currentIcon);

				return true;
			}

		}


		//		// find the item from the name
		//		for (Iterator<ItemDropController> it = itemDropControllerList.iterator(); it
		//				.hasNext();) {
		//
		//			ItemDropController currentItem = it.next(); // No downcasting
		//														// required.
		//
		//			if (currentItem.itemName.compareTo(ReItemName) == 0) {
		//				removeItem(currentItem);
		//
		//				return true;
		//			}
		//
		//		}

		return false;
	}


	public void triggerItem(final String ItemName) {


		for (Iterator<IsInventoryItem> it = inventorysCurrentItems.iterator(); it
				.hasNext();) {


			IsInventoryItem currentItem = it.next();

			if (currentItem.getName().compareTo(ItemName) == 0) {
				// pop it up!
				Log.info("found item to open..|" + currentItem.getName() + "|");			

				currentItem.triggerPopup();

				//((SceneObjectVisual.MyFocusPanel)currentItem.getDropTarget()).getParentSceneObjectAsInventoryIcon().triggerPopup();


			}
		}


		// loop to get item
		//		for (Iterator<ItemDropController> it = itemDropControllerList.iterator(); it
		//				.hasNext();) {
		//
		//			ItemDropController currentItem = it.next();
		//			if (currentItem.itemName.compareTo(ItemName) == 0) {
		//				// pop it up!
		//				Log.info("found item to open..|" + currentItem.itemName + "|");			
		//				
		//				((SceneObjectVisual.MyFocusPanel)currentItem.getDropTarget()).getParentSceneObjectAsInventoryIcon().triggerPopup();
		//				
		//				
		//			}
		//		}

	}


	public void AddItemAndHold(final String ItemName, SceneObject associatedObjectName) {

		Log.severe("AddItemAndHold triggered");

		Log.info("AddItemAndHold triggered for: "+ItemName);

		//if (true){
		//	String meep = "";
		//	meep = meep.substring(0, 11);
		////	System.out.print("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");

		//}

/*
		Runnable holdafteruse = new Runnable(){
			@Override
			public void run() {
				Log.info("Inventory item loaded. Now running hold item");
				holdItem(ItemName);

			}			
		};
*/
		
		itemLoadStates.put(ItemName, itemState.Loading_HoldAfter); //with this we no longer need the runnable.
		
		AddItem(ItemName, false, associatedObjectName);// holdafteruse);
	}


	public IsInventoryItemPopupContent getItemPopUp(final String ItemName) {

		for (Iterator<IsInventoryItem> it = inventorysCurrentItems.iterator(); it
				.hasNext();) {

			//temp cast till get popup can be moved
			IsInventoryItem currentItem = (IsInventoryItem) it.next();


			if (currentItem.getName().compareTo(ItemName) == 0) {


				// return it
				//return ((InventoryIcon) currentItem.getDropTarget()).PopUp;
				return (IsInventoryItemPopupContent) currentItem.getPopup();

			}
		}


		// loop to get item
		//		for (Iterator<ItemDropController> it = itemDropControllerList.iterator(); it
		//				.hasNext();) {
		//
		//			ItemDropController currentItem = it.next();
		//			if (currentItem.itemName.compareTo(ItemName) == 0) {
		//				// return it
		//				//return ((InventoryIcon) currentItem.getDropTarget()).PopUp;
		//				return ((SceneObjectVisual.MyFocusPanel)currentItem.getDropTarget()).getParentSceneObjectAsInventoryIcon().PopUp;
		//				
		//			}
		//		}

		return null;
	}

	public void removeFromLinearSlotSpace(IsInventoryItem itemToRemove) {

		for (int i = 0; i < itemSpace_new.length; i++) {
			IsInventoryItem item = itemSpace_new[i];
			if (item==itemToRemove){
				itemSpace_new[i]=null;
			}
		}

	}

	public int getLinearSlotPosition(IsInventoryItem itemToRemove) {

		for (int i = 0; i < itemSpace_new.length; i++) {
			IsInventoryItem item = itemSpace_new[i];
			if (item==itemToRemove){
				return i;
			}
		}
		return -1; //not found
		

	}

	

	/**
	 * helps debug the free slots in this inventory
	 * @param itemSpace
	 */
	public void debugLinearSlotSpace() {
		String InventoryLinearSpace = this.InventorysName+"_LinearSpace: ";
		int LimitToFirst = 20; //limit to first 20 slots (Rather then all 100!)

		/*
		
		int i=0;
	
		for (boolean b : itemSpace) {
			if (b){
				InventoryLinearSpace=InventoryLinearSpace+"###,";
			} else {
				InventoryLinearSpace=InventoryLinearSpace+"___,";
			}

			i++;
			if (i>LimitToFirst){
				break;
			}
		}
*/
		//Log.info(""+InventoryLinearSpace+"\n");
		//InventoryLinearSpace="";

		int i=0;
		for (IsInventoryItem b : itemSpace_new) {
			if (b!=null){
				InventoryLinearSpace=InventoryLinearSpace+b.getName()+",";
			} else {
				InventoryLinearSpace=InventoryLinearSpace+"___,";
			}

			i++;
			if (i>LimitToFirst){
				break;
			}
		}

		Log.info("_LinearSpace:"+InventoryLinearSpace);
	}


	/** Next free slot in this inventory on the x axis **/
	public int NextFreeSlotX() {
		// work out next gap in item list.
		int pos = getNextFreeLinearPosition();
		int NextPosX = getPositionXForSlot(pos);
	
		return NextPosX;
	}


	public int getPositionXForSlot(int pos) {
		Log.info("next free inventory slot..." + pos);
		int NextPosX = ((pos) * iconSizeX) % RInventorySizeX;
		return NextPosX;
	}


	/**
	 * counting from top left to bottom right, returns the next free slot in this inventory
	 * @return
	 */
	public int getNextFreeLinearPosition() {
		/*
		int pos = 0;
		while (itemSpace[pos] == true) {
			pos = pos + 1;
		}
		if (pos > old_LinerPos && old_LinerPos>-1) { //used to be >0
			pos = old_LinerPos;
		}*/
		
	
		int pos = 0;
		while (itemSpace_new[pos] != null) {
			pos = pos + 1;
		}
	
		//if (pos > old_LinerPos && old_LinerPos>-1) { //used to be >0
		//	pos = old_LinerPos;
		//}
		
		return pos;
	}


	/** Next free slot in this inventory on the y axis **/
	public int NextFreeSlotY() {
	
		int pos = getNextFreeLinearPosition();
		int NextPosY = getPositionYForSlot(pos);
		
		return NextPosY;
	}


	public int getPositionYForSlot(int pos) {
		int NextPosX = (((pos)) * iconSizeX) % RInventorySizeX;
		int NextPosY = iconSizeY * ((((pos) * iconSizeX) - NextPosX) / RInventorySizeX);
		return NextPosY;
	}


	/** Loads the item mix script, which controls...well...item mixing.
	 * Aside from the normal adventure game like item combining, it also can control what
	 * some items are used with or trigger automatically  **/
	static public void LoadItemMixScript() {

		final String itemMixScriptControll = JAMcore.inventory_url + "ItemControllScript.txt";
		Log.info("loading mix script at:"+itemMixScriptControll);

		
		FileCallbackRunnable onSuccess = new FileCallbackRunnable(){

			@Override
			public void run(String responseData, int responseCode) {
				if (responseCode >= 400 || responseCode == 204) {
					Log.info("no item mix script found");
					return;
				}

				String responsetext = responseData;

				responsetext=JAMcore.parseForLanguageSpecificExtension(responsetext);

				//Normalize lineending
				responsetext = responsetext.replaceAll("\\r\\n", "\n");
				//
				
				//itemmixscript = responsetext;
				
				itemMixCommands = ItemMixRequirement.getAllItemMixsFromFile(responsetext);
			}

		};

		FileCallbackError onError = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				Log.severe("error when getting item mix script at:"+itemMixScriptControll);


				Log.severe("game info homedirectory:"+RequiredImplementations.BasicGameInformationImplemention.get().getHomedirectory());
				Log.severe("JAMcore.homeurl:"+JAMcore.homeurl);

				Log.severe("JAMcore.inventory_url:"+JAMcore.inventory_url);
				Log.severe("errorData:\n"+errorData);
				
			}

		};


		RequiredImplementations.getFileManager().getText(itemMixScriptControll,false,onSuccess,onError , false);

	}


	public static void addInventory(String[] CurrentParams) {

		IconMode Mode = IconMode.Image;
		
		int AniLen=6;// length of icons is normally 6 frames

		String InventoryName   = CurrentParams[0].trim();
		
		// detect if mode specified
		if (CurrentParams.length > 1) {
			
			InventoryName   = CurrentParams[0].trim();
			String ModeString = CurrentParams[1].trim().toLowerCase();
			
			if (ModeString.equalsIgnoreCase("text")) {

				Mode = IconMode.Text;
			}
			//detect if length of icons animation is specified
			if (CurrentParams.length > 2) {
				String anl = CurrentParams[2].trim();
				AniLen=Integer.parseInt(anl);
			}

		}

		Log.info("creating inventory called:"+InventoryName);
		//Needs to be changed  to factory method
		final InventoryPanelCore newinventory = SceneObjectFactory.createInventoryPanel(InventoryName, Mode); //new InventoryPanel(CurrentProperty, Mode);


		// MyApplication.allInventorys.put(CurrentProperty, newinventory);

		// create icon for it
		// BIG shouldnt be hardcoded in future
		// Log.info("creating inventory icon from " + "gameicons/BIG"
		// + CurrentProperty + "_inventory0.png");

		// add this one to the list
		JAMcore.allInventorys.put(InventoryName, newinventory);



		Log.info("creating inventory popup called:"+InventoryName);

		final IsPopupPanel newInventoryFrame = SceneObjectFactory.createTitledPopUp(
				null,
				"50%", "25%", 
				InventoryName + " " + GamesInterfaceTextCore.MainGame_Inventory, 
				newinventory ); 


		// add drop controller to existing inventory's

		// add new ones to array
		JAMcore.allInventoryFrames.put(InventoryName,	newInventoryFrame);


		createAndPlaceNewInventoryButton(newinventory, newInventoryFrame, "GameIcons/BIG/" + InventoryName + "_inventory0.png", AniLen);
	
	}

	
	
	

	/**
	 * creates a new inventory button and places it on the page
	 * 
	 * @param associatedInventory
	 * @param associatedInventoryFrame
	 * @param iconLoc
	 * @param aniLen
	 * @return
	 */
	public static hasInventoryButtonFunctionality createAndPlaceNewInventoryButton(final InventoryPanelCore associatedInventory, final IsPopupPanel associatedInventoryFrame, String iconLoc, int aniLen) {

		Log.info("creating new inventory button");
		final hasInventoryButtonFunctionality NewInventoryButton = SceneObjectFactory.createNewInventoryButton(iconLoc, aniLen);
		

		// attach button to inventory
		associatedInventory.setInventorysButton(NewInventoryButton);

		// add actions
		NewInventoryButton.addClickRunnable(new Runnable() {
			@Override
			public void run() {

				if (!associatedInventoryFrame.isShowing()){
				
				Log.info("opening added inventory");

				associatedInventoryFrame.OpenDefault();

				// always open this when a panel is opened;		
				RequiredImplementations.popupPanelOpened(associatedInventoryFrame);				
				// --
				} else {

					Log.info("closing added inventory");
					associatedInventoryFrame.CloseDefault();
				}
			}

		});


		NewInventoryButton.addMouseOverRunnable(new Runnable() {
			@Override
			public void run() {


				Log.info("mouse over  inventory");
				NewInventoryButton.setPlayForward();
			}

		});

		NewInventoryButton.addMouseOutRunnable(new Runnable() {
			@Override
			public void run() {


				Log.info("mouse out inventory");
				if (associatedInventory.isOpen()== false){						
					//if (JAM.DefaultInventoryOpen == false) {
					NewInventoryButton.setPlayBack();
				}
			}
		});

		Log.info("attaching to page = " + "Inventory_"	+ associatedInventory.getName());

		// attach button to the page
		RequiredImplementations.PositionByTag(NewInventoryButton,"Inventory_" + associatedInventory.getName());
		//
		return NewInventoryButton;

	}


	private String getName() {
		return InventorysName;
	}


	/** Does exactly what it says really.
	 * Unholds the currently held item, and returns the cursor to normal.
	 * 
	 * Also hides the "itemHeld" icon **/
	public static void unholdItem() {

		if (currentlyHeldItem != null) {

			if (((IsInventoryItem)currentlyHeldItem).getObjectsActions() != null) {

				//temp cast to IsInventoryIconImplementation for now
				CommandList actions = ((IsInventoryItem)currentlyHeldItem).getObjectsActions().getActionsForTrigger(
						TriggerType.OnDropped, null);

				//	JAM.Log.info("UnHOLD actions found: \n" + actions.toString());
				if (actions.size() > 0) {

		
					InstructionProcessor.processInstructions(actions,
							"FROM_" + SceneObjectDatabase.currentScene.SceneFileName + "_"
									+ currentlyHeldItem.getName(), null);


				}}
			
			currentlyHeldItem.triggerPutDown(); //some implementations need the object to be informed its put down
		}

		isItemCurrentlyBeingHeld = false;
		currentlyHeldItem = null;

		//change cursor to normal		
		OptionalImplementations.setMouseCursorToDefault();

		//hide item held box
		//	JAM.heldItemBox.setVisible(false);

		RequiredImplementations.setHeldItemVisualiserVisible(false);



	}

	/**
	 * hold the item, as if its currently in the players hands ready to use on something
	 */
	public static void holdItem(String itemName) {

		Log.info("told to hold: "+itemName);

		IsInventoryItem holdThis = null;

		//get the item we wish to hold
		if (itemName.equalsIgnoreCase("<LASTINVENTORYITEM>")) {
			holdThis = CurrentScenesVariables.lastInventoryObjectClickedOn;		
		} else {
			holdThis = InventoryPanelCore.getItemFromName(itemName);
		}


		//if the above was successful we hold it
		if (holdThis != null) {

			Log.info("told to hold: "+holdThis.getName());
			
			// hold it
			InventoryPanelCore.isItemCurrentlyBeingHeld = true;
			InventoryPanelCore.currentlyHeldItem =  holdThis; 

			holdThis.triggerPickedUp(true); //some implementations need the object to be informed its picked up
			
			//MouseCursorManagement.setMouseImage(holdThis.getDataURL(),MouseCursorManagement.DefaultCursorString);
			OptionalImplementations.setMouseCursorTo(holdThis);			
			RequiredImplementations.setHeldItemVisualiserVisible(true);
			RequiredImplementations.setHeldItemVisualisation(holdThis);

			
			//RequiredImplementations.closeAllOpenPopUps(); //TODO:instead lets just close where the item came from			
			String inventorysname = holdThis.getNativeInventoryPanel().getName();

			Log.info("closing inventorys with name : "+inventorysname);
			IsPopupPanel inventoryFrame = JAMcore.allInventoryFrames.get(inventorysname);
			inventoryFrame.CloseDefault();

			
		} else {
			
			//if we are currently loading, we set it to hold after loading
			if (itemLoadStates.get(itemName)==itemState.Loading){
				Log.info("item is "+itemName+" currently loading, setting it to hold after");
				itemLoadStates.put(itemName, itemState.Loading_HoldAfter);
			} else {
				//else we have to load the item then hold it
				Log.info("item "+itemName+" not loaded, so attempting to load and setting to hold after");
				InventoryPanelCore.defaultInventory.AddItemAndHold(itemName,null);
			}


		}
	}


	public static void removeItemFromAllInventorys(IsInventoryItem sceneObject) {

		Log.info("removing item:"+sceneObject.getName());

		// loop over each inventory to check - as it could be in any
		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys
				.values().iterator();


		while (inventorys.hasNext()) {

			InventoryPanelCore currentinventoryPanel = (InventoryPanelCore) inventorys
					.next();

			currentinventoryPanel.removeItem(sceneObject);

		}
	}



	public static boolean isItemBeingHeldOrDragged() {

		if (isItemCurrentlyBeingHeld){
			return true;
		}
		if (isItemCurrentlyBeingDragged){
			return true;
		}
		return false;
	}


	public static IsInventoryItem getCurrentItemBeingHeldOrDragged() {
		if (isItemCurrentlyBeingHeld){
			return currentlyHeldItem;
		}
		if (isItemCurrentlyBeingDragged){
			return currentlyDraggedItem;
		}
		return null;
	}


	/**
	 * Gets all the inventoryitems on all the panels
	 **/
	public static Collection<? extends IsInventoryItem> getAllInventoryItems() {
	
		ArrayList<IsInventoryItem> alltheitems = new ArrayList<IsInventoryItem>();
		Iterator<InventoryPanelCore> invenit = JAMcore.allInventorys.values().iterator();
	
		while (invenit.hasNext()) {
			InventoryPanelCore inventory = invenit.next();
			alltheitems.addAll(inventory.inventorysCurrentItems);
		}
	
		return alltheitems;
	}
	


	/**
	 * 
	 * @param Item1
	 * @param Item2
	 * @return
	 */
	public static boolean testForItemMix(IsInventoryItem Item1, IsInventoryItem Item2) {
		//Get the types of what was dropped ;		
		String Item1Type = Item1.getPopup().POPUPTYPE();
		String Item2Type = Item2.getPopup().POPUPTYPE();
	
		IsInventoryItemPopupContent itemOnesPopup = Item1.getPopup();
		IsInventoryItemPopupContent itemTwosPopup = Item2.getPopup();
	
		boolean matchFound=false;
	
		
		//First we look for matches with the mixscript (new system)
		Log.info(" ____________________________item mix test_::"+	Item1.getName() + " with "+	Item2.getName() );


		ArrayList<ItemMixRequirement> mixsMatched = new ArrayList<ItemMixRequirement>();
		ItemMixRequirement highestMatch = null;
		
		if (itemMixCommands==null){
			Log.severe("itemMixCommands not loaded (no scenes yet?) your very probably going to get a error.....");
		}

		//search for any matchs in our 
		for (ItemMixRequirement req : itemMixCommands) {

			boolean mixFound = req.testMix(Item1, Item2,false);

			if (mixFound){
				mixsMatched.add(req);

				if (highestMatch==null || req.getPriority()>highestMatch.getPriority()){
					highestMatch=req;
				}

			}

		}

		if (highestMatch!=null) {
			
			Log.info("Mix Commands found:"+highestMatch.getCommands());
			
			//if we found a match we run it!
			InstructionProcessor.processInstructions(highestMatch.getCommands(),"ItemMix:"+Item2.getName()+"+"+Item1.getName()+"| |ItemMix:"+Item1.getName()+"+"+Item2.getName(),null);
			matchFound=true;
			return matchFound;
		}
		//-------------
		
		
		//Now we test for item generic type mixes	
		//ie. if one is a mag and the other is magnifable, we can display it magnified!		
		if (
				   ((itemTwosPopup.MAGNIFYABLE())	              && (Item2Type.compareTo("MAGNIFYINGGLASS")==0))
				|| ((Item1Type.compareTo("MAGNIFYINGGLASS")==0)   && (itemTwosPopup).MAGNIFYABLE()              )
			)
		{
	
			Log.info("magnafying");
	
			//get the item to open;
			String iURL = "";
			//ImageZoomPopUp mag = null;
			IsPopupPanel ItemWithShadow = null;
	
	
			//if one is an image;
	
			if ((Item1Type.compareTo("PICTURE")==0)){
	
				iURL = (itemOnesPopup).getSourceURL();
				
				int sizex = (itemTwosPopup).sourcesizeX();
				int sizey = (itemTwosPopup).sourcesizeY();		  
				
				String itemname = (itemOnesPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
				if ( Item2.isPopedUp() == false){
					String imageDiscription = ((IsImagePopUpContents)itemOnesPopup).getImageDiscription();
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify;
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item2, iURL, sizex, sizey, imageDiscription, popupTitle);
					Item2.setPopedUp(true);
	
				}
	
			} else if((Item2Type.compareTo("PICTURE")==0)) {
	
				iURL = (itemTwosPopup).getSourceURL();
				//get size
				int sizex = (itemTwosPopup).sourcesizeX();
				int sizey = (itemTwosPopup).sourcesizeY();	
				
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
				if ( Item1.isPopedUp() == false){
	
					String imageDiscription = ((IsImagePopUpContents)itemTwosPopup).getImageDiscription();
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify;
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item1, iURL, sizex, sizey, imageDiscription, popupTitle);			
					Item1.setPopedUp(true);
	
				}
	
			} else if((itemTwosPopup).MAGNIFYABLE()) {
				JAMcore.GameLogger.info("loading mag item2..."+iURL);
	
				//if not then we must get the url from the items name;
				iURL = (itemTwosPopup).getSourceURL();
				//change to image location
				iURL = iURL.substring(0, iURL.length()-4)+".jpg";
	
				//get size
			//	int sizex =  Integer.parseInt(((IsInventoryItemPopupContent)itemTwosPopup).sourcesizeX());
			//	int sizey =  Integer.parseInt(((IsInventoryItemPopupContent)itemTwosPopup).sourcesizeY());
	
				int sizex =  (itemTwosPopup).sourcesizeX();
				int sizey =  (itemTwosPopup).sourcesizeY();
	
	
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
	
				if ( Item1.isPopedUp() == false){
	
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify+itemname;
					String imageDiscription = "";
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item1, iURL, sizex, sizey, imageDiscription, popupTitle);
					Item1.setPopedUp(true);
	
				}
	
	
	
			} else if((itemOnesPopup).MAGNIFYABLE()) {
	
				//if not then we must get the url from the items name;
				iURL = (itemOnesPopup).getSourceURL();
				//change to image location
				iURL = iURL.substring(0, iURL.length()-4)+".jpg";
	
				//get size
			//	int sizex =  Integer.parseInt((itemOnesPopup).sourcesizeX());
			//	int sizey =  Integer.parseInt((itemOnesPopup).sourcesizeY());
				
				int sizex =  (itemOnesPopup).sourcesizeX();
				int sizey =  (itemOnesPopup).sourcesizeY();
	
	
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
				if ( Item2.isPopedUp() == false){
	
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify+itemname;
					String imageDiscription = "";
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item2, iURL, sizex, sizey, imageDiscription, popupTitle);	
					Item2.setPopedUp(true);
	
				}
	
			}
	
			
			ItemWithShadow.OpenDefault();
			matchFound=true;
			return matchFound;
		}
		///-----
		Log.info("no mix match found at all");
	
		
		return matchFound;
	
	}
	/**
	 * Test for if items mixed together should do anything</br>
	 * (this should fire if one is dragged over another, or if one is picked up and clicked on another<br>
	 * 
	 * @param Item1
	 * @param Item2
	 * 
	 * @returns true if a match was found
	 */
	public static boolean testForItemMix_old(IsInventoryItem Item1, IsInventoryItem Item2) {
		//Get the types of what was dropped ;		
		String Item1Type = Item1.getPopup().POPUPTYPE();
		String Item2Type = Item2.getPopup().POPUPTYPE();
	
		IsInventoryItemPopupContent itemOnesPopup = Item1.getPopup();
		IsInventoryItemPopupContent itemTwosPopup = Item2.getPopup();
	
		boolean matchFound=false;
	
		String newline = "\n"; // \r\n
		Log.info("________________________ name = "+ Item1.getName() + " and " + Item2.getName());
	
		String itemMixScriptL = InventoryPanelCore.itemmixscript.toLowerCase(); 
		
		//We now look for a exact match in the item script (if either X+B or B+X exist in the script, then we move on)		
		String firstPossibility  = " "+Item1.getName().toLowerCase()+"+"+Item2.getName().toLowerCase()+newline;		
		String secondPossibility = " "+Item2.getName().toLowerCase()+"+"+Item1.getName().toLowerCase()+newline;
		
	
		Log.info("________________________ looking for: '"+firstPossibility+ "' or '" +secondPossibility+"' in the mix script");
		
		int AnswerIndex = Math.max( (itemMixScriptL.indexOf(firstPossibility)),
									(itemMixScriptL.indexOf(secondPossibility))) ;
	
		// Check specific combination exists
		if  (AnswerIndex>-1){    	
			Log.info("match found");
			//isolate commands to process
			//from
			int from = AnswerIndex+Item1.getName().length()+Item2.getName().length()+1;
			int to  = InventoryPanelCore.itemmixscript.toLowerCase().indexOf("mix",from);
			Log.info(" from: \n"+from+" to "+to);
			String instructionset = InventoryPanelCore.itemmixscript.substring(from, to).trim()+"\n";	
			Log.info(" processing: \n"+instructionset);    	  
			InstructionProcessor.processInstructions(instructionset,"ItemMix:"+Item2.getName()+"+"+Item1.getName()+"| |ItemMix:"+Item1.getName()+"+"+Item2.getName(),null);
			matchFound=true;
			return matchFound;
		} 
	
		Log.info("no specific mix match found");
		
		//Now we test for item generic type mixes
	
		//ie. if one is a mag and the other is magnifable, we can display it magnified!		
		if (
				   ((itemTwosPopup.MAGNIFYABLE())	              && (Item2Type.compareTo("MAGNIFYINGGLASS")==0))
				|| ((Item1Type.compareTo("MAGNIFYINGGLASS")==0)   && (itemTwosPopup).MAGNIFYABLE()              )
			)
		{
	
			Log.info("magnafying");
	
			//get the item to open;
			String iURL = "";
			//ImageZoomPopUp mag = null;
			IsPopupPanel ItemWithShadow = null;
	
	
			//if one is an image;
	
			if ((Item1Type.compareTo("PICTURE")==0)){
	
				iURL = (itemOnesPopup).getSourceURL();
				
				int sizex = (itemTwosPopup).sourcesizeX();
				int sizey = (itemTwosPopup).sourcesizeY();		  
				
				String itemname = (itemOnesPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
				if ( Item2.isPopedUp() == false){
					String imageDiscription = ((IsImagePopUpContents)itemOnesPopup).getImageDiscription();
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify;
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item2, iURL, sizex, sizey, imageDiscription, popupTitle);
					Item2.setPopedUp(true);
	
				}
	
			} else if((Item2Type.compareTo("PICTURE")==0)) {
	
				iURL = (itemTwosPopup).getSourceURL();
				//get size
				int sizex = (itemTwosPopup).sourcesizeX();
				int sizey = (itemTwosPopup).sourcesizeY();	
				
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
				if ( Item1.isPopedUp() == false){
	
					String imageDiscription = ((IsImagePopUpContents)itemTwosPopup).getImageDiscription();
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify;
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item1, iURL, sizex, sizey, imageDiscription, popupTitle);			
					Item1.setPopedUp(true);
	
				}
	
			} else if((itemTwosPopup).MAGNIFYABLE()) {
				JAMcore.GameLogger.info("loading mag item2..."+iURL);
	
				//if not then we must get the url from the items name;
				iURL = (itemTwosPopup).getSourceURL();
				//change to image location
				iURL = iURL.substring(0, iURL.length()-4)+".jpg";
	
				//get size
			//	int sizex =  Integer.parseInt(((IsInventoryItemPopupContent)itemTwosPopup).sourcesizeX());
			//	int sizey =  Integer.parseInt(((IsInventoryItemPopupContent)itemTwosPopup).sourcesizeY());
	
				int sizex =  (itemTwosPopup).sourcesizeX();
				int sizey =  (itemTwosPopup).sourcesizeY();
	
	
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
	
	
				if ( Item1.isPopedUp() == false){
	
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify+itemname;
					String imageDiscription = "";
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item1, iURL, sizex, sizey, imageDiscription, popupTitle);
					Item1.setPopedUp(true);
	
				}
	
	
	
			} else if((itemOnesPopup).MAGNIFYABLE()) {
	
				//if not then we must get the url from the items name;
				iURL = (itemOnesPopup).getSourceURL();
				//change to image location
				iURL = iURL.substring(0, iURL.length()-4)+".jpg";
	
				//get size
			//	int sizex =  Integer.parseInt((itemOnesPopup).sourcesizeX());
			//	int sizey =  Integer.parseInt((itemOnesPopup).sourcesizeY());
				
				int sizex =  (itemOnesPopup).sourcesizeX();
				int sizey =  (itemOnesPopup).sourcesizeY();
	
	
				String itemname = (itemTwosPopup).getSourceURL();
				itemname = itemname.substring( itemname.lastIndexOf("/")+1 );
				if ( Item2.isPopedUp() == false){
	
					String popupTitle = GamesInterfaceTextCore.Magnify_Magnify+itemname;
					String imageDiscription = "";
	
					ItemWithShadow = InventoryItemFactory.createImageZoomPopup(Item2, iURL, sizex, sizey, imageDiscription, popupTitle);	
					Item2.setPopedUp(true);
	
				}
	
			}
	
			ItemWithShadow.OpenDefault();
			matchFound=true;
			return matchFound;
		}
		
		
		
		//Look for singular object match (ie,default response for object)
		// Mix: ItemName1
		// OR
		// Mix: ItemName2		
		//(needs newline added)
		int ObjectGenericAnswerIndex =  Math.max(  itemMixScriptL.indexOf("mix: "+Item1.getName().toLowerCase()+newline) ,
												   itemMixScriptL.indexOf("mix: "+Item2.getName().toLowerCase()+newline)  );
		
		if  (ObjectGenericAnswerIndex>-1){    	
			Log.info("Match found for generic object response");
			//isolate commands to process
			//from
			int from = itemMixScriptL.indexOf("\n",ObjectGenericAnswerIndex);
			int to  = InventoryPanelCore.itemmixscript.toLowerCase().indexOf("mix",from);
			Log.info(" from: \n"+from+" to "+to);
			String instructionset = InventoryPanelCore.itemmixscript.substring(from, to).trim()+"\n";	
			Log.info(" processing: \n"+instructionset);    	  
			InstructionProcessor.processInstructions(instructionset,"GenericItemMix:"+Item2.getName()+"+"+Item1.getName(),null);
			matchFound=true;
			return matchFound;
		}
		
		//lastly total generic if nothing else found
			
		int DefaultAnswerIndex =  Math.max(   itemMixScriptL.indexOf("mix: "+newline) ,
											  itemMixScriptL.indexOf("mix:"+newline)  );
	
		if  (DefaultAnswerIndex>-1){    	
			Log.info("Match found for default object response");
			//isolate commands to process
			//from
			int from = itemMixScriptL.indexOf("\n",DefaultAnswerIndex);
			int to  = InventoryPanelCore.itemmixscript.toLowerCase().indexOf("mix",from);
			Log.info(" from: \n"+from+" to "+to);
			String instructionset = InventoryPanelCore.itemmixscript.substring(from, to).trim()+"\n";	
			Log.info(" processing: \n"+instructionset);    	  
			InstructionProcessor.processInstructions(instructionset,"DefaultItemMix:"+Item2.getName()+"+"+Item1.getName(),null);
			matchFound=true;
			return matchFound;
		}
	
	
		
		
		
		
		///-----
		Log.info("no mix match found at all");
	
		
		return matchFound;
	
	}



	public void CloseDefault() {
		isOpen  = false;

		// get the inventory button and set to close
		if (getInventorysButton() != null) {
			getInventorysButton().setPlayBack();
		}

		triggerInventoryCloseActions();
	}


	@Override
	public void OpenDefault() {
		Log.info("inventory opened");
		isOpen  = true;
		
		triggerInventoryOpenActions();

	}


	/**
	 * triggered when this inventory is opened, it will check for OnInventoryOpen and run them if found
	 */
	private void triggerInventoryOpenActions() {
		
		
		CommandList actions = InventoryPanelCore.globalInventoryActions.getActionsForTrigger(TriggerType.OnInventoryOpen, this.getName().toLowerCase());
				
		if (actions!=null && actions.size()>0){
			InstructionProcessor.processInstructions(actions,
					"FROM_open" + getName(), null);
		};
	}


	/**
	 * triggered when this inventory is closed, it will check for OnInventoryClosed and run them if found
	 */
	private void triggerInventoryCloseActions() {
		// TODO Auto-generated method stub
		CommandList actions = InventoryPanelCore.globalInventoryActions.getActionsForTrigger(TriggerType.OnInventoryClosed, this.getName().toLowerCase());
		if (actions!=null && actions.size()>0)
		{
		InstructionProcessor.processInstructions(actions,
				"FROM_close" + getName(), null);
		};
	}


	/**
	 * 
	 * Creates a new inventory item from the supplied data
	 * 
	 * @param NewItemsName
	 * @param associatedObjectName
	 * @param runwhendone
	 * @param TriggerPopUpAfterLoading
	 * @param ItemDataString
	 **/
	public void createInventoryItemFromData(final String NewItemsName, final SceneObject associatedObjectName, final Runnable runwhendone, final boolean TriggerPopUpAfterLoading, String ItemDataString) {
	
		// get type
		String type = ItemDataString.substring(
				ItemDataString.indexOf("Type = ") + 7,
				ItemDataString.indexOf("\n",
						ItemDataString.indexOf("Type = ") + 7))
				.trim();
	
		String discription = "";
	
		// get description if present
		if (ItemDataString.indexOf("Description = '") > -1) {
			discription = ItemDataString
					.substring(
							ItemDataString
							.indexOf("Description = '") + 15,
							ItemDataString
							.indexOf(
									"'",
									ItemDataString
									.indexOf(
											"Description = '") + 16))
					.trim();
	
			discription = JAMcore.SwapCustomWords(discription);
	
			discription = JAMcore.parseForTextIDs(discription);
	
		}
	
		String URL = "";
	
		// get youtube if present
		if (ItemDataString.indexOf("URL = ") > -1) {
			URL = ItemDataString.substring(
					ItemDataString.indexOf("URL = ") + 6,
					ItemDataString.indexOf("\n",
							ItemDataString.indexOf("URL = ") + 7))
					.trim();
		} else {
			URL = "";
		}
		String title = "";
		
		// get title if present
		if (ItemDataString.indexOf("Title = '") > -1) {
	
			title = ItemDataString
					.substring(
							ItemDataString.indexOf("Title = '") + 9,
							ItemDataString.indexOf(
									"'",
									ItemDataString
									.indexOf("Title = '") + 10))
					.trim();
	
			title = JAMcore.SwapCustomWords(title);
			title = JAMcore.parseForTextIDs(title);
	
	
	
		} else {
			title = NewItemsName;
		}
	
		//Log.severe("Title set to : "+title);
	
		String keepmode = "";
	
		// get keepmode if present
		if (ItemDataString.indexOf("KeepHeld = ") > -1) {
			keepmode = ItemDataString
					.substring(	
							ItemDataString.indexOf("KeepHeld = ") + 11,
							ItemDataString.indexOf(
									"\n",
									ItemDataString
									.indexOf("KeepHeld = ") + 12))
					.trim();
		}
	
		boolean noback = false;
	
		// get no background flag if present
		if (ItemDataString.indexOf("NoBack = ") > -1) {
	
			String nobackstring = ItemDataString
					.substring(	
							ItemDataString.indexOf("NoBack = ") + 9,
							ItemDataString.indexOf(
									"\n",
									ItemDataString
									.indexOf("NoBack = ") + 10))
					.trim();
	
			noback = Boolean.parseBoolean(nobackstring);
		}
	
	
		// get size if present
		String Size = "";
		String size_x = "10";
		String size_y = "10";
		if (ItemDataString.indexOf("Size = ") > -1) {
			Size = ItemDataString.substring(
					ItemDataString.indexOf("Size = ") + 7,
					ItemDataString.indexOf("\n",
							ItemDataString.indexOf("Size = ") + 8))
					.trim();
	
			size_x = Size.split(",")[0];
			size_y = Size.split(",")[1];
			JAMcore.GameLogger.info("loading movie"
					+ size_x);
	
		} else {
			Size = "";
		}
		// get if magnifiable
		boolean is_magnifiable = false;
		if (ItemDataString.indexOf("Magnifiable = ") > -1) {
			String ismag = ItemDataString.substring(
					ItemDataString.indexOf("Magnifiable = ") + 14,
					ItemDataString.indexOf("\n", ItemDataString
							.indexOf("Magnifiable = ") + 14))
					.trim();
	
			if (ismag.compareTo("true") == 0) {
				is_magnifiable = true;
			}
	
			JAMcore.GameLogger.info("is mag?"
					+ is_magnifiable);
	
		}
	
		String Embed = "";
		if (ItemDataString.indexOf("Embed = ") > -1) {
			Embed = ItemDataString
					.substring(
							ItemDataString.indexOf("Embed = ") + 8,
							ItemDataString.indexOf(
									"\n",
									ItemDataString
									.indexOf("Embed = ") + 8))
					.trim();
	
		}
	
		// get the actions if there is any, else we assume default
		// actions
		/*
		if (ItemDataString.contains("ActionsList:")) {
	
			Log.info("actions detected based on ActionsList: being present");
			String actions = ItemDataString.split("ActionsList:")[1]
					.trim();
	
			//ItemDataString = ItemDataString.split("ActionsList:")[0]
			//		.trim();
	
			Log.info("actions=" + actions);
			itemsActions = new ActionList(actions);
	
		} else {
	
			// we assign the default actions
	
			// Log.info("assigning default actions");
	
			// on item left click, we open it
			// itemsActions = defaultActions;
	
		}*/
	
		// --------
	
		//Ensure parameters are suitable for the inventory icon
		ItemDataString = InventoryPanelCore.generateInventoryIconParams(ItemDataString,NewItemsName);
	
		Log.info("Icon ItemDataString generated");
		//create the objectdata for the inventoryitem
		//note: Currently a lot of redundancy with this above, this needs to be dealt with
		InventoryObjectState iconsstate = InventoryPanelCore.generateInventoryIconState(ItemDataString);
	
		Log.info("Icon state generated:"+iconsstate.serialiseToString());
	
		
		
		//create the contents of the items popup first
		IsInventoryItemPopupContent ItemPopUp = InventoryItemFactory.createInventoryItemsPopupContents(type, NewItemsName, discription, URL,
				title, size_x, size_y, is_magnifiable, Embed);
		
		if (ItemPopUp==null){
			Log.severe("Error: cant make popup for inventory type:"+type+" maybe this isnt supported? (GDX has no popup support at the time of writting. Only Type Concept will work)");					
		}
	
		//Now we have the popup contents, we create the inventoryItem itself		
		IsInventoryItem newInventoryIcon = InventoryItemFactory.createInventoryItem(ItemPopUp,  title, this,iconsstate,ItemDataString);
		
		Log.info("Inventory Item Object generated:");
		
		
		if (ItemPopUp.getExpectedZIndex()!=-1){
			newInventoryIcon.setFixedZdepth(ItemPopUp.getExpectedZIndex());			
		}
		
		//set to clear popups background style if set
		//(in future we might let manual style setting - but given the popup has potentially
		//9 styles to set, thats a lot of parameters!)
		if (noback){
			//newInventoryIcon.ItemWithShadow.clearBackgroundStyles();
			newInventoryIcon.setPopupsBackgroundToTransparent();
			
		}
	
	
		Log.info("setting associated object and keep mode, if any");
	
		//set associated object, if there's any
		if (associatedObjectName!=null){
			newInventoryIcon.getObjectsCurrentState().associatedSceneObject =associatedObjectName;
		}
	
		//set keepmode if any
		if (keepmode.length()>2){
			newInventoryIcon.setKeepHeldMode(IsInventoryItem.KeepHeldMode.valueOf(keepmode));
			Log.info("keepheld mode set to:"+newInventoryIcon.getKeepHeldMode().toString());
	
		}
	
		Log.info("setting icon mode to "+CurrentInventoryMode.toString());
	
		//set default display mode
		newInventoryIcon.setIconMode(CurrentInventoryMode);
		
		//fire its actions
		newInventoryIcon.ObjectsLog("fireing item added actions");		
		newInventoryIcon.fireOnItemAddedToInventoryCommands();
	
		attachPreparedInventoryItemToPanel(runwhendone, TriggerPopUpAfterLoading, newInventoryIcon);
	}


	public void removeItem(IsInventoryItem icon) {
		
		if (!inventorysCurrentItems.contains(icon)){
			Log.info("object not present in inventory so cant remove");
			return;
		}
	
	
		Log.info("removing item logically");
		inventorysCurrentItems.remove(icon);
		
		removeFromLinearSlotSpace(icon);				
	
	
		Log.info("removing item physically from screen");
		physicallyRemoveItem(icon);		
		
	
		if (JAMcore.DebugMode){
			debugLinearSlotSpace();
		}
	
		
	}


	static public InventoryObjectState generateInventoryIconState(String currentItemData){
	
		// split actions off if present
		// get scene parameters (anything before a line with : in it)
		/*
		int firstLocOfColon = currentItemData.indexOf(':');
	
		String Parameters = "";
	
		if (firstLocOfColon == -1) {
			Parameters = currentItemData;
		} else {
			int linebeforeColon = currentItemData.lastIndexOf('\n', firstLocOfColon);
			Parameters = currentItemData.substring(0, linebeforeColon);
	
		}
		*/
		String bits[] = SceneObject.splitActionsAndParams(currentItemData);
		
		String ParameterLines[] = bits[0].split("\n");// Parameters.split("\n");
	
		//SceneObjectState newobjectdata = new SceneObjectState(ParameterLines,true);		
		InventoryObjectState  newobjectdata = new InventoryObjectState(ParameterLines,true);	
	
		/** Ensure scene is null - inventory items dont have a scene! **/
		newobjectdata.ObjectsSceneName = SceneObjectState.OBJECT_HAS_NO_SCENE_STRING;
		
		/** we disguise ourselves as a inventory icon...mahahaha **/
		newobjectdata.setObjectsPrimaryType(SceneObjectType.InventoryObject);
		newobjectdata.assignObjectTypeSpecificParametersNew(ParameterLines); //important, this must be done before the state is useable
		
		return newobjectdata;
	}


	static public String generateInventoryIconParams(String Parameters, String itemName){
	
		if (Parameters.length()==0){
	
			Log.info("no parameters!");
			Parameters = "Title = '"+itemName+"'"+"\n"; 
	
		}
	
		Log.info("params size:"+Parameters.length());	
	
	
	
		//remove any type specifications as the type will refer to the PopUp type and not the spites type	
		int typelinestart = Parameters.indexOf("Type:");
		if (typelinestart !=-1){
			int typelineend = Parameters.indexOf("\n",typelinestart+5);
			Parameters=	Parameters.substring(0,  typelinestart)+"\n"+Parameters.substring(typelineend)+"\n";
	
		}
		//remove the actionlist: specification, this was needed for the old item
		//system. Nowdays inventory items are sprite objects, which dont need this line
		//for figure out there the actions start.
		int ActionsListstart = Parameters.indexOf("ActionsList:");
		if (ActionsListstart !=-1){
			int  ActionsListend = Parameters.indexOf("\n",ActionsListstart+11);
	
			Parameters=	Parameters.substring(0,  ActionsListstart)+"\n"+Parameters.substring(ActionsListend)+"\n";
	
		}
	
		//add the name which also is the filename, which is a required field for sprite objects
		String NewBits="\n";
		NewBits=NewBits+"Name = "+itemName+"\n"; 
	
	
		//if theres frames then the name spec has to have a 0 added to it
		if (Parameters.contains("Frames =")|| Parameters.contains("Frames=")){
			NewBits=NewBits+"FileName = thumb_"+itemName+"0.png"; //
	
		} else {
	
			NewBits=NewBits+"FileName = thumb_"+itemName+".png"; //
		}
		Parameters=NewBits+"\n"+Parameters;
	
		Log.info("parameters now:"+Parameters);
	
		return Parameters;
	
	}



	public static void ResizeAllInventoryPanels() {

		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys
				.values().iterator();


		while (inventorys.hasNext()) {
			InventoryPanelCore currentInventoryPanel =inventorys
					.next();
			//	Log.info("checking inventory is open:"
			//			+ currentInventoryPanel.Title);
			if (currentInventoryPanel.isOpen()) {
				currentInventoryPanel.ResizeAndReorder();

			} else {
				// do nothing
				Log.info("inventory not open so skipping");

			}

		}
	}

	public static void hideAllInventoryButtons() {
		
		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys
				.values().iterator();
		
		while (inventorys.hasNext()) {
			
			InventoryPanelCore next = inventorys.next();
			next.hideInventorysButton();
			
		}
		
		//we should hide the visualizer too
		//(note; this will reappear next time a item is picked up)
		setHeldItemVariablesAndVisualsOff();
	}

	

	public void hideInventorysButton() {

		hasInventoryButtonFunctionality inventoryButton = getInventorysButton();
		inventoryButton.setVisible(false);
		
	}


	public void showInventorysButton() {

		hasInventoryButtonFunctionality inventoryButton = getInventorysButton();
		inventoryButton.setVisible(true);
		
	}


	public static void showAllInventoryButtons() {
		Iterator<InventoryPanelCore> inventorys = JAMcore.allInventorys
				.values().iterator();
		
		while (inventorys.hasNext()) {
			hasInventoryButtonFunctionality inventoryButton = inventorys.next().getInventorysButton();
			inventoryButton.setVisible(true);
		}
		
		//note; no need to unhide the visualiser, as it should unhide only when a item is picked up
	}


	/**
	 * returns a comma separated list of item names
	 * @return
	 */
	public String getAllItemNamesAsString() {

		String names = "";
		for (IsInventoryItem idc : inventorysCurrentItems) {

			names = names + ","+ idc.getName();
						
		}

		
		
		return names;
	}


	protected void addAlreadyAttachItemLogically(IsInventoryItem preparedItem, int targetSlot) {
		inventorysCurrentItems.add(preparedItem);
	
		//reduce the items LeftToLoad list (note; some things like tig will have added to this value above as they have sub-things to load)
		JAMcore.itemsLeftToLoad--;
	
		// Now we retrieve the style in the Dom where it says LEFT:
		// ###px; and TOP ###px
		//	int Left = NextPosX;
		//int Top  = NextPosY;
		//	int LeftLimit = dragController.getBoundaryPanel().getOffsetWidth();
	
		// work out the liner slot position and set it to true.
		//	LinerPos = (((Top / iconSizeY) * LeftLimit) + Left) / iconSizeX;
		//	itemSpace[LinerPos] = true;
	
		//	itemSpace[targetSlot] = true;
	
		itemSpace_new[targetSlot] = preparedItem;
	}


	public void updateLinearSlotSpace(int targetSlot, IsInventoryItem object) {

		itemSpace_new[targetSlot] = object;	
	}


	protected static void setHeldItemVisualsAndVariablesOn(IsInventoryItem itemDragged) {
		InventoryPanelCore.isItemCurrentlyBeingDragged = true;
		InventoryPanelCore.currentlyDraggedItem =  itemDragged; 
	
		Log.info("item dragged is: "+itemDragged.getName());				
		OptionalImplementations.setMouseCursorTo(itemDragged);			
		RequiredImplementations.setHeldItemVisualiserVisible(true);
		RequiredImplementations.setHeldItemVisualisation(itemDragged);
	}

		

	protected static void setHeldItemVariablesAndVisualsOff() {
		InventoryPanelCore.isItemCurrentlyBeingDragged = false;
		InventoryPanelCore.currentlyDraggedItem        =  null; 
	
		//(new) mouse cursor goes back when drag ends
		OptionalImplementations.setMouseCursorToDefault();
		RequiredImplementations.setHeldItemVisualiserVisible(false);
	}





}
