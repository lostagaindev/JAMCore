package com.lostagain.Jam.SceneObjects.Interfaces;


/**
 * handles visual elements of the interface.
 * Most importantly is used to send messages to a feedback box
 * 
 * @author darkflame
 *
 */
public interface IsInterfaceVisualHandler {

	public void setHeldItemVisualiserVisible(boolean b);

	/**
	 * sets the visualization of whats being held to a particular inventory icon
	 * That is, this method will fire when that item is held
	 * @param holdThis
	 */
	public void setHeldItemVisualisation(IsInventoryItem holdThis);
	
	/**
	 * sets the current text of the feedback box
	 * REQUIRED
	 * @param text
	 * @param withoutSound - overrides any normal sounds (ie, a normal typing sound)
	 */
	public void setCurrentFeedbackText(String text,boolean withoutSound);

	public void setCurrentFeedbackText(String text);

	public void setCurrentFeedbackTextDelay(int delay);

	public void setCurrentFeedbackTextSpeed(int parseInt);

	/**
	 * sets a runnable that should fire after typing into the feedback box
	 * REQUIRED
	 * @param runnable
	 */
	public void setCurrentFeedbackRunAfter(Runnable runnable);

	
	public void setFeedbackKeyBeep(String soundName);
	public void setFeedbackSpaceKeyBeep(String soundName);

	String getCurrentFeedbackText();
	
	
	
	
	
}

