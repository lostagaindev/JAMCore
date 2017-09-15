package com.lostagain.Jam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.collect.HashMultimap;
import com.lostagain.Jam.JAMTimerController.IsIncrementalCommand;
import com.lostagain.Jam.Factorys.IsTimerObject;
import com.lostagain.Jam.Factorys.NamedActionSetTimer;
import com.lostagain.Jam.Scene.SceneWidget;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;

/**
 * Manages the timing functions in the game.
 * See DeltaTimeController for more details
 * 
 * @author darkflame
 *
 */
public abstract class JAMTimerController extends DeltaTimerController  {

	public static Logger Log = Logger.getLogger("JAMCore.JAMTimerController");
	/** 
	 * list of all active named action set timers 
	 * This lets us both start and stop repeating actions 
	 ***/
	public static HashMultimap<String,NamedActionSetTimer> activeNamedActionSetTimers = HashMultimap.create();
	//new HashMultimap<String,NamedActionSetTimer>();

	static JAMTimerController instance;


	protected static void setup(JAMTimerController subclassOfJamTimerController){

		DeltaTimerController.setup(subclassOfJamTimerController);
		instance = subclassOfJamTimerController;

	}


	//For generic timer creation	
	static public IsTimerObject getNewTimerClass(Runnable triggerThis) {
		Log.info("creating new timer");
		
		return instance.getNewTimerClassImpl(triggerThis);
	}


	public abstract IsTimerObject getNewTimerClassImpl(Runnable triggerThis);



	//timer feedback	
	public static void updateCurrentTimerDebugBoxs(){
		//((JAMTimerController)instanceOfDeltaTimerController).updateCurrentTimerDebugBoxImplm();
		instance.updateCurrentTimerDebugBoxImplm();
	}


	/**
	 * cancel all the named action sets with this name associated with this object
	 * 
	 * @param objectname
	 * @param actionsetname
	 */
	//todo; might have to make this more robust, not sure it will correctly cancel globals
	//the id system seems not flexible enough? 
	//non globals are objectname_actionname as their id
	//but globals might be "<GLOBAL>_"+objectThatCalledThisNAS.getName()+"_"+this.getRunnableName();	
	//or even "<GLOBAL>_GLOBAL_"+this.getRunnableName() if there was no calling object
	public static void cancelNamedActionSetTimer(String objectname, String actionsetname) {

		objectname    = objectname.toLowerCase().trim();
		actionsetname = actionsetname.toLowerCase().trim();

		//we make a copy to prevent concurrent modification errors.
		//This is because the .cancel class of timer will remove stuff from the list we are looping over.
		//copying is a bit crude, but should be ok as this command wont be called much.		
		
		//todo; do this differently for global
		String namedactionsset_id = objectname+"_"+actionsetname;
		
		
		
		Set<NamedActionSetTimer> timers = new HashSet<NamedActionSetTimer>( activeNamedActionSetTimers.get(namedactionsset_id) );

		
		for (NamedActionSetTimer timer : timers) {

			if (timer!=null){

				timer.cancel();							

				if ( activeNamedActionSetTimers.get(namedactionsset_id)!=null){

					Log.info(" still on list for some reason:"+activeNamedActionSetTimers.size());
					Log.info(" still on list for some reason:"+activeNamedActionSetTimers.toString());

				} else {

					Log.info("Canceled remaining:"+activeNamedActionSetTimers.toString());				

				}			

			} else {
				
				Log.info("could not find NamedActionSetTimer: "+ namedactionsset_id );					

			}

		}
	}


	/**
	 * returns true if a namedActionSet is scheduled on the current object.
	 * Use \<GLOBAL\> as the object name for global action sets
	 * 
	 * @param objectname
	 * @param actionsetname
	 * @return
	 **/
	public static boolean isNamedActionSetShreduled(String objectname, String actionsetname) {

		objectname = objectname.toLowerCase().trim();
		actionsetname = actionsetname.toLowerCase().trim();

		return activeNamedActionSetTimers.containsKey(objectname+"_"+actionsetname);
	}


	public abstract void updateCurrentTimerDebugBoxImplm();



	static public void stopUpdatingAllObjects(){
		DeltaTimerController.stopUpdatingAllObjects();


		Log.info("Stopping all NamedActionSetTimer timers");		

		//stop all NamedActionSetTimer
		Iterator<NamedActionSetTimer> nas = activeNamedActionSetTimers.values().iterator();
		while (nas.hasNext()) {
			NamedActionSetTimer timerWasFor = (NamedActionSetTimer) nas.next();

			timerWasFor.TimerImplementation.cancel();
			timerWasFor.TimerImplementation = null;

			nas.remove();
		}



	}


	/**
	 * stops all the timers that were created while on the specified scene
	 */
	static public void stopAllTimersCreatedFromScene(SceneWidget scene){

		Log.info("Stopping all NamedActionSetTimer timers from scene:"+scene.SceneFileName);		

		//stop all NamedActionSetTimer		
		Iterator<NamedActionSetTimer> nas = activeNamedActionSetTimers.values().iterator();

		while (nas.hasNext()) {
			NamedActionSetTimer timerWasFor = (NamedActionSetTimer) nas.next();

			if (timerWasFor.getSceneCurrentWhenCreated() == scene){

				Log.info("Stopping timer:"+timerWasFor.getRunnableName());	

				timerWasFor.TimerImplementation.cancel();
				timerWasFor.TimerImplementation = null;

				nas.remove();
			}
		}



	}

	

	//also a incremental update?
	//defered command?
	//both things that can make gwt implementations run better, but not relivant to normal java/desktop
	//
	//incremental will need a Isincremental interface, something that runs and returns a boolean saying if it would run again

	//do both with overridable commands here?
   public static interface IsIncrementalCommand {
	   public boolean run();
   }
   
   
   public static void scheduleIncremental(IsIncrementalCommand cmd) {
		instance.scheduleIncremental_impl(cmd);
	}
   

   public static void scheduleDefered(Runnable cmd) {
		instance.scheduleDefered_impl(cmd);
	}
   
   
   /**
    * if cmd returns true it will rerun
    * 
    * Note; this is mostly here so gwt can override it to run its own scheduleIncremental method which is designed to run repeating commands
    * on browsers in a cpu friendly way
    * 
    * @param cmd
    */
   public void scheduleIncremental_impl(IsIncrementalCommand cmd){
	   boolean rerun = cmd.run();
	   if (rerun){
		   scheduleIncremental_impl(cmd);
	   }
   }
   
   
   /**
    * Will run the commands straight away
    * Unless the implementation overrides this to add a nesscery delay (in gwt these commands would run after giving the browser time to update)
    * 
    * @param cmd
    */
   public void scheduleDefered_impl(Runnable cmd){
	   cmd.run();
   }



   
   
   
   
   
   
}
