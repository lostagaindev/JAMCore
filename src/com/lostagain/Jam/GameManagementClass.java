package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;

import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.SaveMangement.JamSaveGameManager;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;

/**
 * This class should eventually control all game management functions
 * eg,
 * clearing/resetting the game
 * 
 * 
 * @author darkflame
 *
 */
public class GameManagementClass {

	static Logger Log = Logger.getLogger("JAMCore.GameManagementClass");
	
	/** 
	 * Resets the  game as much as possible by clearing variables and removing all visual elements 
	 * **/
	public static void clearAllGameData(){


		Log.info("Stopping all timers");

		//stop updating all objects
		JAMTimerController.stopUpdatingAllObjects();
		
		//music and sound stop too
		OptionalImplementations.stopAllSoundEffects();
		
		//(we need the same as above implement for music as well)
		Log.info("Clearing all game data");
		
		//clear all scenes
		SceneWidget.clearScenes();

		SceneObject.currentEditedObject=null;

		SceneObjectDatabase.clearAllGameVariables();
		
		JamSaveGameManager.pendingStatesToLoad.clear();
		

		//Log.info("Clearing all sprite objects:"+SceneObjectDatabase.all_sprite_objects.keys().toString());

		//remove all objects
		//Note; we use false in the remove object functions to tell them not to remove themselves from the global game lists
		//We do this because we clear those lists completely straight after which is more efficient.
		//Also, doing it while looping over might cause concurrent modification errors
		/*
		for (SceneObjectVisual so : SceneObjectDatabase.all_sprite_objects.values()) {	
			so.removeObject(false);		
			Log.info("Object removed");
		}		

		Log.info("clearing text objects:"+SceneObjectDatabase.all_text_objects.keys().toString());
		for (SceneDialogObject so : SceneObjectDatabase.all_text_objects.values()) {	
			so.removeObject(false);		
		}	

		Log.info("clearing div objects:"+SceneObjectDatabase.all_div_objects.keys().toString());
		for (SceneDivObject so : SceneObjectDatabase.all_div_objects.values()) {

			so.removeObject(false);		

		}	
		Log.info("clearing input objects:"+SceneObjectDatabase.all_input_objects.keys().toString());
		for (SceneInputObject so : SceneObjectDatabase.all_input_objects.values()) {	
			so.removeObject(false);		
		}		
		Log.info("clearing vector objects:"+SceneObjectDatabase.all_vector_objects.keys().toString());
		for (SceneVectorObject so : SceneObjectDatabase.all_vector_objects.values()) {	
			so.removeObject(false);		
		}	
		 */

		//We now clear them all with one loop after getting all the objects
		Set<IsSceneObject> allObjects = SceneObjectDatabase.getAllGamesObjects();


		Log.info("clearing all objects:"+allObjects.size()+" to remove");
		for (IsSceneObject so : allObjects) {	
			so.removeObject(false,false);		//lists and inventorys are cleared seperately below, so we dont waste time doing it here
		}	


		Log.info("clearing objects from global lists");
		//internally this just runs 
		//SceneObjectDatabase.all_vector_objects.clear();
		//SceneObjectDatabase.all_input_objects.clear();
		//etc...
		SceneObjectDatabase.clearAllObjectDataMaps();

		//clear all pages
		Log.info("Clearing all pages...");

		JAMcore.CurrentScenesAndPages.OpenPagesInSet.clear();
	//	((GWTSceneAndPageSet)JAMcore.CurrentScenesAndPages).visualContents.clear(); //Can't be moved to core yet
		JAMcore.CurrentScenesAndPages.PageCount=0;
		JAMcore.CurrentScenesAndPages.clearVisuals();
		
		for (SceneAndPageSet set : GameManagementClass.AllSceneAndPageSets) {	 //Can't be moved to core yet

			//TODO:: refractor this into a clear set command?
			set.OpenPagesInSet.clear();
			set.PageCount=0;	
			set.clearVisuals();
			
		}			


		JAMcore.currentOpenPages.clear();

		//clear chapters
		//JAM.ChapterList.VTabPanel.clear();

		// this line causes error ?
		GameManagementClass.AllSceneAndPageSets.clear();



	//	JAM.ChapterList.clear();		

		Log.info("running StoryTabs.clear");

		JAMcore.GamesChaptersPanel.clear();

		GameManagementClass.clearInventorysAndVariables();
		
		//addition clear functions that can't go into core
		/*
		Log.info("clearing visual chapter list");
		JAM.ChapterList.VTabPanel.clear();
		JAM.ChapterList.clear();		
		JAM.ChapterList = new VerticalTabs();*/
		
		RequiredImplementations.resetGame();
		
		//-----

		//Log.info("PttL ="+ JAM.ChapterList.PanelsToTriggerList.toString()); 

		GameManagementClass.AllSceneAndPageSets = new ArrayList<SceneAndPageSet>();
		

		Log.info("------");
		Log.info("------------");
		Log.info("-------------------");
		Log.info("------------------------");
		Log.info("------------------------");
		Log.info("------------------------  THE GAME HAS BEEN RESET. ALL OBJECTS AND INVENTORYS SHOULD NOW BE CLEARED");		
		Log.info("------------------------");
		Log.info("------------------------");
		Log.info("--------------------");
		Log.info("--------------");
		Log.info("-----");
		
		
		

	}

	
	static void clearInventorysAndVariables() {

		Log.info("Clearing inventorys");

		//clear all items
		//InventoryPanelCore.defaultInventory.ClearInventory();

		//clear all the items in all the inventories
		for (InventoryPanelCore inventory : JAMcore.allInventorys.values()) {	
			

			Log.info("Closing inventory:"+inventory.InventorysName);
			IsPopupPanel inventoryframe = JAMcore.allInventoryFrames.get(inventory.InventorysName);
			inventoryframe.CloseDefault();		

			Log.info("Clearing inventory:"+inventory.InventorysName);
			inventory.ClearInventory();		

			//remove the inventory's icon if its not the default
			if (inventory!=InventoryPanelCore.defaultInventory){
				inventory.getInventorysButton().removeFromInterface();
			}


		}

		JAMcore.allInventorys.clear();


		//profile
		//JAM.PlayersNotepad.LoadedPages.clear();
		JAMcore.PlayersNotepad.clearPages();
		

		//clues
		JAMcore.playersClues.clear();
		
		//secrets
		//JAM.SecretPanel.clear();
		OptionalImplementations.Cuypers_ClearScrets();
		

		//individual variables
		JAMcore.usersCurrentLocation="";
		if (JAMcore.AnswerBox.isPresent()){
			JAMcore.AnswerBox.get().setText("");
		}
		
		//JAM.Feedback.clear();		
		RequiredImplementations.setCurrentFeedbackText(""); //not sure why we used clear before?
		
		
		GameVariableManagement.GameVariables = new AssArray(); //easiest way to clear all game variables 

		//recreated needed defaults
		JAMcore.allInventorys.put("Inventory Items",  InventoryPanelCore.defaultInventory);
		
		
	}

	/** in future this will attempt to reset all scenes and items back to their default state, as well as erasing all 
	 * game variables and other settings.
	 * effectively providing a fresh game environment ready for a save game...without actually unloading things **/
	public static void expirementalReset() {

		Log.severe("Expiremental RESET does not yet work fully!!!");

		//reset all scenes and objects to their default state
		//for (SceneWidget sceneToReset : SceneWidget.all_scenes.values()) {			
		//	sceneToReset.resetScene();
		//}


		//resets all variables (WIP...not sure if we should reset chapters or not)

		Log.severe("clearInventoryAndVariables (test)!!!");
		clearInventorysAndVariables();

	}

	// list of all opened chapters (each chapter can have many scenes/pages in it)	
	public static ArrayList<SceneAndPageSet> AllSceneAndPageSets = new ArrayList<SceneAndPageSet>();

}
