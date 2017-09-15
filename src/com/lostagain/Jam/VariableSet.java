package com.lostagain.Jam;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;



/** used to store variables*
 * eg<br>
 * Score = 532142<br>
 * Life = 20 <br>
 * Provides checking function to that can understand greater then or less then<br>
 * This class is currently used in sceneobjects for object specific variables<br> */
public class VariableSet extends HashMap<String, String> {

	static Logger Log = Logger.getLogger("JAMcore.VariableSet");
	
	/**
	 * splits the supplied string first by comma and then by either = or :
	 * eg<br>
	 * testname=testvalue,testname2=testvalue2<br>
	 * or<br>
	 * testname:testvalue,testname2:testvalue2<br>
	 * <br>
	 * 
	 * @param newobjectsVaaribles
	 */
	public VariableSet(String newobjectsVaaribles) {
		super();
		
		//deserialise and set values
		String[] varstrings = newobjectsVaaribles.split(",");

		Log.info("setting variables  = "+varstrings.toString());
		
		for (String setting : varstrings) {

			Log.info("setting  to "+setting);
			if (setting.isEmpty()){
				continue; //if the setting string was for some reason empty or lacking a = we just continue
			}
			if (!setting.contains("=") && !setting.contains(":")){
				Log.warning("variable string did not have equals:"+setting+" so we are ignoring it, sorry");				
				continue; //if the setting string was for some reason empty or lacking a = we just continue
			}
			
			//we split by equals or :
			String varname  = setting.split("=|:",2)[0];
			String varvalue = setting.split("=|:",2)[1];
			
			
			this.setVariable(varname, varvalue);
						
		}
		
		
	}

	public VariableSet() {
		super();
	}
	
	/**
	 * copies this variable set
	 */
	public VariableSet clone()
	{
		Log.info("cloning set with "+this.size()+" entries");
		
		return new VariableSet(this);
	}
	
	public VariableSet(VariableSet copythisone) {
		super(copythisone);
	}
	
	/** sets a variable to a value
	 * eg, status, asleep
	 * sober,-5 
	 * damage, 50
	 * 
	 * @return the previous value associated with key, or null if there was no mapping for key. (A null return can also indicate that the map previously associated null with key.)
	 * 
	 * **/
	
	public String setVariable(String name, String value) {

		
		Log.info("setting variable "+name+" to "+value);
		if (name.isEmpty()){			
			Log.warning("attempting to create a variable with a blank name. It was going to be assigned value:"+value);
			return null;
			
		}
		
		// detect special values like "+" or "-"
		////if (value.startsWith("+")) {
		//	int val = Integer.parseInt(value.substring(1, value.length()));

		//	addToVariable(name, val);
		//} else if (value.startsWith("-")) {
		//	int val = Integer.parseInt(value);
		//	addToVariable(name, val);
		//} else {
		if (value.isEmpty()){
			//if its empty we just assign the name to empty (as trim will cause a crash!)
			return super.put(name.trim(),"");
		} else {
			
			return super.put(name.trim(),value.trim());
		}

		//}
	}

	public String getVariable(String name) {

		return super.get(name);

	}

	/** adds a value to a variable <br>
	 * if there is no variable, it creates one.<br>
	 * If the variable that exists already isnt a number, it will crash<br>
	 * eg, dont add 5 to variable "characterstate" if its set to "sleep" <br>
	 * <br>
	 * You can subtract by just adding a negative number <br>**/
	public void addToVariable(String name, int Add) {

		Log.info("adding  variable "+name+" by "+Add); 
		
		String currentVar = getVariable(name);
		
		if (currentVar==null){
			Log.info("variable "+name+" not found, so creating it with value 0");
			setVariable(name, "0");
			 currentVar = "0";
		} 
		
		int val = Integer.parseInt(currentVar);
		
		
		super.put(name, "" + (val + Add));

		Log.info(" variable "+name+" is now  "+super.get(name)); 
		
	}

	/** check against a value
	 * if that value is ">" or "<" then it tests for a number greater or less then the value specified :
	 * returns false if no variable of name "name" exists
	 * 
	 * Note: As this is used to test both strings and ints, it tests for a exact string match FIRST
	 * 
	 * 
	 ***/	
	public boolean testValue(String name, String equals){
		
		String currentVar = getVariable(name);
		if (currentVar==null){
			Log.warning("variable "+name+" not found");
			return false;
		}
		
		if (currentVar.equalsIgnoreCase(equals)){
			return true;
		}
		int val;
				
		try {
			
			val = Integer.parseInt(currentVar);
			
		} catch (NumberFormatException e) {
			
			Log.info("variable "+currentVar+" is not a number");
			return false;
			
		}
		
		if (equals.startsWith(">")){
			
			equals=equals.substring(1);
			int testVal = Integer.parseInt(equals);
			
			if (val>testVal){
				return true;
			}  else {
				return false;
			}
			
			
		}
		if (equals.startsWith("<")){

			equals=equals.substring(1);
			int testVal = Integer.parseInt(equals);
			
			if (val<testVal){
				return true;
			}  else {
				return false;
			}		
			
		}
		
		
		
		return false;
		
	}
	/** Like Serialise, but uses comas for neatness **/

	public String toString() {
		return this.serialise().replace("&", ",");
	}
	/** Serialise the data into an array in the form &var=value&var2=value2 etc **/

	public String serialise() {

		String data = "";

		Set<String> keyset = super.keySet();

		for (String string : keyset) {

			String currentItemName = string;
			String currentItemValue = super.get(currentItemName);

			data = data + currentItemName + "=" + currentItemValue + "&";

		}

		return data;
	}


	
	
	
	
	
	
	
	
	
}
