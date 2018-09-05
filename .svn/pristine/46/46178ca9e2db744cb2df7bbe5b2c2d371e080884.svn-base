package com.lostagain.Jam.audio;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Logger;

import com.lostagain.Jam.OptionalImplementations;

public abstract class MusicBoxCore {

	static Logger Log = Logger.getLogger("JAM.MusicBox");
	public static final ArrayList<String> musicTracks = new ArrayList<String>();
	/**
	 * String that represents no music being played
	 */
	public static final String NO_MUSIC = " -NONE- ";
	public static final HashSet<MusicBoxCore> allMusicBoxs = new HashSet<MusicBoxCore>();
	public static int currentMaxVolume = 100;
	public static int currentTrack = 0;
	
	/**
	 * Has the user ever set the music?
	 */
	protected static boolean hasBeenSetByUser = false;

	public void nextTrack() {
		//if theres a next track;
		if ((currentTrack+1)<(musicTracks.size())){
			currentTrack=currentTrack+1;
			//CurrentMusicTrackLabel.setItemSelected(currentTrack,true);
			//play			
			playtrack(currentTrack,currentMaxVolume);
		}		
	}

	public void prevTrack() {
		//if theres a next track;
		if (currentTrack>0){			
			currentTrack=currentTrack-1;
	
			//CurrentMusicTrackLabel.setItemSelected(currentTrack,true);
			//play	
			playtrack(currentTrack,currentMaxVolume);
		}			
	
	
	}

	public static void playtrack(int ThisTrackNum, int Volume) {
		playtrack( ThisTrackNum, Volume, -1);
		
	}

	

	public static void playtrack(int ThisTrackNum, int Volume, int fadeOver) {
	
		if (ThisTrackNum==0){
			//GwtAudioController.playtrack("");
			OptionalImplementations.playMusicTrack("");
			
		}
		
		String ThisTrackName = 	musicTracks.get(ThisTrackNum);
		Log.info(ThisTrackNum+" Is Track "+ThisTrackName);
		//GwtAudioController.playAudioTrack(ThisTrackName,Volume,true,JamAudioController.AudioType.Music,fadeOver);
		
		OptionalImplementations.playAudioTrack(ThisTrackName,Volume,true,JamAudioController.AudioType.Music,fadeOver);
		
	}

	/**
	 * Has the user ever effected this box manually? 
	 * @return
	 */
	public static boolean musicHasBeenSelectedOnce() {
		if (hasBeenSetByUser){
			return true;
		}
		return false;
	}

	/**
	 * sets the currently playing label on all music boxes in the game
	 * @param ThisTrackName
	 */
	public static void setAllMusicTrackPlayingLabelTo(int ThisTrackNum) {
		
		currentTrack = ThisTrackNum;
		
		for (MusicBoxCore musicBoxinstance : allMusicBoxs) {    
			
			musicBoxinstance.setMusicBoxVisualForItemSelected(ThisTrackNum);
			
		}
		
	}

	public abstract void setMusicBoxVisualForItemSelected(int thisTrackNum);
	
	
}
