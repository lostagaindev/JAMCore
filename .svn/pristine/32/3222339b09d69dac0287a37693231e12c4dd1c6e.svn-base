package com.lostagain.Jam.InstructionProcessing;

import java.util.Set;
import java.util.logging.Logger;

import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.OptionalImplementations;
import com.lostagain.Jam.Movements.MovementState;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDivObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;


/** in development function to replace the current string based condition comparison 
 * It stores a enum of a condition, as well as the string parameters to go with it
 * 
 * A condition can also be a ConditionList too, which allows for nested conditions **/
public class ConditionalLine {


	static final String ERROR_CANT_FIND_OBJECT_SIGNIFIER = "ERROR_cant find object";
	public static Logger Log = Logger.getLogger("JAMCore.ConditionalLine");

	
	public enum ConditionType{

		TRUE,
		CONDITIONLIST,
		hasclass,
		hasnotgotclass,
		divhasclass,
		divhasnotgotclass,
		hasparent,
		hasnotgotparent,
		hasproperty,
		hasnotgotproperty,
		isonscene,
		/**
		 * A simple check to see if the object has the specified name.
		 * Used mostly internally for ItemMixRequirement testing.
		 */
		iscalled,
		isnotcalled,
		iscolliding,
		isnotcolliding,
		ispointwithin,
		ispointnotwithin,
		/**
		 * does this object have collisions set?
		 * collisions=none will return false, anything else true
		 *  
		 */
		iscorporeal,
		isincorporeal,
		/** is the specified object within the specified region ? 
		 * If more then one object has this name will return true if its in ANY of them**/
		iswithin,
		/** is the specified object not within the specified region ? 
		 * If more then one object has this name will return true if its NOT in ANY of them**/
		isnotwithin,
		currentmovement,
		currentmovementnot,

		ismoving,
		/** checks if an object is currently visible**/
		isvisible,
		checkparagraph,
		playerhas,
		playerdoesnothave,
		/** is the player holding the specified item **/
		playerholds,
		/** player is not holding the specified item **/
		playerdoesnothold,
		ison,
		isnoton,
		closertothan,
		furtherfromthan, 
		checkobjectvalue,
		checkobjectnotvalue,		
		checkvalue,
		checknotvalue,
		ondebugmode,
		notondebugmode,
		/**
		 * checks if a named action set is scheduled
		 * objectname,actionsetname
		 **/
		isscheduled,
		isnotscheduled
	}

	public ConditionType specifiedCondition;
	
	//public String currentProperty;
	
	CommandParameterSet params;
	


	/** There is a possibility for conditions to contain other sub lists. 
	 * So, this would be used for brackets within brackets **/
	ConditionalList conditionList; //if any. 

	public ConditionalLine(ConditionType specifiedCondition, String currentProperty, ConditionalList conditionList) {
		super();
		this.specifiedCondition = specifiedCondition;
		//this.currentProperty = currentProperty;
		
		this.params = new CommandParameterSet(currentProperty);
		
		this.conditionList = conditionList;
	}

	public ConditionalLine(String line){

		String currentConditionType;

		String currentProperty;
		//detect if its a condition list itself (ie, a sublist)
		if (line.startsWith("(")) {		

			//its a condition list
			specifiedCondition = ConditionType.CONDITIONLIST;
			//store the list
			String conditions =line.trim().substring(line.indexOf("(")+1,
					line.indexOf(")")).trim();

			conditionList=new ConditionalList(conditions);
			currentProperty="";
			return;

		}

		//split into condition and param
		if (line.contains("=")) {			
			currentConditionType = line.split("=")[0].trim();
			currentProperty = line.split("=")[1].trim();
			this.params = new CommandParameterSet(currentProperty);
			
		} else {
			currentConditionType = line.trim();
			currentProperty = "";
		}

		//test if its a special one
		//we test for these separately as they are designated by a capital letter
		if (currentConditionType.equalsIgnoreCase("TRUE")){
			specifiedCondition = ConditionalLine.ConditionType.TRUE;
			return;
		}

		//get and store enum
		try{
			specifiedCondition= Enum.valueOf(ConditionalLine.ConditionType.class,currentConditionType.toLowerCase());
		} catch (Exception e) {			
			Log.severe("not a recognised conditional:"+currentConditionType);
			return;
		}


	}
	/** checks if the current game conditions pass's this condition **/
	public boolean checkConditional(SceneObject callingObject){		
		return checkConditional(this,callingObject);
	}

	static private boolean checkConditional(ConditionalLine conditionalline, SceneObject callingObject){
		
		
		String currentProperty = ""; //default to no property
		
		CommandParameterSet params = conditionalline.params;
		
		if (params!=null){
			//prepare parameters if any 
			boolean usesVariables   = true;
			boolean usesRandomSplit = false; //never use random splits in conditionals
			boolean usesMaths = true;
			
			params.prepareParameters(callingObject,usesVariables,usesRandomSplit,usesMaths);	
			//currently just get as raw string;
			currentProperty = params.getProccessedParameterString(); // get the processed parameters as a string
		}
		
		//
		//TODO: Just like in instructionprocessor,  currentProperty and CurrentParams[] should be slowly replaced
		//With a just using Parameters, which contains the same information
		//
		
		String CurrentParams[] = null;
		
		if (currentProperty!=null && !currentProperty.isEmpty()){
			CurrentParams =  currentProperty.split(",");
		}

		String currentConditionAsString= conditionalline.specifiedCondition.toString();
		Log.info("currentConditionType   =   " + currentConditionAsString +" | currentProperty   =   " + currentProperty);

		//first we switch based on the conditionalline's type
		switch (conditionalline.specifiedCondition){

		case TRUE:
			//easy
			return true;
			//break; unreachable
		case hasclass:
		{			
			return testForClassOnObject(callingObject, currentProperty,
					CurrentParams);

		}
		case hasnotgotclass:
		{
			return !testForClassOnObject(callingObject, currentProperty,
					CurrentParams); //same as above but we invert the result with !

		}
		case divhasclass:
		{			

			return testIfDivHasClass(currentProperty, CurrentParams);	

		}
		case divhasnotgotclass:
		{
			return !testIfDivHasClass(currentProperty, CurrentParams);	//same as above but we invert the result with !

		}
		case hasparent:
		{
			
			if (callingObject.getObjectsCurrentState().positionedRelativeToo!=null){

				Log.info("testing if "+callingObject.getName()+" has parent.(it did)");
				return true;
			}			

			Log.info("testing if "+callingObject.getName()+" has parent.(it didnt)");
			return false;
		}
		case hasnotgotparent:
		{
			if (callingObject.getObjectsCurrentState().positionedRelativeToo==null){
				return true;
			}			
			return false;
		}
		case hasproperty:
		{

			return testForHasProperty(callingObject, currentProperty, CurrentParams);
			/*
			if (!currentProperty.contains(",")) {
				// defaults to looking at last item clicked on or used
				if (callingObject.hasProperty(currentProperty)) {
					Log.info("has property " + currentProperty	+ " so we continue running commands");
					return true;
				} else {
					Log.info(callingObject.getObjectsCurrentState().ObjectsName+" does not have property " + currentProperty+ " so we stop running commands");
					return false;
				}
			} else {
				String object = CurrentParams[0].trim();
				String property = CurrentParams[1].trim();

				// we get the object specified by name
				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(object,callingObject,true);

				if (curobject==null){
					return false;
				}

				// and test that object
				if (curobject.hasProperty(property)) {
					Log.info(curobject.getName()+ " has property " + property	+ " so we continue running commands");
					return true;
				} else {
					Log.info(curobject.getName()+ " does not have property " + property
							+ " so we stop running commands");

					return false;
				}

			}*/
		}
		case hasnotgotproperty:
			return !testForHasProperty(callingObject, currentProperty, CurrentParams);
			
			/*
			if (!currentProperty.contains(",")) {
				// defaults to looking at last item clicked on or used
				if (!callingObject.hasProperty(currentProperty)) {
					Log.info(callingObject.getName()+ " has not got property " + currentProperty
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info(callingObject.getName()+ "does have property " + currentProperty
							+ " so we stop running commands");

					return false;
				}
			} else {
				String object   = CurrentParams[0].trim();
				String property = CurrentParams[1].trim();

				// Log.info("getting object:" + object);
				// we get the object specified by name

				//SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(object,callingObject)[0];

				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(object,callingObject,true);

				if (curobject != null) {
					Log.info("got object");
				} else {
					Log.info("object is null");
				}
				// and test that object
				if (!curobject.hasProperty(property)) {
					Log.info(curobject.getName()+ "has not got property " + property
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info(curobject.getName()+ "does have property " + property
							+ " so we stop running commands");

					return false;
				}
			}

			//break;	 (unreachable anyway)
*/

		case 	isonscene:
		{
			if (!currentProperty.contains(",")) {

				SceneWidget scenename = SceneWidget.getSceneByName(currentProperty);

				if (callingObject.getParentScene() == scenename ){
					return true;
				} else {
					return false;
				}	


			} else {
				String objectname = CurrentParams[0].trim();
				String scenename  = CurrentParams[1].trim();

				//SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(objectname,callingObject)[0];

				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(objectname,callingObject,true);

				if (curobject==null){
					return false;
				}

				SceneWidget scene = SceneWidget.getSceneByName(scenename);

				if (curobject.getParentScene() == scene ){
					return true;
				} else {
					return false;
				}					

			}

			//break;	 (unreachable anyway)

		}
		case iscalled:
		{
			return nameCheck(callingObject, currentProperty, CurrentParams);
		}
		case isnotcalled:
		{
			return !nameCheck(callingObject, currentProperty, CurrentParams);
		}
		case 	iscolliding:

			Log.info("checking for collision in:");
			Log.info(SceneObjectDatabase.currentScene.SceneFileName);

			if (SceneObjectDatabase.currentScene.TestForCollision(CurrentScenesVariables.lastclicked_x, CurrentScenesVariables.lastclicked_y)) {

				return true;
			} else {

				return false;
			}

			//break;	 (unreachable anyway)



		case 	isnotcolliding:
			Log.info("checking for collision:");

			if (SceneObjectDatabase.currentScene.TestForCollision(CurrentScenesVariables.lastclicked_x, CurrentScenesVariables.lastclicked_y)) {

				return false;
			} else {

				return true;
			}

			//break;	 (unreachable anyway)
		case  	ispointwithin: //is a co-ordinate within
			return isPointWithin(params);
			
		case ispointnotwithin:
			return !isPointWithin(params);
			
		case 	iswithin: //is a OBJECT within

			return isWithin(callingObject, currentProperty, CurrentParams);

			//break;
		case 	isnotwithin:


			return !isWithin(callingObject, currentProperty, CurrentParams);

			/*
			//if no comma, assume we are checking the calling object
			if (!currentProperty.contains(",")) {

				return !callingObject.isWithinRegion(currentProperty.trim());

			} else {

				String objectname = CurrentParams[0].trim();
				String regionname = CurrentParams[1].trim();

				//	SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(objectname,callingObject)[0];

				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(objectname,callingObject,true);

				if (curobject==null){
					Log.info("no object on scene called:"+objectname);
					return true; 

				}

				return !curobject.isWithinRegion(regionname);
			}*/
		case iscorporeal:
		{
			return iscorporeal(callingObject, params);
		}
		case isincorporeal:
		{
			return !iscorporeal(callingObject, params);
		}
		case 	currentmovement:
		{
			return CurrentObjectMovementIs(callingObject, CurrentParams);
		}
		case 	currentmovementnot:
		{
			return !CurrentObjectMovementIs(callingObject, CurrentParams);
		}
		case 	ismoving:
			if (!currentProperty.contains(",")) {

				boolean check = Boolean.parseBoolean(currentProperty);

				// defaults to looking at last item clicked on or used
				if (callingObject.isMoving() == check) {
					Log.info("is moving " + check
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info("is not moving  " + check
							+ " so we stop running commands");

					return false;
				}
			} else {
				String object = CurrentParams[0].trim();
				String property = CurrentParams[1].trim();

				// Log.info("getting object:" + object);
				// we get the object specified by name

				//SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(object,callingObject)[0];

				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(object,callingObject,true);

				if (curobject != null) {
					Log.info("got object");
				} else {
					Log.info("object is null");
				}
				// and test that object
				boolean check = Boolean.parseBoolean(property);

				// defaults to looking at last item clicked on or used
				if (curobject.isMoving() == check) {
					Log.info("is moving " + check
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info("is not moving  " + check
							+ " so we stop running commands");

					return false;
				}
			}
			//break; (unreachable)


		case isvisible:
		{

			if (!currentProperty.contains(",")) {

				boolean check = Boolean.parseBoolean(currentProperty);


				// defaults to looking at last item clicked on or used
				if (callingObject.isVisible() == check) {
					Log.info("is visible " + check
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info("is not visible  " + check
							+ " so we stop running commands");

					return false;
				}
			} else {
				String object = CurrentParams[0].trim();
				String property = CurrentParams[1].trim();

				boolean check = Boolean.parseBoolean(property);

				//SceneObject curobject = SceneWidget
				//		.getSceneObjectByName(object,callingObject)[0];

				SceneObject curobject = SceneObjectDatabase
						.getSingleSceneObjectNEW(object,callingObject,true);

				if (curobject==null){
					return false;
				}

				if (curobject.isVisible() == check) {
					Log.info("is visible " + check
							+ " so we continue running commands");
					return true;
					// return true;
				} else {
					Log.info("is not visible  " + check
							+ " so we stop running commands");

					return false;
				}
			}

		}

		case 	checkparagraph:
		{
			String textname = CurrentParams[0].trim();
			String paragraphname = CurrentParams[1].trim();
			String pp = CurrentParams[2].trim();
			int paragraphposition = Integer.parseInt(pp);

			Log.info("checking " + textname + " is on " + paragraphname
					+ " at " + paragraphposition);

			// get text box
			//SceneDialogObject dialogObject = SceneWidget
			//		.getTextObjectByName(textname)[0];

			//used to get via text and cast
			//Set<IsSceneLabelObject> dialogObjects = SceneObjectDatabase.getTextObjectNEW(textname,callingObject,true);
			//
			Set<IsSceneDialogueObject> dialogObjects = SceneObjectDatabase.getDialogueObjectNEW(textname,callingObject,true);

			//
			//conditional checks only support one object to check
			if (dialogObjects.size()>1){
				Log.warning("Warning: more then one object found for "+textname+". Condition checks for paragraph need to refer to a specific object NOT a set. Arbitarily choosing one to use now...");
			}
			if (dialogObjects.size()==0){
				Log.severe("Warning: no object found for "+textname+". Condition checks for paragraph need to refer to a specific object.");
			}
			//we just use the first object he iterator returns (we cant use .get(0) as Sets have no order and thus no numbered positions!)
			//SceneDialogObject dialogObject = (SceneDialogObject) dialogObjects.iterator().next();

			IsSceneDialogueObject dialogObject = dialogObjects.iterator().next();

			// check paragraph name
			String pn = dialogObject.getParagraphName();

			if (pn.equalsIgnoreCase(paragraphname)) {

				if (paragraphposition == dialogObject.getParagraphNumber()) {
					return true;
				} else {
					return false;
				}

			} else {
				return false;
			}


			//break;

		}
		case 	playerhas:

			// check player has item
			//	if (JAM.defaultInventory.inventoryContainsItem(currentProperty)) {
			if (InventoryPanelCore.playerHasItem(currentProperty)) {


				Log.info("player has item " + currentProperty
						+ "(passed check)");

				return true;

			} else {
				return false;
			}

			//	break;


		case 	playerdoesnothave:

			// check player has not got a item
			//if (JAM.defaultInventory.inventoryContainsItem(currentProperty)) {
			if (InventoryPanelCore.playerHasItem(currentProperty)) {

				return false;
			} else {
				Log.info("player dosnt have item " + currentProperty
						+ "(passed check)");
				return true;
			}
			//break;

		case   playerholds:
		{

			return playerholdstester(currentProperty);


		}
		case   playerdoesnothold:
		{
			return !playerholdstester(currentProperty); //just invert result

			/*
			if (InventoryPanel.currentlyHeldItem==null){
				Log.info("currently held item is null. ie. nothing is held");

				return true;
			}

			if (InventoryPanel.currentlyHeldItem.getName().equalsIgnoreCase(currentProperty)) {


				Log.info("player is holding " + currentProperty
						+ "(failed check)");

				return false;

			} else {


				Log.info("player is not holding " + currentProperty
						+ "(passed check)");

				return true;
			}	

			 */
		}


		case 	ison:

			return isontester(currentProperty);

			//break;

		case 	isnoton:

			return !isontester(currentProperty);


		case 	closertothan:
		{
			Log.info("______________________closertothan condition:");
			// split property
			String objectname = CurrentParams[0].trim()
					.toLowerCase();
			String distance = CurrentParams[1].trim()
					.toLowerCase();
			int limit = Integer.parseInt(distance);

			////SceneObject[] objects = SceneWidget
			//	.getSceneObjectByName(object,callingObject);

			SceneObject object = SceneObjectDatabase
					.getSingleSceneObjectNEW(objectname,callingObject,true);

			// only test the first (in future all?)

			if (object != null) {

				int tx = object.getX();
				int ty = object.getY();

				int cx = callingObject.getX();
				int cy = callingObject.getY();

				double dis = Math.hypot(Math.abs(tx - cx),
						Math.abs(ty - cy));

				Log.info("______________________Distance to object is:"
						+ dis);
				Log.info("______________________testing if closer then:"
						+ limit);

				// compare distance here:
				if (dis < limit) {
					return true;
				} else {
					Log.info("______________________failed check");

					return false;
				}

			}



			break;
		}

		case 	furtherfromthan:		
		{
			Log.info("______________________furtherfromthan condition:");
			// split property
			String objectname = CurrentParams[0].trim()
					.toLowerCase();
			String distance = CurrentParams[1].trim()
					.toLowerCase();
			int limit = Integer.parseInt(distance);

			//SceneObject[] objects = SceneWidget
			//		.getSceneObjectByName(object,callingObject);

			SceneObject object = SceneObjectDatabase
					.getSingleSceneObjectNEW(objectname,callingObject,true);

			// only test the first (in future all?)

			if (object != null) {

				int tx = object.getX();
				int ty = object.getY();

				int cx = callingObject.getX();
				int cy = callingObject.getY();

				double dis = Math.hypot(Math.abs(tx - cx),
						Math.abs(ty - cy));

				Log.info("______________________Distance to object is:"
						+ dis);
				Log.info("______________________testing if further then:"
						+ limit);

				// compare distance here:
				if (dis > limit) {
					return true;
				} else {
					Log.info("______________________failed check");

					return false;
				}

			}

		}
		break;

		case checkobjectvalue:
			// get object
			return checkobjectvalue(callingObject, CurrentParams);
			
		case checkobjectnotvalue:
			// get object
			return !checkobjectvalue(callingObject, CurrentParams);

		case checkvalue:

			return checkvalue(callingObject, CurrentParams);


		case checknotvalue:

			return !checkvalue(callingObject, CurrentParams);

		case ondebugmode:

			return JAMcore.DebugMode;
			
		case notondebugmode:

			return !JAMcore.DebugMode;

		case isscheduled:			

			return issheduled(callingObject, CurrentParams);

		case isnotscheduled:

			return !issheduled(callingObject, CurrentParams);	
		}
		Log.info("condition type '"+conditionalline.specifiedCondition.toString()+"' not recognised, so we continue assuming the check is passed");

		//default to true
		return true;
	}

	
	/**
	 * returns false only if collision type is none or the object is invisible, else returns true
	 * 
	 * @param callingObject
	 * @param params
	 * @return
	 */
	private static boolean iscorporeal(SceneObject callingObject,CommandParameterSet params) {
		
		CollisionType type;
		SceneObject so;
			
		if (params!=null && params.getTotal()>0){
			so = params.get(0).getAsObject(callingObject);	
		} else {
			so= callingObject;
		}

		type=so.getObjectsCurrentState().boundaryType.getCollisionType();		
		
		if (type==CollisionType.none || !so.isVisible() ){
			return false;
		}
		
		return true;
	}

	private static boolean isPointWithin(CommandParameterSet params) {
		
		if (params.getTotal()<4){
			Log.info("Conditional isPointWithin (or isPointNotWithin) requires 4 parameters; x,y,z,object<region>");
			return false;
		}		
		int x = params.get(0).getAsInt();
		int y = params.get(1).getAsInt();
		int z = params.get(2).getAsInt();
		String region = params.get(3).getAsString();
				
		return SceneObject.isWithinRegion(region, x, y, z, null);
	}
	

	/**
	 * 
	 * @param callingObject
	 * @param currentProperty - whole property ystring
	 * @param CurrentParams - string split by comma
	 * @return
	 */
	private static boolean isWithin(SceneObject callingObject, String currentProperty, String[] CurrentParams) {
		
		//if no comma, assume we are checking the calling object
		if (!currentProperty.contains(",")) {

			return callingObject.isWithinRegion(currentProperty.trim());

		} else {

			String objectname = CurrentParams[0].trim();
			String regionname = CurrentParams[1].trim();


			Set<? extends SceneObject> curobject = SceneObjectDatabase.getSceneObjectNEW(objectname,callingObject,true);
			

			if (curobject==null || curobject.isEmpty()){
				Log.info("no object on scene called:"+objectname);
				return false; 

			}
			Log.info("testing "+curobject.size()+" objects");
			for (SceneObject sceneObject : curobject) {
				
				if (sceneObject.isWithinRegion(regionname)){
					return true;
				}
				
			}

			return false;
		}
	}

	public static boolean testForHasProperty(SceneObject callingObject, String currentProperty,
			String[] CurrentParams) {
		
		SceneObject objectToTest = callingObject;
		String      propertyName = currentProperty.trim();

		if (currentProperty.contains(",")) {

			String objectName = CurrentParams[0].trim();
			propertyName      = CurrentParams[1].trim();
			// we get the object specified by name
			objectToTest = SceneObjectDatabase.getSingleSceneObjectNEW(objectName,callingObject,true);
		}

		if (objectToTest==null){
			return false;
		}

		if (propertyName.contains(":")){
			String[] predAndValue = propertyName.split(":");

			Log.info("Predicate:"+predAndValue[0]+" value:"+predAndValue[1]);
			
			if (objectToTest.hasProperty(predAndValue[0],predAndValue[1])) {
				return true;
			} else {
				return false;
			}
		} else {
			if (objectToTest.hasProperty(propertyName)) {
				return true;
			} else {
				return false;
			}
		}
	}

	public static boolean nameCheck(SceneObject callingObject, String currentProperty, String[] CurrentParams) {
		if (!currentProperty.contains(",")) {
			Log.info("has name " + callingObject.getName()	+ " so we continue running commands");

			// defaults to looking at last item clicked on or used
			if (callingObject.getName().equalsIgnoreCase(currentProperty)) {
				Log.info("has name " + currentProperty	+ " so we continue running commands");
				return true;					
			} else {
				Log.info(callingObject.getObjectsCurrentState().ObjectsName+" does not have name " + currentProperty + " so we stop running commands");
				return false;
			}
		} else {
			String object   = CurrentParams[0].trim();
			String property = CurrentParams[1].trim();

			SceneObject curobject = SceneObjectDatabase.getSingleSceneObjectNEW(object,callingObject,true);

			if (curobject==null){
				return false;
			}
			if (callingObject.getName().equalsIgnoreCase(property)) {
				Log.info("has name " + currentProperty	+ " so we continue running commands");
				return true;					
			} else {
				Log.info(callingObject.getObjectsCurrentState().ObjectsName+" does not have name " + currentProperty
						+ " so we stop running commands");
				return false;
			}

		}
	}
	protected static boolean CurrentObjectMovementIs(SceneObject callingObject, String[] CurrentParams) {
		Log.info("checking currentmovement");

		SceneObject objectToTest;
		String movementName = "";

		if (CurrentParams.length==2) {				
			String object   = CurrentParams[0].trim();
			movementName    = CurrentParams[1].trim();

			SceneObject curobject = SceneObjectDatabase
					.getSingleSceneObjectNEW(object,callingObject,true);

			if (curobject != null) {
				objectToTest = curobject;
			} else {
				Log.severe("object requested to test movement of is null");
				return false;
			}				

		} else {
			objectToTest = callingObject;
			movementName  = CurrentParams[0].trim();
		}

		//Check we are moving at all
		if (!callingObject.isMoving()) {
			Log.info(" no movement ");
			return false;
		}

		//Check if they asked if we were on physics?
		if (movementName.equalsIgnoreCase(MovementState.CONDITIONAL_PHYSICS_SIGNFIER)){

			//and are we?
			if (!callingObject.getObjectsCurrentState().moveState.isPresent()){
				return false; //no movement at all we return false
			}			
			if (callingObject.getObjectsCurrentState().moveState.get().currentmovementtype == MovementState.MovementStateType.PhysicsBased){
				return true;
			}

		}

		//else check the name
		if (objectToTest.getObjectsCurrentState().moveState.isPresent() && 
				objectToTest.getObjectsCurrentState().moveState.get().currentPathData.pathsName.equalsIgnoreCase(movementName)) {
			Log.info(" movement=" + movementName	+ " ");
			return true;
		} else {
			Log.info(" movement not= " + movementName+ " ");
			return false;
		}
	}
	private static boolean playerholdstester(String currentProperty) {
		if (InventoryPanelCore.currentlyHeldItem==null){
			Log.info("currently held item is null. ie. nothing is held");				
			return false;
		}

		if (currentProperty==null || currentProperty.isEmpty()){
			Log.info("no specific item specified, and we are holding:"+InventoryPanelCore.currentlyHeldItem.getName()+" so thats good enough");				
			return true;
		}

		if (InventoryPanelCore.currentlyHeldItem.getName().equalsIgnoreCase(currentProperty)) {

			Log.info("player is holding " + currentProperty
					+ "");

			return true;

		} else {
			return false;
		}
	}

	/**
	 * Tests if a named actionset is scheduled to fire
	 * @param callingObject
	 * @param currentParams
	 * @return
	 */
	private static boolean issheduled(SceneObject callingObject,
			String[] currentParams) {

		String objectname      = "";
		String actionsetname   = "";

		Log.info("checking for sheduled command:"+currentParams.length+" parameters");
		Log.info("which are :"+currentParams);

		if (currentParams.length==2){
			objectname      = currentParams[0].trim().toLowerCase();
			actionsetname   = currentParams[1].trim().toLowerCase();
		} 
		if (currentParams.length==1){
			objectname      = callingObject.getObjectsCurrentState().ObjectsName;
			actionsetname   = currentParams[0].trim().toLowerCase();	
		}

		Log.info("checking for sheduled command:"+objectname+"_"+actionsetname);

		return JAMTimerController.isNamedActionSetShreduled(objectname,actionsetname);

	}
	private static boolean isontester(String currentProperty) {
		// check player is on location
		if (JAMcore.usersCurrentLocation.equalsIgnoreCase(currentProperty)) {

			Log.info("player is on location " + currentProperty
					+ "(passed check)");
			return true;
		} else {
			return false;
		}
	}

	//TODO: move to InstructionProcessors option pagestyle interface and refer to that here
	//we cant directly use html functions like Element anymore
	/**
	 * @param currentProperty
	 * @param CurrentParams
	 * @return
	 */
	private static boolean testIfDivHasClass(String currentProperty,String[] CurrentParams) {

		String divWithThisIDToCheck = CurrentParams[0].trim();
		String classToLookFor = CurrentParams[1].trim();

		//new method, we query the PageStyleCommandImplemention if its present rather then doing it ourselves
		if (OptionalImplementations.PageStyleCommandImplemention.isPresent()){
			return OptionalImplementations.PageStyleCommandImplemention.get().testIfDivHasClass(divWithThisIDToCheck,classToLookFor);
		}

		//		Element divToCheck = DOM.getElementById(divWithThisIDToCheck);
		//		
		//		if (divToCheck==null){
		//						
		//			Log.info("No div found called "+currentProperty+" ");								
		//			return false;
		//			
		//		}
		//		
		//		//get its style class's 
		//		String classes = " "+divToCheck.getClassName()+" "; //note we add spaces to make searching easier (else we get parts of words)
		//		Boolean hasClass = classes.contains(" "+classToLookFor+" ");
		//		
		return false; //false if no PageStyleCommandImplemention implementation
	}
	/**
	 * @param callingObject
	 * @param currentProperty
	 * @param CurrentParams
	 * @return
	 */
	private static boolean testForClassOnObject(SceneObject callingObject,
			String currentProperty, String[] CurrentParams) {
		if (!currentProperty.contains(",")) {

			// defaults to looking at last item clicked on or used
			//ensure it has div functions first (ie, supports css)
			if (!callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Div)){
				return false; //not a div
			}

			//we can safely cast to isSceneDivObject if the type is compatible with Div
			if (((IsSceneDivObject)callingObject).hasClass(currentProperty)) {
				Log.info("has class " + currentProperty
						+ " so we continue running commands");
				return true;
				// return true;
			} else {
				Log.info(callingObject.getObjectsCurrentState().ObjectsName+" does not have class " + currentProperty
						+ " so we stop running commands");

				return false;
			}
		} else {
			String object = CurrentParams[0].trim();
			String property = CurrentParams[1].trim();

			// we get the object specified by name
			//SceneObject curobject = SceneWidget
			//		.getSceneObjectByName(object,callingObject)[0];

			SceneObject curobject = SceneObjectDatabase
					.getSingleSceneObjectNEW(object,callingObject,true);

			if (curobject==null){
				return false;
			}
			//ensure it has div functions first (ie, supports css)
			if (!callingObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Div)){
				return false; //not a div
			}
			//we can safely cast to isSceneDivObject if the type is compatible with Div

			// and test that object
			if (((IsSceneDivObject)curobject).hasClass(property)) {
				Log.info("has class " + property
						+ " so we continue running commands");
				return true;
				// return true;
			} else {
				Log.info("does not have class " + property
						+ " so we stop running commands");

				return false;
			}

		}
	}


	private static boolean checkobjectvalue(SceneObject callingObject,
			String[] CurrentParams) {

		SceneObject objectToTest = null;
		String varName="";
		String equals="";

		if (CurrentParams.length==3){
			String objectName = CurrentParams[0].trim()
					.toLowerCase();

			varName = CurrentParams[1].trim()
					.toLowerCase();

			equals = CurrentParams[2].trim()
					.toLowerCase();

			objectToTest = SceneObjectDatabase
					.getSingleSceneObjectNEW(objectName,callingObject,true);

		} else	if (CurrentParams.length==2){

			objectToTest = callingObject;

			varName = CurrentParams[0].trim()
					.toLowerCase();

			equals = CurrentParams[1].trim()
					.toLowerCase();
		}

		//SceneObject object = SceneWidget
		//		.getSceneObjectByName(objectName,callingObject)[0];


		if (objectToTest==null){
			return false;
		}

		if ( objectToTest.checkVariable(varName, equals)){
			Log.info("yup, it matchs");

			return true;
		} else {
			return false;
		}
	}

	/**
	 * compares two numerical values, which can come from any variables or just be static numbers
	 * 
	 * @param callingObject
	 * @param CurrentParams
	 * @return
	 */
	private static boolean checkvalue(SceneObject callingObject,
			String[] CurrentParams) {

		// split property
		String nametocheck      = CurrentParams[0].trim().toLowerCase();
		String valueToCompareTo = CurrentParams[1].trim().toLowerCase();

		// detect if its a greater then comparison
		
		//this means either < followed by no > anywhere
		// ,<50
		// OR
		// two < in a row
		// ,<<ObjectSizeX>
		
		if ((valueToCompareTo.startsWith("<") &&  !valueToCompareTo.contains(">"))
				|| valueToCompareTo.startsWith("<<") ) {
			
			

			//remove the operator
			String valueToCompareTo2 = valueToCompareTo.substring(1, valueToCompareTo.length()).trim();

			String evalue = InstructionProcessor.getValue(nametocheck,callingObject);
			String tvalue = InstructionProcessor.getValue(valueToCompareTo2,callingObject);

			if (evalue==null){
				Log.info("Variable not found when looking for:"+nametocheck);	
				return false;				
			}
			if (tvalue==null){
				Log.info("Variable not found when looking for:"+valueToCompareTo2);	
				return false;				
			}
			if (evalue.equals(ERROR_CANT_FIND_OBJECT_SIGNIFIER) || tvalue.equals(ERROR_CANT_FIND_OBJECT_SIGNIFIER)){
				Log.info("object needed for the comparison not found, so returning false");								
				return false;				
			}
			//Log.info("evalue=" + evalue);
			//Log.info("tvalue=" + tvalue);


			double existingValue = 0;

			if (!evalue.isEmpty()) {
				existingValue = Double.parseDouble(evalue);  //Integer.parseInt(evalue);
			}

			double intValueToCompare = 0;


			if (!tvalue.isEmpty()) {
				intValueToCompare = Double.parseDouble(tvalue);
			}

			//Log.info("testing variable " + existingValue + " is less then "
			//		+ intValueToCompare);

			if (existingValue < intValueToCompare) {
				//	Log.info("yup, its less then");

				return true;
			} else {
				//	Log.info("nope, it isn't less then");

				return false;
			}

		}

		// detect if its a less then comparison
		if (valueToCompareTo.startsWith(">")) {

			//	int intValueToCompare = Integer.parseInt(valueToCompareTo
			//			.substring(1, valueToCompareTo.length()));
			String valueToCompareTo2 = valueToCompareTo.substring(1, valueToCompareTo.length()).trim();

			String evalue = (InstructionProcessor.getValue(nametocheck,callingObject));
			String tvalue = (InstructionProcessor.getValue(valueToCompareTo2,callingObject));
			if (evalue==null){
				Log.info("Variable not found when looking for:"+nametocheck);	
				return false;				
			}
			if (tvalue==null){
				Log.info("Variable not found when looking for:"+valueToCompareTo2);	
				return false;				
			}
			if (evalue.equals(ERROR_CANT_FIND_OBJECT_SIGNIFIER) || tvalue.equals(ERROR_CANT_FIND_OBJECT_SIGNIFIER)){
				Log.info("object needed for comparison not found, so returning false");								
				return false;				
			}

			Log.info("evalue=" + evalue);
			Log.info("tvalue=" + tvalue);

			double existingValue = 0;

			if (!evalue.isEmpty()) {
				existingValue = Double.parseDouble(evalue);
			}

			double intValueToCompare = 0;

			if (!tvalue.isEmpty()) {
				intValueToCompare = Double.parseDouble(tvalue);
			}
			Log.info("variable " + existingValue
					+ " is greater then " + intValueToCompare);

			if (existingValue > intValueToCompare) {
				Log.info("yup, its greater then");

				return true;
			} else {
				Log.info("nope, it isn't greater then");

				return false;
			}

		}

		// check value is equal
		Log.info("variable " + nametocheck + " does it have value "		+ valueToCompareTo);
		//used to be equals, now made case insensitive
		String evalue = (InstructionProcessor.getValue(nametocheck,callingObject));  //are these get values nessecery? wont the values be pre-processed by now already?
		String tvalue = (InstructionProcessor.getValue(valueToCompareTo,callingObject));

		//if (evalue.equalsIgnoreCase(valueToCompareTo)) {
		if (evalue.equalsIgnoreCase(tvalue)) {

			Log.info("variable " + nametocheck + " has value "	+ tvalue);
			return true;

		} else {

			Log.info("no, " + nametocheck + " has value "	+ InstructionProcessor.getValue(nametocheck,callingObject));
			return false;
		}



		//break;
	}

	@Override 
	public String toString(){

		if (conditionList!=null){
			return conditionList.toString();
		}

		String proccessedParameterString = "";
		if (params!=null){
			proccessedParameterString = params.getProccessedParameterString();
		}
		return specifiedCondition.toString()+"="+proccessedParameterString;

	}
}
