package com.lostagain.Jam.CollisionMap;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.google.common.collect.Maps;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;

import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/** A Polygon is...well..a polygon.<br>
 * Its a single, closed, collection of points used to define a region<br>
 * In this engine they are mostly used for collision map componants, or for named regions<br>
 * <br>
 * Internally a Polygon is actually a extended MovementPath. While it doesn't represent something<br>
 * that moves, they have much the same attributes; They both are basically a collection of vertexs<br>
 * and they both are defined in SVG format.<br>
 * 
 * NOTE: in future this should extend SpiffyPath instead
 * **/
public class Polygon extends MovementPath {

	/** The PolygonCollisionMap this polygon is part of **/
	public PolygonCollisionMap sourceMap;

	/**
	 * The object this polygon is relative too, if any. 
	 * (can be different then the sourceMaps)
	 */
	public SceneObject associatedObject = null; 


	/** the amount the outset path is offset by **/
	static int outSetAmount = 32;

	/** a path that goes around this one, outside by the default amount **/
	MovementPath outsetPath;

	/** if its incorporeal then we ignore all collisions.
	 * Outset paths should not be generated or used for incomporal objects.**/
	public boolean incorporeal = false;
	

	static ArrayList<Polygon> allPolygons = new ArrayList<Polygon>();

	/** A single polygon. This is like a movement path, except its closed
	 * and designed to be used for collisions or defining regions 
	 * @param path - an SVG formated path like M 0,0 245,245 0,245 z <br>
	 * @param name - the name assigned to the polygon. Used for testing if something is inside named regions 
	 * @param sourceMap - The PCM that this polygon belongs too.<br>
			Polygons need to know where they come from, so they can see what object<br>
		 they are attached to, and thus work out their absolute co-ordinates in the<br>
		  scene. This ensures they seem to move correctly as the object does. 
	    @param incomporal - if true the polygon is a ghost, and things can pass through it. Used for defining named regions in a scene**/
	public Polygon(String path, String name, PolygonCollisionMap sourceMap, Boolean incorporeal) {

		super(path, name);
		this.sourceMap = sourceMap;
		//we get our associatedObject from the source by default
		associatedObject=sourceMap.defaultAssociatedObject;
		
		this.incorporeal = incorporeal;
		
		allPolygons.add(this);

	}
	/** A single polygon. This is like a movement path, except its closed
	 * and designed to be used for collisions or defining regions 
	 * @param path - an SVG formated path like M 0,0 245,245 0,245 z <br>
	 * @param name - the name assigned to the polygon. Used for testing if something is inside named regions 
	 * @param sourceMap - The PCM that this polygon belongs too.<br>
			Polygons need to know where they come from, so they can see what object<br>
		 they are attached to, and thus work out their absolute co-ordinates in the<br>
		  scene. This ensures they seem to move correctly as the object does. 
	 **/

	public Polygon(String path, String name, PolygonCollisionMap sourceMap) {

		super(path, name);
		this.sourceMap = sourceMap;
		//we get our associatedObject from the source by default
				associatedObject=sourceMap.defaultAssociatedObject;
				

		allPolygons.add(this);

	}
	/** A single polygon. This is like a movement path, except its closed
	 * and designed to be used for collisions or defining regions 
	 * @param clonethispath - an existing path to clone <br>
	 * @param name - the name assigned to the polygon. Used for testing if something is inside named regions 
	 * @param sourceMap - The PCM that this polygon belongs too.<br>
			Polygons need to know where they come from, so they can see what object<br>
		 they are attached to, and thus work out their absolute co-ordinates in the<br>
		  scene. This ensures they seem to move correctly as the object does. 
	 **/
	public Polygon(MovementPath clonethispath, String name,
			PolygonCollisionMap sourceMap) {

		super(clonethispath, name);
		this.sourceMap = sourceMap;
		//we get our associatedObject from the source by default
				associatedObject=sourceMap.defaultAssociatedObject;
				

		allPolygons.add(this);

	}

	/** generates the outset path. Should be triggered after all the path points are added **/
	public void generateOutSetPath() {


		//create the path object
		outsetPath = new MovementPath("",super.pathsName+"_outset");

		//loop over path points, creating a outwards displaced version of them
		int num=0;
		int nextpathsegmentstart=0;
		
		for (MovementWaypoint vertex : this) {

			int vx = vertex.pos.x;  //vx is the vertex x! :P
			int vy = vertex.pos.y;
			
			//if (vertex.isMoveTo()){
			//	Log.info("setting lastMoveToNum:"+num);
			//	lastMoveToNum=num;
			//}

			//displace these co-ordinates outwards
		//	Log.info("getting side:"+num);
			PolySide sidebefore;
			if (num!=nextpathsegmentstart){
				sidebefore = this.getSide(num - 1,true);
			} else {
				//if we are the first side the one before is either the end waypoint or the next AbsoluteLoopToPathStart
				int nextlooptostart = findnextlooptostart(nextpathsegmentstart);
			//	Log.info("nextlooptostart:"+nextlooptostart);
				sidebefore = this.getSide(nextlooptostart-1,true);
			}
			
			//todo; if the last point on side before is a loop to,the next side should start at the last moveto
			PolySide sideafter;
			MovementType type = vertex.type;
			
			/*
			if (this.get(sidebefore.endVertexNumber).type==MovementType.AbsoluteLoopToPathStart){				

				
				sideafter = this.getSide(lastMoveToNum,true); //if it loops this will be wrong as it loops to start not last m
				
				Log.info("loop to sidebefore:"+sidebefore);	
				Log.info("loop to sideafter:"+sideafter+"("+lastMoveToNum+")");	
				Log.info("-->"+type);				
				
				//type = get(sidebefore.startVertexNumber).type; //ensure we match the type of the last line (rather then loop)
				if (vertex.type==MovementType.AbsoluteLoopToPathStart){
					type = MovementType.AbsoluteLineTo;
				}
				
			} else {						
				 sideafter = this.getSide(num,true); 
			}
			
*/
			 sideafter = this.getSide(num,true); 
			 
				
			if (vertex.type!=MovementType.AbsoluteLoopToPathStart){
				//if it is not loop to start we do nothing
			} else {
				//if the vertex is merely there to specify "loop to start, we do nothing"
				//	Log.info("(loop to start, thus no co-ordinates");
				outsetPath.addMovementWayPoint(vertex.type, 0, 0);
				num=num+1;	
				nextpathsegmentstart = num; //we need to remember the next node however, as that marks the start of a path segment (and thus what the next z should loop too)				
				continue;
			}
			 
			

		//	Log.info("loop to sidebefore:"+sidebefore);	
		//	Log.info("loop to sideafter:"+sideafter);	
			
			//work out the outset co-ordinates
			Simple2DPoint outset = insetCorner(sideafter.endPoint,sidebefore.endPoint,sidebefore.startPoint,outSetAmount);

			//if its not a null, we add it to the new path
			if (outset==null){
				Log.info("outset is null :-/ "+sidebefore+"->>"+sideafter);
				//if its a null, we just add the original co-ordinates.
				//this should never happen, but its a relatively safe failure
				//it just means one point of the outset path will be wrong
				outsetPath.addMovementWayPoint(type, vx,vy);
				num=num+1;				

			} else{

				//add to new path			
				outsetPath.addMovementWayPoint(type, outset.x, outset.y);
				num=num+1;

			}


		}

		//by the end of all this we should have a new path that surrounds the original path
		Log.info("new path created:"+outsetPath.getAsSVGPath());


	}

	/**
	 * finds the next loop to start waypoint
	 * If none is present, will return the num of the last waypoint
	 * @param i
	 * @return
	 */
	private int findnextlooptostart(int from) {
		
		for (int num = from; num<super.size(); num++) {
			
			MovementWaypoint wp = this.get(num);
			
			if (wp.type==MovementType.AbsoluteLoopToPathStart){
				return num;
			}
		}
		
		return this.size();
	}
	/** used as part of the outset path generation process.
	 * It returns a point inset from a corner. **/
	private Simple2DPoint insetCorner(Simple3DPoint previous, Simple3DPoint current, Simple3DPoint next, int dis){

		return insetCorner(previous.x,previous.y,
				current.x,current.y,
				next.x,next.y, dis);
	}
	/** used as part of the outset path generation process.
	 * It returns a point inset from a corner. **/
	private Simple2DPoint insetCorner(
			double  px, double  py,   //  previous point
			double  cx, double  cy,   //  current point that needs to be inset
			double  nx, double  ny,   //  next point
			double insetDist) {     //  amount of inset/outset (perpendicular to each line segment)

		double  c1=cx, d1=cy, c2=cx, d2=cy, dx1, dy1, linedisprev, dx2, dy2, linedisnext;

		double insetX, insetY; //result

		//  Calculate length of line segments between this point at the previous
		dx1=cx-px; //difference in x
		dy1=cy-py; //difference in y
		linedisprev= Math.hypot(dx1, dy1); //get the length of the diagonal between this point and the last

		//calculate the length of the line segments between this point (c/d) and the next
		dx2=nx-cx; 
		dy2=ny-cy; 
		linedisnext= Math.hypot(dx2, dy2); //This function is the same as doing Math.sqrt(dx2*dx2+dy2*dy2); [/factoid]

		//  Exit if either segment is zero-length.
		if (linedisprev==0.0 || linedisnext==0.0) {

			Log.info("segments are zero length:"+linedisprev+","+linedisnext);

			return null;					

		};

		//we work out the inset/outset the corner by inserting the lines that lead to it
		//then seeing where they meet

		//  Inset each of the two line segments.

		//first the line segmant before the point we want inset
		insetX= dy1/linedisprev*insetDist; //(Y componant of distance / total distance) * inset amount
		//the above thus gives how much to inset in y axis (that is, we move its start and end points over by that amount)
		px+=insetX; c1+=insetX;

		//now repeat for the X componant of the displacement
		insetY=-dx1/linedisprev*insetDist; 
		py+=insetY; d1+=insetY;

		//-------and now the same for the line after the point we are looking at
		insetX= dy2/linedisnext*insetDist; 
		nx+=insetX; c2+=insetX;

		insetY=-dx2/linedisnext*insetDist; 
		ny+=insetY; d2+=insetY;

		//now we have two new lines, the new corner is basicly where they meet.

		//  If inset segments connect perfectly, return the connection point.
		if (c1==c2 && d1==d2) {					   
			return new Simple2DPoint((int)c1,(int)d1);

		}

		//Return the intersection point of the two lines if theres any
		Simple2DPoint insectPoint =  lineIntersection(px,py,c1,d1,c2,d2,nx,ny);

		if (insectPoint!=null) {				     
			//inset corner point calculated!
			return new Simple2DPoint(insectPoint.x,insectPoint.y);

		} else {
			//none found :(
			Log.info("line points:"+px+","+py+" - "+c1+","+d1+"   "+c2+","+d2+" - "+nx+","+ny);
			Log.info("no intersect point found"); //if we attempted to inset or outset a perfectly parrall line this will happen
		}

		return null;			


	}


	/** this method isnt used anymore, but it might be usefull to keep around
	 * it finds the angle between two sides.
	 * It used to be used to work out the inset/outset, before I found a better method
	 * -Thomas **/
	static public double getAngleBetweenSides(PolySide sidebefore,
			PolySide sideafter, String debugcolor) {

		int diffx1 = sidebefore.startPoint.x - sidebefore.endPoint.x;
		int diffy1 = sidebefore.startPoint.y - sidebefore.endPoint.y;

		double ang1 = Math.atan2(diffy1, diffx1);
		// wrap angle
		//ang1 = ang1 % (2 * Math.PI);

		// Log.info("angle between: " + sidebefore.toString()
		// + " and the vertical is " + Math.toDegrees(ang1));

		// angle between side one and vertical
		int diffx2 = sideafter.startPoint.x - sideafter.endPoint.x;
		int diffy2 = sideafter.startPoint.y - sideafter.endPoint.y;

		double ang2 = Math.atan2(diffy2, diffx2) + Math.PI;


		// Log.info("angle between: " + sideafter.toString()
		// + " and the vertical is " + Math.toDegrees(ang2));

		double ang3 = (ang1 + ang2) / 2;

		// wrap angle
		ang2 = ang2 % (2 * Math.PI);

		int fy3 = (int) (Math.sin(ang3) * outSetAmount);
		int fx3 = (int) (Math.cos(ang3) * outSetAmount);

		// make sure its clear, else flip (this needs to be done in a size-neutral way)
		if (sidebefore.source.isPointInside(sidebefore.endPoint.x + fx3,
				sidebefore.endPoint.y + fy3)) {
			ang3 = ang3 + Math.PI;

			Log.info("angle flipped to" + Math.toDegrees(ang3));
			ang3 = ang3 % (2 * Math.PI);
			fy3 = (int) (Math.sin(ang3) * outSetAmount);
			fx3 = (int) (Math.cos(ang3) * outSetAmount);
		}

		// Log.info("angle between them: " + Math.toDegrees(ang3));
		// Log.info("------------------------------------------------------------------------------------");

		// preview line
		// int fy = (int) (Math.sin(ang1) * collision_margin);
		// int fx = (int) (Math.cos(ang1) * collision_margin);

		// int fy2 = (int) (Math.sin(ang2) * collision_margin);
		// int fx2 = (int) (Math.cos(ang2) * collision_margin);

		//clearSketch();

		//this.addToSketch(" M " + sideafter.startPoint.x + ","
		//		+ sideafter.startPoint.y + " L " + sideafter.endPoint.x + ","
		//		+ sideafter.endPoint.y, "green");
		//this.addToSketch(" M " + sidebefore.startPoint.x + ","
		//		+ sidebefore.startPoint.y + " L " + sidebefore.endPoint.x + ","
		//		+ sidebefore.endPoint.y, "red");
		// " M " + sidebefore.startPoint.x + "," + sidebefore.startPoint.y +
		// " l " + fx + "," + fy + " " +
		// " M "+ sideafter.endPoint.x + "," + sideafter.endPoint.y + " l "+ fx2
		// + "," + fy2 + " " +

		//this.addToSketch(" M " + sidebefore.endPoint.x + ","
		//		+ sidebefore.endPoint.y + " l " + fx3 + "," + fy3 + " ",
		//		debugcolor);

		return ang3;
	}

	
	/**
	 * gets the SVG path, adding the objects position and adding the fake height
	 * @return
	 */
	public String getObjectRelativeSVGPath() {
		
		 SceneObject object = 	 associatedObject;// this.sourceMap.associatedObject;
		 
		 //+ object.getObjectsCurrentState().Z
		 String path =  getAsDisplacedSVGPath(object.getTopLeftBaseX() , object.getTopLeftBaseY() );
					
		 
		return path;
		
	}

	
	/**
	 * 
	 **/
	public String getPathsOfAllNormals(SimpleVector3 scale) {

		String normalsPath="";
		

		Iterator<MovementWaypoint> it = this.iterator();
		MovementWaypoint lastWaypoint = null;

		 SceneObject object = associatedObject;//this.sourceMap.associatedObject;
		 int disX=0;
		 int disY=0;
		 
		 if (object!=null){
			 disX=object.getTopLeftBaseX();
			 disY=object.getTopLeftBaseY();
			 
		 }

			MovementWaypoint movementWaypoint = null;
			
		while (it.hasNext()) {

			 movementWaypoint = (MovementWaypoint) it.next();
			
			//only support lines really
			MovementType type = movementWaypoint.type;
			if (type==MovementType.AbsoluteLineTo && lastWaypoint!=null){
				
				String normal = getNormalsAsSVG(lastWaypoint.pos,movementWaypoint.pos,disX,disY,scale);
				normalsPath=normalsPath+"\n"+ normal;
						
			}
			
			
			lastWaypoint=movementWaypoint;
			
			
		}
		
		
		//and one back to the start
		MovementType type = lastWaypoint.type;		
		MovementWaypoint startpoint = this.get(0);
		if ( lastWaypoint!=null){		
			String normal = getNormalsAsSVG(lastWaypoint.pos,startpoint.pos,disX,disY,scale);
			normalsPath=normalsPath+"\n"+ normal;
					
		}
	

		
		return normalsPath;
		
	}
		
	
	/**
	 * for debugging normals, returns the normal of the path, aligned to its center
	 * 
	 * @param startPoint
	 * @param endPoint
	 * @return
	 */
	public String getNormalsAsSVG(Simple3DPoint startPoint, Simple3DPoint endPoint, int displacementX,int displacementY,SimpleVector3 scale) {
		
		double multiplier = 25; //length
		
		//work out normals
		int dx = endPoint.x-startPoint.x;
		int dy = endPoint.y-startPoint.y;

		SimpleVector3 normal1 = new SimpleVector3(-dy,dx,0); //looks weird, but yes, swapping the x and y is correct here
		
		//normal1.y = normal1.y/2.0; //temp
		normal1.mul(scale);
		
		normal1.normalize();
		
		
		SimpleVector3 normal2 = new SimpleVector3(dy,-dx,0); //see link for how normals are worked out
		//------------------
		
		//make path from it (easiest way to generate the svg?)
		MovementPath debugNormal = new MovementPath("","debub_normal");
		
		double centerlinex = startPoint.x+(dx/2.0);
		double centerliney = startPoint.y+(dy/2.0);
				
		debugNormal.addMovementWayPoint(MovementType.AbsoluteMove, (int)centerlinex, (int)centerliney,   0);	
		

		double normalx= centerlinex+(normal1.x*multiplier);
		double normaly= centerliney+(normal1.y*multiplier);
		
		debugNormal.addMovementWayPoint(MovementType.AbsoluteLineTo, (int)normalx,  (int)normaly, 0);	

		
			
		
		 
		return debugNormal.getAsDisplacedSVGPath(displacementX, displacementY);
		
	}

	
	
	@Override
	/** Its the path displaced in x/y by the co-ordinates specified.
	 * Useful if you want to have the path relative to something else **/
	public String getAsDisplacedSVGPath(int dx, int dy) {

		//only one path if not corporal
		if (incorporeal){
			return super.getAsDisplacedSVGPath(dx, dy);
		}

		//else we have a path and its offset displayed together
		return super.getAsDisplacedSVGPath(dx, dy)+" "+outsetPath.getAsDisplacedSVGPath(dx, dy);
	}

	
	/** gets the polygon side - taking into account the location of the associated object its relative too, if any  
	 * 
	 * Note: Sides that end with "z" auto loop to first co-ordinate.
	 * However, you should never specify a side number that resolves to a "z" in the list. (or any other non-real waypoint)
	 * 
	 *  //TODO: currently this will return sides that start with a real position (M 10,10) but then have a command [] as there next waypoint. Instead it should return the next side after that.
			
	 * **/
	public PolySide getSide(int SideNumber) {
		return getSide(SideNumber,false);
	}
	/** 
	 * gets the polygon side - 
	 * you can choose to take into account the location of the associated object its relative too, if any.  
	 * 
	 * Note: Sides that end with "z" auto loop to first co-ordinate.
	 * However, you should never specify a side number that resolves to a "z" in the list. (or any other non-real waypoint)
	 * You can skip over unreal sides by doing something like;
	 * if (!this.get(j).isRealWayPoint()){
	*			continue; //skip it
	 *}
	 *
	 * NOTE: DOES NOT YET SUPPORT Paths with commands. If you want to use them, we need to auto-skip them when making the side
	 * 
	 * **/	
	//TODO: maybe cache the displaced result? it would need refreshed if the object moves
	//if (relativeCache.get(sideNumber) !=null) then use cache
	//(cache needs to be cleared any time the object moves, however)
	public PolySide getSide(int SideNumber, boolean returnRelativeCoOrdinates) {

		// loop around if negative (again, like a clockface)
		if (SideNumber < 0) {

			//Log.info("_____________________****************____________looping to get side:"+SideNumber);
			//Log.info("_____________________****************____________there is:"+this.numberOfVertexs()+"vertexs");

			SideNumber = (this.numberOfVertexs() ) + SideNumber;

			//Log.info("_____________________****************____________new side number is:"+SideNumber);
		}
		//debug 
		if (SideNumber > this.numberOfVertexs()) {
			Log.info("_____________________****************____________getting side:"+SideNumber);
		}

		//displacement is zero by default
		int dx = 0;
		int dy = 0;
		int dz = 0;
		
		if (!returnRelativeCoOrdinates){
			// displacements if any
			if (associatedObject != null) { //sourceMap.associatedObject
				//	dx = sourceMap.associatedObject.getObjectsCurrentState().X;
				//	dy = sourceMap.associatedObject.getObjectsCurrentState().Y;
				dx = associatedObject.getTopLeftBaseX(); //sourceMap.associatedObject
				dy = associatedObject.getTopLeftBaseY();//sourceMap.associatedObject
				
				//should we also add the fake height?
				//This moves the collision map down the page - as we treat our 2d collision map as "projected downwards" from the top
				//dy=dy+sourceMap.associatedObject.getObjectsCurrentState().Z;

			}
		}

		// to make the side we go to the # vertex and the one after it
		MovementWaypoint cur = this.get(SideNumber); //cur is just used as a tempory variable here to get values from this polygon

		//TODO: rather then "one after" it should be "next valid real waypoint"
		//When generating sides, commands should be ignored
				
		//create a point with the displacements - this is the start of the side
		Simple3DPoint start = new Simple3DPoint(cur.pos.x + dx, cur.pos.y + dy, cur.pos.z + dz);

		// if its the last side then its last point is the same as the first
		// (as these should all be closed polygons)
		if ((SideNumber + 1) > (super.size() - 1)) { //.numberOfVertexs()
			cur = this.get(0); //get the first vertex of this polygon to be the end point of the side we want
		} else {
			cur = this.get(SideNumber + 1); //get the vertex of this polygon which is the end point of the side we want
		}

		//if the end vertext represented a "z" in svg we gotta use the last moveto point instead, as this is where it loops
		if (cur.type==MovementType.AbsoluteLoopToPathStart){
		//	Log.info("this polygon side contains a loop to start, therefor we need to find the last moveto in the list:"+SideNumber);
						
			//we now need to use the last move to instead so we loop over all the points looking for it.
			//if none is specified, we assume the first point in the path
			cur=this.get(0);
			for (int i = SideNumber; i >=0; i--) {
				MovementWaypoint temp = this.get(i);
				if (temp.isMoveTo()){
				//	Log.info("found last move to at "+i+" = "+temp.toString());
					cur=temp;
					break;					
				} 
			}
			
			
		}
		
		
		//create the end point from the new value of cur retrieved above
		Simple3DPoint end = new Simple3DPoint(cur.pos.x + dx, cur.pos.y + dy, cur.pos.z + dz);

		//create a polyside from the start and end points
		return new PolySide(start, end, SideNumber,this);
	}


	/** creates a movement path from the specified side 
	 * might be usefull if you want a quick svg of just one side of a object**/
	public MovementPath getSideAsPath(int C) {

		Log.info("MovementPath getting side as Path:" + C);

		// get the side
		PolySide side = getSide(C);

		int x = side.startPoint.x;
		int y = side.startPoint.y;

		int x2 = side.endPoint.x;
		int y2 = side.endPoint.y;

		MovementPath temp = new MovementPath("", "tempside_" + C);

		temp.add(new MovementWaypoint(x, y, MovementType.AbsoluteMove));
		temp.add(new MovementWaypoint(x2, y2, MovementType.AbsoluteLineTo));

		return temp;

	}
	
	/**
	  * looks over all sides testing if the line intersects them 
	 * if a side intersects it returns a SpiffyPolygonCollision storing the details of the intersection.
	 * 
	 * If both the start and end points are fully inside the polygon, we return a SpiffyPolygonCollision with the startpoint used, but no side set 
	
	* note; we take 3d points, but assume z is 0. this does not yet support 3d lines 
	 * @param start
	 * @param end
	 * @return
	 */
	public SpiffyPolygonCollision testForCollision(Simple3DPoint start,	Simple3DPoint end) {
		return testForCollision(start,end,false);
	}

	/** 
	 * looks over all sides testing if the line intersects them 
	 * if a side intersects it returns a SpiffyPolygonCollision storing the details of the intersection.
	 * 
	 * If both the start and end points are fully inside the polygon, we return a SpiffyPolygonCollision with the startpoint used, but no side set 
	 * 
	* note; we take 3d points, but assume z is 0. this does not yet support 3d lines
	 * @param start
	 * @param end
	 * @param extendTestedLineToInfinity - false by default. Extends the line your testing to infinity in both directions. Usefull for extrapolated movement 
	 * @return
	 */
	public SpiffyPolygonCollision testForCollision(Simple3DPoint start,	Simple3DPoint end, boolean extendTestedLineToInfinity) {

		//if we are a ghost, we ignore, as we can't collide with anything
		if (incorporeal){
			return null;
		}

		//side we are testing
		int currentside = 0;


		SpiffyPolygonCollision currentNearestCollision = null;

		//below is just fordebugging
		//Log.info("sides in poly =  " + (this.numberOfVertexs() - 1));
		//if (this.sourceMap.associatedObject != null) {
		if (associatedObject !=null)	{
			//Log.info("associated with:"
			//		+ this.sourceMap.associatedObject.objectsCurrentState.ObjectsName);
		}


		//test if the start is enclosed
		boolean startEnclosed = this.isPointInside(start.x, start.y);

		//if the start is enclosed and also no line intersected, then we know the whole line must be inside this polygon, so there wont be more collisions
		if (startEnclosed){
			boolean endEnclosed = this.isPointInside(end.x, end.y);
			if (endEnclosed){
				currentNearestCollision = new SpiffyPolygonCollision(start.x,	start.y,this, null, 0);
				Log.info("line:  "+start+">>"+end+" is fully enclosed by a polygon, returning a SpiffyPolygonCollision with null side");
				return currentNearestCollision;
			}
		}

		//start the loop to test each side of this polygon
		while (currentside <= (this.numberOfVertexs() - 1)) {

			//skip any side that starts with a Z, as this doesn't represent a real side 
			if (!get(currentside).isRealWayPoint(true)){
				currentside++;
				continue;
			}
			
			// get side we are testing
			PolySide curside = getSide(currentside); //TODO: currently this will return sides that start with a real position (M 10,10) but then have a command [] as there next waypoint. Instead it should return the next side after that.
			

			
			//Log.info("testing side :" + curside.startVertexNumber + ">>" + curside.endVertexNumber);
			//seems to be relative values?? should be absolute surely? how did this ever work?
			//did I previously compare to relative? did this previous store absolute?
			//Log.info("testing side :" + curside.startPoint.toString() + ">>"+ curside.endPoint.toString());

			//TODO: should we check this first? or lineIntersect? 
			//Which is faster on average?



			// test this line against the specified one to see if it intersects					
			Simple2DPoint hit = lineIntersect2d(start, end, curside.startPoint,	curside.endPoint,extendTestedLineToInfinity);

			//if there's a hit
			if (hit != null) {

				//work out the distance between the start and where we hit
				int distance = (int) Math.hypot(Math.abs(start.x - hit.x),
						Math.abs(start.y - hit.y));

				//Log.info("collision at distance :" + distance);

				//if there's no current nearest collision we set it as the nearest
				if (currentNearestCollision == null) {

					//create a new collision from the data and set currentNearestCollision to it
					currentNearestCollision = new SpiffyPolygonCollision(hit.x,
							hit.y, this, curside, distance);
				}

				//if there's an existing collision thats been found, we only
				//replace it if this ones nearer
				if (distance < currentNearestCollision.distance) {
					//create a new collision and replace the current one
					currentNearestCollision = new SpiffyPolygonCollision(hit.x,	hit.y, this, curside, distance);

				}
			} else {




			}




			//proceed to testing the next side
			currentside++;

		}
		//return the nearest collision found
		return currentNearestCollision;

	}
	
	/**
	 * First Converts the 3d points to 2d points ,ignoring z, then runs the normal lineIntersect2d function
	 * This is too aid in our slow conversion to 3d.
	 * Eventually this command shouldnt be needed
	 * 
	 * @param line1start
	 * @param line1end
	 * @param line2start
	 * @param line2end
	 * @return
	 */
	public static Simple2DPoint lineIntersect2d(Simple3DPoint line1start_atground,
			Simple3DPoint line1end_atground, Simple3DPoint line2start_atground, Simple3DPoint line2end_atground,boolean extendTestedLineToInfinity) {
		
		Simple2DPoint line1start = new Simple2DPoint(line1start_atground.x,line1start_atground.y);
		Simple2DPoint line1end = new Simple2DPoint(line1end_atground.x,line1end_atground.y);
		Simple2DPoint line2start =new Simple2DPoint(line2start_atground.x,line2start_atground.y);
		Simple2DPoint line2end=new Simple2DPoint(line2end_atground.x,line2end_atground.y);
		
		
		return lineIntersect2d(line1start, line1end, line2start, line2end,extendTestedLineToInfinity);
	}
	
	
	/** returns the intersection between two lines or null <br>
	 * Confined to the lines length if extendTestedLineToInfinity is false<br>
	 * <br>
	 * returns null if lines are parallel or don't meet <br>
	 * 
	 * @param line1start
	 * @param line1end
	 * @param line2start
	 * @param line2end
	 * @return
	 */

	public static Simple2DPoint lineIntersect2d(Simple2DPoint line1start,
			Simple2DPoint line1end, Simple2DPoint line2start, Simple2DPoint line2end) {
		
		return lineIntersect2d(line1start,
		 line1end,  line2start,  line2end,false);
	}

	/** returns the intersection between two lines or null <br>
	 * Confined to the lines length if extendTestedLineToInfinity is false<br>
	 * <br>
	 * returns null if lines are parallel or don't meet <br>
	 * 
	 * @param line1start
	 * @param line1end
	 * @param line2start
	 * @param line2end
	 * @param extendTestedLineToInfinity
	 * @return
	 */
	public static Simple2DPoint lineIntersect2d(Simple2DPoint line1start,
			Simple2DPoint line1end, Simple2DPoint line2start, Simple2DPoint line2end,boolean extendTestedLineToInfinity) {

		if (extendTestedLineToInfinity){
			return lineIntersection(line1start.x, line1start.y, line1end.x,
						line1end.y, line2start.x, line2start.y, line2end.x, line2end.y);

		}
		return lineSegmentIntersect(line1start.x, line1start.y, line1end.x,
				line1end.y, line2start.x, line2start.y, line2end.x, line2end.y);

	}

	/** returns the intersection between two lines or null 
	 * Confined to the lines length.
	 * 
	 * returns null if lines are parallel or don't meet 
	 * **/
	public static Simple2DPoint lineSegmentIntersect(double x1, double y1, double x2,
			double y2, double x3, double y3, double x4, double y4) {

		//frankly this is all maths I don't understand
		//scary scary maths
		//umm..yeah...
		double denom = (y4 - y3) * (x2 - x1) - (x4 - x3) * (y2 - y1);

		if (denom == 0.0) { 
			//	Log.info("_________________________________Lines are parallel");
			return null;
		}
		double ua = ((x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3)) / denom;
		double ub = ((x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3)) / denom;

		if (ua >= 0.0f && ua <= 1.0f && ub >= 0.0f && ub <= 1.0f) {
			// Get the intersection point and return it
			return new Simple2DPoint((int) (x1 + ua * (x2 - x1)), (int) (y1 + ua
					* (y2 - y1)));
		}

		return null;
	}



	/** returns the intersection between two lines or null <br>
	 * extends given lines to infinity so anything but parallel lines eventually meet <br>
	 * <br>
	 * This is an alternative to the other method (lineSegmentIntersect). At some point might be an idea to benchmark the two intersection methods
	 * to see what works faster <br>
	 * <br>
	 * @param Ax line1s start x
	 * @param Ay line1s start y
	 * @param Bx line1s end x
	 * @param By line1s end y
	 * @return the intersection between two lines or null
	 * **/
	public static Simple2DPoint lineIntersection(
			double Ax, double Ay, //line 1 start
			double Bx, double By, //line 1 end
			double Cx, double Cy, //line 2 start
			double Dx, double Dy) // line 2 end
	{

		double  distAB, theCos, theSin, newX, ABpos ;

		//  return null if either of the lines is zero length.
		if ((Ax==Bx && Ay==By) || (Cx==Dx && Cy==Dy)) {				
			return null;

		}

		// Translate the system so that point A is on the origin.
		Bx-=Ax; By-=Ay;
		Cx-=Ax; Cy-=Ay;
		Dx-=Ax; Dy-=Ay;

		//  Discover the length of segment A-B.
		//distAB=Math.sqrt(Bx*Bx+By*By);
		distAB=Math.hypot(Bx, By);

		//  (2) Rotate the system so that point B is on the positive X axis.
		theCos=Bx/distAB;
		theSin=By/distAB;
		newX=Cx*theCos+Cy*theSin;
		Cy  =Cy*theCos-Cx*theSin; Cx=newX;
		newX=Dx*theCos+Dy*theSin;
		Dy  =Dy*theCos-Dx*theSin; Dx=newX;

		//  return null if the lines are parallel and thus never meet
		if (Cy==Dy) {
			return null;
		}

		//  (3) Discover the position of the intersection point along line A-B.
		ABpos=Dx+(Cx-Dx)*Dy/(Dy-Cy);

		//  (4) Find the discovered position above in the original lines coordinate system and return it as a simple point
		double  interX=Ax+ABpos*theCos;
		double  interY=Ay+ABpos*theSin;

		//  Success! so we return the point
		return new Simple2DPoint((int)interX,(int)interY); 

	}

	/**  test if this polygon is fully contained by the specified rectangle 
	 * 
	 * @param tlx - top left of rect x
	 * @param tly - top left of rect y
	 * @param brx - bottom right of rect x 
	 * @param bry - guess
	 * @return
	 */
	public boolean isThisContainedBy(int tlx, int tly, int brx, int bry) {

		//just test one point
		int testX = this.getWaypointX(0); //what if waypoint is non-real?
		int testY = this.getWaypointY(0);

		if ((testX>tlx)&&(testX<brx)){

			if ((testY>tly)&&(testY<bry)){
				return true;
			}

		}
		return false;
	}
	
	
	/**
	 * detects if a box is inside or colliding with this polygon	 *  
	 * **/
	public boolean isBoxInside(int tlx, int tly, int brx, int bry) {

		//make points of the 4 specified box corners
		Simple2DPoint topleft = new Simple2DPoint(tlx,tly);
		Simple2DPoint topright = new Simple2DPoint(brx,tly);
		Simple2DPoint bottomleft = new Simple2DPoint(tlx,bry);
		Simple2DPoint bottomright = new Simple2DPoint(brx,bry);

		//---now make sides to test for the box we are testing
		PolySide top = new PolySide(topleft, topright, 0);
		PolySide right = new PolySide(topright, bottomright, 1);
		PolySide bottom = new PolySide(bottomright, bottomleft, 2);
		PolySide left = new PolySide(bottomleft, topleft, 3);		

		// check each of the sides for collision or containment.
		if (testForCollision(top.startPoint,top.endPoint)!=null){

			Log.info("___________________top collision");
			return true;
		}
		if (testForCollision(right.startPoint,right.endPoint)!=null){

			Log.info("___________________right collision");
			return true;
		}
		if (testForCollision(bottom.startPoint,bottom.endPoint)!=null){

			Log.info("___________________bottom collision");
			return true;
		}
		if (testForCollision(left.startPoint,left.endPoint)!=null){

			Log.info("___________________left collision");
			return true;
		}

		//check the whole thing isn't inside  (TODO: maybe this should be before the test for collision lines?)
		//Maybe we should check each of the boxs corners before the line tests? (as the corner being inside is the most likely sort of intersection)
		if (isPointInside(topleft.x, topleft.y)){
			Log.info("___________________contained within");
			return true;
		}

		//check that this isn't completely inside the object
		//(if your completely inside none of your sides are hitting, but your still colliding with it!
		if (isThisContainedBy(tlx, tly, brx,  bry)){
			Log.info("___________________topleft point collision");
			return true;
		}


		return false;




	}
	
	public boolean isPolygonTouching(Polygon testThis) {
		return isPolygonTouching(testThis,0,0,false);
	}

	/**
	 * currently a crude test of each line vs line to see if any collide.
	 * This can be optimized in a few ways.
	 * If we had bounding boxs, for example, we can say things arnt colliding merely if no bounding boxs hit
	 * 
	 * This function will use the cache if its been built with the buildCache()
	 * 
	 * Note; does not account for fully enclosed polygons
	 * 
	 * @param testThis
	 * @return currently just a boolean, would be usefull if we returned a collision?
	 */
	public boolean isPolygonTouching(Polygon testThis,int dx, int dy,boolean overrideNaturalDisplacement) {
		
		
		//---first if the cache is present we use it--- (temp disabled) //TODO: re-enable and check cache
		/*
		knownstate polygonsknownstate = knownPolygons.get(testThis);
		
		if (polygonsknownstate!=null){
			Log.info("using cache to test if polygon is touching. State:"+polygonsknownstate.name());				
			if (polygonsknownstate == knownstate.KnownAsTouching){
				return true;
			} else {
				return false; //assume not touching
			}	
		}*/
		//---------------------------------------------
		
		//else we manually search				
		int testingAgainstTotalSides = testThis.numberOfVertexs();
		boolean wasTouching = false;
		
		int i=0;
		
		//loop over all their sides till we find a touch (if we do)
		for (i = 0; i < testingAgainstTotalSides; i++) {		
			
			
			//we have to ensure non-real waypoints are skipped.
			//
			//When getting a side, waypoint (i) >> waypoint (i+1) represents the side 
			//Its possible for a side to end in a z, but not start with one.
			//Therefor we check the waypoint #i to see if its real or not, but don't test i+1 

		//	Log.info("i="+i+" testingAgainstTotalSides:"+testingAgainstTotalSides);
			
			if (!testThis.get(i).isRealWayPoint(true)){
				continue; //skip it
			}
			
			//Log.info("i2="+i);
						
			
			PolySide testThisSide = testThis.getSide(i,overrideNaturalDisplacement);			//used to always be false
			
			
			//add displacements 
			testThisSide.displaceBy(dx,dy);
			
			//against all of our sides			
			if (isLineTouching(testThisSide.startPoint,testThisSide.endPoint)){
				wasTouching = true;
				break;
			}						
		}	
		
		//add to cache
		if (wasTouching){
			knownPolygons.put(testThis, knownstate.KnownAsTouching);
		} else {
			knownPolygons.put(testThis, knownstate.KnownAsNotTouching);
		}
		
		return wasTouching;
		
	}
	

	public boolean isLineTouching(Simple3DPoint linestart,Simple3DPoint lineend) {
		

		int ourTotalSides = numberOfVertexs();
		
		int j=0;
		for (j = 0; j < ourTotalSides; j++) {
			
			//we have to ensure non-real waypoints are skipped.
			//
			//When getting a side, waypoint (j) >> waypoint (j+1) represents the side 
			//Its possible for a side to end in a z, but not start with one.
			//Therefor we check the waypoint #j to see if its real or not, but dont test j+1 
			if (!this.get(j).isRealWayPoint(true)){
				continue; //skip it
			}

			PolySide testagainst = getSide(j,false);
			
			if (lineIntersect2d(testagainst.startPoint, testagainst.endPoint,linestart,lineend,false) != null){
				return true;
			}
			
				
		}
		
		return false;
		
	}
	
	/**
	 * detects if a point is inside this polygon
	 * 
	 * Based on http://alienryderflex.com/polygon/ (could one day be upgraded to support curves)	 * 
	 **/
	public boolean isPointInside(float x, float y) {
		
		

		//works by counting vertexs above and below a line (i think)
		//if there's an even its inside if not its outside (i think)
		//maths!

		//	Log.info("testing point:" + x + "_" + y);

	//	int polySides = this.numberOfVertexs(); //old
		
		int polySides = this.size();

		//	Log.info("Vertexes = point:" + polySides);

		int i,j = this.size()-1;//this.numberOfVertexs() - 1; //NOTE: We are including the loop waypoints (z) co-ordinates in this.
		//Requesting a loop causes it to return the last M instead. (ie, the last segment start) 

		boolean oddNodes = false;
		
		String coordTestedi = "";
		String coordTestedj = "";
		
		/** if its odd then its outside **/
		for (i = 0; i < polySides; i++) {
			
			//we have to check if the current waypoint is real
			//if not we add one, and loop around.
			while (!get(i).isRealWayPoint(false)){		
				Log.info("skipping i: = :" + i+" x="+getWPX(i)+",y="+getWPY(i)+get(i).type);				
				i++;				 
			}
			//same with j
			while (!get(j).isRealWayPoint(false)){		
				Log.info("skipping j: = :" + j+" x="+getWPX(j)+",y="+getWPY(j)+get(j).type);	
				j++;				 
			}
						
			//Log.info("i: = :" + i+" j="+j+"_("+get(j));	
			
		//	coordTestedi = coordTestedi + " "+getWPX(i)+","+getWPY(i)+"\n";
			//coordTestedj = coordTestedj + " "+getWPX(j)+","+getWPY(j)+"\n";
			
			
			//more maths....seriously...umm...just read the website
			if (getWPY(i) < y && getWPY(j) >= y
					|| getWPY(j) < y && getWPY(i) >= y) {
				if (getWPX(i) + (y - getWPY(i))
						/ (getWPY(j) - getWPY(i))
						* (getWPX(j) - getWPX(i)) < x) {

					oddNodes = !oddNodes;

				}
			}
			j = i;
		}
		
		//Log.info("coordTestedi:\n" + coordTestedi);
		//Log.info("coordTestedj:\n" + coordTestedj);
		
		return oddNodes;
	}


	/** gets the specified outset waypoints x co-ordinate.
	 * The outset points are all precalculated for speed, hence no option on distance **/
	public int getOutsetWaypointX(int i) {
		if (outsetPath!=null){
			
			//outset paths should loop at exact same place as normal paths			
			if (get(i).type==MovementType.AbsoluteLoopToPathStart){
				Log.info("Requested outer loop at:"+i+" thus returning last segment start position "+get(i).getWaypointNumberForLastSegmentStart());			
				i=get(i).getWaypointNumberForLastSegmentStart();
			}
			
			if (associatedObject == null) { //sourceMap.associatedObject

				return outsetPath.get(i).pos.x;

			} else {

				return outsetPath.get(i).pos.x + associatedObject.getTopLeftBaseX(); //sourceMap.associatedObject

			}

		} else {
			return getWaypointX(i);
		}
	}
	/** gets the specified outset waypoints y co-ordinate **/
	public int getOutsetWaypointY(int i) {
		if (outsetPath!=null){
			
			//outset paths should loop at exact same place as normal paths
			if (get(i).type==MovementType.AbsoluteLoopToPathStart){
				Log.info("Requested outer loop at:"+i+" thus returning last segment start position "+get(i).getWaypointNumberForLastSegmentStart());			
				i=get(i).getWaypointNumberForLastSegmentStart();
			}
			
			if (associatedObject == null) {//sourceMap.associatedObject

				return outsetPath.get(i).pos.y;

			} else {

				return outsetPath.get(i).pos.y + associatedObject.getTopLeftBaseY();//sourceMap.associatedObject

			}

		} else {
			return getWaypointY(i);
		}
	}


	/** gets the waypoint x as a float value - convience method that just does a typecast for you **/
	private float getWPX(int i){
		return (float)getWaypointX(i);
	}

	/** gets the waypoint y as a float value - convince method that just does a typecast for you **/
	private float getWPY(int i){
		return (float)getWaypointY(i);
	}



	public int getWaypointX(int i) {
		return this.getWaypointX(i,false);
	}

	/** gets the specified waypoints x co-ordinate.
	 * Note; if you request a waypoint representing a loop(z) it returns the last segment start instead (m) **/
	public int getWaypointX(int i,boolean overrideDisplacement) {

		if (get(i).type==MovementType.AbsoluteLoopToPathStart){
		//	Log.info("Requested loop at:"+i+" thus returning last segment start position "+get(i).getWaypointNumberForLastSegmentStart());			
			i=get(i).getWaypointNumberForLastSegmentStart();
		}
		
		if (associatedObject == null || overrideDisplacement) {//sourceMap.associatedObject

			return this.get(i).pos.x;

		} else {

			return this.get(i).pos.x + associatedObject.getTopLeftBaseX(); //sourceMap.associatedObject

		}
	}

	public int getWaypointY(int i) {
		return this.getWaypointY(i,false);
	}

	/** gets the specified waypoints y co-ordinate 
	 * 
	 *  * Note; if you request a waypoint representing a loop(z) it returns the last segment start instead (m) **/
	public int getWaypointY(int i,boolean overrideDisplacement) {
		if (get(i).type==MovementType.AbsoluteLoopToPathStart){
		//	Log.info("Requested loop at:"+i+" thus returning last segment start position "+get(i).getWaypointNumberForLastSegmentStart());
			
			i=get(i).getWaypointNumberForLastSegmentStart();
		}
		
		if (associatedObject == null || overrideDisplacement) { //sourceMap.associatedObject

			return this.get(i).pos.y;

		} else {

			return this.get(i).pos.y + associatedObject.getTopLeftBaseY();//sourceMap.associatedObject

		}
	}


	/**
	 * tries to find the nearest point on this polygons edge to the dx,dy specified.
	 * Assuming those points are inside this polygon.
	 * We return a spiffypolygon collision, that stores both the nearest point, and also the polygon and side it lands on
	 *
	 * @param tx
	 * @param ty
	 * @return
	 */
	public SpiffyPolygonCollision getNearestOutsidePoint(int tx, int ty,boolean ignoreOutsideScreen) {

		//loop over sides
		int sidecount = numberOfVertexs();
		double lastdistance = 500000; //artibitary max dis
		SpiffyPolygonCollision nearestpoint = null;

		double distance = 0 ;
		for (int i = 0; i < sidecount; i++) {

			PolySide side = this.getSide(i);

			MovementWaypoint point = side.nearestPointOnThisTo(tx,ty);

			Log.info("visually logging points");			
			//draw dot at this point (temp debug)
			if (SceneObjectDatabase.currentScene.scenesCmap.isPresent()){
				SceneObjectDatabase.currentScene.scenesCmap.get().addToSketch("M "+point.pos.x+","+point.pos.y+" L "+(point.pos.x+1)+","+point.pos.y,"ORANGE");
			}
			
			//track shortest
			distance = Math.hypot(tx-point.pos.x, ty-point.pos.y);

			if (distance<lastdistance){

				//ensure we are within the screen limits so we skip this point as we are outside
				if (ignoreOutsideScreen && pointIsOutsideStageLimits(point.pos.x,point.pos.y) ){
					continue;

				}

				lastdistance=distance;
				nearestpoint =  new SpiffyPolygonCollision(point.pos.x, point.pos.y,this, side, (int) distance);;
			}
		}


		return nearestpoint;
	}
	/**
	 * checks if a point is outside the stage limits.
	 * Currently calls the currentScene.
	 * In future we might want to change this to let this class be more independent.
	 * We can have the stages topleft and bottomright fed to a static variable and use that instead.
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	static private boolean pointIsOutsideStageLimits(int x, int y) {

		if (x<0){
			return true;
		}
		if (y<0) {
			return true;
		}
		if (x>SceneObjectDatabase.currentScene.getScenesData().InternalSizeX) {
			return true;
		}
		if (y>SceneObjectDatabase.currentScene.getScenesData().InternalSizeY) {
			return true;
		}


		return false;
	}
	
	
	
	enum knownstate {
		KnownAsTouching,
		KnownAsNotTouching
	}
	/**
	 * we keep a record of all tte polygons we know are either touching or not
	 * if its not on this list, then we need to work it out
	 * 
	 */
	HashMap<Polygon,knownstate> knownPolygons = Maps.newHashMap();
	
	/**
	 * once we have a touching cache, this should clear it
	 * ready to be recreated next time its used.
	 * 
	 * Ideally this should be run when a object first starts moving.
	 */
	public void clearKnownPolygonCache() {
		knownPolygons.clear();	
	}
	
	
	/**
	 * updates the knownPolygons cache using the specified pool of polygons
	 * Anything touching will be stored as KnownAsTouching enum, 
	 * Anything not touching will be stored as KnownAsNotTouching enum
	 * 
	 */
	public void  rebuildCacheFromPool( ArrayList<Polygon> pool ) {
		//remove all from pool already (because they might already be known but with a out of date state)
		
		
		for (Polygon polygon : pool) {
			knownPolygons.remove(polygon);
			
			//we simply test it, as this will trigger the cacheing
			this.isPolygonTouching(polygon);
			
		}
		
		
	}
	/**
	 * removes all mention of these polygons from all the caches
	 * @param resetthese
	 */
	public static void removeFromAllcaches( ArrayList<Polygon> resetthese) {
		
		for (Polygon polygon : allPolygons) {
			
			for (Polygon polytoremove : resetthese) {
				polygon.knownPolygons.remove(polytoremove);
			}
		}
		
	}
	
	
	/**
	 * returns a arraylist of all the "bottom" edges of this polygon
	 * ie. Those with a normal pointing in a positive Y direction if Y co-ordinates went top to bottom of the screen.
	 * 
	 * @return
	 */
	public ArrayList<PolySide> getAllLowerEdges(){
		 
		ArrayList<PolySide> set = new  ArrayList<PolySide>();
		 
		for (int i = 0; i < this.numberOfVertexs(); i++) {
		
			PolySide side = this.getSide(i);			
			SimpleVector3 normal = side.getNormal(false);
			
			if (normal.y>0.01){ //slightly above zero as we dont want total vertical lines to count
				set.add(side);
			}
			
		}
		
		return set;		
	}
	
	/**
	 * returns a arraylist of all the "bottom" edges of this polygon
	 * ie. Those with a normal pointing in a positive Y direction if Y co-ordinates went top to bottom of the screen.
	 * 
	 * @return
	 */
	public ArrayList<PolySide> getAllUpperEdges(){
		 
		ArrayList<PolySide> set = new  ArrayList<PolySide>();
		 
		for (int i = 0; i < this.numberOfVertexs(); i++) {
		
			PolySide side = this.getSide(i);			
			SimpleVector3 normal = side.getNormal(false);
			
			if (normal.y<-0.01){ //slightly above zero as we dont want total vertical lines to count
				set.add(side);
			}
			
		}
		
		return set;		
	}
	
	
	

}
