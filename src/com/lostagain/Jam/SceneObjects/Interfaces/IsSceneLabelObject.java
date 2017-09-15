package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.SceneObjects.SceneDivObjectState;
import com.lostagain.Jam.SceneObjects.SceneLabelObjectState;

/**
 * FOR FUTURE USE:<br>
 * <br>
 * Defines what a SceneDialogueObject can do. 
 * This lets the instruction processor manipulate them without knowing anything about GWT widgets or functions.<br>
 * This will be critical when we separate out the instruction processor into JAMCore, which will be pure Java.<br>
 * <br><br>
 * <br>
 * @author Tom<br>
 *
 */
public interface IsSceneLabelObject extends IsSceneObject {
	

	/**
	 * Sets the text tp the specified contents.
	 * It will either display instantly, or type it out, depending on the objects "objectsCurrentState.TypedText"
	 * setting.
	 * It should also ideally wait for scene loading to finnish, if theres potential elements that might be inserted into the text.
	 * 
	 * @param nexttextcontents
	 */
	public void setText(String nexttextcontents);

	/** sets the current text to a specific string, either straight away, or after being typed **/
	public void setText(String nexttextcontents,boolean Typed);


	/**
	 * Add the css to the text and updates the objects CSSname variable 
	 * @param currentProperty
	 */
	public void addTextCSS(String currentProperty);
			

	/**
	 * set the css to the text and updates the objects CSSname variable 
	 * @param currentProperty
	 */
	public void setTextCSS(String name);
	
	

	/**
	 * remove the css to the text and updates the objects CSSname variable 
	 * @param currentProperty
	 */
	public void removeTextCSS(String name);

	

	
	
	
	//public void setURL(String URL)
	//public void setURL(String URL, int Frames)
	//
	/** sets the name of the current paragraph.
	 *  If "TriggerNow" is set true, the paragraph will be displayed straight away on its first page **/
	//	public void setParagraphName(String name,boolean TriggerNow) 
	//public void setParagraph(int num) {
	//public void addParagraphCSS(String name) {
	//public void removeParagraphCSS(String name) {
	//public void nextParagraph() {
	

	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneLabelObjectState getObjectsCurrentState();
	@Override
	public SceneLabelObjectState getTempState();
	@Override
	public SceneLabelObjectState getInitialState();



	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneLabelObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);

	/**
	 * 
	 * @return
	 */
	public String getCurrentText();
	
	
	
}
