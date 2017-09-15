package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.Iterator;

import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;


/** A list of conditions, and a specification as to if any of these conditions must
 * be true, or all of them.
 * Provides a very easy "check" function to see if the conditions are all met**/
public class ConditionalList extends ArrayList<ConditionalLine> {

	//the types
	enum ConditionalListType {
		AND,
		OR
	}

	//note; a conditional list will only ever store one type, 
	//you should use sublists for mixing ands and ors.
	//this is to prevent  ambiguous lines like " x=7 && x=4 || y=2 "
	ConditionalListType listsType = ConditionalListType.AND;



	public ConditionalList() {
		super();
	}

	/** an array list of conditionallines**/
	public ConditionalList(String conditionalstatementlist) {

		super();

		String conditionsArray[];
		
		//lists can be && or || but not both 
		//this is to prevent  ambiguous lines like " x=7 && x=4 || y=2 "
		if (conditionalstatementlist.contains("&&")) {
			// Log.info("splitting conditions...");
			conditionsArray = conditionalstatementlist.split("&&");
			listsType = ConditionalListType.AND;
		} else if (conditionalstatementlist.contains("||")){
			//untested
			conditionsArray = conditionalstatementlist.split("\\|\\|");
			listsType = ConditionalListType.OR;
		}else{
			// Log.info("just one condition...");
			conditionsArray = new String[] { conditionalstatementlist };
		}

		for (String line : conditionsArray) {

			ConditionalLine newLine = new ConditionalLine(line);
			this.add(newLine);

		}




	}

	/** checks conditional, return true if the conditions are passeed **/
	static public  boolean checkConditionals(ConditionalList conditions,SceneObject callingObject) {

		// if we are a && list all the conditions must be true
		if (conditions.listsType==ConditionalListType.AND){
			for (ConditionalLine condition : conditions) {

				Boolean conditionalPassed = condition.checkConditional(callingObject);

				//GreyLog.info("new check system resulted in a...."+conditionalPassed);

				if (conditionalPassed){
					//we continue to next condition
					continue;
				} else {
					//we return false as condition was not passed
					return false;
				}


			}

			return true;
		}

		// if we are a || list any of the conditions must be true
		if (conditions.listsType==ConditionalListType.OR){

			for (ConditionalLine condition : conditions) {

				Boolean conditionalPassed = condition.checkConditional(callingObject);

				//GreyLog.info("new check system resulted in a...."+conditionalPassed);

				if (conditionalPassed){
					//we return true as any of them can result in a pass
					return true;
				} else {
					//we continue to the next condition
					continue;
				}


			}
			return false;
		}

		//we shouldn't get this far, but by default we assume the conditions are met
		return true;

	}

	/**represents the conditions as a newline separated list **/
	@Override 
	public String toString(){
		String  conditions_as_string="";
		
		for (Iterator<ConditionalLine> it = this.iterator(); it.hasNext();) {
			ConditionalLine type = it.next();
			
		     	conditions_as_string =  conditions_as_string + type.toString();
			
			 //if there's more
			if (it.hasNext()){
				conditions_as_string =  conditions_as_string  + "\n" + listsType.toString();			
			}
			 
		}		
	
		return "("+conditions_as_string+")";
	}

}
