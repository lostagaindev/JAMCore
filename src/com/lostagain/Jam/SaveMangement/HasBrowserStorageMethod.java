package com.lostagain.Jam.SaveMangement;

public interface HasBrowserStorageMethod {

	/**
	 * If savename  is empty, the name should be prompted for
	 * If overwrite is false, a prompt should appear if the name is the same
	 * Else the save should be done silently without bothering the user
	 * 
	 * @param savedata
	 * @param saveName
	 */
	public void saveGameToBrowserImpl(String saveName, String savedata, boolean overWriteIfNameAlreadyPresent);
	

}
