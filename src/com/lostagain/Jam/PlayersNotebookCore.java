package com.lostagain.Jam;

import java.util.ArrayList;

import com.lostagain.Jam.Interfaces.hasInventoryButtonFunctionality;

/**
 * The notebook is a collection of pages that can be added to as the game progress's.
 * In the cuypers code this was used to keep profiles of people
 * In other games it might be used for other reference notes the player needs as they go along
 * (Note; the player cant write to the book themselves, it automatically filled in)
 * 
 * @author darkflame
 *
 */
public abstract class PlayersNotebookCore {
	
	public hasInventoryButtonFunctionality NotepadButton;
	public boolean NotepadOpen = false;
	public final ArrayList<String> LoadedPages = new ArrayList<String>();
	
	
	public abstract void AddPage(String currentProperty, String substring);


	public abstract void clearPagesVisualImplementation();
	
	public void clearPages(){
		LoadedPages.clear();
				
		clearPagesVisualImplementation();
	}

}
