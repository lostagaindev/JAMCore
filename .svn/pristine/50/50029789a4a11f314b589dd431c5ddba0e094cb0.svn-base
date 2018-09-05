package com.lostagain.Jam.audio;

import java.util.logging.Logger;

import com.lostagain.Jam.RequiredImplementations;


public abstract class JamAudioController {

	/**
	 * default sounds in the game
	 * @author Tom
	 *
	 */
	public enum DefaultSound {		
		KeyBeep,SpaceBeep, MessageKeyBeep, MessageSpaceBeep		
	}
	public enum AudioType {
		Music,SoundEffect;
	}
	static Logger Log = Logger.getLogger("JAM.JamAudioController");
	

	
	
	protected static final String DEFAULT_KEY_BEEP      = "CC2keybeep.mp3";
	protected static final String DEFAULT_SPACEKEY_BEEP = "CC2spacebeep.mp3";
	
	/** these are the generic sounds that play on dialogue boxs typing when no sound has been set **/
	protected static String KEY_BEEP = DEFAULT_KEY_BEEP;
	/** these are the generic sounds that play on dialogue boxs typing when no sound has been set **/
	protected static String SPACEKEY_BEEP = DEFAULT_SPACEKEY_BEEP;	
	/** these are the generic sounds that play on dialogue boxs typing when no sound has been set **/
	protected static String MESSAGE_KEY_BEEP = DEFAULT_KEY_BEEP;
	/** these are the generic sounds that play on dialogue boxs typing when no sound has been set **/
	protected static String MESSAGE_SPACEKEY_BEEP = DEFAULT_SPACEKEY_BEEP;

	
	
	
	public static void setDefaultSoundTrack(String soundID, String trackName) {
		
		DefaultSound soundtype = DefaultSound.valueOf(soundID);
		
		switch (soundtype) {
		case KeyBeep:
			KEY_BEEP = trackName;
			break;
		case SpaceBeep:
			SPACEKEY_BEEP = trackName;
			break;		
		case MessageKeyBeep:
			
			MESSAGE_KEY_BEEP = trackName;
			//message settings get applied straight to the Jams main message label, as there's only one of them anyway
			//JAM.Feedback.setCUSTOM_KEY_BEEP(MESSAGE_KEY_BEEP);
			
			RequiredImplementations.setFeedbackKeyBeep(MESSAGE_KEY_BEEP);
			
			
			break;
		case MessageSpaceBeep:
			MESSAGE_SPACEKEY_BEEP = trackName;
			//message settings get applied straight to the Jams main message label, as there's only one of them anyway
		//	JAM.Feedback.setCUSTOM_SPACEKEY_BEEP(MESSAGE_SPACEKEY_BEEP);
			
			RequiredImplementations.setFeedbackSpaceKeyBeep(MESSAGE_KEY_BEEP);
			
			
			break;	
			
		}
		
		
		
	}


	//
	//things to implement
	//
	protected  abstract void playtrack_implementation(String ThisTrackName);


	//Eventually replace this with just the normal cache list?
	protected  abstract void stopAllSoundEffects_implementation();

	protected  abstract void playAudioTrack_implementation(String ThisTrackName,int targetVolume,boolean fadeInAndOut, AudioType audiotype, int FadeOver);    	
	protected  abstract void addMusicTrack_implementation(String Track, boolean autoPlay, int Volume,int fadeOver);
	public abstract void cacheAudio(String trackname,boolean asMusic);
	
	public abstract void  setCurrentMusicVolume(int Vol);
	
	public void playAudioTrack(String ThisTrackName,int targetVolume,boolean fadeInAndOut, AudioType audiotype, int FadeOver){    	
 
			playAudioTrack_implementation(ThisTrackName, targetVolume, fadeInAndOut,  audiotype,  FadeOver);    	

	}
		
		
		
	public void playMusicTrack(String currentProperty) {
		playtrack_implementation(currentProperty);
	}
	

	public void stopAllSoundEffects() {
		stopAllSoundEffects_implementation();
	}


	public void addMusicTrack(String Track, boolean autoPlay, int Volume,int fadeOver) {
		addMusicTrack_implementation( Track,  autoPlay,  Volume, fadeOver);
	}


	/**
	 * Plays a default interface sound
	 * @param type
	 */
	public void playDefaultSound(DefaultSound type) {		
		switch (type) {
		case KeyBeep:			
			playAudioTrack(KEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );			
			break;
		case SpaceBeep:
			playAudioTrack(SPACEKEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
			break;
		case MessageKeyBeep:
			playAudioTrack(MESSAGE_KEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
			break;
		case MessageSpaceBeep:
			playAudioTrack(MESSAGE_SPACEKEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
			break;
		}		
	}

	
}
