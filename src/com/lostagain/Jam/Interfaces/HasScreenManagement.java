package com.lostagain.Jam.Interfaces;

import com.lostagain.Jam.PageLoadingData;
import com.lostagain.Jam.SceneObjects.SceneObject;

/**
 * Manages placement of objects on the game screen, when they are not attached to a scene
 * (ie, "floating" things)
 * @author darkflame
 *
 */
public interface HasScreenManagement {
	

	public static int INITIAL_MAX_ZINDEX = 20000; //default to ensure we are above all scene elements
	
	
	/**
	 * Positions  an object at a point specified by a text tag.
	 * In GWT implementations this would simply be implemented by RootPanel.get(atThisTag)
	 * 
	 * @param placeThis - the object to place, in gwt implementations this is guaranteed to be a Widget
	 * @param atThisTag - in web implementations the tag should be the Element ID on the page.
	 */
	public void PositionByTag(Object placeThis, String atThisTag);

	public void PositionByTag(SceneObject placeThis, String atThisTag);

	/**
	 * returns true if the tag is present
	 */
	boolean hasTag(String hasThisTag);
	
	/**
	 * positions a object by coordinates.
	 * 
	 * @param placeThis - the object to place, in gwt implementations this is guaranteed to be a Widget
	 * @param x
	 * @param y
	 * @param z - in 2d systems you could consider this the zindex (if overhead) or a secondary y displacement if 3/4 style or isometrix
	 */
	public void PositionByCoOrdinates(Object placeThis, int x, int y, int z);


	/**
	 * sets the background image for the game.
	 * (NOTE: not scene background, but rather the backing to the whole screen, which might not be
	 * seen at all depending on what type of interface is used)
	 * 
	 * for gwt you can set this by 
				RootPanel.getBodyElement().setAttribute("background", ""); 
				
	 * @param imageLoc
	 */
	public void setBackgroundImage(String imageLoc);

	/**
	 * Returns a the current background image (ie, the same imageLoc that was used to set it)
	 * @return
	 */
	public String getBackgroundImage();
	
	/**
	 * triggered when a popup panel is opened.
	 * This method doesn't have to do anything, but you could use it to trigger some onscreen change when this happens
	 * eg. Fading out the background, pausing the game, or (as the cuypers code does) animate a little
	 * box that lets you close all the open popups at once.
	 * @param newInventoryFrame
	 */
	public void PopUpWasOpened(IsPopupPanel newInventoryFrame);


	void CloseAllOpenPopUps();
	

	
	
	public void openControlPanel();

	public void closeControlPanel();
	
	public boolean controllPanelIsOpen();



	/**
	 * should also trigger the save string to generate
	 */
	public void openSavePanel();


	
	public void setControlPanelPosition(int x, int y);


	public void toggleLoadGamePopUp();
	

	public void setNewPage(PageLoadingData pageLoadData);
	
	
	
}
