package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.darkflame.client.semantic.SSSProperty;
import com.lostagain.Jam.VariableSet;
import com.lostagain.Jam.Movements.MovementState;

/*** 
 * Contains all the information for a single scene div object state
 * Other states inherit from this because all scene objects in GWT are also Div objects.
 *
 * TODO:
 * None-GWT implementations should flag they arnt true div objects and thus 
 * 	super.setObjectsPrimaryType(SceneObjectType.Div);
 * should not fire.
 * There will still be redundant fields not used, but these can be safely ignored.
 *
 *  //TODO: size probably should be moved to SceneObjectState
 *  //TODO: we might need to introduce a new class of object "IsSceneObjectContainer". In GWT this is exactly the same as DIV
 *  //In other things it acts as a div (with its ability to dump another object in it, and set a size) but lacks the css support
 * 
 ***/

public class SceneDivObjectState extends SceneObjectState {

	public static Logger Log = Logger.getLogger("JAMCore.SceneDivObjectState");
	

	/** size of the div. can also be set by the objects css, but this will override that 
	 * Note; This supports full css shenanigans **/
	public String sizeX="";
	/** size of the div. can also be set by the objects css, but this will override that **/
	public String sizeY="";

	//eventually boxcss handling should be here too



boolean THIS_IS_A_HTML_GAME=true;//temp override, TODO: ASAP remove the checks for this in this class only, then delete this line


	/**
	 * creates a SceneDivObjectState with the default state
	 */
	public SceneDivObjectState(){
		super();

		//NOTE: while all states inherit this, if we arnt running in a html enviroment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype

		//subtypes should set this so the most precise type is the primary type

		if (THIS_IS_A_HTML_GAME){
		//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
	}


	}




	//TODO: change the paremters to pre-split
	/**
	 * generates a default DivObject from the parameters supplied
	 *
	 * @param parametersFromFile
	 * @param InterpretAsIniData
	 */
	public SceneDivObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		if (THIS_IS_A_HTML_GAME){
		//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
		
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

	}




	/**
	 * creates a SceneDivObjectState state from supplied SceneObjectState data
	 * Using default values for Div specific stuff
	 */
	public SceneDivObjectState(SceneObjectState state){		
		super(state);

		//NOTE: while all states inherit this, if we arnt running in a html environment
		//we shouldn't add div to the type list
		//This state shouldn't be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype
		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		//currently size for some reason (really it doesn't have any yet of its own?)
		
		
		Log.info("Div object sizeX is:"+this.sizeX);
	//	sizeX="";//NEW: why were we clearing the size setting? 
	//	sizeY=""; 
	}


	/**
	 * creates a new SceneDivObjectState state from supplied SceneDivObjectState data, copying
	 * all its data
	 */
	public SceneDivObjectState(SceneDivObjectState state){		
		super(state); //copy generic stuff

		//NOTE: while all states inherit this, if we arnt running in a html enviroment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype


		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		sizeX=state.sizeX;
		sizeY=state.sizeY;

	}



	/**
	 * creates a SceneDivObjectState state from supplied SceneObjectState data
	 * and the DivSpecific values following it
	 */
	public SceneDivObjectState(SceneObjectState state,String SizeX,String SizeY){		
		super(state);


		//NOTE: while all states inherit this, if we arnt running in a html enviroment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype


		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		//currently size for some reason (really it doesn't have any yet of its own?)
		sizeX=SizeX;
		sizeY=SizeY;
		//setObjectsPrimaryType(SceneObjectType.Input);//set by super(state), we dont set here in case we are a subtype
	}


	/**
	 * creates a SceneDivObjectState with the default state except for the size
	 */
	public SceneDivObjectState(String sizex,String sizey){
		super();

		//NOTE: while all states inherit this, if we arnt running in a html enviroment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype


		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		sizeX=sizex;
		sizeY=sizey;
	}

	/**
	 * creates a SceneDivObjectState from the following params;
	 * 
	 * @param objectsName
	 * @param sceneName
	 * @param title
	 * @param cssname
	 * @param backstring
	 * @param x
	 * @param y
	 * @param sizex
	 * @param sizey
	 * @param restrictPositionToScreen
	 * @param usesAttachmentFile
	 * @param usesMovementFile
	 * @param pinPointX
	 * @param pinPointY
	 * @param relativeToThis
	 * @param relativeToThisPoint
	 * @param relX
	 * @param relY
	 * @param zIndex
	 * @param variableZindex
	 * @param zIndexLower
	 * @param zIndexUpper
	 * @param zIndexStep
	 * @param linkedZindex
	 * @param linkedZindexDifference
	 * @param ms
	 * @param objectsProperties
	 * @param objectsRuntimeVariables
	 * @param touching
	 * @param currentlyVisible
	 */
	public SceneDivObjectState(
			String objectsName, 			
			String sceneName,
			String title, 
			String cssname,
			String backstring,
			int x, int y, int z,
			String sizex,String sizey,
			boolean restrictPositionToScreen,
			Boolean usesAttachmentFile,
			Boolean usesMovementFile,
			int pinPointX, 
			int pinPointY,
			int pinPointZ,
			SceneObject relativeToThis, 
			String relativeToThisOnceLoaded,
			String relativeToThisPoint,
			linktype relativeToLinkType,
			int relX, int relY,int relZ,	
			int zIndex,
			boolean variableZindex,
			int zIndexLower,
			int zIndexUpper,
			int zIndexStep,
			boolean linkedZindex,
			int linkedZindexDifference,
			Optional<MovementState> ms,
			HashSet<SSSProperty> objectsProperties, 
			VariableSet objectsRuntimeVariables,
			SceneObjectSet touching,
			boolean currentlyVisible,
			double currentOpacity,
			boolean PropagateVisibility, 
			boolean ignoreSceneActions,
			boolean ignorePointerEvents,
			boolean forceReceiveActions,
			CollisionModeSpecs boundaryType,
			pathfindingMode pathfindingType) {

		//universal data
		super(objectsName,"[none]",sceneName,title, cssname,backstring,
				x, y, z,
				restrictPositionToScreen,
				usesAttachmentFile,
				usesMovementFile,
				pinPointX,	pinPointY,  pinPointZ,
				currentlyVisible,
				currentOpacity,
				PropagateVisibility, 
				ignoreSceneActions,
				ignorePointerEvents,
				forceReceiveActions,
				pathfindingType,
				boundaryType, 
				SceneObjectType.Div, //currently it only has 1 type, Div  
				zIndex, 
				variableZindex,
				zIndexLower,
				zIndexUpper,
				zIndexStep,
				linkedZindex,
				linkedZindexDifference,
				relativeToThis,
				relativeToThisOnceLoaded,
				relativeToThisPoint,
				relativeToLinkType,
				relX,  relY, relZ,
				ms,
				new PropertySet(objectsProperties),
				new VariableSet(objectsRuntimeVariables),
				new SceneObjectSet(touching)
				);


		//NOTE: while all states inherit this, if we arnt running in a html environment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype


		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		sizeX=sizex;
		sizeY=sizey;


	}




	public SceneDivObjectState(String serialised) {
		//	super(serialised);

		//NOTE: while all states inherit this, if we arnt running in a html enviroment
		//we shouldn't add div to the type list
		//This state shouldnt be used directly in that situation either, only subtypes
		//This thus only exists for those types as a "parse through" to the SceneObjectState supertype


		if (THIS_IS_A_HTML_GAME){
			//EXPREMENT: we are seeing if LibGDXs can handle all objects being Div (which really means styleable)
			
			super.setObjectsPrimaryType(SceneObjectType.Div); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		}

		deserialise(serialised);


	}
	/*
	public void deserialise(String serialised) {
		String[] data = serialised.split(deliminator, -1);

		// assign data
		Log.info("__________________________________deserialising :"+ data.length);

		// list its easier to manage
		List<String> incomingdatalist = (Arrays.asList(data));
		Iterator<String> incomingdata = incomingdatalist.iterator();

		//load parameters from incomingdata
		loadParameters(incomingdata);

	};
	 */

	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions	 * 
	 */	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){

		//load the parameters of the parent type first
		super.loadParameters(incomingdata);

		//load div specific data		

		sizeX =  incomingdata.next(); //should we save/load the size at all?
		sizeY =  incomingdata.next();

	}


	/** 
	 * Warning, currently non-functional.
	 * Need to look at the working Sprite object serialiser, and apply a similar technique here 
	@Override
	public String serialiseToString() {

		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList =  getAllFieldsAsArrayList();

		String serialised = "";

		//then we add the div specific stuff

		serialised = SceneObjectState.serialiseTheseFields(fieldList);

		Log.info("__________________________________serialised as:"
				+ serialised);

		return serialised;
	}**/



	/**
	 * Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier
	 * **/
	public SceneDivObjectState copy(){

		//get a copy of the generic data from the supertype
		SceneObjectState genericCopy = super.copy(); 
		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		SceneDivObjectState tempObjectData = new SceneDivObjectState(genericCopy,sizeX,sizeY);



		return tempObjectData;
	}

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean genericallyTheSame = super.sameStateAs(state);
		if (!genericallyTheSame){
			return genericallyTheSame;
		}
		//now check div specific fields
		SceneDivObjectState asDivState = (SceneDivObjectState) state;
		if (!asDivState.sizeX.equals(sizeX)){
			return false;
		}
		if (!asDivState.sizeY.equals(sizeY)){
			return false;
		}

		Log.info("All div object data is the same between these two states");
		
		return true;		
	}


	
	
	@Override
	protected ArrayList<Object> getAllFieldsAsArrayList() {


		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();

		//then in most other objects, we would add the fields specific to this object type
		//however, a Div object is so basic, it has none whatsoever! seriously, none at all!
		//Log.info("getting div specific fields. But theres just two; size");	

		fieldList.add(sizeX);
		fieldList.add(sizeY);


		//Log.info("got div fields");

		return fieldList;

	}




	//TODO: further optimisation possible if we just process one line here
	//and let the parent do the while loop and splitting	
	public void assignDivObjectTypeSpecificParameters(String[] itemslines) {


		// now use the pre-split lines to get parameter data
		int currentlinenum = 0;
		
		while (currentlinenum < itemslines.length) {

			String currentline = itemslines[currentlinenum].trim();
			currentlinenum++;
			if ((currentline.length() < 2) || (currentline.startsWith("//"))) {
				continue;
			}

			Log.info("Processing line:"+currentline);

			// split by =
			String param = currentline.split("=")[0].trim();
			String value = currentline.split("=")[1].trim();

			// assign data
			/*
			if (param.equalsIgnoreCase("Name")) {

				// ObjectsName = value;
				objectsCurrentState.ObjectsName = value;
				Log.info("Objects file name set to:"+ objectsCurrentState.ObjectsName);

				//Dialogue objects if they use a folder, its just their object name
				folderName = objectsCurrentState.ObjectsName;

			}
			 */

			/** size can be set in the css or in the script
			 * the script will override any css setting  **/
			if (param.equalsIgnoreCase("Size")) {

				String value1 = value.split(",")[0];
				String value2 = value.split(",")[1];

				value1 = ensureSizeIsRealCss(value1);
				value2 = ensureSizeIsRealCss(value2);

				sizeX = value1;
				sizeY = value2;

				Log.info("div Objects "+ObjectsName+"size set to:" + sizeX
						+ " x " + sizeY);

			}

			if (param.equalsIgnoreCase("RestrictToScreen")) {

				// Note, for div type it defaults *not* to restricting to the screen
				if (value.equalsIgnoreCase("true")) {
					restrictPositionToScreen = true;
				} else {
					restrictPositionToScreen = false;
				}

			}
			/*
			if (param.equalsIgnoreCase("ActionOveride")) {

				// Note, for dialogue it defaults too ignoring scene actions
				if (value.equalsIgnoreCase("false")) {
					ignoreSceneActions = false;
				} else {
					ignoreSceneActions = true;
				}
			}*/

		}


		Log.info("__assigned div ObjectTypeSpecificParameters to object "+ObjectsName+" ___");

	}

	/**
	 * should be overridden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP DIV SPECIFIC PARAMETERS");		
		assignDivObjectTypeSpecificParameters(objectsParamatersFromFile2);
		Log.info("sizeX "+sizeX);
		
	}




	/**
	 * ensures the supplied value is a real css size
	 * acceptable values are;
	 * ##em, ##ex, ##%, ##px, ##cm, ##mm, ##in, ##pt, ##pc,##ch,##rem
	 * ...
	 * auto
	 * inherit
	 * initial
	 * 
	 * if the value is just a number, a PX will be added to the end
	 * 
	 * @param value1
	 * @return
	 **/	
	public static String ensureSizeIsRealCss(String value) {

		if (value.equalsIgnoreCase("auto")){
			return "auto";
		}

		if (value.equalsIgnoreCase("inherit")){
			return "inherit";
		}

		if (value.equalsIgnoreCase("initial")){
			return "initial";
		}

		if (value.endsWith("px")){
			return value;
		} else if (value.endsWith("em")) {
			return value;
		} else if (value.endsWith("ex")) {
			return value;
		} else if (value.endsWith("%")) {
			return value;
		} else if (value.endsWith("px")) {
			return value;
		} else if (value.endsWith("mm")) {
			return value;
		} else if (value.endsWith("cm")){
			return value;
		} else if (value.endsWith("in")) {
			return value;
		} else if (value.endsWith("pt")) {
			return value;
		} else if (value.endsWith("pc")){
			return value;
		} else if (value.endsWith("ch")) {
			return value;
		} else if (value.endsWith("rem")){
			return value;
		} else {
			//Log.warning("can not resolve "+value+" as a css size spec, assuming its meant to be "+value+"px");
			return value+"px";
		}



	}



}
