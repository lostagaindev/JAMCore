package com.lostagain.Jam;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.CommandParameterSet;

/**
 * Triggers effects that change the look of the whole game screen
 * Effects might be a tempory, or persistent till they are turned off. 
 * 
 * GWT uses css based fades
 * GDX manually fades based on delta time
 * 
 * (which is why we dont controll the fadeing in/out in this core file))
 * 
 * @author darkflame
 *
 */
public abstract class JamGlobalGameEffects {

	public static Logger Log = Logger.getLogger("JAMcore.JamGlobalGameEffects");
	
	//copy of a class that extends this (has to be initialized for any of this to work)
	static Optional<JamGlobalGameEffects> instance;
		
	
	
	
	public enum GameEffectType {	
		/** represents the players interface being 'hacked'. Can be turned on/off **/
		hacked,
		/**
		 * fades a 100% by 100% image over the screen. The image wont interfere with anything and purely provided a visual change
		 */
		fullscreenoverlay
		
	}
	
	//stores if effects are currently on or off (really this is legacy support for hackd)
	HashMap<GameEffectType,Boolean> effectStatus = new HashMap<GameEffectType,Boolean>();
	
	/**
	 * Bundles together the data needed for a effect.
	 * (makes loading/saving effect status easier)
	 * 
	 * @author darkflame
	 *
	 */
	static class GameEffectData {
		GameEffectType type; //required		
		String effectID; //required
		String colour;
		String image;
		
		public GameEffectData(GameEffectType type, String effectID, String colour, String image) {
			super();
			this.type = type;
			this.effectID = effectID;
			this.colour = colour;
			this.image = image;
		}		
	}
	
	 //We use a ConcurrentMap as a fading overlay may remove itself as this list is looped over. (unlikely but possible)
	static ConcurrentMap<String,GameEffectData> CurrentActivateGameEffects = Maps.newConcurrentMap();
	//--
	
	
/**<br>
 * recreates the string needed to create all current effects<br>
 * <br>
 * ie, recreates a line like this for each active overlay;<br>
 *  - setgamemode = fullscreenoverlay,true,hackfade,"0,0,0,0.9",,5000<br>
 */	
	static public String getInstructionsToTriggerCurrentEffects() {
		if (CurrentActivateGameEffects.isEmpty()){
			return "";
		}
		
		String instructions = "";
		
		for (String id : CurrentActivateGameEffects.keySet()) {
			
			GameEffectData data = CurrentActivateGameEffects.get(id);
			
			instructions = instructions + "\n - setgamemode ="+data.type+",true,"+id+",\""+data.colour+"\","+data.image; //no duration it should instantly appear
			
			
		}		
		return instructions;
	}
	
	
	
	
	public static void setJamGlobalGameEffectsFunctions(JamGlobalGameEffects instance) {
		JamGlobalGameEffects.instance = Optional.fromNullable( instance);
	}

	

	/**
	 * 
	 * @param RawScriptParameters
	 */
	public static void setGameEffect(CommandParameterSet ScriptParameters) {	
		
		//effectName,(optional)onOffStatus,(optional)extraParams				
		GameEffectType effecttype = GameEffectType.valueOf(ScriptParameters.get(0).getAsString().toLowerCase());
		
		if (ScriptParameters.getTotal()>1){
			boolean effecton = ScriptParameters.get(1).getAsBoolean();
			if (ScriptParameters.getTotal()>2){
				//String effectExtraData = ScriptParameters.get(2).getAsString();	
				//set effect on/off   with all params
				setGameEffect(effecttype,effecton,ScriptParameters);	
				return;
			}			
			//set effect on/off 
			setGameEffect(effecttype,effecton,null);			
			return;
		}
		
	}

	
	
	public static void setGameEffect(GameEffectType effect) {	
		if (!instance.isPresent()){
			Log.warning("NO JamGlobalGameEffects functions supplied, so no effects will be run. If you want effects give JamGlobalGameEffects the needed functions by calling setJamGlobalGameEffectsFunctions()");
			return;
		}		
		//------------
		//get what it is already and invert
		boolean newState = !(instance.get().effectStatus.get(effect));
		setGameEffect(effect,newState,null);		
	}
	
	/**
	 * 
	 * @param effect
	 * @param effectOn
	 * @param optionalExtraSettings
	 */
	public static void setGameEffect(GameEffectType effect, boolean effectOn, CommandParameterSet optionalExtraSettings) {
		
		if (!instance.isPresent()){
			Log.warning("NO JamGlobalGameEffects functions supplied, so no effects will be run. If you want effects give JamGlobalGameEffects the needed functions by calling setJamGlobalGameEffectsFunctions()");
			return;
		}
				
		switch(effect){
		case hacked:
			if (effectOn){
				turnHackedEffectOnImplementation(optionalExtraSettings);	
			} else {
				turnHackedEffectOffImplementation(optionalExtraSettings);	
			}		
			break;
		case fullscreenoverlay:
			if (effectOn){
				turnScreenOverlayEffectOnImplementation(optionalExtraSettings);	
			} else {
				turnScreenOverlayEffectOffImplementation(optionalExtraSettings);	
			}	
			break;
		default:
			break;		
		}
		
		
	}
	
	


	private static void turnScreenOverlayEffectOffImplementation(CommandParameterSet optionalExtraSettings) {
		
		int duration = 500;

		if (optionalExtraSettings==null || optionalExtraSettings.getTotal()<3 ) {
			Log.severe("A screen overlay effect requires parameters: \n"
					+  "fullscreenoverlay,false,effectsID,[duration] \n"
					+  "either the imageurl or the colour must be supplied");
	
		}
		
			
			//get(0) = tells us its a overlay which we alrady know
			//get(1) = tells us to turn it off, which we know already		
			String effectID = optionalExtraSettings.get(2).getAsString(); //its ID, as there can be many overlays at once
			
			if (optionalExtraSettings.getTotal()>2) {	
				duration= optionalExtraSettings.get(3).getAsInt();
			}
			
			//remove the new effect in our list
			CurrentActivateGameEffects.remove(effectID);
			//
			
		instance.get().turnScreenOverlayEffectOffImplementation_impl(effectID,duration); //no optionalExtraSettings yet
		
	}



	private static void turnScreenOverlayEffectOnImplementation(CommandParameterSet optionalExtraSettings) {
		
		String colour = ""; 
		String image  = "";
		int duration = 500;
		

		if (optionalExtraSettings==null || optionalExtraSettings.getTotal()<3) {
			Log.severe("A screen overlay effect requires parameters: \n"
					+  "fullscreenoverlay,true,effectsID,[csscolor],[imageurl],[duration] \n"
					+  "either the imageurl or the colour must be supplied");
		}
		
		//get(0) = tells us its a overlay which we alrady know
		//get(1) = tells us to turn it on, which we know already		
		String effectID = optionalExtraSettings.get(2).getAsString().trim(); //its ID, as there can be many overlays at once
		
		
		if (optionalExtraSettings.getTotal()>3) {	
			colour = optionalExtraSettings.get(3).getAsString();
			colour = colour.replaceAll("\"", ""); //remove any quotes from colourSpecified 

			if (optionalExtraSettings.getTotal()>4){	
				image = optionalExtraSettings.get(4).getAsString();
				
				if (optionalExtraSettings.getTotal()>5){
					duration= optionalExtraSettings.get(5).getAsInt();
					
					
				}				
			}			
		}
		
		

		Log.warning("turnScreenOverlay: ID:"+effectID+" col:"+colour+" img:"+image+" dur:"+duration);
		
		//store the new effect in our list
		GameEffectData overlayinformation = new GameEffectData(GameEffectType.fullscreenoverlay,effectID,colour,image);
		CurrentActivateGameEffects.put(effectID,overlayinformation);
		//
		
		instance.get().turnScreenOverlayEffectOnImplementation_impl(effectID, colour,image,duration);
	}



	private static void turnHackedEffectOffImplementation(CommandParameterSet optionalExtraSettings) {
		int duration = 1000; //default		
		if (optionalExtraSettings!=null && optionalExtraSettings.getTotal()>2){
			duration = optionalExtraSettings.get(3).getAsInt();
		}
		
		//remove the new effect in our list
		CurrentActivateGameEffects.remove("hackedid");
		//
		
		instance.get().turnHackedEffectOffImplementation_impl(duration);
	}



	private static void turnHackedEffectOnImplementation(CommandParameterSet optionalExtraSettings) {

		int duration = 1000; //default		
		if (optionalExtraSettings!=null && optionalExtraSettings.getTotal()>2){
			duration = optionalExtraSettings.get(2).getAsInt();
		}
		
		//store the new effect in our list
				String effectID = "hackedid";
				GameEffectData hackedinformation = new GameEffectData(GameEffectType.hacked,effectID,"","");
				CurrentActivateGameEffects.put(effectID,hackedinformation);
				//
				
		instance.get().turnHackedEffectOnImplementation_impl(duration);
	}



	public static void TriggerEffect(final String EffectName,final CommandParameterSet parameters,final CommandList InstructionSetToRunAfter){		
		instance.get().TriggerEffectImplementation(EffectName,parameters, InstructionSetToRunAfter);		
	}

	public static void setClockMode(String currentProperty) {
		instance.get().setClockModeImplementation(currentProperty);
		
	}
	
	//things the class that extends this has to implement
	/**
	 * @param optionalExtraSettings 
	 * 
	 */
	public abstract void turnHackedEffectOnImplementation_impl(int duration);
	
	/**
	 * @param optionalExtraSettings 
	 * 
	 */
	public abstract void turnHackedEffectOffImplementation_impl(int duration);
	


	/**
	 * fades a 100% by 100% image over the screen and keeps it there. 
	 * The image wont interfere with anything and purely provided a visual change
	 */
	public abstract void turnScreenOverlayEffectOnImplementation_impl(String effectID, String colour, String image, int durationms);

	/**
	 * fades and removes a 100% by 100% image that was set by turnScreenOverlayEffectOnImplementation. 
	 * The image wont interfere with anything and purely provided a visual change
	 */
	public abstract void turnScreenOverlayEffectOffImplementation_impl(String effectID,int duration);
	


	/**
	 * if the game interface has a clock, this will controll any visual effects on it
	 * For example "fast" to make it speed forward madly. For, you know ,fun 
	 *
	 * @param currentProperty
	 */
	public abstract void setClockModeImplementation(String currentProperty);



	public abstract void triggerClearAllEffects_impl(CommandList instructionSet);

	public abstract void triggerHackedEffect_impl(double duration, CommandList runAfter);

	/**
	 * triggers a fadein.
	 * if another fade is inprogress, it should wait till its finished.
	 * 
	 * @param duration
	 * @param backgroundColor
	 * @param backgroundImage - "none" should clear any existing
	 * @param centerImage
	 * @param RunThisAfter
	 */
	public abstract void triggerFadeInEffect_Impl(double duration, String backgroundColor, String backgroundImage, boolean centerImage,
			CommandList RunThisAfter);

	/**
	 * triggers a fadeout.
	 * if another fade is inprogress, it should wait till its finnished.
	 * 
	 * @param duration
	 * @param backgroundColor
	 * @param backgroundImage  - "none" should clear any existing
	 * @param centerImage
	 * @param runTheseAfter
	 */
	public abstract void triggerFadeOutEffect_impl(double duration, String backgroundColor, String backgroundImage, boolean centerImage,
			CommandList runTheseAfter);

	public abstract void triggerFlashEffect_impl(double duration, String backgroundColor, CommandList runThisAfter);



	/** 
	 * Triggers a fancy screen overlay effect, such as "FLASH" or "HACKED" <br>
	 * These effects might not play well with scenes open, or with high z-indexs<br>
	 * They were designed for text based games like the CuypersCode 
	 ***/
	public void TriggerEffectImplementation(String EffectName, CommandParameterSet optionalOptions, final CommandList InstructionSet) {
	
		EffectName=EffectName.toLowerCase();
		Log.info("-" + EffectName); 	
		
		switch (EffectName) {
		case "flash":
		{			
	
			Log.info("- Flash starting -");
			double duration = 100; //should be more like 10!
			String backgroundColor = "";			
			
			if (optionalOptions.getTotal()>1){						
				if (optionalOptions.getTotal()>2){		
					duration         = optionalOptions.get(1).getAsDouble();
					backgroundColor  = optionalOptions.get(2).getAsString();					
				} else {						
					duration         = optionalOptions.get(1).getAsDouble();	
					backgroundColor = "";					
				}		
			}
	
			triggerFlashEffect_impl(duration, backgroundColor, InstructionSet);
		}
		break;
		case "fadeout":
		{			
	
			//----------------------------			
			double duration = 1000.0;
			String backgroundColor = "";
			String backgroundImage = "";
			boolean centerImage = false;
			
			if (optionalOptions.getTotal()>1){						
				duration         = optionalOptions.get(1).getAsDouble();
				Log.info(" duration: "+duration);
				
				if (optionalOptions.getTotal()>2){
					backgroundColor  = optionalOptions.get(2).getAsString();
					Log.info(" backgroundColor: "+backgroundColor);
					
					if (optionalOptions.getTotal()>3){		
						backgroundImage  = optionalOptions.get(3).getAsString();
						Log.info(" backgroundImage: "+backgroundImage);
						
						if (optionalOptions.getTotal()>4){		
							centerImage  = optionalOptions.get(4).getAsBoolean();
						}						
					}
				}			
			}
			//----------------------------
			Log.info(" fadeout triggering: "+duration+","+backgroundColor+","+backgroundImage+","+centerImage);
			
			// set instructions for after fadeout
			//	int InstructionSetLoc = InstructionSet.indexOf("FadeOut", 0) + 7;
			//	String afterFadeInstructions = InstructionSet.substring(InstructionSetLoc).trim();
			Log.info("instructions after fade out;" + InstructionSet.getCode());
	
			triggerFadeOutEffect_impl(duration, backgroundColor, backgroundImage, centerImage, InstructionSet);
			
		}
		break;
		case "fadein":
		{
	
			
			//----------------------------			
			double duration = 1000;
			String backgroundColor = "";
			String backgroundImage = "";
			boolean centerImage = false;
			if (optionalOptions.getTotal()>1){						
				duration         = optionalOptions.get(1).getAsDouble();
				
				if (optionalOptions.getTotal()>2){
					backgroundColor  = optionalOptions.get(2).getAsString();
					
					if (optionalOptions.getTotal()>3){		
						backgroundImage  = optionalOptions.get(3).getAsString();
						
						if (optionalOptions.getTotal()>4){		
							centerImage  = optionalOptions.get(4).getAsBoolean();
						}
					}
				}			
			}
			Log.info(" fadein triggering: "+duration+","+backgroundColor+","+backgroundImage+","+centerImage);
			
			//------------------------------
			triggerFadeInEffect_Impl(duration, backgroundColor, backgroundImage, centerImage, InstructionSet);
		}
		break;
		case "hacked":
		{		
	
			double duration = 100;
			if (optionalOptions.getTotal()>1){		
				duration         = optionalOptions.get(1).getAsDouble();				
			}
			triggerHackedEffect_impl(duration, InstructionSet);
			
		}
		break;
		case "clear":
		{
	
			triggerClearAllEffects_impl(InstructionSet);
		}
		case "clearallqueuedcommands":
		{
	
			triggerClearAllQueuedCommands_impl(InstructionSet);
		}
		break;
		}
	}



	/**
	 * This should clear all commands currently waiting for effects to finish.
	 * It should, however fire the instructionSet passed to it after clearing the others.
	 * 
	 * @param instructionSet 
	 */
	public abstract void triggerClearAllQueuedCommands_impl(CommandList instructionSet);


}
