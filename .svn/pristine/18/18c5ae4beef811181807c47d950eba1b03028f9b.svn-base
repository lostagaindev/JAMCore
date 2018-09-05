package com.lostagain.Jam.Scene;

import java.util.ArrayList;
import java.util.HashMap;

import com.lostagain.Jam.Interfaces.hasVisualRepresentation;
import com.lostagain.Jam.SceneObjects.Interfaces.hasUserActions;

public abstract class TextOptionFlowPanelCore implements hasVisualRepresentation {

/**
 *  originally we were going to put the GlobalTextOptionCache here. 
 *  However, it required a GWT textoption implementation. 
 *  When developing for the libGDX implementation, you will also have to provide a libGDX Textoptioncache.
 *  
 *  This is the reference:
 *	HashMap<hasUserActions, HashMap<String,TextOption>> GlobalTextOptionCache  =   new HashMap;
 */
	
	/*** 
	 * We keep track of all the TextOptionFlowPanels here.
	 * They are given name to allow them to be referred to later (which is the String key here) 
	 ***/
	
	public static HashMap<String,TextOptionFlowPanelCore> globalTextOptionsList = new HashMap<String,TextOptionFlowPanelCore>();
	/** The source that triggered this TextOptionFlowPanel to appear **/
	protected hasUserActions toRememberSource;
	
	/**
	 * 
	 * @param topicsToAdd
	 * @param object
	 * @param optionsTabName
	 */
	public abstract void addOptions(ArrayList<String> topicsToAdd, hasUserActions object, String optionsTabName);


	public abstract void  setSize(String w, String h);

	
}
