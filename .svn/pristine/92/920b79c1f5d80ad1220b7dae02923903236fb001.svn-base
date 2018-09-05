/**
 * 
 */
package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.logging.Logger;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyCalculator;

/**
 * Stores the games answers and has functions to check them<br>
 * The main control script is loaded in here.
 */
public class GamesAnswerStore {
	

	public static Logger Log = Logger.getLogger("JAMCore.GamesAnswerStore");
	
	/** An array of all the answers. An "Answer" object also contains
	 * the desired response actions for that answer **/
	final ArrayList<Answer> AllAnswer = new ArrayList<Answer>();
	
	/** Load an script file, and split it into answers **/
	public GamesAnswerStore(String IncomingScriptFile){
		
		//remove comments
		Log.info("removing comments");
		IncomingScriptFile = removeComments(IncomingScriptFile);
		//Log.info("comments removed :"+IncomingScriptFile);
		//
		
		String CurrentChapter = "";
		String CurrentCodeBlock = "";
		
		//parse file into array.
		int currentPosInScript = 0;	
		while (currentPosInScript<=IncomingScriptFile.length()){
			
			CurrentChapter = "";
			CurrentCodeBlock = "";
			
			//find next ans line
			int Loc = IncomingScriptFile.indexOf("ans=", currentPosInScript)+4;
			
			//if no ans then exit
			if (Loc == -1){
				break;
			}
			
			
			//extract chapter
			int chapEndLoc;
			//Special-case detection for no chapter specified
		//	Log.info("no chapter test:"+IncomingScriptFile.charAt(Loc));
			
			if (IncomingScriptFile.charAt(Loc) == ':')
			{
				//If no chapter is specified we assume "__any__", this is a special string
				//that means this answer is accepted for any chapter.
				CurrentChapter = "__any__";
				chapEndLoc = Loc+1;
				
			} else {
			
			   //get the chapter name
			   chapEndLoc = IncomingScriptFile.indexOf(':',Loc+1);
			   CurrentChapter = IncomingScriptFile.substring(Loc, chapEndLoc);
					
			}
		//	Log.info("Scanning Chapter = "+CurrentChapter);
			
			//extract ans
			//look for next newline
			
			int AnsEnd  = IncomingScriptFile.indexOf("\r",chapEndLoc);
			int AnsEnd2 = IncomingScriptFile.indexOf("\n",chapEndLoc);
			
			///Log.info("AnsEnd="+AnsEnd+" AnsEnd2="+AnsEnd2);			
			//AnsEnd = (AnsEnd<AnsEnd2)?AnsEnd:AnsEnd2;
			
			//we use this to get the smallest non-zero value
			//as that represents the next new line, on either linux or windows 
			AnsEnd = SpiffyCalculator.smallestNonZero(AnsEnd, AnsEnd2);
			
			if (AnsEnd<=chapEndLoc){
				Log.info("no answer string specified. skipping to next codeblock if there is any");
				
				int StartLoc =  IncomingScriptFile.indexOf("- ",Loc+1);				
				//exit if no more codeblocks
				if (StartLoc == -1){
					break;
				} else {
					 int EndLoc = IncomingScriptFile.indexOf("ans=",StartLoc);
					currentPosInScript = EndLoc -1;
					continue;
				}
				
			}
		
			Log.info("("+(chapEndLoc+1)+" to "+AnsEnd+")");
			
			String AnsLine = IncomingScriptFile.substring(chapEndLoc+1,AnsEnd);			
			Log.info("Answer line="+AnsLine+" ("+(chapEndLoc+1)+" to "+AnsEnd+")");
			
			
			//create answer array
			String[] AnsArray = getAnswerArray(AnsLine);
			
			//extract codeblock
			int StartLoc =  IncomingScriptFile.indexOf("- ",Loc+1);
			
			//exit if no more codeblocks
			if (StartLoc == -1){
				break;
			}
			
		    int EndLoc = IncomingScriptFile.indexOf("ans=",StartLoc);
			CurrentCodeBlock = IncomingScriptFile.substring(StartLoc, EndLoc);
			//Log.info("Scanning Code = "+CurrentCodeBlock);
			
			//loop over ans and add them
			
			
			
			for (String CurrentAns:AnsArray){
				
				//Log.info("storing data for ans;"+CurrentAns+" in chapter "+CurrentChapter);
				//Log.info("Code block for this section is...;"+CurrentCodeBlock);
				
				Answer newAnswer = new Answer(CurrentAns.trim(),CurrentChapter.trim(),CurrentCodeBlock);
				//add the answer into the array
				AllAnswer.add(newAnswer);
				
				
			}
			
			
			
			//update current position to end;
			currentPosInScript = EndLoc -1;
				
			
			
		}
		
		
	}
	
	private String removeComments(String incomingScriptFile) {

		
		//The following comment removing code is broken.
		//However, it shouldn't be here anyway.
		
		//Strings might contain //, and might exist over multiple lines
		//for that reason we cant just remove all // over the whole file
		//and instead should deal with them on a line by line bases in Answers
		
		/*
		String test1  = incomingScriptFile.replaceAll("\\/\\/.*?\n", "");
		

		Log.info("comments removed 1:"+test1);
		
		String splitstring = "^(\\/\\/.*?\n)";
		RegExp regularExpression = RegExp.compile(splitstring,"mg");
		
		String test2  = regularExpression.replace(incomingScriptFile, "");
		
		Log.info("comments removed 2:"+test2);
		*/
				
		
		////.*?\n
		//incomingScriptFile = incomingScriptFile.replaceAll("<!--(.|\\s)*?-->", "");
		
		
		// preg_replace('/<!--(.|\s)*?-->/', '', $buffer); 
		
		return incomingScriptFile;
	}

	public String[] getAnswerArray(String ansline){
		
		String Ans[] = ansline.split(",");
				
		return Ans;
	}
	
	public CommandList checkAns(String AnsGiven, String InChapter, boolean isCalc){
	
		AnsGiven = AnsGiven.trim();
		//check for exact match first
		CommandList code = checkForExactMatch(AnsGiven,InChapter);
		
		//else we check for wildcard match in the chapter
		if (code==null){
			Log.info("no exact match found for chapter, checking for regex matchs");
			code = checkForWildcardMatch(AnsGiven,InChapter);
		}
		
		//else we check for match not assigned a chapter
		if (code==null){
			Log.info("checking non-specific chapter answers");
			code = checkForNonChapterSpecificAns(AnsGiven,InChapter);
		}
		
		Log.info("checking for calc ");
		
		Log.info("checking for calc "+isCalc);
		
		// if its a calculation we exit here
		if (isCalc){
			Log.info("is calculation, so skip default replys");
			return null;
		}
		//
		
		//else we check for default match in the chapter
		if (code==null){
			Log.info("no exact match found for non specifics, checking for default matchs");
			code = checkForDefaultAns(AnsGiven,InChapter);
		}
		
		
		return code;
	}
	
	/** returns code to run for answer if found, else null**/
	public CommandList checkForExactMatch(String AnsGiven, String InChapter)
	
	{
		Log.info("checkForExactMatch (not case sensative)"+AnsGiven+" in "+InChapter+" (total ans="+AllAnswer.size()+")");
		
		//look for ans that fits requirements
		for (Answer Ans:AllAnswer){
			
			
		//	Log.info("checking for chapter match:"+InChapter+" "+Ans.Chapter);
		//	Log.info("checking for ans match:"+AnsGiven+" "+Ans.Answer);
						
			if ((Ans.Chapter.equalsIgnoreCase(InChapter)&&(AnsGiven.equalsIgnoreCase(Ans.Answer)))){
				Log.info("matched"+Ans.ScriptCodeBlock);
				return Ans.ScriptCodeBlock;
				
			}
			
			
		}
		
		//--- else we return null		
		return null;
	}
	
	/** returns code to run for answer if found, else null**/
	public CommandList checkForWildcardMatch(String AnsGiven, String InChapter)
	{
		String code = null;		
		
		//look for ans that fits requirements
		for (Answer Ans:AllAnswer){
			
			if ((Ans.Chapter.equalsIgnoreCase(InChapter)&&(AnsGiven.matches(Ans.Answer)))){
				
				return Ans.ScriptCodeBlock;
				
			}
			
			
		}
		
		//--- else we return null		
		return null;
	}
	
	/** returns the script functions to run for answer if {default} actions found, else null**/
	public CommandList checkForDefaultAns(String AnsGiven, String InChapter)
	{
		
		//look for ans that fits requirements
		for (Answer Ans:AllAnswer){
			
			if ((Ans.Chapter.equalsIgnoreCase(InChapter)&&(Ans.Answer.equals("{default}")))){
				
				
				return Ans.ScriptCodeBlock;
				
			}
			
			
		}
		
		//--- else we return null		
		return null;
	}
	
	/** check for non-specific-chapter answers **/
	public CommandList checkForNonChapterSpecificAns(String AnsGiven, String InChapter)
	{
		
		Log.info("Looking for checkForNonChapterSpecificAns "+InChapter+" "+AnsGiven);
		
		//look for ans that fits requirements
		for (Answer Ans:AllAnswer){
			
			//Log.info("checking current ans= "+Ans.Answer);
			
			if ((Ans.Chapter.equals("__any__")&&(Ans.Answer.equalsIgnoreCase(AnsGiven)))){

				Log.info("Ans.ScriptCodeBlock= "+Ans.ScriptCodeBlock);
				return Ans.ScriptCodeBlock;
				
			}
			
			try {
				
				if ((Ans.Chapter.equals("__any__")&&(AnsGiven.matches(Ans.Answer)))){				
					Log.info("Ans.ScriptCodeBlock= "+Ans.ScriptCodeBlock);
					return Ans.ScriptCodeBlock;				
				}
				
			}   catch(Exception pse){
				Log.info("not a regular expression, skipping");
                
            }
			
		}
		Log.info("none found ");
		
		//--- else we return null		
		return null;
	}
}
