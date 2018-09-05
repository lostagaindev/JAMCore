package com.lostagain.Jam;


import java.util.logging.Logger;

import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.InstructionProcessing.ActionSet.TriggerType;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;

import lostagain.nl.spiffyresources.client.spiffycore.DeltaTimerController;
import lostagain.nl.spiffyresources.client.spiffycore.HasDeltaUpdate;

/**
 * Controls the games score, providing a animation of it homing in on its target
 * 
 * @author darkflame
 *
 */
public abstract class ScoreControll implements HasDeltaUpdate {
	public static Logger Log = Logger.getLogger("JamCore.ScoreControll");

	public static long CurrentScore = 0; //<currentscore> in instructionprocessor refers to this 
	
	
	/**
	 * A string that records what scores have been awarded.
	 * This prevents the player from duplicating up on scores that should only be awarded once.
	 * (for example if a game rewards discovery of a area)
	 * If not used, remember to use "-addscore=12345,true" to override
	 */
	public static String ScoresAwarded = "";

	/**
	 * score as its currently displayed (might be counting up/down to reach the real CurrentScore
	 */
	public double CurrentlyDisplayedScore = 0;
	
	/**
	 * has this been set up?
	 */
	private boolean initilised=false;

	
	//variables for  score transition;
	
	double initialScore = 0.0; //score at start of score transition
	double timeToUpdate = 5000; //time taken to transition to the new requested score
	double currentTime = 0.0; //time into current transition

	enum scoreChangeDirection {
		countDown,
		countUp,
		stopped
	}

	/**
	 * are we counting up or down?
	 */
	scoreChangeDirection direction = scoreChangeDirection.stopped;


	public ScoreControll() {
		super();
	}



	public void SetScore(long newscore) {
		//Timer homes-in on correct score, we now use delta based updates
		initialScore = CurrentScore; //start homing from current score
		currentTime=0.0;

		double amounttochangescoreby = newscore-CurrentScore;

		//set direction of change
		if (amounttochangescoreby>0){
			direction=scoreChangeDirection.countUp;			
		} else if (amounttochangescoreby<0) {
			direction=scoreChangeDirection.countDown;			
		};


		//set duration
		calculateAGoodTransitionDuraction(amounttochangescoreby);

		//set new score
		CurrentScore=newscore;		
		
		//start transition to that new score
		startUpdatingScoreVisuals();
	}

	public void AddScore(long AddThis) {

		//Timer homes-in on correct score, we now use delta based updates
		initialScore = CurrentScore; //start homing from current score
		currentTime=0.0;

		//set direction of change
		if (AddThis>0){
			direction=scoreChangeDirection.countUp;			
		} else if (AddThis<0) {
			direction=scoreChangeDirection.countDown;			
		} else {
			Log.info("add zero score? ok...I'll do that. There.done");
			return;
		}

		//set duration
		calculateAGoodTransitionDuraction(AddThis);
		
		//set new score		
		CurrentScore=CurrentScore+AddThis;

		//start transition to that new score
		startUpdatingScoreVisuals();
	}

	/**
	 * this will try to find a aproporate duration for a given change in score
	 * @param amounttochangescoreby
	 */
	public void calculateAGoodTransitionDuraction(double amounttochangescoreby) {

		amounttochangescoreby=Math.abs(amounttochangescoreby); //convert to positive if negative (we just want the differance, we dont care about direction here)

		//time taken to count should be slightly proportional to change size
		//We used some regression to find this formula based on;
		// change  = duration
		//     100 = 1000ms
		// 1000000 = 5000ms
		//10000000 = 7000ms
		//
		//A forular that approximates this is;
		//Y = 503.9218(X^0.1741) 
		//which I worked out using; http://www.had2know.com/academics/regression-calculator-statistics-best-fit.html

		timeToUpdate =( 503.9218*(Math.pow(amounttochangescoreby,0.1741))); //get duration using formula

		//cap to a min and max;
		if (timeToUpdate<1000){
			timeToUpdate=1000;
		} else if (timeToUpdate>7000) {
			timeToUpdate=7000;
		}


		Log.info("durection of update="+timeToUpdate);
	}

	public void startUpdatingScoreVisuals(){


		setVisible(true); //ensure visible (we should be invisible till we are first used)

		if (!initilised){
			//add to display?
			//place score display on its tag
			RequiredImplementations.PositionByTag(JAMcore.PlayersScore.getVisualRepresentation(), "scoreDisplayHolder"); //note; this will do nothing if the id isnt found
			initilised=true;
		}

		startDeltaUpdates();
		

		Log.info("started to Update ScoreVisuals=");

	}

	/**
	 * fired when the visuals reach their target and match the CurrentScore
	 */
	public void stopUpdatingScoreVisuals(){
		this.stopDeltaUpdates();
		
		//hide if on zero
		if (CurrentScore==0){
			setVisible(false);
		}
		

		//fire any triggers here?
		SceneObjectDatabase.currentScene.testForSceneActions(TriggerType.OnScoreCountingEnd, null);
		InstructionProcessor.testForGlobalActions(TriggerType.OnScoreCountingEnd, null, null);
	}


	
	private void startDeltaUpdates() {
		DeltaTimerController.addObjectToUpdateOnFrame(this);
	}

	private void stopDeltaUpdates() {
		DeltaTimerController.removeObjectToUpdateOnFrame(this);
		currentTime=0.0;
	}

	@Override
	public void update(float deltams) {

		currentTime=currentTime+deltams; //cur time
		double alpha = currentTime/timeToUpdate; //0.0=start 1.0 = done (alpha in this context just means representing how far we are between two states, its got nothing to do with opacity)

		//adjust alpha for smoother transition (slow down at start and end, like ease in/out)
		//alpha= alpha * alpha * (3 - 2 * alpha);
		//smoother
		//alpha= alpha*alpha*alpha*(alpha*(alpha*6 - 15) + 10);		
		//alpha = (-20*Math.pow(alpha,7))+(70*Math.pow(alpha,6))+(-84*Math.pow(alpha,5))+(35*Math.pow(alpha,4));			
		//alpha = Interpolationcircle(alpha); //supposed to make it smoother, but doesnt seem to do much effect   http://easings.net/
		
		
		//we take the score we started at, and the target, and work out where the "alpha point" is between them
		//start = initialscore
		//end   = currentscore

		CurrentlyDisplayedScore = initialScore+((CurrentScore-initialScore)*alpha); //temp, basic lerp




		//if within 1.5 of the target
		//note; we need to check both directions in case we are counting down.
		if  (
				(CurrentlyDisplayedScore+1.5 >= CurrentScore) && (direction==scoreChangeDirection.countUp)  
				||
				(CurrentlyDisplayedScore-1.5 <= CurrentScore) && (direction==scoreChangeDirection.countDown)
				) 
		{
			
			CurrentlyDisplayedScore=CurrentScore;
			updateVisualDisplayTo(CurrentlyDisplayedScore);
			direction=scoreChangeDirection.stopped;
			this.stopUpdatingScoreVisuals();
			
		} else {
			
			//difference
			//CurrentlyDisplayedScore=Math.floor(CurrentlyDisplayedScore-Difference);			
			updateVisualDisplayTo(CurrentlyDisplayedScore);

		}


	}

	
	
	static private final float Interpolationcircle (double alpha)
	{
		//circle out;
		alpha--;
		return (float)Math.sqrt(1 - alpha * alpha);
		
		//for circle in and out;
		/*
			if (alpha <= 0.5f) {
				alpha *= 2;
				return (1 - (float)Math.sqrt(1 - alpha * alpha)) / 2;
			}
			alpha--;
			alpha *= 2;
			return ((float)Math.sqrt(1 - alpha * alpha) + 1) / 2;*/
		
	};


	public abstract void updateVisualDisplayTo(double currentlyDisplayedScore2);

	public abstract void setVisible(boolean show);

	public abstract Object getVisualRepresentation();


}
