package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.Iterator;

public abstract class JamClueControll {

	protected AssArray Clues = new AssArray();

	/**
	 * sets the size of a visual representation of these clues
	 * @param w
	 * @param h
	 */
	public abstract void setSize(String w, String h);

	public abstract void clear();

	public void AddClue(String Clue,String ClueName,String ForChapter) {
		AddClueImplementation( Clue,  ClueName,  ForChapter);		
	}
	
	public abstract void AddClueImplementation (String Clue,String ClueName,String ForChapter);

	public String ClueArrayAsString() {
		
		String ClueArrayasString ="";
		
		ArrayList<String> ClueChapters = Clues.getAllUniqueNames();
		ClueArrayasString = "";
		
		for (Iterator<String>ClueChaptersit = ClueChapters.iterator(); ClueChaptersit.hasNext(); ) {
			
			String currentChapter= ClueChaptersit.next(); 
			
			//loop for each clue in chapter
		//	int i=0;
	     	ArrayList<String> CluesForChapter = Clues.GetAllItems(currentChapter);
			for (Iterator<String>CluesForChapterit = CluesForChapter.iterator(); CluesForChapterit.hasNext(); ) {
					
				String cluetoadd = CluesForChapterit.next();
			    String ClueName = cluetoadd.split(":",2)[0].trim();
			    String ClueContents =cluetoadd.split(":",2)[1].trim();
			    	
				ClueArrayasString = ClueArrayasString +currentChapter+","+ClueName+","+ClueContents +"|";	
	
			}
			
			
		}
		
		
		return ClueArrayasString;				
	}

	/** loads from a string seperated by ,  and | **/
	public void LoadFromString(String ArrayAsString) {
		
		
	//divid into pairs
	String[] Pairs = ArrayAsString.trim().split("\\|");
	
	
	int i=0;
	//MyApplication.DebugWindow.setText("Adding clues:"+ArrayAsString);
	
	while (i<(Pairs.length)){
		//MyApplication.DebugWindow.addText("_*"+i+"*_");
		
		if (Pairs[i].contains(",")){
		//	MyApplication.DebugWindow.addText("__Adding:"+Pairs[i]);
			
			String Chapter = (Pairs[i].split(","))[0];
			String ClueName = (Pairs[i].split(","))[1];			
			String ClueText = (Pairs[i].split(","))[2];
			
			// note: This actualy wastes cpu, as it refreshs the object after each element
			// if time, fix this
			AddClue(ClueText,ClueName,Chapter);
		}
		
		
		
		i++;
	}
		
		
		
	
		return;				
	}
	

}
