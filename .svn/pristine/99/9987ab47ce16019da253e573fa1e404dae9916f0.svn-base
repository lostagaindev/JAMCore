package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lostagain.Jam.Factorys.IsTimerObject;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.SceneObjects.SceneLabelObjectState;
import com.lostagain.Jam.SceneObjects.SceneLabelObjectState.cursorVisibleOptions;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectState.TrueFalseOrDefault;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneLabelObject;
import com.lostagain.Jam.audio.JamAudioController;
import com.lostagain.Jam.audio.JamAudioController.DefaultSound;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;


public abstract class TypedLabelCore implements HasDeltaUpdate {
	public static Logger Log = Logger.getLogger("JAM.TypedLabelCore");
	
	
	/** lets the app globally disable all typed text - useful for when on low power clients **/
	protected static boolean globallyDisableTypeing = false;

	

	public static boolean isGloballyDisableTypeing() {
		return globallyDisableTypeing;
	}



	/** lets the app globally disable all typed text - useful for when on low power clients **/
	public static void setGloballyDisableTypeing(boolean globallyDisableTypeing) {
		TypedLabelCore.globallyDisableTypeing = globallyDisableTypeing;
	}



	protected SceneObject associatedObject = null;
	protected ArrayList<String> ObjectsToLoadIntoText = new ArrayList<String>();

	protected String currentText = "";

	protected Runnable runBeforeTextSet = null;

	protected Runnable runAfterTextSet = null;

	protected String targetText = "";

	protected int Speed = 35;

	protected int i = 0;

	protected int Delay = 5;

	/** The widget is currently typing its text, X-files style!**/
	protected boolean currentlyTyping = false;

	/** Indicates we are currently in the "delay" period when a setText is triggered
	 * but typing has yet to start **/
	protected boolean aboutToType = false;

	/**
	 * If the sound should be disabled for the next bit of typed text (resets to false after each bit of text typed)
	 */
	protected boolean SoundCurrentlyDisabled = false;

	/** the cursor style this object uses. **/
	protected cursorVisibleOptions cursorStyle;

	protected double timeSinceLastUpdate = 0;

	boolean showcursor = true;

	/**
	//extra space padding that is used during typing
	//this stops too much "word-wrap jumping" at line ends.**/
	protected String padding = "";




	/**The optional blinking cursor effect.<br>
	 * Shockingly, some webbrowsers no longer support this tag.<br>
	 * Its a sad,sad day.<br>*/
	protected String blinkyBit = getBlinkString();



	protected String getBlinkString() {
		return "<blink>█</blink>";
	}

	protected String nonBlinkyBit = getSquareCursorString();



	protected String getSquareCursorString() {
		return "<div class=\""+cursorclass+"\">"+"█"+"</div>";
	//	return "█"; 
	}

	protected String cursorclass = "blinkcur";

	protected String CUSTOM_KEY_BEEP = null;

	protected String CUSTOM_SPACEKEY_BEEP = null;


	public String getCurrentText() {
		return currentText;
	}

	/*** 
	 * Because text in the JAM engine can have inline commands, heres where they are processed. 
	 * The 3 types of command are<br>
	 * "run:____" which will run a normal script command <br>
	 * "runafter:" which will run a script command after a pause <br>
	 * and "insert:" which will insert a object into the text, inlined. 
	 ***/
	protected String processCommandFromText(String command, String currentText) {
	
			if (command.toLowerCase().startsWith("run:")) {
				// Log.info("running..");
				Log.info("__________command detected=" + command);
	
				command = command.substring(4);
				InstructionProcessor.processInstructions(command,
						"TriggeredFromLab_", associatedObject);
	
			}
			if (command.toLowerCase().startsWith("insert:")) {
				// Log.info("running..");
				Log.info("__________command detected=" + command);
	
				command = command.substring(7).trim();
				currentText = currentText
						+ "<div style=\"display:inline-block\" ID=\"Item_"
						+ command + "\"></div>";
				// add to load list
				ObjectsToLoadIntoText.add(command);
	
			}
	
			if (command.toLowerCase().startsWith("runafter")) {
	
				// <!-- runafter4000: - Message = "blah" -->
				// split at colon
				String runAfterThisTime = command.substring(8).split(":")[0].trim();
				final String runThis    = command.substring(8).split(":")[1].trim();
	
				// Log.info("running..");
				Log.info("__________command detected=" + runThis + " after "
						+ runAfterThisTime + " ms");
	
	
				//Timer runAfter = new Timer() {			
				//	@Override
				//	public void run() {
				//		InstructionProcessor.processInstructions(runThis,
				//				"TriggeredFromLab_", associatedObject);
	//
				//	}
				//};
				
				//getNewTimerClass
				IsTimerObject runAfter = JAMTimerController.getNewTimerClass(new Runnable() {			
					@Override
					public void run() {
						InstructionProcessor.processInstructions(runThis,
								"TriggeredFromLab_", associatedObject);
	
					}
				});
				int delay = Integer.parseInt(runAfterThisTime);
				runAfter.schedule(delay);
	
			}
	
			return currentText;
		}



	public void setRunBeforeTextSet(Runnable runBeforeTextSet) {
		this.runBeforeTextSet = runBeforeTextSet;
	}



	public void setRunAfterTextSet(Runnable runAfterTextSet) {
		this.runAfterTextSet = runAfterTextSet;
	}



	public void setAssociatedObject(SceneObject associatedObject) {
	
		if (this.associatedObject!=null){
			this.associatedObject.ObjectsLog("(unassociating a textlabel was was associated with this object....ekk?");
			this.associatedObject.ObjectsLog("(Old association was with this New is with "+associatedObject.getObjectsCurrentState().ObjectsName);	
			associatedObject.ObjectsLog("(removed prior association with "+this.associatedObject.getObjectsCurrentState().ObjectsName+")");
		}
	
	
		associatedObject.ObjectsLog("(associating textlabel with this object)");
	
		this.associatedObject = associatedObject;
	}



	/** sets the text instantly, without typing it. 
	 * Any inline commands are still proceeded in order.
	 * 
	 * @param text
	 * @param disableSound - if the sound should be disabled for this text only
	 * @return 
	 */
	public boolean setTextNow(String text, boolean disableSound) {
		
		if (!this.isAttached()){
			//Log.severe(" was arnt attached yet! yet setTextNow is triggered...");	
			//todo; should we return?
		}
		
		if (runBeforeTextSet!=null){
			runBeforeTextSet.run();
		}
		
		if (currentText.equals(text)){
			if (associatedObject!=null){
				associatedObject.ObjectsLog("(set text now requested, but text is already the same as requested)");
			}
			return false;
		}
		
		SoundCurrentlyDisabled = disableSound;
		ObjectsToLoadIntoText.clear();
	
	
	
		//stop any current text animations
		//timer.cancel();		
		cancelTypeingTimer();	
		
		//delaybeforetype.cancel();
	
		currentText = text; //maybe set targetText too?
		targetText = text;
		currentlyTyping=false;	
		aboutToType = false;
	
		Log.info("*************************setting text to:"+text);
	
		//next bit purely for debugging, should remove
		if (associatedObject!=null){
		//	associatedObject.ObjectsLog("currently text is " +this.getElement().getInnerHTML());
		//	associatedObject.ObjectsLog("current widgets on panel:"+internalPanel.getWidgetCount()           );
		}
	
	
		//removing existing widgets	inserted	
		this.clear();
		
	
		//--------------
	
		if (!text.contains("<!--")){
			Log.info("_______________________________________________________________________displaying text quickly (no inserts or commmands)");
			//no commands so just see it
			//getElement().setInnerHTML(text);
		      //now use setTextInternalIMPL
			setTextInternalIMPL(text);
			
			//		Log.info("_______________setInnerHTML = "+getElement().getInnerHTML());
	
			if (associatedObject!=null){
				associatedObject.ObjectsLog("text instantly set to:"+text);
			}
	
		} else {
	
			Log.info("_______________________________________________________________________displaying text quickly (has inserts/commands)");
	
			//loop
			int c=0;			
			String newText = "";
	
			while (c<text.length()){
	
				//find next <!--
				int nextCommentStart = text.indexOf("<!--", c);
				int nextCommentEnd = text.indexOf("-->", nextCommentStart)+3;
	
				//if none exit
				if (nextCommentStart==-1){
	
					String textTillComment = text.substring(c,text.length());						
					newText=newText+textTillComment;
	
					break;
				}
	
				String textTillComment = text.substring(c,nextCommentStart);						
				newText=newText+textTillComment;
	
				String command = text.substring(nextCommentStart + 4,
						nextCommentEnd-3).trim();
	
				//process commands
				Log.info("Command is:"+command);
				newText=processCommandFromText(command, newText);
				Log.info("new text is:"+newText);
				//set at end and repeat
				c=nextCommentEnd;
	
			}			
	
			//display it
			//currentText=newText;
	
			//next bit purely for debugging, should remove
			if (associatedObject!=null){
				associatedObject.ObjectsLog("text html is now:"+newText+"( objects to load:"+ObjectsToLoadIntoText.size()+")","orange");
			}
			//--------------
			Log.info("finnaly text html is:"+newText);
			//getElement().setInnerHTML(newText);
			this.setTextInternalIMPL(newText); //new method
			
			//			Log.info("_______________setInnerHTML = "+getElement().getInnerHTML());
			if (associatedObject!=null){
			//	associatedObject.ObjectsLog("final text instantly set to:"+getElement().getInnerHTML());
			}
			loadAnyNeededObjects();
	
		}
	
		if (runAfterTextSet!=null){
			runAfterTextSet.run();
		}
	
		return true;
	
	}

	public void cancelTypeingTimer(){
		//timer.cancel();
		if (associatedObject!=null){
			associatedObject.ObjectsLog("typing timer canceled","purple");
		}

		DeltaTimerController.removeObjectToUpdateOnFrame(this);

	}


	/**
	 * should return true if widget is attached to scene/page
	 * @return
	 */
	public abstract boolean isAttached();

	

	
	/**
	 * Complex.
	 * Should load objects into the text as specified by tags in the text.
	 * See GWT implementation TypedLabel
	 */
	public abstract void loadAnyNeededObjects();

	/**
	 * should clear any objects previously embeded into the text
	 */
	public abstract void clear();



	/**
	 * Should set the widget to display the text specified, with no other actions or fuss
	 * @param newText
	 */
	protected abstract void setTextInternalIMPL(String newText);


	/**
	 * When inserting sub-labels within this label, they might have preportion work to do first.
	 * This mainly concerns if you want them typed out or not, as well as cursor style.
	 * 
	 * @param newObjectAsLabel
	 */
	protected void prepareLabelForInsertIntoThisLabel(IsSceneLabelObject newObjectAsLabel) {
		
		boolean weAreATypedLabel = isTypedLabel(); //if we ourselves are a label that is typed out
		//depending on settings, OUR own typed status might effect if the LABEL BEING INSERTED should also be typed.
				
		//prepare it and set it typing if appropriate to do so;
		Log.info("we are inserting a label object into this label.");

		if (weAreATypedLabel){

			prepareTypedLabelForInsert(newObjectAsLabel);

		}
	}



	protected void prepareTypedLabelForInsert(IsSceneLabelObject newObjectAsLabel) {
		String currentText = newObjectAsLabel.getCurrentText();

		
		//give it the cursor style of the parent
		//note: this currently changes the label style rather
		//then overriding it.
		//is the label is reused,  it will use this new style	
	//	newObjectAsLabel.setCursorMode(this.cursorStyle);
		//newObjectAsLabel.setCursorClass("inlinecursor");

		Log.info("________restting text ("+currentText+")");
		newObjectAsLabel.ObjectsLog("________restting text ("+currentText+")");
		
		//first we erase the text there
		//We do this because the text is already showing at this point
		//and the type text has a slight delay
		//before it effects the text.
		//Thus by erasing it first instantly we ensure nothing of 
		//the text is seen before its typed.								
		newObjectAsLabel.setText("",false);

		//Then we set it to add new text
		Log.info("________setting text on insert to:"+currentText+" typed text state:"+newObjectAsLabel.getObjectsCurrentState().TypedText);
		newObjectAsLabel.ObjectsLog("________setting text on insert to:"+currentText+" typed text state:"+newObjectAsLabel.getObjectsCurrentState().TypedText);
		if (newObjectAsLabel.getObjectsCurrentState().TypedText == TrueFalseOrDefault.FALSE){
			//If the sub-label is set fo FALSE we never type, even though we are typed ourselves
			newObjectAsLabel.setText(currentText ,false);
		} else {									
			//If the sub-label is set to TRUE or DEFAULT then we type it, because the parent label is typed as well
			newObjectAsLabel.setText(currentText ,true); 
		}

		newObjectAsLabel.ObjectsLog("________reset text to : "+currentText);
		Log.info("________reset text to:"+currentText);
	}

	public boolean isTyping() {
		return currentlyTyping;
	}



	public boolean isWaitingToType() {
		return aboutToType;
	}



	protected Boolean isTypedLabel() {
		Boolean typed = false;
		if (associatedObject!=null){
	
			if (associatedObject.getObjectsCurrentState().isCompatibleWith(SceneObjectType.Label)){
				//safe to cast to label interface 
				SceneLabelObjectState associatedObjectAsLabel = (SceneLabelObjectState) associatedObject.getObjectsCurrentState();
				
				if ( associatedObjectAsLabel.TypedText ==  TrueFalseOrDefault.TRUE){
					typed = true;
				} else {
					typed = false;
				}
				
			}
			
			//if ( associatedObject.getAsDialog().getObjectsCurrentState().TypedText ==  TrueFalseOrDefault.TRUE){
			//	typed = true;
			//} else {
			//	typed = false;
			//}
			
			//typed = associatedObject.getAsDialog().objectsCurrentState.TypedText;
	
		} else {
			Log.warning("associatedObject is currently null. This is probably wrong if this should be a on a sceneobject");
		}
	
		return typed;
	}



	public void setSpeed(int newSpeed) {
		Speed = newSpeed;
	}



	public void setDelay(int newDelay) {
		if (newDelay == 0) {
			Delay = 50;
		} else {
			Delay = newDelay * 1000;
		}
	}



	public void setCUSTOM_KEY_BEEP(String keysound) {
	
		Log.info("_______________setting keysound "+keysound);
		CUSTOM_KEY_BEEP = keysound;
	}



	public void setCUSTOM_SPACEKEY_BEEP(String spacesound) {
	
		Log.info("_______________setting spacesound "+spacesound);
		CUSTOM_SPACEKEY_BEEP = spacesound;
	
	}



	/**
	 * Updates the typing of the text by a single character
	 **/
	protected void updateTyping() {
			// if the current target text is set, we remove the timer.
			if (i >= targetText.length()) {
	
			//	getElement().setInnerHTML(currentText +blinkyBit);
			       //now use setTextInternalIMPL
				setTextInternalIMPL(currentText +blinkyBit);
				  
				//	Log.info("_______________setInnerHTML = "+toString());
	
				if (associatedObject!=null){
					//	associatedObject.ObjectsLog.log("text html is finnished typing now:"+toString(),"orange");
					//	associatedObject.ObjectsLog("currentText:"+currentText+" blinkyBit:"+blinkyBit);
	
				//	associatedObject.ObjectsLog("text html is finnished typing InnerHTML="+getElement().getInnerHTML(),"purple");
	
				}
	
				i = 0;
				loadAnyNeededObjects();
	
				currentlyTyping=false;
				SoundCurrentlyDisabled = false; //reset sound effect setting for next time
	
				if (runAfterTextSet!=null){
					runAfterTextSet.run();
				}
	
	
				cancelTypeingTimer();
				//this.cancel();
	
			} else {
	
				// is not we add another letter.					
				char nextletter = targetText.charAt(i);
	
				if (padding.length()>0){
					padding=padding.substring(1);//subtract a character each step
				}
	
				// detect command start
				String next4letters = "";
				if (targetText.length()>=(i+4)){
					next4letters = targetText.substring(i, i + 4); //gets the current letter and the next 3
				}
				
				//	Log.info("_ next4letters:"+next4letters);
				if (next4letters.equalsIgnoreCase("<!--")) {
					Log.info("_____________________________________________comment or command detected!!");
					// extract command and skip to end of comment
					int skiptoo = targetText.indexOf("-->", i);
	
					String command = targetText.substring(i + 4,	skiptoo - 0).trim();
	
					//next letter is now after the END of the command
					//as we never want the command visible.
					i = skiptoo +3;// + 1;
					//nextletter = targetText.charAt(i);
	
					// test command is valid, then run it
					currentText = processCommandFromText(command,currentText);		
					
					
				//	getElement().setInnerHTML(currentText + nonBlinkyBit); 
				       //now use setTextInternalIMPL
					setTextInternalIMPL(currentText + nonBlinkyBit); 
					
					
					//		Log.info("_______________setInnerHTML = "+getElement().getInnerHTML());
					if (associatedObject!=null){
					//	associatedObject.ObjectsLog("After comment detected InnerHTML="+getElement().getInnerHTML(),"purple");
					}
	
				//	i = i + 1;
					next4letters="";
	
					return;
	
				} else if (nextletter == '<')
				{	
	
					//ensure any html commands go in straight away - so we don't see the < briefly
					//Log.info("_____________________________________________tag detected from <  ");
	
					
	
					// extract command and skip to end of it
					int skiptoo = targetText.indexOf(">", i)+1;
	
					//tag to insert as a whole
					String tagbit = targetText.substring(i,	skiptoo).trim();
	
				//	Log.info("_ adding:"+tagbit+":");
					if (associatedObject!=null){
						associatedObject.ObjectsLog("___open tag detected in string being typed < Tag was:"+tagbit);
					}
					//add the whole tag at once
					currentText=currentText+tagbit;
	
					//next letter is now after the END of the command
					//as we never want the command visible.
					i = skiptoo;
	
					return;
					/*
					nextletter = targetText.charAt(i);
	
	//Note;; currently meses up <!-- detection if thats the next bit of text
					Log.info("_ nextletter:"+nextletter+":");
	
					String next4letterstest = targetText.substring(i, i + 4);
	
					Log.info("_ next4letters test:"+next4letterstest+":");
					 */
				} else if (nextletter == '&'){
	
					//we might be entering a html code like
					//&#9654;
					//&nbsp;
					//etc
					//These codes should be entered instantly so we dont see the code appear
	
					//we also ensure the next 4 characters dont contain a space, as that would mean people actually wanted to write &
					//(I think all these &___; codes are at least 4 characters total.
					if (!next4letters.contains(" ")){
	
						
	
						// extract command and skip to end of it
						int skiptoo = targetText.indexOf(";", i)+1;
	
						//tag to insert as a whole
						String tagbit = targetText.substring(i,skiptoo).trim();
	
						Log.info("_ adding:"+tagbit+":");
	
						//add the whole tag at once
						currentText=currentText+tagbit;
	
						if (associatedObject!=null){
							associatedObject.ObjectsLog("___open &__; insert detected in string being typed. Tag was:"+tagbit);
						}
						//next letter is now after the END of the command
						//as we never want the command visible.
						i = skiptoo;// + 1;
	
						return;
	
					}
	
	
				}
	
	
	
				// <!--
				//
				currentText = currentText + nextletter;
				
				if (nextletter=='\n' || nextletter =='\r') {
					//as typed text does not support preformated newlines (\n \r etc) we dont bother updating the inner html for them
					//They will, however, be in the final text when set on the next non-preformated newline update, so it exactly matchs whats requested.
				} else {
				   //in all other cases we update
				   //now use setTextInternalIMPL
					
		//			setTextInternalIMPL(currentText + "<div class=\""+cursorclass+"\">"+nonBlinkyBit+"</div>"+"<div class=\"invis\">"+padding+"<div>"); 
					String paddingPiece = getPostStringPadding(padding);
					setTextInternalIMPL(currentText + nonBlinkyBit+paddingPiece); 
						
				}
				
				//			Log.info("_______________setInnerHTML = "+getElement().getInnerHTML());
				i = i + 1;
	
				
				
				//set invisible next word padding for next time if we just started a new word
				if (nextletter==' '){
						
					int wordend = targetText.indexOf(" ", i+1);
					if (wordend==-1){
						wordend=targetText.length();
					}
					if (wordend==i){
						padding     ="";
					} else {
					Log.info("wordend="+wordend+" , "+i);
					padding     = targetText.substring(i+1,wordend);
					}
	
				} 
	
				// sound if not disabled (checked internally by playDefaultSound)
				//if (JAM.SoundEffectOn) {
	
				//if (associatedObject!=null){
				//	Log.info("sound object:"+associatedObject.objectsCurrentState.ObjectsName+" ");
				//} else {
				//	Log.info("id:"+this.getElement().getId()+" attached:"+this.isAttached());
				//}
	
				//sound cant be disabled
				if (!SoundCurrentlyDisabled)
				{
	
					
					if (nextletter == ' ') {
	
						//if we have a custom sound we play that, else we use the default sound
						if (CUSTOM_SPACEKEY_BEEP!=null){
	
							//	Log.info("playing custom sp:"+CUSTOM_SPACEKEY_BEEP+" (text target starts:"+targetText.substring(0, 15));
							//GwtAudioController.playAudioTrack(CUSTOM_SPACEKEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
							
							OptionalImplementations.playAudioTrack(CUSTOM_SPACEKEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
							//JAMcore.playAudioTrack(ThisTrackName, targetVolume, fadeInAndOut, audiotype, FadeOver);
							
						} else {
	
							//	Log.info("playing default sp:"+CUSTOM_SPACEKEY_BEEP+" (text target starts:"+targetText.substring(0, 15));
							//GwtAudioController.playDefaultSound(DefaultSound.SpaceBeep);
							OptionalImplementations.playDefaultSound(DefaultSound.SpaceBeep);
							
						}
	
					} else {
	
						if (CUSTOM_KEY_BEEP!=null){
	
							//	Log.info("playing custom k:"+CUSTOM_KEY_BEEP+" (text target starts:"+targetText.substring(0, 15));
						//	GwtAudioController.playAudioTrack(CUSTOM_KEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
							
							OptionalImplementations.playAudioTrack(CUSTOM_KEY_BEEP, 100, false, JamAudioController.AudioType.SoundEffect,-1 );
							
						} else {
	
							//	Log.info("playing default k:"+CUSTOM_KEY_BEEP+" (text target starts:"+targetText.substring(0, 15));
						//	GwtAudioController.playDefaultSound(DefaultSound.KeyBeep);
							OptionalImplementations.playDefaultSound(DefaultSound.KeyBeep);
							
						}
	
	
					}
	
	
				}
				//}
			}
		}



	
	protected String getPostStringPadding(String padding) {
		return "<div class=\"invis\">"+padding+"<div>";
	}



	/** sets the blink state to either true,false or whentyping 
	 * currently set for html usage, might need to be overridden for other things
	 * **/
	public void setBlink(cursorVisibleOptions state) {
	
		cursorStyle = state;
	
	
		if (state==cursorVisibleOptions.TRUE) {
	
			blinkyBit = getBlinkString();
			nonBlinkyBit = getSquareCursorString();
	
		} else if (state==cursorVisibleOptions.WHENTYPING){
	
			blinkyBit = "";
			nonBlinkyBit = getSquareCursorString();
	
		} else {
	
			blinkyBit = "";
			nonBlinkyBit = "";
	
		}
	
	
	}



	public void update(float delta) {
	
		//timer.scheduleRepeating(Speed);
		//updateTyping() 
		timeSinceLastUpdate=timeSinceLastUpdate+ delta;
	
		//if we are currently delaying
		if (aboutToType){
	
			if (timeSinceLastUpdate>Delay){
	
				currentlyTyping = true;
				aboutToType     = false;
				timeSinceLastUpdate=0;
				//Delay=0;
				return;
	
			} else {
	
				if (associatedObject!=null){
					associatedObject.ObjectsLog("delay before typing finnished, now starting to type:"+targetText+" which is "+targetText.length()+" characters long");
				}
				return; 
			}
	
		}
	
	
	
		//Log.info("timeSinceLastUpdate="+timeSinceLastUpdate +" timerDelay="+timerDelay);	
		if (timeSinceLastUpdate>Speed && currentlyTyping){
	
	
			timeSinceLastUpdate = timeSinceLastUpdate % Speed; //time since last update is the remainder 
			//after its been divided by timerDelay as much as possible.
			//(That is the time "owed", how far we are behind)
	
			//if (x % 2 == 0){
			//	
			//}
			//timeSinceLastUpdate = 0;
	
			//update the frame
			//NB: This doesn't skip frames if the delta was more then one timerDelay, it probably should
			//we can do that by seeing how many times timerDelay goes into timeSinceLastUpdate
	
			//if (associatedObject!=null){
			//	associatedObject.ObjectsLog.log("Typing updated","purple"); //log spam
			//}
			updateTyping(); 
		}
	}



	/** Timer that runs before typing if there's a delay 
	Timer delaybeforetype = new Timer() {
		public void run() {
	
			if (associatedObject!=null){
				associatedObject.ObjectsLog("delay before typing finnished, now starting to type:"+targetText+" which is "+targetText.length()+" characters long");
			}
	
			startTypeingTimer();
			currentlyTyping = true;
			aboutToType     =false;
		}
	};
	 **/
	protected void startTypeingTimer() {
		//timer.scheduleRepeating(Speed);
		if (associatedObject!=null){
			associatedObject.ObjectsLog("typing timer started","purple");
		}
		DeltaTimerController.addObjectToUpdateOnFrame(this);
	
	}



	public void setTextNow(String text) {
		setTextNow(text, false); 
	}



	public void setText(String text) {
		setText(text,false);
	}



	/**
	 * Sets the text in this box, typeing if its set to type 
	 * @param text
	 * @param disableSound - disable the sound effect for this bit of text only
	 */
	public void setText(String text, boolean disableSound) {
		SoundCurrentlyDisabled = disableSound;
	
		if (!this.isAttached()){
			Log.severe(" was arnt attached yet! yet setText is triggered...");	
		}
		
		//if typing is globally disabled we just use set text now instead
		if (globallyDisableTypeing){
			setTextNow(text);
			return;
		}
				
		if (targetText.equals(text) && associatedObject!=null){ 
			
			associatedObject.ObjectsLog(" Target text already requested text, thus no-op:"+text);	
		//	associatedObject.ObjectsLog(" check real html is currently:"+getElement().getInnerHTML());	
			
			return;
		}
		
		//run any commands set to trigger before start of text
		if (runBeforeTextSet!=null){
			runBeforeTextSet.run();
		}
	
		ObjectsToLoadIntoText.clear();
		aboutToType = true;
		currentlyTyping=false;	 
		
		//delaybeforetype.cancel();
		//timer.cancel();
		cancelTypeingTimer();
	
		//next bit purely for debugging, should remove
		if (associatedObject!=null){
			associatedObject.ObjectsLog("typing text to:"+text);
	
			//associatedObject.ObjectsLog("current widgets on panel:"+internalPanel.getWidgetCount());
	
	
		}
	
		//removing widgets		
		this.clear();
	
	
		//--------------
	
		if (text.length() > 0) {
			i = 0;
			currentText = "";
			targetText = text;
	
			startTypeingTimer();
		} else {
			//empty test was requested
			currentText = "";
			targetText = text;
			
			if (runAfterTextSet!=null){
				runAfterTextSet.run();
			}
			
		}
			
	}


}
