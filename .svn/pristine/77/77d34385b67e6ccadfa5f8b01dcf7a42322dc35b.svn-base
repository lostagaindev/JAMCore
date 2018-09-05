package com.lostagain.Jam.Interfaces;


/**
 * A button you intend to open a inventory should implement this.
 * In order for some functions to work, specifically chucking objects into the inventory, we must have functions
 * to get the screen relative x/y of the button as well as optional implementations to controll animation
 * 
 * @author darkflame
 *
 */
public interface hasInventoryButtonFunctionality {

	
	/**
	 * Sets the button to animate backwards to its start state
	 * Think of this as a box closing
	 */
	public void setPlayBack();
	
	/**
	 * Sets the button to animate to its end state
	 * Think of this as a box opening
	 */
	public void setPlayForward();
	
	/**
	 * Sets the button to animate forwards then backwards.
	 * Think of this as a box opening and then closing straight away
	 * (used to indicate a item being put into this inventory)
	 */
	public void setPlayForwardThenBack();

	/** must return the total number of frames in this buttons animation
	 * frametotal
	 * @return
	 */
	public int getFrameTotal();

	/**
	 * Whatever is needed to remove this button from the interface.
	 * Should be completely removed, not merely hidden
	 */
	public void removeFromInterface();


	/**
	 * should return the absolute left co-ordinate in screen space
	 * @return
	 */
	public int getAbsoluteLeft();


	
	/**
	 * Should return the absolute top co-ordinate in screen space
	 * @return
	 */
	public int getAbsoluteTop();

	

	/**
	 * The runnable should run when the button is clicked
	 * @param onClick
	 */
	public void addClickRunnable(Runnable onClick);


	/**
	 * The runnable should run when the mouse is over it
	 * (this is used a animation on the inventory button)
	 * 
	 * @param mouseOverHandler
	 */
	public void addMouseOverRunnable(Runnable mouseOverHandler);

/**
 * The runnable should run when the mouse stops being over the button
	 * (this is used a animation on the inventory button)
	 * 
 * @param mouseOutHandler
 */
	public void addMouseOutRunnable(Runnable mouseOutHandler);

	/**
	 * This function should change the icon.
	 * For example, maybe the inventory gets progressively more filled up over the course of the game
	 * Or maybe the style of the icon changes to reflect a different characters back-pack?
	 * 
	 * @param location
	 * @param iconsframes
	 */
	public void setInventoryIconTo(String location, int iconsframes);

	/**
	 * hide or show the button
	 * @param b
	 */
	public void setVisible(boolean b);
	
	/**
	 *is the button visible/
	 * @param b
	 */
	public boolean isVisible();
	

	
	


	

}
