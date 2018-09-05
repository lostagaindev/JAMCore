package com.lostagain.Jam.InstructionProcessing;

/**
 * defines a class that manages optional page styling commands.
 * In a HTML implementation these directly use Element IDs and CSS to style page elements - normally interface.
 * 
 * Non-gwt implementations will need to either avoid using these commands, or come up with some equilivents
 * 
 * @author darkflame
 *
 */
public interface HasPageElementStyleCommands {

	/**
	 * Sets a css class on a page (dom) element. 
	 * @param elementsID
	 * @param classestoset
	 * @return true if class was successfully set
	 */
	boolean setCSSClassOnDomElement(String elementsID, String classestoset);
	
	/**
	 * adds a css class on a page (dom) element. 
	 * @param elementsID
	 * @param classestoset
	 * @return true if class was successfully set
	 */
	boolean addCSSClassToDomElement(String elementac, String classac);
	
	/**
	 * removes a css class on a page (dom) element. 
	 * @param elementsID
	 * @param classestoset
	 * @return true if class was successfully set
	 */
	boolean removeCSSClassFromDomElement(String elementsID, String classestoset);
	
	/**
	 * hides a element on the page
	 * @param elementsID
	 * @return true if a element on the page was hidden (other then some built in feedback icons which dont get added to the list for some reason - not sure if correct) 
	 */
	boolean hideelement(String elementsID);
	
	/**
	 * shows a element on the page
	 * @param elementsID
	 * @return true if a element on the page was shown (other then some built in feedback icons which dont get added to the list for some reason - not sure if correct) 
	 */
	boolean showelement(String elementsID);
	
	/**
	 * sets a inline style  on  a element on the page
	 * @param elementsID
	 * @param styletoset - the inline style to set
	 * @return true if a element on the page was set
	 */
	boolean setStyleOnElement(String elementsID, String styletoset);
	
	/**
	 * 
	 * @param elementID
	 * @param FadeInOverInMs
	 * @return
	 */
	boolean fadeOutHTMLElement(final String elementID,int FadeInOverInMs);
	
	
	/**
	 * 
	 * @param elementID
	 * @param FadeInOverInMs
	 * @return
	 */
	boolean fadeInHTMLElement(final String elementID,int FadeInOverInMs);
	
	

	boolean testIfDivHasClass(String divWithThisIDToCheck, String classToLookFor);

	/**
	 * sets the background class on the html page
	 * @param currentProperty
	 */
	void setBackgroundClass(String currentProperty);
	

	

}
