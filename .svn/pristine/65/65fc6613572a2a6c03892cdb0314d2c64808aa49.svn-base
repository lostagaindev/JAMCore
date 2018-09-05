package com.lostagain.Jam.SceneObjects;

import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;

//-----------------------------------------------movement related variables end
//-----------------------------------------------collision handling;
/**
 * Stores the collision method used when checking this object against others. <br>
 * <br>
 * NOTE: This currently applys to collision tests, but not the objects "width" when pathfinding. That is still bottom line, 
 * provided this isnt set to none
 * 
 * Note the presence of a cmap will override this in some cases where specific collisions are needed
 * in future we should refraction the cmap into this class as well
 **/
public class CollisionModeSpecs {

	static Logger Log = Logger.getLogger("JAMCore.CollisionModeSpecs");


	public enum CollisionType {
		/** Default:no collision detection, will not register as hitting anything **/
		none,
		/** Uses the objects lowest edge in its bounding box. This is good for isometrix games where the floor is the collision map **/
		bottomline,
		/** bounding box, defaults to spites boundary but can be specified too **/
		box,
		/** uses the pivot point only **/
		point;
	}

	CollisionType collisionType = CollisionType.none;

	public CollisionType getCollisionType() {
		return collisionType;
	}


	public Optional<CollisionBox> customCollisionBox = Optional.absent();

	/** the approximate height of the object **/
	int height=0;

	public CollisionModeSpecs(CollisionType collisionType, Optional<CollisionBox> customCollisionBox, int height) {
		super();
		this.collisionType = collisionType;
		this.customCollisionBox = customCollisionBox;
		this.height = height;
	}

	public CollisionModeSpecs(CollisionType type) {
		super();
		this.collisionType = type;
	}
	/**
	 * returns this CollisionType in the 
	 */
	public String toString(){
		if (customCollisionBox.isPresent()){

			return collisionType.name()+"("+customCollisionBox.get().toString()+")";

		} else {
			return collisionType.name();
		}

	}


	public static CollisionModeSpecs parseString(String specifiedCollisionType){

		String pieces[] = specifiedCollisionType.split("\\(");
		String mode = pieces[0].trim();

		//get type 
		CollisionType type = CollisionType.valueOf(mode);

		//optional bb
		Optional<CollisionBox> customCollisionBox = Optional.absent();

		//take parameter data from brackets if present
		if (pieces.length>1){   
			String parameters = pieces[1].trim();
			//remove ending brackets too
			parameters= parameters.substring(0, parameters.length()-1);

			//assign parameters parsed based on mode
			switch (type) {
			case bottomline:
				Log.info("parameters supplied, but bottomline mode doesnt use them:"+parameters);

				break;
			case box:
				Log.info("parameters supplied for box collision:"+parameters);

				//parameters should be in the format  (tlx,tly,brx,bry)
				customCollisionBox = Optional.of(new CollisionBox(parameters));	

				break;
			case none:				
				Log.info("parameters supplied, but \"none\" mode doesnt use them:"+parameters);				
				break;								
			case point:
				Log.info("parameters supplied, but point mode doesnt use them:"+parameters);				
				break;
			default:
				break;


			}


		}

		int customHeight = 0;

		return new CollisionModeSpecs(type,customCollisionBox,customHeight);

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collisionType == null) ? 0 : collisionType.hashCode());
		result = prime * result + ((customCollisionBox == null) ? 0 : customCollisionBox.hashCode());
		result = prime * result + height;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CollisionModeSpecs other = (CollisionModeSpecs) obj;
		if (collisionType != other.collisionType)
			return false;
		if (customCollisionBox == null) {
			if (other.customCollisionBox != null)
				return false;
		} else if (!customCollisionBox.equals(other.customCollisionBox))
			return false;
		if (height != other.height)
			return false;
		return true;
	}


	/**
	 * returns true if this object represents the default JAM engine settings for a objects collision
	 * (ie None with 0 height and a absent collision box)
	 */
	public boolean  isDefaultSettings() {

		if (collisionType!=CollisionType.none){
			return false;
		}
		if (height!=0){
			return false;
		}
		if (customCollisionBox.isPresent()){
			return false;
		}

		return true;
	}


}