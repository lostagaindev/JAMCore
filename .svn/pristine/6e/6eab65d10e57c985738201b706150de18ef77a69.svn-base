package com.lostagain.Jam.Interfaces;

import com.lostagain.Jam.Interfaces.PopupTypes.IsPopupContents;

/**
 * A panel that should be able to popup above everything else, be dragged around, and contain IsPopupContents
 * 
 * Remember if the panel is closed CloseDefault should fire.
 * Also, if CloseDefault is run the panel should close.
 * (Careful when implementing this so you don't get into a loop)
 * 
 * @author darkflame
 *
 */
public interface IsPopupPanel extends hasCloseDefault,hasOpenDefault {


	 /**
	  * 
	  * CLONE OF GWT PositionCallback.
	  * You Should mirror behavior if used.
	  * 
	   * A callback that is used to set the position of a {@link PopupPanel} right
	   * before it is shown.
	   */
	  public interface JamPositionCallback {

	    /**
	     * 
	  * CLONE OF GWT PositionCallback.
	  * You Should mirror behavior if used.
	  * 
	     * Provides the opportunity to set the position of the PopupPanel right
	     * before the PopupPanel is shown. The offsetWidth and offsetHeight values
	     * of the PopupPanel are made available to allow for positioning based on
	     * its size.
	     *
	     * @param offsetWidth the offsetWidth of the PopupPanel
	     * @param offsetHeight the offsetHeight of the PopupPanel
	     * @see PopupPanel#setPopupPositionAndShow(PositionCallback)
	     */
	    void setPosition(int offsetWidth, int offsetHeight);
	  }
	
	
	boolean isShowing();

	/**
	 * sets the popup to go above others
	 */
	void setZIndexTop();

	void setPopupPosition(int i, int j);

	
	void add(IsPopupContents popupMenuBar);

	/**
	 * When set to true, all mouse and keyboard events that dont target the popup should be ignored
	 * (should clone GWTs setModal behavour)
	 * @param b
	 * 
	 */
	void setModal(boolean b);
	
	/**
	 * Enable or disable the autoHide feature
	 *  When enabled, the popup will be automatically hidden when the user clicks outside of it.
	 * @param b
	 * 
	 */
	void setAutoHideEnabled(boolean b);

	
	/**
	 * 
	 * @param callback
	 */
	void setPopupPositionAndShow(JamPositionCallback callback);

	/**
	 * Optional implementation to set the style of the popup.
	 * In gwt implementations this would be the css
	 * @param string
	 */
	void setStyleName(String string);

	/**
	 * if the popup should close, this should be run
	 * @param runnable
	 */
	void addRunOnClose(Runnable runnable);

	void setCaptionText(String mainGame_Inventory);
	
	

	
	
	

	
	//= new PopUpWithShadow(
	//null,
	//"50%", "25%", CurrentProperty + " " + GamesInterfaceText.MainGame_Inventory, 
	//newinventory );
	
}
