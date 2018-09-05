package com.lostagain.Jam.Movements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/**
 * a svg like specification of movement It specifies a single path
 * 
 * You specify it using svg path formating. eg M 1,2 97,2 97,96 8,99 z
 * 
 * Its then Stored as an array list of waypoints
 **/
public class MovementPath extends ArrayList<MovementWaypoint> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5062518940638269709L;

	public static Logger Log = Logger.getLogger("JAMCore.MovementPath");

	

	public String pathsName = "";

	public int PathLength = 0; // length of path in pixels
	String commandStore = "";

	boolean commandStoreMode = false;

	public boolean pathFindingBroke = false;

	//public Command postAnimationCommands;
	public Runnable postAnimationCommands;

	public MovementPath(MovementPath clonethispath, String name) {

		//Log.info("cloning path:" + name);

		pathsName = name;
		pathFindingBroke = clonethispath.pathFindingBroke;

		this.addAll(clonethispath);

	}

	boolean secondcurveparam = false;

	public MovementPath(String path, String name) {

		Log.info("adding path:" + path);
		pathsName = name;

		// split by spaces
		String[] waypointList = path.split(" ");
		int len = waypointList.length;
		int i = 0;
		int cxi = 0, cyi = 0, czi=0;
		int lcxi = 0, lcyi = 0, lczi=0;

		int cmx = -1, cmy = -1;

		int waypointNumberForLastSegmentStart=0;
		
		
		MovementType currentType = MovementType.RelativeLineTo;//  "l"; default
		

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
					
					super.add(loopnode);		
					
					//tell the segment start the number of this end of it too
					super.get(waypointNumberForLastSegmentStart).setWaypointNumberForNextSegmentEnd(super.size()-1);
					
					
				
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

				this.add(new MovementWaypoint(commandStore));

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
					waypointNumberForLastSegmentStart = super.size();
					//note no break, we want to run the same code as LineTo as well
				case AbsoluteLineTo:			
				case RelativeLineTo:
				{
					Simple3DPoint dest = new Simple3DPoint(currentPoint);
					addMovementWayPoint(  new MovementWaypoint(currentType, dest) );
					break;
				}
				case RelativeQCurveToo:
				case AbsoluteQCurveToo:
				{
					Simple3DPoint controllPoint1 = new Simple3DPoint(currentPoint);
					Simple3DPoint dest = new Simple3DPoint(waypointList[i]);
					i++;
					addMovementWayPoint(  new MovementWaypoint(currentType, controllPoint1,dest) );
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
					addMovementWayPoint(  new MovementWaypoint(currentType, controllPoint1,controllPoint2, dest) );
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



				//old method below



				/*

				//save the old destination values
				lcxi = cxi;
				lcyi = cyi;
				lczi = czi;

				// get the new x/y and optional z
				cx  = cparray[0];
				cy  = cparray[1];
				czi = 0;

				if (cparray.length==3){ //optional z
					cz  = cparray[2];
					czi = (int) Double.parseDouble(cz);					
				}


				cxi = (int) Double.parseDouble(cx);
				cyi = (int) Double.parseDouble(cy);
			//	czi = (int) Double.parseDouble(cz);

				if (currentType == MovementType.QCurveToo && secondcurveparam == false){
					//control point
					mx = cparray[0];
					my = cparray[1];

					cmx = (int) Double.parseDouble(mx);
					cmy = (int) Double.parseDouble(my);

					secondcurveparam = true;
				} else if (currentType == MovementType.QCurveToo && secondcurveparam == true){
					// destination					
					cxi = (int) Double.parseDouble(cx);
					cyi = (int) Double.parseDouble(cy);

				}
				 */
			}

			/*
			if (!(currentType == MovementType.QCurveToo)) {

				addMovementWayPoint(currentType, cxi, cyi, czi);

			} else if (secondcurveparam) {
				//quadratic curve
				//addMovementWayPoint(MovementType.QCurveToo, cxi, cyi, cmx, cmy);
				MovementWaypoint newpoint = new MovementWaypoint(cxi, cyi, cmx, cmy);
				if (currentType.equals("q")){
					newpoint.setRelative();
				}
				super.add(newpoint);
				secondcurveparam = false;
			}
			 */

			// if the next type hasn't been specified above, it defaults to
			// absolute lineto
			//	currentType = "L";


			//currentType = MovementType.LineTo; //default back to line?

			// distance from previous waypoint if on second or more
			/*
			if (i > 2) {

				int dx = cxi - lcxi;
				int dy = cyi - lcyi;
				int dz = czi - lczi;

				int distance = 0;

				if (dz==0){
					distance = (int) Math.hypot(dx, dy);
				} else {					
					distance = (int) Math.sqrt( 
							Math.pow(dx, 2)  +
							Math.pow(dy, 2)  + 
							Math.pow(dz, 2) 
							);
				}

				PathLength = PathLength + distance;
				// Log.info("_________distance = " + distance);
				// Log.info("_________PathLength = " + PathLength);

			}*/

			// add to path length

			//
		}

		//Loop is now added as soon as its detected
		//Log.info("_________end of path type = " + currentType);
		//	if (currentType.equalsIgnoreCase("z")) {
		//if (currentType == MovementType.AbsoluteLoopToPathStart) {
		//	super.add(new MovementWaypoint(0, 0, MovementType.AbsoluteLoopToPathStart));
		//}

		

		Log.info("added paths");
	}

	public boolean addSome(int from, Collection<? extends MovementWaypoint> col) {

		boolean result = true;
		int c = 0;

		for (MovementWaypoint movementWaypoint : col) {

			if (c >= from) {
				result = this.add(movementWaypoint);
			}
			c++;

		}

		return result;

	}

	@Override
	public boolean addAll(Collection<? extends MovementWaypoint> col) {

		boolean result = true;

		for (MovementWaypoint movementWaypoint : col) {

			result = this.add(movementWaypoint);

		}

		return result;

	}

	@Override
	public boolean add(MovementWaypoint newwp) {

		return this.addMovementWayPoint(newwp);

	}

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

/**
 * remember: run generateOutSetPath() after all points are added
	 * 
 * @param type
 * @param x
 * @param y
 * @param z
 */
	public void addMovementWayPoint(MovementType type, int x, int y, int z) {

		super.add(new MovementWaypoint(x, y, z, type));
	}

	/**
	 * remember: run generateOutSetPath() after all points are added
	 * 
	 * @param type
	 * @param x
	 * @param y
	 */
	public void addMovementWayPoint(MovementType type, int x, int y) {
		super.add(new MovementWaypoint(x, y, type));
	}
/**
 * remember: run generateOutSetPath() after all points are added
	 * 
 * @param type
 * @param x
 * @param y
 * @param mx
 * @param my
 */
	public void addMovementWayPoint(MovementType type, int x, int y, int mx, int my) {
		super.add(new MovementWaypoint(x, y, mx, my));
	}

	public void addMovementWayPoint(String type, int x, int y,int z) {

		MovementWaypoint newwaypoint = new MovementWaypoint(x,y,z,type);

		/*
		if (type.equalsIgnoreCase("m")) {
			 newwaypoint =new MovementWaypoint(x, y,z, MovementType.Move);
		} else if (type.equalsIgnoreCase("l")) {
			 newwaypoint =new MovementWaypoint(x, y,z, MovementType.LineTo);
		} else if (type.equalsIgnoreCase("q")) {
			 newwaypoint =new MovementWaypoint(x, y,z, MovementType.QCurveToo);
		} else if (type.equalsIgnoreCase("z")) {
			 newwaypoint =new MovementWaypoint(x, y,z, MovementType.LoopToStart);
		} else {
			newwaypoint = new MovementWaypoint(x, y,z, MovementType.LineTo);

		}


		//set relative mode if lower case
		if (type.equals("m")||(type.equals("l")||(type.equals("q")))){	
			newwaypoint.setRelative();
		//	Log.info("addeding as relative position");
		}*/

		super.add(newwaypoint);


		//Log.info("added:" + x + "," + y + " type" + type);

	}

	/** 
	 * calculates the current length as the .PathLength variable might not be up to date.
	 * returns the pathlength as well as storing that new value in PathLength **/
	public int getCurrentLength(){
		Log.info("Getting path length");
		
		int length2=0; //could use double for a bit more accuracy?

		int lastWaypointX=this.get(0).pos.x;
		int lastWaypointY=this.get(0).pos.y;
		int lastWaypointZ=this.get(0).pos.z;

	//	int lastX=0;
	//	int lastY=0;
	//	int lastZ=0;
		
		//add up all X's and Ys
		Iterator<MovementWaypoint> tit = this.iterator();

		int dx,dy,dz= 0;
		
		while (tit.hasNext()) {

			MovementWaypoint vertex = (MovementWaypoint) tit.next();

			
			if (vertex.isRelative()) {
				
				//if relative the distance is just the current points magnitudes
				dx = Math.abs(vertex.pos.x);
				dy = Math.abs(vertex.pos.y);
				dz = Math.abs(vertex.pos.z);
				
			}  else {

				//else its the distance between this point and the last
				dx=Math.abs(vertex.pos.x-lastWaypointX);
				dy=Math.abs(vertex.pos.y-lastWaypointY);
				dz=Math.abs(vertex.pos.z-lastWaypointZ);
			}
			
			int dis = (int) Math.sqrt((dx*dx)  +  (dy*dy) + (dz*dz)); //assuming straight diagonal lines, no curve support, sorry
			
			Log.info("Adding segmentLength:"+dis +" dx="+dx+" dy="+dy+" dz="+dz);
			
			
			length2=length2+dis;
		
			
			

			//lastX=vertex.pos.x;
		//	lastY=vertex.pos.y;
			//lastZ=vertex.pos.z;
			
			//distance from last pos			
			lastWaypointX=vertex.pos.x;
			lastWaypointY=vertex.pos.y;			
			lastWaypointZ=vertex.pos.z;
			
			
		}

		Log.info("length by lots of trig="+length2);

		PathLength = length2;

		return length2;
	}

	/** Its the path displaced in x/y by the co-ordinates specified.
	 * Usefull if you want to have the path relative to something else **/
	public String getAsDisplacedSVGPath(int dx, int dy) {

		String path = "";

		Iterator<MovementWaypoint> it = this.iterator();

		while (it.hasNext()) {

			MovementWaypoint movementWaypoint = (MovementWaypoint) it.next();

			/*
			if (movementWaypoint.type == MovementType.Move) {
				path = path + " M";
			}

			if (movementWaypoint.type == MovementType.LineTo) {
				path = path + " L";
			}
			if (movementWaypoint.type == MovementType.QCurveToo) {
				path = path + " Q";

			}
			if (movementWaypoint.type == MovementType.CCurveToo) {
				path = path + " C";

			}
			

			if (movementWaypoint.isRelative()){
				path = path + movementWaypoint.type.svg_letter.toLowerCase();
			} else {
				path = path + movementWaypoint.type.svg_letter.toUpperCase();
			} */
			
			
			MovementType type = movementWaypoint.type;
			
			//first we just add the letter (this will do nothing if its a command or runnable)
			path = path + type.svg_letter;		
			
			//then add the co-ordinates based on the type
			switch (type) {
			case AbsoluteLoopToPathStart:
				path = path + " ";  //"z"; //wont this produce z z ? 
				break;
			case RelativeQCurveToo:
			case AbsoluteQCurveToo:
				path = path + " " 
						+ (movementWaypoint.midPoint1.x + dx) + "," + (movementWaypoint.midPoint1.y + dy) +" "
						+ (movementWaypoint.pos.x + dx)          + "," + (movementWaypoint.pos.y + dy);
				break;
			case RelativeCCurveToo:
			case AbsoluteCCurveToo:				
				path = path + " "
						+ (movementWaypoint.midPoint1.x + dx) + "," + (movementWaypoint.midPoint1.y + dy) +" "
						+ (movementWaypoint.midPoint2.x + dx) + "," + (movementWaypoint.midPoint2.y + dy) +" "						            
						+ (movementWaypoint.pos.x + dx)       + "," + (movementWaypoint.pos.y + dy);
				break;		
			case Command:
				break;
			case InternalRunnable:
				break;
				//we default too just adding the co-ordinates
			case AbsoluteLineTo:
			case AbsoluteMove:
			case RelativeLineTo:
			case RelativeMove:
			default:
				path = path + " " + (movementWaypoint.pos.x + dx) + ","	+ (movementWaypoint.pos.y + dy);
				break;			
			}




		}
		path = path + " ";

		//Log.info("path=" + path.trim());

		// loop and get real path

		//	Log.info("got svg path:"+path);


		return path.trim();

	}

	/**
	 * Gets the number of vertexs
	 * Note; if the last node is a loop/closepath(z) this isnt included in the total
	 * However, other loop signifier in the middle might be
	 * @return
	 */
	public int numberOfVertexs(){

		//if the last node is a loop back, then we must ignore it when getting the size
		if ((super.get(super.size()-1)).type == MovementType.AbsoluteLoopToPathStart){
			return super.size()-1;	
		}

		return super.size();		

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


			/*
			if (movementWaypoint.isRelative()){
				Log.info("setting movement string to lowercase2");
				
				path = path + nextCommandString.toLowerCase();
			} else {
				path = path + nextCommandString.toUpperCase();
			}*/
			
			path = path + nextCommandString;
			
			if (movementtype == MovementType.AbsoluteLoopToPathStart) {

				path = path + " z";

			} else {
				//insert the standard co-ordinates
				if (insertzvalues){
					path = path + " " + movementWaypoint.pos.x + "," + movementWaypoint.pos.y+","+movementWaypoint.pos.z;
				} else {
					path = path + " " + movementWaypoint.pos.x + "," + movementWaypoint.pos.y;

				}

			}

		}
		path = path + " ";

		//Log.info("path=" + path.trim());

		// loop and get real path

		return path.trim();

	}

	public void setName(String newname) {
		pathsName=newname;


	}
	public String getName() {
		return pathsName;


	}

	public void setPostAnimationCommands(Runnable runthese){
		postAnimationCommands=runthese;

	}

	
	public MovementWaypoint getLastWaypoint() {
		return this.get(this.numberOfVertexs()-1);
	}

}
