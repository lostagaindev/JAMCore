package com.lostagain.Jam;

import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager;
import com.google.common.base.Optional;
import com.lostagain.Jam.Interfaces.BasicGameInformation;
import com.lostagain.Jam.Interfaces.HasScreenManagement;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.JAMGenericFileManager;
import com.lostagain.Jam.Interfaces.JamChapterControl;
import com.lostagain.Jam.SaveMangement.JamSaveGameManager;
import com.lostagain.Jam.Scene.CoordinateSpaceConvertor;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInterfaceVisualHandler;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;

public class RequiredImplementations {
	static Logger Log = Logger.getLogger("JAMCore.RequiredImplementations");

	///
	//--------------------------
	//==================
	//------------
	//=======
	//Utility functions that the whole game uses
	//This makes use of implementations that have to already be given too it.
	//If a implementation is optional, and its not present when used, the game will crash

	//SO ALL REQUIRED (despite saying optional):
	static JAMGenericFileManager fileManager=null;
	static SceneObjectFactory objectFactory = null;
	/**
	 * REQUIRED: functions that return needed information from the implementation.
	 * For example, the current width or height of the interface
	 */
	public static Optional<BasicGameInformation> BasicGameInformationImplemention = Optional.absent();
	public static Optional<BasicGameFunctions>     BasicGameFunctionsImplemention = Optional.absent();
	public static Optional<IsInterfaceVisualHandler>      interfaceVisualElements = Optional.absent();
	public static Optional<HasScreenManagement>                     screenManager = Optional.absent();

	public static Optional<JamSaveGameManager>                        saveManager = Optional.absent();


	/**
	 * sets all required implementations NOTE; basic game informat6ion should be set first before running this
	 * 
	 * @param interfaceVisualElements
	 * @param screenManagerImplementation
	 * @param BasicGameFunctionsImp
	 * @param FileManagerImp
	 * @param savemanager
	 * @param objectFactory
	 * @param scoreDisplay - visual widget to display scores at Tag or Div ID "ScoreDisplayHolder"
	 * 
	 * @return
	 */
	public static Object setAllRequiredImplementations(IsInterfaceVisualHandler interfaceVisualElements,
			HasScreenManagement screenManagerImplementation,
			BasicGameFunctions BasicGameFunctionsImp,
			JAMGenericFileManager FileManagerImp,
			JamSaveGameManager savemanager,
			SceneObjectFactory objectFactory,
			JamChapterControl chapterControll,
			CoordinateSpaceConvertor coordinateSpaceConverter,
			ScoreControll scoreDisplay
			) {

		//--Set all implementations--
		setInterfaceVisualElementsImplementation(interfaceVisualElements);
		setScreenManagementImplementation(screenManagerImplementation);
		setBasicGameFunctionsImplemention(BasicGameFunctionsImp);		
		SetFileManager( FileManagerImp);
		setSaveManager(savemanager);
		//---

		SceneObjectFactory.setup(objectFactory);
		
		//set the coordinate space interpreter
		CoordinateSpaceConvertor.setup(coordinateSpaceConverter);
		

		JAMcore.GamesChaptersPanel = chapterControll; 
		JAMcore.PlayersScore = scoreDisplay;
		
		return null;
	}



	public static void setSaveManager(JamSaveGameManager savemanager) {
		RequiredImplementations.saveManager = Optional.fromNullable(savemanager);

	}


	public static void setInterfaceVisualElementsImplementation(IsInterfaceVisualHandler interfaceVisualElements) {		
		RequiredImplementations.interfaceVisualElements = Optional.fromNullable(interfaceVisualElements);

	}
	/**
	 * call this function to set the class that manages adding stuff to the screen directly (ie, interface elements
	 * or other things not on a scene)
	 * @param HasScreenManagement
	 */
	public static void setScreenManagementImplementation(HasScreenManagement screenManagerImplementation) {
		RequiredImplementations.screenManager = Optional.of(screenManagerImplementation);
	}



	public static BasicGameFunctions setBasicGameFunctionsImplemention(BasicGameFunctions BasicGameFunctionsImp) {
		RequiredImplementations.BasicGameFunctionsImplemention = Optional.of(BasicGameFunctionsImp);

		//set out global logger here too
		JAMcore.GameLogger = BasicGameFunctionsImp.getLogger();

		return BasicGameFunctionsImp;
	}


	/** 
	//should be run asap as many things depend on the variables in BasicGameInformation
	 * 
	 * REQUIRED: functions that return needed information from the implementation.
	 * For example, the current width or height of the interface
	 */
	public static BasicGameInformation setBasicGameInformationImplemention(BasicGameInformation basicGameInformationImplemention) {


		BasicGameInformationImplemention = Optional.of(basicGameInformationImplemention);

		JAMcore.setupEngineVariables(
				BasicGameInformationImplemention.get().getGameName(),
				BasicGameInformationImplemention.get().getHomedirectory(),
				//"../",
				BasicGameInformationImplemention.get().getSecureFolderLocation(),
				BasicGameInformationImplemention.get().getLanguageExtension(),
				BasicGameInformationImplemention.get().getRequiredLogin(),
				BasicGameInformationImplemention.get().getHasServerSave(),
				BasicGameInformationImplemention.get().getDisableAutoSave(),
				BasicGameInformationImplemention.get().getQuality(),
				BasicGameInformationImplemention.get().getDebugSetting()
				);
		return null;
	}




	public static void closeAllOpenPopUps() {
		if (screenManager.isPresent()){
			screenManager.get().CloseAllOpenPopUps();
		} else {
			Log.severe("Can not set closeAllOpenPopUps - screenManager not set");			
		}

	}

	static public int getCurrentGameStageHeight(){		
		if (!BasicGameInformationImplemention.isPresent()){		
			Log.severe("Can not get stage height- BasicGameInformationImplemention not set");	
		}		
		return BasicGameInformationImplemention.get().getCurrentGameStageHeight();					
	}

	static public int getCurrentGameStageWidth(){
		if (!BasicGameInformationImplemention.isPresent()){		
			Log.severe("Can not get stage width- BasicGameInformationImplemention not set");	
		}		
		return BasicGameInformationImplemention.get().getCurrentGameStageWidth();	
	}




	public static JAMGenericFileManager getFileManager() {
		return fileManager;
	}



	public static void openSavePanel() {
		if (screenManager.isPresent()){
			screenManager.get().openSavePanel();
		} else {
			Log.severe("Can not set openSavePanel - screenManager not set");			
		}
	}

	/**
	 * triggered when a popup panel is opened.
	 * This method doesnt have to do anything, but you could use it to trigger some onscreen change when this happens
	 * eg. Fading out the background, pausing the game, or (as the cuypers code does) animate a little
	 * box that lets you close all the open popups at once.
	 * @param newInventoryFrame
	 */
	public static void popupPanelOpened(IsPopupPanel newInventoryFrame) {
		if (screenManager.isPresent()){
			screenManager.get().PopUpWasOpened(newInventoryFrame);
		} else {
			Log.severe("Can not set popupPanelOpened - screenManager not set");			
		}

	}

	public static void PositionByCoOrdinates(Object placeThis, int x, int y , int z) {
		if (screenManager.isPresent()){
			screenManager.get().PositionByCoOrdinates(placeThis,  x,  y,  z);
		} else {
			Log.severe("Can not place element by co-ordinates: No screenManager was supplied during setup");			
		}
	}

	public static void PositionByTag(Object placeThis, String atThisTag) {
		if (screenManager.isPresent()){
			screenManager.get().PositionByTag(placeThis, atThisTag);
		} else {
			Log.severe("Can not place element by tag "+atThisTag+": No screenManager was supplied during setup");			
		}
	}
	public static void PositionByTag(SceneObject placeThis, String atThisTag) {
		if (screenManager.isPresent()){
			screenManager.get().PositionByTag(placeThis, atThisTag);
		} else {
			Log.severe("Can not place sceneobject by tag "+atThisTag+": No screenManager was supplied during setup");			
		}
	}
	
	public static boolean hasTag(String atThisTag) {
		if (screenManager.isPresent()){
			return screenManager.get().hasTag(atThisTag);
		} else {
			Log.severe("Can not test for tag "+atThisTag+": No screenManager was supplied during setup");
			return false;
		}
	}
	public static void resetGame() {
		if (BasicGameFunctionsImplemention.isPresent()){
			BasicGameFunctionsImplemention.get().resetGame();
		}
	}

	public static void setBackgroundImage(String imageLoc) {
		if (screenManager.isPresent()){
			screenManager.get().setBackgroundImage(imageLoc);
		} else {
			Log.severe("Can not set background - screenManager not set");			
		}

	}

	
	public static String getBackgroundImage() {
		if (screenManager.isPresent()){
			return screenManager.get().getBackgroundImage();
		} else {
			
			Log.severe("Can not get background - screenManager not set");	
			return "";
		}

	}

	public static void setCurrentFeedbackRunAfter(Runnable runnable) {
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setCurrentFeedbackRunAfter(runnable);
		}	

		//
	}

	static public void setCurrentFeedbackText(String text) {		
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setCurrentFeedbackText(text);
		}		
	}
	static public String getCurrentFeedbackText() {		
		if (interfaceVisualElements.isPresent()){
			return interfaceVisualElements.get().getCurrentFeedbackText();
		}		else {
			return "";
		}
	}
	
	static public void setCurrentFeedbackTextDelay(int delay) {		
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setCurrentFeedbackTextDelay(delay);
		}		
	}

	public static void setCurrentFeedbackTextSpeed(int parseInt) {
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setCurrentFeedbackTextSpeed(parseInt);
		}
	}


	public static void setFeedbackKeyBeep(String trackName) {
		//	JAM.Feedback.setCUSTOM_KEY_BEEP(MESSAGE_KEY_BEEP);
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setFeedbackKeyBeep(trackName);
		}
	}
	public static void setFeedbackSpaceKeyBeep(String trackName) {
		//	JAM.Feedback.setCUSTOM_KEY_BEEP(MESSAGE_KEY_BEEP);
		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setFeedbackSpaceKeyBeep(trackName);
		}
	}
	/**
	 * A SSSGenericFileManager file manager class MUST be set
	 * SuperSimpleSemantics has a implementation for both JAVA native and GWT
	 * @param fileManager
	 */
	public static void SetFileManager(JAMGenericFileManager fileManager) {
		RequiredImplementations.fileManager = fileManager;
	}

	public static void setHeldItemVisualisation(IsInventoryItem holdThis) {

		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setHeldItemVisualisation(holdThis);
		}

	}
	/**
	 * This sets the visibility of a interface element to hide/show that I item is being held
	 * (optional implementation)
	 * @param b
	 */
	public static void setHeldItemVisualiserVisible(boolean b) {

		if (interfaceVisualElements.isPresent()){
			interfaceVisualElements.get().setHeldItemVisualiserVisible(b);
		}

	}


	public static void setWindowTitle(String string) {
		if (BasicGameFunctionsImplemention.isPresent()){
			BasicGameFunctionsImplemention.get().setWindowTitle(string);

		}
	}
	public static void toggleLoadGamePopUp() {
		if (screenManager.isPresent()){
			screenManager.get().toggleLoadGamePopUp();
		} else {
			Log.severe("Can not toggleLoadGamePopUp - screenManager not set");			
		}

	}


	public static String getLocalSecureFolderLocation() {
		if (BasicGameInformationImplemention.isPresent()){
			return	BasicGameInformationImplemention.get().getSecureFolderLocation();
		} else {
			Log.severe("Can not getLocalFolderLocation - BasicGameInformationImplemention not set");			
		}
		return "";
	}



	public static String getHomedirectory() {
		if (BasicGameInformationImplemention.isPresent()){
			return	BasicGameInformationImplemention.get().getHomedirectory();
		} else {
			Log.severe("Can not getHomedirectory - BasicGameInformationImplemention not set");			
		}
		return "";
	}


}
