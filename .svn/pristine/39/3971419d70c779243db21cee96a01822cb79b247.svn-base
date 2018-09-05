package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.SceneObjects.SceneObjectState.SceneObjectParamTypes;

/** Contains all the information for a single scene sprite object data state **/
public class SceneSpriteObjectState extends SceneDivObjectState {
	public static Logger Log = Logger.getLogger("JAMCore.SceneSpriteObjectState");
	
	//

	/**
	 * url of the sprite
	 */
	public String ObjectsURL;

	/**
	 * If this object has a parent, then you can set this objects url to change based on the parents url <br>
	 * eg;<br>
	 * DependantURLPrefix = "shadow_"<br>
	 * <br>
	 * This will swap add the parents ObjectURL to the end of the prefix then set this objects url to that string.<br>
	 * eg, if the parent is:  "meryll0.png"<br>
	 * this will be set to:  "shadow_meryll0.png" <br>
	 * <br>
	 */
	public String DependantURLPrefix = "";

	/** Note; This isn't automatically updated, if you need to know the current frame
	 *  for sure, use the SceneObjectIcon itself **/
	public int currentFrame = 0;

	//public String currentmovement = "";
	// Things the object is touching (optional)

	public int     currentNumberOfFrames = -1;
	/**
	 * ms between frames
	 */
	public int currentFrameGap=100;
	
	public String currentlyAnimationState= "";

	

	/** Create a fully default sprite from generic state object, with sprite specific stuff set to their defaults  */
	public SceneSpriteObjectState(SceneObjectState state){
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //any subtypes will need to set this themselves to override sprite
				
		//set defaults;		
		ObjectsURL              = null;
		currentlyAnimationState = "";

		Log.info(" storing movement state :");
		currentNumberOfFrames   = 1;

		currentFrame            = 0;
	}
	
	

	public SceneSpriteObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //any subtypes will need to set this themselves to override sprite
		
		//set defaults;		
		ObjectsURL              = null;
		currentlyAnimationState = "";

		Log.info(" storing movement state :");
		currentNumberOfFrames   = 1;

		currentFrame            = 0;
}



	/**
	 * This state will be a copy of the supplied one
	 * @param state
	 */
	public SceneSpriteObjectState(SceneSpriteObjectState state){	
		super(state); //copys the generic div stuff.
		//now the sprite specific stuff
		this.ObjectsURL              = state.ObjectsURL;
		this.DependantURLPrefix=state.DependantURLPrefix;
		this.currentFrameGap= state.currentFrameGap;
		this.currentlyAnimationState = state.currentlyAnimationState;
		this.currentNumberOfFrames = state.currentNumberOfFrames;
		this.currentFrame = state.currentFrame;
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		
	}
	/**
	 * creates a SceneSpriteObjectState state from supplied SceneDivObjectState data
	 * Using default values for sprite specific stuff.
	 */
	public SceneSpriteObjectState(SceneDivObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//set defaults;		
		ObjectsURL              = null;
		currentlyAnimationState = "";

		Log.info(" storing movement state :");
		currentNumberOfFrames = 1;

		currentFrame = 0;		
		
	}
	
	/**
	 * creates a SceneSpriteObjectState state with the default state
	 */
	public SceneSpriteObjectState(){		
		super(); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//set defaults;		
		ObjectsURL              = null;
		currentlyAnimationState = "";

		Log.info(" storing movement state :");
		currentNumberOfFrames = 1;

		currentFrame = 0;		
		
	}
	
	

	/** Create a new scenesprite object from the generic state and supplied specific parameters  */
	public SceneSpriteObjectState(SceneDivObjectState state, String ObjectsURL,String DependantURLPrefix,int currentFrameGap, String currentlyAnimationState,int currentNumberOfFrames, int currentFrame ){
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //any subtypes will need to set this themselves to override sprite
		
	
		//set defaults;		
		this.ObjectsURL              = ObjectsURL;
		this.DependantURLPrefix=DependantURLPrefix;
		this.currentFrameGap = currentFrameGap;
		this.currentlyAnimationState = currentlyAnimationState;

		Log.info(" storing movement state :");

		this.currentNumberOfFrames = currentNumberOfFrames;
		this.currentFrame = currentFrame;
	}
	
	
	//old method below (now done via other constructors)
	/*
	public SceneSpriteObjectState(String objectURL, String objectsFileName,String sceneName,
			String objectName, String title, String cssname,String backstring,
			int x, int y,
			Boolean restrictPositionToScreen,
			Boolean usesAttachmentPointFile,
			Boolean usesMovementPointFile,
			int pinPointX, int pinPointY, 
			int zIndex, 
			boolean variableZindex,
			int zIndexLower,
			int zIndexUpper,
			int zIndexStep,
			boolean linkedZindex,
			int linkedZindexDifference,
			SceneObjectVisual relativeToThis, String relativeToThisPoint, int relX, int relY,
			//	String newcurrentmovement, 
			movementState ms,

			HashSet<SSSProperty> objectsProperties,
			VariableSet ObjectRuntimeVariables,

			HashSet<String> touching,
			int currentNumberOfFrames, String newcurrentlyAnimationState,
			int newcurrentFrame, boolean isCurrentlyVisible) {


		//universal data
		super(objectName,objectsFileName,sceneName,title, cssname,backstring,
				x, y,  
				restrictPositionToScreen,
				usesAttachmentPointFile,
				usesMovementPointFile,
				pinPointX,
				pinPointY,  isCurrentlyVisible,
				SceneObjectType.Sprite,  //currently it has one type; Sprite if theres subtypes of sprite this shouldnt be hard coded
				zIndex, 
				variableZindex,
				zIndexLower,
				zIndexUpper,
				zIndexStep,
				linkedZindex,
				linkedZindexDifference,
				relativeToThis,relativeToThisPoint, relX,  relY,
				ms,
				new PropertySet(objectsProperties),
				new VariableSet(ObjectRuntimeVariables),
				touching);
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //any subtypes will beed to set this themselves to override sprite
		
		//unique sprite data

		ObjectsURL = objectURL;
		Log.info("ObjectsURL=" + ObjectsURL);

		currentlyAnimationState = newcurrentlyAnimationState;

		Log.info(" storing movement state :");



		//this.touching = touching;
		this.currentNumberOfFrames = currentNumberOfFrames;

		currentFrame = newcurrentFrame;


	}*/


	public SceneSpriteObjectState(String serialised) {
		super();
		//needs to be retracted so common data goes to the superconstructer
	//	super(serialized);
		super.setObjectsPrimaryType(SceneObjectType.Sprite); //any subtypes will beed to set this themselves to override sprite
		
		//setObjectsOnlyType(SceneObjectType.Sprite);  //DONT SET THE TYPE as this could also be extensions of sprite
		//(type is set by super() anyway

		if (ObjectsName == null) {
			Log.info("object name is null");
		}

		deserialise(serialised);

	}

/*
	public void deserialise(String serialised) {

		String[] data = serialised.split(deliminator, -1);

		// list its easier to manage
		List<String> incomingdatalist = (Arrays.asList(data));
		Iterator<String> incomingdata = incomingdatalist.iterator();

		//load global data first
		loadParameters(incomingdata);


	}
*/

	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions	 * 
	 */	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){
		
		//load the parameters of the parent type first
		super.loadParameters(incomingdata);
				
		//load sprite specific data				
		Log.info("loading sprite specific object data");
		
		// then the sprite specific data
		ObjectsURL = incomingdata.next();
		ObjectsFileName = incomingdata.next();


		currentNumberOfFrames = Integer.parseInt(incomingdata.next());
		currentFrame = Integer.parseInt(incomingdata.next()); // be sure to
		// update this
		// before save
		//currentlyVisible = Boolean.parseBoolean(incomingdata.next());
		currentFrameGap = Integer.parseInt(incomingdata.next());
		currentlyAnimationState = incomingdata.next();

		Log.info("got fields");
		
	}

	@Override
	protected ArrayList<Object> getAllFieldsAsArrayList() {


		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();

		//then we get the sprite specific fields
		//Log.info("getting sprite specific fields.");

		// sprite data
		//The order these are added HAS TO BE THE SAME AS IN deserialise(String serialized) 
		// IF THERES A PROBLEM loading a saved file please check the order below matchs the order there.
		fieldList.add(ObjectsURL);
		fieldList.add(ObjectsFileName);

		fieldList.add(currentNumberOfFrames);
		fieldList.add(currentFrame); // be sure to update this before save
	//	fieldList.add(currentlyVisible); //no need to set this as its set in the super.getAllFieldsAsArrayList
		fieldList.add(currentFrameGap);
		fieldList.add(currentlyAnimationState); // be sure to update this before save


	//	if ((touching != null) && (!touching.isEmpty())) {
	//		fieldList.add(touching);
	//	} else {
	//		fieldList.add(null);
	//	}

		Log.info("got fields");

		return fieldList;

	}

	/*
	@Override
	public String serialiseToString() { //probably can be moved to supertype

		//Log.info("________________________serialising:   " + this.ObjectsName);


		String serialised = "";
		if (clonedFrom!=null){
			Log.info("________________________testing clonedFrom field.." + clonedFrom.objectsCurrentState.ObjectsName);
		}
		ArrayList<Object> fieldList = getAllFieldsAsArrayList();



		serialised = SceneObjectState.serialiseTheseFields(fieldList);
		Log.info("__________________________________serialised as:"
				+ serialised);

		return serialised;
	}*/



	/**Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier**/
	
	@Override
	public SceneSpriteObjectState copy(){


		//SceneObjectState genericCopy = super.copy(); 
		//get a copy of the generic data from the supertype
		//This will nowbe a SceneDivObjectState as thats are parent class
		SceneDivObjectState genericCopy = super.copy(); 
		
		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		SceneSpriteObjectState newObject = new SceneSpriteObjectState(genericCopy,
				ObjectsURL,
				DependantURLPrefix,
				currentFrameGap,
				currentlyAnimationState,
				currentNumberOfFrames,
				currentFrame);
/*
		SceneSpriteObjectState newObject = new SceneSpriteObjectState(
				ObjectsURL,
				ObjectsFileName,
				ObjectsSceneName,
				ObjectsName,
				Title,
				CurrentBoxCSS,				
				BackgroundString,
				X,
				Y,
				restrictPositionToScreen,
				hasAttachmentPointFile,
				hasMovementFile,				
				PinPointX,
				PinPointY,
				zindex,
				variableZindex,
				lowerZindex,
				upperZindex,
				stepZindex,
				linkedZindex,
				linkedZindexDifference,
				positionedRelativeToo,
				positionedRelativeToPoint,
				relX,
				relY,
				moveState.copy(),

				(HashSet<SSSProperty>) objectsProperties.clone(),
				ObjectRuntimeVariables.clone(),
				(HashSet<String>) touching.clone(),
				currentNumberOfFrames,
				currentlyAnimationState,
				currentFrame, currentlyVisible);
*/
		//	newObject.currentType = currentType;

		return newObject;


	}
	

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
	
		//now check label specific fields
		SceneSpriteObjectState asSpriteState = (SceneSpriteObjectState) state;
		

		if (!asSpriteState.ObjectsURL.equals(ObjectsURL)){
			return false;
		}
		//
		if (!asSpriteState.DependantURLPrefix.equals(DependantURLPrefix)){
			return false;
		}
		
		if (asSpriteState.currentFrameGap!=(currentFrameGap)){
			return false;
		}
		if (!asSpriteState.currentlyAnimationState.equals(currentlyAnimationState)){
			return false;
		}
		if (asSpriteState.currentNumberOfFrames!=(currentNumberOfFrames)){
			return false;
		}
		if (asSpriteState.currentFrame!=(currentFrame)){
			return false;
		}
		Log.info("All SceneSpriteObjectState object data is the same between these two states");
		
		return true;		
	}

	
	
	

	/**
	 * enums defining parameters relevant to sprites.
	 * Some duplicate general ones, because sprites deal with them differently
	 * @author darkflame
	 *
	 */
	public enum SceneSpriteObjectParamTypes {
		name,filename,dependanturlprefix,frames
	}
	
	
	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP SPRITE SPECIFIC PARAMETERS");		
		assignSpriteObjectTypeSpecificParameters(objectsParamatersFromFile2);
	}

	
	
	
	
/**
 * Assigns the parameters specific to spites from the supplied array of parameter lines
 * 
 * @param itemslines
 */
	//change to enum public enum SceneSpriteObjectParamTypes 
	public void assignSpriteObjectTypeSpecificParameters(String[] itemslines) {
		
		//set folder here ?
		if (ObjectsFileName!=null && !ObjectsFileName.isEmpty()){
			String folderName = ObjectsFileName.split("0\\.")[0];
			Log.info("foldername = "+folderName+" name="+ObjectsName);	
		}
		
		
		//String itemslines[] = Parameters.split("\n");

		int currentlinenum = 0;

		// assign data not yet assigned in super
		while (currentlinenum < itemslines.length) {

			String currentline = itemslines[currentlinenum].trim();
			currentlinenum++;

			if (currentline.equals("")){				
				continue;
			}
			if ((currentline.length() < 3) || (currentline.startsWith("//"))) {
				continue;
			}

			// split by =
			String param = currentline.split("=")[0].trim();
			String value = currentline.split("=")[1].trim();

			SceneSpriteObjectParamTypes currentParamType; 
			try {
				currentParamType =  SceneSpriteObjectParamTypes.valueOf(param.toLowerCase());
			} catch (IllegalArgumentException e) {
				continue;
			}

			switch(currentParamType){
			case dependanturlprefix:
				DependantURLPrefix = value;
				Log.info("DependantURLPrefix set to:"
						+ DependantURLPrefix);

				break;
			case filename:
				ObjectsFileName = value;
				Log.info("Objects file name specificly set to:"
						+ ObjectsFileName);

				break;
			case frames:
				currentNumberOfFrames = Integer.parseInt(value);
				Log.info("Objects Frame count set to:" + value);
				break;
			case name:
				ObjectsName = value;
				ObjectsFileName = value;
				Log.info("Objects file name set by default to:"
						+ ObjectsFileName);

				break;
			}
			
			/*
			if (param.equalsIgnoreCase("Name")) {

				ObjectsName = value;
				ObjectsFileName = value;
				Log.info("Objects file name set by default to:"
						+ ObjectsFileName);

			}
			
			if (param.equalsIgnoreCase("FileName")) {

				ObjectsFileName = value;
				Log.info("Objects file name specificly set to:"
						+ ObjectsFileName);

			}

			if (param.equalsIgnoreCase("DependantURLPrefix")) {

				DependantURLPrefix = value;
				Log.info("DependantURLPrefix set to:"
						+ DependantURLPrefix);

			}
			
			if (param.equalsIgnoreCase("Frames")) {

				currentNumberOfFrames = Integer
						.parseInt(value);
				Log.info("Objects Frame count set to:" + value);

			}
			 */
		}
		
		//------------------------------------------------------------------------
		//----------------------------------------------------
		//----------------------------------
		String sourceFolder = "";
		//we will in future get the folder from the name so we dont have to pass it in
		//the following is a temp check they will always match
		if (getPrimaryObjectType() == SceneObjectType.InventoryObject){
			sourceFolder = "InventoryItems";
		} else {
			sourceFolder = "Game Scenes/"+ObjectsSceneName    ;//newobjectdata.ObjectsSceneName;						
		}


		//Sprites need some extra work to get the settings right
		//This is because the filename has to be treated differently due to it containing frame numbers
		//or even being a internal animation

		// strip extension from name
		if (ObjectsName.contains(".")) {

			ObjectsName = ObjectsName
					.substring(0,
							(ObjectsName.indexOf(".")));

			//if theres a 0 at the end remove that too 
			if (ObjectsName.endsWith("0")){
				ObjectsName=ObjectsName.substring(0, ObjectsName.length()-1);
			}


		}

		if (ObjectsFileName.startsWith("<")) {

			ObjectsURL = ObjectsFileName;

			// if name is blank
			if (ObjectsName.length() < 3) {
				ObjectsName = ObjectsFileName
						.trim();
			}
			Log.info("______________________currentObjectState.ObjectsName:"
					+ ObjectsName + ":");

		} else {

			String folderName = ObjectsFileName.split("0\\.")[0];

			Log.info("foldername = "+folderName+" name="+ObjectsName);

			//different source folder for inventory items

			if (getPrimaryObjectType()==SceneObjectType.InventoryObject){

				Log.info("setting up location for inventory icon ");

				ObjectsURL = sourceFolder
						+ "/" + ObjectsName+ "/"
						+ ObjectsFileName;
				//Note; For inventory items the folder name is the same as the objectname
				//This isnt always true of sceneitems, which the folder name is always 
				//the same as the filename, but can be different from its objectname (ie, when many objects refer to the same image file)
				//in future it might be worth changing inventory icons to the same system
				//by having their "folderName" correctly set earlier.

			} else {

				Log.info("setting up location for sprite ");

				if (!(folderName.contains("\\") || folderName.contains("/"))){
					
					ObjectsURL = sourceFolder
							+ "/Objects/" + folderName+ "/"
							+ ObjectsFileName;
					
					Log.info("using default path  :"+ObjectsURL);
					
				} else {
					Log.info("setting up location for sprite with full path ");
					ObjectsURL = ObjectsFileName;

				}
			}
		}
		
		

		Log.info("__assigned sprite ObjectTypeSpecificParameters to object "+ObjectsName+" ___");

		
		
	}


}




