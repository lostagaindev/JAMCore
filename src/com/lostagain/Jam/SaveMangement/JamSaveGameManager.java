
package com.lostagain.Jam.SaveMangement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.GameManagementClass;
import com.lostagain.Jam.GameVariableManagement;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.JamClueControll;
import com.lostagain.Jam.JamGlobalGameEffects;
import com.lostagain.Jam.OptionalImplementations;
import com.lostagain.Jam.PlayersNotebookCore;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.SceneAndPageSet;
import com.lostagain.Jam.ScoreControll;
import com.lostagain.Jam.Factorys.NamedActionSetTimer;
import com.lostagain.Jam.GwtLegacySupport.secretsPanelCore;
import com.lostagain.Jam.GwtLegacySupport.secretsPanelCore.linkItem;
import com.lostagain.Jam.Interfaces.JamChapterControl;
import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;
import com.lostagain.Jam.SaveMangement.HasCompressionSystem.CompressionResult;
import com.lostagain.Jam.Scene.SceneStatus;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.InventoryObjectState;
import com.lostagain.Jam.SceneObjects.SceneDialogueObjectState;
import com.lostagain.Jam.SceneObjects.SceneDivObjectState;
import com.lostagain.Jam.SceneObjects.SceneInputObjectState;
import com.lostagain.Jam.SceneObjects.SceneLabelObjectState;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;
import com.lostagain.Jam.SceneObjects.SceneVectorObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.audio.MusicBoxCore;

import lostagain.nl.spiffyresources.client.SpiffyGWTLzma.StringResult;

/**
 * functions common to all save game methods
 * This handles non-visual stuff only eventually;
 * 
 * 1. generation of save string
 * 2. loading from save string
 * 3. triggering of save/load via optional methods, but not the methods themselves
 * 
 * @author darkflame
 *
 */
public abstract class JamSaveGameManager {

	public static Logger Log = Logger.getLogger("JAMCore.SaveGameManager");

	public static    final String END_OF_GAMEDATA_MARKER      = "__end";
	public static    final String START_OF_GAMEDATA_MARKER    = "LoadGameData=";
	protected static final String ENDLOADSCENEDATAMARKER      = "===ENDOFDATA";
	public static    final int    COMPRESSION_LENGTH_LIMIT    = 200000;
	public static    final String COMPRESSION_LIMIT_SEPERATOR = "==|==";

	//save game postfixs
	public static    final String AUTOSAVEPOSTFIX = "__AUTOSAVE";


	/**
	 * The name of the save game last loaded or saved. This will be used as the prefix for the autosave game
	 */
	public static String lastSaveName="";

	/**
	 * Stores objectstates that cant be loaded yet as the object doesn't exist<br>
	 * This happens when, for example, the state is part of a inventoryitem and that item hasn't yet been loaded<br>
	 * It can also happen for items associated with the inventory item that belong to scenesn not loaded.<br>
	 * This hashmap will be checked at the same time new objects are added to the ObjectDatabase.<br>
	 * If a match is found the pending state is loaded.<br>
	 * NOTE: only one state should be pending for a object at any one time.<br>
	 * @return
	 */
	public static HashMap<String,SceneObjectState> pendingStatesToLoad = new HashMap<String,SceneObjectState>();



	/**
	 * we override to give the data atm
	 * Slowly we will move all the essential save data here and only implementation specific stuff will be in the overide
	 * 
	 * remember before storing to add START_OF_GAMEDATA_MARKER and the start and END_OF_GAMEDATA_MARKER at the end
	 * This function will not do that for you	
	 * 
	 * @return
	 */
	public String CreateSaveString(){

		String saveString = "\n"; 

		//Optional scrollbars on the interface of the chapter controller. 
		//(only used in GWT implementation really, but its only a boolean so all versions can set it on/off)
		if (JamChapterControl.SetStoryPageScrollbars==false){
			saveString = saveString + "\n- SetStoryPageScrollbars = off"; //$NON-NLS-1$
		} else {
			saveString = saveString + "\n- SetStoryPageScrollbars = on"; //$NON-NLS-1$
		}

		//Now we save the inventory objects and their associated scene objects and popups
		Log.info("Saving inventory objects...");
		String inventoryItemsAsCommands = createInventoryContentsSaveString();

		saveString = saveString + inventoryItemsAsCommands;


		//then the profile pages if we had a notepad (gdx impl does not yet support this)	  
		Log.info("Saving profiles...");
		PlayersNotebookCore playersNotepad = JAMcore.PlayersNotepad;

		if (playersNotepad!=null){
			Log.info("Saving notebook pages/profiles...");

			for (Iterator<String> it = 	playersNotepad.LoadedPages.iterator(); it.hasNext(); )
			{
				String currentItem = it.next(); 
				saveString = saveString + "\n- AddProfile = " + currentItem; 
			}

		} else {
			Log.info("no notebook/profiles...");

		}
		//then the chapters and pages
		Log.info("saving chapters...");


		/**
		 * Saves the currently open chapters. The next foreach loop saves the associated pages with that chapter.
		 * Finally, it checks whether it's a scene - in which case it will set it to load silently in the background - or a html page/ storybox. 
		 **/
		// This new chapter iterator will replace the GWT specific one below
		for (SceneAndPageSet TempTabSet :  GameManagementClass.AllSceneAndPageSets)  {
			//TODO: store chapter name in SceneAndPageSet then we can access it directly
			String chapterName = TempTabSet.getChapterName();
			saveString = saveString + "\n- NewChapter = \"" + chapterName+"\""; //real
			//saveString = saveString + "\n- Message = \"" + chapterName+"\"";  //for tests


			//}
			//TODO: TEST THE NEW ABOVE SYSTEM. We need to save/load cupers2 to test as that has lots of chapters
			///
			//for (Widget currentItem : JAM.ChapterList.VTabPanel)  { 
			//	Log.info("number of chaps="+JAM.ChapterList.VTabPanel.getWidgetCount());
			//	 Widget currentItem = it.next();
			//	Label currentLabel= ((Label)((DecoratorPanel)(currentItem)).getWidget()); 

			//	saveString = saveString + "\n- NewChapter = \"" + currentLabel.getText()+"\""; //$NON-NLS-1$ //$NON-NLS-2$

			//	Log.info("saving chapter..."+currentLabel.getText());

			//System.out.print(saveString);

			//	int widgetnum = JAM.ChapterList.VTabPanel.getWidgetIndex(currentItem);
			//	Log.info("--"+widgetnum); 
			//	Log.info("--"+JAM.ChapterList.PanelsToTriggerList[widgetnum]); 

			// now the pages in that chapter
			//	SceneAndPageSet TempTabSet =  GameManagementClass.AllSceneAndPageSets.get(JAM.ChapterList.PanelsToTriggerList[widgetnum]);


			Log.info("--"+ GameManagementClass.AllSceneAndPageSets.get(0).OpenPagesInSet.size()); //$NON-NLS-1$

			for (Iterator<String> lt_it = TempTabSet.OpenPagesInSet.iterator(); lt_it.hasNext(); ) {

				String lt_currentItem = lt_it.next(); 

				//if its a scene we need to use the load scene command here
				if (lt_currentItem.endsWith("__scene")) {

					lt_currentItem = lt_currentItem.substring(0, lt_currentItem.length()-7);				   

					saveString = saveString + "\n- LoadSceneSilently = " + lt_currentItem+","+ lt_currentItem+",false"; 

				} else {


					saveString = saveString + "\n- StoryBox = " + lt_currentItem+".html";
				}
				//	System.out.print("="+saveString); //$NON-NLS-1$

			}

		}



		Log.info("saving music...");

		//then the music unlocked skipping the first one - because the first slot is always to turn off the music.

		for (Iterator<String> it = MusicBoxCore.musicTracks.iterator(); it.hasNext(); ) {

			String currentItem = it.next(); 

			if (currentItem.compareTo(" -NONE- ")!=0){
				saveString = saveString + "\n- AddMusicTrack = " + currentItem; //$NON-NLS-1$
			}
			Log.info(saveString);

		}

		//then the secrets unlocked 
		Log.info("saving secrets...");

		if (OptionalImplementations.JamSecretsPanel.isPresent()){
			secretsPanelCore sp = OptionalImplementations.JamSecretsPanel.get();

			for (linkItem linkItem : sp.allLinks) {


				saveString = saveString + "\n- AddSecret = " + linkItem.name +","+linkItem.data; //$NON-NLS-1$ //$NON-NLS-2$

				//	}

				//}
				//OLD GWT specific method;
				//for (Iterator<Widget> it = JAM.SecretPanel.iterator(); it.hasNext(); ) {

				//	Hyperlink currentItem = (Hyperlink) it.next(); 

				//	saveString = saveString + "\n- AddSecret = " + currentItem.getText() +","+currentItem.getTitle(); //$NON-NLS-1$ //$NON-NLS-2$

			}

			//Log.info(saveString);

		}

		//then the variables
		String GameValues = GameVariableManagement.GameVariables.serialiseForSave("- SetVariable = ");
		saveString = saveString + "\n"+GameValues;


		//set clues if present
		JamClueControll playersClues = JAMcore.playersClues;
		if (playersClues!=null){

			Log.info("saving setting; clues...");
			if (playersClues.ClueArrayAsString().length()>1){
				saveString = saveString + "\n- SetClueBox = " + playersClues.ClueArrayAsString(); //$NON-NLS-1$
			}
			JAMcore.GameLogger.info(playersClues.ClueArrayAsString());
		} else {

			Log.info("saving setting; no clue implementation to save...");
		}


		//set score if not zero
		ScoreControll playersScore = JAMcore.PlayersScore;
		if (playersScore!=null){
			Log.info("saving setting; score...");
			if (playersScore.CurrentScore!=0){
				saveString = saveString + "\n- SetScore = " + playersScore.CurrentScore; //$NON-NLS-1$
			}
		} else {
			Log.info("saving setting; no score implementation to save...");
		}
		
		// Set Score list (to stop scores reaccoring)
		saveString = saveString + "\n- PointsAwardedFor = " + ScoreControll.ScoresAwarded; //$NON-NLS-1$

		// set background
		Log.info("saving setting ;background...");
		String backgroundLoc = RequiredImplementations.getBackgroundImage();
		saveString = saveString + "\n- SetBackgroundImage = " + backgroundLoc; 
		//	saveString = saveString + "\n- SetBackgroundImage = " + RootPanel.getBodyElement().getAttribute("background"); //TODO: made GWT independant implementation


		//set current overlays
		saveString = saveString + "\n"+JamGlobalGameEffects.getInstructionsToTriggerCurrentEffects();
		
		// set current music playing
		saveString = saveString + "\n- PlayMusicTrack = " + MusicBoxCore.currentTrack+"\n";



		if (SceneObjectDatabase.currentScene!=null){
			//will pop up the scene straight away, however this is likely to be the loading animation
			//till its done
			String displayCurrentScene = "- SelectSceneSilently = "+SceneObjectDatabase.currentScene.getScenesData().SceneFolderName+"\n";

			saveString = saveString+displayCurrentScene;

			// set chapter
			Log.info("saving setting ;location...");

			String setlocation =  "- SetLocation = " + JAMcore.usersCurrentLocation+"\n"; //$NON-NLS-1$


			saveString = saveString+setlocation;

			String setScenesPosition = "- SetScenePosition = "+SceneObjectDatabase.currentScene.getPosX()+","+SceneObjectDatabase.currentScene.getPosY()+"\n";

			saveString = saveString+setScenesPosition;
		}

		//get the scene save string
		String sceneSaveStrings = getAllScenesSaveString(); //getSceneSaveString(tempState,sceneobjectsCurrentState);
		saveString = saveString+sceneSaveStrings;


		//todo; add runnamedaction information here?
		//these runnables should be triggered when the scenes are ready (all objects loaded, at least logically, as well as their states)
		String activeNamedActionSetTimers = getAllActiveNamedActionSetStates();		
		//todo; loop over runnables storing active ones data somehow
		//(actually might want to do this earlier in the save process and only use it here)		
		saveString = saveString+"\n - ResumeTimerStatesAfterLoad= "+activeNamedActionSetTimers+"\n";


		//Finally we add the current feedback message
		//we do this at the end to ensure nothing else can override it
		//and, if animated, the user will see it typed to draw the eye to it
		//saveString = saveString + "\n- Message = "+JAM.Feedback.getCurrentText()+"\n";       

		saveString = saveString + "\n- Message = "+RequiredImplementations.getCurrentFeedbackText()+"\n";       


		Log.info("saving setting gwt specific interface icons...");



		return saveString;
	}


	private String getAllActiveNamedActionSetStates() {
		
		String states = "";
		
		
		//note; we use keys not values, as we only need to save one action set per id
		for (NamedActionSetTimer set : JAMTimerController.activeNamedActionSetTimers.values()) {
			
			/*
			String timerData = "" + set.getSceneCurrentWhenCreated().SceneFileName+","
								   +set.getRunnableObject()+","
								   +set.getRunnableName()+","
								   +set.getFuseLength()+","			
								   +set.getActivateEvery_ShortestTimePossibility()+","		
								   +set.getActivateEvery_LongestTimePossibility()+","			
								   +set.getTimeRemaining()+"";
			
			*/
			String timerData = "" + set.seralise();
			
			states = states + "<nas>" + timerData+"</nas>";
			
		}
		
		
		String statesString = "<NamedActionSetStates>"+states+"</NamedActionSetStates>";
		
		
		// TODO Auto-generated method stub
		return statesString;
	}


	public static String getAllScenesSaveString(){

		String data = "";

		Log.info("getting all scenes save strings");

		//get array of all scenes
		Iterator<SceneWidget> allScenesit = SceneWidget.getAllScenes().iterator(); // SceneWidgetVisual.all_scenes.values().iterator();

		//getAllScenes()
		while (allScenesit.hasNext()) {

			//maybe temp cast
			SceneWidget currentScene =  allScenesit.next();

			//we used to skip scenes that have not been made current yet, this has been removed
			//because scenes can effect other scenes even if the player has never visited them
			//eg, from "OnLoad" actions, or from cross-scene references as objects are global
			//if (currentScene.currentState.hasNotBeenCurrentYet == true){
			//	continue; //skip it as its in default state before loading
			//}

			Log.info("getting save string for:"+currentScene.SceneFileName);

			//Generate a snapshot of the current state
			//(we should add an internal "clone" command to scenestate to make this easier)
			/*
			SceneStatus tempState = new SceneStatus(
					currentScene.currentState.SceneName,
					currentScene.currentState.PosX,
					currentScene.currentState.PosY,
					currentScene.currentState.NumOfTimesPlayerHasBeenHere,
					currentScene.currentState.currentBackground, currentScene.currentState.DynamicOverlayCSS,
					currentScene.currentState.StaticOverlayCSS,					
					currentScene.currentState.hasNotBeenCurrentYet); //we should be able to assume false here, as if its yet to be current, then the scene shouldnt be saved at all!
			 */
			
			SceneStatus tempState =	currentScene.currentState.clone();
			
			currentScene.tempState = tempState;

			Log.info("not been current:"+currentScene.currentState.hasNotBeenCurrentYet);
			Log.info("serialised state:"+currentScene.currentState.seralise());
			Log.info("serialised temp state:"+tempState.seralise());


			SceneObjectState[] scenesobjectsstate = currentScene.getAllSceneObjectData(true);



			data = data+ getSceneSaveString(tempState, scenesobjectsstate);


		}


		return data;

	}


	public static String getSceneSaveString(SceneStatus tempState,			
			SceneObjectState[] sceneobjectsCurrentState){


		//start with load scene command

		//then the data
		String data= "" +"\n";
		data= data +"- LoadSceneData="; 

		//first the scene set up data
		data=data+"<scene>"+tempState.seralise()+"<\\scene>"; 

		data=data+ "";// Objects:\n"+"\n";
		Log.info("serialising objects");

		//all objects 
		for (SceneObjectState sceneObjectData : sceneobjectsCurrentState) {

			if (sceneObjectData==null){
				continue; //we skip null entries. There might be nulls due to object collection skipping unchanged objects
			}

			Log.info("seralising object"+sceneObjectData.ObjectsName);	

			if (sceneObjectData.clonedFrom!=null){
				Log.info("cloned from:"+sceneObjectData.clonedFrom.getObjectsCurrentState().ObjectsName);	
			}



			data=data+"<object>"+sceneObjectData.serialiseToString()+""+"<\\object>";

		}
		
	
		data=data+ENDLOADSCENEDATAMARKER+"\n";

		return data;
	}

	//still too implement/refractor
	public void loadFromSaveString(String incomeingSaveString){

	}



	public static void autoSaveCurrentGameSilently(){
		String saveGameData = RequiredImplementations.saveManager.get().CreateSaveString();

		//compress it if we have a oompresser
		if (OptionalImplementations.StringCompressionMethod.isPresent()){
			OptionalImplementations.StringCompressionMethod.get().compress(saveGameData, new CompressionResult() {					
				@Override
				public void gotResult(String result) {
					updateBrowserAutoSave(result);
				}
			});
		} else {
			updateBrowserAutoSave(saveGameData);
		}


	}
	/**
	 * This should look at the last saved game name by the user and add JamSaveGameManager.AUTOSAVEPOSTFIX
	 * after it. If a autosave file of the same name exists already,we overwrite it	automatically * 
	 */
	static public void updateBrowserAutoSave(String savedata){

		if (lastSaveName.isEmpty()){
			lastSaveName="GAME";
		}		

		String name = lastSaveName;
		if (!name.endsWith(AUTOSAVEPOSTFIX)){
			name=name+AUTOSAVEPOSTFIX;
		}


		savedata = START_OF_GAMEDATA_MARKER+savedata+END_OF_GAMEDATA_MARKER;// add the start and end markers

		saveGameToBrowser(name,savedata,true);

	}

	//implementations

	public void saveGameToServer(String savedata){

		if (OptionalImplementations.ServerStorageImplementation.isPresent()){
			//triggered save to server
			OptionalImplementations.ServerStorageImplementation.get().saveGameToServerImpl(savedata);
		} else {
			Log.severe("No Server Storage Implementation Provided, so cant save via this method");
		}

	}


	public static void saveGameToBrowser(String savename,String savedataWithMarkers,boolean overwrite){

		if (OptionalImplementations.BrowserStorageImplementation.isPresent()){
			//triggered save to server			
			OptionalImplementations.BrowserStorageImplementation.get().saveGameToBrowserImpl(savename,savedataWithMarkers,overwrite);

		} else {
			Log.severe("No Browser Storage Implementation Provided, so cant save via this method");
		}

	}


	public void saveGameToEmail(String savedata){

		if (OptionalImplementations.EmailStorageImplementation.isPresent()){
			//triggered save to server
			OptionalImplementations.EmailStorageImplementation.get().saveGameToEmailImpl(savedata);
		} else {
			Log.severe("No Email Storage Implementation Provided, so cant save via this method");
		}

	}



	public static void decompressAndLoad(String totalGameData,  final boolean ClearGameFirst) {

		//strip newlines
		totalGameData = totalGameData.replaceAll("\\r\\n|\\r|\\n", " ");
		Log.info("loading:"+totalGameData);

		//remove the marker at the start
		if (totalGameData.startsWith(JamSaveGameManager.START_OF_GAMEDATA_MARKER)){
			Log.info("removing:"+JamSaveGameManager.START_OF_GAMEDATA_MARKER+" from save string");

			totalGameData=totalGameData.substring(JamSaveGameManager.START_OF_GAMEDATA_MARKER.length()); //remove the start marker
		}

		//ensure  marker exists
		if (!totalGameData.endsWith(JamSaveGameManager.END_OF_GAMEDATA_MARKER)){
			Log.severe("Save game data incomplete, no end marker found.");
			Log.severe("Last characters in save string was:   "+totalGameData.substring(totalGameData.length()-15,totalGameData.length()));			
			return;
		}

		totalGameData = totalGameData.substring(0,totalGameData.indexOf(END_OF_GAMEDATA_MARKER));

		//-----------------------------------------
		//decompress if we are using a decompression system
		//TODO: add feedback
		final CompressionResult runwhendone = new CompressionResult(){			
			@Override
			public void gotResult(String result) {
				//display (if on debug) and load resulting save string	
				RequiredImplementations.saveManager.get().displayAndLoadDecompressedString(result,ClearGameFirst);  

			}
		};

		if (OptionalImplementations.StringCompressionMethod.isPresent()){
			Log.info("decompressing string");

			OptionalImplementations.StringCompressionMethod.get().decompress(totalGameData, runwhendone);


		} else {
			Log.info("no decompression system found, assuming game doesnt use compression for its saves");

			runwhendone.gotResult(totalGameData);

		}




	}

	/**
	 * Creates a new object state from the supplied serialized data
	 * 
	 * @param objectData - as a serialized string of parameters separated by SceneObjectState.deliminator
	 * @return
	 */
	public static SceneObjectState createObjectStateFromSeralisedString(String objectData) {
		//create the correct state based on type of objectdata supplied
		SceneObjectState state = null; //the variable where we will hold the specific state		

		String typeString = objectData.substring(0, objectData.indexOf(SceneObjectState.deliminator));

		SceneObjectType type = SceneObjectType.valueOf(typeString);

		//	Log.info("loading data for a: "+typeString+" __ "+type.name());


		switch(type){
		case Sprite:
			Log.info("deserialising data for a sprite ");
			state = new SceneSpriteObjectState(objectData);
			break;			
		case DialogBox:
			Log.info("deserialising data for a DialogBox ");
			state = new SceneDialogueObjectState(objectData);
			break;			
		case Div:
			Log.info("deserialising data for a Div ");
			state = new SceneDivObjectState(objectData);
			break;
		case Input:
			Log.info("deserialising data for a Input ");
			state = new SceneInputObjectState(objectData);		
			break;
		case InventoryObject:
			Log.warning("deserialising data for a InventoryIcon (expiremental! please check!)");
			state = new InventoryObjectState(objectData);	

			Log.info("\n\n test associated:"+((InventoryObjectState)state).associatedSceneObjectWhenNeeded);
			break;
		case Label:
			Log.info("deserialising data for a Label ");	
			state = new SceneLabelObjectState(objectData);
			break;		
		case Vector:
			Log.info("deserialising data for a Vector ");
			state = new SceneVectorObjectState(objectData);
			break;
		case SceneObject:
			Log.info("deserialising generic sceneobject type:");
			state = new SceneObjectState(objectData);

			break;

		}

		return state;
	}


	//todo; should not be static?
	public static void loadState(SceneStatus sceneState, SceneObjectState[] sceneobjectsCurrentState) {
	
		//loads the scenes data 	
		Log.info("____________________loading scene state for:"+sceneState.SceneName);
		RequiredImplementations.saveManager.get().loadingfeedback("____________loading scene state for:"+sceneState.SceneName);
			
		//get the scene
		SceneWidget sceneToAlter=  SceneWidget.getSceneByName(sceneState.SceneName);
	
		
		//if found, we can load its state
		if (sceneToAlter!=null){
	
			Log.info(" loading scene data "+sceneToAlter.SceneFolderLocation);
			//sceneToAlter.ScenesLog.info("..");			
			sceneToAlter.loadSceneState(sceneState,sceneobjectsCurrentState);
			
		} else {
	
			Log.severe(" cant find scene "+sceneState.SceneName);
			Log.severe(" cant find scene "+SceneWidget.getAllScenes().toString());//.all_scenes.toString());
		}
	
	}


	protected static SceneObjectState[] getSceneObjects(String text) {
	
		ArrayList<SceneObjectState> objectdata = new  ArrayList<SceneObjectState>();
	
		Log.info("scene objects text.length() is: "+text.length());
	
		int i=0;
	
		while (i<text.length()){
	
			//search for next object
			int startpoint = text.indexOf("<object>",i); 
			int endpoint   = text.indexOf("<\\object>",startpoint); 
	
			//if start or end isn't present then exit
			if (startpoint==-1){
				break;
			}
			if (endpoint==-1){
				Log.info("no matching <\\object> found in object data.");
				Log.info("startpoint was: "+startpoint+" i was:"+i);
				break;
			}
	
	
			String objectData = text.substring(startpoint+8, endpoint).trim(); //trim the xml style start and end tags 			
	
			Log.info("Object data="+objectData);
	
			//create the correct state based on type of objectdata supplied
			SceneObjectState state = createObjectStateFromSeralisedString(objectData);
	
			//store our newly created state object if we were able to make it
			if (state!=null){
				objectdata.add(state);
			}  else {
				Log.severe("could not load state:"+objectData+" its likely a upsupported type");
			}
	
	
			// update start search position
			i=endpoint+8;
	
		}
	
		if (objectdata.size()==0){
			Log.info("object data is size 0 ");
		}
		Log.info("converting to array:"+objectdata.size());
	
		SceneObjectState[] array = new SceneObjectState[objectdata.size()];		
	
		array = objectdata.toArray(array);
	
		Log.info("converted array:");
	
		return array;
	}


	protected static SceneStatus getSceneData(String text) {
	
		//get data section
		int endpoint=text.indexOf("<\\scene>"); //<scene>
		String sceneData = text.substring(7, endpoint);
	
		Log.info("got scene data="+sceneData);
	
		//get data from string 
		SceneStatus status = new SceneStatus(sceneData);
	
		Log.info("got scene state="+status.toString());
	
		return status;
	
	}


	public static void deseraliseAndRestoreStateFromString(String state) {
	
	
		//first ensure the data is valid by checking for the end of data marker
		//if not we throw a crash at the user.
		//Its normally rude to throw stuff, but its helpful in this case
	
		if (!state.endsWith(JamSaveGameManager.ENDLOADSCENEDATAMARKER)){
	
			Log.severe("data is missing its end marker, so its probably been cropped off");
			Log.severe("data is:"+state);
			Log.severe("_____________________________________________________________________");
			Log.severe("_____________________________________________________________________");
			Log.severe("_____________________________________________________________________");
			Log.severe("_____________________________________________________________________");
	
			return;
		} else {
	
			//remove the end bit
			int endmarkerpos = state.indexOf(JamSaveGameManager.ENDLOADSCENEDATAMARKER);
			state = state.substring(0, endmarkerpos);
	
		}
	
	
		//display
		//displayData(state);
		//loadData.setEnabled(false);
		//loadData.setText("Loading..");
		
		RequiredImplementations.saveManager.get().loadDataButtonState(false, "Loading..");		//temp this line can be more simple in future
		RequiredImplementations.saveManager.get().loadingfeedback("getting scene data... ");		//temp this line can be more simple in future
		
		//compressedlink.setText(("getting scene data... "));
	
		SceneStatus scenestate =  getSceneData(state);	
		//compressedlink.setText(("getting scene objects... "));
		RequiredImplementations.saveManager.get().loadingfeedback(("getting scene objects... "));	//temp this line can be more simple in future
				
		SceneObjectState[] sceneeobjectsCurrentState = getSceneObjects(state);
		
		//get active RunNamedActions (experimental)
	//	String RunNamedActionsResumes = getScenesActiveRunNamedActions(state);
		//--
	
		//compressedlink.setText(("loading state... "));
		RequiredImplementations.saveManager.get().loadingfeedback(("loading state... "));	//temp this line can be more simple in future
		
		loadState(scenestate,sceneeobjectsCurrentState);
		RequiredImplementations.saveManager.get().loadDataButtonState(true, "Loaded..");		//temp this line can be more simple in future
		
		//loadData.setEnabled(true);
		//loadData.setText("Loaded..");
	
		//get SceneStatus
	
		//get SceneSpriteObjectData[]
	
		//load them
	
	}


	abstract public void displayAndLoadDecompressedString(String data, boolean ClearGameFirst);

	/**
	 * This should popup some sort of window to manage the various save options
	 */
	abstract public void display();

	/**
	 *  optional feedback on state of load
	 */
	abstract public void loadingfeedback(String progresstext);
	
	
	/**
	 *  enable/disable the manual load data button, and optionally set its text
	 */
	abstract public void loadDataButtonState(boolean enable,String caption);
	
	/**
	 * creates the portion of the save string that re-creates both the inventory's, their contents, and any associated items and popups.
	 * @return
	 */
	protected String createInventoryContentsSaveString() {

		String inventoryItemsAsCommands = "";

		for (String inventoryName : JAMcore.allInventorys.keySet()) {

			// - AddInventory = concepts,default,16
			InventoryPanelCore currentPanel = JAMcore.allInventorys.get(inventoryName);
			//
			//		 - AddItem = cookieconcept,concepts

			//ignore default inventory as its there by default and will crash if attempted to recreate
			if (currentPanel!=InventoryPanelCore.defaultInventory){
				inventoryItemsAsCommands = inventoryItemsAsCommands + "\n\n- AddInventory = " + inventoryName+","+currentPanel.getPanelsMode()+","+(currentPanel.getInventorysButton().getFrameTotal()+1);
			}
			
			//set if they are visible or not
			if (currentPanel.getInventorysButton().isVisible()){
				inventoryItemsAsCommands = inventoryItemsAsCommands + "\n- showinventorybutton  = "+inventoryName;				
			} else {
				inventoryItemsAsCommands = inventoryItemsAsCommands + "\n- hideinventorybutton  = "+inventoryName;
			}
			
			
			//this loop is very messy, really need a better way to iterate over items
			//maybe store them differently? A list of dropcontrollers we need to cast so much seems dumb
			//we need a simple way to get to the item contents
			//for (Iterator<ItemDropController> item_it = currentPanel.itemDropControllerList.iterator(); item_it.hasNext(); ) {

			for (Iterator<IsInventoryItem> item_it = currentPanel.inventorysCurrentItems.iterator(); item_it.hasNext(); ) {

				//tempcast, will eventually be using IsInventoryIcon directly
				//this is only here till getPopup can be used in IsInventoryIcon
				IsInventoryItem currentItem = (IsInventoryItem) item_it.next(); 


				inventoryItemsAsCommands = inventoryItemsAsCommands + "\n\n- AddItem = " + currentItem.getName()+","+inventoryName; //$NON-NLS-1$

				//if the item was a tig we also need to save its state				
				//we now have to use some complex casting unfortunately due to InventoryIcons not being widgets anymore
				//isInventoryItem itemsPopup =  ((SceneObjectVisual.MyFocusPanel)(currentItem.gwtWidget)).getParentSceneObjectAsInventoryIcon().PopUp;

				//save the state of the icons popup, if any
				IsInventoryItemPopupContent itemsPopup =  currentItem.getPopup();

				Log.info("saving item of type:"+itemsPopup.getClass());

				String itemsState = itemsPopup.getState();
				if (itemsState!=null && !itemsState.isEmpty())
				{
					Log.info("items State:"+itemsState);
					inventoryItemsAsCommands = inventoryItemsAsCommands + "\n- SetItemState = "+ currentItem.getName()+","+itemsState;
				}


				//TODO: save state of inventory icon if its changed from its initial state (the sameStateAs doesn't always pick up they are the same right now)
				if (!currentItem.getObjectsCurrentState().sameStateAs(currentItem.getInitialState())){
					inventoryItemsAsCommands=inventoryItemsAsCommands+"\n- LoadObjectData = <object>"+currentItem.getObjectsCurrentState().serialiseToString()+"<\\object>";
				} else {
					Log.info("InventoryIcon "+currentItem.getName()+" has not changed");
				}

				//save the state of the associated object (we always save this as if its in the inventory then it must have been
				//at least removed from the scene)
				//hmmm technically if someone tried saving too quickly before the associated object can be retrieved they will lose it
				//but people shouldn't be saving during the loading anyway
				SceneObject associatedSceneObject = currentItem.getObjectsCurrentState().getAssociatedSceneObject();
				if (associatedSceneObject!=null){					
					inventoryItemsAsCommands=inventoryItemsAsCommands+"\n- LoadObjectData = <object>"+associatedSceneObject.getObjectsCurrentState().serialiseToString()+"<\\object>";
				}






				//System.out.print(saveString);

			}


		}
		return inventoryItemsAsCommands;
	}

}
