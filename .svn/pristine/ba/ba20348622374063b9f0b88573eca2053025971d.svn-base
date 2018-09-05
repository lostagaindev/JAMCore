package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.SceneAndPageSet;
import com.lostagain.Jam.Factorys.JamMenuBar;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.Interfaces.IsBamfImage;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.hasInventoryButtonFunctionality;
import com.lostagain.Jam.Interfaces.PopupTypes.IsPopupContents;
import com.lostagain.Jam.Scene.SceneMenuWithPopUp;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.Scene.TextOptionFlowPanelCore;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.IconMode;

//all methods needed to create or clone objects
/**
 * setup has to be run for this to work.
 * You MUST supply a implemented subtype (such as GWTSceneObjectFactory) in the static setup(..) function
 * for the scene object  factory - and thus the games object creation functions 
 * to work  
 * @author darkflame
 *
 */
public abstract class SceneObjectFactory {
	public static Logger Log = Logger.getLogger("JAM.SceneObjectFactory");
	
		
	static SceneObjectFactory instance;

	public static void setup(SceneObjectFactory subClass) {
		instance = subClass;
	}
	
	/**
	 * Returns a clone of a object
	 * 
	 * SPRITES  - can be cloned 100% works (initialise implemented)
	 * DialogBox - embeded objects not appearing (might need its own initilisation for them? but shouldnt its label supertype deal with this?)
	 * LABEL - seems to work (initialize implemented)
	 * 
	 * @param object
	 * @param clonesName
	 * @return
	 */
	public static SceneObject returnclone(SceneObject object, String clonesName){

		
		SceneObject clone = null;
		SceneWidget objectScene = object.getParentScene(); 
	//	SceneObjectType objectTypeToClone = object.getObjectsCurrentState().getPrimaryObjectType();
		
		
		//first we do the global stuff needed for all sceneobject types
		//first copy our state 
		SceneObjectState newClonesState = object.getObjectsCurrentState().copy();			
		
		//change this new state where needed
		newClonesState.ObjectsName = clonesName;
		newClonesState.ObjectsSceneName = objectScene.SceneFileName;
		
		Log.info("new state scene set too:"+newClonesState.ObjectsSceneName);
		ActionList objectsActions = object.objectsActions;
		

		Log.info("oxoxo new cloned state scene set too:"+newClonesState.serialiseToString()); //checked, has all data
		
		//Now get the object itself
		clone =	createObjectFromExistingData(newClonesState,objectsActions,objectScene);
		
		/*
		if (objectTypeToClone == SceneObjectType.Sprite) {
			
			//get the object casted
			SceneSpriteObject spriteObjectToClone = (SceneSpriteObject) object;
			
			//debug stuff (temp)
			SOLog.info("cloning "+spriteObjectToClone.getName()+". Should be in focus panel="+spriteObjectToClone.shouldBeInFocusPanel());	
			
			//copy the object		
			//Note: we cast to ensure the correct constructor is triggered, as we dont want to lose information from the sprites state
			
			SceneSpriteObject spriteclone = new SceneSpriteObject( (SceneSpriteObjectState) newClonesState,  objectsActions ,objectScene);
			SOLog.info("cloned is "+spriteclone.getName()+". Should be in focus panel="+spriteclone.shouldBeInFocusPanel());	
			
			clone = spriteclone;
			
		} else {

			SOLog.severe("Cloning on non-sprite objects not yet supported");

		}
		
		if (objectTypeToClone == SceneObjectType.Div) {

			// not supported yet

			// make sure it knows where it comes from
			// clone.objectsData.clonedFrom = this;
			return null;
		}

		
		*/
		
		
			//add it to the scene at a arbitrary point			
			//This function deals with pointEvents and shouldBeInFocusPanel() differences;
			objectScene.addObjectToScene(clone);
			//
			
			//now update its state
			clone.alreadyLoaded = false;

			Log.info("initialising:" + clone.getName());
			clone.initCurrentState(true);		//just for sprites implemented? probably all object types need this?

			// add to scene
			Log.info("adding clone object to scenes lists:" + clone.getName());

			objectScene.getScenesData().addToScenesObjects(clone);

			// make sure it knows where it comes from
			clone.getObjectsCurrentState().clonedFrom = object;

		
		return clone;
		//return instance.returnCloneImplementation(object, clonesName);
	}

	/**
	 * 
	 * @param sceneObjectData - should be a subclass of SceneObjectState with the data already loaded
	 * @param actions
	 * @param sceneObjectBelongsTo
	 * @return
	 */
	public static hasInventoryButtonFunctionality createNewInventoryButton(String loc, int aniLen){
		return instance.createNewInventoryButtonImplementation(loc,  aniLen);
	}
	
	/**
	 * 
	 * @param sceneObjectData - should be a subclass of SceneObjectState with the data already loaded
	 * @param actions
	 * @param sceneObjectBelongsTo
	 * @return
	 */
	public static IsBamfImage createNewBamfImage(String loc){
		return instance.createNewBamfObjectImplementation(loc);
	}
	
	
	/**
	 * Creates a new inventory panel
	 * 
	 * @param CurrentProperty - name
	 * @param Mode - text or sprite mode
	 * @return
	 */
	public static InventoryPanelCore createInventoryPanel(String Name,IconMode Mode){
		return instance.createInventoryPanelImplementation(Name, Mode);
	}
	
	protected abstract InventoryPanelCore createInventoryPanelImplementation(String Name,IconMode Mode);
	
	/**
	 * creates a new object from new data.
	 * 
	 * @param actions
	 * @param newobjectdata - should be a newly created sceneobjectstate NOT a subclass of it.
	 * @param scenesWidget
	 * @return
	 */
	public static SceneObject createNewObjectFromData(ActionList actions, SceneObjectState newobjectdata,	SceneWidget scenesWidget) {
		return instance.createNewObjectImplementation(actions,newobjectdata,scenesWidget);
	}
	
	/**
	 * 
	 * @param sceneObjectData - should be a subclass of SceneObjectState with the data already loaded
	 * 
	 * @param actions
	 * @param sceneObjectBelongsTo
	 * @return	
	 */
	public static SceneObject createObjectFromExistingData(SceneObjectState sceneObjectData, ActionList actions,
			SceneWidget sceneObjectBelongsTo) {
		
	//	SceneWidget sceneItBelongsTo = sceneObjectBelongsTo; 
		
		//the variable for the new object
	//	SceneObject newObject = null; 
		
		//specific creation varies based on type
	//	SceneObjectType primaryObjectType = sceneObjectData.getPrimaryObjectType();
		
		
		SceneObject newObject  = instance.createNewObjectImplementation(actions,sceneObjectData,sceneObjectBelongsTo);

		Log.info("oxoxo  created object with state:"+newObject.getObjectsCurrentState().serialiseToString()); 
		newObject.updateState(sceneObjectData); //runs updateState(sceneObjectData,true,true) on subtype
		//TODO: is this needed? update state might not even work if its not on the scene yet?
		//might be best just to initialize after attachment?
		Log.info("oxoxo updated state to:"+newObject.getObjectsCurrentState().serialiseToString());
		
		
		return newObject;
		
	//	return instance.createObjectFromExistingDataImplementation(sceneObjectData, actions, sceneObjectBelongsTo);
	}

	
	public static JamMenuBar createJamMenuBar(SceneMenuWithPopUp parentbox) {
		return instance.createJamMenuBarImplementation(parentbox);
	}
	
	

	public static IsPopupPanel createTitledPopUp(
		    IsInventoryItem IconTrigger,
		    String X,
		    String Y,
			String title,
			IsPopupContents SetContents) {			
		
		return instance.createTitledPopUpImplementation(
			     IconTrigger,
			     X,
			     Y,
				 title,
				 SetContents);
	}

	public static IsPopupPanel createBasicPopUp(IsPopupContents SetContents) {				
		return instance.createBasicPopUpImplementation( SetContents);
	}
	
	
	public static SceneWidget makeNewScene(String CurrentLoc){
		return instance.makeNewSceneImplementation(CurrentLoc);
	}

	public static SceneAndPageSet createSceneAndPageSet(String chapterName) {
		return instance.createSceneAndPageSetImplementation(chapterName);
	}

	 
	public static TextOptionFlowPanelCore createTextOptionFlowPanel(ArrayList<String> options,	SceneObject callingObject, String divName, String tabName) {
		return  instance.createTextOptionFlowPanelImplementation( options,callingObject,divName,  tabName);
	}
	
	protected abstract SceneAndPageSet createSceneAndPageSetImplementation(String chapterName);

	/**
	 * Creates a Popup Panel with a title and close button
	 * This should be a visual element able to contain IsPopUpContents, and optionally be dragged about by
	 * the user. Its used for inventorys as well as inventoryitems.
	 * It might also be used for other interface elements
	 * 
	 * @param object
	 * @param clonesName
	 * @return
	 */
	protected abstract IsPopupPanel createTitledPopUpImplementation(
		    IsInventoryItem IconTrigger,
		    String X,
		    String Y,
			String title,
			IsPopupContents SetContents);
	

	/**
	 * Creates a Basic Popup Panel.
	 * This should be a visual element able to contain IsPopUpContents, and optionally be dragged about by
	 * the user. Its used for inventorys as well as inventoryitems.
	 * It might also be used for other interface elements
	 * 
	 * @param object
	 * @param clonesName
	 * @return
	 */
	protected abstract IsPopupPanel createBasicPopUpImplementation(IsPopupContents SetContents);
	
	/**
	 * 
	 * @param object
	 * @param clonesName
	 * @return
	 */
	//protected abstract SceneObject returnCloneImplementation(SceneObject object, String clonesName);
	

	/**
	 * Creates a new object of the correct type from the data supplied
	 * 
	 * @param sceneObjectData - the scene object data of the correct subtype
	 * @param actions - the actions the object will have (can be null)
	 * @param sceneItBelongsTo - the scene the object belongs too
	 * @return
	 */
	//protected abstract SceneObject createObjectFromExistingDataImplementation(SceneObjectState sceneObjectData, ActionList actions,
	//		SceneWidget sceneObjectBelongsTo);

	
	/**
	 * 
	 * @param actions
	 * @param newobjectdata - the state object, which is safe to cast to its subtype
	 * @param scenesWidget
	 * @return
	 */
	protected abstract SceneObject createNewObjectImplementation(ActionList actions, SceneObjectState newobjectdata,
			SceneWidget scenesWidget);


	/**
	 * Not, in fact, a scene object but rather a inventory button creator. This will return a implementation
	 * of a inventory button ready to be put on the user interface.
	 * This function is in SceneObjectFactory purely as the best match for what it does.
	 * Dont expect the inventory button to be a sceneobject. Because it isnt.
	 * 
	 * @param string
	 * @param aniLen
	 * @return
	 */
	public abstract hasInventoryButtonFunctionality createNewInventoryButtonImplementation(String string, int aniLen);
	
	

	/**
	 * Not, in fact, a scene object but rather a bamf image creator. This will return a implementation
	 * of a bamf image ready to be faded on/off.
	 * This function is in SceneObjectFactory purely as the best match for what it does.
	 * Dont expect the bamf to be a real sceneobject. Because it isnt.
	 * 
	 * @param image location
	 * @return
	 */
	public abstract IsBamfImage createNewBamfObjectImplementation(String location);

	

	public abstract SceneWidget makeNewSceneImplementation(String location);


	public abstract JamMenuBar createJamMenuBarImplementation(SceneMenuWithPopUp parentbox);
		


	
	public abstract TextOptionFlowPanelCore createTextOptionFlowPanelImplementation(ArrayList<String> options,	SceneObject callingObject, String divName, String tabName);
	
	
	
		
	
}
