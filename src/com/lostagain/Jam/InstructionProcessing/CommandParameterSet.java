package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lostagain.Jam.GameVariableManagement;
import com.lostagain.Jam.SceneObjects.SceneObject;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyTextUti;

public class CommandParameterSet {

	public static Logger Log = Logger.getLogger("JAMCore.CommandParameterSet");
	
	/**
	 * The raw parameter string, as its written in the script file. Before any variables are replaced.
	 * This never changes once loaded.
	 **/
	final String rawParameterString; //we might want to make this optional, or have some other specific way of saying "this command has no parameterss"
	
	/**
	 * if ANY of the parameters for this command contain variables, then  this is set to true.
	 * Currently this is worked out by the presence of a < in the 	rawParameterString.
	 */
	final boolean containsVariables; 
	
	public boolean containsVariables() {
		return containsVariables;
	}
	
	/**
	 * if ANY of the parameters for this command contain maths, then  this is set to true.
	 * Currently this is worked out by the presence of a ( in the 	rawParameterString.
	 * This doesnt garenty maths is present, however, as brackets can be used for other things.
	 * 
	 */
	final boolean mightContainsMaths; 
	/**
	 * If this returns true, then WARNING as this might contain maths ;)
	 * @return
	 */
	public boolean mightcontainMaths() {
		return mightContainsMaths;
	}
	/**
	 * The parameters split into a comma separated array.
	 * This will be updated each time instructionprocssor runs on this command, if (and only if) rawParameterString
	 * contains a variable.
	 *
	 **/
	CommandParameter params[]; //in future we might even make another class for each individual parameter, with a type setting as below
	//then we only update it if we know its got variables
	
	
	enum ParameterType {
		Empty,
		Int,
		Double,
		StringWithVariables,
		StringWithoutVaribles,
		Object
	}

	boolean isEmpty=false;
	
	public CommandParameterSet(String rawParameterString) {
		
		
		this.rawParameterString = rawParameterString;	
		this.proccessedParameterString = rawParameterString; //by default the purchased params are the same as raw ones (its only if they contain variables that they are different)
		
		if (rawParameterString==null || rawParameterString.trim().equals("")){
		//	if (callingObject!=null){
		//		Log.warning(" calling object = "+callingObject.getObjectsCurrentState().ObjectsFileName);
		//	}	
			containsVariables  = false;
			mightContainsMaths = false;
			isEmpty = true;
			
			//params should be a zero length set
			params = new CommandParameter[0];
			
			return;
		}
		
		 splitParameters(rawParameterString);
	//	this.params = rawParameterString.split(",");
		
		
		
		if (rawParameterString.contains("<")) {
			containsVariables = true;
		} else {
			containsVariables= false;
		}
			

		if (rawParameterString.contains("(")) {
			mightContainsMaths = true;
		} else {
			mightContainsMaths= false;
		}
			
	}

	@Override
	public String toString() {			
		//return rawParameterString;		
		//better for debugging;
		String allParams="[";
		for (CommandParameter commandParameter : params) {
			allParams=allParams+commandParameter.getAsString()+",";
		}		
		return allParams+"]";
	}

	public String getRawParameterString() {
		return rawParameterString;
	}
	
	public CommandParameter[] getProcessedParameters() {
		return params;
	}
	
	@Deprecated
	public String[] getProccessedSplit() {
		
		if (!isEmpty){
		//horrible, phase out. This is for tempory backwards compatibility only
		String stringParams[] = new String[params.length];
		
		for (int j = 0; j < params.length; j++) {
			 stringParams[j]=params[j].parameterString;
			
		}
			return stringParams;
		} else {
			return null;
		}
		
		
	}
	
	/**
	 * the current parameters with the variables swapped for their values.
	 * This should be refreshed each time this commandparameter set is used
	 */
	String proccessedParameterString = "";
	
	/**
	 * fills all the variables in based on the current values.
	 * This should be run each time the instruction processor runs this command.
	 * 
	 * If theres no variables, it also checks for a random parameter string (that is, when | is used to have a selection
	 * of possible random values)
	 * @param object
	 */
	public void prepareParameters(SceneObject ObjectThatCalledThis,boolean usesVariables,boolean usesRandomSplit,boolean usesMaths){
		
		boolean changed = false;
		
		//TODO: in future we shouldn't need to resplit if we have variables.
		//the variables should be processed within the already split parameters. Maybe even only when they are used??
		
		//process variables and maths first if theres any
		if ((usesVariables && containsVariables()) || 
			(usesMaths     &&	mightcontainMaths())          ){
			Log.info("Potential vars or maths found in property "+rawParameterString+", replacing with values");

			proccessedParameterString = GameVariableManagement.replaceGameVariablesWithValues(rawParameterString,ObjectThatCalledThis);
			Log.info("new  proccessedParameterString: "+proccessedParameterString);

			 changed = true;
		} 
		
			
			//this is an old randomization system thats powerful, but unprecise and should be used with care
			//other variables and random selection systems are now between <> which is why we only look for a | after processing variables
			//this prevents conflicts with new stuff
			if (usesRandomSplit && rawParameterString.contains("|")){					
				//Log.info("random property set detected");

				proccessedParameterString=getRandomProperty(rawParameterString);
			//	Log.info("CurrentProperty picked = "+proccessedParameterString);
				

				 changed = true;
				
			}
			
			if (changed){
				 splitParameters(proccessedParameterString);
					
			}
		
	}

	/**
	 * splits the parameter string into a trimmed array.
	 * For parameters without any oldschool randomizations ( | ) this should only ever need to be done once
	 * 
	 * @param proccessedParameterString
	 */
	private void splitParameters(String proccessedParameterString){
			
		
	//	String[] parametersAsStrings  = proccessedParameterString.split(",");		
		

		//TODO: SpiffyTextUti.splitNotWithinBrackets could probably be optimized to speed up loading
		ArrayList<String> parametersAsStrings = SpiffyTextUti.splitNotWithinBrackets(proccessedParameterString, ",", '"', '"');
		
	//	params = new CommandParameter[parametersAsStrings.length];
		params = new CommandParameter[parametersAsStrings.size()];
		
		
		//We might want to use; SpiffyTextUti.splitNotWithinBrackets(tempsplit[2], ";", '"', '"');
		//Instead. Slower, but would allow commas in other commands more easily.
		//(currently stuff like Message has its own split for this purpose, which isn't saved like this one is)

		
		//trim them all! trim trim trim!
		//(yes,I used a for loop!  any decade now they will be back in fashion)
		
		for(int x=0;x<parametersAsStrings.size();x++){				
		//	Log.info("storing parameter picked ="+parametersAsStrings[x]);
			params[x] = new CommandParameter(parametersAsStrings.get(x).trim());	

		//	Log.info("CommandParameter ="+parametersAsStrings.get(x));
		}
		
		
		
	}
	private static String getRandomProperty(String currentProperty) {

		String props[]=currentProperty.split("\\|");

		int rnd = (int) Math.round((Math.random()*(props.length-1)));		

		Log.info("property picked ="+rnd);

		return props[rnd];
	}
	
	public String getProccessedParameterString() {
		return proccessedParameterString;
	}

	/**
	 * should return true if there is no parameters specified
	 * @return
	 */
	public boolean isEmpty() {
		return isEmpty;
	}

	public int getTotal(){
		
		
		return params.length;
	}

	public CommandParameter get(int i) {
	//	Log.info("getting "+i);
		return params[i];
	}
	
	
}
