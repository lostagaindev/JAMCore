package com.lostagain.Jam.Scene;

import java.util.HashSet;
import java.util.logging.Logger;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Sets;


/** contains the current status of the scene itself (objects contain their own history data)**/
public class SceneStatus {

	static Logger Log = Logger.getLogger("JAM.SceneStatus");
	
	
	
public int NumOfTimesPlayerHasBeenHere=0; //not yet updated correctly. Should be made to work and then replace hasNotBeenMadecurrent at sonme point




/** flag for the first time its  made current.
 * ie, for sceneDebut triggers and for seeing if data needs to be saved for this scene **/
public boolean hasNotBeenCurrentYet=true; 


final static String deliminator = "_#_";

	//Scenes Background (PNG or JPG background image)
	public String currentBackground;

	//overlay settings NOTE: Overlays seem to mess up some click actions right now (when holding sometihng). This should be fixed if overlays are reimplemented with simple pointer-events
	public String DynamicOverlayCSS = "OFF"; //off by default
	public String StaticOverlayCSS = "OFF";  //off by default (note; null would mean "leave as current" in states, we thus need a string reserved to represent OFF	
	
	public String SceneName;
	
	/** A list of dependent scenes - these scenes must be loaded first (but not necessarily their states) before this
	 * scenes state should be loaded.
	 * This is because objects can move from scene too scene, if the scene this state refers too loads first, but it uses something that
	 * originally came from another scene, then it must wait for that scene to load first so it knows the objects fixed details. (such as its actions)
	 */
	HashSet<String> dependantSceneNames = new HashSet<String>();
	
	
	
	/**Note: currently this is used for scene set up only; dont expect it to be in sycn with
	 * the current SpffyDragPanels location if the panel is moving.
	 * Its set to the destination only at the start of movement triggered by SceneWidget**/
	public int PosX=-1,PosY = -1;
	
	public int getPosX() {
		return PosX;
	}



	public int getPosY() {
		return PosY;
	}



	public SceneStatus(String SceneName,int PosX,int PosY,int numOfTimesPlayerHasBeenHere,
			String currentBackground, String dynamicOverlayCSS,
			String staticOverlayCSS,boolean hasNotBeenMadeCurrent,HashSet<String> dependantSceneNames) {
		super();
		this.SceneName = SceneName;
		this.PosX = PosX;
		this.PosY = PosY;
	
		this.NumOfTimesPlayerHasBeenHere = numOfTimesPlayerHasBeenHere;
		this.currentBackground = currentBackground;
		this.DynamicOverlayCSS = dynamicOverlayCSS;
		this.StaticOverlayCSS = staticOverlayCSS;		
		this.hasNotBeenCurrentYet=hasNotBeenMadeCurrent;
		this.dependantSceneNames=new HashSet<String>(dependantSceneNames);
		
	}
	
	

	public SceneStatus() {
	}

	


	public SceneStatus(String sceneData) {
		
		// deserialise data
		deseralise(sceneData);
	}
	
	public SceneStatus clone(){
		
		return new SceneStatus(SceneName, PosX, PosY,
				NumOfTimesPlayerHasBeenHere,
				 currentBackground, 
				 DynamicOverlayCSS,
				 StaticOverlayCSS, 
				 hasNotBeenCurrentYet,
				 dependantSceneNames);
		
	}

	public void deseralise(String sceneData) {
	
		String[] data = sceneData.split(deliminator); //careful for regex

		SceneName = data[0];

		PosX =  Integer.parseInt(data[1]);
		PosY =  Integer.parseInt(data[2]);
		 NumOfTimesPlayerHasBeenHere = Integer.parseInt(data[3]);
		 currentBackground= data[4];
		 DynamicOverlayCSS= data[5];
		 StaticOverlayCSS = data[6];
		 hasNotBeenCurrentYet=Boolean.parseBoolean(data[7]);
		 
		 //add dependantSceneNames!
		 String sequence = data[8];
		 if (sequence!=null && !sequence.isEmpty()){
		 dependantSceneNames = Sets.newHashSet(Splitter.on(',').trimResults().split(sequence)); //convert coma seperated strings to a hashset. Thanks guava!
		 }
		 
	}



	public String seralise() {
		Log.info("_____________________________________________________serialising scene data");

		String[] data = new String[9]; //used to be 6? what? hasNotBeenCurrentYet was never set. Maybe it was never needed too as only things that had been current before had been saved and thus it was always false? With dependant scenes thats no longer true though

		// convert to array
		data[0] = SceneName;
		data[1] = ""+PosX;
		data[2] = ""+PosY;
		data[3] = String.valueOf(NumOfTimesPlayerHasBeenHere);
		data[4] = currentBackground;
		data[5] = DynamicOverlayCSS;
		data[6] = StaticOverlayCSS;
		data[7] =  String.valueOf(hasNotBeenCurrentYet);
		//Guavas joiner will put a collection of strings together separated by what you specify
		//(the opersite of Splitter!)
		data[8] = Joiner.on(",").join(dependantSceneNames); //dependantSceneNames.toString();
				
		String serialised = "";

		// loop over array to serialize
		for (String string : data) {

			if (string != null) {
				serialised = serialised + string + deliminator;
			} else {
				serialised = serialised + deliminator;
			}

		}
		return serialised;
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
 * @param SceneFileName
 */
	public void addSceneDependancy(String SceneFileName) {

		dependantSceneNames.add(SceneFileName);
	}
	
	/**
	 * This will remove a scenes dependancy on another.
	 * Remember to ensure NO objects from the other scenes are on this scene before removing their dependency.
	 * 
	 * @param objectScene
	 */
	
	public void removeSceneDependancy(SceneWidget objectScene) {

		dependantSceneNames.remove(objectScene.SceneFileName);
	}
	
	

	public boolean allDependancysLoaded(){
	
		
		if (dependantSceneNames.size()==0){
			
			return true;
		}
		
		//loop over ensuring all dependencies are loaded		
		for (String sceneName : dependantSceneNames) {
			
			SceneWidget sceneToTest = SceneWidget.getSceneByName(sceneName);
			
			//if it doesn't exist, or its still loading we return false
			if (sceneToTest==null || sceneToTest.Loading){
				
				Log.info("scene dependancy for "+this.SceneName+" not loaded:"+sceneToTest.SceneFileName);
								
				return false;
			};
			
		}
		
		
			
		return true;
	}
	
}
