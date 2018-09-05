package com.lostagain.Jam.SceneObjects.Interfaces;

import com.darkflame.client.semantic.SSSNode;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;

/**
 *

* General;
* IsSceneObject() - tells the engine it can handle all normal SceneObject stuff (probably not needed as they will all be just )
*  isSceneDivObject() - tells the engine it can handle all div related functions (like styling)
* 
* InstructionProcessor functions should not use SceneObjectVisual, but rather isSceneDivObject for all html/div related operations
*  
*
* GWT engine;
* 
*  ALL object types implement isSceneDivObject which in turn implements IsSceneObject
*  They do this automatically by extending SceneObjectVisual
*  By calling super() during setup on their subtype, they will call SceneObjectVisuals setup that will add the object to the relevant DivObject list for scene and game
*     
*  In addition they might implement other things (isSpriteObject), and thus appear on those lists too. They will handle being added to those lists 
*  themselves, however, in their own constructor.
*  
*  InstructionProcessor functions should not use SceneObjectVisual, but rather isSceneDivObject for all html/div related operations
*  
* 
*
*/
public interface IsSceneObject extends  hasUserActions {

	//define all functions universe to all object types, but not specific to html in any way (ie, no styleing)
	
	//This will at least include;
	
	//Setposition/all movement commands
	//Save state/load state
	//Add/remove propertys
	//
	
	
	/**
	 * 
	 */
	public void stopObjectsSounds();
	
	/**
	 * resumes any stopped but not cleared sounds
	 */
	public void resumeObjectsSounds();
	

	/**
	 * completely removes the objects sound
	 */
	public void clearObjectsSounds();
	

	public String getName();

	public int getX();
	public int getY();

	public void wasLastObjectUpdated();

//	public ActionList getObjectsActions();
	
	//public SceneObjectState getObjectsCurrentState();
	
	//public SceneObjectState getTempState();
	
	//public SceneObjectState getInitialState();
	

	void setVisible(boolean b);
	
	public boolean hasProperty(String property);
	public boolean hasProperty(SSSNode property);
	
	public void ObjectsLog(String string);

	void updateDebugInfo();

	public void removeObject(boolean removeFromLists,boolean removeFromInventorys);

	
	//public ActionList getObjectsActions();
	
	public void wasLastObjectClicked();
	


	public ActionList getObjectsActions();
	
	
	public SceneObjectState getObjectsCurrentState();
	
	public SceneObjectState getTempState();
	
	public SceneObjectState getInitialState();

	public SceneWidget getParentScene();

	//just for sprites (method is in sceneobject but gets redirected to sprite specific function)
	//this is to allow implementations to share code easier
	public void setSpritesURL(SceneSpriteObjectState state,String url,int Frames);
	
	//just for inventory items
	void justUsedButNoActionsFound();
	void justUsedButOnlyDefaultActionsFound();
	void justUsedSpecificActionsFound();
	
}

