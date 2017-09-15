package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;


/**
 * A special hashset of objects that allows serializing and deserialising the objects to a list of objectnames as Strings<br>
 * 
 * In order to use the hashset the list of objectnames has to be converted to realobjects by running CacheObjectSet
 * this only needs to be triggered once - provided all the objects are loaded at that point and thus known to the game
 * 
 * @author darkflame
 *
 */
public class SceneObjectSet extends HashSet<SceneObject> {


	/** The general console log **/
	public static Logger Log = Logger.getLogger("JAMcore.SceneObjectSet");

	HashSet<String> tempObjectnames;// = new HashSet<String>();

	/**
	 * clones the supplied set
	 * @param touching
	 */
	public SceneObjectSet(SceneObjectSet touching) {
		super(touching); //clones only the actual contents of the real hashset
		if (touching.tempObjectnames!=null && !touching.tempObjectnames.isEmpty()){
			tempObjectnames  = new HashSet<String>(touching.tempObjectnames); //and we copy the names as well in case the set hasnt had its sceneobjects cached yet
		}
	}

	public SceneObjectSet() {
		super();
	}

	/**
	 * This splits the objects names and remembers this, but this set itself doesn't get filled till cache is called on it
	 * 
	 * @param objectnames
	 */
	public void fromSerialisedString(String objectnames)
	{
	//	Log.info("Adding Touching Data = " + objectnames);
		
		if (!objectnames.isEmpty()) {

			if (tempObjectnames == null) {
				tempObjectnames = new HashSet<String>();
			} else {
				tempObjectnames.clear();
			}
			
			// crop start and end off []
			objectnames = objectnames.substring(1, objectnames.length() - 1);

		//	Log.info("Touching = " + objectnames);
			String[] touchingArray = objectnames.split(",");

			if (touchingArray.length > 0) {
				// split arrays
				for (String string : touchingArray) {

					Log.info("touch = " + string.trim());
					// store
					tempObjectnames.add(string.trim());

				}
			}

		}
	}

	public void fromNameCollection(String[] replacements) {
		if (tempObjectnames==null){
			tempObjectnames = new HashSet<String>();
		}
		tempObjectnames.clear();
		for (String name : replacements) {
			tempObjectnames.add(name);
		}
	}

	public void fromNameCollection(Collection<String> replacements) {
		if (tempObjectnames==null){
			tempObjectnames = new HashSet<String>();
		}
		tempObjectnames.clear();
		tempObjectnames.addAll(replacements);
	}

	public String toSerialisedString()
	{
		
		
		String serialised = "[";

		//Note: objects might not be loaded yet
		//If this is the case, we just use the stored name strings instead
		if (super.isEmpty() && tempObjectnames!=null && !tempObjectnames.isEmpty()){

			boolean first = true;
			for (String name : tempObjectnames) {
				if (first){				
					first=false;	
					serialised=serialised+name;
				} else {
					serialised=serialised+","+name;
				}
			}

		} else {


			//if objects are loaded however, we can do our serialization from our objects
			//(Note; We dont store the names if we have the objects - this is simply a safety thing to ensure the objects/names never are out of sycn. They cant be out of sycn if we only have one at a time)


			boolean first = true;
			for (SceneObject so : this) {

				if (first){				
					first=false;	
					serialised=serialised+so.getName();
				} else {
					serialised=serialised+","+so.getName();
				}

			}

		}

		serialised=serialised+"]";

		return serialised;
	}

	/**
	 * rebuilds this object set from the list of stored object names
	 * These names should be unique.
	 * Once loaded we clear the name list.
	 * 
	 * NOTE: this function currently assumes all objects are loaded
	 */
	public void cacheObjectSet(){
		this.clear();		
		if (tempObjectnames!=null){

			for (String objectname : tempObjectnames) {			
				SceneObject sceneObject = SceneObjectDatabase.getSingleSceneObjectNEW(objectname, null, true);
				add(sceneObject);			
			}

			tempObjectnames.clear();
		}
	}


	/**
	 * Note: This returns the real number of SceneObject elements.
	 * There could still be other ones specified but not loaded yet in the tempObjectName array
	 */
	@Override
	public int size() {
		return super.size();
	}

	/**
	 * Only returns true if both the (true) Set of SceneObjects is empty AND the internal tempObjectName list
	 */
	@Override
	public boolean isEmpty() {
		if (super.isEmpty() && (tempObjectnames==null  || tempObjectnames.isEmpty()) ){
			return true;
		}
		return false;
	}

	/**
	 * If the objects are loaded, it gets their names, else it gets the internal list of object names that have yet to be cached
	 * into a proper object list for this
	 * 
	 * @return
	 */
	public ArrayList<String> getNames() {
		
		if (super.isEmpty() && tempObjectnames!=null && !tempObjectnames.isEmpty()){
			return new ArrayList<String>(tempObjectnames);
		}
		
		ArrayList<String> objectsnames = new ArrayList<String>();
		
		for (SceneObject so : this) {
			objectsnames.add(so.getName());
		}

		return objectsnames;
	}

	/**
	 * returns true if these two sets refer to the same objects.<br>
	 * This isn't as simple as a equals, as one or both sets might not yet have been cached into objects.<br>
	 * We thus get the object names from both sets, and compare them instead.<br>
	 * <br>
	 * BEWARE: dont attempt touching stuff on objects that have the same name as this can cause confusion with whats touching what 
	 * @param set
	 * @return
	 */
	public boolean contentsMatchs(SceneObjectSet set){
		ArrayList<String> ourNames = this.getNames();
		ArrayList<String> comparisonNames = set.getNames();
		
		return ourNames.containsAll(comparisonNames);
	}


	


}
