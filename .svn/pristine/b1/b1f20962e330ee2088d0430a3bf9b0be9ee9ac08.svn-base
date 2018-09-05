package com.lostagain.Jam.SceneObjects;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.Table;
import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.GameStatistics;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDivObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneInputObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneLabelObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneVectorObject;


//---------------------------------------------------------------------------------------------------------------------------------
//----These NEW functions should replace the old ones in SceneWidget eventually their NEW suffix should be removed-----------------
//---------------------------------------------------------------------------------------------------------------------------------

//NOTE: These new functions search GLOBAL over all the currently loaded scenes
//The old functions searched inconsistently across the current scene and sometimes global
//We do, however, allow a boolean flag to search current scene only.
//In future a script function might be introduced to limit the returned objects to the current scene.
//This might speed up very large games where lots of scenes are loaded at once
//----
//If trying to understand this code its best to follow over the route of a specific request, rather then 
//trying to just read it all and keep it all in your head at once.
//Say, a get text object request
//Start at say, getTextObjectNEW(String,SceneObject,boolean)
//Then follow over what it uses.

public class SceneObjectDatabase  {

	
	public static Logger DBLog = Logger.getLogger("JAMCore.SceneObjectDatabase");

	//static boolean useOldSytem = false;


	/** Global hashmap of all known label objects (for search purposes) <br>
	 * <br>
	 * Remember all names are stored lowercase! use .toLowerCase() when searching **/
	//public static HashMultimap<String, SceneLabelObject> all_label_objects = HashMultimap
	//		.create(); //nb; will eventually replace text above once SceneDialogObject extends SceneLabelObject
	private static void testResults(String search, Set<? extends Object> results,
			Set<? extends Object> newresults) {

		DBLog.info("testing results");

		if (results == null || newresults==null){

			DBLog.info("one or both results null");
			if (results == null && newresults!=null){
				DBLog.severe("results null, new not:"+search);
				DBLog.severe("newresults had:"+newresults.size());
			}
			if (results != null && newresults==null){
				DBLog.severe("Newresults null, results not:"+search+" Results had:"+results.size());
			}			

			return;
		}

		if (results.size() != newresults.size()){
			DBLog.severe("Warning new database did not return the same number of results for:"+search);
		}
	}
	
	/*

	public static void addInputObjectToDatabase(SceneInputObject object){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.Input);
	}

	public static void addVectorObjectToDatabase(SceneVectorObject object){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.Vector);
	}

	public static void addDivObjectToDatabase(SceneDivObject object){	
	//	SceneObjectDatabase_oldcore.addDivObjectToDatabase(object);
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.Div);
	}

	public static void addTextObjectToDatabase(SceneLabelObject object){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.Label);
	}

	public static void addSpriteObjectToDatabase(SceneSpriteObject object){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.Sprite);
	}
	public static void addDialogueObjectToDatabase(SceneDialogObject object){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object,SceneObjectType.DialogBox);
	}
	*/
	
	/**
	 * Adds a object to the database under the catagory of the type specified.
	 * 
	 * NOTE: we dont automatically check the type from the ObjectsCurrentState. type data because
	 * objects can be many types and this function will be called once for each type in the objects hyrachy
	 * ie. Dialog will call its superclass Label which will call its superclass Div during GWT construction of a dialog object
	 * Each constructor method in that chain will class this function, adding itself as a different type.
	 * Thus the dialogue object is also stored as a label object and as a div object.
	 * 
	 * @param object
	 * @param asType
	 */
	private static void addObjectToDatabase(SceneObject object, SceneObjectType asType){	
		SceneObjectDatabase_newcore.addObjectToDatabase(object, asType);
		
	}
	
	/**
	 * Adds a object to the database of internal objects
	 * This function will automatically add it under each type its compatibility with - dont call this multiple times
	 * in the objects construction hyreachy
	 * 
	 * @param sceneObject
	 */
	public static void addObjectToDatabase(SceneObject sceneObject) {
		
		//can move this function to SceneObjectDatabase rather then having a loop here
		for (SceneObjectType supportedtype : sceneObject.getObjectsCurrentState().getObjectCapabilities()) {			
			DBLog.info("Adding "+sceneObject.getName()+" to Database as:"+supportedtype);
			SceneObjectDatabase.addObjectToDatabase(sceneObject,supportedtype);							
		}		
				
	}




	public static void clearAllObjectDataMaps() {
		//old
		//SceneObjectDatabase_oldcore.clearAllObjectDataMaps();
		//new
		SceneObjectDatabase_newcore.clearAllObjectDataMaps();

	}


	//Sets of objects
	//Set of sprites
	public static Set<? extends IsSceneSpriteObject> getSpriteObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){


		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	Set<SceneSpriteObject> results = SceneObjectDatabase_oldcore.getSpriteObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		//	return results;
		//}
		//------new;
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneSpriteObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Sprite,search,callingObject,searchGlobal);

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----

		//		testResults(search, results, newresults);
		return newresults;

	}

	//Set of Divs
	public static Set<? extends IsSceneDivObject> getDivObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){


		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//


		//	Set<? extends IsSceneDivObject> results = SceneObjectDatabase_oldcore.getDivObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return results;

		//}

		//DBLog.info("New db test");	
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneDivObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Div,search,callingObject,searchGlobal);

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults(search, results, newresults);

		return newresults;
	}

	
	public static  Set<IsSceneDialogueObject> getDialogueObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		Set<IsSceneDialogueObject> results = SceneObjectDatabase_newcore.getObjects(SceneObjectType.DialogBox,search,callingObject,searchGlobal);
		return results;				
	}
	
	//text
	public static  Set<IsSceneLabelObject> getTextObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){



		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//


		//	Set<SceneLabelObject> results = SceneObjectDatabase_oldcore.getTextObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		///	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

			//return results; //temp remove comment to use old method
	//	}



		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//
	//	DBLog.info("new db test");		
		Set<IsSceneLabelObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Label,search,callingObject,searchGlobal);
	//	DBLog.info("new db test..");		
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults(search, results, newresults);

		return newresults;
	}

	//vector
	public static Set<? extends IsSceneVectorObject> getVectorObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//


		//	Set<? extends SceneVectorObject> results = SceneObjectDatabase_oldcore.getVectorObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return results;
		//}

		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneVectorObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Vector,search,callingObject,searchGlobal);

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults(search, results, newresults);

		return newresults;
	}



	//-------------------------------------------------------------------------------------------------------------


	//returns single objects
	//input	
	public static IsSceneInputObject getSingleInputObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//


		//	SceneInputObject result = SceneObjectDatabase_oldcore.getSingleInputObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		//	return result;
		//}

		//------
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneInputObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Input,search,callingObject,searchGlobal);
		IsSceneInputObject newresult = newresults.iterator().next();
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----

		return newresult;
	}






	public static IsSceneVectorObject getSingleVectorObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){


		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	SceneVectorObject result = SceneObjectDatabase_oldcore.getSingleVectorObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return result;
		//}
		//------
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneVectorObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Vector,search,callingObject,searchGlobal);
		IsSceneVectorObject newresult = newresults.iterator().next();

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----
		return newresult;
	}

	public static IsSceneLabelObject getSingleTextBoxObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	SceneLabelObject result = SceneObjectDatabase_oldcore.getSingleTextBoxObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return result;
		//}
		//------new;
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneLabelObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Label,search,callingObject,searchGlobal);
		IsSceneLabelObject newresult = newresults.iterator().next();

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----

		return newresult;


	}
	public static IsSceneDivObject getSingleDivObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	IsSceneDivObject result = SceneObjectDatabase_oldcore.getSingleDivObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		//	return result;
		//}

		//------new;
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneDivObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Div,search,callingObject,searchGlobal);
		IsSceneDivObject newresult = null;
		if (newresults!=null){
			newresult = newresults.iterator().next();
		}
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----
		return newresult;

	}

	public static IsSceneSpriteObject getSingleSpriteObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	SceneObject result =  SceneObjectDatabase_oldcore.getSingleSpriteObjectNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return (IsSceneSpriteObject) result; //not used
		//}

		//------new;
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends IsSceneSpriteObject> newresults = SceneObjectDatabase_newcore.getObjects(SceneObjectType.Sprite,search,callingObject,searchGlobal);
		IsSceneSpriteObject newresult = null;
		if (newresults!=null){
			newresult = newresults.iterator().next();
		}

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//-----

		return newresult;

	}


	//Generic types
	//.....
	//...
	public static Set<? extends SceneObject> getSceneObjectNEW(String name, SceneObject callingObject, boolean searchGlobal) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
	//	if (useOldSytem){	
	//		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//
	//		Set<? extends SceneObject> results =  SceneObjectDatabase_oldcore.getSceneObjectNEW(name,callingObject,searchGlobal);
	//		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
	//		GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
	//		return results;
	//	}


		//DBLog.info("new db used");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<? extends SceneObject> newresults = SceneObjectDatabase_newcore.getSceneObject(name, null, callingObject, searchGlobal);	
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults(name, results, newresults);

		return newresults;
	}	

	public static SceneObject getSingleSceneObjectNEW(String search, SceneObject callingObject, boolean searchGlobal) {
		//long TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//SceneObject result = SceneObjectDatabase_oldcore.getSingleSceneObjectNEW(search,callingObject,searchGlobal);

		//long TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		Set<? extends SceneObject> results =  getSceneObjectNEW(search,  callingObject,  searchGlobal);
		SceneObject newresult = null;
		if (results!=null && !results.isEmpty()){
			newresult = results.iterator().next();
			if (results.size()>1){

				DBLog.info("single scene object requested:"+search+" but "+results.size()+" found. Using arbitary first");	
			}
		} else {
			DBLog.info("no results found for"+search);		
		}
		
		
		return newresult;
	}

	/*


//REMOVED
//We should have zero dependancy on SceneObjectVisual now
//Everything needs to run on SceneObject or Interfaces of specific sceneobject types
 */
	/**
	 * This method should be phased out in favor of getSceneObjectNew
	 * @param search
	 * @param callingObject
	 * @param searchGlobal
	 * @return
	 
	public static Set<SceneObjectVisual> getSceneObjectVisualNEW(String search, SceneObject callingObject, boolean searchGlobal) {	
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
	//	if (useOldSytem){
		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken		//
		//	Set<? extends SceneObjectVisual> results = SceneObjectDatabase_oldcore.getSceneObjectVisualNEW(search,callingObject,searchGlobal);
		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return (Set<SceneObjectVisual>) results; //bad cast, this method wont be used in future anyway though
		//}	

		//--------------- new method;
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<SceneObject> newresults = SceneObjectDatabase_newcore.getSceneObject(search,null,callingObject,searchGlobal);					

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults(search, results, newresults);

		//TEMP Conversion of type (This whole method will be removed anyway once instruction processor no longer takes sceneobject visuals)
		if (newresults!=null){
		Set<SceneObjectVisual> results = new HashSet <SceneObjectVisual>();
		for (SceneObject so : newresults) {
			results.add((SceneObjectVisual) so);
		}
		return results;
		} else {
			return null;
		}



	
	}
**/
	
	//---------------
	//-------
	//-------
	
	//visuals should not be needed anymore directly
	/*
	

	public static SceneObjectVisual getSingleSceneObjectVisualNEW(String search, SceneObject callingObject, boolean searchGlobal) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		//	SceneObjectVisual result = SceneObjectDatabase_oldcore.getSingleSceneObjectVisualNEW(search,callingObject,searchGlobal);

		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return result;
		//}

		//---------------
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		SceneObject newresult = SceneObjectDatabase_newcore.getSingleSceneObject(search, null, callingObject, searchGlobal);

		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		//if (result!=newresult){
		//	DBLog.severe("Warning new database did not return the same result for:"+search);
		//}


		return (SceneObjectVisual) newresult; //temp cast, this method should be phased out anyway
	}

*/


	//---------------
	//-------
	//-------



	//probably should be moved into supertype?
	/*
	public static Set<SceneSpriteObject> getSceneSpriteObjectFromSpriteSpecificVariable(String searchVariable) {
		//long TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//

		Set<SceneSpriteObject> result = SceneObjectDatabase_oldcore.getSceneSpriteObjectFromSpriteSpecificVariable(searchVariable);

		//long TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		return result;
	}

	 */

	public static Collection<? extends SceneObject> getAllObjectsOnScene(SceneWidget sceneWidgetVisual) {

		Collection<? extends SceneObject> results = sceneWidgetVisual.getScenesData().allScenesCurrentObjects();

		return results;
	}


	public static Set<IsSceneObject> getAllGamesObjects() {
		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken	
		//	Set<SceneObjectVisual> results = SceneObjectDatabase_oldcore.getAllGamesObjects();
		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
			//return results;
		//}
		DBLog.info("new db testt");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		Set<IsSceneObject> newresults = SceneObjectDatabase_newcore.getAllGamesObjects();		
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		//testResults("AllObjectsTest", results, newresults);

		return newresults;
	}

	public static void removeObjectFromAllLists(SceneObject sceneObject) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken	
		//	SceneObjectDatabase_oldcore.removeObjectFromAllLists(sceneObject.getObjectsCurrentState().ObjectsName);
		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken

		//}

	//	DBLog.info("new db testt");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		SceneObjectDatabase_newcore.removeObjectFromAllLists(sceneObject);
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken



	}

	/*
	public static HashMultimap<String, SceneSpriteObject> getAll_sprite_objects() {

		long TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken	
		HashMultimap<String, SceneSpriteObject> results = SceneObjectDatabase_oldcore.getAll_sprite_objects();
		long TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken



		DBLog.info("new db testt");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		SceneObjectDatabase_newcore.getGamesObjectsOfType(SceneObjectType.Sprite);
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		return results;
	}



	public static HashMultimap<String, SceneLabelObject> getAll_text_objects() {

		long TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken	
		HashMultimap<String, SceneLabelObject> results = SceneObjectDatabase_oldcore.getAll_text_objects();
		long TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken



		DBLog.info("new db testt");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		SceneObjectDatabase_newcore.getGamesObjectsOfType(SceneObjectType.Label);
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		return results;
	}

	 */

	public static SceneObject getSingleObjectOfType(String objectName, SceneObject callingObject, boolean searchGlobal,
			SceneObjectType type) {

		long TimeTakenStart = 0;
		long TimeTakenEnd = 0;		
		//if (useOldSytem){

		//	TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken	
		//	SceneObject result = SceneObjectDatabase_oldcore.getSingleObjectOfType(objectName, callingObject, searchGlobal, type);
		//	TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		//	GameStatistics.TotalOldDatabaseLookupTime = GameStatistics.TotalOldDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken
		//	return result;
		//}


		DBLog.info("new db testt");		
		TimeTakenStart = System.currentTimeMillis();		//purely for profiling the time taken			//		
		SceneObject newresult = SceneObjectDatabase_newcore.getSingleSceneObject(objectName, type, callingObject, searchGlobal);
		TimeTakenEnd = System.currentTimeMillis();			//purely for profiling the time taken
		GameStatistics.TotalNewDatabaseLookupTime = GameStatistics.TotalNewDatabaseLookupTime +(TimeTakenEnd-TimeTakenStart);//purely for profiling the time taken


		return newresult;
	}

	/**
	 * returns the whole database - for debugging only
	 * @return
	 */
	public static Table<SceneObjectType, String, HashSet<SceneObject>> getObjectDatabase() {
		return SceneObjectDatabase_newcore.getObjectDatabase();
	}

	/**
	 * This stores the last object that fired a UserActionUsed.
	 * For example if "grab" was triggered last you can use it to retrieve what was grabbed.
	 * TODO: implement
	 */
	//public static SceneObject lastActuatedObject  = null;

	
	
	static public void wasLastObjectClicked(SceneObject lastupdated) {
		CurrentScenesVariables.lastSceneObjectClicked = lastupdated;
	}
	/**
	 * This function will tell the instruction processor this object was the last updated.
	 * It does this by checking this objects type then setting the right "last___" variable to this in InstructionProcessor.
	 * 
	 * In future this will need updating, once objects can support a few different commands at once 
	 * (ie, if its Sprite object it will also count as the last Div, because a Sprite will do all the functions of a Div)
	 */
	static public void wasLastObjectUpdated(SceneObject lastupdated) {
	
		DBLog.info("============last clicked on setting to:"+ lastupdated.getObjectsCurrentState().ObjectsName+"(type is a"+ lastupdated.getObjectsCurrentState().getPrimaryObjectType().name()+")");
	
		CurrentScenesVariables.lastSceneObjectUpdated = lastupdated; 
		
		//SceneObjectType types = lastupdated.objectsCurrentState.getPrimaryObjectType();	
		HashSet<SceneObjectType> types = lastupdated.getObjectsCurrentState().getObjectCapabilities();
	
	
		for (SceneObjectType type : types) {
	
			switch (type) {
	
			case Sprite:
				CurrentScenesVariables.lastSpriteObjectUpdated      = (IsSceneSpriteObject) lastupdated; 
				break;
			case DialogBox:
				CurrentScenesVariables.lastTextObjectUpdated        = (IsSceneLabelObject)    lastupdated;
				CurrentScenesVariables.lastDialogueObjectUpdated    = (IsSceneDialogueObject) lastupdated;
				break;
			case Label:
				CurrentScenesVariables.lastTextObjectUpdated        = (IsSceneLabelObject) lastupdated;
				break;
			case Div:
				CurrentScenesVariables.lastDivObjectUpdated         = (IsSceneDivObject) lastupdated;
				break;
			case Vector:
				CurrentScenesVariables.lastVectorObjectUpdated      = (IsSceneVectorObject) lastupdated;
				break;
			case Input:
				CurrentScenesVariables.lastInputObjectUpdated       = (IsSceneInputObject) lastupdated;
				break;
			case InventoryObject:
				CurrentScenesVariables.lastInventoryObjectClickedOn = (IsInventoryItem) lastupdated;
				break;
			}
	
	
		}
	
		if (JAMcore.DebugMode){
			lastupdated.updateDebugGlobalVariableInfo();
			//
		}
		
		
	}

	public static SceneWidget currentScene = null;

	public static void clearAllGameVariables() {

		SceneObjectDatabase.currentScene=null;
		CurrentScenesVariables.lastSceneObjectUpdated = null;
		CurrentScenesVariables.lastSceneObjectClicked = null;
		
		//InstructionProcessor.lastDivObjectClickedOn=null;
		CurrentScenesVariables.lastDivObjectUpdated=null;
		CurrentScenesVariables.lastInputObjectUpdated=null;
		CurrentScenesVariables.lastInventoryObjectClickedOn=null;
		CurrentScenesVariables.lastObjectThatTouchedAnother=null;
		CurrentScenesVariables.lastSpriteObjectUpdated=null;
		CurrentScenesVariables.lastTextObjectUpdated=null;
		CurrentScenesVariables.lastDialogueObjectUpdated = null;
		//InstructionProcessor.lastVectorObjectClickedOn=null;
		CurrentScenesVariables.lastVectorObjectUpdated=null;
		
	}

	}
