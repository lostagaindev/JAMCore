package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.base.CharMatcher;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;

/** a collection of actions and trigger sets **/

public class ActionList extends ArrayList<ActionSet> {

	public static Logger Log = Logger.getLogger("JAMCore.ActionList");
	
	
	/** function to check if any triggers match, returns all the actions if it does **/
	public CommandList getActionsForTrigger(TriggerType type, String Parameter){

		CommandList ActionListToReturn = new CommandList();

		//Log.info("_________________Looking for trigger-: ");

		//iterate over checking types
		Iterator<ActionSet> actionit = super.iterator();

		//Log.info("_looking for trigger out of :"+super.size()+" triggers");

		while (actionit.hasNext()) {

			ActionSet actionSet = (ActionSet) actionit.next();

			if (actionSet.hasTrigger(type,Parameter)){

				//Log.info("_has trigger:"+type.toString()+" "+Parameter);
				ActionListToReturn.addAll(actionSet.CommandsInSet);

			}


		}	

		return ActionListToReturn;	
	}
	/** loads a new action list from a string 
	 * 
	 * a action list could be something like;
	 * 
MouseOverActions:
- Message = "mouse over triggered"
- PlaySound = overblip.mp3

MouseOutActions:
- Message = "mouse outr triggered"
- PlaySound = outblip.mp3

MouseClickActions:
- Message = "mouse click triggered"
- PlaySound = oclickblip.mp3

UserActionUsed = ignite||spark:
- Message = "ignite or spark triggered"
(condition: hasProperty = Flamable)
- addProperty: onFire

PropertyAddedAction = fire:
- Message = "something has been set onfire!"
	 * 
	 * 
	 * NOTE: The parameters (starting with -Item: and ending in the first trigger line detected) should be split off
	 * from the string you give this.
	 * **/

	public ActionList (String sourceString){

		Log.info("converting string to action list:\n"+sourceString);
		
		int currentLoc = 0;
		
		//look for the next action set
		while (currentLoc<sourceString.length()){
			
			int startOfTriggerLine = findNextTriggerLineStart(sourceString,currentLoc);
									
			if (startOfTriggerLine==-1){
				//No more triggers
				break;
			}			
			int endOfTriggerLine = sourceString.indexOf(":", startOfTriggerLine+1); //index of the colon marks the end of the trigger, not the newline (ie, the trigger should not contain the colon itself)
 			
			//if theres no end newline, then assume it ends with the string itself
			//However, this also means there is no contents of this trigger, thus we should skip it
			if (endOfTriggerLine == -1){
				Log.info("no more trigger lines");
				break;
				//endOfTriggerLine = sourceString.length();
			}
			
		//	Log.info("="+startOfTriggerLine+","+endOfTriggerLine);
			
			String triggerLine = sourceString.substring(startOfTriggerLine, endOfTriggerLine).trim();
			
			//now we need to find the set contents
			int StartOfSet  = endOfTriggerLine+1;
			int EndOfSet    = findNextTriggerLineStart(sourceString,StartOfSet);
			
			if (EndOfSet == -1) {
				EndOfSet = sourceString.length(); //assume we end at the end of the string
			} 
			String actions = sourceString.substring(StartOfSet, EndOfSet).trim();
			
			//create a new actionset from the data
			Log.info("______________________________triggerLine:  "+triggerLine);
			Log.info("______________________________actions: \n "+actions);
			
			
			ActionSet newset = new ActionSet(actions,triggerLine);
			super.add(newset);
						
			currentLoc = EndOfSet;
		}

		Log.info("number of actions="+super.size());

		//create a new ActionSet from this

	}

	/** loads a new action list from a string 
	 * 
	 * a action list could be something like;
	 * 
MouseOverActions:
- Message = "mouse over triggered"
- PlaySound = overblip.mp3

MouseOutActions:
- Message = "mouse outr triggered"
- PlaySound = outblip.mp3

MouseClickActions:
- Message = "mouse click triggered"
- PlaySound = oclickblip.mp3

UserActionUsed = ignite||spark:
- Message = "ignite or spark triggered"
(condition: hasProperty = Flamable)
- addProperty: onFire

PropertyAddedAction = fire:
- Message = "something has been set onfire!"
	 * 
	 * 
	 * 
	 * **/

	public void ActionList_old (String sourceString){


		//	Log.info("converting string to action list"+sourceString);

		int currentLoc = 0;
		//look for the next action set
		while (currentLoc<sourceString.length()){

			//position of next colon (TODO: thats followed by a newline)
			
			//int colonpos = sourceString.indexOf(":",currentLoc);
			int startOfTriggerLine = findNextTriggerLineStart(sourceString,currentLoc);
			
			//TODO: The above method now used makes some of the below redundant
			//Specifically checks for length and - 
			
			if (startOfTriggerLine==-1){
				break;
			}
			
			//text before :
			int newlinebefore = sourceString.lastIndexOf('\n', startOfTriggerLine);


			//if it starts with the actions
			if (newlinebefore == -1){
				newlinebefore=0;
			}

			String actiontype = sourceString.substring(newlinebefore, startOfTriggerLine).trim();

			Log.info("actiontype line="+actiontype);
			//make sure this line doesn't contain a "-", or is too short, if so it isn't a real trigger
			if (actiontype.contains("-") || actiontype.length()<4){
				Log.info("not a real trigger line");
				//skip to next line
				currentLoc = startOfTriggerLine+actiontype.length(); //used to be +1
				continue;
			}


			//	Log.info("actiontype="+actiontype);

			int newlinebeforetheoneafter;
			int lookfrom=startOfTriggerLine + 1;
			do {
				//position of next colon
				//	int theoneaftercolonpos = sourceString.indexOf(":",	lookfrom);
				int theoneaftercolonpos = findNextTriggerLineStart(sourceString,lookfrom);

				newlinebeforetheoneafter = 0;
				//if there isnt, assume its the end
				if (theoneaftercolonpos == -1) {
					Log.info("(last action in list)");
					theoneaftercolonpos = sourceString.length();
					newlinebeforetheoneafter = sourceString.length();
				} else {
					//text before :				
					newlinebeforetheoneafter = sourceString.lastIndexOf('\n',
							theoneaftercolonpos);
				}
				String testEndline = sourceString.substring(
						newlinebeforetheoneafter, theoneaftercolonpos).trim();
				
				//make sure this line doesn't contain a "-", if so it isnt a real trigger
				if (testEndline.contains("-")) {
					Log.info("not a real trigger line");
					//skip to next line
					lookfrom = theoneaftercolonpos + 1;
					continue;
				} else {
					break;
				}
				
			} while (currentLoc<sourceString.length());


			String actions = sourceString.substring(startOfTriggerLine+1, newlinebeforetheoneafter).trim();

			Log.info("actions="+actions);

			currentLoc = newlinebeforetheoneafter;

			//get triggers from type (basicly its the same as the action type, but allows for multiple
			// look for ORs in the property string which are represented by ||
			String triggers = actiontype;

			//create a new actionset from the data
			ActionSet newset = new ActionSet(actions,triggers);
			super.add(newset);

			Log.info("number of actions="+super.size());
		}


		//create a new ActionSet from this

	}

	/**
	 * a new robust method for finding the next trigger lines colon location
	 * 
	 * @param contents
	 * @param currentLoc
	 * @return
	 */
	static public int findNextTriggerLineStart(String contents, int currentLoc) {

		//We are looking for the first : in the contents starting from currentLoc
		//that defines the end of a line, but also that that line didn't start with a comment
		int LocToTest = 0;
		String TriggerLine = "";
		int newlinebefore =-1;
		
		while(currentLoc<contents.length()){

			//find the next colon to check
			LocToTest = contents.indexOf(":", currentLoc);
			if (LocToTest == -1){
				Log.info("no new triggers");
				return -1;

			}

			//ok, we know theres a colon here, but is there any characters other then whitespace between here and the next newline?
			int NextNewLine = contents.indexOf("\n",LocToTest);
			
			if (NextNewLine == -1){
				NextNewLine = contents.length(); //just at the end
			}

			if (NextNewLine>LocToTest+1){
				String gapAfterColon = contents.substring(LocToTest+1, NextNewLine);
				//Log.info("testing if "+gapAfterColon+" contains just whitespace");
				if (CharMatcher.WHITESPACE.matchesAllOf(gapAfterColon)){
				//	Log.info("Yes, only whitespace are after the colon");				
				} else {
				//	Log.info("No, there was stuff written after the colon");				
					//skip to next colon to test
					currentLoc=LocToTest+1;
					continue;
				}
			}

			//now we need to look backwards to test the contents of the line
			newlinebefore = contents.lastIndexOf('\n', LocToTest);

			//if it starts with the actions
			if (newlinebefore == -1){
				newlinebefore = 0;
			} else {
				newlinebefore = newlinebefore + 1; //we do this to remove that newline from the string location
			}
			
			TriggerLine = contents.substring(newlinebefore, LocToTest).trim();

			//Log.info("Trigger line to test:"+TriggerLine);

			//ensure this TriggerLine meets the requirements

			if (    TriggerLine.contains("-")        ||
					TriggerLine.contains("(")    ||
					TriggerLine.length()<4       ||
					TriggerLine.startsWith("\\") || 
					TriggerLine.startsWith("//"))	
			{
				//Log.info("Trigger line does not meet requirements (contains - or brackets or is too short");
				currentLoc=LocToTest+1;
				continue;
			} else {
				//yup! the trigger line is fine
				break;
			}

		}

		//Log.info("_______________________Trigger line identified:"+newlinebefore);
		//Log.info("_______________________Trigger line identified:"+TriggerLine);

		return newlinebefore;
	}

	/**
	 * New method to split a string into action sets - this new method detects actionsets by the colon only at the end
	 * of the line, if the line was not a comment.
	 * Previous to this colons couldn't relayable be used within commands as it might result in a bad split
	 * 
	 * @return
	 */
	private ArrayList<String> SplitStringIntoActionSets_NOTIMPLEMENTED(String actionSetList) {

		//This methos is based around this regex;
		//\n[^\/\\\n](.*)(\t)*:(\t)*\n
		//The splits the string around lines ending in :
		//The colon can have any number of spaces around it, provided they contain no newlines.
		//It also fails to match if the line started with // or \\ indicating a comment

		String ActionSetSplitter = "\\n[^\\/\\\\\\n](.*)(\\t)*:(\\t)*\\n"; 

		//-sigh-
		//actually we cant use this method as theres Javas Split function loses the deliminator
		//We NEED to know what it split on because the thing matched for the split is the actionsets trigger line!
		//ARRRGHGGGHHHH


		return null;
	}

	public ActionList() {

	}
	/**
	 * Returns the code for this action set
	 * (Note;This does not yet exactly match what the JAM code would be)
	 * 
	 * @return
	 */
	public String getCode(){
		String contents="";

		for (ActionSet set : this) {

			contents+=set.getCode()+" \n";

		} 

		contents+="";

		return contents;
	}

	@Override
	public String toString(){

		String contents="";

		for (ActionSet set : this) {

			contents+=set.toString()+" \n";

		} 

		contents+="";

		return contents;

	}
	
	public boolean hasActionsFor(TriggerType type) {
		
		for (ActionSet set : this) {
			if (set.hasTrigger(type, null)){
				return true;
			}
			
		}
		return false;
	}


}
