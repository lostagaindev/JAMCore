package com.lostagain.Jam.SceneObjects;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;

import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.VariableSet;
import com.lostagain.Jam.Movements.MovementState;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.Movements.MovementState.MovementStateType;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

public class SceneObjectState {


	public static Logger Log = Logger.getLogger("JAMCore.SceneObjectState");
	

	/**
	 * This variable should be set to true if the game is running<br>
	 * in a html markup environment. ie, a GWT based implementation of the engine.<br>
	 * (A LIBGDX version or another type running in a canvas on a webpage doesn't count).<br>
	 * <br>
	 * This variable used to determine if all state objects add "Div object" to the list of supported types. <br>
	 * (as in HTML all object types are also Divs)<br>
	 * It is also used to check if CSS size's are allowed when setting sizes (ie, "50%" "33em" etc)<br>
	 * When not in html, only pixel sizes should be allowed.<br>
	 * 
	 */
	public static boolean THIS_IS_A_HTML_GAME = false; //todo; inconsistant behavour. css style applying should be allowed in libgdx etc, as its emulate aproxiomately


	/**
	 * This value is used to multiply values expected to be low with decimal points.
	 * We multiply on save and divide on load to preserve them in a INT string
	 */
	public static final double SAVE_MULTIPLIER = 10000.0;

	/** Used for serialization **/
	public final static String deliminator = "_#_";

	final static String ESCAPED_NEWLINE = "_ESCAPENL_";

	/** purely used during the setup phase, this is the string containing the parameters.
	 * This is to let the subtypes process their parameters without needing to re-strip it from the sourcefile **/
	public String ObjectsParamatersFromFile[] = null;

	/**
	 * A simple enum to specify if a parameter is specifically set to true or false,
	 * or if it should just be considered as the default value (ie, unspecified in the file)
	 * @author darkflame
	 *
	 */
	public enum TrueFalseOrDefault {
		TRUE,FALSE,DEFAULT
	}

	public enum SceneObjectParamTypes {
		minquality,
		name,
		title,
		cssname,
		boxcss,
		background,
		variables,
		properties,
		touching,
		touchingmode,
		type,
		fakeheight,
		hasglufile,
		hasmovementfile,
		pin,
		actionoveride,
		/** pointer transparent allows clicks on the Object of Origin to interact with Objects that are physically positioned beneath the Object of Origin. 
		 * Physical position underneath: in a Object covering the same area, behind the Object of Origin.
		 * Interacted Objects are cousins of one another **/
		pointertransparent,
		zindex,
		linkedzindex,
		located,
		linktype,
		visibility,
		opacity,
		/** what shall we use to determine this objects collision?**/
		collisiontype,
		pathfinding,
		objectfriction,
		bounceenergyretention,
		/** propagate visibility sets anything that is relatively positioned to the Origin Object to its state of visibility. **/	
		propagatevisibility,	
		/** capture interactions prevents clicks to go up the chain ('bubble'), towards the Parent. Even when the Object has no actions.
		 * There is no point in applying capture interactions on an Object that has pointer transparency.
		 * On Objects with Actions, it defaults to 'true'. It makes sense only to apply it on Objects that have no actions, 
		 * but should NOT interact with the Parent (like the Dragpanel) or DO require interaction with an Inventory Object.
		 *  **/
		captureinteractions
	};



	/** Objects name, used to identify it - should not be changed after creation.
	 * It should also not be used for any file locating, as it only is the same by default, and not
	 * a guaranty **/
	public String ObjectsName = "";


	/** The actual filename of the image if the object is a sprite
	 * not used if its text or dialogue <br>
	 * eg. Bookcase0.png <br>
	 * The characters before the frame number are the directory name<br>
	 * eg. Bookcase  <br>
	 * **/
	public String ObjectsFileName="";

	/** Objects scene name, used mostly to get the root folder name for the url **/
	public String ObjectsSceneName = "";

	/**
	 * This string signifier will be used in the scene name slot if a object should be present in the database
	 * but not assigned  to a scene (ie, its in the inventory).	 *
	 */
	public static final String OBJECT_HAS_NO_SCENE_STRING = "[no scene]"; //It might be worth specifying specifically if on "inventory" if there is any other ways for a object not to be on a scene?

	/** 
	 * Tool tip popup, mostly for development 
	 **/
	public String Title = ""; //TODO: Move to scenedivobjectstate as this is html specific

	/** 
	 * Its current CSS - used mostly for Div objects, but can be applied to anything
	 * Useful for drawing outlines around objects, or given them a temp background.
	 * Note; The CSS is of the overall widget on the page.
	 * This is different from the CSS applied to the text in dialogue widgets
	 * 
	 * This is kept here, rather then in subclasses specific to HTML (ie DivState) to allow non-HTML games to emulate styles
	 **/
	public String CurrentBoxCSS="";

	/** 
	 * This background string goes into the objects element tag in the dom, overriding any css set 
	 **/
	public String BackgroundString="";

	/** real, absolute, co-ordinates **/
	public int X = -2000;
	public int Y = -2000;
	public int Z = 0; //Measured relative to the floor

	/** 
	 * Objects Pin: point where the objects co-ordinates are measured from 
	 **/
//	public int PinPointX = 0;
//	public int PinPointY = 0;
//	public int PinPointZ = 0;
	public Simple3DPoint CurrentPinPoint = new Simple3DPoint(0,0,0);
	
	/**
	 * The last pinpoint set manually (ie, not from a glu file)
	 * this normally is the same as what was specified in the parameters, it is also the one thats saved/loaded in states
	 * (current will be regenerated from either the default,or the glu file if present)
	 */
	public Simple3DPoint DefaultPinPoint = new Simple3DPoint(0,0,0);
	
	/**
	 * Objects Div ID, if set, object will be attached to a page element with this ID, not
	 * the scene widget.
	 * Alternatively, if he object is a scenedialogueobject, this field will specify 
	 * what other dialogue object its contained by.
	 * If thats the case the string in this field will be of the format Item_textobject-Pintro2
	 * Where "Item_" is a fixed prefix and "textobject-Pintro2" is the text object to insert it into.	 <br>
	 **/
	public String attachToHTMLDiv = "";

	public boolean currentlyVisible = true;
	
	/**
	 * messured 0-1
	 */
	public double currentOpacity = 1.0f; //new



	/** 
	 * The primary object type.
	 * This is what its saved as, and whats used to construct it. It directly refers to the java file that
	 * implements how the object works, rather then its superclass's or interfaces 
	 ***/
	private SceneObjectType objectsPrimaryType =SceneObjectType.Sprite; // defaults to

	/**
	 * SceneObjects will in future support multiple interface types.
	 * That said, they can be classified as many types at once.
	 * A Sprite object can also do what  Div object cab, for example (when this engine is used in a website basically everything will be Div objects )
	 * By default, however, objects will just have the capability of the type they really are (which by default is sprite)
	 * 	
	 **/
	private HashSet<SceneObjectType> objectsCapabilities = Sets.newHashSet(); //empty be default



	public int getX() {
		return X + CurrentPinPoint.x;//PinPointX;
	}

	public int getY() {
		return Y + CurrentPinPoint.y;//PinPointY;
	}

	public int getZ(){
		return Z + CurrentPinPoint.z;//PinPointZ;
	}


	public int lowerZindex = 0;
	public int upperZindex = 100;	
	//temp; shouldn't need both of these as one is worked out from the other.
	public int stepZindex = 10;
	//public int stepZindexDiv = 10;

	public int zindex = -1;

	public boolean variableZindex = false;

	//if this object is positioned relatively, do we copy its zindex?
	/**NOTE: Things with a linkedZIndex should not also have a variable zIndex, as these things conflict	**/
	public boolean linkedZindex = false;

	/**NOTE: Things with a linkedZIndex should not also have a variable zIndex, as these things conflict	**/
	public int linkedZindexDifference = 0;
	//--

	/**
	 * restrict position to screen - makes sure the objects stays completely visible. 
	 * 
	 * Useful for popups and dialogues
	 * In the case of dialogues it also displaces it up/down in Y to ensure it doesnt overlap with other dialogues
	 **/
	public boolean restrictPositionToScreen = false;

	/** Determines if this object has a associated Glu file (for attachment points) **/
	public boolean hasAttachmentPointFile = false;

	/** Determines if this object has a associated movement file (for preset movements) **/
	public boolean hasMovementFile = false;


	/** object this is positioned relative too **/
	public SceneObject positionedRelativeToo;

	/** where on the parent object this is positioned relative too
	 * If this variable is left blank, we assume its the objects pin.
	 * Else the string specify the parent objects attachment point name
	 * ie "head" or "hand" **/
	public String positionedRelativeToPoint="";
	
	/**
	 * How is this object linked to the other?
	 * By default all axiss are sycned - x/y/z, but we can support just linkined in x/y 
	 * (this is useful for shadows, which always stay on the floor)
	 */
	public linktype positionedRelativeLinkType = linktype.allAxis;//default link type is x/y/z are all linked

	/** Determines if when setting this objects visibility it should also effect any objects positioned relative to it **/
	public boolean PropagateVisibility = false;


	/** Override Scene Actions, if this is true scene actions wont run, just this object specific ones and global **/
	public boolean ignoreSceneActions = false;

	/**
	 * Makes this object transparent to all pointer events, as if its not there at all. (obviously, MouseActions wont work if this is true)
	 */
	public boolean ignorePointerEvents = false;

	/** Captures and absorbs all interactions that would normally propagate to the parent object. It's an override of an earlier optimalisation that removed click listeners from Objects without Actions. **/

	public boolean forceReceiveActions = false;

	/** Now we treat pathfinding **/
	public enum pathfindingMode {
		/** give up after 15 attempts and instead just ignore collisions to get to the destination  **/
		normal, 
		/** if more then 15 attempts fail then don't move  **/
		strict  
	}

	/**
	 * If pathfinding is used, how do we treat attempts to find a path?
	 * normal or strict?	
	 */
	public pathfindingMode pathfinding = pathfindingMode.normal;

	public CollisionModeSpecs boundaryType  = new CollisionModeSpecs(CollisionType.none); //default

	
	/** starts with movement state data **/
	public Optional<MovementState> moveState =  Optional.absent();	//MovementState is created the first time its needed,rather then assuming all objects need them.
	//Having it a optional makes it safer code wise, rather then just setting it to null when it isn't used.
	
	
	
	/**
	 * The default friction ratio that determines how objects slows when on the ground
	 * @return 0 = no friction 1=friction will be equal to the value of the force pushing the objects together
	 * (typically gravity)
	 * 
	 */
	public static final SimpleVector3 DefaultFrictionalResistance = new SimpleVector3(0.5f,0.5f,0.5f); //Note; not saved.thus should not change after loading from the params
	
	/**
	 * The friction ratio that determines how this object slows when on the ground
	 * @return 0 = no friction 1=friction will be equal to the value of the force pushing the objects together
	 * (typically gravity)
	 * 
	 */
	public SimpleVector3 objectsFrictionalResistance = DefaultFrictionalResistance.copy(); //Note; not saved.thus should not change after loading from the params
	
	/**
	 * when bouncing this is how much energy is retained 
	 */
	public double bounceEnergyRetention = 0.5;
	
	/**
	 * creates a movestate for this object if it doesnt exist.
	 * This should only need to be run once, the first time any movement is used
	 */
	public void createMovementStateIfNeeded(){
		if (!moveState.isPresent()){
			moveState=Optional.of(new MovementState());			
		}
	}
	
	
	//loading data (not used once loaded)
	/** When loading if a position-by-this object doesn't yet exist, we store its name here to recheck later;**/
	public String positionRelativeToOnceLoaded ="";

	/** When loading if a cloned from an object doesn't yet exist, we store its name here to recheck later;**/
	public String clonedFromOnceLoaded ="";

	/** When loading if a spawned from an object doesn't yet exist, we store its name here to recheck later;**/
	public String spawningObjectFromOnceLoaded="";

	/**
	 * relative co-ordinates (not used unless its positioned relatively)
	 **/
	public int relX = 0;
	public int relY = 0;
	public int relZ = 0;




	/**
	 * if this object is a clone, this is its parent. This is mostly just to
	 * enable a inheritance of its actionset if we are loading from a save state
	 **/
	public SceneObject clonedFrom = null; //note; we might change these back to sceneobjects directly once both are in the jamcore 

	/**
	 * if this is dynamically created object, the object that created it is here
	 * Note; This is not the same as the object it might be cloned from eg. A
	 * clone of fire is spawned from the object that caused it (ie, paper) , but
	 * is still a clone of a fire sprite
	 **/
	public SceneObject spawningObject = null;



	/*** 
	 * The objects properties.
	 * This is a full semantically power system. You can add and remove
	 * Properties, and a test for a property will also automatically count all it subclass's **/
	public PropertySet objectsProperties = new PropertySet();

	/** storage for object variables defined and set at runtime **/
	public VariableSet ObjectRuntimeVariables = new VariableSet();

	/**
	 * A manually adjusted array of what objects are "touching" this one.
	 * NOTE: This isn't Div's touching eachother in the 2D space of the screen.
	 * This is objects in the game universe that, within that universe, are touching eachother.
	 * The idea is to allow property to spread between objects (like fire) or for objects touching eachother to
	 * be able to effect eachother. (ie, the character is standing in the water when the electric cable hits it...)
	 * This lets games be intelligent somewhat as to what is touching what even if the 3d landscape is faked.
	 */
	//	public HashSet<String> touching = new HashSet<String>(); //NOTE: Currently only saved/loaded/processed by sprite objects
	//The generic getAllFieldsAsArrayList should be adjusted to include it at the end, and the deserialisation made to match.
	public SceneObjectSet touching = new SceneObjectSet();


	public enum touchingMode {
		
		/** objects are considered touching only if they are manually specified to be */
		Manual,
		
		/** the objects considered to be touching this auto-updates based on collision maps after every movement
		 * NOTE: you can still also update manually, but bare in mind whats added manually might be removed 
		 * automatically after each movement if its not touching by collision maps.
		 * (this makes manually adding only really useful for updates mid-movement)
		 * 
		 * Automatic touching data is updated when the scene is loaded - therefor it is not used in comparisons
		 * in save states if this mode is set.
		 * **/
		Automatic 
	}
	
	public touchingMode ObjectsTouchingUpdateMode = touchingMode.Automatic; //automatic at the moment, we will allow manual in future;




	/** used when loading this objects data, it flags as true if this
	 * item specifies a higher quality then the game settings are set too.
	 * So, if this is true the object should not be made or placed on the scene,
	 * and no references too it will be there for variables or properties, it simple
	 * would never have existed! **/
	public boolean DONT_USE_THIS_OBJECT = false;



	/**
	 * create a new state with the default values
	 */
	public SceneObjectState(){
		setObjectsPrimaryType(SceneObjectType.SceneObject); //everything is a sceneobject as well as other things, so we assign this first and overwrite if needed

	}

	/** constructs a new SceneObjectState from the supplied states data**/
	public SceneObjectState(SceneObjectState sourceData){
		setObjectsPrimaryType(SceneObjectType.SceneObject); //everything is a sceneobject as well as other things, so we assign this first and overwrite below

		this.setGenericObjectData(sourceData);

	}

	/**
	 * constructs a sceneobjectstate from all its fields....thats a lot!:
	 * 
	 * @param objectsName
	 * @param objectsFileName
	 * @param objectsSceneName
	 * @param title
	 * @param cssname
	 * @param backgroundstring
	 * @param x
	 * @param y
	 * @param restrictPositionToScreen
	 * @param usesAttachmentPointFile
	 * @param usesMovementFile
	 * @param pinPointX
	 * @param pinPointY
	 * @param currentlyVisible
	 * @param currentType
	 * @param zindex
	 * @param variableZindex
	 * @param zIndexLower
	 * @param zIndexUpper
	 * @param zIndexStep
	 * @param linkedZindex
	 * @param linkedZindexDifference
	 * @param positionedRelativeToo
	 * @param positionedRelativeToPoint
	 * @param relX
	 * @param relY
	 * @param ms
	 * @param objectsProperties
	 * @param ObjectRuntimeVariables
	 */




	public SceneObjectState(
			String objectsName,
			String objectsFileName,
			String objectsSceneName,
			String title, 
			String cssname,
			String backgroundstring,
			int x, int y,  int z,
			boolean restrictPositionToScreen,
			boolean usesAttachmentPointFile,
			boolean usesMovementFile,
			int pinPointX, int pinPointY,  int pinPointZ,
			boolean currentlyVisible,
			double currentOpacity,
			boolean PropagateVisibility, 
			boolean ignoreSceneActions,
			boolean ignorePointerEvents,
			boolean forceReceiveActions,
			pathfindingMode pathfinding,
			CollisionModeSpecs boundaryType, 
			SceneObjectType currentType, 
			int zindex, 
			boolean variableZindex,
			int zIndexLower,
			int zIndexUpper,
			int zIndexStep,
			boolean linkedZindex,
			int linkedZindexDifference,
			SceneObject positionedRelativeToo,
			String positionRelativeToOnceLoaded,
			String positionedRelativeToPoint, 
			linktype positionedRelativeLinkType,
			int relX, int relY,	int relZ,
			Optional<MovementState> ms,
			PropertySet objectsProperties,
			VariableSet ObjectRuntimeVariables,
			SceneObjectSet touching) {




		ObjectsFileName =objectsFileName;
		ObjectsName = objectsName;
		ObjectsSceneName = objectsSceneName;
		Title = title;
		CurrentBoxCSS=cssname;
		BackgroundString=backgroundstring;
		X = x;
		Y = y;
		Z=z;
		this.restrictPositionToScreen = restrictPositionToScreen;
		hasAttachmentPointFile = usesAttachmentPointFile;
		hasMovementFile = usesMovementFile;

	//	PinPointX = pinPointX;
	//	PinPointY = pinPointY;
	//	PinPointZ = pinPointZ;
		
		DefaultPinPoint.x =pinPointX;
		DefaultPinPoint.y =pinPointY;
		DefaultPinPoint.z =pinPointZ;
		
		//current matches default, by, eh, default
		CurrentPinPoint.set(DefaultPinPoint);
				
				
		this.currentlyVisible = currentlyVisible;
		this.currentOpacity = currentOpacity; 
		this.PropagateVisibility =PropagateVisibility; 
		this.ignoreSceneActions=ignoreSceneActions;
		this.ignorePointerEvents=ignorePointerEvents;
		this.forceReceiveActions=forceReceiveActions;
		this.pathfinding=pathfinding;
		this.boundaryType=boundaryType;

		setObjectsPrimaryType(SceneObjectType.SceneObject); //everything is a sceneobject as well as other things, so we assign this first and overight below
		setObjectsPrimaryType(currentType); //we start by setting the primary/only type with this. Subclasses might add to this type

		this.zindex = zindex;
		this.variableZindex = variableZindex;
		this.lowerZindex = zIndexLower;
		this.upperZindex = zIndexUpper;
		this.stepZindex = zIndexStep;

		this.linkedZindex = linkedZindex;
		this.linkedZindexDifference=linkedZindexDifference;

		this.positionedRelativeToo = positionedRelativeToo;
		this.positionRelativeToOnceLoaded = positionRelativeToOnceLoaded;
		this.positionedRelativeToPoint = positionedRelativeToPoint;
		this.positionedRelativeLinkType = positionedRelativeLinkType;
		
		this.relX = relX;
		this.relY = relY;		
		this.relZ = relZ;

		//copy over movement state data
		this.moveState = ms;


		this.objectsProperties = objectsProperties;
		Log.info("setting new variables for "+ObjectsName+" of which there are:"+ObjectRuntimeVariables.size()+"");

		this.ObjectRuntimeVariables = ObjectRuntimeVariables;
		this.touching=touching;

	}


	public SceneObjectState(String parametersFromFile[],boolean InterpretAsIniData){
		setObjectsPrimaryType(SceneObjectType.SceneObject); //everything is a sceneobject as well as other things, so we assign this first and overwrite below


		//first extract the universal object data
		this.extractGenericData(parametersFromFile);
	}

	/**
	 * creates a object from serialized data (ie, whats in a save string)
	 * at the moment it will just set the options type to whats specified at the start of the string (ie, before the first deliminator)
	 * @param serialised
	 */

	public SceneObjectState(String serialised) {
		setObjectsPrimaryType(SceneObjectType.SceneObject); //everything is a sceneobject as well as other things, so we assign this first and overwrite below

		//ensure type is set to what it starts with
		String typeString = serialised.split(deliminator,2)[0];
		SceneObjectType type = SceneObjectType.valueOf(typeString);
		this.objectsPrimaryType = type;

	}



	//TODO: if we already have subtypes, why not do this and the specific data within the same function?
	//This class splits it, and calls a "processParameterLine" function which is overriden by subclasses?
	/**
	 * extras object data from parameters (ie, what's in the scene file)
	 * 
	 * @param Parameters
	 */
	public void extractGenericData(String Parameters[]) {
		
		//Extract data
		Log.info("Extracting universal object data from:" + Parameters);

		// now split again by new lines to get parameter data
		String itemslines[] = Parameters;//.split("\n");
		Log.info("Itemslines:" + itemslines.length);

		this.ObjectsParamatersFromFile = itemslines;  //we save the parameters lines as subclass might use this data for fields specific to their subtype (ie, anything not dealt with here)

		int currentlinenum = 0;

		SceneObjectType ObjectsTypeSpecified = null; //temp variable we save the objects type in rather then setting direct. (this lets us set a default if none have been specified by the time we reach the end)

		while (currentlinenum < itemslines.length) {

			String currentline = itemslines[currentlinenum].trim();

			currentlinenum++;
			if ((currentline.length() < 3) || (currentline.startsWith("//"))) {
				continue;
			}

			// split by =
			String[] paramBits = currentline.split("=");
			String   param = paramBits[0].trim();
			String   value = paramBits[1].trim();

			//find the enum for the param
			//if none is found then we just continue straight away - as its a parameter for a specific object type rather then generic parameter
			//(it could also have been a typo though)
			SceneObjectParamTypes currentParamType; 
			try {
				currentParamType =  SceneObjectParamTypes.valueOf(param.toLowerCase());
			} catch (IllegalArgumentException e) {

				//	Log.warning(" Parameter Type : "+param+" not found. Either its not a generic param, or its a typo");				
				continue;
			}



			//if we find a quality parameter
			//we test it straight away. If the games quality setting is 
			//less then it, then we abort making this item with a flag that it shouldn't
			//be used.

			if (currentParamType == SceneObjectParamTypes.minquality) {
				//if (param.equalsIgnoreCase("MinQuality")) {
				Log.info("Item specifies quality of :" + value);

				int val = Integer.parseInt(value);
				int gamequality = Integer.parseInt(JAMcore.Quality);
				if (gamequality < val)
				{
					Log.info("Game quality too low for this item");
					DONT_USE_THIS_OBJECT = true;

					return;
				}



			}


			String[] params = value.split(",");

			// assign data			
			switch (currentParamType) {
			case name:
				ObjectsName = value;
				Log.info("Objects name set to:" + ObjectsName);
				break;
			case title:
			{
				//only set a title if the value is big enough
				if (value.trim().length()>2){

					//process for variable strings
					value = JAMcore.SwapCustomWords(value);
					value = JAMcore.parseForTextIDs(value);

					Title = value;

					Log.info("Objects Title set to:" + Title);
				}
				break;
			}
			case cssname:
				CurrentBoxCSS = value;
				Log.info("Objects css set to:"+ CurrentBoxCSS);
				break;
			case boxcss:
				CurrentBoxCSS = value;
				Log.info("Objects CurrentBoxCSS set to:"+ CurrentBoxCSS);
				break;
			case background:
				BackgroundString = value;
				Log.info("Objects BackgroundString set to:"+ value);
				break;
			case variables:
				ObjectRuntimeVariables = new VariableSet(value); //auto deserialises itself
				Log.info("Objects variables set to:" + ObjectRuntimeVariables.toString());
				break;
			case properties:
			{
				String propertys[] = value.toLowerCase().split(",");
				objectsProperties = new PropertySet(propertys);
				Log.info("Objects Properties set to:" + propertys.length);
				break;
			}
			case touching:
			{
				String propertys[] = params;

				//touching = new HashSet<String>(	Arrays.asList(propertys) );	
				touching.fromNameCollection(propertys);

				Log.info("Objects touching set to:"	+ touching.tempObjectnames);
				break;
			}
			case touchingmode:
			{
				//ObjectsTouchingUpdateMode
				if (value.equalsIgnoreCase("manual")){
					ObjectsTouchingUpdateMode = touchingMode.Manual;
				} else {
					ObjectsTouchingUpdateMode = touchingMode.Automatic;
				}
				
			}
			case type:
			{

				if (value.equalsIgnoreCase("TextBox")) {

					ObjectsTypeSpecified = SceneObjectType.DialogBox; //we save the type now and set it at the end. This ensures we can add the default sprite type if we are sure no other types are set

					//this.setObjectsPrimaryType(SceneObjectType.DialogBox);


				} else if (  (value.equalsIgnoreCase("Text")) || (value.equalsIgnoreCase("Label")) )  {
					ObjectsTypeSpecified = SceneObjectType.Label;

					//	this.setObjectsPrimaryType(SceneObjectType.Label);


				} else if (value.equalsIgnoreCase("Div")) {

					ObjectsTypeSpecified = SceneObjectType.Div;

					//this.setObjectsPrimaryType(SceneObjectType.Div);


				} else if (value.equalsIgnoreCase("Vector")) {

					ObjectsTypeSpecified = SceneObjectType.Vector;

					//this.setObjectsPrimaryType(SceneObjectType.Vector);
				} else if (value.equalsIgnoreCase("Input")) {

					ObjectsTypeSpecified = SceneObjectType.Input;

					//	this.setObjectsPrimaryType(SceneObjectType.Input);
				} else {


					ObjectsTypeSpecified = SceneObjectType.Sprite;

					//this.setObjectsPrimaryType(SceneObjectType.Sprite);
				}
				Log.info("Objects Type set to:" + objectsPrimaryType.toString());
				break;
			}
			case fakeheight:
			{
				String value1 = value;
				int fh = Integer.parseInt(value1);
				Z = fh;
				Log.info("Objects FakeHeight set to:" +Z);
				break;
			}
			case hasglufile:
			{
				Boolean glue = Boolean.parseBoolean(value);
				hasAttachmentPointFile=glue;
				Log.info("Objects attachment point use:" + hasAttachmentPointFile);
				break;
			}
			case hasmovementfile:
			{
				Boolean mov = Boolean.parseBoolean(value);
				hasMovementFile=mov;
				Log.info("Objects movement file use:" + hasMovementFile);
				break;
			}
			case pin:
			{
				String value1 = params[0];
				String value2 = params[1];
				int X = Integer.parseInt(value1);
				int Y = Integer.parseInt(value2);
				
				DefaultPinPoint.x = X;
				DefaultPinPoint.y = Y;
				DefaultPinPoint.z = 0;
				
				if (value.length()==3){

					String value3 = params[2];
					int Z = Integer.parseInt(value3);
					DefaultPinPoint.z = Z; //optional

				}
				
				CurrentPinPoint.set(DefaultPinPoint);	//set current to default
				
				Log.info("Default pin set to:" + CurrentPinPoint.toString());
				Log.info("Current pin set to:" + CurrentPinPoint.toString());
				
			}
			break;
			case actionoveride:
				ignoreSceneActions  = Boolean.parseBoolean(value);
				Log.info("ignore scene actions set to:"+ignoreSceneActions);
				break;
			case pointertransparent:
				ignorePointerEvents  = Boolean.parseBoolean(value);
				Log.info("ignorePointerEvents set to:"+ignorePointerEvents);
				break;
			case captureinteractions:
				forceReceiveActions  = Boolean.parseBoolean(value);
				Log.info("forceReceiveActions set to:"+forceReceiveActions);
				break;
			case zindex:
			{
				String bits[] = params;
				if (bits.length == 1) {
					variableZindex = false;
					zindex = Integer.parseInt(bits[0].trim());

					Log.info("zindex set too--" + zindex);
				}
				if (bits.length == 3) {
					lowerZindex = Integer.parseInt(params[0].trim());
					upperZindex = Integer.parseInt(params[1].trim());
					double div = Integer.parseInt(params[2].trim());

					stepZindex = (int) Math.round((upperZindex - lowerZindex) / div);
					variableZindex = true;

					Log.info("Got zindex =" + lowerZindex + ">" + upperZindex + " " + stepZindex);
				}
			}
			break;
			case linkedzindex:
				linkedZindex = true;
				linkedZindexDifference =  Integer.parseInt(value.trim());
				Log.info("linking "+ObjectsName+" to "+positionedRelativeToo);
				Log.info("zindex different =  "+linkedZindexDifference);
				break;
			case located:
			{
				Log.info("Value is "+param +" with "+params.length+" s");

				//first work out if the first parameter is a number or not
				Integer firstparam = Ints.tryParse(params[0]);

				if (firstparam!=null){
					// if its a number then this are positioned absolutely
					// x,y,[z]  (3 co-ordinates, optional z)
					Log.info("(positioning absolutely)");

					//	String xRelString = firstparam;		
					String yRelString = params[1];	

					X = firstparam;
					Y = Integer.parseInt(yRelString);

					if ( params.length == 3) {
						String zRelString = params[2];		
						Z = Integer.parseInt(zRelString);						
					}



				} else {
					//if its a word then this look for if there's any more params

					//if there's no more parameters, then its a DivID			
					if (params.length <= 1) {

						attachToHTMLDiv = value.trim();
						Log.info("Objects Location set to a div:" + attachToHTMLDiv);

					} else {

						//if there is parameters then the first param is the object its positioned relative too
						String positionRelativeToThisString = params[0];	

						//detect if a attachment point is specified
						if (positionRelativeToThisString.contains("(")){

							Log.info("attachment point found");
							//get the attachment point name							
							String attachmentPointName = positionRelativeToThisString.split("\\(")[1];
							//remove ending )
							attachmentPointName=attachmentPointName.substring(0, attachmentPointName.length()-1).trim();

							Log.info("attachment point found="+attachmentPointName);

							positionedRelativeToPoint = attachmentPointName;
							//Separate the object name
							positionRelativeToThisString = positionRelativeToThisString.split("\\(")[0];
							//Log.info("value1= "+value1);
						}



						//SceneObject positionRelativeToThis = SceneObjectDatabase.getSingleSceneObjectNEW(positionRelativeToThisString,null,true);
						positionedRelativeToo = SceneObjectDatabase.getSingleSceneObjectNEW(positionRelativeToThisString,null,true);


						String xRelString = params[1];		
						String yRelString = params[2];	

						/*
						X = Integer.parseInt(xRelString);
						Y = Integer.parseInt(yRelString);

						if ( params.length == 4) {
							String zRelString = params[3];		
							Z = Integer.parseInt(zRelString);						
						}

						relX = X;
						relY = Y;
						relZ = Z;*/


						relX = Integer.parseInt(xRelString);
						relY = Integer.parseInt(yRelString);
						relZ = 0;
						if ( params.length == 4) {
							String zRelString = params[3];		
							relZ = Integer.parseInt(zRelString);						
						}

						//	setupRelativePosition(positionRelativeToThisString, positionRelativeToThis);
						if (positionedRelativeToo==null){


							Log.warning("object"+ positionRelativeToThisString+" to position relative too not found");
							Log.warning("assigning values to position later (untested)");

							positionRelativeToOnceLoaded = positionRelativeToThisString;


						}

					}



				}
			}
			break;
			case linktype:
				positionedRelativeLinkType = new linktype(value);
				Log.info("link type is:"+value);				
				break;
			case pathfinding:
				pathfinding = pathfindingMode.valueOf(value);
				Log.info("pathfinding  set to:"+ pathfinding );
				break;
			case collisiontype:
				boundaryType = CollisionModeSpecs.parseString(value);
				Log.info("collisiontype  set to:"+ boundaryType );
				break;
			case objectfriction:
				
				objectsFrictionalResistance = new SimpleVector3(value); 
				//Double.parseDouble(value);
				Log.info("defaultObjectFriction  set to:"+ objectsFrictionalResistance );
				
				break;
			case bounceenergyretention:
				bounceEnergyRetention = Double.parseDouble(value);
				Log.info("bounceEnergyRetention:"+ bounceEnergyRetention );				
				break;
			case visibility:
				currentlyVisible = Boolean.parseBoolean(value);
				Log.info("Visibility  set to:"+ currentlyVisible );
				break;
			case opacity:
				currentOpacity = Double.parseDouble(value);
				Log.info("Opacity  set to:"+ currentOpacity );
				break;
			case propagatevisibility:
				PropagateVisibility  = Boolean.parseBoolean(value);
				Log.info("Objects PropagateVisibility  set to:"+ PropagateVisibility );
				break;
			}



		}

		//set objects type now we know for sure if none have been specified
		if (ObjectsTypeSpecified==null){
			this.setObjectsPrimaryType(SceneObjectType.Sprite); //default to sprite if it hasnt been set to anything specific
		} else {
			this.setObjectsPrimaryType(ObjectsTypeSpecified);

		}

		//---
		//Now we have all the params we setup the relative position
		Log.info("setting up RelativePosition data");

		setupRelativePosition();


	}

	/**
	 * This should be run as part of the sceneobjectstate paramater loading<br>
	 * Specifically after we know for sure both the relative variables (relX,relY,relZ) and
	 * the pinpoints (PinPointX,PinPointY,PinPointZ)<br>
	 * 
	 * @param positionRelativeToThisString
	 * @param positionRelativeToThis
	 */
	private void setupRelativePosition() {
				
		
		
		if (positionedRelativeToo!=null){

			Log.info("Setting relative to :::"	+ positionedRelativeToo.getObjectsCurrentState().ObjectsName+":"+positionedRelativeToPoint);

			//positionedRelativeToo = positionRelativeToThis;

			//if theres a glupoint we should use
			final String posTo = positionedRelativeToPoint;

			int RX=0;
			int RY=0;
			int RZ=0;

			if (posTo.isEmpty()){

				//get by the pin
				RX = positionedRelativeToo.getX();
				RY = positionedRelativeToo.getY();
				RZ = positionedRelativeToo.getZ();

			} else {
				
				//get by the attachment point
				MovementWaypoint glueToo = positionedRelativeToo.getAttachmentPointsFor(posTo);

				if (glueToo!=null){
					RX = positionedRelativeToo.getTopLeftBaseX()+ glueToo.pos.x;
					RY = positionedRelativeToo.getTopLeftBaseY()+ glueToo.pos.y;
					RZ = positionedRelativeToo.getTopLeftBaseZ()+ glueToo.pos.z;
				} else {
					//we have to default to pin as there's no attachment of the specified name
					RX = positionedRelativeToo.getX();
					RY = positionedRelativeToo.getY();	
					RZ = positionedRelativeToo.getZ();	
				}
			}

			//now work out the real X/Y/Z. While these arnt used to position the object when on relative
			//mode, they are used for collision map features							
			X = RX+ relX - CurrentPinPoint.x;
			Y = RY+ relY - CurrentPinPoint.y;
			//if ( params.length == 4) {
			Z = RZ+ relZ - CurrentPinPoint.z;
			//}
			
			//update relative z-index
			if (linkedZindex){				
				zindex = positionedRelativeToo.getObjectsCurrentState().zindex+linkedZindexDifference;	
				Log.info("Relative zindexd on "+ObjectsName+" set to "+zindex);
				
			}
			
		} else {

			X = X - CurrentPinPoint.x;
			Y = Y - CurrentPinPoint.y;
			Z = Z - CurrentPinPoint.z;		

		}
		
		
		
		
		/*else {

			Log.warning("object"+ positionRelativeToThisString+" to position relative too not found");
			Log.warning("assigning values to position later (untested)");

			positionRelativeToOnceLoaded = positionRelativeToThisString;

		}*/
	}

	/** method should be overriden by subclasses **/ 
	public String serialiseToString() {

		// first we get all the global fields.
		// That is, ones that apply to both Spites,Text and any other object types.
		ArrayList<Object> fieldList =  getAllFieldsAsArrayList();

		String serialised = "";

		serialised = SceneObjectState.serialiseTheseFields(fieldList);

		Log.info("__________________________________serialised as:\n" + serialised);

		return serialised;
	}

	/**
	 * Serialis's an arraylist of objects, converting each one to a string based on their type
	 * This is a critical part of saving a game, as this string should contain all the information needed
	 * to recreate a object in its current state.
	 * 
	 * @param fieldList - an arraylist of all the objects fields. (Name,Title,X,Y,PinX,PinY etc)
	 * 
	 * @return
	 */
	static public String serialiseTheseFields(ArrayList<Object> fieldList) {
		// loop over array to serialise

		String serialised = "";
		Log.info("total fields:"+fieldList.size());

		for (Object object : fieldList) {

			String field = "";


			//if its null we just add the deliminator and skip to the next object in the fieldlist
			if (object == null) {
				serialised = serialised + deliminator;
				continue;
			}


			//now we check if the object is a sceneobject of some sort
			//we do this by checking all the classes, and superclasses, of the object for a match			
			Class<? extends Object> testclass = object.getClass();

			while(testclass!=null){
				if (testclass.equals(SceneObject.class)){ //Visual
					//if it is a sceneobjectvisual then we can safely cast to it, and then save the name in the serialized state string
					field = ((SceneObject) object).getName();
					break;
				}
				testclass = testclass.getSuperclass();
			}

			//if a field was found above while looping, we add to the serialized string here and continue to the next object
			if (!field.isEmpty()) {		
				serialised = serialised + field + deliminator;
				continue;
			} 




			//else we start checking the object against other types of class's
			if (object.getClass().equals(PropertySet.class)) {
				//	Log.info("property set");

				field = object.toString();

			} else if (object.getClass().equals(SceneObjectSet.class)) {

				field = ((SceneObjectSet)object).toSerialisedString();
				Log.info("____________________________________________________SceneObjectSet set:"+field);

			} else if (object.getClass().equals(HashSet.class)) {
				//	Log.info("HashSet set");

				field = object.toString();

				//note; this is crude we need a gwt way to check any ancestor is SceneObjectVisual
			} 
			/*
			else if (object.getClass().getSuperclass().getSuperclass().equals(SceneObjectVisual.class)) {
				//	Log.info("SceneObject set from subclass object ");

				field = ((SceneObject) object).objectsCurrentState.ObjectsName;

			} else if (object.getClass().getSuperclass().equals(SceneObjectVisual.class)) {
				//	Log.info("SceneObject set from subclass object ");

				field = ((SceneObject) object).objectsCurrentState.ObjectsName;

			} else if (object.getClass().equals(SceneObjectVisual.class)) {
				//Log.info("SceneObject set");

				field = ((SceneObject) object).objectsCurrentState.ObjectsName;

			} 
			 */
			else if (object.getClass().equals(Boolean.class)) {

				//	Log.info("boolean set : ");
				field = ((Boolean) object).toString();

			} else {

				field = String.valueOf(object);
				// Log.info("String or int set");
			}

			if (field != null) {
				serialised = serialised + field + deliminator;
			} // else {
			// serialised = serialised + deliminator;
			// }

		}

		//	 Log.info("__________________________________serialised as:"
		// + serialised);

		//now we have looped ober all the fields we return the string we formed from them
		return serialised;
	}


	// seralisable class (not sure if needed anymore, I think was old expirements in serailisation)
	/*
	class sString{
		public String seraliseThis="";

		public sString(String seraliseThis) {
			super();
			this.seraliseThis = seraliseThis;
		}

		public String getSeraliseThis() {
			return seraliseThis;
		}

		public void setSeraliseThis(String seraliseThis) {
			this.seraliseThis = seraliseThis;
		}



	}*/

	/**
	 * Deserialises the data by splitting the string on each {@value#deliminator}
	 * then looping over it loading each item in its specific order. First generic parameters (specified by SceneObjectData)
	 * then the specific ones for subclasses and sub-subclasses
	 * @param serialised
	 */
	public void deserialise(String serialised) {
		String[] data = serialised.split(deliminator, -1);

		// assign data

		// list its easier to manage
		List<String> incomingdatalist = (Arrays.asList(data));

		Log.info("__________________________________Deserialising. Total fields:"+ incomingdatalist.size());		
		Iterator<String> incomingdata = incomingdatalist.iterator();


		//load parameters from incomingdata
		loadParameters(incomingdata);

	};

	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions.
	 * Subclasses should override this to load their own parameters from the incoming data,
	 * first calling super.loadparameters to load the generic ones	 * 
	 */	
	protected void loadParameters(Iterator<String> incomingdata) {

		//we skip the first as thats the objects type
		incomingdata.next();

		Log.info("loading global object data");

		ObjectsSceneName= incomingdata.next();
		ObjectsName = incomingdata.next();
		Title = incomingdata.next(); 
		CurrentBoxCSS = incomingdata.next(); 
		BackgroundString = incomingdata.next(); 

		X = Integer.parseInt(incomingdata.next());
		Y = Integer.parseInt(incomingdata.next());
		Z = Integer.parseInt(incomingdata.next());

		restrictPositionToScreen =  Boolean.parseBoolean(incomingdata.next());
		attachToHTMLDiv = incomingdata.next(); 

		DefaultPinPoint.x = Integer.parseInt(incomingdata.next());
		DefaultPinPoint.y = Integer.parseInt(incomingdata.next());
		DefaultPinPoint.z = Integer.parseInt(incomingdata.next());
		
		//current matches default, by, eh, default
				CurrentPinPoint.set(DefaultPinPoint);
				
				
		//moveState.loadParameters(incomingdata);		
		//New:
		//if the movementstate is anything other then none, we create a movementstate and set it
		//If not, we set the movementstate as absent
		MovementStateType currentmovementtype = MovementStateType.valueOf(incomingdata.next());
		// we only use the rest if we have a setting other then none
		if (currentmovementtype==MovementStateType.None){
			moveState=Optional.absent();
		} else {
			this.createMovementStateIfNeeded();
			moveState.get().loadParameters(currentmovementtype,incomingdata);
		}



		currentlyVisible =  Boolean.parseBoolean(incomingdata.next());
		currentOpacity   =  Double.parseDouble(incomingdata.next());
				
		zindex = Integer.parseInt(incomingdata.next());
		lowerZindex = Integer.parseInt(incomingdata.next());
		upperZindex = Integer.parseInt(incomingdata.next());
		stepZindex = Integer.parseInt(incomingdata.next());
		variableZindex = Boolean.parseBoolean(incomingdata.next());
		linkedZindex = Boolean.parseBoolean(incomingdata.next());
		linkedZindexDifference = Integer.parseInt(incomingdata.next());

		//Log.info("got zindex.." + zindex);

		String objectToPosRelativeToo = incomingdata.next();

		if (objectToPosRelativeToo.length() > 2) {


			//	positionedRelativeToo = SceneWidget
			//		.getSceneObjectByName(objectToPosRelativeToo,null)[0];


			positionedRelativeToo =  SceneObjectDatabase
					.getSingleSceneObjectNEW(objectToPosRelativeToo,null,true);


			positionRelativeToOnceLoaded="";

			if (positionedRelativeToo==null){
				Log.info("waiting to position relative to:"+objectToPosRelativeToo);
				positionRelativeToOnceLoaded=objectToPosRelativeToo;
			}

		}

		relX = Integer.parseInt(incomingdata.next());
		relY = Integer.parseInt(incomingdata.next());
		relZ = Integer.parseInt(incomingdata.next());


		String objectClonedFrom = incomingdata.next();

		if (objectClonedFrom.length() > 2) {

			Log.info("Cloned from.."+objectClonedFrom);

			//we dont flag missing errors as the object might not exist yet to clone from
			//clonedFrom = SceneWidget.getSpriteObjectByName(objectClonedFrom,null,false)[0];

			//	clonedFrom = SceneWidget.getSceneObjectByName(objectClonedFrom,null)[0];

			clonedFrom =  SceneObjectDatabase
					.getSingleSceneObjectNEW(objectClonedFrom,null,true);

			if (clonedFrom==null){

				Log.info("Cloned source not found ("+objectClonedFrom+") setting cloneFromOnceLoaded string to finnish the job latter");

				clonedFromOnceLoaded = objectClonedFrom; //if the object is not present we store its string at least.
				//once loaded this string should be swapped with the object
			}

		}


		String objectSpawnedFrom = incomingdata.next();

		if (objectSpawnedFrom.length() > 2) {

			Log.info("Spawned from.."+objectSpawnedFrom);

			//we suppress the missing object warnings, as if its missing we just remember
			//the name and it will be checked again when loading is done
			//spawningObject = SceneWidget.getSpriteObjectByName(objectSpawnedFrom,null,false)[0];

			spawningObject = SceneObjectDatabase
					.getSingleSceneObjectNEW(objectSpawnedFrom,null,true);

			//if (curobjectrot.size()>1){
			//	Log.info("Warning: more then one object found for "+objectSpawnedFrom+". This is clearly an error, as we can only be spawned from one object");
			//}

			//spawningObject = curobjectrot.iterator().next();

			if (spawningObject==null){
				spawningObjectFromOnceLoaded = objectSpawnedFrom; //if the object is present we store its string at least.
				//once loaded this string should be swapped with the object
			}
		}

		//used to be specific to spites 
		String newobjectsProperties = incomingdata.next();

		Log.info(" New Properties: "+newobjectsProperties);

		if (!newobjectsProperties.isEmpty()) {

			newobjectsProperties = newobjectsProperties.substring(0,
					newobjectsProperties.length());

			//	Log.info("properties = " + newobjectsProperties);

			String[] objectsPropertiesArray = newobjectsProperties.split(",");

			Log.info(objectsPropertiesArray.length + " properties ");
			if (objectsProperties == null) {
				objectsProperties = new PropertySet();
			}
			objectsProperties.clear();

			// split arrays
			for (String prop : objectsPropertiesArray) {

				Log.info("prop = " + prop.trim());
				// store
				objectsProperties.add(prop.trim());
			}

			Log.info("properties added ");

		}

		String newobjectsVaaribles = incomingdata.next();
		Log.info("newobjectsVaribles = "+newobjectsVaaribles);

		if (!newobjectsVaaribles.isEmpty()) {
			ObjectRuntimeVariables = new VariableSet(newobjectsVaaribles);
		}


		String newtouching = incomingdata.next();

		Log.info("Getting "+this.ObjectsName+" touching properties from: "+newtouching);
		touching.fromSerialisedString(newtouching);


		/*
		if (!newtouching.isEmpty()) {

			if (touching == null) {
				touching = new HashSet<String>();

			}
			touching.clear();

			// crop start and end off []
			newtouching = newtouching.substring(1, newtouching.length() - 1);

			Log.info("touching = " + newtouching);

			String[] touchingArray = newtouching.split(",");

			if (touchingArray.length > 0) {
				// split arrays
				for (String string : touchingArray) {

					Log.info("touch = " + string.trim());
					// store
					touching.add(string.trim());

				}
			}

		}*/


	}


	/** used for serialization; It gets all the fields as a arraylist
	 * 
	 * Make sure that all the fieldlists are in exactly the same order as the ones in the deserialise section.
	 * When making a new property, also assure adding it to this list.
	 * 
	 **/ 
	protected ArrayList<Object> getAllFieldsAsArrayList() {	

		// used for serialisation
		ArrayList<Object> fieldList = new ArrayList<Object>();

		Log.info("getting global object fields.");

		// general object data
		fieldList.add(objectsPrimaryType);
		fieldList.add(ObjectsSceneName);
		fieldList.add(ObjectsName);	
		fieldList.add(Title); 	
		fieldList.add(CurrentBoxCSS); 	
		fieldList.add(BackgroundString);

		fieldList.add(X);
		fieldList.add(Y);
		fieldList.add(Z);

		fieldList.add(restrictPositionToScreen);
		fieldList.add(attachToHTMLDiv);

		fieldList.add(DefaultPinPoint.x);//should be defaults
		fieldList.add(DefaultPinPoint.y);
		fieldList.add(DefaultPinPoint.z);

		if (moveState.isPresent()){
			fieldList.addAll(moveState.get().getAllFieldsAsArrayList());
		} else {
			fieldList.add(MovementStateType.None.name());
		}
		
		//--

		//fieldList.add((int)(moveState.movement_speed*SAVE_MULTIPLIER)); //we x10000.0 on saving and divide by it on loading. This lets us store a small decimal value in a int (ie, values like 0.05 dont get rounded to 0)


		fieldList.add(currentlyVisible);
		fieldList.add(currentOpacity); //new
		
		fieldList.add(zindex);
		fieldList.add(lowerZindex);
		fieldList.add(upperZindex);
		fieldList.add(stepZindex);
		fieldList.add(variableZindex);
		fieldList.add(linkedZindex);
		fieldList.add(linkedZindexDifference);


		if ((positionedRelativeToo==null) && (positionRelativeToOnceLoaded.length()>2)){
			Log.info("________________________adding to field list rel once loaded:" + positionedRelativeToo);			
			fieldList.add(positionRelativeToOnceLoaded);
		} else {
			Log.info("________________________adding to field list rel:" + positionedRelativeToo);
			fieldList.add(positionedRelativeToo);
		}

		fieldList.add(relX);
		fieldList.add(relY);
		fieldList.add(relZ);

		fieldList.add(clonedFrom);
		fieldList.add(spawningObject);

		if (clonedFrom!=null){
			Log.info("________________________getting clonedFrom field.." + clonedFrom.getObjectsCurrentState().ObjectsName);
		}

		if ((objectsProperties != null) && (!objectsProperties.isEmpty())) {
			fieldList.add(objectsProperties);
		} else {
			fieldList.add(null);
		}

		if ((ObjectRuntimeVariables != null) && (!ObjectRuntimeVariables.isEmpty())) {
			fieldList.add(ObjectRuntimeVariables);
		} else {
			fieldList.add(null);
		}

		//NEW We now add touching objects here
		if ((touching != null) && (!touching.isEmpty())) {
			Log.info("adding touching field to set");
			fieldList.add(touching);
		} else {	
			fieldList.add(null);
		}


		Log.info("got all "+fieldList.size()+" global object fields.");

		return fieldList;
	}

	/** This string should match the above **/
	public String serilisationHelperString() {

		String deliminator=",";

		String helperString = "currentType"
				+deliminator+"ObjectsSceneName"
				+deliminator+"ObjectsName"
				+deliminator+"Title" 	
				+deliminator+"CurrentBoxCSS" 	
				+deliminator+"BackgroundString"

				+deliminator+"X"
				+deliminator+"Y"				
				+deliminator+"Z"

				+deliminator+"restrictPositionToScreen"
				+deliminator+"attachToDiv"

				+deliminator+"PinPointX"
				+deliminator+"PinPointY"
				+deliminator+"PinPointZ"

			+deliminator+"moveState.currentmovement"
			+deliminator+"moveState.movement_currentWaypoint"

			+deliminator+"moveState.movement_SX"
			+deliminator+"moveState.movement_SY"

			+deliminator+"moveState.movement_CX"
			+deliminator+"moveState.movement_CY"

			+deliminator+"moveState.movement_DX"
			+deliminator+"moveState.movement_DY"

			+deliminator+"moveState.movement_StepX"
			+deliminator+"moveState.movement_StepY"

		+deliminator+"moveState.movement_currentX"
		+deliminator+"moveState.movement_currentY"
		+deliminator+"moveState.movement_currentZ"

		+deliminator+"moveState.movement_speed"

		+deliminator+"currentlyVisible"

		+deliminator+"zindex"
		+deliminator+"lowerZindex"
		+deliminator+"upperZindex"
		+deliminator+"stepZindex"
		+deliminator+"variableZindex"
		+deliminator+"linkedZindex"
		+deliminator+"linkedZindexDifference";


		if ((positionedRelativeToo==null) && (positionRelativeToOnceLoaded.length()>2)){
			helperString=helperString+"positionRelativeToOnceLoaded";
		} else {
			helperString=helperString+"positionedRelativeToo";
		}

		helperString=helperString+"relX"
				+deliminator+"relY"

				+deliminator+"relZ"
				+deliminator+"clonedFrom"
				+deliminator+"spawningObject";

		if ((objectsProperties != null) && (!objectsProperties.isEmpty())) {
			helperString=helperString+"objectsProperties";
		} else {
			helperString=helperString+"null";
		}


		if ((ObjectRuntimeVariables != null) && (!ObjectRuntimeVariables.isEmpty())) {
			helperString=helperString+"ObjectRuntimeVariables";
		} else {
			helperString=helperString+"null";
		}

		if ((touching != null) && (!touching.isEmpty())) {
			helperString=helperString+"Touching objects";
		} else {
			helperString=helperString+"null";
		}

		return helperString;
	}



	/**
	 *  copies all the generic object data to this SceneObjectState from the supplied source data
	 * **/
	public void setGenericObjectData(SceneObjectState sourceData) {

		ObjectsName = sourceData.ObjectsName;
		ObjectsSceneName = sourceData.ObjectsSceneName;
		ObjectsFileName=sourceData.ObjectsFileName;
		Title = sourceData.Title;

		X = sourceData.X;
		Y = sourceData.Y;
		Z = sourceData.Z;

		CurrentBoxCSS = sourceData.CurrentBoxCSS;
		BackgroundString= sourceData.BackgroundString;

		positionedRelativeToo = sourceData.positionedRelativeToo;
		positionRelativeToOnceLoaded = sourceData.positionRelativeToOnceLoaded;
		positionedRelativeToPoint = sourceData.positionedRelativeToPoint;
		positionedRelativeLinkType = sourceData.positionedRelativeLinkType;

		
		PropagateVisibility = sourceData.PropagateVisibility;

		Log.info("sourceData of "+ObjectsName+" has visibility:"+sourceData.currentlyVisible+" and opacity:"+sourceData.currentOpacity);
		currentlyVisible = sourceData.currentlyVisible;
		currentOpacity = sourceData.currentOpacity;
		
		Log.info(ObjectsName+"s original data for currentlyVisible1:"+currentlyVisible);

		ignoreSceneActions = sourceData.ignoreSceneActions;
		ignorePointerEvents= sourceData.ignorePointerEvents;

		forceReceiveActions = sourceData.forceReceiveActions;
		pathfinding         = sourceData.pathfinding;
		boundaryType        = sourceData.boundaryType;

		relX = sourceData.relX;
		relY = sourceData.relY;
		relZ = sourceData.relZ;


		attachToHTMLDiv = sourceData.attachToHTMLDiv;

		objectsPrimaryType = sourceData.objectsPrimaryType;

		restrictPositionToScreen = sourceData.restrictPositionToScreen;
		hasAttachmentPointFile = sourceData.hasAttachmentPointFile;
		hasMovementFile = sourceData.hasMovementFile;

		DefaultPinPoint.x = sourceData.DefaultPinPoint.x;
		DefaultPinPoint.y = sourceData.DefaultPinPoint.y;
		DefaultPinPoint.z = sourceData.DefaultPinPoint.z;
		//current matches default, by, eh, default
		CurrentPinPoint.set(DefaultPinPoint);
		

		variableZindex = sourceData.variableZindex;

		lowerZindex = sourceData.lowerZindex;
		upperZindex = sourceData.upperZindex;
		stepZindex = sourceData.stepZindex;

		zindex = sourceData.zindex;
		stepZindex = sourceData.stepZindex;
		upperZindex = sourceData.upperZindex;
		lowerZindex = sourceData.lowerZindex;

		linkedZindex = sourceData.linkedZindex;
		linkedZindexDifference = sourceData.linkedZindexDifference;

		Log.info("storing zindex as:" + zindex);


		moveState = sourceData.moveState;

		objectsProperties = sourceData.objectsProperties;
		ObjectRuntimeVariables = sourceData.ObjectRuntimeVariables;

		//umm...do we need to add variables here?
		/*
		if (moveState.isPresent()){
			moveState.get().movement_current_pos.x = X;
			moveState.get().movement_current_pos.y = Y;
			moveState.get().movement_current_pos.z = Z;
		}*/

		touching = sourceData.touching;


		Log.info(ObjectsName+"s currentlyVisible1:"+currentlyVisible);

		//Lastly we copy the raw parameters (only needed during set up, to pass the objects parameters to subtypes
		//That might need more variables set beyond just the generic stuff
		ObjectsParamatersFromFile = sourceData.ObjectsParamatersFromFile;

	}



	//TODO: sameStateAs is a real pain with its current implementation
	//So we need to do a manual compare of each field instead
	//More painful, but would be much easier to see things matching correctly
	//(we could at least have a Unit test to ensure that all fields are compared somehow? If possible? maybe)
	/**
	 * 
	 * Compares if the specified state represents the same state as this one. <br>
	 * This compares each field one at a time, returning false on the first one that does not match.<br>
	 * <br>
	 * NOTE: absolute position of objects are not compared if both states are relatively positioned 
	 * (ie, when attached to a object that can be the same state if their relative positions are the same, even if 
	 * their parent object is at a different position)<br>
	 * <br>
	 * We also ignore zindex if we are set for relative zindex for similiar reasons <br>
	 * Oh, and differences in Touching objects are ignored if touching is set to auto. (as its overwritten on loading anyway)<br>
	 * 
	 * @param state
	 * @return
	 */
	public boolean sameStateAs(SceneObjectState state){

		Log.info("Checking generic object data is the same between these two states");

		//---Check name and type first---------
		if (!ObjectsName.equals(state.ObjectsName)){
			return false;
		}
		if (objectsPrimaryType != state.objectsPrimaryType){
			return false;
		}
		//-------------------------------------

		if (!ObjectsSceneName.equals(state.ObjectsSceneName)){
			return false;
		}
		if (!ObjectsFileName.equals(state.ObjectsFileName)){
			return false;
		}
		if (!Title.equals(state.Title)){
			return false;
		}
		if (currentlyVisible != state.currentlyVisible){
			return false;
		}
		if (currentOpacity != state.currentOpacity){
			return false;
		}

		if (positionedRelativeToo != state.positionedRelativeToo){
			return false;
		}

		//We ignore the absolute co-ordinates if we are relative
		if (positionedRelativeToo==null || positionRelativeToOnceLoaded.isEmpty()){
			Log.info("Checking position object data is the same between these two states");

			if (X != state.X){
				return false;
			}
			if (Y != state.Y){
				return false;
			}
			if (Z != state.Z){
				return false;
			}
		}  else {
			Log.info("Not checking position object data as both states are relative");			
		}

		if (!positionRelativeToOnceLoaded.equals(state.positionRelativeToOnceLoaded)){
			return false;
		}
		if (!positionedRelativeToPoint.equals(state.positionedRelativeToPoint)){
			return false;
		}
		if (!positionedRelativeLinkType.equals(state.positionedRelativeLinkType)){
			
			return false;
		}

		if (relX != state.relX){
			return false;
		}
		if (relY != state.relY){
			return false;
		}
		if (relZ != state.relZ){
			return false;
		}

		if (!attachToHTMLDiv.equals(state.attachToHTMLDiv)){
			return false;
		}

		if (restrictPositionToScreen != state.restrictPositionToScreen){
			return false;
		}


		Log.info("Checking various states");	

		if (!CurrentBoxCSS.equals(state.CurrentBoxCSS)){
			return false;
		}
		if (!BackgroundString.equals(state.BackgroundString)){
			return false;
		}		

		if (PropagateVisibility != state.PropagateVisibility){
			return false;
		}

		Log.info("Checking various states..");	


		if (ignoreSceneActions != state.ignoreSceneActions){
			return false;
		}
		if (ignorePointerEvents != state.ignorePointerEvents){
			return false;
		}
		
		if (bounceEnergyRetention  != state.bounceEnergyRetention){
			return false;
		}

		Log.info("Checking various states....");	

		//todo: whats not matching here?
		if (forceReceiveActions != state.forceReceiveActions){
			return false;
		}

		Log.info("Checking pathfinding various states.......");	
		if (pathfinding != state.pathfinding){
			return false;
		} else {
			Log.info("Checking "+pathfinding+" various states......."+state.pathfinding);	
		}

		Log.info("Checking various states..........");	

		if (!boundaryType.equals(state.boundaryType)){
			return false;
		} else {
			Log.info("Checking "+boundaryType+" various states......."+state.boundaryType);	
		}

		Log.info("Checking movement and attachment point object data is the same between these two states");

		if (!moveState.equals(state.moveState)){
			return false;
		}
		if (hasAttachmentPointFile != state.hasAttachmentPointFile){
			return false;
		}
		if (hasMovementFile != state.hasMovementFile){
			return false;
		}

		if (DefaultPinPoint.x != state.DefaultPinPoint.x){
			return false;
		}
		if (DefaultPinPoint.y != state.DefaultPinPoint.y){
			return false;
		}
		if (DefaultPinPoint.z != state.DefaultPinPoint.z){
			return false;
		}

		Log.info("Checking zindex object data is the same between these two states");

		if (variableZindex != state.variableZindex){
			return false;
		}

		if (lowerZindex != state.lowerZindex){
			return false;
		}
		if (upperZindex != state.upperZindex){
			return false;
		}
		if (stepZindex != state.stepZindex){
			return false;
		}

		//Only check z-index if both arnt variable
		if (	     variableZindex==true 
				&& state.variableZindex==true){			
		} else {
			if (zindex != state.zindex){
				return false;
			}
		}

		if (stepZindex != state.stepZindex){
			return false;
		}
		if (upperZindex != state.upperZindex){
			return false;
		}
		if (lowerZindex != state.lowerZindex){
			return false;
		}

		if (linkedZindex != state.linkedZindex){
			return false;
		}
		if (linkedZindexDifference != state.linkedZindexDifference){
			return false;
		}




		if (!objectsProperties.equals(state.objectsProperties)){
			return false;
		}

		if (!ObjectRuntimeVariables.equals( state.ObjectRuntimeVariables)){
			return false;
		}

		//we don't check the touching data if both states are on auto
		if (             ObjectsTouchingUpdateMode == touchingMode.Automatic
				&& state.ObjectsTouchingUpdateMode == touchingMode.Automatic){
			//Don't check
		} else {

			if (!touching.contentsMatchs(state.touching)){
				return false;
			}

		}


		Log.info("All generic object data is the same between these two states");

		return true;

	}

	/**
	 * Compares too states (which have to the same type) to see if they are equilivent to eachother.
	 * This method should be overridden in the subglasses to deal with their own individual attributes.
	 * 
	 * We could get compare the serialized states of two objects too see if the strings match, but this wont take some things into account like;
	 *  - states are considered the same even if they have varying locations IF the objects are positioned relatively and the relative position matchs 
	 *  
	 * 
	 * 
	 * @param objectsInitialState
	 * @return
	 */
	public boolean sameStateAs_GenericClassMethod(SceneObjectState compareToThis) {

		Log.info("comparing:"+this.ObjectsName+" to "+compareToThis.ObjectsName);
		Log.info("testing all fields in object against all fields in other object");


		//first we get its fields
		ArrayList<Object> fieldListToCompare = compareToThis.getAllFieldsAsArrayList();

		//Then we get our own fields
		ArrayList<Object> thisObjectsFieldsList = this.getAllFieldsAsArrayList();


		//if its positioned relatively we exclude X/Y from the checks, the easiest way to do this is to remove them from both arraylists
		//Note; remember the arrays need to be kept insycn for this to work.
		if (this.positionedRelativeToo!=null || !this.positionRelativeToOnceLoaded.isEmpty()){			
			Log.info("object positioned relatively, so ignoring x/y/z position in comparison");
			fieldListToCompare.remove((Object)X); //Note; we have to cast to an object here as we want to remove the object in the list of that number. If we just ask for "5" (for example) without casting it assumes we want to remove the 5th item.(that is, the one at position 5)
			thisObjectsFieldsList.remove((Object)compareToThis.X);

			fieldListToCompare.remove((Object)Y);
			thisObjectsFieldsList.remove((Object)compareToThis.Y);	

			fieldListToCompare.remove((Object)Z);
			thisObjectsFieldsList.remove((Object)compareToThis.Z);	
		}




		//we do a last check to ensure their lengths are the same, if they are not something is wrong above
		if (fieldListToCompare.size()!=thisObjectsFieldsList.size()){
			Log.severe("miss-matched comparison lengths when checking states are identical. Its likely that unchanged objects will now save making save files bigger then needed");
			return false;
		}

		// loop over every object testing if its the same.
		// all field lists should have the same order. This is vital for save serialization to work		

		Iterator<Object> compareToIt = fieldListToCompare.iterator();
		Iterator<Object> thisIT      = thisObjectsFieldsList.iterator();

		Log.info("Starting to test fields against eachother");


		while (thisIT.hasNext()) {

			Object co = (Object) thisIT.next();      //get current object to test from this state
			Object ta = (Object) compareToIt.next(); //get object to test against this state


			//Log.info("testing field");

			if (co==null && ta!=null){
				Log.severe("Warning: Asymetrical Nullness Error!"); //Did I just invent a new error type? whoa.		
				Log.severe("Non none field was: "+ta.toString()); 
				return false;
			}

			if (co!=null && ta==null){
				Log.severe("Warning: Asymetrical Nullness Error!!"); //Did I just invent a new error type? whoa.		
				Log.severe("Non none field was: "+co.toString()); 
				return false;
			}

			if (co==null && ta==null){
				//both null so continue (maybe an unset title?)
				continue;
			}

			//Test for same type (should ALWAYS match if not nulls)
			if (co.getClass() != ta.getClass()){
				Log.severe("Warning: Types dont match when testing if states match");
				return false;
			}

			//SceneObjectSet
			if (co.getClass().equals(SceneObjectSet.class)) {
				//sceneobjectset needs to do its own special comparison, as objectsmight not be loaded yet
				//This special check thus states if the objectnames are equal if the objects themselves havnt been loaded
				//into one of the sets

				SceneObjectSet a = ((SceneObjectSet)co);
				SceneObjectSet b = ((SceneObjectSet)ta);

				if (!a.contentsMatchs(b)){
					return false;
				} else {
					continue;
				}
			}


			//Generic equals test (does not work on properties and other arraylists?)
			if (co.equals(ta)){
				//	Log.info("Field:"+co.toString()+" matchs "+ta.toString()); //log spam remove				
				continue;
			} else {
				Log.info("Field:"+co.toString()+" is different from field "+ta.toString()); //log spam remove
				return false;
			}


			//test for specific problems


			/*




			Log.info("testing field type="+co.getClass());

			//test for same value NOTE; a lot of these can be replaced with a general ".equals()" function as that works for almost all classes

			//if its a enum class, we just test for match
			if (co.getClass().equals(SceneObjectType.class)) {

				if (!(co==ta)){
					Log.info("(objects have different SceneObjectTypes");
					return false;
				}
				continue;

			}
			//if its a string class, we just test for matching strings
			if (co.getClass().equals(String.class)) {

				if (!(((String)co).equalsIgnoreCase(((String)ta)))){
					Log.info("(objects have different strings");
					return false;
				}
				continue;

			}

			//test for a simple int match
			 if (co.getClass().equals(Integer.class)) {
				 if (!co.equals(ta)){
					 return false;
				 }			
				 continue;

			}
			 if (co.getClass().equals(Double.class)) {
				 if (!co.equals(ta)){
					 return false;
				 }			
				 continue;

			}
			//test for matching propertys
			if (co.getClass().equals(PropertySet.class)) {
				//Note; just comparing strings for now as property sets output to string in a comparable way
				//If we adjust the property class to have its own equals function though it would probably be quicker to use that
				if (!co.toString().equals(ta.toString())){
					Log.info("(objects have different properties");
					return false;
				}
				continue;

		    } 

			//test for matching hashsets
			 if (co.getClass().equals(HashSet.class)) {

				 HashSet a = ((HashSet)co);
				 HashSet b = ((HashSet)ta);

				 if (!a.equals(b)){					 
					Log.info("(objects have different hashset values?");
					 return false;
				 }
				 continue;

			}

			 //test for matching sceneobject types (superclass because sceneobject is a superclass of dialogue,sprite,text etc
			 //used in stuff like positioning, where we id the object tis positioned against
			 if (co.getClass().getSuperclass().equals(SceneObject.class)) {

				 if (!co.equals(ta)){
					 Log.info("(objects have different superclass values?");
					 return false;
				 }
				 continue;

			} 
			 if (co.getClass().equals(SceneObject.class)) {

				 if (!co.equals(ta)){
					 return false;
				 }
				 continue;

			} 
			 //test for a simple boolean match
			 if (co.getClass().equals(Boolean.class)) {
				 if (!co.equals(ta)){
					 return false;
				 }			
				 continue;

			}

			 Log.severe("Warning: Type not found");

			 */

		}


		Log.info("compared:"+this.ObjectsName+" to "+compareToThis.ObjectsName);
		Log.info("(states seem identical between objects: "+this.ObjectsName+" and "+compareToThis.ObjectsName+")");

		return true;
	}

	/**
	 * Should return the generic parameters in the same state as in the JAM file.
	 * Please test before using.
	 * In most situations you should use the overwritten function by the subtype, which in turn calls this one.
	 * 
	 * Note; still very wip, doesnt match real jam files yet. This should exactly match what the loadParameters function understands as input
	 * 
	 * @return a string with this states parameters
	 **/
	public String getParametersAsCode(){

		String GenericParameters = "\n";

		//Cant add minimum quality yet as we dont store it!
		//GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.minquality,minQuality);

		//only add a type setting if we arnt sprite, as thats the default
		if (!this.isCompatibleWith(SceneObjectType.Sprite)){		//new method, tests for not being a sprite
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.type,objectsPrimaryType.toString());
		}

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.title,Title);

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.name,ObjectsName);

		//Note; CSS has to be dealt with in the subtypes because dialogue deals with it different to the rest
		//GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.cssname,CurrentBoxCSS);
		//GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.boxcss,CurrentBoxCSS);

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.background,BackgroundString);

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.variables,ObjectRuntimeVariables.toString());

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.properties,objectsProperties.toString());

		//---phase out;
		if (Z!=0){ //only add it if its not the default value
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.fakeheight,""+Z);
		}
		//---

		if (hasAttachmentPointFile){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.hasglufile,""+hasAttachmentPointFile);
		}
		if (hasMovementFile){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.hasmovementfile,""+hasMovementFile);
		}
		if (DefaultPinPoint.x!=0 || DefaultPinPoint.y !=0 || DefaultPinPoint.z != 0){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.pin,DefaultPinPoint.toString());
		}
		if (ignoreSceneActions){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.actionoveride,""+ignoreSceneActions);
		}
		if (ignorePointerEvents){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.pointertransparent,""+ignorePointerEvents);
		}
		if (forceReceiveActions){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.captureinteractions,""+forceReceiveActions);
		}

		if (pathfinding!=pathfindingMode.normal){ //if not default
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.pathfinding,""+pathfinding);
		}
		if (!boundaryType.isDefaultSettings()){ //if not default
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.collisiontype,""+boundaryType.toString());
		}
		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.zindex,""+zindex);

		//TODO: add objectfriction selectively if its not the default for this object type
		if (!objectsFrictionalResistance.equals(DefaultFrictionalResistance)){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.objectfriction,""+objectsFrictionalResistance);
		
			
		}

		
		if (linkedZindex){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.linkedzindex,""+linkedZindex);
		}

		GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.located," (broken) "+X+","+Y+","+Z);
		if (PropagateVisibility){
			GenericParameters=GenericParameters+getParameterCodeLine(SceneObjectParamTypes.propagatevisibility,""+PropagateVisibility);
		}

		return GenericParameters;

	}

	private String getParameterCodeLine(SceneObjectParamTypes param, String Value){
		//ensure its a real value else return nothing
		if (Value!=null && Value.length()<1){
			return "";
		}

		String paramline =param.toString()+" = "+Value+"\n";

		return paramline;
	}

	/**
	 * Returns a copy of this object using all the variables set in this state, making copys of them where needed
	 **/
	public SceneObjectState copy(){
		
		
		Log.info("copying generic state ");
		
		//first if we have movements we make a copy
		//else we just make a new absent movestate reference
		Optional<MovementState> movementCopy = Optional.absent();
		if (moveState.isPresent()){		
			movementCopy= Optional.of(moveState.get().copy());
			
		}
		
		
		
		SceneObjectState newState = new SceneObjectState(
				ObjectsName,
				ObjectsFileName,
				ObjectsSceneName,
				Title, 
				CurrentBoxCSS,
				BackgroundString,
				X,  Y,  Z,
				restrictPositionToScreen,
				hasAttachmentPointFile,
				hasMovementFile,
				DefaultPinPoint.x,  DefaultPinPoint.y, DefaultPinPoint.z, //change to default
				currentlyVisible,
				currentOpacity,
				PropagateVisibility,
				ignoreSceneActions,
				ignorePointerEvents,
				forceReceiveActions, 
				pathfinding,
				boundaryType, 
				objectsPrimaryType, 
				zindex, 
				variableZindex,
				lowerZindex,
				upperZindex,
				stepZindex,
				linkedZindex,
				linkedZindexDifference,
				positionedRelativeToo,
				positionRelativeToOnceLoaded,
				positionedRelativeToPoint, 
				positionedRelativeLinkType,
				relX, relY,	relZ,
				movementCopy,
				//moveState.copy(),
				new PropertySet(objectsProperties),
				new VariableSet(ObjectRuntimeVariables),
				new SceneObjectSet(touching)
				);


		
		return newState;		

	}


	/**
	 * Returns true if one of this objects types matchs the one given
	 **/
	public boolean isCompatibleWith(SceneObjectType thisType) {		
		return objectsCapabilities.contains(thisType);
	}

	/**
	 * returns the hashmap storing the objects capabilities.
	 * Its capabilities are all the object types that make up this object.
	 * 
	 *  ie. A Sprite might have the capabilities of a Sprite and a Div, because in a webbrowser implementation
	 *  everything is also a Div
	 * A DialogueBox object meanwhile might be both Dialogue, Text and Div
	 * 
	 * 
	 * Do not edit this list, only read from it.
	 * @return
	 */
	public HashSet<SceneObjectType> getObjectCapabilities() {
		return objectsCapabilities;
	}


	/**
	 * Returns a comma separated string of all the capabilities a object has
	 * This is to help debug objects and to ensure they are the expected type
	 **/
	public String getCapabilitiesAsString() {		

		String capabilitiesString = "";

		for (SceneObjectType capability : objectsCapabilities) {
			capabilitiesString=capabilitiesString+capability+",";
		}

		return capabilitiesString;
	}



	/**
	 * Returns the primary type of this object. This is the type used to construct it, save and load it.<br>
	 * Use isCompatibleWith() to check if it can do the same stuff as another type<br>
	 * ie if it can do what a html object can.<br>
	 * 
	 * This directly corresponds to what its inherited functions or interfaces are<br>
	 * <br>
	 * Current examples of types this can return are;<br>
	 * <br>
	 * Sprite, <br>
		DialogBox, <br>
		Label,<br>
		Div,<br>
		Input, <br><br>
	 * 
	 * 
	 * see; SceneObjectType<br>
	 * 
	 *  @return
	 */
	public SceneObjectType getPrimaryObjectType() {
		return objectsPrimaryType;
	}

	/**
	 * WIP: SceneObjects can have multiple types.
	 * This command sets an objects primary type and adds that type to its capabilitys list
	 * 
	 * @param objectsPrimaryType
	 */
	public void setObjectsPrimaryType(SceneObjectType objectsNewType) {
		objectsPrimaryType = objectsNewType;
		//objectsCapabilities.clear();
		objectsCapabilities.add(objectsNewType);
	}

	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		Log.info("Assigning generic object parameters (placeholder, as theres none");

	}

	/**
	 * Loops over the supplied parameters for a sceneobject and returns the objecttype it defines.
	 * (We and thus use this to construct the correct state type straight away, rather then making a generic state first)
	 * 	 *  
	 * @param itemslines
	 * @return
	 */
	public static SceneObjectType getTypeFromParameters(String[] itemslines) {
		SceneObjectType ObjectsTypeSpecified = null; //temp variable we save the objects type in rather then setting direct. (this lets us set a default if none have been specified by the time we reach the end)
		Log.info("itemslines:"+itemslines.length);
		
		for (String currentline : itemslines) {
			currentline=currentline.trim(); //trim first in case the line is nothing but whitespace
			if ((currentline.length() < 3) || (currentline.startsWith("//"))) {
				continue;
			}
			Log.info("processing line:"+currentline);
			
			String[] lineBits = currentline.split("=");
			
			String valuetype = lineBits[0].trim();
			String value     = lineBits[1].trim();
			
			if (valuetype.equalsIgnoreCase("type")) {

				if (value.equalsIgnoreCase("TextBox")) {

					ObjectsTypeSpecified = SceneObjectType.DialogBox; //we save the type now and set it at the end. This ensures we can add the default sprite type if we are sure no other types are set

				} else if (  (value.equalsIgnoreCase("Text")) || (value.equalsIgnoreCase("Label")) )  {

					ObjectsTypeSpecified = SceneObjectType.Label;

				} else if (value.equalsIgnoreCase("Div")) {

					ObjectsTypeSpecified = SceneObjectType.Div;


				} else if (value.equalsIgnoreCase("Vector")) {

					ObjectsTypeSpecified = SceneObjectType.Vector;

				} else if (value.equalsIgnoreCase("Input")) {

					ObjectsTypeSpecified = SceneObjectType.Input;

				} else {

					ObjectsTypeSpecified = SceneObjectType.Sprite;

				}

				break;
			}

		}


		if (ObjectsTypeSpecified==null){
			return SceneObjectType.Sprite;//default to sprite
		}

		return ObjectsTypeSpecified;
	}
	

	/**
	 * is this positioned relative to this object, or relative to one that is
	 * 
	 * @param associatedObject
	 * @return
	 */
	public boolean isRelativeToAncestor(SceneObject associatedObject) {
		
		if (positionedRelativeToo!=null){
			if (positionedRelativeToo==associatedObject){
				return true;
			}
		
			if (positionedRelativeToo.getObjectsCurrentState().isRelativeToAncestor(associatedObject)){
				return true;
			}
		}
		
		return false;
	}


}
