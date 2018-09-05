package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/** 
 * contains all the information for a single scene vector object state
 ***/

public class SceneVectorObjectState extends SceneDivObjectState {

	public static Logger Log = Logger.getLogger("JAMCore.SceneVectorObjectData");
	
	
	 /** The vector string text of the object - or null if specified by a file **/
	public String objectsVectorString;
	
	/**
	 * the url containing the vectors shape. Empty string if specified directly
	 */
	public String objectsVectorSourceURL=""; // source url of the vector, if any
	
	
	///** size of the vector. can also be set by css, but this will override that **/
	//int sizeX=-1;
	///** size of the vector. can also be set by css, but this will override that **/
	//int sizeY=-1;
	
	
	//old creation method below
	//we now create from a DIV State + Vector specific stuff instead as its neater
	/*
	public SceneVectorObjectState(
			String objectsName,
			String title, 
			String objectsFileName,
			String objectsVectorString,
			String objectsURL,
			String sceneName,
			String cssname,
			String backstring,
			int x, int y,
			int sizex,int sizey,
			boolean restrictPositionToScreen,
			Boolean usesAttachmentFile,
			Boolean usesMovementFile,
			int pinPointX, 
			int pinPointY,
			SceneObjectVisual relativeToThis, String relativeToThisPoint,int relX, int relY,	
			int zIndex,
			boolean variableZindex,
			int zIndexLower,
			int zIndexUpper,
			int zIndexStep,
			boolean linkedZindex,
			int linkedZindexDifference,
			movementState ms,
			HashSet<SSSProperty> objectsProperties, 
			VariableSet ObjectRuntimeVariables,
			HashSet<String> touching,			
			boolean currentlyVisible) {
		
		//universal data
			super(objectsName,"[none]",sceneName,title, cssname,backstring,
					    x, y,  
					    restrictPositionToScreen,
					    usesAttachmentFile,
					    usesMovementFile,
					    pinPointX,
						 pinPointY,  currentlyVisible,
						SceneObjectType.Vector,  //currently it has one type;Vector
						zIndex, 
						variableZindex,
						zIndexLower,
						zIndexUpper,
						zIndexStep,
						linkedZindex,
						linkedZindexDifference,
						relativeToThis, relativeToThisPoint,
						relX,  relY,
						ms,
						new PropertySet(objectsProperties),
						new VariableSet(ObjectRuntimeVariables),
						touching);
			
			Log.info("Setting Vector string equal to:"+objectsVectorString);
			
			ObjectsCurrentVectorString = objectsVectorString;
			ObjectsCurrentURL = objectsURL;
			
			sizeX=sizex;
			sizeY=sizey;
			

		
	}
*/
	
	

	
	//boolean currentlyVisible = true;
	
	/**
	 * creates a SceneVectorObjectState state from supplied SceneObjectState data
	 *  Using default values for all Div (this classes parent) as well as Vector (this class itself)
	 */
	public SceneVectorObjectState(SceneObjectState state){		
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//vector specifics goto their defaults; 
		objectsVectorString    = "L 0,0 10,0 10,10 0,10 s";
		objectsVectorSourceURL = "";
		
		//sizeX=-1;
		//sizeY=-1; //new size defaults to "" as its a string		
		
	}
	
	
	
	public SceneVectorObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//vector specifics goto their defaults; 
		objectsVectorString    = "L 0,0 10,0 10,10 0,10 s";
		objectsVectorSourceURL = "";
	}



	/**
	 * creates a SceneVectorObjectState state from supplied SceneDivObjectState data
	 * Using default values for Vector specific stuff.
	 */
	public SceneVectorObjectState(SceneDivObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		objectsVectorString = "L 0,0 10,0 10,10 0,10 s";
		objectsVectorSourceURL = "";
		
		
	}
	
	
	
	
	/**
	 * creates a SceneVectorObjectState state from supplied SceneDivObjectState data
	 * and the VectorSpecific values following it
	 * (size will eventually be moved to Div)
	 **/
	public SceneVectorObjectState(SceneDivObjectState state,String VectorString,
			String objectsCurrentURL){		
		super(state);//setup the superclass (SceneDivObjectState) with the state data we got for it
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		//now setup the vector specific data
		objectsVectorString = VectorString;
		objectsVectorSourceURL = objectsCurrentURL;
	}
	
	/*
	/**
	 * creates a SceneVectorObjectState state from supplied SceneObjectState data
	 * and the VectorSpecific values following it
	 
	public SceneVectorObjectState(SceneObjectState genericState, int sizeX,
			int sizeY, String objectsCurrentVectorString,
			String objectsCurrentURL) {
		
		super(genericState);
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		this.sizeX=sizeX;
		this.sizeY=sizeY;
		
		this.ObjectsCurrentVectorString = objectsCurrentVectorString;
		this.ObjectsCurrentURL = objectsCurrentURL;
		
	}
	**/
	
	
	public SceneVectorObjectState(String serialised) {
		super();
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		
		deserialise(serialised); //will fire our specific loadParameters to get Vector specific data
		

	}


/*
	public void deserialise(String serialised) {
		String[] data = serialised.split(deliminator, -1);
		// assign data
		Log.info("__________________________________deserialising :"
				+ data.length);


		// list its easier to manage
		List<String> incomingdatalist = (Arrays.asList(data));
		Iterator<String> incomingdata = incomingdatalist.iterator();

		//load global data first
		loadParameters(incomingdata);
		


	};*/
	
	/** creates a new SceneVectorObjectState with default values **/
	public SceneVectorObjectState(){
		super(); //just use the defaults of the parent class combined with the defaults of this class
		super.setObjectsPrimaryType(SceneObjectType.Vector); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/
		//if SceneVectorObjectState needs different default values to those specified above they should be specified below
		
		objectsVectorString = "L 0,0 10,0 10,10 0,10 s";
		objectsVectorSourceURL = "";
	}

	
	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions	 * 
	 */	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){
		
		//load the parameters of the parent type first
		super.loadParameters(incomingdata);

		//load vector specific data
		objectsVectorString    = incomingdata.next().replaceAll(ESCAPED_NEWLINE,"\n");
		
		//source url
		objectsVectorSourceURL = incomingdata.next();
		
		
		
		
	}
	
	
	/** Warning, currently non-functional.
	 * Need to look at the working Sprite object serialiser, and apply a similar technique here
	@Override
	public String serialiseToString() {

		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		
		 Log.info("heeeloooooo I'm about to crash yesh?");
		 Log.info("serialising: "+this.ObjectsName);
		ArrayList<Object> fieldList =  getAllFieldsAsArrayList();

		String serialised = "";
		
		//then we add the vector specific stuff
		
		/*
		

	//	Log.info("__________________________________serialised as:"
		//		+ serialised);

		//return serialised;
		
		Log.info("Lolz no crashy crash yet?");
		serialised = SceneObjectState.serialiseTheseFields(fieldList);
		
		 Log.info("__________________________________serialised as:"
		 + serialised);
		 Log.info("Oh noez teh serialisationz?");
		 return serialised;
		 
		
	} **/

	/**Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier**/
	public SceneVectorObjectState copy(){
		
		//get a copy of the generic data from the supertype
		//This will be a SceneDivObjectState as thats are parent class
		SceneDivObjectState genericCopy = super.copy(); 
		
		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		SceneVectorObjectState tempObjectData = new SceneVectorObjectState(genericCopy,
				objectsVectorString,objectsVectorSourceURL);
		//sizeX,sizeY, is no longer included in the above, as the DivObject state handles it
		
		/*
		SceneVectorObjectState tempObjectData = new SceneVectorObjectState(
				ObjectsName,
				Title,	
				ObjectsFileName,
				ObjectsCurrentVectorString,
				ObjectsCurrentURL, 
				ObjectsSceneName,
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
				(HashSet<String>) touching.clone(),
				currentlyVisible);
		
		tempObjectData.setObjectsOnlyType(super.getCurrentType());
		*/
		
		return tempObjectData;
	}
	
	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
		
		//now check vector specific fields
		SceneVectorObjectState asVectorState = (SceneVectorObjectState) state;
		

		Log.info("asVectorState.objectsVectorString:"+asVectorState.objectsVectorString);
		Log.info("asVectorState.objectsVectorSourceURL:"+asVectorState.objectsVectorSourceURL);
		
		if (!asVectorState.objectsVectorString.equals(objectsVectorString)){
			return false;
		}
		if (!asVectorState.objectsVectorSourceURL.equals(objectsVectorSourceURL)){
			return false;
		}
		
		Log.info("All SceneVectorObjectState object data is the same between these two states");
		
		return true;		
	}

	
	protected ArrayList<Object> getAllFieldsAsArrayList() {


		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();
		
		//then we add vector specific fields
		Log.info("getting vector specific fields. There are two: vector string, objectsVectorSourceURL");	
		
		//Log.info("Pass 1");
		
		//OPTIMISATION ALERT
		//We should not save the vector if it hasn't changed
		//Instead we save "-" which indicates to the loadState to use the initial state vector string
		//We cant easily do this optimization here though, as we dont know if its changed or not
		//hmm..
		//Non-changed vectors shouldn't be saved at all anyway. No object should save if it hasn't changed
		//so maybe this optimization isnt worth it?
		
		String vectorStringWithEscapedNewlines = objectsVectorString;
		//Log.info("Pass 2");
		
		if (vectorStringWithEscapedNewlines == null) {
			Log.info("Ffffffffffff 0rz");
			}
		
		//TODO: check this works;
	//	if (vectorStringWithEscapedNewlines.contains("\n")) {			
	//		vectorStringWithEscapedNewlines = objectsVectorString.replaceAll("\n",ESCAPED_NEWLINE);						
	//	}
		
		vectorStringWithEscapedNewlines = vectorStringWithEscapedNewlines.replaceAll("\\r\\n|\\r|\\n",ESCAPED_NEWLINE);		
	
		
		
	//	Log.info("Pass 4");
		fieldList.add(vectorStringWithEscapedNewlines);
		fieldList.add(objectsVectorSourceURL);
		
		//size now handled by superclass
		//fieldList.add(sizeX);
		//fieldList.add(sizeY);
		
		//Log.info("Pass 5");
		
		Log.info("got vector specific fields");

		return fieldList;

	}


	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP VECTOR SPECIFIC PARAMETERS");		
		assignObjectTypeSpecificParameters(objectsParamatersFromFile2);
	}
	
	public void assignObjectTypeSpecificParameters(String[] itemslines) {

		// now split again by new lines to get parameter data
				//String itemslines[] = Parameters.split("\n");
				int currentlinenum = 0;
				while (currentlinenum < itemslines.length) {

					String currentline = itemslines[currentlinenum].trim();
					currentlinenum++;
					if ((currentline.length() < 2) || (currentline.startsWith("//"))) {
						continue;
					}

					Log.info("Processing line:"+currentline);
					
					
					//Split by =
					String param = currentline.split("=",2)[0].trim();
					String value = currentline.split("=",2)[1].trim();
								
					if (param.equalsIgnoreCase("DefaultURL")) {

						objectsVectorSourceURL = value;

						Log.info("Objects ObjectsCurrentURL set to:"
								+ objectsVectorSourceURL);
					}

					if (param.equalsIgnoreCase("VectorString")) { 
						
						objectsVectorString = value;

						Log.info("Objects ObjectsCurrentText set to:"
								+ objectsVectorString);
					}
				

				}

	}
}
