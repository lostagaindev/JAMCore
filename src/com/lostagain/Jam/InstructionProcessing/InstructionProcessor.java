/**
 * 
 */
package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.darkflame.client.semantic.SSSNode;


import com.lostagain.Jam.AssArray;
import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.FeedbackHistoryCore;
import com.lostagain.Jam.GameManagementClass;
import com.lostagain.Jam.GameStatistics;
import com.lostagain.Jam.GameVariableManagement;
import com.lostagain.Jam.GamesInterfaceTextCore;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.JamGlobalGameEffects;
import com.lostagain.Jam.SceneAndPageSet;
import com.lostagain.Jam.ScoreControll;
import com.lostagain.Jam.CollisionMap.PolySide;
import com.lostagain.Jam.CollisionMap.Polygon;
import com.lostagain.Jam.Factorys.IsTimerObject;
import com.lostagain.Jam.Factorys.NamedActionSetTimer;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.InstructionProcessing.CommandLine.GameCommands;
import com.lostagain.Jam.Interfaces.IsBamfImage;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.IsPopupPanel.JamPositionCallback;
import com.lostagain.Jam.Interfaces.PopupTypes.IsInventoryItemPopupContent;
import com.lostagain.Jam.Interfaces.JamChapterControl;
import com.lostagain.Jam.InventoryItems.InventoryItemFactory;
import com.lostagain.Jam.InventoryItems.IsImageWithAlphaItem;
import com.lostagain.Jam.InventoryItems.SelectItemFromInventoryMenu;
import com.lostagain.Jam.InventoryItems.TigItemCore;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementState;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.SaveMangement.JamSaveGameManager;
import com.lostagain.Jam.OptionalImplementations;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.Scene.SceneMenuWithPopUp;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.Scene.TextOptionFlowPanelCore;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectState.pathfindingMode;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDivObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneLabelObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneVectorObject;
import com.lostagain.Jam.SceneObjects.Interfaces.hasUserActions;
import com.lostagain.Jam.audio.JamAudioController;
import com.lostagain.Jam.audio.MusicBoxCore;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController.DeltaRunnable;
import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController.DeltaRunnable.fadeState;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyCalculator;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyTextUti;

/**
 * This is the games main instruction processor.
 * 
 * It takes a CommandList as input (which is basically a ArrayList of commands), then loops over
 * it running each command, provided it meets any specified conditionals.
 * 
 * It can also take a String as the input, where each command is on a new line.
 * This method is slower, but useful for inline commands in text files/conversations.
 * 
 * @author Thomas Wrobel
 */
public class InstructionProcessor    {

	public static boolean DisableInstructions = false;

	public static Logger Log = Logger.getLogger("JAMCore.InstructionProcessor");

	//not really used - was for testing the scene system outside of the game engine as a whole
	//private static boolean SceneTestMode = false;

	// stuff needed for saving 
	/** maintains a list of  all the divs ids that have had their css class's manipulated in some way.
	 * This is used so the save string knows what needs to be set when reloading. **/
	public static HashSet<String> ChangedHtmlPageElement = new HashSet<String>();

	//stuff needed for instruction processing;

	/** multimessage place array
	/ This is used for when messages in sequences are specified " Text1>>Text2>>Text3 " for example.
	 * This array tracks what message you are on for each bit of text, thus it knows the correct one to display next **/
	static AssArray MultiMessagePlace = new AssArray();

	
	/**
	 * CurrentlyChucked contains a set of objects currently being chucked into the inventory. We can always expend it to everything chucked. 
	 * 
	 * We check this later, as to prevent a crash when the object is chucked and checked at the same time (w a conditional f e)
	 */
	public static HashSet<SceneObject> CurrentlyChucked = new HashSet<SceneObject>();
	

	// function to convert a string to a CommandList. Needed for what the commands are supplied as a string.
	// for example; commands in text files, or movement files.
	// This function was moved to CommandList.java as a constructor option, but can be accessed here still for convenience
	public static CommandList StringToCommandList( String list) {

		/*
		CommandList commandList = new CommandList();

		//(\r?\n|\r)(-|\().* //old regex

		//^(-| -|\()[\s\S]*?$(\r?\n|\r)(?=-| -|\()  //new regex

		//we have to start with a newline and end with a - to make the regex work
		list = "\n"+list+"\n -";

		//float TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//


		String splitstring = "^(-| -|\\()[\\s\\S]*?$(\r?\n|\r)(?=-| -|\\(|\\/\\/)"; //splits by newlines that are followed by - or (

		RegExp regularExpression = RegExp.compile(splitstring,"mg");

		MatchResult matchResult = regularExpression.exec(list);

		Log.info("spliting "+list);		

		while ( matchResult!=null ){

			String line = matchResult.getGroup(0).trim();

			Log.info("split piece= "+line);			


			//add to command list if its a full line and not a quote
			if ((line.length()>2) && (!line.startsWith("\\")) ){
				CommandLine nc = new CommandLine(line);
				commandList.add(nc);
			}

			matchResult= regularExpression.exec(list);
		}


		//	float TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken


		//	Log.info("conversion to commands took= "+(TimeTakenStart-TimeTakenEnd));
		Log.info("number of  to commands  = "+commandList.size());	

		//TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		list=list.substring(0, list.length()-1); //remove the last character "-" we added above

		 */
		CommandList newcommandList = new CommandList(list);

		/*
		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken

		//Log.info("newconversion to commands took= "+(TimeTakenStart-TimeTakenEnd));
		Log.info("newnumber of  to commands  = "+newcommandList.size());	

		//now check they are the same
		for (int i = 0; i < newcommandList.size(); i++) {

			CommandLine line	 = commandList.get(i);
			CommandLine newline  = newcommandList.get(i);

			if (line.TheCommand!=newline.TheCommand){	

				Log.severe("MissMatched command: "+line.toString()+" not equal "+newline.toString());	

			}

		}*/



		/*

		for (int i=0; i<=matchResult.getGroupCount(); i++) {
	        String groupStr = matchResult.getGroup(i);
	        Log.info("split piece= "+groupStr);
	    }

		/*
		String[] ListIT = list.split(splitstring); //old method \r?\n|\r

		Log.info("split string= "+splitstring);


		while ( matcher.find() ){

			String string = matcher.group().trim();
			if (string.length()>2){
				CommandLine nc = new CommandLine(string);
				commandList.add(nc);
			}

		}





		for (String string : ListIT) {
			string=string.trim();
			if (string.length()>2){
				CommandLine nc = new CommandLine(string);
				commandList.add(nc);
			}
		}	*/

		return newcommandList;

	}


	//public static void processInstructions(String instructionset,
	//		String UniqueTriggerIndent, SceneObject ObjectThatCalledThis) {


	//	processInstructions(instructionset,UniqueTriggerIndent,(SceneObjectVisual)ObjectThatCalledThis); //temp version needed untill we are using SceneObjects in the instruction processor

	//}


	//if the commands were supplied as a string, we convert to a commandlist first 
	//before processing.
	public static void processInstructions(String instructionset,
			String UniqueTriggerIndent, SceneObject ObjectThatCalledThis) {

		//convert it to a command list
		CommandList commandsToRun = StringToCommandList(instructionset);

		//ensure there's at least some commands in the list before we process them!
		if (commandsToRun.size()>0) {
			processInstructions(commandsToRun,
					UniqueTriggerIndent, ObjectThatCalledThis);

		}

	}


	/** new process instructions system 
	 * This works by looping over the command list (commandsToRun), and only running commands if
	 * they meat any specified conditions.
	 * If they dont it skips to the next conditions in the  list
	 * 
	 * @param commandsToRun is just the commands you want to run. Its just a commandlist of commandlines.
	 * 
	 * @param UniqueTriggerIndent is a unique string you can specify for this specific "triggering" of the commands.
	 * Its used mostly for scoring. So people dont get scores added twice for doing the exact same thing.
	 * 
	 * @param ObjectThatCalledThis should be the object that triggered this event, and thus the object commands will run
	 * on by default
	 * **/
	//public static void processInstructions(CommandList commandsToRun,
	//		String UniqueTriggerIndent, SceneObject ObjectThatCalledThis) {
	//Note; Object that called this is currently SceneObjectVisual
	//Eventually it will be just SceneObject.
	//For the moment during the transition we will allow this alternative function
	//that auto-casts SceneObject to SceneObjectVisual
	//This is safe for the GWT version, but once we are ready for alternative versions it wont work
	//(ie, ones where SceneObjectVisual is not SceneObjects implementation)

	//	processInstructions( commandsToRun,UniqueTriggerIndent,(SceneObjectVisual)ObjectThatCalledThis);

	//}

	/** 
	 * new process instructions system 
	 * This works by looping over the command list (commandsToRun), and only running commands if
	 * they meat any specified conditions.
	 * If they dont it skips to the next conditions in the  list
	 * 
	 * @param commandsToRun is just the commands you want to run. Its just a commandlist of commandlines.
	 * 
	 * @param UniqueTriggerIndent is a unique string you can specify for this specific "triggering" of the commands.
	 * Its used mostly for scoring. So people don't get scores added twice for doing the exact same thing.
	 * 
	 * @param ObjectThatCalledThis should be the object that triggered this event, and thus the object commands will run
	 * on by default
	 * **/
	public static void processInstructions(
			CommandList commandsToRun,
			String UniqueTriggerIndent, 
			SceneObject ObjectThatCalledThis) {

		if (DisableInstructions){
			Log.info("Instructions aborted - probably due to resetting the scene");
			return;
		}

		//	Log.info("new process instructions GO!");		
		if (commandsToRun==null || commandsToRun.isEmpty() ){
			Log.info("Commands empty or null triggerid was:"+UniqueTriggerIndent);
			return;
		}

		//set the log to show where these game from
		if (ObjectThatCalledThis!=null){
			GameStatistics.addLastCommandToStack(" [commands from:"+ObjectThatCalledThis.getName()+"]  ","color:grey;font-size:x-small",UniqueTriggerIndent);
		} else {
			GameStatistics.addLastCommandToStack(" [commands not from object]  ","color:grey;font-size:x-small",UniqueTriggerIndent);

		}

		//reset must be called before looping over all the items
		//it basically tells the commandlist to "go to the start"
		//so that the next() and hasNext() functions get the commands first to last.....and dont get stuck on the last one next time  they are run.
		commandsToRun.resetIterator();

		// clear command variables
		boolean conditionalsPassed    = true; 
		boolean lastConditionalFailed = false;

		//start looping over all the commands till we run out of them (ie there's no next one)
		while (commandsToRun.hasNext()) {

			//we start the loop here...wheee....
			CommandLine line;

			//if we have previously failed a condition on the last loop
			if (lastConditionalFailed){
				//...we skip to the next conditional
				line = commandsToRun.getNextConditional();
				//if no next condition, we exit as we have run out of commands to run
				if (line==null){
					Log.info("skipped due to no new conditions " );
					tempDisableLog=false;//reenable the log if it was disabled

					return;
				}

				lastConditionalFailed = false;

			} else {					
				//if there was no failed conditional on the last line, we grab the next command in the list to run!
				line = commandsToRun.next();
			}


			//if we have been told to stop processing by a previous command
			if (commandsToRun.commandsAreSetToStop()){
				Log.info("Exiting command list due to stop flag being set on commandlist"); 
				commandsToRun.resetStopProcessingFlag();
				tempDisableLog=false;//reenable the log if it was disabled

				return;	
			}

			CommandLine.GameCommands currentCommandEnum = line.TheCommand;
			String CurrentProperty                      = line.TheParameters.getRawParameterString();


			//Log.info("Current line " + currentCommandEnum.toString()+" Permas:"+CurrentProperty);

			if (currentCommandEnum == CommandLine.GameCommands.CONDITIONAL){

				conditionalsPassed = false;

				//for profiling
				long TimeTakenStart = System.currentTimeMillis();					//
				conditionalsPassed = ConditionalList.checkConditionals(	line.CONDITIONS,ObjectThatCalledThis); //checkConditionals(
				//	line.CONDITIONS,ObjectThatCalledThis);
				long TimeTakenEnd = System.currentTimeMillis();						
				GameStatistics.TotalConditionalCheckTime = GameStatistics.TotalConditionalCheckTime+(TimeTakenEnd-TimeTakenStart);


				//test conditionals
				if (!conditionalsPassed) {

					// if not, we flag that it should skip to the next condition next time it loops back
					lastConditionalFailed = true;

					//add to the log in red to show it failed
					if (!tempDisableLog){
						GameStatistics.addLastCommandToStack("   "+line.CONDITIONS.toString(),"color:RED;font-size:smaller","failed conditional");
					}

					Log.info("exit due to conditions not met, going to next condition");
					//...and this continue statement loops it right back to the state of the while statement

					continue;


				} else {
					lastConditionalFailed = false;

					//add to the log in green to show it passed
					if (!tempDisableLog){						
						GameStatistics.addLastCommandToStack("   "+line.CONDITIONS.toString(),"color:GREEN;font-size:smaller","conditional passed");
					}

					// if it passed we shunt the StartHere to the next
					// instruction and continue

					Log.info("past conditions");

					continue;
				}


			}

			//if its not a conditional we process

			// swap variables if present
			//if (CurrentProperty.contains("<")) {	
			//line.TheParameters.containsVariables() &&

			//	if (  line.TheCommand.usesVariables()) { //NOTE: we should not be processing variables for certain command types. Any command that
			//uses the raw string only, like save strings, should not bother processing variables at all


			//Do we need to pre-process the parameters?
			//(most commands do, but some do not)
			boolean usesVariables   = line.TheCommand.usesVariables();
			boolean usesRandomSplit = line.TheCommand.usesRandomSplit();
			boolean usesMaths = line.TheCommand.usesMaths();
			
			if (line.TheParameters==null){
				Log.severe("line="+line.TheCommand+" has no params.");
			}
			
			if (usesVariables){
				Log.info("Command "+line.TheCommand.toString()+" uses variables:"+usesVariables);
			} else {
				Log.info("Command "+line.TheCommand.toString()+" does not use variables");
				
			}
				

			//Log.info(" processing params;"+line.TheParameters.getRawParameterString());
			line.TheParameters.prepareParameters(ObjectThatCalledThis,usesVariables,usesRandomSplit,usesMaths);	

			//	if (!SceneTestMode) {

			//		CurrentProperty = GameVariableManagement.replaceGameVariablesWithValues(CurrentProperty,ObjectThatCalledThis);

			//}

			//}

			//test
			if (!line.TheParameters.isEmpty()){
				Log.info(" command recieved;" + currentCommandEnum.toString() + "|TheParameters:" + line.TheParameters + " \n"); 
			} else {
				Log.info(" command recieved;" + currentCommandEnum.toString() + "|TheParameters: (no params)"); 				
			}

			if (currentCommandEnum==CommandLine.GameCommands.exitactions)
			{
				Log.info("Exiting command list \n"); 

				tempDisableLog=false;//reenable the log if it was disabled

				return;
			}


			//Real command processing goes here
			runCommand(
					line,
					//	currentCommandEnum,
					line.TheParameters,//line.TheParameters.getProccessedParameterString(),
					UniqueTriggerIndent, 
					commandsToRun, 
					ObjectThatCalledThis
					);




		}

		//reenable the log if it was disabled
		tempDisableLog=false;

	}

	//old processInstructions
	/**
	public static void processInstructions(String instructionset,
			String UniqueTriggerIndent, SceneObject ObjectThatCalledThis) {
		int StartHere;
		// Start pos to zero
		StartHere = 0;

		instructionset = instructionset + "\n"; //$NON-NLS-1$
		// clear command var
		String CurrentCommand = ""; //$NON-NLS-1$
		String CurrentProperty = ""; //$NON-NLS-1$

		boolean conditionalsPassed = true;

		while (instructionset.indexOf("- ", StartHere) > -1) //$NON-NLS-1$
		{

			StartHere = instructionset.indexOf("- ", StartHere) + 2; //$NON-NLS-1$

			// if endInstructions is triggered
			if (!SceneTestMode) {
				if (MyApplication.endInstructions) {
					MyApplication.endInstructions = false;
					break;
				}
			}
			// first we check for a conditional
			String currentLine = instructionset.substring(StartHere,
					instructionset.indexOf("\n", StartHere));

			if (currentLine.trim().startsWith("(")) {

				conditionalsPassed = false;
				String Conditionals = currentLine.trim().substring(1,
						currentLine.indexOf(")"));

				Log.info("Conditionals=" + Conditionals);

				// if there is one, we check whether the player pass's it.
				if (Conditionals.length() > 1) {
					conditionalsPassed = checkConditionals(Conditionals,ObjectThatCalledThis);
					Log.info("Conditionals checked " + Conditionals);

				}

				// if not, we shunt the StartHere to the next conditional, or
				// end it
				if (!conditionalsPassed) {
					int NextCondition = instructionset.indexOf(
							"- (", StartHere + Conditionals.length() + 2); //$NON-NLS-1$

					if (NextCondition == -1) {

						// exit instruction processing
						Log.info("exit due to conditions not met 1");

						return;

					} else {

						Log.info("exit due to conditions not met, going to next condition at _"
								+ NextCondition);
						StartHere = NextCondition;

						continue;
					}

				} else {
					// if it passed we shunt the StartHere to the next
					// instruction and continue
					continue;
				}
			}
			// else we continue

			int nextequals = instructionset.indexOf("=", StartHere) + 1;
			int nextnewline = instructionset.indexOf("\n", StartHere);

			// Log.info("index of next = " + nextequals);
			// Log.info("index of newline = " + nextnewline);

			// test there isnt a newline before the next equals
			if (nextnewline < nextequals) {
				// then its a command without properties
				Log.info("no propertys for current command");

				CurrentCommand = instructionset.substring(StartHere,
						nextnewline).trim();
				Log.info("CurrentCommand =" + CurrentCommand);
				// set the property to null

				CurrentProperty = "NO_PROPERTY_SET";

			} else {

				// Get name of current command
				CurrentCommand = instructionset.substring(StartHere,
						instructionset.indexOf("=", StartHere)).trim();




				// Get the property of the command

				CurrentProperty = instructionset.substring(nextequals,
						nextnewline).trim();

			}

			// swap variables if present
			if (CurrentProperty.contains("<V:")) {
				Log.info("vars found in property, replacing with values");

				if (!SceneTestMode) {
					CurrentProperty = MyApplication
							.replaceGameVariablesWithValues(CurrentProperty);
				}

			}

			// test
			Log.info("\n command recieved;" + CurrentCommand + "|Property:" + CurrentProperty + " \n"); //$NON-NLS-1$ //$NON-NLS-2$

			//convert to enum 
			CommandLine.GameCommands currentCommandEnum = CommandLine.GameCommands.notarecognisedcommand;
			try {
				currentCommandEnum = Enum.valueOf(CommandLine.GameCommands.class,CurrentCommand.toLowerCase());
			} catch (Exception e) {			
				Log.info("not a recognised command so we return");
				return;
			}

			if (currentCommandEnum==CommandLine.GameCommands.exitactions) //$NON-NLS-1$
			{
				Log.info("Exiting command list \n"); //$NON-NLS-1$ 
				return;
			}

			// process a subset of the commands to test them
			// runTestCommands(CurrentCommand, CurrentProperty,
			// UniqueTriggerIndent, instructionset);

			// real command processing goes here
			runCommand(currentCommandEnum, CurrentProperty,
					UniqueTriggerIndent, instructionset, ObjectThatCalledThis);


		}
	}
	 **/
	/**
	 * checks if the command is scene related, and if it is, we run its actions.
	 * @param currentParams 
	 **/
	private static void checkSceneRelatedCommands(
			CommandLine currentCommand,
			String theParameterString, //phase this out (use the CommandParameterSet instead)
			CommandParameterSet Parameters, 
			String UniqueTriggerIndent,
			CommandList instructionset,
			SceneObject callingObject, 
			String[] CurrentParams     //phase this out 
			)	
	{



		//TODO: at some point this whole called stuff probably should be looked into. Is it needed?

		// Log.info("checking Scene Related Commands");
		IsSceneSpriteObject lastSpriteObjectCalled = null;
		IsSceneLabelObject  lastTextObjectCalled   = null;
		//	SceneDivObject lastDivObjectCalled = null;
		//	SceneDivObject lastVectorObjectCalled = null;

		// first check type of last object
		if (callingObject != null) {

			if (callingObject.getObjectsCurrentState().getPrimaryObjectType() == SceneObjectType.Sprite
					|| callingObject.getObjectsCurrentState().getPrimaryObjectType() == SceneObjectType.InventoryObject) {
				lastSpriteObjectCalled = (IsSceneSpriteObject) callingObject;
			} else {
				lastSpriteObjectCalled = CurrentScenesVariables.lastSpriteObjectUpdated;
			}

			if ((callingObject.getObjectsCurrentState().getPrimaryObjectType() == SceneObjectType.DialogBox)
					|| (callingObject.getObjectsCurrentState().getPrimaryObjectType() == SceneObjectType.Label)) {
				lastTextObjectCalled = (IsSceneLabelObject) callingObject;
			} else {
				lastTextObjectCalled = CurrentScenesVariables.lastTextObjectUpdated;
			}

			//if ((callingObject.objectsCurrentState.getPrimaryObjectType() == SceneObjectType.Div)) {
			//	lastDivObjectCalled = (SceneDivObject) callingObject;
			//} else {
			//	lastDivObjectCalled = InstructionProcessor.lastDivObjectUpdated;
			//}

			//input?


		}



		//get the parameters
		//String theParameters   = Parameters.getProccessedParameterString();//theParameters should be phased out of use and we should use Parameters directly

		//phase this out -we should use commandparameterset directly, not via a string array
		//String CurrentParams[] = Parameters.getProccessedSplit(); //theParameters.split(",");


		//trim them all! trim trim trim!
		//(yes,I used a for loop!  any decade now they will be back in fashion)

		/*
		for(int x=0;x<CurrentParams.length;x++){	

			CurrentParams[x]=CurrentParams[x].trim();		
			//Log.info("Parameter trimmed:"+CurrentParams[x]);


		}*/

		//reset ParamsOrANull top null
		//	Arrays.fill(ParamsOrANull,null);

		//fill with new params as much as we can (assuming there was any params at all, else we just leave it null.
		//NOTE: when theres no commas there is still one parameter in CurrentParams[0], even if its a zero length string
		//This is why wwe test with a if like this first.
		//	if (!CurrentProperty.isEmpty()){
		//	System.arraycopy(CurrentParams, 0, ParamsOrANull, 0, CurrentParams.length);
		//	}



		//save the commands to the command stack for debugger
		//GameDataBox.addLastCommandToStack(currentCommand.toString()+":"+CurrentProperty);


		//now we find  what the command is, and run its instructions		
		switch (currentCommand.TheCommand){


		case displaymenu:
		{
			// display context menu
			String[] items = CurrentParams;//the items are just all the current parameters   //theParameters.trim().split(",");
			triggerADisplayMenu(callingObject, items);			
			break; 
		}
		case clearmenu: {
			if (SceneMenuWithPopUp.menuShowing) { 
				SceneMenuWithPopUp.currentMenu.CloseDefault();

			}	
			break;
		}		
		case filloptionbox:
		{

			/**
			 *  Currentproperty should first be split by comma into divName,tabName,optionsArray
			 * 
			 * - FillOptionBox = DivID,FirstTab,"Option1";"Option2";"Option,3";Option4
			 * 
			 **/

			String divName     =  Parameters.get(0).getAsString();                         // CurrentParams[0].trim();
			String tabName 	   =  Parameters.get(1).getAsString(); 
			String tempsplit[] =  Parameters.getProccessedParameterString().split(",", 3);

			ArrayList<String> options = SpiffyTextUti.splitNotWithinBrackets(tempsplit[2], ";", '"', '"');

			Log.info("triggering a display option box on div "+divName+" with "+options.size()+" options");

			//new method;
			IsSceneDivObject divObject = SceneObjectDatabase.getSingleDivObjectNEW(divName,callingObject,true);

			//set it
			if (divObject==null){
				Log.info("DIV NOT FOUND:"+divName);
			}

			//create option flow box
			//Label tempList = new Label("Wheeeeeeeeeeeeeeeee!!!!!!!!");
			TextOptionFlowPanelCore tempList = SceneObjectFactory.createTextOptionFlowPanel(options,callingObject,divName,tabName);
			//new TextOptionFlowPanel(options,callingObject,divName,tabName);
			tempList.setSize("100%", "100%");
			divObject.setDivsWidget(tempList.getVisualRepresentation());


		}
		break;
		case addboxoptions:
		{
			String divToFillName  =  Parameters.get(0).getAsString();            
			String optionsTabName =  Parameters.get(1).getAsString();             
			String tempsplit[]    =  Parameters.getProccessedParameterString().split(",", 3);
			ArrayList<String> topicsToAdd = SpiffyTextUti.splitNotWithinBrackets(tempsplit[2], ";", '"', '"');

			Log.info("filling an option box on div "+divToFillName+" with options "+optionsTabName.toString());
			TextOptionFlowPanelCore textOptionPanelToAddToo = TextOptionFlowPanelCore.globalTextOptionsList.get(divToFillName);


			//make sure it exists
			if (textOptionPanelToAddToo==null){
				Log.info("ERROR:"+divToFillName+" not found");
			}

			textOptionPanelToAddToo.addOptions(topicsToAdd, null,optionsTabName);

			break;
		}
		case  applyaction:
		{
			if (Parameters.getTotal()>1) {

				String objectname    =  Parameters.get(0).getAsString();        
				String actiontoapply =  Parameters.get(1).getAsString();     


				Set<? extends hasUserActions> curobjectaa = SceneObjectDatabase.getSceneObjectNEW(objectname,callingObject, Parameters.get(0).searchGlobal); 

				if (curobjectaa==null || curobjectaa.isEmpty()){
					Log.warning("applyaction triggered on:"+objectname+" but no objects found with that name");
					return;
					
				}
				//its possibly that the actions triggered will remove the object.
				//therefor we make a copy of the list of objects we are looping over to stop a concurrent modification error
				Set<? extends hasUserActions> tempcopy = new HashSet<>(curobjectaa);
				
				//trigger action on all found
				for (hasUserActions sceneObject : tempcopy) {
					sceneObject.userActionTriggeredOnObject(actiontoapply); 
				}
				
				

			} else {
				callingObject.userActionTriggeredOnObject(theParameterString);
			}
			break;
		}		 
		case sendrightclickto:
		{
			if (Parameters.getTotal()>0) {

				String object = theParameterString;
				String mode   = "";

				if (Parameters.getTotal() == 2) {
					object = Parameters.get(0).getAsString(); 
					mode   = Parameters.get(1).getAsString(); 
				}

				triggerSendRightClickToObject(callingObject, object, mode);

			} else {
				callingObject.triggerContextClickOnObject();
			}
			break;
		}
		case sendclickto:
		{
			if (Parameters.getTotal()>0) {

				String object = theParameterString;
				String mode   = "";

				if (Parameters.getTotal() == 2) {
					object = Parameters.get(0).getAsString(); 
					mode   = Parameters.get(1).getAsString(); 
				}

				triggerSendClickToObject(callingObject, object, mode);

			} else {

				callingObject.triggerClickOnObject();

			}
			break;
		}			
		case bamfitem:
		{
			//object name,object location, fade duration, hold duration
			String item_Name = Parameters.get(0).getAsString();

			//	int iposx = getNumericalValue(CurrentParams[1].trim(),callingObject);
			//	int iposy = getNumericalValue(CurrentParams[2].trim(),callingObject);

			int iposx = Parameters.get(1).getAsInt();
			int iposy = Parameters.get(2).getAsInt();

			//default values
			int fadeDurationItem = 1000;
			int holdDurationItem = 1000;

			//user values if specified
			if (Parameters.getTotal()>=4){
				//fadeDurationItem = getNumericalValue(CurrentParams[3].trim(),callingObject);
				fadeDurationItem = Parameters.get(3).getAsInt();
			}
			if (Parameters.getTotal()>=5){
				//	holdDurationItem = getNumericalValue(CurrentParams[4].trim(),callingObject);
				holdDurationItem = Parameters.get(4).getAsInt();
			}

			Log.info("Bamfing an inventory item at "+iposx+","+iposy+" for 2x"+fadeDurationItem+"+"+holdDurationItem);

			//get object
			//Image itemToBamf = InventoryPanel.getItemFromName(item_Name).getImage();
			//Log.info("Item URL Is"+itemToBamf.getUrl());

			String ImgBamfurl = JAMcore.homeurl+"InventoryItems/"+item_Name+"/thumb_"+item_Name+".png"; 

			triggerBamf(ImgBamfurl ,iposx,iposy,fadeDurationItem,holdDurationItem);

		}

		break;

		case bamfobject:
		{
			//item name,object location, fade duration, hold duration
			String objectName = Parameters.get(0).getAsString();

			//	int oposx = getNumericalValue(CurrentParams[1].trim(),callingObject);
			//	int oposy = getNumericalValue(CurrentParams[2].trim(),callingObject);

			int oposx = Parameters.get(1).getAsInt();
			int oposy = Parameters.get(2).getAsInt();


			//default values
			int fadeDuration = 1000;
			int holdDuration = 1000;

			//user values if specified
			if (Parameters.getTotal()>=4){
				//	fadeDuration = getNumericalValue(CurrentParams[3].trim(),callingObject);
				fadeDuration = Parameters.get(3).getAsInt();
			}
			if (Parameters.getTotal()>=5){
				//holdDuration = getNumericalValue(CurrentParams[4].trim(),callingObject);
				holdDuration = Parameters.get(4).getAsInt();
			}


			Log.info("Bamfing an object at "+oposx+","+oposy+" for 2x"+fadeDuration+"+"+holdDuration);

			//get object
			//SceneSpriteObject objectToBamf = SceneWidget
			//		.getSpriteObjectByName(objectName,callingObject)[0];

			Set<? extends IsSceneSpriteObject> objectsToBamf = SceneObjectDatabase.getSpriteObjectNEW(objectName,callingObject,Parameters.get(0).searchGlobal);

			//we need just one element from this set, as we can only bamfing a single object at a time is supported right now
			//first we warn if we have more then one object
			if (objectsToBamf.size()>1){
				Log.info("Warning: more then one object found for "+objectName+". We can only however, bamf a single object at a time. So we choose one arbitarily");
			}
			//we just use the first object the iterator returns (we can't use .get(0) as Sets have no order and thus no numbered positions!)
			IsSceneSpriteObject objectToBamf = objectsToBamf.iterator().next();


			triggerBamf(objectToBamf.getObjectsCurrentState().ObjectsURL ,oposx,oposy,fadeDuration,holdDuration);



			break;
		}
		case toggleloadgame:
		{
			Log.info("toggleloadgame");
			RequiredImplementations.toggleLoadGamePopUp();
			break;
		}			
		case toggleinventory: {

			String inventoryname   = Parameters.getProccessedParameterString();//  theParameterString;
			IsPopupPanel inventory = JAMcore.allInventoryFrames.get(inventoryname);

			if (inventory!=null){
				if (!inventory.isShowing()){
					inventory.OpenDefault();
				} else {
					inventory.CloseDefault();
				}
			}
			break;
		}
		case hideinventory: {

			String inventoryname   = Parameters.getProccessedParameterString();//  theParameterString;
			IsPopupPanel inventory = JAMcore.allInventoryFrames.get(inventoryname);

			if (inventory!=null){
				inventory.CloseDefault();
			}

			break;	
		}
		case showinventory: {

			String inventoryname   = Parameters.getProccessedParameterString();//  theParameterString;
			IsPopupPanel inventory = JAMcore.allInventoryFrames.get(inventoryname);

			if (inventory!=null){
				inventory.OpenDefault();
			}

			break;		
		}

		case displayinventorylist:
		{
			String inventorynametodisplay = Parameters.get(0).getAsString(); //CurrentParams[0];
			String objectosendactionstoo  = Parameters.get(1).getAsString();

			triggerAInventoryList(inventorynametodisplay, objectosendactionstoo,callingObject);
		}
		break; 
		case settext:
		{

			if (Parameters.getTotal() == 2) {

				String dialoguename = Parameters.get(0).getAsString();
				String nexttextcontents = Parameters.get(1).getAsString();

				nexttextcontents = SpiffyTextUti.stripOuterQuotes(nexttextcontents);

				Set<IsSceneLabelObject> curdobjectsct = SceneObjectDatabase.getTextObjectNEW(dialoguename,callingObject,Parameters.get(0).searchGlobal);

				for (IsSceneLabelObject so : curdobjectsct) {

					so.setText(nexttextcontents);
					CurrentScenesVariables.lastTextObjectUpdated = so;

				}

			} else if (Parameters.getTotal()==1) {

				String nexttextcontents = Parameters.get(0).getAsString();		
				nexttextcontents = SpiffyTextUti.stripOuterQuotes(nexttextcontents);

				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Label))
				{
					((IsSceneLabelObject)callingObject).setText(nexttextcontents);
					CurrentScenesVariables.lastTextObjectUpdated = (IsSceneLabelObject)callingObject;
				} else {
					Log.severe("no object specified for setting text, and calling object is not a label");

				}


			} else {
				Log.severe("bad number of parameters for set text");


			}



			break;
		}
		case setcurrenttext:
		{
			String textname = Parameters.get(0).getAsString();// CurrentParams[0];

			//Note; once SceneObjectDatabase returns IsSceneLabelObject rather then SceneLabelObjects
			//we no longer needs this weird "? extends" generics 
			Set<IsSceneLabelObject> curdobjectsct = SceneObjectDatabase.getTextObjectNEW(textname,callingObject,Parameters.get(0).searchGlobal);

			for (IsSceneLabelObject so : curdobjectsct) {
				//this is a bit silly
				//we are asked to set the current text, but if the user specifies a bunch of text objects we want the LAST
				//however, as sets have no guaranteed order the best we can do is loop over and keep changing it till we run out of objects.
				CurrentScenesVariables.lastTextObjectUpdated = so;

			}


			break; 
		}
		case settexturl:
		{
			if (Parameters.getTotal() == 2) {
				String name = Parameters.get(0).getAsString();
				String url  = Parameters.get(1).getAsString();

				//Note; once SceneObjectDatabase returns IsSceneLabelObject rather then SceneLabelObjects
				//we no longer needs this weird "? extends" generics 			
				//We also need SceneObjectDatabase to return a DIALOG object, not a label one.
				//This is currently a limitation of the database system that needs fixing
				Set<IsSceneDialogueObject> curobjectstu = SceneObjectDatabase.getDialogueObjectNEW(name,callingObject,Parameters.get(0).searchGlobal);

				for (IsSceneDialogueObject dialogueobject : curobjectstu) {

					dialogueobject.setURL(url);

				}

			} else {
				String url = theParameterString.trim();

				//if callingObject supports it, we use that
				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.DialogBox)){
					((IsSceneDialogueObject)callingObject).setURL(url);
				} else {
					Log.info("no object specified for setting dialog url, and calling object is not dialogbox");

				}

			}

		}

		break; 
		case setobjecturl:
		{

			// split to get num of frames and the item to change - if none,
			// assume its just a image with 1 frames
			// String[] ParamArray = CurrentProperty.split(",");

			// if theres 2 params then assume its url+frames
			if (Parameters.getTotal() == 3) {

				String object =  Parameters.get(0).getAsString();
				String url    =  Parameters.get(1).getAsString();
				String frames =  Parameters.get(2).getAsString();

				//replaced with new method below
				Set<? extends IsSceneSpriteObject> curobjectsou = SceneObjectDatabase
						.getSpriteObjectNEW(object,callingObject,Parameters.get(0).searchGlobal);

				for (IsSceneSpriteObject so : curobjectsou) {
					so.setSpritesURL(so.getObjectsCurrentState(),url, Integer.parseInt(frames));
				}


			} else if (Parameters.getTotal() == 2) {
				String url    =   Parameters.get(0).getAsString();
				String frames =   Parameters.get(1).getAsString();

				lastSpriteObjectCalled.setSpritesURL(lastSpriteObjectCalled.getObjectsCurrentState(),url, Integer.parseInt(frames));

			} else {
				String url = theParameterString;
				lastSpriteObjectCalled.setSpritesURL(lastSpriteObjectCalled.getObjectsCurrentState(),url, 1); //used to be zero for some reason
			}

		}

		break; 
		case  addobjecttouching:
		{
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String object           = Parameters.get(0).getAsString();
				String newtouchingname  = Parameters.get(1).getAsString();

				SceneObject touchingObject = SceneObjectDatabase.getSingleSceneObjectNEW( newtouchingname, callingObject,  Parameters.get(1).searchGlobal); 

				// get the object				
				Set<? extends SceneObject> curobjectaot = SceneObjectDatabase.getSceneObjectNEW(object,callingObject,Parameters.get(0).searchGlobal);

				//add touching property to all objects found
				for (SceneObject so : curobjectaot) {					
					so.addTouchingProperty(touchingObject,callingObject);
				}

			} else {

				SceneObject touchingObject = SceneObjectDatabase.getSingleSceneObjectNEW( Parameters.get(0).getAsString(), callingObject,  Parameters.get(0).searchGlobal); 
				callingObject.addTouchingProperty(touchingObject,callingObject);

			}

			break; 
		}
		case  removeobjecttouching:
		{
			// in future maybe support a comma separated list?
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String object                  = Parameters.get(0).getAsString();
				String newtouchingobjectsname  = Parameters.get(1).getAsString();

				SceneObject touchingObject = SceneObjectDatabase.getSingleSceneObjectNEW(newtouchingobjectsname, callingObject,  Parameters.get(1).searchGlobal); 

				// get the object			
				Set<? extends SceneObject> curobjectrot = SceneObjectDatabase.getSceneObjectNEW(object,callingObject,Parameters.get(0).searchGlobal);

				//removing touching properties from all objects
				for (SceneObject so : curobjectrot) {
					so.removeTouchingProperty(touchingObject,callingObject);
				}

			} else {
				SceneObject touchingObject = SceneObjectDatabase.getSingleSceneObjectNEW( Parameters.get(0).getAsString(), callingObject,  Parameters.get(0).searchGlobal); 

				callingObject.removeTouchingProperty(touchingObject,callingObject);
			}

		}
		break; 
		case setobjectpin:
		{
			if (Parameters.getTotal() >= 3) {

				Log.info("setting objects pin");

				// if there's a comma, the first part is the object to apply it too
				String objectName = Parameters.get(0).getAsString();
				int px            = Parameters.get(1).getAsInt();                                 //Integer.parseInt(CurrentParams[1].trim());
				int py            = Parameters.get(2).getAsInt();
				int pz            = 0;

				if (Parameters.getTotal() == 4){
					//	pz = Integer.parseInt(CurrentParams[3].trim());
					pz = Parameters.get(3).getAsInt();					
				}
				// get the object (NOTE: This should be replaced with just getsceneobject, not sprite object)
				Set<? extends SceneObject> curobjects = SceneObjectDatabase.getSceneObjectNEW( objectName, callingObject,  Parameters.get(0).searchGlobal); 

				// set their states
				for (SceneObject widget : curobjects) {
					widget.setPin(px, py,pz,true);
				}

			}
		}
		break; 
		case setobjecttoscene:
		{
			String objectname =  Parameters.get(0).getAsString();//  CurrentParams[0];
			String scenename  =  Parameters.get(1).getAsString();//  CurrentParams[1];

			SceneWidget scene = SceneWidget.getSceneByName(scenename);

			//new method;
			Set<? extends SceneObject> objectstoset = SceneObjectDatabase.getSceneObjectNEW(objectname,callingObject,  Parameters.get(0).searchGlobal);

			//change their scene
			for (SceneObject sceneObject : objectstoset) {

				Log.info("setting scene of "+sceneObject.getObjectsCurrentState().ObjectsName+" to: "+scenename);
				sceneObject.setObjectsScene(scene);

			}
		}
		break; 
		case setobjecttopositionof:  //used to be setobjecttoposition
		{
			String objectname1 = Parameters.get(0).getAsString();
			String objectname2 = Parameters.get(1).getAsString();

			//object or objects we are positioning
			Set<? extends SceneObject> objectstoposition = SceneObjectDatabase.getSceneObjectNEW(objectname1,callingObject, Parameters.get(0).searchGlobal);

			//object used as a reference for the new position
			SceneObject refObject                        = SceneObjectDatabase.getSingleSceneObjectNEW(objectname2, callingObject, Parameters.get(1).searchGlobal);

			Log.info(" setobjecttopositionof;"+objectstoposition.size()+" objects to: "+refObject.getObjectsCurrentState().ObjectsName);

			for (SceneObject sceneObject : objectstoposition) {
				sceneObject.setPosition(refObject.getX(), refObject.getY(),refObject.getZ(),true);
			}

		}
		break; 
		case setvectorstring:  
		{
			Log.info("Changing a vector: "+theParameterString);

			//String vectorobject =  theParameterString.split(",",2)[0];
			//String vectorstring =  theParameterString.split(",",2)[1];

			String vectorobject =  Parameters.get(0).getAsString();
			String vectorstring =  Parameters.getProccessedParameterString().split(",",2)[1];

			//this if shouldnt be needed, variables are processed over all theParameters string  already
			if (vectorstring.contains("<")){

				Log.info("Replacing the variables in the vector string ");
				vectorstring = replaceVarValuesInString(vectorstring, callingObject);

			}

			Set<? extends IsSceneVectorObject> objecttoset = SceneObjectDatabase.getVectorObjectNEW(vectorobject,callingObject,Parameters.get(0).searchGlobal);

			for (IsSceneVectorObject sceneObject : objecttoset) {

				Log.info("Replacing  "+sceneObject.getObjectsCurrentState().ObjectsName+" to: "+vectorstring);	
				sceneObject.setNewVectorString(vectorstring);

			}

		}
		break;
		case setobjectcss:
		{
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objectnametosetstyle  = Parameters.get(0).getAsString();
				String stylename 			 = Parameters.get(1).getAsString();

				Set<? extends IsSceneDivObject> objectstoset = SceneObjectDatabase.getDivObjectNEW(objectnametosetstyle,callingObject,Parameters.get(0).searchGlobal);
				if (objectstoset==null){
					//REMEBER: only in GWT implementations are objects all automatically also DIVs
					//This function will almost certainly do nothing in other implementations unless you have made a special CSS emulation system
					//
					Log.info("No object found called:"+objectnametosetstyle);
					return;
				}


				//their states		
				for (IsSceneDivObject sceneObject : objectstoset) {
					//Log.info("setting style of "+sceneObject.objectsCurrentState.ObjectsName+" to: "+stylename);				
					//sceneObject.setStylePrimaryName(stylename);
					//sceneObject.objectsCurrentState.CurrentBoxCSS = stylename;

					sceneObject.setBoxCSS(stylename);
				}

			} else {
				Log.info("setting style to: "+theParameterString);

				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Div)){

					((IsSceneDivObject)callingObject).setBoxCSS(theParameterString);

				}


			}

		}
		break;
		case addobjectcss:
		{
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objectnametoaddstyle  =  Parameters.get(0).getAsString();
				String stylenametoadd		 =  Parameters.get(1).getAsString();

				// get the objects
				Set<? extends IsSceneDivObject> objectstoset = SceneObjectDatabase.getDivObjectNEW(objectnametoaddstyle,callingObject, Parameters.get(0).searchGlobal);
				if (objectstoset==null){
					//This function will almost certainly do nothing in other implementations unless you have made a special CSS emulation system
					//
					Log.info("No object found called:"+objectnametoaddstyle);
					return;
				}


				//their states		
				for (IsSceneDivObject sceneObject : objectstoset) {	
					//Log.info("adding style of "+sceneObject.objectsCurrentState.ObjectsName+" to: "+stylenametoadd);				
					//sceneObject.setStylePrimaryName(stylename);
					//sceneObject.objectsCurrentState.CurrentBoxCSS = stylename;
					sceneObject.addBoxCSS(stylenametoadd);


				}
			} else {
				Log.info("adding style to: "+theParameterString);
				//callingObject.addBoxCSS(CurrentProperty);

				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Div)){
					((IsSceneDivObject)callingObject).addBoxCSS(theParameterString);
				} else {
					Log.info("object not css compatible: "+theParameterString);
				}

			}

		}
		break;

		case removeobjectcss:
		{
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objectnametoremovestyle = Parameters.get(0).getAsString();
				String stylenametoadd 		   = Parameters.get(1).getAsString();

				// get the objects
				Set<? extends IsSceneDivObject> objectstoset = SceneObjectDatabase.getDivObjectNEW(objectnametoremovestyle,callingObject, Parameters.get(0).searchGlobal);

				if (objectstoset==null){
					//REMEBER: only in GWT implementations are objects all automatically also DIVs
					//This function will almost certainly do nothing in other implementations unless you have made a special CSS emulation system
					//
					Log.info("No object found called:"+objectnametoremovestyle);
					return;
				}

				//their states	
				for (IsSceneDivObject sceneObject : objectstoset) {	
					//Log.info("removing style of "+sceneObject.objectsCurrentState.ObjectsName+" from: "+stylenametoadd);				
					//sceneObject.setStylePrimaryName(stylename);
					//sceneObject.objectsCurrentState.CurrentBoxCSS = stylename;
					sceneObject.removeBoxCSS(stylenametoadd);

				}
			} else {
				Log.info("removing style : "+theParameterString);

				//	callingObject.removeBoxCSS(CurrentProperty);
				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Div)){
					((IsSceneDivObject)callingObject).removeBoxCSS(theParameterString);
				}

			}

		}
		break;
		case moveobjectto:

			if (Parameters.getTotal() >= 3) {

				Log.info("moving object too");

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttomove = Parameters.get(0).getAsString();
				//int tx = ProcessPosition(CurrentParams[1].trim(),callingObject);
				//int ty = ProcessPosition(CurrentParams[2].trim(),callingObject);				
				//	int tx = getNumericalValue(CurrentParams[1].trim(),callingObject);
				//	int ty = getNumericalValue(CurrentParams[2].trim(),callingObject);

				int tx = Parameters.get(1).getAsInt();
				int ty = Parameters.get(2).getAsInt();
				int speed = 300;

				if (Parameters.getTotal() >= 4) {
					speed = Parameters.get(3).getAsInt();
				}

				triggerMoveObjectsTo(objecttomove, tx, ty,speed,callingObject,false,Parameters.get(0).searchGlobal ); //currently we dont ignore collisions. Later we might like this as a option

			}

			break; 
		case moveobject: 
		{

			//TODO: this whole commands a mess
			Log.info("moving object");
			//defaults
			String objects = ""; //empty string will result in callingObject being used


			String xpos    = Parameters.get(0).getAsString(); 
			String ypos    = Parameters.get(1).getAsString();			
			String zpos    = "0";

			boolean detectCollision = true;

			if (Parameters.getTotal() > 2) {

				// if there's a comma, the first part is the object to apply it
				// too
				objects =  Parameters.get(0).getAsString();
				xpos    =  Parameters.get(1).getAsString(); //why we getting as string even?
				ypos    =  Parameters.get(2).getAsString(); //why we getting as string even? we can get ints directly

				if (Parameters.getTotal()>3){

					detectCollision= Boolean.parseBoolean(  Parameters.get(3).getAsString());	

					//TODO:wait x,y,boolean,z? thats dumb
					//how often did we use x,y,true or false?
					//do we need to change each instance of that?
					if (Parameters.getTotal()>4){
						zpos =  Parameters.get(4).getAsString();		
					}

				}

			}
			int dx = 0, dy = 0, dz=0;
			dx = Integer.parseInt(xpos);
			dy = Integer.parseInt(ypos);
			dz = Integer.parseInt(zpos);

			Log.info("moving objects.."+objects+" to"+dx+","+dy);
			displaceObjects(objects, dx, dy,dz,detectCollision,callingObject);


		}
		break; 
		case droptoparentsbase:
		{
			//drop commands basically have the same parameters so we just send the current command to the function
			dropObjects(Parameters, callingObject,currentCommand.TheCommand);
			break;
		}
		case droptofloor:
		{		
			//drop commands basically have the same parameters so we just send the current command to the function
			dropObjects(Parameters, callingObject,currentCommand.TheCommand);
			break;
		}
		case addimpulse:
		{
			//specified in pixels per second, and we convert to ms
			SimpleVector3 impulseMag = new SimpleVector3(0,0,0);//default

			String objectname = "";

			if (Parameters.getTotal() == 3) {

				double x = Parameters.get(0).getAsDouble() / 1000.0;
				double y = Parameters.get(1).getAsDouble() / 1000.0;
				double z = Parameters.get(2).getAsDouble() / 1000.0;

				impulseMag = new SimpleVector3(x,y,z);


			} else if (Parameters.getTotal() == 4) {

				String parameterOne = Parameters.get(0).getAsString();		
				objectname = parameterOne;				

				double x = Parameters.get(1).getAsDouble() / 1000.0;
				double y = Parameters.get(2).getAsDouble() / 1000.0;
				double z = Parameters.get(3).getAsDouble() / 1000.0;

				impulseMag = new SimpleVector3(x,y,z);//
			} 


			if (!objectname.isEmpty()){

				Set<? extends SceneObject> objectsToDrop = Parameters.get(0).getAsObjects(callingObject);


				if (objectsToDrop!=null){
					for (SceneObject so : objectsToDrop) {

						so.addImpulse(impulseMag);

					}
				}


			}  else {

				Log.info("Adding impulse to object :"+impulseMag.toString());
				callingObject.addImpulse(impulseMag);

			}

		}
		break;
		case addlink:
		{
			Log.info("linking relative object(/s):"+Parameters.rawParameterString);


			String childName      = "";
			String parentName     = "";

			Set<? extends SceneObject> childObjects = null;
			Set<? extends SceneObject> newParents   = Sets.newHashSet(callingObject);			

			if        (Parameters.getTotal() == 1) {

				childName    = Parameters.get(0).getAsString();
				childObjects = SceneObjectDatabase.getSceneObjectNEW(childName, callingObject, Parameters.get(0).searchGlobal);

			} else if (Parameters.getTotal() == 2) {

				parentName = Parameters.get(0).getAsString();	
				childName  = Parameters.get(1).getAsString();

				newParents   = SceneObjectDatabase.getSceneObjectNEW(parentName,  callingObject,  Parameters.get(0).searchGlobal);
				childObjects = SceneObjectDatabase.getSceneObjectNEW(childName,   callingObject,  Parameters.get(1).searchGlobal);

			}

			//double loop time!

			for (SceneObject child : childObjects) {	

				for (SceneObject parent : newParents) {							
					Log.info("attaching (child) "+child.getName()+" to (parent) "+parent.getName());

					child.attachTo(parent);
				}
			}


			break; 
		}
		case removelink:
		{

			SceneObject removeFrom = callingObject;
			String objectsToRemoveName = theParameterString;


			if (Parameters.getTotal() == 2) {
				removeFrom = Parameters.get(0).getAsObject(callingObject);
				objectsToRemoveName = Parameters.get(1).getAsString();

			}


			// get the object to position
			Set<? extends SceneObject> removeTheseObject = SceneObjectDatabase.getSceneObjectNEW(objectsToRemoveName,removeFrom, true);

			if (removeTheseObject==null){
				return;
			}

			for (SceneObject sceneObject : removeTheseObject) {

				//callingObject.relativeObjects.remove(sceneObject);	//now built into detach
				sceneObject.getObjectsCurrentState().positionedRelativeToo = null;
				sceneObject.detach();
				Log.info("unlinking specified relative object(/s):"+sceneObject.getName()+" from "+removeFrom.getName());

			}

		}
		break; 
		case copymovementstate:

			Set<? extends SceneObject> copyTo; //we can copy to many
			SceneObject copyFrom; //only one object
			Log.info("copy movement state request");
			
			if (Parameters.getTotal()>1) {
				
				copyTo   = Parameters.get(0).getAsObjects(callingObject);
				copyFrom = Parameters.get(1).getAsObject(callingObject);	

				if (copyTo==null || copyTo.isEmpty()){
					Log.severe("no objects found to copy the state too");					
					return;
				}
				if (copyFrom==null){
					Log.severe("no object found to copy the state from");					
					return;
				}

				Optional<MovementState> copyOfState = Optional.absent();
				
				if (copyFrom.getObjectsCurrentState().moveState.isPresent()){		
					Log.info(copyFrom.getName()+" had movement");	
					
					copyOfState = Optional.of( copyFrom.getObjectsCurrentState().moveState.get().copy() );
					
				} else {
					Log.info(copyFrom.getName()+" had no movement");					
				}
				
				Log.info("copying state from "+copyFrom.getName()+" to "+copyTo.size()+" objects");					
				
				for (SceneObject so : copyTo) {					
					so.setMovementState(copyOfState);					
				}
				
				
				Log.info("completed state copying");
				

			} else {				
				copyFrom = Parameters.get(0).getAsObject(callingObject);		
				Optional<MovementState> copyOfState = Optional.absent();
				if (copyFrom.getObjectsCurrentState().moveState.isPresent()){		
					copyOfState = Optional.of( copyFrom.getObjectsCurrentState().moveState.get().copy());
				}


				callingObject.setMovementState(copyOfState);

				Log.info("Completed the state copying");
				return;
			}





			break;
		case clearlastsceneobject:
			//this simple, we just clear the last scene item updated.
			//We call this "item" in the commands because in the script the variable is called by <LASTSCENEITEM>
			CurrentScenesVariables.lastSceneObjectUpdated=null;
			break;
		case cloneover:		
		{
			
			
			
			
			//sourcetoclonefrom,targettocloneover,newname,typeofcoverage,spaceing
			//wip; we will support "bottom edge" coverage first

			if (Parameters.getTotal() >= 5) {
				
			SceneObject objectToClone = Parameters.get(0).getAsObject(callingObject);
			if (objectToClone==null){
				Log.severe("object to clone not found");
				return;
			}
			
			Set<? extends SceneObject> targetsToCloneOver = Parameters.get(1).getAsObjects(callingObject);
			if (targetsToCloneOver==null || targetsToCloneOver.isEmpty()){
				Log.severe("object to clone over not found");
				return;
			}

			String newname = Parameters.get(2).getAsString(); 
			String type = Parameters.get(3).getAsString(); 
			int spaceing = Parameters.get(4).getAsInt();
			
			//
			//CloneOver is a slow, asycn, command. We therefor need to save later instructions and trigger them after its done
			//
			//crop the instructionset so it starts from under this line.
			CommandList postInstructions = cropInstructionSetToCurrentCommand(currentCommand, instructionset);

			Log.info("Setting cloneover to start: " + Parameters.toString() + ""); 
			Log.info("With post instructions; " + postInstructions.getCode() + ""); 

			//Set the current actionsset to stop processing (as the copy above will now run instead)
			instructionset.stopProcessing();

			
			//only supporting bottom edge type right now, so we are assuming this is bottom edge
			//if (type.equals("bottomedge")){
			
			for (SceneObject cloneoverthis : targetsToCloneOver) {
				
				Log.info("cloning over:"+cloneoverthis);
				wip_cloneover_bottomedge(postInstructions,callingObject,objectToClone,newname,cloneoverthis,spaceing);
				
				
			}
			
			
			} else {
				
				Log.severe("Not enough parameters giving for cloning function:"+Parameters.getProccessedParameterString());
				Log.severe("[sourcetoclonefrom,targettocloneover,newname,typeofcoverage,spaceing]");

			}
			break;
			
		}
		case cloneat:		
		{	
			if (Parameters.getTotal() >= 4) { //there must be more then 4 params for cloning functions

				Log.info("Cloning object");

				// the first part is the object to apply it too 
				SceneObject objectToClone = Parameters.get(0).getAsObject(callingObject);// .getAsString();
				
				//New parameter system is pre-processed for variables and can get ints itself.
				int x =  Parameters.get(1).getAsInt(); 
				int y =  Parameters.get(2).getAsInt();
				int z =  0;

				String newname = Parameters.get(3).getAsString().trim();  //CurrentParams[3].trim();

				//or if we have a z co-ordinate the newname was actually at slot 4 rather then 3
				if (Parameters.getTotal()==5){
					//z = getNumericalValue(CurrentParams[3].trim(),callingObject);
					z       = Parameters.get(3).getAsInt();					
					newname = Parameters.get(4).getAsString(); //CurrentParams[4].trim();

				}


				triggerCloneObject(callingObject, objectToClone, newname, x, y, z);

			} else {
				Log.severe("Not enough parameters giving for cloning function:"+Parameters.getProccessedParameterString());

			}
		}

			break; 
		case setobjectposition:

			Log.info("positioning object "+Parameters.getTotal()+" parameters:"+Parameters.getProccessedParameterString());

			//-sigh- 
			//this is complicated to work out as we need to support various combinations of parameters
			// 
			// number,number,[number] = just the new position absolute applied to the current object
			// objectname,number,number,[number] = just the position absolute applied to the specified object
			// objectname,objectname,number,number,[number] = position relative to the second object

			//NOTE: to avoid ambiguities, the objects name must be supplied if positioning relative to something

			//TODO:So first we work out if we are applying ourselves to the current object or not
			//We do this by working out if we start with a string or number
			if (Parameters.get(0).isNumber()) {
				//then we are moving just the calling object
				//aka
				//number,number,[number] = just the new position absolute applied to the current object

				int x = Parameters.get(0).getAsInt();
				int y = Parameters.get(1).getAsInt();
				int z = 0;
				if (Parameters.getTotal()==3){
					z = Parameters.get(2).getAsInt();
				}
				Log.info("positioning calling object absolutely at "+x+","+y+","+z);				
				callingObject.StopCurrentMovement();	

				//as we are positioning absolute we ensure we are detached from anything else 
				callingObject.detach();

				//then set the position
				callingObject.setPosition(x, y, z,true);

				return;
			} else {
				String objectToPosition = Parameters.get(0).getAsString();
				Log.info("Positioning object "+objectToPosition+" with "+Parameters.getTotal());	


				//now need to test if the second parameter is a number or name as well
				//if number, then we are positioning absolutely. If name, its a relative position.
				if (Parameters.get(1).isNumber()){
					// aka					
					// objectname,number,number,[number] = just the absolute position applied to the specified object
					int x = Parameters.get(1).getAsInt();
					int y = Parameters.get(2).getAsInt();
					int z = 0;
					if (Parameters.getTotal()==4){
						z = Parameters.get(3).getAsInt();
					}

					Set<? extends SceneObject> objectsToPosition = Parameters.get(0).getAsObjects(callingObject);

					if (objectsToPosition==null || objectsToPosition.size()==0){
						Log.severe("no objects found called:"+objectToPosition);

					}
					Log.info("total objects called "+objectToPosition+": "+objectsToPosition.size());				
					Log.info("positioning them absolutely at "+x+","+y+","+z);	

					// set their state
					for (SceneObject curobject : objectsToPosition) {
						//stop any movements
						curobject.StopCurrentMovement();
						curobject.detach();

						//set the position						
						curobject.setPosition(x, y, z, true);
					}

					return;
				} else {
					// We have two object names in a row
					// aka					
					// objectname,objectname,number,number,[number] = position relative to the second object
					//or
					// objectname,objectname(attachmentPointName),number,number,[number] = position relative to the second object
					Set<? extends SceneObject> objectsToPosition = Parameters.get(0).getAsObjects(callingObject);
					String objecttopositionrelativeto = Parameters.get(1).getAsString();
					int x = Parameters.get(2).getAsInt();
					int y = Parameters.get(3).getAsInt();
					int z = 0;
					if (Parameters.getTotal()==5){
						z = Parameters.get(4).getAsInt();
					}
					Log.info("positioning "+Parameters.get(0).getAsString()+" relative to "+objecttopositionrelativeto+" at "+x+","+y+","+z);	
					String positionedRelativeToPoint="";
					//detect if a attachment point is specified
					if (objecttopositionrelativeto.contains("(")){
						Log.info("attachment point found");

						//get the attachment point name							
						String attachmentPointName = objecttopositionrelativeto.split("\\(")[1];

						//remove ending )
						attachmentPointName=attachmentPointName.substring(0, attachmentPointName.length()-1).trim();

						Log.info("attachment point found="+attachmentPointName);

						positionedRelativeToPoint = attachmentPointName;

						//Separate the object name from attachment point brackets
						objecttopositionrelativeto = objecttopositionrelativeto.split("\\(")[0];
					}

					//  
					//get the object to position relative too
					SceneObject postionTo = SceneObjectDatabase.getSingleSceneObjectNEW(objecttopositionrelativeto, callingObject, Parameters.get(1).searchGlobal);

					Log.info("positioning:"+objectsToPosition.size()+" objects rel to "+postionTo.getObjectsCurrentState().ObjectsName);
					Log.info("which is on scene "+postionTo.getObjectsCurrentState().ObjectsSceneName);

					// set their state
					for (SceneObject so : objectsToPosition) {

						Log.info("updating objects data");

						//update object
						so.getObjectsCurrentState().relX = x;
						so.getObjectsCurrentState().relY = y;
						so.getObjectsCurrentState().relZ = z;

						so.getObjectsCurrentState().positionedRelativeToo     = postionTo;
						so.getObjectsCurrentState().positionedRelativeToPoint = positionedRelativeToPoint;

						Log.info("positioning:"+so.getObjectsCurrentState().ObjectsName);					


						//associate with parent
						postionTo.addChild(so); //.relativeObjects.add(so);
						Log.info("testing objects are both in scene:"+postionTo.getParentScene().SceneFileName);

						Log.info(postionTo.getObjectsCurrentState().ObjectsName+" is attached:"+postionTo.getParentScene().isObjectInScene(postionTo));
						Log.info(so.getObjectsCurrentState().ObjectsName+" is attached:"+postionTo.getParentScene().isObjectInScene(so));

						so.updateRelativePosition(true);

						postionTo.updateThingsPositionedRelativeToThis(true);

						Log.info("updated objects relative positions");

					}

				}


			}


			/*
			if (CurrentParams.length >= 4) {

				Log.info("positioning relative");

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttomove               = CurrentParams[0];
				String objecttopositionrelativeto = CurrentParams[1];

				int x = getNumericalValue(CurrentParams[2],callingObject); //we used to use "process position", getNumericalValue function does the same, but offers more features
				int y = getNumericalValue(CurrentParams[3],callingObject); //too....like supporting equations to work out the resulting number! (objectx+50/3 or whatever) 
				int z = 0;
				if (CurrentParams.length == 5){
					z = getNumericalValue(CurrentParams[4],callingObject);					
				}

				String positionedRelativeToPoint="";

				//detect if a attachment point is specified
				if (objecttopositionrelativeto.contains("(")){
					Log.info("attachment point found");

					//get the attachment point name							
					String attachmentPointName = objecttopositionrelativeto.split("\\(")[1];

					//remove ending )
					attachmentPointName=attachmentPointName.substring(0, attachmentPointName.length()-1).trim();

					Log.info("attachment point found="+attachmentPointName);

					positionedRelativeToPoint = attachmentPointName;

					//Separate the object name from attachment point brackets
					objecttopositionrelativeto = objecttopositionrelativeto.split("\\(")[0];
				}


				// get the objects to position
				Set<? extends SceneObject> curobjects = SceneObjectDatabase
						.getSceneObjectNEW(objecttomove,callingObject,true);



				SceneObject postionTo = SceneObjectDatabase.getSingleSceneObjectNEW(objecttopositionrelativeto, callingObject, true);


				Log.info("positioning:"+curobjects.size()+" objects rel to "+postionTo.getObjectsCurrentState().ObjectsName);
				Log.info("which is on scene "+postionTo.getObjectsCurrentState().ObjectsSceneName);
				Log.info("Number of objects called "+objecttomove+":"+curobjects.size());

				//Log.info(curobjects[0].objectsCurrentState.ObjectsName+"is attached:"+postionTo.getScene().isObjectInScene(curobjects[0]));



				// set its state
				for (SceneObject so : curobjects) {


					Log.info("updating objects data");

					//update object
					so.getObjectsCurrentState().relX = x;
					so.getObjectsCurrentState().relY = y;
					so.getObjectsCurrentState().relZ = z;

					so.getObjectsCurrentState().positionedRelativeToo     = postionTo;
					so.getObjectsCurrentState().positionedRelativeToPoint = positionedRelativeToPoint;

					Log.info("positioning:"+so.getObjectsCurrentState().ObjectsName);					
					//widget.setPosition(x, y);

					//associate with parent
					postionTo.relativeObjects.add(so);
					Log.info("testing objects are both in scene:"+postionTo.getParentScene().SceneFileName);

					Log.info(postionTo.getObjectsCurrentState().ObjectsName+"is attached:"+postionTo.getParentScene().isObjectInScene(postionTo));
					Log.info(so.getObjectsCurrentState().ObjectsName+"is attached:"+postionTo.getParentScene().isObjectInScene(so));

					so.updateRelativePosition(true);

					postionTo.updateThingsPositionedRelativeToThis(true);

					Log.info("updated");

				}


				Log.info("positioning done:");


			} else if (CurrentParams.length == 3) {

				Log.info("positioning absolute (no z position supported here yet)");

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoposition = CurrentParams[0];
				int x = getNumericalValue(CurrentParams[1].trim(),callingObject); //we used to use "process position", getNumericalValue function does the same, but offers more features
				int y = getNumericalValue(CurrentParams[2].trim(),callingObject);
				int z = 0;
				//
				// get the objects to set
				//SceneObject[] curobjects = SceneWidget
				//		.getSceneObjectByName(objecttoposition,callingObject);

				Set<? extends SceneObject> curobjects = SceneObjectDatabase
						.getSceneObjectNEW(objecttoposition,callingObject,true);


				// set its state
				for (SceneObject curobject : curobjects) {

					//stop any movements
					curobject.StopCurrentMovement();

					//remove any relative settings
					if (curobject.getObjectsCurrentState().positionedRelativeToo != null){						
						curobject.getObjectsCurrentState().positionedRelativeToo.relativeObjects.remove(curobject);
					}					
					curobject.getObjectsCurrentState().positionedRelativeToo = null;
					curobject.getObjectsCurrentState().positionedRelativeToPoint = null;
					curobject.getObjectsCurrentState().linkedZindex  = false; //when we have no relative object, we cant have a linkedzindex either

					//set the position
					curobject.setPosition(x, y, z, true);

				}

			} else if (CurrentParams.length == 2) {

				String xpos = CurrentParams[0];
				String ypos = CurrentParams[1];

				int x = getNumericalValue(xpos,callingObject);//we used to use "process position", getNumericalValue function does the same, but offers more features
				int y = getNumericalValue(ypos,callingObject);
				int z = 0;

				callingObject.StopCurrentMovement();
				callingObject.setPosition(x, y,z,true);

			}
			 */


			break; 
			/**
			 * set the objects z co-ordinates without effecting its other co-ordinates or its relative status 
			 */
		case setobjectzposition: 
		{
			Log.info("setting the objects z position only");
			if (Parameters.getTotal()>1) {

				Set<? extends SceneObject> objects = Parameters.get(0).getAsObjects(callingObject);
				int zpos  =    Parameters.get(1).getAsInt();     
				for (SceneObject sceneObject : objects) {
					sceneObject.setZPositionOnly(zpos, true);
				}


			} else {
				int zpos  =    Parameters.get(0).getAsInt();  
				callingObject.setZPositionOnly(zpos, true);

			}


		}
		break;
		/** sets the object to the specified z-index **/
		case setobjectzindex:			
		{    
			Log.info("setobjectzindex: "+Parameters.toString());

			if (Parameters.getTotal()>1) {

				String objecttosetzindex = Parameters.get(0).getAsString();
				int zindex =    Parameters.get(1).getAsInt();                                //Integer.parseInt(zindexstring); 

				// get the object
				Set<? extends SceneObject> curobjectaot = SceneObjectDatabase
						.getSceneObjectNEW(objecttosetzindex,callingObject,Parameters.get(0).searchGlobal);

				if (curobjectaot==null || curobjectaot.size()==0){
					Log.warning("No objects found called: "+objecttosetzindex);
					return;
				}

				for (SceneObject sceneObject : curobjectaot) {

					Log.info("setobjectzindex on: "+sceneObject.getName());

					sceneObject.setVaribleZIndexOff();

					// set its state
					sceneObject.setZIndex(zindex);
				}

			} else {
				//	String zindexstring = theParameterString;
				int zindex = Parameters.get(0).getAsInt();                                //Integer.parseInt(zindexstring); 
				//Integer.parseInt(zindexstring); 
				callingObject.setVaribleZIndexOff();

				callingObject.setZIndex(zindex);
			}

			break;
		}
		case setvariablezindex:
		{

			//setVaribleZIndex
			if (Parameters.getTotal()>3) {

				String objecttosetzindex = Parameters.get(0).getAsString();
				int lower =    Parameters.get(1).getAsInt();                                //Integer.parseInt(zindexstring); 
				int upper =    Parameters.get(2).getAsInt();
				int step  =    Parameters.get(3).getAsInt();

				// get the object
				Set<? extends SceneObject> curobjectaot = SceneObjectDatabase
						.getSceneObjectNEW(objecttosetzindex,callingObject,Parameters.get(0).searchGlobal);

				if (curobjectaot==null || curobjectaot.size()==0){
					Log.warning("No objects found called: "+objecttosetzindex);
					return;
				}

				for (SceneObject sceneObject : curobjectaot) {
					// set its state
					sceneObject.setVaribleZIndex(lower,upper,step);
				}

			} else {
				int lower =    Parameters.get(0).getAsInt();                                //Integer.parseInt(zindexstring); 
				int upper =    Parameters.get(1).getAsInt();
				int step  =    Parameters.get(2).getAsInt();

				callingObject.setVaribleZIndex(lower,upper,step);
			}

			break;

		}
		case linkzindex:
		{
			//this command links one objects zindex to the object its positioned relative to
			// objectolink, (difference to add to z index
			// eg
			// playersprite, 5
			// would mean playersprite zindex is always 5 above whatever its positioned relative to

			Log.info("____________linking z index");

			Set<SceneObject> objectsToEffect = new HashSet<SceneObject>();
			objectsToEffect.add(callingObject);

			//String difference="";
			int difference = 0;

			if (Parameters.getTotal()==1) {				
				difference = Parameters.get(0).getAsInt();
			} 

			if (Parameters.getTotal()==2) {				

				String objectToEffectsName = Parameters.get(0).getAsString();

				objectsToEffect.clear(); //clear the default calling object to effect.				
				Set<? extends SceneObject> matchingObjects = SceneObjectDatabase.getSceneObjectNEW(objectToEffectsName,callingObject,true); 


				objectsToEffect.addAll(matchingObjects);
				//add the new objects to effect

				difference = Parameters.get(1).getAsInt();
			} 

			if ((objectsToEffect.size()==0)){
				Log.info("objectsToEffect is empty. No objects match request ");
			}

			Log.info("linking "+objectsToEffect.size()+" objects to their parents (things they are positioned relative to.) ");//+objectToEffect.objectsCurrentState.positionedRelativeToo);
			Log.info("z index difference  "+difference);


			for (SceneObject sceneObject : objectsToEffect) {

				sceneObject.setZIndexAsLinked(true,difference);



			}


		}

		break;
		case unlinkzindex:
		{



			if (Parameters.isEmpty()) {				

				Log.info("____________Unlinking z index on this object");
				callingObject.setZIndexAsLinked(false,0);

			}  else {

				Log.info("____________Unlinking z index");
				Set<? extends SceneObject> objectsToUnlink = Parameters.get(0).getAsObjects(callingObject);

				for (SceneObject sceneObject : objectsToUnlink) {
					sceneObject.setZIndexAsLinked(false,0);
				}

			}


		}

		break;
		case addobjectproperty:
		{


			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoaddpropertytoo = Parameters.get(0).getAsString();
				String state = Parameters.get(1).getAsString();



				Set<? extends SceneObject> matchingObjects = Parameters.get(0).getAsObjects(callingObject);


				if (matchingObjects==null || matchingObjects.isEmpty() ){
					Log.severe("No objects found for:"+state);					
					return;
				}

				// add property to all matching objects
				for (SceneObject so : matchingObjects) {
					so.addProperty(state,true,true);
				}

			} else {

				callingObject.addProperty(Parameters.get(0).getAsString(),true,true);
			}

		}
		break; 
		case addobjectpropertyforconditional: //same as addobjectproperty, but we dont process any propertyadded actions, or touchingchange actions of naboughing objects
		{


			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoaddpropertytoo = Parameters.get(0).getAsString();
				String state = Parameters.get(1).getAsString();
				Set<? extends SceneObject> matchingObjects = Parameters.get(0).getAsObjects(callingObject);


				if (matchingObjects==null  || matchingObjects.isEmpty()){
					Log.severe("No objects found for:"+state);
					
					return;
				}

				// add property to all matching objects
				for (SceneObject so : matchingObjects) {
					so.addProperty(state,false,false); //dont process events
				}

			} else {

				callingObject.addProperty(Parameters.get(0).getAsString(),false,false);//dont process events
			}

		}
		break; 
		case removeobjectproperty:
		{

			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoremoveproperty = Parameters.get(0).getAsString();
				String propertyToRemove = Parameters.get(1).getAsString();

				// get the objects
				Set<? extends SceneObject> matchingObjects =  Parameters.get(0).getAsObjects(callingObject);


				if (matchingObjects==null  || matchingObjects.isEmpty()){
					Log.severe("No objects found for:"+propertyToRemove);
					
					return;
				}

				// remove property from all matching objects
				for (SceneObject so : matchingObjects) {
					so.removeProperty(propertyToRemove,true,true);
				}

			} else {
				callingObject.removeProperty(Parameters.get(0).getAsString(),true,true);
			}
		}
		break; 
		case removeobjectpropertyforconditional://same as removeobjectproperty, but we dont process any propertyremoved actions, or touchingchange actions of naboughing objects
		{

			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoremoveproperty = Parameters.get(0).getAsString();
				String state = Parameters.get(1).getAsString();

				// get the objects
				Set<? extends SceneObject> matchingObjects =  Parameters.get(0).getAsObjects(callingObject);

				if (matchingObjects==null  || matchingObjects.isEmpty()){
					Log.severe("No objects found for:"+state);
					return;
				}

				// remove property from all matching objects
				for (SceneObject so : matchingObjects) {
					so.removeProperty(state,false,false);
				}

			} else {
				callingObject.removeProperty(Parameters.get(0).getAsString(),false,false);
			}
		}
		break; 
		case removeallobjectproperties:
		{

			if (Parameters.getTotal()>0) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttoremoveproperty = Parameters.get(0).parameterString;

				// get the objects
				Set<? extends SceneObject> matchingObjects =  Parameters.get(0).getAsObjects(callingObject);


				//SceneObjectDatabase
				//.getSceneObjectNEW(objecttoremoveproperty,callingObject,Parameters.get(0).searchGlobal); 

				if (matchingObjects==null  || matchingObjects.isEmpty()){
					return;
				}
				// remove all propertys for each object with that name
				for (SceneObject widget : matchingObjects) {
					widget.removeAllPropertys();
				}

			} else {
				callingObject.removeAllPropertys();
			}

		}
		break; 

		case swapobjectproperty:
		{

			if (Parameters.getTotal()>2) {

				// if there's 3 params, the first part is the object to apply it
				// too
				String objecttoswapprops = Parameters.get(0).getAsString();//   CurrentParams[0];
				String replaceThis =Parameters.get(1).getAsString();//  CurrentParams[1];
				String withThis = Parameters.get(2).getAsString().trim();// CurrentParams[2].trim();

				// get the objects
				//SceneObject[] curobjects = SceneWidget
				//		.getSceneObjectByName(objecttoswapprops,callingObject);				
				Set<? extends SceneObject> matchingObjects = Parameters.get(0).getAsObjects(callingObject);
				//SceneObjectDatabase
				//.getSceneObjectNEW(objecttoswapprops,callingObject,Parameters.get(0).searchGlobal); 

				// set its state
				for (SceneObject widget : matchingObjects) {
					widget.removeProperty(replaceThis,true,true);
					widget.addProperty(withThis,true,true);
				}

			} else {

				String replaceThis = Parameters.get(0).getAsString();//CurrentParams[0];
				String withThis = Parameters.get(1).getAsString();//CurrentParams[1];

				callingObject.removeProperty(replaceThis,true,true);
				callingObject.addProperty(withThis,true,true);

			}

		}
		break; 
		case saveobjectstate:
		{
			// in future maybe support a coma separated list?
			if (Parameters.getTotal()>0) {

				// if there's a coma, the first part is the object to apply it
				// too
				String objecttosavestate = theParameterString.trim();

				// get the object
				//SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(objecttosavestate,callingObject)[0];
				Set<? extends SceneObject> curobject = SceneObjectDatabase
						.getSceneObjectNEW(objecttosavestate,callingObject,true); 

				// set its state
				for (SceneObject sceneObject : curobject) {
					sceneObject.saveTempState();
				}


			} else {
				callingObject.saveTempState();
			}

		}
		break; 
		case restoreobjectstate:
		{
			// in future maybe support a comma separated list?
			if (Parameters.getTotal()>0) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttorestore = theParameterString.trim();

				// get the object
				//	SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(objecttorestore,callingObject)[0];

				Set<? extends SceneObject> curobject = SceneObjectDatabase
						.getSceneObjectNEW(objecttorestore,callingObject,true); 

				// set their states
				for (SceneObject sceneObject : curobject) {
					sceneObject.restoreTempState();

				}



			} else {
				callingObject.restoreTempState();
			}
		}
		break; 
		case gotoobjectmovement:
		{
			if (Parameters.getTotal()>1) {

				// if there's a coma, the first part is the object to apply it
				// too
				String objecttogotomovement = Parameters.get(0).getAsString();// CurrentParams[0];
				String movementsname        = Parameters.get(1).getAsString(); //CurrentParams[1];

				int duration = 5000;
				int startfrom = -1; // default

				if (Parameters.getTotal() >= 3) {

					duration = Parameters.get(2).getAsInt();
					//Integer.parseInt(CurrentParams[2]);
				}

				if (Parameters.getTotal() == 4) {

					startfrom = Parameters.get(3).getAsInt()-1;
					//Integer.parseInt(CurrentParams[3]) - 1;
					Log.info(" start from=" + startfrom);
				}

				// get the objects
				Set<? extends SceneObject> curobjects = Parameters.get(0).getAsObjects(callingObject);
				//SceneObjectDatabase
				//.getSceneObjectNEW(objecttogotomovement,callingObject,true);



				// play movements on all found objects
				for (SceneObject so : curobjects) {
					so.playMovement(movementsname, duration,  startfrom,true);
				}



			} else {
				// note: in future this has to be much more complex to set
				// movements speed/update interval

				callingObject.playMovement(theParameterString, 5000,-1, true);

			}
		}
		break; 
		case chuckobjectat:
		{
			String ChuckThis = Parameters.get(0).getAsString();
			String FromThis  = Parameters.get(1).getAsString();
			String AtThis 	 = Parameters.get(2).getAsString();

			Set<? extends SceneObject> thingsToChuck = SceneObjectDatabase
					.getSceneObjectNEW(ChuckThis,callingObject,Parameters.get(0).searchGlobal); 


			SceneObject locObject = SceneObjectDatabase
					.getSingleSceneObjectNEW(FromThis,callingObject,Parameters.get(1).searchGlobal); 



			SceneObject tarobject = SceneObjectDatabase
					.getSingleSceneObjectNEW(AtThis,callingObject,Parameters.get(2).searchGlobal); 


			for (SceneObject curobject : thingsToChuck) {

				curobject.chuckObject(locObject,tarobject);
			}

		}


		break; 

		case chuckobjectintoinventory:
		{
			//Experimental function to chuck something into the inventory

			String ChuckThisObject = Parameters.get(0).getAsString();// CurrentParams[0];
			String ToThisInventory = Parameters.get(1).getAsString(); //CurrentParams[1];
			//Integer fallDownBy =  Integer.parseInt(CurrentParams[3]);


		
			
			//new method to get objects (at some point once chuck is functional on SceneObject rather then just sprites this
			//and the rest of the function will need to be changed.
			Set<? extends SceneObject> ObjectToChuckAtInventory = Parameters.get(0).getAsObjects(callingObject);

			
			
			//SceneObjectDatabase
			//.getSceneObjectNEW(ChuckThisObject,callingObject,true);

			if (ObjectToChuckAtInventory==null){
				return;
			}

			SceneObject chuckthis = null;

			//select the first non-inventory item type 
			//we need to do this now as inventoryitems are sprites now
			for (SceneObject sso : ObjectToChuckAtInventory) {

				if (sso.getObjectsCurrentState().getPrimaryObjectType() != SceneObjectType.InventoryObject){
					//if (sso.getClass() != InventoryIcon.class){
					chuckthis = sso;

					Log.info("got sprite to chuck "+sso.getObjectsCurrentState().ObjectsFileName);

				}

			}


			Log.info("chucking into inventory named:"+ToThisInventory);
			chuckthis.chuckIntoInventory(ToThisInventory);

		}

		break; 	
		case setobjectmovement:
		{
			if (Parameters.getTotal()>1) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objecttosetmovement = Parameters.get(0).getAsString();
				String movementsname       = Parameters.get(1).getAsString();

				int duration  = 5000;
				int startfrom = -1; // default

				if (Parameters.getTotal() >= 3) {

					duration = Parameters.get(2).getAsInt();

				}

				if (Parameters.getTotal() == 4) {

					startfrom = Parameters.get(3).getAsInt()-1;//Integer.parseInt(CurrentParams[3]) - 1;
					Log.info(" start from=" + startfrom);
				}

				//Get the objects
				Set<? extends SceneObject> objectsToSetMovementOn =Parameters.get(0).getAsObjects(callingObject);


				if (objectsToSetMovementOn==null){
					Log.severe("No objects found called:"+objecttosetmovement+" did you remember that set movement needs a specific object set? ");

				}

				// set their movement
				for (SceneObject curobjecttosetm : objectsToSetMovementOn) {

					curobjecttosetm.playMovement(movementsname, duration,  startfrom,
							false);
				}


			} else {

				// note: in future this has to be much more complex to set
				// movements speed			
				if (!Parameters.get(0).isNumber()) {
					//if the only parameter is a name we assume thats the movement to play
					//and default to 5 seconds
					//	callingObject.playMovement(theParameterString, Parameters.get(0).getAsInt());
					callingObject.playMovement(Parameters.get(0).getAsString(), 5000);


				} else {
					//error
				}




			}
		}
		break; 

		case stopobjectmovement: {


			if (Parameters.getTotal()>0) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objectstostopmovementstring = CurrentParams[0];

				Set<? extends SceneObject> objectsToStopMovementOn = SceneObjectDatabase
						.getSceneObjectNEW(objectstostopmovementstring,callingObject,true);

				// stops their movement
				for (SceneObject curobjecttostopm : objectsToStopMovementOn) {

					curobjecttostopm.StopCurrentMovement();
				}


			} else {
				callingObject.StopCurrentMovement();
			}

		}	
		break;

		case previousparagraph:
		{
			if (theParameterString.length() > 0) {

				//Note; once SceneObjectDatabase returns IsSceneLabelObject rather then SceneLabelObjects
				//we no longer needs this weird "? extends" generics 

				// get the object
				//(we will need a dialogue specific method in SceneObjectDatabase eventually)				
				Set<IsSceneDialogueObject> curobjectpp = SceneObjectDatabase.getDialogueObjectNEW(theParameterString,callingObject,true);
				//

				// set their paragraph
				for (IsSceneDialogueObject textobject : curobjectpp) {

					//TEMP: For now we just cast to dialog, this will flag as errors if it isnt
					((IsSceneDialogueObject)textobject).previousParagraph();

				}

			} else {

				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.DialogBox)){
					((IsSceneDialogueObject)callingObject).previousParagraph();
				} else {
					Log.info("no object specified for setting dialog previousParagraph, and calling object is not dialogbox");

				}

			}

			break; 
		}
		case currentparagraph:
		{
			//TODO: change to new Parameters system
			if (Parameters.getTotal()==3) {

				String objecttosetpara =  Parameters.get(0).getAsString().trim(); //CurrentParams[0].trim();
				String paragraphName   =  Parameters.get(1).getAsString().trim(); //CurrentParams[1].trim();
				String value 	       =  Parameters.get(2).getAsString().trim(); // CurrentParams[2].trim();

				int valuepara = Integer.parseInt(value);

				// get the object
				Set<IsSceneDialogueObject> curobjectpp = SceneObjectDatabase.getDialogueObjectNEW(objecttosetpara,callingObject,true);

				if (curobjectpp!=null){				

					Log.info("changing paragraph # in this many objects:"+curobjectpp.size());
					// set its state
					for (IsSceneDialogueObject textobject : curobjectpp) {

						((IsSceneDialogueObject)textobject).setParagraphName(paragraphName, true);
						((IsSceneDialogueObject)textobject).setParagraph(valuepara);					

					}
				} else {
					Log.info("no matching objects found:"+objecttosetpara);

				}
			} else {
				// its just a state, so we assume its the calling object if thats compatible
				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.DialogBox)){

					String name  = Parameters.get(0).getAsString().trim(); // CurrentParams[0].trim();
					String value = Parameters.get(1).getAsString().trim(); // CurrentParams[1].trim();

					
					int valuepara = Integer.parseInt(value);

					((IsSceneDialogueObject)callingObject).setParagraphName(name, true);
					((IsSceneDialogueObject)callingObject).setParagraph(valuepara);

				} else {
					Log.warning("no object specified for setting dialog currentparagraph, and calling object is not dialogbox");
				}




			}

		}
		break; 
		case setparagraph:

			if (theParameterString.contains(",")) {

				String objecttosetpara = Parameters.get(0).getAsString().trim(); //CurrentParams[0].trim();
				String name = Parameters.get(1).getAsString().trim();// CurrentParams[1].trim();


				Set<IsSceneDialogueObject> curobjectpp = SceneObjectDatabase.getDialogueObjectNEW(objecttosetpara,callingObject,true);

				Log.info("setting paragraph name in this many objects:"+curobjectpp.size());

				Boolean autorun = true;

				// set its state
				if (Parameters.getTotal()>=3){

					String autorunstring = Parameters.get(2).getAsString().trim(); //TODO: change to get as boolean
					//	CurrentParams[2].trim();

					Boolean arb = Boolean.parseBoolean(autorunstring);
					Log.info("setting paragraph on "+objecttosetpara+" to "+name+" with autorun set to "+autorun);

					autorun = arb;
					//curobjectt.setParagraphName(name,arb);
				} else {
					Log.info("setting paragraph with autorun on because it was not specified to false");
					//curobjectt.setParagraphName(name,true);
				}
				//set all objects in the desired paragraph name
				for (IsSceneDialogueObject textobject : curobjectpp) {

					Log.info("setting paragraph on"+textobject.getObjectsCurrentState().ObjectsName+" to "+name);
					textobject.setParagraphName(name,autorun);
					textobject.ObjectsLog("(setParagraphName ran:"+name+","+autorun+")");
				}

			} else {


				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.DialogBox)){

					// its just a state, so we assume its the last object clicked on
					((IsSceneDialogueObject)callingObject).setParagraphName(theParameterString.trim(),false);
					((IsSceneDialogueObject)callingObject).ObjectsLog("(setParagraphName ran:"+theParameterString+")");

				} else {

					Log.info("no object specified for setting dialog setparagraph, and calling object is not dialogbox");


				}
			}

			break; 
		case settextcss:
		{
			if (theParameterString.contains(",")) {

				String objectaddtextcss = CurrentParams[0];
				String state = CurrentParams[1];

				// get the object
				Set<? extends IsSceneLabelObject> curobjecttoget = SceneObjectDatabase.getTextObjectNEW(objectaddtextcss,callingObject,true);

				// set its state
				for (IsSceneLabelObject textobject : curobjecttoget) {

					textobject.setTextCSS(state);

				}



			} else {
				// its just a state, so we assume its the last object clicked on
				Log.info("setting text css");

				lastTextObjectCalled.setTextCSS(theParameterString);

				Log.info("setting css on:"
						+ lastTextObjectCalled.getObjectsCurrentState().ObjectsName);
			}
		}
		break;
		case addtextcss:
		{
			if (theParameterString.contains(",")) {

				String objectaddtextcss = CurrentParams[0];
				String state = CurrentParams[1];

				// get the object
				Set<? extends IsSceneLabelObject> curobjecttoget = SceneObjectDatabase.getTextObjectNEW(objectaddtextcss,callingObject,true);

				if (curobjecttoget!=null){
					// set its state
					for (IsSceneLabelObject textobject : curobjecttoget) {

						textobject.addTextCSS(state);

					}
				} else {
					Log.warning("No object found to add CSS too:"+objectaddtextcss);

				}


			} else {
				// its just a state, so we assume its the last object clicked on
				Log.info("adding css");

				lastTextObjectCalled.addTextCSS(theParameterString);

				Log.info("added css to:"
						+ lastTextObjectCalled.getObjectsCurrentState().ObjectsName);
			}
		}
		break; 
		case removetextcss:
		{

			if (theParameterString.contains(",")) {

				String objectrt = CurrentParams[0];
				String state = CurrentParams[1];
				// get the object
				Set<? extends IsSceneLabelObject> curobjectrt = SceneObjectDatabase
						.getTextObjectNEW(objectrt,callingObject,true);


				if (curobjectrt!=null){
					// set its state
					for (IsSceneLabelObject textobject : curobjectrt) {

						textobject.removeTextCSS(state);

					}
				} else {
					Log.warning("No object found to remove CSS from:"+objectrt);

				}

			} else {
				// its just a state, so we assume its the last object clicked on
				lastTextObjectCalled.removeTextCSS(theParameterString);
			}

		}
		break; 
		case nextparagraph:
		{
			if (theParameterString.length() > 0) {

				// get the objects
				Set<IsSceneDialogueObject> curobjectnp = SceneObjectDatabase.getDialogueObjectNEW(theParameterString,callingObject,true);


				// set all their paragraphs to the next paragraph
				//curobjectnp.nextParagraph();
				for (IsSceneDialogueObject textobject : curobjectnp) {

					Log.info("dialogue object to change to next paragraph:"+textobject.getObjectsCurrentState().ObjectsName);
					textobject.nextParagraph();

				}


			} else {


				if (callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.DialogBox)){
					((IsSceneDialogueObject)callingObject).nextParagraph();
					callingObject.ObjectsLog("(next paragraph run)");
				} else {
					Log.info("no object specified for setting dialog nextParagraph, and calling object is not dialogbox");					
				}


				// its just a state, so we assume its the last object clicked on
				//Log.info("Dialogue object to change to next paragraph:"+lastTextObjectCalled.getObjectsCurrentState().ObjectsName);	
				//((SceneDialogObject)lastTextObjectCalled).nextParagraph();
				//lastTextObjectCalled.ObjectsLog("(next paragraph run)");
			}
		}
		break; 
		case setobjectframegap:

			//framegap(theParameterString, callingObject, lastSpriteObjectCalled,
			//		CurrentParams);

			framegap(callingObject, lastSpriteObjectCalled,	Parameters);


			//
			break; 

		case setframegap: 	

			framegap(callingObject, lastSpriteObjectCalled,	Parameters);

			break; 
		case subtractobjectvariable:


			//get the object name, if its specified as a param
			if (CurrentParams.length==3) {

				String objectaov = CurrentParams[0];
				String name      = CurrentParams[1];
				//String value = CurrentParams[2].trim();

				//String value     = getValue(CurrentParams[2].trim(),callingObject);

				String value     = CurrentParams[2];

				Set<? extends SceneObject> curobjectaov = SceneObjectDatabase
						.getSceneObjectNEW(objectaov,callingObject,true);

				for (SceneObject sceneObject : curobjectaov) {

					sceneObject.subtractkFromVariable(name, value);
				}


			} else {

				//else we assume we want to use the calling object
				String name = CurrentParams[0];
				// value = CurrentParams[1];
				//String value     = getValue(CurrentParams[1].trim(),callingObject);

				String value     = CurrentParams[1];

				//add to the objects internal variable
				callingObject.subtractkFromVariable(name,value);



			}

			break; 	
		case addobjectvariable:
		{

			//get the object name, if its specified as a param
			if (CurrentParams.length==3) {
				//TODO: change to new Parameters system

				String objectaov = CurrentParams[0];
				String name      = CurrentParams[1];

				String value     = CurrentParams[2].trim();

				//	String value     = getValue(CurrentParams[2].trim(),callingObject);



				Set<? extends SceneObject> curobjectaov = SceneObjectDatabase
						.getSceneObjectNEW(objectaov,callingObject,true);


				for (SceneObject sceneObject : curobjectaov) {

					sceneObject.addToVariable(name, value);
				}


			} else {

				//else we assume we want to use the calling object
				String name = CurrentParams[0];
				String value = CurrentParams[1];
				//	String value     = getValue(CurrentParams[1].trim(),callingObject);

				//add to the objects internal variable
				callingObject.addToVariable(name,value);


			}
		}
		break; 
		case setobjectvariable:
		{
			Log.info("setting variable :"+theParameterString);

			//get the object name, if any
			if (Parameters.getTotal()==3){
				//if (CurrentParams.length==3) {

				Log.info("dividing params");

				String objectsov = Parameters.get(0).getAsString();//   CurrentParams[0];
				String name =   Parameters.get(1).getAsString(); // CurrentParams[1];
				//	String value = CurrentParams[2].trim();
				String value = getValue(CurrentParams[2].trim(),callingObject); //TODO: change to new parameter system

				Log.info("getting object:"+objectsov);
				// get the object
				//	SceneObject curobjectsov[] = SceneWidget
				//		.getSceneObjectByName(objectsov,callingObject);

				Set<? extends SceneObject> curobjectsov = SceneObjectDatabase
						.getSceneObjectNEW(objectsov,callingObject,true);

				Log.info("setting variable "+name+" to "+value+" on object[s] "+objectsov);

				for (SceneObject sceneObject : curobjectsov) {

					sceneObject.setVariable(name,value);
				}


			} else {



				String name = CurrentParams[0];
				//String value = CurrentParams[1];

				String value     = getValue(CurrentParams[1].trim(),callingObject);
				Log.info("setting variable on last object : "+name+" to "+value);
				if (callingObject==null){
					Log.info("__________________________null ");
				}
				callingObject.setVariable(name,value);


			}
		}
		break; 
		case setobjectstate:

			changeanimationstate(theParameterString, callingObject,
					lastSpriteObjectCalled, CurrentParams);

			break;
		case setanimationmode: 
		{

			changeanimationstate(theParameterString, callingObject,
					lastSpriteObjectCalled, CurrentParams);

			break;
		}

		case scrollscenetoposition: 		
		{

			//Parameters have already been processed for variables so we can now just getInt directly;
			int Xpos =  Parameters.get(0).getAsInt();
			int Ypos =  Parameters.get(1).getAsInt();	

			int duration = 0;

			//optional speed
			if (Parameters.getTotal() ==3){
				//	String Speeds = CurrentParams[2]; //get the speed requested
				//	Speed = Integer.parseInt(getValue(Speeds,callingObject));	//convert the speed string to a int				
				//	Speed = Parameters.get(2).getAsInt();
				duration = Parameters.get(2).getAsInt();

			} else {
				//when duration isn't specified we need to work out duration from speed and distance
				double Speed     = (75.0/30.0); //default speed was 75pixels per update at 30 frames per second
				int StartX = SceneObjectDatabase.currentScene.getPosX();
				int StartY = SceneObjectDatabase.currentScene.getPosY();
				Log.info("___________StartX:" + StartX);
				Log.info("___________StartY:" + StartY);

				int distanceX = Xpos-StartX;
				int distanceY = Ypos-StartY;
				Log.info("___________distancex:" + Xpos+"-"+StartX);
				Log.info("___________distancex:" + Ypos+"-"+StartY);

				double distance = Math.hypot(distanceX, distanceY); //distance in pixels
				Log.info("___________distance:" + distance+"ms");

				duration = (int) (distance/Speed);



			}

			//	int Xpos = Integer.parseInt(getValue(Xs,callingObject));
			//	int Ypos = Integer.parseInt(getValue(Ys,callingObject));

			Log.info("____________scrolling scene position to:" + Xpos + "," + Ypos+" over "+duration+"ms");

			if (SceneObjectDatabase.currentScene == null) {
				Log.info(" currentScene is null");
			}

			SceneObjectDatabase.currentScene.scrollViewToPosition(Xpos, Ypos,duration,true); //used to use Speed. But using speed is bad.


			break; 	
		}

		case setsceneshake:
		{

			int duration = 0;
			int distance = 0;

			if (Parameters.getTotal() == 2) {
				duration = Parameters.get(0).getAsInt();  //Integer.parseInt(CurrentParams[0]);
				distance = Parameters.get(1).getAsInt(); // Integer.parseInt(CurrentParams[1]);						
			} else {
				Log.info("wrong number of params, assuming defualt shake");	
				duration = 2000;
				distance = 50;					
			}


			Log.info(" setting shake to:"+distance+" for "+duration);	
			SceneObjectDatabase.currentScene.shakeView(duration, distance);
			break;

		}	
		case setsceneposition: 		
		{

			if (Parameters.getTotal() == 2) {
				// too
				//String Xs = CurrentParams[0];
				//String Ys = CurrentParams[1];

				//getNumericalValue or getValue should not be needed anymore - we parse globally for variables and formulas in the property string now before it reaches this point

				//int Xpos = Integer.parseInt(Xs);
				//int Ypos = Integer.parseInt(Ys);

				int Xpos = Parameters.get(0).getAsInt();
				int Ypos = Parameters.get(1).getAsInt();

				Log.info("____________setting scene position to:" + Xpos + "," + Ypos);

				if (SceneObjectDatabase.currentScene == null) {
					Log.info(" currentScene is null");
				}

				SceneObjectDatabase.currentScene.setViewPosition(Xpos, Ypos,true);
				break; 
				//TODO: change to new Parameters system

			} else if (CurrentParams.length == 3) {
				//	String Scene = CurrentParams[0];
				//String Xs = CurrentParams[1];
				//	String Ys = CurrentParams[2];

				String Scene = Parameters.get(0).getAsString();


				//	int Xpos = (getNumericalValue(Xs,callingObject));
				//	int Ypos = (getNumericalValue(Ys,callingObject));

				int Xpos = Parameters.get(1).getAsInt();
				int Ypos = Parameters.get(2).getAsInt();


				SceneWidget PickScene = SceneWidget.getSceneByName(Scene);

				Log.info("____________setting scene position to:" + Xpos + "," + Ypos);

				if (PickScene == null) {
					Log.info(" currentScene is null");
				}

				PickScene.setViewPosition(Xpos, Ypos,true);
				break; 

			} else {

				Log.warning("SetScenePosition has an invalid number of parameters. Pick 2 for current scene ~ X,Y ~ and 3 if you wish to change the position of a different scene ~ Scene,X,Y");
			} 



		}
		case setscenebackground:

			Log.info("SetSceneBackground");

			if (SceneObjectDatabase.currentScene == null) {
				Log.info(" currentScene is null");
			}

			SceneObjectDatabase.currentScene.setBackgroundUrl(theParameterString);

			break; 
		case loadobjectdata:
		{
			//TODO: refractor this to a JamSaveManager function
			Log.info("Loading Object Data");
			//only one parameter as it contains the object within it anyway
			String data = Parameters.getRawParameterString().trim(); //we dont use any variables or randoms, just the raw string

			int startpoint = data.indexOf("<object>",0); 
			int endpoint   = data.indexOf("<\\object>",startpoint); 

			//if start or end isn't present then exit
			if (startpoint==-1 || endpoint==-1){
				Log.info("Object data not correctly formatted. The serialised string should be between <Object> tags");
				break;
			}
			String objectData = data.substring(startpoint+8, endpoint); //trim the xml style start and end tags 			

			Log.info("setting objects data too:\n"+objectData);

			//create the correct state based on type of objectdata supplied
			SceneObjectState state = JamSaveGameManager.createObjectStateFromSeralisedString(objectData);

			Log.info("setting objects data too:\n"+state.serialiseToString());

			//get object from its name
			String objectsName = state.ObjectsName;

			Set<? extends SceneObject> objectsFound = SceneObjectDatabase.getSceneObjectNEW(objectsName,callingObject,true);

			if (objectsFound==null){
				Log.info("Object not found when loading state of "+objectsName+" thus adding to pending list");
				//TODO: Object not loaded when loading state method
				//Some method to wait and recheck? But how will we know what to wait for?
				//Possibly maintain a list of "pending states to load" with their object name, type, and scene.
				//Then use the database to load a state automatically once a object has been created

				//Experimental pending list
				JamSaveGameManager.pendingStatesToLoad.put( state.ObjectsName, state);
				return;
			}

			if (objectsFound.size()>1){
				Log.warning("Multiple Objects found when loading state of "+objectsName);
			}

			objectsFound.iterator().next().updateGeneralObjectState(state);

		}
		break;
		case loadscenedata:


			Log.info("Loading scene data");


			//we trigger the long complicated deserialisation process
			//which converts the string to all the information for
			//all the scenes and all the object states within them
			//it then loads those states
			//	SaveGameManager.deseraliseAndRestoreStateFromString(CurrentProperty);

			JAMcore.deseraliseAndRestoreStateFromString(Parameters.getRawParameterString());//theParameters);

			//NOTE: The above will cause a server error if the correct end marker isnt found on the supplied string
			//the end marker is to ensure the string is fully present and not cropped of by a previous problem
			//

			break; 
		case loadscenestate:


			Log.info("LoadSceneState currentScene");

			if (SceneObjectDatabase.currentScene == null) {
				Log.info(" currentScene is null");
			}

			SceneObjectDatabase.currentScene.loadSceneTempState();

			break; 
		case resumetimerstatesafterload:

			//function for save loading only, should not be used in gameplay
			Log.info("resumetimerstatesafterload");
			SceneWidget.setActionSetDataToResume(Parameters.getRawParameterString()); //just the raw params, no processing!
			//---------------------------

			break;
		case savegame:			
		{
			Log.info("opening save game box");
			//SaveGameManager.display(); 
			RequiredImplementations.openSavePanel();

			break;
		}
		case autosavegame:			
		{
			Log.info("updating autosave");
			JamSaveGameManager.autoSaveCurrentGameSilently();

			break;
		}
		case savescenestate:

			Log.warning("savescenestate not supported. You may, however, use savegame");
			//TODO: check instruction processors save/restore scene functions to see how they work
			//...


			//Log.info("SaveSceneState currentScene");

			//if (InstructionProcessor.currentScene == null) {
			//	Log.info(" currentScene is null");
			//}

			//InstructionProcessor.currentScene.saveSceneState();

			break; 
		case setcurrentscenescroll:


			Log.info("setting SceneScroll");

			if (SceneObjectDatabase.currentScene == null) {
				Log.info(" currentScene is null");
			}

			String scrollX = CurrentParams[0];
			String scrollY = CurrentParams[1];

			boolean xMovement = Boolean.parseBoolean(scrollX);
			boolean yMovement = Boolean.parseBoolean(scrollY);

			SceneObjectDatabase.currentScene.setScroll(xMovement, yMovement); //was untill recently the other way around


			break;
		case cleargamedata:

			//THIS clears all the games data
			GameManagementClass.clearAllGameData();

			break;
		case resetscene:

			if (theParameterString.length()>1) {

				//get scene name
				String sceneName = CurrentParams[0].trim();
				SceneWidget sceneToReset = SceneWidget.getSceneByName(sceneName);
				sceneToReset.resetScene();

			} else {
				Log.info("resetting currentScene");

				if (SceneObjectDatabase.currentScene == null) {
					Log.info(" currentScene is null");
					return;
				}
				SceneObjectDatabase.currentScene.resetScene();
			}



			break; 
		case resetobject:

			if (theParameterString.length() > 1) {


				Set<? extends SceneObject> curobjectsov = SceneObjectDatabase
						.getSceneObjectNEW(theParameterString,callingObject,true);

				for (SceneObject sceneObject : curobjectsov) {
					// set its state
					sceneObject.resetObject();
				}


			} else {
				// its just a state, so we assume its the last object clicked on
				callingObject.resetObject();
			}

			break; 
		case fadeout:
		{
			if (Parameters.getTotal() > 1) {

				String objectfo =  Parameters.get(0).getAsString();
				String fadetime =  Parameters.get(1).getAsString();

				// get the object

				Set<? extends SceneObject> curobjectfo = SceneObjectDatabase.getSceneObjectNEW(objectfo,callingObject,true);

				int time = Integer.parseInt(fadetime);

				if (curobjectfo.size()==0){
					Log.warning("NO OBJECTS FOUND TO FADE OUT:"+objectfo);					
				}

				// trigger fadeout
				for (SceneObject sceneObject : curobjectfo) {
					sceneObject.fadeOut(time,null,true); //last param means resume from current opacity
				}

			} else {

				int time = Integer.parseInt(theParameterString);

				// trigger fadein
				callingObject.fadeOut(time,null,true);//last param means resume from current opacity

			}
		}
		break; 
		case fadein:
		{

			if (Parameters.getTotal() > 1) {

				String objectfi = Parameters.get(0).getAsString();
				String fadetime = Parameters.get(1).getAsString();

				// get the objects to fade in
				Set<? extends SceneObject> objects_to_fade = SceneObjectDatabase.getSceneObjectNEW(objectfi,callingObject,true);

				int time = Integer.parseInt(fadetime);

				if (objects_to_fade.size()==0){
					Log.warning("NO OBJECTS FOUND TO FADE IN:"+objectfi);					
				}


				// trigger fadein
				for (SceneObject sceneObject : objects_to_fade) {
					sceneObject.fadeIn(time,null,true); //last param means resume from current opacity
				}

			} else {

				int time = Integer.parseInt(theParameterString);

				// trigger fadein
				callingObject.fadeIn(time,null,true); //last param means resume from current opacity

			}
		}
		break; 
		case setobjectopacity: 
		{
			//--------------
			Log.info("setting objectsopacity");

			if (Parameters.getTotal() == 1 ){
				//assume calling object
				callingObject.setObjectOpacity(Parameters.get(0).getAsDouble());

			} else {

				String ObjectName = Parameters.get(0).getAsString();				
				double Opacity    = Parameters.get(1).getAsDouble();

				//get all the objects to remove
				Set<? extends SceneObject> objectsToSet = SceneObjectDatabase.getSceneObjectNEW(ObjectName,callingObject,Parameters.get(0).searchGlobal);

				if (objectsToSet==null || objectsToSet.size()==0){
					Log.warning("NO OBJECTS FOUND TO set opacity of");	
					return;
				}

				for (SceneObject co : objectsToSet) {

					co.setObjectOpacity(Opacity);

				}

			}		
			//-------------------
		}
		break;
		case objectvisible:
		{
			Log.info("setting objectvisible");

			if (Parameters.getTotal()==2) {

				String objectov = Parameters.get(0).getAsString();
				String state    = Parameters.get(1).getAsString();

				// get the objects to change
				Set<? extends SceneObject> curobjectaot = SceneObjectDatabase
						.getSceneObjectNEW(objectov,callingObject,true);

				if (curobjectaot==null || curobjectaot.size()==0){
					Log.warning("NO OBJECTS FOUND TO SET VISIBLE:"+objectov);	
					return;
				}

				for (SceneObject co : curobjectaot) {

					// set its state
					if (state.equalsIgnoreCase("false")) {

						co.setVisible(false);
					} else {

						co.setVisible(true);
					}

				}

			} else {

				// set its state
				if (theParameterString.equalsIgnoreCase("false")) {

					callingObject.setVisible(false);

				} else {

					callingObject.setVisible(true);

				}

			}
		}
		break;
		case removeobject:
		{
			//

			if (Parameters.getTotal() == 0 ){
				//assume calling object
				Log.info("Now removing "+ callingObject.getName()); //temp, change log back to info level
				callingObject.removeObject();

			} else {

				String ObjectName = Parameters.get(0).getAsString();

				Log.info("ObjectName: "+ ObjectName);

				//get all the objects to remove
				Set<? extends SceneObject> curobjectro = SceneObjectDatabase.getSceneObjectNEW(ObjectName,callingObject,Parameters.get(0).searchGlobal);

				if (curobjectro==null || curobjectro.size()==0){
					Log.warning("NO OBJECTS FOUND TO remove:"+ObjectName);	
					return;
				}

				HashSet<SceneObject> tempcopy = new HashSet<SceneObject>(curobjectro);
				//loop over and remove them
				for (SceneObject co : tempcopy) {

					co.removeObject();

				}

			}
		}
		break; 
		case runnamedcommandsafter:

			// in future maybe support a comma separated list?

			if (Parameters.getTotal()>=3) {
				
				// if there's 3 bits, the first part is the object to apply it
				// too
				final String objectnamestring =  Parameters.get(0).getAsString();// CurrentParams[0].trim();
				final String name             =  Parameters.get(1).getAsString();// CurrentParams[1].trim();
				final String after            =  Parameters.get(2).getAsString();// CurrentParams[2].trim();

				boolean overwritePreviousAction = false;

				if (Parameters.getTotal()==4){					
					overwritePreviousAction = Boolean.parseBoolean(Parameters.get(3).getAsString().trim());
				}

				final int activateAfter = Integer.parseInt(after);


				if (objectnamestring.equals("<GLOBAL>")){

					NamedActionSetTimer.triggerGlobalRunNamedActionSetAfter(name, activateAfter,callingObject,overwritePreviousAction);

				}  else {

					// get the object or objects specified by the name supplied


					NamedActionSetTimer.triggerRunNamedActionSetAfter(callingObject,objectnamestring, name, activateAfter,overwritePreviousAction);


				}
			}

			if (Parameters.getTotal()==2) {

				final String name  = Parameters.get(0).getAsString().trim();   //CurrentParams[0].trim();
				final String after = Parameters.get(1).getAsString().trim();  //CurrentParams[1].trim();

				final int activateAfter = Integer.parseInt(after);

				//just use the calling object
				//SceneObject[] curobjects = new SceneObject[]{callingObject};
				Set<SceneObject> curobjects = new HashSet<SceneObject>();
				curobjects.add(callingObject);
				NamedActionSetTimer.triggerRunNamedActionSetAfter(curobjects, name, activateAfter,false);
			}



			break;
		case stopnamedcommands:


			if (Parameters.getTotal()>1) {
			//if (theParameterString.contains(",")) {

				// if there's a comma, the first part is the source object the commands come from
				final String objectsnc =  Parameters.get(0).getAsString();// CurrentParams[0];
				final String name      =  Parameters.get(1).getAsString();//CurrentParams[1];

				cancelNamedCommands(objectsnc,name,callingObject);

			}
			break; 
		case runnamedcommandsevery:
		{

			// object,actionsetname,500
			// Object,actionsetname,500,true
			// actionsetname,500,true  <-- can impl this as its ambigious with the first set

			if (Parameters.getTotal()>2) {

				// if there's a comma, the first part is the object to apply it
				// too
				final String objectrnce = CurrentParams[0];      //object name containing the named commends
				final String name = CurrentParams[1];            //the name of the named commands
				final String interval = CurrentParams[2].trim(); //run them every this many milliseconds

				int activateEveryLow = 3000;
				int activateEveryHigh = activateEveryLow;

				if (interval.contains("-")){

					//its a random range
					activateEveryLow  = Integer.parseInt(interval.split("-")[0]);
					activateEveryHigh = Integer.parseInt(interval.split("-")[1]);

				} else {

					activateEveryLow  = Integer.parseInt(interval);
					activateEveryHigh = activateEveryLow;

				}

				boolean overwritePreviousAction = false; //If set to true it will cancel the previous timer setting this action.

				if (CurrentParams.length==4){

					overwritePreviousAction = Boolean.parseBoolean(CurrentParams[3]);
				}

				NamedActionSetTimer.triggerRunNamedCommandsEvery(objectrnce, name, activateEveryLow,activateEveryHigh,callingObject,overwritePreviousAction,-1);


			}  else {

				Log.info(" Not enough params in runnamedcommandsevery statement. ");

			}

		}
		break; 
		case runnamedcommands:
		{
			//if theres two params
			if (Parameters.getTotal() ==2) {

				// if there's a comma, the first part is the object to apply it
				// too
				String objectrnc = Parameters.get(0).getAsString();  //CurrentParams[0];
				String name      = Parameters.get(1).getAsString();//CurrentParams[1];

				//if only the word global is supplied we assume we want to apply a global action called "name" to the current object
				if (objectrnc.equals("<GLOBAL>")){
					//we simply run the global named actionset called this;
					InstructionProcessor.testForGlobalActions(TriggerType.NamedActionSet, name, callingObject);
					return;
				} 

				// get the objects
				Set<? extends SceneObject> curobjectsrnc = SceneObjectDatabase.getSceneObjectNEW(objectrnc,callingObject,true);

				if (curobjectsrnc.size()==0){
					//	GameDataBox.addLastCommandToStack("Attempted To Run ActionSet On Object "+objectrnc+" but object now found. Maybe not loaded yet?","red","command attempted was "+name);
					Log.warning("Attempted To Run ActionSet On Object "+objectrnc+" but object now found. Maybe not loaded yet?");					

				}

				//its possibly that the actions triggered will remove the object.
				//if this happens we should make a copy of the list of objects we are looping over to stop a concurrent modification error
				//Set<? extends hasUserActions> tempcopy = new HashSet<>(curobjectaa);
				//then loop over this copy of the list instead
				
				// fire actions on all found objects
				for (SceneObject so : curobjectsrnc) {
					//much like with click actions, we first run object specific actions
					so.runNamedActionSet(name);

				}




			} else {
				callingObject.runNamedActionSet(theParameterString);
			}

		}
		break; 
		case setscenestaticoverlay:

			// displays a overlay in the form SceneName, Overlay CSS

			if (theParameterString.contains(",")) {

				// if there's a coma, the first part is the object to apply it
				// too
				String sceneName = CurrentParams[0];
				String cssName = CurrentParams[1];

				SceneWidget.getSceneByName(sceneName).setStaticSceneOverlay(
						cssName);

				//SceneWidgetVisual.all_scenes.get(sceneName).setStaticSceneOverlay(
				//	cssName);

			} else {
				Log.info("_________ adding overlay:" + theParameterString);

				// if nothing specified wwe assume the current scene
				SceneObjectDatabase.currentScene.setStaticSceneOverlay(theParameterString);

			}

			break; 
		case setscenedynamicoverlay:
		{
			// displays a overlay in the form SceneName, Overlay CSS

			if (theParameterString.contains(",")) {

				// if there's a coma, the first part is the object to apply it
				// too
				String sceneName = CurrentParams[0];
				String cssName = CurrentParams[1];

				SceneWidget.getSceneByName(sceneName).setDynamicSceneOverlay(
						cssName);

			} else {
				Log.info("_________ adding overlay:" + theParameterString);

				// if nothing specified we assume the current scene
				SceneObjectDatabase.currentScene.setDynamicSceneOverlay(theParameterString);

			}
			break;
		}


		case setlocation:
		{

			String locationName = theParameterString; //TODO:get from parameters

			if (locationName.startsWith("<") && locationName.endsWith(">")){


				Log.info(" getting location name from variable named:"+locationName);
				locationName = getValue(locationName,callingObject);

				Log.info(" getting scene named:"+locationName);

				if (locationName.isEmpty()){
					Log.severe("NO SCENE FOUND FOR VARIABLE");
				}
			} 



			JAMcore.usersCurrentLocation = locationName;

			RequiredImplementations.setWindowTitle(
					JAMcore.Username
					+ GamesInterfaceTextCore.MainGame_is_on_chapter
					+ JAMcore.usersCurrentLocation
					);



		}
		break; 
		case message:

			theParameterString = triggerMessage(theParameterString);
			break; 
		case previousmessage:

			//JAM.Feedback.setText(MessageHistory
			//		.getlastmessage(Integer.parseInt(CurrentProperty)));

			RequiredImplementations.setCurrentFeedbackText(FeedbackHistoryCore.getlastmessage(Integer.parseInt(theParameterString)));


			break; 
		case setmessagedelay:

			RequiredImplementations.setCurrentFeedbackTextDelay(Integer.parseInt(theParameterString));

			break; 

		case setvariable: 
		{
			//values are saved with case, but names are always lowercase
			//comparisons are always case insensitive, however

			// Get the variable name
			String namesetv = Parameters.get(0).getAsString().trim().toLowerCase();  // CurrentParams[0].trim().toLowerCase();

			// Get value to set
			String valuesetv = Parameters.get(1).getAsString().trim();  //CurrentParams[1].trim();//.toLowerCase();

			//not needed anymore, its done globally to all parameters;
			//if the value starts with a < and ends with a > its a variable, so get that value
			//if (valuesetv.startsWith("<") && valuesetv.endsWith(">")){
			//	valuesetv = getValue(valuesetv,callingObject);
			//} //not needed

			// Set name to value
			GameVariableManagement.GameVariables.AddItemUniquely(valuesetv, namesetv);

			break; 
		}
		case subvariable:
		{
			// Get the variable name
			String namesv = Parameters.get(0).getAsString().trim().toLowerCase();// CurrentParams[0].trim().toLowerCase();

			// Get value to sub
			String valuesv = Parameters.get(1).getAsString().trim().toLowerCase();// CurrentParams[1].trim().toLowerCase();

			// is it a number?							
			if (Parameters.get(1).isNumber()){
						
				//double ValueOf = Integer.valueOf(valuesv); //rather then try/catch we could use Parameters.get(1).isNumber()
				double ValueOf = Parameters.get(1).getAsDouble();
				
				// add to this if it exists
				double ValueOfItem = Double.parseDouble((GameVariableManagement.GameVariables
						.GetItem(namesv)));

				double newValue = (ValueOfItem - ValueOf);
				Log.info("subtract to " + newValue);

				// Set name to value
				GameVariableManagement.GameVariables.AddItemUniquely( newValue, namesv);

			} else {
				// Not a number, then we just ignore this command
				Log.severe("attempted to subtract  something not a number:"+valuesv+",or variable not set");
			}
		}
			break; 
		case addvariable:
		{
			// Get the variable name
			String nameav  = Parameters.get(0).getAsString().trim().toLowerCase();

			// Get value to add 
			String valueav = Parameters.get(1).getAsString().trim().toLowerCase();

			if (Parameters.get(1).isNumber()){
				
				double ValueOf = Parameters.get(1).getAsDouble();
				
				String variableValueFound = GameVariableManagement.GameVariables.GetItem(nameav);

				double ValueOfItem = 0;
				//if its empty then assume its zero
				if (variableValueFound==null || variableValueFound.isEmpty()){
					ValueOfItem = 0;
				} else {
					ValueOfItem = Double.parseDouble(variableValueFound);
				}

				
				// add to this
				double newValue = (ValueOfItem + ValueOf);

				Log.info("setting to " + newValue);

				// Set name to value
				GameVariableManagement.GameVariables.AddItemUniquely(newValue, nameav);

							
			} else {
				Log.severe("attempted to add  something not a number:"+valueav);				
			}
			
			/*
			 * 
			// is it a number?
			try {

				
				int ValueOf = Integer.valueOf(valueav); //rather then try/catch we could use Parameters.get(1).isNumber()

				String variableValueFound = GameVariableManagement.GameVariables.GetItem(nameav);

				int ValueOfItem = 0;
				//if its empty then assume its zero
				if (variableValueFound==null || variableValueFound.isEmpty()){
					ValueOfItem = 0;
				} else {
					ValueOfItem = Integer.valueOf(variableValueFound);
				}

				// add to this

				int newValue = (ValueOfItem + ValueOf);

				Log.info("setting to " + newValue);

				// Set name to value
				GameVariableManagement.GameVariables.AddItemUniquely("" + newValue, nameav);

			} catch (NumberFormatException e) {
				// Not a number, then we just ignore this command
				Log.severe("attempted to add  something not a number:"+valueav);
			}*/
			break;
		}
		case setmouseimage:

			OptionalImplementations.setMouseFromImage(theParameterString);
			//MouseCursorManagement.setMouseFromImage(CurrentProperty);

			/*
			//make temp image
			final Image tempImage = new Image(CurrentProperty);

			//when loaded trigger change
			tempImage.addLoadHandler(new LoadHandler() {
				@Override
				public void onLoad(LoadEvent event) {
					GwtMouseCursorManagement.setMouseImage(tempImage,GwtMouseCursorManagement.DefaultCursorString);
					//remove image after its down, as cursor doesn't need it.
					tempImage.removeFromParent();
				}
			});
			 */

			break;

		case setscore:
		{
			double valueToSet = Parameters.get(0).getAsDouble(); 
			
			// if score isn't already taken;
			JAMcore.PlayersScore.SetScore((long)valueToSet);//TODO: need a getAsLong so we dont need to cast

			if (JAMcore.ScoreBoxVisible_CuypersMode){
				RequiredImplementations.setCurrentFeedbackText("Current Score is:"+ valueToSet);
				FeedbackHistoryCore.AddNewMessage("<div class=\"MyApplication.messagehistoryReplyStyle\" >  Current Score is:" + valueToSet + "</div>"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			
			break;
		}
		case enterans:
		{	
			// this is a special function for entering another answer
			// Automatically.
			// It should be used at the end of scripts, and was for the purpose
			// of allowing nested
			// or repeated sets of actions to be written easily.
			//Typically this would be done with actionsets these days, however
			Log.info("answer given by script:" + theParameterString	+ " at location=" + JAMcore.usersCurrentLocation);

			JAMcore.AnswerGiven(theParameterString);
		}
			break; 
		case  setcluebox:
		{
			JAMcore.playersClues.LoadFromString(theParameterString.trim());
		}
			break; 			
		case setscorevisible:	
		{
			boolean visible = Parameters.get(0).getAsBoolean();
			JAMcore.PlayersScore.setVisible(visible);
		}
			break;
		case addscore:

			double amountToAdd = Parameters.get(0).getAsDouble();

			boolean checkForUniqueID = true;
			if (Parameters.getTotal()>1){
				checkForUniqueID = Parameters.get(0).getAsBoolean();
			}


			//if we are checking for unique ID and this ones been used before we exit without adding the score
			if (checkForUniqueID && ScoreControll.ScoresAwarded.contains("|" + UniqueTriggerIndent + "|")) {
				Log.info("points already awarded for:"+UniqueTriggerIndent);
				Log.info("(if you did not wish to check for if the trigger id was used before, simple use \"###,false\" on you addscore statement)");				
				return;
			}


			//else we add the score
			JAMcore.PlayersScore.AddScore((long)amountToAdd); //TODO: need a getAsLong so we dont need to cast

			Log.info("score added:"+amountToAdd); //temp, change back to info level
			
			
			if (JAMcore.ScoreBoxVisible_CuypersMode){
				
				RequiredImplementations.setCurrentFeedbackText(GamesInterfaceTextCore.MainGame_Score_Awarded + " "
						+ amountToAdd);
				FeedbackHistoryCore.AddNewMessage("<div class=\"MyApplication.messagehistoryReplyStyle\" >  Score Awarded:" + 
						amountToAdd + "</div>");
				
			}


			// add to scores set string if we were using it
			if (checkForUniqueID){
				ScoreControll.ScoresAwarded = ScoreControll.ScoresAwarded	+ " |" + UniqueTriggerIndent + "| ";
			}

			//Old implementation;
			/*
			// if score isnt already taken
			if (ScoreControll.ScoresAwarded.indexOf("|" + UniqueTriggerIndent
					+ "|") == -1) {

				JAMcore.PlayersScore.AddScore(Integer
						.parseInt(theParameterString));

				RequiredImplementations.setCurrentFeedbackText(GamesInterfaceTextCore.MainGame_Score_Awarded + " "
						+ theParameterString);

				FeedbackHistoryCore.AddNewMessage("<div class=\"MyApplication.messagehistoryReplyStyle\" >  Score Awarded:" + 
						theParameterString + "</div>");

				// add to scores set string
				ScoreControll.ScoresAwarded = ScoreControll.ScoresAwarded	+ " |" + UniqueTriggerIndent + "| ";
			} else {
				Log.info("points already awarded for:"+UniqueTriggerIndent);

			}*/

			break; 
		case pointsawardedfor:

			ScoreControll.ScoresAwarded = theParameterString;

			break; 
		case setmessagespeed:

			RequiredImplementations.setCurrentFeedbackTextSpeed(Integer.parseInt(theParameterString));
			//JAM.Feedback.setSpeed(Integer.parseInt(CurrentProperty));

			break; 
		case addinventory:

			/** function to add an inventory to the game **/
			InventoryPanelCore.addInventory(CurrentParams);

			break; 
		case pocketobject:


			triggerPocketObject(theParameterString, CurrentParams, callingObject);
			break; 
		case additem:

			String itemName = Parameters.get(0).getAsString();

			if (Parameters.getTotal()>1){
				String inventoryName = Parameters.get(1).getAsString(); //inventory name to put it in specified
				addItemToInventory(itemName, inventoryName,null);				
			} else {
				addItemToInventory(itemName, null,null);				//else uses default inventory
			}

			/*
			if (theParameterString.trim().length() > 0) {
				if (theParameterString.contains(",")) {
					addItemToInventory(CurrentParams[0], CurrentParams[1],null);
				}else {
					addItemToInventory(CurrentParams[0], null,null);
				}

			}
			 */
			break; 
		case holditem:
		{
			Log.info("told to hold1: "+theParameterString);
			//the inventory panel will deal with this
			InventoryPanelCore.holdItem(theParameterString);


			break; 
		}
		case unholditem:
		{
			//the inventory panel will deal with this
			InventoryPanelCore.unholdItem();

			break; 
		}
		case clearinventory:
		{
			// get the correct inventory if one is specified

			InventoryPanelCore inventoryTouse;

			// get the correct inventory if one is specified
			if (Parameters.getTotal()>0){

				String inventoryName = Parameters.get(0).getAsString();
				inventoryTouse = JAMcore.allInventorys.get(inventoryName);

				if (inventoryTouse == null) {
					inventoryTouse = InventoryPanelCore.defaultInventory;
				}

			} else {
				inventoryTouse = InventoryPanelCore.defaultInventory;
			}
			// else assume its the default

			Log.info("Clearing inventory:"+inventoryTouse.InventorysName);
			inventoryTouse.ClearInventory();
		}
		break; 
		case removeitem:
		{

			String objectName = Parameters.getProccessedParameterString();

			Log.info("Removing " + objectName + " from the inventory \n"); 
			//TODO: use new parameter system

			if (objectName.equalsIgnoreCase("<LASTINVENTORYITEM>")) {

				Log.info("Removing last inventory item");

				IsInventoryItem ItemToRemove = CurrentScenesVariables.lastInventoryObjectClickedOn;
				ItemToRemove.getNativeInventoryPanel().RemoveItem(ItemToRemove.getName());

				//	ItemToRemove.NativeInventoryPanel.RemoveItem(ItemToRemove.getName());

			} else if (objectName.equalsIgnoreCase("<HELDITEM>")){

				IsInventoryItem ItemToRemove = (IsInventoryItem) InventoryPanelCore.currentlyHeldItem; //temp cast
				if (ItemToRemove==null){
					Log.severe("No Held Item, yet was asked to remove <HELDITEM> from inventory");

				}
				ItemToRemove.getNativeInventoryPanel().RemoveItem(ItemToRemove.getName());

			} else {

				Iterator<InventoryPanelCore> Inventorys = JAMcore.allInventorys	.values().iterator();


				while (Inventorys.hasNext()) {
					InventoryPanelCore inventoryPanel = Inventorys.next();


					if (inventoryPanel.RemoveItem(objectName)) {
						return;
					}
				}

			}

		}
		break; 
		case specialeffect:
		{
			//because of silly legacy reasons, we have to parse the current commandlist as a string 
			//and send it to the trigger effect
			//TriggerEffect needs it so it can delay actions for later(ie, some run after a fade is finished)

			//crop the instructionset so it starts from under this line.
			CommandList newList = cropInstructionSetToCurrentCommand(currentCommand, instructionset);

			Log.info("Setting special effect to " + Parameters.toString() + ""); 
			Log.info("With post instructions; " + newList.getCode() + ""); 

			//Set the current actionsset to stop processing (as the copy above will now run instead)
			instructionset.stopProcessing();


			//
			JamGlobalGameEffects.TriggerEffect(Parameters.get(0).getAsString(), Parameters, newList);


		}
		break; 
		case loadscene:
		{
			//MyApplication.triggerSelectCheck = false;
			//MyApplication.pageToSelect = "";
			String name = theParameterString.trim();


			Boolean autoDisplay = true;
			String locationname = JAMcore.usersCurrentLocation;
			//
			if (CurrentParams.length>1){

				name = CurrentParams[0].trim();
				if (CurrentParams.length==2){
					autoDisplay = Boolean.valueOf(CurrentParams[1].trim());
				}
				if (CurrentParams.length==3){
					locationname =(CurrentParams[1].trim());
					autoDisplay = Boolean.valueOf(CurrentParams[2].trim());
				}
			}


			//check if scenename is specified by a variable
			if (name.startsWith("<") && name.endsWith(">")){


				Log.info(" getting scene name from variable named:"+name);
				name = getValue(name,callingObject);

				Log.info(" getting scene named:"+name);
			} 

			//check if location name is specified by a variable
			if (locationname.startsWith("<") && locationname.endsWith(">")){
				locationname = getValue(locationname,callingObject);
			} 

			loadSceneIntoNewTab(name,locationname,autoDisplay);

			break;
		}
		case loadscenesilently:
		{    
			//this function is for loading a scene without triggering any of its scenetofront,scenedebut,or scenefirstload actions
			//it effectively acts as if the scene has already been loaded and triggered before
			String scenename = theParameterString.trim();			

			Boolean autoDisplay = true;
			String locationname = JAMcore.usersCurrentLocation;
			//
			if (CurrentParams.length>1){

				scenename = CurrentParams[0].trim();
				if (CurrentParams.length==2){
					autoDisplay = Boolean.valueOf(CurrentParams[1].trim());
				}
				if (CurrentParams.length==3){
					locationname =(CurrentParams[1].trim());
					autoDisplay = Boolean.valueOf(CurrentParams[2].trim());
				}
			}

			//ensure scene doesnt retrigger stuff by using the "true" flag for silent mode
			loadSceneIntoNewTab(scenename,locationname,autoDisplay,true);

			break;
		}
		case gotoscene:
		{
			//scene must be loaded already
			//else it goes to the still loading tab			
			JAMcore.triggerSelectCheck = false;
			JAMcore.pageToSelect = "";

			String scenename = theParameterString;

			//check if scene name is specified by a variable
			//if (scenename.startsWith("<") && scenename.endsWith(">")){
			//	valuesetv = getValue(scenename,callingObject);
			//} 


			bringSceneToFront(theParameterString,false);
		}
			break;
		case storybox:

			//We are well aware how stupid this looks but it was necessary apparently.
			if (theParameterString.equals("1 Pierres aktetas.html")) {
				theParameterString = "10 Pierres aktetas.html";
			}


			System.out.print("Setting '" + theParameterString + "' to storybox \n"); //$NON-NLS-1$ //$NON-NLS-2$

			// remove any page selection stuff
			JAMcore.triggerSelectCheck = false;
			JAMcore.pageToSelect = "";

			// clear any tig messages\
			if (TigItemCore.lastclickedTig != null) {
				//	JAM.lastclickedTig.TiGFeedback.setText("");
				TigItemCore.lastclickedTig.setFeedbackText("");
			}

			// Page selection should be done -after- all the new pages are
			// specified, not before.

			// add to page loading queue, together with the current chapter
			// (was the chapter could change while the page is downloading, such
			// as during loading a save game)
			JAMcore.AddStoryTextToLoader(theParameterString);

			// CurrentLocationTabs.setNewPage(CurrentProperty);
			break;
		case selectscenesilently:


			bringSceneToFront(theParameterString,true);


			break; 
		case selectscene:


			bringSceneToFront(theParameterString,false);


			break; 
		case  selectpage:


			SceneAndPageSet.selectPageIfLoadedOrSceneStraightAway(theParameterString);
			// CurrentLocationTabs.setNewPage(CurrentProperty);

			break; 
		case removelocation:

			System.out.print("Removing '" + theParameterString + "' from storybox \n"); //$NON-NLS-1$ //$NON-NLS-2$
			// removePage(CurrentProperty);
			JAMcore.CurrentScenesAndPages.removePageFromSet(theParameterString);

			break; 
		case setitemstate:
		{
			Log.info("settings items state");
			String itemsname = CurrentParams[0].trim();
			String state     = CurrentParams[1].trim();

			//temp cast till get popup can be moved
			IsInventoryItem item = (IsInventoryItem) InventoryPanelCore.getItemFromName(itemsname);

			if (item!=null){

				Log.info("item:"+item.getName()+" found loading state:"+state);
				item.getPopup().loadState(state);

			} else {
				Log.info("item "+itemsname+" not found. It might still be loading so we add this command to the process when ready list");

				if (JAMcore.itemsLeftToLoad>0){
					Log.info("inventory items left to load:"+JAMcore.itemsLeftToLoad);

					//we add this instruction to the processor when ready list
					CommandList commands = new CommandList();
					commands.add(new CommandLine(GameCommands.setitemstate,itemsname+","+state));


					JAMcore.processInstructionsWhenInventorysReady( commands, UniqueTriggerIndent);
				} else {

					Log.info("but nothing is loading!");

				}


			}

		}
		break;
		case openitem:

			// if the property is <LASTINVENTORYITEM> then just trigger it

			if (theParameterString.equalsIgnoreCase("<LASTINVENTORYITEM>")) {
				CurrentScenesVariables.lastInventoryObjectClickedOn
				.triggerPopup();
				// no need to check anything else

			} else {

				// if the item not is currently in the inventory, we set it to add
				// and
				// open automatically.
				if (InventoryPanelCore.defaultInventory
						.inventoryContainsItem(theParameterString) == false) {

					// add item with trigger
					//	JAM.InventoryButton.setPlayForwardThenBack();

					InventoryPanelCore.defaultInventory.getInventorysButton().setPlayForwardThenBack();

					InventoryPanelCore.defaultInventory.AddItem(theParameterString,true,null);
					InventoryPanelCore.defaultInventory.OpenDefault();

				} else {
					// else we just open it
					// loop over all inventory's
					InventoryPanelCore.defaultInventory.triggerItem(theParameterString);

				}
			}

			break; 
		case  openurl:

			//Window.open(CurrentProperty, "_blank", ""); 

			OptionalImplementations.openWebpage(theParameterString, "_blank", ""); 
			break; 
		case  newchapter:

			// remove any page selection stuff
			JAMcore.triggerSelectCheck = false;
			JAMcore.pageToSelect = "";

			Log.info("setting new chapter");
			JAMcore.GamesChaptersPanel.newchapter(theParameterString);

			break; 
		case addprofile:

			if (theParameterString.equals("Thm45.html")) {
				theParameterString = "Th0m45.html";
			}


			//JAM.PlayersNotepad.AddPage(
			//		CurrentProperty,
			//		CurrentProperty.substring(0,
			//				CurrentProperty.indexOf(".html")));

			Log.info("adding profile");
			if (JAMcore.PlayersNotepad==null){				
				Log.info("PlayersNotepad is null");				
			}

			JAMcore.PlayersNotepad.AddPage(
					theParameterString,
					theParameterString.substring(0,theParameterString.indexOf(".html"))
					);

			JAMcore.PlayersNotepad.NotepadButton.setPlayForwardThenBack();
			//	JAM.PlayersNotepad.NotepadButton.setPlayForwardThenBack();

			break; 

		case settigitem:

			String itemnamesearch = CurrentParams[0];
			String state = CurrentParams[1];

			Log.info("\n setting tig item.." + state); 
			//note; shouldnt used last, should have a callingTIG variable
			(TigItemCore.lastclickedTig).setItemState(itemnamesearch, state);

			break; 
		case checktig:


			(TigItemCore.lastclickedTig).testCombination();

			break; 
		case disabletig:
			(TigItemCore.lastclickedTig).disable();

			break; 	
		case  tigmessage:
			//a complex mess waits within this function
			tigMessage(theParameterString);
			//tigs are togglable image groups - very usefull for puzzle items.
			//this sends a message to them for feedback.

			break; 
		case popupmessage:
			//toggleImageGroupPopUp.triggerPopUpMessage(CurrentProperty);

			TigItemCore.lastclickedTig.triggerPopUpMessage(theParameterString);

			break; 
		case popupadvert:

			//need factory? use inventoryitem factory(as this seems to be anyway?)
			//imageWithAlphaItem imagepop = new imageWithAlphaItem(CurrentProperty.trim(),""); 

			IsImageWithAlphaItem imagepop = InventoryItemFactory.createNewImageWithAlphaItem(theParameterString.trim(),""); 



			IsPopupPanel imagepopupcontainer = SceneObjectFactory.createTitledPopUp(null,
					"30%", "25%", "ADVERT", imagepop); 


			//new PopUpWithShadow(null,
			//"30%", "25%", "ADVERT", imagepop); 


			imagepopupcontainer.OpenDefault();

			//need a way to get the size of the game area in a implementation independent way
			//
			//imagepopupcontainer.setPopupPosition(
			//		(int) (Math.random() * (Window.getClientWidth() / 2)),
			//		(int) (Math.random() * (Window.getClientHeight() / 2)));

			imagepopupcontainer.setPopupPosition(
					(int) (Math.random() * (RequiredImplementations.getCurrentGameStageWidth() / 2)),
					(int) (Math.random() * (RequiredImplementations.getCurrentGameStageHeight() / 2))
					);




			break; 
		case popupimage:

			//imageWithAlphaItem imagepop2 = new imageWithAlphaItem(CurrentProperty.trim(),	""); 

			IsImageWithAlphaItem imagepop2 = InventoryItemFactory.createNewImageWithAlphaItem(theParameterString.trim(),""); 


			IsPopupPanel imagepopupcontainer2 = SceneObjectFactory.createTitledPopUp(null,
					"30%", "25%", "Amuse", imagepop2);
			//	new PopUpWithShadow(null,
			//	"30%", "25%", "Amuse", imagepop2);
			imagepopupcontainer2.OpenDefault();

			break; 	

		case setdefaultsound:
		{
			String soundID   = CurrentParams[0].trim();
			String trackName = CurrentParams[1].trim();

			//	GwtAudioController.setDefaultSoundTrack(soundID,trackName);

			OptionalImplementations.setDefaultSoundTrack(soundID,trackName);

		}
		case stopsounds:
		{	
			OptionalImplementations.stopAllSoundEffects();

		}
		break; 
		case stopobjectsound:
		{
			stopObjectsSound(theParameterString, CurrentParams,callingObject);
		}
		break; 
		case playobjectsound:
		{
			//  object[s]name, soundname, [vol 0-100]

			Set<? extends SceneObject> applytoo = null;
			String soundname = "";
			int vol = 100;
						
			
			//depending on number of params, we do different things
			switch (Parameters.getTotal()) {
			case 1:
				{
				soundname = Parameters.get(0).getAsString();
				HashSet<SceneObject> temp = new HashSet<SceneObject>();
				temp.add(callingObject); //silly, but we need to add calling object to a temp hashset first and then set applytoo to point to that set. We cant add callingObject directly to apply too, as applyto at that point doesnt know it can take that object type.
				applytoo = temp;
				}
				break;
			case 2:
				//we determine which by seeing if the 2nd param is a number (then its volume)
				if ( Parameters.get(1).isNumber()){
					
					HashSet<SceneObject> temp = new HashSet<SceneObject>();
					temp.add(callingObject); //silly, but we need to add calling object to a temp hashset first and then set applytoo to point to that set. We cant add callingObject directly to apply too, as applyto at that point doesnt know it can take that object type.
					applytoo = temp;
						
					soundname = Parameters.get(0).getAsString();	
					vol = Parameters.get(1).getAsInt();
					
				} else {
					applytoo = Parameters.get(0).getAsObjects(callingObject);
					soundname = Parameters.get(1).getAsString();	
				}
				break;
			case 3:
				applytoo = Parameters.get(0).getAsObjects(callingObject);
				soundname = Parameters.get(1).getAsString();
				vol = Parameters.get(2).getAsInt();
				break;
			default:
				Log.warning("wrong number of params on playobjectsound statement: "+Parameters.getProccessedParameterString());				
				break;	
			}
			
			
			
			playObjectsSound(soundname, applytoo,vol);

			break; 
		}
		case playsound:
		{
			int Volume =100;
			//alter volume if its set
			String filename = theParameterString;
			if (theParameterString.contains(",")) {
				// if there's a comma, then the second number is the volume
				filename = CurrentParams[0].trim();
				String VolumeString = CurrentParams[1].trim();
				Volume = getNumericalValue(VolumeString,callingObject);
			}
			/*
			// default type used
			String stypeps = JAM.defaultSoundType;

			// note: we should override for all extensions in case the default
			// type doesn't match extension
			if (CurrentProperty.toLowerCase().endsWith(".mp3")) {
				stypeps = Sound.MIME_TYPE_AUDIO_MPEG_MP3;
			}
			if (CurrentProperty.toLowerCase().endsWith(".ogg")) {
				stypeps = Sound.MIME_TYPE_AUDIO_OGG_VORBIS;
			}

			final Sound sound = AudioController.soundController.createSound(stypeps,
					JAM.audioLocation_url + CurrentProperty,true,false);			

			Log.info("playing:"+sound.getUrl()+" at vol:"+sound.getVolume()+" is type:"+sound.getSoundType()+" state="+sound.getLoadState()+" Vol:"+Volume);

			sound.setVolume(Volume);
			sound.play();
			 */

			Log.info("_______________playing filename: "+filename);
			//GwtAudioController.playAudioTrack(filename, Volume, false, JamAudioController.AudioType.SoundEffect,-1 );

			OptionalImplementations.playAudioTrack(filename, Volume, false, JamAudioController.AudioType.SoundEffect,-1 );

			//We no longer have a seperate playing sound list, it will be part of the normal audiosound cache 

			/*
			sound.addEventHandler( new SoundHandler() {				
				@Override
				public void onSoundLoadStateChange(SoundLoadStateChangeEvent event) {

				}

				@Override
				public void onPlaybackComplete(PlaybackCompleteEvent event) {
					AudioController.CurrentPlayingSounds.remove(sound); 
				}
			});


			AudioController.CurrentPlayingSounds.add(sound); //doesnt automatically remove
			 */
		}
		break; 
		case cacheaudiotrack:

			//Note; The main difference between audio and music is music loops and is put into the users playlist dropdown
			//GwtAudioController.cacheAudio(CurrentProperty,false);
			OptionalImplementations.cacheAudio(theParameterString,false);
			break; 
		case cachemusictrack:
			//Note; The main difference between audio and music is music loops and is put into the users playlist dropdown
			//GwtAudioController.cacheAudio(CurrentProperty,true);
			OptionalImplementations.cacheAudio(theParameterString,true);
			break; 
		case  addmusictrack:
		{

			// Get the track name
			String trackName = Parameters.get(0).getAsString();
			Boolean autoPlay = true;
			Integer volume = 100;
			int fadeOver = -1;
			
			// Do we autoplay? (true by default)
			if (Parameters.getTotal()>1){

			//	String autoPlayString = Parameters.get(1).getAsString().toLowerCase();
				//autoPlay = Boolean.parseBoolean(autoPlayString);

				autoPlay = Parameters.get(1).getAsBoolean();
				
				if (Parameters.getTotal()>2){
					// At what volume?
				//	String volumeString = Parameters.get(2).getAsString().toLowerCase();
					//volume =  Ints.tryParse(volumeString); 

					volume = Parameters.get(2).getAsInt();
					
					if (Parameters.getTotal()>3){
						// fade over
					//	String fadeOverStr = Parameters.get(3).getAsString().toLowerCase();
					//	fadeOver =  Ints.tryParse(fadeOverStr); 


						fadeOver = Parameters.get(3).getAsInt();
					}
				}

			}


			//note will also play the track automatically unless asked not too
			Log.info("adding track:"+trackName+" autoplay:"+autoPlay+" at "+volume+" volume and fading over:"+fadeOver);
			//GwtAudioController.addMusicTrack(trackName,autoPlay,volume,fadeOver);	
			OptionalImplementations.addMusicTrack(trackName,autoPlay,volume,fadeOver);	

			break; 

		}
		case  playmusictrack:
		{
			//can specify a number or a string
			// string = music track name
			// num = the number of the track in the players musicbox

			//we first check if its a string (ie track name) rather then number
			Integer tracknumber =  Ints.tryParse(theParameterString); //using the Ints method from guava it will return null rather then crashing if its not a number
			if (tracknumber != null) {
				// start music
				
				Log.info("playing track #:"+tracknumber);
				
				MusicBoxCore.setAllMusicTrackPlayingLabelTo(tracknumber.intValue()); //ensure labels are updated
				MusicBoxCore.playtrack(tracknumber.intValue(),100);


			} else {
				
				Log.info("playing track #:"+theParameterString);
				
				//then its a string! (we hope)
				OptionalImplementations.playMusicTrack(theParameterString);

			}

			
			break;
		}

		case setcurrentmusictrackvolume:
		{
			//set the volume of the current track being played
			String volumeString = theParameterString.trim().toLowerCase();
			int vol =  Ints.tryParse(volumeString); 
			//Log.info("setting volume on current track ("+GwtAudioController.getLastTrackName()+") to:"+vol);

			//GwtAudioController.setCurrentMusicVolume(vol);			
			//GwtAudioController.setCurrentMusicVolume(vol);		
			OptionalImplementations.setCurrentMusicVolume(vol);	

		}
		case addsecret:

			String secretname = CurrentParams[0];
			String secreturl = CurrentParams[1];

			OptionalImplementations.Cuypers_AddScret(secretname, secreturl);

			break; 
		case  openpanel:

			int X = 0;
			int Y = 0;

			// if theres x/y co-ordinates specified
			if (theParameterString.indexOf("@") > -1) {

				String XY = theParameterString.split("@")[1];
				theParameterString = theParameterString.split("@")[0];
				Log.info("xy = " + XY);
				X = Integer.parseInt(XY.split(",")[0]);
				Y = Integer.parseInt(XY.split(",")[1]);
				Log.info("x=" + X + " Y=" + Y);

			}

			if (theParameterString.equalsIgnoreCase("Control")) {

				if (TigItemCore.lastclickedTig != null) {
					// open relative to parent
					// int height =
					// MyApplication.lastclicked_item.imagegroupframe.getOffsetHeight();
					// int width =
					// MyApplication.lastclicked_item.imagegroupframe.getOffsetWidth();

					int dx =// JAM.lastclickedTig.imagegroupframe
							//.getAbsoluteLeft();
							TigItemCore.lastclickedTig.getAbsoluteLeftOfImage();

					int dy = //JAM.lastclickedTig.imagegroupframe
							//.getAbsoluteTop();
							TigItemCore.lastclickedTig.getAbsoluteTopOfImage();

					//					JAM.ControllPanelShadows.setPopupPosition(X + dx,
					//							Y + dy);
					//
					//					JAM.ControllPanelShadows.centered = false;

					//	JAM.toggleControlPanel();
					RequiredImplementations.screenManager.get().setControlPanelPosition(X + dx,	Y + dy);

					;


					if (RequiredImplementations.screenManager.get().controllPanelIsOpen()){
						RequiredImplementations.screenManager.get().closeControlPanel();

					} else {
						//GwtScreenMangement.openControlPanel();
						RequiredImplementations.screenManager.get().openControlPanel();
					}

					//JAM.openControlPanel();

				} else {
					// open default

					//JAM.toggleControlPanel();
					if (RequiredImplementations.screenManager.get().controllPanelIsOpen()){
						RequiredImplementations.screenManager.get().closeControlPanel();

					} else {
						//GwtScreenMangement.openControlPanel();
						RequiredImplementations.screenManager.get().openControlPanel();
					}
				}

			}

			break; 
		case sendemail:

			OptionalImplementations.sendEmailTemplate(theParameterString);

			//move to web specific functions
			//JAM.sendEmailTemplate(CurrentProperty);

			break; 
		case setstorypagescrollbars:

			Log.info("setting scrollbars:");

			if (theParameterString.equalsIgnoreCase("off")) {

				Log.info("setting scrollbars off");
				JamChapterControl.SetStoryPageScrollbars = false;

				JAMcore.GamesChaptersPanel.setStoryPageScrollBar(false);

				//	JAM.GamesChaptersPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
				//	JAM.GamesChaptersPanel.getDeckPanel().getElement().getStyle().setOverflow(Overflow.HIDDEN);

			} else {

				Log.info("setting scrollbars on");
				JamChapterControl.SetStoryPageScrollbars = true;

				JAMcore.GamesChaptersPanel.setStoryPageScrollBar(true);

				//JAM.GamesChaptersPanel.getElement().getStyle().setOverflow(Overflow.AUTO);
				//JAM.GamesChaptersPanel.getDeckPanel().getElement().getStyle().setOverflow(Overflow.AUTO);

			}

			break; 
		case setsoldiericon:

			String iconloc = CurrentParams[0];
			// only update if url string is present
			if (iconloc.length() > 5) {
				int iconframes = Integer.parseInt(CurrentParams[1]);

				OptionalImplementations.Cuypers_SetSoldierURL("GameIcons/"
						+ JAMcore.iconsizestring + "/" + iconloc,
						iconframes);


				/*
				JAM.solider.setURL("GameIcons/"
						+ JAM.iconsizestring + "/" + iconloc,
						iconframes);*/

				//	JAM.DebugWindow.addText("-seticon-" + iconloc);
			}
			break; 
		case setclockladyicon:

			String iconlocscl = CurrentParams[0];
			// only update if url string is present
			if (iconlocscl.length() > 5) {
				int iconframes = Integer.parseInt(CurrentParams[1]);

				OptionalImplementations.Cuypers_StatueHeadUrl("GameIcons/"
						+ JAMcore.iconsizestring + "/" + iconlocscl,
						iconframes);


				/*
				JAM.StatueHead.setURL("GameIcons/"
						+ JAM.iconsizestring + "/" + iconlocscl,
						iconframes);
				 */

				//JAM.DebugWindow.addText("-seticon-" + iconlocscl);
			}
			break; 
		case setinventoryicon:				
			//set the main inventory icon 
			//setDefaultInventoryIcon(CurrentProperty, CurrentParams);


			String location = CurrentParams[0];
			String frames_string   = CurrentParams[1];
			int iconsframes = Integer.parseInt(frames_string);

			InventoryPanelCore.defaultInventory.getInventorysButton().setInventoryIconTo(location, iconsframes);

			break;
		case setstoryboxbackgroundclass:

			JAMcore.CurrentScenesAndPages.setstoryboxbackgroundclass(theParameterString);

			//			// Element bottom panel =
			//			// (Element)MyApplication.StoryTabs.getDeckPanel().getElement().getFirstChild().getFirstChild().getFirstChild().getChildNodes().getItem(1);
			//			Log.info("setting storybox background style to "
			//					+ CurrentProperty);
			//			// test bits
			//			// CurrentLocationTabs.getElement().setClassName("_level1_test");
			//			// CurrentLocationTabs.getElement().getFirstChildElement().setClassName("_level2_test");
			//			Element bottompanel = (Element) (JAM.CurrentScenesAndPages.visualContents
			//					.getElement().getFirstChildElement().getChildNodes()
			//					.getItem(1));
			//
			//			if (bottompanel == null) {
			//				Log.info("null element, cant set style");
			//			}
			//
			//			if (CurrentProperty.compareTo("none") == 0) { //$NON-NLS-1$
			//				// MyApplication.StoryTabs.getDeckPanel().setStyleName("");
			//				bottompanel.setClassName("_none_");
			//
			//			} else {
			//				Log.info("setting deck style.");
			//				// MyApplication.StoryTabs.getDeckPanel().setStyleName("blah1"+CurrentProperty);
			//
			//				// Log.info("nodes="+bottompanel.getInnerHTML());
			//				bottompanel.setClassName(CurrentProperty);
			//
			//			}

			break;
		case removeclassfromelement:
		{


			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				String elementsID   = CurrentParams[0];
				String classestoset = CurrentParams[1];

				Log.info("set element ID  =" + elementsID);
				Log.info("set set classes =" + classestoset);

				boolean success = OptionalImplementations.PageStyleCommandImplemention.get().removeCSSClassFromDomElement(elementsID,classestoset);

				if (success){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					InstructionProcessor.ChangedHtmlPageElement.add(elementsID);

				}

			}



		}
		break; 
		case setclassonelement:		
		{
			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				String elementsID   = CurrentParams[0];
				String classestoset = CurrentParams[1];

				Log.info("set element ID  =" + elementsID);
				Log.info("set set classes =" + classestoset);

				boolean success = OptionalImplementations.PageStyleCommandImplemention.get().setCSSClassOnDomElement(elementsID,classestoset);

				if (success){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					InstructionProcessor.ChangedHtmlPageElement.add(elementsID);

				}

			}

			/*
			// adds a class to any div element

			Element elementToSetac = DOM.getElementById(elementsID);

			if (elementToSetac != null) {

				elementToSetac.setClassName(classestoset);				

				//Ensure the element is on the changed div list 
				//This list is so the save functions know what element states need to save
				ChangedHtmlPageElement.add(elementToSetac);

			}*/
		}
		break;
		case addclasstoelement:
			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				// adds a class to any div element
				String Elementac = CurrentParams[0];
				String Classac   = CurrentParams[1];

				Log.info("set element ID=" + Elementac);
				Log.info("set class =" + Classac);

				boolean success = OptionalImplementations.PageStyleCommandImplemention.get().addCSSClassToDomElement(Elementac,Classac);
				if (success){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					ChangedHtmlPageElement.add(Elementac);
				}
			}
			break; 
		case showelement: //NOTE: All html specific functions will eventually need to be moved to a class
			//and somehow made optional to implement
			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){
				// hides an ID on the page
				String IDname = theParameterString;

				Log.info("show element ID=" + IDname);

				boolean addToChangedElements = OptionalImplementations.PageStyleCommandImplemention.get().showelement(IDname);
				if (addToChangedElements){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					ChangedHtmlPageElement.add(IDname);
				}



			}
			break; 
		case setstyleonelement:
		{


			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				String elementsID = CurrentParams[0];
				String styletoset = CurrentParams[1];

				Log.info("set element ID=" + elementsID);
				Log.info("set set inlinestyles =" + styletoset);


				boolean success = OptionalImplementations.PageStyleCommandImplemention.get().setStyleOnElement(elementsID,styletoset);
				if (success){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					ChangedHtmlPageElement.add(elementsID);
				}

			}
			//Element elementToSet = DOM.getElementById(elementsID);

			//if (elementToSet==null){
			//	Log.info("Error: Cant find requested element in DOM");
			//	return;
			//}


			//elementToSet.setAttribute("style", styletoset);

			//ChangedHtmlPageElement.add(elementsID);	

		}
		break;	

		/** Note, fade in and out elements have a maximum duration of 10,000ms (100ms interval x 100 intervals)**/
		case fadeinelement:

			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				boolean success = false;
				String elementsID = "";

				if (CurrentParams.length==1){				
					elementsID = theParameterString.trim();
					//InstructionProcessor.fadeInHTMLElement(CurrentProperty.trim());

					success = OptionalImplementations.PageStyleCommandImplemention.get().fadeInHTMLElement(elementsID,1670); //default duration

				} else if (CurrentParams.length==2) {

					elementsID = CurrentParams[0].trim();
					String lengthString = CurrentParams[1].trim();
					int length = Integer.parseInt(lengthString);

					success = OptionalImplementations.PageStyleCommandImplemention.get().fadeInHTMLElement(elementsID,length);

					//InstructionProcessor.fadeInHTMLElement(elementsID,length);

				}
				//if fade was set successfully and we arnt a loading div, then we save the element as changed
				if (success && !elementsID.equalsIgnoreCase("loadingdiv")){
					ChangedHtmlPageElement.add(elementsID); //we only add if not loadingdiv, as thats controlled seperately
				}

			}

			break;

		case fadeoutelement: 

			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				boolean success = false;
				String elementsID = "";

				if (CurrentParams.length==1){				
					elementsID = theParameterString.trim();

					success = OptionalImplementations.PageStyleCommandImplemention.get().fadeOutHTMLElement(elementsID,1670); //default duration

				} else if (CurrentParams.length==2) {

					elementsID = CurrentParams[0].trim();
					String lengthString = CurrentParams[1].trim();
					int length = Integer.parseInt(lengthString);

					success = OptionalImplementations.PageStyleCommandImplemention.get().fadeOutHTMLElement(elementsID,length);


				}
				//if fade was set successfully and we arnt a loading div, then we save the element as changed
				if (success && !elementsID.equalsIgnoreCase("loadingdiv")){
					ChangedHtmlPageElement.add(elementsID); //we only add if not loadingdiv, as thats controlled separately
				}

			}

			//if (CurrentParams.length==1){
			//	JAM.fadeOutHTMLElement(CurrentProperty.trim());
			//} else if (CurrentParams.length==2) {

			//	String elementsID = CurrentParams[0].trim();
			//	String lengthString = CurrentParams[1].trim();
			//	int length = Integer.parseInt(lengthString);

			//	JAM.fadeOutHTMLElement(elementsID,length);

			//}

			break;
		case hideinventorybutton:
		{
			String inventoryname   = Parameters.get(0).getAsString();
			InventoryPanelCore inventory = JAMcore.allInventorys.get(inventoryname);
			
			inventory.hideInventorysButton();
			
			break;

		}
		case showinventorybutton:
		{
			
			String inventoryname   = Parameters.get(0).getAsString();
			InventoryPanelCore inventory = JAMcore.allInventorys.get(inventoryname);
			
			inventory.showInventorysButton();
			break;

		}		
		case hideinventorybuttons:
		{
			InventoryPanelCore.hideAllInventoryButtons();
			break;

		}
		case showinventorybuttons:
		{
			InventoryPanelCore.showAllInventoryButtons();
			break;

		}			
		case hideelement:

			// hides an ID on the page			
			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){
				// hides an ID on the page
				String IDname = theParameterString;

				Log.info("show element ID=" + IDname);

				boolean addToChangedElements = OptionalImplementations.PageStyleCommandImplemention.get().hideelement(IDname);
				if (addToChangedElements){
					//Ensure the element is on the changed div list 
					//This list is so the save functions know what element states need to save
					ChangedHtmlPageElement.add(IDname);
				}

			}



			break; 
		case replacediv:


			// internal use only really.
			String divname = CurrentParams[0];
			String ItemToAddsName = CurrentParams[1];

			Log.info("replacing div=" + divname + " with item "
					+ ItemToAddsName);

			IsInventoryItemPopupContent ItemToAdd = InventoryPanelCore.defaultInventory.getItemPopUp(ItemToAddsName);

			if (ItemToAdd == null) {
				Log.info("no item found under that name : "
						+ ItemToAddsName + ", thus cant add to div");
				return;
			}

			RequiredImplementations.PositionByTag(ItemToAdd,divname);

			break; 
		case setbackgroundclass:

			if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){

				OptionalImplementations.PageStyleCommandImplemention.get().setBackgroundClass(theParameterString);

				//
				//				if (CurrentProperty.compareTo("none") == 0) { 				
				//					RootPanel.getBodyElement().setClassName("");
				//				} else {				
				//					RootPanel.getBodyElement().setClassName(CurrentProperty);
				//				}

			}	

			break; 
		case setbackgroundimage:

			RequiredImplementations.setBackgroundImage(theParameterString);

			//
			//
			//			if (CurrentProperty.compareTo("none") == 0) { 
			//				RootPanel.getBodyElement().setAttribute("background", ""); 
			//				JAM.CurrentBackground = "none";
			//
			//			} else {
			//				RootPanel.getBodyElement().setAttribute("background", CurrentProperty); 
			//
			//				JAM.CurrentBackground = CurrentProperty;
			//
			//			}



			break; 
		case setclockmode:
			JamGlobalGameEffects.setClockMode(theParameterString);

			//			
			//			if (CurrentProperty.compareTo("fast") == 0) { //$NON-NLS-1$
			//				JAM.gwt_clock.setModeFast();
			//			} else {
			//				JAM.gwt_clock.setModeNormal();
			//			}

			break; 
		case  debug_image_urls:
			Log.severe("_______________debug_image_url no longer supported. This command used to list all the image urls the game had used into this log");

			/*
			Iterator<String> urlit = GWTAnimatedIcon.urlList.iterator();
			Log.info("urls:");
			String string = "";
			while (urlit.hasNext()) {

				string = string + "\n\r" + urlit.next();

			}
			Log.info("" + string);
			 */
			break; 
		case setgamemode:
			JamGlobalGameEffects.setGameEffect(Parameters);
			break;
		case notarecognisedcommand:
		{
			Log.severe("_________________*__ Not a Recognised Command:"+theParameterString);
			break;
		}
		case commandnotset:
		{
			Log.severe("_________________*__ Command not set:"+theParameterString);
			break;
		}
		case command_log_off:
		{
			boolean setOff = false;
			if (Parameters.isEmpty()){
				Log.info("___________________ Command log set off");
				setOff=true;
			} else {
				setOff = Parameters.get(0).getAsBoolean();				
			}

			if (setOff){
				Log.info("___________________ Command log set off");
				tempDisableLog=true;
			} else {
				Log.info("___________________ Command log set on");
				tempDisableLog=false;
			}



			break;
		}



		}





	}


	private static CommandList cropInstructionSetToCurrentCommand(CommandLine currentCommand,
			CommandList instructionset) {
		CommandList newList = new CommandList();
		boolean skip = true;

		for (CommandLine commandLine : instructionset) {				
			if (commandLine==currentCommand){
				skip=false;
				continue;
			}				
			if (skip){
				continue;
			} else {
				newList.add(commandLine);
			}
		}
		return newList;
	}


	protected static void dropObjects(CommandParameterSet Parameters, SceneObject callingObject, GameCommands currentCommand) {

		Log.info("Dropping Object : (wip)"+currentCommand.name()+" - "+Parameters.getProccessedParameterString());
		if (callingObject == null){
			Log.warning("calling object is null!"); //temp
		}
		double bounce = -1;//0.5;//default -1 means take from objects settings? the value already in its movestate)

		String objectname = "";
		if (Parameters.isEmpty()) {
			Log.warning("Parameters empty, assuming calling object which is:"+callingObject.getName());
			//callingObject.dropToFloor(bounce);		


			if (currentCommand == GameCommands.droptofloor) {
				callingObject.dropToFloor(bounce);	

			} else if (currentCommand == GameCommands.droptoparentsbase){
				callingObject.dropToParentsBase(bounce);	

			}

			return;
		} else if (Parameters.getTotal()==1) {

			//if its a string then its a object name, if its a number its a bounce value
			String parameterOne = Parameters.get(0).parameterString;

			if (parameterOne.matches("[0-9.-]*") && (!parameterOne.isEmpty()) ){
				// take its value
				bounce = Double.parseDouble(parameterOne);	
			}	else {								
				//if it wasn't a number then we assume its a name
				objectname = parameterOne;
				bounce = 0.5;

			}

		} else if (Parameters.getTotal()==2) {

			String parameterOne = Parameters.get(0).getAsString();	
			objectname = parameterOne.trim();				
			String parameterTwo = Parameters.get(1).getAsString();	
			bounce = Double.parseDouble(parameterTwo);

		} 

		if (!objectname.isEmpty()){

			Set<? extends SceneObject> objectsToDrop = SceneObjectDatabase
					.getSceneObjectNEW(objectname, callingObject, true);

			if (    objectsToDrop!=null 
					&& !objectsToDrop.isEmpty()){
				for (SceneObject so : objectsToDrop) {

					if (currentCommand == GameCommands.droptofloor) {
						so.dropToFloor(bounce);	

					} else if (currentCommand == GameCommands.droptoparentsbase){
						so.dropToParentsBase(bounce);	

					}
				}
			} else {

				Log.info("Cant find object called:"+objectname);

			}

		} else {

			if (currentCommand == GameCommands.droptofloor) {
				callingObject.dropToFloor(bounce);	

			} else if (currentCommand == GameCommands.droptoparentsbase){
				callingObject.dropToParentsBase(bounce);	

			}


		}



	}
	//
	//	public static void framegap(
	//			String CurrentProperty,
	/// callingObject,
	//		IsSceneSpriteObject lastSpriteObjectCalled, String[] CurrentParams) {

	//	}

	public static void framegap(
			SceneObject callingObject,
			IsSceneSpriteObject lastSpriteObjectCalled, 
			CommandParameterSet params) {

		if (params.getTotal()>1) {

			// if there's a coma, the first part is the object to apply it
			// too
			String objectsfg = params.get(0).getAsString();
			int gap = params.get(1).getAsInt();

			// get the objects 

			Set<? extends IsSceneSpriteObject> curobjectsfg = SceneObjectDatabase
					.getSpriteObjectNEW(objectsfg,callingObject,true);

			//set all the scene sprite objects found to the requested frame gap
			for (IsSceneSpriteObject sso : curobjectsfg) {
				sso.setFrameGap(gap);
			}


		} else {

			int gap = params.get(0).getAsInt();
			// its just a state, so we assume its the last object clicked on
			Log.info("setting frame gap on object:"
					+ lastSpriteObjectCalled.getObjectsCurrentState().ObjectsName);

			lastSpriteObjectCalled.setFrameGap(gap);

		}
	}

	public static void changeanimationstate(String CurrentProperty,
			SceneObject callingObject,
			IsSceneSpriteObject lastSpriteObjectCalled,
			String[] CurrentParams) {

		if (CurrentProperty.contains(",")) {

			// if there's a comma, the first part is the object to apply it
			// too
			String objectsName = CurrentParams[0];
			String state       = CurrentParams[1];

			// get the object
			//new methods to get all the objects to change
			Set<? extends IsSceneSpriteObject> curobjectsos = SceneObjectDatabase
					.getSpriteObjectNEW(objectsName,callingObject,true);




			if (curobjectsos==null){

				//do nothing

			} else {

				Log.info("found "+curobjectsos.size()+" to change animation state of");

				//set the state of all the objects
				for (IsSceneSpriteObject objectToSet : curobjectsos) {

					//	Log.info("testing if object is in scene:"+objectToSet.getScene().isObjectInScene(objectToSet));
					objectToSet.setAnimationStatus(state);	

				}

			}

		} else {
			// its just a state, so we assume its the last object clicked on
			//	Log.info("testing if object is in scene:"+lastSpriteObjectCalled.getScene().isObjectInScene(lastSpriteObjectCalled));

			lastSpriteObjectCalled.setAnimationStatus(CurrentProperty);
		}

	}

	/** This will make an image briefly appear at a specified location for awhile before fading again */
	private static void triggerBamf(String objectsURL, int posx,			
			int posy, final int fadeDuration, final int holdDuration) {

		//TODO: convert this to a implementation agnostic version
		//Option1 - use a tempory SceneSpriteObject and dispose it after
		//Option2 - create a new basic image type for the SceneObjectFactory to return


		//Create bamf object
		//final Image bamfPop = new Image(objectsURL);
		final IsBamfImage bamfPop = SceneObjectFactory.createNewBamfImage(objectsURL);
		//int defaultZindex = 90000;

		//final PopupPanel bamfPop = new PopupPanel();



		//bamfPop.add(bamfImage);		
		bamfPop.setOpacity(0);

		/*
		bamfPop.getElement().getStyle().setOpacity(0);
		bamfPop.getElement().getStyle().clearDisplay();
		//bamfPop.setPopupPosition(posx, posy);

		bamfPop.setStyleName("BAMFPopup");
		bamfPop.getElement().setId("_BAMFPopup_");
		bamfPop.getElement().getStyle().setZIndex(defaultZindex);
		 */
		//h

		final double OpacityStepPerMS = 100.0/fadeDuration;
		final double delayAmount = 0;                      //amount of time before FADEIN starts
		final double holdAmount =  holdDuration;           //amount of time the bamf stay on the screen

		//maybe this can be moved into the Bamf interface?
		final DeltaRunnable fadeControll = new DeltaRunnable(){

			double Opacity              = 0;						
			fadeState state             = fadeState.delay;
			double timeIntoCurrentState = 0;

			@Override			
			public void update(float delta) {	

				timeIntoCurrentState=timeIntoCurrentState+delta;

				double changeAmount = 0;

				switch (state) {
				case delay:
					if (timeIntoCurrentState>delayAmount){
						state = fadeState.fadeIn;//no break we contain straight away
						timeIntoCurrentState=0;
					} else {
						return; //still waiting for the delay phase to finish
					}
				case fadeIn:
					changeAmount = OpacityStepPerMS * delta;
					Opacity=Opacity+changeAmount;
					if (Opacity>=100){
						Opacity=100;
						state = fadeState.hold;
						timeIntoCurrentState=0;
					}
					//bamfPop.getElement().getStyle().setOpacity(Opacity / 100.0);

					bamfPop.setOpacity(Opacity);

					break;

				case hold:
					if (timeIntoCurrentState>holdAmount){
						state = fadeState.fadeOut;//no break we contain straight away
						timeIntoCurrentState=0;
					} else {
						return; //still waiting for the hold phase to finnish
					}
					break;

				case fadeOut:
					changeAmount = -(OpacityStepPerMS * delta);
					Opacity=Opacity+changeAmount;
					if (Opacity<=0){
						Opacity=0;			
						timeIntoCurrentState=0;
						//bamfPop.getElement().getStyle().setOpacity(0);
						//bamfPop.getElement().getStyle().setDisplay(Display.NONE);

						bamfPop.setOpacity(0);
						cancel();
						return;
					}

					bamfPop.setOpacity(Opacity);
					//bamfPop.getElement().getStyle().setOpacity(Opacity / 100.0);
					break;
				}



				//if (Opacity>=100){
				//	Log.info("starting fadeout after pause");
				//	delay.schedule(holdDuration);
				//	this.cancel();
				//}
			}

		};




		Log.info("starting fade in");
		//bamfPop.show();
		RequiredImplementations.PositionByCoOrdinates(bamfPop, posx, posy, 0);

		JAMTimerController.addObjectToUpdateOnFrame(fadeControll);

		//fadeIn.scheduleRepeating(fadeDuration/10);


	}

	public static void triggerSendClickToObject(SceneObject callingObject,
			String objectsname, String mode) {


		Set<? extends SceneObject> curobjectaa = SceneObjectDatabase
				.getSceneObjectNEW(objectsname,callingObject,true);

		for (SceneObject currentObject : curobjectaa) {

			Log.info("triggering click on "+currentObject.getObjectsCurrentState().ObjectsName);					

			if (mode.equalsIgnoreCase("test")){
				boolean clickHitsObject = currentObject.testIfMouseWouldHit(CurrentScenesVariables.lastclicked_x,CurrentScenesVariables.lastclicked_y);

				if (clickHitsObject){
					currentObject.triggerClickOnObject(); 
				}

			} else {
				currentObject.triggerClickOnObject(); 
			}

		}


	}
	public static void triggerSendRightClickToObject(SceneObject callingObject,
			String objectsname, String mode) {

		// get the objects
		//SceneObject curobjectaa[] = SceneWidget
		//		.getSceneObjectByName(objectsname,callingObject);


		Set<? extends SceneObject> curobjectaa = SceneObjectDatabase
				.getSceneObjectNEW(objectsname,callingObject,true);

		for (SceneObject currentObject : curobjectaa) {

			Log.info("triggering context click on "+currentObject.getObjectsCurrentState().ObjectsName);					

			if (mode.equalsIgnoreCase("test")){
				Boolean clickHitsObject = currentObject.testIfMouseWouldHit(CurrentScenesVariables.lastclicked_x,CurrentScenesVariables.lastclicked_y);
				if (clickHitsObject){
					currentObject.triggerContextClickOnObject(); 
				}
			} else {
				currentObject.triggerContextClickOnObject(); 
			}

		}


	}

	private static void cancelNamedCommands(final String commandSourceObject, final String name,final SceneObject callingObject ){


		if (commandSourceObject.equalsIgnoreCase("<GLOBAL>")){

			final String uniquename = callingObject.getName()+"_"+name;
			Log.info("canceling global: "+uniquename);					

			JAMTimerController.cancelNamedActionSetTimer("<global>", uniquename);

		} else {
			Log.info("canceling: "+ name+" on "+commandSourceObject);				

			JAMTimerController.cancelNamedActionSetTimer(commandSourceObject, name);


		}



	}

	
	//idea;
	//we could have a '2dcloneover' function that will clone over a cmap or named region
	//0. Retrieve the desired density of points (pixel interval?)
	//1. Make a grid of scene co-ordinates over the source object (or, preferably, over the regions bounding box)
	//2. Remove any points not in the region we want
	//3. loop over remaining points firing existing clone function to clone a object at those positions
	
	// This function would be great for making fire or anything else that should cover a objects area.
	//(ie, lots of tall thin sprites for fire)
	// could be very costly as it generates a lot of objects though
	
	//usefull modes when generating points;
	//1 - uniform distribution at ground level
	//2 - random distribution at ground level
	//3/4 - same but for 3d volumes?
	//
	//might end up with a lot of perameters here though'
	// objectToClone,newName,SourceRegion/Object,Mode,Density
	//
	//maybe varable density in x/y/z? for uniform grid?
	//with random just a total number?
	//
	//more usefull for fire;; bottom edge - regular points over the bottom edge of a cmap
	//-- how to isolate the bottom edges?
	//-- normals have positive y?
	
	//add function to polys "get lower edges" which returns a set
	//add static function to polyside "get points on edge" which returns a arraylist of points at regular intervals from a set of edges
	//
	//todo: also for behind? we can then surround the object
	//longterm: maybe even let it spread out from a point? (alternate cw/acs points spreading along the lines)
	
	public static void wip_cloneover_bottomedge(CommandList postInstructions, SceneObject callingobject, SceneObject objectToClone, String newname, SceneObject edgesFrom, int spaceing){

		if (!edgesFrom.cmap.isPresent()){
			Log.severe("Object to clone over has no cmap");
			InstructionProcessor.processInstructions(postInstructions, "postCloneOver", callingobject);
			
			return;
		}
				
		
		
		
		Iterator<Polygon> allPolygons = edgesFrom.cmap.get().iterator(); //we iterate over all the polygons in the map
		
		while (allPolygons.hasNext()) {
			Polygon polygon = (Polygon) allPolygons.next();
			Log.info("using polygon with size "+polygon.size()+" name "+polygon.getName());
			
			wip_cloneover_bottomedge(postInstructions,callingobject,  objectToClone,  newname,  polygon,spaceing);
			
		}
		
		
	}
		
	//when finished move this elsewhere?
	public static void wip_cloneover_bottomedge(final CommandList postInstructions,final SceneObject callingobject, final SceneObject objectToClone, final String newname, Polygon edgesFrom,int spaceing){
		
		
				
		//1. Get edges
		ArrayList<PolySide> edges = edgesFrom.getAllLowerEdges();		
		Log.info("Got "+edges.size()+" edges from "+edgesFrom.associatedObject.getName());
			
		//get points on each polyside
		final ArrayList<Simple3DPoint>  points = PolySide.getEvenlySpacedPointsAlong(edges,spaceing);
		
		
		Log.info("Got "+points.size()+" points.");
		
		
		JAMTimerController.IsIncrementalCommand cloneAPoint = new JAMTimerController.IsIncrementalCommand(){
			Iterator<Simple3DPoint> pointit = points.iterator();
			int current_zindex = objectToClone.getZindex()+1;
			
			@Override
			public boolean run() {
				Simple3DPoint location = pointit.next();

				int x = location.x;			
				int y = location.y;
				int z = location.z;

				Log.info("Clone at point: "+location);
				
				SceneObject clonedObject = triggerCloneObject(callingobject,objectToClone,newname,x,y,z);
				
				//z index should count up ?
				clonedObject.setZIndex(current_zindex);
				current_zindex=current_zindex+1;
				
				boolean moreToClone = pointit.hasNext();
				
				//trigger commands that were waiting for the cloning to finish
				if (!moreToClone){
					InstructionProcessor.processInstructions(postInstructions, "postCloneOver", callingobject);
				}
				
				return moreToClone;
			}
			
		};
		
		JAMTimerController.scheduleIncremental(cloneAPoint);
		
		
//		
//		for (Simple3DPoint location : points) {
//
//			int x = location.x;			
//			int y = location.y;
//			int z = location.z;
//
//			Log.info("Clone at point: "+location);
//			
//			SceneObject clonedObject = triggerCloneObject(callingobject,objectToClone,newname,x,y,z);
//			
//			//z index should count up ?
//			clonedObject.setZIndex(current_zindex);
//			current_zindex=current_zindex+1;
//		}
		
	}
	
	
	/**
	 *  This function will clone a sceneobject and position it at a specified location.
	 *  You must name the new object so it can be referred to later.
	 *  
	 *  Notes; 
	 *  If this function is called from a object (rather then say, a global timer) the new objects z-index will be positioned over the calling object
	 *  
	 *  @return the new object
	 **/
	public static SceneObject triggerCloneObject(
			SceneObject callingobject, SceneObject objectToClone,
			String newname, int dx, int dy, int dz) {
		
		if (objectToClone==null){			
			Log.severe("Was cloneing a new object to be called "+newname+" but could not find source object");	
			return null;
		}

		Log.warning("cloneing: "+objectToClone.getName()+" as "+newname+" at location:"+dx+","+dy+","+dz);

		// get the object to clone		
	//	SceneObject curobject = SceneObjectDatabase.getSingleSceneObjectNEW(objectToClone,callingobject,true);
		SceneObject newobject =  objectToClone.returnclone(newname);


		if (newobject==null){			
			Log.severe("cloneing of: "+objectToClone+" failed :(");	
			return null;
		}
		//temp removed; (new system should copypin correctly

		//set it by pin if appropriate (the initial cloning clears the pin state as it uses state loading and that always loads by top left)
		//newobject.objectsCurrentState.PinPointX = curobject.objectsCurrentState.PinPointX;
		//newobject.objectsCurrentState.PinPointY = curobject.objectsCurrentState.PinPointY;

		Log.warning("setting position of clone "+dx+","+dy+","+dz);
		
		//finally we set it to the requested location		
		newobject.setPosition(dx, dy,dz, true);
		
		//set scene to calling object
		if (callingobject!=null && callingobject.getParentScene()!=null){
			SceneWidget callingObjectsScene = callingobject.getParentScene();
			newobject.setObjectsScene(callingObjectsScene);
		} else {
			newobject.ObjectsLog("Clone created but no callingobject, assuming the scene of the source object instead");			
		}
		//--
		

		//Log.warning("setting zindex of clone ");
		//also set the current x/y on its movement.

		// its clone OVER so the zindex should be the source objects +1
		// btw, I say zin-dex rather then Z-index as its quicker....

		if (callingobject!=null){
			newobject.setZIndex(callingobject.getZindex() + 1); //this means that if a clone is fired from, say, a chair to make a fire, the fire will go ontop of it.
			newobject.getObjectsCurrentState().spawningObject = callingobject; //remember the object that spawned it, if any
		} else {
			Log.info("no calling object so not setting zindex on clone");
		}


		newobject.getParentScene().AllDynamicObjects.add(newobject);

		Log.warning("triggerActionsToRunWhenCloned");
		newobject.triggerActionsToRunWhenCloned();

		Log.info("cloned "+objectToClone+"object");		
		//Log.info("cloned object should be on "+newobject.getParentScene().SceneFileName);
		//Log.info("testing if "+newobject.getObjectsCurrentState().ObjectsName+" is on that scene: "+newobject.getParentScene().isObjectInScene(newobject));

		return newobject;
	}




	/**
	 * displace one or more objects 
	 * (moved here for neatness as its a complex thing)
	 * 
	 * @param objects
	 * @param dx
	 * @param dy
	 * @param dz
	 * @param detectCollision
	 * @param callingObject
	 */
	private static void displaceObjects(String objects, int dx, int dy, int dz, boolean detectCollision,SceneObject callingObject) {

		Set<? extends SceneObject> curobjects;
		if (!objects.isEmpty()){ 
			curobjects = SceneObjectDatabase.getSceneObjectNEW( objects, callingObject,  true); 
		} else {
			curobjects = Sets.newHashSet(callingObject);
		}

		// set their states
		for (SceneObject object : curobjects) {

			int nx = object.getX() + dx; //work out the new location of the pivot
			int ny = object.getY() + dy;
			int nz = object.getZ() + dz;

			Log.info("to " + nx + " " + ny+" "+nz);

			if (detectCollision){
				// by default the position to test against if the object topleft:
				// if (widget.getParentScene().TestForCollision(nx, ny)==false){
				// widget.setPosition(nx, ny);
				// }

				// by default, we use the objects bounding box determined by its size;

				//location of new top left (note we ignore the pivot)				
				int tlx = object.getTopLeftBaseX() + dx; 
				int tly = object.getTopLeftBaseY() + dy;
				//location of new bottom right
				int brx = tlx + object.getPhysicalObjectWidth(); //used to be offset
				int bry = tly + object.getPhysicalObjectHeight();


				//TODO: implement bounding box;
				//(if a bounding box is specified though, we use that instead)
				//(bounding boxs are relative to the top left of the object)
				//(tlx= TopLeftX + bbtlx + dx)
				//(tly= TopLeftY + bbtly + dy);
				//(brx = TopLeftX + bbbrx + dx)
				//,,etc

				if (object.getParentScene().TestForBoxCollision(tlx, tly, brx,bry) == false) {

					object.ObjectsLog("Setting Requested Position:"+nx+","+ny+","+nz,"#00FF00");					
					object.setPosition(nx, ny,nz, true);


				} else {

					Log.info("(collision so no movement) ");

				}
			} else {

				object.ObjectsLog("Setting Requested Position:"+nx+","+ny+","+nz,"#00FF00");		
				object.setPosition(nx, ny,nz, true);
			}
		}


	}

	/**
	 * 
	 * Note: special case:if the object is positioned relative, a relative path is created. No collision detection is used either.
	 * 
	 * @param object
	 * @param tx
	 * @param ty
	 * @param speed
	 * @param callingObject
	 * @param ignoreCollisions
	 * @param searchGlobal
	 */
	private static void triggerMoveObjectsTo(String object, int tx, int ty, int speed, SceneObject callingObject,boolean ignoreCollisions, boolean searchGlobal) {


		// get the objects
		Set<? extends SceneObject> curobjects = SceneObjectDatabase.getSceneObjectNEW(object,callingObject,searchGlobal);


		// set its state
		for (SceneObject so : curobjects) {

			Log.info("Setting " + so.getObjectsCurrentState().ObjectsName + " moving to " + tx + " " + ty+" using collisions on scene "+so.getParentScene().SceneFileName);

			// find path
			MovementPath mp;

			boolean positionedRelative = so.getObjectsCurrentState().positionedRelativeToo!=null;

			if (ignoreCollisions || positionedRelative){						
				Log.info("told to ignore collisions");
				//if ignoring collisions we can generate a simple two point path directly.
				mp = new MovementPath("", "_internal_");

				if (positionedRelative) {
					mp.add(new MovementWaypoint(so.getObjectsCurrentState().relX, so.getObjectsCurrentState().relY,so.getObjectsCurrentState().relZ, MovementType.AbsoluteMove)); 
					//dont get confused with this saying absolute. Relative movement mode isnt the same as relative positioning using absolute movement. Relative movement is when each movement is relative to the last position - which isnt what we want. We want every waypoint relative to the start point, which is 0,0.							

				} else {
					mp.add(new MovementWaypoint(so.getX(), so.getY(), so.getZ() ,MovementType.AbsoluteMove)); 
				}

				mp.add(new MovementWaypoint(tx, ty, MovementType.AbsoluteLineTo));

			}	else {
				//we use the scene to generate a safe path, which avoids collisions
				mp = so.getParentScene().getSafePath(tx, ty, so); 
			}

			//if path finding broke
			if (mp.pathFindingBroke && so.getObjectsCurrentState().pathfinding==pathfindingMode.strict){
				Log.info("pathfinding broke and object on strict mode ");
				return;		//dont move at all		
			}

			//	Log.info("_______________________mp path length:" + mp.PathLength);

			// add and set path going
			so.playMovementAtFixedSpeed(mp, speed);


		}
	}

	private static void triggerAInventoryList(String inventoryname,
			String objectosendactionstoo,SceneObject callingObject) {

		//SceneDialogObject curobject = SceneWidget
		//		.getTextObjectByName(objectosendactionstoo)[0];

		Log.info("triggering inventory list: " + inventoryname + " "
				+ objectosendactionstoo);

		//why label? shouldnt this be any object?
		Set<? extends IsSceneLabelObject> curobjectset = SceneObjectDatabase.getTextObjectNEW(objectosendactionstoo,callingObject,true);

		//we need just one element from this set, as we can only send the actions back to one object
		//first we warn if we have more then one object
		if (curobjectset.size()>1){
			Log.info("Warning: more then one object found for "+objectosendactionstoo+". Inventory Lists should only have one object to send actions too specified");
		}
		//we just use the first object he iterator returns (we cant use .get(0) as Sets have no order and thus no numbered positions!)
		IsSceneLabelObject curobject = curobjectset.iterator().next();

		InventoryPanelCore inventoryTouse = JAMcore.allInventorys.get(inventoryname);

		if (inventoryTouse==null){
			Log.info("inventory:"+inventoryTouse+" not found. Current Inventorys are:"+JAMcore.allInventorys.keySet().toString());
		}

		final SelectItemFromInventoryMenu testpopup = new SelectItemFromInventoryMenu(inventoryTouse, curobject);

		Log.info("displaying menu at: " + CurrentScenesVariables.lastclickedscreen_x + " "+ CurrentScenesVariables.lastclickedscreen_y);

		// ensure its above everything
		//((Widget)testpopup.getVisualRepresentation()) .getElement().getStyle().setZIndex(JAMcore.z_depth_max + 10);

		((IsPopupPanel)testpopup.getVisualRepresentation()).setZIndexTop();

		// --
		// testpopup.setPopupPosition(lastclicked_x, lastclicked_y);

		testpopup.setPopupPositionAndShow(new JamPositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				testpopup.setPopupPosition(CurrentScenesVariables.lastclickedscreen_x
						- (offsetWidth / 2), CurrentScenesVariables.lastclickedscreen_y);
			}

		});

		//	testpopup.show();
	}

	/**
	 *  triggers a display menu to appear at the mouse location.
	 * If one is already open,it adds the new menu options to it 
	 * **/
	private static void triggerADisplayMenu(hasUserActions callingObject, String[] items) {

		final SceneMenuWithPopUp testpopup;

		//if menu already showing we add to it
		if (SceneMenuWithPopUp.menuShowing){
			Log.info("adding to menu");
			SceneMenuWithPopUp.addItemsToCurrentMenu(items,callingObject);
			return;

		}

		// set correct last object based on which is null
		if (callingObject == null) {
			testpopup = new SceneMenuWithPopUp(items, CurrentScenesVariables.lastInventoryObjectClickedOn);
		} else {
			testpopup = new SceneMenuWithPopUp(items, callingObject);
		}

		Log.info("displaying menu at: " + CurrentScenesVariables.lastclickedscreen_x + " "	+ CurrentScenesVariables.lastclickedscreen_y);

		// ensure its above everything
		//testpopup.getElement().getStyle().setZIndex(JAMcore.z_depth_max + 10);
		//((Widget)testpopup.getVisualRepresentation()) .getElement().getStyle().setZIndex(JAMcore.z_depth_max + 10);


		((IsPopupPanel)testpopup.getVisualRepresentation()).setZIndexTop();

		// --
		// testpopup.setPopupPosition(lastclicked_x, lastclicked_y);

		testpopup.setPopupPositionAndShow(new JamPositionCallback() {
			@Override
			public void setPosition(int offsetWidth, int offsetHeight) {
				testpopup.setPopupPosition(CurrentScenesVariables.lastclickedscreen_x
						- (offsetWidth / 2), CurrentScenesVariables.lastclickedscreen_y
						- (offsetHeight / 2));
			}

		});

		//testpopup.show();
		Log.info("menuShowing: "+SceneMenuWithPopUp.menuShowing);
	}

	/** gets a position from a string **/
	private static int ProcessPosition(String positionString,IsSceneObject callingObject) {

		int pos;

		if (positionString.equalsIgnoreCase("<LastObjectsX>")) {
			pos = callingObject.getX();
		} else if (positionString.equalsIgnoreCase("<LastObjectsY>")) {
			pos = callingObject.getY();
		} else if (positionString.equalsIgnoreCase("<LastClickedX>")) {
			pos = CurrentScenesVariables.lastclicked_x;
		} else if (positionString.equalsIgnoreCase("<LastClickedY>")) {
			pos = CurrentScenesVariables.lastclicked_y;
		} else {
			pos = Integer.parseInt(positionString);
		}

		return pos;
	}



	/**
	 * 
	 * now we have a specific command, we run it! (assuming its recognized)
	 * 
	 * We also get the correct property to use, if we detect a random set specified
	 * (random possibility's are just separated by a |)
	 * 
	 * **/
	private static void runCommand
	(
			CommandLine currentCommand,
			//GameCommands currentCommand,
			CommandParameterSet Parameters, 
			String UniqueTriggerIndent,
			CommandList instructionset, 
			SceneObject callingObject
			) {


		if (currentCommand.TheCommand==GameCommands.notarecognisedcommand){
			Log.info("no recognised command specified");
			return;
		}
		/*
		//if there's a | thats NOT between <> then we split and pick random one
		//this is an old randomization system thats powerful, but unprecise and should be used with care
		//other variables and random selection systems are now between <> which is why we only look for a | in this case if no < is precent
		//this prevents conflicts with new stuff
		if (!CurrentProperty.contains("<") && CurrentProperty.contains("|")){					
			Log.info("random property set detected");

			CurrentProperty=getRandomProperty(CurrentProperty);
			Log.info("CurrentProperty picked = "+CurrentProperty);

		}
		 */
		//else get the cached split in the CommandParameters variable



		// extract params (we should phase the use of CurrentParams[] out

		//phase this out -we should use commandparameterset directly, not via a string array
		String CurrentParams[] = Parameters.getProccessedSplit(); //  theParameters.split(",");		

		String theParameterString   = Parameters.getProccessedParameterString();//theParameters should be phased out of use and we should use Parameters directly




		//save the commands to the command stack for debugger
		if (!tempDisableLog && currentCommand.TheCommand!=GameCommands.command_log_off ){ //we also dont log the log off command!
			GameStatistics.addLastCommandToStack(currentCommand.toString()+":"+Parameters.toString());
		}

		//TODO: combine these commands, the split is becoming pretty pointless
		//These are also pretty packed with redundancies because we need to phase the old pre-parameter set system out
		checkSceneRelatedCommands(currentCommand, theParameterString, Parameters,	UniqueTriggerIndent, instructionset, callingObject, CurrentParams);

		//	checkGameRelatedCommands(currentCommand,  theParameterString, Parameters,  UniqueTriggerIndent, instructionset, callingObject, CurrentParams);


	}


	/**
	 * disables logging commands till the end of the action set
	 */
	static boolean tempDisableLog = false;

	/*
	private static void checkGameRelatedCommands(
			CommandLine.GameCommands currentCommand,
			String theParameterString, //phase out
			CommandParameterSet Parameters, 
			String UniqueTriggerIndent,
			CommandList instructionset,
			SceneObject callingObject, 
			String[] CurrentParams) //phase out
	{

		switch (currentCommand){


		}
	}*/


	public static void triggerPocketObject(String CurrentProperty,
			String[] CurrentParams, SceneObject callingObject) {

		String objectNamepo  = CurrentParams[0].trim();

		Log.info("triggering pocket object CurrentProperty="+CurrentProperty+"");
		Log.info("triggering pocket object CurrentParams="+CurrentParams+"("+CurrentParams.length+")");

		Set<? extends SceneObject> curobjectpo;// = SceneWidget.getSpriteObjectByName(objectNamepo,callingObject);

		curobjectpo = SceneObjectDatabase.getSceneObjectNEW(objectNamepo,callingObject,true); 

		for (SceneObject so : curobjectpo) {

			//old ;
			/*
			//correct the name (in case it was a variable)
			objectNamepo=so.getObjectsCurrentState().ObjectsName;

			//make scene object invisible
			so.setVisible(false);

			//add a new item with the same name
			if (CurrentProperty.contains(",")) {
				addItemToInventory(InventoryPanel.PocketedPrefix+objectNamepo, CurrentParams[1],objectNamepo);			
			} else {
				addItemToInventory(InventoryPanel.PocketedPrefix+objectNamepo, null,objectNamepo);
			}*/

			//trigger pocket object with optional inventory param
			if (CurrentParams.length>1){			
				so.triggerPocketObject(CurrentParams[1],true); //WE ALSO tell it to detach itself from anything its positioned relative too
			} else {
				so.triggerPocketObject(null,true); //WE ALSO tell it to detach itself from anything its positioned relative too

			}

			//add anything attached as well; (which calls this whole function again)
			//for (SceneObject attachedObject : so.relativeObjects) {
			//needs optimising as this researchs for objects above when we already have it
			//	triggerPocketObject(CurrentProperty,CurrentParams, attachedObject);
			//}




		}
	}

	public static void tigMessage(String CurrentProperty) {
		// progressive messages
		if (CurrentProperty.indexOf(">>") > -1) { 
			// test location we are at in ans message memory
			//JAM.DebugWindow.addText("display message:"); 

			int CurrentItem = 0;

			//NOTE: for some reason this relays on answer box...hmm
			if (MultiMessagePlace.GetItem(JAMcore.AnswerBox.get().getText().trim()).isEmpty()) {
				// set it to item 0
				CurrentItem = 0;

			} else {
				// get current item
				CurrentItem = Integer
						.parseInt(MultiMessagePlace
								.GetItem((JAMcore.AnswerBox.get().getText())));
				//	JAM.DebugWindow
				//	.addText("message num:" + CurrentItem); //$NON-NLS-1$
			}

			//JAM.DebugWindow
			//.addText("display message:" + CurrentItem); //$NON-NLS-1$
			// display message number..
			int totalmessages = CurrentProperty
					.split("(\">>\")|(\" >> \")").length; //$NON-NLS-1$
			if (CurrentItem > totalmessages) {
				CurrentItem = 0;

			}

			CurrentProperty = CurrentProperty.split("(\">>\")|(\" >> \")")[CurrentItem]; //$NON-NLS-1$

			CurrentItem = CurrentItem + 1;

			MultiMessagePlace.RemoveItem((JAMcore.AnswerBox.get().getText().trim()));
			MultiMessagePlace.AddItem("" + CurrentItem, (JAMcore.AnswerBox.get().getText().trim())); //$NON-NLS-1$

			//JAM.DebugWindow
			//.addText("get item test" + MultiMessagePlace.GetItem("thistest") + ":"); //$NON-NLS-1$ 

			//$NON-NLS-2$ //$NON-NLS-3$

		}

		// if theres a , in the propertys then we randomize
		if (CurrentProperty.indexOf("\",\"") > -1) { //$NON-NLS-1$

			int totalmessages = CurrentProperty.split("\",\"").length; //$NON-NLS-1$
			int selectthis = (int) (Math.random() * totalmessages);
			// note - to increase speed a precompiled regex can be used
			// here.
			CurrentProperty = CurrentProperty.split("(\",\")|(\" , \")")[selectthis]; //$NON-NLS-1$

		}
		CurrentProperty = CurrentProperty.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$
		// toggleimagegroupPopUp.TiGMyApplication.Feedback.setText(CurrentProperty);
		TigItemCore.lastclickedTig.setFeedbackText(CurrentProperty);
		//TiGFeedback.setText(CurrentProperty);

	}

	public static void stopObjectsSound(String CurrentProperty,
			String[] CurrentParams, SceneObject callingObject) {

		Set<? extends IsSceneObject> curobjectsos;

		//String soundFilename;
		if (CurrentProperty.contains(",")) {

			// if there's a comma, the first part is the object to apply it
			// too
			String object = CurrentParams[0].trim();
			//soundFilename = CurrentParams[1].trim();

			// get the objects
			//curobjectsos = SceneWidget.getSpriteObjectByName(object,callingObjects);

			curobjectsos = SceneObjectDatabase
					.getSceneObjectNEW(object,callingObject,true);


		} else {
			//curobjectsos =new SceneObject[]{lastSpriteObjectClickedOn};
			//curobjectsos = new HashSet<SceneSpriteObject>(); 
			//	curobjectsos.add(callingObject);

			Set<IsSceneObject> resultobjects = new HashSet<IsSceneObject>();
			resultobjects.add(callingObject);
			curobjectsos = resultobjects;
		}
		if (curobjectsos!=null){

			//loop over selected objects stopping the sounds
			for (IsSceneObject so : curobjectsos) {
				so.clearObjectsSounds();

				//if (so.ObjectsSound!=null){
				//	so.ObjectsSound.stop();
				//	so.ObjectsSound=null;
				//}
			}


		} else {
			Log.info("failed to cancel sound, null object error");
		}
	}

	
	
/**
 * Sets a sound loop on a object
 * 
 * @param soundFilename
 * @param applytoo - tells these objects to play the sound
 * @param Vol - 0-100 vol
 */
	public static void playObjectsSound(String soundFilename,
			Set<? extends SceneObject> applytoo, int Vol) {
		
		
		String soundLocation = RequiredImplementations.BasicGameInformationImplemention.get().getAudioLocation() + soundFilename;
		
		for (SceneObject so : applytoo) {
			
			so.setSound(soundLocation, Vol);	
			so.playSoundLoop();
		}

		
	}
	
	/*
	public static void playObjectsSound_old(String CurrentProperty,
			String[] CurrentParams, SceneObject callingObject) {

		Set<? extends SceneObject>  curobject;		
		String soundFilename;
		int vol = 100;

		if (CurrentProperty.contains(",")) {

			// if there's a comma, the first part is the object to apply it
			// too
			String object = CurrentParams[0].trim();
			soundFilename = CurrentParams[1].trim();

			// get the objects
			//curobject = SceneWidget.getSpriteObjectByName(object,callingObject);

			curobject = SceneObjectDatabase.getSceneObjectNEW(object,callingObject,true);


			// default type used

		} else {

			//	curobject = new HashSet<SceneObjectVisual>(); 
			//curobject.add(callingObject);			
			Set<SceneObject> resultobjects = new HashSet<SceneObject>();
			resultobjects.add(callingObject);
			curobject = resultobjects;

			soundFilename = CurrentProperty;
		}


		String soundLocation = RequiredImplementations.BasicGameInformationImplemention.get().getAudioLocation() + soundFilename;
		
		// loop over all selected objects setting their sounds
		for (SceneObject so : curobject) {
			//this set sound should probably be within the sceneobject implementation itself
			//as sound is implementation specific
			//	so.setSound(AudioController.soundController.createSound(stype, JAM.audioLocation_url + soundFilename));

			//	so.setSound(JAM.audioLocation_url + soundFilename);		

			
			so.setSound(soundLocation, vol);	

			so.playSoundLoop();

		}




	}*/


	public static void addItemToInventory(String itemName,
			String inventoryName,
			SceneObject associatedObjectName) {

		Log.info("adding object "+itemName+" to:"+inventoryName);

		InventoryPanelCore inventoryTouse;


		// get the correct inventory if one is specified
		if (inventoryName!=null) {

			inventoryTouse = JAMcore.allInventorys.get(inventoryName);

			if (inventoryTouse == null) {
				inventoryTouse = InventoryPanelCore.defaultInventory;
			}

		} else {// else assume its the default
			inventoryTouse = InventoryPanelCore.defaultInventory;

		}



		if (inventoryTouse.inventoryContainsItem(itemName) == false) {
			Log.info("Adding " + itemName + " to the inventory \n"); //$NON-NLS-1$ //$NON-NLS-2$

			if (inventoryTouse.getInventorysButton()!=null){
				inventoryTouse.getInventorysButton().setPlayForwardThenBack();			
			}

			inventoryTouse.AddItem(itemName,false,associatedObjectName);


		} else {

			RequiredImplementations.setCurrentFeedbackText(GamesInterfaceTextCore.MainGame_YouAlreadyHaveThisItem);

		}

	}

	private static String triggerMessage(String CurrentProperty) {
		// progressive messages
		if (CurrentProperty.indexOf(">>") > -1) { //$NON-NLS-1$
			// test location we are at in ans message memory
			//JAM.DebugWindow.addText("display message:"); //$NON-NLS-1$

			int CurrentItem = 0;
			if (MultiMessagePlace.GetItem(
					JAMcore.AnswerBox.get().getText().trim()).isEmpty()) {
				// set it to item 0
				CurrentItem = 0;

			} else {
				// get current item
				CurrentItem = Integer.parseInt(MultiMessagePlace
						.GetItem((JAMcore.AnswerBox.get().getText())));
				//JAM.DebugWindow.addText("message num:" + CurrentItem); //$NON-NLS-1$
			}

			//JAM.DebugWindow.addText("display message:" + CurrentItem); //$NON-NLS-1$
			// display message number..
			int totalmessages = CurrentProperty.split("(\">>\")|(\" >> \")").length; //$NON-NLS-1$
			if (CurrentItem > totalmessages) {
				CurrentItem = 0;

			}

			CurrentProperty = CurrentProperty.split("(\">>\")|(\" >> \")")[CurrentItem]; //$NON-NLS-1$

			CurrentItem = CurrentItem + 1;

			MultiMessagePlace.RemoveItem((JAMcore.AnswerBox.get()
					.getText().trim()));
			MultiMessagePlace
			.AddItem(
					"" + CurrentItem, (JAMcore.AnswerBox.get().getText().trim())); //$NON-NLS-1$

			//JAM.DebugWindow
			//.addText("get item test" + MultiMessagePlace.GetItem("thistest") + ":"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		}

		// if theres a , in the propertys then we randomize
		if (CurrentProperty.indexOf("\",\"") > -1) { //$NON-NLS-1$

			int totalmessages = CurrentProperty.split("\",\"").length; //$NON-NLS-1$
			int selectthis = (int) (Math.random() * totalmessages);
			// note - to increase speed a precompiled regex can be used
			// here.
			CurrentProperty = CurrentProperty.split("(\",\")|(\" , \")")[selectthis]; //$NON-NLS-1$

		}
		CurrentProperty = CurrentProperty.replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$

		//	JAM.messagehistory.AddNewMessage("<div class=\"MyApplication.messagehistoryReplyStyle\" >  " + CurrentProperty + "</div>"); //$NON-NLS-1$ //$NON-NLS-2$

		FeedbackHistoryCore.AddNewMessage("<div class=\"MyApplication.messagehistoryReplyStyle\" >  Score Awarded:" + 
				CurrentProperty + "</div>");


		//really only needs to be setup once?
		//not sure why its constantly run and removed each time message is triggered
		//JAM.Feedback.setRunAfterTextSet(
		RequiredImplementations.setCurrentFeedbackRunAfter(new Runnable(){
			@Override
			public void run() {
				//first test for global keyboard actions
				//	InstructionProcessor.testForGlobalActions(TriggerType.OnMessageEnd, null, null);

				JAMcore.testForGlobalActions(TriggerType.OnMessageEnd, null, null);

				//then scene specific actions
				if (SceneObjectDatabase.currentScene!=null){
					SceneObjectDatabase.currentScene.testForSceneActions(
							TriggerType.OnMessageEnd,  null);
				}
				//remove this runnable when done
				RequiredImplementations.setCurrentFeedbackRunAfter(null);
			}

		});

		//run onMessageStart
		//first test for global keyboard actions
		InstructionProcessor.testForGlobalActions(TriggerType.OnMessageStart, null, null);

		//then scene specific actions
		if (SceneObjectDatabase.currentScene!=null){
			SceneObjectDatabase.currentScene.testForSceneActions(
					TriggerType.OnMessageStart,  null);
		}

		//Set the message
		//		JAM.Feedback.setText(CurrentProperty);
		RequiredImplementations.setCurrentFeedbackText(CurrentProperty);

		return CurrentProperty;
	}

	/** loads the specified scene into a new tab **/
	private static void loadSceneIntoNewTab(String CurrentProperty,String locationname, boolean autopopUp) {
		loadSceneIntoNewTab( CurrentProperty, locationname,  autopopUp,false); 
	}

	/** loads the specified scene into a new tab **/
	public static void loadSceneIntoNewTab(String CurrentProperty,String locationname, final boolean autopopUp,final boolean silentmode) {

		//Scenes need to be loaded in the correct case due to windows file systems being case sensative
		//CurrentProperty=CurrentProperty.toLowerCase();
		//----



		Log.info("Loading Scene " + CurrentProperty + " \n");

		// detect if scene is already open
		if (SceneWidget.sceneExists(CurrentProperty)) {

			//SceneWidgetVisual.all_scenes.containsKey(CurrentProperty)				
			Log.info("Scene already present:.");

			if (autopopUp){
				bringSceneToFront(CurrentProperty,silentmode);
			}

		} else {

			Log.info("Current  Loaded scenes "+SceneWidget.getAllSceneNames().size()+"| "+SceneWidget.getAllSceneNames().toString());
			Log.info("Scene Not Loaded: creating new scene...");

			if (autopopUp){

				JAMcore.triggerSelectCheck = true;
				JAMcore.pageToSelect = CurrentProperty+"__scene";

			}

			// make the scene, taking the information from its scenes directory			
			final SceneWidget newScene = SceneObjectFactory.makeNewScene(CurrentProperty);
			//new SceneWidgetVisual(CurrentProperty);



			//tells the scene currently loading if it should load normally (false) or if
			//it should suppress all "onSceneLoad" style actions from autorunning
			newScene.loadingsilently = silentmode;

			if (autopopUp){
				//Log.info("setting current scene to "+newScene.SceneFileName);
				//TODO: probably remove as bringSceneToFront does this
				//SceneObjectDatabase.currentScene = newScene;
				//Log.info("current scene set to_:"+SceneObjectDatabase.currentScene.SceneFileName);
			}

			JAMcore.CurrentScenesAndPages.addNewSceneToSet(newScene,
					CurrentProperty, locationname,autopopUp,silentmode); //autopopUp used to just be true so it was always automatically made active


			/*
			IsTimerObject smalldelay = JAMTimerController.getNewTimerClass(new Runnable() {		
				@Override
				public void run() {



				}
			});*/
			
			
			JAMTimerController.scheduleDefered(new Runnable() {				
				@Override
				public void run() {
					newScene.intialize();//need to initialize after attaching, this is because initialize is what attaches the objects to the scene and that might require looking for element IDs

					if (autopopUp){		
						Log.info("---------");
						Log.info("bringSceneToFront:"+newScene.SceneFileName);

						//maybe we need to auto pop it up here as well as setting it current? (to ensure loading is visible?)
						bringSceneToFront(newScene,silentmode); //Doesn't work - breaks something, maybe currentScene variable gets  out of sycn?

						//TODO: this should probably be removed as bringSceneToFront fires it itself via setActiveScene
						//if (!silentmode){
						//	newScene.onSceneMadeCurrent(); //only if not silent mode
						//}//

					}
				}
			});

			
			//smalldelay.schedule(500); //TODO:This delay was to ensure scene is attached on dom first
			//(Doesn't seem to help, remove after more testing
			//Is there any way either to tell when we are attached to the dom rather then use a fixed time guess?
			//Or some way to tell if we need to wait at all?

		}
	}

	public static void bringSceneToFront(String scenename,boolean silentmode) {
		scenename=scenename.toLowerCase();
		Log.info("Bringing scene to front!"+scenename);


		SceneWidget scene = SceneWidget.getSceneByName(scenename);

		bringSceneToFront(scene,silentmode);

	}

	public static void bringSceneToFront(SceneWidget sceneToFocus,boolean silentmode) {

		SceneWidget scene = (SceneWidget)sceneToFocus;


		//SceneObjectDatabase.currentScene = scene;  //No longer needed, as we fire setActiveScene below, which does this
		
		Log.info("current scene set to_:"+scene.SceneFileName);

		String scenename =scene.SceneFileName; //used to be scene.scenesData.SceneFolderName;

		
		Log.info(" selecting:"+scenename);

		//adds __scene at the end if needed
		if (!scenename.endsWith("__scene")){
			scenename=scenename+"__scene";
		}


		//int tabindex=MyApplication.CurrentLocationTabs.getWidgetIndex(container);
		Log.info(" selecting:"+scenename);

		JAMcore.CurrentScenesAndPages.selectPage(scenename);

		Log.info(" setting size ");		
		//check and update size 
		scene.resizeScene();

		//Log.info(" tab index:  "+tabindex);
		//	Log.info(" tab selected out of "+((GWTSceneAndPageSet)JAMcore.CurrentScenesAndPages).visualContents.getWidgetCount());
		SceneWidget.setActiveScene(scene,silentmode);

	}




	/** checks conditional, return true if the conditions are passeed 
	static boolean checkConditionals(ConditionalList CONDITIONS,SceneObject callingObject) {


		return CONDITIONS.checkConditionals(CONDITIONS, callingObject);


	}**/

	//new test (vars should have already been processed)
	public static int getNumericalValue(String valueToWorkOut,SceneObject currentObject){//TODO: phase out
		return Integer.parseInt(valueToWorkOut); 
	}

	/** gets a numerical value from a script expression
	 * ie. <objectname:objectX>-50 would return the objects x value - 50**/
	@Deprecated
	public static int getNumericalValue_nu(String valueToWorkOut,SceneObject currentObject){
		//NOTE: This function should not be needed for InstructionProcessor functions
		//All the properties are scanned for values/variables before we even check what function we are running

		double val=-1;

		//replace all variables
		if (valueToWorkOut.contains("<")){

			//split and get parts

			valueToWorkOut = replaceVarValuesInString(valueToWorkOut, currentObject); //temp maybe

		}

		//replace a random statement
		if (valueToWorkOut.contains("random()")){

			while (valueToWorkOut.contains("random()"))
			{				
				valueToWorkOut = valueToWorkOut.replace ("random()", ""+Math.random());				
			}


		}

		//we detect if its a calculation
		//eg <objectname:objectX>-50
		if (SpiffyCalculator.isCalculation(valueToWorkOut)){
			Log.info("calculation detected");


			//solve the maths
			val = SpiffyCalculator.AdvanceCalculation(valueToWorkOut);
			Log.info("result is :"+val);

		} else {

			if (valueToWorkOut.matches("^[-+]?\\d+(\\.\\d+)?$")){
				val = Integer.parseInt(valueToWorkOut);
			} else {
				Log.info("getting value of:"+valueToWorkOut+" failed because ITS NOT A NUMBER. ");

			}
			//parse to ensure its a valid number
			/*
			try {

				val = Integer.parseInt(valueToWorkOut);

			} catch (NumberFormatException e) {

				Log.info("getting value of:"+valueToWorkOut+" failed because ITS NOT A NUMBER. ");


			}*/

		}



		//round it to the nearest whole number and return it
		return  (int) Math.round(val);
	}

	private static String replaceVarValuesInString(String valueToWorkOut, SceneObject currentObject) {
		String parts[] = (" spacer " +valueToWorkOut).split(">|<");
		int i = 1;
		for (String match : parts) {

			//ensure we are at a match and not a inbetween bit
			//as the first part will always be outside the match, we know even numbers are inside
			if((i%2)==0){

				String replacethis = "<"+parts[i-1]+">";
				String replacement = getValue(replacethis,currentObject);

				Log.info("replacing:"+replacethis+" with "+replacement);

				valueToWorkOut = valueToWorkOut.replace(replacethis, replacement);

				Log.info("new valueToWorkOut is :"+valueToWorkOut);
				//	parts = valueToWorkOut.split(">");
			}


			i++;


		}

		/*
		while (valueToWorkOut.contains("<"))
		{

			//replace first part
			String replacethis = parts[0]+">";
			String replacement = getValue(replacethis,currentObject);

			Log.info("replacing:"+replacethis+" with "+replacement);

			valueToWorkOut = valueToWorkOut.replace(replacethis, replacement);


			Log.info("new valueToWorkOut is :"+valueToWorkOut);
			parts = valueToWorkOut.split(">");
		}*/

		return valueToWorkOut;
	}

	//use getNumericalValue instead
	/** 
	 * gets the value from a string and the current object. 
	 * This could be a variable, or a attribute of an object like its position 
	 * 
	 * examples;<br>
	 * {@literal <}objectdirection>    = currentobjects direction <br>
	 * {@literal <}objectX> 		   = currentobjects x position <br>
	 * {@literal <}objectScreenY>      = currentobjects y position on screen <br>
	 * {@literal <}objectname:objectX> = objectnames current x position <br>
	 * {@literal <}{@literal <}TOUCHER>:objectX> = the TOUCHING objects current x position (other variables can be used provided they return a object)<br>
	 * 
	 * 
	 * **/
	//Note "{@literal <}" is used in the examples above because else eclipse will think <objectx> etc is a real tag for html styling the doc!
	//hover over getValue below too see it correctly styled
	public static String getValue(String nametocheck,IsSceneObject IscurrentObject) {

		Log.fine("getting value of:"+nametocheck);


		SceneObject currentObject = (SceneObject)IscurrentObject;//temp cast till we have support fully on the interface
		//check if its just a number


		//temp for debug
		if (nametocheck.equalsIgnoreCase("<>")){

			Log.warning("name was <>");

			if (currentObject!=null){
				Log.warning("sourceobject:"+currentObject.getName());
			}

			//DebugElement lastcom = GameStatistics.lastCommandsStack.peek();
			//Log.warning("last command:"+lastcom.debugstring);
			/*
			Iterator<DebugElement> dit = GameStatistics.lastCommandsStack.iterator();
			while (dit.hasNext()) {
				GameStatistics.DebugElement lastcom = (GameStatistics.DebugElement) dit.next();
				Log.warning(",\n"+lastcom.debugstring);

			}*/


		}
		//-----------

		try {

			int val=ProcessPosition(nametocheck,IscurrentObject); 
			return ""+val;

		} catch (NumberFormatException e) {
			//Log.info("getting value of:"+nametocheck);
		}

		//if theres no < and its not a number (see above) then its just a string specifying a variable
		if (!nametocheck.startsWith("<")){
			Log.info("Value :"+nametocheck+" is not a variable. If you meant to refer to a global variable, it should be in the style <V:#############> ");
			return nametocheck; //if it was not a variable we return it as-is

			//return GameVariableManagement.GameVariables.GetItem(nametocheck);
		}

		// if the name containers a colon, we separate out the object name and use that as the default object
		// eg <objectname:objectX>
		boolean objectSpecified = false; //if the object is specified or left as default

		int locationOfColon = nametocheck.indexOf(":");
		int locationOfBracket = nametocheck.indexOf("(");

		//If we have a colon not inside a bracket, then we split of what is before it as the object
		//we are referring too
		if (locationOfColon>0 && (locationOfBracket==-1 || locationOfBracket>locationOfColon )){

			objectSpecified = true;

			nametocheck=nametocheck.substring(1,nametocheck.length()-1); //remove opening < 

			//Note; Colons can also be used in semantics as prefixes
			//eg, <redsweet:propertyvalue(dbo:colour)>
			//we thus only want to split by the first colon
			//
			String[] split = nametocheck.split(":",2);
			String objectName = split[0].trim();
			String valueName  = split[1].trim(); 

			Log.fine("objectName:"+objectName);
			Log.fine("valueName:"+valueName);

			//if objectName is just V then we are looking for a global variable
			if (objectName.equalsIgnoreCase("V")){

				//	if (nametocheck.startsWith("<") && nametocheck.endsWith(">")){
				//	nametocheck = nametocheck.substring(0,nametocheck.length()-1);
				Log.warning("Getting global valueName:"+valueName);

				return GameVariableManagement.GameVariables.GetItem(valueName.toLowerCase());

				//	}

			}

			//Log.info("objectName:"+objectName);

			//currentObject = SceneWidget.getSceneObjectByName(objectName,null)[0];
			Log.info("looking for variable using object "+objectName);

			currentObject =  SceneObjectDatabase.getSingleSceneObjectNEW(objectName, currentObject, true); //for some reason calling object used to be null
			if (currentObject==null){
				Log.warning("Can not find :"+objectName+" in object database, so cant check its variables");
				return ConditionalLine.ERROR_CANT_FIND_OBJECT_SIGNIFIER;

			}

			nametocheck="<"+valueName+">";//with brackets again



		}
		Log.fine("var is: "+nametocheck);

		// check if its a object reserved variable
		//NOTE: its kinda stupid we are sending a string back only to convert to a number again
		//But this is the easiest to do for now
		if (nametocheck.equalsIgnoreCase("<objectdirection>")) {
			return "" + ((int) Math.round(currentObject.FacingDirection));
		}

		if (nametocheck.equalsIgnoreCase("<impactspeed>")) {
			Log.info(currentObject.getName()+ ".SpeedOfLastImpact =  "+currentObject.SpeedOfLastImpact);

			return ""+(currentObject.SpeedOfLastImpact);
		}

		if (nametocheck.equalsIgnoreCase("<objectX>")) {
			int ox=Math.round(currentObject.getX());
			return "" + ox;
		}

		if (nametocheck.equalsIgnoreCase("<objectY>")) {
			int oy=Math.round(currentObject.getY());
			return "" + oy;

		}
		if (nametocheck.equalsIgnoreCase("<objectZ>")) {
			int oz=Math.round(currentObject.getZ());
			return "" + oz;
		}

		if (nametocheck.equalsIgnoreCase("<objectSizeX>")) {
			int ox=Math.round(currentObject.getSizeX());
			return "" + ox;
		}

		if (nametocheck.equalsIgnoreCase("<objectSizeY>")) {
			int oy=Math.round(currentObject.getSizeY());
			return "" + oy;

		}
		if (nametocheck.equalsIgnoreCase("<objectSizeZ>")) {
			int oz=Math.round(currentObject.getSizeZ());
			return "" + oz;
		}

		if (nametocheck.equalsIgnoreCase("<objectMiddleX>")) {
			double midpointX = currentObject.getTopLeftBaseX() + (currentObject.getSizeX() / 2.0);
			int ox=(int) Math.round(midpointX);
			return "" + ox;
		}

		if (nametocheck.equalsIgnoreCase("<objectMiddleY>")) {
			double midpointY = currentObject.getTopLeftBaseY() + (currentObject.getSizeY() /2.0);
			int oy=(int) Math.round(midpointY );
			return "" + oy;

		}
		if (nametocheck.equalsIgnoreCase("<objectMiddleZ>")) {

			double midpointZ = currentObject.getTopLeftBaseZ() + (currentObject.getSizeZ()/2.0);
			int oz=(int) Math.round(midpointZ );
			return "" + oz;
		}

		//
		if (nametocheck.equalsIgnoreCase("<objectBaseZ>")) {			
			double baseZ = currentObject.getTopLeftBaseZ();
			int oz=(int) Math.round(baseZ);
			return "" + oz;
		}


		if (nametocheck.equalsIgnoreCase("<objectScreenX>")) {

			int ox=Math.round(currentObject.getX());

			//Compensate for scene position
			ox = ox + currentObject.getParentScene().getCurrentPanelAbsoluteX();


			return ""+ ox;

		}

		if (nametocheck.equalsIgnoreCase("<objectScreenY>")) {

			int oy=Math.round(currentObject.getY());

			//Compensate for scene position
			oy = oy + currentObject.getParentScene().getCurrentPanelAbsoluteY();

			return ""+ oy;

		}


		if (nametocheck.equalsIgnoreCase("<objectZIndex>")) {
			return "" + currentObject.getZindex();
		}

		if (nametocheck.equalsIgnoreCase("<name>")) {
			return "" + currentObject.getName();
		}


		//--------- information on objects current movements
		if (currentObject!=null && currentObject.getObjectsCurrentState().moveState.isPresent()){
			
			
			MovementPath currentPath = currentObject.getObjectsCurrentState().moveState.get().currentPathData;
			if (currentPath!=null){
				if (nametocheck.equalsIgnoreCase("<currentdestX>")) {
					int destx=Math.round(currentPath.getLastWaypoint().pos.x );
					return "" + destx;
				}
				if (nametocheck.equalsIgnoreCase("<currentdestY>")) {
					int desty=Math.round(currentPath.getLastWaypoint().pos.y );
					return "" + desty;
				}

				if (nametocheck.equalsIgnoreCase("<currentdestDuration>")) {
					double destDuration=currentObject.getCurrentMovementDuration();
					return "" + destDuration;
				}
			} else {
				//error if dest was requested in some  way
				if (nametocheck.startsWith("<currentdest")){
					Log.severe("no current path, cant get dest or duration");
				}				
			}
		} else {
			if (nametocheck.startsWith("<currentdest")){				
				Log.severe("no current movement, cant get dest or duration");
			}
		}
		//------------


		//check for reserved game global variables
		if (nametocheck.equalsIgnoreCase("<currentScenePosX>")) {
			return ""+ SceneObjectDatabase.currentScene.getPosX(); //variable might not match scene. Need better get function? or better variable setting?
		}
		if (nametocheck.equalsIgnoreCase("<currentScenePosY>")) {
			return ""+ SceneObjectDatabase.currentScene.getPosY();
		}
		
		if (nametocheck.equalsIgnoreCase("<currentScenePanSettingX>")) {
			return ""+ SceneObjectDatabase.currentScene.getScenesData().PanX;
		}
		if (nametocheck.equalsIgnoreCase("<currentScenePanSettingY>")) {
			return ""+ SceneObjectDatabase.currentScene.getScenesData().PanY;
		}
		
		if (nametocheck.equalsIgnoreCase("<currentscenename>")) {
			if (SceneObjectDatabase.currentScene==null){
				Log.info("current scene not yet set");
				return "NO CURRENT SCENE WAS SET WHEN THIS WAS LOADED";
			}
			return SceneObjectDatabase.currentScene.SceneFileName;
		}

		if (nametocheck.equalsIgnoreCase("<currentscenedescription>")) {
			if (SceneObjectDatabase.currentScene==null){
				Log.info("current scene not yet set");
				return "NO CURRENT SCENE WAS SET WHEN THIS WAS LOADED";
			}
			return SceneObjectDatabase.currentScene.getScenesData().getSceneDescription();
		}

		if (nametocheck.equalsIgnoreCase("<currentobjectname>")) {
			if (currentObject==null){
				Log.info("no current object");
				return "NO CURRENT OBJECT WAS SET WHEN THIS VARIABLE WAS REQUESTED";
			}
			return  currentObject.getName();

		}

		if (nametocheck.equalsIgnoreCase("<currentlocationname>")) {

			return JAMcore.usersCurrentLocation;

		}
		if (nametocheck.equalsIgnoreCase("<currentscore>")) {

			return ""+ScoreControll.CurrentScore;

		}

		
		if (nametocheck.equalsIgnoreCase("<screencenterx>")) {
			
			//actually is the same as currentScenePosX???
			//pan messures from center anyway no?
			return ""+SceneObjectDatabase.currentScene.getPosX();
			
			//returns the current center of the screen in scene co-ordinates
			//(Useful if you want a popup or pause menu to appear in the center)
			//Log.info("getting screen center x");			
			//current scene top left
			//int x = SceneObjectDatabase.currentScene.getCurrentPanelAbsoluteX(); //variable might not match scene. Need better get function? or better variable setting?
			//Log.info("getting screen center posx="+x);
			//current screen height/width
			//int w = SceneObjectDatabase.currentScene.getScenePanelSizeX();			
			//Log.info("getting panel width="+w);
			//centerx is x+(w/2)			
			//return ""+(x+(w/2));
		}
		
		if (nametocheck.equalsIgnoreCase("<screencentery>")) {
			return ""+SceneObjectDatabase.currentScene.getPosY();			
		}
		
		if (nametocheck.toLowerCase().startsWith("<propertyvalue(")) {

			String predicateToLookFor = nametocheck.substring("<propertyvalue(".length(),nametocheck.length()-2);

			//strip quotes if present
			if (predicateToLookFor.startsWith("\"")){
				predicateToLookFor = predicateToLookFor.substring(1, predicateToLookFor.length()-1);
			}


			Log.info("Looking for semantic property value "+predicateToLookFor+" from object object: "+currentObject.getName());

			SSSNode value = currentObject.getValueOfProperty(predicateToLookFor);

			if (value == SSSNode.NOTFOUND){
				Log.info("No semantic predicate of "+predicateToLookFor+" found on object");
			}

			return value.getPLabel();

		}


		// else if its in quotes we remove them and assume its a game variable
		//if (nametocheck.startsWith("<") && nametocheck.endsWith(">")){
		//	nametocheck = nametocheck.substring(1,nametocheck.length()-1);
		//	return JAM.GameVariables.GetItem(nametocheck);
		//}
		if (objectSpecified && currentObject!=null){ //only if the object is specifically specified

			nametocheck = nametocheck.substring(1,nametocheck.length()-1); //can be done better by changing above
			Log.info("looking for variable using object "+currentObject.getName()+" var:"+nametocheck);
			//get the objects variable
			return currentObject.getObjectsCurrentState().ObjectRuntimeVariables.getVariable(nametocheck);

		}

		Log.info("defaulting to global variable check for var:"+nametocheck);

		//remove brackets if present		
		if (nametocheck.startsWith("<") && nametocheck.endsWith(">")){
			nametocheck = nametocheck.substring(1,nametocheck.length()-1);
			return GameVariableManagement.GameVariables.GetItem(nametocheck);
		}

		//should never get this far!
		return  GameVariableManagement.GameVariables.GetItem(nametocheck);

	}



	/**
	 * tests for actions global to the game for instance, all right clicks could
	 * make a certain sound
	 * 
	 * if it finds any, its runs them
	 **/
	public static void testForGlobalActions(TriggerType type, String Parameter, SceneObject sourceObject) {

		//GameDataBox.addLastCommandToStack("testing for global actions:");

		CommandList actions = InstructionProcessor.globalActions.getActionsForTrigger(type, Parameter);

		if (actions.size() > 0) {

			SceneWidget.Log.info("global actions:" + actions.toString());

			if (sourceObject!=null){		

				//make sure its the correct sort of interaction
				if (      (type==TriggerType.MouseClickActions)
						||(type==TriggerType.MouseRightClickActions)
						||(type==TriggerType.MouseDoubleClickActions)
						||(type==TriggerType.MouseOutActions)
						||(type==TriggerType.MouseOverActions)
						)
				{
					//if so, update
					sourceObject.wasLastObjectUpdated();
					sourceObject.updateLastClickedLocation(); //surely this function is not necessarily from a click?
				}	
				Log.info("running global actions");
				if (sourceObject.getParentScene()!=null){

					processInstructions(actions, "FROM_"
							+ sourceObject.getParentScene().SceneFileName + "_"
							+ sourceObject.getName(), sourceObject);

				} else {

					processInstructions(actions, "FROM_"
							+ "NoScene_"
							+ sourceObject.getName(), sourceObject);

				}

			} else {

				processInstructions(actions, "FROM_no object", null);
			}

		} else {
			SceneWidget.Log.info("no global actions");
		}

		return;
	}

	/** Global actions
	 * These actions are global to the whole game.
	 * They will run when there conditions are met, no matter what scene the player is on
	 * Useful, for example, for making all clicks make a noise.
	 * or to have a global help system. <br>
	 * Note the order of testing is;<br> 
	 * <br> 
	 *  Global Actions will run first.<br> 
	 *  Then scene specific actions (if the object hasn't got "ignoreSceneActions" turned on)<br> 
	 *  Then the objects own actions will run <br> **/
	public static ActionList globalActions = new ActionList();


	public static void loadGlobalSceneActions() {

		String GLOBALACTIONLOCATION = SceneWidget.SceneFileRoot + "GlobalActions.jam";		



		//set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable(){

			@Override
			public void run(String responseData, int responseCode) {
				//moved into its own method to make unit testing easier
				parseGlobalSceneActions(responseData);	
			}

		};

		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){
			@Override
			public void run(String errorData, Throwable exception) {
				SceneWidget.Log.info("error loading global actions"
						+ exception.getMessage());
			}					
		};

		//using the above, try to get the text!
		//used to be getTextFile in FileManager
		RequiredImplementations.getFileManager().getText(GLOBALACTIONLOCATION,true,
				onResponse,
				onError,
				false);




	}


	//temp; for temp interface
	//once instruction processor is in core, this wont be needed
	/**
	 * 
	 * @param commandsToRun - must be CommandList
	 * @param UniqueTriggerIndent
	 * @param ObjectThatCalledThis
	 */
	@Deprecated
	static public void processInstructionsImpl(Object commandsToRun, String UniqueTriggerIndent,
			IsSceneObject ObjectThatCalledThis) {

		processInstructions(((CommandList)commandsToRun),  UniqueTriggerIndent, (SceneObject)ObjectThatCalledThis);


	}


	public static void parseGlobalSceneActions(String responseData) {
		SceneWidget.Log.info("setting global actions");
		String globalactions = responseData;

		//make sure all file names are language specific
		globalactions=JAMcore.parseForLanguageSpecificExtension(globalactions);

		// swap TextIds for text
		globalactions=JAMcore.parseForTextIDs(globalactions);

		globalActions = new ActionList(globalactions);
	}



	//	public static void fadeInHTMLElement(final String elementID) {
	//		fadeInHTMLElement(elementID,1670);
	//	}



}
