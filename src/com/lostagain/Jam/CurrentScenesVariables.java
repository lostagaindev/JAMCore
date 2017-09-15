package com.lostagain.Jam;

import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDivObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneInputObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneLabelObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneVectorObject;

public class CurrentScenesVariables {

	// static data determining current game state
	// maybe move these somewhere else?

	/**
	 * the last clicked x location, relative to the scene
	 */
	public static int lastclicked_x = 0;

	/**
	 * the last clicked y location, relative to the scene
	 */
	public static int lastclicked_y = 0;
	/**
	 * 3d implementations might need the 3d click location relative to their scene co-ordinates
	 * Therefor, we also have z
	 */
	public static int lastclicked_z = 0;

	public static int lastclickedscreen_x = 0;
	public static int lastclickedscreen_y = 0;
	//--

	/**
	 * The last scene object clicked on, either with a left or a right click
	 * 
	 */
	public static SceneObject lastSceneObjectClicked = null;

	public static IsInventoryItem   lastInventoryObjectClickedOn = null; //check if this is really last clicked on, rather then last updated

	//The following should be moved to CurrentSceneVariables in the JamCore once
	//the interfaces (Is...) are moved over. (which in turn depends on SceneObjectState being moved over)
	/** last object of any type that was interacted with **/
	public static SceneObject lastSceneObjectUpdated = null;

	public static SceneObject       lastObjectThatTouchedAnother = null;

	public static IsSceneInputObject     lastInputObjectUpdated  = null;

	public static IsSceneVectorObject    lastVectorObjectUpdated = null;

	public static IsSceneDivObject       lastDivObjectUpdated    = null;

	public static IsSceneDialogueObject lastDialogueObjectUpdated = null;

	/** the last text object (Label or Dialogue) clicked or interactive with in any way **/
	public static IsSceneLabelObject        lastTextObjectUpdated = null;

	/** the last sprite object clicked on; Note this used to be the only object type
	 * so some parts of the code might need to be updated to use the generic last click on above **/
	public static IsSceneSpriteObject lastSpriteObjectUpdated = null;


	/**
	 * returns a string of all the current reserved game variables and their values
	 * @return
	 */
	public static String debugCurrentVariableValues() {

		String currentvals = ""
				+"\n lastclicked_x:"+lastclicked_x
				+"\n lastclicked_y:"+lastclicked_y
				+"\n lastclicked_z:"+lastclicked_z
				+"\n lastclickedscreen_x:"+lastclickedscreen_x
				+"\n lastclickedscreen_y:"+lastclickedscreen_y
				+"\n";

		if (lastSceneObjectClicked!=null){
			currentvals=currentvals+"\n lastSceneObjectClicked:"+lastSceneObjectClicked.getName();
		}
		if (lastInventoryObjectClickedOn!=null){
			currentvals=currentvals+"\n lastInventoryObjectClickedOn:"+lastInventoryObjectClickedOn.getName();
		}
		if (lastSceneObjectUpdated!=null){
			currentvals=currentvals+"\n lastSceneObjectUpdated:"+lastSceneObjectUpdated.getName();
		}
		if (lastObjectThatTouchedAnother!=null){
			currentvals=currentvals+"\n lastObjectThatTouchedAnother:"+lastObjectThatTouchedAnother.getName();
		}
		if (lastInputObjectUpdated!=null){
			currentvals=currentvals+"\n lastInputObjectUpdated:"+lastInputObjectUpdated.getName();
		};

		if (lastVectorObjectUpdated!=null){
			currentvals=currentvals+"\n lastVectorObjectUpdated:"+lastVectorObjectUpdated.getName();
		}
		if (lastDivObjectUpdated!=null){
			currentvals=currentvals	+"\n lastDivObjectUpdated:"+lastDivObjectUpdated.getName();
		}
		if (lastDialogueObjectUpdated!=null){
			currentvals=currentvals	+"\n lastDialogueObjectUpdated:"+lastDialogueObjectUpdated.getName();
		}
		if (lastTextObjectUpdated!=null){
			currentvals=currentvals	+"\n lastTextObjectUpdated:"+lastTextObjectUpdated.getName();
		}
		if (lastSpriteObjectUpdated!=null){
			currentvals=currentvals+"\n lastSpriteObjectUpdated:"+lastSpriteObjectUpdated.getName();
		}		

		return currentvals;
	}




}
