package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.SceneObjects.SceneDivObjectState;
import com.lostagain.Jam.SceneObjects.SceneVectorObjectState;

/**
 * FOR FUTURE USE:<br>
 * <br>
 * Defines what a SceneDivObject can do. <br>
 * This lets the instruction processor manipulate them without knowing anything about GWT widgets or functions.<br>
 * This will be critical when we separate out the instruction processor into JAMCore, which will be pure Java.<br>
 * <br><br>
 * <br>
 * @author Tom<br>
 *

* General;
* 
*  isSceneDivObject() - tells the engine it can handle css styleing functions, or a emulation thereof. It also currently tells it that it can handle putting a widget in it, but this might change
* 
* InstructionProcessor functions should not use SceneObjectVisual, but rather isSceneDivObject for all html/div related operations
*  
*
* GWT engine;
* 
*  ALL scene object types implement isSceneDivObject
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
//TODO: rename to IsSceneStyleableObect
public interface IsSceneDivObject extends IsSceneObject {

	
	/**sets the css of this object (think of it as the outerbox containing the object
	 * if its a text object and you want to set the text css, you should use settextcss)**/
	void setBoxCSS(String currentProperty);

	/**adds a the css of this object (think of it as the outerbox containing the object
	 * if its a text object and you want to set the text css, you should use settextcss)**/
	void addBoxCSS(String currentProperty);

	/**removes a css of this object (think of it as the outerbox containing the object
	 * if its a text object and you want to set the text css, you should use settextcss)**/
	void removeBoxCSS(String currentProperty);

	/** This sets the CSS style.
	 * To explain this further; This sets the css style **/
	void setCSS(String primaryName);
	
	/**
	 * does this object have the specified CSS class applied currently?
	 * @param currentProperty
	 * @return
	 */
	boolean hasClass(String currentProperty);
	
	
	
	
	/**
	 * puts a widget in the div
	 * This is used for providing text options.
	 * 
	 * In future we might have this as a separate interface on its own so that non-gwt things can support text option lists
	 * without also needing CSS support required above
	 * 
	 * @param widget - should be a GWT widget if a get implemrntation
	 */
	void setDivsWidget(Object widget);
	
	
	
	
	

	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneDivObjectState getObjectsCurrentState();
	@Override
	public SceneDivObjectState getTempState();
	@Override
	public SceneDivObjectState getInitialState();



	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneDivObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);
	
	
	
	
	

	

}