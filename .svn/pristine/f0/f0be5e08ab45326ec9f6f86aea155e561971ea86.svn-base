package com.lostagain.Jam.Movements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectState;

import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/**
 * This class contains all the data for an object currently moving along a path of
 * some sort. There's a lot of data, as there's a lot of possible
 * movement states 
 ***/
public class MovementState {
	final static Logger Log = Logger.getLogger("JAMCore.MovementState");

	public enum MovementStateType {		
		None,
		PhysicsBased,
		OnLinePath,
		OnCurvePath
	}	

	/**
	 * If you want to use a "CurrentMovement" conditional to check for physics, use this as the movement ''name'' parameter
	 **/
	public final static String CONDITIONAL_PHYSICS_SIGNFIER = "INTERNAL_PHYSICS";
	
	
	//for future use : (also replaces "oncurve" boolean)
	public MovementStateType currentmovementtype = MovementStateType.None;


	private static double gravity = -9.81 * 100; //gravity magnitude in meters (we should maybe have a gravity setting somewhere for the vector of acceleration)
	public static double GravityMS = (gravity / (1000.0 * 1000.0)); //gravity acceleration per ms 

	
	
	
	/**
	 * Set the default gravity movement states are created with<br>
	 * This is how many pixels should a object falls by in the first second of the fall <br>
	 * For example, if you are doing realistic gravity and the scale of the scene is 100 pixels = 1 meter<br>
	 * then the gravity you should set is; <br>
	 * -9.81 * 100 = -981.0 (pixels per second)<br>
	 * <br>
	 * @param gravity
	 */
	public static void setGravity(double gravity) {
		MovementState.gravity   = gravity;
		MovementState.GravityMS = (gravity / (1000.0 * 1000.0)); //gravity acceleration per ms 

	}


	


	//--------------------
	//Needed for all modes:
	/**
	 * the current position on the scene
	 */
	public SimpleVector3 movement_current_pos = new SimpleVector3(-1,-1,-1); //used to be Simple3DPoint

	/**
	 * the current velocity for each dimension<br>
	 * worked out dynamically in the case of paths at each node<br>
	 * for physics based its changed each delta update from the acceleration (assuming there is any)<br>
	 * else it remains the same <br>
	 */	
	public SimpleVector3 movement_vel  = new SimpleVector3(0,0,0);

/**
 * how much does our speed reduce by each ms?
 * (never goes below zero)
 */
	SimpleVector3 movement_friction = new SimpleVector3(0,0,0);
	
	
	public void setMovement_friction(double movement_friction) {
		this.movement_friction.x=movement_friction;
		this.movement_friction.y=movement_friction;
		this.movement_friction.z=movement_friction;
		
	}
	
	/**
	 * @param movement_friction
	 */
	public void setMovement_friction(SimpleVector3 movement_friction) {
		this.movement_friction.set(movement_friction);
		
	}



	

	//----------------------	
	//for paths
	public int movement_currentWaypoint = -1; // current number of waypoint
	public String currentmovementpathname="";
	public Simple3DPoint movement_dest = new Simple3DPoint(-1,-1,-1);

	public double movement_speed = -1; //directionless pixels per ms (ie real speed that should be maintained)

	//For path line mode only
	//-----	


	//For path curve mode only
	//-----	
	//public int movement_SX = -1; 
	//public int movement_SY = -1; //
	public SimpleVector3 movement_SC  = new SimpleVector3(-1,-1,-1);// start x/y/z used for curves
	
	//public int movement_CX = -1; // control x/y
	//public int movement_CY = -1;
	public SimpleVector3 movement_CC  = new SimpleVector3(-1,-1,-1);// start x/y/z used for curves

	public double  movement_curveTime     = 0;
	public double  movement_curveTimeStep = 0;


	//For physics mode only
	//-----	
	public SimpleVector3 movement_acc  = new SimpleVector3(0,0,GravityMS);

	static final double defaultBounceEnergyRetention = 0.5;
	public double bounceEnergyRetention = defaultBounceEnergyRetention;




	/**
	 * A temp variable only used if this object is currently being dropped.
	 * It remembers its previous parent and re-attaches relative to it when it lands.
	 * (so, its most useful when the parent object has been destroyed and the object attached  to it lands on rubble)
	 */
	public String parentBeforeDropping = null;
	public boolean hasLinkedZindexBeforeDropping = false; //we need to track this separately as when dropped we also lose our linked zindex

	
	/**
	 * the current path data this object should be animating over if going along a path. MovementPaths are
	 * currently 2D only.
	 * Note: not currently saved. we would need to serialize/deseraialise it for that
	 **/
	public MovementPath						currentPathData			= null; 


	/** Creates a new, blank, movement state object
	 * Gravity is seet to GravityMS by default **/
	public MovementState(){
		//leave default values
	}

	/** Creates a new, blank, movement state object
	 * Gravity is seet to GravityMS by default **/
	public MovementState(String currentmovement, 
			MovementPath pathdata,
			MovementStateType currentmovementtype,
			//	boolean isMoving,
			int movement_currentWaypoint, 
			SimpleVector3 movement_SC,
			SimpleVector3 movement_CC,
			
			Simple3DPoint movement_dest,
			SimpleVector3 movement_vel,
			SimpleVector3 movement_acc,  
			SimpleVector3 movement_friction,
			SimpleVector3 movement_current,
			double movement_speed,
			double movement_curveTime,
			double movement_curveTimeStep,
			double bounceEnergyRetention,
			String parentBeforeDropping,
			boolean hasLinkedZindexBeforeDropping
			) {

		
		super();

		this.currentmovementpathname=currentmovement;
		this.currentPathData = pathdata;
		this.currentmovementtype = currentmovementtype;

		//this.isMoving=isMoving;
		this.movement_currentWaypoint = movement_currentWaypoint;

	//	this.movement_SX = movement_SX; 
	//	this.movement_SY = movement_SY;
		this.movement_SC = movement_SC;
		//this.movement_CX = movement_CX;
		//this.movement_CY = movement_CY;
		this.movement_CC = movement_CC;
		
		//this.movement_DX = movement_DX;
		//this.movement_DY = movement_DY;
		//this.movement_DZ = movement_DZ;
		this.movement_dest = movement_dest;

		//this.movement_VelX = movement_StepX;
		//this.movement_VelY = movement_StepY;
		this.movement_vel=movement_vel;
		this.movement_acc  = movement_acc;

		this.movement_friction=movement_friction;
		//	this.movement_currentX = movement_currentX;
		//this.movement_currentY = movement_currentY;
		//this.movement_currentZ = movement_currentZ;
		this.movement_current_pos = movement_current;

		this.movement_speed = movement_speed;

		//	this.movement_onCurve =movement_onCurve;
		this.movement_curveTime =movement_curveTime;
		this.movement_curveTimeStep =movement_curveTimeStep;
		this.bounceEnergyRetention = bounceEnergyRetention;
		

		this.parentBeforeDropping= parentBeforeDropping;
		this.hasLinkedZindexBeforeDropping = hasLinkedZindexBeforeDropping;
	}

	/** clones all the data **/
	public MovementState copy(){
		MovementPath copyofcurrentpathdata = null;
		if (currentPathData!=null){
			 copyofcurrentpathdata = new MovementPath(currentPathData,currentPathData.pathsName);
		}
		
		MovementState newcopy = new MovementState(
				currentmovementpathname, 
				copyofcurrentpathdata,
				currentmovementtype,
				//			isMoving,
				movement_currentWaypoint,  
				//movement_SX,movement_SY,  
				movement_SC.copy(),
			//	movement_CX,  movement_CY,
				movement_CC.copy(),
				movement_dest.copy(),
				movement_vel.copy(),
				movement_acc.copy(),
				movement_friction.copy(),
				movement_current_pos.copy(),
				movement_speed,				 
				//movement_onCurve,
				movement_curveTime,
				movement_curveTimeStep,
				bounceEnergyRetention,
				parentBeforeDropping,
				hasLinkedZindexBeforeDropping
				);

		return newcopy;
	}

	/**
	 * gets all the fields ready for serialisation.
	 * Note: the field field is the typename.
	 * When deserialising, be sure to crop this off
	 * @return
	 */
	public ArrayList<Object> getAllFieldsAsArrayList() {

		ArrayList<Object> fieldList = new ArrayList<Object>();


		fieldList.add(currentmovementtype.name());	
		
		// only store the rest if we have a setting other then none
		if (currentmovementtype!=MovementStateType.None){

		//rest should vary based on type I think

		fieldList.add(currentmovementpathname);

		//fieldList.add(isMoving);
		fieldList.add(movement_currentWaypoint);

	//	fieldList.add(movement_SX);
	//	fieldList.add(movement_SY);
//
		fieldList.add(movement_SC.x);
		fieldList.add(movement_SC.y);
		fieldList.add(movement_SC.z);
		
		fieldList.add(movement_CC.x); 
		fieldList.add(movement_CC.y);
		fieldList.add(movement_CC.z);

		//combine these?
		fieldList.add(movement_dest.x);
		fieldList.add(movement_dest.y);
		fieldList.add(movement_dest.z);

		fieldList.add(movement_vel.x);
		fieldList.add(movement_vel.y);
		fieldList.add(movement_vel.z);
		//--
		fieldList.add(movement_acc.x);
		fieldList.add(movement_acc.y);
		fieldList.add(movement_acc.z);
		
		fieldList.add(movement_friction.x);
		fieldList.add(movement_friction.y);
		fieldList.add(movement_friction.z);
		
		//--
		fieldList.add(movement_current_pos.x);
		fieldList.add(movement_current_pos.y);
		fieldList.add(movement_current_pos.z);
		//--

		fieldList.add(movement_speed);
	//	fieldList.add((int)(movement_speed*SceneObjectState.SAVE_MULTIPLIER)); //we x10000.0 on saving and divide by it on loading. This lets us store a small decimal value in a int (ie, values like 0.05 dont get rounded to 0)

		fieldList.add(bounceEnergyRetention);

		fieldList.add(parentBeforeDropping);
		fieldList.add(hasLinkedZindexBeforeDropping);
		}

		return fieldList;

	}

	/**
	 * Load the parameters.
	 * Note, the type should be specified seperately and not in the iterator list like it is on generation
	 * This is to allow the loading of parameters only if movementstate is not none.
	 * IF it is none, you can bi-pass creating this whole object.
	 * 
	 * Note; Supplying a type None manually here will result in no data but the movement type being set
	 * 
	 * @param type
	 * @param incomingdata
	 */
	public void loadParameters(MovementStateType type,Iterator<String> incomingdata) {


		//currentmovementtype = MovementStateType.valueOf(incomingdata.next());

		currentmovementtype=type;
		// we only use the rest if we have a setting other then none
		if (currentmovementtype==MovementStateType.None){
			
			return;
		}
		
		
		currentmovementpathname = incomingdata.next();

		//	isMoving = Boolean.parseBoolean(incomingdata.next());
		movement_currentWaypoint= Integer.parseInt(incomingdata.next()); 

	//	movement_SX= Integer.parseInt(incomingdata.next()); 
		//movement_SY= Integer.parseInt(incomingdata.next()); 
		
		

		movement_SC.x = Double.parseDouble(incomingdata.next()); 
		movement_SC.y = Double.parseDouble(incomingdata.next()); 		
		movement_SC.z = Double.parseDouble(incomingdata.next()); 

		movement_CC.x= Double.parseDouble(incomingdata.next()); 
		movement_CC.y= Double.parseDouble(incomingdata.next()); 
		movement_CC.z= Double.parseDouble(incomingdata.next()); 

		movement_dest.x= Integer.parseInt(incomingdata.next()); 
		movement_dest.y= Integer.parseInt(incomingdata.next()); 
		movement_dest.z= Integer.parseInt(incomingdata.next()); 

		//movement_VelX   = Double.parseDouble(incomingdata.next()); 
		//movement_VelY   = Double.parseDouble(incomingdata.next()); 

		movement_vel.x = Double.parseDouble(incomingdata.next()); 
		movement_vel.y = Double.parseDouble(incomingdata.next()); 		
		movement_vel.z = Double.parseDouble(incomingdata.next()); 


		movement_acc.x = Double.parseDouble(incomingdata.next()); 
		movement_acc.y = Double.parseDouble(incomingdata.next()); 		
		movement_acc.z = Double.parseDouble(incomingdata.next()); 
		
		movement_friction.x= Double.parseDouble(incomingdata.next()); 
		movement_friction.y= Double.parseDouble(incomingdata.next()); 
		movement_friction.z= Double.parseDouble(incomingdata.next()); 
		

		//used to be int based, now double
		//movement_current_pos.x = Integer.parseInt(incomingdata.next()); 
		//movement_current_pos.y = Integer.parseInt(incomingdata.next()); 
		//movement_current_pos.z = Integer.parseInt(incomingdata.next()); 
		
		movement_current_pos.x = Double.parseDouble(incomingdata.next()); 
		movement_current_pos.y = Double.parseDouble(incomingdata.next()); 
		movement_current_pos.z = Double.parseDouble(incomingdata.next()); 

		
		movement_speed   =   Double.parseDouble(incomingdata.next()); 
				//old;
				//(Integer.parseInt(incomingdata.next()));//SAVE_MULTIPLIER;  //we x10000.0 on saving and divide by it on loading. This lets us store a small decimal value in a int (ie, values like 0.05 dont get rounded to 0)



		bounceEnergyRetention= Double.parseDouble(incomingdata.next()); 
		
		//post physics data
		parentBeforeDropping =  incomingdata.next();

		hasLinkedZindexBeforeDropping = Boolean.parseBoolean(incomingdata.next());
		
	}
	//gets all the fields for our serialiser
	//public String getAllFields(){
	//			
	//	return;
	//}

	public String getCurrentPathName() {
		return currentmovementpathname;
	}
	public int getMovement_currentWaypoint() {
		return movement_currentWaypoint;
	}
	
	public SimpleVector3 get_SC() {
		return movement_SC;
	}
	
	//public int get_SY() {
	//	return movement_SY;
	//}
	public SimpleVector3 get_CC() {
		return movement_CC;
	}
	//public int get_CY() {
	//	return movement_CY;
	//}
	//public int get_DX() {
	//	return movement_dest.x;
	//}
	//public int get_DY() {
	//	return movement_dest.y;
	//}
	//	public double get_VelX() {
	//		return movement_VelX;
	//	}
	//	public double get_VelY() {
	//		return movement_VelY;
	//	}

	//TODO: Concern not casting here, as int isnt used much where these are requested
	public int get_currentX_as_int() {
		return (int) movement_current_pos.x;
	}
	public int get_currentY_as_int() {
		return (int) movement_current_pos.y;
	}
	public int get_currentZ_as_int() {
		return (int) movement_current_pos.z;
	}
	//------------------
	
	public Simple3DPoint get_current_destination() {
		return movement_dest;
	}
	public SimpleVector3 get_current_position() {
		return movement_current_pos;
	}
	public SimpleVector3 get_current_velocity() {
		return movement_vel;
	}

	public SimpleVector3 get_current_acceleration() {
		return movement_acc;
	}



	public double get_speed() {
		return movement_speed;
	}
	public boolean is_onCurve() {
		if (currentmovementtype==MovementStateType.OnCurvePath){
			return true;
		} else {
			return false;
		}
	}

	public MovementStateType getCurrentMode() {
		return currentmovementtype;
	}

	public double get_curveTime() {
		return movement_curveTime;
	}
	public double get_curveTimeStep() {
		return movement_curveTimeStep;
	}

	public boolean isMoving() {
		if (currentmovementtype!=MovementStateType.None){
			return true;
		} else {
			return false;
		}
	}
	
	
	public boolean isMovingUnderPhysics() {
		if (currentmovementtype==MovementStateType.PhysicsBased){
			return true;
		} else {
			return false;
		}
	}


	@Override
	public String toString() {
		return "movementState ["
				+ ", currentMovementType=" + currentmovementtype
				+ ", currentmovementpath="+currentmovementpathname 
				+ ", movement_currentWaypoint=" + movement_currentWaypoint 
				+ ", movement_current_pos=" + movement_current_pos.toString()
				+ ", movement_SX=" + movement_SC.toString() 
				+ ", movement_CX=" + movement_CC.toString()
				+ ", movement_dest" + movement_dest.toString()
				+ ", movement_vel=" + movement_vel.toString()
				+ ", movement_acc=" + movement_acc.toString()
				+ ", movement_friction="+movement_friction
				+ ", movement_speed=" + movement_speed 				
				+ ", movement_curveTime=" + movement_curveTime
				+ ", movement_curveTimeStep=" + movement_curveTimeStep + "]";
	}
	
	/**
	 * resets all movement data to its default values
	 * This does not, however, clear any set "parent before dropping" or "hasLinkedZindexBeforeDropping" settings
	 */
	public void clearMovements() {

		currentmovementtype = MovementStateType.None;

		movement_current_pos.set(-1,-1,-1); 		
		movement_vel.set(0,0,0);
		movement_acc.set(0,0,GravityMS);
		
		movement_friction.set(0,0,0);
		movement_dest.set(-1,-1,-1);

		currentmovementpathname = "";
		
		movement_currentWaypoint = -1; // current number of waypoint

		//movement_SX = -1; // start x/y used for curves
		//movement_SY = -1; //
		movement_SC.x =-1;
		movement_SC.y =-1;
		movement_SC.z =-1;
		
		
		movement_CC.x = -1; // control x/y
		movement_CC.y  = -1;
		movement_CC.z = -1;
		
		//movement_dest.x = -1; 

		//movement_dest.y = -1;
		//movement_dest.z = -1;

		movement_speed = -1; // pixels per cycle
		movement_curveTime     = 0;
		movement_curveTimeStep = 0;

		bounceEnergyRetention = defaultBounceEnergyRetention;

	}

	public void setVelocity(double x, double y, double z) {
		this.movement_vel.x = x;
		this.movement_vel.y = y;
		this.movement_vel.z = z;


	}

	public void setAcceleration(double x, double y, double z) {
		this.movement_acc.x = x;
		this.movement_acc.y = y;
		this.movement_acc.z = z;


	}
	public void updateVelocity(double deltatime) {
		updateVelocity(deltatime,false,false,false);
	}
	
	
	/**
	 * updates the currently velocity from the acceleration
	 * newv = oldv + (acc * deltatime)
	 * 
	 * really we could have more accurate friction;
	 * https://gamedevelopment.tutsplus.com/tutorials/how-to-create-a-custom-2d-physics-engine-friction-scene-and-jump-table--gamedev-7756
	 * 
	 * @param deltatime - in ms
	 */
	public void updateVelocity(double deltatime,boolean fixedX,boolean fixedY,boolean fixedZ) {

		

		if (!fixedX){
			movement_vel.x = movement_vel.x +(movement_acc.x*deltatime);
		}
		
		if (!fixedY){
			movement_vel.y = movement_vel.y +(movement_acc.y*deltatime);
		}
		
		if (!fixedZ){
			movement_vel.z = movement_vel.z +(movement_acc.z*deltatime);
		}
		
		
		//also reduce by friction if there is any
		if (movement_friction.x>0 ||
		    movement_friction.y>0 ||
			movement_friction.z>0){
			
			SimpleVector3 reduceBy = movement_friction.copy().mul(deltatime);
			//double reduceBy = movement_friction*deltatime;
			//reduce if positive, add if negative (do nothing if zero)
			
			//x reduce;
			if (movement_vel.x>0){
				movement_vel.x=movement_vel.x-reduceBy.x;				
				//dont go past zero in either direction				
				if (movement_vel.x<0){
					movement_vel.x=0;
				}				
			} else if (movement_vel.x<0){
				movement_vel.x=movement_vel.x+reduceBy.x;
				if (movement_vel.x>0){
					movement_vel.x=0;
				}				
			}
			
			//y reduce;
			if (movement_vel.y>0){
				movement_vel.y=movement_vel.y-reduceBy.y;				
				//dont go past zero in either direction				
				if (movement_vel.y<0){
					movement_vel.y=0;
				}				
			} else if (movement_vel.y<0){
				movement_vel.y=movement_vel.y+reduceBy.y;
				if (movement_vel.y>0){
					movement_vel.y=0;
				}				
			}
			

			//z reduce;
			if (movement_vel.z>0){
				movement_vel.z=movement_vel.z-reduceBy.z;				
				//dont go past zero in either direction				
				if (movement_vel.z<0){
					movement_vel.z=0;
				}				
			} else if (movement_vel.z<0){
				movement_vel.z=movement_vel.z+reduceBy.z;
				if (movement_vel.z>0){
					movement_vel.z=0;
				}				
			}			
			
		}
	}
	
	
	

	public void updatePosition(double deltatime) {
		updatePosition(deltatime,false,false,false);
	}
	
	
	/**
	 * updates position based on current acceleration and velocity
	 * @param deltatime
	 */
	public void updatePosition(double deltatime,boolean fixedX,boolean fixedY,boolean fixedZ) {
		
		//Note; really large deltas likely will produce odd results, especially in bounces.
		//Not sure yet how to deal with this.
		
		movement_current_pos.x = (movement_current_pos.x +(movement_vel.x*deltatime));	
		movement_current_pos.y = (movement_current_pos.y +(movement_vel.y*deltatime));
		movement_current_pos.z = (movement_current_pos.z +(movement_vel.z*deltatime));
		
		//also add half the acceleration to compensate for acceleration that happens mid-frame

		if (!fixedX){
			movement_current_pos.x = (movement_current_pos.x+ ((movement_acc.x * deltatime) * 0.5));
		}
		
		if (!fixedY){
			movement_current_pos.y = (movement_current_pos.y+ ((movement_acc.y * deltatime) * 0.5));
		}
		
		if (!fixedZ){
			movement_current_pos.z = (movement_current_pos.z+ ((movement_acc.z * deltatime) * 0.5));
		}
		
	}
	
	
	
	/**
	 * @return the bounceEnergyRetention
	 */
	public double getBounceEnergyRetention() {
		return bounceEnergyRetention;
	}
	/**
	 * @param bounceEnergyRetention the bounceEnergyRetention to set
	 */
	public void setBounceEnergyRetention(double bounceEnergyRetention) {
		this.bounceEnergyRetention = bounceEnergyRetention;
	}
	public void setPosition(int x, int y, int z) {

		movement_current_pos.x = x;
		movement_current_pos.y = y;
		movement_current_pos.z = z;

	}
	public void addVelocity(SimpleVector3 impulseMag) {
		movement_vel.add(impulseMag);
	}


	public void setTo(MovementState moveState) {
		
		this.currentmovementpathname = moveState.currentmovementpathname;
		this.currentmovementtype = moveState.currentmovementtype;

		//this.isMoving=isMoving;
		this.movement_currentWaypoint = moveState.movement_currentWaypoint;

		//this.movement_SX = moveState.movement_SX;
		//this.movement_SY = moveState.movement_SY;
		this.movement_SC.set(moveState.movement_SC);

	//	this.movement_CX = moveState.movement_CX;
	//	this.movement_CY = moveState.movement_CY;
		this.movement_CC.set(moveState.movement_CC);

		this.movement_dest.set(moveState.movement_dest);		
		this.movement_vel.set(moveState.movement_vel);
		this.movement_acc.set(moveState.movement_acc);
		this.movement_friction.set(moveState.movement_friction);
		

		this.movement_current_pos.set(moveState.movement_current_pos);

		this.movement_speed = moveState.movement_speed;

		//	this.movement_onCurve =movement_onCurve;
		this.movement_curveTime     =moveState.movement_curveTime;
		this.movement_curveTimeStep =moveState.movement_curveTimeStep;
		this.bounceEnergyRetention  = moveState.bounceEnergyRetention;
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		
		MovementState other = (MovementState) obj;
		
		//Log.info("checking movementstates match..");
		
		
		if (Double.doubleToLongBits(bounceEnergyRetention) != Double.doubleToLongBits(other.bounceEnergyRetention))
		{
			return false;
		}
		
		
		if (currentmovementpathname == null) {
			if (other.currentmovementpathname != null){
				return false;
			}
		} else if (!currentmovementpathname.equals(other.currentmovementpathname)){
			return false;
		}
	//	Log.info("checking movementstates match....");
		
		
		if (currentmovementtype != other.currentmovementtype){
			return false;
		}
		if (movement_CC == null) {
			if (other.movement_CC != null){
				return false;
			}
		} else if (!movement_CC.equals(other.movement_CC)){
			return false;
		}
		if (movement_SC == null) {
			if (other.movement_SC != null){
				return false;
			}
		} else if (!movement_SC.equals(other.movement_SC)){
			return false;
		}
		
	//	Log.info("checking movementstates match.......");
		
		
		if (movement_acc == null) {
			if (other.movement_acc != null){
				return false;
			}
		} else if (!movement_acc.equals(other.movement_acc)){
			return false;
		}
		//Log.info("checking movementstates match.........");
		
		if (!movement_friction.equals(other.movement_friction)){
			//Log.info("friction does not match");			
			return false;			
		}
	//	Log.info("checking movementstates match.......a....");
		
		if (movement_currentWaypoint != other.movement_currentWaypoint){
			return false;
		}
		//Log.info("checking movementstates match.....b.......");
		
		if (movement_current_pos == null) {
			if (other.movement_current_pos != null){
				return false;
			}
		} else if (!movement_current_pos.equals(other.movement_current_pos)){
			return false;
		}
	//	Log.info("checking movementstates match...............");
		
		if (Double.doubleToLongBits(movement_curveTime) != Double.doubleToLongBits(other.movement_curveTime)){
			return false;
		}
		if (Double.doubleToLongBits(movement_curveTimeStep) != Double.doubleToLongBits(other.movement_curveTimeStep)){
			return false;
		}
		
		if (movement_dest == null) {
			if (other.movement_dest != null){
				return false;
			}
		} else if (!movement_dest.equals(other.movement_dest)){
			return false;
		}
		
		if (Double.doubleToLongBits(movement_speed) != Double.doubleToLongBits(other.movement_speed)){
			return false;
		}
		
		//Log.info("checking movementstates match...........................");
		
		if (movement_vel == null) {
			if (other.movement_vel != null){
				return false;
			}
		} else if (!movement_vel.equals(other.movement_vel)){
			return false;
		}
		
	//	Log.info("checking movementstates match..........................................");
		
		return true;
	}
	
	/**
	 * If the absolute velocity in x,y and z are all less then the value specified
	 * (sign/direction ignored)
	 * 
	 * @param d (positive value only)
	 * @return
	 */
	public boolean velocityLessThen(double d) {
		
		if (   Math.abs(movement_vel.x)<d 
			&& Math.abs(movement_vel.y)<d 
			&& Math.abs(movement_vel.z)<d){
			return true;
		}
		
		return false;
	}
		
	
	
	
	

}