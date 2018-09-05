/**
 * 
 */
package com.lostagain.Jam.Movements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/**
 * WIP REPLACEMENT SYSTEM FOR PATHS
 * 
 * Represents a path segment.
 * SVG wise Path segments always start with M (move to)
 * They then contain any number of lines or curves, then they might end with a Z (or not) to represent "close polygon"
 * 
 * PathSegment may also contain Commands. These are stored as waypoints, but can be skipped over when iterating if needed.
 * 
 * 
 * TODO: iteration and distance calculation
 * 
 * @author darkflame
 *
 */
public class SpiffyPathSegment extends ArrayList<MovementWaypoint> {

	static Logger Log = Logger.getLogger("JAMCore.SpiffyPathSegment");
	
	private static final long serialVersionUID = -8737397384961838837L;
	
	boolean closed = false;

	public SpiffyPathSegment(String svgPathSegment) {		
		this(parseToArrayList(svgPathSegment));
	}

	
	public SpiffyPathSegment(List<? extends MovementWaypoint> waypoints) {
		super(waypoints);		

		//check it starts with M and optionally ends with z. No z's or m's can be in the middle.
		if (!isvalid(waypoints)){
			Log.severe("_Illegal Path Segment:"+waypoints+"_");			
			
		}
		
		//if it ends in z, remove it ans set close
		MovementWaypoint lastWaypoint =  super.get(waypoints.size()-1);
		if (lastWaypoint.type==MovementType.AbsoluteLoopToPathStart){
			closed=true;
			super.remove(waypoints.size()-1);
		}
		
		Log.info("Made path segment:"+this.toString());
		
		//calc distance
		
		
	}
	
	
	private boolean isvalid(List<? extends MovementWaypoint> waypoints) {
		//ensure starts with M
		if(!waypoints.get(0).isMoveTo()){
			return false;
		}
		

		MovementWaypoint lastWaypoint  =  waypoints.get(waypoints.size()-1);
		MovementWaypoint firstWaypoint =  waypoints.get(0);
		
		//-------------ensure no zs or m's are in the middle
		for (Iterator<? extends MovementWaypoint> iterator = waypoints.iterator(); iterator.hasNext();) {
			
			MovementWaypoint movementWaypoint = (MovementWaypoint) iterator.next();
			
			if ( movementWaypoint!= lastWaypoint   &&   movementWaypoint.type==MovementType.AbsoluteLoopToPathStart){
				return false;
			}
			
			if ( movementWaypoint!= firstWaypoint   &&   movementWaypoint.isMoveTo()){
				return false;
			}
		}
		//------------------------------------------
		
		
		return true;
	}


	/**
	 * Parses a SVG-like string describing a path into a ArrayList of waypoints.<br>
	 * This should be compatible with SVG, but supports a optional third co-ordinate for each waypoint.<br>
	 * <br>
	 * Examples of valid paths; <br>
	 * 
	 * M 915,600 L 989,686 L 905,755 L 834,660 z
	 * 
	 * 
	 * NOTE: This should only be for parsing path segments (ie, Starts with M and might end with Z).
	 * Pathsegments ending in z will be automatically marked as closed. Nothing will be registered after the z.
	 * 
	 * @param svgPathSegment
	 * @return
	 */
	//TODO: This parseing can be simplied in future - since it should not need to deal with z's or m's in the middle
	//nor should it have to handle given them information about where their matching endpoint is.
	private static ArrayList<MovementWaypoint> parseToArrayList(String svgPathSegment) {
		
		 ArrayList<MovementWaypoint> newList = new ArrayList<MovementWaypoint>();
		 
		 String[] waypointList = svgPathSegment.split(" ");
		 
			int len = waypointList.length;
			int i = 0;
			//int cxi = 0, cyi = 0, czi=0;
			//int lcxi = 0, lcyi = 0, lczi=0;
			//int cmx = -1, cmy = -1;

			int waypointNumberForLastSegmentStart=0;
			boolean commandStoreMode = false;
			
			MovementType currentType = MovementType.RelativeLineTo;//  "l"; default
			String commandStore = "";

			// loop over the array
			while (i < len) {

				String currentPoint = waypointList[i].trim();
				i++;

				// Log.info(" currentPoint- "+currentPoint);

				// if empty skip
				if (currentPoint.length() == 0) {
					continue;
				}

				// if its a single character, assume its a specification for
				// changing the type
				if ((currentPoint.length() == 1) && (!commandStoreMode)) {
					currentType = MovementType.getFromString(currentPoint);
					
					//if its a z then we add it straight away (to ensure its not overwritten by whatever letter comes next)
					if (currentType==MovementType.AbsoluteLoopToPathStart){
						MovementWaypoint loopnode = new MovementWaypoint(0, 0, MovementType.AbsoluteLoopToPathStart);
						
						loopnode.setWaypointNumberForLastSegmentStart(waypointNumberForLastSegmentStart);
						
						newList.add(loopnode);		
						
						//tell the segment start the number of this end of it too
						newList.get(waypointNumberForLastSegmentStart).setWaypointNumberForNextSegmentEnd(newList.size()-1);
						
						
					
					}
					
					continue;
				}

				if (currentPoint.startsWith("[") && (!commandStoreMode)) {

					// Log.info(" adding command mode on- ");

					// start command store
					commandStore = currentPoint.substring(1);
					commandStoreMode = true;

					// check if it ends with it straight away
					continue;
				}

				if (currentPoint.endsWith("]") && (commandStoreMode)) {

					// Log.info(" adding command mode off- ");

					// start command store
					commandStore = commandStore + " "
							+ currentPoint.substring(0, currentPoint.length() - 1);
					commandStoreMode = false;

					// Log.info(" adding command: " + commandStore);

					newList.add(new MovementWaypoint(commandStore));

					// check if it ends with it straight away
					continue;
				}

				if (commandStoreMode) {

					// Log.info(" adding command mode continue ");

					commandStore = commandStore + " " + currentPoint;
					continue;
				}

				//String cx;
				//	String cy;
				//	String cz; //(cz support results in not real SVG strings, but still something we can use for glupoints and later movement paths)

				//	String mx;
				//	String my; //no z support yet here (for curves)

				if (!commandStoreMode) {

					//String cparray[] = currentPoint.split(","); //split co-ordinates by comma

					//should do this differently
					//depending on mode, we store different numbers of co-ordinates
					//lines and movetos only need one co-ordinate set (their destination)
					//QCurves need two   (1 control point + destination)
					//CCurves need three (2 control points + destination)


					switch (currentType) {
					case AbsoluteMove:
					case RelativeMove:
						waypointNumberForLastSegmentStart = newList.size();
						//note no break, we want to run the same code as LineTo as well
					case AbsoluteLineTo:			
					case RelativeLineTo:
					{
						Simple3DPoint dest = new Simple3DPoint(currentPoint);
						newList.add(  new MovementWaypoint(currentType, dest) );
						break;
					}
					case RelativeQCurveToo:
					case AbsoluteQCurveToo:
					{
						Simple3DPoint controllPoint1 = new Simple3DPoint(currentPoint);
						Simple3DPoint dest = new Simple3DPoint(waypointList[i]);
						i++;
						newList.add(  new MovementWaypoint(currentType, controllPoint1,dest) );
						break;
					}
					case RelativeCCurveToo:
					case AbsoluteCCurveToo:
					{
						Simple3DPoint controllPoint1 = new Simple3DPoint(currentPoint);
						Simple3DPoint controllPoint2 = new Simple3DPoint(waypointList[i]);
						i++;
						Simple3DPoint dest = new Simple3DPoint(waypointList[i]);
						i++;
						newList.add(  new MovementWaypoint(currentType, controllPoint1,controllPoint2, dest) );
					}
					break;
					}


					//after we have finnished adding the segment we set the nexttype to "line" if we are a moveto
					//This should be overriden if the next type is specified, it merely makes line a default if (and only if) we did a moveto
					//This is different from SVG spec which would always default to the last thing, even if it was move to.
					if (currentType==MovementType.AbsoluteMove || currentType==MovementType.RelativeMove){
						boolean wasrelative = currentType.isRelative();
						//we stay relative if relative, or absolute if absolute 
						if (wasrelative){
							currentType = MovementType.RelativeLineTo; 
						} else {
							currentType = MovementType.AbsoluteLineTo; 
						}

					}


				}
		 
			}
		
		return newList;
	}


	/** 
	 * Gets this path as a SVG string
	 ***/
	public String getAsSVGPath() {
		return getAsSVGPath(false);
	}

	
	/**
	 * Gets this path as a SVG string
	 * @param insertzvalues - insert z co-ordinate values
	 * @return
	 */
	public String getAsSVGPath(boolean insertzvalues) {

		String path = "";

		Iterator<MovementWaypoint> it = this.iterator();
		while (it.hasNext()) {

			MovementWaypoint movementWaypoint = (MovementWaypoint) it.next();
			
			String nextCommandString = "";
			
			MovementType movementtype = movementWaypoint.type;
			
			switch (movementtype) {
			case AbsoluteMove:
			case AbsoluteLineTo:
			case RelativeLineTo:
			case RelativeMove:
				//relative movements and lines just need the type set
				nextCommandString = " "+movementtype.svg_letter;
				break;
			case RelativeQCurveToo:
			case AbsoluteQCurveToo:
				//curve types need the type set, and a extra co-ordinate
				//path = path + " C"+ " " + movementWaypoint.midpoint_x + "," + movementWaypoint.midpoint_y;
				nextCommandString = " "+movementtype.svg_letter+ " "  + movementWaypoint.midPoint1.x + "," + movementWaypoint.midPoint1.y +" "; //z?
				break;
			case RelativeCCurveToo:
			case AbsoluteCCurveToo:
				//c curves are similar but need two extra co-ordinates
				//path = path + " C"+ " " + movementWaypoint.midpoint_x + "," + movementWaypoint.midpoint_y;
				nextCommandString = " "+movementtype.svg_letter+ " " 
						+ movementWaypoint.midPoint1.x + "," + movementWaypoint.midPoint1.y +" "
						+ movementWaypoint.midPoint2.x + "," + movementWaypoint.midPoint2.y +" "; //z?
				break;
			//other types we do nothing 	
			case AbsoluteLoopToPathStart:
				break;
			case Command:
				break;
			case InternalRunnable:
				break;
			default:
				break;
			}

			path = path + nextCommandString;
			
			//insert the standard co-ordinates
			if (insertzvalues){
				path = path + " " + movementWaypoint.pos.x + "," + movementWaypoint.pos.y+","+movementWaypoint.pos.z;
			} else {
				path = path + " " + movementWaypoint.pos.x + "," + movementWaypoint.pos.y;
			}

		

		}
		path = path + " ";
		
		if (closed){
			path = path + "z"; 
		}
		
		return path.trim();

	}


	public boolean isClosed() {
		return closed;
	}

	public int PathLength = 0; // length of path in pixels

	/**
	 * Calculates the current length as the .PathLength variable might not be up to date.
	 * returns the new pathlength as well as storing that new value in PathLength'
	 * 
	 * NOTE; THIS IMPLEMENTATION IS WRONG. See movementpaths one for correct.
	 * Each segment should work out its distance, then we just add them up in SpiffyPath. 
	 * (we dont want distances between segments to be included)
	 ***/
	public int getCurrentLength(){

		int length2=0;

		int startY=this.get(0).pos.y;
		int startX=this.get(0).pos.x;
		int startZ=this.get(0).pos.z;

		int lastY=0;
		int lastX=0;
		int lastZ=0;
		
		//add up all X's and Ys
		Iterator<MovementWaypoint> tit = this.iterator();

		int dx,dy,dz= 0;
		
		while (tit.hasNext()) {

			MovementWaypoint vertex = (MovementWaypoint) tit.next();

			//distance from last pos
			startY=lastY;
			startX=lastX;
			startZ=lastZ;
			
			if (vertex.isRelative()) {
				
				//if relative the distance is just the current points magnitudes
				dx = Math.abs(vertex.pos.x);
				dy = Math.abs(vertex.pos.y);
				dz = Math.abs(vertex.pos.z);
				
			}  else {

				//else its the distance between this point and the last
				dx=Math.abs(vertex.pos.x-startX);
				dy=Math.abs(vertex.pos.y-startY);
				dz=Math.abs(vertex.pos.z-startZ);
			}
			
			int dis = (int) Math.sqrt((dx*dx)  +  (dy*dy) + (dz*dz));
			length2=length2+dis;
		//	length2=length2+(int)(Math.hypot(dx, dy));

			lastY=vertex.pos.y;
			lastX=vertex.pos.x;
			lastZ=vertex.pos.z;
			
		}

		//Log.info("length by lots of trig="+length2);

		PathLength = length2;

		return length2;
	}

	
	/**
	 * adds segment and updates internal length
	 * 
	 * @param newwp
	 * @return
	 */
	/*
	public boolean addMovementWayPoint(MovementWaypoint newwp) {

		Log.info("new svg waypoint=" + newwp.toString());

		if (super.size() > 0) {
			
			MovementWaypoint lastwaypoint = super.get(super.size() - 1);
			
			int distancex,distancey,distancez = 0;
			
			if (newwp.isRelative()) {
				
				//if relative the distance is just the current points magnatudes
				distancex = Math.abs(newwp.pos.x);
				distancey = Math.abs(newwp.pos.y);
				distancez = Math.abs(newwp.pos.z);
				
			} else {
				//if we are absolute the distance is the difference between this point and the last 
				
				distancex = Math.abs(lastwaypoint.pos.x - newwp.pos.x);
				distancey = Math.abs(lastwaypoint.pos.y - newwp.pos.y);
				distancez = Math.abs(lastwaypoint.pos.z - newwp.pos.z);
					
			}
			
			
		//	int dis = (int) Math.hypot(distancex, distancey);

			//Log.info("distance="+distancex+" , "+distancey+" , "+distancez);

		//	Log.info("distance="+(distancex^2)+" , "+(distancey^2)+" , "+(distancez^2) );
			
			int dis = (int) Math.sqrt( 
					   (distancex*distancex) 
					+  (distancey*distancey)
					+  (distancez*distancez) 
					);
			

			PathLength = PathLength + dis;

			Log.info("new distance=" + dis);
		}

		return super.add(newwp);
	}

*/
			
}
