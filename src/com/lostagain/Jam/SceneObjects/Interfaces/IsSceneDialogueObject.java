package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.DialogueCollection;
import com.lostagain.Jam.SceneObjects.SceneDialogueObjectState;

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
public interface IsSceneDialogueObject extends IsSceneLabelObject {
	
	
	/**
	 * This function should load a dialogue file.
	 * The text object will display the "default" text by..umm..default.
	 * @param url
	 */
	public void setURL(String url);
	
	
	/**
	 * goes to the previous paragraph
	 */
	public void previousParagraph();

	/**
	 * 
	 */
	public void nextParagraph();
	
	/**
	 * dialogue implementations should use a DialogueCollection to store and retrieve their text
	 * @return
	 */
	public DialogueCollection getKnownParagraphs();
	/**
	 * dialogue implementations should use a DialogueCollection to store and retrieve their text
	 * @return
	 */
	public void setKnownParagraphs(DialogueCollection knownParagraphs);
/**
 * 
 * @param paragraphName
 * @param b
 */
	public void setParagraphName(String paragraphName, boolean TriggerNow);
	

	public String getParagraphName();
	

	public int getParagraphNumber();
	
	/**
	 * 
	 * @param valuepara
	 */
	public void setParagraph(int num);

	
	/** sets the name of the current paragraph.
	 *  If "TriggerNow" is set true, the paragraph will be displayed straight away on its first page **/
	//	public void setParagraphName(String name,boolean TriggerNow) 
	//public void setParagraph(int num) {
	//public void addParagraphCSS(String name) {
	//public void removeParagraphCSS(String name) {
	//public void nextParagraph() {
	

	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneDialogueObjectState getObjectsCurrentState();
	@Override
	public SceneDialogueObjectState getTempState();
	@Override
	public SceneDialogueObjectState getInitialState();


	

	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneDialogueObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);


	
	

	






	
}
