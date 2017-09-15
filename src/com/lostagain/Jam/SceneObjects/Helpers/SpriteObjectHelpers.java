package com.lostagain.Jam.SceneObjects.Helpers;

import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectType;
import com.lostagain.Jam.SceneObjects.SceneSpriteObjectState;
import com.lostagain.Jam.SceneObjects.Interfaces.IsSceneSpriteObject;

import lostagain.nl.spiffyresources.client.spiffycore.FramedAnimationManager;

public class SpriteObjectHelpers {

	/**
	 * HELPER METHOD FOR SPRITE OBJECTS<br>
	 * <br>
	 * If using a FramedAnimationController, this helper method can set the specified sprite to the correct animation <br>
	 * mode for you, updating its state at the same time.<br>
	 * Note; you currently still have to update objectsCurrentState.ObjectsURL to SceneObjectIcon.getUrl(); yourself though<br>
	 * where getURL is the url of the sprite after this status change has been applied.<br>
	 * 	   <br>
	 * @param animationsStatus
	 * @param sprite
	 * @param animationController
	 **/
	public static void setAnimationsStatusHelper(String animationsStatus, IsSceneSpriteObject sprite, FramedAnimationManager animationController) {
		SceneSpriteObjectState objectState = sprite.getObjectsCurrentState();
		
		// set Items based on state
		sprite.ObjectsLog("Setting :" + objectState.ObjectsFileName
							+ " to " + animationsStatus + " with "
							+ objectState.currentNumberOfFrames + " frames");
		
	
		if ( objectState.currentNumberOfFrames==1){
			sprite.ObjectsLog("only 1 frame, so not bothering to change states and setting animation to paused");
			SceneObject.SOLog.info("only 1 frame, so not bothering to change states and setting animation to paused");
			animationController.pauseAnimation();
			return;
	
		}
	
	
		if (animationsStatus.equalsIgnoreCase("PlayForward")) {
			sprite.ObjectsLog("playing forward");
			animationController.setPlayForward();
		} else if (animationsStatus.equalsIgnoreCase("PlayBounceLoop")) {
	
			// should be;
			SceneObject.SOLog.info("____________________play bounce loop set");
			animationController.setPlayForwardThenBackLoop();
	
		} else if (animationsStatus.equalsIgnoreCase("PlayBounce")) {
			animationController.setPlayForwardThenBack();
		} else if (animationsStatus.equalsIgnoreCase("PlayBack")) {
			animationController.setPlayBack();
		} else if (animationsStatus.equalsIgnoreCase("Pause")) {
			animationController.pauseAnimation();
		} else if (animationsStatus.equalsIgnoreCase("Stop")) {
			animationController.stopAnimation();
		} else if (animationsStatus.equalsIgnoreCase("PlayLoop")) {
			animationController.setPlayLoop();
		} else if (animationsStatus.toLowerCase().startsWith("gotoframe")) {
	
			String targetFrameStr = animationsStatus.substring(9);
			int targetFrame = Integer.parseInt(targetFrameStr);
			SceneObject.SOLog.info("Setting frame to:" + targetFrame);
	
			animationController.gotoFrame(targetFrame);
		} else if (animationsStatus.equalsIgnoreCase("NextFrameLoop")) {
			animationController.nextFrameLoop();
		} else if (animationsStatus.equalsIgnoreCase("PrevFrameLoop")) {
			animationController.prevFrameLoop();
		} else if (animationsStatus.equalsIgnoreCase("NextFrame")) {
			animationController.nextFrame();
		} else if (animationsStatus.equalsIgnoreCase("PrevFrame")) {
			animationController.prevFrame();
		} else if (animationsStatus.equalsIgnoreCase("PlayForwardXframes")) {
			// 5 needs to be changed to variable number
			// SceneObjectIcon.playForwardXframes(4);
		} else {
			SceneObject.SOLog.info("Not a recognised animation state:" + animationsStatus);
	
			return;
		}
	
		
		objectState.currentFrame = animationController.currentframe;		
		objectState.currentlyAnimationState = animationController.serialiseAnimationState();
		//objectState.ObjectsURL   = animationController.getUrl();//cant do this here yet
		
		// update any debuging info if used
		//sprite.updateDebugInfo();
		
	}

	/**
	 * This should be run on each frame change.
	 * It will sync the objectstate data, and update and child objects if needed
	 * 
	 * TODO;
	 * add dependant url support
	 * @param sceneSpriteObject
	 */
	static public void runOnFrameChangeSpriteHelper(IsSceneSpriteObject sceneSpriteObject) {
		
		SceneSpriteObjectState objectsstate = sceneSpriteObject.getObjectsCurrentState();
		SceneObject asSceneObject = (SceneObject) sceneSpriteObject; //safe to cast as all Is___ are sceneobjects of some type
		
		sceneSpriteObject.getCurrentFrame(); //this get function updates the states frame by itself, so we skip the below line now as its redundant		
		//objectsstate.currentFrame = SceneObjectIcon.getCurrentframe();		
		sceneSpriteObject.getCurrentURL(); //this get function updates the states url by itself, so we skip the below line as its now redundant 
		//objectsstate.ObjectsURL = SceneObjectIcon.getUrl();				
	
	
	
		//Log.info("currentObjectState.ObjectsURL ="+currentObjectState.ObjectsURL);
		//Log.info("current SceneObjectIcon strings:"+ SceneObjectIcon.basefilename + "" + SceneObjectIcon.currentframe + "." + SceneObjectIcon.filenameext);
	
		//ObjectsLog.info("ObjectsURL after frame change-" + objectsCurrentState.ObjectsURL);
		
		
		//if this object has a attachmentPoints glu file and it specifys its primary pin there, then we need to update our pin
		//note; this is useful when you have a sprites visual position change but dont want its scene position too.
		//maybe even the size of the sprite changes, but you still want it clamped at a point that isnt the topleft
		if (asSceneObject.attachmentPoints!=null && asSceneObject.attachmentPoints.hasPrimaryPinData()){
			
			MovementWaypoint wp=asSceneObject.attachmentPoints.getPrimaryPinDataFor(objectsstate.currentFrame);
			
			if (wp!=null){
				asSceneObject.setPin(wp.pos.x, wp.pos.y, wp.pos.z, false);	
				//ObjectsLog("refreshing position with pin: "+wp.pos.x+","+wp.pos.y+","+wp.pos.z); //Perhaps only fire if pin changed?
				//refreshPosition();
				
			} else {
				//do nothing
			}
			
		}
	
				
		//if an object is positioned relative to this object AND this object has attachment points, we have
		//to update, as things might be attached to a moving point on the animation. (ie, a hat might be attached to a head that can move up and down)
		//Note; the objects being positioned to this might not necessarily use the attachment points
		//So there still can be some waste here, but its probably more resources to pre-check all relative objects
		//first.
		if (asSceneObject.relativeObjects!=null && asSceneObject.attachmentPoints!=null){
			asSceneObject.updateThingsPositionedRelativeToThis(true);
		}
		
		
		if (asSceneObject.relativeObjects!=null){

			
			//parent url needs stripping to last slash
			//(have to look for both slashs
			int indexOfLastSlash = objectsstate.ObjectsURL.lastIndexOf("/");
			int indexOfLastSlash2 = objectsstate.ObjectsURL.lastIndexOf("\\");
			if (indexOfLastSlash2>indexOfLastSlash){
				indexOfLastSlash=indexOfLastSlash2;
			}
			
			
			String parentURL = objectsstate.ObjectsURL.substring(indexOfLastSlash+1);
			
		
			//tell children our url has changed
			for (SceneObject child : asSceneObject.relativeObjects) {
				
				child.parentsURLJustChanged(parentURL);
				
			
			}
			
			
		}
		
		
		
		
	}

	/**
	 * sets the sprites url to match exactly whats given.
	 * It will not assume its the first frame of a animation. It will just take the image url and display it, as
	 * well as updating the state.
	 * (framecount will be set to 1)
	 * 
	 * @param asSprite
	 * @param string
	 */
	public static void setSpriteUrlExactly(IsSceneSpriteObject Sprite, String url) {
		
		SceneSpriteObjectState objectsCurrentState = Sprite.getObjectsCurrentState();
		
		objectsCurrentState.ObjectsURL = url;
		objectsCurrentState.currentNumberOfFrames = 1;
		objectsCurrentState.currentFrame=0;
		objectsCurrentState.ObjectsFileName = url;
		
		((SceneObject)Sprite).recheckAttachmentPoints(objectsCurrentState);
		
		Sprite.setURLPhysically(false);
		
		
	}

}
