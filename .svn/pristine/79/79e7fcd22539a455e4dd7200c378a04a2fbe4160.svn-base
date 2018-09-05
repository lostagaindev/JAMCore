package com.lostagain.Jam.Interfaces;

import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;

/**
 * Manages the visual changes of the mouse, as well as providing a default look
 * 
 * @author darkflame
 *
 */
public interface HasMouseCursorChangeImplementation {
	
	/**
	 * should set the mouse cursor to the default for the current game
	 */
	public void setMouseCursorToDefault();

	
	public void setMouseCursorToHolding(IsInventoryItem holdThis);
	
	/**
	 * If the mouse is currently holding one thing over another thing. (ie, about to mix too inventory icons)
	 * 
	 * @param holdThis
	 * @param overThis
	 */
	public void setMouseCursorToHoldingOver(IsInventoryItem holdThis, IsInventoryItem overThis);


	
	public void setMouseFromImage(String imagelocation);
	
	
}
