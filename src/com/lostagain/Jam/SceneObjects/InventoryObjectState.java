package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

/**
 * 
 * Essentially the same as a spites state but correctly assigns the type to inventory.
 * 
 * 
 * @author Tom
 *
 */
public class InventoryObjectState extends SceneSpriteObjectState {
	static Logger Log = Logger.getLogger("JAMCore.InventoryObjectState");
	
	public SceneObject associatedSceneObject = null;
	public String associatedSceneObjectWhenNeeded = ""; //in case the object wasn't loaded yet
	/**
	 * gets the associated scene object with this InventoryObjectState, assuming there is one
	 * 
	 * @return
	 */
	public SceneObject getAssociatedSceneObject() {

		if (associatedSceneObject==null && !associatedSceneObjectWhenNeeded.isEmpty()){
			associatedSceneObject = SceneObjectDatabase.getSingleSceneObjectNEW(associatedSceneObjectWhenNeeded,null,true);			 
		}

		return associatedSceneObject;
	}



	public InventoryObjectState(String parametersFromFile[], boolean InterpretAsIniData) {
		super(parametersFromFile, InterpretAsIniData);
		super.setObjectsPrimaryType(SceneObjectType.InventoryObject);

	}
	
	public InventoryObjectState(SceneSpriteObjectState spriteState, String associatedSceneObjectWhenNeeded, SceneObject so) {
		super(spriteState);

		this.associatedSceneObject = so;
		this.associatedSceneObjectWhenNeeded = associatedSceneObjectWhenNeeded;
		super.setObjectsPrimaryType(SceneObjectType.InventoryObject); //subtypes should also run this statement straight after the super command in order to override their parents type and set their own as the primary/

	}


	//public InventoryObjectState(SceneDivObjectState state, String ObjectsURL,
	//		String currentlyAnimationState, int currentNumberOfFrames,
	//		int currentFrame) {
	//	super(state, ObjectsURL, currentlyAnimationState, currentNumberOfFrames,
	//			currentFrame);
	//	super.setObjectsPrimaryType(SceneObjectType.InventoryIcon);
	//}

	public InventoryObjectState(SceneObjectState state) {
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.InventoryObject);
	}



	/**
	 * Create a inventory icon state from serialized object data
	 * 
	 * @param serialised
	 */
	public InventoryObjectState(String serialised) {
		super();
		super.setObjectsPrimaryType(SceneObjectType.InventoryObject);

		deserialise(serialised);
	//	Log.info("\n\n test associated:"+associatedSceneObjectWhenNeeded);
	}



	/**
	 * Loads the parameters supplied into this state object.
	 * This is used as part of the deserialization functions	 * 
	 **/	
	@Override
	protected void loadParameters(Iterator<String> incomingdata){

		//load the parameters of the parent type first
		super.loadParameters(incomingdata);

		//load sprite specific data				
		Log.info("Loading inventory specific object data");

		// then the input specific data
		String associatedObjectsName = incomingdata.next();

		if (!associatedObjectsName.isEmpty()){
			associatedSceneObject =  SceneObjectDatabase.getSingleSceneObjectNEW(associatedObjectsName,null,true);
			
			//If the associated object isn't loaded we can flag a string with the name to get it when needed
			if (associatedSceneObject==null){

				associatedSceneObjectWhenNeeded = associatedObjectsName;

				Log.info("associated object not found yet, so storing name instead:"+associatedSceneObjectWhenNeeded);
			}
		}

		Log.info("Got Inventory Fields");

	}

	@Override
	public InventoryObjectState copy(){

		//This will make a SceneSpriteObjectState as thats are parent class
		SceneSpriteObjectState genericCopy = super.copy(); 

		//then generate a copy of this specific data using it (which is easier then specifying all the fields
		//Separately like we used too)
		InventoryObjectState newObject = new InventoryObjectState(genericCopy,
				associatedSceneObjectWhenNeeded,associatedSceneObject );

		return newObject;
	}

	@Override
	public boolean sameStateAs(SceneObjectState state){
		boolean parentTheSame = super.sameStateAs(state);
		if (!parentTheSame){
			return parentTheSame;
		}
		
		//now check label specific fields
		InventoryObjectState asInventoryObjectState = (InventoryObjectState) state;
		

		if (!asInventoryObjectState.associatedSceneObjectWhenNeeded.equals(associatedSceneObjectWhenNeeded)){
			return false;
		}
		if (asInventoryObjectState.associatedSceneObject!=associatedSceneObject){
			return false;
		}
		
		Log.info("All SceneVectorObjectState object data is the same between these two states");
		
		return true;		
	}
	/**
	 * should be overriden by subclasses
	 * @param objectsParamatersFromFile2
	 */
	@Override
	public void assignObjectTypeSpecificParametersNew(String[] objectsParamatersFromFile2) {
		super.assignObjectTypeSpecificParametersNew(objectsParamatersFromFile2);
		Log.info("SETTING UP InventoryIcon SPECIFIC PARAMETERS");		
		assignInventoryObjectTypeSpecificParameters(objectsParamatersFromFile2);
	}


	private void assignInventoryObjectTypeSpecificParameters(String[] objectsParamatersFromFile2) {
		// none (Because this generated from parameters at the moment! )

	}

	@Override
	protected ArrayList<Object> getAllFieldsAsArrayList() {

		// first we get all the global fields.
		// That is, ones that apply to both Sprites,Text and any other object types.
		ArrayList<Object> fieldList = super.getAllFieldsAsArrayList();

		//then we get the sprite specific fields
		Log.info("getting inventory specific fields.");
		
		if (associatedSceneObject!=null){
			fieldList.add(associatedSceneObject.getName());
		} else if (!associatedSceneObjectWhenNeeded.isEmpty()){
			Log.info("no object found, using name "+associatedSceneObjectWhenNeeded);
			fieldList.add(associatedSceneObjectWhenNeeded);			
		} else {
			Log.info("(no associated object or name found)");
			fieldList.add("");			
		}

		Log.info("Got inventory fields:"+fieldList.size()+" total fields");

		return fieldList;

	}



}
