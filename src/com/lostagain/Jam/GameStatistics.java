package com.lostagain.Jam;

import java.util.LinkedList;
import java.util.Queue;
import com.google.common.base.Optional;

/** keeps track of some useful game statistics **/
public class GameStatistics {

	
	static Optional<GameStatisticDisplayer> GameStatisticDisplayerImplementation = Optional.absent();
	public static int MAX_LAST_COMMAND_LIST_SIZE = 50;
	private static boolean keepLogs=false;
	
	
	
	public static void setKeepLogs(boolean keepLogs) {
		GameStatistics.keepLogs = keepLogs;
	}


	public static void setGameStatisticDisplayer(GameStatisticDisplayer	gameStatisticDisplayerImplementation) {
				
		GameStatisticDisplayerImplementation= Optional.fromNullable(gameStatisticDisplayerImplementation);
			
	}

	
	public static void addLastCommandToStack(String lastcommand) {
		addLastCommandToStack(lastcommand, "","");
	}

	/***
	 * Fired from the instruction processor, this will add the string
	 * used to represent the command to the log.
	 * The log is currently set to display the last 50 commands, and uses
	 * a stack so the earliest commands get removed as new ones come in. 
	 * 
	 * @param string 
	 ***/
	public static void addLastCommandToStack(String lastcommand, String style, String tooltip) {
		
		
		lastCommandsStack.add(new DebugElement(lastcommand,style,tooltip));
		 
		 if (lastCommandsStack.size()>MAX_LAST_COMMAND_LIST_SIZE && !keepLogs){
			 
			 lastCommandsStack.poll();
		 }
		 
		 //now the stack array above is updated, we display the whole array with this command
		// updateCommandList(lastcommandstriggered,GameStatistics.lastCommandsStack);
		//GameStatisticDisplayerImplementation
		 if (GameStatisticDisplayerImplementation.isPresent()){
			 GameStatisticDisplayerImplementation.get().updateLastCommandList();
		 }
	}

	public static void setCommandStackSize(int CommandStackSize) {
		MAX_LAST_COMMAND_LIST_SIZE=CommandStackSize;
	}

	
	/** 
	 * Used to help debug scene events
	 * Stores a string, and option a color, to put onto the GameDataBoxs "lastsceneevent" line 
	 * **/
	public static class DebugElement {

		public String debugstring="";
		public String debugstyle="";
		public String debugtooltip="";

		public DebugElement(String debugstring) {
			super();
			this.debugstring = debugstring;
		}

		public DebugElement(String debugstring, String debugcolor) {
			super();
			this.debugstring = debugstring;
			this.debugstyle = debugcolor;
		}

		public DebugElement(String debugstring, String debugcolor,String debugtooltip) {
			super();
			this.debugstring = debugstring;
			this.debugstyle = debugcolor;
			this.debugtooltip = debugtooltip;

		}


	}

	//Global game data for profiling speeds of various engine functions
	public static long TotalConditionalCheckTime = 0;
	public static long TotalPropertyCheckCheckTime = 0;	
	public static long TotalCollisionCheckTime = 0;

	public static long TotalOldDatabaseLookupTime = 0;
	public static long TotalNewDatabaseLookupTime = 0;

	/** maintains a list of the last commands fired in the instruction processor 
	 * VERY useful for debuging scripts.
	 * Note the VERY is in capitals - that means it shouldn't be ignored */
	public static Queue <DebugElement> lastCommandsStack = new LinkedList<DebugElement>();	

	//---------------


}
