package com.lostagain.Jam.Movements;

import java.util.logging.Logger;

import com.google.common.base.CharMatcher;
import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/** a single movement from the current point to a new point **/
public class MovementWaypoint {

	




	static Logger Log = Logger.getLogger("JAMCore.MovementWaypoint");
	
	//note: not all of these will define x/y points, some are just for commands to be set
	/**
	 * The type of movement (absolute by default, to be relative you need to explictly set that)
	 * @author darkflame
	 *
	 */
	public enum MovementType {
		
		AbsoluteMove("M"), 		
		AbsoluteLineTo("L"),
		AbsoluteLoopToPathStart("Z"), 
		/** a script command that runs when we get to this waypoint **/
		Command, 
		/** a internal to the jam engine runnable that fires when we get to this waypoint **/
		InternalRunnable,
		AbsoluteQCurveToo("Q"),
		AbsoluteCCurveToo("C"),//not supported yet by movement yet
		//now the relative versions
		RelativeMove("m",true),
		RelativeLineTo("l",true),
		RelativeQCurveToo("q",true),
		RelativeCCurveToo("c",true);
		
		/** Determines if these co-ordinates should be treated relative to the last or not**/
		private boolean relativeMode=false;

		String svg_letter="";
		
		MovementType(){
			svg_letter="";
		}
		
		MovementType(String letter){
			svg_letter=letter;
			relativeMode=false;
		}
		MovementType(String letter,boolean relativeMode){
			svg_letter=letter;
			this.relativeMode = relativeMode;
		}
		
		static public MovementType getFromString(String letter) {
			
		//	Log.info("parseing svg string letter:"+letter);			
			//set relative mode if lower case
			
			MovementType newmovementtype;
			
			//set the correct type based on the character supplied
			switch (letter) {
			case "M":
				newmovementtype= MovementType.AbsoluteMove;
				break;
			case "L":
				newmovementtype= MovementType.AbsoluteLineTo;
				break;
			case "Z":
				newmovementtype= MovementType.AbsoluteLoopToPathStart;
				break;
			case "Q":
				newmovementtype= MovementType.AbsoluteQCurveToo;
				break;
			case "C":
				newmovementtype= MovementType.AbsoluteCCurveToo;
				break;
			case "m":
				newmovementtype= MovementType.RelativeMove;			
				break;
			case "l":
				newmovementtype= MovementType.RelativeLineTo;
				break;
			case "z":
				newmovementtype= MovementType.AbsoluteLoopToPathStart; //there is no relative loop to start, it means the same thing
				break;
			case "q":
				newmovementtype= MovementType.RelativeQCurveToo;
				break;
			case "c":
				newmovementtype= MovementType.RelativeCCurveToo;
				break;
			default:
				Log.severe("not recgonised svg letter:"+letter);
				newmovementtype= MovementType.AbsoluteMove;
				break;
			}
			
			/*
			if (letter.equalsIgnoreCase("m")) {
				newmovementtype= MovementType.AbsoluteMove;
			} else if (letter.equalsIgnoreCase("l")) {
				newmovementtype= MovementType.AbsoluteLineTo;
			} else if (letter.equalsIgnoreCase("z")) {
				newmovementtype= MovementType.AbsoluteLoopToStart;
			} else if (letter.equalsIgnoreCase("q")) {
				newmovementtype= MovementType.AbsoluteQCurveToo;
			} else if (letter.equalsIgnoreCase("c")) {
				newmovementtype= MovementType.AbsoluteCCurveToo;
			} else {
				Log.severe("not recgonised svg letter:"+letter);
				newmovementtype= MovementType.AbsoluteMove;
			}
			
			newmovementtype.setAsRelative(CharMatcher.JAVA_LOWER_CASE.matchesAllOf(letter));
			
			//ensure canonical letter is the right case 
			if (newmovementtype.isRelative()){
				newmovementtype.svg_letter = newmovementtype.svg_letter.toLowerCase();
			} else {
				newmovementtype.svg_letter = newmovementtype.svg_letter.toUpperCase();
			}*/

			//Log.info("parsed svg string letter "+letter+" as "+	newmovementtype.name() );
			
			return newmovementtype;
		}

		/**
		 * @return the relativeMode
		 */
		boolean isRelative() {
			return relativeMode;
		}



		
		
		
	}
	

	//public int x = 0;	
	//public int y = 0;
	/**
	 * to support 3d paths we have a z co-ordinate.
	 * If this is used, however, the SVG paths will be invalid
	 */
	//public int z = 0;
	/**
	 * the primary point of this waypoint. If it represents a line or curve this is the end of it.
	 * if its a moveto, its the position we will move to
	 */
	public Simple3DPoint pos = new Simple3DPoint(0,0,0);
		
	

	/**
	 * If a C curve, this is the first control point
	 * If its a Q curve this is the only control point
	 */
	public Simple3DPoint midPoint1 = new Simple3DPoint(0,0,0);  //for Q and C curved movement
	
	/**
	 * note; this is for the second controll point of C curved based movement
	 * but isnt yet fully implemented
	 */
	public Simple3DPoint midPoint2 = new Simple3DPoint(0,0,0);  //for C curved movement (NOT implemented in movement yet)
	
	public MovementType type;	
	
	/**
	 * Only used if type is "loop"
	 * This int tells us what to loop too.
	 */
	int waypointNumberForLastSegmentStart = -1;

	int waypointNumberForNextSegmentEnd = -1;
	
	/**
	 * -1 if not set. This is only set on waypoints representing loops (z)
	 * @return
	 */
	public int getWaypointNumberForLastSegmentStart() {
		return waypointNumberForLastSegmentStart;
	}

	public void setWaypointNumberForLastSegmentStart(int waypointNumberForLastSegmentStart) {
		this.waypointNumberForLastSegmentStart = waypointNumberForLastSegmentStart;
	}
	/**
	 * -1 if not set. This is only set on waypoints representing loops (z)
	 * @return
	 */
	public int getWaypointNumberForNextSegmentEnd() {
		return waypointNumberForNextSegmentEnd;
	}

	public void setWaypointNumberForNextSegmentEnd(int waypointNumberForNextSegmentEnd) {
		this.waypointNumberForNextSegmentEnd = waypointNumberForNextSegmentEnd;
	}

	/**
	 * A mid-movement script command that will be triggered when its waypoint is reached
	 */
	public CommandList Command;

	/**
	 * A mid-movement runnable triggered when this waypoint is reached
	 */
	public  Runnable InternalRunnable;
	
	
	


	

	
	public MovementWaypoint(MovementType currentType, Simple3DPoint targetpos) {
		
		pos = targetpos.copy();
		this.type = currentType;
		
	}
	
	public MovementWaypoint(MovementType currentType, Simple3DPoint cp1, Simple3DPoint targetpos) {
		
		pos = targetpos.copy();
		midPoint1 = cp1.copy();
		this.type = currentType;
		
	}

	public MovementWaypoint(MovementType currentType,Simple3DPoint cp1, Simple3DPoint cp2, Simple3DPoint targetpos) {
		
		midPoint1 = cp1.copy();
		midPoint2 = cp2.copy();
		pos = targetpos.copy();
		
		this.type = currentType;
				
	}
	public MovementWaypoint(Simple3DPoint cp1, Simple3DPoint cp2, Simple3DPoint targetpos ,boolean relative) {
		
		midPoint1 = cp1.copy();
		midPoint2 = cp2.copy();//only ccurves have two points
		pos = targetpos.copy();
		
		if (relative){
			type = MovementType.RelativeCCurveToo; 				
		} else {
			type = MovementType.AbsoluteCCurveToo; 
		}
		
	}
	
	public MovementWaypoint(int x, int y,int mx,int my) {
		
		pos.x = x;
		pos.y = y;
		
		midPoint1.x = mx;
		midPoint1.y = my;
		
		this.type = MovementType.AbsoluteQCurveToo;

	}
	public MovementWaypoint(int x, int y,int mx,int my,boolean relative) {
		
		pos.x = x;
		pos.y = y;
		
		midPoint1.x = mx;
		midPoint1.y = my;
		if (relative){
			this.type = MovementType.RelativeQCurveToo;
		} else {
			this.type = MovementType.AbsoluteQCurveToo;
			
		}
	}
	

	public boolean isRelative(){
		return type.isRelative();
	}
	

	//public MovementWaypoint(int x, int y,int z, MovementType type) {
	//	this(x,y,z,type);
	//}
	//public MovementWaypoint(int x, int y, MovementType type) {
	//	this(x,y,type);
	//	
	//}

	public MovementWaypoint(int x, int y, MovementType type) {
		this(x,y,0,type); //assume zero
	}
	
	public MovementWaypoint(int x, int y,int z, MovementType type) {
		
		pos.x = x;
		pos.y = y;
		pos.z = z;
		
		this.type = type;

	}
	
	public MovementWaypoint(int x, int y,int z, String type) {
		
		pos.x = x;
		pos.y = y;
		pos.z = z;
		
		this.type = MovementType.getFromString(type);
		
	}
	
	
	public MovementWaypoint(Runnable command) {
		
		//Log.info(" adding command: "+Command);
	
		pos.x = 0;
		pos.y = 0;
		
		this.InternalRunnable= command;		
		this.type = MovementType.InternalRunnable;

	}

	public MovementWaypoint(CommandList command) {
		
		//Log.info(" adding command: "+Command);
	
		pos.x = 0;
		pos.y = 0;
		
		this.Command= command;		
		this.type = MovementType.Command;

	}
	public MovementWaypoint(String Command) {
		
		//Log.info(" adding command: "+Command);
	
		pos.x = 0;
		pos.y = 0;
		
		//this.Command=Command;
		this.Command= InstructionProcessor.StringToCommandList(Command);

		
		this.type = MovementType.Command;

	}

	@Override
	public String toString() {
		return type.svg_letter+" to "+this.pos.toString();
	}


	public boolean equalsLocation(Simple2DPoint nsp) {
		
		if (nsp.x==pos.x){
			if (nsp.y==pos.y){
										
					return true;
					
				
			}
		}
		
		
		return false;
	}

	/**
	 * if its either a relative or absolute move too
	 * @return
	 */
	public boolean isMoveTo() {
		if (this.type == MovementType.AbsoluteMove){
			return true;
		}
		if (this.type == MovementType.RelativeMove){
			return true;
		}
		return false;
	}
	
	/**
	 * A waypoint is "real" if it contains real co-ordinates to travel to in someway.
	 * Examples of real waypoints are L and M
	 * Examples of "fake" waypoints are Command,and InternalRunnable.
	 * 
	 * (z signifys looping to last M, it does not contain co-ordinates, but it is still considered real)
	 * 
	 * @return
	 */
	public boolean isRealWayPoint(boolean zCountsAsUnreal) {
		if (zCountsAsUnreal && this.type == MovementType.AbsoluteLoopToPathStart){
			return false;
		}
		if (this.type == MovementType.InternalRunnable){
			return false;
		}	
		if (this.type == MovementType.Command){
			return false;
		}		
		return true;
	}
}
