package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/** contains all the information for a single scene dialogue object state **/

public class SceneDialogueObjectState extends SceneLabelObjectState {
	public static Logger Log = Logger.getLogger("JAMCore.SceneDialogObjectState");


	
	//String ObjectsName;
	public String currentparagraphName  = "default"; //used to be ""

	/** The current text of the object - or null if specified by a file **/
	public String ObjectsCurrentURL; // source url of the text, if any



	//int X=-1;
	//int Y=-1;
	//String sizeX;//="-1";
	//String sizeY;//="-1";

	//int ZIndex=-1;
	public int currentParagraphPage =0;		//this should never be less then -1 once a bit of text has been loaded.
	public int currentNumberOfParagraphs=-5; 


	public String NextParagraphObject="";

	
	
	/**create a fully default dialogue state with default values  */
	public SceneDialogueObjectState(){
		super();
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);

	}
	
	

	public SceneDialogueObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);
	}



	/** create a fully default dialogue from generic state object, with dialogue specific stuff set to their defaults  */
	public SceneDialogueObjectState(SceneObjectState state){
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);
		//super.getCurrentType()=SceneObjectType.DialogBox;
		//super.setObjectsOnlyType(SceneObjectType.DialogBox); //we don't set this anymore as subclasses may set this themselves!

		//Clear css parameters from general settings passed to this
		//This is because Dialogue objects treats these differently.
		//We fill them up correctly when the dialogue specific stuff is processed
		CurrentBoxCSS="";
		//	CSSname="";

	}


	/**
	 * creates a SceneInputObjectState state from supplied SceneDivObjectState data
	 * Using default values for Input specific stuff.
	 */
	public SceneDialogueObjectState(SceneDivObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.DialogBox); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/

		//Clear css parameters from general settings passed to this
		//This is because Dialogue objects treats these differently.
		//We fill them up correctly when the dialogue specific stuff is processed
		CurrentBoxCSS="";
		//	CSSname="";


	}



	/**
	 * creates a SceneDialogObjectState state from supplied SceneDivObjectState data
	 * and the DialogueSpecific values following it
	 * CurrentBoxCSS is also passed to it, as its handled differently then in the superclass
	 */
	public SceneDialogueObjectState(SceneDivObjectState genericState,
			//	String sizeX,
			//	String sizeY, 
			String objectsText,
			String objectsURL,
			String cSSname,
			String currentparagraphName,
			int currentNumberOfParagraphs,
			int currentParagraphPage, 
			String NextParagraphObject,
			cursorVisibleOptions cursorVisible,			
			TrueFalseOrDefault typedText,
			String Custom_Key_Beep,
			String Custom_Space_Beep) {

		super(genericState);

		//	this.sizeX=sizeX;
		//	this.sizeY=sizeY;


		this.ObjectsCurrentText = objectsText;
		this.ObjectsCurrentURL = objectsURL;

		this.CSSname = cSSname;
		this.currentparagraphName = currentparagraphName;

		this.currentNumberOfParagraphs = currentNumberOfParagraphs;
		this.currentParagraphPage = currentParagraphPage;

		this.NextParagraphObject = NextParagraphObject;		

		this.cursorVisible=cursorVisible;
		this.TypedText=typedText;

		this.Custom_Key_Beep = Custom_Key_Beep;
		this.Custom_Space_Beep = Custom_Space_Beep;

		super.setObjectsPrimaryType(SceneObjectType.DialogBox);
	}



	/**
	 * Create a dialogue with the default generic property but with the dialogue specific ones specified 
	 * This will create a state with its type set to dialoguebox. To use this constructor with subclass's, please specific the type with setObjectsOnlyType after this is called. 
	 * Else use the constructor SceneDialogObjectState(SceneObjectState state) 
	 * */
	public SceneDialogueObjectState(
			String objectsText,
			String objectsURL,
			String sizeX,
			String sizeY,
			String cSSname,
			String currentparagraphName,
			int currentNumberOfParagraphs,
			int currentParagraphPage, 
			String NextParagraphObject,
			cursorVisibleOptions cursorVisible,
			TrueFalseOrDefault typedText,
			String Custom_Key_Beep,
			String Custom_Space_Beep
			)

	{
		//universal data defaults
		super();
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);

		ObjectsCurrentText = objectsText;
		ObjectsCurrentURL = objectsURL;


		//Title = title;
		//	X = x;
		//Y = y;
		this.sizeX = sizeX;
		this.sizeY = sizeY;

		this.CSSname = cSSname;
		//	zindex = zIndex;
		//this.objectsProperties = new PropertySet(objectsProperties);
		this.currentparagraphName = currentparagraphName;

		this.currentNumberOfParagraphs = currentNumberOfParagraphs;
		this.currentParagraphPage = currentParagraphPage;

		this.NextParagraphObject = NextParagraphObject;

		this.cursorVisible=cursorVisible;
		this.TypedText=typedText;


		this.Custom_Key_Beep = Custom_Key_Beep;
		this.Custom_Space_Beep = Custom_Space_Beep;
	}



	public SceneDialogueObjectState(String serialised) {
		//	super(serialised);
		super();
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);
		deserialise(serialised);


	}


	public SceneDialogueObjectState(
			SceneLabelObjectState genericLabelState,
			String objectsCurrentURL,
			String currentparagraphName, 
			int currentNumberOfParagraphs, 
			int currentParagraphPage,
			String nextParagraphObject) {

		super(genericLabelState);
		super.setObjectsPrimaryType(SceneObjectType.DialogBox);

		this.ObjectsCurrentURL         = objectsCurrentURL;
		this.currentparagraphName      = currentparagraphName;
		this.currentNumberOfParagraphs = currentNumberOfParagraphs;
		this.currentParagraphPage      = currentParagraphPage;
		this.NextParagraphObject       = nextParagraphObject;

	}

	/*
	public void deserialise(String serialised) {
		String[] data = serialised.split(deliminator, -1);

		// assign data
		Log.info("__________________________________deserialising :"+ data.length);


		// list its easier to manage
		List<String> incomingdatalist = (Arrays.asList(data));
		Iterator<String> incomingdata = incomingdatalist.iterator();

		//load global data first
		loadParameters(incomingdata);

	};*/


	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions	 * 
	 */	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){

		//load the parameters of the parent type first
		super.loadParameters(incomingdata);

		//load dialogue specific data				
		//MUST be in the same order as "getAllFieldsAsArrayList"

		//ObjectsCurrentText=incomingdata.next().replaceAll(ESCAPED_NEWLINE,"\n");
		//CSSname=incomingdata.next();
		//Log.info("__________________________________CSSname set to :"+CSSname);

		ObjectsCurrentURL=incomingdata.next();
		
		currentparagraphName=incomingdata.next();

		currentParagraphPage=Integer.parseInt(incomingdata.next());
		currentNumberOfParagraphs=Integer.parseInt(incomingdata.next());

		NextParagraphObject = incomingdata.next();

		//currentlyVisible=Boolean.parseBoolean(incomingdata.next());

		Log.info("__________________________________currentlyVisible set to :"+currentlyVisible);



		//String newobjectsProperties = data[24].trim();


	}



	/**
	 * 
	 * Make sure that all the fieldlists are in exactly the same order as the ones in the deserialise section.
	 * When making a new property, also assure adding it to this list.
	 * 
	 *  */
	protected ArrayList<Object> getAllFieldsAsArrayList() {

		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();

		//then we get the dialogue specific fields

		//if (ObjectsCurrentText==null){
		//	Log.severe("Text is null at this point..is there a previous error? It could be the text contains characters the paragraph object doesnt support.");

		//}

		// dialog data
		//fieldList.add(ObjectsCurrentText.replaceAll("\n",ESCAPED_NEWLINE));
		//fieldList.add(CSSname);
		Log.info("getting dialogue specific fields for object "+ObjectsName);

		//ObjectsCurrentURL

		fieldList.add(ObjectsCurrentURL); //
		
		fieldList.add(currentparagraphName);

		fieldList.add(currentParagraphPage); //be sure to update this before save
		fieldList.add(currentNumberOfParagraphs);


		fieldList.add(NextParagraphObject);
		//fieldList.add(currentlyVisible);


		return fieldList;

	}

	/*
	@Override
	public String serialiseToString() {

		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = getAllFieldsAsArrayList();

		String serialised = "";
		//then we add the dialogue specific stuff



		//	Log.info("__________________________________serialised as:"
		//		+ serialised);

		serialised = SceneObjectState.serialiseTheseFields(fieldList);
		Log.info("__________________________________serialised as:"
				+ serialised);

		return serialised;
	}*/

	/**
	 * Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier
	 * **/
	public SceneDialogueObjectState copy(){

		//get a copy of the generic data from the supertype (Label)
		SceneLabelObjectState genericCopy = super.copy(); 

		SceneDialogueObjectState newObject = new SceneDialogueObjectState(
				genericCopy,
				ObjectsCurrentURL,		
				currentparagraphName,
				currentNumberOfParagraphs,
				currentParagraphPage, 
				NextParagraphObject);




		/*
		//get a copy of the generic data from the supertype
		SceneDivObjectState genericCopy = super.copy(); 

		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		SceneDialogObjectState newObject = new SceneDialogObjectState(
				genericCopy,
			//	sizeX,
		//		sizeY,
				ObjectsCurrentText,
				ObjectsCurrentURL,				
				CSSname,
				currentparagraphName,
				currentNumberOfParagraphs,
				currentParagraphPage, 
				NextParagraphObject,
				cursorVisible,
				TypedText,
				Custom_Key_Beep,
				Custom_Space_Beep);


		SceneDialogObjectState newObject = new SceneDialogObjectState(
				ObjectsName,
				ObjectsFileName,
				ObjectsCurrentText,
				ObjectsCurrentURL, 
				ObjectsSceneName,
				Title,			
				CurrentBoxCSS,
				BackgroundString,
				X, Y,
				restrictPositionToScreen,
				hasAttachmentPointFile,
				hasMovementFile,
				PinPointX, PinPointY,
				positionedRelativeToo,positionedRelativeToPoint, relX,relY,
				sizeX, sizeY,
				CSSname, 
				cursorVisible,
				TypedText, 
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
				currentparagraphName,				
				currentParagraphPage,  //this and the below were the wrong way around before
				currentNumberOfParagraphs, //this and the above were the wrong way around
				NextParagraphObject,
				currentlyVisible);

		newObject.setObjectsOnlyType(super.getCurrentType());
		 */

		return newObject;


	}

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
	
		//now check label specific fields
		SceneDialogueObjectState asDialogueState = (SceneDialogueObjectState) state;
		
		//if both are not null compare the url
		if (ObjectsCurrentURL!=null && ObjectsCurrentURL!=null){
			if (!asDialogueState.ObjectsCurrentURL.equals(ObjectsCurrentURL)){
				return false;
			}
			//if either of them is null but not both
		} else if (ObjectsCurrentURL!=null && ObjectsCurrentURL==null) {
			return false;
		} else if (ObjectsCurrentURL==null && ObjectsCurrentURL!=null) {
			return false;
		}
		
		if (!asDialogueState.currentparagraphName.equals(currentparagraphName)){
			return false;
		}
		if (asDialogueState.currentNumberOfParagraphs!=(currentNumberOfParagraphs)){
			return false;
		}
		if (asDialogueState.currentParagraphPage!=(currentParagraphPage)){
			return false;
		}
		if (!asDialogueState.NextParagraphObject.equals(NextParagraphObject)){
			return false;
		}
		

		Log.info("All dialogue object data is the same between these two states");
		
		return true;		
	}
	
	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP DIALOG SPECIFIC PARAMETERS");
		assignDialogueObjectTypeSpecificParameters(objectsParamatersFromFile2);
	}
	
	
	//@Override	
	public void assignDialogueObjectTypeSpecificParameters(String[] itemslines) {


		//String itemslines[] = Parameters.split("\n");
		int currentlinenum = 0;
		while (currentlinenum < itemslines.length) {

			String currentline = itemslines[currentlinenum].trim();
			currentlinenum++;
			if ((currentline.length() < 2) || (currentline.startsWith("//"))) {
				continue;
			}

			Log.info("processing line:"+currentline);


			// split by =
			String param = currentline.split("=")[0].trim();
			String value = currentline.split("=")[1].trim();


			//note we have cleared these settings above only to reset them here
			//because dialogue objects treat these parameters differently
			//Normally CSSname is assumed to be BoxCSS, but with text objects it refers to the inner css of the text
			//and BoxCSS only the containers CSS
			//Other object types only have container CSS, thus both parameters effect that instead.
			if (param.equalsIgnoreCase("BoxCSS")) {

				CurrentBoxCSS = value;				
				Log.info("Objects CurrentBoxCSS set to:"+ CurrentBoxCSS);
			}

			if (param.equalsIgnoreCase("CSSname")) {

				CSSname = value;

				Log.info("Objects CSSname set to:"+ CSSname);
			}


			if (param.equalsIgnoreCase("DefaultURL")) {

				ObjectsCurrentURL = value;

				Log.info("Objects ObjectsCurrentURL set to:" + ObjectsCurrentURL);
			}

			if (param.equalsIgnoreCase("NextParagraphObject")) {

				NextParagraphObject = value;

				Log.info("Objects NextParagraphObject set to:"
						+ NextParagraphObject);
			}


		}



	}
}
