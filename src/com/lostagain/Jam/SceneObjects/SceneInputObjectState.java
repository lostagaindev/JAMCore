package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.darkflame.client.semantic.SSSProperty;
import com.lostagain.Jam.VariableSet;
import com.lostagain.Jam.Movements.MovementState;

/** 
 * Contains all the information for a single scene input object state<br>
 * We extend SceneDivObjectState and only store what isn't stored by that
 ***/

public class SceneInputObjectState extends SceneDivObjectState {

	public static Logger Log = Logger.getLogger("JAMCore.SceneInputObjectState");
	
	
	///** size of the div. can also be set by css, but this will override that **/
	//int sizeX=-1;
	///** size of the div. can also be set by css, but this will override that **/
	//int sizeY=-1;
	
	/**
	 * Is this input field in read only mode?
	 */
	public boolean ReadOnly = false;

	/**
	 * SET from parameter file, but not saved
	 */
	public int maxcharacters = 200;
	
	/** creates a new SceneInputObject with default values **/
	public SceneInputObjectState(){
		super(); //just use the defaults of the parent class combined with the defaults of this class
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		//if SceneInputObjectState needs different default values to those specified above they should be specified below
		
		//...none needed?
		
		}
	
	
	
	public SceneInputObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
	}



	/**
	 * creates a SceneInputObjectState state from supplied SceneObjectState data
	 * Using default values for all Div (this classes parent) as well as Input (this class itself)
	 */
	public SceneInputObjectState(SceneObjectState state){		
		super(state); //will create a default SceneDivObjectState from SceneObjectState
		
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		
		//no input specific data to set to defaults yet
		//but will go here if theres any
		//
		//
		ReadOnly = false;
		
	}
			
	/**
	 * creates a SceneInputObjectState state from supplied SceneDivObjectState data
	 * Using default values for Input specific stuff.
	 */
	public SceneInputObjectState(SceneDivObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//no input specific data to set to defaults yet
		//but will go here if theres any
		//
		//
		ReadOnly = false;

		
	}
	
	
	/**
	 * creates a SceneInputObjectState state from supplied SceneDivObjectState data
	 * and the InputSpecific values following it
	 * (size will eventually be moved to Div)
	 */
	public SceneInputObjectState(SceneDivObjectState state,boolean ReadOnly){		
		super(state);//will create a SceneDivObjectState from the supplied SceneObjectState and the supplied extra data
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//input specific data
		this.ReadOnly = ReadOnly;
		
	}
	
	
	
	
	
	public SceneInputObjectState(
			String objectsName, 			
			String sceneName,
			String title, 
			String cssname,
			String backstring,
			int x, int y,int z,
			String sizex,String sizey,
			boolean restrictPositionToScreen,
			boolean usesAttachmentFile,
			boolean usesMovementFile,
			int pinPointX, 
			int pinPointY,
			int pinPointZ,
			SceneObject relativeToThis, 
			String relativeToThisOnceLoaded,
			String relativeToThisPoint,
			linktype relativeToLinkType,
			int relX, int relY,	int relZ,
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
			double 	currentOpacity,
			boolean PropagateVisibility, 
			boolean ignoreSceneActions,
			boolean ignorePointerEvents,
			boolean forceReceiveActions,
			CollisionModeSpecs boundaryType, 
			pathfindingMode pathfindMode,
			boolean readonly) {
		
		//universal data
			super( objectsName, 			
					 sceneName,
					 title, 
					 cssname,
					 backstring,
					 x,  y, z,
					 sizex, sizey,
					 restrictPositionToScreen,
					 usesAttachmentFile,
					 usesMovementFile,
					 pinPointX, 
					 pinPointY,
					 pinPointZ,
					 relativeToThis, 
					 relativeToThisOnceLoaded,
					 relativeToThisPoint,
					 relativeToLinkType,
					 relX,  relY,	relZ,
					 zIndex,
					 variableZindex,
					 zIndexLower,
					 zIndexUpper,
					 zIndexStep,
					 linkedZindex,
					 linkedZindexDifference,
					 ms,
					 objectsProperties, 
					 objectsRuntimeVariables,
					 touching,
					 currentlyVisible,
					currentOpacity,
						 PropagateVisibility, 
						 ignoreSceneActions,
						 ignorePointerEvents,
						 forceReceiveActions,
						 boundaryType,
						 pathfindMode);
			
			super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
			
			this.ReadOnly = readonly;
	}

	
	
	
	
	
	public SceneInputObjectState(String serialised) {
		//super(serialized); //NOTE: we DONT use the supertype serialize here, as we already override the loadParameters function
		//This means when deserialise is run it will do our own input-specific loading of parameters
		//which in turn calls the super classes loadParameters as well.
		super();
		super.setObjectsPrimaryType(SceneObjectType.Input); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		deserialise(serialised);
		

	}
/*
	public void deserialise(String serialised) {
		String[] data = serialised.split(deliminator, -1);
		
		// assign data
		Log.info("__________________________________deserialising :"+ data.length);

		//split to a list and get its iterator
		List<String> incomingdatalist = (Arrays.asList(data));
		Iterator<String> incomingdata = incomingdatalist.iterator();

		//run the load parameters over this iterator
		loadParameters(incomingdata);

	};
	
	*/
	
	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialisation functions
	 * 
	 */	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){
		
		//load the parameters of the parent type first
		super.loadParameters(incomingdata);
				
		//load input specific data		 (none yet)
		//sizeX =Integer.parseInt( incomingdata.next());
		//sizeY =Integer.parseInt( incomingdata.next());
		
		this.ReadOnly = Boolean.parseBoolean(incomingdata.next());
		
	}
	
	
	
	/* Serializes this input state to a string 
	@Override
	public String serialiseToString() {

		// first we get all the global fields.
		// That is, ones that apply to both Spites,Text and any other object types.
		ArrayList<Object> fieldList =  getAllFieldsAsArrayList();

		String serialised = "";
			

		serialised = SceneObjectState.serialiseTheseFields(fieldList);
		
		 Log.info("__________________________________serialised as:" + serialised);
		 
		 return serialised;
	}**/

	
	
	
	/**
	 * Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier
	 * **/
	@Override
	public SceneInputObjectState copy(){
		
		SceneDivObjectState genericCopy = super.copy(); //gives us a SceneDivObjectState thats a copy of this one

		SceneInputObjectState tempObjectData = new SceneInputObjectState(genericCopy,ReadOnly); //make a specific InputState from it
		//(eventually that constructor will have to take the specific parameters in this input function, but theres non yet)
		
		
		/*
		SceneInputObjectState tempObjectData = new SceneInputObjectState(
				ObjectsName,
				ObjectsSceneName,
				Title,	
				CurrentBoxCSS,
				BackgroundString,
				X, Y,
				sizeX,sizeY,
				restrictPositionToScreen,
				hasAttachmentPointFile,
				hasMovementFile,
				PinPointX, PinPointY,
				positionedRelativeToo,positionedRelativeToPoint, relX,relY,
				zindex,
				variableZindex,
				lowerZindex,
				upperZindex,
				stepZindex,
				linkedZindex,
				linkedZindexDifference,
				moveState.copy(),
				(HashSet<SSSProperty>) objectsProperties.clone(),
				ObjectRuntimeVariables.clone(),
				(HashSet<String>)touching.clone(),
				currentlyVisible);
		*/
		
		//tempObjectData.setObjectsOnlyType(super.getCurrentType());
				
		return tempObjectData;
	}
	

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
	
		//now check label specific fields
		SceneInputObjectState asInputState = (SceneInputObjectState) state;
		
		if (asInputState.ReadOnly != (ReadOnly)){
			return false;
		}
		if (asInputState.maxcharacters != (maxcharacters)){
			return false;
		}
		
		Log.info("All SceneInputObjectState object data is the same between these two states");
		
		return true;		
	}

	
	
	@Override
	protected ArrayList<Object> getAllFieldsAsArrayList() {


		// first we get all the fields from the parent class
		// That is, ones that all subtypes of it have in common
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();
		
		//then we add the fields specific to this subtype
		Log.info("getting input specific fields. (just one, its read only state)");	//just two; size
		
		fieldList.add(ReadOnly);

		Log.info("got input specific fields");

		return fieldList;

	}
	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP INPUT SPECIFIC PARAMETERS");		
		assignInputObjectTypeSpecificParameters(objectsParamatersFromFile2);
	}
	
	public void assignInputObjectTypeSpecificParameters(String[] itemslines) {
		
		
		int currentlinenum = 0;
		while (currentlinenum < itemslines.length) {
			//trim current line
			String currentline = itemslines[currentlinenum].trim();
			currentlinenum++;
			//skip if comment or empty
			if ((currentline.length() < 2) || (currentline.startsWith("//"))) {
				continue;
			}

			Log.info("processing line:"+currentline);


			// split by =
			String param = currentline.split("=")[0].trim();
			String value = currentline.split("=")[1].trim();
			
			
			if (param.equalsIgnoreCase("MaxCharacters")) {

				maxcharacters = Integer.parseInt(value);
				Log.info("maxcharacters set to:"+maxcharacters);

			}


		}

		
	}
}
