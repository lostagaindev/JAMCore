package com.lostagain.Jam.Interfaces;

public interface BasicGameInformation {

	int getCurrentGameStageWidth();
	int getCurrentGameStageHeight();
	
	
	//global game variables (set once at startup)
	/**
	 * Should get the URL prefix of the hosting page, or the "base directory" of the applications folder
	 * 
	 * should end in a slash
	 * @return
	 */
	String getHomedirectory();
	String getGameName();

	/**
	 * This should return the folder name of the ""Game Data Files"" directory (ie, the directory that contains the Game Controll Script folder, Game Scenes, Message text etc.
	 * Essentially this folder represents the 'secure' portion of the game which contains the actual code that makes things work.
	 * In a web-running GWT based version of the game, this should return a empty string, and instead a textfetcher.php should be used by the file manager to fetch these text files in a way so the user can never see the directory
	 * (NOTE: images are not kept in this or its subfolder, they are in another Game Scenes folder in a direct subdirectory of getHomeLocation() )
	 *  
	 * 
	 * should end in a slash
	 * @return
	 */
	String getSecureFolderLocation();
	
	String getLanguageExtension();
	boolean getRequiredLogin();		
	boolean getHasServerSave();
	boolean getDisableAutoSave();
	String getQuality();
	String getDebugSetting();
	
	
	String getAudioLocation();
	

	String getSemanticsLocation();
	/**
	 * Some implemenetations may allow window resizing
	 * If so, whats the smallest you will allow the height to shrink too?
	 * ie. A factor of 2 means you will allow the "pixels" to be half sized, but after that point the window will just pan.
	 * If you dont want any shrinking, specify a factor of 1.
	 * 
	 * NOTE: at no point will this limit the window itself. It merely specifies the point where shrinking the window no longer shrinks the games sprites, and instead just
	 * shrinks the viewable area. <br> (this is the only way GWT implementations work due) <br>
	 * 
	 */
	double getMaximumShrinkFactor();
	
	/**
	 * dumb fix needed for semantics on some servers
	 * @return
	 */
	boolean setToNeverRequestFilesWithPost();
	
	

	

	

	
}
