package com.lostagain.Jam.SceneObjects.Interfaces;

import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;

public interface IsSceneSpriteObject extends IsSceneObject { 

	/**
	 * 
	 * @param settingNewAnimaion -
	 *  true = assumes ObjectUrl is specifying the first frame of a new animation
	 * false = assumes your specifing url directly, including frame number within settingNewAnimaion
	 */
	void setURLPhysically(boolean settingNewAnimaion);
	

	void setFrameGap(int gap);

	//TODO: this should somehow be in core so things implementing this just need to implement the animation change itself, not the state change stuff
	void setAnimationStatus(String state);
	
	
	

	/**
	 * Should get a string that represents the current animation state
	 * (including frame,animation direction and if its looping)
	 * should mirror the setAnimationState function
	 * running this function should also update the animation state in SceneSpriteObjectState to match
	 */
	String getSerialisedAnimationState();
	
	/**
	 * returns the current frame (should also set currentframe variable in the state if its not uptodate)
	 * @return
	 */
	int getCurrentFrame();
	
	/**
	 * returns the current url (should also set currenturl variable in the state if its not uptodate)
	 * @return
	 */
	String getCurrentURL();
	
	
	/**
	 * updates the state of the object to match the data supplied
	 * 
	 * @param sceneObjectData
	 * @param runOnLoad
	 * @param repositionObjectsRelativeToThis
	 */
	void updateState(SceneSpriteObjectState sceneObjectData, boolean runOnLoad, boolean repositionObjectsRelativeToThis);
	
	
	
	//subclasses have to override these to provide their own more specific types
	@Override	
	public SceneSpriteObjectState getObjectsCurrentState();
	@Override
	public SceneSpriteObjectState getTempState();
	@Override
	public SceneSpriteObjectState getInitialState();

	


	

	

	
}