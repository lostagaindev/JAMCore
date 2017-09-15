package com.lostagain.Jam.SceneObjects.Helpers;

import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.lostagain.Jam.DialogueCollection;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.SceneDialogueObjectState;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneDialogueObject;

/**
 * HELPER METHODS FOR dialogue OBJECTS
 * 
 * @author darkflame
 *
 */
public class DialogueObjectHelper {

	public static Logger	Log								= Logger.getLogger("JAMCore.DialogueObjectHelper");

	/**
	 * HELPER METHOD FOR dialogue OBJECTS<br>
	 * 
	 * @param newurl
	 * @param dialogue
	 **/
	public static void setDialogueURL_Helper(String url, final IsSceneDialogueObject dialogue_object,boolean useCache) {
		Log.info("________getting url:"+url);

		final SceneDialogueObjectState dialogue_objects_state = dialogue_object.getObjectsCurrentState();

		//set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable(){

			@Override
			public void run(String responseData, int responseCode) {
				Log.info("________got:"+responseData);

				// check response is not an error
				if (responseCode == 404) {
					Log.info("________no text file recieved (404):\n");
					return;
				}
				//

				String text = responseData;

				//make sure all file names are language specific				
				text = JAMcore.parseForLanguageSpecificExtension(text);

				// swap TextIds for text
				text = JAMcore.parseForTextIDs(text);


				// split into paragraphs
				Log.info("create DialogueCollection");
				DialogueCollection knownParagraphs = new DialogueCollection(text);
				
				Log.info("setKnownParagraphs:");
				dialogue_object.setKnownParagraphs( knownParagraphs );

				Log.info("setting current number of paragraphs for "	+ dialogue_objects_state.currentparagraphName);

				dialogue_objects_state.currentNumberOfParagraphs = dialogue_object.getKnownParagraphs().getNumberOfParagraphs(dialogue_objects_state.currentparagraphName);

				Log.info("num is "+dialogue_objects_state.currentNumberOfParagraphs );

				//is it correct to start from p0?
				dialogue_objects_state.currentParagraphPage =0;

				Log.info("Now setting text..........");

				if (dialogue_objects_state.currentparagraphName.length()>1){

					//we log the current name and paranumber
					//note; a paranumber is not, like Ghostpi.
					Log.info("setting newly loaded file to paragraph "+dialogue_objects_state.currentparagraphName +" with paranumber "+dialogue_objects_state.currentParagraphPage);

					dialogue_object.ObjectsLog("setting newly loaded file to paragraph "+dialogue_objects_state.currentparagraphName +" with paranumber "+dialogue_objects_state.currentParagraphPage);

					//set the text to the currently set  paragraph
					dialogue_object.setText(""
							+ dialogue_object.getKnownParagraphs().getText(dialogue_objects_state.currentparagraphName, dialogue_objects_state.currentParagraphPage));

				} else {
					Log.info("setting newly loaded file to default paragraph:");

					dialogue_object.ObjectsLog("setting newly loaded file to default paragraph:");

					dialogue_object.setText(""
							+ dialogue_object.getKnownParagraphs().getText("default", 0));
				}
			}

		};

		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){

			@Override
			public void run(String errorData, Throwable exception) {
				Log.info("________no text file recieved:\n"+exception.getLocalizedMessage()+"\n"+errorData);
				dialogue_object.setText(exception.getLocalizedMessage());
			}

		};


		RequiredImplementations.getFileManager().getText(SceneWidget.SceneFileRoot
				+ dialogue_object.getParentScene().SceneFileName + "/Text/" + url,
				true,
				onResponse,
				onError,
				false,
				useCache);

	}


	/**
	 * Sets the paragraph to the approciate number (assuming its in range), updates the state, then tells the engine this was the last dialogue updated <br>
	 * Oh, also updates the debugwindow if theres one
	 * 
	 * @param num
	 * @param dialogue_object
	 */
	public static void setParagraph_Helper(int num, final IsSceneDialogueObject dialogue_object) {

		final SceneDialogueObjectState dialogue_objects_state = dialogue_object.getObjectsCurrentState();

		dialogue_object.ObjectsLog("setting Paragraph to "+num);

		if (num <= (dialogue_objects_state.currentNumberOfParagraphs - 1)) {
			dialogue_objects_state.currentParagraphPage = num;

			dialogue_object.ObjectsLog("(setting text after set paragraph requested)");

			dialogue_object.setText(""
					+ dialogue_object.getKnownParagraphs().getText(
							dialogue_objects_state.currentparagraphName,
							dialogue_objects_state.currentParagraphPage));

		}

		dialogue_object.wasLastObjectUpdated();

		dialogue_object.updateDebugInfo();


	}

	/**
	 * Note; Classes using a animated typeing effect should only use this method if the object is not currently typeing.
	 * If it is, they should skip to the end instead of going to the next paragraph (and thus not use this method)
	 * 
	 * @param num
	 * @param dialogue_object
	 */
	public static void nextParagraph_Helper(final IsSceneDialogueObject dialogue_object) {
		final SceneDialogueObjectState dialogue_objects_state = dialogue_object.getObjectsCurrentState();

		dialogue_object.wasLastObjectUpdated();

		//if not typing goto next paragraph if there is one
		//
		//(trying to goto)   <= (where we can goto) 
		// eg, (currentparagraph + 1) <= (total - 1)
		//(4+1) <= (5-1) 
		if ((dialogue_objects_state.currentParagraphPage+1) <= (dialogue_objects_state.currentNumberOfParagraphs - 1)) {
			//+1 added
			Log.info("currentParagraph was at:"+dialogue_objects_state.currentParagraphPage);

			dialogue_objects_state.currentParagraphPage++;


			Log.info("currentParagraph is  at:"+dialogue_objects_state.currentParagraphPage);

			dialogue_object.ObjectsLog("(Setting Text after next paragraph requested)");
			dialogue_object.setText("" +  dialogue_object.getKnownParagraphs().getText(
					dialogue_objects_state.currentparagraphName,
					dialogue_objects_state.currentParagraphPage));

			return;
		} else {

			dialogue_object.ObjectsLog("Requested paragraph out of range:"+(dialogue_objects_state.currentParagraphPage+1)+" requested out of "+dialogue_objects_state.currentNumberOfParagraphs);
			Log.info("Requested paragraph out of range:"+(dialogue_objects_state.currentParagraphPage+1)+" requested out of "+dialogue_objects_state.currentNumberOfParagraphs);
			Log.info("Paragraph name is  :"+dialogue_objects_state.currentparagraphName);
		}


	}

	public static void previousParagraph_Helper(final IsSceneDialogueObject dialogue_object) {
		final SceneDialogueObjectState dialogue_objects_state = dialogue_object.getObjectsCurrentState();

		if (dialogue_objects_state.currentParagraphPage > 0) {
			dialogue_objects_state.currentParagraphPage--;

			dialogue_object.ObjectsLog("(setting text after previous paragraph requested)"					);

			dialogue_object.setText(""
					+ dialogue_object.getKnownParagraphs().getText(
							dialogue_objects_state.currentparagraphName,
							dialogue_objects_state.currentParagraphPage));
		}
		//SceneObjectDatabase.lastTextObjectUpdated = this;

		dialogue_object.wasLastObjectUpdated();


	}


	static public void setParagraphName_Helper(String name,boolean TriggerNow,final IsSceneDialogueObject dialogue_object) {


		SceneDialogueObjectState dialogue_objects_current_state = dialogue_object.getObjectsCurrentState();

		dialogue_object.ObjectsLog("Setting paragraph to:"+name+" triggernow = "+TriggerNow);


		dialogue_objects_current_state.currentParagraphPage = 0;
		Log.info("paragraph name was "+dialogue_objects_current_state.currentparagraphName+" is now set too "+name);

		dialogue_objects_current_state.currentparagraphName = name;

		//update number of paragraphs if paragraphs are loaded
		//if they arnt loaded we assume its 1 page (this will auto fix to 
		//the correct number when loading is finished)
		if (dialogue_object.getKnownParagraphs() !=null){
			dialogue_objects_current_state.currentNumberOfParagraphs =  dialogue_object.getKnownParagraphs().getNumberOfParagraphs(dialogue_objects_current_state.currentparagraphName);
		} else {
			dialogue_objects_current_state.currentNumberOfParagraphs = 1;
		}


		Log.info("This new paragraphset has "+dialogue_objects_current_state.currentNumberOfParagraphs+" paragraphs ");

		if (TriggerNow){
			dialogue_object.setText(""+ dialogue_object.getKnownParagraphs().getText(
					dialogue_objects_current_state.currentparagraphName,
					dialogue_objects_current_state.currentParagraphPage));
		} else {
			Log.info("New paragraphname set for next NextParagraph(), unless a new setURL happens first");

			dialogue_objects_current_state.currentParagraphPage = -1;

			dialogue_object.ObjectsLog("Paragraph set to:"+dialogue_objects_current_state.currentparagraphName+":"+
					dialogue_objects_current_state.currentParagraphPage+", but not set to appear yet");
		}

		Log.info("paragraph number is set to "+dialogue_objects_current_state.currentParagraphPage);		



		dialogue_object.wasLastObjectUpdated();


	}




}
