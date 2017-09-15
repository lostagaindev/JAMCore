package com.lostagain.Jam;

import com.lostagain.Jam.Interfaces.hasVisualRepresentation;

public abstract class JamAnswerBox implements hasVisualRepresentation{

	/**
	 * should enable/disable if the user can type answers into it
	 * @param b
	 */
	public abstract void setEnabled(boolean b);

	/**
	 * gets the current text in the box
	 */
	public abstract String  getText();

	public abstract void setText(String text);
	

	public abstract void setFocus(boolean b);
	
	

}
