package com.lostagain.Jam.SceneObjects;

/** same as a dialogue state, only it ensures the correct object type is set on creation 
 * 
 * This is really backwards, we need to somehow make dialogue extend text and have text do everything except
 * paragraph handeling**/
//Replaced with SceneLabelObject
@Deprecated
public class SceneTextObjectState extends SceneDialogueObjectState {

	
	

	public SceneTextObjectState(String serialised) {
					
		super(serialised);
		//super.getCurrentType() = SceneObjectType.Label;
		super.setObjectsPrimaryType(SceneObjectType.Label);
	}

	public SceneTextObjectState() {
		super();
		super.setObjectsPrimaryType(SceneObjectType.Label);
	}

	public SceneTextObjectState(SceneDivObjectState genericState, String sizeX,
			String sizeY, String objectsText, String objectsURL,
			String cSSname, String currentparagraphName,
			int currentNumberOfParagraphs, int currentParagraphPage,
			String NextParagraphObject, cursorVisibleOptions cursorVisible,
			TrueFalseOrDefault typedText, String Custom_Key_Beep, String Custom_Space_Beep) {
		//sizeX, sizeY,
		super(genericState,  objectsText, objectsURL, cSSname,
				currentparagraphName, currentNumberOfParagraphs, currentParagraphPage,
				NextParagraphObject, cursorVisible, typedText, Custom_Key_Beep,
				Custom_Space_Beep);
		super.setObjectsPrimaryType(SceneObjectType.Label);
	}

	public SceneTextObjectState(SceneObjectState state) {
		super(state);
		super.setObjectsPrimaryType(SceneObjectType.Label);
	}

	public SceneTextObjectState(String objectsText, String objectsURL,
			String sizeX, String sizeY, String cSSname,
			String currentparagraphName, int currentNumberOfParagraphs,
			int currentParagraphPage, String NextParagraphObject,
			cursorVisibleOptions cursorVisible, TrueFalseOrDefault typedText, String Custom_Key_Beep,
			String Custom_Space_Beep) {
		
		super(objectsText, objectsURL, sizeX, sizeY, cSSname, currentparagraphName,
				currentNumberOfParagraphs, currentParagraphPage, NextParagraphObject,
				cursorVisible, typedText, Custom_Key_Beep, Custom_Space_Beep);
		super.setObjectsPrimaryType(SceneObjectType.Label);
	}

}
