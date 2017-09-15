package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.SceneObjects.SceneLabelObjectState.cursorVisibleOptions;
import com.lostagain.Jam.SceneObjects.SceneObjectState.TrueFalseOrDefault;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState.SceneSpriteObjectParamTypes;

/** contains all the information for a single scene labels object state 
 * 
 * SceneLabels are not yet implemented, but eventually will be a simple class
 * based on TypeLabel, that does not use a text file or a paragraph collection.**/

public class SceneLabelObjectState extends SceneDivObjectState {

	public static Logger Log = Logger.getLogger("JAMCore.SceneLabelObjectState");


	/** used for referencing **/
	public String ObjectsCurrentText;
	public String CSSname = "";

	public enum cursorVisibleOptions  {
		TRUE,
		FALSE,
		/**
		 * Only show a cursor while typing
		 */
		WHENTYPING,
		DEFAULT
	}

	public cursorVisibleOptions cursorVisible = cursorVisibleOptions.DEFAULT; //cursor not visible for text labels by default

	public TrueFalseOrDefault TypedText = TrueFalseOrDefault.DEFAULT;

	/** optional overrides for the typing sound this dialogue makes **/
	public String Custom_Key_Beep = "";

	/** optional overrides for the typing sound this dialogue makes **/
	public String Custom_Space_Beep = "";

	/**create a fully default Label state with default values  */
	public SceneLabelObjectState(){
		super();
		super.setObjectsPrimaryType(SceneObjectType.Label);

	}







	public SceneLabelObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.Label);
		//Clear css parameters from general settings passed to this
		//This is because Dialogue objects treats these differently.
		//We fill them up correctly when the dialogue specific stuff is processed
		CurrentBoxCSS="";

	}







	/** create a fully default label from generic state object, with label & div specific stuff set to their defaults  */
	public SceneLabelObjectState(SceneObjectState state){
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.Label);
		//super.getCurrentType()=SceneObjectType.DialogBox;
		//super.setObjectsOnlyType(SceneObjectType.DialogBox); //we don't set this anymore as subclasses may set this themselves!

		//Clear css parameters from general settings passed to this
		//This is because Dialogue objects treats these differently.
		//We fill them up correctly when the dialogue specific stuff is processed
		CurrentBoxCSS="";

	}

	/**
	 * creates a SceneLabelObjectState state from supplied SceneDivObjectState data
	 * Using default values for Label specific stuff.
	 */
	public SceneLabelObjectState(SceneDivObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Label); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/

		//Clear css parameters from general settings passed to this
		//This is because Dialogue objects treats these differently.
		//We fill them up correctly when the dialogue specific stuff is processed
		CurrentBoxCSS="";

	}

	/**
	 * creates a SceneLabelObjectState state from the supplied SceneLabelObjectState, copying all its data
	 * 
	 */
	public SceneLabelObjectState(SceneLabelObjectState state){		
		super(state); //will create a SceneDivObjectState from the supplied state
		super.setObjectsPrimaryType(SceneObjectType.Label); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/


		this.ObjectsCurrentText = state.ObjectsCurrentText;
		this.CSSname = state.CSSname;
		this.cursorVisible=state.cursorVisible;
		this.TypedText=state.TypedText;		
		this.Custom_Key_Beep = state.Custom_Key_Beep;
		this.Custom_Space_Beep = state.Custom_Space_Beep;

	}


	/**
	 * creates a SceneLabelObjectState state from supplied SceneDivObjectState data
	 * and the LabelSpecific values following it
	 * CurrentBoxCSS is also passed to it, as its handled differently then in the superclass
	 */
	public SceneLabelObjectState(SceneDivObjectState genericState,			
			String objectsText,
			String cSSname,		
			cursorVisibleOptions cursorVisible,			
			TrueFalseOrDefault typedText,
			String Custom_Key_Beep,
			String Custom_Space_Beep) {

		super(genericState);
		super.setObjectsPrimaryType(SceneObjectType.Label);

		//	this.sizeX=sizeX;
		//	this.sizeY=sizeY;

		this.ObjectsCurrentText = objectsText;

		this.CSSname = cSSname;

		this.cursorVisible=cursorVisible;
		this.TypedText=typedText;

		this.Custom_Key_Beep = Custom_Key_Beep;
		this.Custom_Space_Beep = Custom_Space_Beep;

	}


	public SceneLabelObjectState(String serialised) {
		//	super(serialised);
		super();
		super.setObjectsPrimaryType(SceneObjectType.Label);
		//setObjectsOnlyType(SceneObjectType.DialogBox);
		deserialise(serialised);


	}

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

		ObjectsCurrentText=incomingdata.next().replaceAll(ESCAPED_NEWLINE,"\n");
		CSSname=incomingdata.next();
		Log.info("__________________________________CSSname set to :"+CSSname);



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
		Log.info("getting label specific fields for object "+ObjectsName);

		if (ObjectsCurrentText==null){
			Log.severe("Text is null at this point..is there a previous error? It could be the text contains characters the paragraph object doesnt support.");
		}

		// dialog data
		//

		//we escape all newlines
		String currentTextWithEscapedNewlines = ObjectsCurrentText.replaceAll("\\r\\n|\\r|\\n",ESCAPED_NEWLINE);		
		fieldList.add(currentTextWithEscapedNewlines );		
		//	fieldList.add(ObjectsCurrentText.replaceAll("\n",ESCAPED_NEWLINE) );
		fieldList.add(CSSname);


		return fieldList;

	}

	/***
	 * Makes an exact copy of this objects data.
	 * The intention is to make snapshots of objects easier
	 ***/
	public SceneLabelObjectState copy(){

		//get a copy of the generic data from the supertype
		SceneDivObjectState genericCopy = super.copy(); 

		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		SceneLabelObjectState newObject = new SceneLabelObjectState(
				genericCopy,
				ObjectsCurrentText,		
				CSSname,
				cursorVisible,
				TypedText,
				Custom_Key_Beep,
				Custom_Space_Beep);

		return newObject;


	}

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
		//now check label specific fields
		SceneLabelObjectState asLabelState = (SceneLabelObjectState) state;

		if (!asLabelState.ObjectsCurrentText.equals(ObjectsCurrentText)){
			return false;
		}
		if (!asLabelState.CSSname.equals(CSSname)){
			return false;
		}
		if (!asLabelState.cursorVisible.equals(cursorVisible)){
			return false;
		}
		if (!asLabelState.TypedText.equals(TypedText)){
			return false;
		}
		if (!asLabelState.Custom_Key_Beep.equals(Custom_Key_Beep)){
			return false;
		}
		if (!asLabelState.Custom_Space_Beep.equals(Custom_Space_Beep)){
			return false;
		}

		Log.info("All label object data is the same between these two states");

		return true;		
	}



	/**
	 * enums defining parameters relevant to sprites.
	 * Some duplicate general ones, because sprites deal with them differently
	 * @author darkflame
	 *
	 */
	public enum SceneLabelObjectParamTypes {
		boxcss,
		cssname,
		cursorvisible,
		typetext,
		keybeep,	spacekeybeep,

		defaulttext
	}


	/**
	 * should be overridden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP LABEL SPECIFIC PARAMETERS");
		assignObjectLabelTypeSpecificParameters(objectsParamatersFromFile2);
	}




	public void assignObjectLabelTypeSpecificParameters(String[] itemslines) {

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

			SceneLabelObjectParamTypes currentParamType; 
			try {
				currentParamType =  SceneLabelObjectParamTypes.valueOf(param.toLowerCase());
			} catch (IllegalArgumentException e) {
				continue;
			}


			switch(currentParamType){
			//note we have cleared these css/boxcss settings above only to reset them here
			//because dialogue objects treat these parameters differently
			//Normally CSSname is assumed to be BoxCSS, but with text objects it refers to the inner css of the text
			//and BoxCSS only the containers CSS
			//Other object types only have container CSS, thus both parameters effect that instead.

			case boxcss:
				CurrentBoxCSS = value;				
				Log.info("Objects CurrentBoxCSS set to:"+ CurrentBoxCSS);			
				break;
			case cssname:
				CSSname = value;
				Log.info("Objects CSSname set to:"+ CSSname);
				break;
			case cursorvisible:
				cursorVisible = cursorVisibleOptions.valueOf(value.toUpperCase());  //value;
				Log.info("_______________________________________Objects visibility set to:"+ cursorVisible);		
				break;
			case defaulttext:
				ObjectsCurrentText = value;
				Log.info("Objects ObjectsCurrentText set to:"					+ ObjectsCurrentText);	
				break;
			case keybeep:
				Custom_Key_Beep = value;
				Log.info(ObjectsName+"_____________Objects Custom_Key_Beep set to:"+ Custom_Key_Beep);
				break;
			case spacekeybeep:
				Custom_Space_Beep = value;
				Log.info(ObjectsName+"_____________Objects SpaceKeyBeep set to:"+ Custom_Space_Beep);
				break;
			case typetext:
				TypedText = TrueFalseOrDefault.valueOf(value.toUpperCase());
				Log.info(ObjectsName+"_____________Objects TypedText set to:"						+ TypedText);
				break;
			}

			/*
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

			//-------------

			if (param.equalsIgnoreCase("CursorVisible")) {

				cursorVisible = cursorVisibleOptions.valueOf(value.toUpperCase());  //value;
				Log.info("_______________________________________Objects visibility set to:"+ cursorVisible);
			}
			if (param.equalsIgnoreCase("TypeText")) {

				//TypedText = Boolean.parseBoolean(value);

				TypedText = TrueFalseOrDefault.valueOf(value.toUpperCase());

				Log.info(ObjectsName+"_____________Objects TypedText set to:"
						+ TypedText);
			}

			if (param.equalsIgnoreCase("KeyBeep")) {

				Custom_Key_Beep = value;

				Log.info(ObjectsName+"_____________Objects Custom_Key_Beep set to:"+ Custom_Key_Beep);
			}

			if (param.equalsIgnoreCase("SpaceKeyBeep")) {

				Custom_Space_Beep = value;

				Log.info(ObjectsName+"_____________Objects SpaceKeyBeep set to:"+ Custom_Space_Beep);
			}


			if (param.equalsIgnoreCase("DefaultText")) {

				ObjectsCurrentText = value;

				Log.info("Objects ObjectsCurrentText set to:"
						+ ObjectsCurrentText);	
			}*/

		}


	}
}
