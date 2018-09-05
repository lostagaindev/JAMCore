package com.lostagain.Jam.Scene;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Splitter;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import com.lostagain.Jam.OptionalImplementations;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.SceneObjects.SceneDialogueObjectState;
import com.lostagain.Jam.SceneObjects.SceneDivObjectState;
import com.lostagain.Jam.SceneObjects.SceneInputObjectState;
import com.lostagain.Jam.SceneObjects.SceneLabelObjectState;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.SceneObjectState;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;
import com.lostagain.Jam.SceneObjects.SceneVectorObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;

import lostagain.nl.spiffyresources.client.spiffycore.RunnableWithString;

import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.JAMTimerController.IsIncrementalCommand;

/** will contain all the non-gwt related data relating to a single scene object **/
public class SceneData {


	public static Logger Log = Logger.getLogger("JAMCore.SceneData");



	//FIXED INFO:


	//Scenes name
	public String Title;

	//Scenes defaultBackground (PNG or JPG background image)
	public String defaultBackground;

	//Container Background
	public String containerBackground = "000000"; //black by default

	//Container Background repeat.
	/**Sets how the scenes background repeats. Normal CSS settings apply 
	 * Note; This is set once on loading, and (unlike the background image) can't be changed dynamically**/	
	public String currentBackgroundRepeat = "no-repeat";

	//size
	public int InternalSizeX;
	public int InternalSizeY;

	//movement limits
	public int MovementLimitsSX = -1; 
	public int MovementLimitsSY = -1; 
	public int MovementLimitsEX = -1 ; 
	public int MovementLimitsEY = -1; 

	//panable?
	public boolean PanX=true;
	public boolean PanY=true;

	/**
	 * scales friction and reflection effects.
	 * Useful when scene co-ordinates dont match what they represent.
	 * (ie,x/y is a 1:1 ratio on screen, but scene is isometrix style)
	 */
	public SimpleVector3 physicsBias = new SimpleVector3(1.0,1.0,1.0); //default, can be changed in scene parameters


	/**
	 * note; x/y have a default value, but z does not.
	 * Its assumed the scene is a flat floor with air above, so going upwards should be frictionless, or near enough.	 * 
	 **/

	SimpleVector3 surfacefriction = new SimpleVector3(0.5f,0.5f,0.0);


	//Scenes filelocation
	public String SceneFolderName;


	//Scenes Description
	public String Description;

	public String getSceneDescription() {
		return Description;
	}
	//contains the scenes dynamic data
	public SceneStatus currentState;


	//VARIABLE INFO:

	/**
	 * All the scenes objects
	 * Subclasses should extend this to provide a link to their own top object type
	 * In GWTs case this means assigning this array to point to a collection of SceneObjectVisuals
	 */
	protected HashSet<SceneObject> scenesCurrentObjects = new HashSet<SceneObject>(); 
	protected Set<SceneObject> unmodifableListOfScenesCurrentObjects;




	/** The original objects specified by the scenes file - this shouldn't change after loading**/
	public HashSet<SceneObject> scenesOriginalObjects = new HashSet<SceneObject>();

	/**
	 * 
	 * These objects will recieve click events even when behind other objects.
	 * These events are handled by the OnClickedWhileBehind trigger
	 */
	public ArrayList<SceneObject> scenesObjectsThatSupportClicksWhileBehind = new ArrayList<SceneObject>();


	public ActionList sceneActions;


	protected String itemssourcefile;


	private HashMap<SceneObject,String> rawObjectData = new HashMap<SceneObject,String>();

	SceneWidget scenesWidget;

	public SceneData(
			String mainsourcefile, 
			String Itemssourcefile, 
			SceneWidget scenesWidget,
			SceneStatus State) {

		this.currentState = State;
		this.itemssourcefile = Itemssourcefile;
		this.SceneFolderName=scenesWidget.SceneFileName;
		this.scenesWidget = scenesWidget;

		//default background assignment is a jpeg
		currentState.currentBackground = scenesWidget.SceneFolderLocation +"/"+ scenesWidget.SceneFileName+".jpg";

		Log.info("Background recieved:"+currentState.currentBackground);
		defaultBackground = currentState.currentBackground;

		//process into scene data file				
		String scenedataastext= mainsourcefile;

		//get scene parameters (anything before a line with : in it)
		//int firstLocOfColon = scenedataastext.indexOf(':'); //TODO: This needs to look for a newline after it as well
		int firstLocOfFirstActions = ActionList.findNextTriggerLineStart(scenedataastext, 0) ; //TODO: This needs to look for a newline after it as well


		Log.info("firstLocOfFirstActions recieved:"+firstLocOfFirstActions);

		String Parameters="";
		String allactions="";

		if (firstLocOfFirstActions==-1){
			Parameters= scenedataastext;										
		} else {
			//int linebeforeColon = scenedataastext.lastIndexOf('\r',firstLocOfColon);
			Parameters = scenedataastext.substring(0, firstLocOfFirstActions-1);
			allactions = scenedataastext.substring(firstLocOfFirstActions);				


		}
		Log.info("Scene Parameters recieved:\n"+Parameters);
		extractParams(Parameters);
		//get actions 
		Log.info("Scene actions recieved:\n"+allactions);

		//create SceneData object from them

		//add actions if there was any
		if (allactions.length()>3){
			sceneActions = new ActionList(allactions);
		}
	}



	public SceneData() {
	}



	/** Returns all the current scene objects in a READ ONLY list **/
	public Set<SceneObject> allScenesCurrentObjects() {		
		if (scenesCurrentObjects==null){
			Log.severe("ERROR requestes all objects on scene "+this.SceneFolderName+" but there was no objects set");

		}
		if (unmodifableListOfScenesCurrentObjects==null){

			//	unmodifableListOfScenesCurrentObjects =   Collections.unmodifiableList(scenesCurrentObjects)

			unmodifableListOfScenesCurrentObjects =   Collections.unmodifiableSet(scenesCurrentObjects);

		}
		return unmodifableListOfScenesCurrentObjects; //ensure its read only
	}



	public boolean removeFromScenesObjects(SceneObject object) {
		scenesObjectsThatSupportClicksWhileBehind.remove(object);
		return scenesCurrentObjects.remove(object);
	}




	public boolean addToScenesObjects( SceneObject object) {
		if (scenesCurrentObjects.contains(object)){			
			//		Log.warning("object already in scenesCurrentObjects list:"+object.getObjectsCurrentState().ObjectsName);
			return true;
		}

		//if it supports clicks while its behind stuff, we also add it to the special "clicked while behind" list
		if (object.objectsActions.hasActionsFor(TriggerType.OnClickedWhileBehind)){				
			scenesObjectsThatSupportClicksWhileBehind.add(object);
		}


		return 	scenesCurrentObjects.add(object);
	}

	/** 
	 * This should only be used if you want an operation over all the scenes objects
	 * Try to use SceneObject in your implementations where you can rather then SceneObjectVisual, as this will help with the GWT split later
	 ***/
	//TODO: remove this in favor of above, its only used in one place
	public HashSet<SceneObject> getAllScenesCurrentObjects() {								
		return scenesCurrentObjects;
	}

	/**
	 * TEMP new function to return IsSceneObjectVisual rather then scene objects
	 * as we change to a interface based system, this should be used instead
	 * This function will be able to be rewriten again once they are stored as interfaces and allScenesCurrentObjects returns interfaces
	 * 
	 * Returns objects on this scene of a specific type.
	 * This is part of the process of phasing out usage of individual lists, which were barely used.
	 * NOTE: this will do a unchecked typecast to the type specified by the what your setting this equal too.
	 * Make sure the object type requested matchs
	 */
	public <T extends IsSceneObject> Set<T> getScenesCurrentObjectsOfTypeNew(SceneObjectType type) {

		Set<T> results = new HashSet<T>();
		Set<SceneObject> allScenesObjects =  allScenesCurrentObjects(); //Note eventually this function will just be returning a arraylist directly



		for (SceneObject sceneObject : allScenesObjects) {
			if (type==null || sceneObject.getObjectsCurrentState().getPrimaryObjectType()==type){
				results.add((T) sceneObject); //currently unchecked typecast. Possibly we should check class first?
			}
		}

		return results;		

	}



	/**
	 * Returns objects on this scene of a specific type.
	 * This is part of the process of phasing out usage of individual lists, which were barely used.
	 * NOTE: this will do a unchecked typecast to the type specified by the what your setting this equal too.
	 * Make sure the object type requested matchs
	 */
	public <T extends SceneObject> Set<T> getScenesCurrentObjectsOfType(SceneObjectType type) {

		Set<T> results = new HashSet<T>();
		Set<SceneObject> allScenesObjects =  allScenesCurrentObjects(); //Note eventually this function will just be returning a arraylist directly



		for (SceneObject sceneObject : allScenesObjects) {
			if (sceneObject.getObjectsCurrentState().getPrimaryObjectType()==type){
				results.add((T) sceneObject); //currently unchecked typecast. Possibly we should check class first?
			}
		}

		return results;		

	}



	protected void extractParams(String sourceParam) {

		//split by line
		String[] sourceArray = sourceParam.split("\r");
		int len = sourceArray.length;
		int i=0;
		while (i<len){

			String currentline = sourceArray[i].trim();
			i++;
			//if its empty or contains no =, then skip it
			if ((currentline.length()<1) ||(!currentline.contains("=") )){
				continue;
			}


			//split by ' = '
			String param = currentline.split("=")[0].trim().toLowerCase();
			String value = currentline.split("=")[1].trim();

			//trim quotes from start and end
			if (value.startsWith("\"")){
				value = value.substring(1, value.length()-1);
			}
			if (value.startsWith("\'")){
				value = value.substring(1, value.length()-1);
			}

			Log.info("param="+param);
			Log.info("value="+value);

			switch (param) {
			case "description":
				Description = value;
				Log.info("Description="+Description);
				break;
			case "title":
				Title  = value;
				Log.info("Title ="+Title);
				break;
			case "containerbackground":
				containerBackground  = value;
				Log.info("ContainerBackground ="+containerBackground);
				break;
			case "scenebackground":
				//scene relative 

				currentState.currentBackground  = scenesWidget.SceneFolderLocation +"/" + value;
				Log.info("SceneBackground ="+currentState.currentBackground);
				break;
			case "backgroundrepeat":
				currentBackgroundRepeat  = value;
				Log.info("ContainerBackgroundRepeat ="+currentBackgroundRepeat);
				break;
			case "location":
				//used to be just  locx  and locy used
				currentState.PosX = Integer.parseInt(value.split(",")[0].trim());
				currentState.PosY  = Integer.parseInt(value.split(",")[1].trim());
				Log.info("Got Loc ="+currentState.PosX +","+currentState.PosY);
				break;
			case "size":
				InternalSizeX = Integer.parseInt(value.split(",")[0].trim());
				InternalSizeY = Integer.parseInt(value.split(",")[1].trim());
				Log.info("Got Size ="+InternalSizeX+","+InternalSizeY);
				break;
			case "pan":
				if (value.equalsIgnoreCase("y")){
					PanX=false;
					PanY=true;
				}
				if (value.equalsIgnoreCase("x")){
					PanX=true;
					PanY=false;
				}
				if (value.equalsIgnoreCase("none")){
					PanX=false;
					PanY=false;
				}
				Log.info("Got Pan ="+value);
				break;
			case "movementlimits":
				MovementLimitsSX = Integer.parseInt(value.split(",")[0].trim());
				MovementLimitsSY = Integer.parseInt(value.split(",")[1].trim());
				MovementLimitsEX = Integer.parseInt(value.split(",")[2].trim());
				MovementLimitsEY = Integer.parseInt(value.split(",")[3].trim());
				Log.info("MovementLimits start ="+MovementLimitsSX+","+MovementLimitsSY);
				Log.info("MovementLimits end ="+MovementLimitsEX+","+MovementLimitsEY);
				break;
			case "physicsbias":
			{
				SimpleVector3 bias = new SimpleVector3(value);				
				this.physicsBias.set(bias);
				Log.info("physicsBias ="+physicsBias);

			}
			break;
			case "surfacefriction":
			{
				SimpleVector3 bias = new SimpleVector3(value);				
				this.surfacefriction.set(bias);
				Log.info("surfacefriction ="+surfacefriction);

			}

			break;
			}
		}



	}



	/** extract image url's from string and preload them.
	 * We also load attachment files that go with them, if there's any. **/
	//TODO:horrible mess, probably best to split by linesfirst rather then look for each png
	protected void extractAllImageURLs(String allactions, String defaultItemUrl, SceneObject defaultObject) {

		//ensure theres a url, even if its blank
		if (defaultItemUrl==null){
			defaultItemUrl="";
		}
		//remove trailing frame number and extension
		if (defaultItemUrl.contains(".")){
			defaultItemUrl = defaultItemUrl.split("0\\.")[0];
		}

		//make sure there even are actions
		if (allactions.length()<4){

			return;

		}

		//allactions=allactions.toLowerCase();//why???
		//Remove the lower case function as file url's should be case-specific.

		int pos=0;

		/** This runnable is just a lump of code that will log what just loaded and tell the loading to advance.
		 * We pass it to the preloader for each image loaded, so each image will advantage the scenesloading as its received **/
		RunnableWithString advanceLoading = new RunnableWithString(){

			@Override
			public void run(String result) {
				//TODO: maybe maintain an explicit loading list?
				//crop url to filename
				int slashIndex    = result.lastIndexOf("/");
				int slashIndex2   = result.lastIndexOf("\\");
				if (slashIndex2>slashIndex){
					slashIndex=slashIndex2;
				}
				String filename = result;
				if (slashIndex!=-1){
					filename = result.substring(slashIndex+1);

				}



				Log.info(" file "+filename+" loaded(or failed) so we subtract 1 from the loading");
				scenesWidget.advancePhysicalLoading(filename);

				scenesWidget.stepLoadingTotalVisual(1); //advancePhysicalLoading no longer does this itself

			}

		};

		//if when searching for a images url we find it has an object before it, we know this object should supply the images folder
		//if left null we assume the default object, which is the one this set of actions belonged too
		SceneObject objectURLisAppliedtoo=null;

		while(pos<allactions.length()){

			String itemURL=defaultItemUrl;

			int nextImageEnd = allactions.indexOf(".png", pos); //just pngs for now, but we should support jpegs in future somehow
			if (nextImageEnd==-1){
				break;
			}

			int lastNewlineStart = allactions.lastIndexOf("\n",nextImageEnd)+1;
			if (lastNewlineStart==-1){
				lastNewlineStart=0;
			}

			String commandName = allactions.substring(lastNewlineStart, nextImageEnd).trim().toLowerCase();

			//only for setobjecturl, else we skip
			if (commandName.startsWith("- setobjecturl")
					|| commandName.startsWith("-setobjecturl")){

			} else {
				Log.info("command: "+commandName+" not suitable for preloading");
				pos=nextImageEnd+1;
				continue;
			}

			//starts at previous = or ,
			int nextImageStart = allactions.lastIndexOf("=", nextImageEnd)+1;
			int nextImageStartb = allactions.lastIndexOf(",", nextImageEnd)+1;

			nextImageEnd=nextImageEnd+4;

			if (nextImageStartb>nextImageStart){

				nextImageStart=nextImageStartb;
				//if it starts with , then the previous param will be the item it applys too, 
				// and thus the directory to look in

				int locStart = allactions.lastIndexOf("=", nextImageStart)+1;

				String objectName=allactions.substring(locStart, nextImageStart-1).trim();

				Log.info("preloader itemname is for:"+objectName); //note; While the itemname is often the url, this isnt automaticaly the case. Sometimes items can share a image url but have different names (ie, the same image is used many times)
				//because of this we need to get the item and look at its foldername and use that
				// objectURLisAppliedtoo = SceneWidget.getSceneObjectByName(objectName, null)[0];


				objectURLisAppliedtoo= SceneObjectDatabase
						.getSingleSceneObjectNEW(objectName,null,true);

				if (objectURLisAppliedtoo==null){
					Log.info("cant find object "+objectName+" that is going to have its url set (in a statement in object "+defaultItemUrl+"");
					Log.info("dont panic, this just means your likely going to get some 404 and the frames wont be cached before loading. This can happen if a scene does stuff to a scene not yet loaded");
					Log.info("which should be avoided");
				} else {

					itemURL = objectURLisAppliedtoo.folderName;

				}





			}

			Log.info("_nextImageStart:"+nextImageStart);
			String imageURL = allactions.substring(nextImageStart, nextImageEnd).trim();

			Log.info("_Image url found:"+imageURL); //always lower..why?

			//get the number of frames
			int nextComa =  allactions.indexOf(",", nextImageEnd);
			if (nextComa==-1){
				pos=nextImageEnd+1;
				continue;
			}
			nextComa=nextComa+1;

			int nextComaEnd =  allactions.indexOf("\n", nextImageEnd);

			if (nextComaEnd==-1){

				nextComaEnd=allactions.length();

				Log.info("comma end set to length:"+nextComaEnd);
			}
			if (nextComaEnd<nextComa){
				pos=nextImageEnd+1;
				continue;
			}

			//	Log.info("_nextComa"+nextComa);
			//	Log.info("_nextComaEnd"+nextComaEnd);

			pos=nextComaEnd+1;

			String nfs  = allactions.substring(nextComa, nextComaEnd).trim();

			int numFrames;
			try {
				numFrames = Integer.parseInt(nfs);

				Log.info("_numFrames:"+numFrames);

			} catch (NumberFormatException e) {

				//if not a number we ignore this whole url

				Log.info("no frame number found:"+pos);

				continue;
			}
			//ensure not preloading a variable name
			if (imageURL.contains("<")||imageURL.contains(">")){
				Log.info("__filename::"+imageURL+" contains < or > thus its a illegal filename, likely a variable based file setting. We dont preload those");
				continue;
			}

			//now set it preloading

			// set for each frame:	 (old url)
			//String baseURL = scenesWidget.SceneFolderLocation +"/Objects/"+itemURL+"/";


			String bframeName = imageURL.split("0\\.")[0];




			SceneObject currentObject =defaultObject; //SceneWidget.getSceneObjectByName(itemURL, null)[0];
			//get the object this image will belong too
			if (objectURLisAppliedtoo!=null){
				currentObject=objectURLisAppliedtoo;
			}


			Log.info("__frameName:"+imageURL+"__itemURL:"+itemURL+" objectname:"+currentObject.getObjectsCurrentState().ObjectsName);
			String baseURL = SceneWidget.SceneFileRoot + currentObject.getInitialState().ObjectsSceneName+"/Objects/"+itemURL+"/"; //new url method
			//should fix wrong scenename in some cases

			//if theres a equilivent attachment point file	
			//TODO: while having this here collects all potential attachment points for each frame of each each itemURL
			//Its a little odd maybe in regard to the variable that says if they are loaded or not.
			//Should some of this be in sceneobject?
			if (currentObject.getObjectsCurrentState().hasAttachmentPointFile){				
				Log.info("Loading attachment points named:"+baseURL+bframeName+".glu");					
				currentObject.loadAttachmentPoints(baseURL+bframeName+".glu", false,false,null);	//load glu for attachment points for specific url and frame number
			} 


			//load all the frames
			int currentNum = 0;		

			//if we dont have a imagepreloader we skip the following loop and return;
			if (!OptionalImplementations.ImagePreloader.isPresent()){
				Log.info("No Preloader In Use For Images");
				return;
			}

			while(currentNum<numFrames){

				String url = baseURL+bframeName+currentNum+".png";
				boolean successfullyAddedToLoading = OptionalImplementations.ImagePreloader.get().addImageToLoading(url,advanceLoading);
				//SpiffyPreloader.addToLoading(baseURL+bframeName+currentNum+".png",advanceLoading);

				if (successfullyAddedToLoading){
					Log.info("__added to loading::" + url);
					//Log.info("__added to loading (fix?)::" + baseURL_FIX+bframeName+currentNum+".png");					
					scenesWidget.addToLoading(bframeName+currentNum+".png");
				} else {
					Log.info("__did not add to loading::" + baseURL+bframeName+currentNum+".png probably because its already in the preloaders list");

				}

				currentNum++;

			}	

			//ensure preloader has started (no effect if running already)
			Log.info("starting  preloader if its not alreading running");			
			//SpiffyPreloader.preloadList();			
			OptionalImplementations.ImagePreloader.get().startPreloading();
		}



		return;
	}


	//TODO:remove this variable and its assignment when we can, its only used for a scenewidget.getSceneLoadingDebugString() which
	//is to identify where loading went wrong
	public String debugLastObjectData = "";

	/**
	 * the potential items left to process into sceneobjects
	 */
	int TotalItemsNativeToScene =0;
	
	public int getTotalKnowenItems() {
		return TotalItemsNativeToScene;
	}



	/**
	 * Extracts the items into sceneobjects, then runs the associated scenes setup function when done.
	 * NOTE:   This is a asycn operation. Dont expect it finished by the next line.
	 * @param itemsourcefile
	 */
	protected void extractItems(String itemsourcefile) { //incoming string case sensitive still

		//if no items then return
		if (!itemsourcefile.contains("Item:")){
			Log.info("__________empty scene file__________");			
			//SpiffyPreloader.preloadList();
			//ensure preloader is started, if we have one
			if (OptionalImplementations.ImagePreloader.isPresent()){

				OptionalImplementations.ImagePreloader.get().startPreloading();
			}


			return;
		}

		//split by items (ignoring commented out ones)
		final String ItemData[] = itemsourcefile.split("[\\n\\r](?!\\/\\/)( *)-Item:"); //old -Item:|[\n\r]Item:
		//^[^\/\/]-Item:|[\n\r]Item:
		Log.info("Total items found:" + ItemData.length);
		
		
		//Set loading total to ItemData length *2
		//(as it needs to first load the data, and then physically load, for each object)
		//we have to be such to update the load icon for each
		
		TotalItemsNativeToScene = ItemData.length;
		scenesWidget.setLoadingTotal(ItemData.length);
		
		//
		//Start processing each item in ItemData
		//
		//As there might be a lot of items to process for the scene, we use a incremental command to give browser implementations 
		//breathing room. (gwts implementation will run this a few times between giving the screen a update, then running it a few more times)
		//
		JAMTimerController.scheduleIncremental(new IsIncrementalCommand(){
			int itemnum = 0; //start from item zero of course!
			//This function will keep running for as long as it returns true
			@Override
			public boolean run() {

				//more items left?
				boolean moreToProcess = itemnum<ItemData.length;

				if (!moreToProcess){ 
					//If no more left to process...
					
					//We run image preloading and other tidying up
					setImagesPreloadingAndFinnishItemDataProccessing(); //ssets allitems known also tests for loadcomplete
		
				    //Then we are ready to load the scene		
					scenesWidget.setUpScene(SceneData.this); //adds the objects to the scene, then tests for load complete
					scenesWidget.testForLoadComplete();      //we do a single check here in case loading is finnished straight away. (could happen on a local version)
					
					//return false to not run this IsIncrementalCommand again - as its done everything it needs too!
					return false;
				}

				//Get current item based on the counter (itemnum)
				String currentItemData = ItemData[itemnum].trim();
				itemnum++;

				
				//check its data isn't too short
				if (currentItemData.length()<2){
					//scenesWidget.subFromLoadingTotal(); //we have to sub again as each item adds twice to the list, if it wasnt real we thus need to sub twice
					TotalItemsNativeToScene--; //wasnt a real item
					
					return true; //skip to next item in array
				}
				//check data even contains an =
				//if no equals, then theres no parameters or actions! thus no item!
				//The data cut off in the split might not contain an item if an
				//item isnt the first thing in the file.
				//For example, the file might start with a comment
				if (!currentItemData.contains("=")){
					//scenesWidget.subFromLoadingTotal(); //we have to sub again as each item adds twice to the list, if it wasn't real we thus need to sub twice
					TotalItemsNativeToScene--; //Wasn't a real item
					
					return true;//skip to next item in array
				}

				//process the items data into a actual sceneobject
				//the value returned determines if it was successfully made or now.
				//(in some cases items might be skipped)			
				boolean objectAdded = processItemsData(currentItemData,itemnum);

				
				scenesWidget.stepLoadingTotalVisual(0.5f); //we do a half step for processing the data, the other half happens when the item is fuilly loaded
				
				
				return true; //run this function again for the next item
			}

		});

		
		
		//Old method (can be removed once new method above is tested)
		//
		/*
		while(itemnum<ItemData.length){

			//current item
			String currentItemData = ItemData[itemnum].trim();
			itemnum++;

			//check data isn't too short
			if (currentItemData.length()<2){
				continue;
			}
			//check data even contains an =
			//if no equals, then theres no parameters or actions! thus no item!
			//The data cut off in the split might not contain an item if an
			//item isnt the first thing in the file.
			//For example, the file might start with a comment
			if (!currentItemData.contains("=")){
				continue;
			}

			//process the items data into a actual sceneobject
			//thevalue returned determains if it was successfully made or now.
			//(in some cases items might be skipped)			
			boolean objectAdded = processItemsData(currentItemData,itemnum);


		}



		//Preloading and other tidying up
		setImagesPreloadingAndFinnishItemDataProccessing();
		
		//Now we are ready to load the scene		
		this.scenesWidget.setUpScene(this); //adds the objects to the scene
		scenesWidget.testForLoadComplete(); //we do a single check here in case loading is finnished straight away. (could happen on a local version)

		 */


	}



	private void setImagesPreloadingAndFinnishItemDataProccessing() {

		scenesWidget.setLoadingMessage("Processed Object data........."); //update load state message
		
		//use the "allactions" text dump as a base for preloading all the images
		//and other files we might need in the game.
		//we do this after all the objects have been created, as some of the files (like *.glu) need
		//the objects to have already been created.		
		for (SceneObject currentObjectToCacheFrom : rawObjectData.keySet()) {

			Log.info("starting to cache dynamicly changed images from:"+currentObjectToCacheFrom.getObjectsCurrentState().ObjectsName);	
			Log.info("objects file name is:"+currentObjectToCacheFrom.getObjectsCurrentState().ObjectsFileName);	
			Log.info("objects folder name is:"+currentObjectToCacheFrom.folderName);

			//Note; just because we are caching from a particular object doesn't mean
			//the files we are caching are just for that object.
			//For example, if you use a - seturl = command, it can be effecting any sceneobject
			//in the game. The currentObject is only the *default* one to effect if no others
			//are assigned.
			extractAllImageURLs(rawObjectData.get(currentObjectToCacheFrom),currentObjectToCacheFrom.folderName,currentObjectToCacheFrom);

		}

		//preload anything needed
		if (OptionalImplementations.ImagePreloader.isPresent()){	

			OptionalImplementations.ImagePreloader.get().startPreloading();

		} //else {
		//Log.info("No image preloading present, checking if we are loaded straight away");

		//	scenesWidget.testForLoadComplete();//we do a single check here in case loading is finished straight away.
		//Normally the preloader does this check after each image, but as we arnt preloadaing we need to at least check once.

		//	}
		//	SpiffyPreloader.preloadList();


		//clean up everything now not needed
		rawObjectData=null; //should not be reused elsewhere
		//anything else to clean?
		
		
		//ok
		//set data as ready (all objects now knowen)
		allItemsKnown = true;

		//test for load complete (its possible that items load all instantly - if we are offline or have no sprites)
		scenesWidget.testForLoadComplete();
		
	}



	private boolean processItemsData(String currentItemData, int itemnum) {



		//remove -Item: spec if there
		if (currentItemData.startsWith("-Item:")){
			currentItemData=currentItemData.substring("-Item:".length());
		}
		debugLastObjectData=currentItemData;
		// extract data
		Log.info("extracting data from:\r" + currentItemData); //still case sensitive

		// split actions off if present
		// get scene parameters (anything before a line ending with : )
		//int firstLocOfColon = currentItemData.indexOf(':');				
		//int firstLocOfColon = currentItemData.indexOf(":\n"); //:[\s]*\n

		//Log.info("locationOfColon recieved for object:" + firstLocOfColon);

		String Parameters = "";
		ActionList newObjectsActions = null; //given to the objects constructor
		String allactionstring = "";  //needed further done to cache images

		//new split method
		String[] datacomponants = currentItemData.split(":[\\s]*\\n",2);


		if (datacomponants.length==1) {

			Parameters = currentItemData;

		} else {


			Parameters = datacomponants[0].trim();
			allactionstring = datacomponants[1];

			//parameters also includes the start of allactionstring at the end
			//we have to remove this and add it to allactionstring
			//This is a bit stupid, but we need to do this because we cant use "look aheads" in our regex, and if we instead searched for the true start
			//of actions we would lose the line completely

			int linebeforeColon = Parameters.lastIndexOf('\n');
			if (linebeforeColon==-1){
				linebeforeColon=0; //if its the start of the file
			}

			String actionsFirstLine = Parameters.substring(linebeforeColon);

			//Log.info("actionsFirstLine:" + actionsFirstLine);

			Parameters = Parameters.substring(0, linebeforeColon);
			allactionstring=actionsFirstLine+":\n"+allactionstring;

			Log.info("allactionstring:" + allactionstring);

			//set if long enough
			if (allactionstring.length()>3){
				newObjectsActions = new ActionList(allactionstring);
			} 

		}

		//Log.info("datacomponants test (should be 1 or 2 only):" + datacomponants.length);




		//old method
		/*			if (firstLocOfColon == -1) {

				Parameters = currentItemData;

			} else {

				int linebeforeColon = currentItemData.lastIndexOf('\n', firstLocOfColon);

				Parameters = currentItemData.substring(0, linebeforeColon);
				allactionstring = currentItemData.substring(linebeforeColon);

				actions = new ActionList(allactionstring);

			}


		 */



		//new method
		//1. get object type from "Parameters" string
		String ParameterLines[] = Parameters.split("\n");
		SceneObjectType objectsType = SceneObjectState.getTypeFromParameters(ParameterLines);

		//2. make object state of that specific type with ParameterLines,true (which means load the paremters as if from a file)
		SceneObjectState newobjectdata = null;

		switch (objectsType) {
		case DialogBox:
			newobjectdata = new SceneDialogueObjectState(ParameterLines,true);
			break;
		case Div:
			newobjectdata = new SceneDivObjectState(ParameterLines,true);					
			break;
		case Input:
			newobjectdata = new SceneInputObjectState(ParameterLines,true);		
			break;
		case InventoryObject:
			//Inventory Icons shouldnt be made here.
			Log.info("Warning InventoyIcons shouldnt be made here");					
			break;
		case Label:
			newobjectdata = new SceneLabelObjectState(ParameterLines,true);	
			break;
		case SceneObject:
			newobjectdata = new SceneObjectState(ParameterLines,true);					
			break;
		case Sprite:
			newobjectdata = new SceneSpriteObjectState(ParameterLines,true);
			break;
		case Vector:
			newobjectdata = new SceneVectorObjectState(ParameterLines,true);
			break;	

		}

		//3. then run assignObjectTypeSpecificParametersNew() on it
		//TODO: 
		//we can improve this a bit by creating only a blank state, then dealing with all the params ina single loop
		newobjectdata.ObjectsSceneName = scenesWidget.SceneFileName;	//scene name must be supplied on loading, as it doesn't come from the parameters			
		newobjectdata.assignObjectTypeSpecificParametersNew(ParameterLines);					
		//---

		Log.info(" new objectsType:" + objectsType);
		Log.info("extractData done objectsType:"+newobjectdata.getPrimaryObjectType());

		//newobjectdata.getPrimaryObjectType();
		
		
		if (newobjectdata.getPrimaryObjectType()==SceneObjectType.Sprite){
			Log.info("extractData done :"+newobjectdata.serialiseToString());

		}

		if (!objectsType.equals(newobjectdata.getPrimaryObjectType())){
			Log.severe("WARNING_____________TYPES___________DONT__________________MATCH__________ERROR");
		}


		//test to ensure we should use the object
		//for example, the object will flag as dont use if the quality setting of the game
		//is lower then the object specifies
		if (newobjectdata.DONT_USE_THIS_OBJECT){

			Log.info("(Not creating object due to games html quality setting not enough to support it");
			//remove reference to objects data to ensure nothings done with it
			newobjectdata=null;

			return false;
		}



		//set object to this scene by default
		newobjectdata.ObjectsSceneName = scenesWidget.SceneFileName;


		//set its zindex if its on auto
		if (newobjectdata.zindex==-1){	
			Log.info("setting auto zindex based on element number, later ones are thus higher");
			newobjectdata.zindex = itemnum;

		}



		//now create the object!
		SceneObject newObject =  SceneObjectFactory.createNewObjectFromData(newObjectsActions, newobjectdata,scenesWidget);
		//The object internally should add itself to logical loading
		//For the scene to finish loading, it will also have to remove itself from this list.

		//This lets the scene know it still might have to wait for this object to be fully ready to be used logically.
		//ie. Collisionmap is still loading.
		//Its the objects own responsibility to remove itself from the queue with the matching advance command.
		//
		//Additionally; Its possible for logical loading to finish on each, and indeed all, objects as they are created
		//So its important to check for loading being finished after extract items.
		//(loading cant finish while items are still being extracted)
		//---------------------------------


		if (newObject==null){
			Log.severe("Object "+newobjectdata.ObjectsName+" of type:"+newobjectdata.getPrimaryObjectType()+" could not be constructed. Does it have a factory method?");
			Log.severe("It was also of types:"+newobjectdata.getCapabilitiesAsString());

		}

		Log.info("adding new objects to lists");			
		scenesOriginalObjects.add(newObject);

		addToScenesObjects(newObject);
		//The following now handled in the addToScenesObjects function
		/*
			 scenesCurrentObjects.add(newObject);

			//if it supports clicks while its behind stuff, we also add it to the special "clicked while behind" list
			if (newObject.objectsActions.hasActionsFor(TriggerType.OnClickedWhileBehind)){				
				scenesObjectsThatSupportClicksWhileBehind.add(newObject);
			}
		 */


		if (newobjectdata.positionedRelativeToo!=null){					
			newObject.ObjectsLog("assigning position relative to:"+newobjectdata.positionedRelativeToo.getName());
			newobjectdata.positionedRelativeToo.addChild(newObject); //.relativeObjects.add(newObject);					
		} else {
			newObject.ObjectsLog("no object to position relative to present yet or set");
			newObject.ObjectsLog("positionRelativeToOnceLoaded:"+newobjectdata.positionRelativeToOnceLoaded);
		}

		//load attachment points if there's any (spites deal with this loading themselves, 
		//as they need to change per frame
		if (newobjectdata.hasAttachmentPointFile && newobjectdata.getPrimaryObjectType()!=SceneObjectType.Sprite){

			String atachmentPointUrl = "Game Scenes/"+newobjectdata.ObjectsSceneName
					+ "/Objects/" + newObject.folderName + "/"+ newObject.getObjectsCurrentState().ObjectsName +".glu";

			Log.info("Loading attachment Points for non-sprite object");
			newObject.getAttachmentPointMovements(atachmentPointUrl);
		}

		rawObjectData.put(newObject, allactionstring);

		//extract all urls from allactions (ie, other images to cache)


		int numOfActions = newObject.objectsActions.size();
		newObject.ObjectsLog("Object created. Has "+numOfActions+" actions");
		if (numOfActions==0){
			newObject.ObjectsLog("datacomponants.length was:"+datacomponants.length);
		}


		return true;
	}


	boolean allItemsKnown = false;

	/**
	 * all sceneobjects are extracted.
	 * This doesnt mean, however, they are loaded (either physically or logically)
	 * merely we have a list of all the scene items
	 * @return
	 */
	public boolean isAllItemsKnown() {
		return allItemsKnown;
	}



	/**
	 * sets up the data and fires the assocated scenes setup when its done.
	 * Note; This is a asycn operation. Dont expect it finnished by the next line.
	 */
	public void initialize() {
		
		Log.info("dealing with objects:"+itemssourcefile);
		extractItems(itemssourcefile);		

	}



	//TODO: cache this? use SimplePoint instead?
	/**
	 * returns the center of the movement limits, if they have been set, else returns scene centerx
	 * @return
	 */
	public int getCenterOfMovementLimitsX() {

		if (MovementLimitsSX==-1){
			int centerX = (int) (InternalSizeX/2.0);
			return centerX;
		} else {
			int centerX = (int) (MovementLimitsSX+((MovementLimitsEX - MovementLimitsSX)/2.0));
			return centerX;
		}

	}
	//TODO: cache this? use SimplePoint instead?
	/**
	 * returns the center of the movement limits, if they have been set, else returns scene centery
	 * @return
	 */
	public int getCenterOfMovementLimitsY() {

		if (MovementLimitsSY==-1){
			int centerY = (int) (InternalSizeY/2.0);
			return centerY;
		} else {
			int centerY = (int) (MovementLimitsSY+((MovementLimitsEY - MovementLimitsSY)/2.0));
			return centerY;
		}

	}



	/**
	 * for debugging
	 * @return
	 */
	public String getMovementLimitsAsString() {
		return this.MovementLimitsSX+","+this.MovementLimitsSY+" > "+this.MovementLimitsEX+","+this.MovementLimitsEY;
	}




}
