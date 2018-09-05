package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.SceneObjects.SceneObjectState;

/**
 * FOR FUTURE USE:<br>
 * <br>
 * Defines what a SceneObject can do. <br>
 * This lets the instruction processor manipulate them without knowing anything about GWT widgets or functions.<br>
 * This will be critical when we separate out the instruction processor into JAMCore, which will be pure Java.<br>
 * <br><br>
 * <br>
 * @author Tom<br>
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
public interface IsSceneObjectImplementation extends IsSceneObject, hasUserActions {


	


	



	
}

