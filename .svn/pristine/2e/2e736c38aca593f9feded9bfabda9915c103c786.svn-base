package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSProperty;
import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.CollisionMap.PolySide;
import com.lostagain.Jam.CollisionMap.Polygon;
import com.lostagain.Jam.CollisionMap.PolygonCollisionMap;
import com.lostagain.Jam.CollisionMap.SceneCollisionMap;
import com.lostagain.Jam.CollisionMap.SpiffyPolygonCollision;
import com.lostagain.Jam.Factorys.IsJamSoundObject;
import com.lostagain.Jam.Factorys.SoundFactory;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.InstructionProcessing.ActionSet;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.InstructionProcessing.ActionSet.Trigger;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.Movements.MovementList;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementState;
import com.lostagain.Jam.Movements.MovementState.MovementStateType;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.SaveMangement.JamSaveGameManager;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.Scene.SceneMenuWithPopUp;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;
import com.lostagain.Jam.SceneObjects.SceneObjectState.touchingMode;

import lostagain.nl.spiffyresources.client.spiffycore.FramedAnimationManager;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;
import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

import com.lostagain.Jam.SceneObjects.Helpers.SpriteObjectHelpers;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDivObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneInputObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneLabelObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneVectorObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.KeepHeldMode;

/**
 * Home of ALL core (non-visual non-GWT/LibGDX) SceneObject functions.<br>
 * All sceneobject types should ultimately extend this class, supplying the required methods.<br>
 * ie.<br>
 * SceneSpriteObject>>SceneObjectVisual>>SceneObject<br>
 * <br>
 * SceneObjectVisual supply's the needed methods this core class lacks (ie, the visual stuff only GWT or GDX implementations can supply)<br>
 * Then SceneSpriteObject supplys the specific functions for a sprite object.<br>
 * 
 * 
 ***/
public abstract class SceneObject implements HasDeltaUpdate, IsSceneObject {

	public static Logger	SOLog								= Logger.getLogger("JAMCore.SceneObject");

	// We have implemented a new detailed system so we know precisely
	// what bits of the sceneobject still have to load
	// or are not needed. This is used to know when the object is logically
	// loaded. This means its ready to place on the scene, and run code
	// but might not have its visual appearance yet.
	boolean					CollisionMapLoadedOrNotNeeded		= false;									// done
	boolean					AttachmentPointsLoadedOrNotNeeded	= false;									// done (default glu only)
	boolean					MovementFilesLoadedOrNotNeeded		= false;									// done
	// Note; all the above must become true for the object - and scene its on -
	// to finish loading.
	// -----------------------------------------------------------

	/**
	 * For sprite objects only
	 * 
	 * @param SpriteObject
	 * @param URL
	 * @param animationManager
	 */
	protected static void updateAnimationEndActionsHELPER(final SceneObject SpriteObject, String URL,
			FramedAnimationManager animationManager) {
		final CommandList actions = SpriteObject.objectsActions.getActionsForTrigger(TriggerType.OnAnimationEnd, URL);
		// FramedAnimationManager animationManager = SceneObjectIcon.animation;

		if (actions != null && actions.size()>0) {

			final String CurrentSceneObjectNaame = SpriteObject.objectsCurrentState.ObjectsFileName;

			// final SceneObject CurrentSceneObject = this;

			SOLog.info("____________________animation end actions for    " + URL);
			SOLog.info("____________________animation end actions found: " + actions.toString());

			final SceneWidget thisObjectsScene = SpriteObject.getParentScene();

			Runnable runThis = new Runnable() {
				@Override
				public void run() {

					SOLog.info("updating after open or close");

					String spritesScene = "";
					if (thisObjectsScene != null) { // used to just use
						// objectScene before it
						// went protected
						spritesScene = thisObjectsScene.SceneFileName; // objectScene.SceneFileName;
					} else {
						spritesScene = "sceneless"; // has no parent scene
						// (probably inventory item)
					}

					InstructionProcessor.processInstructions(actions,
							"FROMAniEnd_" + spritesScene + "_" + CurrentSceneObjectNaame, SpriteObject);

				}
			};

			SpriteObject.ObjectsLog("adding " + actions.size() + " actions to run after open for url:" + URL);
			animationManager.setCommandToRunAfterOpen(runThis);

			// We probably need this too, so it runs when we open or close
			SpriteObject.ObjectsLog("adding " + actions.size() + " actions to run after close for url:" + URL);
			animationManager.setCommandToRunAfterClose(runThis);

			SpriteObject.ObjectsLog("Actions added were: " + actions.toString() + "");

		} else {
			SpriteObject.ObjectsLog("No action s found for end of animation:" + URL);
			animationManager.clearRunthisAfterClose();
			animationManager.clearRunthisAfterOpen();
			
		}
	}

	/**
	 * 
	 * @return - 0=params, 1=actions
	 */
	public static String[] splitActionsAndParams(String rawParameterAndActionData){
		SOLog.info("extracting action data from:\r" + rawParameterAndActionData);

		// split actions off if present
		// get scene parameters (anything before a line with : in it)
		//int firstLocOfColon = rawParameterAndActionData.indexOf(':');

		//anything before a line _ending_ with :
		String[] bits = rawParameterAndActionData.split(":[\\s]*\\n",2);
		String allactions = "";

		if (bits.length==1) {

			bits[0]=rawParameterAndActionData;
		} else {
			//Unfortunately we need to get the whole linestart before the : so we look where the colon was and search back
			String paramssuncropped = bits[0];
			String actionsuncropped = bits[1];
			int linebeforeColon = paramssuncropped.lastIndexOf('\n', paramssuncropped.length());

			allactions = rawParameterAndActionData.substring(linebeforeColon);

			bits[0]=rawParameterAndActionData.substring(0,linebeforeColon);			
			bits[1]=allactions;

			//SOLog.info("allactions:\n" + bits[1]);

		}


		return bits;
	}

	/**
	 * strips the actions out of the parameter and data string This should be
	 * probably be moved elsewhere. Probably back when the parameters are split?
	 * Then we give this class the actions directly? (we should however, remove
	 * all references to the raw strings after loading else memory would be
	 * wasted)
	 */
	public static ActionList splitActionsFromParametersAndActions(String rawParameterAndActionData) {

		String[] bits=splitActionsAndParams(rawParameterAndActionData);
		String allactions="";

		SOLog.info("params:\n" + bits[0]);
		if(bits.length==2){
			SOLog.info("allactions:\n" + bits[1]);
			allactions=bits[1];
		}
		//old method;
		/*

		SOLog.info("LocOfColon recieved for  object:" + firstLocOfColon);

		// String Parameters = "";
		String allactions = "";
		if (firstLocOfColon == -1) {
			// Parameters = rawParameterAndActionData;
		} else {
			int linebeforeColon = rawParameterAndActionData.lastIndexOf('\n', firstLocOfColon);
			allactions = rawParameterAndActionData.substring(linebeforeColon);
		}
		 */
		if (allactions.length() > 0) {
			// Log.info("actions are:" + allactions);
			return new ActionList(allactions);

		} else {
			return null;
		}

	}

	/**
	 * folder name is by default just the first file names given name, minus the
	 * 0 and the png. This is for internal use only - and can be recovered from
	 * the initial filename
	 ***/
	public String			folderName		= "";

	/**
	 * The current, up to date data for the object
	 **/
	public SceneObjectState	objectsCurrentState;

	/***
	 * The default state of an object is how its specified in the file.<br>
	 * Or at the time it was first created (if it was, for example, a clone)
	 * <br>
	 * 
	 ***/
	public SceneObjectState	initialObjectState;

	/**
	 * A temp state of the object. When fully implemented - which it almost is -
	 * we can use this for a fast save/restore function. So, for example, on
	 * entering a scene it saves, and when the user messs up badly, they can
	 * reset
	 **/
	public SceneObjectState	tempObjectState;

	/**
	 * Indicates if this object is already loaded. (ie, ran its onfirstload)
	 ***/
	public boolean			alreadyLoaded	= false;

	/** Indicates if this object is in edit mode **/
	public static boolean	editMode		= false;

	/**
	 * Scene this object is on. Subclass's of this should also have a copy with
	 * a subclass of SceneWidget they use for the implementation
	 **/
	public SceneWidget		objectScene;

	/***
	 * Gets the scene the object is on
	 ***/
	public SceneWidget getParentScene() {
		return objectScene;
	}

	// -----------------------------------------------action related variables
	/**
	 * The objects list of Actions (that is, actions the object does under
	 * certain conditions, <br>
	 * ie, being on fire, being clicked etc)<br>
	 * Also item mixs, however, objects also do tests.<br>
	 * global Scene triggers apply <br>
	 * first then object actions
	 **/
	public ActionList						objectsActions		= new ActionList();

	// action sets (strictly speaking we can get this information from the
	// actionlist above, but by copying it to these variables once on loading
	// it means we save the effort of having to look them up each time)
	//
	protected ActionSet						actionsToRunForMouseOver;
	protected ActionSet						actionsToRunForMouseOut;
	protected HashSet<ActionSet>			actionsToRunForMouseClick;
	protected ActionSet						actionsToRunForMouseDoubleClick;
	protected ActionSet						actionsToRunForMouseRightClick;
	protected ActionSet						actionsToRunForWhenATouchingObjectChanges;
	protected ActionSet						actionsToRunOnFirstLoad;

	protected ActionSet						actionsToRunOnEveryReload;
	protected ActionSet						actionsToRunWhenCloned;
	protected ActionSet						actionsToRunOnDirectionChange;

	// this one is a map because there can be many onstep actions
	// each one with its own step interval, and count for that interval 
	public class onStepInfo {
		public int interval = 0;
		public int currentStep=0;
		public onStepInfo(int interval) {
			super();
			this.interval=interval;
		}
		
	}
	
	public HashMap<ActionSet, onStepInfo>	actionsToRunOnStep	= new HashMap<ActionSet, onStepInfo>();
	
	//
	
	int										stepActionIterval	= -1;
	public boolean							movementsLoaded		= false;
	
	/**
	 * all the paths this object could animated over. MovementPaths are
	 * currently 2D only.
	 **/	
	public MovementList						objectsMovements;

	boolean									skipFirstJump		= false;

	public boolean							isMoving_old		= false;

	// fade and opacity
	// double Opacity = 100; //now in state
	Runnable								afterFadeIn;
	Runnable								afterFadeOut;
	double									stepPerMS;

	// Timer moveme;

	/**
	 * In some situations we need to move objects up a set amount temporarily to
	 * avoid other objects We dont want to change the canonical Y, so we instead
	 * keep a temp one.
	 */
	public Optional<Integer>				tempYOverride		= Optional.absent();

	// movement
	int										DestinationX		= 0;
	int										DestinationY		= 0;

	int										timeStep			= 100;

	// -----------
	boolean									telePortFlag;

	/** keep track of pixel rounding errors during movements **/
	SimpleVector3 remainder = new SimpleVector3(0,0,0);
	//double									remainderX			= 0;
	//double									remainderY			= 0;
	//double									remainderZ			= 0;

	// dynamic zindex settings (should be recalculated on loading or scene
	// change)
	int										numberOfDivisions	= -1;
	int										pixelstep			= -1;

	/**
	 * Override Scene Actions, if this is true scene actions wont run, just this
	 * objects ones
	 **/
	// boolean ignoreSceneActions = false;

	// int zIndex = -1;

	// z-index functions
	int										nextThreshold		= -1;
	int										prevThreshhold		= -1;

	enum FadeMode {
		None, FadeIn, FadeOut
	}

	FadeMode								currentFade							= FadeMode.None;

	/**
	 * used when other objects may collide with this one - calculated with the
	 * scenes collision map
	 **/
	public Optional<PolygonCollisionMap>	cmap								= Optional.absent();
	/**
	 * if the object is still loading its cmap this will be true. If false you
	 * can use cmap.isPresent() to determine if a cmap exists or not
	 */
	boolean									cmapStillLoading					= true;

	// current angle, up is 0/360 down is 180
	public double							FacingDirection						= 0;

	/**
	 * If this object hits another, this is the speed it was traveling at when
	 * it hit <br>
	 * pixels per ms <br>
	 */
	public double							SpeedOfLastImpact					= 0;

	/**
	 * used for inventoryitems only
	 */
	public static boolean					iconclickedrecently					= false;

	// ------------------------------------------------------collision handling
	// end
	// attachment point list
	/**
	 * This is a list of points on each frame that can be refereed too This
	 * could be used for a character to hold a gun, wear a hat, or simple have a
	 * shadow glued to their feet
	 **/
	public AttachmentList					attachmentPoints;

	/** a cache of the attachment points, and the filename they belong too **/
	static HashMap<String, AttachmentList>	attachmentPointCache				= new HashMap<String, AttachmentList>();

	/**
	 * the currently edited object is stored here
	 * 
	 * NOTE: When SceneWidget is separated into GWT/Non-GWT specific bits, this
	 * variable can be moved to SceneObject as well
	 **/
	public static SceneObject				currentEditedObject					= null;

	/**
	 * Objects positioned relative to this. This doesn't need to be saved, as
	 * it can be worked out from the various objects
	 * currentObjectsState.positionedRelativeToo
	 **/
	public HashSet<SceneObject>				relativeObjects						= new HashSet<SceneObject>();

	/**
	 * A sound that might be associated with this object
	 ***/
	public IsJamSoundObject					ObjectsSound;

	/**
	 * This is the current number of iterations the current pathfinding for this
	 * object has gone though. This is purely used to place a cap on pathfinding
	 * - allowing "emergency exits" when it gets too big. (depending on the
	 * objects pathfindingmode setting, this could result in no movement at all,
	 * or it simply moving though the objects to reach its destination anyway)
	 */
	public int								objectsCurrentPathfindingIterator	= 0;									// reset
	// each
	// time
	// getSafePath
	// is
	// used

	/**
	 * Common setup that all scene objects have
	 * 
	 * @param actions
	 * @param sceneItsOn
	 * @param subclassStateObject
	 */
	public SceneObject(ActionList actions, SceneObjectState StateObject, SceneWidget sceneItsOn) {
		
		// set state object
		SOLog.info("_________setting objectsCurrentState object on " + StateObject.ObjectsName + "________________");
		this.objectsCurrentState = StateObject;

		// set the scene if we have one
		if (sceneItsOn != null) {
			this.setObjectsSceneVariable(sceneItsOn);	

		}


		if (sceneItsOn != null) {
			//add to its scenes logical loading after state has been set
			//---------------------------------		
			sceneItsOn.addToLogicalLoading(this);
			//-------------------------------------
			//It has to be removed from the logical loading queue later else the scene wont finish loading
			//(unless the scene is ALREADY loaded, and this is a clone object, in which case it should have no effect)
			//

		}

		// if theres a non-empty Filename that is not a internal animation, then thats the default folder
		if (folderName.isEmpty() && 
				!StateObject.ObjectsFileName.isEmpty() 
				&& !StateObject.ObjectsFileName.startsWith("<")) {
			folderName = StateObject.ObjectsFileName.split("0\\.")[0];
			SOLog.info("foldername set to to: " + folderName);
		} else {
			// we also set the folder name to the ObjectsName if it hasn't been set
			// above
			// this is a default ONLY, and can (and will) be overridden by subtypes
			// that implement the FileName parameter
			// (such as sprites)
			if (folderName.isEmpty()) {
				folderName = StateObject.ObjectsName.split("0\\.")[0];
				SOLog.info("foldername defaults to: " + folderName);

			}
		}
		// Set data here if theres any:
		// (clones wont have any because they didnt really come from a file, and
		// should have all their state information already)
		if (StateObject.ObjectsParamatersFromFile != null) {
		//	SOLog.info("_________assignObjectTypeSpecificParametersNew________________");

			// TODO: why assign this here? cant this be done when the state is
			// created?
			// TODO: should not be needed now, remove
		//	objectsCurrentState.assignObjectTypeSpecificParametersNew(StateObject.ObjectsParamatersFromFile);
			//hu? scene name on objects missing if we remove this?
		}

		setObjectsInitialState(objectsCurrentState.copy());

		// if there's actions then we prepare them
		if (actions != null) {
			objectsActions = actions;
			updateHandlersToMatchActions();
			SOLog.info("actions loaded");
		}




		// Finally we check some loading states.
		// Specifically, if we don't have attachment or movement we can set them
		// as loaded straight away
		//
		if (objectsCurrentState.hasAttachmentPointFile) {

			// load default urls attachment points if we arnt a sprite (spites handle it themselves on a per-frame base)
			if (!objectsCurrentState.isCompatibleWith(SceneObjectType.Sprite)){

				String baseURL = SceneWidget.SceneFileRoot + objectsCurrentState.ObjectsSceneName+"/Objects/"+ objectsCurrentState.ObjectsName+"/"; //new url method
				String atachmentPointUrl = baseURL+objectsCurrentState.ObjectsName+".glu";		//is this correct? no sub directory?			
				loadAttachmentPoints(atachmentPointUrl, true,true,null);

			}


		} else {
			PreconstructionObjectsLog("(AttachmentPointsLoadedOrNotNeeded)");
			AttachmentPointsLoadedOrNotNeeded = true;
		}
		if (!objectsCurrentState.hasMovementFile) {
			PreconstructionObjectsLog("(MovementFilesLoadedOrNotNeeded)");
			MovementFilesLoadedOrNotNeeded = true;
		}

		// note; collision maps have no flag, so we need to wait to test for the
		// files presence
		// -----------------------------------------------------------
		testIfWeAreLogicallyLoaded();
		// ---

		/*
		 * //inventory icons should not load collision maps - thats just silly
		 * if (StateObject.getPrimaryObjectType() ==
		 * SceneObjectType.InventoryIcon){
		 * 
		 * } else { //everything else should though
		 * startLoadingCmap(StateObject); }
		 */

	}




	//-------------------------------------------------------------------------------------
	/**
	 * used to remember ObjectLog messages before the ObjectLog has been made
	 */
	ArrayList<String> preContrustionLogCache = new ArrayList<String>();

	/**
	 * This will remember things to later put in the objectslog after its construction is done.
	 * (it updates the real log when initialize is first fired)
	 * @param name
	 * @param col
	 */
	private void PreconstructionObjectsLog(String logString) {		
		preContrustionLogCache.add(logString);

	}

	/**
	 * dump our pre-construction logs to the real ObjectLog object, which should now be constructed
	 */
	private void preConstructionObjectLog_CopyToRealLog() {		
		this.ObjectsLog("(--------preconstruction logs:)","#3c3b3b");

		for (String log : preContrustionLogCache) {
			this.ObjectsLog(log,"#3c3b3b");
		}
		this.ObjectsLog("(-----------------------------)","#3c3b3b");
	}
	///--------------------------------------------------------------------------

	/**
	 * Loads new attachment points into the cache

	 * @param atachmentPointUrl
	 * @param setAsCurrentUsedPoints
	 * @param setAsRequiredForLogicalLoad - is this the very first, default frames attachment file? then true. Else false.
	 * @param runafterreload - run this after loading successfully. (can be null)
	 */
	public void loadAttachmentPoints(final String atachmentPointUrl, 
			final boolean setAsCurrentUsedPoints,
			final boolean setAsRequiredForLogicalLoad, 
			final Runnable runafterload) {
		

		
		// preparing what to do after we retrieve the attachment points from the
		// server
		FileCallbackRunnable onResponse = new FileCallbackRunnable() {
			@Override
			public void run(String responseData, int responseCode) {

				if (setAsRequiredForLogicalLoad){
					AttachmentPointsLoadedOrNotNeeded = true;
					SceneObject.this.ObjectsLog("got attachment file response (AttachmentPointsLoadedOrNotNeeded)");				
					testIfWeAreLogicallyLoaded();
				}

				SOLog.info("Attachment points request returned code:" + responseCode);
				SOLog.info("Request was for :" + atachmentPointUrl);

				if (responseCode >= 400 || responseCode == 204) {
					SOLog.info("no attachment points found");
					SceneObject.this.ObjectsLog("no attachment points found at:"+atachmentPointUrl);			

					return;
				}

				String responsetext = responseData;

				SOLog.info("got attachmet points from url: " + responsetext);
				AttachmentList newAttachmentPoints = new AttachmentList(responsetext);

				if (setAsCurrentUsedPoints) {
					attachmentPoints = newAttachmentPoints;
					SOLog.info("processed points." + attachmentPoints.numberOfSets()
					+ " sets found,cached, and set as current");
				} else {
					SOLog.info("processed points." + newAttachmentPoints.numberOfSets()
					+ " sets found and added to cache ready for use latter");
				}

				// add to cache
				attachmentPointCache.put(atachmentPointUrl, newAttachmentPoints);

				//fire runnable if supplied
				if (runafterload!=null){
					runafterload.run();
				}
			}
		};

		// what to do if there's an error
		FileCallbackError onError = new FileCallbackError() {
			@Override
			public void run(String errorData, Throwable exception) {

				SceneObject.this.ObjectsLog("error getting  attachment points  at:"+atachmentPointUrl);		
				SOLog.info("error getting attachmet points from url ");

				if (setAsRequiredForLogicalLoad){
					SOLog.info("WAS REQUIRED FOR LOGICAL LOAD, setting as not needed");		
					SceneObject.this.ObjectsLog("WAS REQUIRED FOR LOGICAL LOAD, setting as not needed:");		
					AttachmentPointsLoadedOrNotNeeded = true;
					testIfWeAreLogicallyLoaded();
				}

			}

		};

		// using the above, try to get the text!
		// Note, this is one of the few times we use an extra "false" to stop
		// the general file-cache being used.
		// Thats because the attachmentPoints have their own caching system
		// (attachmentPointCache)

		RequiredImplementations.getFileManager().getText(atachmentPointUrl, true,onResponse, onError, false, false);
	}

	/**
	 * start loading the collision map, if there's one
	 * 
	 * @param stateObject
	 **/
	protected void startLoadingCmap(final SceneObjectState stateObject) {

		String url = objectScene.SceneFolderLocation + "/Objects/" + folderName + "/" + folderName + "_vmap.ini";

		SOLog.info("getting cmap file..." + url);
		cmapStillLoading = true;

		// note;
		// we should not use this inside a callable or another thread.
		// see; http://www.javapractices.com/topic/TopicAction.do?Id=252
		// in future maybe we should avoid calling this here at all, and instead
		// put it somewhere else gaurentied to run post-construction
		// this is because its dangerous to use callbacks in a constructor, as
		// it might try to use stuff in the object
		// that hasn't been constructed
		final SceneObject thisObject = this;

		// set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable() {

			@Override
			public void run(String responseData, int responseCode) {
				cmapStillLoading = false;
				CollisionMapLoadedOrNotNeeded = true;
				SceneObject.this.ObjectsLog("got cmap file response (CollisionMapLoadedOrNotNeeded)");				
				testIfWeAreLogicallyLoaded();

				// return if 404
				if (responseCode >= 400 || responseCode == 204) {
					SOLog.info("________no cmap file recieved (" + responseCode + "):\n");
					return;
				}
				// SceneObject.this is a way to refer to this current
				// sceneobject ("this" on its own would just refer to
				// the FileCallbackRunnable that these statements are in)
				SOLog.info("______" + stateObject.ObjectsName + "__cmap recieved:\n" + responseData);
				SOLog.info("______");
				if (stateObject == null) { // should not use this!
					SOLog.info("_____SSState not yet set...hmm");
				}
				SOLog.info("______" + stateObject.ObjectsName);

				cmap = Optional.of(new PolygonCollisionMap(responseData, thisObject));

				// if we have a cmap with co-poral elements ensure are collision
				// mode isn't set to none
				// In future we might want a decide mode for cmap rather then
				// bottom line
				// For now we need to specify any to ensure collisions happen
				if (cmap.get().hasCoporalPart()) {
					if (stateObject.boundaryType.collisionType == CollisionType.none) {
						stateObject.boundaryType.collisionType = CollisionType.bottomline;
						
					}
					
				}
			}

		};

		// what to do if theres an error
		FileCallbackError onError = new FileCallbackError() {

			@Override
			public void run(String errorData, Throwable exception) {
				CollisionMapLoadedOrNotNeeded = true;
				SceneObject.this.ObjectsLog("no cmap file found (CollisionMapLoadedOrNotNeeded)");				
				testIfWeAreLogicallyLoaded();

			}

		};

		RequiredImplementations.getFileManager().getText(url,true, onResponse, onError, false);

	}

	/** gets X location of the pinpoint **/
	public int getX() {

		// if (this.objectsData.positionedRelativeToo != null) {

		// int RX = objectsData.positionedRelativeToo.getX();

		// return RX + objectsData.relX + objectsData.PinPointX;

		// }

		// objectsData.X
		// int currentX = this.getElement().getParentElement().getOffsetLeft();

		return this.getTopLeftBaseX() + objectsCurrentState.CurrentPinPoint.x;
	}

	/** gets y location of the pinpoint **/
	public int getY() {

		// if (this.objectsData.positionedRelativeToo != null) {
		// int RY = objectsData.positionedRelativeToo.getY();
		// return RY + objectsData.relY + objectsData.PinPointY;
		// }

		// int currentY= this.getElement().getParentElement().getOffsetTop();

		// if we have a tempory co-ordinate override in Y we return that instead
		if (tempYOverride.isPresent()) {
			return tempYOverride.get();
		}

		return this.getTopLeftBaseY() + objectsCurrentState.CurrentPinPoint.y;
	}

	/** gets z co-ordinate **/
	public int getZ() {
		return this.getTopLeftBaseZ() + objectsCurrentState.CurrentPinPoint.z;
	}

	public float getSizeX() {
		return this.getPhysicalObjectWidth();
	}

	public float getSizeY() {
		return this.getPhysicalObjectHeight();
	}

	/**
	 * currently determined by boundary type height in future this might come
	 * from different sources
	 * 
	 * @return
	 */
	public float getSizeZ() {
		if (getObjectsCurrentState().boundaryType != null) {
			return getObjectsCurrentState().boundaryType.height;
		}

		return 0;
	}

	public int getTopLeftBaseX() {

		if (this.objectsCurrentState.positionedRelativeToo != null && this.objectsCurrentState.positionedRelativeLinkType.linkX) {

			int RX = objectsCurrentState.positionedRelativeToo.getX(); // pin
			// position
			// of
			// what
			// we
			// are
			// positioning
			// relative
			// too
			final String posTo = this.objectsCurrentState.positionedRelativeToPoint;
			// if theres a particular position specified we use that rather then
			// the standard z point
			if (!posTo.isEmpty()) {
				MovementWaypoint glueToo = objectsCurrentState.positionedRelativeToo.getAttachmentPointsFor(posTo);
				if (glueToo != null) {
					RX = objectsCurrentState.positionedRelativeToo.getTopLeftBaseX() + glueToo.pos.x;
				}
			}

			return RX + objectsCurrentState.relX - objectsCurrentState.CurrentPinPoint.x; // test
			// new

		}

		return objectsCurrentState.X;
	}

	public int getTopLeftBaseY() {

		if (this.objectsCurrentState.positionedRelativeToo != null && this.objectsCurrentState.positionedRelativeLinkType.linkY) {

			int RY = objectsCurrentState.positionedRelativeToo.getY();
			final String posTo = this.objectsCurrentState.positionedRelativeToPoint;
			// if theres a particular position specified we use that rather then
			// the standard z point
			if (!posTo.isEmpty()) {
				MovementWaypoint glueToo = objectsCurrentState.positionedRelativeToo.getAttachmentPointsFor(posTo);
				if (glueToo != null) {
					RY = objectsCurrentState.positionedRelativeToo.getTopLeftBaseY() + glueToo.pos.y;
				}
			}

			return RY + objectsCurrentState.relY - objectsCurrentState.CurrentPinPoint.y;

		}

		return objectsCurrentState.Y;

	}

	public int getTopLeftBaseZ() {

		//if we use relative positioning for z
		if (this.objectsCurrentState.positionedRelativeToo != null && this.objectsCurrentState.positionedRelativeLinkType.linkZ) {

			int RZ = objectsCurrentState.positionedRelativeToo.getZ();

			final String posTo = this.objectsCurrentState.positionedRelativeToPoint;
			// if there's a particular position specified we use that rather
			// then the standard z point
			if (!posTo.isEmpty()) {
				MovementWaypoint glueToo = objectsCurrentState.positionedRelativeToo.getAttachmentPointsFor(posTo);
				if (glueToo != null) {
					RZ = objectsCurrentState.positionedRelativeToo.getTopLeftBaseZ() + glueToo.pos.z;
				}
			}

			return RZ + objectsCurrentState.relZ - objectsCurrentState.CurrentPinPoint.z;

		}

		//else
		return objectsCurrentState.Z; // Vertical height of objects base
	}

	/**
	 * moveTo just moves to a point in a certain time period. Its more efficient
	 * then using a path based movement public void moveTo(int X, int Y, int
	 * Time, boolean timeSpecifiedIsSpeed) {
	 * 
	 * double distanceX = 0; double distanceY = 0;
	 * 
	 * if (timeSpecifiedIsSpeed) {
	 * 
	 * DestinationX = X - objectsCurrentState.PinPointX; // we sub the pinpoint
	 * DestinationY = Y - objectsCurrentState.PinPointY;
	 * 
	 * distanceX = Math.abs(DestinationX - objectsCurrentState.X); distanceY =
	 * Math.abs(DestinationY - objectsCurrentState.Y);
	 * 
	 * ObjectsLog("distanceX=" + distanceX,LogLevel.Info);
	 * ObjectsLog("distanceY=" + distanceY,LogLevel.Info);
	 * 
	 * timeStep = 100;
	 * 
	 * double ratio = distanceX / (distanceY + distanceX) * 1.0; double ratio2 =
	 * distanceY / (distanceX + distanceY) * 1.0;
	 * 
	 * if (distanceX == 0) { ratio = 0; ratio2 = 1; } if (distanceY == 0) {
	 * ratio = 1; ratio2 = 0; }
	 * 
	 * ObjectsLog("ratio1=" + ratio,LogLevel.Info); ObjectsLog("ratio2=" +
	 * ratio2,LogLevel.Info);
	 * 
	 * StepX = ratio * Time * (Math.signum(DestinationX -
	 * objectsCurrentState.X)); StepY = (ratio2) * Time
	 * (Math.signum(DestinationY - objectsCurrentState.Y));
	 * 
	 * } else { DestinationX = X - objectsCurrentState.PinPointX;
	 * 
	 * DestinationY = Y - objectsCurrentState.PinPointY;
	 * 
	 * distanceX = DestinationX - objectsCurrentState.X; distanceY =
	 * DestinationY - objectsCurrentState.Y;
	 * 
	 * ObjectsLog("distanceX=" + distanceX,LogLevel.Info);
	 * ObjectsLog("distanceY=" + distanceY,LogLevel.Info);
	 * 
	 * timeStep = 100;
	 * 
	 * StepX = (distanceX / (Time * 1.0)) * timeStep; StepY = (distanceY / (Time
	 * * 1.0)) * timeStep;
	 * 
	 * }
	 * 
	 * ObjectsLog("step x =" + StepX,LogLevel.Info); ObjectsLog("step y =" +
	 * StepY,LogLevel.Info);
	 * 
	 * // move to destination over time period
	 * 
	 * //reset remainders before starting the timer remainderX = 0; remainderY =
	 * 0;
	 * 
	 * // update every 1/10th of a second moveme.scheduleRepeating(timeStep);
	 * 
	 * }
	 * 
	 **/

	public void triggerClickOnObject() {
		triggerClickOnObject(false); // normally when trigger click is fired we
		// dont also test for clicks on objects
		// behind
		// We only test for clicks behind on the initial real click.
		// TriggerClickOnObject can also fire from non-real clicks though (like
		// -sendclickto)
		// In this case we dont want to test for clicks behind, therefor we
		// defeault to false
	}

	/**
	 * should be fired when a click event has been detected on a object.<br>
	 * <br>
	 * This function will<br>
	 * 1. set this object as the last clicked<br>
	 * 2. Update the game engines last clicked location variables.<br>
	 * <br>
	 * If no item is held it then;<br>
	 * 3. Fires any apporate click actions (global, then scene, then local)<br>
	 * 4. If no local actions were found it also checks for a object directly under it on zindex, and runs triggerClickOnObject on that object too
	 * 5. If none of them are found either, it triggers background click actions
	 *<br>
	 * If item was held;<br>
	 * 3. Runs UsedOn actions<br>
	 * 4. Possibly Unholds item depending on keepheld mode<br>
	 * 
	 * NOTE;
	 * #4 - identical zindexs will behave unpredictably as it wont know which is on top, thus avoid
	 *
	 */
	public void triggerClickOnObject(boolean testForClicksOnObjectsBehindThisToo) {

		SOLog.info("TriggerClickOnObject is triggered. Item is currently held yes/no:"	+ InventoryPanelCore.isItemCurrentlyBeingHeld);
		wasLastObjectClicked(); 
		
		// if no object held:
		if (!InventoryPanelCore.isItemCurrentlyBeingHeld) {

			wasLastObjectUpdated();
			updateLastClickedLocation();

			// we used to set correct scene here. No clue why.
			// scene should automatically be set on "bringSceneToFront" in
			// Instruction Processor. As well as "loadSceneIntoNewTab" if its
			// set to autopop up
			// InstructionProcessor.currentScene = objectScene;
			// SOLog.info("tcoo current scene set
			// to_:"+InstructionProcessor.currentScene.SceneFileName);

			// test for global actions'
			SOLog.info("testing for game global actions...");

			//TODO: region specific global mouse click actions
			//
			//Standard global:							
			InstructionProcessor.testForGlobalActions(TriggerType.MouseClickActions, null, this);

			// temp test
			if (objectScene == null) {
				SOLog.info("scene object is null! Thats really mega wrong :(");
			}

			if (!getObjectsCurrentState().ignoreSceneActions) {

				SOLog.info("testing for ClickActions specific to scene " + objectScene.SceneFileName + "...");

				// test for scene actions				
				//TODO: region specific mouse click scene actions? 
				//
				//Standard:				
				objectScene.testForSceneActions(TriggerType.MouseClickActions, null, this);

			} else {
				ObjectsLog("scene actions ignored (set ignoreSceneActions to false to make this work)");

			}

			//
			// local object actions
			boolean firedActions=false; //we track if we found actions or not
			
			if (actionsToRunForMouseClick != null) {
				SOLog.info("_________actionsToRunForMouseClick ");
				//attempt to fire both generic and region-specific mouseclicks on this object
				firedActions=fireAproperateMouseClickActions(actionsToRunForMouseClick);
				//---------------------------
				//-------------------
				//----------
			}

			//if we did not run anything above, we should also test objects under us;
			//(assuming this object hasnt been set to capture all actions - such as when you might want a 'click blocker'
			if (!firedActions && (this.getObjectsCurrentState().forceReceiveActions==false)){
				ObjectsLog("(no click actions found, or click was not in any of the specified regions)");

				//first get the objects we are over
				ArrayList<SceneObject> objectsWeAreOver =  getParentScene().getObjectsMouseIsOver(getParentScene().getScenesData().allScenesCurrentObjects());
				ObjectsLog("Currently Over; "+objectsWeAreOver.size()+" objects, checking...");		

				SceneObject currentHighestUnder = null;


				for (SceneObject so : objectsWeAreOver) {
					//exclude ourselves
					if (so==this){
						continue;
					}
					//exclude if set to IgnorePointerEvents
					if (so.getObjectsCurrentState().ignorePointerEvents){
						continue;
					}
					//find the highest zindex yet is still under us
					//(set currentHighestUnder if empty or higher then the current highest while always being less then ourselves)
					if (
							(currentHighestUnder==null || so.getZindex()>currentHighestUnder.getZindex()) 
							&& (so.getZindex()<getZindex()) //without this we may loop
							)
					{
						currentHighestUnder=so;
					}

					//ObjectsLog("Currently Over;"+so.getName()+" zindex:"+so.getZindex());		
				}

				//now fire click on highest found, if there was one;					
				if (currentHighestUnder!=null){
					ObjectsLog("Firing click on ObjectDirectlyUnder;"+currentHighestUnder.getName());	
					currentHighestUnder.triggerClickOnObject(testForClicksOnObjectsBehindThisToo);
					return;
				} else {
					ObjectsLog("(nothing hitable found under, so running backgroundclick on scene","blue");
					boolean backgroundActionsFound = getParentScene().testForSceneActions(TriggerType.BackgroundClickAction, null);
					ObjectsLog("backgroundActionsFound:"+backgroundActionsFound);

				}
			}

		} else {
			// if object held:
			SOLog.info("_________actionsToRunForMouseClick while object held ");

			wasLastObjectUpdated();

			updateLastClickedLocation();

			// trigger objects events
			if (((IsInventoryItem) InventoryPanelCore.currentlyHeldItem).getObjectsActions() != null) {

				CommandList actions = ((IsInventoryItem) InventoryPanelCore.currentlyHeldItem).getObjectsActions()
						.getActionsForTrigger(TriggerType.UsedOnObject, null);

				InstructionProcessor.processInstructions(actions, "useditem_" + getObjectsCurrentState().ObjectsName
						+ InventoryPanelCore.currentlyHeldItem.getName(), this);


				// unhold if its still being showen and the menu isnt being
				// displayed and its not set to be kept held when actions have
				// been found
				// note the "onsuccess" is just determined by the other check
				// for actions existing
				// in future possibly a more precise method is needed? Like if
				// the object your clicking on
				// has actions for this specific item?
				if (InventoryPanelCore.currentlyHeldItem != null) {

					if (!SceneMenuWithPopUp.menuShowing
							&& InventoryPanelCore.currentlyHeldItem.getKeepHeldMode() != KeepHeldMode.onuse) {
						SOLog.info("_________unholdItem after processInstructions ");
						InventoryPanelCore.unholdItem();
					} else {
						SOLog.info(
								"_________menu showing or set to hold on use and uses were found, so we keep holding");

					}

				}

			}

			// maybe unhold here?
		}

		// after we have dealt with the normal click actions we must also test
		// for any objects under us that
		// are set to fire their special "OnClickWhileUnder" actions. This
		// method is primarily used for moving objects that may end up
		// hidden behind other objects. This ensures they can still be clicked.
		if (testForClicksOnObjectsBehindThisToo) {
			objectScene.testForClickedWhileBehindActions(this);
		}

	}

	/**
	 * Will fire mouse click actions in the supplied sets, provided the click matchs any region requirements
	 * specified.
	 * 
	 * @param actionsToCheckForMouseClick
	 * @return
	 */
	private boolean fireAproperateMouseClickActions(HashSet<ActionSet> actionsToCheckForMouseClick) {

		boolean ranActions=false; 

		for (ActionSet actionSet : actionsToCheckForMouseClick) {										
			for (Trigger trigger : actionSet.getTriggers()) {
				//if standard action set it has no parameters
				if (trigger.parameter==null){
					ObjectsLog("Running MouseClickActions on object (no region set)","BLUE");

					InstructionProcessor.processInstructions(actionSet.CommandsInSet.getActions(),
							"c_" + getObjectsCurrentState().ObjectsName, this);
					//break out of this layer of the loop to the next ActionSet
					//<----
					ranActions=true;
					break;
				} else {			

					if (!cmap.isPresent()){
						SOLog.severe("_____warning region test needed on object with no cmap__");									
					}

					String regionName = trigger.parameter;

					this.ObjectsLog("Testing MouseClickActions on object (region set:"+regionName+")","BLUE");
					//test if we are in region regionName
					//if so, run actions
					int x=CurrentScenesVariables.lastclicked_x;
					int y=CurrentScenesVariables.lastclicked_y;
					int z=CurrentScenesVariables.lastclicked_z;
					boolean inside = SceneObject.checkPointIsWithinRegion(this.cmap.get(),regionName,x,y,z); //z not yet used
					if (inside){
						InstructionProcessor.processInstructions(actionSet.CommandsInSet.getActions(),
								"c_" + getObjectsCurrentState().ObjectsName, this);
						ranActions=true;
					} else {
						ObjectsLog("(click not in region :"+regionName+")");

					}

				}	
			} 

		}

		return ranActions;

	}

	public void triggerDoubleClickOnObject() {

		SOLog.info("_________________Event.ONDOUBLECLICK");
		wasLastObjectClicked();

		JAMcore.testForGlobalActions(TriggerType.MouseDoubleClickActions, null, this);

		// test for scene actions
		if (!getObjectsCurrentState().ignoreSceneActions) {
			objectScene.testForSceneActions(TriggerType.MouseDoubleClickActions, null, this);
		}

		// run object actions
		if (actionsToRunForMouseRightClick != null) {
			wasLastObjectUpdated();
			updateLastClickedLocation();

			InstructionProcessor.processInstructions(actionsToRunForMouseDoubleClick.CommandsInSet.getActions(),
					"doublec_" + getObjectsCurrentState().ObjectsName, this);

		}

	}

	public void triggerContextClickOnObject() {
		SOLog.info("_________________Event.ONCONTEXTMENU");
		wasLastObjectClicked();

		// test for global actions
		// InstructionProcessor.testForGlobalActions(TriggerType.MouseRightClickAction,
		// null, this);
		JAMcore.testForGlobalActions(TriggerType.MouseRightClickActions, null, this);

		// test for scene actions
		if (!getObjectsCurrentState().ignoreSceneActions) {
			objectScene.testForSceneActions(TriggerType.MouseRightClickActions, null, this);
		}

		// run object actions
		if (actionsToRunForMouseRightClick != null) {
			// SOLog.info("________Running actions:"
			// + actionsToRunForMouseRightClick.Actions.toString());

			wasLastObjectUpdated();

			updateLastClickedLocation();

			InstructionProcessor.processInstructions(actionsToRunForMouseRightClick.CommandsInSet.getActions(),
					"rc_" + getObjectsCurrentState().ObjectsName, this);

		}
	}

	/**
	 * 
	 * @param touching2
	 * @param callingObject
	 */
	public void replaceTouchingPropertys(ArrayList<SceneObject> replacements, SceneObject callingObject) {

		ObjectsLog("- Setting touching:" + replacements.size() + " to " + this.getName());

		// we need to first work out whats new
		ArrayList<SceneObject> newAdditions = new ArrayList<SceneObject>(replacements);
		newAdditions.removeAll(getObjectsCurrentState().touching); // .getNames()

		// newAdditions is now a list of all the things we are adding to
		// touching that arnt there already
		// each of these objects will need to be told that there is a new object
		// touching them

		// we also need to work out what's being removed
		ArrayList<SceneObject> newSubtractions = new ArrayList<SceneObject>(getObjectsCurrentState().touching); // .getNames()
		newSubtractions.removeAll(replacements); // what's left is what we
		// effectively are removing

		// The two above make a list of things CHANGED (either added or removed)

		// Now we know what will change we can simply replace the touching array
		// with the new list
		getObjectsCurrentState().touching.clear();
		// getObjectsCurrentState().touching.addAll(replacements);
		// getObjectsCurrentState().touching.fromNameCollection(replacements);
		// getObjectsCurrentState().touching.cacheObjectSet();
		getObjectsCurrentState().touching.addAll(replacements);

		// then loop over the added objects
		for (SceneObject touchingObject : newAdditions) {

			// SceneObject touchingObject =
			// SceneObjectDatabase.getSingleSceneObjectNEW(new_touching,
			// callingObject, true);

			// if (touchingObject!=null){
			// add to ourselves and ourselves from it
			runOnTouchingChangeActions(touchingObject);
			touchingObject.addTouchingProperty(this, this);
			// } else {
			// SOLog.warning("cant find object: "+touchingObject.getName());
			// }

		}

		// and the removed
		for (SceneObject touchingObject : newSubtractions) {
			// SceneObject touchingObject =
			// SceneObjectDatabase.getSingleSceneObjectNEW(newlyremoved,
			// callingObject, true);

			// if (touchingObject!=null){
			// remove from both ourselves and ourselves from it
			runOnTouchingChangeActions(touchingObject);
			touchingObject.removeTouchingProperty(this, this);
			// } else {
			// SOLog.warning("cant find object: "+touchingObject.getName());
			// }

		}

		ObjectsLog("-updating  touching list Changed");

	}

	public void addTouchingProperty(SceneObject touchingObject, SceneObject callingObject) {

		// tell this object its now touching the new one
		boolean changed = getObjectsCurrentState().touching.add(touchingObject);

		ObjectsLog("-adding touching:" + touchingObject.getName() + " to " + this.getName() + " changed:" + changed);
		SOLog.warning("-adding touching:" + touchingObject.getName() + " to " + this.getName() + " changed:" + changed);

		if (changed) {
			// Set<? extends SceneObject> curobject =
			// SceneObjectDatabase.getSceneObjectNEW(new_touching,
			// callingObject, true);
			// NOTE:this should only run on the single object thats now touching
			// it, not every object with the same name
			// SceneObject touchingObject =
			// SceneObjectDatabase.getSingleSceneObjectNEW(new_touching,
			// callingObject, true);

			// if (touchingObject!=null){
			runOnTouchingChangeActions(touchingObject);

			/*
			 * for (SceneObject so : curobject) {
			 * 
			 * ObjectsLog("-updating  ChangeActions");
			 * runOnTouchingChangeActions(so);
			 * 
			 * }
			 */

			touchingObject.addTouchingProperty(this, this); // new
			// } else {

			// SOLog.warning("cant find object: "+touchingObject.getName());

			// }

			// shouldn't we also add this object to these new things touching
			// changed actions?
			// after all, the touch goes both ways?

			// ObjectsLog("-updating propertysChanged ChangeActions"); //NEWLY
			// REMOVED
			// touchingPropertysChanged(); //NEWLY REMOVED
		} else {
			ObjectsLog("no change, had touching property already");
			SOLog.warning("no change, had touching property already");

		}

	}

	public void removeTouchingProperty(SceneObject touchingObject, SceneObject callingObject) {

		ObjectsLog("-removing touching:" + touchingObject.getName() + " to " + getObjectsCurrentState().ObjectsName);

		boolean changed = getObjectsCurrentState().touching.remove(touchingObject);

		if (changed) {
			// SceneSpriteObject curobject =
			// SceneWidget.getSpriteObjectByName(new_touching,null)[0];
			// Set<SceneSpriteObject> curobjects =
			// SceneObjectDatabase.getSpriteObjectNEW(new_touching,callingObject,true);

			// now we run our own touching changed actions

			// Set<? extends SceneObject> curobjects =
			// SceneObjectDatabase.getSceneObjectNEW(new_touching,
			// callingObject, true);

			// NOTE:this should only run on the single object thats now touching
			// it, not every object with the same name
			// SceneObject touchingObject =
			// SceneObjectDatabase.getSingleSceneObjectNEW(new_touching_to_remove,
			// callingObject, true);

			// if (touchingObject!=null){

			// updates touching objects
			// ObjectsLog("-updating ChangeActions");
			runOnTouchingChangeActions(touchingObject);
			// touchingObject.runOnTouchingChangeActions(this);

			// update what we used to touch as well
			touchingObject.removeTouchingProperty(this, this); // new added
			// } else {
			// SOLog.warning("cant find object: "+touchingObject.getName());

			// }

		} else {
			ObjectsLog("no change, did not have property");

		}

		/*
		 * for (SceneObject so : curobjects) { runOnTouchingChangeActions(so); }
		 */

		// ObjectsLog("-updating propertysChanged ChangeActions");//NEWLY
		// REMOVED

		// this will automatically run touching changed actions on the new
		// touching object
		// touchingPropertysChanged(); //NEWLY REMOVED
	}

	protected void touchingPropertysChanged() {

		// this runs to update any neighboring objects if they exist
		// this only applies to sprites

		// if (objectsCurrentState.getCurrentType()==SceneObjectType.Sprite){
		// ((SceneSpriteObject)(thisobject)).TriggeringNaboursOnTouchingChange(null);
		// }

		// run touching changes actions on the new thing this is touching
		// (note; things called the same will also have their touching changed
		// actions run)
		TriggeringNaboursOnTouchingChange(null);

		updateDebugInfo();
	}

	/**
	 * if this is set to true the current loop for updating nabouring objects is
	 * aborted. This is purely done if another "TriggerNaboursOnTouching" is
	 * started while a existing one is running.
	 */
	// boolean abortCurrentTriggerNaboursTouchingChange = false;
	int TempCountOfSimultaniousTouchingChanges = 0;

	/**
	 * should be run when any property on this object changes
	 * 
	 * @param callingObject
	 */
	public void TriggeringNaboursOnTouchingChange(SceneObject callingObject) {
		// abortCurrentTriggerNaboursTouchingChange = true;

		if (getObjectsCurrentState().touching.size() > 0) {

			TempCountOfSimultaniousTouchingChanges++;

			ObjectsLog("TriggeringNaboursOnTouchingChange currently:" + TempCountOfSimultaniousTouchingChanges);
			ObjectsLog("currently touching:" + this.getObjectsCurrentState().touching.toSerialisedString());
			// TODO possibly tell outer updates to abort if they have
			// re-triggered themselves anyway?

			SOLog.info("_______running OnTouchingChangeActions for the objects touching:" + this.getName());

			// loop over touching array and trigger their OnTouchingChange
			// events
			// NOTE: These events can do anything - including moving objects and
			// thus changing
			// the touching list

			HashSet<SceneObject> touchingObjects = new HashSet<SceneObject>(getObjectsCurrentState().touching); // -sigh-
			// we
			// have
			// to
			// copy
			// to
			// avoid
			// modifications
			// to
			// the
			// list
			// as
			// we
			// are
			// looping.
			// See
			// below
			// for
			// details
			// TODO:
			// should we try to avoiding copying this ?
			// potentially this statement can call something else, which changes
			// its property's
			// which in turn changes this ones property's,
			// which in turn calls this
			// Which will give a concurrent modification error
			// we could also try to abort one instance of this function running
			// when any changes are made, but that gets even more messy

			// unfortunately we cant use CopyOnWriteArraySets as it not gwt
			// compatible.
			// ConcurrentHashMaps from guava might be possible though

			// (HashSet<String>)
			Iterator<SceneObject> touching_objects_it = touchingObjects.iterator();
			while (touching_objects_it.hasNext()) {
				// emergance exit
				// if (abortCurrentTriggerNaboursTouchingChange){
				// break;
				// }

				SceneObject so = touching_objects_it.next();

				if (so != null) { // pointless?
					// get Object by name
					// SceneSpriteObject curObject = SceneWidget
					// .getSpriteObjectByName(objectName,null)[0];
					// Set<SceneSpriteObject> curobjects =
					// SceneObjectDatabase.getSpriteObjectNEW(objectName,callingObject,true);

					// Set<? extends SceneObject> curobjects =
					// SceneObjectDatabase.getSceneObjectNEW(objectName,callingObject,true);

					// for (SceneObject so : curobjects) {
					SOLog.info("_______running OnTouchingChangeActions for" + so.getName());
					so.runOnTouchingChangeActions(this);

					// }

					// curObject.runOnTouchingChangeActions(this);
					SOLog.info("_______ranOnTouchingChange_" + so.getName());

				} else {
					SOLog.info("_invalid or object in touching list");

				}
			}
			TempCountOfSimultaniousTouchingChanges--;
			// abortCurrentTriggerNaboursTouchingChange = false;
		}

	}

	protected void testAndRunSceneAndGlobalActions(TriggerType trigger, String param) {

		// test for global actions
		JAMcore.testForGlobalActions(trigger, param, this);

		// we used to set correct scene here. No clue why.
		// scene should automatically be set on "bringSceneToFront" in
		// Instruction Processor. As well as "loadSceneIntoNewTab" if its set to
		// autopop up

		// InstructionProcessor.currentScene = objectScene;
		// SOLog.info("gar current scene set
		// to_:"+InstructionProcessor.currentScene.SceneFileName);

		// test for scene actions if this object is on a scene
		// Note: Inventory items are not on a scene
		if (objectScene != null) {
			objectScene.testForSceneActions(trigger, param, this);
		}

		return;

	}

	/**
	 * runs actions stored when a neighboring object changes
	 **/
	void runOnTouchingChangeActions(SceneObject sourceObject) {

		// global actions

		// do we check for scene actions?

		wasLastObjectUpdated();

		// set correct scene
		// we used to set correct scene here. No clue why.
		// scene should automatically be set on "bringSceneToFront" in
		// Instruction Processor. As well as "loadSceneIntoNewTab" if its set to
		// autopop up
		// InstructionProcessor.currentScene = objectScene;
		// Log.info("rotca current scene set
		// to_:"+InstructionProcessor.currentScene.SceneFileName);

		// test for global actions
		CurrentScenesVariables.lastSceneObjectUpdated = this; // TODO: probably
		// can be
		// removed as
		// this is done
		// in
		// wasLastObjectUpdated

		CurrentScenesVariables.lastObjectThatTouchedAnother = sourceObject;
		// InstructionProcessor.testForGlobalActions(TriggerType.OnTouchingChange,
		// null, this);

		SOLog.info("testing for global touching actions");
		JAMcore.testForGlobalActions(TriggerType.OnTouchingChange, null, this);

		// local actions
		if (actionsToRunForWhenATouchingObjectChanges != null) {

			CurrentScenesVariables.lastSceneObjectUpdated = this;

			CurrentScenesVariables.lastObjectThatTouchedAnother = sourceObject;
			SOLog.info("last object clicked on set to:" + this.getObjectsCurrentState().ObjectsName);

			InstructionProcessor.processInstructions(
					actionsToRunForWhenATouchingObjectChanges.CommandsInSet.getActions(),
					"nc_" + this.getObjectsCurrentState().ObjectsName, this);

		}

	}

	/**
	 * note: all property are converted to lower case for case-insensitive
	 * comparisons
	 **/
	public void addProperty(String addThis,boolean processPropertyAddedActions, boolean tellNeighbours) {

		addThis = addThis.toLowerCase();

		//createSSSProperty from string
		SSSProperty newProp = PropertySet.createStandardProperty(addThis);		
		//(we do this here, rather then using the objectsProperties.add(string..) to do itself, so we can look at its parent classes

		//now we get all the parents, parents of parents etc
		//basically all the ancestors of the class.
		//So if the property "shurbet lemon" was added, then the parent classes might be "sweet" and the parent of that "food"
		//We get all the parents as 
		//PropertyAddedAction triggers should fire if any subproperty of their spec is fired
		//So PropertyAddedAction=food: should fire on ANY food being added. (such as shurbet lemon)
		HashSet<SSSNode> parentClasses=null;
		if (newProp.getPred()==SSSNode.SubClassOf){
			parentClasses = newProp.getValue().getAllClassesThisBelongsToo();
			ObjectsLog("Adding property:" + addThis+" It has "+parentClasses+" parents/anvestors.");
		}
		//
		SOLog.info("Adding property:" + addThis);

		if (getObjectsCurrentState().objectsProperties.add(newProp)) {

			// --------
			// update debug boxes
			updateDebugInfo();

			if (processPropertyAddedActions){
				// check for global commands
				testAndRunSceneAndGlobalActions(TriggerType.PropertyAddedActions, addThis);

				//if we had parents, check for them too
				if (parentClasses!=null){
					for (SSSNode sssNode : parentClasses) {		

						SOLog.info("Testing parent property:" + sssNode.getPLabel());
						testAndRunSceneAndGlobalActions(TriggerType.PropertyAddedActions, sssNode.getPLabel());					
					}
				}

				// check for object specific actions
				if (objectsActions != null) {

					TriggerType triggerType = TriggerType.PropertyAddedActions;
					testAndRunObjectSpecificActions(addThis,triggerType);

					//if we had parents, check and run for them too
					if (parentClasses!=null){
						for (SSSNode sssNode : parentClasses) {	

							SOLog.info("Testing parent property:" + sssNode.getPLabel());
							testAndRunObjectSpecificActions(sssNode.getPLabel(),triggerType);					
						}
					}

				}
			}

			SOLog.info(" (propertys changed on " + this.getName() + ",telling the nabourghs) ");
			if (tellNeighbours){
				touchingPropertysChanged();
			}
		} else {

			ObjectsLog("property " + addThis + " already on item " + this.getName());
			SOLog.info("property already on item " + addThis);

		}
	}

	private void testAndRunObjectSpecificActions(String addOrRemoveThis,TriggerType triggerType) {


		CommandList actions = objectsActions.getActionsForTrigger(triggerType, addOrRemoveThis);

		if (actions==null || actions.isEmpty()){
			//	ObjectsLog("No property change actions found for "+addOrRemoveThis);
			return;
		}


		//ObjectsLog("property change actions found for "+addOrRemoveThis+": \n" + actions.toString());

		// we may or may not have a filename for the scene, because
		// sometimes objects dont have scenes
		// (ie,inventory items)
		if (getParentScene() != null) {


			InstructionProcessor.processInstructions(actions,
					"FROM_" + objectScene.SceneFileName + "_" + this.getObjectsCurrentState().ObjectsName,
					this);

		} else {

			InstructionProcessor.processInstructions(actions,
					"FROM_" + "NoScene_" + this.getObjectsCurrentState().ObjectsName, this);

		}
	}

	public void removeAllPropertys() {

		// loop over all propertys removing them
		// note; this will trigger all the property change actions
		ArrayList<String> propertyStrings = getObjectsCurrentState().objectsProperties.getAllDirectPropertysAsStrings();

		SOLog.info("Propertys to remove:" + propertyStrings.toString());

		Iterator<String> propertyIT = propertyStrings.iterator();

		while (propertyIT.hasNext()) {
			String removeThis = propertyIT.next();

			removeProperty(removeThis,true,true);

		}

	}

	public boolean removeProperty(String removeThis,boolean processPropertyRemovedActions, boolean tellNeighbours) {

		//ensure not empty or null
		if (removeThis==null || removeThis.isEmpty()){
			SOLog.severe("No property specified to remove");
			return false;
		}
		
		
		
		
		
		//createSSSProperty from string
		SSSProperty newProp = PropertySet.createStandardProperty(removeThis);	

		//now we get all the parents, parents of parents etc
		//basically all the ancestors of the class.
		//So if the property "shurbet lemon" was added, then the parent classes might be "sweet" and the parent of that "food"
		//We get all the parents as 
		//PropertyAddedAction triggers should fire if any subproperty of their spec is fired
		//So PropertyAddedAction=food: should fire on ANY food being added. (such as shurbet lemon)
		HashSet<SSSNode> parentClasses=null;
		if (newProp.getPred()==SSSNode.SubClassOf){
			parentClasses = newProp.getValue().getAllClassesThisBelongsToo();
			ObjectsLog("Removing property:" + removeThis+" It has "+parentClasses+" parents/anvestors.");
		}
		//



		if (getObjectsCurrentState().objectsProperties.remove(newProp)) {

			ObjectsLog(removeThis + " was removed from " + this.getObjectsCurrentState().ObjectsName);
			// --------
			// update debug boxes
			// if (oipu != null) {

			// oipu.update();
			// }
			updateDebugInfo();

			// we no longer set the title to the propertys
			// as we have better debugging methods for this now.
			// this.setTitle(objectsData.objectsProperties
			// .toString());

			if (processPropertyRemovedActions){

				// check for global commands
				testAndRunSceneAndGlobalActions(TriggerType.PropertyRemovedActions, removeThis); // mu
				//if we had parents, check for them too (see addPropertys)


				//if we had parents, check for them too
				if (parentClasses!=null){
					for (SSSNode sssNode : parentClasses) {		

						SOLog.info("Testing parent property:" + sssNode.getPLabel());
						testAndRunSceneAndGlobalActions(TriggerType.PropertyRemovedActions, sssNode.getPLabel());					
					}
				}




				// check for object specific actions
				if (objectsActions != null) {

					TriggerType triggerType = TriggerType.PropertyRemovedActions;
					//run our property removed actions					
					testAndRunObjectSpecificActions(removeThis,triggerType);


					//if we had parents, check and run for them too
					if (parentClasses!=null){
						for (SSSNode sssNode : parentClasses) {	

							SOLog.info("Testing parent property:" + sssNode.getPLabel());
							testAndRunObjectSpecificActions(sssNode.getPLabel(),triggerType);					
						}
					}


					//CommandList actions = objectsActions.getActionsForTrigger(TriggerType.PropertyRemovedActions,
					//		removeThis);
					//ObjectsLog("property remove actions found: \n" + actions.toString());

					// InstructionProcessor.processInstructions(actions, "FROM_"
					// + objectScene.SceneFileName + "_"
					// + this.getObjectsCurrentState().ObjectsName, this);

					//InstructionProcessor.processInstructions(actions,
					//		"FROM_" + objectScene.SceneFileName + "_" + this.getObjectsCurrentState().ObjectsName, this);

				}

			}

			if (tellNeighbours){
				touchingPropertysChanged();
			}
			return true;
		}

		return false;
	}

	/**
	 * Sets the widgets visibility WITHOUT changing the status of Any "last
	 * clicked" or "last called" variables
	 * 
	 * Note; currently also changes opacity to 0.0 or 1.0
	 **/
	public void setVisibleSecretly(boolean status) {

		ObjectsLog("(so) setting " + this.objectsCurrentState.ObjectsName + " visible secretly:" + status,
				LogLevel.Info);

		if (status) {
			// thisobject.getElement().getStyle().setOpacity(100);
			objectsCurrentState.currentlyVisible = true;

			objectsCurrentState.currentOpacity = 1.0;
			// thisobject.getElement().getStyle().setOpacity(100);
			setOpacityImplementation(1);

		} else {
			// thisobject.getElement().getStyle().setOpacity(0);
			// Opacity = 0;
			objectsCurrentState.currentlyVisible = false;

			objectsCurrentState.currentOpacity = 0.0;
			// thisobject.getElement().getStyle().setOpacity(0);
			setOpacityImplementation(0);

		}

		// update the objects inspector if there is one
		updateDebugInfo();
		// if (oipu != null) {
		/// oipu.update();
		// }
		// super.setVisible(status);
		// setVisibleInternalFocusPanel(status);
		setVisibleImplementation(status);

		// ObjectsLog.info("display is now set
		// to:"+super.getElement().getStyle().getDisplay());

	}

	/**
	 * fadein from 0% (default)
	 * 
	 * @param time
	 * @param afterFadeIn
	 */
	public void fadeIn(int time, final Runnable afterFadeIn,boolean resumeFromCurrentOpacity) {

		
		currentFade = FadeMode.FadeIn;		
		this.afterFadeIn = afterFadeIn;
		
		
		if (!resumeFromCurrentOpacity){
			setOpacityImplementation(0);	
			objectsCurrentState.currentOpacity = 0;
			updateRelativeObjectsOpacity();
		}

		ObjectsLog("fading in......( from"+objectsCurrentState.currentOpacity+")");
		
		objectsCurrentState.currentlyVisible = false;

		stepPerMS = 1.0 / time; // how much our opacity should change each ms

		ObjectsLog("fading in with " + stepPerMS + " change in opacity per ms");
		
		setVisibleImplementation(true);
		updateRelativeObjectsVisibility(true);

		JAMTimerController.addObjectToUpdateOnFrame(this);

	}

	/**
	 * fadeout from 100% (default)
	 * 
	 * @param time
	 * @param afterFadeOut
	 */
	public void fadeOut(int time, final Runnable afterFadeOut,boolean resumeFromCurrentOpacity) {

		
		
		if (!resumeFromCurrentOpacity){			
			setOpacityImplementation(1);
			updateRelativeObjectsOpacity();		
			objectsCurrentState.currentOpacity = 1;
		}
		
		ObjectsLog("fading out......(from "+objectsCurrentState.currentOpacity+")");
		
		currentFade = FadeMode.FadeOut;
		this.afterFadeOut = afterFadeOut;

		stepPerMS = 1.0 / time; // how much our opacity should change each ms

		setVisibleImplementation(true);
		updateRelativeObjectsVisibility(true);		

		ObjectsLog("fading out with :" + stepPerMS+" change in opacity per ms");
		JAMTimerController.addObjectToUpdateOnFrame(this);

		objectsCurrentState.currentlyVisible = true;
	}

	public void cancelCurrentFade(boolean cancelPostFadeCommands) {

		currentFade = FadeMode.None;

		if (cancelPostFadeCommands) {
			this.afterFadeOut = null;
			this.afterFadeIn = null;
		}

	}

	/**
	 * 
	 * @param newopacity
	 */
	public void setObjectOpacity(double newopacity) {

		ObjectsLog("setObjectOpacity requested:....." + newopacity);
		objectsCurrentState.currentOpacity = newopacity;
		setOpacityImplementation(newopacity);
		updateRelativeObjectsOpacity();

		// update the objects inspector if there is one
		updateDebugInfo();

		// should we auto setvisible false at zero?

	}

	public void StopCurrentMovement() {

		stopNextMovementFrame();

		//clear movements if we have them
		if (objectsCurrentState.moveState.isPresent()){
			//we could possibly even set this to absent, but its likely moved objects will be moved again, so probably best not too
			objectsCurrentState.moveState.get().clearMovements();
		}

		// triggerMovementEndCommands();
		wasJustMoved(true);

		// update inspector

		// update the objects inspector if there is one
		updateDebugInfo();

	}

	public enum LogLevel {
		Spam, Info, Warning, MildPanic, Error, Apoclypse
	}

	/**
	 * default log at info level. See ObjectsLog(String contents,LogLevel level)
	 **/
	public void ObjectsLog(String contents) {
		ObjectsLog(contents, LogLevel.Info);
	}

	/**
	 * This can be overridden to allow custom UI for displaying the log in
	 * different implementations. Defaults to the internal java util log which
	 * if using gwt will be visible in chrome/firefoxs inspector
	 **/
	public void ObjectsLog(String contents, LogLevel level) {

		switch (level) {
		case Apoclypse:
			SOLog.severe(contents);
			break;
		case Error:
			SOLog.severe(contents);
			break;
		case Warning:
			SOLog.warning(contents);
			break;
		case MildPanic:
			SOLog.warning(contents);
			break;
		case Info:
			SOLog.info(contents);
			break;
		case Spam:
			SOLog.finest(contents);
			break;

		}
	}

	/**
	 * This can be overridden to allow custom UI for displaying the log in
	 * different implementations. Defaults to the internal java util log which
	 * if using gwt will be visible in chrome/firefoxs inspector (which ignores
	 * colour)
	 **/
	public void ObjectsLog(String contents, String colour) {
		SOLog.info(contents);
	}

	public boolean hasProperty(SSSNode pred, SSSNode val) {
		return objectsCurrentState.objectsProperties.hasProperty(pred, val);
	}

	public boolean hasProperty(SSSNode testThis) {

		return objectsCurrentState.objectsProperties.hasProperty(testThis);
	}

	public boolean hasProperty(String testThis) {

		return objectsCurrentState.objectsProperties.hasProperty(testThis);
	}

	/**
	 * Note; searching for value ANY> means we just check if we have any
	 * property with the specified predicate.
	 * 
	 * @param pred
	 * @param val
	 * @return
	 */
	public boolean hasProperty(String pred, String val) {
		if (val.equalsIgnoreCase("<ANY>")) {
			return objectsCurrentState.objectsProperties.hasPredicate(pred);
		}
		return objectsCurrentState.objectsProperties.hasProperty(pred, val);
	}

	public void subtractkFromVariable(String name, String value) {
		SOLog.info("subtracting = " + value);


		int val = (int) Double.parseDouble(value); // currently we just cast too
		if (val==0){
			return; //no op
		}
		// int
		objectsCurrentState.ObjectRuntimeVariables.addToVariable(name, -val);

		triggerActionsToRunWhenObjectVariableChanges(name);
	}

	public void addToVariable(String name, String value) {
		SOLog.info("adding = " + value);

		int val = (int) Double.parseDouble(value);
		if (val==0){
			return; //no op
		}
		objectsCurrentState.ObjectRuntimeVariables.addToVariable(name, val);
		triggerActionsToRunWhenObjectVariableChanges(name);
	}

	public void setVariable(String name, String value) {


		String previous=objectsCurrentState.ObjectRuntimeVariables.setVariable(name, value);

		//only fire the change actions if it actually changed from the previous value
		if (previous==null || !previous.equals(value)){
			triggerActionsToRunWhenObjectVariableChanges(name);
		}

	}

	private void triggerActionsToRunWhenObjectVariableChanges(String name) {

		// first test global
		InstructionProcessor.testForGlobalActions(TriggerType.OnObjectVariableChanged, name, this);

		//then scene 
		if (!getObjectsCurrentState().ignoreSceneActions) {

			SOLog.info("testing for OnObjectVariableChanged specific to scene " + objectScene.SceneFileName + "...");
			// test for scene actions
			objectScene.testForSceneActions(TriggerType.OnObjectVariableChanged, name, this);
		} else {
			ObjectsLog("scene actions ignored (set ignoreSceneActions to false to make this work)");

		}
		CommandList actions = objectsActions.getActionsForTrigger(TriggerType.OnObjectVariableChanged, name);

		//then local
		if (actions!=null && !actions.isEmpty()){
			InstructionProcessor.processInstructions(actions,
					"ovc_" + getObjectsCurrentState().ObjectsName, this);
		}


	}

	public boolean checkVariable(String name, String equals) {

		return objectsCurrentState.ObjectRuntimeVariables.testValue(name, equals);
	}

	/**
	 * This stores most of the Actionsets in a more efficient to call manner. So
	 * that if you refer to it later, you can directly use a varible the stores them, rather than to go search through the whole
	 * actionset array.
	 * 
	 * So see it like a shortcut, or a shortloadedcut, or, well, whatever, it's
	 * more efficient.
	 * 
	 * We also check for invalid actions here. For example, OnFirstLoad applied to a inventory object will fire a warning<br>
	 * 
	 */
	public void updateHandlersToMatchActions() {

		// loop over actions
		Iterator<ActionSet> actionsIt = objectsActions.iterator();

		SOLog.info("updateHandlersToMatchActions on text box");

		while (actionsIt.hasNext()) {

			final ActionSet actionSet = actionsIt.next();

			// loop for each trigger in action set
			ArrayList<Trigger> triggers = actionSet.getTriggers();
			Iterator<Trigger> triggerIT = triggers.iterator();
			// SOLog.info("Updating handlers for actionset");

			while (triggerIT.hasNext()) {

				ActionSet.Trigger trigger = triggerIT.next();

				// SOLog.info("updateing trigger:" +
				// trigger.triggertype.toString());

				if (trigger.triggertype == TriggerType.MouseOverActions) {
					actionsToRunForMouseOver = actionSet;
					SOLog.info("mouse over set1" + actionSet.CommandsInSet.getActions().toString());
				}

				if (trigger.triggertype == TriggerType.MouseOutActions) {
					actionsToRunForMouseOut = actionSet;
				}

				if (trigger.triggertype == TriggerType.MouseClickActions) {

					if (actionsToRunForMouseClick==null){
						actionsToRunForMouseClick=new HashSet<ActionSet>();
					}
					actionsToRunForMouseClick.add( actionSet );
					SOLog.info("action set added with contents " + actionSet.CommandsInSet.getActions().toString());
				}

				if (trigger.triggertype == TriggerType.MouseDoubleClickActions) {
					actionsToRunForMouseDoubleClick = actionSet;
				}

				if (trigger.triggertype == TriggerType.MouseRightClickActions) {
					actionsToRunForMouseRightClick = actionSet;
				}

				if (trigger.triggertype == TriggerType.OnTouchingChange) {
					actionsToRunForWhenATouchingObjectChanges = actionSet;
				}

				if (trigger.triggertype == TriggerType.OnDirectionChanged) {

					actionsToRunOnDirectionChange = actionSet;

				}
				if (trigger.triggertype == TriggerType.OnStep) {

					// actionsToRunOnStep = actionSet;

					int stepActionIterval = Integer.parseInt(trigger.parameter);

					SOLog.info("creating step action interval set too" + stepActionIterval);

					actionsToRunOnStep.put(actionSet,new onStepInfo(stepActionIterval));

				}

				if (trigger.triggertype == TriggerType.OnFirstLoad) {

					SOLog.info("__________actionsToRunOnFirstLoad");

					if (objectsCurrentState.getPrimaryObjectType() == SceneObjectType.InventoryObject){
						SOLog.severe(" "+this.getName()+" HAS ONFIRSTLOADSET, yet inventory objects dont support this. Use OnItemAdded: instead");
						PreconstructionObjectsLog("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");						
						PreconstructionObjectsLog("WARNING: THIS OBJECT HAS A ONFIRSTLOAD SET, yet inventory objects dont support this. Use OnItemAdded: instead");
						PreconstructionObjectsLog("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

					}

					actionsToRunOnFirstLoad = actionSet;


				}
				if (trigger.triggertype == TriggerType.OnCloned) {

					SOLog.info("__________actionsToRunWhenCloned");
					SOLog.info(
							"__________actionsToRunWhenCloned = :" + actionSet.CommandsInSet.getActions().toString());

					actionsToRunWhenCloned = actionSet;
				}
				if (trigger.triggertype == TriggerType.OnReload) {

					SOLog.info("__________actionsToRunOnReload");
					SOLog.info("__________actionsToRunOnReload = :" + actionSet.CommandsInSet.getActions().toString());

					actionsToRunOnEveryReload = actionSet;
				}
			}

		}

	}

	// --positioning;
	public void updateRelativePosition(boolean updateTouching) {

		int ox = this.objectsCurrentState.relX;
		int oy = this.objectsCurrentState.relY;
		int oz = this.objectsCurrentState.relZ;

		setPosition(ox, oy, oz, true, false, updateTouching);

	}

	/**
	 * sets the z position without effecting its other co-ordinates or changing its relative status. 
	 * (ie, if its relative it will remain so with this as its new relz value, 
	 * if its absolute it will remain so with this as its new abs z position)
	 * 
	 * This is usefull when dealing with shadows, which are often placed at a fixed z when the rest of the object is moving in x/y
	 * @param z
	 * @param updateTouching
	 */
	public void setZPositionOnly(int z, boolean updateTouching) {

		//if z position isnt relative
		if (objectsCurrentState.positionedRelativeToo != null && objectsCurrentState.positionedRelativeLinkType.linkZ==false) {
			//relative in x/y, absolute in z


			//Note; the stored X/Y/Z has the pin position burnt in, so when setting one co-ordinate only
			//we dont do it by pin, except the new Z value we are supplying
			objectsCurrentState.Z=z- objectsCurrentState.CurrentPinPoint.z; //manually set the stored co-ordinate here as the setPosition wont update z unless linkZ is true
			updateRelativePosition(updateTouching);

			//else
		} else if (objectsCurrentState.positionedRelativeToo != null && objectsCurrentState.positionedRelativeLinkType.linkZ==true){
			//Relatively positioned with linkZ true  (ie, all co-ordinates relative)
			//relative in x/y,z

			objectsCurrentState.relZ = z; //onlychange z
			updateRelativePosition(updateTouching);
		} else {			
			//absolute in all

			objectsCurrentState.Z=z- objectsCurrentState.CurrentPinPoint.z; //manually set the stored co-ordinate here as the setPosition wont update z unless linkZ is true
			//note; running the below is a bit wasteful as we only need the functions at the end of it
			setPosition(objectsCurrentState.X, objectsCurrentState.Y,objectsCurrentState.Z, false, true, updateTouching);

		}

	}


	public void setPosition(int x, int y, int z, boolean updateTouching) {

		if (objectsCurrentState.positionedRelativeToo == null) {

			setPosition(x, y, z, true, true, updateTouching);

		} else {

			objectsCurrentState.relX = x;
			objectsCurrentState.relY = y;
			objectsCurrentState.relZ = z;

			updateRelativePosition(updateTouching);
		}

	}

	/**
	 * the height of the ""floor"" directly under us
	 */
	int floor_height = 0;

	/**
	 * Removes this object from being relatively positioned
	 */
	public void detach() {

		// we obviously only attempt to unlink if we are relatively positioned
		// to start with

		if (getObjectsCurrentState().positionedRelativeToo != null) {

			SceneObject wasPositionedRelativeToo = getObjectsCurrentState().positionedRelativeToo;
			SOLog.info("Detaching from:" + wasPositionedRelativeToo.getName());


			getObjectsCurrentState().positionedRelativeToo.removeChild(this);
			getObjectsCurrentState().positionedRelativeToo = null;
			getObjectsCurrentState().positionedRelativeToPoint = "";
			getObjectsCurrentState().linkedZindex = false; // when we have no
			// relative object,
			// we can't have a
			// linkedzindex
			// either

			getObjectsCurrentState().relX = 0;
			getObjectsCurrentState().relY = 0;
			getObjectsCurrentState().relZ = 0;


			Optional<MovementState> parentsMoveState = wasPositionedRelativeToo.getObjectsCurrentState().moveState;
			// if parent was moving under physics, we inherit the physics from our parent
			if (parentsMoveState.isPresent() && parentsMoveState.get().isMovingUnderPhysics()) {

				SOLog.info("Inheriting movement from parent");

				//make a copy
				getObjectsCurrentState().moveState = Optional.of(parentsMoveState.get().copy()); //wasPositionedRelativeToo.getObjectsCurrentState().moveState.copy();
				// parentBeforeDropping =
				// wasPositionedRelativeToo.parentBeforeDropping; //we re-attach
				// to the same thing as our parent will, if any

				startCurrentMovement();

				ObjectsLog("___Inheriting movement from parent:" + getObjectsCurrentState().moveState.toString(),
						"orange");

			}

		}

	}


	public void removeChild(SceneObject sceneObject) {
		relativeObjects.remove(sceneObject);
		//invalidate a compoundmap if we have one
		invalidateCompoundCmap();
	}

	public void addChild(SceneObject sceneObject) {
		relativeObjects.add(sceneObject);
		//invalidate a compoundmap if we have one
		invalidateCompoundCmap();
	}

	
	/**
	 * any time our child objects change this should be fired
	 */
	public void invalidateCompoundCmap() {
		newCompoundMap=null;
		//and our parents,if there's one
		if (this.getObjectsCurrentState().positionedRelativeToo!=null){
			this.getObjectsCurrentState().positionedRelativeToo.invalidateCompoundCmap();
		}

	}
	public boolean hasCompoundMap() {
		if (newCompoundMap!=null){
			return true;
		}
		return false;
	}
	/**
	 * opersite of detach. This will link this object to the specified one,
	 * keeping the current displacement from it as the new relative displacement
	 * 
	 * @param parentBeforeDropping2
	 */
	public void attachTo(SceneObject newParent) {

		// first we set the new relative positions as our current displacement
		ObjectsLog("attaching to parent object:" + newParent.getName(), "green");

		getObjectsCurrentState().relX = getX() - newParent.getX();
		getObjectsCurrentState().relY = getY() - newParent.getY();
		getObjectsCurrentState().relZ = getZ() - newParent.getZ();

		ObjectsLog(getObjectsCurrentState().relX + " = " + getX() + " - " + newParent.getX(), "yellow");
		ObjectsLog(getObjectsCurrentState().relY + " = " + getY() + " - " + newParent.getY(), "yellow");
		ObjectsLog(getObjectsCurrentState().relZ + " = " + getZ() + " - " + newParent.getZ(), "yellow");

		newParent.addChild(this);
		getObjectsCurrentState().positionedRelativeToo = newParent;
		getObjectsCurrentState().positionedRelativeToPoint = ""; // no support
		// for
		// specific
		// positions
		// when
		// using
		// this
		// function

		updateDebugInfo();
	}

	/**
	 * Drops the object down the screen by the default gravitational
	 * acceleration, detaching it from allparent objects first<br>
	 * you can specify, however, how bouncy the object is<br>
	 * <br>
	 * <br>
	 * 
	 * @param bounceEnergyRetention<br>
	 */
	public void dropToFloor(double bounceEnergyRetention) {
		SOLog.info("bounceEnergyRetention:" + bounceEnergyRetention);

		ObjectsLog("(dropping to ground so clearing parentBeforeDropping )" + bounceEnergyRetention, "red");

		//if we had a move state we clear a few things
		if (objectsCurrentState.moveState.isPresent()){
			objectsCurrentState.moveState.get().parentBeforeDropping = null; // ensure we arnt re-attaching to anything after drop
			objectsCurrentState.moveState.get().hasLinkedZindexBeforeDropping = false;
		}

		// Unlink from parent if there's one
		detach();

		// work out the height of the floor
		floor_height = 0; // in future we calculate the height of what's under
		// us

		dropTo(bounceEnergyRetention);
	}

	/**
	 * Drops the object down the screen by the default gravitational
	 * acceleration, detaching it from allparent objects first<br>
	 * you can specify, however, how bouncy the object is<br>
	 * <br>
	 * <br>
	 * 
	 * @param bounceEnergyRetention<br>
	 */
	public void dropToParentsBase(double bounceEnergyRetention) {
		SOLog.info("bounceEnergyRetention:" + bounceEnergyRetention);

		//ensure movementstate is present (as we need one too drop)
		objectsCurrentState.createMovementStateIfNeeded();

		// remember parent
		// (This is because while falling we are not attached to anything!
		// When the object lands it auto reattaches to this parent.
		// In future we might instead auto-attach to anything under us, but that
		// would require true 3d)
		SceneObject parentBeforeDrop = getObjectsCurrentState().positionedRelativeToo;


		if (getObjectsCurrentState().positionedRelativeToo == null) {
			ObjectsLog("(no parent to this object1)");

		}
		//if (objectsCurrentState.moveState.parentBeforeDropping == null) {
		//	ObjectsLog("(no parent to this object2)");
		//}

		// work out the height of the floor
		floor_height = parentBeforeDrop.getTopLeftBaseZ(); // in future we

		//cancel if parent is above us already (we cant drop to whats above!)
		if (floor_height>getZ()){
			ObjectsLog("(parent base is already above us! canceling droptoparentbase)");
			return;
		}


		objectsCurrentState.moveState.get().parentBeforeDropping = parentBeforeDrop.getName();
		objectsCurrentState.moveState.get().hasLinkedZindexBeforeDropping = getObjectsCurrentState().linkedZindex;



		// Unlink from parent if there's one
		detach();


		// calculate the
		// height of what's
		// under us

		ObjectsLog("parentBeforeDropping=" + objectsCurrentState.moveState.get().parentBeforeDropping+" (base z="+floor_height+") our z="+this.getZ());

		dropTo(bounceEnergyRetention);

	}

	/**
	 * Drops the object down the screen by the default gravitational
	 * acceleration, detaching it from allparent objects first<br>
	 * you can specify, however, how bouncy the object is<br>
	 * <br>
	 * <br>
	 * 
	 * @param bounceEnergyRetention<br>
	 */
	public void dropTo(double bounceEnergyRetention) {

		//TODO: hmz. long deltas can mess up bounces
		//We could either;
		//a)Figure out a maxdelta based on start height
		//or b) Figure out maxheight per-bounce then cap z to that each time 
		//b might look odd timing wise
		//a would mean running multi updates in a single frame.
		//
		
		SOLog.info("dropping object:" + this.getName());

		//ensure movementstate is present (as we need one too drop)
		objectsCurrentState.createMovementStateIfNeeded();

		// clear any existing movement settings
		objectsCurrentState.moveState.get().clearMovements();

		// if we are already on the ""floor"" we do nothing
		if (getZ() == floor_height) {

			ObjectsLog("(object already on ground at " + floor_height + ")", "yellow");

			if (objectsCurrentState.moveState.get().parentBeforeDropping != null) {
				String parentName = objectsCurrentState.moveState.get().parentBeforeDropping;
				ObjectsLog("Reattaching to parent object:" + parentName);

				SceneObject parentObject = SceneObjectDatabase.getSingleSceneObjectNEW(parentName, null, true); // TODO:Once
				// scene
				// specific
				// searchs
				// are
				// supported
				// this
				// TRUE
				// could
				// be
				// false

				attachTo(parentObject);

				if (this.objectsCurrentState.moveState.get().hasLinkedZindexBeforeDropping) {
					this.setZIndexAsLinked(true, this.getObjectsCurrentState().linkedZindexDifference);
				}

			}

			return;
		}

		// set movement type
		objectsCurrentState.moveState.get().currentmovementtype = MovementStateType.PhysicsBased;
		
		if (bounceEnergyRetention==-1){ //default, means use objects state value
			bounceEnergyRetention= getObjectsCurrentState().bounceEnergyRetention;
		}
		objectsCurrentState.moveState.get().setBounceEnergyRetention(bounceEnergyRetention);

		// set velocity to zero
		objectsCurrentState.moveState.get().setVelocity(0, 0, 0); // set our velocity
		
		// set gravity 
		objectsCurrentState.moveState.get().setAcceleration(0, 0, MovementState.GravityMS); // set no acc except gravity
		
		

		// update position
		objectsCurrentState.moveState.get().setPosition(getObjectsCurrentState().getX(), getObjectsCurrentState().getY(),
				getObjectsCurrentState().getZ());
		//todo; .setPosition(this.getX(),this.getY(),this.getZ()); maybe? rather then using the state directly?

		// start movements
		startCurrentMovement();

		SOLog.info("starting drop:" + this.getName());

		ObjectsLog(
				"_______Dropping object:" + objectsCurrentState.moveState.get().movement_vel.toString() + " - acc:"
						+ objectsCurrentState.moveState.get().movement_acc.toString() + " - bounce:"
						+ objectsCurrentState.moveState.get().getBounceEnergyRetention() + " -floor:" + floor_height,
				"green");

		updateDebugInfo();

	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param byPin
	 * @param autoUpdateZindex
	 * @param updateTouching
	 *            - if true will update the touching objects and polygon
	 *            collision cache for this object. Only use this at the end of a
	 *            animation
	 */
	public void setPosition(int x, int y, int z, boolean byPin, boolean autoUpdateZindex, boolean updateTouching) {

		// if its positioned by Div, we ignore all this.
		// position by div must be removed before any other movements are
		// possible
		if (this.objectsCurrentState.attachToHTMLDiv.length() > 1) {
			ObjectsLog("cant position by location, as its set to a DIV", LogLevel.Error);
			return;
		}

		//We no longer update the currentstate variables here, but rather after we determine if we are using relative positioning or not
		//This is because when using selective relative updates (ie, move in x/y but not z) we want to preserve the original co-ordinate for the axis it didnt move in
		int newX = x;
		int newY = y;
		int newZ = z;

		if (byPin) {			
			newX = newX - objectsCurrentState.CurrentPinPoint.x;
			newY = newY - objectsCurrentState.CurrentPinPoint.y;
			newZ = newZ - objectsCurrentState.CurrentPinPoint.z;			
			//objectsCurrentState.X = x - objectsCurrentState.PinPointX;
			//objectsCurrentState.Y = y - objectsCurrentState.PinPointY;
			//objectsCurrentState.Z = z - objectsCurrentState.PinPointZ;
		} else {
			//objectsCurrentState.X = x;
			//objectsCurrentState.Y = y;
			//objectsCurrentState.Z = z;
		}

		if (objectsCurrentState.positionedRelativeToo != null) {
			// if its positioned relatively
			ObjectsLog("positioning relatively to:" + objectsCurrentState.positionedRelativeToo.getName());

			final String posTo = this.objectsCurrentState.positionedRelativeToPoint;

			int RX = 0;
			int RY = 0;
			int RZ = 0;

			if (posTo.length() == 0) {

				// get by the pin
				RX = objectsCurrentState.positionedRelativeToo.getX();
				RY = objectsCurrentState.positionedRelativeToo.getY();
				RZ = objectsCurrentState.positionedRelativeToo.getZ();

			} else {
				SOLog.info("positioning to:" + posTo);

				// get by the attachment point
				MovementWaypoint glueToo = objectsCurrentState.positionedRelativeToo.getAttachmentPointsFor(posTo);

				if (glueToo != null) {

					RX = objectsCurrentState.positionedRelativeToo.getTopLeftBaseX() + glueToo.pos.x;
					RY = objectsCurrentState.positionedRelativeToo.getTopLeftBaseY() + glueToo.pos.y;
					RZ = objectsCurrentState.positionedRelativeToo.getTopLeftBaseZ() + glueToo.pos.z;

				} else {
					// we have to default to pin as there's no attachment of the
					// specified name
					RX = objectsCurrentState.positionedRelativeToo.getX();
					RY = objectsCurrentState.positionedRelativeToo.getY();
					RZ = objectsCurrentState.positionedRelativeToo.getZ();

				}

			}

			ObjectsLog("positioning relatively :" + newX + "," + newY + ","
					+ newZ + " + " + RX + "," + RY + "," + RZ);

			if (objectsCurrentState.positionedRelativeLinkType.linkX){				
				objectsCurrentState.X = newX + RX;
			}

			if (objectsCurrentState.positionedRelativeLinkType.linkY){
				objectsCurrentState.Y = newY + RY;
			}

			if (objectsCurrentState.positionedRelativeLinkType.linkZ){
				objectsCurrentState.Z = newZ + RZ;
			}

			//ObjectsLog("positioning at:" + objectsCurrentState.X + "," + objectsCurrentState.Y + ","+ objectsCurrentState.Z);


		} else {

			//	ObjectsLog("positioning absolutely :" + newX + "," + newY + ","		+ newZ );
			//non relative positioning we just assign the co-ordinates asked for
			objectsCurrentState.X=newX;
			objectsCurrentState.Y=newY;
			objectsCurrentState.Z=newZ;

		}

		if (autoUpdateZindex) {

			setZIndexByPosition(objectsCurrentState.Y);

		}

		// ObjectsLog(" x="+ objectsCurrentState.X+",
		// y="+objectsCurrentState.Y+",z="+objectsCurrentState.Z+" restrict to
		// screen="+objectsCurrentState.restrictPositionToScreen );

		setPositionOnItsScene(objectsCurrentState.X, objectsCurrentState.Y, objectsCurrentState.Z,
				objectsCurrentState.restrictPositionToScreen);

		// GreySOLog.info("updateThingsPositionedRelativeToThis:");
		//SOLog.info("now updating objects relative to " + this.getName() + ":" + this.relativeObjects.size());

		// ObjectsLog("(updating things positioned relatively");
		updateThingsPositionedRelativeToThis(updateTouching);

		if (updateTouching) {
			wasJustMoved(false);// (probably too intensive to check this in this
			// function as this is called constantly within
			// animations)
		}

		// ObjectsLog("(finnished position change)");
	}

	/**
	 * Updates just the internal values of how its positioned relatively. Really
	 * this should only be used after an edit, or in conjunction with moving the
	 * real object on the page - because this does not really move it at all
	 ***/
	public void updateRealtivePositionData(int x, int y, int z) {

		SOLog.info("updateRealtivePositionData");

		objectsCurrentState.relX = x;
		objectsCurrentState.relY = y;
		objectsCurrentState.relZ = z;

		updateDebugInfo();
		// if (oipu != null) {
		// oipu.update();
		// }
	}

	/**
	 * works out and remembers how the z-index of this item changes for in its
	 * current scene. This should be retriggered if the scene changes
	 * 
	 * It divides the scene up in vertical sections. When an object moves from
	 * one section to the other, it updates the z-index of that object.
	 * 
	 * This is the setup. This works out the number of divisions given the
	 * height.
	 */
	protected void setUpVariableZindex() {

		numberOfDivisions = (int) Math.round(
				(objectsCurrentState.upperZindex - objectsCurrentState.lowerZindex) / objectsCurrentState.stepZindex);
		ObjectsLog("numberOfDivisions" + numberOfDivisions);
		ObjectsLog("scenes Y size = " + this.objectScene.getScenesData().InternalSizeY);
		pixelstep = (int) Math.round(this.objectScene.getScenesData().InternalSizeY / numberOfDivisions);
		ObjectsLog("pixel step=" + pixelstep);

	}

	/**
	 * updates just the internal values of how its positioned as well as
	 * retesting the variable zindex(if any) Really this should only be used
	 * after an edit, or in conjunction with moving the real object on the page
	 * - because this does not really move it at all
	 ***/
	public void updatePositionData(int x, int y, int z) {

		SOLog.info("updatePositionData");

		objectsCurrentState.X = x;
		objectsCurrentState.Y = y;
		objectsCurrentState.Z = z;

		if (this.getObjectsCurrentState().variableZindex) {
			setZIndexByPosition(this.getY());
		}

		updateDebugInfo();
	}

	//
	// /**
	// * Gets the co-ordinate of the specified attachment point at the current
	// frame.
	// * NOTE: Objects which can have multiple frames, such as sprite
	// animations, should override this
	// * to use points.getPointsFor(atachmentPointName, currentFrame); instead
	// ***/
	// public MovementWaypoint getAttachmentPointsFor(String atachmentPointName)
	// {
	//
	// AttachmentList points = this.attachmentPoints;
	//
	// if (points!=null){
	// int currentFrame =0;
	// //NOTE: As a SceneObject we can only assume a currentFrame of zero
	// //Methods using this supertype should override to supply the current
	// frame here
	// return points.getPointsFor(atachmentPointName, currentFrame);
	//
	// } else {
	// SOLog.info( "no attachment points found for attachment
	// name:"+atachmentPointName);
	// }
	//
	//
	//
	// return null;
	// }

	/**
	 * Gets the co-ordinate of the specified attachment point at the current
	 * frame. NOTE: Objects which can have multiple frames, such as sprite
	 * animations, should override this to use
	 * points.getPointsFor(atachmentPointName, currentFrame); instead
	 **/
	public MovementWaypoint getAttachmentPointsFor(String atachmentPointName) {

		AttachmentList points = this.attachmentPoints;

		if (points != null) {
			int currentFrame = 0;
			// if its a sprite, we check the frame! Else, we assume zero.
			if (this.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Sprite)) {
				// currentFrame =
				// this.getAsSprite().SceneObjectIcon.getCurrentframe();
				// //faster?
				currentFrame = ((SceneSpriteObjectState) getObjectsCurrentState()).currentFrame;
				SOLog.info("getting glu point for frame:" + currentFrame);
			}
			return points.getPointsFor(atachmentPointName, currentFrame);

		} else {
			SOLog.info("no attachment points found for :" + atachmentPointName + "(0)");
		}

		return null;
	}

	/**
	 * Is this object currently moving ?
	 * 
	 * @return
	 */
	public boolean isMoving() {
		if (!objectsCurrentState.moveState.isPresent()){
			return false;
		}		
		return objectsCurrentState.moveState.get().isMoving();
	}

	protected void startCurrentMovement() {

		this.ObjectsLog("(movement start)");

		// isMoving = true;
		// objectsCurrentState.moveState.isMoving = true;

		// objectsCurrentState.moveState.currentmovementtype =
		// currentMovementType.OnLinePath; //default

		// new method
		JAMTimerController.addObjectToUpdateOnFrame(this);

		collisionCheckPool = null; // ensure the pool is cleared ready to be
		// recreated

		currentBounce = 0;

		// animationRunner =
		// AnimationScheduler.get().requestAnimationFrame(animationUpdateCode,this.getElementOnPanel());
	}

	/**
	 * updates the objects direction variable to face the specified co-ordinates
	 **/
	protected void updateObjectsDirection(int DX, int DY) {

		int sx = this.getX();
		int sy = this.getY();

		int differenceX = (sx - DX);
		int differenceY = (sy - DY);

		double ang = Math.atan2(differenceY, differenceX);
		ang = Math.toDegrees(ang);

		// We remove 90 degrees to compensate for the startpoint

		FacingDirection = ang - 90;

		if (FacingDirection < 0) {

			FacingDirection = 360 + FacingDirection;

		}
		if (FacingDirection > 360) {

			FacingDirection = FacingDirection - 360;

		}

		ObjectsLog("angle =" + FacingDirection);

		runOnDirectionChangeActions();

	}

	protected void stopNextMovementFrame() {

		// new method
		JAMTimerController.removeObjectToUpdateOnFrame(this);

		// isMoving = false;
		// objectsCurrentState.moveState.isMoving = false;

		//if we have movements set them to none. (we presumably do if we are calling this, after all)
		if (objectsCurrentState.moveState.isPresent()){
			objectsCurrentState.moveState.get().currentmovementtype = MovementStateType.None;
		}
	}

	/**
	 * New delta based animation system. This statement is called each time the
	 * browser has a free moment, and this object has been set to animate
	 */
	public void update(float delta) {

		// if we arnt moving and not fadeing we remove from the updates
		if (!isMoving() && currentFade == FadeMode.None) {
			JAMTimerController.removeObjectToUpdateOnFrame(this);
			return;
		}

		// if moving we update the movement
		if (isMoving()) {
			movementFrameUpdate(delta);
		}

		// if fading we update the fade
		if (currentFade != FadeMode.None) {
			updateFade(delta);
		}

		// old below
		// if (!isMoving()) {
		// JAMTimer.removeObjectToUpdateOnFrame(this);
		// return;
		// }

		// movementFrameUpdate(delta);
	}

	double	StepX	= 0;
	double	StepY	= 0;

	/**
	 * This will update the movement between two points. Its more efficient then
	 * movementFrameUpdate, so if we arnt on a path and just moving from point
	 * to point we use this. (NOT USED(
	 * 
	 * @param deltaTime
	 *            - the time since the last update (currently not used as
	 *            framerate is fixed at interval)
	 **/
	@Deprecated
	void simpleMovementFrameUpdate(double deltaTime) {

		int cx = getX();
		int cy = getY();

		if (Math.abs(cx - DestinationX) <= Math.abs(StepX)) {
			cx = DestinationX;
		} else {

			double newcx = (getX() + StepX) + remainder.x;
			cx = (int) Math.round(newcx);

			// keep track of remainder
			remainder.x = newcx - cx;

		}

		if (Math.abs(cy - DestinationY) <= Math.abs(StepY)) {
			cy = DestinationY;
		} else {
			double newcy = (getY() + StepY) + remainder.y;
			cy = (int) Math.round(newcy);

			// keep track of remainder
			remainder.y = newcy - cy;
		}

		setPosition(cx, cy, 0, false); // 0 should be cz

		if ((cy == DestinationY) && (cx == DestinationX)) {

			remainder.x = 0;
			remainder.y = 0; // was missing
			remainder.z = 0;

			stopNextMovementFrame();
			wasJustMoved(true);
			// triggerMovementEndCommands();
		}

	}

	/**
	 * Plays a specific movement on this object. The movement is defined by
	 * name, and this will first check object specific motions and if none was
	 * found look for a global one
	 * 
	 * @param MovementName
	 * @param Duration
	 */
	public void playMovement(String MovementName, int Duration) {
		// , int Interval has been removed
		playMovement(MovementName, Duration, -1, true);

	}

	/**
	 * Plays a specific movement on this object. The movement is defined by
	 * name, and this will first check object specific motions and if none was
	 * found look for a global one
	 * 
	 * @param MovementName
	 * @param Duration
	 * @param FromNode
	 * @param gotopos
	 */
	public void playMovement(String MovementName, int Duration, int FromNode, boolean gotopos) {

		objectsCurrentState.createMovementStateIfNeeded();
		MovementState ourMovementState = objectsCurrentState.moveState.get(); //safe to get as we created it above if needed
	
		
		// return if movements not ready (in future queue)
		if (movementsLoaded != false) {
			ourMovementState.currentPathData = objectsMovements.getMovement(MovementName);
			// Note: there still might be no movements found, in which case it
			// returns null and we let the check below for global movements also
			// run
		} else {
			// SOLog.info("attempted to play, but movements arnt loaded");
			// No object movements, but there might still be global
			ourMovementState.currentPathData = null;
		}

		if (ourMovementState.currentPathData == null) {
			// no object specific movement called this, so we check for global
			// ones
			ourMovementState.currentPathData = MovementList.globalMovements.get(MovementName);

			if (ourMovementState.currentPathData == null) {
				SOLog.severe("Warning path: " + MovementName + " not recognised as either local or global");
				return;
			}

		}

		playMovement(ourMovementState.currentPathData, Duration, FromNode, gotopos);

	}

	/**
	 * Plays a specific movement on this object.
	 * 
	 * @param MovementPath
	 * @param Duration
	 */
	public void playMovement(MovementPath MovementPath, int Duration) {
		// , int Interval
		playMovement(MovementPath, Duration, -1, false);

	}

	/** Speed is measured in pixels per second **/
	public void playMovementAtFixedSpeed(MovementPath path, int speed) {

		SOLog.info("_______________________playing path:" + path.getAsSVGPath());

		// int Interval
		// calc duration from length
		double Distance = path.PathLength;

		SOLog.info("_______________________________________________________Distance :" + Distance);

		// SOLog.info("current distance="+Distance);
		SOLog.info("current speed=" + speed);
		int Duration = (int) (Distance / (speed / 1000.0)); // convert to ms
		SOLog.info("current Duration=" + Duration);

		// 450 / ( X / 1000)
		// 450 * 1000 / X

		playMovement(path, Duration, -1, false);

	}

	/**
	 * Plays the specified movement
	 * 
	 * @param path
	 *            - the movement to play
	 * @param Duration
	 *            - duration to take to move over this path (apr)
	 * @param FromNode
	 *            - node to start from?
	 * @param gotopos
	 *            - ?
	 */
	public void playMovement(MovementPath path, int Duration, int FromNode, boolean gotopos) {

		if (isMoving()) {
			SOLog.info("Movement :" +  objectsCurrentState.moveState.get().currentPathData.pathsName + " was playing on "
					+ this.getObjectsCurrentState().ObjectsName + ", so we are stopping it");
			StopCurrentMovement();
		}

		skipFirstJump = gotopos;



		//first we create a movestate if we dont already have one
		objectsCurrentState.createMovementStateIfNeeded();
		MovementState ourMovementState = objectsCurrentState.moveState.get(); //safe to get as we created it above if needed
		
		// get the right movement
		ourMovementState.currentPathData = path;

		//then set the starting values
		ourMovementState.currentmovementtype = MovementStateType.OnLinePath;
		ourMovementState.movement_curveTime = 0;
		ourMovementState.movement_curveTimeStep = 0;

		// work out the right settings to run
		ourMovementState.currentmovementpathname = ourMovementState.currentPathData.pathsName;
		ourMovementState.movement_currentWaypoint = FromNode; // current

		// start location set
		// NOTE: If we are positioned relative, our movements position will also
		// be relative
		// For that reason we are measuring relative to our existing relative
		// displacement
		if (this.getObjectsCurrentState().positionedRelativeToo != null) {
			// TODO: does this work?
			ObjectsLog("position relative too movement mode");

			ourMovementState.movement_current_pos.x = objectsCurrentState.relX;
			ourMovementState.movement_current_pos.y = objectsCurrentState.relY;
			ourMovementState.movement_current_pos.z = objectsCurrentState.relZ;

		} else {

			ourMovementState.movement_current_pos.x = objectsCurrentState.getX();
			ourMovementState.movement_current_pos.y = objectsCurrentState.getY();
			ourMovementState.movement_current_pos.z = objectsCurrentState.getZ();

		}

		// number of
		// waypoint (0
		// is
		// the
		// first, so -1 is before the first)

		// the following ensures the destination and step is set on first firing
		// of timer
		ourMovementState.movement_dest.x = (int) ourMovementState.movement_current_pos.x;
		ourMovementState.movement_dest.y = (int) ourMovementState.movement_current_pos.y;
		ourMovementState.movement_dest.z = (int) ourMovementState.movement_current_pos.z;

		ourMovementState.movement_vel.x = 5;
		ourMovementState.movement_vel.y = 5;
		ourMovementState.movement_vel.z = 5;

		// distance of 100
		// in 20 seconds
		// =5 steps

		double speedInMs = path.PathLength / (double) Duration;
		ourMovementState.movement_speed = (speedInMs * 1); // 100
		// is
		// current
		// interval

		// start the timer on this path

		SOLog.info("__________running path waypoint total:" + ourMovementState.currentPathData.size());
		SOLog.info("__________running path :" + ourMovementState.currentPathData.getAsSVGPath(true));
		SOLog.info("__________running pathlength :" + ourMovementState.currentPathData.PathLength);

		SOLog.info("__________movement_speed:" + ourMovementState.movement_speed);
		SOLog.info("__________speedInMs:" + speedInMs);
		SOLog.info("__________movement_Dest:" + ourMovementState.movement_dest.toString());
		SOLog.info(
				"__________movement_current position:" + ourMovementState.movement_current_pos.toString());

		SOLog.info("__________getX:" + getX() + "_______getY:" + getY() + "_______getZ:" + getZ());
		SOLog.info("__________FromNode:" + ourMovementState.movement_currentWaypoint);

		ObjectsLog("_________Playing Movement:" + ourMovementState.currentPathData.pathsName, "green");

		// objectsCurrentState.moveState.isMoving = true;
		startCurrentMovement();

	}

	/**
	 * This will update the objects movement along its current path based on the
	 * delta time
	 * 
	 * @param deltaTime
	 *            - the time since the last update in ms
	 **/
	protected void movementFrameUpdate(double DELTA_TIME) {

		// NB: Check curve movement is handled right Not sure if CurveTimeStep
		// should be just multiplied by delta

		if (!objectsCurrentState.moveState.isPresent()){
			SOLog.severe("Warning: frame update called but no moveState created");

		}


		MovementState ourMoveState = objectsCurrentState.moveState.get(); //putting this in ourMoveState just to save us writting it out fully each time


		// If we are on physics based movement we use a much more simple
		// functions

		if (ourMoveState.currentmovementtype == MovementStateType.PhysicsBased) {

			movementPhysicsBasedUpdate(DELTA_TIME);

			// if we are still moving after the update, we do whats needed to
			// visually reflect the change

			if (ourMoveState.currentmovementtype == MovementStateType.PhysicsBased) {

				//ObjectsLog("updating position: Y="+ourMoveState.get_currentY_as_int());



				// need to update onstep

				// need to update direction

				// need to update position
				setPosition(
						ourMoveState.get_currentX_as_int(),
						ourMoveState.get_currentY_as_int(),
						ourMoveState.get_currentZ_as_int(), false);

			}
			return;
		} else if (ourMoveState.currentmovementtype == MovementStateType.None) {
			// means we have been stoped between frames, so we just return
			return;
		}

		// WAYPOINT MOVEMENT:

		// because the delta update might be big enough to travel to a few
		// waypoints
		// we should loop until our destination is outside this updates range
		// ie, if we are moving 100 pixels due to a poor framerate, how many
		// waypoints do we cross? which waypoint are we now heading too?

		// (note all checks for destination right now are 2d, amongst other
		// things)
		boolean atAWayPoint = testIfWeAreWithinThresholdOfWaypoint(DELTA_TIME);

		while (atAWayPoint) {

			// we are at the waypoint, so we snap to this one and reset values;
			SOLog.info(this.getName() + " at waypoint:" + ourMoveState.movement_currentWaypoint
					+ " in path:" + ourMoveState.getCurrentPathName());
			ObjectsLog("at waypoint" + ourMoveState.movement_currentWaypoint, "grey");

			// snap to current waypoint reached for consistency in
			// movement
			ourMoveState.movement_current_pos.x = ourMoveState.movement_dest.x;
			ourMoveState.movement_current_pos.y = ourMoveState.movement_dest.y;
			ourMoveState.movement_current_pos.z = ourMoveState.movement_dest.z;

			// remove flags
			// objectsCurrentState.moveState.movement_onCurve = false;
			ourMoveState.currentmovementtype = MovementStateType.OnLinePath;
			ourMoveState.movement_SC.x = -1;
			ourMoveState.movement_SC.y = -1;
			ourMoveState.movement_SC.z = -1;

			remainder.x = 0;// was missing (although should be too small to
			// mater)
			remainder.y = 0;
			remainder.z = 0;

			// Increase the waypoint num we are on by one
			ourMoveState.movement_currentWaypoint = ourMoveState.movement_currentWaypoint
					+ 1;

			// is there still a path running?
			// if so we check if there is more waypoints and we get the next one
			// Note; currentPath can be null if a internal movement was being
			// played at the time something was saved.
			if (ourMoveState.currentPathData != null && ourMoveState.movement_currentWaypoint < ourMoveState.currentPathData.size()) {

				int prevDX = ourMoveState.movement_dest.x;
				int prevDY = ourMoveState.movement_dest.y;
				int prevDZ = ourMoveState.movement_dest.z;

				ObjectsLog("last waypoint was " + ourMoveState.movement_current_pos.toString());

				// sets the next waypoint as active, including the current
				// movement step values as well as if we are teleporting or not
				// this might also trigger mid-movement commands
				MovementWaypoint cp = setNextWayPointMovement();

				// if the waypoint was a command we return straight away as no
				// movement should take place the same update as a command
				// (this is to give the command a chance to stop the motion if
				// desired)
				if (cp.type == MovementType.Command) {
					return;
				}

				// we need to them check again if we are within the threshold,
				// so we loop back over

				// we need some safety here to avoid a infinite loop however
				// if the threshold is too big potentially we could loop forever
				// presumably we need to reduce the delta time at each step?
				atAWayPoint = testIfWeAreWithinThresholdOfWaypoint(DELTA_TIME);

				// if we are at a waypoint then reduce the delta time to ensure
				// we dont loop forever
				if (atAWayPoint) {

					// SOLog.info("at waypoint");//("+speedX+" , "+SpeedY+")" );

					// the following maths to work out the duration it should
					// have taken
					// can be badly optimized - get rid of those hypot! We dont
					// need squareroots so much!

					float distancebetweenpointsX = Math.abs(prevDX - ourMoveState.movement_dest.x);
					float distancebetweenpointsY = Math.abs(prevDY - ourMoveState.movement_dest.y);
					float distancebetweenpointsZ = Math.abs(prevDZ - ourMoveState.movement_dest.z);

					// double dis = Math.hypot(distancebetweenpointsX,
					// distancebetweenpointsY);
					double dis = Math.sqrt(Math.pow(distancebetweenpointsX, 2) + Math.pow(distancebetweenpointsY, 2)
					+ Math.pow(distancebetweenpointsZ, 2));

					// ensure minimum distance
					if (dis < 1) {
						dis = 1;
					}

					// double speedX =
					// Math.abs(objectsCurrentState.moveState.movement_StepX);
					/// double SpeedY =
					// Math.abs(objectsCurrentState.moveState.movement_StepY);
					double speed = ourMoveState.movement_speed;// Math.hypot(speedX,
					// SpeedY);

					// SOLog.info("speed:"+speed);//("+speedX+" , "+SpeedY+")"
					// );

					double timeItshouldHaveTakenToGetThere = dis / speed;

					// SOLog.info("delta time being reduced
					// by:"+timeItshouldHaveTakenToGetThere +"("+dis+" /
					// "+speed+")" );

					DELTA_TIME = DELTA_TIME - timeItshouldHaveTakenToGetThere;// time
					// we
					// should
					// have
					// taken
					// to
					// get
					// there

					// SOLog.info("is now:"+DELTA_TIME );

					// ensure we dont go negative
					if (DELTA_TIME < 1) {
						DELTA_TIME = 1;
					}

				}

				continue; // loop
			} else {

				// if at end, stop
				SOLog.info("end of movement path");

				ObjectsLog("___end of movement path, last Delta was " + DELTA_TIME, "purple");

				setPosition(
						(int)ourMoveState.movement_current_pos.x,
						(int)ourMoveState.movement_current_pos.y,
						(int)ourMoveState.movement_current_pos.z, false);

				remainder.x = 0;
				remainder.y = 0;
				remainder.z = 0;

				// animationCallback.cancel();
				// stopNextMovementFrame();
				this.StopCurrentMovement();

				// isMoving = false;
				// path specific commands
				if (ourMoveState.currentPathData != null && ourMoveState.currentPathData.postAnimationCommands != null) {
					ourMoveState.currentPathData.postAnimationCommands.run();
				} else {
					SOLog.info("no post animation commands found");
				}

				// generic movement end commands
				// wasJustMoved(true);
				// triggerMovementEndCommands();

				return;
			}
			// };

		}

		// Now we have the right waypoint to move towards, we process the
		// movement
		if (!telePortFlag && !ourMoveState.is_onCurve()) {

			// if its not a teleport waypoint continue movement as we
			// were before
			if (ourMoveState.movement_current_pos.x != ourMoveState.movement_dest.x) {

				double stepX = ourMoveState.movement_vel.x * DELTA_TIME;
				double newX = remainder.x + (ourMoveState.movement_current_pos.x + stepX);

				ourMoveState.movement_current_pos.x = (int) newX;

				remainder.x = newX - ourMoveState.movement_current_pos.x;

				ObjectsLog("newX=" + newX + " stepX was=" + stepX + " Delta was " + DELTA_TIME, "purple");
			}

			if (ourMoveState.movement_current_pos.y != ourMoveState.movement_dest.y) {

				double stepY = (ourMoveState.movement_vel.y * DELTA_TIME);
				double newY = remainder.y + (ourMoveState.movement_current_pos.y + stepY);

				ourMoveState.movement_current_pos.y = (int) newY;
				remainder.y = newY - ourMoveState.movement_current_pos.y;

				ObjectsLog("newY=" + newY + "  stepY was=" + stepY + " Delta was " + DELTA_TIME, "purple");

			}
			if (ourMoveState.movement_current_pos.z != ourMoveState.movement_dest.z) {

				double stepZ = (ourMoveState.movement_vel.z * DELTA_TIME);
				double newZ = remainder.z + (ourMoveState.movement_current_pos.z + stepZ);

				ourMoveState.movement_current_pos.z = (int) newZ;
				remainder.z = newZ - ourMoveState.movement_current_pos.z;

				ObjectsLog("newZ=" + newZ + "  stepZ was=" + stepZ + " Delta was " + DELTA_TIME, "purple");

			}

			// Movement step trigger
			advanceOnStepActions(ourMoveState.movement_vel.x * DELTA_TIME,
					ourMoveState.movement_vel.y * DELTA_TIME);

		}

		if (ourMoveState.is_onCurve()) {
			// http://stackoverflow.com/questions/5634460/quadratic-bezier-curve-calculate-point
			// the basic formular is;
			// x = (1 - t) * (1 - t)
			// * p[0].x + 2
			// * (1 - t)
			// * t
			// * p[1].x + t * t
			// * p[2].x;
			// for each dimension

			// to simplify some working we have worked out a few t componants in
			// advance;
			double inverseTime = 1 - ourMoveState.movement_curveTime;

			int newX = (int) (Math.pow(inverseTime, 2) * ourMoveState.movement_SC.x
					+ 2 * inverseTime * ourMoveState.movement_curveTime
					* ourMoveState.movement_CC.x
					+ Math.pow(ourMoveState.movement_curveTime, 2)
					* ourMoveState.movement_dest.x);

			int newY = (int) (Math.pow(inverseTime, 2) * ourMoveState.movement_SC.y
					+ 2 * inverseTime * ourMoveState.movement_curveTime
					* ourMoveState.movement_CC.y
					+ Math.pow(ourMoveState.movement_curveTime, 2)
					* ourMoveState.movement_dest.y);

			int newZ = (int) (Math.pow(inverseTime, 2) * ourMoveState.movement_SC.z
					+ 2 * inverseTime * ourMoveState.movement_curveTime
					* ourMoveState.movement_CC.z
					+ Math.pow(ourMoveState.movement_curveTime, 2)
					* ourMoveState.movement_dest.z);

			ourMoveState.movement_curveTime = ourMoveState.movement_curveTime
					+ (ourMoveState.movement_curveTimeStep * DELTA_TIME); // Curve
			// time
			// step
			// should
			// also
			// move
			// to
			// being
			// delta
			// based

			// Movement step trigger
			// not tested!
			advanceOnStepActions(Math.abs(
					ourMoveState.movement_current_pos.x - newX),
					Math.abs(ourMoveState.movement_current_pos.y - newY));

			ourMoveState.movement_current_pos.x = newX;
			ourMoveState.movement_current_pos.y = newY;
			ourMoveState.movement_current_pos.z = newZ;

		}

		// thisObject.objectScene.setWidgetsPosition(
		// thisObject,
		// objectsData.moveState.movement_currentX,
		// objectsData.moveState.movement_currentY);

		// SOLog.info("setting position of"+thisObject.ObjectsName);

		// thisobject.
		setPosition((int)ourMoveState.movement_current_pos.x,
				(int)ourMoveState.movement_current_pos.y,
				(int)ourMoveState.movement_current_pos.z, false);
		//
		// SOLog.info("current object position:"+movement_currentX+"
		// "+movement_currentY);
	}

	private boolean testIfWeAreWithinThresholdOfWaypoint(final double DELTA_TIME) {

		boolean withinX = false;
		boolean withinY = false;
		boolean withinZ = false;

		// if at or within just under one step of next waypoint, then we
		// test the next waypoint
		// SOLog.info("_____testing if we are at waypoint: desX = "
		// + currentObjectState.movement_DX + " cx = " +
		// currentObjectState.movement_currentX);

		// SOLog.info("_____testing if we are at waypoint: desY = "
		// + currentObjectState.movement_DY + " cy = " +
		// currentObjectState.movement_currentY);

		if (!objectsCurrentState.moveState.get().is_onCurve()) {

			/**
			 * 
			 * 1000 < 50+ 100 < 50+ 10 < 50+ 1 < 50+ -10 < 50+ -100 < 50+ -1000
			 * < 50+
			 * 
			 * if positive; dis_x > -(50+)
			 * 
			 * 
			 * 1000 < -50 100 < -50 10 < -50 1 < -50 -10 < -50 -100 < -50 -1000
			 * < -50
			 * 
			 * if negative; dis_x < -(-50)
			 **/

			// --
			// (distance to destinationX) < (movement step * deltatime +1)
			// --
			if (Math.abs(objectsCurrentState.moveState.get().movement_current_pos.x
					- objectsCurrentState.moveState.get().movement_dest.x) < (Math
							.abs(objectsCurrentState.moveState.get().movement_vel.x * DELTA_TIME) + 1)) { // will
				// need
				// to
				// be
				// step
				// x
				// delta
				// in
				// future

				withinX = true;
				objectsCurrentState.moveState.get().movement_current_pos.x = objectsCurrentState.moveState.get().movement_dest.x;
				objectsCurrentState.moveState.get().movement_vel.x = 0;
			} else {
				withinX = false;
			}
			;

			if (Math.abs(objectsCurrentState.moveState.get().movement_current_pos.y
					- objectsCurrentState.moveState.get().movement_dest.y) < (Math
							.abs(objectsCurrentState.moveState.get().movement_vel.y * DELTA_TIME) + 1)) {

				withinY = true;
				objectsCurrentState.moveState.get().movement_current_pos.y = objectsCurrentState.moveState.get().movement_dest.y;
				objectsCurrentState.moveState.get().movement_vel.y = 0;

			} else {
				withinY = false;
			}
			;

			if (Math.abs(objectsCurrentState.moveState.get().movement_current_pos.z
					- objectsCurrentState.moveState.get().movement_dest.z) < (Math
							.abs(objectsCurrentState.moveState.get().movement_vel.z * DELTA_TIME) + 1)) {

				withinZ = true;
				objectsCurrentState.moveState.get().movement_current_pos.z = objectsCurrentState.moveState.get().movement_dest.z;
				objectsCurrentState.moveState.get().movement_vel.z = 0;

			} else {
				withinZ = false;
			}
			;
		} else {

			withinX = false;
			withinY = false;
			withinZ = false;

			if (objectsCurrentState.moveState.get().movement_curveTime
					+ (objectsCurrentState.moveState.get().movement_curveTimeStep * DELTA_TIME) > 1) {

				ObjectsLog(" movement on curve within tolerance of destiniation. Tolerance was: "
						+ (objectsCurrentState.moveState.get().movement_curveTimeStep * DELTA_TIME));

				withinX = true;
				withinY = true;
				withinZ = true;
			}

		}

		if (withinY && withinX && withinZ) {
			return true;
		} else {
			return false;
		}
	}

	private MovementWaypoint setNextWayPointMovement() {
		// ObjectsLog("updating
		// waypoint"+objectsCurrentState.moveState.movement_currentWaypoint);

		MovementState ourMovementState = objectsCurrentState.moveState.get(); 

		MovementWaypoint cp = getObjectsCurrentState().moveState.get().currentPathData.get(ourMovementState.movement_currentWaypoint);
		MovementType currenttype = cp.type;

		if (skipFirstJump == true) {
			currenttype = MovementType.AbsoluteLineTo;
			skipFirstJump = false;
		}

		// if the new waypoint is at the current location, then we force a
		// teleport to
		// save calculation power
		if (cp.pos.x == ourMovementState.movement_current_pos.x) {
			if (cp.pos.y == ourMovementState.movement_current_pos.y) {
				if (cp.pos.z == ourMovementState.movement_current_pos.z) {
					SOLog.info("already at destination");
					currenttype = MovementType.AbsoluteMove;
				}
			}

		}

		if (currenttype == MovementType.AbsoluteLineTo || currenttype == MovementType.RelativeLineTo) {

			ourMovementState.movement_dest.x = cp.pos.x;
			ourMovementState.movement_dest.y = cp.pos.y;
			ourMovementState.movement_dest.z = cp.pos.z;

			// if relative add the last location on
			if (cp.isRelative()) {

				ourMovementState.movement_dest.x = cp.pos.x
						+ ourMovementState.get_currentX_as_int();
				ourMovementState.movement_dest.y = cp.pos.y
						+ ourMovementState.get_currentY_as_int();
				ourMovementState.movement_dest.z = cp.pos.z
						+ ourMovementState.get_currentZ_as_int();

				SOLog.info(this.objectsCurrentState.ObjectsName + " taking relative movement line to:"
						+ ourMovementState.movement_dest.x + ","
						+ ourMovementState.movement_dest.y);

				ObjectsLog("next waypoint (" + ourMovementState.movement_currentWaypoint
						+ ") sets a relative line :", "grey");
				ObjectsLog(" " + ourMovementState.movement_current_pos.toString() + "--(" + cp.pos.x + ","
						+ cp.pos.y + "," + cp.pos.z + ")-->" + ourMovementState.movement_dest.toString(),
						"green");

			} else {

				ObjectsLog("next waypoint (" + ourMovementState.movement_currentWaypoint
						+ ") sets a absolute line :", "grey");
				ObjectsLog(" " + ourMovementState.movement_current_pos.toString() + "---->"
						+ ourMovementState.movement_dest.toString(), "green");

			}

			/*
			 * ObjectsLog("next waypoint is a line going to " +
			 * objectsCurrentState.moveState.movement_DX + "," +
			 * objectsCurrentState.moveState.movement_DY,"green");
			 * 
			 * ObjectsLog("from " +
			 * objectsCurrentState.moveState.movement_currentX + "," +
			 * objectsCurrentState.moveState.movement_currentY);
			 */

			// update direction //NOTE: this is 2d "on the floor"
			updateObjectsDirection(ourMovementState.movement_dest.x,
					ourMovementState.movement_dest.y);

			// ObjectsLog("movement_currentX " +
			// objectsCurrentState.moveState.movement_currentX + " ");
			// ObjectsLog("movement_currentY " +
			// objectsCurrentState.moveState.movement_currentY + " ");

			int Xdistance = ((ourMovementState.movement_dest.x
					- ourMovementState.get_currentX_as_int()));
			int Ydistance = ((ourMovementState.movement_dest.y
					- ourMovementState.get_currentY_as_int()));
			int Zdistance = ((ourMovementState.movement_dest.z
					- ourMovementState.get_currentZ_as_int()));

			// note; not a error, this is meant to be the total x/y magnitudes,
			// not the hypo
			int TotalLength = Math.abs(Ydistance) + Math.abs(Xdistance) + Math.abs(Zdistance);

			// work out the vector x/y between 0 and 1, and
			// then multiply by the stepsize

			// ObjectsLog("TotalLength "+ TotalLength + " ");

			// work out ratios
			double Xratio = ((double) Xdistance / (double) TotalLength);
			double Yratio = ((double) Ydistance / (double) TotalLength);
			double Zratio = ((double) Zdistance / (double) TotalLength);

			// TODO: goes wrong when z is the only distance for some reason.
			// Likely a problem with detection of waypoint/end

			// GreySOLog.info("Xratio "
			// + Xratio + " ");

			// GreySOLog.info("Yratio "
			// + Yratio + " ");

			double StepX = (Xratio * (ourMovementState.movement_speed)); // *DELTA_TIME
			// should
			// only
			// be
			// done
			// before
			// adding
			// to
			// position.
			// NOT
			// stored
			// in
			// Step
			// values

			ourMovementState.movement_vel.x = StepX;

			double StepY = (Yratio * (ourMovementState.movement_speed)); // *DELTA_TIME
			// should
			// only
			// be
			// done
			// before
			// adding
			// to
			// position.
			// NOT
			// stored
			// in
			// Step
			// values

			ourMovementState.movement_vel.y = StepY;

			double StepZ = (Zratio * (ourMovementState.movement_speed)); // *DELTA_TIME
			// should
			// only
			// be
			// done
			// before
			// adding
			// to
			// position.
			// NOT
			// stored
			// in
			// Step
			// values

			ourMovementState.movement_vel.z = StepZ;

			// ensure minimum movement
			/*
			 * if (objectsCurrentState.moveState.movement_vel.y == 0 &&
			 * objectsCurrentState.moveState.movement_dest.y < 0) {
			 * 
			 * objectsCurrentState.moveState.movement_vel.y = -1;
			 * 
			 * } if (objectsCurrentState.moveState.movement_vel.y == 0 &&
			 * objectsCurrentState.moveState.movement_dest.y > 0) {
			 * objectsCurrentState.moveState.movement_vel.y = 1; } if
			 * (objectsCurrentState.moveState.movement_vel.x == 0 &&
			 * objectsCurrentState.moveState.movement_dest.x < 0) {
			 * objectsCurrentState.moveState.movement_vel.x = -1; } if
			 * (objectsCurrentState.moveState.movement_vel.x == 0 &&
			 * objectsCurrentState.moveState.movement_dest.x > 0) {
			 * objectsCurrentState.moveState.movement_vel.x = 1; }
			 */

			ObjectsLog(" Per-ms step X,Y,Z=" + ourMovementState.movement_vel.toString() + " ratios:"
					+ Xratio + "," + Yratio + "," + Zratio + " objectsCurrentState.moveState.movement_speed = "
					+ ourMovementState.movement_speed);

			// SOLog.info("so we are moving
			// by..."+movement_StepX+","+movement_StepY);

			// movement_currentX = movement_currentX +
			// movement_StepX;
			// movement_currentY = movement_currentY +
			// movement_StepY;

			telePortFlag = false;

		} else if (currenttype == MovementType.AbsoluteMove || currenttype == MovementType.RelativeMove) {

			if (cp.isRelative()) {
				ObjectsLog("relative move");
				ourMovementState.movement_dest.x = cp.pos.x + ourMovementState.get_currentX_as_int();// getX();
				// //used
				// to
				// be
				// ?
				// getX
				// /
				// getY/
				// getZ
				ourMovementState.movement_dest.y = cp.pos.y + ourMovementState.get_currentY_as_int();
				ourMovementState.movement_dest.z = cp.pos.z + ourMovementState.get_currentZ_as_int();
				// set starting loc to x/y of pin
			} else {
				ourMovementState.movement_dest.x = cp.pos.x;
				ourMovementState.movement_dest.y = cp.pos.y;
				ourMovementState.movement_dest.z = cp.pos.z;

			}

			ObjectsLog("Next waypoint is to move to " + ourMovementState.movement_dest.toString(),
					"green");

			// teleport there
			ourMovementState.movement_current_pos.x = ourMovementState.movement_dest.x;
			ourMovementState.movement_current_pos.y = ourMovementState.movement_dest.y;
			ourMovementState.movement_current_pos.z = ourMovementState.movement_dest.z;

			telePortFlag = true;

			// movement_StepX = (cp.x - movement_currentX);
			// movement_StepY = (cp.y - movement_currentY);

			// SOLog.info("so we are moving
			// by..."+movement_StepX+","+movement_StepY);

		} else if (currenttype == MovementType.AbsoluteQCurveToo || currenttype == MovementType.RelativeQCurveToo) {

			// sets up the params needed for curved movement
			// updates

			ourMovementState.movement_dest.x = cp.pos.x;
			ourMovementState.movement_dest.y = cp.pos.y;
			ourMovementState.movement_dest.z = cp.pos.z;

			ourMovementState.movement_CC.x = cp.midPoint1.x;
			ourMovementState.movement_CC.y = cp.midPoint1.y;
			ourMovementState.movement_CC.z = cp.midPoint1.z;

			ourMovementState.movement_SC.x = ourMovementState.movement_current_pos.x;
			ourMovementState.movement_SC.y = ourMovementState.movement_current_pos.y;
			ourMovementState.movement_SC.z = ourMovementState.movement_current_pos.z;

			// if relative add the last location on
			if (cp.isRelative()) {

				ourMovementState.movement_dest.x = cp.pos.x
						+ ourMovementState.get_currentX_as_int();
				ourMovementState.movement_dest.y = cp.pos.y
						+ ourMovementState.get_currentY_as_int();
				ourMovementState.movement_dest.z = cp.pos.z
						+ ourMovementState.get_currentZ_as_int();

				ourMovementState.movement_CC.x = cp.midPoint1.x
						+ ourMovementState.movement_current_pos.x;
				ourMovementState.movement_CC.y = cp.midPoint1.y
						+ ourMovementState.movement_current_pos.y;
				ourMovementState.movement_CC.z = cp.midPoint1.z
						+ ourMovementState.movement_current_pos.z;

				ObjectsLog("relative curve to " + ourMovementState.movement_dest.x + ","
						+ ourMovementState.movement_dest.y);
				ObjectsLog("via " + ourMovementState.movement_CC.toString());

			} else {

				ObjectsLog("next waypoint (" + ourMovementState.movement_currentWaypoint
						+ ") sets a absolute curve :", "grey");
				ObjectsLog(" " + ourMovementState.movement_current_pos.toString() + "-->"
						+ ourMovementState.movement_CC.toString() + "-->"
						+ ourMovementState.movement_dest.toString(), "green");

			}

			ourMovementState.currentmovementtype = MovementStateType.OnCurvePath;

			/*
			 * ObjectsLog("curve data x: " +
			 * objectsCurrentState.moveState.movement_SC.x + " " +
			 * objectsCurrentState.moveState.movement_CC.x + " " +
			 * objectsCurrentState.moveState.movement_dest.x);
			 * 
			 * ObjectsLog("curve data y: " +
			 * objectsCurrentState.moveState.movement_SC.y + " " +
			 * objectsCurrentState.moveState.movement_CC.y + " " +
			 * objectsCurrentState.moveState.movement_dest.y);
			 * 
			 * ObjectsLog("curve data z: " +
			 * objectsCurrentState.moveState.movement_SC.z + " " +
			 * objectsCurrentState.moveState.movement_CC.z + " " +
			 * objectsCurrentState.moveState.movement_dest.z);
			 */

			// ObjectsLog("curve data z: "
			// + " (3d curves nor correctly supported yet, lacking midpoint
			// information and correct processing) "
			// + objectsCurrentState.moveState.movement_dest.z);

			// temp, need to be corrected:
			ourMovementState.movement_curveTime = 0;
			ourMovementState.movement_curveTimeStep = 0.01
					* (ourMovementState.movement_speed); // note:
			// Before
			// changing
			// to a
			// delta
			// system
			// the
			// speed
			// used
			// to be
			// 100times
			// bigger
			// as
			// that
			// was
			// the
			// interval
			// of
			// refreshs.

			ObjectsLog(" time step=" + ourMovementState.movement_curveTimeStep + " ");

			if (ourMovementState.movement_curveTimeStep < (0.01 / 100)) { // minimum
				// has
				// been
				// reduced
				// by
				// 100
				// due
				// to
				// TimeStep
				// being
				// per
				// ms
				// now
				// not
				// per
				// update
				// (see
				// above
				// reg
				// delta)
				ObjectsLog(ourMovementState.movement_curveTimeStep
						+ " is under minimum curve speed setting it to:" + (0.01 / 100));

				ourMovementState.movement_curveTimeStep = (0.01 / 100);
			}

			ourMovementState.movement_vel.y = 10; // curves dont
			// use vel?
			ourMovementState.movement_vel.x = 10;
			ourMovementState.movement_vel.z = 10;

		} else if (currenttype == MovementType.AbsoluteLoopToPathStart) {

			ObjectsLog("(looping to start)");

			// if there's a "z" then loop
			// next destination is the first again
			// SOLog.info("looping to start");

			ourMovementState.movement_currentWaypoint = -1;
			telePortFlag = true;

		} else if (currenttype == MovementType.Command) {

			ObjectsLog("(running mid movement command)");
			this.triggerMidMovementCommand(cp.Command);

		} else if (currenttype == MovementType.InternalRunnable) {

			ObjectsLog("(running mid movement runnable)");
			cp.InternalRunnable.run();

		} else {
			// etc for other types
			// SOLog.info("unknown type "+cp.type.toString());

			telePortFlag = false;
		}

		return cp;
	}

	/**
	 * The hard limit on the number of bounces allowed during physic based
	 * movement. This is a safety cap that ideally shouldn't be reached anyway.
	 * If we ever decide to allow unlimited bounces we would need to find a way
	 * to optionally change this
	 */
	final static int	MAXIMUM_BOUNCES	= 7;

	int					currentBounce	= 0;

	/**
	 * Updates position according to physics
	 * 
	 * NOTE: currently collision detection only works at ground level.
	 * In future this limitation will be removed, but first we need collisionmaps to know their height
	 * 
	 * @param deltatime
	 */
	private void movementPhysicsBasedUpdate(double deltatime) {

		//Lots of notes, lots of mess, lots of inefficiancies
		//have to tidy this up
		//
		
		if (!objectsCurrentState.moveState.isPresent()){
			SOLog.severe("Warning: physics frame update called but no moveState created");

		}


		MovementState currentMoveState = getObjectsCurrentState().moveState.get();

		// change position by previous velocity x delta
		// we can make this all more efficient by working out movement_acc *
		// deltatime and reusing in updateVelocity
		if (Math.abs(currentMoveState.movement_current_pos.z - floor_height) < 0.0001  ){		
			ObjectsLog("(updating position at ground) ");	
			currentMoveState.updatePosition(deltatime,false,false,true); //this statement updates the state
		} else {
			currentMoveState.updatePosition(deltatime); //this statement updates the state
		}
		
		//----------------
		//NOTE: large deltas currently mess up bounces
		//TODO: how to fix or avoid this?
		//maybe run multiple times at a lower delta?
		//Max delta should probably be proportional to speed.
		//------------------
		
		//hmm..do we test the new co-ordinates first?
		//or test the new movestate directly? (do we have enough information then to bounce correctly?)

		//test for collisions if we have a cmap		
		//probably a lot of optimization possible here
		//for example;
		// a)_if no movement in z we can reduce our checks to only the pool of objects overlapping us in z (Once z support is in)
		// b) A bounding sphere check based on extrapolated travel? (only work for linear motion with no acc, gravity/curves wont work perfectly, but does that mater?)
		//
		// Current check works by first checking all points, then all lines if no point overlaps.
		// The line check is slow compared to the point one.
		//
		//
		if (	(this.getCompoundCmap()!=null)
				&& this.getParentScene().scenesCmap.isPresent() 
				&& this.getZ()==0){ //currently only test at ground level TODO: remove this when we have true 3d support

			//TODO: In order to support correct physical bouncing, we need to return a SpiffyPolygonCollision, not just a polygon
			//This is because we need to know the side which collided
			//It might be easier to test for a line collision instead, based on travel vectors and our size?"
			//No, wait
			//If we collided with a polygon we can test just that polygon against a line of our movement
			//Best of both worlds? (in many cases this will avoid uneeded line checks each frame)
			SceneCollisionMap sceneCollisionMap = this.getParentScene().scenesCmap.get(); //we know its present as we tested in the if


			int currentX = currentMoveState.get_currentX_as_int();			
			int currentY = currentMoveState.get_currentY_as_int();
			int currentZ = currentMoveState.get_currentZ_as_int();


			Polygon collides = sceneCollisionMap.isObjectColliding(this, currentX, currentY,currentZ);

			//HMM...this doesnt take into account that child objects might collide with scanary
			//So if B attached to A represents a solid link, really B being hit should stop As movements
			//This would take a fair bit more work to support, especially at a good speed.
			//probably need to make a temp compound cmap of all the linked polygons? (should be made before movement starts)

			if (collides!=null){

				String objectWehit = "[no object, probably scene itself]";
				if (collides.associatedObject!=null){ //sourceMap.defaultAssociatedObject;
					objectWehit = collides.associatedObject.getName(); //sourceMap.defaultAssociatedObject;
				}


				ObjectsLog("||Collision with;"+objectWehit+" pathname:"+collides.getName()+" ");
				ObjectsLog("||was testing at: "+currentX+" , "+currentY+" ");

				//TODO:
				Simple3DPoint start = new Simple3DPoint(getX(),getY(),0);				
				Simple3DPoint end = new Simple3DPoint(currentX,currentY,0);


				//arg, no a line wont do!
				//as we have a size not just a center! arg
				//whatever we use it should still be just testing against collides, however
				//Also; we dont need to know precisely where it collides, just the side it hits
				//So a line might work, if we extrapolate forward? Yes...
				//allthough we are still pin-centric. This will only work if the pin is within the cmap
				//(make a requirement?)
				SpiffyPolygonCollision hit = collides.testForCollision(
						start, 
						end,true); //start = our current location end (getX/getY etc) = proposed update position (movestates pos)

				if (hit!=null && hit.getHitSide()!=null){
					ObjectsLog("||collision was at;"+hit.toString());
				} else {
					ObjectsLog("(SpiffyPolygonCollision not found error: "+start.toString()+">>"+end.toString()+")");					
					//just stop
					//reset movestate to real location as we didn't move
					currentMoveState.setPosition(this.getX(),this.getY(),this.getZ());
					//we stop without updating the position to the state
					StopCurrentMovement();
					return;

				}

				//get co-ordinates of hit side
				PolySide sidehit=hit.getHitSide(); //is this correct chair going west seems wrong?(gets verticle line of left bit of sofa)



				Simple3DPoint linesstart = sidehit.getStart();
				Simple3DPoint linesend   = sidehit.getEnd();

				ObjectsLog("||collision was with line;"+linesstart+">>>"+linesend);


				//then get the normal of the poly we hit as x/y/z vector
				int dx = linesend.x-linesstart.x;
				int dy = linesend.y-linesstart.y;

				SimpleVector3 normal1 = new SimpleVector3(-dy,dx,0); //looks weird, but yes, swapping the x and y is correct here
				SimpleVector3 normal2 = new SimpleVector3(dy,-dx,0); //see link for how normals are worked out

				//err..how to pick correct normal?
				//it would be the one within 90 degrees +/- of the current velocity if it was going the other way
				//but how to get that?	
				//(correct one seems to be #1)
				//also then adjust the normal if this is a isometrix scene;
				//https://www.scirra.com/forum/solved-how-do-i-calculate-bounce-angle-in-isometric-view_t184222
				//basically we need scale y
				//TODO: give scene a optional "ScaleNormalsForReflections" variable?
				if (this.getParentScene()!=null){
					normal1.mul(this.getParentScene().getNormalScaleingForReflections());
				}


				ObjectsLog("||normals of line;"+normal1+" and "+normal2);

				//(NOTE: we are currently 2d, in 3d we might hit the top or bottom of something, the same formular should work though if we  get the normal correct)
				//after we get the normal we need to express our current motion aligned to it
				//see:
				// http://stackoverflow.com/questions/573084/how-to-calculate-bounce-angle

				//now with the normal of what we hit we get our current velocity expressed in components perpendicular and parallel  to that
				//u = (v · n / n · n) n 
				//w = v − u
				//n is the normal of what we hit
				//v is our current velocity vector
				SimpleVector3 vel = currentMoveState.get_current_velocity();  //note, not a copy
				ObjectsLog("||current vel;"+vel);

				double n_dot_n = normal1.dot(normal1);
				double v_dot_n = vel.dot(normal1);
				SimpleVector3 u = normal1.copy().mul( (v_dot_n/n_dot_n) );
				SimpleVector3 w = vel.copy().sub(u);


				ObjectsLog("||new componants;"+u+"   and   "+w);

				//v′ = f w − r u.
				//(where f is friction and r is elasticity)
				//w is parral to what we hit, u is perpendicular
				//we have a setting for r ,currentMoveState.getBounceEnergyRetention(), but for now we have perfect elastic with no fraction;
				//v=w-u
				u.mul(currentMoveState.getBounceEnergyRetention()); //scale to reflect bounce amount

				//w.mul(1.0-polygonwehit.getFriction() ); ?

				//subtract this per frame; (objectfriction*scenefriction)*gravity ?

				SimpleVector3 new_vel = w.sub(u);



				//
				//update scene visualizer to help debug				
				//

				MovementPath debugNormal = new MovementPath("","debugForCollisions_normal");

				debugNormal.addMovementWayPoint(MovementType.AbsoluteMove, hit.X, hit.Y,   0);	

				normal1.normalize();
				double normalx= hit.X+(normal1.x*100);
				double normaly= hit.Y+(normal1.y*100);

				debugNormal.addMovementWayPoint(MovementType.AbsoluteLineTo, (int)normalx,(int)normaly, 0);	

				sceneCollisionMap.updatePath(debugNormal,sceneCollisionMap.lastCollision);

				MovementPath debugPath = new MovementPath("","debugForCollisions_objectspath");


				double pastX= start.x-(vel.x*1800);
				double pastY= start.y-(vel.y*1800);

				debugPath.addMovementWayPoint(MovementType.AbsoluteMove, (int)pastX,(int)pastY, 0);	


				debugPath.addMovementWayPoint(MovementType.AbsoluteLineTo, hit.X, hit.Y,   0);	

				double futureX= hit.X+(new_vel.x*1800);
				double futureY= hit.Y+(new_vel.y*1800);

				debugPath.addMovementWayPoint(MovementType.AbsoluteLineTo,(int) futureX,(int) futureY, 0);	 //need to extrapolate based on hitx/y + new vel

				sceneCollisionMap.updatePath(debugPath,sceneCollisionMap.lastCollision);


				//Finally set the velocity
				currentMoveState.setVelocity(new_vel.x, new_vel.y, new_vel.z);

				SimpleVector3 cvel = currentMoveState.get_current_velocity();
				ObjectsLog("||New velocity;"+cvel+" from:"+this.getX()+","+this.getY());


				//maybe also take acceleration into account now
				//currentMoveState.updateVelocity(deltatime);

				//set current movetstate to current location
				currentMoveState.setPosition(this.getX(),this.getY(),this.getZ());				
				//without this it will for one update be colliding with the object
				//with this it means it will pause for one update, before reflecting away (as we set the new velocity)
				//This second option is safer, as it prevents the chance of getting caught on the edge

				return;
			}

		}



		// ObjectsLog("new vel:"+currentMoveState.movement_vel.toString()+" ");
		//	ObjectsLog("pos:" + currentMoveState.get_current_position().toString());

		// (we might want to test for other stop conditions here - if we have a
		// fractional constant maybe when the speed
		// slows down too much?)
		// TODO: other non-floor impacts?

		
		//
		//If we hit the floor:
		//
		//note: we add the 0.0001 to give some allowence for cpus making a z position  of zero
		//into 0.00000000001 and then saying its under the floor when really both are at zero.
		//This check didnt seem enough however (?) so we also checked we have a velocity going downwards
		//This IF should only fire if its LESS then zero, not exactly zero
		if (currentMoveState.movement_current_pos.z+0.0001 < floor_height 
				&&
			(currentMoveState.movement_vel.z+0.0001) < 0.0) 
		{

			
			// we now should jump back up by that amount if the speed is enough
			// to bother
			double newspeed = (-currentMoveState.get_current_velocity().z)
					* currentMoveState.getBounceEnergyRetention();

			ObjectsLog("hit ground: velocity was :" + currentMoveState.movement_vel.z+" ", "blue");
			ObjectsLog("currentMoveState.movement_current_pos.z:" + currentMoveState.movement_current_pos.z+"   floor_height="+floor_height);

			// update the last impact variable on this object
			SpeedOfLastImpact = Math.abs(currentMoveState.get_current_velocity().z);
			ObjectsLog("ImpactSpeed=" + SpeedOfLastImpact, "blue");
			// we should temporarily fire a touching change update
			runOnTouchingChangeActions(this);
			SpeedOfLastImpact = 0; // we reset this after running the actions,
			// to ensure nothing touching inbetween
			// added manually has a impact speed.

			// NOTE: we fire this as we touched the ground, even though no new
			// objects have been added or removed from the touching list
			// in future we might want to replace this with a ground object of
			// some sort?

			// increase currentBounce (this is a safety to put a hard limit on
			// the number of bounces
			currentBounce++;
			if (currentBounce > SceneObject.MAXIMUM_BOUNCES) {
				currentBounce = 0;
				newspeed = 0;
			}

			if (newspeed < 0.05) {

				ObjectsLog("bounce Speed under 0.05 so stopping bounces ");
				
				//set to ground level
				currentMoveState.get_current_position().z = floor_height;
				objectsCurrentState.moveState.get().movement_current_pos.z = floor_height;
				currentMoveState.get_current_velocity().z = 0.0;
				
				
				//friction should now apply if we are dragging/sliding/rolling on the floor
				SimpleVector3 calcFriction = getCalculatedFrictionAgainstSceneSurface(); //NOTE: this is object rubbing against the scene surface. In future if the 'floor' is not the scene we need a different function to get Object against Object friction
				this.setCurrentMovementFriction(calcFriction);
								
				// update position one last time to ensure match
				setPosition(objectsCurrentState.moveState.get().get_currentX_as_int(),
						objectsCurrentState.moveState.get().get_currentY_as_int(),
						objectsCurrentState.moveState.get().get_currentZ_as_int(), false);


				//now dealt with at the end of this function;
				// reattach if we were previously				
				//postDropReattachCheck();
				// update position and end
				//StopCurrentMovement();

				//update velocitys (this was absent before, if this works we can move all of these to the end)
			//	currentMoveState.updateVelocity(deltatime,false,false,fixedZ); //we disable acceleration in z


			} else {

				// ObjectsLog("bouncing:"+timeleftover);
				// set position to ground
				currentMoveState.get_current_position().z = floor_height;

				// set new z speed
				currentMoveState.get_current_velocity().z = newspeed;// -currentMoveState.get_current_velocity().z;

				// currentMoveState.updateVelocity(timeleftover);

				// currentMoveState.updatePosition(timeleftover); //really we
				// should apply this as well

				// update velocity
				//currentMoveState.updateVelocity(deltatime);

			}

		} else {

			// update velocity
			//currentMoveState.updateVelocity(deltatime);
		}

		
		//Now we Update velocity:
		//
		//if on a surface we dont apply any acceleration downwards.
		//(we have to check within in a range as double vales are never absolute zero)	
		if (Math.abs(currentMoveState.movement_current_pos.z - floor_height) < 0.0001  ){			
			ObjectsLog("(updating velocity at ground) ");						
			currentMoveState.updateVelocity(deltatime,false,false,true);  //we disable acceleration in z
		} else {
			currentMoveState.updateVelocity(deltatime); 
		}
		//--
		

		//if  velocity is now zero we stop
		if (currentMoveState.velocityLessThen(0.001)){

			ObjectsLog("x,y,z velocity all under 0.001 so stopping");			
			StopCurrentMovement();

			// reattach if we were previously	(ie for a drop)			
			postDropReattachCheck();
		}



		// update visual position

		// if (y<floory) {

		// work out how long it took within this update
		// int floordis = Math.abs(floory-y)
		// int timeittooktillhit = floordis / sy;
		// int timeleftover = deltatime-timeittooktohit
		// //we now should jump back up by that amount
		//
		// sy = (-sy)*bounceEnergyRetention
		// currenty = sy x timeleftover;
		// if (abs(s)<0.1){
		// // atDestiniation
		// StopCurrentMovement()
		// }

		//
		// }

	}

	private void postDropReattachCheck() {
		if (objectsCurrentState.moveState.get().parentBeforeDropping == null) {
			ObjectsLog("No parent object");
		} else {
			ObjectsLog("Reattaching parent object");
			String parentName = objectsCurrentState.moveState.get().parentBeforeDropping;
			ObjectsLog("Reattaching to parent object:" + parentName);

			SceneObject parentObject = SceneObjectDatabase.getSingleSceneObjectNEW(parentName, null, true);

			attachTo(parentObject);

			if (this.objectsCurrentState.moveState.get().hasLinkedZindexBeforeDropping) {
				this.setZIndexAsLinked(true, this.getObjectsCurrentState().linkedZindexDifference);
			}

		}
	}

	/**
	 * As soon as a objects scene is known it should be set here. Note, this
	 * method can be override to give subtypes knowledge of the scene
	 * implementation (SceneWidgetVisual for example) provided that also refer
	 * to this method via a super so the two bits stay in sync
	 ***/
	public void setObjectsSceneVariable(SceneWidget scene) {

		if (scene == null) {
			// remove the old scene from loadinglists of parent scene (if there is one). We do
			// this in case we were still loading as images from objects not on
			// a scene don't need to be loaded.
			//Note; This does make the assumption that anything needed will load by the time this object is used again- if it ever is
			//TODO: Maybe do something to fix that? Seems unlikely to be important though, as load time should never be that long
			if (objectScene != null) {

				objectScene.advancePhysicalLoading(this.getName());
				objectScene.advanceLogicalLoading(this);

			}
			//
			SOLog.info("Objects Scene Is Being Set To Null");
		} else {
			SOLog.info("Objects Scene Is Being Set To " + scene.SceneFileName);
		}

		objectScene = scene;

		// also update name if its present?
		if (getObjectsCurrentState() != null) {

			SOLog.info("setting filename");

			if (objectScene != null) {
				SOLog.info("setting object to new scene:" + scene.SceneFileName);

				this.getObjectsCurrentState().ObjectsSceneName = objectScene.SceneFileName;
			} else {

				// set scene name to the no scene signifier:
				SOLog.info("setting object to [no scene]");
				this.getObjectsCurrentState().ObjectsSceneName = SceneObjectState.OBJECT_HAS_NO_SCENE_STRING;

			}
		}
	}

	// public SceneObjectState getObjectsInitialState() {
	// return objectsInitialState;
	// }

	/**
	 * sets the initial state. This should be called after the settings are
	 * known. subclass's should override this to take a copy themselves as well
	 * as calling the parent class That way each class in the inheritance chain
	 * has a copy of the state as precise as that class allows
	 * 
	 * ie, the same state will be a objectstate here, a divobjectstate in the
	 * sceneobjectdivclass and a spriteobjectstate in the spriteobjectstate
	 * class This is because SceneSpriteObject is a SceneDivObject which is a
	 * SceneVisualObject which is a SceneObject
	 * 
	 * @param objectsInitialState
	 **/
	protected void setObjectsInitialState(SceneObjectState objectsInitialState) {
		// this.objectsInitialState = objectsInitialState;
		this.initialObjectState = objectsInitialState;
	}

	/*
	 * 
	 * public SceneObjectState getObjectsInitialState() { return
	 * initialObjectState; // return objectsInitialState; }
	 */

	public SceneObjectState getInitialState() {
		return (SceneObjectState) initialObjectState;
	}

	public ActionList getObjectsActions() {
		return objectsActions;
	}

	/**
	 * @return the objectsCurrentState subclasses should override this to give
	 *         their exact subtype
	 */
	public SceneObjectState getObjectsCurrentState() {
		return objectsCurrentState;
	}

	public SceneObjectState getTempState() {
		return (SceneObjectState) tempObjectState;
	}

	/** should be overridden by the subtypes **/
	public void saveTempState() {
		//

	}

	/** should be overridden by the subtypes **/
	public void restoreTempState() {

	}

	/**
	 * Removes the object from everything, including the internal game list of
	 * all objects
	 **/
	public void removeObject() {
		removeObject(true, true);
	}

	/**
	 * Removes the objects from the game - you can use the boolean flag to
	 * determine if to remove it from the internal list of all game objects.
	 * Normally true. But set to false if, for example, you are clearing all
	 * objects from the game and are using clear() on those lists to remove
	 * everything anyway.
	 **/
	public void removeObject(boolean removeFromLists, boolean removeFromInventorys) {

		if (objectsCurrentState == null) {
			SOLog.info("object data null error");
			return;
		}

		// SOLog.info("removing this object: " +
		// this.objectsCurrentState.ObjectsName);

		// remove from list

		// if its an inventory object its just in one list and doesn't have a
		// native scene set (objects scene)
		if (this.objectsCurrentState.getPrimaryObjectType() == SceneObjectType.InventoryObject) {

			// InventoryPanel.getAllInventoryItems().remove(this); //WAIT THIS
			// WONT WORK
			// We are removing it only from the list getAll makes.
			// instead we need to find the panel its on and remove it from that.

			// new method
			// maybe we should remove any associated item too?
			if (removeFromInventorys) {
				SOLog.info("removing :" + this.getName() + " from inventorys");
				InventoryPanelCore.removeItemFromAllInventorys((IsInventoryItem) this);
			} else {

			}

		} else {

			if (objectScene == null) {
				SOLog.info("objects scene is null,so cant remove it");
				return;
			}

			// if its any other item of object it will have a native scene and
			// we should ensure its removed from it
			SOLog.info("removing this object from its scene lists: " + this.objectsCurrentState.ObjectsName);
			objectScene.getScenesData().removeFromScenesObjects(this);

			if (removeFromLists) {

				SOLog.info(
						"removing this object from all global object lists: " + this.objectsCurrentState.ObjectsName);
				SceneObjectDatabase.removeObjectFromAllLists(this);

			}

			SOLog.info("removing this object from background: " + this.objectsCurrentState.ObjectsName);

			// remove it physically
			removeThisObjectFromItsSceneImplementation();

		}

	}

	/**
	 * moves the object to a different scene, doing all the steps needed.
	 * 
	 * Do not get mixed up with setObjectsSceneVariable(), which only sets the
	 * objects scene variable
	 ***/
	// NOTE: the existing scene might be null, this can happen if the object was
	// previously in the inventory
	public void setObjectsScene(SceneWidget newScene) {

		// This variable holds the origin scene of this object. (ie, where the
		// files are kept)
		String objectsInitialSceneName = this.getInitialState().ObjectsSceneName;

		// This variable holds the scene name we are coming from.
		String objectsPreviousSceneName = objectsCurrentState.ObjectsSceneName;
		// This variable holds the scene itself we are coming from
		SceneWidget previousScene = objectScene;

		if (newScene == null) {
			SOLog.info(
					"requested to put object on null scene, so instead we just remove it from any scene it is and update the variable");

			this.removeObject(true, false); // we dont want to remove it from
			// inventory's, as it might supposed
			// to be there

			setObjectsSceneVariable(null);

			for (SceneObject relObject : relativeObjects) {
				relObject.ObjectsLog("parent is being removed from all scenes, so we do too!", LogLevel.Info);
				relObject.setObjectsScene(null);

				if (!objectsPreviousSceneName.equals(SceneObjectState.OBJECT_HAS_NO_SCENE_STRING)) { // if
					// the
					// object
					// had
					// no
					// scene
					// before
					// we
					// ignore
					ObjectsLog(
							"object was non-native so testing previous scene if old scenes dependancy can be removed..");
					previousScene.removeSceneDependancyIfSafe(newScene);
				}
			}

			SOLog.info("(object now on null scene)");
			return;
		}

		// make sure its not there already
		// Note; we check if its on the scene and on the panel UNLESS its
		// attached to a Div, as then it wont be on the spiffydragpanel anyway
		// (as it "hovers above" the scene on a html div rather the directly
		// attached)
		if ((previousScene == newScene)
				&& (newScene.isObjectInScene(this) || this.objectsCurrentState.attachToHTMLDiv != "")) {

			ObjectsLog("object already on correct scene", "yellow");
			return;
		}

		// remove it from the old scene
		if (previousScene != null) {
			ObjectsLog("removing from current scene (" + previousScene.SceneFileName + ")");
		}
		this.removeObject();


		ObjectsLog(" setting scene logically to: " + newScene.SceneFileName);
		// set this objects scene variable to point to the new scene
		SOLog.info("setting object scenevariable to point to the new scene");
		// objectScene = newScene;
		// super.setObjectsSceneVariable(newScene);
		setObjectsSceneVariable(newScene);

		objectsCurrentState.ObjectsSceneName = newScene.SceneFileName;

		ObjectsLog("adding to scene " + newScene.SceneFileName, "RED");
		// add it to the new scene
		newScene.addObjectToScene(this); // <---scene variable setting needs to
		// be set before this point as the
		// process of adding might use the
		// scene data

		// update the new scenes status so it knows it is dependent on another
		// scene
		// for this we use the original scene the object came from, as it could
		// have passed over many scenes to get here
		// and its only the original one that matters.
		newScene.addSceneDependancy(objectsInitialSceneName);
		// TODO: should only do the above if scene is not null

		// if object is non native to the scene it just came from we remove it
		// from that
		if (!objectsPreviousSceneName.equalsIgnoreCase(SceneObjectState.OBJECT_HAS_NO_SCENE_STRING)) { // if
			// the
			// object
			// had
			// no
			// scene
			// before
			// we
			// ignore

			if (!objectsPreviousSceneName.equalsIgnoreCase(objectsInitialSceneName)) {

				ObjectsLog("object was non-native so testing previous scene if old scenes dependancy can be removed..");
				previousScene.removeSceneDependancyIfSafe(newScene);

			}

		}

		// remove from old lists?
		// ObjectsLog.log("removing from old lists scene");
		// SOLog.info("removing from old lists scene");

		// update scene data

		ObjectsLog("removing from old lists and adding to new lists");

		SOLog.info("removing from old and adding to new lists...");
		if (previousScene != null) {
			previousScene.getScenesData().removeFromScenesObjects(this);
		}

		newScene.getScenesData().addToScenesObjects(this); // <----------This
		// currently a bit
		// crude
		// SOME SORT OF CRASH HAPPENS BEFORE THE NEXT LOG STATEMENT

		// add to global list if not there already? (will it ever not be there?)
		// (This whole bit is probably pointless)

		// scene.scenesData.scenesObjects.add(this);
		SceneObjectDatabase.addObjectToDatabase(this);

		/*
		 * if (this.objectsCurrentState.getPrimaryObjectType()==SceneObjectType.
		 * Sprite){
		 * 
		 * SOLog.info("moving sprite");
		 * //objectScene.getScenesData().sceneSpriteObjects.remove(this);
		 * //objectScene.getScenesData().removeFromScenesObjects(this);
		 * //newScene.getScenesData().sceneSpriteObjects.add((SceneSpriteObject)
		 * this); //newScene.getScenesData().addToScenesObjects(this);
		 * 
		 * //SceneObjectDatabase.all_sprite_objects.put(objectsCurrentState.
		 * ObjectsName.toLowerCase(),(SceneSpriteObject)this);
		 * 
		 * // SceneObjectDatabase.addSpriteObjectToDatabase((SceneSpriteObject)
		 * this);
		 * 
		 * 
		 * SceneObjectDatabase.addObjectToDatabase(this,SceneObjectType.Sprite);
		 * 
		 * } if
		 * (this.objectsCurrentState.getPrimaryObjectType()==SceneObjectType.
		 * DialogBox){ SOLog.info("moving dialogue");
		 * //objectScene.getScenesData().SceneDialogObjects.remove(this);
		 * //newScene.getScenesData().SceneDialogObjects.add((SceneDialogObject)
		 * this);
		 * //SceneObjectDatabase.all_text_objects.put(objectsCurrentState.
		 * ObjectsName.toLowerCase(),(SceneDialogObject) this);
		 * 
		 * //SceneObjectDatabase.addTextObjectToDatabase((SceneLabelObject)
		 * this); //should be dialogue specific
		 * 
		 * 
		 * SceneObjectDatabase.addObjectToDatabase(this,SceneObjectType.
		 * DialogBox); } if
		 * (this.objectsCurrentState.getPrimaryObjectType()==SceneObjectType.
		 * Label){ SOLog.info("moving label");
		 * //objectScene.getScenesData().SceneTextObjects.remove(this);
		 * //newScene.getScenesData().SceneTextObjects.add((SceneTextObject)
		 * this);
		 * //SceneObjectDatabase.all_text_objects.put(objectsCurrentState.
		 * ObjectsName.toLowerCase(),(SceneLabelObject) this);
		 * 
		 * //SceneObjectDatabase.addTextObjectToDatabase((SceneLabelObject)
		 * this);
		 * 
		 * 
		 * SceneObjectDatabase.addObjectToDatabase(this,SceneObjectType.Label);
		 * 
		 * } if
		 * (this.objectsCurrentState.getPrimaryObjectType()==SceneObjectType.Div
		 * ){ SOLog.info("moving div");
		 * //objectScene.getScenesData().SceneDivObjects.remove(this);
		 * //newScene.getScenesData().SceneDivObjects.add((SceneDivObject)
		 * this); //SceneObjectDatabase.all_div_objects.put(objectsCurrentState.
		 * ObjectsName.toLowerCase(),(SceneDivObject) this);
		 * 
		 * //SceneObjectDatabase.addDivObjectToDatabase((SceneDivObject) this);
		 * 
		 * SceneObjectDatabase.addObjectToDatabase(this,SceneObjectType.Div);
		 * 
		 * }
		 * 
		 * if (this.objectsCurrentState.getPrimaryObjectType()==SceneObjectType.
		 * Vector){ SOLog.info("moving vector");
		 * //objectScene.getScenesData().SceneVectorObjects.remove(this);
		 * //newScene.getScenesData().SceneVectorObjects.add((SceneVectorObject)
		 * this);
		 * //SceneObjectDatabase.all_vector_objects.put(objectsCurrentState.
		 * ObjectsName.toLowerCase(),(SceneVectorObject) this);
		 * 
		 * //SceneObjectDatabase.addVectorObjectToDatabase((SceneVectorObject)
		 * this);
		 * 
		 * 
		 * SceneObjectDatabase.addObjectToDatabase(this,SceneObjectType.Vector);
		 * }
		 */

		// Input objects should be supported here like above!

		// we set its scene now to the new scene
		// This of course, has to be done after the above, because after this
		// point it doesnt know what scene it came from!
		// Scene objects have a very poor memory :(
		// SOLog.info("setting object to new scene");
		// objectScene = newScene;
		// super.setObjectsSceneVariable(newScene);
		// setObjectsSceneVariable(newScene);

		// objectsCurrentState.ObjectsSceneName = newScene.SceneFileName;

		SOLog.info("positioning relative stuff to this, if any");

		// we now have to repeat the process for all objects positioned relative
		// to this one
		// as relatively positioned things should always be on the same scene
		for (SceneObject relObject : relativeObjects) {

			// relObject.ObjectsLog.info("parent is changing scene, so we do
			// too!");
			relObject.ObjectsLog("parent is changing scene, so we do too!", LogLevel.Info);

			relObject.setObjectsScene(newScene);

		}

		// finally update this objects variable zindex info
		ObjectsLog("setUpVariableZindex", "green");
		SOLog.info("setUpVariableZindex");
		setUpVariableZindex();

		// and ensure zindex is correct
		if (getObjectsCurrentState().variableZindex) {
			setZIndexByPosition(getObjectsCurrentState().getY());
		} else {
			setZIndex(getObjectsCurrentState().zindex);
		}

		this.updateDebugInfo();

	}

	public void resetObject() {
		resetObject(true, true); // note: will call the subclasses override
		// remember, carefull not to cause loops
	}

	/**
	 * Resets the object (should be overridden for specific types) the options
	 * are for the overrides
	 ***/
	public void resetObject(boolean resetState, boolean reRunOnLoad) {
		SOLog.info("resetObject triggered");
		ObjectsLog("<-------resetObject triggered");
		if (movementsLoaded) {

			// start animations if its a default
			MovementPath DefaultMovementPath = objectsMovements.getMovement("default");
			ObjectsLog("got DefaultMovementPath");
			if (DefaultMovementPath != null) {
				ObjectsLog("running objects movements again");
				SOLog.info("running default movements again");
				playMovement(DefaultMovementPath, 5000);
			}
		}

		// --------
		// update debug boxes
		updateDebugInfo();

	}

	// See SceneObject Divs ovveridden version for details
	public void setVisible(boolean status) {
		setVisible(status, true);
	}

	public void setVisible(boolean status, boolean byPin) {

		ObjectsLog("(sov) setting " + this.objectsCurrentState.ObjectsName + " visiblity to:" + status);
		SOLog.info("setting " + this.objectsCurrentState.ObjectsName + " visiblity to:" + status);

		// if the visibility should be updated to match this object
		updateRelativeObjectsVisibility(status);

		// The following method correctly sets up visibility
		// The reason we have this "redirecting" method is so that any calls to
		// "setVisible"
		// will correctly go to the subclass method first (say
		// "SceneDialogueObjects" setVisible statement)
		// then that method would set up its (in this case dialogue-specific)
		// stuff. That method would then call
		// "super.setVisible" which triggers this method.
		// Then finnaly, this method triggers the one below!
		// We can also trigger the method below directly rather then this, if we
		// specificly dont want any subclass's (like Dialogue's setVisible)
		// being triggered.
		setVisibleSecretly(status);

	}

	private void updateRelativeObjectsVisibility(boolean status) {
		if (this.objectsCurrentState.PropagateVisibility) {
			SOLog.info("propergating visibility to relatively positioned objects");
			for (SceneObject so : relativeObjects) {
				so.setVisible(status); // should by pin be here?
			}
		}
	}

	public Simple2DPoint getLowerLeftDisplacement() {

		int brdy = this.getPhysicalObjectHeight() - this.objectsCurrentState.CurrentPinPoint.y;
		int brdx = -this.objectsCurrentState.CurrentPinPoint.x;

		Simple2DPoint dis = new Simple2DPoint(brdx, brdy);

		// SOLog.info("________________________________lower corner of object
		// is..."
		// + dis.x + "," + dis.y);

		return dis;

	}

	public Simple2DPoint getLowerLeft() {

		int X = this.objectsCurrentState.X;
		int Y = this.objectsCurrentState.Y + this.getPhysicalObjectHeight();

		return new Simple2DPoint(X, Y);
	}

	public Simple2DPoint getLowerRight() {

		int X = this.objectsCurrentState.X + this.getPhysicalObjectWidth();
		int Y = this.objectsCurrentState.Y + this.getPhysicalObjectHeight();

		return new Simple2DPoint(X, Y);
	}

	public Simple2DPoint getUpperLeft() {

		int X = this.objectsCurrentState.X;
		int Y = this.objectsCurrentState.Y;

		return new Simple2DPoint(X, Y);
	}

	public Simple2DPoint getUpperRight() {

		int X = this.objectsCurrentState.X + this.getPhysicalObjectWidth();
		int Y = this.objectsCurrentState.Y;

		return new Simple2DPoint(X, Y);
	}

	public void updateThingsPositionedRelativeToThis(boolean updateTouching) {
		
		//SOLog.info("updating objects relative to " + this.getName() + ":" + this.relativeObjects.size());

		if (relativeObjects != null) {
			
			this.ObjectsLog("Updateing child objects:"+relativeObjects.size());
			
			

			for (SceneObject so : relativeObjects) {

				// SOLog.info("updating: "+so.getName());

				// ignore if not attached yet?
				if (!so.isAttached()) {
					SOLog.warning(this.objectsCurrentState.ObjectsName + " attempted to update position of "
							+ so.objectsCurrentState.ObjectsName + " but its not attached to the page. Thus no op");
					continue;
				}

				if (so.getParentScene() == null) {
					SOLog.severe("___________" + so.getName() + "s Scene currently set to :"
							+ so.getObjectsCurrentState().ObjectsSceneName);
					SOLog.severe("__________ while updating relative objects on:" + this.getName());

					continue;
				}

				so.updateRelativePosition(updateTouching);

				// if the z-index should be updated, will only apply to objects
				// with zindex (ie, div objects)
				if (so.objectsCurrentState.linkedZindex) {

					so.setZIndex(this.getZindex() + so.objectsCurrentState.linkedZindexDifference);
					SOLog.info("setting zindex based on object to " + this.getZindex() + " + "
							+ so.objectsCurrentState.linkedZindexDifference);

				}

			}

		}
	}

	/**
	 * Used purely when loading, in case this is positioned relative to
	 * something that didnt exist when it was created
	 **/
	public void recheckRelativePositioningSetup() {

		String posRel = objectsCurrentState.positionRelativeToOnceLoaded;

		if (objectsCurrentState.positionRelativeToOnceLoaded.length() > 2) {

			SOLog.info("position relative to once loaded == " + posRel);
			ObjectsLog("________rechecking rel pos: " + objectsCurrentState.ObjectsName);
			ObjectsLog("__________________with pos: " + posRel);

			objectsCurrentState.positionedRelativeToo = SceneObjectDatabase.getSingleSceneObjectNEW(posRel, null, true);

			if (objectsCurrentState.positionedRelativeToo != null) {

				objectsCurrentState.positionRelativeToOnceLoaded = "";

				// update absolute position from a combination of this object
				// and the relative setting
				boolean changed =false;
				if (objectsCurrentState.positionedRelativeLinkType.linkX){
					objectsCurrentState.X = objectsCurrentState.positionedRelativeToo.getX() + objectsCurrentState.relX;
					changed=true;
				}
				if (objectsCurrentState.positionedRelativeLinkType.linkY){					
					objectsCurrentState.Y = objectsCurrentState.positionedRelativeToo.getY() + objectsCurrentState.relY;
					changed=true;
				}
				if (objectsCurrentState.positionedRelativeLinkType.linkZ){					
					objectsCurrentState.Z = objectsCurrentState.positionedRelativeToo.getZ() + objectsCurrentState.relZ;
					changed=true;
				}

			
				if (changed){
					ObjectsLog("___________position thus set to: " + objectsCurrentState.X + "," + objectsCurrentState.Y
							+ "," + objectsCurrentState.Z);

					setPositionOnItsScene(objectsCurrentState.X, objectsCurrentState.Y, objectsCurrentState.Z,
							objectsCurrentState.restrictPositionToScreen);
				} else {
					ObjectsLog("___________position not changed ");

				}
				
				// update relative z-index (if any)
				if (objectsCurrentState.linkedZindex) {

					// objectsCurrentState.zindex =
					// objectsCurrentState.positionedRelativeToo.getObjectsCurrentState().zindex+objectsCurrentState.linkedZindexDifference;
					SOLog.info("Relative zindexd on " + objectsCurrentState.ObjectsName + " set to "
							+ objectsCurrentState.zindex);
					setZIndex(0); // NOTE: the value we give this function is
					// irrelevant when on linked z-index, we are
					// just telling it to refresh now we know
					// the parent

				}

			} else {
				SOLog.warning("could not find " + posRel);
				ObjectsLog("could not find " + posRel, LogLevel.Error);
			}

			objectsCurrentState.positionedRelativeToo.addChild(this); //relativeObjects.add(this);


		}

	}

	/**
	 * Detaches this object from any parents and makes it positioned absolutely
	 * to the scene You can imagine this like a character wearing a hat, and
	 * then dropping it on the floor
	 **/
	protected void makeAbsolute() {

		// make sure this is even needed;
		if (objectsCurrentState.positionedRelativeToo == null) {
			return;
		}

		// else we get the absolute location
		int xpos = getX();
		int ypos = getY();
		int zpos = getZ();

		// detach it

		objectsCurrentState.positionedRelativeToo.removeChild(this); //relativeObjects.remove(this);
		objectsCurrentState.positionedRelativeToo = null;

		// set its "new" location, which should be visually the same as the old
		// one
		setPosition(xpos, ypos, zpos, true);

	}

	/**
	 * onStep actions are designed to be run every so many pixels of movement.
	 * They are useful but should be used sparingly as they are cpu intensive.
	 * This method checks if the onStep actions should be triggered
	 * 
	 * NOTE: does not currently 'catch up' for steps missed. each onstep is fired once each updated at most.
	 */
	public void advanceOnStepActions(double disX, double disY) {

		if (actionsToRunOnStep != null && !actionsToRunOnStep.isEmpty()) {

			int dis = (int) (Math.hypot(disX, disY));
			// SOLog.info("___________________________________currentStep="+ currentStep);
			// SOLog.info("___________________________________stepActionIterval="+ stepActionIterval);


			// test all onstep intervals
			for (ActionSet actions : actionsToRunOnStep.keySet()) {

				//each onstep command keeps its own count of how far since its been triggered (currentStep)
				//this is kept in its associated onStepInfo
				//we then test that currentStep against its step interval (interval)
				//this is also kept in the actions associated onStepInfo'
				
				onStepInfo onStepsData = actionsToRunOnStep.get(actions); //get the actions onStepInfo
				
				onStepsData.currentStep=onStepsData.currentStep+dis;//update that infos currentstep by adding the new distance

				//just a log
				SceneObject.this.ObjectsLog("(temp)----onstep for int"+onStepsData.interval+" now "+onStepsData.currentStep);
				
				//test if this actions currentStep is beyond its interval
				//if so, reset it to 0, and run the Actions to run for this onStep
				if (onStepsData.currentStep >= onStepsData.interval) {
				
					
					onStepsData.currentStep =0; //reset counter for this specific onstep
					SceneObject.this.ObjectsLog("(temp)----onstep cleared for int"+onStepsData.interval);
					//run actions for this specific onstep
					InstructionProcessor.processInstructions(actions.CommandsInSet.getActions(), "FROM_"
							+ objectScene.SceneFileName + "_" + SceneObject.this.objectsCurrentState.ObjectsName,
							SceneObject.this);

				}

			}

		}
	}

	/** Runs actions stored when a neighboring object changes **/
	public void runOnDirectionChangeActions() {

		// actions

		// run them
		if (actionsToRunOnDirectionChange != null) {

			// check for object specific actions
			CommandList actions = actionsToRunOnDirectionChange.CommandsInSet.getActions();
			// objectsActions.getActionsForTrigger(TriggerType.OnDirectionChanged,
			// null);

			// updatedLastClickedOn();

			SOLog.info("ondirectionchanged actions found: \n" + actions.toString());

			// InstructionProcessor.processInstructions(actions, "FROM_"
			// + objectScene.SceneFileName + "_"
			// + this.objectsCurrentState.ObjectsName, this);

			InstructionProcessor.processInstructions(actions,
					"FROM_" + objectScene.SceneFileName + "_" + this.objectsCurrentState.ObjectsName, this);

		}
		// do we check for global stuff?

	}

	/**
	 * 
	 */
	protected void updateFade(float currentDelta) {

		double fadeChangeAmount = stepPerMS * currentDelta;

		switch (currentFade) {
		case None:
			// do nothing if not fading in or out
			return;

		case FadeIn:
			objectsCurrentState.currentOpacity = objectsCurrentState.currentOpacity + fadeChangeAmount; {
				if (objectsCurrentState.currentOpacity >= 1.0) {
					// Opacity = 100;
					setVisible(true);
					currentFade = FadeMode.None;
					// this.cancel();
					if (afterFadeIn != null) {
						afterFadeIn.run();
					}
				}
			}
			break;

		case FadeOut:
			objectsCurrentState.currentOpacity = objectsCurrentState.currentOpacity - fadeChangeAmount; {
				if (objectsCurrentState.currentOpacity <= 0.0) {
					// this.cancel(); //no need to cancel as the update function
					// will do that itself
					// Opacity = 0;
					setVisible(false);
					currentFade = FadeMode.None;

					if (afterFadeOut != null) {
						afterFadeOut.run();
					}

				}
			}
			break;
		}

		// update with the new opacity
		setOpacityImplementation(objectsCurrentState.currentOpacity);

		// update any objects we propergate our visibility too as well
		updateRelativeObjectsOpacity();

	}

	private void updateRelativeObjectsOpacity() {
		if (objectsCurrentState.PropagateVisibility) {
			// SOLog.info("propergating visibility to relatively positioned
			// objects");
			for (SceneObject so : relativeObjects) {
				so.setOpacityImplementation(objectsCurrentState.currentOpacity);
			}
		}
	}

	/**
	 * Generates a movement path for this object to emulate it being thrown
	 * towards another
	 ***/
	// TODO: once we have some true 3d based collision system we can use the
	// physics system "addImpulse" combined with normal gravity
	// will actually make a correctly curved path for us, we just need to make
	// sure the impulse is in the right direction and strength. (which can be
	// done with basic trig)
	public void chuckObject(SceneObject fromThis, final SceneObject atThis) {

		// Ensure we are set to the right scene, as if the object is from the
		// inventory it might not have a scene set
		if (this.objectScene != atThis.objectScene) {
			setCurrentlyStoredInInventory(false);

		}

		// ---

		int realStartHeight = getPhysicalObjectHeight(); // height of start object
		// (currently faked)

		// get start x/y/z (middle of fromThis)
		int cx = Math.round(fromThis.objectsCurrentState.X + (fromThis.getPhysicalObjectWidth() / 2));
		int cy = Math.round(fromThis.objectsCurrentState.Y + (fromThis.getPhysicalObjectHeight() / 2));
		int cz = Math.round(fromThis.objectsCurrentState.Z + (realStartHeight / 2));

		int realDestHeight = atThis.getPhysicalObjectHeight(); // height of destination
		// object

		// get target x/y (middle of atThis object on the ground level)
		// z (middle of its height)
		int tx = Math.round(atThis.getTopLeftBaseX() + (atThis.getPhysicalObjectWidth() / 2));
		int ty = Math.round(atThis.getTopLeftBaseY() + (atThis.getPhysicalObjectHeight())); // probably
		// wrong?
		// should
		// be
		// the
		// middle
		// of
		// the
		// cmap
		// y
		// not
		// the
		// whoke
		// objects
		int tz = Math.round(atThis.getTopLeftBaseZ() + (realDestHeight / 2));

		// Compensate for this objects size (so we are aligning center to
		// center)
		tx = Math.round(tx - (this.getPhysicalObjectWidth() / 2));
		ty = Math.round(ty - (this.getPhysicalObjectHeight() / 2));
		int ourRealHeight = 0; // height of destination object
		tz = Math.round(tz - (ourRealHeight / 2));

		// TODO: currently we get the midpoint of the target object. This is
		// wrong
		// We should instead get the collision point of a line drawn from the
		// start locations base to the target objects base
		// if (atThis.cmap.isPresent()){

		ObjectsLog("(testing target location new method): " + tx + "," + ty + "," + tz);

		/*
		 * Simple2DPoint start = new Simple2DPoint(cx, cy); Simple2DPoint end =
		 * new Simple2DPoint(tx, ty );
		 * 
		 * SpiffyPolygonCollision col;
		 * 
		 * for (Polygon poly : atThis.cmap.get() ) { col =
		 * poly.testForCollision(start, end); if (col!=null){ //cx = col.X; //cy
		 * = col.Y; tx = col.X; ty = col.Y;
		 * 
		 * ObjectsLog("Target set to: "+tx+","+ty+","+tz); break; } }
		 */

		// TODO: why isnt width taken into account?
		ArrayList<MovementWaypoint> pathWithSafeEnding = this.getParentScene().findSafeEndpointOnLine(cx, cy, tx, ty,
				this);
		// we want the new endpoint, but we arnt interested in any bounce, so we
		// grab the last part of the array

		tx = pathWithSafeEnding.get(pathWithSafeEnding.size() - 1).pos.x;
		ty = pathWithSafeEnding.get(pathWithSafeEnding.size() - 1).pos.y;

		// }

		final int throwSpeed = 100; // pixels per second

		ObjectsLog(" Chucking object at middle of " + atThis.getName() + " object: " + tx + "," + ty + "," + tz);

		// generate the curve path
		MovementPath trajectory = new MovementPath("", "trajectory");
		trajectory.add(new MovementWaypoint(cx, cy, cz, MovementType.AbsoluteMove)); // start

		MovementWaypoint curve = new MovementWaypoint(tx, ty, tz, MovementType.AbsoluteQCurveToo);

		curve.midPoint1.x = Math.round((cx + tx) / 2);
		curve.midPoint1.y = Math.round((cy + ty) / 2);// -90;
		curve.midPoint1.z = Math.round((cz + tz) / 2) + 400; // 100 is how high
		// up the arc
		// goes

		trajectory.add(curve); // middle and destination of hit

		// Now we set commands to run ""on impact""
		// We have to manually set the impact speed because this is a path and
		// not real physics. There is thus no "real" impact.
		Runnable manuallySetImpactSpeed = new Runnable() {
			@Override
			public void run() {
				SceneObject.this.SpeedOfLastImpact = throwSpeed / 1000.0;
				SOLog.info("Mid-chuck impact:" + SceneObject.this.SpeedOfLastImpact + " between "
						+ SceneObject.this.getName() + " and " + atThis.getName());
				SceneObject.this.addTouchingProperty(atThis, SceneObject.this);
				SceneObject.this.SpeedOfLastImpact = 0;
				SceneObject.this.removeTouchingProperty(atThis, SceneObject.this);

			}
		};
		MovementWaypoint command_manually_addImpactSpeed = new MovementWaypoint(manuallySetImpactSpeed);
		trajectory.add(command_manually_addImpactSpeed);

		/*
		 * //after curve, its briefly touching the target object //[-
		 * AddObjectTouching = Zombie,Wardrobe] MovementWaypoint command = new
		 * MovementWaypoint("- AddObjectTouching = "+this.objectsCurrentState.
		 * ObjectsName+","+atThis.objectsCurrentState.ObjectsName);
		 * trajectory.add(command);
		 * 
		 * Runnable manuallyRemoveImpactSpeed = new Runnable(){
		 * 
		 * @Override public void run() { SceneObject.this.SpeedOfLastImpact = 0;
		 * } }; MovementWaypoint command_manuallyRemoveImpactSpeed = new
		 * MovementWaypoint(manuallyRemoveImpactSpeed);
		 * trajectory.add(command_manuallyRemoveImpactSpeed);
		 * 
		 * //TODO: commands should be specified in a non-string form?
		 * 
		 * MovementWaypoint command3 = new
		 * MovementWaypoint("- RemoveObjectTouching = "+this.objectsCurrentState
		 * .ObjectsName+","+atThis.objectsCurrentState.ObjectsName);
		 * trajectory.add(command3);
		 */
		// maybe a momentum based approach?
		//

		// fall down after
		// int fallDownBy=atThis.objectsCurrentState.FakeHeight;
		// //Math.round((atThis.getOffsetHeight()/2))-this.getOffsetHeight();

		// we fall down from the center of the object to the floor
		// This is by default done by half the objects height + the fake height
		// the object was at
		// int fallDownBy = atThis.objectsCurrentState.Z +
		// (atThis.getObjectHeight()/2);

		// EXPIREMENTAL: if the object has a cmap we then look upwards from this
		// point to find the first collision. We consider
		// that the ground under the center of the object. (without this in
		// isometrix games the collision will go under the square sprite and not
		// its real border)
		/*
		 * if (atThis.cmap.isPresent()){
		 * 
		 * Simple2DPoint targetPossibilitysStart = new Simple2DPoint(tx,ty);
		 * //the current target for where to fall too (we will move up from here
		 * to find the real edge of the object we hit) ty = ty+fallDownBy;
		 * Simple2DPoint targetPossibilitysEnd = new Simple2DPoint(tx,ty); //the
		 * current target for where to fall too (we will move up from here to
		 * find the real edge of the object we hit)
		 * 
		 * //(The following should be refractored into a cmap function really)
		 * //find first collision Iterator<Polygon> cit =
		 * atThis.cmap.get().iterator();
		 * 
		 * SpiffyPolygonCollision pc = null;//will store the collision, if
		 * there's one
		 * 
		 * while (cit.hasNext()) { Polygon polygon = (Polygon) cit.next();
		 * 
		 * //skip if not visible or incoporal if (polygon.incorporeal){
		 * continue; }
		 * 
		 * SpiffyPolygonCollision nc =
		 * polygon.testForCollision(targetPossibilitysEnd,
		 * targetPossibilitysStart);
		 * 
		 * if (nc != null) { if (pc == null || nc.distance < pc.distance) {
		 * 
		 * pc = nc;
		 * 
		 * } }
		 * 
		 * 
		 * }
		 * 
		 * if (pc!=null){ ty = pc.Y; //now we steal its Y }
		 * 
		 * } else { tx = tx; //remains the same ty = ty;+fallDownBy; //just move
		 * down by the height worked out as half the objects size
		 * 
		 * }
		 */

		// --

		// now we need to find a safe point for the end. This should be the
		// nearest point outside of the collision map for point tx,ty,0
		// In future when we have true (or more true) physics, this should be
		// replaced by it just falling down to the surface under where it hit

		trajectory.add(new MovementWaypoint(tx, ty, 0, MovementType.AbsoluteLineTo)); // end
		// point.
		// this
		// should
		// be
		// safe
		// if
		// collision
		// was
		// checked
		// above....but
		// it
		// doesnt
		// seem
		// to
		// me.hmz
		// TODO: check zindex updates on path?
		// Do we update zindex at the end based on position?
		// Or all over the chuck?
		ObjectsLog("Made Chuck Path");

		// remove relative pos

		this.detach();
		/*
		 * if (this.objectsCurrentState.positionedRelativeToo!=null){
		 * 
		 * this.objectsCurrentState.positionedRelativeToo.relativeObjects.remove
		 * (this); this.objectsCurrentState.positionedRelativeToo = null;
		 * 
		 * }
		 */

		SOLog.info("~~~~~setting starting position for path playback :");

		this.setPosition(cx, cy, cz, false); // we dont update collisions at
		// first, as we manually control
		// the impact on the path
		// itself, and at the end of the
		// path it updates again

		// ObjectsLog("Setting visible ");

		this.setVisible(true);

		// ObjectsLog("Running chuck motion");

		// use the path we just made
		this.playMovementAtFixedSpeed(trajectory, throwSpeed);

		// ObjectsLog("Play chuck Movement triggered");
	}

	public void updateRelativelyZIndexedObjects(int newzindex) {
		if (relativeObjects != null) {

			SOLog.info("~~~~~setting relative zindexs to:" + this.objectsCurrentState.ObjectsName);

			for (SceneObject so : relativeObjects) {

				if (so.objectsCurrentState.linkedZindex) {
					// so.setZIndexInternal(newzindex +
					// so.objectsCurrentState.linkedZindexDifference);

					so.setZIndex(newzindex + so.objectsCurrentState.linkedZindexDifference);
					// NOTE: Things with a linkedZIndex should not also have a
					// variable zIndex, as these things conflict

				}

			}
		}
	}

	/**
	 * sets the z-index to the value, if appropriate. In cases where zindex is
	 * determined by position or a parent object, newzindexs value is not used
	 * at all, and instead we just update
	 * 
	 **/
	public void setZIndex(int newzindex) {

		 SOLog.info("Setobjectzindex: "+newzindex);

		if (objectsCurrentState.linkedZindex) {

			SceneObject positionedRelativeToo = objectsCurrentState.positionedRelativeToo;
			if (positionedRelativeToo != null) { // ensure parent is set first
				int parentZ = positionedRelativeToo.getZindex();
				newzindex = parentZ + objectsCurrentState.linkedZindexDifference;

				ObjectsLog("____setting ZIndex to:" + parentZ + "+" + objectsCurrentState.linkedZindexDifference);
				setZIndexImplementation(newzindex);

				ObjectsLog("____updating relatively zindex linked objects:");
				updateRelativelyZIndexedObjects(newzindex);
			}
		} else if (objectsCurrentState.variableZindex) {

			SOLog.info("____setting ZIndex by Y position & cmap [if present]");
			ObjectsLog("____setting ZIndex by Y position & cmap [if present]");

			// ignore the number and set by position
			setZIndexByPosition(this.getY());

		} else {

			 SOLog.info("____setting ZIndex to: "+newzindex);
			ObjectsLog("____setting ZIndex to:" + newzindex);
			setZIndexImplementation(newzindex);

			ObjectsLog("____updating relatively zindex linked objects:");
			updateRelativelyZIndexedObjects(newzindex);

		}

	}

	/**
	 * 
	 */
	public void setVaribleZIndexOff() {
		ObjectsLog("Setting variable zindex off )");

		getObjectsCurrentState().variableZindex = false;

	}

	/** sets the specifications for the variable zindex **/
	public void setVaribleZIndex(int lowestZindex, int heightZIndex, int step) {

		getObjectsCurrentState().variableZindex = true;
		getObjectsCurrentState().upperZindex = heightZIndex;
		getObjectsCurrentState().lowerZindex = lowestZindex;
		getObjectsCurrentState().stepZindex = step;

		ObjectsLog(
				"Setting variable zindex to:" + lowestZindex + "-->" + heightZIndex + "  ( by units of " + step + " )");

		// recalc data
		setUpVariableZindex();

		// update
		setZIndexByPosition(this.getY());

	}

	/**
	 * temp system that lets SceneObject run commands from its movements
	 * eventually this will have to be inlined to SceneObject But that depends
	 * on the instructionprocessor no longer having a SceneObjectVisual
	 * dependency.
	 * 
	 * @param command
	 */
	protected void triggerMidMovementCommand(CommandList Command) {
		SOLog.info("running command :" + Command.getCode());

		InstructionProcessor.processInstructions(Command, "movec_" + objectsCurrentState.ObjectsName, this);
	}

	/**
	 * convience method to check if this object is either a SceneTextObject or a
	 * SceneDialogueObject
	 **/
	public boolean isTextObject() {

		if (this.objectsCurrentState.getPrimaryObjectType() == SceneObjectType.DialogBox) {
			return true;
		}
		if (this.objectsCurrentState.getPrimaryObjectType() == SceneObjectType.Label) {
			return true;
		}
		return false;
	}

	// Things that subclasses need to implement
	/**
	 * Makes the internal object reflect the current state.
	 * 
	 * Normally only needed when loading, as once loaded the sceneobject and its objectstate should be in sycn anyway 
	 * and any changes to the state should be done with updatestate which ensures they are kept sycn.	
	 * initialistion should only fire when the object is on the scene
	 * @param b
	 */
	public abstract void initCurrentState(boolean runOnload);
	
	
	/**
	 * This needs to implement the actual removing of the object from its scene.
	 * This should purely remove it visually, as the internal data should
	 * already be changed by the time this is called.
	 * NOTE: the object should not be used to move a object to a different scene - use setObjectsScene for that.
	 * This method might dispose/remove all objects resources, thus its designed for removing from the game completely.
	 */
	public abstract void removeThisObjectFromItsSceneImplementation();

	/**
	 * MUST BE IMPLEMENTED when this is triggered
	 * 
	 * InstructionProcessor.lastclicked_x
	 * InstructionProcessor.lastclicked_y
	 *
	 * and
	 * 
	 * InstructionProcessor.lastclickedscreen_x = x;
	 * InstructionProcessor.lastclickedscreen_y = y;
	 *
	 * needs to be updated to the last clicked location
	 **/
	public abstract void updateLastClickedLocation();

	/**
	 * Called from the animation system. Should not be called separately as then
	 * the positional variables and the real position will be out of sync.
	 * 
	 * normally; objectScene.setObjectsPosition(this,x,
	 * y,restrictPositionToScreen); would be a typical implementation.
	 * 
	 * Note: Your SceneWidget implementation will need to have its own
	 * setObjectsPosition method supplied to it in turn. which takes your
	 * SceneObject class
	 *
	 */
	protected abstract void setPositionOnItsScene(int x, int y, int z, boolean restrictPositionToScreen);

	/**
	 * actions that run after movement ends. NB: This is here mostly as a
	 * transitional state while objectactions are handeled in sceneobjectvisual.
	 * Eventually all actions must be handled by SceneObject itself, after thats
	 * done the triggerMovementEndCommands() can be moved to this file and this
	 * abstract can be removed
	 **/
	// abstract public void triggerMovementEndCommands();

	/**
	 * again like triggerMovementEndCommands and setPositionOnItsScene this is a
	 * temp abstract to help transition the split between GWT and JAMCore In
	 * this case this is to allow movements to trigger commands mid-movement,
	 * despite the fact the instruction processor needs SceneObjectVisuals
	 * handed to it Once InstructionProcessor can take sceneobjects (which is a
	 * lot of work), then this can be removed in the same way as the above, that
	 * is its code from SceneObjectVisual moved to SceneObject
	 * 
	 * @param Command
	 */
	// abstract protected void triggerMidMovementCommand(String Command);

	/**
	 * again, same as triggerMidMovementCommand
	 */
	// abstract protected void runOnDirectionChangeActions();

	/**
	 * Much like triggerMovementEndCommands this is here for transitional
	 * reasons and can be removed when its triggerMovementEndCommands method is
	 * put in this file
	 * 
	 * The intention of advanceOnStepActions() is to fire events every few steps
	 * of a character moving. (usefull for sycning frame animations to movement)
	 */
	// abstract public void advanceOnStepActions(double disX, double disY);

	/**
	 * this should return true if the object has been placed on the scene or is
	 * visible in some way, or false if not either
	 **/
	public abstract boolean isAttached();

	/**
	 * Optionally this method can be used to tell a debug inspecting tool that
	 * the objects data has been updated. Implement it if you want to be attach
	 * something that updates on any change to a object, else create just a
	 * empty method that does nothing
	 **/
	public abstract void updateDebugInfo();

	/**
	 * Optionally this method can be used to open a object inspector for
	 * debugging Implement it if your implementation supports in-game debugging
	 **/
	public abstract void openObjectsInspector();

	/**
	 * If you are implementing object inspectors, this should both close and
	 * clear any inspectors for this object.
	 */
	public abstract void clearObjectsInspector();

	/**
	 * the implementation of set opacity. NOTE: this should be implemented on
	 * the class that extends this, but not used directly. This method should
	 * not be called anywhere directly. It should ONLY do whats needed to set
	 * the visual opacity of a element to the specified percentage ie, if its a
	 * web implementation, you could use the Elements style
	 * getElement().getStyle().setOpacity(opacityPercentage);
	 * 
	 * Set the opacity of this object
	 * 
	 * @param opacityDouble
	 *            0-1 (1=fully visible,0=invisible)
	 */
	protected abstract void setOpacityImplementation(double opacityDouble);

	/**
	 * the implementation of set visible. NOTE: this should be implemented on
	 * the class that extends this, but not used directly. Things should go via
	 * the normal setVisible statement, and extra functions should, if needed,
	 * be added by overriding that or SetVisibleSilently. This method should not
	 * be called anywhere, and only has to be do whats needed to make the
	 * SceneObject visible or invisible on the screen
	 **/
	protected abstract void setVisibleImplementation(boolean status);

	/** sets the objects Z-Index by position. In future this will be inline **/
	// abstract void setZIndexByPosition(int Y);

	/**
	 * getZIndex should return the Z index if the html element if its a html
	 * implementation. How to handle this generically is tricky. Maybe
	 * eventually this should just be a plane Z-ordinate and 2d games handled as
	 * if directly overhead, with the z co-ordinate becoming the z index of the
	 * stack? In which case the idea of a "linkedZindex" because redundant as
	 * thats the same as a object positioned relative to another and then moving
	 * towards/away from the camera.
	 * 
	 * in either case it should probably default to "objectsCurrentState.zindex"
	 **/
	abstract public int getZindex();

	// abstract public void setZIndex(int zindex);

	public abstract void setZIndexImplementation(int newzindex);

	/**
	 * returns the exact screen width of the object (in pixels if sprite) The exact
	 * implementation of the exact object type determines this Its primarily
	 * used for stuff like pathfinding, determining if something is within the
	 * bounding box of the object and chucking the object into the inventory
	 */
	abstract public int getPhysicalObjectWidth();

	/**
	 * returns the exact screen height of the object (in pixels if sprite) The exact
	 * implementation of the exact object type determines this Its primarily
	 * used for stuff like pathfinding, determining if something is within the
	 * bounding box of the object and chucking the object into the inventory
	 */
	abstract public int getPhysicalObjectHeight();

	/**
	 * returns the absolute left of the object in pixels relative to the games
	 * client screen in X The exact implementation of the exact object type
	 * determines this Its primarily used for chucking the object to a inventory
	 */
	abstract public int getAbsoluteLeft();

	/**
	 * returns the absolute top of the object in pixels relative to the games
	 * client screen in Y and measured downwards The exact implementation of the
	 * exact object type determines this Its primarily used for chucking the
	 * object to a inventory
	 */
	abstract public int getAbsoluteTop();

	public void setPin(int X, int Y, int Z,boolean updateDefault) {

		//to preserve position we save it first
		Simple3DPoint oldpos;
		if (objectsCurrentState.positionedRelativeToo == null) {
			oldpos= new Simple3DPoint(objectsCurrentState.getX(),objectsCurrentState.getY(),objectsCurrentState.getZ()); 
		} else {
			oldpos= new Simple3DPoint(objectsCurrentState.relX, objectsCurrentState.relY, objectsCurrentState.relZ);			 			
		}
		//		
		objectsCurrentState.CurrentPinPoint.x = X;
		objectsCurrentState.CurrentPinPoint.y = Y;
		objectsCurrentState.CurrentPinPoint.z = Z;

		//update the default (should not update if the setting came from a movement file) 
		if (updateDefault){
			objectsCurrentState.DefaultPinPoint.set(objectsCurrentState.CurrentPinPoint);
		}

		//reset our position
		setPosition(oldpos.x, oldpos.y, oldpos.z, true, true,true);

		// update relatively positioned objects
		updateThingsPositionedRelativeToThis(true);

	}

	/**
	 * refreshes this objects location based on its pin.
	 * 
	 */
	public void refreshPosition(){

		if (objectsCurrentState.positionedRelativeToo == null) {
			setPosition(this.getX(), this.getY(), this.getZ(), true, true,true);

		} else {			
			setPosition(objectsCurrentState.relX, objectsCurrentState.relY, objectsCurrentState.relZ, true, true,true);			
		}

	}

	/**
	 * clears the attachment points and (if not in cache) triggers a load from a
	 * glu file
	 * @param b 
	 **/
	public void getAttachmentPointMovements(final String atachmentPointUrl) {
		getAttachmentPointMovements(atachmentPointUrl,false);
	}


	/**
	 * clears the attachment points and (if not in cache) triggers a load from a
	 * glu file
	 * @param b 
	 **/
	public void getAttachmentPointMovements(final String atachmentPointUrl, boolean reequiredForLogicalLoad) {


		// clear existing ones
		attachmentPoints = null;

		// ensure we dont already have a cache of these points
		// Note, as its cached here, we should turn of the cache on the file
		// manager to save ram.
		AttachmentList cacheTest = attachmentPointCache.get(atachmentPointUrl);

		if (cacheTest != null) {
			SOLog.info("attachment points for: " + atachmentPointUrl + " found in cache");
			attachmentPoints = cacheTest;
			if (reequiredForLogicalLoad){
				AttachmentPointsLoadedOrNotNeeded = true;
				SceneObject.this.ObjectsLog("got attachment from cache (AttachmentPointsLoadedOrNotNeeded)");				
				testIfWeAreLogicallyLoaded();
			}

			return;
		}

		SOLog.info("getting attachmet points for url: " + atachmentPointUrl);
		loadAttachmentPoints(atachmentPointUrl, true,reequiredForLogicalLoad,null);

	}

	/***
	 * This is a visual check if the object overlaps in 2d screen space.
	 * Its used right now for click detection.
	 * Currently Z is subtracted from the Y position to work out the hit rectangle.
	 * 
	 * TODO: upgrade or make alternative method for 3d this to support true 3d positioning (this will first
	 * require objects have a height and thus a bounding box volume)
	 * 
	 * @param lastclicked_x
	 *            - position on the dragpanel in 2d browser co-ordinates
	 * @param lastclicked_y
	 * @return
	 */
	public boolean testIfMouseWouldHit(int lastclicked_x, int lastclicked_y) {

		SOLog.info("testing if:" + lastclicked_x + "," + lastclicked_y + " is overlapping with object "+this.getName());

		// get current top left (2d)
		int topleftX = this.getTopLeftBaseX();
		int topleftY = this.getTopLeftBaseY() - this.getTopLeftBaseZ(); //in order to find the box in 2d screen space we need to subtract Z. This is because Y is messured down from screen top, and Z up from that location.
		//Because thats how fake 3d works 


		// get bottom right (2d)
		int bottomrightX = topleftX + this.getPhysicalObjectWidth();
		int bottomrightY = topleftY + this.getPhysicalObjectHeight();

		// test if its within that box;
		if ((lastclicked_x > topleftX) && 
				(lastclicked_x < bottomrightX)) {

			if ((lastclicked_y > topleftY) && 
					(lastclicked_y < bottomrightY)) 
			{
				SOLog.info("its overlaping!");
				return true;
			}

		}

		SOLog.info("its not overlaping!");
		return false;
	}

	/**
	 * This function will tell the instruction processor this object was the
	 * last updated. It does this by checking this objects type then setting the
	 * right "last___" variable to this in InstructionProcessor.
	 * 
	 * This can update multiple types (ie, if its Sprite object it will also
	 * count as the last Div, because a Sprite will do all the functions of a
	 * Div)
	 */
	public void wasLastObjectUpdated() {
		SceneObjectDatabase.wasLastObjectUpdated(this);
	}

	public void wasLastObjectClicked() {
		SceneObjectDatabase.wasLastObjectClicked(this);
	}

	/**
	 * start loading the animations Note; Animations shouldn't run unless the
	 * "animationLoaded" flag is true
	 * 
	 * 
	 * Note; this should only be triggered from subtypes
	 * 
	 **/
	protected void startLoadingMovementAnimations() {

		// get the moment file, if this scene object has one
		if (objectsCurrentState.hasMovementFile) {

			// String url = objectScene.SceneFolderLocation + "/Objects/"+
			// folderName + "/movements.ini";
			SOLog.info("getting " + getObjectsCurrentState().ObjectsName + " movement file...");
			String url = objectScene.SceneFolderLocation + "/Objects/" + getObjectsCurrentState().ObjectsName
					+ "/movements.ini";

			loadMovements(url);
		} else {
			movementsLoaded = false;
			MovementFilesLoadedOrNotNeeded = true;

			SceneObject.this.ObjectsLog("(MovementFilesLoadedOrNotNeeded)");
			testIfWeAreLogicallyLoaded();
		}

	}

	protected void loadMovements(String url) {

		// set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable() {
			@Override
			public void run(String responseData, int responseCode) {

				MovementFilesLoadedOrNotNeeded = true;
				SceneObject.this.ObjectsLog("got movement file response (MovementFilesLoadedOrNotNeeded)");
				testIfWeAreLogicallyLoaded();

				// return if 404
				if (responseCode >= 400 || responseCode == 204) {
					SOLog.info("________no movement file recieved (404):\n");
					movementsLoaded = false;
					return;
				}

				SOLog.info("________movement recieved:\n" + responseData);

				final String MovementData = responseData;

				// return if no paths found
				if (!MovementData.contains("-Path")) {
					movementsLoaded = false;
					SOLog.info("________no movement file recieved :\n");

					return;
				}

				// get the items for the scene
				objectsMovements = new MovementList(MovementData);

				// if successfully loaded then set to true
				movementsLoaded = true;

				// start animations if its a default
				MovementPath DefaultMovementPath = objectsMovements.getMovement("default");

				if (DefaultMovementPath != null) {
					SceneObject.this.playMovement(DefaultMovementPath, 5000);
				}

			}
		};

		// what to do if there's an error
		FileCallbackError onError = new FileCallbackError() {
			@Override
			public void run(String errorData, Throwable exception) {

				SOLog.info("no file found (so no movements set)");
				movementsLoaded = false;
				MovementFilesLoadedOrNotNeeded = true;
				SceneObject.this.ObjectsLog("no movement file found (MovementFilesLoadedOrNotNeeded)");
				testIfWeAreLogicallyLoaded();
			}

		};

		RequiredImplementations.getFileManager().getText(url,true, onResponse, onError, false);
	}

	public void triggerMovementEndCommands() {

		if (objectsActions != null) {

			final CommandList actions = objectsActions.getActionsForTrigger(TriggerType.OnMovementEnd, null);

			// if its not empty (ie, there's actions)
			if (!actions.isEmpty()) {

				final String CurrentObjectName = objectsCurrentState.ObjectsName;

				SOLog.info("____________________" + CurrentObjectName + " __movement end actions found: \n"
						+ actions.toString());

				ObjectsLog("Running movementend actions: \n"+actions.size(),"green");

				InstructionProcessor.processInstructions(actions,
						"FROMMoveEnd_" + objectScene.SceneFileName + "_" + CurrentObjectName, this);
			}
		}
	}

	/**
	 * is the specified point either encased or touched by this object?
	 * 
	 * @param pinPointX
	 * @param pinPointY
	 * @return
	 */
	private boolean isPointTouching(int pinPointX, int pinPointY) {

		if (cmap.isPresent()) {

			// SOLog.info("testing for line against collision map");
			return cmap.get().isCollidingWith(new Simple2DPoint(pinPointX, pinPointY));

		} else {

			// SOLog.info("testing for line against collision map");

			switch (getObjectsCurrentState().boundaryType.collisionType) {
			case bottomline:
				SOLog.severe("testing for point against  bottomline not yet supported ");

				break;
			case box:
				SOLog.severe("testing for point against  box not yet supported ");

				break;
			case none:
				return false;
			case point:
				SOLog.severe("testing for point against  point not yet supported ");

				break;
			default:
				break;

			}

		}

		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * This should fire just after any movement It clears the internal cache of
	 * touching objects in the collision map (if any) as well as then updating
	 * the touching object list.
	 */
	private void wasJustMoved(boolean fromAnimation) {

		// first update the touching data
		if (cmap.isPresent()) {
			cmap.get().clearTouchingCache();

			// also remove ourselves from every other cache
			Polygon.removeFromAllcaches(cmap.get());

			/*
			 * faster maybe; for (String touching :
			 * getObjectsCurrentState().touching) { SceneObject curobject =
			 * SceneObjectDatabase.getSingleSceneObjectNEW(touching, null,
			 * true); if (curobject.cmap.isPresent()) {
			 * curobject.cmap.get().removeFromCache(cmap.get()); } }
			 */

		}

		if (getObjectsCurrentState().ObjectsTouchingUpdateMode == touchingMode.Automatic) {
			updateTouchingAutomatically();
		}

		// then fire any movement end commands, if there's any
		if (fromAnimation) {
			triggerMovementEndCommands();
		}
	}

	public boolean isLineTouching(Simple2DPoint start, Simple2DPoint end) {

		// if we have a collision map
		if (cmap.isPresent()) {

			// SOLog.info("testing for line against collision map");
			//	ObjectsLog("testing for line against collision map:" + start.toString() + "-->" + end.toString(), "orange");

			return cmap.get().isCollidingWith(start, end);

		} else {

			// we use our boundary mode
			switch (getObjectsCurrentState().boundaryType.collisionType) {

			case bottomline:

				// SOLog.info("testing for bottom line against line");
				// test for line against testAgainst
				Simple2DPoint ourLowerLineStart = this.getLowerLeft();
				Simple2DPoint ourLowerLineEnd = this.getLowerRight();

				if (Polygon.lineIntersect2d(ourLowerLineStart, ourLowerLineEnd, start, end) != null) {
					return true;
				}

				break;
			case box:

				// test for box against testAgainst
				if (getObjectsCurrentState().boundaryType.customCollisionBox.isPresent()) {

					SOLog.severe("testing for custom bounding box against  line (NOT IMPLEMENTED YET)");
					// should be easy, the information is in the boundaryType
					// enum customBox variable above
				} else {
					// SOLog.info("testing for default bounding box against line
					// ");
					// should be easy, just use objects size/shape

					// this objects bounding box
					Simple2DPoint ourUpperLeft = this.getUpperLeft();
					Simple2DPoint ourUpperRight = this.getUpperRight();
					Simple2DPoint ourLowerLeft = this.getLowerLeft();
					Simple2DPoint ourLowerRight = this.getLowerRight();

					// now test each line made from those points
					if (Polygon.lineIntersect2d(ourUpperLeft, ourUpperRight, start, end) != null) {
						return true;
					}
					if (Polygon.lineIntersect2d(ourUpperRight, ourLowerRight, start, end) != null) {
						return true;
					}
					if (Polygon.lineIntersect2d(ourLowerRight, ourLowerLeft, start, end) != null) {
						return true;
					}
					if (Polygon.lineIntersect2d(ourLowerLeft, ourUpperLeft, start, end) != null) {
						return true;
					}

					return false;
				}

				break;
			case none:
				return false;// we don't collide with anything
			case point:
				// test point against testAgainst line
				SOLog.severe("testing for line against pivot point (NOT IMPLEMENTED YET)");
				break;
			default:
				SOLog.info("BOUNDARY MODE NOT RECOGNISED:" + this.getObjectsCurrentState().boundaryType.toString());
				break;

			}

		}

		return false;
	}

	/**
	 * detects if a objects touches this based on this ones collision mode
	 * "touches" means any part of the object overlaps with any other -
	 * including if its completely enclosed
	 * 
	 * @param testAgainst
	 * @return
	 */
	public boolean isObjectTouching(SceneObject testAgainst) {
		// SOLog.info("Testing "+this.getName()+" collision against
		// object:"+testAgainst.getName());

		CollisionModeSpecs boundarytype = this.getObjectsCurrentState().boundaryType;

		// dismiss straight away if no boundary type
		if (boundarytype.collisionType == CollisionType.none) {
			return false;
		}

		// ObjectsLog("Testing "+getName()+" collision against
		// sobject:"+testAgainst.getName());

		// TODO:precheck they are on the same height
		// height range of this object
		// lowestpoint, highest point
		// height range of testAgainst
		// lowestpoint, highest point

		// if we have a collisionmap ourselves but the requested object doesn't
		// we switch the test around
		// we basically are just running this function again with "testAgainst"
		// and "this" switched
		// so we dont need to write separate code for the reverse
		if (cmap.isPresent() && cmap.get().hasCoporalPart()) { // now now check
			// to ensure
			// theres at
			// least 1
			// coporal
			// polygon in
			// the cmap

			if (!testAgainst.cmap.isPresent() || !testAgainst.cmap.get().hasCoporalPart()) {

				return testAgainst.isObjectTouching(this);
			}
		}

		// if both objects have collision maps we compare them directly against
		// eachother
		if (cmap.isPresent() && cmap.get().hasCoporalPart() && // now now check
				// to ensure
				// theres at
				// least 1
				// corporal
				// polygon in
				// the cmap
				testAgainst.cmap.isPresent() && testAgainst.cmap.get().hasCoporalPart()) {// now
			// now
			// check
			// to
			// ensure
			// theres
			// at
			// least
			// 1
			// coporal
			// polygon
			// in
			// the
			// cmap
			// ObjectsLog(" (using both collision maps to compare) ");

			// NOTE: we might want to examine the possibility that objects only
			// have non-corporal collisions
			// And in that case default to using the boundary type?
			// Or maybe collisonmap should be a boundary mode to start with so
			// dont use it automatically if present?)
			//SOLog.info("(using both collision maps to compare)");
			Polygon collision = cmap.get().isCollidingWith(testAgainst.cmap.get());

			if (collision != null) {
				return true;
			} else {
				return false;
			}

		}

		// else we use our boundary type

		switch (boundarytype.collisionType) {
		case bottomline:

			// test for line against testAgainst
			Simple2DPoint ourLowerLineStart = getLowerLeft();
			Simple2DPoint ourLowerLineEnd = getLowerRight();

			// SOLog.info("(testing line from:"+ourLowerLineStart+" to
			// "+ourLowerLineEnd+")");
			// testAgainst.ObjectsLog("Testing "+this.getName()+" for baseline
			// collision on object:"+testAgainst.getName());
			// testAgainst.ObjectsLog("our size is:
			// "+testAgainst.getObjectHeight()+"
			// ,"+testAgainst.getObjectWidth());

			// ObjectsLog("Testing "+this.getName()+" for baseline collision on
			// object:"+testAgainst.getName());
			// ObjectsLog("our size is: "+this.getObjectHeight()+"
			// ,"+this.getObjectWidth());

			return testAgainst.isLineTouching(ourLowerLineStart, ourLowerLineEnd);
		case box:
			// SOLog.info("box collision type");

			// defaults
			CollisionBox ourbox = getDefaultBoundingBox();

			if (getObjectsCurrentState().boundaryType.customCollisionBox.isPresent()) {
				// this objects default bounding box
				ourbox = getObjectsCurrentState().boundaryType.customCollisionBox.get();
			}

			// we should move this downwards by our fake height

			// not test against requested
			return testAgainst.isBoxTouching(ourbox);

		case none:
			return false; // we don't collide with anything
		case point:

			// we should move this downwards by our fake height

			// test pivot point against testAgainst
			return testAgainst.isPointTouching(this.getObjectsCurrentState().getX(),
					this.getObjectsCurrentState().getY());

		default:

			SOLog.severe("this objects collision type not recognised" + getObjectsCurrentState().boundaryType);

			return false;

		}

	}

	public boolean isBoxTouching(CollisionBox testAgainstThisBox) {
		// SOLog.info("box collision type testing");

		if (cmap.isPresent()) {
			if (cmap.get().isCollidingWith(testAgainstThisBox)) {
				return true;
			} else {
				return false;
			}
		}

		switch (getObjectsCurrentState().boundaryType.collisionType) {

		case bottomline:

			SOLog.info("Box collision type test against line from " + this.getName() + "  ");

			// test for line against testAgainst
			Simple2DPoint ourLowerLineStart = getLowerLeft();
			Simple2DPoint ourLowerLineEnd = getLowerRight();

			return testAgainstThisBox.isLineCrossing(ourLowerLineStart, ourLowerLineEnd);
			// ---

		case box:
			SOLog.severe("box collision type test against box type not yet supported ");
			break;
		case none:
			return false;
		case point:
			SOLog.severe("box collision type test against point type not yet supported ");
			// is our pin point in the box?
			boolean status = testAgainstThisBox.isPointInside(getX(), getY());

			return status;
		default:
			break;

		}

		return false;
	}

	private CollisionBox getDefaultBoundingBox() {

		Simple2DPoint ourUpperLeft = getUpperLeft();
		Simple2DPoint ourLowerRight = getLowerRight();

		CollisionBox box = new CollisionBox(ourUpperLeft, ourLowerRight);

		return box;
	}




	public boolean isWithinRegion(String regionSpecified) {

		SOLog.info("testing " + this.objectsCurrentState.ObjectsName + " is within region:" + regionSpecified);

		return isWithinRegion(regionSpecified,this.getX(),this.getY(),this.getZ(),this);
	}


	/**
	 * Check if the object is within the region specified.<br>
	 * Regions can be either a scene region, to check the current scene<br>
	 * "SniperVision"<br>
	 * or a region specified in an objects vmap, eg<br>
	 * "Wendy<lineofsight> <br>
	 * to check the objects collision map use "Wendy<COLLISIONMAP> <br>
	 **/
	static public boolean isWithinRegion(String regionSpecified, int x, int y, int z, SceneObject sourceObject) {

		SOLog.info("testing "+x+","+y+","+z+" is within region:" + regionSpecified);

		PolygonCollisionMap cmapToTest = null;// =
		// InstructionProcessor.currentScene.scenesCmap.scenesOwnMap;
		String regionname = "";

		if (regionSpecified.contains("<")) {

			// get the attachment point name
			regionname = regionSpecified.split("\\<")[1];

			// remove ending >
			regionname = regionname.substring(0, regionname.length() - 1).trim();

			SOLog.info("regionname found=" + regionname);

			// Separate the object name
			String objectName = regionSpecified.split("\\<")[0];
			SOLog.info("objectname found=" + objectName);

			// get the cmaps we are checking
			Set<? extends SceneObject> testerobjects = SceneObjectDatabase.getSceneObjectNEW(objectName, null, true);

			// ensure a object was found
			if (testerobjects == null || testerobjects.isEmpty()) {
				SOLog.info("no object found to get region from:" + objectName + ", so assuming not in region");
				return false;
			}

			for (SceneObject testerobject : testerobjects) {

				if (!testerobject.cmap.isPresent()){
					SOLog.warning("Object "+testerobject.getName()+" has no cmap, so cant check isWithin");
					if (testerobjects.size()==1){
						SOLog.warning("(no other objects with that name exist)");
						
					}
					continue;
				}
				
				cmapToTest = testerobject.cmap.get();
				//boolean wellIsItInside = cmapToTest.checkWithinRegion(regionname, this.getX(), this.getY());
				boolean wellIsItInside = false;

				if (sourceObject!=null){
					wellIsItInside = sourceObject.checkThisIsWithinRegion(cmapToTest,regionname); //z not yet used
				} else {
					wellIsItInside = SceneObject.checkPointIsWithinRegion(cmapToTest,regionname,x,y,z); //z not yet used

				}

				if (wellIsItInside) {
					return true;
				}

			}

			return false;

		} else {

			if (SceneObjectDatabase.currentScene.scenesCmap.isPresent()) {

				cmapToTest = SceneObjectDatabase.currentScene.scenesCmap.get().scenesOwnMap;

			} else {

				SOLog.info("no collision map present on scene but also no object specified, so we are returning false");
				return false;
			}

			regionname = regionSpecified;
		}

		//boolean wellIsItInside = cmapToTest.checkWithinRegion(regionname, this.getX(), this.getY());
		boolean wellIsItInside = false;

		if (sourceObject!=null){		
			wellIsItInside = sourceObject.checkThisIsWithinRegion(cmapToTest,regionname); //z not yet used
		} else {
			wellIsItInside = SceneObject.checkPointIsWithinRegion(cmapToTest,regionname,x,y,z); //z not yet used


		}

		return wellIsItInside;
	}

	/**
	 * 
	 * @param regionsmap
	 * @param regionname
	 * @param x
	 * @param y
	 * @param z - NOT USED YET
	 * 
	 * @return
	 */
	public static boolean checkPointIsWithinRegion(PolygonCollisionMap regionsmap,String regionname, int x, int y, int z){
		//Note; z not used
		boolean wellIsItInside = regionsmap.checkWithinRegion(regionname, x,y);
		return wellIsItInside;
	}

	/**
	 * checks if this object is within the specified region
	 * 
	 * @param regionsmap
	 * @param regionname
	 * @return
	 */
	public boolean checkThisIsWithinRegion(PolygonCollisionMap regionsmap,String regionname){
		if (regionsmap==null){
			return false;
		}
		SceneObject testerobject = regionsmap.defaultAssociatedObject;

		if (testerobject!=null){
			if (!testerobject.isVisible()) {
				SOLog.info("Object not visible, so not within.");
				//continue;
				return false;
			}

			// ensure the object is even on the same scene
			if (testerobject.getParentScene() != this.getParentScene()) {
				SOLog.info("was testing for collision map on object, but objects arnt even on the same scene("
						+ getParentScene().SceneFileName + "," + testerobject.getParentScene().SceneFileName
						+ ") thus cant be intersecting.");
				//continue;
				return false;
			}
			if (!testerobject.cmap.isPresent()) {
				SOLog.info("specified object has no cmap");
				//continue;
				return false;
			}
		}



		boolean wellIsItInside = checkPointIsWithinRegion(regionsmap,regionname, this.getX(), this.getY(),this.getZ()); //z not yet used

		return wellIsItInside;


	}

	/**
	 * Stores an array of all objects that need to be checked when this object
	 * moves though the scene. Effectively this should be all the scenes objects
	 * EXCEPT this one. This is used to ensure this object stays visualy infront
	 * of all objects it should be in front of. (currently only applicable to 2d
	 * applications)
	 */
	HashSet<SceneObject>	collisionCheckPool	= null;

	public KeepHeldMode		keepheld			= KeepHeldMode.never;

	/**
	 * This is what actually updates the Z-index while the object moves through
	 * the scene. (It is not nesscerily base on Y though, it depends)
	 * 
	 * @param Y
	 */
	public void setZIndexByPosition(int Y) {

		if (getObjectsCurrentState().linkedZindex) {
			return;// for now we dont change the zindex by position on linked
			// zindex objects - their parents should be the only thing
			// controlling their zindex
		}

		// detect if at or over a threshold
		if (objectsCurrentState.variableZindex) {

			if (numberOfDivisions == -1) {
				setUpVariableZindex();
			}
			// temp, this shouldn't be worked out each time
			// int numberOfDivisions = (int) Math.round((objectsData.upperZindex
			// - objectsData.lowerZindex) /objectsData.stepZindex);
			// ObjectsLog.log("numberOfDivisions" + numberOfDivisions);
			// int pixelstep = (int)
			// Math.round(this.objectScene.scenesData.InternalSizeY/numberOfDivisions);
			// ObjectsLog.log("pixel step=" + pixelstep);

			ObjectsLog("Testing Y, its currently..." + Y);
			// ObjectsLog.log("next threshold is currently..." +nextThreshold);

			if (Y > nextThreshold || Y < prevThreshhold) {

				// percentage down
				// int pd = (int) ((objectScene.getOffsetHeight()/100.0)*Y);

				// count down from now
				double currentfloor = Math.floor(Y / pixelstep); // objectsData.stepZindex);

				prevThreshhold = (int) currentfloor * pixelstep; // objectsData.stepZindex;
				nextThreshold = (int) (currentfloor + 1) * pixelstep; // objectsData.stepZindex;

				ObjectsLog("currentfloor===" + currentfloor);
				ObjectsLog("nextThreshold===" + nextThreshold);
				// new zindex scaled
				// int zi = pd*objectsData.step+objectsData.lower;
				objectsCurrentState.zindex = (int) ((currentfloor * objectsCurrentState.stepZindex)
						+ objectsCurrentState.lowerZindex);

				ObjectsLog("zindex set too..." + objectsCurrentState.zindex);

				// setzindex
				// thisobject.getElement().getParentElement().getStyle().setZIndex(objectsCurrentState.zindex);
				// this.getElementOnPanel().getStyle().setZIndex(objectsCurrentState.zindex);
				// //old

				this.setZIndexImplementation(objectsCurrentState.zindex); // new

				// set next threshold

			}

		}

		// if no scene for this object (ie, its floating in a div?) we just stop
		// now
		if (objectScene == null) {
			return;
		}

		// Now we ensure our zindex is correct for our current position:

		// new method of zindex "fixing"
		// This one uses the cmap to work out what we should be infront/behind
		// of
		// the existing objects in the scene need to be correct zindex wise
		// though

		// we only try to change the zindex if the object has not got a parent
		// (which should supply one)
		// and its set to be variable. Fixed zindex objects should naturally not
		// change
		if (getObjectsCurrentState().positionedRelativeToo == null && getObjectsCurrentState().variableZindex) {

			objectScene.updateZIndexBasedOnPerspective(this); // we currently
			// only run this
			// if we are not
			// positioned
			// relative to
			// somethis else
			// this is because we are likely overlapping with the parent object
			// to start with, hich will mess up collision detection
			// and thus deduction of where in the draw order to put ourselves.
			// However, as we are positioned relative anyway, its likely we also
			// have a relative zindex to the parent and thus this update isnt
			// needed to start with.
		}
		// --------------

		// old method;
		/*
		 * boolean disable = true; //temp test to see how well variable y works
		 * with the new method if (!disable &&
		 * objectScene.scenesCmap.isPresent() &&
		 * !(getObjectsCurrentState().boundaryType.collisionType ==
		 * CollisionType.none) ) {
		 * 
		 * 
		 * 
		 * long TimeTakenStart = System.currentTimeMillis(); //purely for
		 * profiling the time taken //
		 * 
		 * //If there is no collision check pool set we create one. //This is a
		 * array of all objects but ourselves if (collisionCheckPool==null){
		 * collisionCheckPool = new
		 * HashSet<SceneObject>(objectScene.getScenesData().
		 * allScenesCurrentObjects());
		 * collisionCheckPool.removeAll(relativeObjects); //we should not check
		 * against stuff positioned relative to ourselves, as they should be
		 * moving with us anyway collisionCheckPool.remove(this); } //--
		 * 
		 * // in future we should support the object having a custom bounding
		 * box Polygon polygon = objectScene.scenesCmap.get().isBoxColliding (
		 * objectsCurrentState.X+10, objectsCurrentState.Y,
		 * objectsCurrentState.X + this.getObjectWidth()-10,
		 * objectsCurrentState.Y + this.getObjectHeight()-30, true,
		 * collisionCheckPool);
		 * 
		 * 
		 * 
		 * long TimeTakenEnd = System.currentTimeMillis(); //purely for
		 * profiling the time taken
		 * 
		 * GameStatistics.TotalCollisionCheckTime =
		 * GameStatistics.TotalCollisionCheckTime+(TimeTakenEnd-TimeTakenStart);
		 * //purely for profiling the time taken
		 * 
		 * if (polygon != null) {
		 * 
		 * PolygonCollisionMap box = polygon.sourceMap;
		 * 
		 * // SOLog.info("is over a box.");
		 * 
		 * if (box.associatedObject != null) { // SOLog.info(
		 * box.AssociatedObject.objectsData.ObjectsName); // move over it if its
		 * not already if (this.getZindex() < box.associatedObject.getZindex())
		 * {
		 * 
		 * int newzindex = box.associatedObject.getZindex() + 10;
		 * ObjectsLog("Box is over "+box.associatedObject.objectsCurrentState.
		 * ObjectsName+" setting zindex to:"+newzindex);
		 * 
		 * setZIndexImplementation(newzindex);
		 * updateRelativelyZIndexedObjects(newzindex);
		 * 
		 * }
		 * 
		 * } else { // SOLog.info("no associated object"); }
		 * 
		 * }
		 * 
		 * }
		 */
	}

	/**
	 * Script example: NamedActionSet=RunThis:
	 * 
	 * In this case, 'RunThis' would be the 'name'
	 * We first look for a a set called that on this object.If none is found we look for actionset with that name specified on the scene
	 * 
	 * @param name
	 */
	public void runNamedActionSet(String name) {

		SOLog.info("Running action set named " + name + " on " + objectsCurrentState.ObjectsName);

		if (objectsActions != null) {
			CommandList actions = objectsActions.getActionsForTrigger(TriggerType.NamedActionSet, name);

			if (actions == null || actions.size() == 0) {
				// if no object specific set is found we look for one on its
				// scene
				actions = this.getParentScene().sceneActions.getActionsForTrigger(TriggerType.NamedActionSet, name);
			}

			if (actions == null || actions.size() == 0) {
				ObjectsLog("Attempted To Run Action Set:" + name
						+ " but no actions called that found on this object, or on its scene", "red");
			} else {
				InstructionProcessor.processInstructions(actions, "nas_" + name, this);
			}

			//should we also then check global?

		} else {
			
			CommandList actions = this.getParentScene().sceneActions.getActionsForTrigger(TriggerType.NamedActionSet, name);
			
			if (actions == null || actions.size() == 0) {
				ObjectsLog("Attempted To Run Action Set:" + name + " but this object has no actions, and there is no scene action with that name!", "red");
			} else {
				InstructionProcessor.processInstructions(actions, "nas_" + name, this);				
			}
			

		}

	}

	/**
	 * Convince method that calls the SceneObjectFactory.returnclone function
	 * with this object, giving you a handy clone in return
	 * 
	 * @param name
	 * @return
	 */
	public SceneObject returnclone(String name) {
		return SceneObjectFactory.returnclone(this, name);

	}

	/**
	 * Convenience method to get the name of this object
	 * 
	 * @return
	 */
	public String getName() {
		return objectsCurrentState.ObjectsName;
	}

	/**
	 * Chucks this object into the nventory, swapping it for the INV_ version
	 * (basically we run pocketobject at the end of the path)
	 * 
	 * @param toThisInventory
	 */
	public void chuckIntoInventory(String toThisInventory) {

		if (InstructionProcessor.CurrentlyChucked.contains(this)) {			
			SOLog.severe(this.getName()+" is already being chucked about.");
			return;
		}
		
		
		InstructionProcessor.CurrentlyChucked.add(this);
		
		// make the current object absolute to the scene, if its relative.
		makeAbsolute();

		// get inventory location for end of path
		InventoryPanelCore inventory = JAMcore.allInventorys.get(toThisInventory);

		// we work in relative as its easier
		int destXabs = inventory.getInventorysButton().getAbsoluteLeft();
		int destYabs = inventory.getInventorysButton().getAbsoluteTop();

		int startXabs = this.getAbsoluteLeft(); //
		int startYabs = this.getAbsoluteTop();

		SOLog.info("_from x abs _" + startXabs);
		SOLog.info("_from y abs _" + startYabs);

		SOLog.info("_to inventory x abs_" + destXabs);
		SOLog.info("_to inventory y abs _" + destYabs);

		int destX = destXabs - startXabs;
		int destY = destYabs - startYabs;

		SOLog.info("_which is _" + destX);
		SOLog.info("_which is _" + destY);

		MovementPath trajectory = new MovementPath("", "trajectory");

		// just a line for now
		trajectory.add(new MovementWaypoint(0, 0, MovementType.RelativeMove));

		// trajectory.add(new MovementWaypoint(destX/2, (destY/2)-150,
		// MovementType.LineTo,true));

		trajectory.add(new MovementWaypoint(destX, destY, destX / 2, (destY / 2) - 150, true));

		// put in inventory at end of path
		// (NOTE: running pocket would cancel the animation before postanimation
		// commands is run, therefor we run this in the post animation commands
		// instead)
		// MovementWaypoint command = new MovementWaypoint("- pocketobject =
		// "+getObjectsCurrentState().ObjectsName);
		// trajectory.add(command);

		setVisible(true);

		// remember old state to re-apply after animation
		final boolean oldVariableZindex = getObjectsCurrentState().variableZindex;
		final int oldZindex = getObjectsCurrentState().zindex;

		// set it to the front
		getObjectsCurrentState().variableZindex = false;
		setZIndex(10500);

		SOLog.info("setting post animation commands");
		trajectory.setPostAnimationCommands(new Runnable() {
			@Override
			public void run() {

				// put it into the inventory
				CommandList pocketObject = new CommandList("- pocketobject = " + getObjectsCurrentState().ObjectsName);
				InstructionProcessor.processInstructions(pocketObject,
						"EndOfChuckToInventory_for" + SceneObject.this.getName(), SceneObject.this);

				SOLog.info(" resetting zindex data to before chucktoinventory varible: " + oldVariableZindex);

				getObjectsCurrentState().variableZindex = oldVariableZindex;
				getObjectsCurrentState().zindex = oldZindex;
				setZIndex(getObjectsCurrentState().zindex);

				// removing from scene
				// clear its scene variable
				// setObjectsSceneVariable(null); //causes crash :-/

				// remove the object from the currentlychucked list as it's done being chucked.								
				InstructionProcessor.CurrentlyChucked.remove(SceneObject.this);
				SOLog.info(SceneObject.this.getName()+" is no longer being chucked about.");
			}

		});

		// --
		SOLog.info(" playing chuck to inventory ");
		playMovementAtFixedSpeed(trajectory, 100);

	}

	/**
	 * 
	 * @param soundFileName
	 * @param Vol - 0 to 100
	 */
	public void setSound(String soundFileName, int Vol) {

		SOLog.info("setting "+this.getName()+" to play sound "+soundFileName+" at volume:"+Vol);
		
		
		ObjectsSound = SoundFactory.returnNewSound(soundFileName);
		ObjectsSound.setVolume(Vol);
		
		/*
		 * String stype = JAM.defaultSoundType;
		 * 
		 * // note: we should override for all extensions in case the default //
		 * type doesn't match extension if
		 * (soundFileName.toLowerCase().endsWith(".mp3")) { stype =
		 * Sound.MIME_TYPE_AUDIO_MPEG_MP3; } if
		 * (soundFileName.toLowerCase().endsWith(".aac")) { stype =
		 * Sound.MIME_TYPE_AUDIO_MP4_MP4A_40_2; } if
		 * (soundFileName.toLowerCase().endsWith(".ogg")) { stype =
		 * Sound.MIME_TYPE_AUDIO_OGG_VORBIS; }
		 * 
		 * setSound(AudioController.soundController.createSound(stype,
		 * soundFileName));
		 */
	}
	
	/*
	 * public void setSound(Sound sound){
	 * 
	 * ObjectsLog.info("setting objects sound"); ObjectsSound = sound;
	 * 
	 * }
	 */

	public void playSoundLoop() {

		// ObjectsLog.info("playing sound as loop");
		ObjectsSound.setLooping(true); // sounds remember internally if they
		// loop or not. This lets them resume
		// correctly if stopped
		ObjectsSound.play();
	}

	public void clearObjectsSounds() {
		stopObjectsSounds();
		ObjectsSound = null;
		return;
	}

	public void stopObjectsSounds() {
		if (ObjectsSound != null) {
			ObjectsSound.stop();
		}
		return;
	}

	public void resumeObjectsSounds() {
		if (ObjectsSound != null) {
			ObjectsSound.play();
		}
		return;
	}

	public void triggerActionsToRunWhenCloned() {

		// run actions
		if (actionsToRunWhenCloned != null) {

			SceneObjectDatabase.wasLastObjectUpdated(this);

			CommandList actions = actionsToRunWhenCloned.CommandsInSet.getActions();
			
			SOLog.info("running; \n"+actions.getCode());
			
			
			
			InstructionProcessor.processInstructions(actions,
					"fl_" + this.getObjectsCurrentState().ObjectsName, this);

			SOLog.info("__________onClone actions ran");
		}
	}

	/**
	 * returns objectsCurrentState.currentlyVisible, which should be in sycn
	 * with any setvisible implementation
	 * 
	 * @return
	 */
	public boolean isVisible() {
		return objectsCurrentState.currentlyVisible;
	}

	@Override
	public void userActionTriggeredOnObject(String actionname) {

		SOLog.info("action triggered:" + actionname + " on " + this.getName());


		// check for global and scene actions;
		testAndRunSceneAndGlobalActions(TriggerType.UserActionUsed, actionname);

		// check for object specific actions
		if (objectsActions != null) {

			CommandList actions = objectsActions.getActionsForTrigger(TriggerType.UserActionUsed, actionname);

			SOLog.info("..actions found: \n" + actions.toString());

			if (actions.size() > 0) {

				SOLog.info("actions found:" + actions.size());

				InstructionProcessor.processInstructions(actions,
						"FROM_" + this.getParentScene().SceneFileName + "_" + 
								this.getObjectsCurrentState().ObjectsName,
								this);

				// should fire justUsedSpecificActionsFound() here?
				if (InventoryPanelCore.currentlyHeldItem != null) {
					InventoryPanelCore.currentlyHeldItem.justUsedSpecificActionsFound();
				}

				// run its default actions
			} else {
				// (there is actions, but not of the name asked for)

				// NB: some redundancy here with below code wise
				// if tidying up be careful it triggers under the same
				// conditions
				if (InventoryPanelCore.currentlyHeldItem != null) {

					SOLog.info(" running default held actions " + actionname + " on held object"); // changed
					// to
					// held
					// actions
					// run the items default actions, if theres any
					CommandList defaultActions = ((IsInventoryItem) InventoryPanelCore.currentlyHeldItem)
							.getObjectsActions().getActionsForTrigger(TriggerType.DefaultActionFor, actionname);

					if (defaultActions!=null  && !defaultActions.isEmpty()){
					
					InstructionProcessor.processInstructions(defaultActions, 
							"FROM_" + this.getParentScene().SceneFileName + "_"
									+ InventoryPanelCore.currentlyHeldItem.getName(), this);
					
					}
					
					// SOLog.info(" unholding due to no actions found,or purely
					// default actions found "); //we used to unhold for all
					// default actions

					// in future we might want to check "keep held mode" here in
					// the item
					// as use failed, we unhold even if "onuse" is set. However,
					// there might
					// be settings in future to keep it held even if no use is
					// found for it!

					// Purhapes it would be neater to let the InventoryItems
					// deal with their own unholding functions by just telling
					// them what happened then letting them check themselves if
					// they want to unhold the item.
					// ie add thesefunctions to InventoryItem;
					// justUsedSpecificActionsFound()
					// justUsedButOnlyDefaultActionsFound()
					// justUsedButNoActionsFound()
					// Then internally in those actions check the keep held mode

					// unhold if no actions were found (previous we just unheld
					// anyway)
					if (defaultActions.size() == 0) {
						InventoryPanelCore.currentlyHeldItem.justUsedButNoActionsFound();
					} else {
						InventoryPanelCore.currentlyHeldItem.justUsedButOnlyDefaultActionsFound();
					}

				}
			}
		}

		// if actions are found run them:
		else if (InventoryPanelCore.isItemCurrentlyBeingHeld) {

			SOLog.info("itemCurrentlyBeingHeld - looking for default action " + actionname + " \n");

			if (((IsInventoryItem) InventoryPanelCore.currentlyHeldItem).getObjectsActions() != null) {

				// run its default actions
				CommandList defaultActions = ((IsInventoryItem) InventoryPanelCore.currentlyHeldItem)
						.getObjectsActions().getActionsForTrigger(TriggerType.DefaultActionFor, actionname);


				InstructionProcessor.processInstructions(defaultActions, "FROM_" + this.getParentScene().SceneFileName + "_"
						+ InventoryPanelCore.currentlyHeldItem.getName(), this);


				// unhold if no actions were found and no menu is showing
				if (defaultActions.size() == 0) {
					InventoryPanelCore.currentlyHeldItem.justUsedButNoActionsFound();
				} else {
					InventoryPanelCore.currentlyHeldItem.justUsedButOnlyDefaultActionsFound();
				}

			}
		}

		// This bit might be redundant?
		if (InventoryPanelCore.currentlyHeldItem != null) {
			if (!SceneMenuWithPopUp.menuShowing
					&& (InventoryPanelCore.currentlyHeldItem.getKeepHeldMode() != KeepHeldMode.onuse)) {
				SOLog.info("_________unholdItem after user action triggered ");
				InventoryPanelCore.unholdItem();
			} else {
				SOLog.info("_________menu  still showing or on keepheld onuse mode , so we keep holding");
			}
		}
	}

	/**
	 * This should be called from the method in a subclass ie. After all the
	 * objectCurrentState objects are set up, and only if that class is the most
	 * precise definition of the object (and not itself a superclass of another
	 * subtype) eg. Call this from "Dialogue Object" if making a dialog object,
	 * not its label, div or visual supertypes
	 * 
	 * This function has to be called before the object can be used, however, so
	 * it still should be done in the subtypes constructor rather then anywhere
	 * else.
	 */
	public void initialiseAndAddToDatabase() {

		//First thing we do is tell our preconstruction log to dump its data to the real objectlog, as that has to now be constructed.
		preConstructionObjectLog_CopyToRealLog();
		//-------------------


		// this has to be done here, not in the constructor;
		// http://www.javapractices.com/topic/TopicAction.do?Id=252
		// also;inventory icons should not load collision  maps  or movements - thats just
		// silly
		if (this.getObjectsCurrentState().getPrimaryObjectType() == SceneObjectType.InventoryObject) {

			CollisionMapLoadedOrNotNeeded = true;
			MovementFilesLoadedOrNotNeeded = true;			
			ObjectsLog("(CollisionMapLoadedOrNotNeeded)");
			ObjectsLog("(MovementFilesLoadedOrNotNeeded)");

			testIfWeAreLogicallyLoaded();

		} else {

			//load movements here too? or is it better to do it earlier?
			//at the moment its done per object, thats really silly
			startLoadingMovementAnimations();

			// everything else should though
			startLoadingCmap(getObjectsCurrentState());

		}



		// --
		if (JamSaveGameManager.pendingStatesToLoad.containsKey(getName())) {

			SOLog.info(this.getName() + "  had a state pending to load, checking types");
			SceneObjectState stateToLoad = JamSaveGameManager.pendingStatesToLoad.get(this.getName());
			// first a type check to make sure of compatibility
			if (stateToLoad.getPrimaryObjectType() != this.getObjectsCurrentState().getPrimaryObjectType()) {
				SOLog.severe("type wasnt compatible, cant load state into object");

			} else {

				ObjectsLog("Pending state found, loading state into this object", "blue");
				SOLog.info("loading pending state into object " + this.getName());
				updateState(stateToLoad);
				// clear state from pending list
				JamSaveGameManager.pendingStatesToLoad.remove(this.getName());
			}
		}
		// --
		SceneObjectDatabase.addObjectToDatabase(this);

	}

	/**
	 * auto-update the touching data based on the collision maps of nearby
	 * objects
	 */
	public void updateTouchingAutomatically() {

		// no touching if the mode is none
		// or the object isnt attached to a scene
		if (getObjectsCurrentState().boundaryType.collisionType == CollisionType.none
				|| this.getParentScene() == null) {
			getObjectsCurrentState().touching.clear();
			return;
		}

		// clear existing
		// getObjectsCurrentState().touching.clear();

		// now we have to get all touching objects by testing against all
		// visible objects in the scene
		Set<SceneObject> objects = this.getParentScene().getScenesData().allScenesCurrentObjects();

		this.ObjectsLog("Updating collisions: total objects to check for collisions=" + objects.size(), "blue");

		ArrayList<SceneObject> new_touching = new ArrayList<SceneObject>();

		for (SceneObject testagainst : objects) {

			// skip if invisible or we are testing against ourselves
			if (!testagainst.isVisible() || testagainst == this) {
				continue;
			}

			// SOLog.info("Testing if "+this.getName()+" is touching
			// "+testagainst.getName());
			if (this.isObjectTouching(testagainst)) {

				//	SOLog.info("Was touching!");
				new_touching.add(testagainst);
				// getObjectsCurrentState().touching.add(testagainst.getName());

				//	this.ObjectsLog(" touching:" + testagainst.getName(), "green");

			} else {
				//this.ObjectsLog(" not touching:" + testagainst.getName(), "grey");

				if (testagainst.cmapStillLoading) {
					//	this.ObjectsLog(" (cmap still loading) ", "red");
				}
				// if (!testagainst.cmap.isPresent()){
				// this.ObjectsLog(" (no cmap) ");
				// }

			}
		}

		this.ObjectsLog(" new_touching #=" + new_touching.size());

		this.replaceTouchingPropertys(new_touching, this); // when updating
		// automatically the
		// calling object is
		// ourselves
		// update inspector if there's one
		this.updateDebugInfo();

	}

	public String getCollisionPath() {
		String objectsPath = "";

		// we also add the pinpoint of the object so we prepare this as a little
		// white square
		String pinpoint = getPinAndAttachmentsAsSVG();

		// SOLog.warning("getting collision line for "+this.getName());

		if (cmap.isPresent()) {
			// get each objects path and add it to the overall AllPath
			// which contains both the scenes collision map and all its objects
			// maps
			// giving us all the things that should be avoided

			SimpleVector3 scaleNorm = new SimpleVector3(1.0,1.0,1.0);
			if (getParentScene()!=null){						
				scaleNorm=getParentScene().getNormalScaleingForReflections();					
			}				




			objectsPath = objectsPath + cmap.get().getPath(scaleNorm);
			return objectsPath + pinpoint;
		}

		// else path is determined by collision type
		switch (this.getObjectsCurrentState().boundaryType.collisionType) {
		case bottomline:

			Simple2DPoint ourLowerLineStart = getLowerLeft();
			Simple2DPoint ourLowerLineEnd = getLowerRight();

			objectsPath = "M " + ourLowerLineStart + " L " + ourLowerLineEnd;
			break;
		case box:
			SOLog.warning("preview for box boundary type not implemented yet");			
			break;
		case none:
			return pinpoint;
		case point:
			objectsPath = "M " + this.getX() + "," + this.getY() + "";
			break;
		default:
			break;
		}

		String svgCode = "<path id=\"lineBC\" d=\"" + objectsPath + "\" stroke=\"" + "RED"
				+ "\" stroke-width=\"3\" fill=\"rgba(45,5,25,0.5)\" style=\"opacity:0.5\" />";

		return svgCode + pinpoint;
	}

	/**
	 * To help visually debug a game, especially animations which attachment
	 * points, this function returns a SVG made from rectangles, each one being
	 * at one of the points. Note: we only returns the current frame if its
	 * framed
	 * 
	 * @return
	 */
	protected String getPinAndAttachmentsAsSVG() {

		int pinx = this.getX();
		int piny = this.getY();
		int pinz = this.getZ();

		piny = piny - pinz;
		String pinpoint = "<rect width=\"3\" height=\"3\" style=\"stroke-width:1;stroke:rgb(255,255,255)\" x=\"" + pinx
				+ "\" y=\"" + piny + "\"></rect>";

		// now add the current attachment points if we have any
		if (this.getObjectsCurrentState().hasAttachmentPointFile) {

			if (attachmentPoints == null) {
				SOLog.info("(no attachement points stored for current filename:"
						+ this.getObjectsCurrentState().ObjectsFileName + ")");
				return "";
			}

			for (MovementPath attachmentPoint : attachmentPoints.attachmentPointLists.values()) {

				int currentFrame = 0;

				if (this.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Sprite)) {
					currentFrame = ((SceneSpriteObjectState) this.getObjectsCurrentState()).currentFrame;
				}

				Simple3DPoint loc = attachmentPoint.get(currentFrame).pos;

				int attachx = loc.x + this.getTopLeftBaseX();
				int attachy = loc.y + this.getTopLeftBaseY();
				int attachz = loc.z + this.getTopLeftBaseZ();
				attachy = attachy - attachz;
				String attachpoint = "<rect width=\"3\" height=\"3\" style=\"stroke-width:1;stroke:rgb(5,205,5)\" x=\""
						+ attachx + "\" y=\"" + attachy + "\"></rect>";
				pinpoint = pinpoint + attachpoint;
			}

		}

		return pinpoint;
	}

	/**
	 * Adds a impulse to the objects movement. It will then act on this impulse
	 * forever, assuming there is no acceleration or friction set
	 * 
	 * Note; gravity is applied automatically in the default acceleration, dont factor it into the impulse
	 * 
	 * @param impulseMag
	 *            - pixels per ms in each direction
	 */
	public void addImpulse(SimpleVector3 impulseMag) {
	
		SOLog.info("Adding a impulse to this object :" + impulseMag.toString());
	
		//ensure we have a movementstate
		objectsCurrentState.createMovementStateIfNeeded();
	
		// set movement type
		objectsCurrentState.moveState.get().currentmovementtype = MovementStateType.PhysicsBased;
	
		// set variables
		objectsCurrentState.moveState.get().addVelocity(impulseMag); // add to existing velocity
	
		
		SimpleVector3 calcFriction = getCalculatedFrictionAgainstSceneSurface();		
		//+ nf.format(calcFriction));
	
		//	double movement_friction_mag = 0.000005; //default, should be customisable on object and scene
	//	ObjectsLog("default Friction is: ");
	//	ObjectsLog(movement_friction_mag);	
	
		
		//we only apply friction if on the ground for now. No air friction
		if (this.getZ()==0){
			calcFriction.z=0.0; //no air friction for now. (TODO:when objects and scene can set their own friction, remove this)
			setCurrentMovementFriction(calcFriction);
			//--
			
			
		} else {			 
			
			ObjectsLog(" no friction as we are off the ground ("+getZ()+")");
			
			
		}
	
	
		// update position
		objectsCurrentState.moveState.get().setPosition(
				getObjectsCurrentState().getX(),
				getObjectsCurrentState().getY(),	
				getObjectsCurrentState().getZ());
	
		// start movements
		startCurrentMovement();
	
		ObjectsLog("Added impulse of " + impulseMag + " now:" + objectsCurrentState.moveState.get().movement_vel.toString());
	
	}

	/**
	 * calculates this object rubbing against the scene its in, under default gravity (-MovementState.GravityMS)
	 * It also applies the physics bias of the scene
	 * @return
	 */
	private SimpleVector3 getCalculatedFrictionAgainstSceneSurface() {
		SimpleVector3 objectFriction = this.getObjectsCurrentState().objectsFrictionalResistance;
		SimpleVector3 sceneFriction = this.getParentScene().getDefaultSceneFriction();
		
		//the calculated friction is the two surface frictions multiplied then times the force pushing them together
		SimpleVector3 calcFriction = objectFriction.copy();
		calcFriction.mul(sceneFriction);
		calcFriction.mul(-MovementState.GravityMS);
			
		
		//the friction we use is the magnitude above times any scene bias
		 calcFriction.mul(getParentScene().getPhysicsBias()); //is mul correct? or does the bias go the other way?
	
		//double calcFriction = (objectFriction*sceneFriction)*-MovementState.GravityMS;
		
		//gwt debug only; (gdx cant use NumberFormat)
		//NumberFormat nf = NumberFormat.getDecimalFormat();		
		// nf.overrideFractionDigits(15);	    
		ObjectsLog("Calculated Friction is: ");
		ObjectsLog(calcFriction.toString());
		return calcFriction;
	}
	
	
/**
 * addimpulse auto sets this based on object & scene friction and gravity,
 * but you can manually override it to experiment
 * @param movement_friction
 */
	public void setCurrentMovementFriction(SimpleVector3 movement_friction) {
	//	ObjectsLog("Movement friction Friction is set to: ");
	//	ObjectsLog(movement_friction.toString());	
		
		objectsCurrentState.moveState.get().setMovement_friction(movement_friction);
	}

	protected void ObjectsLog(double calcFriction) {
		SOLog.info(""+calcFriction);

	}

	/**
	 * 
	 * @param predicateToLookFor
	 * @return
	 */
	public SSSNode getValueOfProperty(String predicateToLookFor) {

		SOLog.info("Getting value of predicate:" + predicateToLookFor + " on object:" + this.getName());

		SSSNode value = this.getObjectsCurrentState().objectsProperties.getValueForPredicate(predicateToLookFor);

		return value;
	}

	abstract public void updateDebugGlobalVariableInfo();

	// TODO: move as much type specific updates here too, use the method style
	// below to do it
	// (also add a check we are of the correct type?)
	/*
	 * protected void updateState(SceneSpriteObjectState newState) {
	 * updateState(newState); //now update SceneSpriteObject specific stuff by
	 * casting ourselves to its interface IsSceneSpriteObject thisAsSprite =
	 * (IsSceneSpriteObject) this; thisAsSprite. }
	 */

	/**
	 * <br>
	 * Update the object to match the supplied state<br>
	 * This should be run before the objects specific loading in the subclass of
	 * SceneObject.<br>
	 * ie. SceneDivObject.updateState should trigger this before its own
	 * functions<br>
	 * <br>
	 * 
	 * internally runs updateState(true,true) for specific subtype
	 * 
	 * @param newState
	 **/
	public void updateState(SceneObjectState sceneObjectData) {

		switch (this.getObjectsCurrentState().getPrimaryObjectType()) {

		case Sprite:
			// check data is matching type
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.Sprite) {

				// ((SceneSpriteObject) object).updateState(
				// (SceneSpriteObjectState) sceneObjectData, true,true);

				((IsSceneSpriteObject) this).updateState((SceneSpriteObjectState) sceneObjectData, true, true);

				SOLog.info("updated object:" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case Label:
			SOLog.info("updating object label:" + this.getObjectsCurrentState().ObjectsName);
			SOLog.info("type is:" + sceneObjectData.getPrimaryObjectType());
			// check data is matching type
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.Label){
				//	|| sceneObjectData.getPrimaryObjectType() == SceneObjectType.DialogBox) {

				((IsSceneLabelObject) this).updateState((SceneLabelObjectState) sceneObjectData, true, true);

				SOLog.info("updated object label:" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case DialogBox:
			SOLog.info("updating object dialogue:" + this.getObjectsCurrentState().ObjectsName);
			SOLog.info("type is:" + sceneObjectData.getPrimaryObjectType());
			// check data is matching type OR Label (Dialogues are a subtype of
			// label so can take the same data.
			// This should only be necessary from loading saves from before they
			// were a subtype)
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.DialogBox){
			//		|| sceneObjectData.getPrimaryObjectType() == SceneObjectType.Label) {

				((IsSceneDialogueObject) this).updateState((SceneDialogueObjectState) sceneObjectData, true, true);

				SOLog.info("updated object dialogue:" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case Div:
			// check data is matching type
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.Div) {

				// would this use the correct constructor without the cast to
				// SceneDivObject?
				// will we need to put "updateState" in the interfaces and use
				// them instead?
				((IsSceneDivObject) this).updateState((SceneDivObjectState) sceneObjectData, true, true);

				SOLog.info("updated div dialogue:" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case Vector:
			// check data is matching type
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.Vector) {

				((IsSceneVectorObject) this).updateState((SceneVectorObjectState) sceneObjectData, true, true);

				SOLog.info("updated Vector :" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case Input:
			// check data is matching type
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.Input) {

				((IsSceneInputObject) this).updateState((SceneInputObjectState) sceneObjectData, true, true);

				SOLog.info("updated Input :" + this.getObjectsCurrentState().ObjectsName);

			}
			break;
		case InventoryObject:
			if (sceneObjectData.getPrimaryObjectType() == SceneObjectType.InventoryObject) {

				((IsInventoryItem) this).updateState((InventoryObjectState) sceneObjectData, true, true);

				SOLog.info("updated inventory icon :" + this.getObjectsCurrentState().ObjectsName);

			}

			break;
		default:
			break;
		}

	}

	/**
	 * <br>
	 * Update the object to match the supplied state<br>
	 * This should be run before the objects specific loading in the subclass of
	 * SceneObject.<br>
	 * ie. SceneDivObject.updateState should trigger this before its own
	 * functions<br>
	 * <br>
	 * 
	 * @param newState
	 *            - the new state this object will be set too
	 * @param runOnload
	 *            - set to false. Subclasses should run the onLoad after their
	 *            data is processed.
	 * @param repositionObjectsRelativeToThis
	 *            - set to false. Subclasses should reposition objects after
	 *            their data is processed
	 */
	// TODO: eventually we want to support part-state updating. That is, only
	// updating whats changed.
	// This will allow more quickly changing states, as we only need to store or
	// update whats changed.
	// To do this though we need to ensure every single variable in the state
	// can tell the difference between it being present or not.
	public void updateGeneralObjectState(SceneObjectState newState) {

		stopNextMovementFrame();

		//clear existing movements if we have them
		if (objectsCurrentState.moveState.isPresent()){
			objectsCurrentState.moveState.get().clearMovements();
		}


		// cancel any current fades and associated runactions
		cancelCurrentFade(true);
		// ---------------------

		//
		ObjectsLog("Updating general SceneObject State To: \n" + newState.serialiseToString(), "orange");
		SOLog.warning("Updating SceneObject State To: \n" + newState.serialiseToString());

		// should we clear polygon caches here too?
		// technically that should only be needed if we move or set visibility
		/*
		 * if (cmap.isPresent()){ cmap.get().clearTouchingCache();
		 * 
		 * //also remove ourselves from every other cache
		 * Polygon.removeFromAllcaches(cmap.get());
		 * 
		 * 
		 * /* faster maybe; for (String touching :
		 * getObjectsCurrentState().touching) { SceneObject curobject =
		 * SceneObjectDatabase.getSingleSceneObjectNEW(touching, null, true); if
		 * (curobject.cmap.isPresent()) {
		 * curobject.cmap.get().removeFromCache(cmap.get()); } } }
		 */
		// isMoving = false;

		String objectsSceneName = newState.ObjectsSceneName;
		boolean sceneLessLoadMode = false;

		if (!objectsSceneName.equals(SceneObjectState.OBJECT_HAS_NO_SCENE_STRING)) {

			SceneWidget sw = SceneWidget.getSceneByName(objectsSceneName);

			if (sw == null) {
				SOLog.warning("scene name not recognised" + objectsSceneName);
				ObjectsLog("scene name not recognised:" + objectsSceneName);
			}
			setObjectsScene(sw);

		} else {

			ObjectsLog("(object setting to no scene, likely its now in a inventory)");
			setObjectsScene(null); // setting to no-scene mode

			// TODO: what if we have no scene? special state setting mode that
			// doesn't position?
			sceneLessLoadMode = true;
		}

		if (newState.ObjectsName != null) {
			getObjectsCurrentState().ObjectsName = newState.ObjectsName;
		}

		// moved to div update (as title is div specific)
		// if (newState.Title != null) {
		// getObjectsCurrentState().Title = newState.Title;
		// }

		// TODO: we need a better way to handle co-ordinates
		// Ideally switch to a Simple3DPoint and then use some designation for
		// if its change or not
		// Optional<Simple3DPoint> maybe
		if (newState.X != -1) {
			getObjectsCurrentState().X = newState.X;

		}
		if (newState.Y != -1) {
			getObjectsCurrentState().Y = newState.Y;
		}
		if (newState.Z != -1) {
			getObjectsCurrentState().Z = newState.Z;
		}

		//movestate should always match our x/y/z
		if (getObjectsCurrentState().moveState.isPresent()){
			getObjectsCurrentState().moveState.get().movement_current_pos.x = getObjectsCurrentState().X;
			getObjectsCurrentState().moveState.get().movement_current_pos.y = getObjectsCurrentState().Y;
			getObjectsCurrentState().moveState.get().movement_current_pos.z = getObjectsCurrentState().Z;
		}

		// copy pin data
		getObjectsCurrentState().CurrentPinPoint.x = newState.CurrentPinPoint.x; //not needed
		getObjectsCurrentState().CurrentPinPoint.y = newState.CurrentPinPoint.y; //not needed
		getObjectsCurrentState().CurrentPinPoint.z = newState.CurrentPinPoint.z; //not needed

		getObjectsCurrentState().DefaultPinPoint.set(newState.DefaultPinPoint); //new

		// positioned relative too
		if (newState.positionedRelativeToo != null) {
			getObjectsCurrentState().positionedRelativeToo = newState.positionedRelativeToo;
			//ensure parent knows
			getObjectsCurrentState().positionedRelativeToo.addChild(this);

		} else if (newState.positionRelativeToOnceLoaded.length() > 2) {
			getObjectsCurrentState().positionRelativeToOnceLoaded = newState.positionRelativeToOnceLoaded;
		} else {


			if (getObjectsCurrentState().positionedRelativeToo != null) {
				getObjectsCurrentState().positionedRelativeToo.removeChild(this);//relativeObjects.remove(this);
				// removes this relative object from the object that its
				// positioned relatively too. (so, if this was a hat,remove it from being positioned by the head)
			}

			getObjectsCurrentState().positionedRelativeToo = null; // clear any
			// relative positioning if none
			// is specified in this new state.
			getObjectsCurrentState().positionRelativeToOnceLoaded = ""; // Note:
			// this breaks
			// the ability to only update parts
			// of the state while
			// maintaining the rest. Unused
			// atm, but that was needed for a potential
			// rewind functionining
			// :-/
			// really we should store a specific value for meaning "newstate has
			// no relative position" and a seperate one for "newstate specifys
			// no change in relative positioning"
			// This should be solved by Guava Optional<>
		}

		if (newState.positionedRelativeToo != null || newState.positionRelativeToOnceLoaded.length() > 2) {
			// if (newState.relX != 0) {
			getObjectsCurrentState().relX = newState.relX;

			// }

			// if (newState.relY != 0) {
			getObjectsCurrentState().relY = newState.relY;
			// }

			// if (newState.relZ != 0) {
			getObjectsCurrentState().relZ = newState.relZ;
			// }
		}

		if (newState.zindex != -1) {

			getObjectsCurrentState().zindex = newState.zindex;
			ObjectsLog("objectsData.zindex=" + getObjectsCurrentState().zindex);

			// getObjectsCurrentState().zindex =
			// getObjectsCurrentState().zindex; //PRETTY sure all cases of this
			// "zIndex" should be changed to objectsCurrentState.zindex.

		}
		// set up variable zindex
		getObjectsCurrentState().variableZindex = newState.variableZindex;

		if (newState.lowerZindex != -1) {
			getObjectsCurrentState().lowerZindex = newState.lowerZindex;
		}
		if (newState.upperZindex != -1) {
			getObjectsCurrentState().upperZindex = newState.upperZindex;
		}
		if (newState.stepZindex != -1) {
			getObjectsCurrentState().stepZindex = newState.stepZindex;
		}

		// linked
		getObjectsCurrentState().linkedZindex = newState.linkedZindex;
		getObjectsCurrentState().linkedZindexDifference = newState.linkedZindexDifference;


		//we use to selective update what's not -1, but now we just copy the state if present
		//if (newState.moveState.isPresent()){			
		//getObjectsCurrentState().createMovementStateIfNeeded();
		//	getObjectsCurrentState().moveState = Optional.of(newState.moveState.get().copy());		
		//we could also increase efficiency by setting the existing state,if theres one, rather then copying or making a new one
		//} else {
		//set our movements to none if we have them
		//	if (getObjectsCurrentState().moveState.isPresent()){
		//		getObjectsCurrentState().moveState.get().clearMovements();
		//	}
		//}
		//----------------------------------------------------------------------
		//-------------------------------------------
		//---------------------------
		/*
		if (newState.moveState.get().movement_currentWaypoint != -1) {
			getObjectsCurrentState().moveState.get().movement_currentWaypoint = newState.moveState.get().movement_currentWaypoint; // current
			// number
			// of
			// waypoint
		}

		ObjectsLog("setting objects data to newState d");

		//
		// TODO: copy all movementstate stuff in one go?
		// Do we need all the minus1 stuff
		if (newState.moveState.movement_SC.x != -1) {
			getObjectsCurrentState().moveState.movement_SC.x = newState.moveState.movement_SC.x; // destination

		}
		if (newState.moveState.movement_SC.y != -1) {
			getObjectsCurrentState().moveState.movement_SC.y = newState.moveState.movement_SC.y;
		}
		if (newState.moveState.movement_SC.z != -1) {
			getObjectsCurrentState().moveState.movement_SC.z = newState.moveState.movement_SC.z;
		}
		if (newState.moveState.movement_CC.x != -1) {
			getObjectsCurrentState().moveState.movement_CC.x = newState.moveState.movement_CC.x; // destination

		}
		if (newState.moveState.movement_CC.y != -1) {
			getObjectsCurrentState().moveState.movement_CC.y = newState.moveState.movement_CC.y;
		}
		if (newState.moveState.movement_CC.z != -1) {
			getObjectsCurrentState().moveState.movement_CC.z = newState.moveState.movement_CC.z;
		}
		if (newState.moveState.movement_dest.x != -1) {
			getObjectsCurrentState().moveState.movement_dest.x = newState.moveState.movement_dest.x; // destination
			// X
		}
		if (newState.moveState.movement_dest.y != -1) {
			getObjectsCurrentState().moveState.movement_dest.y = newState.moveState.movement_dest.y;
		}
		if (newState.moveState.movement_dest.z != -1) {
			getObjectsCurrentState().moveState.movement_dest.z = newState.moveState.movement_dest.z;
		}

		// --
		if (newState.moveState.movement_vel.x != -1) {
			getObjectsCurrentState().moveState.movement_vel.x = newState.moveState.movement_vel.x;
		}
		if (newState.moveState.movement_vel.y != -1) {
			getObjectsCurrentState().moveState.movement_vel.y = newState.moveState.movement_vel.y;
		}
		if (newState.moveState.movement_vel.z != -1) {
			getObjectsCurrentState().moveState.movement_vel.z = newState.moveState.movement_vel.z;
		}
		// -

		if (newState.moveState.movement_acc.x != -1) {
			getObjectsCurrentState().moveState.movement_acc.x = newState.moveState.movement_acc.x;
		}
		if (newState.moveState.movement_acc.y != -1) {
			getObjectsCurrentState().moveState.movement_acc.y = newState.moveState.movement_acc.y;
		}
		if (newState.moveState.movement_acc.z != -1) {
			getObjectsCurrentState().moveState.movement_acc.z = newState.moveState.movement_acc.z;
		}
		// -
		if (newState.moveState.movement_current_pos.x != -1) {
			getObjectsCurrentState().moveState.movement_current_pos.x = newState.moveState.movement_current_pos.x;
		}

		if (newState.moveState.movement_current_pos.y != -1) {
			getObjectsCurrentState().moveState.movement_current_pos.y = newState.moveState.movement_current_pos.y;
		}
		if (newState.moveState.movement_current_pos.z != -1) {
			getObjectsCurrentState().moveState.movement_current_pos.z = newState.moveState.movement_current_pos.z;
		}

		if (newState.moveState.movement_speed != -1) {
			getObjectsCurrentState().moveState.movement_speed = newState.moveState.movement_speed; // pixels
			// per
			// cycle
		}

		if (newState.moveState.getBounceEnergyRetention() != 1.0) { // 1.0 is
			// the
			// current
			// default,
			// this
			// should
			// change
			getObjectsCurrentState().moveState.bounceEnergyRetention = newState.moveState.getBounceEnergyRetention();
		}

		 */

		ObjectsLog("setting objects movement state");

		// if (movementsLoaded) {

		if ( newState.moveState.isPresent() ){

			ObjectsLog("newState.currentmovement path:" + newState.moveState.get().currentmovementpathname);

			
			ObjectsLog("Setting Movement to:" + newState.moveState.get().toString());

			//match our state to this one specified if we have a state, else creating a new movestate if we never had one.
			if (getObjectsCurrentState().moveState.isPresent()){
				getObjectsCurrentState().moveState.get().setTo(newState.moveState.get());
			} else {
				getObjectsCurrentState().moveState = Optional.of(newState.moveState.get().copy());					
			}
			
			//load path
			if ((newState.moveState.get().currentmovementpathname != "")
					&& (!newState.moveState.get().currentmovementpathname.startsWith("_internal_"))) {

				if (objectsMovements != null) {

					getObjectsCurrentState().moveState.get().currentPathData = objectsMovements.getMovement(newState.moveState.get().currentmovementpathname);

					if (getObjectsCurrentState().moveState.get().currentPathData == null) {
						ObjectsLog("_cant find:" + newState.moveState.get().currentmovementpathname, LogLevel.Error);

					}

				}
			}


		} else {
			//set our movements to none if we have them

			if (getObjectsCurrentState().moveState.isPresent()){
				getObjectsCurrentState().moveState.get().clearMovements();
			}

		}

		/*
		 * getObjectsCurrentState().moveState.currentmovementpath =
		 * newState.moveState.currentmovementpath; // if oncurve
		 * getObjectsCurrentState().moveState.currentmovementtype =
		 * newState.moveState.currentmovementtype;
		 * 
		 * getObjectsCurrentState().moveState.movement_curveTime = 0;
		 * getObjectsCurrentState().moveState.movement_curveTimeStep = 0.01;
		 * 
		 * if (getObjectsCurrentState().moveState.movement_SX != -1) {
		 * getObjectsCurrentState().moveState.currentmovementtype =
		 * currentMovementType.OnCurvePath;
		 * 
		 * }
		 */
		// run animation if we have one (moved to end)
		//ObjectsLog("_____________________________________________________animationRunner is a go!");
		//	if (getObjectsCurrentState().moveState.isPresent()){
		//		if (getObjectsCurrentState().moveState.get().isMoving() && !sceneLessLoadMode) {
		//			startCurrentMovement();
		//		}
		//	}


		ObjectsLog("setting objects data to newState e");

		if ((newState.objectsProperties != null) && (newState.objectsProperties != null)) {

			ObjectsLog("Setting objects Properties. Num of new props = " + newState.objectsProperties.size());

			getObjectsCurrentState().objectsProperties = new PropertySet(newState.objectsProperties.clone());

		}

		if ((newState.ObjectRuntimeVariables != null) && (newState.ObjectRuntimeVariables != null)) {

			ObjectsLog("Setting RuntimeVariables. Num of new vars = " + newState.ObjectRuntimeVariables.size());

			getObjectsCurrentState().ObjectRuntimeVariables = newState.ObjectRuntimeVariables.clone();

		}

		// Things the object is touching (optional)
		if (newState.touching != null) {

			ObjectsLog("setting touching data");

			// Things the object is touching (optional)
			getObjectsCurrentState().touching = new SceneObjectSet(newState.touching);
			// (HashSet<String>) newState.touching
			// .clone();

			ObjectsLog("setting touching data" + getObjectsCurrentState().touching.size());

		} else {

			ObjectsLog("touching data null, so we ignore");

		}

		// only set a position if we have a scene
		// if we are in the inventory we shouldn't try to move as, well, we cant
		if (!sceneLessLoadMode) {

			if ((this.getObjectsCurrentState().attachToHTMLDiv.length() < 1)
					&& (this.getObjectsCurrentState().positionedRelativeToo == null)) {

				ObjectsLog("setting position non-relative");

				setPosition(getObjectsCurrentState().X, getObjectsCurrentState().Y, getObjectsCurrentState().Z, false,
						false, true); // when state loading,the co-ordinates are
				// set to the top right, not by the pin

				setZIndex(getObjectsCurrentState().zindex);

				ObjectsLog("set position too:" + getObjectsCurrentState().X + "," + getObjectsCurrentState().Y + " "
						+ getObjectsCurrentState().Z + "(Z INDEX " + getObjectsCurrentState().zindex + ")");

			} /*
			 * The following code I thought we would need to handle loading
			 * divs into text but it can be handled by normal div
			 * positioning else if
			 * ((this.objectsData.attachToDiv.length()>1)&&(this.objectsData
			 * .positionedRelativeToo!=null)){
			 * 
			 * //if a relative object is specified AND a attachToDiv then we
			 * look for a div within that object. //This is ONLY supported
			 * on TextObjects being inserted into Dialogues at the moment
			 * SOLog.info("attach to div within "+objectsData.
			 * positionedRelativeToo.objectsData.ObjectsName+" detected");
			 * 
			 * if
			 * (objectsData.positionedRelativeToo.objectsData.currentType==
			 * SceneObjectType.DialogBox ||
			 * objectsData.positionedRelativeToo.objectsData.currentType==
			 * SceneObjectType.Label){
			 * 
			 * SOLog.info("attach to div within a label or dialogue ");
			 * 
			 * s
			 * 
			 * 
			 * }
			 * 
			 * 
			 * 
			 * }
			 */else if (this.getObjectsCurrentState().positionedRelativeToo != null) {

				 ObjectsLog("setting position RelativeToo: "
						 + getObjectsCurrentState().positionedRelativeToo.getObjectsCurrentState().ObjectsName);
				 ObjectsLog("which is at: " + getObjectsCurrentState().positionedRelativeToo.getObjectsCurrentState().X
						 + "," + getObjectsCurrentState().positionedRelativeToo.getObjectsCurrentState().Y);

				 getObjectsCurrentState().positionedRelativeToo.addChild(this); //.relativeObjects.add(this);

				 updateRelativePosition(true);

				 // super.setPosition(objectsData.relX ,
				 // objectsData.relY ,false,false); //when state loading,the
				 // co-ordinates are set to the top right, not by the pin

				 setZIndex(getObjectsCurrentState().zindex);

				 ObjectsLog("set position too:" + getObjectsCurrentState().X + "," + getObjectsCurrentState().Y);

			 } else if (this.getObjectsCurrentState().attachToHTMLDiv.length() > 1) {
				 // to optimize we should check its not already in the div?
				 // maybe.
				 SOLog.info("setting position into html div with id: " + this.getObjectsCurrentState().attachToHTMLDiv);
				 ObjectsLog("Setting Position into html with id:" + this.getObjectsCurrentState().attachToHTMLDiv);

				 // see; this needs to be changed to a generic implementation.
				 // addObjectToScene in SceneWidget can handle attachto divs
				 // but doesn't seem to do the element wrapping/check we have
				 // here.
				 //
				 // Element toaddto =
				 // DOM.getElementById(this.getObjectsCurrentState().attachToHTMLDiv);

				 // new method;
				 RequiredImplementations.PositionByTag(this, this.getObjectsCurrentState().attachToHTMLDiv);

				 // old method
				 // yet because we can't get internal widget from parent class
				 /*
				  * if (toaddto!=null) {
				  * 
				  * 
				  * //ObjectsLog.info("div current contents: "+toaddto.
				  * getInnerHTML()); HTMLPanel container =
				  * HTMLPanel.wrap(toaddto);
				  * 
				  * if (container.getWidgetCount()>0){
				  * ObjectsLog.info("elements already in container:"+container.
				  * getWidgetCount()); }
				  * 
				  * container.clear();
				  * container.add(this.getInternalGwtWidget());
				  * setZIndex(getObjectsCurrentState().zindex);
				  * 
				  * ObjectsLog.info("positioned");
				  * 
				  * } else { SOLog.
				  * severe("cant find element, so attaching at arbitary point");
				  * //Note; Hopefully a latter loaded tech box will pick it up
				  * for placement ObjectsLog.
				  * log("cant find html element with that id, so attaching at arbitary point"
				  * ,"red");
				  * 
				  * setPosition(getObjectsCurrentState().X,getObjectsCurrentState
				  * ().Y,getObjectsCurrentState().Z ,false,false,true);
				  * 
				  * 
				  * }
				  */

			 }

		}

		ObjectsLog("currentObjectState.zindex is " + getObjectsCurrentState().zindex);

		ObjectsLog("setting Visible:" + newState.currentlyVisible + " with opacity:" + newState.currentOpacity);

		// set its visibility (note; this will set opacity 100% or 0% on its
		// own)
		SOLog.info("updating visiblity and opacity");
		setVisible(newState.currentlyVisible, false); // false stops it being
		// positioned by pin

		// and opacity (note; this will NOT change set visibility true/false)
		// (which makes sense, even if you want something 0.01 opacity, thats
		// still visible!)
		setObjectOpacity(newState.currentOpacity); // false stops it being
		// positioned by pin
		// --

		ObjectsLog("Objects title setting to:" + getObjectsCurrentState().Title);

		// this.setTitle(getObjectsCurrentState().Title); //should be on div

		// SOLog.info("clearing state");
		// previousStates.clear();

		ObjectsLog("Objects propertysChanged");

		SOLog.info("firing touching properties changed");
		touchingPropertysChanged();


		ObjectsLog("_____________________________________________________animationRunner is a go!");
		if (getObjectsCurrentState().moveState.isPresent()){
			if (getObjectsCurrentState().moveState.get().isMoving() && !sceneLessLoadMode) {
				startCurrentMovement();
			}
		}

	}

	public void triggerPocketObject(String inventory, boolean unlinkFromParent) { // SceneObject
		// so,

		SOLog.info("Trigger Pocket Object");
		// stop motions
		stopNextMovementFrame();

		SceneObject so = this;

		// ensure the item itself is detached
		if (unlinkFromParent) {
			so.detach();
		}

		// correct the name (in case it was a variable)
		String objectNamepo = so.getName();

		// make scene object invisible
		// so.setVisible(false);

		// remove from scene but not lists (as we need to find this object again
		// when its taken out)
		so.setCurrentlyStoredInInventory(true);

		// add it
		InstructionProcessor.addItemToInventory(InventoryPanelCore.PocketedPrefix + objectNamepo, inventory, so);

		if (so.relativeObjects.size() > 0) {
			SOLog.info("pocketing attached objects");
			// add anything attached as well; (which calls this whole function
			// again)
			for (SceneObject attachedObject : so.relativeObjects) {
				InstructionProcessor.Log
				.info("pocketing attached object:" + attachedObject.getObjectsCurrentState().ObjectsName);
				attachedObject.triggerPocketObject(inventory, false); // NOTE:
				// WE
				// DONT
				// DETACH
				// OUR
				// CHILDREN
				// FROM
				// OURSELVES
				// WHEN
				// POCKETING
				// THE
				// OBJECT
			}
		} else {
			SOLog.info("No attached Objects");
		}

	}

	/**
	 * by setting this object as stored in the inventory it will remove it from
	 * the scene without adding it anywhere else it remains in the database By
	 * setting this to false it re-adds it to the current scene.
	 * 
	 * @param bool
	 */
	private void setCurrentlyStoredInInventory(boolean bool) {
		ObjectsLog("Setting as stored in inventory:" + bool);

		if (bool) {

			SOLog.info("--- Removing from scene (physical) ");

			// removeThisObjectFromItsSceneImplementation();
			removeObject(false, false);

			SOLog.info("--- Removing From Scene (setting variable to null)");
			setObjectsSceneVariable(null);

		} else {
			setObjectsScene(SceneObjectDatabase.currentScene);
			// ensure zindex is correct

		}

	}

	
	//TODO: move to sprite helper class
	/**
	 *
	 * <br>
	 * A helper function that should ONLY BE RUN ON SPRITE OBJECTS<br>
	 * Nothing happens if this is run on other objects.<br>
	 * Its done here, rather then in sprite-specific classes, to allow this code
	 * to be shared easily between implementations<br>
	 * <br>
	 * This function sets the sprites new URL and number of frames, in order to
	 * switch from one animation to another.<br>
	 */
	public void setSpritesURL(SceneSpriteObjectState ourState, String URL, int Frames) {
		// ensure we are a sprite
		if (!ourState.isCompatibleWith(SceneObjectType.Sprite)) {
			return;
		}

		// first cast ourselves to a sprite
		IsSceneSpriteObject ourselvesAsSprite = (IsSceneSpriteObject) this;
		SceneSpriteObjectState ourStateAsSprite = ourState;// (SceneSpriteObjectState)this.objectsCurrentState;

		// first the state change

		// if its internal we dont change the url as it isn't a real url, but
		// just a id for the animation
		if (URL.startsWith("<")) {

			ourStateAsSprite.ObjectsURL = URL;

		} else if (!(URL.contains("\\") || URL.contains("/"))) {

			ObjectsLog("setting up defaultlocation from url: " + URL);

			// if inventoryitem we handle the path assumption different as
			// inventory objects are all in the InventoryItems folder
			if (ourState.isCompatibleWith(SceneObjectType.InventoryObject)) {

				String sourceFolder = "InventoryItems";

				ourStateAsSprite.ObjectsURL = sourceFolder + "/" + this.getName() + "/" + URL;

			} else {

				String sourceFolder = "Game Scenes/" + initialObjectState.ObjectsSceneName;
				// get url from name
				// note we use the DEFAULT scene name, as objects can move from
				// scene to scene
				ourStateAsSprite.ObjectsURL = sourceFolder + "/Objects/" + folderName + "/" + URL;
			}

		} else {
			// load a full filepath
			ObjectsLog("setting up location for sprite with full path " + URL);
			ourStateAsSprite.ObjectsURL = URL;

		}

		// get url from name
		// note we use the DEFAULT scene name, as objects can move from scene to
		// scene
		// currentObjectState.ObjectsURL = "Game
		// Scenes/"+defaultObjectState.ObjectsSceneName
		// + "/Objects/" + folderName + "/" + URL;

		ObjectsLog("Objects default scene is:" + initialObjectState.ObjectsSceneName);

		ourStateAsSprite.currentNumberOfFrames = Frames;
		ourStateAsSprite.ObjectsFileName = URL;
		ourStateAsSprite.currentFrame = 0;

		ObjectsLog("Objects url set to:" + ourStateAsSprite.ObjectsURL + " with "
				+ ourStateAsSprite.currentNumberOfFrames);

		// check for and load attachment point movements
		recheckAttachmentPoints(ourStateAsSprite);

		// then the "physically" reflection of it
		ourselvesAsSprite.setURLPhysically(true);

		

	}

	public void recheckAttachmentPoints(SceneSpriteObjectState ourStateAsSprite) {
		
		if (ourStateAsSprite.hasAttachmentPointFile) {

			// wait, what if the url is already a full path? This will mess up
			// surely?
			// String atachmentPointUrl = "Game
			// Scenes/"+initialObjectState.ObjectsSceneName
			// + "/Objects/" + folderName + "/"+ URL.subSequence(0,
			// URL.lastIndexOf(".")-1)+".glu";

			String atachmentPointUrl = ourStateAsSprite.ObjectsURL.subSequence(0,
					ourStateAsSprite.ObjectsURL.lastIndexOf(".") - 1) + ".glu";

			getAttachmentPointMovements(atachmentPointUrl);

			//also update our own pin if this file specifies it
			//note; currentlyu only works if glu was already loaded
			if (attachmentPoints!=null){
				MovementWaypoint wp=attachmentPoints.getPrimaryPinDataFor(ourStateAsSprite.currentFrame);
				if (wp!=null){
					setPin(wp.pos.x, wp.pos.y, wp.pos.z, false);						
				} else {
					setPin(ourStateAsSprite.DefaultPinPoint.x, ourStateAsSprite.DefaultPinPoint.y, ourStateAsSprite.DefaultPinPoint.z, false);						
				}

				//update our position to reflect a potential pin change
				//ObjectsLog("refreshing position"); //purhapes only fire if pin changed?
				//refreshPosition();


			}

		}
	}


	/**
	 * fired when all the objects are fully loaded and the scene is just about to appear.
	 * (if set to front)
	 * 
	 */
	public void onFullyLoadedCompleteForAllObjectsInScene() {
		ObjectsLog("<--- onFullyLoadedCompleteForAllObjectsInScene fireing","GREEN");

		// if we are not attached to a scene we dont set the zindex (this
		// happens when we are attached to a inventory or when all logical loading finnishs first)
		if (getParentScene() != null) {

			// set zindex
			if (getObjectsCurrentState().zindex > 0) {

				SOLog.info("_____attempting to set zindex on " + getObjectsCurrentState().ObjectsName + " to;"	+ getObjectsCurrentState().zindex);


				setZIndex(getObjectsCurrentState().zindex);

				SOLog.info("_____set zindex to " + this.getZindex());

				ObjectsLog("after setting zindex content length=" + toString().length());

				// this.getElement().getParentElement().getStyle().setZIndex(zIndex);
				// this.set

			} else {
				SOLog.info("_____no zindex on object;" + getObjectsCurrentState().zindex);
			}

		} else {
			// not on a scene, so we dont set the zindex
		}

	}

	/**
	 * Fired when all the objects in the scene are logically loaded (all files but visuals)
	 */
	public void onLogicalLoadCompleteForAllObjectsInScene() {

		SOLog.info("<--- onLogicalLoadCompleteForAllObjectsInScene fireing");
		ObjectsLog("<--- onLogicalLoadCompleteForAllObjectsInScene fireing","GREEN");



		SOLog.info("__________onLoad actions:");
		// ok...deep breath....we trigger the OnFirstLoad actions when there is
		// actions to run,
		// and this hasn't already been loaded ,
		// and, if this object has a scene at all, that scene is not loading
		// silently!
		if (actionsToRunOnFirstLoad != null && alreadyLoaded == false
				&& (getParentScene() == null || !getParentScene().loadingsilently)) {

			wasLastObjectUpdated();

			ObjectsLog("(running OnFirstLoad commands)");

			InstructionProcessor.processInstructions(actionsToRunOnFirstLoad.CommandsInSet.getActions(),
					"fl_" + this.getObjectsCurrentState().ObjectsName, this);
		}

		SOLog.info("__________onLoad actions ran");
		alreadyLoaded = true;

	}

	/**
	 * FOR INVENTORY ITEMS ONLY <br>
	 * 
	 * If this item was just tested for a useritem used but no actions where
	 * found/ran this should be ran It will unhold the item if no menu is
	 * showing
	 */
	public void justUsedButNoActionsFound() {
		if (!SceneMenuWithPopUp.menuShowing) {
			SOLog.info("__unholding due to no default actions and no menu showing");
			InventoryPanelCore.unholdItem();
		}
	}

	/**
	 * FOR INVENTORY ITEMS ONLY <br>
	 * 
	 * If this item was just tested and specific actions where found/ran this
	 * should be triggered It will unhold the item if no menu is showing and we
	 * arnt set to keep holding on use
	 */
	public void justUsedSpecificActionsFound() {
		if (!SceneMenuWithPopUp.menuShowing && (keepheld != KeepHeldMode.onuse)) {
			SOLog.info("__unholding due to actions being found,no menu showing, and \"keep on use\" not being set");
			InventoryPanelCore.unholdItem();
		}

	}

	/**
	 * FOR INVENTORY ITEMS ONLY <br>
	 * 
	 * If this item was just tested only default actions were found/ran this
	 * should be triggered *
	 */
	public void justUsedButOnlyDefaultActionsFound() {
		// currently no unHold fires when default actions are found.
		// purhapes this should be a keepheld option?
	}

	/**
	 * should be fired if theres a click while the mouse is over this object,
	 * yet this object is blocked by another object
	 */
	public void triggerActionsToRunWhenMouseClickedWhileBehind() {
		SOLog.info("triggerActionsToRunWhenMouseClickedWhileBehind. Item is currently held yes/no:"
				+ InventoryPanelCore.isItemCurrentlyBeingHeld);

		CommandList commands = this.objectsActions.getActionsForTrigger(TriggerType.OnClickedWhileBehind, null);

		if (commands != null) {
			wasLastObjectClicked();

			InstructionProcessor.processInstructions(commands, "rc_" + getObjectsCurrentState().ObjectsName, this);
		}
	}

	/**
	 * Set this object to sycn its z-index to its parent object.
	 * If this object is not positioned relative to something, it will flag a warning.
	 * 
	 * @param linkedOn
	 * @param difference
	 */
	public void setZIndexAsLinked(boolean linkedOn, int difference) {

		if (linkedOn) {

			if (getObjectsCurrentState().positionedRelativeToo==null){
				SOLog.warning("Attempting to setZIndexAsLinked but "+this.getName()+" has no parent. Did you set the link request the correct way around? [objecttosetzindexaslinked,objecttogetitszindexfrom,differenceinz]");				
			}

			SOLog.info("Linking " + getName() + " zindex to " + getObjectsCurrentState().positionedRelativeToo.getName());

			getObjectsCurrentState().linkedZindex = true;
			getObjectsCurrentState().linkedZindexDifference = difference;// Integer.parseInt(difference);

			// update the zindex to reflect this new setting
			setZIndex(0); // NOTE: the value we give this function is irrelevant
			// when on linked zindex

		} else {
			// turn linking off
			getObjectsCurrentState().linkedZindex = false;

		}

	}

	/**
	 * sets this objects state to the one supplied. 
	 * 
	 * NOTE:Remember to copy the used state if you dont want it to be shared!
	 * 
	 * @param copy
	 */
	public void setMovementState(Optional<MovementState> copy) {
		SOLog.info("setting movement state on " + getName() + " ");
		getObjectsCurrentState().moveState = copy;
		
		if (copy!=null && copy.isPresent()){
			ObjectsLog("Set MovementState To:" + copy.get().toString());
		} else {
			ObjectsLog("Set MovementState To: Null (no movement)" );
		}		
		
		// start movements if there is any
		if (copy.isPresent() && copy.get().isMoving()) {
			
			if  (objectsCurrentState.moveState.get().currentmovementtype == MovementStateType.OnLinePath
					|| objectsCurrentState.moveState.get().currentmovementtype == MovementStateType.OnCurvePath){
				
				ObjectsLog("(starting movement on path "+objectsCurrentState.moveState.get().getCurrentPathName()+" start)","green");
				ObjectsLog("__________running path :" + objectsCurrentState.moveState.get().currentPathData.getAsSVGPath(true));
			
			} else {
				ObjectsLog("(starting movement of type:"+objectsCurrentState.moveState.get().currentmovementtype+")");
			}
			
			startCurrentMovement();
		}

	}

	/**
	 * fires any checks needed if the size of the object changes
	 */
	protected void objectSizeChanged() {

		// If variable position, we re-check it as our baseline might have
		// shifted (as well as bounding boxs)
		setZIndexByPosition(getObjectsCurrentState().getY());
		// In future we might want to just refresh the current position, in case
		// attachments need to be updated too?

	}

	/**
	 * returns the objects name, in brackets. Usefull for listing arrays on objects
	 */
	@Override
	public String toString() {
		return "(" + this.getName() + ")";
	}

	/**
	 * PURELY FOR DEBUG
	 * we return a string containing some information as to the current load status of this object
	 * @return
	 */
	public String getLoadStatusDebug() {

		String loadStatus = " logicalLoad (:"
				+"collisions:"+CollisionMapLoadedOrNotNeeded+","
				+"glu files:"+AttachmentPointsLoadedOrNotNeeded+","
				+"movement files:"+MovementFilesLoadedOrNotNeeded
				+":)\n"
				+"isPhysicallyLoaded:"+isPhysicallyLoaded+"\n"
				+" Already loaded:"+alreadyLoaded+"\n";

		return loadStatus;
	}


	/**
	 * returns true only if all the things needed for the object to be logically loaded are loaded.
	 * This essentially means all files except images/visuals.
	 * 
	 * ie currently;
	 * CollisionMapLoadedOrNotNeeded
	 * AttachmentPointsLoadedOrNotNeeded
	 * MovementFilesLoadedOrNotNeeded
	 * are all checked
	 * 
	 * @return
	 */
	public boolean isLogicalyLoaded() {

		if (CollisionMapLoadedOrNotNeeded 
				&& AttachmentPointsLoadedOrNotNeeded 
				&& MovementFilesLoadedOrNotNeeded) {
			return true;
		}

		return false;
	}

	/**
	 * should be set to true when the object is both loaded in terms of visuals (if any) and attached to the scene.
	 * non-image based widgets can ignore the visual part, and just set this when attached 
	 */
	private boolean isPhysicallyLoaded = false;

	public void setAsPhysicallyLoaded() {

		this.ObjectsLog("<--------------------Object now physically loaded","green");  //might not be safe to use yet if this is called from constrctor
		this.isPhysicallyLoaded = true;
		SOLog.warning("<------------------- Object:" + this.getName() + " has been physically loaded");

		if (this.isLogicalyLoaded() && this.isPhysicallyLoaded()){
			onFullyLoaded();
		}

	}


	private void testIfWeAreLogicallyLoaded() {

		if (isLogicalyLoaded()) {

			SOLog.warning("Object:" + this.getName() + " has been logically loaded");
			PreconstructionObjectsLog("Object:" + this.getName() + " has been logically loaded (pre)");  //might not be safe to use yet if this is called from constrctor

			if (getParentScene()!=null){
				this.getParentScene().advanceLogicalLoading(this); // tell the
				// parent scene
				// we are
				// logically
				// loaded if we have a parent
				// This will also fire onLogicalLoadComplete if the scene was loading
			} else {
				//if we don't have a scene (like we are a inventory item) we fire our own logical load
				onLogicalLoadComplete();			


			}


		}

		//test if we are fully loaded
		if (isLogicalyLoaded() && isPhysicallyLoaded()){
			onFullyLoaded();
		}

	}


	/**
	 * has this object been physically loaded?
	 * That means its images are ready, and its attached
	 * 
	 * @return
	 */
	public boolean isPhysicallyLoaded() {		
		return isPhysicallyLoaded;
	}

	/**
	 * both physical and logical loading is complete on this object
	 */
	protected void onFullyLoaded() {

		this.ObjectsLog("<--------------------Object now fully loaded","green");  //might not be safe to use yet if this is called from constructor

		//we recheck zindex  if its variable
		if (getObjectsCurrentState().variableZindex){
			//Note; if the rest of the scene objects arnt yet ready, this might do nothing.
			//However, we need to check in case they are.
			//If not , another check should happen when "onLogicalLoadCompleteForAllObjects" fires.
			//The conditions for running a setZindex might need collision maps for all objects (logical for everything) and our size (physical for us)
			setZIndexByPosition(getObjectsCurrentState().getY()); //NOTE: we use original Y, not the one with the fake height built in
		}

	}

	/**
	 * fired when this objects logical load is complete (that is, it has all the files it needs except images/visuals)
	 */
	public void onLogicalLoadComplete() {
		this.ObjectsLog("<--------------------Object now logically loaded","green");  //might not be safe to use yet if this is called from constrctor

	}


	/**
	 * returns the full duration, in ms, that the current movement is set to take. 
	 * This only works with path motions, physics movement goes on forever, or until the friction slows its speed to under a certain level
	 * The result assumes straight line based pathsas well, curved paths dont yet return accurate lengths
	 * 
	 * @return - either duration in ms....or a meaningless value if on physics motion
	 */
	public double getCurrentMovementDuration() {


		double dur = 0;
		if (this.getObjectsCurrentState().moveState.isPresent() && isMoving()){

			MovementState ourmovestate = objectsCurrentState.moveState.get();
			
			double pathLength = (double)ourmovestate.currentPathData.PathLength;
			double movement_speed = (double)ourmovestate.movement_speed;
			
			dur =
					pathLength / 
					movement_speed;

			if (dur<0.0){

				ObjectsLog("Error; duration of travel negative ("+dur+")","Red");
				ObjectsLog("PathLength:"+pathLength);
				ObjectsLog("Speed:"+movement_speed);
				dur=-dur;		

			} else {
				ObjectsLog("Deduced Travel Time:"+pathLength+" / "+movement_speed+" = "+dur); //temp log to help debug long durations
				
				ObjectsLog("regened path:"+ourmovestate.currentPathData.getAsSVGPath()); 				
				ObjectsLog("regened path:"+ourmovestate.currentPathData.getCurrentLength()); 
				
			}


		}else {

			ObjectsLog("Error; Attempted to get travel duration time while object was not moving","Orange");		
			SOLog.info("Error; Attempted to get travel duration time while object "+this.getName()+" was not moving");

			dur=0;

		}

		return dur;
	}

	/**
	 * returns either this objects cmap, or a compound map if this object has children
	 * 
	 * @return
	 */
	public Optional<PolygonCollisionMap> getCompoundCmap() {
		if (newCompoundMap!=null){

			ObjectsLog("Returning pre-generated Compoundmap:");
			return newCompoundMap;
		}		
		if (newCompoundMap==null && !relativeObjects.isEmpty()){
			return generateCompoundCmap();
		}



		return cmap;
	}


	/**
	 * A "compound" collision map of our map and our children's
	 * If no children are present this is just our own map.
	 */
	Optional<PolygonCollisionMap> newCompoundMap;


	/**
	 * Generates a "compound" collision map of our map and our children's
	 * If no children, this just return our own map.
	 */
	public Optional<PolygonCollisionMap> generateCompoundCmap() {
		
		ObjectsLog("Generating New Compoundmap:");
		
		//if no child objects its just our our own cmap
		if (this.relativeObjects.isEmpty()){

			if (this.cmap.isPresent() && this.cmap.get().hasCoporalPart()){
				return cmap;
			}

			return Optional.absent(); //no map or empty map
		} else {

			newCompoundMap = Optional.of(new PolygonCollisionMap("",this));

			//now our children's
			for (SceneObject child : relativeObjects) {

				//if (child.cmap.isPresent()){

				Optional<PolygonCollisionMap> childMap = child.getCompoundCmap();

				if (childMap.isPresent() && !childMap.get().isEmpty() && childMap.get().hasCoporalPart()){
					newCompoundMap.get().addAll(childMap.get()); 

				}

				//}

			}


			if (cmap.isPresent() && !cmap.get().isEmpty() && cmap.get().hasCoporalPart()){
				newCompoundMap.get().addAll(cmap.get()); //add our own map if we have one


			} else {
				//<-------------------------------------------------------------------------------------------------------------urgent
				//TODO: what if we have a bottom line and relative objects?
				//should we auto-generate a cmap approximately like the line and add that?
				//(just a long thing rectangle a few pixels high?)
				//maybe;
				
				SOLog.info("getting path for compound map from object with no cmap");
				ObjectsLog("getting path for compound map from object with no cmap"+newCompoundMap.get().size());
				
				
				//TODO: what if we arnt on botttom line mode?
				//only generate this if there was corporal relative parts found ?
				if (!newCompoundMap.get().isEmpty() && this.getObjectsCurrentState().boundaryType.collisionType==CollisionType.bottomline){

					//lower line relative to objects topleft
					Simple2DPoint ourLowerLineStart = new Simple2DPoint(0,this.getPhysicalObjectHeight());
					ourLowerLineStart.y=ourLowerLineStart.y-5;
					Simple2DPoint bottomRight = new Simple2DPoint(this.getPhysicalObjectWidth(),this.getPhysicalObjectHeight());
					
					//needs to be pin relative
					//bottomRight.x=bottomRight.x-this.getObjectsCurrentState().CurrentPinPoint.x;
					//bottomRight.y=bottomRight.y-this.getObjectsCurrentState().CurrentPinPoint.y;
					//ourLowerLineStart.x=ourLowerLineStart.x-this.getObjectsCurrentState().CurrentPinPoint.x;
					//ourLowerLineStart.y=ourLowerLineStart.y-this.getObjectsCurrentState().CurrentPinPoint.y;
					
					ObjectsLog("          topleft:"+ourLowerLineStart.toString());
					ObjectsLog("      bottomRight:"+bottomRight.toString());
					

					PolygonCollisionMap line = PolygonCollisionMap.generateRect(ourLowerLineStart,bottomRight,this);	
					
					SOLog.info("getting path for preview:"+line.size());
					
					ObjectsLog("__generated map from bottom line. Result=\n"+line.getPath("green", false,new SimpleVector3(1,1,1)));
//					
					newCompoundMap.get().addAll(line); //add above

				}		

				
			}



			//temp test; removing incoporals
			newCompoundMap.get().removeIncoporalBits();

			//if zero return as absent
			if (newCompoundMap.get().isEmpty()){				
				ObjectsLog("Generated Compound Cmap empty (happens when we have subobjects but none with coporal cmaps are visible)");				
				return Optional.absent();
			}

			this.ObjectsLog("Generated Compound Cmap of size:"+newCompoundMap.get().size());

			//Note; compoun map seems to be correct, inc sub-sub objects. It all results in absolute co-ordinates 

			/*
			String path = newCompoundMap.get().getPath("rgba(95,5,95,0.5)",false);
			this.ObjectsLog("Compound Cmap \n:"+path);

			SceneCollisionMap sceneCollisionMap = this.getParentScene().scenesCmap.get(); //we know its present as we tested in the if
			sceneCollisionMap.addToSketch(path, "rgba(95,5,95,0.5)",false);
			 */
			return newCompoundMap;
		}

	}

	/**
	 * child classes can override this if they want to run stuff when the parents url changes
	 * @param newParentURL - including frame  number and png burnt in! (eg, meryll5.png)
	 * @param frameNum - incase the frame num is needed seperately, this saves the need to strip it
	 */
	public void parentsURLJustChanged(String newParentURL) {
		
		//DepedantURL update 
		if (this.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Sprite)){
		
			//--messy; we need our own root (should this be saved?)			
			String sourceFolder = "Game Scenes/" + getInitialState().ObjectsSceneName;
			String ourURL = sourceFolder + "/Objects/" + folderName + "/";
			
			IsSceneSpriteObject asSprite = (IsSceneSpriteObject) this;
			
			if (!asSprite.getObjectsCurrentState().DependantURLPrefix.isEmpty()){						
				String DepPrefix = asSprite.getObjectsCurrentState().DependantURLPrefix;						
				
				//asSprite.setSpritesURL(asSprite.getObjectsCurrentState(), ourURL+DepPrefix+newParentURL, 1);
				
				//theres no need for a lot of the processing the setSpriteURL method does, so we use the Exactly method instead; 
				SpriteObjectHelpers.setSpriteUrlExactly(asSprite, ourURL+DepPrefix+newParentURL);
			}
			
		}
		
		
	}
	
	
	

	

	



	/**
	 * method purely here to make timmer events on movement neater. All this
	 * stuff is "inside" a timer event above and not really meant to be used
	 * elsewhere
	 * 
	 * @param deltaTime
	 *            - the time since the last update (currently not used as
	 *            framerate is fixed at interval)
	 *
	 *            private void movementFrameUpdate_old(double deltaTime) {
	 *            //This is all moved to super, it doesnt need to be here
	 * 
	 *            final double DELTA_TIME = deltaTime; //100 temp value to help
	 *            change to the new system (as the fixed interval used to be
	 *            100ms) //This should be changed to 100 at the same time SPEED
	 *            is reduced by 100 (because its no longer multiplied by
	 *            interval) //NB: Check curve movement is handled right Not sure
	 *            if CurveTimeStep should be just multiplied by delta
	 * 
	 *            //isMoving = true; objectsCurrentState.moveState.isMoving =
	 *            true;
	 * 
	 *            boolean withinY = false; boolean withinX = false;
	 * 
	 *            // if at or within just under one step of next waypoint, then
	 *            we // test the next waypoint // SOLog.info("_____testing if we
	 *            are at waypoint: desX = " // + currentObjectState.movement_DX
	 *            + " cx = " + currentObjectState.movement_currentX);
	 * 
	 *            // SOLog.info("_____testing if we are at waypoint: desY = " //
	 *            + currentObjectState.movement_DY + " cy = " +
	 *            currentObjectState.movement_currentY);
	 * 
	 *            if (!objectsCurrentState.moveState.movement_onCurve) {
	 * 
	 * 
	 *            if (Math.abs(objectsCurrentState.moveState.movement_currentX -
	 *            objectsCurrentState.moveState.movement_DX) < (Math
	 *            .abs(objectsCurrentState.moveState.movement_StepX*DELTA_TIME)
	 *            + 1)) { //will need to be step x delta in future
	 * 
	 *            withinX = true;
	 *            objectsCurrentState.moveState.movement_currentX =
	 *            objectsCurrentState.moveState.movement_DX;
	 *            objectsCurrentState.moveState.movement_StepX = 0;
	 * 
	 *            } else { withinX = false; };
	 * 
	 * 
	 * 
	 *            if (Math.abs(objectsCurrentState.moveState.movement_currentY -
	 *            objectsCurrentState.moveState.movement_DY) < (Math
	 *            .abs(objectsCurrentState.moveState.movement_StepY*DELTA_TIME)
	 *            + 1)) {
	 * 
	 *            withinY = true;
	 *            objectsCurrentState.moveState.movement_currentY =
	 *            objectsCurrentState.moveState.movement_DY;
	 *            objectsCurrentState.moveState.movement_StepY = 0;
	 * 
	 *            } else { withinY = false; } ; } else {
	 * 
	 *            withinX = false; withinY = false;
	 * 
	 *            if (objectsCurrentState.moveState.movement_curveTime +
	 *            (objectsCurrentState.moveState.movement_curveTimeStep*DELTA_TIME)
	 *            > 1) {
	 * 
	 * 
	 *            ObjectsLog(" movement on curve within tolerance of
	 *            destiniation. Tolerance was:
	 *            "+(objectsCurrentState.moveState.movement_curveTimeStep*DELTA_TIME));
	 * 
	 *            withinX = true; withinY = true;
	 * 
	 *            }
	 * 
	 *            }
	 * 
	 *            if ((withinX) && (withinY)) {
	 * 
	 *            ObjectsLog("at waypoint"+
	 *            objectsCurrentState.moveState.movement_currentWaypoint);
	 * 
	 *            objectsCurrentState.moveState.movement_currentWaypoint =
	 *            objectsCurrentState.moveState.movement_currentWaypoint + 1;
	 * 
	 * 
	 *            // snap to current waypoint reached for consistency in //
	 *            movement objectsCurrentState.moveState.movement_currentX =
	 *            objectsCurrentState.moveState.movement_DX;
	 *            objectsCurrentState.moveState.movement_currentY =
	 *            objectsCurrentState.moveState.movement_DY;
	 * 
	 *            // remove flags objectsCurrentState.moveState.movement_onCurve
	 *            = false; objectsCurrentState.moveState.movement_SX = -1;
	 *            objectsCurrentState.moveState.movement_SY = -1;
	 * 
	 * 
	 *            // SOLog.info("currentPath size
	 *            test:"+currentPath.getAsSVGPath());
	 * 
	 * 
	 *            if (objectsCurrentState.moveState.movement_currentWaypoint <
	 *            currentPath .size()) {
	 * 
	 *            ObjectsLog("updating
	 *            waypoint"+objectsCurrentState.moveState.movement_currentWaypoint);
	 * 
	 *            MovementWaypoint cp = currentPath
	 *            .get(objectsCurrentState.moveState.movement_currentWaypoint);
	 * 
	 *            MovementType currenttype = cp.type;
	 * 
	 * 
	 * 
	 * 
	 *            if (skipFirstJump == true) {
	 * 
	 *            currenttype = MovementType.LineTo;
	 * 
	 *            skipFirstJump = false;
	 * 
	 *            }
	 * 
	 *            //if the new waypoint is at the current location, then we
	 *            force a teleport to //save calculation power if
	 *            (cp.x==objectsCurrentState.moveState.movement_currentX){ if
	 *            (cp.y==objectsCurrentState.moveState.movement_currentY){
	 *            SOLog.info("already at destination");
	 *            currenttype=MovementType.Move; }
	 * 
	 *            }
	 * 
	 *            if (currenttype == MovementType.LineTo) {
	 * 
	 * 
	 *            objectsCurrentState.moveState.movement_DX = cp.x;
	 *            objectsCurrentState.moveState.movement_DY = cp.y;
	 * 
	 *            //if relative add the last location on if (cp.isRelative()){
	 *            SOLog.info(this.objectsCurrentState.ObjectsName+" taking
	 *            relative movement line to");
	 * 
	 *            objectsCurrentState.moveState.movement_DX = cp.x+
	 *            objectsCurrentState.moveState.movement_currentX;
	 *            objectsCurrentState.moveState.movement_DY = cp.y+
	 *            objectsCurrentState.moveState.movement_currentY;
	 * 
	 *            }
	 * 
	 * 
	 *            ObjectsLog("next waypoint is a line going to " +
	 *            objectsCurrentState.moveState.movement_DX + "," +
	 *            objectsCurrentState.moveState.movement_DY);
	 * 
	 *            ObjectsLog("from " +
	 *            objectsCurrentState.moveState.movement_currentX + "," +
	 *            objectsCurrentState.moveState.movement_currentY);
	 * 
	 *            // update direction updateObjectsDirection(
	 *            objectsCurrentState.moveState.movement_DX,
	 *            objectsCurrentState.moveState.movement_DY);
	 * 
	 *            // we ensure a minimum speed by rounding up
	 *            ObjectsLog("movement_currentX " +
	 *            objectsCurrentState.moveState.movement_currentX + " ");
	 * 
	 *            ObjectsLog("movement_currentY " +
	 *            objectsCurrentState.moveState.movement_currentY + " ");
	 * 
	 *            int Xdistance = ((objectsCurrentState.moveState.movement_DX -
	 *            objectsCurrentState.moveState.movement_currentX)); int
	 *            Ydistance = ((objectsCurrentState.moveState.movement_DY -
	 *            objectsCurrentState.moveState.movement_currentY));
	 * 
	 *            //note; not a error, this is meant to be the total x/y
	 *            magnitudes, not the hypo int TotalLength =
	 *            Math.abs((Ydistance))+ Math.abs((Xdistance));
	 * 
	 *            // work out the vector x/y between 0 and 1, and // then
	 *            multiply by the stepsize
	 * 
	 * 
	 * 
	 *            ObjectsLog("TotalLength "+ TotalLength + " ");
	 * 
	 *            // work out ratios Double Xratio = ((double)Xdistance /
	 *            (double) TotalLength); Double Yratio = ((double)Ydistance /
	 *            (double) TotalLength);
	 * 
	 * 
	 *            // GreySOLog.info("Xratio " // + Xratio + " ");
	 * 
	 *            // GreySOLog.info("Yratio " // + Yratio + " ");
	 * 
	 *            double StepX = (Xratio *
	 *            (objectsCurrentState.moveState.movement_speed)); //*DELTA_TIME
	 *            should only be done before adding to position. NOT stored in
	 *            Step values
	 * 
	 * 
	 *            objectsCurrentState.moveState.movement_StepX = StepX;
	 * 
	 * 
	 *            // 10 // can // be // replaced // by the speed in // pixels a
	 *            cycle // // NB: Speed is currently pixels per cycle // We
	 *            should change this to just pixels per MS by removing the x100
	 *            multiplier from movement speed // Then ensuring the values
	 *            down the chain maintain the decimal points before finally
	 *            multiplying by the precise // Delta value (time since last
	 *            frame) double StepY = (Yratio *
	 *            (objectsCurrentState.moveState.movement_speed)); //*DELTA_TIME
	 *            should only be done before adding to position. NOT stored in
	 *            Step values
	 * 
	 *            objectsCurrentState.moveState.movement_StepY = StepY;
	 * 
	 * 
	 *            ObjectsLog(" step
	 *            X="+objectsCurrentState.moveState.movement_StepX+" step
	 *            Y="+objectsCurrentState.moveState.movement_StepY);
	 * 
	 *            // ensure minimum movement if
	 *            (objectsCurrentState.moveState.movement_StepY == 0 &&
	 *            objectsCurrentState.moveState.movement_DY < 0) {
	 *            objectsCurrentState.moveState.movement_StepY = -1;
	 *            //*DELTA_TIME } if
	 *            (objectsCurrentState.moveState.movement_StepY == 0 &&
	 *            objectsCurrentState.moveState.movement_DY > 0) {
	 *            objectsCurrentState.moveState.movement_StepY = 1;
	 *            //*DELTA_TIME } if
	 *            (objectsCurrentState.moveState.movement_StepX == 0 &&
	 *            objectsCurrentState.moveState.movement_DX < 0) {
	 *            objectsCurrentState.moveState.movement_StepX = -1; } if
	 *            (objectsCurrentState.moveState.movement_StepX == 0 &&
	 *            objectsCurrentState.moveState.movement_DX > 0) {
	 *            objectsCurrentState.moveState.movement_StepX = 1; }
	 * 
	 *            // SOLog.info("so we are moving
	 *            by..."+movement_StepX+","+movement_StepY);
	 * 
	 *            // movement_currentX = movement_currentX + // movement_StepX;
	 *            // movement_currentY = movement_currentY + // movement_StepY;
	 * 
	 *            telePortFlag = false;
	 * 
	 *            } else if (currenttype == MovementType.Move) {
	 * 
	 *            objectsCurrentState.moveState.movement_DX = cp.x;
	 *            objectsCurrentState.moveState.movement_DY = cp.y;
	 * 
	 *            if (cp.isRelative()){
	 * 
	 *            ObjectsLog("relative move");
	 *            objectsCurrentState.moveState.movement_DX = cp.x+
	 *            thisobject.getX(); objectsCurrentState.moveState.movement_DY =
	 *            cp.y+ thisobject.getY();
	 *            //objectsData.moveState.movement_currentY;
	 * 
	 *            //set starting loc to x/y of pin }
	 * 
	 *            ObjectsLog("next waypoint is to move to"+
	 *            objectsCurrentState.moveState.movement_DX+"
	 *            "+objectsCurrentState.moveState.movement_DY);
	 * 
	 *            // teleport there
	 * 
	 *            objectsCurrentState.moveState.movement_currentX =
	 *            objectsCurrentState.moveState.movement_DX;
	 *            objectsCurrentState.moveState.movement_currentY =
	 *            objectsCurrentState.moveState.movement_DY;
	 * 
	 *            telePortFlag = true;
	 * 
	 *            // movement_StepX = (cp.x - movement_currentX); //
	 *            movement_StepY = (cp.y - movement_currentY);
	 * 
	 *            // SOLog.info("so we are moving
	 *            by..."+movement_StepX+","+movement_StepY);
	 * 
	 *            } else if (currenttype == MovementType.CurveToo) {
	 * 
	 * 
	 *            // sets up the params needed for curved movement // updates
	 * 
	 *            objectsCurrentState.moveState.movement_DX = cp.x;
	 *            objectsCurrentState.moveState.movement_DY = cp.y;
	 * 
	 *            objectsCurrentState.moveState.movement_CX = cp.midpoint_x;
	 *            objectsCurrentState.moveState.movement_CY = cp.midpoint_y;
	 * 
	 *            objectsCurrentState.moveState.movement_SX =
	 *            objectsCurrentState.moveState.movement_currentX;
	 *            objectsCurrentState.moveState.movement_SY =
	 *            objectsCurrentState.moveState.movement_currentY;
	 * 
	 *            //if relative add the last location on if (cp.isRelative()){
	 * 
	 *            objectsCurrentState.moveState.movement_DX = cp.x+
	 *            objectsCurrentState.moveState.movement_currentX;
	 *            objectsCurrentState.moveState.movement_DY = cp.y+
	 *            objectsCurrentState.moveState.movement_currentY;
	 * 
	 *            objectsCurrentState.moveState.movement_CX = cp.midpoint_x+
	 *            objectsCurrentState.moveState.movement_currentX;
	 *            objectsCurrentState.moveState.movement_CY = cp.midpoint_y+
	 *            objectsCurrentState.moveState.movement_currentY;
	 * 
	 *            SOLog.info("relative curve to
	 *            "+objectsCurrentState.moveState.movement_DX
	 *            +","+objectsCurrentState.moveState.movement_DY);
	 *            SOLog.info("via "+objectsCurrentState.moveState.movement_CX
	 *            +","+objectsCurrentState.moveState.movement_CY);
	 * 
	 *            }
	 * 
	 *            objectsCurrentState.moveState.movement_onCurve = true;
	 * 
	 *            ObjectsLog("curve data x: " +
	 *            objectsCurrentState.moveState.movement_SX + " " +
	 *            objectsCurrentState.moveState.movement_CX + " " +
	 *            objectsCurrentState.moveState.movement_DX);
	 * 
	 *            ObjectsLog("curve data y: " +
	 *            objectsCurrentState.moveState.movement_SY + " " +
	 *            objectsCurrentState.moveState.movement_CY + " " +
	 *            objectsCurrentState.moveState.movement_DY);
	 * 
	 *            // temp, need to be corrected:
	 *            objectsCurrentState.moveState.movement_curveTime = 0;
	 *            objectsCurrentState.moveState.movement_curveTimeStep = 0.01 *
	 *            (objectsCurrentState.moveState.movement_speed); //note: Before
	 *            changing to a delta system the speed used to be 100times
	 *            bigger as that was the interval of refreshs.
	 * 
	 * 
	 *            ObjectsLog(" time step=" +
	 *            objectsCurrentState.moveState.movement_curveTimeStep+ " ");
	 * 
	 *            if (objectsCurrentState.moveState.movement_curveTimeStep <
	 *            (0.01/100)) { //minimum has been reduced by 100 due to
	 *            TimeStep being per ms now not per update (see above reg delta)
	 *            ObjectsLog(objectsCurrentState.moveState.movement_curveTimeStep
	 *            + " is under minimum curve speed setting it to:"+(0.01/100));
	 * 
	 *            objectsCurrentState.moveState.movement_curveTimeStep =
	 *            (0.01/100); }
	 * 
	 *            objectsCurrentState.moveState.movement_StepY = 10;
	 *            objectsCurrentState.moveState.movement_StepX = 10;
	 * 
	 *            } else if (currenttype == MovementType.LoopToStart) {
	 * 
	 *            // if there's a "z" then loop // next destination is the first
	 *            again // SOLog.info("looping to start");
	 * 
	 *            objectsCurrentState.moveState.movement_currentWaypoint = -1;
	 *            telePortFlag = true;
	 * 
	 *            } else if (currenttype == MovementType.Command) {
	 * 
	 *            SOLog.info("running command :" + cp.Command);
	 * 
	 *            InstructionProcessor .processInstructions( cp.Command,
	 *            "movec_" + thisobject.objectsCurrentState.ObjectsName,
	 *            thisobject);
	 * 
	 *            } else { // etc for other types // SOLog.info("unknown type
	 *            "+cp.type.toString());
	 * 
	 *            telePortFlag = false; }
	 * 
	 *            } else { // if at end, stop SOLog.info("end of movement
	 *            path"); thisobject.setPosition(
	 *            objectsCurrentState.moveState.movement_currentX,
	 *            objectsCurrentState.moveState.movement_currentY);
	 * 
	 *            //animationCallback.cancel(); stopNextMovementFrame();
	 * 
	 * 
	 *            //isMoving = false;
	 * 
	 *            //path specific commands if
	 *            (currentPath.postAnimationCommands!=null){
	 *            currentPath.postAnimationCommands.run(); //this used to be a
	 *            command with .execute() used on it }
	 * 
	 * 
	 *            //generic movement end commands triggerMovementEndCommands();
	 * 
	 * 
	 *            return; } } ;
	 * 
	 *            if (!telePortFlag &&
	 *            !objectsCurrentState.moveState.movement_onCurve) { // if its
	 *            not a teleport waypoint continue movement as we // were before
	 *            if (objectsCurrentState.moveState.movement_currentX !=
	 *            objectsCurrentState.moveState.movement_DX) {
	 * 
	 *            double newX =remainderX+
	 *            (objectsCurrentState.moveState.movement_currentX +
	 *            (objectsCurrentState.moveState.movement_StepX*DELTA_TIME));
	 *            //movementStepX/y should be multiplied by Delta before adding
	 *            to co-ordinates.
	 *            objectsCurrentState.moveState.movement_currentX = (int) newX;
	 * 
	 *            remainderX =
	 *            newX-objectsCurrentState.moveState.movement_currentX;
	 * 
	 *            } if (objectsCurrentState.moveState.movement_currentY !=
	 *            objectsCurrentState.moveState.movement_DY) {
	 * 
	 *            double newY =remainderY+
	 *            (objectsCurrentState.moveState.movement_currentY +
	 *            (objectsCurrentState.moveState.movement_StepY*DELTA_TIME));
	 *            //movementStepX/y should be multiplied by Delta before adding
	 *            to co-ordinates.
	 * 
	 *            objectsCurrentState.moveState.movement_currentY = (int)newY;
	 *            remainderY =
	 *            newY-objectsCurrentState.moveState.movement_currentY; }
	 * 
	 *            // Movement step trigger
	 *            advanceOnStepActions(objectsCurrentState.moveState.movement_StepX*DELTA_TIME,
	 *            objectsCurrentState.moveState.movement_StepY*DELTA_TIME);
	 * 
	 *            }
	 * 
	 *            if (objectsCurrentState.moveState.movement_onCurve) {
	 * 
	 *            int newX = (int) (Math.pow(1 -
	 *            objectsCurrentState.moveState.movement_curveTime, 2)
	 *            objectsCurrentState.moveState.movement_SX + 2 (1 -
	 *            objectsCurrentState.moveState.movement_curveTime) *
	 *            objectsCurrentState.moveState.movement_curveTime
	 *            objectsCurrentState.moveState.movement_CX + Math.pow(
	 *            objectsCurrentState.moveState.movement_curveTime, 2)
	 *            objectsCurrentState.moveState.movement_DX);
	 * 
	 *            int newY = (int) (Math.pow(1 -
	 *            objectsCurrentState.moveState.movement_curveTime, 2)
	 *            objectsCurrentState.moveState.movement_SY + 2 (1 -
	 *            objectsCurrentState.moveState.movement_curveTime) *
	 *            objectsCurrentState.moveState.movement_curveTime
	 *            objectsCurrentState.moveState.movement_CY + Math.pow(
	 *            objectsCurrentState.moveState.movement_curveTime, 2)
	 *            objectsCurrentState.moveState.movement_DY);
	 * 
	 *            objectsCurrentState.moveState.movement_curveTime =
	 *            objectsCurrentState.moveState.movement_curveTime +
	 *            (objectsCurrentState.moveState.movement_curveTimeStep*DELTA_TIME);
	 *            //Curve time step should also move to being delta based
	 * 
	 *            // Movement step trigger // not tested! advanceOnStepActions(
	 *            Math.abs(objectsCurrentState.moveState.movement_currentX -
	 *            newX),
	 *            Math.abs(objectsCurrentState.moveState.movement_currentY -
	 *            newY));
	 * 
	 *            objectsCurrentState.moveState.movement_currentX = newX;
	 *            objectsCurrentState.moveState.movement_currentY = newY;
	 * 
	 *            }
	 * 
	 *            // thisObject.objectScene.setWidgetsPosition( // thisObject,
	 *            // objectsData.moveState.movement_currentX, //
	 *            objectsData.moveState.movement_currentY);
	 * 
	 *            // SOLog.info("setting position of"+thisObject.ObjectsName);
	 * 
	 *            //thisobject.
	 *            setPosition(objectsCurrentState.moveState.movement_currentX,
	 *            objectsCurrentState.moveState.movement_currentY); // //
	 *            SOLog.info("current object position:"+movement_currentX+"
	 *            "+movement_currentY); }
	 */

}
