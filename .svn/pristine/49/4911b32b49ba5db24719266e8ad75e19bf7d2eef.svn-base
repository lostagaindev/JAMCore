package com.lostagain.Jam;

import java.util.logging.Logger;

import com.google.common.base.CharMatcher;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyCalculator;
import lostagain.nl.spiffyresources.client.spiffycore.SpiffyTextUti;


public class GameVariableManagement {

public	static Logger Log = Logger.getLogger("JAMCore.GameVariableManagement");

	/** users global variables array<br>
	values are saved with case, but names are always lowercase<br>
	comparisons are always case insensitive, however<br>
	**/
	public static AssArray GameVariables = new AssArray();
	
	
	
	/**
	 * Replaces all the game variables in a string with their values
	 * It also works out maths specified in brackets like (3+5) or (4*<meryll:objectX>)
	 * 
	 * Things this should pass examples;
     * 
     *  SetObjectPosition = <HELDITEM>,<meryll:objectX>,<meryll:objectY>
	 *  Message = players score is <V:globalscore>
	 *  SetObjectPosition = <HELDITEM>,(<meryll:objectX>-90),(<meryll:objectY>-99)		
	 * 
	 * Variables that are for returning objects, like <HELDITEM> will stay as they are, only string and number replacements
	 * will by replaced.
	 * If no calling SceneObject is specified, we assume null
	 * 
	 * @param input_string
	 * @return
	 */
	public static String replaceGameVariablesWithValues(String input_string) {		
		return replaceGameVariablesWithValues(input_string,null);		
	}
	
	
	
	//Note; may be moved elsewhere
	//or maybe gameobjectdatabase should use this for its var names?
	enum reservedVariables {
		//html related
		DIV,BR,SPAN,A,P,
		//game engine reserved
		SCENE,
		OBJECT,
		GLOBAL,
		
		//game related (needs more)
		//This should be all game related variables THAT DONT RETURN STRINGS OR NUMBERS
		//(so all the ones that return *objects*)
		PROPERTY(true), //true because it will be in the form PROPERTY:##### rather then just property
		HELDITEM,
		CALLINGOBJECT,		
		LASTSCENEOBJECT,
		LASTCLICKEDON,
		PARENT,
		LASTCLICKEDSCREATOR,
		TOUCHER,
		CHILDREN,
		LASTINVENTORYITEM,
		LASTVECTORUPDATED,
		ALLVECTOROBJECTS,
		LASTINPUTUPDATED,
		ALLINPUTOBJECTS,
		ALLOBJECTS,
		LASTTEXTUPDATED,
		ALLLABELOBJECTS,
		LASTSPRITEITEM,
		LASTDIVUPDATED,
		ALLDIVOBJECTS,
		CURRENTOBJECT;
		//--
		
		
		boolean startsWithMatch = false;
		
		reservedVariables(){
			//leave default
		}
		reservedVariables(boolean startsWithMatch){
			this.startsWithMatch=startsWithMatch;
		}
		
		//returns true if theres a case insentive exact match
		//or, in some cases, if the start matches ending in a colon
		//NOTE: "DIV class=" wont match against DIV. Be sure to crop to the first space before testing

		boolean matchs(String testAgainst){
			
			if (!startsWithMatch){
				if (this.name().equalsIgnoreCase(testAgainst)){
					return true;
				}
			} else {
				//Test for PROPERTY:blah style matchs
				if (testAgainst.toUpperCase().startsWith(this.name()+":")){
					return true;
				}
			}
			
			return false;
		}
		
		//Test if the string specified is a reserve variable, or a html tag
		//This returns true also if a space is discovered before a colon(??)
		//we also return false if starting with a !
		static boolean isReservedVariable(String testThis){
			if (       testThis.startsWith("!")
					|| testThis.startsWith("/") 
					|| testThis.startsWith("\\")  ){	//ie </p> or </div>			
				return true;
			}
			
			
			//if we have a space before a colon then we know we must be html (ie, "DIV ")
			int IndexOfSpace = testThis.indexOf(" ");
			if (IndexOfSpace != -1){
				int IndexOfColon = testThis.indexOf(":"); //hu????? why a :???
				if (IndexOfSpace<IndexOfColon || IndexOfColon==-1){
					return true;
				}
			}
			
			//test all for exact match
			for (reservedVariables testVar : reservedVariables.values()) {
				if (testVar.matchs(testThis)){
					return true;
				}				
			}
			return false;
			
		}
		
	}
	
	/**
	 * Replaces all the game variables in a string with their values<br>
	 * It also works out maths specified in brackets like (3+5) or (4*<meryll:objectX>)<br>
	 * <br>
	 * Things this should pass examples;<br>
     * <br>
     *  SetObjectPosition = <HELDITEM>,<meryll:objectX>,<meryll:objectY><br>
	 *  Message			  = players score is <V:globalscore><br>
	 *  SetObjectPosition = <HELDITEM>,(<meryll:objectX>-90),(<meryll:objectY>-99)		<br>
	 * <br>
	 * Variables that are for returning objects, like <HELDITEM> will stay as they are, only string and number replacements<br>
	 * will by replaced. <br>
	 * 
	 * NOTE: Its possible to use variables in variables. The inner ones will be processed first.<br>
	 * eg.<br>
	 * <Level<V:CurrentLevelNumber>Marker:Objecty><br>
	 * will return the y position of LevelMaker1 if the global variable CurrentLevelNumber is set to 1<br>
	 * <br>
	 * <br>
	 * @param input_string
	 * @param currentObject
	 * @return
	 */
	public static String replaceGameVariablesWithValues(String input_string,IsSceneObject currentObject) {

		Log.info("inputstring:"+input_string);	
			
		//Once this new system parses everything correctly and does maths, we can start removing 
		//processing variables from the instruction processors individual statements		
		
		//First we replace variables with numbers
		//Then we sort out any maths that remains
		//So (<meryll:objectX>-90) will become something like (344-90) first, and then is worked out in the second loop
		
		//create variables needed for first loop
		int pos = 0; //position we are in the loop
		int loc = -1; //location of current open bracket
		int loc_end= -1; //location of that brackets end
		
		while (true){					
			
			loc = input_string.indexOf("<",pos);
			
			
			if (loc==-1){
				//Log.info("no more open < found");				
				break; //if there is no more open brackets we exit the loop
			}
				
			//loc_end = input_string.indexOf(">",loc);			
			loc_end =  SpiffyTextUti.findClosingBracket(input_string, loc+1, '<', '>'); //we can have brackets in brackets so this is needed to be sure to get the real matching end
			//(loc+1 used to be loc-1 due to a error in findClosingBracket which is now fixed)
			
			//was no end bracket found?
			//or is the next character a number?
			if (loc_end==-1 ||
				CharMatcher.javaDigit().matches(input_string.charAt(loc+1)) 
				)
			{
				Log.info("match for this open bracket not found or next character was a number");
				//as variables never start with numbers we skip
				pos = loc+1;
				continue;
			}
			//------------------------------------------
			
			String variableSpecification = input_string.substring(loc+1, loc_end);
			pos = loc+1; //we search again from the start of the last found +1, not the end like we used too. This is because when we replace part of the string it might get shorter, thus the end position will be too far along and might miss other variable starts // loc_end+1;

			Log.info("Looking for variable specification:"+variableSpecification);
			if (variableSpecification.contains("<")){
				//if we still contain variables we check again, effectively iterating the whole thing so the inner most variables are dealt with first				
				variableSpecification = replaceGameVariablesWithValues(variableSpecification,currentObject);
			}
			//-----------------------------------------
			
			//ensure its not a common html tag (there's are restricted keywords you could say)
			//Also checks its not a reserved variable, such as HELDITEM or PROPERTY:
			//We should only be swapping for user-variables here that return strings
			if (reservedVariables.isReservedVariable(variableSpecification)){
				Log.info(variableSpecification+" IS RESERVED VARIABLE, so skipping further checks");
				continue;		
			}
			
			
			
			// run variable checks now
			// V: is for global variables
			// ObjectName: is for object variables
				
			//if its a global or object variable then it will contain a :
			if (variableSpecification.startsWith("V:")){
				
				String varname    = variableSpecification.split(":",2)[1]; //the bit after the : is the global variables name
						
					String currentVarName =  varname.toLowerCase(); //all variable names are saved lower case
					String currentVarValue = GameVariableManagement.GameVariables.GetItem(currentVarName);			
					Log.info("replacing global:"+currentVarName+" with "+currentVarValue);					
					
					String before = input_string.substring(0, loc);
					String after  = input_string.substring(loc_end+1);
					
					input_string = before+currentVarValue+after;
					
					
					continue;
					
			} else {
				//else we check for object variables
				Log.info("looking to get variable val:"+variableSpecification);
				//v with none
				//String replacement = InstructionProcessor.getValue("<"+variableSpecification+">", currentObject);
			
				String replacement =  InstructionProcessor.getValue("<"+variableSpecification+">", currentObject);
						
				//getValueFunction				
				if (replacement!=null && !replacement.isEmpty()){
					Log.info("replacement = :"+replacement);
					
					//replace
					//input_string = input_string.replaceAll("<" + variableSpecification + ">", replacement); // currentVarName

					String before = input_string.substring(0, loc);
					String after = input_string.substring(loc_end+1);
					
					input_string = before+replacement+after;
				} else {
					//no replacement was found, so we continue
					Log.info("none found, leaving variable as-is :");					
					//(this can happen for variables that dont return strings, such as <HELDITEM>)
				}
							
			}
						
		}
		
		/*
		if (input_string.contains("random()")){
			while (input_string.contains("random()"))
			{				
				input_string = input_string.replace ("random()", ""+Math.random());		
				
			}
		}
		*/
		
		//
		//--------------------------------
		//We then swap for special dynamic values. There is only one of these; 
		//random
		//We might have more in future, but we have to be aware doing a separate loop here is vary wasteful
		
		//Consider; support for Sin and Cos? AdvancedCalculation doesn't support them.
		int startfrom = 0;

		Log.info("input_string       :"+input_string);
		
		while(input_string.indexOf("random(", startfrom)!=-1){
			//find next random(
			int nextcommandpos = input_string.indexOf("random(", startfrom);
			//find the next )
			int nextcommandend = input_string.indexOf(")", nextcommandpos); //used to be 'startfrom' but nextcommandpos is safer to get the right end bracket
			String randomResult = "";
		
			//if there was no params specified we assume we just replace with a random number between 0 and 1 (like Math.random() )
			if (nextcommandend == nextcommandpos+7){
				
				randomResult = ""+Math.random();
				
			} else {			

				String nval=input_string.substring(nextcommandpos+7, nextcommandend);
			
				randomResult = ""+SpiffyCalculator.getRandomFromRange(nval);
				

			}
			
			
			String before = input_string.substring(0, nextcommandpos);
			String after = input_string.substring(nextcommandend+1,input_string.length() ); //used to be +2
			
			//Log.info("before       :"+before);
		//	Log.info("randomresult :"+randomResult);
			//Log.info("after        :"+after);
			
			input_string = before +  randomResult +after;
			
			
			
			//loop back
			startfrom = nextcommandpos+1;//nextcommandpos+newbit.length();

		}

		//--------------------------------
		
		// After we have swapped all variables we should also test for calculations
		// we detect calculations by brackets followed by a number 
		// without any space
		// eg (10-30) not ( 10-30 )
		// or
		// brackets followed by brackets
		// eg ((7+...)
		//
		
		pos = 0;
		loc = -1;
		loc_end = -1;		
		while (true){		
			
			loc = input_string.indexOf("(",pos); 			
			if (loc==-1){
				break; //no more open brackets
			}
			if (loc+1==input_string.length()){
				Log.info("was testing:"+input_string+" open brackets found at end of string - thus no room for close. We assume there was no maths intended");
				break;
			}
			loc_end = SpiffyTextUti.findClosingBracket(input_string, loc+1, '(', ')'); //finds the matching close bracket
						//+1 added above
			
			
			
			
			String potentialEquation = input_string.substring(loc+1, loc_end);
		
			pos = loc;//+1;//loc_end+1;
			
			//ensure the character after the loc is a number or another bracket
			char characterAfterStart = potentialEquation.charAt(0);			
			
			boolean isDigit = CharMatcher.DIGIT.or(CharMatcher.is('-'))
					                           .or(CharMatcher.is('(')).matches(characterAfterStart);
					
			
			if (!isDigit){
				//not real equation, so skip
				pos=loc_end;//
				continue;
			}
			
			if (SpiffyCalculator.isCalculation(potentialEquation)){
				Log.info("calculation detected :"+potentialEquation);
				//solve the maths
				double val = SpiffyCalculator.AdvanceCalculation(potentialEquation);
				Log.info("result is :"+val);
				//can be done better (as we already know the place
				//we dont really need the replaceall either
				String before = input_string.substring(0, loc);
				String after = input_string.substring(loc_end+1);
				
				input_string = before+val+after;
				
				
			} else {
				// if all numbers,remove brackets
				boolean isAllDigits = CharMatcher.javaDigit()
						.or(CharMatcher.is('('))
						.or(CharMatcher.is(')'))
						.matchesAllOf(potentialEquation);
				
				if (isAllDigits){
					
					//Log.info("isAllDigits:"+potentialEquation);
					String before = input_string.substring(0, loc);
					String after = input_string.substring(loc_end+1);					
					input_string = before+potentialEquation+after;
					
				
				} else {
				//not real equation, so skip
					pos=loc_end;//
				continue;
				}
			}
			
		}
		Log.info("equation result is :"+input_string);
		return input_string;
		
	}




}
