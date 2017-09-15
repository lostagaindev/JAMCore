package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.SceneObjects.SceneInputObjectState;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;

public interface IsSceneInputObject extends IsSceneObject {

	/**
	 * Sets if this input box is readonly or not
	 * @param value
	 */
	void setReadOnly(Boolean value);
	
	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneInputObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);
	
	

	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneInputObjectState getObjectsCurrentState();
	@Override
	public SceneInputObjectState getTempState();
	@Override
	public SceneInputObjectState getInitialState();

}