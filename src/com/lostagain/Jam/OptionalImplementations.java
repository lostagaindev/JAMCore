package com.lostagain.Jam;

import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.lostagain.Jam.GwtLegacySupport.secretsPanelCore;
import com.lostagain.Jam.InstructionProcessing.HasPageElementStyleCommands;
import com.lostagain.Jam.Interfaces.HasImagePreloading;
import com.lostagain.Jam.Interfaces.HasLoadMonitor;
import com.lostagain.Jam.Interfaces.HasMouseCursorChangeImplementation;
import com.lostagain.Jam.Interfaces.JamCuypersCodeSpecificFunctions;
import com.lostagain.Jam.SaveMangement.HasBrowserStorageMethod;
import com.lostagain.Jam.SaveMangement.HasCompressionSystem;
import com.lostagain.Jam.SaveMangement.HasEmailStorageMethod;
import com.lostagain.Jam.SaveMangement.HasServerStorageMethod;
import com.lostagain.Jam.SaveMangement.HasTextStorageMethod;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.audio.JamAudioController;
import com.lostagain.Jam.audio.JamAudioController.AudioType;
import com.lostagain.Jam.audio.JamAudioController.DefaultSound;

public class OptionalImplementations {

	static Logger Log = Logger.getLogger("JamCore.OptionalImplementations");
	
	//Functions to set the various visual implementations
	static Optional<HasMouseCursorChangeImplementation> mouseCursorImplementation       = Optional.absent();
	static Optional<WebGameFunctions> WebGameFunctionsImplementation   					= Optional.absent();
	static Optional<JamCuypersCodeSpecificFunctions> CuypersCodeFunctionsImplementation = Optional.absent();
	public static Optional<secretsPanelCore> JamSecretsPanel                                   = Optional.absent();
	static Optional<JamAudioController> JamAudioControllerImplementation                = Optional.absent();
	
	//Functions to set various save/load game storage options
	public static Optional<HasCompressionSystem> StringCompressionMethod                = Optional.absent(); 
	public static Optional<HasServerStorageMethod> ServerStorageImplementation          = Optional.absent();	
	public static Optional<HasBrowserStorageMethod> BrowserStorageImplementation        = Optional.absent();
	public static Optional<HasTextStorageMethod> TextStorageImplementation              = Optional.absent();
	public static Optional<HasEmailStorageMethod> EmailStorageImplementation            = Optional.absent();
	
	//Loading
	public static Optional<HasLoadMonitor>        gamesLoadMonitor                      = Optional.absent();
		
	//preloading
	public static Optional<HasImagePreloading> ImagePreloader                           = Optional.absent();
	

	/**
	 * optional implementation of html commands
	 * use isPresent() to check if these commands are present before use.
	 *  
	 *  Primarily the purpose of this is to allow the separation of the GWT/Core engine components while
	 *   allowing html-specific functions like styling
	 *  <br>
	 *  NOTE: Supplying these functions will also set  SceneObjectState.IS_A_HTML_GAME to true.<br>
	 *  This ensures all SceneObjects know they are really DIVS, and thus abled to be styled<br>
	 *  
	 */
	public static Optional<HasPageElementStyleCommands> PageStyleCommandImplemention = Optional.absent();
	
	/**
	 * Sets the class that handles old CuypersCode game functions, if any.
	 */
	public static void SetCompressionSystemFunctions(HasCompressionSystem impl)
	{
		StringCompressionMethod =  Optional.of(impl);		 
	}
	
	/**
	 * Sets the class that handles old CuypersCode game functions, if any.
	 **/
	public static void SetHasLoadMonitor(HasLoadMonitor impl)
	{
		gamesLoadMonitor =  Optional.of(impl);		 
	}
	
	
	
	
	/**
	 * Sets the class that handles old CuypersCode game functions, if any.
	 **/
	public static void SetImagePreloadFunctions(HasImagePreloading impl)
	{
		ImagePreloader =  Optional.of(impl);		 
	}
	
	
	/**
	 * Sets the class that handles old CuypersCode game functions, if any.
	 */
	public static void SetCuypersCodeSpecificFunctions(JamCuypersCodeSpecificFunctions impl)
	{
		CuypersCodeFunctionsImplementation =  Optional.of(impl);		 
	}
	
	/**
	 * Sets the class that handles old CuypersCode game functions, if any.
	 */
	public static void SetJamSecretsPanel(secretsPanelCore impl)
	{
		JamSecretsPanel =  Optional.of(impl);		 
	}
	
	//
	/**
	 * Sets the class that handles mouse visuals, if any.
	 * This should handle changing the mouse image as well as providing a default one
	 */
	public static void SetWebGameFunctionality(WebGameFunctions impl)
	{
		WebGameFunctionsImplementation =  Optional.of(impl);		 
	}
	/**
	 * Sets the class that handles mouse visuals, if any.
	 * This should handle changing the mouse image as well as providing a default one
	 */
	public static void SetAudioControllerFunctions(JamAudioController impl)
	{
		JamAudioControllerImplementation =  Optional.of(impl);		 
	}
	public static void openWebpage(String url, String target, String settings) {
		if (WebGameFunctionsImplementation.isPresent()){
			
			WebGameFunctionsImplementation.get().openWebpage(url,  target,  settings);
		}
	}
	public static void sendEmailTemplate(String currentProperty) {
	if (WebGameFunctionsImplementation.isPresent()){
			
			WebGameFunctionsImplementation.get().sendEmailTemplate(currentProperty);
		}
	}
	public static void addMusicTrack(String trackname, boolean autoPlay, int Volume, int fadeOver ) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().addMusicTrack(trackname, autoPlay, Volume, fadeOver);
			
		}
	}
	public static void playMusicTrack(String currentProperty) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().playMusicTrack(currentProperty);
		}
	}
	public static void playAudioTrack(String ThisTrackName,int targetVolume,boolean fadeInAndOut,AudioType audiotype,int FadeOver) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().playAudioTrack(ThisTrackName, targetVolume, fadeInAndOut, audiotype, FadeOver);
		}
	}
	public static void playDefaultSound(DefaultSound type) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().playDefaultSound(type);
		}
	}
	public static void stopAllSoundEffects() {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().stopAllSoundEffects();
		}
	}
	public static void setDefaultSoundTrack(String soundID, String trackName) {
		if (JamAudioControllerImplementation.isPresent()){			
			//JamAudioControllerImplementation.get();
			JamAudioController.setDefaultSoundTrack( soundID,  trackName);
		}
	}
	public static void cacheAudio(String trackname,boolean asMusic) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().cacheAudio( trackname,  asMusic);
		}
	}
	public static void setCurrentMusicVolume(int vol) {
		if (JamAudioControllerImplementation.isPresent()){			
			JamAudioControllerImplementation.get().setCurrentMusicVolume(vol);
		}
	}
	//specific cuypers code stuff (not used for other games)
	public static void Cuypers_StatueHeadUrl(String iconloc, int iconframes){
	if (CuypersCodeFunctionsImplementation.isPresent()){
			
		CuypersCodeFunctionsImplementation.get().setStatueHeadUrl(iconloc, iconframes);
		
		}
	}
	public static void Cuypers_SetSoldierURL(String iconloc, int iconframes) {
		if (CuypersCodeFunctionsImplementation.isPresent()){
			
		CuypersCodeFunctionsImplementation.get().setSoldierURL(iconloc, iconframes);
		}
	}
	public static void Cuypers_ClearScrets() {
		if (CuypersCodeFunctionsImplementation.isPresent()){			
			CuypersCodeFunctionsImplementation.get().clearSecrets();
		}
	}
	//currently cuypers specific but we could make this general?
	public static void Cuypers_AddScret(String secretname, String secreturl) {
		if (CuypersCodeFunctionsImplementation.isPresent()){			
			CuypersCodeFunctionsImplementation.get().addSecret( secretname, secreturl);
		}	
	}
	//
	/**
	 * Sets the class that handles mouse visuals, if any.
	 * This should handle changing the mouse image as well as providing a default one
	 */
	public static void SetMouseVisualImplementation(HasMouseCursorChangeImplementation mouseImplementation)
	{
		OptionalImplementations.mouseCursorImplementation =  Optional.of(mouseImplementation);		 
	}
	//functions that call the various visual implementations
	public static void setMouseCursorToDefault() {
		if (mouseCursorImplementation.isPresent()){
			mouseCursorImplementation.get().setMouseCursorToDefault();
		}
	}
	public static void setMouseCursorTo(IsInventoryItem holdThis) {
		if (mouseCursorImplementation.isPresent()){
			mouseCursorImplementation.get().setMouseCursorToHolding(holdThis);
		}	else {
			Log.info("no mouse cursor implementation supplied");
			
		}
	}
	public static void setMouseCursorToHoldingOver(IsInventoryItem itemBeingHeldOrDragged,
			IsInventoryItem overThis) {
		
		if (mouseCursorImplementation.isPresent()){
			mouseCursorImplementation.get().setMouseCursorToHoldingOver(itemBeingHeldOrDragged,overThis);
		} else {
			Log.info("no mouse cursor implementation supplied");
			
		}
		
	}
	public static void setMouseFromImage(String Imagelocation) {
		if (mouseCursorImplementation.isPresent()){
			mouseCursorImplementation.get().setMouseFromImage(Imagelocation);
		}		
		
		
	}
	
	/**
	 * Call this function to set the class that manages the html specific commands
	 * @param pageStyleCommandImplemention
	 */
	public static void setPageStyleCommandImplemention(HasPageElementStyleCommands pageStyleCommandImplemention) {
		PageStyleCommandImplemention = Optional.of(pageStyleCommandImplemention);
		
		SceneObjectState.THIS_IS_A_HTML_GAME = true;
		
	}
	
	
	
	//save system optional ...err..options
	
	public static void setServerStorageImplementation(HasServerStorageMethod serverStorageImplementation) {
		ServerStorageImplementation =  Optional.of(serverStorageImplementation);
	}
	public static void setBrowserStorageImplementation(HasBrowserStorageMethod browserStorageImplementation) {
		BrowserStorageImplementation =  Optional.of(browserStorageImplementation);
	}
	public static void setEmailStorageImplementation(HasEmailStorageMethod emailStorageImplementation) {
		EmailStorageImplementation =  Optional.of(emailStorageImplementation);
	}
	
	public static void setTextStorageImplementation(HasTextStorageMethod textStorageImplementation) {
		TextStorageImplementation =  Optional.of(textStorageImplementation);
	}

	
	
	///-----------------------
	
	

}
