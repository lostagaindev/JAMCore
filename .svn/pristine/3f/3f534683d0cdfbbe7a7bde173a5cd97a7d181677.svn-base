package com.lostagain.Jam.InstructionProcessing;

import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;

/**
 * Currently just stores a string, and if should preform a global search or not when looking for a object called this string.
 * 
 *  In future this will be more intelligent, and cache objects when not a changable variable
 *  
 *  NOTE: when storing objects we should store a direct reference to the "objectswithsamename" set that the database holds
 *  This way if more objects are added, this correctly returns all of them, rather then just the ones in the set when first cached.
 *  we should not, however, ever write to or remove from this set!!!
 *  There might be a guava thing we can wrap it in to prevent changes.
 *  
 * @author darkflame
 *
 */
public class CommandParameter {
	public static Logger Log = Logger.getLogger("JAMCore.CommandParameter");

	
	//the parameterString, as its written in the file.
	//We do, however, remove a searchscene prefix if its present. (see below)
	String parameterString = "";
	
	
	Optional<Integer> parameterAsInt    = Optional.absent(); //we can get a tiny bit more proformance if needed by using ints (not Integer), and then using some other method to determain their presence of absence
	Optional<Double>  parameterAsDouble = Optional.absent(); //we can get a tiny bit more proformance if needed by using ints (not Integer), and then using some other method to determain their presence of absence
	
	Optional<Boolean>  parameterAsBoolean = Optional.absent(); //we can get a tiny bit more proformance if needed by using ints (not Integer), and then using some other method to determain their presence of absence

	
	/**
	 * The set of objects called "parameterString" if that string represented a object or objects name.
	 * Note, this list will be unmodifiable yet auto-updating to include new created objects called the same thing.
	 * For this reason its safe to cache here.
	 * We dont cache, however, if the ParameterString is a variable, as the object it refers to could change
	 */
	private Set<? extends SceneObject> sceneObjects = null;
	
	/**
	 * Same as above, but in case we represent just one object
	 */
	private SceneObject sceneObject = null;
	
	/**
	 * If we represent a object or set of objects do we search globally? (ie, the whole loaded game) 
	 */
	boolean searchGlobal = true;

	boolean containsVariables = false;
	
	public CommandParameter(String string) {
		//remove any command extensions if present
		//atm this is just to determine if this parameter should do a local or global search (global by default)
		if (string.startsWith("inscene__")){
			string = string.substring("inscene__".length());
			searchGlobal=false;
		}
		
		 parameterString = string;
		 
			if (parameterString.contains("<")) {
				containsVariables = true;
			} else {
				containsVariables= false;
			}
				
	}
	
	public String getAsString(){
		return parameterString;
	}
	
	
	//NOTE: in future we possibly should check in this was a variable, and always regenerate if so
	//currently this is dealt with by the commmandparameterset which regenerates all CommandParemeter each time if any variable was present in any of the parameters
	public int getAsInt(){
		
		//create in if not already made
		if (!parameterAsInt.isPresent()){	
			parameterAsInt = Optional.of( (int)Double.parseDouble(parameterString) ); //we parse as double and round down in case it came from a double source
			parameterAsDouble = Optional.of( Double.parseDouble(parameterString) );
			
		}
		
		return parameterAsInt.get();
	}
	
	public double getAsDouble(){
		
		//create in if not already made
		if (!parameterAsDouble.isPresent()){				
			parameterAsDouble = Optional.of( Double.parseDouble(parameterString) ); //we parse as double and round down in case it came from a double source
			parameterAsInt = Optional.of( (int)Double.parseDouble(parameterString) ); 
		}
		
		return parameterAsDouble.get();
	}
	
	/** is it a int or double? ***/	
	public boolean isNumber() {
		
		if (parameterAsInt.isPresent() || parameterAsDouble.isPresent() ){
			return true;
		}
				
		//we parse as a double, then cast down for ints
		//Double num = Doubles.tryParse(parameterString);
		//cant use Doubles.tryParse in gwt at the time this is written so we need the crude try/catch		
		Double num;
		try {
			num = Double.parseDouble(parameterString);
		} catch (NumberFormatException e) {
			num = null;
		}
		
		if (num!=null){
	//		Log.info("setting to"+num);
			parameterAsInt    = Optional.of( (int)num.doubleValue() );
			parameterAsDouble = Optional.of( num );			
			return true;
		}
	//	
		//Log.info("returning false");
		return false;
	}


	/**
	 * The calling object is needed in case this object is a variable that refers to it
	 * @param callingObject
	 * @return
	 */
	public Set<? extends SceneObject> getAsObjects(SceneObject callingObject) {
		
		//hmm...oddly, benchmarking this shows caching objects doesn't make too much difference.
		//At least, for small object databases.
		//It seems neater somewhat to keep it in for now though
		if (sceneObjects==null || containsVariables){
			sceneObjects = SceneObjectDatabase.getSceneObjectNEW(parameterString,callingObject,searchGlobal);
			
			if (sceneObjects==null || sceneObjects.size()==0){
				Log.severe("no objects found called:"+parameterString);
				
			}
		}
		
		return sceneObjects;
	}

	public SceneObject getAsObject(SceneObject callingObject) {
		
		if (sceneObject==null || containsVariables){
			sceneObject = SceneObjectDatabase.getSingleSceneObjectNEW(parameterString,callingObject,searchGlobal);
			
			if (sceneObject==null){
				Log.severe("no object found called:"+parameterString);
				
			}
		}
		
		return sceneObject;
	}

	public boolean getAsBoolean() {
		if (!parameterAsBoolean.isPresent()){	
			parameterAsBoolean = Optional.of( Boolean.parseBoolean(parameterString) ); 			
		}		
		return parameterAsBoolean.get();
	}
	
}
