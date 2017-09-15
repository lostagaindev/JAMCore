package com.lostagain.Jam.Factorys;

import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.lostagain.Jam.JAMTimerController;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;

public class NamedActionSetTimer {

	public static Logger Log = Logger.getLogger("JAMCore.NamedActionSetTimer");

	/**
	 * length of time remaining before the next run() is fired
	 */
	protected int FuseLengthMS = 0;
	
	SceneObject objectThatCalledThisNAS;
	


	
	/**
	 * where the commands are stored. Either a object name or  {@literal <GLOBAL>}
	 */
	protected String RunnablesObject = "";
	/**
	 * name of the runnable to run 
	 */
	protected String RunnableName = "";
	
	/**
	 * are we repeating?
	 */
	boolean repeating = false;
	
	/**
	 * time schedule() was last called (used to display a countdown in inspector)
	 * 
	 */
	protected long StartTime = 0;
	
	/**
	 * repeats can be done at a random interval, this is the shortest time for that interval 
	 */
	int activateEvery_ShortestTimePossibility=-1;	
	/**
	 * repeats can be done at a random interval, this is the longest time for that interval 
	 */
	int activateEvery_LongestTimePossibility=-1;
	//---
	
	
	
	
	//todo; make protected, p
	public IsTimerObject TimerImplementation;

	
	
	
	/**
	 * Stores the current scene when each action set was created
	 */
	private SceneWidget sceneCurrentWhenCreated;
			
	public SceneWidget getSceneCurrentWhenCreated() {
		return sceneCurrentWhenCreated;
	}
	

	/**
	 * REQUIRES ALL SCENES LOADED for deseralisation and resuming to work
	 * 
	 * @param seralisedResumeState
	 */
	private NamedActionSetTimer(String seralisedResumeState) {
		super();
		this.deseralise(seralisedResumeState);
		
	}
	public NamedActionSetTimer(String runnableName, String runnableObject,SceneWidget currentScene) {
		super();
		RunnableName    = runnableName.toLowerCase().trim();
		RunnablesObject = runnableObject.toLowerCase().trim();
		
		sceneCurrentWhenCreated = currentScene;
		
		repeating = false;
		activateEvery_ShortestTimePossibility=-1;	
		activateEvery_LongestTimePossibility=-1;
	}
	
	/**
	 * 
	 * @param objectThatCalledThisNAS - can be null
	 * @param runnableName
	 * @param runnableObject
	 * @param currentScene
	 * @param repeating
	 * @param activateShortestTimePossibility
	 * @param activateEveryLonghestTimePossibility
	 */
	public NamedActionSetTimer(
			SceneObject objectThatCalledThisNAS,
			String runnableName, 
			String runnableObject,
			SceneWidget currentScene,
			boolean repeating,
			final int activateShortestTimePossibility,
			final int activateEveryLonghestTimePossibility) 
	{
		super();
		
		this.objectThatCalledThisNAS = objectThatCalledThisNAS;
		RunnableName    = runnableName.toLowerCase().trim();
		RunnablesObject = runnableObject.toLowerCase().trim();
		
		sceneCurrentWhenCreated = currentScene;
		
		//currently used purely for nas resuming later if saved
		//future we might inline the repeat code here too
		this.repeating = repeating;
		this.activateEvery_ShortestTimePossibility=activateShortestTimePossibility;	
		this.activateEvery_LongestTimePossibility=activateEveryLonghestTimePossibility;
	}
	
	/**
	 * creates and resumes a standard NamedActionSetTimer set from a savestring
	 * 
	 * REQUIRES ALL SCENES LOADED for deseralisation and resuming to work.
	 * Also requires unique names OR its acceptable for the named action set to be fired on all objects of the same name.
	 * (it cant tell them apart remember!)
	 * 
	 * @param seralisedState
	 */
	static public void createAndResumeNamedActionState(String seralisedState){
		
		Log.info(" Resuming action set:"+seralisedState);	

		//split
		String bits [] = seralisedState.split(",");	

		String scenename = bits[0];		
		String callingobjectName = bits[1];
		String actionSetSourceObject = bits[2];
		String actionSetName = bits[3];
		int fuseLength = Integer.parseInt(bits[4]);		
		boolean repeats = Boolean.parseBoolean(bits[5]);
		
		int short_duration   = Integer.parseInt(bits[6]);
		int longest_duration = Integer.parseInt(bits[7]);
		
				
		int fuseLeft =  Integer.parseInt(bits[8]);
		
		
		//get calling object
		SceneObject callingObject = SceneObjectDatabase.getSingleSceneObjectNEW(callingobjectName,null,true);
		//what if many objects are called the same? do we run on each? what if its just a specific one?
		//
		
		 if (repeats){

				Log.info(" action was repeating every:"+short_duration+"-"+longest_duration);	
				
			 triggerRunNamedCommandsEvery(
				 	 actionSetSourceObject,
				     actionSetName, //not saved?
					 short_duration,
					 longest_duration,
					 callingObject,  //need to store this too, or at least its name
					 true,
					 fuseLeft);
			 
		 } else {
			 
			 int activateAfter = fuseLength;
			 
			 if (fuseLeft!=-1){
				 activateAfter = fuseLeft;
			 }

				Log.info(" action should activate in :"+activateAfter);
				
			 triggerRunNamedActionSetAfter(
					 callingObject,
					 actionSetSourceObject,
					 actionSetName,
					 activateAfter,
					 true);
		 }
		 
		 
		return;
	}
	
	public String seralise(){
		String seralised = ""; 
		
				
		long timeRemaining = getTimeRemaining(); //for some reason this can sometimes be negative. For that reason we test and cap it at 10ms
		if (timeRemaining<10){
			timeRemaining=10; //TODO: investigate why getTimeRemaining can be a negative
		}
		
		
		if (getObjectThatCalledThisNAS()!=null) {
			
		seralised = "" 
				   + getSceneCurrentWhenCreated().SceneFileName+","
				   + getObjectThatCalledThisNAS().getName()+","
				   + getRunnableObject()+","
				   + getRunnableName()+","
				   + getFuseLength()+","		
				   + isRepeating()+","
				   + getActivateEvery_ShortestTimePossibility()+","		
				   + getActivateEvery_LongestTimePossibility()+","			
				   + timeRemaining+"";
		} else {
			
			seralised = "" 
					   + getSceneCurrentWhenCreated().SceneFileName+","
					   + ","
					   + getRunnableObject()+","
					   + getRunnableName()+","
					   + getFuseLength()+","		
					   + isRepeating()+","
					   + getActivateEvery_ShortestTimePossibility()+","		
					   + getActivateEvery_LongestTimePossibility()+","			
					   + timeRemaining+"";
		}
		
		return seralised;

	}
	
	private void deseralise(String stateData){
		
		Log.info(" resuming action set:"+stateData);	
		//split
		String bits [] = stateData.split(",");	
		
		String scenename = bits[0];		
		String callingobject = bits[1];
		String objectName = bits[2];
		String actionSetName = bits[3];
		String fuseLength = bits[4];		
		boolean repeats = Boolean.parseBoolean(bits[5]);
		String short_duration = bits[6];
		String longest_duration = bits[7];
		String fuseLeft = bits[8];
		
		
		Log.info(" scene:"+scenename);	
		Log.info(" callingobject:"+callingobject);	
		Log.info(" object:"+objectName);	
		Log.info(" name:"+actionSetName);	
		Log.info(" fuseLength:"+fuseLength);
		Log.info(" repeats:"+repeats);		
		Log.info(" shortest:"+short_duration);	
		Log.info(" longest:"+longest_duration);
		Log.info(" fuse left:"+fuseLeft);
		
		/*
		RunnableName    = runnableName.toLowerCase().trim();
		RunnablesObject = runnableObject.toLowerCase().trim();		
		sceneCurrentWhenCreated = currentScene;
		
		//currently used purely for nas resuming later if saved
		//future we might inline the repeat code here too
		this.repeating = repeating;
		this.activateEvery_ShortestTimePossibility=activateShortestTimePossibility;	
		this.activateEveryLonghestTimePossibility=activateEveryLonghestTimePossibility;
		*/
		
		return;

	}
	
	private void createNewTimerImpl() {
		
		//only create new impl if we dont already have one
		if (TimerImplementation==null) {

		Log.info("creating timer implementation for "+RunnablesObject+"_"+RunnableName);
		
		TimerImplementation = JAMTimerController.getNewTimerClass(new Runnable() {		
			@Override
			public void run() {				
				
			//   Log.info("fireing timers runnable "+this.hashCode());
			   NamedActionSetTimer.this.run();
			   
			   //post run actions (this might re-trigger the timer if its set to repeat)
			   //the specific parameters of the action set will determine this.
			   //Theres 3 possibility's,  however;
			   //No repeat at all
			   //Repeat at fixed interval
			   //Repeat at a random range of intervals
			   NamedActionSetTimer.this.postTimerFireActions();
			   
			}
		});
		
		}
	}


	//prepare next repeat if it hasn't been canceled 
	protected void postTimerFireActions() {
		
		//first ensure we have not been canceled
		if (this.isCanceled()){
			//do nothing
			return;
		}
		//--------------
		
		//then repeat if we are set too
		if (this.isRepeating()){
			
			//if repeating we look to see the range of times that determain the repeat interval
			//(if the times are the same its a fixed repeat interval)
			
			int range = (this.activateEvery_LongestTimePossibility-this.activateEvery_ShortestTimePossibility);	
			int nextGap = (int) (Math.random()*range)+this.activateEvery_ShortestTimePossibility;	

			//Reset start time
			StartTime = System.currentTimeMillis();	
			FuseLengthMS = nextGap;
			//--
			TimerImplementation.schedule(nextGap); //note we use the TimerImplementation schedule, not our own as that will do addition setup that we dont want
		
			
			
			
			Log.info("postTimerFireActions:Resheduleing action set: "+this.getRunnableObject()+"_"+ this.getRunnableName());
			
		}
		//
		
		
	}


	/**
	 * Overridden by subclasses and run by the internal TimerImplementation
	 */
	protected void run() {
		Log.info(" (should be overriden) ");
		
	}
	
	/**
	 * cancels the timer and removes its runnable from activeNamedActionSetTimers
	 * Also updates the GameDataBox debug box
	 */
	public void cancel() {
		Log.info("canceling "+this.getRunnableObject()+"_"+this.getRunnableName());
		Log.info("hashcode "+TimerImplementation.hashCode());
		
		TimerImplementation.cancel();
		removeFromActiveTimers();
		
		//remove the implementation completely so it will be recreated if we reshedule
		TimerImplementation = null;
		
	}
	
	public String getRunnableObject() {			
		return RunnablesObject;
	}

	public String getRunnableName() {			
		return RunnableName;
	}

	public int getFuseLength() {
		return FuseLengthMS;
	}


	public long getTimeRemaining(){
		return FuseLengthMS - (System.currentTimeMillis()-StartTime);
	}
	
	public boolean isRepeating() {
		return repeating;
	}

	public int getActivateEvery_ShortestTimePossibility() {
		return activateEvery_ShortestTimePossibility;
	}

	public int getActivateEvery_LongestTimePossibility() {
		return activateEvery_LongestTimePossibility;
	}
	
	/**
	 * removes it from the list of active timers.
	 * This should be called after running has finished and its not scheduled
	 * We will add to the list when scheduled so this list is handled automatically.
	 */
	protected void removeFromActiveTimers() {
		
		JAMTimerController.activeNamedActionSetTimers.remove(getThisRunnablesID(),this);
		
		JAMTimerController.updateCurrentTimerDebugBoxs();
	
	}

	public void schedule(int delayMillis) {

		createNewTimerImpl(); //create new impl if needed
		
		TimerImplementation.schedule(delayMillis);
		FuseLengthMS = delayMillis;
		StartTime = System.currentTimeMillis();
	
		//add to active timers
		JAMTimerController.activeNamedActionSetTimers.put(getThisRunnablesID(), this);
		

		Log.info("added "+RunnablesObject+"_"+RunnableName+" to shedraled commands. FuseLengthMS= "+FuseLengthMS);
		
		JAMTimerController.updateCurrentTimerDebugBoxs();
		
	
	}

	public SceneObject getObjectThatCalledThisNAS() {
		return objectThatCalledThisNAS;
	}

	/**
	 * The ID for the runnable.
	 * This is how its identified in the hashmap storage for it.
	 * 
	 * Note; the id should always be fully lower case
	 * 
	 * @return
	 */
	public String getThisRunnablesID() {
		
		String uniquename = "";
		
		//if this is a global runnable (the code isnt tied to a object)
		if (RunnablesObject.equalsIgnoreCase("<GLOBAL>")){
				//if we have a calling object, that forms part of the ID	
				if (objectThatCalledThisNAS!=null){				
					uniquename = "<GLOBAL>_"+objectThatCalledThisNAS.getName()+"_"+this.getRunnableName();				
				} else {				
					uniquename = "<GLOBAL>_"+"GLOBAL_"+this.getRunnableName(); //if there's no calling object we have to just give it a arbitrary name "global"				
				}
				//
		} else {
			//else our id is just the source of the nas and its name
			 uniquename =  RunnablesObject+"_"+RunnableName;
							
		}
		
		
		
		return uniquename.toLowerCase();
	}

	/*
	public void scheduleRepeating(int periodMillis) {

		createNewTimerImpl(); //create new impl if needed
		
		
		TimerImplementation.scheduleRepeating(periodMillis);
		FuseLengthMS = periodMillis;
		StartTime = System.currentTimeMillis();	
	
		
		//add to active timers
		JAMTimerController.activeNamedActionSetTimers.put(getThisRunnablesID(), this);
	
		//note the name we end up with might be something like;
		//<GLOBAL>_nameofobject_nameofrtunnable
		
		Log.info("added "+RunnablesObject+"_"+RunnableName+" to repeated shedraled commands ");
		
	
		//update gamedatabox for debugging
		JAMTimerController.updateCurrentTimerDebugBoxs();
	
	}
	*/
	
	public  boolean isCanceled() {
		if (TimerImplementation==null){
			return true;
		}
		return false;
	}


	public static void triggerGlobalRunNamedActionSetAfter(final String name,
			int activateAfter,final SceneObject callingObject, boolean overwritePreviousAction) {
		//We also associate with the current scene
		//this purely so we can cancel all the NamedActionSets triggered by the scene
		SceneWidget currentScene =  SceneObjectDatabase.currentScene;
	
		NamedActionSetTimer newtimer = new NamedActionSetTimer(name,"<GLOBAL>",currentScene) {
			@Override
			public void run() {
	
				InstructionProcessor.testForGlobalActions(TriggerType.NamedActionSet, name, callingObject);
	
				super.removeFromActiveTimers();
				//remove after running
				//JAMTimer.activeNamedActionSetTimers.remove("<GLOBAL>_"+name);
			}
	
		};
	
		newtimer.schedule(activateAfter);
	
		if (overwritePreviousAction){
	
			//NamedActionSetTimer oldTimer = JAMTimer.activeNamedActionSetTimers.get("<GLOBAL>_"+name);
			//if (oldTimer!=null){
			//	oldTimer.cancel();
			//}
			JAMTimerController.cancelNamedActionSetTimer("<GLOBAL>", name);
	
	
		}
	
		//JAMTimer.activeNamedActionSetTimers.put("<GLOBAL>_"+name, newtimer);
	
	}


	/**
	 * This will run named action sets after a period on all objects with the namestring.
	 * I am not sure this is a good idea.
	 * Maybe it should be single object only like runnamedactionsetevery?
	 * 
	 * @param callingObject
	 * @param objectnamestring
	 * @param name_of_commands
	 * @param activateAfter
	 * @param overwritePreviousAction
	 */
	public static void triggerRunNamedActionSetAfter(
			final SceneObject callingObject,
			final String objectnamestring, 
			final String name_of_commands, 
			final int activateAfter, 
			boolean overwritePreviousAction) {
	
		//if only a name wassupplied we get the objects first
		Set<? extends SceneObject> requestedobjects = SceneObjectDatabase
				.getSceneObjectNEW(objectnamestring,callingObject,true);
		
		triggerRunNamedActionSetAfter(requestedobjects, name_of_commands,activateAfter, overwritePreviousAction);
		
	}
	
	public static void triggerRunNamedActionSetAfter(
			final Set<? extends SceneObject> runcommandsontheseobjects, 
			final String name_of_commands, 
			final int activateAfter, 
			boolean overwritePreviousAction) {
	
	
	
		//Set its action timers
		for (final SceneObject so : runcommandsontheseobjects) {
	
			String objectsName = so.getObjectsCurrentState().ObjectsName;
			//We also associate with the current scene
			//this purely so we can cancel all the NamedActionSets triggered by the scene
			SceneWidget currentScene =  SceneObjectDatabase.currentScene;
	
			NamedActionSetTimer newtimer = new NamedActionSetTimer(name_of_commands,objectsName,currentScene) {
				@Override
				public void run() {
	
					so.runNamedActionSet(name_of_commands);
					super.removeFromActiveTimers();
	
				}
	
			};
	
	
			if (overwritePreviousAction){
				JAMTimerController.cancelNamedActionSetTimer(objectsName, name_of_commands);
			
	
			}
	
			newtimer.schedule(activateAfter);
	
		}
	}


	/**
	 * creates a NAS that will run a JAM command set every period or period range
	 * 
	 * @param commandSourceObject
	 * @param name
	 * @param activatelow
	 * @param activateEveryHigh
	 * @param callingObject
	 * @param overwritePreviousAction
	 * @param initialFuseTime, -1 to not use (default, only used when resuming timers)
	 */
	public static void triggerRunNamedCommandsEvery(
			final String commandSourceObject,
			final String name_of_commands, 
			final int activatelow, final int activateEveryHigh,
			final SceneObject callingObject, 
			boolean overwritePreviousAction,
			final int initialFuseTime) {
	
		// if we are just using the Global actionsets and not any specific object
		if (commandSourceObject.equalsIgnoreCase("<GLOBAL>")){
	
			//We add the callingObject to the name so we can identify it uniquely
			//this is because the same global command might be running on a few objects at once
			//
			//note; calling object is not necessarily the same as runnables object (ie, where the action set is)
			final String uniquename;
			
			if (callingObject!=null){				
				uniquename = callingObject.getName()+"_"+name_of_commands;				
			} else {				
				uniquename = "GLOBAL_"+name_of_commands; //if there's no calling object we have to just give it a arbitrary name "global"				
			}
	
			//todo; in order to add resume, calling object must be stored too.
			//we could then also refractor the unique name bit to its own function maybe?
			
			//We also associate with the current scene
			//this purely so we can cancel all the NamedActionSets triggered by the scene
			SceneWidget currentScene =  SceneObjectDatabase.currentScene;
	
			
			//uniquename not used anymore
			final NamedActionSetTimer newtimer = new NamedActionSetTimer(callingObject,name_of_commands,commandSourceObject,currentScene,true,activatelow,activateEveryHigh) {
				@Override
				public void run() {
					//	Log.info("running global actions associated with timer:"+this.TimerImplementation.hashCode());
	
					//we simply run the global named actionset called this;
					InstructionProcessor.testForGlobalActions(TriggerType.NamedActionSet,name_of_commands, callingObject);
		
				}
	
	
			};
	
			//if a timer is already set for this object, and the overwritePreviousAction is true, we cancel it
			if (overwritePreviousAction){
	
				JAMTimerController.cancelNamedActionSetTimer(commandSourceObject, uniquename);
			
	
			}
	
			//pick random gap
			int range = (activateEveryHigh-activatelow);
			int nextGap = (int) (Math.random()*range)+activatelow;
	
			//if we have a initial fuse override use it (used when resuming action sets)
			if (initialFuseTime!=-1){
				nextGap=initialFuseTime;
			}
			
			InstructionProcessor.Log.info("sheduled action set");
			newtimer.schedule(nextGap);
	
			//exit, as we don't have to look for objects, this was a Global request!
			return;
		} 
	
	
		// as its not global we get the objects whos actionsets we want to run every certain time period
		
		Set<? extends SceneObject> curobjects = SceneObjectDatabase
				.getSceneObjectNEW(commandSourceObject,callingObject,true);
	
		// set its state
		for (final SceneObject so : curobjects) {
			//We also associate with the current scene
			//this purely so we can cancel all the NamedActionSets triggered by the scene
			SceneWidget currentScene =  SceneObjectDatabase.currentScene;
	
			final NamedActionSetTimer newtimer = new NamedActionSetTimer(callingObject,name_of_commands,commandSourceObject,currentScene,true,activatelow,activateEveryHigh) {
	
				@Override
				public void run() {
					so.runNamedActionSet(name_of_commands);
	
					/*
					//prepare next repeat if it hasn't been canceled by the above
					if (!this.isCanceled()){
	
						int range = (this.activateEveryLonghestTimePossibility-this.activateEvery_ShortestTimePossibility);	
						int nextGap = (int) (Math.random()*range)+this.activateEvery_ShortestTimePossibility;	
						this.schedule(nextGap);
						
						//pick next random gap (old)
						//int range = (activateEveryHigh-activatelow);	
						//int nextGap = (int) (Math.random()*range)+activatelow;	
						//this.schedule(nextGap);
						
					}*/
				}
	
			};
	
			//if a timer is already set for this object, and the overwritePreviousAction is true, we cancel it
			if (overwritePreviousAction){
				JAMTimerController.cancelNamedActionSetTimer(commandSourceObject, name_of_commands); //does this cancel globals?
			}
	
			//pick next random gap
			int range = (activateEveryHigh-activatelow);
			int nextGap = (int) (Math.random()*range)+activatelow;
	
			//if we have a initial fuse override use it (used when resuming action sets)
			if (initialFuseTime!=-1){
				nextGap=initialFuseTime;
			}
			newtimer.schedule(nextGap);
	
	
	
		}
	}
}