package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;
import com.lostagain.Jam.SceneObjects.SceneVectorObjectState;

public interface IsSceneVectorObject extends IsSceneObject {

	
	/**
	 * changes the vectors shape to the vector string specified
	 * @param vectorstring
	 **/
	void setNewVectorString(String vectorstring);

	public String getCurrentVectorString();

	

	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneVectorObjectState getObjectsCurrentState();
	@Override
	public SceneVectorObjectState getTempState();
	@Override
	public SceneVectorObjectState getInitialState();


	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneVectorObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);


	

}