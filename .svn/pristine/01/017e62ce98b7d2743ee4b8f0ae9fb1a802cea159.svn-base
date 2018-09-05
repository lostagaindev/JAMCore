package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyFunctions;

/** 
 * This class should be used to manage all the text in the game that the player see's 
 * that isnt loaded from *.html or other external files.
 * So, for example, all the text displayed by 
 * - Message =
 * commands in the game script.
 * 
 *   By using a database like this, text can be given a ID string, and the correct message
 *   loaded for the current specified language **/
public class GameTextDatabase {


	static Logger Log = Logger.getLogger("JAMCore.GameTextDatabase");
	
	/*** all the language IDs ares stored here ***/ 
	static ArrayList<String> LanIDs = new ArrayList<String>();
	
	public static ArrayList<String> getLanIDs() {
		return LanIDs;
	}


	static HashMap<String,HashMap<String,String>> textDatabases = new HashMap<String,HashMap<String,String>>();
	
	
	static boolean loaded= false;

	private static Runnable RunOnComplete;
	
	/** Load a new game text database from a CSV file. 
	 * Note the formating of the file is very fussy, especially regarding
	 * the correct number of quotes **/
	static public void loadDatabase(String CSVFileLocation){
		
		//set what to do when we get the text data retrieved
				FileCallbackRunnable onResponse = new FileCallbackRunnable(){

					@Override
					public void run(String databaseFile, int responseCode) {

						//loop and parse
						Log.info("Text Database Recieved");
						//Moved to its own method to help unit tests
						parseDatabaseFile(databaseFile);
					}
		
				};
		
				//what to do if theres an error
				FileCallbackError onError = new FileCallbackError(){

					@Override
					public void run(String errorData, Throwable exception) {

					}
					
				};
				
				
				//using the above, try to get the text!
				
				RequiredImplementations.getFileManager().getText(
						CSVFileLocation,
						true,
						onResponse,
						onError,
						false);

		
		
		
		
	}
	
	
	static public void addTextLineIntoDatabase(String line){
		
		//String linebits[]=line.split(",");

		Log.info("adding line:"+line);
		
		ArrayList<String> linebits= SpiffyFunctions.parseCsvLine(line, ',', '"', '\\', false);
		
		//Log.info("which has "+linebits.size()+" bits");
		
		
		//ensure the line has enough segments for all the lanIDs, else we just skip this line
		if ((linebits.size()-1)<LanIDs.size()){
			return;
		}
		
		
		String TextID = linebits.get(0);
		
		int i=-2;
		
		for (String linebit : linebits) {
			i++;
			//ignore first line as thats the LanID data
			if (i==-1){
				continue;
			}
			
			String LanID = LanIDs.get(i);
			
			addTextIntoDatabase(TextID,LanID,linebit);
			
			
		}
		
		
		
	}
	
	/**
	 * 
	 * @param TextID - the texts ID (ie "Greeting1")
	 * @param LanID  - its language ID (ie "EN")
	 * @param TextContent - the content of this text (ie "Hello!")
	 */
	static public void addTextIntoDatabase (String TextID,String LanID, String TextContent){
		
		//ensure the TextID has a minimum length
		if (TextID.length()<3){
			return;
		}
		
		
		//ensure database exists for this lanID
		if (!textDatabases.containsKey(LanID)){
			
			HashMap<String,String> newTextDatabase = new HashMap<String,String>();
			textDatabases.put(LanID, newTextDatabase);
			
		}
				
		
		//get correct database
		HashMap<String,String> currentDatabase = textDatabases.get(LanID);
		
		//put the new text entry in it
		currentDatabase.put(TextID, TextContent);
		
	}
	


	public static boolean isLoaded() {
		
		
		
		return loaded;
	}


	public static String replaceIDsWithText(String sourceString,			
			String languageExtension) {
		
		//get the database for the specified languageExtension
		HashMap<String,String> currentDatabase = textDatabases.get(languageExtension);
		
		if (currentDatabase==null){
			Log.info("languageExtension:"+languageExtension+" not found");
			
		}
		
		//loop over all IDs
		Log.info("replacing all TextIDs with text for selected language");
		
		for (String textID : currentDatabase.keySet()) {
			
			//swap all entries of this textID for the real text value
			//Log.info("replacing "+textID+" with "+currentDatabase.get(textID));
			
			sourceString = sourceString.replaceAll(textID, currentDatabase.get(textID));
			
			
		}
		
		//Log.info("new text is:"+sourceString);
		
		return sourceString;
	}


	public static void setRunOnCompleteCallback(Runnable remainingSetup) {
		
		RunOnComplete=remainingSetup;
	
		
		
		
	}


	public static void parseDatabaseFile(String databaseFile) {
		String database =databaseFile;

		//Log.info("Text Database:"+database);
						
		
		
		//split and loop
		
				
		String[] lines = database.split("\r?\n|\r");
						
		//first row gives all the IDs
		String[] firstrowselements = lines[0].split(",");
		Boolean firstline = true;
		
		//loop and add all ids
		for (String newID : firstrowselements) {
			
			//ignore first line
			if (firstline){
				firstline = false;
				continue;
			}
			
			LanIDs.add(newID);					
			
		}				

		Log.info("Language ids listed:"+LanIDs.toString());
		firstline = true;
		Log.info("Language ids listed:"+LanIDs.toString());
		//other rows give data
		for (String currentline : lines) {
			
			//ignore first line as thats the LanID data
			if (firstline){
				firstline = false;
				continue;
			}
			
			//ignore if its a comment
			if (currentline.startsWith("\\")||currentline.startsWith("//")){
				continue;
			}
			
			addTextLineIntoDatabase(currentline);
			

			
		}				

		//Log.info("lines stored:"+textDatabases.get);
		
		loaded=true;
		Log.info("GameTextDatabase loaded, running next bits if needed");
		
		if (RunOnComplete!=null){
			Log.info("...");
			RunOnComplete.run();
		}
	}
	
}
