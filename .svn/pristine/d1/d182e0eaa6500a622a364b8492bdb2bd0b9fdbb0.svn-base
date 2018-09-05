package com.lostagain.Jam.Scene;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.google.common.base.Optional;
import com.lostagain.Jam.CurrentScenesVariables;
import com.lostagain.Jam.GameStatistics;
import com.lostagain.Jam.InventoryPanelCore;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.CollisionMap.IsCollisionMapVisualiser;
import com.lostagain.Jam.CollisionMap.SceneCollisionMap;
import com.lostagain.Jam.CollisionMap.SpiffyPolygonCollision;
import com.lostagain.Jam.Factorys.NamedActionSetTimer;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.Movements.MovementList;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.SceneObjects.CollisionBox;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem.KeepHeldMode;
import com.lostagain.Jam.SceneObjects.SceneObject.LogLevel;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffygwt.SpiffyDragPanel.DragRestriction;

import com.lostagain.Jam.SceneObjects.SceneObjectState.touchingMode;

public abstract class SceneWidget {
	/** The general console log **/
	public static Logger Log = Logger.getLogger("JAMCore.SceneWidget");

	/** 
	 * A background image that appears during loading.
	 * At the moment this is left black and we just have a black background
	 * with the loading clock 
	 ***/
	public static String LoadingBackground ="";


	/**
	 * Flag for first time any scene is loaded
	 * this is used to load global data once
	 **/
	public static boolean firstLoadOfAnyScene = true;

	/** Global hashmap of all scenes **/
	public static HashMap<String, SceneWidget> all_scenes = new HashMap<String, SceneWidget>();


	//dont think this is used;
	public static SceneWidget currentActiveScene;
	//use SceneObjectDatabase.currentScene instead
	
	/**
	 * Where we keep the scenes.<br>
	 * Note; This will refer to actually two "Game Scenes" directories<br>
	 * one in the route of the game folder, and one in the secure subdirectory.<br>
	 * The subdirectory one is used for all the text files, then one in the game folders route
	 * for all images.<br>
	 * This is for security; the text files are protected behind a php.<br>
	 **/
	public static final String SceneFileRoot = "Game Scenes/";



	/**
	 * Sets up a new scene ready to load (run initialise to load)
	 * @param SceneFileName
	 */
	public SceneWidget(String SceneFileName) {		
		super();

		all_scenes.put(SceneFileName.toLowerCase(), this);

		Log.info("Setting up scene: "+SceneFileName+" loading:"+Loading);
		this.SceneFileName = SceneFileName;

		// get the location
		SceneFolderLocation = SceneFileRoot + SceneFileName;
		SceneFileLocation = SceneFolderLocation + "/" + SceneFileName + ".jam";


		// get universal data files if not yet loaded
		// as well as setup visualizers for logger classes
		if (firstLoadOfAnyScene) {

			Log.info(" Loading global actions ");
			InstructionProcessor.loadGlobalSceneActions();	

			MovementList.loadGlobalMovements();

			firstLoadOfAnyScene = false;

		}



	}



	//combine following with one after
	/**
	 * tests and runs actions specific to the scene for instance, any action
	 * might raise a counter variable and a bomb goes off if it gets too high!
	 **/
	/*
	public boolean testForSceneActions_old(TriggerType type, String Parameter) {
		// temp test
		if (sceneActions == null) {
			ScenesLog("(scene sceneActions is null! maybe not loaded yet? ");

			Log.info("scene sceneActions is null! Thats really mega wrong :(");
			return false;
		}
		//Log.info("testing scene actions");
		CommandList actions = sceneActions.getActionsForTrigger(type,
				Parameter);

		if (actions.size() > 0) {

			Log.info("running scene actions:" + actions+" for trigger "+type.toString());
			ScenesLog("running scene actions for trigger:"+type.toString());			

			//	InstructionProcessor.processInstructions(actions,
			//			"NoSourceObject_", null);

			JAMcore.processInstructions(actions,
					"NoSourceObject_", null);


			return true;

		} else {
			ScenesLog("no scene actions  for trigger "+type.toString());			
			Log.info("no scene actions  for trigger "+type.toString());
		}

		return false;
	}
	 */


	/**
	 * 
	 * tests and runs actions specific to the scene for instance, any action
	 * might raise a counter variable and a bomb goes off if it gets too high!
	 **/
	public boolean testForSceneActions(TriggerType type, String Parameter) {
		return testForSceneActions(type,  Parameter,  null); 
	}

	/**
	 * Tests and runs actions specific to the scene for instance, any action
	 * might raise a counter variable and a bomb goes off if it gets too high!
	 **/
	public boolean testForSceneActions(TriggerType type, String Parameter, SceneObject sourceObject) {

		return SceneWidget.runForSceneActions(this, type, Parameter,  sourceObject );
	}

	/**
	 * This variable is used to ensure we don't run two simultaneous click behind tests
	 * Two clicks should never run at the same time, but a clickbehind can trigger might trigger a sendclickto, and we dont want
	 * that second click to re-trigger this
	 */
	boolean testforClickBehindRunning = false;

	/**
	 * Will check over all the ObjectsWithClickedWhileBehindActions objects
	 * to see if any are under the cursor.
	 * If so, will fire there clicked when behind actions
	 *
	 * @param ignoreThisObject - we ignore this object (set this to the object that was ontop and received the clickaction that triggered this check for objects under it)
	 * 
	 * @return
	 **/
	public void testForClickedWhileBehindActions(SceneObject ignoreThisObject){
		if (testforClickBehindRunning){
			return; //dont run if already running
		}

		Log.info("Testing For ClickedWhileBehindActions on all scene objects except "+ignoreThisObject.getName());
		testforClickBehindRunning = true;

		
		ArrayList<SceneObject> objectsMouseIsOver = getObjectsMouseIsOver(getScenesData().scenesObjectsThatSupportClicksWhileBehind);


		for (SceneObject sceneObject : objectsMouseIsOver) {
			
			//if the object is set to be ignored (this is likely the object ontop that would recieve the normal clicks anyway - we dont want it to recieve both)
			if (sceneObject==ignoreThisObject){
				continue;
			}
			//if its not visible, dont trigger the action
			if (!sceneObject.isVisible()){
				continue;
			}
			
			sceneObject.triggerActionsToRunWhenMouseClickedWhileBehind();
		}
		testforClickBehindRunning = false;

		return;
	}

	/**
	 * Should return all the objects in the scene which the mouse is currently over
	 * @param scenesObjectsThatSupportClicksWhileBehind
	 * @return
	 */
	public abstract  ArrayList<SceneObject> getObjectsMouseIsOver(Collection<SceneObject> restrictToThese);






	//NOTE: The following 3 things probably should be in SceneData
	/** this scenes filename**/
	public String SceneFileName;

	/** this scenes folder **/
	public String SceneFolderLocation;


	/** this scenes ini file location **/
	public  String SceneFileLocation;

	/** scene specific actions
	 * These actions are specific to this scene itself, but not to any one object in particular.
	 *  <br> 
	 *  Global Actions will run first.<br> 
	 *  Then scene specific actions (if the object hasn't got "ignoreSceneActions" turned on)<br> 
	 *  Then the objects own actions will run. <br> **/
	public ActionList sceneActions;


	/** 
	 * Scene collision data
	 * If this scene has a collision map, its stored here.
	 * 
	 *  This is to be separated into visual/non-visual components by making a separate 
	 *  VectorMapVisualer interface 
	 **/
	public Optional<SceneCollisionMap> scenesCmap = Optional.absent();




	/** 
	 * Keeps track of the objects and images left to load.
	 * This needs to be empty for the load to be considered complete and the scene ready to display.
	 * <br>
	 * Note; before this is complete, however, the scene can be "Logical" loaded which means it isnt ready to be displayed,
	 * But all the data needed for all the objects OnFirstLoad: is ready. <br>
	 * <br>
	 * **/
	public HashSet<String> ObjectsPhysicallyLeftToLoad =  new HashSet<String>();


	/**
	 * before the full load is complete,  the scene can be "Logical" loaded which means it isnt ready to be displayed,
	 * But all the data needed for all the objects OnFirstLoad: is ready so they can be fired.
	 * This allows the scene to be setup as much as possible while image loading is still going <br>
	 * <br>
	 * Once this list is empty, the scene is logically ready.
	 */
	public HashSet<SceneObject> ObjectsLogicallyLeftToLoad =  new HashSet<SceneObject>();



	/** flags if this scene is loading or not **/
	public boolean Loading = true;
	



	/**if set to true, this scene wont trigger any "onloaded" commands when loaded or automatically brought to the front**/
	public boolean loadingsilently=false;


	public boolean PreparingLoading = true;


	/** scenes default state. Its set once when first loaded to potentially
	 * be used as a reset.
	 * Note; This does not contain the majority of the scenes data,
	 * merely a few things that are purely to do with the scene (and not its objects)
	 * and which can change. (and thus need to be saved)
	 *  **/
	public SceneStatus defaultState;

	/** scenes current state. 
	 * Note; This does not contain the majority of the scenes data,
	 * merely a few things that are purely to do with the scene (and not its objects)
	 * and which can change.(and thus need to be saved)
	 * 
	 *  **/
	public SceneStatus currentState = new SceneStatus();

	/** scenes temp state. 
	 * Note; This does not contain the majority of the scenes data,
	 * merely a few things that are purely to do with the scene (and not its objects)
	 * and which can change.(and thus need to be saved)
	 * 
	 *  **/
	public SceneStatus tempState;

	/** scenes data (includes objects)
	 * <br><br>
	 * The scenes data contains everything needed to set up this scene not related or using gwt functions or widgets.
	 * It mirrors the subclass copy and shouldn't be set separately <br>**/
	protected SceneData scenesData = null;


	/** keeps track of dynamically created objects.
	 * These are objects not native to the scene file - either they are
	 * clone or they are borrowed from another scene **/
	public HashSet<SceneObject> AllDynamicObjects = new HashSet<SceneObject>();

	public boolean StateToLoad = false;

	public SceneStatus sceneStateToLoad;

	public SceneObjectState[] objectsCurrentStateToLoad;

	private DragRestriction currentEditingDragRestriction;




	public SceneData getScenesData() {
		return scenesData;	
	}

	/** sets the sceneData object. Extensions of this class using extensions of SceneData should
	 * always use this to set SceneWidgets reference to the scenedata the same as the one the class extending this is using.
	 * So if you create a new SceneData object, or an extension of it (SceneDataVisual for example), use this on the next line
	 * to set this one to the same thing **/
	protected void setScenesData(SceneData scenesData) {
		this.scenesData = scenesData;
	}

	/**
	 *  This will add a scene to the list of dependent scenes.
	 *  These are scenes which have to load before this one.
	 *  (Note; "Load" does not mean they need their status updated first, merely that the original JAM file has been loaded)
	 *  
	 *  This is required because if an object moves from one scene to another, we have to ensure its original data is loaded 
	 *  from its source scene before loading the savestate into it.
	 *  The save data just contains the stuff that changes, not the original
	 *  
	 * @param objectSceneFileName
	 */
	public void addSceneDependancy(String objectSceneFileName) {


		if (objectSceneFileName==this.SceneFileName){
			return; //why add a self dependency? thats silly
		}
		if (objectSceneFileName.equals(SceneObjectState.OBJECT_HAS_NO_SCENE_STRING)){
			return; //likewise we shouldn't add a no-scene, as thats also bananas
		}


		Log.info("addSceneDependancy:"+objectSceneFileName);
		ScenesLog("Adding depedancy to this scene:"+objectSceneFileName);

		currentState.addSceneDependancy(objectSceneFileName);


	}


	/**
	 * This function should check if any objects from the specified scene remain on this one, if so the dependency is removed
	 * Warning; Function requires rechecking all scenes objects so potentially can be quite slow compared to adding a dependency
	 * If the game lags when moving away from a scene, this may be why.
	 * 
	 * @param objectScene
	 */
	public void removeSceneDependancyIfSafe(SceneWidget objectScene) {

		Log.info("removeing SceneDependancy:"+objectScene.SceneFileName);

		ScenesLog("Removing depedancy from this scene:"+objectScene);
		
		boolean safeToRemove = true;

		//loop over all objects from this scene ensuring that none came from the scene requested to be removed
		//we use the original state to check this
		//Note; We could keep a copy of all foreign objects in the status to avoid this, but it would add to save/loading time.

		//	ArrayList<SceneObjectVisual> objects = this.getScenesData().allScenesCurrentObjects();
		Set<SceneObject> objects = this.getScenesData().allScenesCurrentObjects();


		for (SceneObject sceneObject : objects) {

			if (sceneObject.getInitialState().ObjectsSceneName.equalsIgnoreCase(objectScene.SceneFileName)){

				safeToRemove=false;
				Log.info("not safe to remove, object from that scene still exists : "+sceneObject.getObjectsCurrentState().ObjectsName);

			}
		}


		if (safeToRemove){
			currentState.removeSceneDependancy(objectScene);

			Log.info("removed SceneDependancy success");
		}	


	}





	//NOTE: The X,Y,Z co-ordinates in the state should already be updated to reflect any relative positioning
	public void addObjectToScene(SceneObject objectToAttach) {



		int x = objectToAttach.getObjectsCurrentState().X;
		int y = objectToAttach.getObjectsCurrentState().Y;
		int z = objectToAttach.getObjectsCurrentState().Z; 

		objectToAttach.ObjectsLog("Adding object "+objectToAttach.getName()+" to scene "+this.SceneFolderLocation+" at "+x+","+y+","+z, "green");

		//SceneObject relativeObject = objectToAttach.getObjectsCurrentState().positionedRelativeToo;

		// if its positioned relative to something else get its absolute x/y values 
		/*
		if (relativeObject != null ) {

			objectToAttach.ObjectsLog("_____adding object "+objectToAttach.getObjectsCurrentState().ObjectsName+" relatively:");
			objectToAttach.ObjectsLog("_____relative object is attached "+relativeObject.isAttached());
			objectToAttach.ObjectsLog("_____relative test "+relativeObject.getObjectsCurrentState().X);
			objectToAttach.ObjectsLog("_____source object is at: "+relativeObject.getX()+","+relativeObject.getY()+","+relativeObject.getZ());

			//if needed we could replace x,y,z with the RelX,RelY,RelZ values
			x = x + relativeObject.getX() - objectToAttach.getObjectsCurrentState().PinPointX;
			y = y + relativeObject.getY() - objectToAttach.getObjectsCurrentState().PinPointY;
			z = z + relativeObject.getZ() - objectToAttach.getObjectsCurrentState().PinPointZ;
		}*/

		attachObjectToSceneIMPL(objectToAttach,x,y,z); 

	}


	//2. use as one of the possible ways of collision detection on objects 
	public boolean TestForBoxCollision(int tlx, int tly, int brx, int bry) {

		if (!scenesCmap.isPresent()) {
			Log.info("cmap not set");
			return false;
		} else {
			// temp box is just the size, in future we will have this stored

			if (scenesCmap.get().isBoxColliding(tlx, tly, brx, bry, false) != null) {
				return true;
			} else {
				return false;
			}
			//	return scenesCmap.get().isBoxColliding(tlx, tly, brx, bry, false);
		}

		//if (scenesCmap.isBoxColliding(tlx, tly, brx, bry, false) != null) {
		//return true;
		//	}

		//	return false;

	}


	public boolean TestForCollision(int x, int y) {

		if (!scenesCmap.isPresent()) {
			Log.info("cmap not set");
			return false;
		}

		boolean bol = false;

		if (scenesCmap.get() .isPointColliding(x, y) != null) {

			bol = true;

		}

		return bol;

	}




	//abstract methods for the visual implementation to implement
	//We also need to figure out how to implement SceneMovement here too.
	//What commands do we need abstract?
	//SceneWidgetVisual should really just be the most simple implementation possible, with this class doing the bulk of the work

	/**
	 * Physically attach the object to where it should go on the scene.
	 * ie. If this is a html implementation, this method should be what adds it to the page.
	 * 
	 * @param object
	 * @param x
	 * @param y
	 * @param z  - traditionally the height in 3d implementations. In 2d this will be faked by moving "up" in the Y (so we subtractk it from the y as we messure from the top)
	 */
	abstract protected void attachObjectToSceneIMPL(SceneObject object, int x, int y, int z);


	/**
	 * returns the x position of the scene in screen co-ordinates
	 * 
	 * @return
	 */
	public abstract int getCurrentPanelAbsoluteX();

	/**
	 * returns the y position of the scene in screen co-ordinates
	 * 
	 * @return
	 */
	public abstract int getCurrentPanelAbsoluteY();

	/**
	 * returns the external size of the panel containing the scene
	 * 
	 * @return
	 */
	public abstract int getScenePanelSizeX();

	/**
	 * returns the external size of the panel containing the scene
	 * 
	 * @return
	 */
	public abstract int getScenePanelSizeY();


	
	

	/** fires when a scene scroll successfully completes (not a drag!)**/
	protected void firePostScrollActions() {
		
		CommandList actions = this.sceneActions.getActionsForTrigger(TriggerType.OnScrollComplete, null);
				
		if (actions!=null && !actions.isEmpty()){

			ScenesLog("Running post scroll actions: \n"+actions.getCode(),"green");
			InstructionProcessor.processInstructions(actions, "FROM_"+ this.SceneFileName, null);		
		
		}
		
	}
		
	/** 
	 * The keep visible parameters ensures its position is kept within the screen limits, and in the case
	 * of dialogues not overlapping with an existing dialogue box.
	 * NOTE: it does not ensure its visible from a z-index or visibility standpoint.  
	 * @param a SceneObject to position.  Cast this to your own implementation when writing the implementation of this method
	 * **/
	//abstract public void setObjectsPosition(SceneObject object, int x, int y,boolean keepvisible);

	/**
	 * should return true if the specified object that extends SceneObject is placed on this SceneWidget.
	 * might be handled internally eventually.
	 * At the moment implementations of this method probably need a cast to whatever the base extended type of is SceneObject.
	 * I am not sure of a way around this.
	 * In the case of GWT, all the games objects extend SceneObjectVisual which in turn extends SceneObject, so casting to SceneObjectVisual
	 * is always safe. Other implementations should have a similar guaranteed object type which extends SceneObject, but in turn everything else used on a scene extends
	 ***/
	abstract public boolean isObjectInScene(SceneObject object);

	/**
	 * 
	 * @param duration - duration of shake
	 * @param distance - distance of shake
	 */
	abstract public void shakeView(int duration,int distance);

	/**
	 * should set the view to the current x and y position, assuming its within the allowed limits
	 * -1,-1 should centralize 
	 *
	 * the following should be updated after any change too;
		currentState.PosX = centerX;
		currentState.PosY = centerY;

	 * TODO: More of the SceneWidgetVisual code for this could be handled here to make implementing this easier
	 * 
	 * 
	 * @param xpos
	 * @param ypos
	 * @param ignoreMovementDisabled - if true this should set the position even if movement is disabled
	 * 
	 * @return Should return the corrected xpos and ypos if they had to be changed in order for the new view position to be safe. Else returns the requested xpos and ypos back
	 */
	abstract public Simple2DPoint setViewPosition_implementation(int xpos, int ypos, boolean ignoreMovementDisabled);

	public void setViewPosition(int centerX, int centerY, boolean ignoreMovementDisabled){

		//if -1,-1 is requested this is taken to mean centralize the scene. 
		//Real co-ordinates can only ever be positive as they refer to where the screen *center* should go, thus using -1,-1 to represent center is safe
		if (centerX==-1 && centerY==-1){
			centerX = scenesData.getCenterOfMovementLimitsX();
			centerY = scenesData.getCenterOfMovementLimitsY();
		}

		Simple2DPoint correctedPosition = setViewPosition_implementation(centerX, centerY, ignoreMovementDisabled);

		currentState.PosX = correctedPosition.x;
		currentState.PosY = correctedPosition.y;

		//update to the returned position when		
		//currentState.PosX = centerX;
		//currentState.PosY = centerY;

	}




	/**
	 * optional - this will log changes from one scene to another.
	 */
	//abstract public void addToGlobalGameLog(String message);

	/**
	 * If you are using a collision map visualiser, this is where it should be setup and attached to the scene
	 * This method will be called after the scene loading
	 */
	protected abstract void setupCmapVisualiser();




	/**
	 * Scrolls the scene to the position at the pixel speed specified
	 * 
	 * @param centerX
	 * @param centerY
	 * @param speed
	 * @param overrideMOVEMENTDISABLED

	 * @return  should return the corrected position of the scrolls end. (assuming it needed to be corrected. Else we just return the requested end)
	 */
	abstract public Simple2DPoint scrollViewToPositionImplemenation(int centerX, int centerY,int duration, boolean overrideMOVEMENTDISABLED);

	/** 
	 * Scrolls the scene to the position at the default speed 
	 ***/
	public void scrollViewToPosition(int centerX, int centerY,int duration, boolean overrideMOVEMENTDISABLED) {

		Simple2DPoint correctedPosition= scrollViewToPositionImplemenation( centerX,  centerY, duration,  overrideMOVEMENTDISABLED);

		//currentState.PosX = centerX; // We update straight away to the requested x/y, we dont yet autoupdate the scrolls position as it moves
		//	currentState.PosY = centerY;

		currentState.PosX = correctedPosition.x;
		currentState.PosY = correctedPosition.y;

		Log.info("correctedPosition:"+correctedPosition);
		
	}


	/** 
	 * Scrolls the scene to the position at the default duration 
	 ***/
	public void scrollViewToPosition(int centerX, int centerY, boolean overrideMOVEMENTDISABLED) {

		this.scrollViewToPosition( centerX,  centerY, 750,  overrideMOVEMENTDISABLED);
		//currentState.PosX = centerX; // We update straight away to the requested x/y, we dont yet autoupdate the scrolls position as it moves
		//currentState.PosY = centerY;

	}






	public MovementPath getSafePath(int dx, int dy, SceneObject object) {

		if (!scenesCmap.isPresent() || object.getObjectsCurrentState().boundaryType.getCollisionType()==CollisionType.none) {
			Log.info("collision map not set  or object has collisions set to none, so we assume we can go in a straight line");			
			MovementPath np = new MovementPath("", "_internal_");	
			np.add(new MovementWaypoint(object.getX(), object.getY(),MovementType.AbsoluteMove)); //	
			np.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo));	
			return np;
		}

		return scenesCmap.get().getSafePath(dx, dy, object);
	}
	/*
	public boolean TestForObjectCollision(int tlx, int tly, int brx, int bry) {

		if (scenesCmap == null) {
			Log.info("cmap not set");
			return false;
		}

		// temp box is just the size, in future we will have this stored

		if (scenesCmap.isBoxColliding(tlx, tly, brx, bry, false) != null) {
			return true;
		}

		return false;

	}

	public boolean TestForCollision(int x, int y) {

		if (scenesCmap == null) {
			Log.info("cmap not set");
			return false;
		}

		boolean bol = false;

		if (scenesCmap.isPointColliding(x, y) != null) {

			bol = true;

		}

		return bol;

	}*/

	/** Pauses all currently running sounds on the scene **/
	public void pauseAllSceneSounds() {


		ScenesLog("Pauseing all sounds on scene","orange");
		
		
		//loops over all scenes objects pausing their sounds		
		//remember which are paused
		Collection<? extends SceneObject> sceneobjects = SceneObjectDatabase.getAllObjectsOnScene(this);

		for (IsSceneObject sprite : sceneobjects) {		

			sprite.stopObjectsSounds();

		}


	}

	public void setViewPosition(int centerX, int centerY) {
		// temp
		setViewPosition(centerX, centerY,false);

	}

	//TODO: implement saveSceneState to mirror below
	//It should save the tempState, then run over all the current objects saving their temp state
	//

	/** restoring all the objects on this scene **/
	public void loadSceneTempState() {

		if (tempState==null){

			ScenesLog("Load temp state requested on scene but temp state is null", "RED");

			return;
		}

		Log.info("restoring scene");

		// remove overlays
		this.setStaticSceneOverlay("OFF");
		this.setDynamicSceneOverlay("OFF");

		Log.info("restoring scene objects");

		// loop over and restore all objects

		Iterator<? extends SceneObject> objectIT = getScenesData().allScenesCurrentObjects().iterator();

		while (objectIT.hasNext()) {


			SceneObject sceneObject = objectIT.next();

			Log.info("___________restoring object:"
					+ sceneObject.getObjectsCurrentState().ObjectsName);

			sceneObject.restoreTempState();

		}
		//


		setToSceneState(tempState);

		/*
		currentState.NumOfTimesPlayerHasBeenHere = tempState.NumOfTimesPlayerHasBeenHere;
		currentState.hasNotBeenCurrentYet = tempState.hasNotBeenCurrentYet;
		currentState.currentBackground = tempState.currentBackground;
		currentState.DynamicOverlayCSS = tempState.DynamicOverlayCSS;
		setDynamicSceneOverlay(currentState.DynamicOverlayCSS);
		currentState.StaticOverlayCSS = tempState.StaticOverlayCSS;
		setStaticSceneOverlay(currentState.StaticOverlayCSS);

		sceneBackground.setDraggableBackground(currentState.currentBackground);*/


	}

	protected void setToSceneState(SceneStatus setToThisState) {

		ScenesLog("Setting to supplied scene state:"+setToThisState.seralise(),"green");

		currentState.NumOfTimesPlayerHasBeenHere = setToThisState.NumOfTimesPlayerHasBeenHere;
		currentState.hasNotBeenCurrentYet = setToThisState.hasNotBeenCurrentYet;

		currentState.currentBackground = setToThisState.currentBackground;

		//	sceneBackground.setDraggableBackground(currentState.currentBackground);
		setBackgroundUrlImplementation(currentState.currentBackground);

		currentState.DynamicOverlayCSS = setToThisState.DynamicOverlayCSS;
		setDynamicSceneOverlay(currentState.DynamicOverlayCSS);

		currentState.StaticOverlayCSS = setToThisState.StaticOverlayCSS;
		setStaticSceneOverlay(currentState.StaticOverlayCSS);

		currentState.PosX = setToThisState.PosX;
		currentState.PosY = setToThisState.PosY;
		
		currentState.dependantSceneNames = new HashSet<String>(setToThisState.dependantSceneNames);
		
		Log.info("resetting scene to the position:"+setToThisState.PosX+","+setToThisState.PosY);
		//set scene position to match file

		//if (setToThisState.PosX==-1 && setToThisState.PosY==-1){
		//default to the center (-1,-1 represents default)
		//	setViewToCenter(true);
		//} else {
		//set to position in state
		setViewPosition(setToThisState.PosX,setToThisState.PosY ,true); //will centralize if co-ordinates are -1 -1 anyway
		//}
		//sceneBackground.setViewToPos(currentState.PosX, currentState.PosY,true);

		ScenesLog("(done)");

	}

	/**
	 * dont change returned object!use it read only!
	 * @return
	 */
	public SceneStatus getSceneStatus(){
		return currentState;
	}
	
	public void onSceneNoLongerCurrent() {
		//test for any actions to run when this scene is no longer to the front
		//if (thisWidget!=null){
		
		//Log.severe("onSceneNoLongerCurrent "+this.SceneFileName);
		
		pauseAllSceneSounds();

		testForSceneActions(TriggerType.SceneToBackActions, null);

		//}
	}

	/** resumes all currently running sounds on the scene.
	 * If the scene is still loading it will do nothing, as object data is required for it to work **/
	public void resumeAllSceneSounds() {

		//ignore if scene not loaded
		if (this.Loading || this.PreparingLoading){
			Log.info("scene still loading, so we wont resume sounds");
			return;
		}
		
		ScenesLog("Resuming all sounds on scene","orange");
		

		//loops over all scenes objects resuming their sounds		
		Collection<? extends IsSceneObject> objects = SceneObjectDatabase.getAllObjectsOnScene(this);		
		if (objects!=null){
			for (IsSceneObject object : objects) {		
				object.resumeObjectsSounds();
			}
		} else {
			Log.severe("ERROR resuming sounds on scene "+this.SceneFileName+" but there was no objects returned");

		}

	}

	public void setBackgroundUrl(String url) {
		currentState.currentBackground = this.SceneFolderLocation + "/" + url;
		setBackgroundUrlImplementation(currentState.currentBackground);
		//sceneBackground.setDraggableBackground(currentState.currentBackground);
	}

	/**
	 * should set the background of the Scene.
	 * @param currentProperty
	 */
	abstract public void setBackgroundUrlImplementation(String currentProperty);

	/**
	 * should Set a overlay image on the scene that does not effect clicks
	 * or rollovers
	 * 
	 * in html this can be done with Pointer-events:none on the css
	 * 
	 * @param CSS
	 */
	abstract public void setDynamicSceneOverlay(String CSS);

	/**
	 * should set a overlay image on the scene that blocks clicks and rollovers 
	 *
	 * @param CSS
	 */
	abstract public void setStaticSceneOverlay(String CSS);



	/**
	 * you can optionally implement a logger for scene events, this will fire to update it
	 * if you dont want one, just implement a empty method
	 * @param logthis
	 */
	abstract public void ScenesLog(String logthis);
	/**
	 * you can optionally implement a logger for scene events, this will fire to update it
	 * if you dont want one, just implement a empty method
	 * @param logthis
	 */
	abstract public void ScenesLog(String logthis, String colour);


	public void addToGlobalGameLog(String message){

		GameStatistics.addLastCommandToStack(message);


	}
	/**
	 * should enable or disable x/y movement of the scene, including movement
	 * caused by dragging of the mouse or touch
	 * 
	 * @param xMovement
	 * @param yMovement
	 */
	public void setScroll(boolean xMovement, boolean yMovement){

		//Log.severe("Settting scroll on drag:"+xMovement+","+yMovement);
		
		//logical
		scenesData.PanX = xMovement;
		scenesData.PanY = yMovement;
		
		//physical
		setScroll_imp(xMovement,yMovement);
		
	}

	/**
	 * should enable or disable x/y movement of the scene, including movement
	 * caused by dragging of the mouse or touch
	 * 
	 * @param xMovement
	 * @param yMovement
	 */
	abstract public void setScroll_imp(boolean xMovement, boolean yMovement);

	/** 
	 * Tests if the scene is being shown for the first time
	 * This should not run while loading, or while the scene is not visible.
	 * Therefor debut should be checked both when "turning" to the scenes tab or
	 * after a scene has finished loading.
	 ***/
	protected void testForSceneDebut() {

		Log.info("scene "+SceneFileName+" loading="+Loading+" PreparingLoading="+PreparingLoading);

		if (currentState.hasNotBeenCurrentYet && Loading == false && PreparingLoading == false ){

			Log.info("scene "+SceneFileName+" made current for first time");
			//	GameDataBox.addLastCommandToStack("___GONE TO SCENE:"+thisWidget.SceneFileName+" FOR FIRST TIME");
			addToGlobalGameLog("___aGONE TO SCENE:"+SceneFileName+" FOR FIRST TIME");

			testForSceneActions(TriggerType.SceneDebut, null);

			currentState.hasNotBeenCurrentYet=false;	
		} else {
			Log.info("scene "+SceneFileName+" has already been current or still loading");

			//add a message to the last commands run box making it clear we are on a new scene
			//	GameDataBox.addLastCommandToStack("___GONE TO SCENE:"+thisWidget.SceneFileName);
			addToGlobalGameLog("___bGONE TO SCENE:"+SceneFileName);

		}
	}

	/**stuff to run when the scene is brought to the front**/
	public void onSceneMadeCurrent() {

		//TODO: why is this sometimes triggering twice????
		//LOE&H intro screen does this

		ScenesLog("onSceneMadeCurrent");

		//test for any actions to run when this scene is brought to the front
		//if (thisWidget!=null){

		resumeAllSceneSounds();

		boolean hadActions = testForSceneActions(TriggerType.SceneToFrontActions, null);

		ScenesLog("SceneToFrontActions ran:"+hadActions,"green");


		testForSceneDebut();

		//}




	}

	/** 
	 * Resets all the objects on this scene and the scenes settings.
	 * It also stops any NamedActionSet timers that were created while on this scene
	 * 
	 * Basically acts like the scene was loaded for the first time 
	 * **/
	public void resetScene() {


		Log.info("resetting scene :"+this.SceneFolderLocation);
		ScenesLog("resetting scene :"+this.SceneFolderLocation);

		// remove overlays
		this.setStaticSceneOverlay("OFF");
		this.setDynamicSceneOverlay("OFF");

		//stop timers		
		JAMTimerController.stopAllTimersCreatedFromScene(this);

		// remove all dynamically created objects (as the save string currently
		// stores the difference since the default/file scene state)
		InstructionProcessor.DisableInstructions = true;
		Log.info("InstructionProcessor Disabled");
		for (SceneObject dynamicObjects : AllDynamicObjects) {
			Log.info("During Dynamic Object Remove Loop - Removing: "+dynamicObjects.getName());
			dynamicObjects.removeObject();
		}

		Log.info("removed dynamic objects : "+AllDynamicObjects.size());

		// erase dynamic list (it will be recreated below)
		AllDynamicObjects.clear();

		// reset scene specific states
		// set default background
		//currentState.currentBackground = scenesData.defaultBackground;
		//sceneBackground.setDraggableBackground(currentState.currentBackground);


		//update the scene states!

		Log.info("resetting scene state");

		setToSceneState(defaultState);

		Log.info("resetting scene objects");

		// loop over and reset all objects

		Iterator<SceneObject> objectIT = getScenesData().scenesOriginalObjects.iterator();


		Log.info("total objects = "+getScenesData().scenesOriginalObjects.size());


		while (objectIT.hasNext()) {

			SceneObject sceneObject = objectIT.next();


			Log.info("___________resetting object:"+ sceneObject.getObjectsCurrentState().ObjectsName);


			//as we are resetting we also remove the objects list of things positioned relative too it
			//this is because if a object is positioned relative to something, it will add itself to the parent.
			//thus it will be recreated by other objects as we loop

			sceneObject.relativeObjects.clear();

			sceneObject.resetObject(true,false); //reset without running onload



		}

		InstructionProcessor.DisableInstructions = false;

		//now run all the onloads separately

		Log.info("________________firing onLogicalLoadCompleteForAllObjectsInScene");
		
		 objectIT = getScenesData().scenesOriginalObjects.iterator(); //get a new iterator as we are looping again

		while (objectIT.hasNext()) {
			SceneObject sceneObject = objectIT.next();
			sceneObject.alreadyLoaded=false;
			sceneObject.onLogicalLoadCompleteForAllObjectsInScene();  //now we run onload separately
		}



		Log.info("________________firing onSceneMadeCurrent ");
		//finally we re-run the scenes onLoad stuff if this scene is already the current one
		if (SceneObjectDatabase.currentScene == this){
			onSceneMadeCurrent();
		}

	}

	/** gets the scenes current pan location (NOTE: if mid-pan this will not necessarily match the visual position) */
	public int getPosY() {
		return currentState.PosY;
	}

	/** gets the scenes current pan location (NOTE: if mid-pan this will not necessarily match the visual position)*/
	public int getPosX() {
		return currentState.PosX;
	}

	/**
	 * toggles the visibility of the collision map
	 */
	public void togglePath() {
		if (scenesCmap.isPresent()){
			scenesCmap.get().showPath(!scenesCmap.get().isPathVisible());			
		}				
	}

	public void toggleCmap() {
		if (scenesCmap.isPresent()){
			scenesCmap.get().showCmap(!scenesCmap.get().isCmapVisible());			
		}	
	}




	/**
 	* A scene is fully loaded once all its objects are fully loaded AND any pending state settings are loaded.
	* This however, does exclude RunNamedActions being triggered, which happens after the fully loaded state is set for ALL scenes. (future:ideally we could move it to "logically loaded" for all scenes to be a bit quicker?)
    **/
	public boolean isSceneFullyLoaded() {
		
		//loading needs to be false and StateToLoad needs to be null
		if (Loading == false && StateToLoad==false ){
			return true;
		} else {
			return false;
		}
		
	}

//



	/** Work in progress;
	 * Doesn't yet support dynamic creation of non-sprite objects **/
	protected void loadSceneStateInternal(SceneStatus sceneState, SceneObjectState[] objectsCurrentState) {

		Log.info("loading SceneState from data supplied:"+sceneState.seralise());
		ScenesLog("loading scene state.");

		Log.info("restoring scene object from datas");
		StateToLoad = false;
		sceneStateToLoad = null;
		objectsCurrentStateToLoad = null;

		this.setStaticSceneOverlay("OFF");
		this.setDynamicSceneOverlay("OFF");


		// remove all dynamically created objects (as the save string currently
		// stores the difference since the default/file scene state)
		for (SceneObject dynamicObjects : AllDynamicObjects) {
			dynamicObjects.removeObject();

		}

		Log.info("removed dynamic objects");

		// erase dynamic list (it will be recreated below)
		AllDynamicObjects.clear();

		// loop over and restore all objects

		Log.info("Updating "+objectsCurrentState.length+" objects");
		ScenesLog("Updating "+objectsCurrentState.length+" objects");

		for (SceneObjectState sceneObjectData : objectsCurrentState) {

			String objectName = sceneObjectData.ObjectsName;

			Log.info("Updating object named:"+objectName+" Type:"+sceneObjectData.getPrimaryObjectType());

			//NOTE: This only works if all names are unique, regardless of scene
			//SceneObject objectsWithName[] = SceneWidget.getSceneObjectByName(objectName, null);

			//get all the objects that match name. This really should only be one
			//if more the one found we use the first found of the correct type
			//Set<? extends SceneObject> objectsWithName =  SceneObjectDatabase
			//			.getSceneObjectNEW(objectName,null,true);


			//This new function gets a object of the right type straight away, with no extra checking or searching needed
			SceneObject object = SceneObjectDatabase.getSingleObjectOfType(objectName,null,true,sceneObjectData.getPrimaryObjectType());

			/*
			SceneObject firstobject = objectsWithName.iterator().next();//get first object arbitarily
			SceneObject object = firstobject; //first chosen in the iterator by default (no garentied order)

			if (objectsWithName.size()>1 && firstobject.objectsCurrentState.currentType != sceneObjectData.currentType){

				Log.info("More then one Object found but first one is wrong type, searching others ");
				for (SceneObject objectWithName : objectsWithName) {

					//if the object has a type that matchs the one we are trying to load we assume its the correct object.
					//So, in other words; Same name. Same type  =  assume its the correct object. (we cant test its social security number unfortunately)
					if (objectWithName.objectsCurrentState.currentType == sceneObjectData.currentType){
						object = objectWithName;
					}

				}


			} else if (objectsWithName.size()>1) {
				Log.info("more then one object found with name, updating only the first");
				object = firstobject;
			} else {
				object = firstobject;
			}

			 */



			Log.info("(searched for object)");

			//this.getSpriteObjectInSceneByName(objectName); // only
			// update
			// the
			// first,
			// multi-selection/update
			// not
			// going
			// to
			// be
			// implemented

			// if the object doesn't exist yet, we need to create it.
			// we also create clones, as its intended for there to be many copys
			// of them
			if (	   object == null
					|| object.getObjectsCurrentState().clonedFrom!=null
					|| object.getObjectsCurrentState().clonedFromOnceLoaded.length()>1)

			{
				//(object.objectsCurrentState.ObjectsName
				//.equalsIgnoreCase("_clone_")
				//used to be used instead to determain a cloned object, but this was bad as we
				//cant garenty its named that way
				//the _clone_ object name is almost certainly wrong above, I think it should be endswith _clone instead

				//we now check the objects data to see if it was a clone itself

				Log.info("__________________________object doesnt exist or is clone:"+ sceneObjectData.ObjectsName+"");

				//temp debugs;----------------------------
				if (object!=null){
					if (object.getObjectsCurrentState().clonedFrom!=null){
						Log.info("__________________________object.objectsCurrentState.clonedFrom:"+object.getObjectsCurrentState().clonedFrom);
					}
					if (object.getObjectsCurrentState().clonedFromOnceLoaded!=null){
						Log.info("__________________________object.objectsCurrentState.clonedFromOnceLoaded:"+object.getObjectsCurrentState().clonedFromOnceLoaded);
					}
					Log.info("object is null:"+sceneObjectData.ObjectsName);
				}
				//----------------------------------------


				//NOTE: Might be possible to generalize this as it doesnt seem sprite specific
				//if (sceneObjectData.getCurrentPrimaryType() == SceneObjectType.Sprite) {

				// for non-cloned new objects assume no triggers
				ActionList actions = null;

				// for cloned objects, which should inherit the action sets
				// of their parents
				if (sceneObjectData.clonedFrom != null) {

					Log.info("loading actions from parent ");

					actions = sceneObjectData.clonedFrom.objectsActions;

					Log.info(actions.size() + " actions loaded");

				} else {

					//if its cloned from an object that didn't use to exist, we check for that too
					//as it might now exist
					if (sceneObjectData.clonedFromOnceLoaded != null && !sceneObjectData.clonedFromOnceLoaded.isEmpty()) {

						String cloneFromName = sceneObjectData.clonedFromOnceLoaded;
						Log.info("loading actions from parent ("+cloneFromName+")");


						Set<? extends SceneObject> clonedFromSet = SceneObjectDatabase.getSceneObjectNEW(cloneFromName,null,true);

						//we need just one element from this set, as we can only send the actions back to one object
						//first we warn if we have more then one object
						if (clonedFromSet.size()>1){
							Log.info("Warning: more then one object found for "+cloneFromName+". clonedFromOnceLoaded should only have one specific object");
						}
						//we just use the first object the iterator returns (we cant use .get(0) as Sets have no order and thus no numbered positions!)
						///	sceneObjectData.clonedFrom = SceneWidget.getSpriteObjectByName(cloneFromName,null)[0];
						sceneObjectData.clonedFrom = clonedFromSet.iterator().next(); //get any object

						if (sceneObjectData.clonedFrom!=null){
							actions = sceneObjectData.clonedFrom.objectsActions;

							Log.info(actions.size() + " actions loaded");
						}

					}
				}


				//now we have the data we need, we recreate the object based on its type
				SceneObject newObject = SceneObjectFactory.createObjectFromExistingData(sceneObjectData, actions, this); //this also runs update state, which sets the scene
				//this seems unideal as the state is set twice effectively.
				//If we could ensure all physical attributes are set without resetting state it would be more efficiant.
				newObject.initCurrentState(false);
				
				//---
				newObject.alreadyLoaded = true;


				//add to the scenes lists
				getScenesData().addToScenesObjects(newObject);

				//DynamicObjects also needs to be changed to just use SceneObject, not SceneObject visual
				AllDynamicObjects.add(newObject);

				// add to relative object if set
				if (newObject.getObjectsCurrentState().positionedRelativeToo != null) {

					newObject.getObjectsCurrentState().positionedRelativeToo.addChild(newObject); //relativeObjects.add(newObject);

				}
				

				Log.info("object created:" + newObject.initialObjectState.ObjectsName + " at-" + newObject.getObjectsCurrentState().X + "," + newObject.getObjectsCurrentState().Y);
				Log.info("with zindex:" + newObject.getObjectsCurrentState().zindex);
				Log.info("from data with zindex:"+ newObject.getObjectsCurrentState().zindex);

				// currently the objects are auto added if created from
				// state
				// addObjectToScene(newObject);
				//} else {
				//	Log.severe("dynamic recreation of non-sprite objects not yet supported");

				//}

				// repeat for the dynamic creation of other types


				//go to next object to process
				continue;
			}

			Log.info("(object already exists:"+object.getObjectsCurrentState().ObjectsName+")");
			Log.info("(object is type::"+object.getObjectsCurrentState().getPrimaryObjectType().toString()+")");
			Log.info("(data is type::"+sceneObjectData.getPrimaryObjectType().toString()+")");

			
			//Objects now update their state based on their type internally
			object.ObjectsLog("Updating objects state due to scenestate loading","blue");
			object.updateState(sceneObjectData);

		}



		//set other scene data
		setToSceneState(sceneState);

		/*
		currentState.NumOfTimesPlayerHasBeenHere = sceneState.NumOfTimesPlayerHasBeenHere;
		currentState.hasNotBeenCurrentYet = sceneState.hasNotBeenCurrentYet;
		currentState.currentBackground = sceneState.currentBackground;
		sceneBackground.setDraggableBackground(currentState.currentBackground);
		currentState.DynamicOverlayCSS = sceneState.DynamicOverlayCSS;
		setDynamicSceneOverlay(currentState.DynamicOverlayCSS);
		currentState.StaticOverlayCSS = sceneState.StaticOverlayCSS;
		setStaticSceneOverlay(currentState.StaticOverlayCSS);*/

	}



	/**
	 * This mess of a named function runs a few checks that have to be done after all the objects are on the page
	 * with their data loaded.
	 * Relative objects waiting to be positioned, and cloned objects waiting for their sources data are dealt with here
	 * Then touching collision maps are checked
	 */
	protected void recheckRelativeandSpawnedObjectsThenUpdateTouchingData() {

		//Iterator<SceneObjectVisual> oit = getScenesData().getAllScenesCurrentObjects().iterator(); //(replacement)

		Set<SceneObject> oit = this.getScenesData().allScenesCurrentObjects();


		for (SceneObject so : oit) {


			//	}

			//what? why is this ALL sprites? surely it should be all scene objects?
			//Iterator<SceneSpriteObject> oit = all_sprite_objects.values().iterator();


			//while (oit.hasNext()) {

			//SceneObject so = oit
			//		.next();

			so.recheckRelativePositioningSetup(); //recheckRelativePositioningSetup 

			//dont we need clonedfrom once loaded too?

			if (so.getObjectsCurrentState().spawningObjectFromOnceLoaded.length()>1){

				String objectname  = so.getObjectsCurrentState().spawningObjectFromOnceLoaded;

				//try to find its spawning object from the name
				//SceneSpriteObject spawnedFrom = SceneWidget.getSpriteObjectByName(objectname,null,false)[0];

				Set<? extends SceneObject> spawnedFromObj = SceneObjectDatabase.getSceneObjectNEW(objectname,null,true); //used to be sprite only


				if (spawnedFromObj.size() > 1){
					Log.info("spawnedFromObj name is ambigious, refers to more then one object "+objectname);
				}

				SceneObject spawnedFrom = spawnedFromObj.iterator().next();

				if (spawnedFrom!=null){	

					so.getObjectsCurrentState().spawningObject=spawnedFrom;
					//remove the name
					so.getObjectsCurrentState().spawningObjectFromOnceLoaded="";
				}

			}

			//probably should be repeated for cloned objects too, with nearly the same commands as spawned objects




		}

		//Finally we recheck the touching data now everything is guaranteed loaded.		
		ScenesLog("(now updating touching data on all "+oit.size()+" objects)");

		for (SceneObject so : oit) {
			so.getObjectsCurrentState().touching.cacheObjectSet(); //needs to be run before the touching variable will work

			if (       so.getObjectsCurrentState().ObjectsTouchingUpdateMode == touchingMode.Automatic
					&& so.getObjectsCurrentState().boundaryType.getCollisionType() != CollisionType.none )
			{
				so.ObjectsLog("(auto updated touching data after load)","blue");
				so.updateTouchingAutomatically(); 

			}

		}



	}



	/**
	 * Loads the data in scene state to the current scene, and then updates all
	 * the objects to their new states
	 **/
	public void loadSceneState(SceneStatus sceneState, SceneObjectState[] objectsCurrentState) {


		// check if dependences are loaded (that is, scenes this scene state depends upon)
		// Scene dependences typically happen if the new scene data has a object that originated in a different scene
		boolean dependancysLoaded = sceneState.allDependancysLoaded();

		// We then have to store the data and wait (see below)
		// We also need to make a function so that if ANY scene loads it checks if any scenes were waiting for it.


		// delay till scene is loaded and attached
		if (Loading || dependancysLoaded==false) {

			Log.info("waiting till loaded scene to update data:"+sceneState.SceneName);
			Log.info("dependancysLoaded:"+dependancysLoaded);
			ScenesLog("waiting till loaded scene to update data");
			ScenesLog("dependancysLoaded:"+dependancysLoaded);
			// delay a bit till loaded
			StateToLoad = true;
			sceneStateToLoad = sceneState;
			objectsCurrentStateToLoad = objectsCurrentState;

		} else {
			Log.info("_______________________<<<<<________________restoring scene "+this.getScenesData().SceneFolderName+" from data");
			
			ScenesLog("dependancysLoaded:"+dependancysLoaded);			
			ScenesLog("sceneloaded now loading its state","blue");
			

			loadSceneStateInternal(sceneState, objectsCurrentState);

			Log.info("rechecking all relatively positioned objects to make sure they are linked and correctly positioned");

			ScenesLog("rechecking all relatively positioned objects to make sure they are linked and correctly positioned");

			recheckRelativeandSpawnedObjectsThenUpdateTouchingData();
			//same for dialogues? <<what was this? (probably the above only works on sprite objects!? fix it!) 18-3-16 It's already fixed, yo
		}

	}



	/**
	 * gets all the scenes objects data, this function is used as part of the save game generation
	 * 
	 * possible optimization; we could grab and store the serialized state strings here, instead of collecting object data first and then getting
	 * the serialized strings of it in getSceneSaveString?
	 * 
	 * @param excludeUnchanged
	 * @return
	 */
	public SceneObjectState[] getAllSceneObjectData(boolean excludeUnchanged) {

		Log.info("saving scene object states for scene:"+getScenesData().SceneFolderName);

		// loop over and save all objects

		//get an upto date list of all objects on this scene
		//Note; Is this correct? Objects imported from other scenes should be loaded with those scenes
		//else how will they get the correct initial data (as well as actions etc) from their source files
		//ArrayList<SceneObjectVisual> allSceneObjects = getScenesData().getAllScenesCurrentObjects();

		Set<SceneObject> allSceneObjects = this.getScenesData().allScenesCurrentObjects();



		//create an array for all the state data
		SceneObjectState allSData[] = new SceneObjectState[allSceneObjects.size()]; //this is the max size, might end up smaller if excludeUnchanged is set to true

		//SceneSpriteObjectState allSData[] = new SceneSpriteObjectState[scenesData.scenesSpriteObjects
		//     .size()];
		//an iterator to loop over them
		Iterator<? extends SceneObject> objectIT = allSceneObjects.iterator();



		//Iterator<SceneSpriteObject> objectIT = scenesData.scenesSpriteObjects
		//	.iterator();

		int i = 0;
		while (objectIT.hasNext()) {

			SceneObject sceneObject = objectIT.next();



			Log.info("___________saving object state:"
					+ sceneObject.getObjectsCurrentState().ObjectsName);

			// update the animation serialization if its a sprite
			if (sceneObject.getObjectsCurrentState().getPrimaryObjectType()==SceneObjectType.Sprite)
			{

				//as we know its a sprite from the above IF, we use typecasting to 
				//get it as a sprite object for the purpose's of trigger its sprite-specific functions.
				IsSceneSpriteObject sceneObjectAsSprite = (IsSceneSpriteObject)sceneObject;

				//ensure the state is SpriteObjectState is sycned to the state of the animation
				//we dont need the values, but these functions also sycn it internally which is what we need

				sceneObjectAsSprite.getSerialisedAnimationState();
				sceneObjectAsSprite.getCurrentFrame();

				//
				/*
				//if its animating we need to ensure its data is uptodate (ie, what frame its on, what direction the animation is going etc)
				if (!sceneObjectAsSprite.SceneObjectIcon.isAnimating()) {

					sceneObjectAsSprite.getObjectsCurrentState().currentlyAnimationState = "";

				} else {
					sceneObjectAsSprite.getObjectsCurrentState().currentlyAnimationState = sceneObjectAsSprite.SceneObjectIcon.serialiseAnimationState();
				}

				//update current frame to ensure it matches the icon
				sceneObjectAsSprite.getObjectsCurrentState().currentFrame = sceneObjectAsSprite.SceneObjectIcon.currentframe;
				 */
				//make sure data is fresh (? why is this here given the above if?)
				//sceneObjectAsSprite.currentObjectState.currentlyAnimationState = sceneObjectAsSprite.SceneObjectIcon.serialiseAnimationState();

			}


			//check if its changed or not
			if (excludeUnchanged)
			{

				//String currentStateAsString = sceneObject.objectsCurrentState.serialiseToString();
				//Log.info("done serialising current state");		

				//String initialStateAsString = sceneObject.objectsInitialState.serialiseToString();
				//Log.info("done serialising initial state");
				Log.info("null checks:");
				if (sceneObject.getObjectsCurrentState() == null){
					Log.info("current state null");
				}
				if (sceneObject.getInitialState() == null){
					Log.info("initial state null");
				}

				if (sceneObject.getObjectsCurrentState().clonedFrom == null  && 
						sceneObject.getObjectsCurrentState().sameStateAs(sceneObject.getInitialState())){					

					Log.info("Skipping object as its unchanged:"+sceneObject.getObjectsCurrentState().ObjectsName);
					sceneObject.ObjectsLog("Object Is Unchanged, so not saving",  "red"); 

					continue;//skip it as its the same
				}

			}


			allSData[i] = sceneObject.getObjectsCurrentState(); // 

			//sceneObject.saveTempState(); //might not be needed? (this is more for temp save/restore points)

			if (allSData[i] == null) {
				Log.info("failed to save:"
						+ sceneObject.getObjectsCurrentState().ObjectsName);

				sceneObject.ObjectsLog("Failed to save", "RED");
			}
			i++;

		}
		return allSData;
	}



	public void fetchCollisionData(final String SceneFileName) {


		final String SceneItemFileLocation = SceneFileRoot + SceneFileName
				+ "/" + SceneFileName + "_vmap.ini";

		
		Log.info("________getting cmap at:"+SceneItemFileLocation);


		FileCallbackRunnable onResponse = new FileCallbackRunnable(){

			@Override
			public void run(String responseData, int responseCode) {
				
				Log.info("Retrieved cmap data."+responseCode);

				// check response is not an error
				if (responseCode >= 400 || responseCode == 204) {
					Log.info("________no cmap file recieved ("+responseCode+"):\n");
					retrievedSceneCollisionFileData = Optional.absent();
					parseSceneFilesIfReady();
					return;
				}

				retrievedSceneCollisionFileData = Optional.of(responseData);

				parseSceneFilesIfReady();
			}
		};


		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {

				Log.info("error or "+SceneFileName+" has no cmap at "+SceneItemFileLocation+"   Throwable:"+ errorData);
				Log.info("had cmap "+scenesCmap.isPresent());

				scenesCmap = Optional.absent();
				retrievedSceneCollisionFileData = Optional.absent();

				parseSceneFilesIfReady();
			}

		};


		//using the above, try to get the text!
		RequiredImplementations.getFileManager().getText(SceneItemFileLocation,true,
				onResponse,
				onError,
				false);



	}



	public static boolean runForSceneActions(SceneWidget scene, TriggerType type, String Parameter, SceneObject sourceObject){

		ActionList sceneActions = scene.sceneActions;

		// temp test
		if (sceneActions == null) {
			//	GameDataBox.addLastCommandToStack("(No scene actions found for "+this.SceneFileName+")");
			scene.ScenesLog("(scene sceneActions is null! maybe not loaded yet? ");

			Log.info("scene sceneActions is null! Thats really mega wrong :(");
			return false;
		}

		CommandList actions = sceneActions.getActionsForTrigger(type,Parameter);

		if (actions.size() > 0) {

			Log.info("Scene actions found:" + actions+" trigger was:"+type);

			String sourceObjectName = "NoSourceObject_";

			if (sourceObject!=null){			
				
				sourceObjectName = sourceObject.getName();
				
				if (type==TriggerType.MouseClickActions || type==TriggerType.MouseRightClickActions ){
					sourceObject.wasLastObjectClicked(); //newly added					
				}

				//make sure its the correct sort of interaction
				if ((type==TriggerType.MouseClickActions)
						||(type==TriggerType.MouseRightClickActions)
						||(type==TriggerType.MouseOutActions)
						||(type==TriggerType.MouseOverActions)){

					//if so, update
					sourceObject.wasLastObjectUpdated();
					sourceObject.updateLastClickedLocation();

					if (sourceObject.getParentScene()!=null){
						sourceObjectName = sourceObject.getParentScene().SceneFileName+ "_"+ sourceObject.getName();

					} else {
						sourceObjectName = sourceObject.getName();

					}


				}				
			}


			InstructionProcessor.processInstructions(actions, "FROM_"+ sourceObjectName, sourceObject);			

			return true;

		} else {
			Log.info("no scene actions  for trigger "+type.toString());
		}

		return false;
	}



	public static SceneWidget getSceneByName(String scenename) {

		String rawscenename=scenename.toLowerCase();

		// rather crude, should handle better
		if (scenename.endsWith("__scene")){
			rawscenename=scenename.substring(0, scenename.length()-7);			
		}


		SceneWidget scene = SceneWidget.all_scenes.get(rawscenename);

		if (scene==null){
			Log.info("Scene not present or still loading");			
			Log.info("current number of scenes:"+ SceneWidget.all_scenes.size());			
			Log.info("was looking for:"+ rawscenename);		
			//set to popup when done			
			return null;
		} else {
			Log.info("Scene found");		
		}

		return scene;

	}



	public static Set<String> getAllSceneNames() {
		return SceneWidget.all_scenes.keySet();		
	}



	public static boolean sceneExists(String scenename) {

		String rawscenename=scenename.toLowerCase();
		// rather crude, should handle better
		if (scenename.endsWith("__scene")){
			rawscenename=scenename.substring(0, scenename.length()-7);			
		}	


		return SceneWidget.all_scenes.containsKey(rawscenename);

	}



	/** clears scenes from all lists as well as the current active scene **/
	public static void clearScenes() {
		SceneWidget.all_scenes.clear();
		SceneWidget.currentActiveScene = null;	

	}



	public static Collection<SceneWidget> getAllScenes() {		
		return SceneWidget.all_scenes.values();		
	}



	public static void setActiveScene(SceneWidget scene, boolean silentmode) {

		//used to be currentActiveScene not currentScene
		if (SceneObjectDatabase.currentScene!=scene){

			Log.info("____scene changed too:"+scene.SceneFolderLocation);


			if (SceneObjectDatabase.currentScene!=null){
				SceneObjectDatabase.currentScene.onSceneNoLongerCurrent();
			}
			SceneObjectDatabase.currentScene = scene;		

			if (!silentmode){
				scene.onSceneMadeCurrent();	
			}
			
			scene.SceneActive();
			
			
		} else {

			Log.info("____current active scene is already:"+SceneObjectDatabase.currentScene.SceneFileName);
			//Despite already being current, we re-fire this, as its likely expected
			//that any script commands should be fired again
			//Also, if a scene is already currentActiveScene, but not yet loaded, this ensures the "to front" actions
			//still fire.
			if (!silentmode){				
				scene.onSceneMadeCurrent();		
			}
		}
	}




	/**
	 * Fired when the scene is made current, regardless of if we are loading silently or not.
	 * Only needed for some implementations to override
	 */
	protected void SceneActive() {
		//do nothing		
	}



	/**
	 * This should be fired every time a new scene loads as other scenes might depend upon it to load a pending state
	 * (ie, they are waiting for this scene)
	 * If needed for optimization, we can keep a shortlist of just scenes waiting, rather then all the scenes
	 * and checking if they are waiting
	 */
	public static void checkAllScenesStillWaitingToLoadStates() {
		Log.info("Rechecking all scenes in case they have states to load ");
		boolean allScenesFullyLoaded = true; //we set to false when we find a single one that isnt
		
		for (SceneWidget scenetoCheck : all_scenes.values()) {
			
			//See if its waiting to load
			if (scenetoCheck.StateToLoad){
				Log.info("retesting "+scenetoCheck.SceneFileName+" as its waiting to load a state");
				//tell it to test
				scenetoCheck.loadSceneState(scenetoCheck.sceneStateToLoad, scenetoCheck.objectsCurrentStateToLoad);
			}	

			//---
			if (!scenetoCheck.isSceneFullyLoaded()){
				allScenesFullyLoaded=false;
			} else {				
				scenetoCheck.ScenesLog("scene FullyLoaded with states (checked)","blue");	
				scenetoCheck.onSceneFullyLoaded();
				
			}
			
		}
		
		//we also check if all scenes are finnished
		if (allScenesFullyLoaded){
			Log.info(" <-------------------------------------------allScenesFullyLoaded with states");	
			OnAllScenesFullyLoadedComplete();
		}
		

	}

	
	static String actionSetDataToResume="";
	
	public static void setActionSetDataToResume(String actionSetDataToResume) {
		SceneWidget.actionSetDataToResume = actionSetDataToResume;
	}



	/**
	 * should be fired when all the scenes are fully loaded.
	 * This includes logical and physical loading of all objects, and any states.
	 * It does not, however, include RunNamedAction timers waiting pending to run - those are triggered here
	 * 
	 */
	static public void OnAllScenesFullyLoadedComplete() {
		Log.info(" <--allScenesFullyLoaded with states");	
		//do we have runnable data?
		if (actionSetDataToResume!=null && !actionSetDataToResume.isEmpty() ){
			resumeActionSets(actionSetDataToResume);
			actionSetDataToResume=null;
		}
		
		
	}

	/**
	 * <NamedActionSetStates>
	 * <nas>
	 * 
	 * </nas>	 
	 * </NamedActionSetStates>
	 * 
	 * might move this to the timer core functions?
	 * 
	 * @param actionSetDataToResume
	 */
	private static void resumeActionSets(String actionSetDataToResume) {
		Log.info(" resuming action sets:"+actionSetDataToResume);			
		//deserialise
		//NamedActionSetStates
		int startpoint=actionSetDataToResume.indexOf("<NamedActionSetStates>")+"<NamedActionSetStates>".length(); 
		if (startpoint==-1){
			Log.info(" (error no start point)");
			
		}
		int   endpoint=actionSetDataToResume.indexOf("</NamedActionSetStates>"); 
		if (endpoint==-1){
			Log.info(" (error no endpoint)");
			
		}
		
		String NamedActionSetStates = actionSetDataToResume.substring(startpoint, endpoint);
		Log.info(" NamedActionSetStates:"+NamedActionSetStates);
		int nasstartpoint=actionSetDataToResume.indexOf("<nas>"); 
		//nas
		
		while (nasstartpoint!=-1) {
			int   nasendpoint=actionSetDataToResume.indexOf("</nas>",nasstartpoint+3); 
			
			String stateData = actionSetDataToResume.substring(nasstartpoint+5, nasendpoint);
			Log.info(" stateData:"+stateData);		
			//process state data
			resumeActionSet(stateData);
			//---
			nasstartpoint=actionSetDataToResume.indexOf("<nas>",nasendpoint); 
		}
		
				
		
		
	}
	//scene,object,name345645,567
	private static void resumeActionSet(String seralisedState) {
		
		NamedActionSetTimer.createAndResumeNamedActionState(seralisedState);
		
		
		

	}

	/**
	 * ensure the scene is sized correctly for the viewport
	 * called after scene creation
	 */
	public abstract void resizeScene();


	boolean allFilesLogicallyLoaded = false;

	/**
	 * is all the objects on the files on the scene logically loaded?
	 * 
	 * @return
	 */
	public boolean isAllFilesLogicallyLoaded() {
		return allFilesLogicallyLoaded;
	}


	boolean allFilesPhysicalLoaded = false;

	/**
	 * is all the objects on the files on the scene logically loaded?
	 * 
	 * @return
	 */
	public boolean isAllFilesPhysicalLoaded() {
		return allFilesPhysicalLoaded;
	}

	/**
	 * should be fired when all the objects are logically ready (all data) but their visuals (png/jpg)  might not be loaded yet
	 * 
	 */
	public void OnAllSceneObjectsLogicalLoadComplete() {

		allFilesLogicallyLoaded = true;

		ScenesLog("(fireing logical functions on all objects)");
		Log.info("(fireing logical functions on all objects)");	

		Iterator<SceneObject> sceneobjectit = this.getScenesData().scenesOriginalObjects.iterator();

		//fire load events on all objects
		while (sceneobjectit.hasNext()) {

			SceneObject so = sceneobjectit.next();
			so.onLogicalLoadCompleteForAllObjectsInScene(); //new replacement for the one based on attach

		}


	}


	/**
	 * should be fired when all the objects are logically ready (all data) AND their visuals (png/jpg) AND they are attached	 * 
	 */
	public void OnAllSceneObjectsFullyLoadedComplete() {

		HashSet<SceneObject> scenesOriginalObjects = this.getScenesData().scenesOriginalObjects;
		
		ScenesLog("(fireing onFullyLoadedCompleteForAllObjectsInScene() on all "+scenesOriginalObjects.size()+" objects)");
		Log.info("(fireing onFullyLoadedCompleteForAllObjectsInScene()  on all "+scenesOriginalObjects.size()+" objects)");	

		Iterator<SceneObject> sceneobjectit = scenesOriginalObjects.iterator();

		//fire load events on all objects
		while (sceneobjectit.hasNext()) {

			SceneObject so = sceneobjectit.next();
			so.onFullyLoadedCompleteForAllObjectsInScene(); 

		}


	}





	/**
	 * Should be fired when the load is fully complete and ready to appear OR ready to load its states
	 */
	public void onObjectLoadFullyComplete() {

		this.ScenesLog("<----------FULL LOADING OF ALL SCENE OBJECTS COMPLETE","GREEN");
		Log.info("<----------FULL LOADING OF ALL SCENE OBJECTS COMPLETE__"+this.SceneFileName+"___Scene load complete");
		
		Loading = false;

		final Set<SceneObject> allScenesCurrentObjects = this.getScenesData().allScenesCurrentObjects();
		ScenesLog("Scenes load objects complete. Total Objects:"+allScenesCurrentObjects.size());
		ScenesLog("loading scene objects complete. (Loading=false) (PreparingLoading="+this.PreparingLoading+")", "green");

		//fire the  OnLoadFullyComplete for all objects 
	//	OnAllSceneObjectsFullyLoadedComplete(); //should this fire at all if theres a state pending? probably best to wait till after the state
		
		//if we are already on this scene (and have been watching it load) we check for scene Debut commands
		if (SceneObjectDatabase.currentScene == this){
			
			if (!StateToLoad){
						
			Log.info("testing for scene debut after load");
			ScenesLog("testing for scene debut after visible loading");

			//testForSceneDebut(); //old
			if (!loadingsilently){
				onSceneMadeCurrent(); //new...test all scenes
			}
			
			} else {
				ScenesLog("[not running scene debut or to front actions as we have a state to load]");

			}
		} else {

			//if we are not already to front:

			// check if it should be moved to the front, as long as this was the one we were waiting for
			if (JAMcore.triggerSelectCheck) {
				ScenesLog("Triggered SelectCheck");


				if (JAMcore.pageToSelect.startsWith(SceneFileName.toLowerCase())){

					Log.info("____________________________________________>> setting page too :"		+ JAMcore.pageToSelect + ":");

					
					boolean dontTriggersceneToFrontActions = loadingsilently;
					//also dont trigger scene to front if we are waiting for a state
					if (StateToLoad){
						dontTriggersceneToFrontActions=true;
					}
					InstructionProcessor.bringSceneToFront(JAMcore.pageToSelect,dontTriggersceneToFrontActions);

				}

			}

		}


		//we now check relative and spawned objects as well as update touching data
		//this can only be done after everything is laded
		if (!StateToLoad){
			ScenesLog("recheckRelativeandSpawnedObjectsThenUpdateTouchingData","blue");
			recheckRelativeandSpawnedObjectsThenUpdateTouchingData(); //catchy name eh?
			
			//if there's no state to load then this scene is also fully loaded
			onSceneFullyLoaded();
			
			
		} //we only check if there's no state to load as the state also runs this same check and we only want it run once

		//Note; we now check ALL scenes for scene states to load not just this one.
		//This is because some scenes might have been waiting for this one.
		SceneWidget.checkAllScenesStillWaitingToLoadStates();



	}

	boolean sceneFullyLoadComplete=false;;
	
	
	public void onSceneFullyLoaded() {
		if (!sceneFullyLoadComplete){
			sceneFullyLoadComplete=true;
			ScenesLog("<------ Loading scene complete inc. any states, if any", "blue");
			
			OnAllSceneObjectsFullyLoadedComplete(); //fire this now all the states are set

		}
	}

	/**
	 * ensures OnLogicalLoadComplete() only fires once per scene
	 */
	boolean firedOnLogicalLoadComplete=false;
	
	
	/**
	 * ensures this.ScenesLog("<----------PHYSICAL LOADING OF ALL SCENE OBJECTS COMPLETE","GREEN");
	 * (just a log for now)
	 */
	boolean firedOnPhysicalLoadComplete = false;

	/**
	 * tests for both ObjectsLeftToLoad & ObjectsLogicallyLeftToLoad to be empty, which is the ultimate thing determining if the load is done.
	 * 
	 * 
	 */
	protected void testForLoadComplete() {

		Log.info("_____________________________________________________________ObjectsLeftToLoad:"	+ ObjectsPhysicallyLeftToLoad+" Loading:"+Loading);
		Log.info("_____________________________________________________________ObjectsLogicallyLeftToLoad:"	+ ObjectsLogicallyLeftToLoad);
		
		
		boolean allItemsKnown = getScenesData().isAllItemsKnown();
		
		if (Loading && allItemsKnown && !PreparingLoading){ //we now ensure all items are known - testForLoadComplete() can fire while scene objects are still being created!
			//We also test we arnt still preparing to load (for platfoems with no image load time, its possible the placement of objects happens instantly)
			
			//
			// When ObjectsLogicallyLeftToLoad is empty we are ready to set things up logically
			// (this will include final zindex setup as well as firing all OnFirstLoads )
			//
			boolean logicalLoadFinnished  = ObjectsLogicallyLeftToLoad.isEmpty();
			boolean physicalLoadFinnished = ObjectsPhysicallyLeftToLoad.isEmpty();
			
			if (physicalLoadFinnished && !firedOnPhysicalLoadComplete) { 
				firedOnPhysicalLoadComplete=true;
				this.ScenesLog("<----------PHYSICAL LOADING OF ALL SCENE OBJECTS COMPLETE","GREEN");
				allFilesPhysicalLoaded =true;				
			}

			if (logicalLoadFinnished && !firedOnLogicalLoadComplete) { 
				firedOnLogicalLoadComplete=true;
				this.ScenesLog("<----------LOGICAL LOADING OF ALL SCENE OBJECTS COMPLETE","GREEN");
				//ensure this only fires once
				OnAllSceneObjectsLogicalLoadComplete();
			}



			//
			// When both the logical load is empty AND the regular load (with images) then the scene is fully ready to start
			//
			if (logicalLoadFinnished && physicalLoadFinnished) { 
				onObjectLoadFullyComplete();
				//Log.info("_____________________________________________________________Time Take:"	+ sceneBackground.getLoadingTime());
			}


		} else {
			Log.info("_______________(scene not loading ("+Loading+") &or scenedata still extracting objects ("+(!allItemsKnown)+") )_______________________");

		}

	}


	/**
	 * remove a object from the logical loading when all its "logical" elements are loaded.
	 * This would be, for example, both its collision map and its attachment points, assuming it has either.
	 * (Visual elements don't count as logical, only things that can effect its position or variables)
	 * The idea is for once all the logical elements to be loaded, the object can be put on the scene in the right place and zindex - even while still waiting for images.
	 * (which makes webgames load better)
	 * 
	 * @param objectThatsReadyLogically
	 */
	public void advanceLogicalLoading(SceneObject objectThatsReadyLogically) {

		Log.info("_____________________________________removing "+objectThatsReadyLogically.getName()+" from logical loading__");

		if (Loading) {

			ObjectsLogicallyLeftToLoad.remove(objectThatsReadyLogically);
			objectThatsReadyLogically.onLogicalLoadComplete();			
			this.ScenesLog("Removed From Logical Loading:"+objectThatsReadyLogically.getName()+" Left In List:"+ObjectsLogicallyLeftToLoad.size());

			testForLoadComplete();

		} else {
			Log.info("not currently loading scene");
		}

	}


	/**
	 * advance the overall loading 
	 * @param LoadIDToRemove - can be a object or image
	 */
	public void advancePhysicalLoading(String LoadIDToRemove) {

		if (Loading) {

			ObjectsPhysicallyLeftToLoad.remove(LoadIDToRemove);
			this.ScenesLog("Removed From physical Loading:"+LoadIDToRemove+" Left In List:"+ObjectsPhysicallyLeftToLoad.size());

			//	ObjectsLeftTooLoad = ObjectsLeftTooLoad - 1;

			Log.info("_____________________________________"+LoadIDToRemove+" removed from loading__"+this.SceneFileName+"_  Left to load="+ ObjectsPhysicallyLeftToLoad);

			//	sceneBackground.stepLoading();

			testForLoadComplete();

		} else {
			Log.info("not currently loading");
		}

	}



	/**
	 * When loaded, the ItemLoadID should be supplied to the advance Loading method
	 * You could use, say, a URL as the LoadID provided its unique
	 * 
	 * @param ItemLoadID
	 */
	public boolean addToLoading(String ItemLoadID) {

		Log.info("____adding to physical loading__ "+ItemLoadID+" :"+ObjectsPhysicallyLeftToLoad);
		ScenesLog("added to physical loading:"+ItemLoadID+" :"+ObjectsPhysicallyLeftToLoad);

		//ObjectsLeftTooLoad = ObjectsLeftTooLoad + 1;
		return ObjectsPhysicallyLeftToLoad.add(ItemLoadID);

	}
	
	public void updateLoadingVisualTotal() {
	}
	
	

	/**
	 * Add the object to the list of things that need to load in order to set up the scene logically.
	 * We also have a list for just addToLoading(String..) 
	 * The difference is thats the list determining ALL the things needed before the scene can be displayed.
	 * <br>
	 * This is the list determining just the things the scene needs in order to setup logically (ie. no visuals yet, but collisionmaps and loadAttachmentPoints)
	 * and anything else that can determine values or effect OnFirstLoad code).<br> 
	 *
	 *1. ObjectsLogicallyLeftToLoad; When all the data needed to position them and run commands is loaded. (inc. collisionmaps)
	 *2. ObjectsLeftToLoad; When all objects are ready to be shown (including visuals)
	 * 
	 *<br>
	 * When:
	 * 1) Is loaded the objects OnFirstLoad is run, and possibly there zindex rechecked if automatic.
	 * 1+2) Is loaded the page is displayed.
	 *
	 * @param ItemLoadID
	 */
	public void addToLogicalLoading(SceneObject so) {

		if (Loading){
			Log.info("____adding to logical loading__ "+so.getName()+" :# left:"+ObjectsLogicallyLeftToLoad.size());
			ScenesLog("added to logical loading:"+so.getName()+" :# left:"+ObjectsLogicallyLeftToLoad.size());
			ObjectsLogicallyLeftToLoad.add(so);
		} else {
			ScenesLog("not added to logical loading as scene is already loaded:"+so.getName());
			
		}
	}



	/** sets the scene widgets background size, movement limits and background image **/
	protected abstract void setupBackground(SceneData scenedata);



	public void setUpScene(SceneData scenedata) {

		Log.info("setting up:"+getScenesData().SceneFolderName); 
		
		Log.info("_____setting up scene data and background for "+this.SceneFileName+";");

		//set movement limits to size by default	
		if (scenedata.MovementLimitsSX == -1 ){
			scenedata.MovementLimitsSX = 0;			
		}
		if (scenedata.MovementLimitsSY == -1 ){
			scenedata.MovementLimitsSY = 0;			
		}
		if (scenedata.MovementLimitsEX == -1 ){
			scenedata.MovementLimitsEX = scenedata.InternalSizeX;			
		}
		if (scenedata.MovementLimitsEY == -1 ){
			scenedata.MovementLimitsEY = scenedata.InternalSizeY;			
		}

		//
		setupBackground(scenedata);

		Log.info("_____Setting Up scene objects on "+this.SceneFileName+";");

		ScenesLog("Attaching all "+scenedata.scenesOriginalObjects.size()+" objects to scene....");


		Iterator<SceneObject> sceneobjectit = scenedata.scenesOriginalObjects.iterator();

		//attach all objects
		while (sceneobjectit.hasNext()) {

			SceneObject sceneObject = sceneobjectit.next();

			//only add objects to the scene if they have a scene!
			//Its possible that a object was created for this scene, but then moved to the inventory
			//If the inventory loads first, we thus shouldn't now put it back on the scene
			boolean hasNoScene = sceneObject.objectsCurrentState.ObjectsSceneName.equals(SceneObjectState.OBJECT_HAS_NO_SCENE_STRING);
			//TODO: we need also to make sure it isnt attached aleady.particularly if it was too a different scene (ie the object moved)
			//This can happen when state loading happens before this
			
			if (!hasNoScene ){

				//---------------------------------
				//Important; We add it to the logical loading queue
				//addToLogicalLoading(sceneObject);
				//This lets the scene know it still might have to wait for this object to be fully ready to be used logically.
				//ie. Collisionmap is still loading.
				//Its the objects own list to remove itself from the queue with the matching advance command.
				//Now done;  in SceneData
				//---------------------------------
				sceneObject.ObjectsLog("Adding object to scene ("+this.SceneFolderLocation+") for first time","RED");				
				addObjectToScene(sceneObject);
			}
			

			Log.info("_____added object to scene: "+sceneObject.getName()+";");

		}

		ScenesLog("...done attaching objects");
		Log.info("Objects loaded on "+SceneFileName);


		//maybe this is a good point to run some sort of extra setup on each object? now we know everything is technically on the scene?
		//It should also fire OnFirstLoad, rather then as soon as the widget is attached to the page.
		//This would ensure objects referring to other objects positions or z-indexs already have that data.
		//It would not, however, help with collision maps which are asynchronimiously loaded from the widgets themselfs
		//and might not be ready by this statement.
		//hmmm...
		//should we also load the objects collision maps as part of the widget loading? give it to them? (so we can keep track when all are loaded)
		//But then we lose parrallness in setting up the scene while we wait
		//Somehow I think we need some way to track once all the bits from ALL the objects are ready THEN fire their OnFirstLoad commands.
		//we could do this as part of the loading counter
		//but loading more onto that might get messy.
		//I guess a separate list of "objects still loading" that gets shorter as things are loaded off completely?
		//
		//
		//Iterator<SceneObject> sceneobjectit = scenedata.scenesOriginalObjects.iterator();		
		//initlize all objects
		//while (sceneobjectit.hasNext()) {
		//	
		//}



		//add debugging overlays
		//setupCmapVisualiser();  Note: we dont run 	setupCmapVisualiser(); here anymore, as it requires the objects to load first
		//its thus now put into loadcomplete

		// set up the actions
		Log.info("_____setting up scene actions;");
		sceneActions = scenedata.sceneActions;
		if (scenedata.sceneActions==null){
			Log.info("_____no scene actions");
		}

		//Log.info("_____setting up scene location;"+scenedata.locx+","+scenedata.locy);
		//	ScenesLog("__setting up scene location data;"+scenedata.locx+","+scenedata.locy);
		ScenesLog("__setting up scene location status;"+scenedata.currentState.PosX+","+scenedata.currentState.PosY);

		//we probably suse use the function we made to load states here instead?

		currentState.PosX = scenedata.currentState.PosX; //scenedata.locx;
		currentState.PosY = scenedata.currentState.PosY; //scenedata.locy;

		setViewPosition(currentState.PosX , currentState.PosY,true);

		/*
			if (currentState.PosX  == -1 && currentState.PosY == -1) {

			//	sceneBackground.setViewToCenter(true);
				setViewToCenter(true);

			} else {

				sceneBackground.setViewToPos(currentState.PosX , currentState.PosY);
				ScenesLog(" setViewToPos="+currentState.PosX +","+ currentState.PosY);


			}*/

		//
		setScroll_imp(scenedata.PanX, scenedata.PanY);
		//	sceneBackground.XMOVEMENTDISABLED = !scenedata.PanX;
		//	sceneBackground.YMOVEMENTDISABLED = !scenedata.PanY;

		Log.info(" scene set up done so we add to the list ");

		//all_scenes.put(scenedata.SceneFolderName, this);
		this.ScenesLog("setUpScene done. PreparingLoading=false","blue");
		PreparingLoading=false;
		
		
		//we probably have to wait for imagges to load, but in case we dont we should check if we are finished straight away
		testForLoadComplete(); 
		
		//test for loading complete if there was no sprite objects
		//if (scenedata.getScenesCurrentObjectsOfType(SceneObjectType.Sprite).size()==0){
		//	Log.info("no sprite objects in scene so we check if we are loaded straight away"); //normally as each sprite image loads it checks if its the last
		//	ScenesLog("no sprite objects in scene so we check if we are loaded straight away (what a weird scene);");

		//	testForLoadComplete(); 
		//}



	}
	
	
	/**
	 * optional overriden. sets total loading units to load (so for a loading visualizer this is the total "out of)"
	 * @param total
	 */
	public void setLoadingTotal(int total){
		
	}
	/**
	 * optional overriden. subs from total loading units left to load(so for a loading visualizer this is the total "out of)"
	 * @param total
	 */
	public void stepLoadingTotalVisual(float steps){
		
	}
	
	/**
	 * optional overriden. sets from total loaded units. We use float in case implementations want to use fractions in their loading for  some reason
	 * @param total
	 */	
	public void setLoadingTotalProgress(float loadedSofar) {
		
	}
	
	/**
	 * should set a specific load message, if your not using setLoadingMessages
	 * @param message
	 */

	public void setLoadingMessage(String message) {

	}


	/**
	 * This will fire when the scene starts loading its data.
	 * Use this to display some messages on screen, such as a sequenced or random selection of text hints.
	 * 
	 */

	public void setLoadingMessages() {

	}



	protected void setLoadingBackground() {

	}



	public void fetchItemData(final String SceneFileName) {

		setLoadingMessage("Getting Object data....");

		String SceneItemFileLocation = SceneFileRoot + SceneFileName	+ "/" + SceneFileName + "_items.jam";

		//set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable(){	
			@Override
			public void run(String sceneItemData, int responseCode) {

				// check response is not an error
				if (responseCode >= 400 || responseCode == 204) {
					Log.severe("________no items file recieved (404):\n");
					retrievedSceneItemFileData = Optional.absent();
					parseSceneFilesIfReady();
					return;
				}

				setLoadingMessage("Got Object data....");
				retrievedSceneItemFileData = Optional.of(sceneItemData);
				parseSceneFilesIfReady();
			}
		};

		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){	
			@Override
			public void run(String errorData, Throwable exception) {
				Log.severe("error recieved:"	+ exception.getMessage());

				retrievedSceneItemFileData = Optional.absent();
				parseSceneFilesIfReady();
			}

		};

		RequiredImplementations.getFileManager().getText(SceneItemFileLocation,true,
				onResponse,
				onError,
				false);

	}



	protected void fetchSceneData(final String SceneFileName) {
		//moved these to super constructor;
		//this.SceneFileName = SceneFileName;

		// get the location
		//SceneFolderLocation = SceneFileRoot + SceneFileName;
		//SceneFileLocation = SceneFolderLocation + "/" + SceneFileName + ".jam";
		//--



		//set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable(){
			@Override
			public void run(String sceneFileData, int responseCode) {
				// check response is not an error
				if (responseCode >= 400 || responseCode == 204) {
					Log.severe("________no scene file recieved (404):\n");
					retrievedSceneFileData = Optional.absent();
					parseSceneFilesIfReady();
					return;
				}

				retrievedSceneFileData = Optional.of(sceneFileData);
				parseSceneFilesIfReady();

			}
		};

		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				Log.severe("error recieved:" + exception.getMessage());
				retrievedSceneFileData = Optional.absent();
				parseSceneFilesIfReady();

			}

		};


		Log.info("getting scene file:"+SceneFileLocation);


		RequiredImplementations.getFileManager().getText(SceneFileLocation,true,
				onResponse,
				onError,
				false);
	}



	/**
	 * Starts the actual file loading process. This should be done AFTER the scene has been attached to the page if using GWT
	 * (as widgets might be placed by ID)
	 * 
	 */
	public void intialize() {

		ScenesLog("Starting to retrieve scene files", "GREEN");

		Log.info("retrieveAllNeededFiles in scene: "+SceneFileName+" loading:"+Loading);
		retrieveAllNeededFiles(SceneFileName);

	}



	public ArrayList<MovementWaypoint> findSafeEndpointOnLine(int cx, int cy, int tx, int ty,
			SceneObject sceneObject) {

		if (this.scenesCmap.isPresent()){
			return scenesCmap.get().findSafeEndpointOnLine(cx, cy, tx, ty, sceneObject);

		} else {			
			//if theres no cmap, then the required target points are already safe - nothing else needed
			ArrayList<MovementWaypoint> pathWithSafeEnding = new ArrayList<MovementWaypoint>();
			pathWithSafeEnding.add(new MovementWaypoint(tx,	ty, MovementType.AbsoluteLineTo));
			return pathWithSafeEnding;
		}
	}



	/**
	 */
	public void leftclickortouch() {





		//Log.info("scene clicked at " + x + " , " + y);

		//and screen space (for menus mostly)
		//might need to use different x/y positions as I think these are scenerelative not screen relative
		//see how SceneObjectVisual sets it if this doesnt work on scrolling scenes;


		//todo: this can be simplified by just passing though the screen x/y from the statement that calls this
		//InstructionProcessor.lastclickedscreen_x = x
		//		- this.getParent().getElement().getAbsoluteLeft()
		//		+ this.getParent().getElement().getScrollLeft()
		//		+ this.getParent().getElement().getOwnerDocument().getScrollLeft()
		//		+ x; 



		//InstructionProcessor.lastclickedscreen_y = y
		//		- this.getParent().getElement().getAbsoluteTop()
		//		+ this.getParent().getElement().getScrollTop()
		//		+ this.getParent().getElement().getOwnerDocument().getScrollTop()
		//		+ y;

		//updateLastClickedLocation(x, y, screenx, screeny);

		// test for background actions:
		Log.info("testing for background click actions...");
		boolean backgroundActionsFound = testForSceneActions(TriggerType.BackgroundClickAction, null);


		// test for global actions
		// SceneWidget.testForGlobalActions(TriggerType.Click,
		// null);

		// test for scene actions
		boolean sceneActionsFound = testForSceneActions(TriggerType.MouseClickActions, null);


		if (InventoryPanelCore.currentlyHeldItem!=null){

			//unhold if no actions found
			if (!backgroundActionsFound && !sceneActionsFound){

				Log.info("no actions found so unholding");	
				InventoryPanelCore.unholdItem();
				return;
			}

			//or unhold if actions found but "onuse keep held" is not set
			if (!SceneMenuWithPopUp.menuShowing && InventoryPanelCore.currentlyHeldItem.getKeepHeldMode()  != KeepHeldMode.onuse){
				Log.info("_________unholdItem after processInstructions ");
				InventoryPanelCore.unholdItem();
			} else {
				Log.info("_________menu showing, so we keep holding");

			}



		}


	}

	//TODO: doubleclick support on background

	/*** 
	 * @param x - scene relative x
	 * @param y - scene relative y	 * 
	 */
	public void rightclickortouch() {



		//	updateLastClickedLocation(x, y, screenx, screeny);
		//---

		// test for background actions
		boolean backgroundActionsFound = testForSceneActions(TriggerType.BackgroundRightClickActions, null);

		// test for global actions
		// SceneWidget.testForGlobalActions(TriggerType.Click,
		// null);

		// test for scene actions
		boolean sceneActionsFound = testForSceneActions(TriggerType.MouseRightClickActions, null);


		if (InventoryPanelCore.currentlyHeldItem!=null){

			//unhold if no actions found
			if (!backgroundActionsFound && !sceneActionsFound){

				Log.info("no right click  actions found so unholding");

				InventoryPanelCore.unholdItem();
				return;
			}

			//or unhold if actions found but "onuse keep held" is not set
			if (!SceneMenuWithPopUp.menuShowing && InventoryPanelCore.currentlyHeldItem.getKeepHeldMode()  != KeepHeldMode.onuse){
				Log.info("_________unholdItem after processInstructions ");
				InventoryPanelCore.unholdItem();
			} else {
				Log.info("_________menu showing, so we keep holding");

			}



		}


	}



	protected void updateLastClickedLocation(int x, int y, int screenx, int screeny) {
		CurrentScenesVariables.lastclicked_x = x;
		CurrentScenesVariables.lastclicked_y = y;
		CurrentScenesVariables.lastclicked_z = 0; //as we are clicking on the background/floor of the scene z should be zero


		CurrentScenesVariables.lastclickedscreen_x = screenx;//x;
		CurrentScenesVariables.lastclickedscreen_y = screeny;//y;
	}


	/**
	 * Part of the new loading system where the various files are loaded simultaneously<br>
	 * null = unknown state<br>
	 * absent = file checked for but not present (ie, 404 or other error in retrieving the file)<br>
	 * present = The string is the contents of the file, which was retrieved correctly	 <br>
	 */	
	Optional<String> retrievedSceneFileData = null;
	/**<br>
	 * Part of the new loading system where the various files are loaded simultaneously<br>
	 * null = unknown state<br>
	 * absent = file checked for but not present (ie, 404 or other error in retrieving the file)<br>
	 * present = The string is the contents of the file, which was retrieved correctly	 <br>
	 */	
	Optional<String> retrievedSceneItemFileData = null;

	/**
	 * Part of the new loading system where the various files are loaded simultaneously<br>
	 * null = unknown state<br>
	 * absent = file checked for but not present (ie, 404 or other error in retrieving the file)<br>
	 * present = The string is the contents of the file, which was retrieved correctly	 <br>
	 */	
	Optional<String> retrievedSceneCollisionFileData = null; //Note: This one, unlike the others can be .absent and the scene is still valid

	/**
	 * New method that starts the methods to retrieve all the needed files for the scene to be processed
	 * After each file is retrieved the parseSceneFilesIfReady() function is fired, which only starts processing if they have
	 * 
	 * all be retrieved (or in the case of CollisionData, is alternatively absent)
	 * 
	 * @param SceneFileName
	 */
	public void retrieveAllNeededFiles(final String SceneFileName){

		// get the items for the scene
		Log.info("getting scene data");
		fetchSceneData(SceneFileName);

		Log.info("getting item data");
		fetchItemData(SceneFileName);

		Log.info("getting cmap data");
		fetchCollisionData(SceneFileName);

	}

	
	/**
	 * Tests if we have all the files needed, and if we do, it starts to parse them
	 */
	public void parseSceneFilesIfReady(){
		
		Log.info("(checking if "+this.SceneFileName+" is ready to process)");	
		

		//While the rest of the data has to wait to be processed, cmaps can be done straight away if present
		if (       retrievedSceneCollisionFileData!=null 
				&& retrievedSceneCollisionFileData.isPresent()){

			Log.info("(processing cmap for: "+this.SceneFileName+" )");	
			parseCmapFile(retrievedSceneCollisionFileData.get()); //might process twice?
			
		}

		//test if the required scene files have a known state, return if any are not
		if (                retrievedSceneFileData == null
				||      retrievedSceneItemFileData == null 
				|| retrievedSceneCollisionFileData == null ) {

			Log.info("(waiting for other files for "+this.SceneFileName+" )");
			if (retrievedSceneFileData==null){
				Log.info("(specifically the scenefile)");					
			}
			if (retrievedSceneItemFileData==null){
				Log.info("(specifically the sceneitemfile)");			
			}
			if (retrievedSceneCollisionFileData==null){
				Log.info("(specifically the collisionmap)");				
			}
			return;			
			
		} else {
			
			Log.info("(all scene file retrieved or known as missing for "+this.SceneFileName+", proceeding to parse....)");
			if (alreadyProcessedScene){
				Log.info("(scene already processed, however, so returning)");
				return;
			}
			
		}

		//test if the required SceneFileData and SceneItemFileData are present, error if absent
		if (!retrievedSceneFileData.isPresent()){
			Log.severe(" SceneFileData (.jam) not retrieved correctly! Check filepath or file-fetching method?");			
			return;
		} 
		if (!retrievedSceneItemFileData.isPresent()){
			Log.severe(" SceneFileItemData ( _items.jam) not retrieved correctly! Check filepath or file-fetching method?");			
			return;
		} 		
		//NOTE: the collision data isn't checked because it can be absent for scenes with no collision map	

		//now we have everything we can process it all! Yay!		
		processSceneFileData(retrievedSceneFileData.get(), retrievedSceneItemFileData.get());

	}


	boolean alreadyProcessedScene = false;

	//public only for unit testing
	/** <br>
	 * processes the scene data, and when its all done (all objects loaded) displays the scene) <br>
	 *  <br>
	 * @param rawSceneFileData - the data from the scene #####.Jam file <br>
	 * @param rawSceneItemData - the data from the scene #####_items.Jam <br>
	 * <br>
	 * (Note: CollisionMapData should be processed separately - it does not have to wait for either of the above files) <br>
	 **/
	public void processSceneFileData(String rawSceneFileData, String rawSceneItemData) {
		
		if (alreadyProcessedScene){
			Log.severe("(processSceneFileData:scene already processed, however, so no-op)");
			return;
		}
		alreadyProcessedScene = true;
		
		setLoadingMessage("Processing Scenes Data....");
		
		//make sure all file names are language specific				
		String proccessedSceneFileData = JAMcore.parseForLanguageSpecificExtension(rawSceneFileData);

		// swap TextIds for text
		proccessedSceneFileData        = JAMcore.parseForTextIDs(proccessedSceneFileData);


		//make sure all file names are language specific
		String itemsData = JAMcore.parseForLanguageSpecificExtension(rawSceneItemData);

		// swap TextIds for text
		itemsData=JAMcore.parseForTextIDs(itemsData);

		Log.info("Scene file proccessed for IDs and Language " + proccessedSceneFileData);

		// set foreground to loading
		setLoadingBackground();

		//	ScenesLog("loading scene", "green");
		//	sceneBackground.setLoading(true,"Loading Scene....");

		ScenesLog("loading scenes items...");


		//insert silly loading messages here
		setLoadingMessages();		
		setLoadingMessage("Processing Object data....");

		Log.warning("Processing Object data for scene...."+this.SceneFileName); //Note; wheel of loading hasn't appeared yet, even though loadingdiv has....not sure why Surely the SceneData adds to the wheel and makes it appear?


		// create data object (this also creates the separate scene object items inside it)
		SceneData sceneData = new SceneData(proccessedSceneFileData, itemsData, SceneWidget.this, currentState);
				
		
		setScenesData(sceneData);


		setLoadingMessage("Processing Object data.......");

		Log.warning("scene data loaded for "+getScenesData().SceneFolderName); //Note; wheel of loading hasn't appeared yet, even though loadingdiv has....not sure why Surely the SceneData adds to the wheel and makes it appear?

		currentState.SceneName = getScenesData().SceneFolderName;

		// store as default		
		/*
		defaultState = new SceneStatus(						
				currentState.SceneName,
				currentState.PosX,
				currentState.PosY,
				currentState.NumOfTimesPlayerHasBeenHere,
				currentState.currentBackground,
				currentState.DynamicOverlayCSS,
				currentState.StaticOverlayCSS,
				currentState.hasNotBeenCurrentYet);
		*/
		defaultState = currentState.clone();
		
		Log.info("initialising:"+getScenesData().SceneFolderName); 
		
		
		sceneData.initialize(); //Note:Initializing has to happen after setting the scene data
		//Initializing will first create the objects for this scene, then fire this scenes setUpScene() function to add them all
		//to the scene.		
		//--------------------------------------------
		//-----------
		
		
		
		//old;
	//	Log.info("setting up:"+getScenesData().SceneFolderName); 
	//	setUpScene(getScenesData()); //adds the objects to the scene

	//	//advanceLoading(); //we remove the initial load item set upon scene creation (because now the code should know the total items on the scene to load)

		//testForLoadComplete();//we do a single check here in case loading is finnished straight away. (could happen on a local version)


	}



	public void parseCmapFile(String cmapFileData) {
		//
		String vmapdata = cmapFileData;
		Log.info("________________________________________________________vmap data recieved:");

		Log.info("collision data recieved:"
				+ vmapdata);

		if (vmapdata.length() < 3) {
			Log.info("collision data too short error");
			return;
		}

		//create the scenes collision map from the vmap data we just received
		scenesCmap = Optional.of(new SceneCollisionMap(vmapdata,SceneWidget.this));
		//Note: we dont run 	setupCmapVisualiser(); here anymore, as it requires the objects to load first
		//its thus now put into loadcomplete

		//	Log.info("setting up collision visualiser on:"+SceneWidget.this.getScenesData().SceneFolderName);
		//	Log.info("number of objects:"+SceneWidget.this.getScenesData().allScenesCurrentObjects());

		//	setupCmapVisualiser();
	}




	//--
	//* take a square region under meryll (in y)
	//
	//* get ALL contents in it, regardless of collision mode
	//
	//* tests for lowest Y, ensure meryll is under it
	//
	//Repeat same for square above, ensuring meryll is over it 
	//
	//
	//We can optimize to check if the zindex would effect her first, before doing any collision region checking


	/**
	 * Attempts to deduce its zindex (ie, draw order) from the objects around the specified object.
	 * By default, checks all the scenes objects to work this out, unless the object is positioned relatively, in which
	 * case it only checks its siblings.
	 * (this is because it would already be overlapping with its parent object, which in turn should already be correctly positioned)
	 *
	 * For this to work LOGICAL LOADING has to be finished (so all objects have their collision maps)
	 * AND
	 * Physical (as we need to know the VISUAL size of the object, which requires the images!)
	 *
	 *
	 * @param sceneObject
	 */
	public void updateZIndexBasedOnPerspective(SceneObject sceneObject) {

		sceneObject.ObjectsLog("(Deduceing zindex)","blue");

		//check object is ready, all objects must be logically loaded, but only we need to be physically
		if (!sceneObject.getParentScene().isAllFilesLogicallyLoaded() || !sceneObject.isPhysicallyLoaded() ){
			sceneObject.ObjectsLog("(can not deduce zindex as either logical loading or physical loading is not finnished)","orange");
			sceneObject.ObjectsLog("(logical load for all so:"+sceneObject.getParentScene().isAllFilesLogicallyLoaded() +"  physicalload for this object: "+sceneObject.isPhysicallyLoaded()+")","orange");
			return;
		}



		//loop over all objects
		//for each object
		// get its z

		// is the z higher then current highest tested z?
		//  test for if its in the square above (y)
		//     if so, set as highestTestedz
		//(continue)

		// is the z lower then the current lowest tested z?
		//    test for if its in the square below (in y)
		//       if so set as lowestTestedZ

		//---

		//once we have done that for all objects we will have the lowerest and highest tested Z values
		//the sceneObjects zIndex should be inbetween these two values

		//Now we do it;

		//first we prepare as much as possible outside the loop


		int Y = sceneObject.getLowerLeft().y;
		int X = sceneObject.getLowerLeft().x;
		int width = sceneObject.getPhysicalObjectWidth(); //the above coordinates + width represent the objectsbase line

		sceneObject.ObjectsLog("(ObjectHeight Currently:"+sceneObject.getPhysicalObjectHeight()+" LowerLeftY:"+Y+")"); //temp test, seems it might return the wrong result if the url just changed. 
		//TODO: check this. Maybe its because its waiting for the browser to setURL but the browser needs time to catch up?

		int lengthUpAndDownTocheck = 250; //This is the amount of pixels we look up and down from in Y to see what we are colliding with
		//It should be at least the perceptive height of  the tallest object in most situations.


		//the box above the object
		Simple2DPoint BoxAbove_TL = new Simple2DPoint(X,Y-lengthUpAndDownTocheck);
		Simple2DPoint BoxAbove_BR = new Simple2DPoint(X+width,Y);

		//the bow below the object
		Simple2DPoint BoxBelow_TL = new Simple2DPoint(X,Y);
		Simple2DPoint BoxBelow_BR = new Simple2DPoint(X+width,Y+lengthUpAndDownTocheck);

		CollisionBox BoxAbove = new CollisionBox(BoxAbove_TL,BoxAbove_BR);
		CollisionBox BoxBelow = new CollisionBox(BoxBelow_TL,BoxBelow_BR);

		//box testing
		if (scenesCmap.isPresent()){			

			String asSVGPath = BoxAbove.getAsSVGPath();			
			String asSVGPath2 = BoxBelow.getAsSVGPath();


			if (scenesCmap.get().CMapVisualiser.isPresent()){
				IsCollisionMapVisualiser isCollisionMapVisualiser = scenesCmap.get().CMapVisualiser.get();
				isCollisionMapVisualiser.addToSketch(asSVGPath, "rgba(0, 0, 255, 0.25)",true);				
				isCollisionMapVisualiser.addToSketch(asSVGPath2, "rgba(128, 0, 128, 0.22)",true);		
			}

			//sceneObject.ObjectsLog("(drawing cmap:  "+asSVGPath+")", "Orange");
			//sceneObject.ObjectsLog("(drawing cmap: "+asSVGPath2+")", "Orange");
		}

		//this variable will become the highest z index of whats above us
		int highestTestedZ = -1; // -1 indicates unset
		//this variable will become the lowest z index of whats below us
		int lowestTestedZ  = -1; 

		//variables kept purely for debugging


		//loop over all scene objects 
		HashSet<SceneObject> checkTheseObjects;
		if (sceneObject.getObjectsCurrentState().positionedRelativeToo!=null){
			checkTheseObjects = sceneObject.getObjectsCurrentState().positionedRelativeToo.relativeObjects;
			sceneObject.ObjectsLog("(Deduceing from:"+checkTheseObjects+")");
		} else {
			checkTheseObjects = getScenesData().getAllScenesCurrentObjects();

			sceneObject.ObjectsLog("(Deduceing from:"+checkTheseObjects.size()+" objects)");
		}


		for (SceneObject so : checkTheseObjects) {

			//we obviously don't test the object against itself.
			if (so==sceneObject){
				continue; //skip to next scene object
			}

			//also skip anything not visible
			if (!so.isVisible()){
				continue;
			}
			//and anything with no collisions
			if (so.getObjectsCurrentState().boundaryType.getCollisionType()==CollisionType.none){
				continue;
			}

			//for each object
			// get its z	
			int zToTest = so.getZindex();


			boolean boxTouchingAbove = false;
			boolean boxTouchingBelow = false;

			// is the z higher then current highest tested z?
			if (zToTest>highestTestedZ || highestTestedZ==-1){
				boxTouchingAbove = so.isBoxTouching(BoxAbove);
			}
			//is it lower then the lowest zindex?
			if (zToTest<lowestTestedZ || lowestTestedZ==-1 ){
				boxTouchingBelow = so.isBoxTouching(BoxBelow);
			}

			//if we touch above and below we ignore the object, as its contradictory.
			//this can happen if the bottom line intersects the object - ideally it should not do that
			if (boxTouchingAbove && boxTouchingBelow){				
				sceneObject.ObjectsLog("("+so.getName()+" is both above and below object (bottom line intersects?). Thus ignoreing)", "Orange");	
				continue;
			}

			if (boxTouchingAbove){				
				highestTestedZ=zToTest;
				sceneObject.ObjectsLog("(highestTestedZ from:"+so.getName()+")", "Orange");
			} 
			if (boxTouchingBelow){
				lowestTestedZ=zToTest;
				sceneObject.ObjectsLog("(lowerthan from:"+so.getName()+")", "Orange");

			}
			/*
			// is the z higher then current highest tested z?
			if (zToTest>highestTestedZ || highestTestedZ==-1){
				//maybe test for visual overlap first?
				//if its not overlapping, we can ignore it completely.
				//if it is then we need to make a choice if its under or over us (although that would require 2 cmap checks which would be bad)

				//  test for if its in the square above (y)
				boolean boxTouching = so.isBoxTouching(BoxAbove);
				if (boxTouching){
					objectsAbove.add(so);

					highestTestedZ=zToTest;
					sceneObject.ObjectsLog("(highestTestedZ from:"+so.getName()+")", "Orange");
				} else {
					sceneObject.ObjectsLog("("+so.getName()+" not above bottomline)", "grey");

				}

			} else {
				sceneObject.ObjectsLog("("+so.getName()+" not highest)", "grey");

			}

			if (zToTest<lowestTestedZ || lowestTestedZ==-1 ){
				//  test for if its in the square above (y)
				boolean boxTouching = so.isBoxTouching(BoxBelow);
				if (boxTouching){
					objectsBelow.add(so);

					lowestTestedZ=zToTest;
					sceneObject.ObjectsLog("(lowerthan from:"+so.getName()+")", "Orange");

				}
			}*/

		}

		//this variable will become the highest z index of whats above us
		int lowerThan   = highestTestedZ; // -1 indicates unset
		//this variable will become the lowest z index of whats below us
		int higherThan  = lowestTestedZ; 

		sceneObject.ObjectsLog("Changing ZIndex based on cmap (lowerthan:"+lowerThan+" higherthan:"+higherThan+")", "Orange");

		int newzindex = -1;


		//if both min and max were set we take the average
		if (lowerThan!=-1 && higherThan!=-1){
			//TODO: what if the maxZ is less then the minZ ? 
			newzindex = (int) ((lowerThan+higherThan)/2.0f);		
		} else if (higherThan!=-1){
			//else if max was set we use that -1
			newzindex = higherThan-1; 
		} else if (lowerThan!=-1) {
			//else if min was set we use that +1
			newzindex = lowerThan+1; 				
		} else {
			sceneObject.ObjectsLog("(zindex unchanged)", "Orange");				
		}

		if (newzindex!=-1){
			sceneObject.setZIndexImplementation(newzindex);
			sceneObject.updateRelativelyZIndexedObjects(newzindex);		
		}


	}


	/**
	 * Positions the objects zIndex so its in front of anything above it in Y and below anything under it in Y.
	 * For orthographic faked 3d games this works. (ie, isometrix or 3/4th)
	 * For other projection where Y does not represent distant from "camera" this will need to be overriden by other methods
	 * 
	 * We assume other objects are positioned correctly - this function only checks the first object above/below that it hits,provided
	 * they are within 150 pixels of the object having its zindex set
	 * 
	 * @param sceneObject - the sceneobject whos zindex to update
	 * */

	public void updateZIndexBasedOnPerspective_old(SceneObject sceneObject) {

		if (!scenesCmap.isPresent()){			
			//use of this function requires a cmap set
			Log.info("can not set zindex by perspective on scenes without cmap");			
		}


		boolean changed =false;

		int lengthUpAndDownTocheck = 150; //This is the amount of pixels we look up and down from in Y to see what we are colliding with
		//It should be at least the perceptive height of the object in most situations.


		//we draw lines down and up from the pivot, and use the found collisions to determine out zindex position
		int x = sceneObject.getX();
		int y = sceneObject.getY();
		int z = 0;	//note; we are only interested in its position if it was on the ground


		int minZ = -1;
		int maxZ = -1;


		int dx = x;
		int dy = y-lengthUpAndDownTocheck; //Arbitrary amount up (this is the length we check)
		int dz = 0;

		sceneObject.ObjectsLog("(drawing line from:"+x+","+y+"to:"+dx+","+dy+")", "Orange");
		//- if you draw a line down in y and hit a objects cmap you should be behind that object
		Simple2DPoint BottomLeftofBB = sceneObject.getLowerLeftDisplacement();
		SpiffyPolygonCollision col = scenesCmap.get().findFirstCollision(dx, dy, x, y, BottomLeftofBB, sceneObject.getPhysicalObjectWidth());

		if (col!=null){

			SceneObject associatedObject = col.collidingObject.associatedObject; //sourceMap.defaultAssociatedObject;

			if (col.collidingObject.associatedObject!=null){ //sourceMap.defaultAssociatedObject;
				minZ = associatedObject.getZindex();	
				sceneObject.ObjectsLog("(min from:"+associatedObject.getName()+")", "Orange");

				changed=true;
			}

		}


		//- if you draw a line up in y and hit the cmap you should be above
		dx = x;
		dy = y+lengthUpAndDownTocheck; //Arbitrary amount down (this is the length we check)
		dz = 0;	

		sceneObject.ObjectsLog("(drawing line from:"+x+","+y+"to:"+dx+","+dy+")", "Orange");
		col = scenesCmap.get().findFirstCollision(dx, dy, x, y, BottomLeftofBB, sceneObject.getPhysicalObjectWidth());

		if (col!=null){

			SceneObject associatedObject = col.collidingObject.associatedObject; //sourceMap.defaultAssociatedObject;

			if (col.collidingObject.associatedObject!=null){ //sourceMap.defaultAssociatedObject;
				maxZ = associatedObject.getZindex();
				sceneObject.ObjectsLog("(max from:"+associatedObject.getName()+")", "Orange");

				changed=true;
			}
		}




		if (changed){
			int newzindex = 0;
			sceneObject.ObjectsLog("Changing ZIndex based on cmap (min:"+minZ+" max:"+maxZ+")", "Orange");

			//if both min and max were set we take the average
			if (minZ!=-1 && maxZ!=-1){

				//TODO: what if the maxZ is less then the minZ ? 

				newzindex = (int) ((minZ+maxZ)/2.0f);		

			} else if (maxZ!=-1){
				//else if max was set we use that -1
				newzindex = maxZ-1; 
			} else if (minZ!=-1) {
				//else if min was set we use that +1
				newzindex = minZ+1; 				
			}

			sceneObject.setZIndexImplementation(newzindex);
			sceneObject.updateRelativelyZIndexedObjects(newzindex);		
		} else {
			sceneObject.ObjectsLog("(zindex unchanged)", "Orange");

		}



	}



	/**
	 * development use only. Override this to provide a method to force any loading screens to close even if not all objects are loaded.
	 * 
	 */
	public void forceCloseLoading() {
		//first check if its even still open
		
		
		ScenesLog("WARNING LOADING SCREEN WAS FORCE CLOSED WHILE INCOMPLETE", "RED");
	}



	/**
	 * same as the physics bias
	 * @return
	 */
	public SimpleVector3 getNormalScaleingForReflections() {		
		return getScenesData().physicsBias;
				//new SimpleVector3(1.0,0.5,1.0);//temp,needs to be scene file setting
	}

	
	
	/**
	 * 
	 * @return 0 = no friction 1=friction will be equal to the value of the force pushing the objects together
	 * (typically gravity)
	 */
	public SimpleVector3 getDefaultSceneFriction() {
		return  this.getScenesData().surfacefriction;
	}



	public SimpleVector3 getPhysicsBias() {			
		return this.getScenesData().physicsBias;
	}



	/**
	 * WIP: returns a string with a lot of information concerning the load state of each scene it knows about.
	 * This is to confirm/help debug when a scene has not loaded. (as opposed to, say, just not visible for some reason)
	 * @return
	 */
	public static String getSceneLoadingDebugString() {
		
		String debuginfo = "";
		
		if (all_scenes==null || all_scenes.isEmpty()){
			return "all_scenes map null or empty error";
		}
		
		
		for (SceneWidget scene : all_scenes.values()) {
			
			String sceneloadinfo = "";
			
			sceneloadinfo = scene.SceneFolderLocation+"- Loading:"+scene.Loading+" PreparingLoading:"+scene.PreparingLoading;
			
			if (scene.PreparingLoading){
				
				if (scene.getScenesData()==null){
					return sceneloadinfo +" <-- scenesData was null error";
				}
				//some data to try to work out where we went wrong. 
				//if, for example, the name is not set, we know it went wrong before that point
				//if allItemsKnown==false, yet total objects is non zero, the problem is probably in the item extraction
				SceneData scene_data = scene.getScenesData();
				
				sceneloadinfo=sceneloadinfo+"\n----- retrievedSceneFileData:"+scene.retrievedSceneFileData.isPresent()+" retrievedSceneItemFileData:"+scene.retrievedSceneItemFileData.isPresent()+" retrievedSceneCollisionFileData:"+scene.retrievedSceneCollisionFileData.isPresent();				
				sceneloadinfo=sceneloadinfo+"\n----- allItemsKnown:"+scene_data.allItemsKnown+" actionssetup:"+(scene.sceneActions!=null)+" scenename: "+scene_data.currentState.SceneName;
				sceneloadinfo=sceneloadinfo+"\n----- totalobjects so far:"+scene_data.scenesOriginalObjects.size();
				
				//not too helpful, as it wont match the file order (sets dont ensure order)
				if (!scene_data.allItemsKnown){
					sceneloadinfo=sceneloadinfo+"\n----- knowen:";
					//if items missing, we at least list what we know
					for (SceneObject so : scene_data.scenesOriginalObjects) {
						sceneloadinfo=sceneloadinfo+so.getName()+",";					
						
					}					
				}
				sceneloadinfo=sceneloadinfo+"\n Last Item data:\n";
				sceneloadinfo=sceneloadinfo+scene_data.debugLastObjectData;
						
			} else if (scene.Loading){
				
				sceneloadinfo=sceneloadinfo+"\n----- Logical left to load:"+scene.ObjectsLogicallyLeftToLoad;
				sceneloadinfo=sceneloadinfo+"\n----- Physical left to load:"+scene.ObjectsPhysicallyLeftToLoad;
				
				//Logical objects left status
				for (SceneObject potentialbrokenObject : scene.ObjectsLogicallyLeftToLoad) {	
					
					sceneloadinfo=sceneloadinfo+"\n Pointentially Logical Broken Object:"+potentialbrokenObject.getName();
					sceneloadinfo=sceneloadinfo+"\n Objects state:\n"+potentialbrokenObject.getLoadStatusDebug();						
					
				}
				
				
				//Physical objects left status
				for (String objectname : scene.ObjectsPhysicallyLeftToLoad) {					
					SceneObject potentialbrokenObject = SceneObjectDatabase.getSingleSceneObjectNEW(objectname, null, true);
					if (potentialbrokenObject!=null){
						sceneloadinfo=sceneloadinfo+"\n Pointentially Physical Broken Object:"+potentialbrokenObject.getName();
						sceneloadinfo=sceneloadinfo+"\n Objects state:\n"+potentialbrokenObject.getLoadStatusDebug();						
					} else {
						sceneloadinfo=sceneloadinfo+"\n Object not in database for some reason. Crash before adding?:"+objectname;
					}
				}
				
			}
				
			
			
			if (scene ==currentActiveScene){
				sceneloadinfo=sceneloadinfo+"-----(currentscene)";
			}
					
			debuginfo=debuginfo+sceneloadinfo+"\n";
			
			
			//currentActiveScene
			
		}
		// TODO Auto-generated method stub
		return debuginfo;
	}








}
