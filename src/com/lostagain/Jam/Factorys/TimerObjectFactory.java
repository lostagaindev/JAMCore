package com.lostagain.Jam.Factorys;


//method to produce a object able to play,pause or resume a sound.
//designed for sceneobjects to be able to own their own sounds. = maybe one day have volume based on distance to screen center?
/**
 * setup has to be run for this to work.
 * You MUST supply a implemented subtype (such as GWTSoundFactory) in the static setup(..) function
 * for scenes sounds to work. This factory, as well as the IsJamSoundObject interface allows the JAMCore to create new sound objects
 * play, and stop them without even understanding what a sound is ;)
 * 
 * @author darkflame
 *
 */
public abstract class TimerObjectFactory {
		
	static TimerObjectFactory instance;

	public static void setup(TimerObjectFactory subClass) {
		instance = subClass;
	}
		
	static public IsTimerObject getNewTimerClass(Runnable triggerThis) {	
		return instance.getNewTimerClassImpl(triggerThis);
	}
	
	
   public abstract IsTimerObject getNewTimerClassImpl(Runnable triggerThis);
   
   

}
