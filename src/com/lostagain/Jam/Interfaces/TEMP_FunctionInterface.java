package com.lostagain.Jam.Interfaces;

import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneObject;

/**
 * a temp interface purely for Instructions we cant yet move over to the core, but should be
 * This whole function will eventually be moved into the core so this, and its set method, wont be needed
 * 
 * @author darkflame
 *
 */
public interface TEMP_FunctionInterface {
	
//	public void processInstructions(CommandList commandsToRun,String UniqueTriggerIndent, IsSceneObject ObjectThatCalledThis);
	
	//object for now will be cast to CommandList, as soon as command list is in Core this can be changed back to commandList
	//public void processInstructionsImpl(Object commandsToRun,String UniqueTriggerIndent, IsSceneObject ObjectThatCalledThis);

	//public void processInstructionsImpl(String command,String UniqueTriggerIndent, IsSceneObject ObjectThatCalledThis);

	//
	//public void testForGlobalActions(TriggerType type, String Parameter, IsSceneObject sourceObject);
	//public void testForGlobalActions(Object type, String Parameter, IsSceneObject sourceObject);
	
	///**
	// * process the string as if the user has entered it into the answer box
	// * @param ans
	// */
	//public void AnswerGiven(String ans);
	
	/**
	 * just trigger 
			SaveGameManager.deseraliseAndRestoreStateFromString(CurrentProperty);
	 * @param ans
	 */
	//public void deseraliseAndRestoreStateFromString(String ans);
	
	
}
