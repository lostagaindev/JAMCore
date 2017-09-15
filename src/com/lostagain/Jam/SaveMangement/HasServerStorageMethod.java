package com.lostagain.Jam.SaveMangement;

/**
 * manages saving and loading of games to a server
 * as well as associated logins and logouts of that server
 * 
 * @author darkflame
 *
 */
public interface HasServerStorageMethod {

	/**
	 * load the users save game from the server.
	 * If theres more then one we load the last one saved or whatever is considered the "primary" save
	 * 
	 */
	public void loadSaveGameFromSever();
	
	public void saveGameToServerImpl(String savedata);
	
	
	public void openLoginBox();
	
	public void closeLoginBox();
	
	
}
