package com.lostagain.Jam.CollisionMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import com.google.common.base.Optional;
import com.lostagain.Jam.Interfaces.IsCollisionLogger;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;
import com.lostagain.Jam.Scene.SceneWidget;
import com.lostagain.Jam.SceneObjects.CollisionBox;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs;
import com.lostagain.Jam.SceneObjects.CollisionModeSpecs.CollisionType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.SceneObjectDatabase;

/** manages the scenes collisions with all its objects, as well as its own map. **/
public class SceneCollisionMap {

	public static Logger Log = Logger.getLogger("JAMCore.SceneCollisionMap");

	
	/** 
	 * The scenes collision map. 
	 * Its basically a collection of polygons 
	 ***/
	public PolygonCollisionMap scenesOwnMap;

	/** the last collision that happened **/
	public PolygonCollisionMap lastCollision = null;

	


	/** the calculated path after pathfinding **/
	//HTML svgCalculatedPath = new HTML();

	/** used to display a svg of the refined path to help debug it **/
	//HTML svgRefinedPath = new HTML();

	/** preview widget - helps with debugging by visualizing the map**/
	//public AbsolutePanel previewRoot = new AbsolutePanel();

	/** svg preview layer, which display the scenes collision map **/
	//HTML svgPreview = new HTML();

	/** svg sketch layer, all the current paths are displayed on this  **/
	//HTML svgSketchPad = new HTML("");

	/** mains all the current path strings **/
	//HashSet<String> PathList = new HashSet<String>();

	/** panel we put the svgs on **/
	//public AbsolutePanel svgLayer = new AbsolutePanel();



	public Optional<? extends IsCollisionMapVisualiser> CMapVisualiser = Optional.absent();// Optional.of(new CollisionMapVisualiser());


	public void setCMapVisualiser(IsCollisionMapVisualiser cMapVisualiser) {
		CMapVisualiser = Optional.fromNullable(cMapVisualiser);



		if (CMapVisualiser.isPresent()){
			//fill svg layer
			CMapVisualiser.get() .setupVisualiser(sourcescene,scenesOwnMap);
			//generate used to be before setup
			CMapVisualiser.get() .generatePreviewWidget();
		}
	}


	/** the global log of collision, helps debug collisions **/
	//public static CollisionDebugBox CollisionLog; //needs to be refractored out or made into a interface
	//also assign the logger in same place as CMapVisual
	public static Optional<? extends IsCollisionLogger> CollisionLogger = Optional.absent();


	public static void setCollisionLogger(IsCollisionLogger collisionLogger) {
		CollisionLogger =  Optional.fromNullable(collisionLogger);
	}

	/** the scene this collision map is one **/
	SceneWidget sourcescene = null;

	//private int SearchIterator = 0;


	/** default collision margin only - should be set for the real object being checked **/
	private int collision_margin = 32; // this should be set to match the
	// objects width

	//used to stop infinite loops when it all goes wrong
	private int maxpathNodes = 15;
	private static int totalIterationLimit = 30;

	//This should be per-pathfinding not global to all of them on the scene;
	/**
	 * This is the current number of iterations the current pathfinding for this object has gone though.
	 * This is purely used to place a cap on pathfinding - allowing "emergency exits" when it gets too big.
	 * (depending on the objects pathfindingmode setting, this could result in no movement at all, or it simply
	 * moving though the objects to reach its destination anyway)
	 */
	//public int objectsCurrentPathfindingIterator = 0; //reset each time this is called


	//TODO: when refine used paths often mess up
	static boolean refineDisabled=false; //if path smoothing is enabled

	public static boolean isRefineDisabled() {
		return refineDisabled;
	}





	/**
	 * If set to true, pathfinding routes arnt optimised
	 * @param refineDisabled
	 */
	public static void setRefineDisabled(boolean refineDisabled) {
		SceneCollisionMap.refineDisabled = refineDisabled;
	}

	/** defines a search direction- ie, going around an object clockwise or anticlockwise
	 * it should be checking both by default, but once it start going around one way, that particularly 
	 * check keeps going around that a way.
	 * (else it will attempt to take both paths for each corner of the object, really multiplying workload, when it should only take both parts after it first hits **/
	public enum searchdirection {
		clockwise, anticlockwise, both
	}

	enum collisionside {
		left,right,center;
	}

	/**
	 * Provides both a vector collision map for the scene, and functions to test
	 * for collisions
	 **/
	public SceneCollisionMap(String mapData, SceneWidget sourcescene) {

		//general setup functions

		//create a new collision log if one doesn't exist already
		//in future we need to split this off so its implementation can come from outside the core
		//if (SceneCollisionMap.CollisionLog==null){
		//	SceneCollisionMap.CollisionLog = new CollisionDebugBox(sourcescene);
		//}		
		//if (!CollisionLogger.isPresent()){
		//	CollisionLogger = Optional.of(new CollisionDebugBox(sourcescene)); //need to be split of somewhere outside core
		//}	


		this.sourcescene = sourcescene;

		CollisionLogInfo("creating cmap...............................");

		//create the collision map for this scene from the map data supplied
		scenesOwnMap = new PolygonCollisionMap(mapData, null,sourcescene.SceneFileName+"_Poly");
		
		//fill svg layer
		//	CMapVisualiser.setupVisualiser(sourcescene,scenesOwnMap);
		//generate used to be before setup
		//	CMapVisualiser.generatePreviewWidget();

		//if (this.CMapVisualiser.isPresent()){
		//fill svg layer
		//	CMapVisualiser.get() .setupVisualiser(sourcescene,scenesOwnMap);
		//generate used to be before setup
		//	CMapVisualiser.get() .generatePreviewWidget();
		//}
	}





	/** given start and end points of a path, as well as the bottom right of a objects bounding box and its width,
	 * this will return the first collision it finds along that path.
	 * Its best to imagine it detecting the lower line of a box - which is the box was a sprite
	 * standing, would be either side of the characters feet.
	 * This method lets a 2D scene have the illusion of 3d by having characters able to stand in front of something,
	 * with only their feet limiting their movement. Remember collision maps should just reflect
	 * Collisions at floor level for this reason **/
	public SpiffyPolygonCollision findFirstCollision(int dx, int dy, int x,
			int y, Simple2DPoint BottomLeftofBB, int Width) {

		// this is a crude test, that first tests the middle (actual) path, then
		// it tests the left corner, then right corner.
		// if any of these collides, it returns the closest collision

		CollisionLogInfo("testing objects for scene collision.......");

		// start and end used to be transposed
		Simple3DPoint end = new Simple3DPoint(dx, dy,0);
		Simple3DPoint start = new Simple3DPoint(x, y,0);

		// start and end used to be transposed
		Simple3DPoint brend = new Simple3DPoint(dx + BottomLeftofBB.x + Width, dy,0);
		Simple3DPoint brstart = new Simple3DPoint(x + BottomLeftofBB.x + Width, y,0);
		//CollisionLogInfo("bottom right start is...................." + brend.x + ","
		//		+ brend.y);

		addToSketch("M " + brstart.x + "," + brstart.y + " L " + brend.x + ","
				+ brend.y, "black");

		// start and end used to be transposed
		Simple3DPoint blend = new Simple3DPoint(dx + BottomLeftofBB.x, dy,0);
		Simple3DPoint blstart = new Simple3DPoint(x + BottomLeftofBB.x, y,0);

		CollisionLogInfo( "<---bottom right target:"+ brend.x + ","+brend.y+" bottomleft target: " + blend.x + ","
				+ blend.y);

		addToSketch("M " + blstart.x + "," + blstart.y + " L " + blend.x + ","
				+ blend.y, "white");

		//this stores the collision side, assuming there's a collision at all		
		//as explained above we test left,center and right
		//by default its center, but it could be anything
		collisionside CollisionSide = collisionside.center; 


		//-----------------------First we test the scenes own map for collisions
		//-----------------------------------------------------------------------

		//we get the iterator of all the polygons for the scenes collision map
		Iterator<Polygon> sit = scenesOwnMap.iterator();

		SpiffyPolygonCollision pc = null;//will store the collision, if there's one

		//loop over all those polygons testing them!
		while (sit.hasNext()) {

			Polygon polygon = (Polygon) sit.next();

			//test the middle path for collisions
			SpiffyPolygonCollision nc = polygon.testForCollision(start, end);

			if (nc != null) {

				//if theres no collision yet or the distance to the new collision
				//is less then the previous one found, then we keep this new collision
				if (pc == null || nc.distance < pc.distance) {
					pc = nc;

					lastCollision = nc.collidingObject.sourceMap;
					CollisionSide = collisionside.center;
				}
			}

			// test left lower line for collisions
			SpiffyPolygonCollision nc_left = polygon.testForCollision(blstart,		blend);

			if (nc_left != null) {

				//if theres no collision yet or the distance to the new collision
				//is less then the previous one found, then we keep this new collision				
				if (pc == null || nc_left.distance < pc.distance) {
					pc = nc_left;

					lastCollision = nc_left.collidingObject.sourceMap;
					CollisionSide = collisionside.left;
				}
			}

			// test right lower line for collisions
			SpiffyPolygonCollision nc_right = polygon.testForCollision(brstart,
					brend);

			if (nc_right != null) {

				//if theres no collision yet or the distance to the new collision
				//is less then the previous one found, then we keep this new collision

				if (pc == null || nc_right.distance < pc.distance) {
					pc = nc_right;

					lastCollision = nc_right.collidingObject.sourceMap;
					CollisionSide = collisionside.right;
				}
			}

		}


		//--------------------------------now  we test all the objects on the scene for collisions too (in basically the same way as the above)
		//---------------------------------------------------------------------------
		Iterator<SceneObject> soit = sourcescene.getScenesData().scenesOriginalObjects //hmm...we seem to only be testing the original objects. What is sme new ones are created dynamicly. dont we want to test clones too?
				.iterator();


		while (soit.hasNext()) {

			SceneObject so = (SceneObject) soit.next();

			// Log.info("testing ......" + so.objectsCurrentState.ObjectsName);

			if (so.cmap.isPresent()) {
				
				Iterator<Polygon> cit = so.cmap.get().iterator();

				while (cit.hasNext()) {
					Polygon polygon = (Polygon) cit.next();

					//skip if not visible or incoporal
					if (polygon.incorporeal){
						continue;
					}
					//if(!polygon.sourceMap.defaultAssociatedObject.isVisible()){ //added this was missing for some reason

					if (!polygon.associatedObject.isVisible()){	
						continue;
					}

					SpiffyPolygonCollision nc = polygon.testForCollision(start,	end);

					if (nc != null) {
						if (pc == null || nc.distance < pc.distance) {

							CollisionLogInfo("new collision = ......" + nc.X+","+nc.Y+"("+nc.distance+")");
							pc = nc;

							lastCollision = nc.collidingObject.sourceMap;
							CollisionSide = collisionside.center;
						}
					}

					// test left lower line
					SpiffyPolygonCollision nc_left = polygon.testForCollision(
							blstart, blend);

					if (nc_left != null) {

						// addToSketch("M "+nc_left.X+","+nc_left.Y+" l "+0+","+8,
						// "purple");

						if (pc == null || nc_left.distance < pc.distance) {
							pc = nc_left;

							lastCollision = nc_left.collidingObject.sourceMap;

							addToSketch("M " + pc.X + "," + pc.Y + " l " + 8
									+ "," + 0, "purple");

							CollisionSide = collisionside.left;
						}
					} else {
						//Log.info("<________________________________________________no left collision found for:"
						//	+ so.objectsCurrentState.ObjectsName);
					}

					// test right lower line
					SpiffyPolygonCollision nc_right = polygon.testForCollision(
							brstart, brend);

					if (nc_right != null) {
						if (pc == null || nc_right.distance < pc.distance) {
							pc = nc_right;

							lastCollision = nc_right.collidingObject.sourceMap;
							CollisionSide = collisionside.right;

							addToSketch("M " + pc.X + "," + pc.Y + " l " + 8
									+ "," + 0, "cyan");

						}
					}

				}
			}

		}

		// if its not collisionside.center (which represents the normal central movement point),
		// then we adjust the collision to represent
		// that point, so resulting position still makes sense
		if (CollisionSide != collisionside.center) {

			CollisionLogInfo("non central collision");

			if (CollisionSide == collisionside.left) {

				addToSketch("M " + pc.X + "," + pc.Y + " l " + 5 + "," + 0,
						"purple");

				//adjust for bottom left
				pc.X = pc.X - BottomLeftofBB.x;
				pc.Y = pc.Y + BottomLeftofBB.y;

				CollisionLogInfo("adjusted left collision:" + pc.X + "," + pc.Y);

				addToSketch("M " + pc.X + "," + pc.Y + " l " + 5 + "," + 0,
						"purple");
			}

			if (CollisionSide == collisionside.right) {

				addToSketch("M " + pc.X + "," + pc.Y + " l " + 5 + "," + 0,
						"cyan");

				pc.X = pc.X - (Width + BottomLeftofBB.x); // _______(-7)________-________
				pc.Y = pc.Y + BottomLeftofBB.y; // 10

				addToSketch("M " + pc.X + "," + pc.Y + " l " + 5 + "," + 0,
						"orange");

				CollisionLogInfo("adjusted right collision:" + pc.X + "," + pc.Y);

			}

		}

		return pc;

	}

	public void addToSketch(String sketchSVGPath, String color) {
		addToSketch(sketchSVGPath,color,true);
	}
	
	
	
	public void addToSketch(String sketchSVGPath, String color,boolean rawPathSupplied) {

		//CMapVisualiser.addToSketch(sketchSVGPath, color);

		if (this.CMapVisualiser.isPresent()){
			CMapVisualiser.get().addToSketch(sketchSVGPath, color,rawPathSupplied);
		}

	}



	public void clearSketch() {
		//CMapVisualiser.clearSketch();
		if (this.CMapVisualiser.isPresent()){
			CMapVisualiser.get().clearSketch();
		}
	}





	/** more simple method that takes no account of width **/
	public SpiffyPolygonCollision findFirstCollision(int dx, int dy, int x,	int y) {
		
		

		// iterate over all objects testing for collision, then get the nearest
		// to the starting point

		// Log.info("testing objects for scene collision.......");
		Simple3DPoint start = new Simple3DPoint(dx, dy,0);
		Simple3DPoint end   = new Simple3DPoint(x, y,0);

		Iterator<Polygon> sit = scenesOwnMap.iterator();
		SpiffyPolygonCollision pc = null;

		while (sit.hasNext()) {

			Polygon polygon = (Polygon) sit.next();

			SpiffyPolygonCollision nc = polygon.testForCollision(start, end);

			if (nc != null) {
				if (pc == null || nc.distance < pc.distance) {
					pc = nc;

					lastCollision = nc.collidingObject.sourceMap;
				}
			}
		}

		// Log.info("testing objects for collision.......");

		// now all the objects
		Iterator<SceneObject> soit = sourcescene.getScenesData().scenesOriginalObjects.iterator();


		while (soit.hasNext()) {

			SceneObject so = (SceneObject) soit.next();
			//ignore if objects invisible
			if (!so.isVisible()){
				continue;
			}


			if (so.cmap.isPresent()) {

				//	 Log.info("Testing ......" + so.getObjectsCurrentState().ObjectsName+" against "+start.toString()+"-"+end.toString());

				Iterator<Polygon> cit = so.cmap.get().iterator();

				while (cit.hasNext()) {
					Polygon polygon = (Polygon) cit.next();

					//ignore if incoporal 
					if (polygon.incorporeal){

						Log.info("skipping incoporal polygon");
						continue;
					}


					SpiffyPolygonCollision nc = polygon.testForCollision(start,	end);

					if (nc != null) {
						Log.info("cf");

						if (pc == null || nc.distance < pc.distance) {

							CollisionLogInfo("new collision = ......" + nc.X+","+nc.Y+"("+nc.distance+")");
							pc = nc;

							lastCollision = nc.collidingObject.sourceMap;
						}
					}

				}
			}

		}

		return pc;
	}

	/** Ok this is the big complex process that works out a path around objects!<br>
	 * When given the required parameters it trys to research your destination, taking a path<br>
	 * around 1 or more objects on the cmap if necessary, and once it has found route it adds it<br> 
	 * to the supplied existing path (np)<br>
	 * The search direction is if its going left or right around an object it finds. When a path
	 * is first asked for, this should be set to BOTH......<br>
	 * How it works:<br>
	 * <br>
	 * First it tries a straight line from its last position on the supplied path (np) to the destination<br>
	 * dx and dy.<br>
	 * When it finds a collision, it takesa few steps back from the object it hit, storing it in lastCollision.<br>
	 * It then looks at its SearchDirection. If SearchDirection is on "both" it then splits into a quantom superposition! oww...wow! science.<br>
	 * Well, actually it takes both paths at once. That is, it tries to go around the object anticlockwise, and at the same time tries to go around it<br>
	 * clockwise.<br>
	 * So once split in two, both possibility's are looked at. It will proceed to, for example, the next clockwise corner of the object it hit. Once<br>
	 * at the corner it tries to goto its destination again (by re-running this command). Same for the other corner.<br>
	 * If it fails, it keeps around around the object in the same direction till it can form a line to its destinition - or at least, move forward a<br>
	 * bit and crash into a different object. <br>
	 * Once one possible route reaches the definition it waits for the other possible routes to be checked.<br>
	 * Once all the possible routes are found, the distances are compared at the shortest one used as the real route.<br>
	 * <br>
	 * If any route starts taking too long, its eventually gives up and passs though the solid object, flagging as an error. (pathFindingBroke = true)<br>
	 * Given a choice, no routes with an error are chosen as the one to use.<br>
	 * <br>
	 * If at any time a path being checked comes across a different object (that is, its navigated around one but crashs into another)<br>
	 * the process starts again, resetting the search direction to "both" - thus checking both the clockwise and anticlockwise paths for this new object as well.<br>
	 * This can thus result in many more then two paths being checked, as each collision on route multiplys the possible routes by 2.<br>
	 * <br>
	 * <br>
	 * @author Thomas Wrobel
	 * <br>
	 * <br>
	 * @param dx - destinations X position
	 * @param dy - destinations Y position
	 * @param np - the movement path the new route will be added onto
	 * @param direction - the search direction, that is, does it goe around objects clockwise,anti-clockwise or both. When calling this command it should be both!
	 * @param object - the object that will move along this path (used to get its width)
	 * @param lastCollision - the last collision that happened when calculating the path. When calling this function it should be set to null, if this function needs to call itself it will set it to the last collision that happened. It does this so it can tell if its colliding with a new object <br>
	 * @return
	 */
	public MovementPath findPathBetweenAndAddToExistingPath(int dx, int dy,
			MovementPath np, searchdirection direction, SceneObject object, SpiffyPolygonCollision lastCollision) {

		// get start location from paths end
		int sx = np.get(np.size() - 1).pos.x;
		int sy = np.get(np.size() - 1).pos.y;
		
		//quick check we arnt moving to where we are already
		if (sx==dx && sy==dy){
			Log.info("Requested new destination same as current one ("+sx+","+sy+"), adding redundant node without collision check");
			np.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo));
			return np;
		}
		

		// set collision as half the width of the object and a bit
		// in future this should be the objects bounding box ...and a bit
		// additional note; if the pin is off center horizontally this might not
		// be enough
		collision_margin = (object.getPhysicalObjectWidth() / 2) + 15;

		// get displacement for width checking
		//This is the lower left corner of the object relative to its position pin
		Simple2DPoint lowerLeft = object.getLowerLeftDisplacement();

		// pathfinding goes here

		// test if it can reach destination in a straight line without collision
		// if so add endpoint and end

		// this method broke..why? umm...seems to work now...co co co
		SpiffyPolygonCollision fc = findFirstCollision(dx, dy, sx, sy,	lowerLeft, object.getPhysicalObjectWidth());

		boolean newObject=true; //did we hit a new object? 
		
		if (fc != null) {

			//we do a lot of collision log updating, because a lot of information is needed to debug
			CollisionLogInfo("__collision:");
			CollisionLogInfo("__location of hit:" + fc.X + "," + fc.Y
					+ "  distance:" + fc.distance + " ");
			//if there's an associated object with what we collide with
			//in most circumstances there will be - unless wwe are colliding with the scenes own map
			if (fc.collidingObject.associatedObject != null) { //sourceMap.defaultAssociatedObject 

				//log the object we hit
				CollisionLogInfo("__with object: "
						+ fc.collidingObject.associatedObject.getObjectsCurrentState().ObjectsName);//sourceMap.defaultAssociatedObject 

				//reset the direction to bidirectional check
				//this really should only happen if the collision is a different object from the last
				if (lastCollision!=null){
					//if the object we just hit is different to the object we hit before we flag to start checking both directions again
					//imgine if its currently going around the corners of an object - at each corner is checks if it can goto its destination
					//if it draws a line from the corner and hits the same object as before *that means its navigating around the same object still*
					//if it draws a line and hits a different object, it means it found a path around one object, but just hit another.
					if (lastCollision.collidingObject != fc.collidingObject){
						direction = searchdirection.both;
						newObject=true;
						CollisionLogInfo("__direction reset to bidirection check_");
					} else {
						newObject=false; //no we didnt
						CollisionLogInfo("__colliding object hasnt changed_"); //so we continue going around whatever direction we are going around already
					}
				}
			} else {
				CollisionLogInfo("__with poly:"+fc.collidingObject.getName());
				
			}
			//set the last collision variable to the current collision
			lastCollision=fc;
		}
		/*
		// this method old method always works but is inaccurate, and was replaced with the above
		fc = findFirstCollision(sx, sy, dx, dy);

		if (fc != null) {
			Log.info("normal col:" + fc.X + "," + fc.Y + "  " + fc.distance);

			if (fc.collidingObject.sourceMap.associatedObject != null) {
				Log.info("object name:"
						+ fc.collidingObject.sourceMap.associatedObject.objectsCurrentState.ObjectsName);
			}
		}
		 */

		//If there was no collision found above, (ie, fc==null) we simply add a straight line
		//to out requested destination onto the path!
		if (fc == null) {

			//add a straight line from the current location to the destination onto the path
			np.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo));
			//update the visual display of the path
			updatePath(np,this.lastCollision);
			//exits this function
			return np;

		} else {

			// if theres a collision we first check we havnt been pathfinding too long,
			// The total number of nodes in the path (np.size) has to be less then the maxpathNodes variable
			// Also, the currentIteration has to be less then the total iteration limit.
			// An iteration is each time this function has to call itself - which happens each time a collision is found
			// this stops too many "multiple realitys" of the path splitting into more and more superpositions 
			//as it tries many cw and acw routes at the same time.
			//in almost all scenes this should not be possible to happen, but the check is here because it causes a nasty crash if it does
			if ((np.size() < maxpathNodes)&&(object.objectsCurrentPathfindingIterator<totalIterationLimit)) {
				object.objectsCurrentPathfindingIterator++;

				// first we move backwards a bit before getting
				// paths
				// for both clockwise and anticlockwise routes around object

				CollisionLogLog("currently path has _"+np.size()+"_ nodes","grey");
				
				Simple2DPoint nsp = getPositionFromEndOfPath(sx, sy, fc.X, fc.Y,
						collision_margin);//move back from where we hit the object

				CollisionLogLog("moved back from collision at "+fc.X+","+fc.Y+"  to  "+nsp,"grey");
				
				
				

				//if new waypoint is same as previous we exit here as clearly this path is bad.
				//Typically this means the path has ending up banging into itself, 
				//possibly caused by extream concave bits
				if ((np.get(np.size()-1).equalsLocation(nsp))&& (newObject==false)){
					CollisionLogLog("same location ("+nsp+"), so we abort this path","FF0000");

					return abortCurrentPath(dx, dy, np);
				}

				//what if this new point collides? (like we started in a tight space and reversed into something
				if (this.isPointColliding(nsp.x, nsp.y)!=null){
					
					CollisionLogLog("but this spot collides! (so not using it)","red");
					//we thus dont add the new point spaced back, as it collides
					
				} else {
				
				//ok, before we start working around the objects edge,
				//we store the point that we moved back too (from the "getpositionFromEndofPath")
				//without storing this point, we would take a diagonal to the first corner from the starting point
				//which while in some cases that may seem more efficient a path - it could also hit something new!
				//so at this point we are cautious and put this extra waypoint in.
				//remember the RefinePath function can later be used to smooth out unnecessary points if no collision is caused by removing them
				np.add(new MovementWaypoint(nsp.x, nsp.y, MovementType.AbsoluteLineTo)); 
				}
				
				CollisionLogLog("currently path has _"+np.size()+"_ nodes","grey");
				CollisionLogLogPath(np);

				//store details of the collisions object
				//Specifically we store what polygon on the object it hit as well as what side it was on
				fc.collidingObject.sourceMap.currentPoly = fc.collidingObject.sourceMap.indexOf(fc.collidingObject);

				//the side number if the start vertex number of the sides line. (each line is of course, two vertexs, we number the sides the same as its start vertex)
				if (fc.side!=null){
					fc.collidingObject.sourceMap.currentSide = fc.side.startVertexNumber;
				} //note: The side can be null if the collision was completely within the polygon (ie, the line we drew is fully enclosed by it, rather then hitting a side)
				else {
					
					Log.info("_-~ fc side is null. This means path route was fully encased by a collision map ~-_");
					SceneCollisionMap.CollisionLogError("fc side null at:"+fc.toString());
					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
				}
				// Log.info("___________________________________________________cp="+fc.collidingObject.sourceMap.currentPoly+"("+fc.collidingObject.sourceMap.associatedObject.objectsCurrentState.ObjectsName+")");
				// Log.info("___________________________________________________cs="+fc.collidingObject.sourceMap.currentSide);

				//clear the debug sketch
				clearSketch();

				//Ok now we split into a quantom super position!
				//We make a new path for cw and acw:
				MovementPath possibilityOne = new MovementPath("","_internal_anticlockwise");

				MovementPath possibilityTwo = new MovementPath("","_internal_clockwise");

				// get next free point going anti-clockwise
				if (!(direction == searchdirection.clockwise)) {

					//This function will walk around the object we hit going clockwise
					//it will go from corner to corner. 
					//At each corner it tests if it can keep moving towards the finnal destination again without bumping 
					//into this object.
					//if it cant it keeps going
					//when it can..when its "free" to keep heading to its destination, then it returns
					//and stores this path as "possibilityTwo"
					possibilityTwo = walkAroundAntiClockwise(dx, dy, np, fc,object,0); //TODO:FC.side can be null here in GDX version...why?

					CollisionLogInfo("_______________possibilityTwo length:" + possibilityTwo.PathLength + "(acs)broke:"+possibilityTwo.pathFindingBroke);
					CollisionLogLogPath(possibilityTwo);

					//we refine the path here - this removes any waypoint nodes that arnt needed, testing to ensure no new collisions are generated
					//from removing them.
					possibilityTwo = this.refinePath(possibilityTwo,object);

					CollisionLogInfo("__________________path after refine:(cs)");
					CollisionLogLogPath(possibilityTwo);	
					// Log.info("____________________________________possibilityTwo length after refine:"+possibilityTwo.PathLength
					// + "(cs)");

					///now we have the new path, we call this whole function again with it, specifying to keep going clockwise unless a new object is hit
					//In some ways this search direction default probably not necessary, as the "walkaround" should have already stopped at a point where
					//it can get to a new object. Still, its there just in case its still going to end up going around this same object.

					possibilityTwo = findPathBetweenAndAddToExistingPath(dx,
							dy, possibilityTwo, searchdirection.anticlockwise,
							object,lastCollision);

				}

				// now, as well as the above, we try anticlockwise as well
				//this does the same as the above, generating another route possibility.
				if (!(direction == searchdirection.anticlockwise)) {

					possibilityOne = walkAroundClockwise(dx, dy, np, fc,object,0);
					CollisionLogInfo("__________________possibilityOne length:"
							+ possibilityOne.PathLength + "(cs) broke:"+possibilityOne.pathFindingBroke);

					CollisionLogLogPath(possibilityOne);					
					possibilityOne = this.refinePath(possibilityOne,object);
					CollisionLogInfo("__________________path after refine:(cs)");
					CollisionLogLogPath(possibilityOne);	

					// Log.info("____________________________________possibilityOne after refine:"+possibilityOne.PathLength
					// + "(cs)");

					// Finish movement from here by iteration
					possibilityOne = findPathBetweenAndAddToExistingPath(dx,
							dy, possibilityOne, searchdirection.clockwise,
							object,lastCollision);
				}

				// if we are purely looking for cw or acw we end here..
				if (direction == searchdirection.anticlockwise) {

					CollisionLogInfo("anticlockwise searchonly");
					return possibilityTwo;
				}
				if (direction == searchdirection.clockwise) {

					CollisionLogInfo("clockwise searchonly");

					return possibilityOne;
				}

				//if now, we look at the cw and acw routes and find which is shorter

				if (direction == searchdirection.both) {

					//these two log statements are a test.
					// "getCurrentLength" almost certainly should be used before PathLength is correct
					//This is because the length needs to be calculated from all the points and a big of pythagoras
					CollisionLogInfo("___________comparing lengths:"
							+ possibilityOne.PathLength
							+ "(cw) to "
							+ possibilityTwo.PathLength + "(acs)");

					CollisionLogInfo("___________comparing lengths:"
							+ possibilityOne.getCurrentLength() + " (broke:"+possibilityOne.pathFindingBroke+")"
							+ "(cw) to "
							+ possibilityTwo.getCurrentLength() +" (broke:"+possibilityTwo.pathFindingBroke+")"
							+ "(acs)");

					// Log.info("____________(refinement done)_________________");
					// Log.info("____________________________________comparing lengths:"+possibilityOne.PathLength
					// +"(cw) to "+possibilityTwo.PathLength+"(acs)");
					// Log.info("____________________________________comparing lengths:"+possibilityOne.getCurrentLength()
					// +"(cw) to "+possibilityTwo.getCurrentLength() +"(acs)");

					// Log.info("____________________________________comparing lengths after end added:"+possibilityOne.PathLength
					// +"(cw) to "+possibilityTwo.PathLength+"(acs)");
					// Log.info("____________________________________comparing lengths after end added:"+possibilityOne.getCurrentLength()
					// +"(cw) to "+possibilityTwo.getCurrentLength()+"(acs)");
					
					
					
					//if both broke then quit
					if (   possibilityOne.pathFindingBroke == true
						&& possibilityTwo.pathFindingBroke == true){
						
						CollisionLogError("both path routes broke");
						
						updatePath(possibilityOne,this.lastCollision);

						return possibilityOne;
					}
					
					// take shorter of the two path lengths OR whatever one isnt broken.
					if ((possibilityOne.pathFindingBroke == true)
							|| ((possibilityOne.PathLength > possibilityTwo.PathLength)
									&&(possibilityTwo.pathFindingBroke==false))) {

						updatePath(possibilityTwo,this.lastCollision);

						return possibilityTwo;
					} else {

						updatePath(possibilityOne,this.lastCollision);

						return possibilityOne;
					}

				}
			} else {
				//if the overall big if failed (remember? the one that checked if the iterations were too much?
				//scroll up and check again if not , it was awhile ago :)

				//anyway, if that check fails we need to do an emergency exit, making our path go straight though objects :(
				//Still, its better the character that this path controls rearchs the desired point in the end rather then not at all.

				CollisionLogError("emergancy exit");
				CollisionLogError("current it = "+object.objectsCurrentPathfindingIterator);
				CollisionLogError("nodes = "+np.size());

				//skip to destination
				np.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo));

				//update path sketch for debugging
				updatePath(np, this.lastCollision);

				//ensure path is flagged broken
				np.pathFindingBroke=true;

				//return the path as well
				return np;
			}

			return null; // the program should never get here.

		}
	}





	private MovementPath abortCurrentPath(int dx, int dy, MovementPath np) {
		np.pathFindingBroke=true;
		np.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo)); //Finish the path by going straight to the destination. (and thus going though all objects)
		//we register it as broken though, so it wont be used unless really needed
		return np;
	}









	

	public void updatePath(MovementPath np, PolygonCollisionMap lastCollision) {

		if (this.CMapVisualiser.isPresent()){
			CMapVisualiser.get() .updatePath(np, lastCollision);
		}
	}





	/**This function will walk around the object we hit going clockwise<br>
	//it will go from cornor to corner. <br>
	//At each corner it tests if it can keep moving towards the finnal destination again without bumping <br>
	//into this object.<br>
	//if it cant it keeps going<br>
	//when it can..when its "free" to keep heading to its destination, then it returns<br>
	//and returns this path it just made <br><br>
	 * 
	 * Note;Currently this function only works on objects of 25 corners or less. <br>
	 * 
	 * @param dx - destination x<br>
	 * @param dy - destinations y<br>
	 * @param np - the current path<br>
	 * @param fc - the last collision (used to check if any new collisions are different)<br>
	 * @param object 
	 * @return
	 */
	private MovementPath walkAroundClockwise(int dx, int dy, MovementPath np, SpiffyPolygonCollision fc, SceneObject object, int emergancyexit_startfrom) {
		Log.info("------------------------------------------------------------------------------------(clockwise test)");

		// copy supplied path
		String newname = np.pathsName;
		MovementPath pathcopy = new MovementPath(np, "_");

		//now, as we are trying to walk around the corners going clickwise
		//our first job is to find the next corner number to look at (startFrom)
		//we do this based on the collision side. That is, the side of the object we just hit.
		//
		//We also at the last corner we should look at (endAt), which will be just the first corner in the other direction
		//as we, in the worst case, want to do a complete loop of the object.
		//after all, if we go around the object and theres still no path we can take, we don't want to loop forever
		//hence why we need to know when to stop.
		//
		//Its also important to get these the right way around. else we wont go around the object, but rather just go down one side and stop.
		//
		int startFrom = fc.side.startVertexNumber+1;
		int endAt     = fc.side.startVertexNumber; //- 1; // temp, should be tested

		//we have to ensure the above is wraped.
		//that is, if the "startVertextNumber"+1 ends up being one after the last vertex, then it becomes the first vertext instead.
		//Think of it like a analogue clock, go too high and you go back to 1 again. 
		//Much the same, if we go too low, it wraps the other way.
		if (endAt > (fc.collidingObject.numberOfVertexs() - 1)){
			Log.info("------endAt too big:"+endAt);
			endAt=endAt-(fc.collidingObject.numberOfVertexs());
			Log.info("------endAt now:"+endAt);
		}
		if (startFrom > (fc.collidingObject.numberOfVertexs() - 1)){
			Log.info("------startFrom too big:"+startFrom);
			startFrom=startFrom-(fc.collidingObject.numberOfVertexs());
			Log.info("------startFrom now:"+startFrom);
		}


		Log.info("------|startFrom "+startFrom+" at "+fc.collidingObject.getWaypointX(startFrom)+","+fc.collidingObject.getWaypointY(startFrom));
		Log.info("------|endAt "+endAt+" at "+fc.collidingObject.getWaypointX(endAt)+","+fc.collidingObject.getWaypointY(endAt));		
		Log.info("------|path "+fc.collidingObject.getAsSVGPath()+" s="+fc.collidingObject.size());

		
		//set the current vertext to the start vertext
		int curVertex = startFrom;
		
		//the walkaround function has its own emergency exit if it goes on too long within the same loop
		int emergancyexitcount = emergancyexit_startfrom;

		//do!
		//its a while loop, but you can also define it starting with Do and ending with While
		//This means the check to continue or not is done at the end or each loop rather then the start of each loop
		do {
			newname=newname+"c"; //we add C onto the pathname, this is just to help debugging so we know this path went around clockwise

			// loop also if out of range - that is, if the current vertex is too high, it goes back to zero
			// like a clockface. This should only happen if the path was foolish enough not to have a z closing it correctly
			if (curVertex > (fc.collidingObject.size()-1)) {				
				curVertex = 0;
				continue;
			}
						
			MovementWaypoint currentWaypoint = fc.collidingObject.get(curVertex);
			
			if (currentWaypoint.type == MovementType.AbsoluteLoopToPathStart){				
				curVertex = currentWaypoint.getWaypointNumberForLastSegmentStart();
				Log.info("vertex was z thus looping to last m");
				continue;
			}
			

			// Emergency exit!
			// 25 loops is the current limit. So, 25 corners on the object
			emergancyexitcount++;
			if (emergancyexitcount > 25) {
				break;
			}

			//int x = fc.collidingObject.getWaypointX(curVertex);
			//int y = fc.collidingObject.getWaypointY(curVertex);
			
			
			//no side should start with a loopback (z) waypoint, or any other type of non-real waypoint.
			//it is exceptable that a side ends with a z, however.
			/*
			if (!fc.collidingObject.get(curVertex).isRealWayPoint(true)){
				Log.info("(not real)");	
				
				if (curVertex==0){
					continue;
				} 
				
				//actually, if a z is encountered and we arnt at the start or end, then it means we have encountered a new segment
				//as we cant "walk around" to a new segment, we just end here.
				if (fc.collidingObject.get(curVertex).type == MovementType.AbsoluteLoopToPathStart ) {
					
					Log.info("_-~ |encountered a loop(z) halfway into path. This means we reached the end of a polygon segment, thus we abort the path| ~-_");
					SceneCollisionMap.CollisionLogError("encountered a loop(z) halfway into path.This means we reached the end of a polygon segment, thus we abort the path:"+fc.toString());					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
				}				
				
				continue;
			}*/
			

			//we get the outside waypoint for this current vertext
			//An outside waypoint is just the corner of the object, but out a bit from it.
			//these are precaculated in advance, and can be seen on the collision map preview.
			int MovedOutX = fc.collidingObject.getOutsetWaypointX(curVertex);
			int MovedOutY = fc.collidingObject.getOutsetWaypointY(curVertex);

			Log.info("cs cur vertext = " + curVertex+"("+currentWaypoint+")   outsetwp:"+MovedOutX+","+MovedOutY);
			//test if this new corner is even safe to use (NEW addition)

			//	if (this.isObjectColliding(object, MovedOutX, MovedOutY)!=null) {
			//	if (this.isPointColliding(MovedOutX, MovedOutY)!=null){  //we previously checked a generic point, rather then a proper collision test we do now
			//		Log.info("Corner point not safe , abandoning this subpath");
			//		break;
			//	}

			//check line to this new corner if its not the first
			if (pathcopy.size()>0){

				MovementWaypoint previously = pathcopy.get(pathcopy.numberOfVertexs()-1);
						
				SpiffyPolygonCollision cornerpos = findFirstCollision(previously.pos.x ,  previously.pos.y,  MovedOutX,  MovedOutY);
				
				Log.info("Checking if line to corner is safe line from : " + previously+" >>to>> "+MovedOutX+","+MovedOutY+"         collision:"+cornerpos);
				Log.info("pathcopy.size():"+pathcopy.size());
				
				//collision path was fully enclosed! This means all movement is impossible (arguably, we should never even get here)
				if (cornerpos != null && cornerpos.side==null){
					Log.info("_-~ fc side is null. This means path route was fully encased by a collision map ~-_");
					SceneCollisionMap.CollisionLogError("cw fc side null at:"+fc.toString());
					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
				}
				
				//collision with different object
				if (cornerpos != null && cornerpos.collidingObject != fc.collidingObject ){

					Log.info("route to corner not safe, thus walking around antic");
					CollisionLogInfo("Route to corner not safe, thus walking antic around from "+cornerpos.X+","+cornerpos.Y);

					
					return walkAroundClockwise(dx, dy, pathcopy, cornerpos,object,emergancyexitcount);
				}
				
				//if we collided with ourselves
				if (cornerpos!=null && cornerpos.collidingObject == fc.collidingObject ){
					
					String pathErrorMsg = "(collide with same polygon we were walking around. \n"
							+ " This shouldnt happen, so aborting this pathfinding route test. \n"
							+ "Probably due to incorrect handeling of multi-segment paths)";
					Log.info(pathErrorMsg);
					SceneCollisionMap.CollisionLogError("cw collision with self at:"+fc.toString()+" should never be true:"+fc.collidingObject.incorporeal);					
					SceneCollisionMap.CollisionLogLog(pathErrorMsg,"grey");					
					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
					
				} 
				
				
				Log.info("(route to corner was safe)  " + previously+" >>to>> "+MovedOutX+","+MovedOutY);
				

			}

			//
			//(check line instead)
			//(find colliding point, if any)
			//(trigger new walkaround from there, ending this one)
			// (walkAroundAntiClockwise(int dx, int dy,MovementPath np, SpiffyPolygonCollision fc, SceneObject object) )
			//

			//NOTE: In future we will sadly have to check the whole line rather then just the corner in case a object overlaped with this one
			//That is, from each vertex to vertex as we go around we need to check if that line is safe
			//As that is computationally intensive, we will need some record
			//of what polygons touch what other polygons, and only check the touching ones.
			//
			//The touching array should be cleared for each object when it moves (as well as any its touching)
			//Its then regenerated here the first time its needed till its moved again
			//
			//Note; we also need some way to stop the check from rechecking the same bit of the same polygon.
			//(ie, if we move from one polygon to another and its checking in both directions, it will turn back and recheck
			//its first polygon and get court in a loop)
			//maybe we thus should only check one-way? (only one way should be safe anyway in this situation - the same way we are currently going)
			//--------------
			//--------------

			//add this new waypoint to the path
			pathcopy.add(new MovementWaypoint(MovedOutX, MovedOutY,
					MovementType.AbsoluteLineTo));


			//test this current object to see if we can draw a line from this corner to our destination without hitting it
			SpiffyPolygonCollision nr = fc.collidingObject.testForCollision(
					new Simple3DPoint(MovedOutX, MovedOutY,0), new Simple3DPoint(dx,
							dy,0));
			//if no collision was found above we can exit looping around the object!
			if (nr == null) {
				pathcopy.setName(newname);
				break;

			}

			//we used to have to check if it was a different colliding object
			//that was because we inefficiantly used to check the WHOLE SCENES objects, rather then just the one we were looping around.
			//for the purposes of the loop, that was pointless. We only need to know when we have walked around the object enough
			//as the rest of the pathfinding functions will sort out any new objects we collide with after this one
			// if (nr.collidingObject != fc.collidingObject) {

			// Log.info("freeeeeeeeeeeeeeeeeedddddddddddddddooooooooom");

			// break;
			//
			// }

			curVertex++;

		} while (!(curVertex == (endAt + 1)));

		// if no route was found above, we just return the original path - ie, we dont walk around at all
		// the path returned is just stopping at the objects side it hit

		return pathcopy;
	}



	/**<br>
	 * For more details see "walk around anti-clockwise", which has more uptodate comments<br>
	 * <br>
	 * This function will walk around the object we hit going anticlockwise
	//it will go from cornor to corner. 
	//At each corner it tests if it can keep moving towards the final destination again without bumping 
	//into this object.
	//if it cant it keeps going
	//when it can..when its "free" to keep heading to its destination, then it returns
	//and returns this path it just made 
	 * @param dx - destination x
	 * @param dy - destinations y
	 * @param np - the current path
	 * @param fc - the last collision (used to check if any new collisions are different)
	 * @param object 
	 * @return
	 * 
	 * 
	 */
	private MovementPath walkAroundAntiClockwise(int dx, int dy,
			MovementPath np, SpiffyPolygonCollision fc, SceneObject object,int emergancyexit_startfrom) {

		Log.info("------------------------------------------------------------------------------------(anticlockwise test)");
		String newname = np.pathsName;
		// copy path
		MovementPath pathcopy = new MovementPath(np,
				"_");

		int startFrom = fc.side.startVertexNumber;
		int endAt     = fc.side.startVertexNumber+1; //+ 1; // temp, should be tested
		// each time.


		//loop is out of range
		//if (endAt == (fc.collidingObject.numberOfVertexs() - 1)) {
		//endAt = 0;
		//}

		if (endAt > (fc.collidingObject.numberOfVertexs() - 1)){
			Log.info("------endAt too big:"+endAt);
			endAt=endAt-(fc.collidingObject.numberOfVertexs());
			Log.info("------endAt now:"+endAt);
		}
		if (startFrom > (fc.collidingObject.numberOfVertexs() - 1)){
			Log.info("------startFrom too big:"+startFrom);
			startFrom=startFrom-(fc.collidingObject.numberOfVertexs());
			Log.info("------startFrom now:"+startFrom);
		}




		Log.info("--ac--startFrom"+startFrom+" at "+fc.collidingObject.getWaypointX(startFrom)+","+fc.collidingObject.getWaypointY(startFrom));
		Log.info("------endAt"+endAt+" at "+fc.collidingObject.getWaypointX(endAt)+","+fc.collidingObject.getWaypointY(endAt));


		// Log.info("startFrom = " + startFrom);
		// Log.info("endAt = " + endAt);
		// Log.info("totalvertexs= " + (fc.collidingObject.numberOfVertexs() -
		// 1));
		// Log.info("last side x = "
		// + fc.collidingObject.get(fc.collidingObject.numberOfVertexs() -
		// 1).x);
		// Log.info("last side y = "
		// + fc.collidingObject.get(fc.collidingObject.numberOfVertexs() -
		// 1).y);

		int curVertex = startFrom;
		int emergancyexitcount = emergancyexit_startfrom;

		do {
			newname=newname+"a";
			
			// loop if out of range
			if (curVertex < 0) {				
				curVertex = (fc.collidingObject.numberOfVertexs() - 1);
				continue;
			}
		
			
			// Emergency exit!
			emergancyexitcount++;
			if (emergancyexitcount > 25) {
				break;
			}

			//int x = fc.collidingObject.getWaypointX(curVertex);
			//int y = fc.collidingObject.getWaypointY(curVertex);

			//no side should start with a loopback (z) waypoint, or any other type of non-real waypoint.
			//it is exceptable that a side ends with a z, however.
			/*
			if (!fc.collidingObject.get(curVertex).isRealWayPoint(true)){
				Log.info("(not real)");	
				
				if (curVertex==0){
					continue;
				} 
				
				//actually, if a z is encountered and we arnt at the start or end, then it means we have encountered a new segment
				//as we cant "walk around" to a new segment, we just end here.
				if (fc.collidingObject.get(curVertex).type == MovementType.AbsoluteLoopToPathStart ) {
					
					Log.info("_-~ |encountered a loop(z) halfway into path. This means we reached the end of a polygon segment, thus we abort the path| ~-_");
					SceneCollisionMap.CollisionLogError("encountered a loop(z) halfway into path.This means we reached the end of a polygon segment, thus we abort the path:"+fc.toString());					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
				}				
				
				continue;
			}*/

			int MovedOutX = fc.collidingObject.getOutsetWaypointX(curVertex);
			int MovedOutY = fc.collidingObject.getOutsetWaypointY(curVertex);

			Log.info("anti cur vertext = " + curVertex+"   outsetwp:"+MovedOutX+","+MovedOutY);
			
			//test if this new corner is even safe to use (NEW addition)
			//if (this.isObjectColliding(object, MovedOutX, MovedOutY)!=null)	 {		
			//if (isPointColliding(MovedOutX, MovedOutY)!=null){
			//	Log.info("corner point not safe, abandoning this subpath");
			//	break;
			//}
			
			//check line to this new corner is safe(temp inefficient)
			if (pathcopy.size()>0){
				
				MovementWaypoint previously = pathcopy.get(pathcopy.numberOfVertexs()-1);
				
				
				//we could make this more efficient by only searching known touching objects?
				//use fc.collidingObject.knownPolygons to find that 
				
				SpiffyPolygonCollision cornerpos = findFirstCollision(previously.pos.x ,  previously.pos.y,  MovedOutX,  MovedOutY);
				Log.info("Checking if line to corner is safe line from:  " + previously+" >>to>> "+MovedOutX+","+MovedOutY+"         collision:"+cornerpos);
				
				if (cornerpos != null && cornerpos.side==null){
					Log.info("_-~ fc side is null. This means path route was fully encased by a collision map ~-_");
					SceneCollisionMap.CollisionLogError("acw fc side null at:"+fc.toString());
					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
				}
				
				if (cornerpos != null && cornerpos.collidingObject != fc.collidingObject ){

					Log.info("route to corner not safe, thus walking around");
					CollisionLogInfo("route to corner not safe, thus walking ac around from "+cornerpos.X+","+cornerpos.Y);

					return walkAroundAntiClockwise(dx, dy, pathcopy, cornerpos,object,emergancyexitcount);
				}
				
				//if we collided with ourselves
				if (cornerpos!=null && cornerpos.collidingObject == fc.collidingObject ){
					
					String pathErrorMsg = "(collide with same polygon we were walking around. \n "
							+ "This shouldnt happen, so aborting this pathfinding route test. \n"
							+ "Probably due to incorrect handeling of multi-segment paths)";
					Log.info(pathErrorMsg);
					SceneCollisionMap.CollisionLogError("acw collision with self at:"+fc.toString()+" should never be true:"+fc.collidingObject.incorporeal);					
					SceneCollisionMap.CollisionLogLog(pathErrorMsg,"grey");					
					
					//we thus abort
					return abortCurrentPath(dx, dy, np);
					
				} 
				
				
				
			}

			//
			//(check line instead)
			//(find colliding point, if any)
			//(trigger new walkaround from there, ending this one)
			// (walkAroundAntiClockwise(int dx, int dy,MovementPath np, SpiffyPolygonCollision fc, SceneObject object) )
			//

			//NOTE: In future we will sadly have to check the whole line rather then just the corner in case a object overlaped with this one
			//That is, from each vertex to vertex as we go around we need to check if that line is safe
			//As that is computationally intensive, we will need some record
			//of what polygons touch what other polygons, and only check the touching ones.
			//
			//The touching array should be cleared for each object when it moves (as well as any its touching)
			//Its then regenerated here the first time its needed till its moved again
			//
			//Note; we also need some way to stop the check from rechecking the same bit of the same polygon.
			//(ie, if we move from one polygon to another and its checking in both directions, it will turn back and recheck
			//its first polygon and get court in a loop)
			//maybe we thus should only check one-way? (only one way should be safe anyway in this situation - the same way we are currently going)
			//--------------

			/*
			// next side before waypoint
			PolySide sidebefore = fc.collidingObject.getSide(curVertex - 1);

			// move outwards from both
			// int mbx = ((sidebefore.endPoint.x - sidebefore.startPoint.x) /
			// 15);
			// int mby = ((sidebefore.endPoint.y - sidebefore.startPoint.y) /
			// 15);

			PolySide sideafter = fc.collidingObject.getSide(curVertex);

			// int mbx2 = ((sideafter.endPoint.x - sideafter.startPoint.x) /
			// 15);
			// int mby2 = ((sideafter.endPoint.y - sideafter.startPoint.y) /
			// 15);

			//this next bit could be cached?
			double angBetweenSides = Polygon.getAngleBetweenSides(sidebefore,
					sideafter, "orange");

			// move outwards from both
			// Log.info("mbx = " + mbx);
			// Log.info("mbx2 = " + mbx2);

			int MovedOutX = (int) (Math.cos(angBetweenSides) * collision_margin);
			MovedOutX = MovedOutX + x;
			int MovedOutY = (int) (Math.sin(angBetweenSides) * collision_margin);
			MovedOutY = MovedOutY + y;

			 */



			pathcopy.add(new MovementWaypoint(MovedOutX, MovedOutY,
					MovementType.AbsoluteLineTo));


			// pathcopy.add(new MovementWaypoint(x - (mby + mby2), y
			// + (mbx + mbx2), MovementType.LineTo));

			// test if waypoint allows a safe new route
			// SpiffyPolygonCollision nr = findFirstCollision(x - (mby + mby2),
			// y
			// + (mbx + mbx2), dx, dy);

			// SpiffyPolygonCollision nr = findFirstCollision(MovedOutX,
			// MovedOutY, dx, dy);
			SpiffyPolygonCollision nr = fc.collidingObject.testForCollision(
					new Simple3DPoint(MovedOutX, MovedOutY,0), 
					new Simple3DPoint(dx,	dy,0));

			if (nr == null) {
				pathcopy.setName(newname);
				return pathcopy; //successfully found a route from the corner to a different object or the destination
			}


			// if (nr.collidingObject != fc.collidingObject) {
			//
			// Log.info("freeeeeeeeeeeeeeeeeedddddddddddddddooooooooom");

			// break;
			//
			// }
			
			//if the waypoint we just did was a moveto, then we loop to its matching z rather then going back one
			MovementWaypoint currentWaypoint = fc.collidingObject.get(curVertex);
			if (currentWaypoint.isMoveTo()){
				curVertex = currentWaypoint.getWaypointNumberForNextSegmentEnd();	
				Log.info("looping to matching z");
				
				continue;
			} else {
				curVertex--;
			}
			
		} while (!(curVertex == (endAt - 1)));

		//if we have gone right around, assume a break;
		pathcopy.pathFindingBroke=true;

		// use the shorter path
		// destination
		// pathcopy.add(new MovementWaypoint(dx, dy, MovementType.LineTo));

		return pathcopy;
	}

	/** given a path specified by start points and destination points,<br>
	 * we find a point at (Displacement) distance back from the end (dx/dy)<br>
	 * This is used in pathfinding when we bump into something, so we move back a bit before going around 
	 * the object we bumped into.<br>
	 * <br>
	 * <br>
	 * Warning: Contains maths!<br>**/
	private Simple2DPoint getPositionFromEndOfPath(int startX, int startY,
			int dx, int dy, int Displacement) {


		//first we get the distance of the path in both X and Y
		//Math.abs means it ignores the sign (that is, if the sum comes out negative, it gives a positive value of it instead).
		double distanceX = Math.abs(dx - startX);
		double distanceY = Math.abs(dy - startY);

		//Log.info("distance X=" + distanceX+" Y=" + distanceY);

		//work out the ratios for the x and y movement. 
		//That is, a number from 0-1 determaining how far your moving vertically
		//and a number from 0-1 determaining how far your moving horizontally.
		//By scaling it 0-1 in range it means you can work out how much of the Displacement asked for needs to be done in X and Y co-ordinates 
		double ratio = (distanceX * 1.0) / ((distanceY + distanceX) * 1.0);
		double ratio2 = (distanceY * 1.0) / ((distanceX + distanceY) * 1.0);

		//either we are going straight up or straight accross we get the ratios below
		//as the above will return funny stuff if it needs to divide by zero
		if (distanceX == 0) {
			ratio = 0;
			ratio2 = 1;
		}
		if (distanceY == 0) {
			ratio = 1;
			ratio2 = 0;
		}

		// Log.info("ratio1="+ratio);
		// Log.info("ratio2="+ratio2);

		//now we have the ratios we simply multiply them by the Displacement asked for.
		//ie. If the calling function asked for Displacement 10 pixels
		//we need to work out how far back we move in X and Y in order for it to be 10 pixels
		//in total diagonolly. 
		//The signum thing just makes sure its going in the right direction!
		int disx = (int) (ratio * Displacement * (Math.signum(dx - startX)));
		int disy = (int) ((ratio2) * Displacement * (Math.signum(dy - startY)));

		//Log.info("move back by x=" + disx+" y =" + disy);

		Log.info("new end set too:" + (dx - disx) + "," + (dy - disy));

		//make a point from it and return
		Simple2DPoint nsp = new Simple2DPoint(dx - (disx), dy - (disy));

		return nsp;
	}





	/** 
	 * This function loops over all the path nodes, testing if removing one will cause a collision
	 * if not it removes it.
	 * This makes the path more of a optimal route - its somewhat akin to pulling a string tighter so it doesnt unnescerily "hug" objects
	 * 
	 * note: doesn't update the paths internal length! **/
	public MovementPath refinePath(MovementPath p,SceneObject object) {

		//if refine is globally disabled, just return the path without doing anything
		if (refineDisabled){
			return p;
		}
		Simple2DPoint lowerLeft = object.getLowerLeftDisplacement();

		//start from node zero
		int nodestart = 0;

		Log.info("new refining...");

		// try to remove each node in turn;
		CollisionLogInfo("$*$*$*The NUMBER of NODES BEFORE REFINING:"+p.size());

		while (nodestart < (p.size() - 2)) {

			// start is the current node
			int sx = p.get(nodestart + 0).pos.x;
			int sy = p.get(nodestart + 0).pos.y;
			// end is not the next node, but the one after
			int ex = p.get(nodestart + 2).pos.x;
			int ey = p.get(nodestart + 2).pos.y;

			//So we might have nodes 4,5,6 the sx/sy is node 4 the ex ey is node 6
			//This means we are trying to remove the middle node 5.

			// test; this currently takes no account of the width, maybe thats a problem?
		//	SpiffyPolygonCollision c   = findFirstCollision(sx, sy, ex, ey); //needs to use width (so should be switched for the more advanced findFirstCollison function:)
		SpiffyPolygonCollision c = findFirstCollision(sx, sy, ex, ey,lowerLeft, object.getPhysicalObjectWidth());

			//in order to use that function, however, the current object being moved (object)
			//will have to be fed to this function as well as the bottom right point
			//of it						

			//if ,after removing the middle node, no collision is found we remove it
			if (c == null) {
				CollisionLogInfo("No collisions found at node "+nodestart);
				p.remove(nodestart + 1);

			} else {				
				//if not we move to the next node to test and loop
				nodestart = nodestart + 1;
			}

		}

		CollisionLogInfo("�-�-�-The NUMBER of NODES IS NOW"+p.size());

		//update the preview of this reviewed path if there is one:

		if (this.CMapVisualiser.isPresent()){
			CMapVisualiser.get().svgSmoothPathUpdate(p.getAsSVGPath());

		}

		Log.info("now refined...");

		return p;
	}




	public static void CollisionLogInfo(String string) {

		if (CollisionLogger.isPresent()){

			CollisionLogger.get().info(string);

		}		

	}




	public static void CollisionLogLogPath(MovementPath mp) {

		if (CollisionLogger.isPresent()){

			CollisionLogger.get().logPath(mp);

		}	

	}



	public static void CollisionLogError(String error) {

		if (CollisionLogger.isPresent()){

			CollisionLogger.get().error(error);

		}	

	}
	public static void CollisionLogLog(String log) {
		if (CollisionLogger.isPresent()){			
			CollisionLogger.get().log(log);

		}	
	}
	public static void CollisionLogLog(String log, String col) {
		if (CollisionLogger.isPresent()){			
			CollisionLogger.get().log(log,col);

		}	
	}


	/**
	 * Tests if the object collides with the environment using the objects collision mode setting
	 * 
	 * @param object
	 * @param pretendPositionX
	 * @param pretendPositionY
	 * @param pretendPositionZ - currently not used. In future we hope to have 3 dimensional support, even if the 3rd dimension is purely extrusions of objects maps upto a certain height. (so a world of parralagrams - good enough for objects to be chucked over others)
	 * 
	 */ //TODO: Z axis check. We have objects Z pos now, but we also need their height for the full "range" of their z presence 
	public Polygon isObjectColliding(SceneObject object, int pretendPositionX,int pretendPositionY,int pretendPositionZ){
		
		Log.info("Testing for "+object.getName()+" colliding with scene at "+pretendPositionX+","+pretendPositionY+","+pretendPositionZ);

		//get collision mode
		CollisionModeSpecs mode = object.getObjectsCurrentState().boundaryType;
		
		Simple2DPoint lowerLeftDisplacement = object.getLowerLeftDisplacement(); 
		
		int blx = pretendPositionX + lowerLeftDisplacement.x; //we do this to convert the pin position supplied to where the map needs to be
		int bly = pretendPositionY + lowerLeftDisplacement.y;
		//
		//New experimental
		//TODO: really we should check if the compound map has a corporal part
		
		//urg...probably wont work with submaps as the pretend position is only for the parent object, their locations will be different
		//
		if (  (object.cmap.isPresent() && object.cmap.get().hasCoporalPart() ) 
				|| 
			  (object.getCompoundCmap().isPresent() && object.getCompoundCmap().get().hasCoporalPart()) 
			) { //now now check to ensure there's at least 1 corporal polygon in the cmap

			int tlx = blx;
			int tly = bly - object.getPhysicalObjectHeight(); //convert to upper left (rather then lower left)
			
			Log.info("Testing cmap for "+object.getName()+" colliding with scene at "+pretendPositionX+","+pretendPositionY);
			
			
			//we convert to relative
			//this is because if we have collision maps of child objects in our compound map, those objects
			//might be in completely different positions/pins etc.
			//absolute pretend positions wont work, but displacing everything the same relative amount will
			
			//to get the pretend position as a relative displacement we simply subtract
			int relative_tlx = pretendPositionX - object.getX();
			int relative_tly = pretendPositionY - object.getY();
			int relative_tlz = pretendPositionZ - object.getZ();
			
			
			//Polygon collision = isPolygonColliding(object.getCompoundCmap().get(),tlx,tly,true);
			
			Polygon collision = isPolygonColliding(object.getCompoundCmap().get(),relative_tlx,relative_tly,false);
			
			if (collision != null){
				
				object.ObjectsLog(" collision found :"+tlx+","+tly+" ("+object.getPhysicalObjectHeight());
				
				
				return collision;
			} else {
				return null;
			}

		}


		
		switch (mode.getCollisionType()) {
		case bottomline:
			Log.info("testing for collision using objects base line...");
			CollisionLogInfo("Testing bottomline of "+object.getName());
			
			addToSketch("M "+blx+","+bly+" L "+(blx + object.getPhysicalObjectWidth())+","+bly,"#555");

			SpiffyPolygonCollision col = (findFirstCollision(blx, bly, blx + object.getPhysicalObjectWidth(), bly));

			if (col==null){
				Log.info("no collision found.");

				return null;
			}

			return col.collidingObject;

		case box:

			int topleftX= 0;
			int topleftY= 0;
			int bottomrightX = 0;
			int bottomrightY  = 0;

			if (mode.customCollisionBox.isPresent()){
				Log.info("testing for collision using objects custom collision box");

				CollisionBox cbox = mode.customCollisionBox.get();

				topleftX = blx;
				topleftY = bly - object.getPhysicalObjectHeight();

				//custom collision box mode;
				topleftX = topleftX+cbox.topleft.x;
				topleftY = topleftY+cbox.topleft.y;

				bottomrightX = topleftX+cbox.topleft.x;
				bottomrightY = topleftY+cbox.topleft.y;
				//--
				//-
			} else {
				Log.info("testing for collision using objects default collision box (which is the sprites own size/position)");

				//default we just use the spites size
				topleftX = blx;
				topleftY = bly - object.getPhysicalObjectHeight();
				bottomrightX = blx+object.getPhysicalObjectHeight();
				bottomrightY = bly;

			}

			return (isBoxColliding(topleftX, topleftY, bottomrightX, bottomrightY, false));

		case none:
			Log.info("NOT testing for collisions - objects collision mode set to none");

			return null;//no collisions!

		case point:
			Log.info("testing for collision using objects pivot point");
			
			return isPointColliding(pretendPositionX,pretendPositionY);			
		default:

			Log.severe("OBJECTS COLLISION MODE NOT RECOGNISED OR SUPPORTED:"+mode);

			return null; 
		}
	}



	/** test if the supplied point collides with anything on this map **/
	public Polygon isPointColliding(int dx, int dy) {

		Polygon col = null;

		//first we test all the polygons on the scenes own map
		Iterator<Polygon> sit = scenesOwnMap.iterator();

		while (sit.hasNext()) {
			Polygon polygon = (Polygon) sit.next();

			//we should ignore polygons that are incorporeal
			if (polygon.incorporeal){
				continue;
			}
			//else we test if the point is inside this polygon
			if (polygon.isPointInside(dx, dy)) {
				col = polygon;//this.scenesOwnMap;
			}

		}



		// Log.info("testing scene objects for collision at " + dx + "," + dy);
		// now we test all the scenes objects objects (note, this overrides the collision above if one was found above)

		Iterator<SceneObject> soit = sourcescene.getScenesData().scenesOriginalObjects.iterator();

		//loop over all the scenes objects
		while (soit.hasNext()) {
			SceneObject so = (SceneObject) soit.next();

			//test if this object has a collision map (if not we do nothing and goto the next object)
			if (so.cmap.isPresent()) {

				// Log.info("testing scene objects: " +
				// so.objectsCurrentState.ObjectsName);

				//loop over all the polygons in this objects cmap
				Iterator<Polygon> cit = so.cmap.get().iterator();

				while (cit.hasNext()) {

					Polygon npolygon = (Polygon) cit.next();

					//we should ignore polygons that are incorporeal
					if (npolygon.incorporeal){
						continue;
					}
					//and those not visible
					if (!npolygon.associatedObject.isVisible()){ //sourceMap.defaultAssociatedObject 
						continue;
					}

					// Log.info("testing polygon: "
					// + npolygon.getAsDisplacedSVGPath(so.objectsCurrentState.X,
					// so.objectsCurrentState.Y));
					//test the polygon to see if the points are inside it
					if (npolygon.isPointInside(dx, dy)) {
						Log.info("is inside! ");

						col = npolygon;//so.cmap;
					}

				}
			}

		}
		//return any collisions found. This will be null if none are found.
		return col;
	}

	/** 
	 * WARNING: NOT YET TESTED 
	 * 
	 * @param polygonCollisionMap 
	 * @param tly - displacement Y (either from 0 or from the default position which comes from the associated object)
	 * @param tlx - displacement X
	 * @param overrideAssociatedObjectsDisplacement - true = tlx/tly specify absolute co-ordinates false=they specify relative to the normal object location
	 * @return
	 */
	public Polygon isPolygonColliding(PolygonCollisionMap polygonCollisionMap, int tlx, int tly ,boolean overrideAssociatedObjectsDisplacement) {
		// some point implement:
		// http://www.codeproject.com/Articles/15573/2D-Polygon-Collision-Detection
		// instead
		if (polygonCollisionMap.defaultAssociatedObject!=null){
			Log.info("Testing for collision using "+polygonCollisionMap.defaultAssociatedObject.getName()+" polygons at "+tlx+","+tly);
		} else {
			Log.info("Testing for collision using objects polygons...");			
		}
		
		//first we test all the polygons on the scenes own map
		Polygon collision = scenesOwnMap.isCollidingWith(polygonCollisionMap, tlx,  tly,overrideAssociatedObjectsDisplacement);
		
		if (collision != null){
			
			Log.info("(Collided with scene : object- "+polygonCollisionMap.defaultAssociatedObject.getName()+" )");			
			Log.info("(Collided with scene : object- "+polygonCollisionMap.getPath()+" )");	
			Log.info("(Collided with scene : object dx- "+polygonCollisionMap.defaultAssociatedObject.getTopLeftBaseX()+" )");
			Log.info("(Collided with scene : object dy- "+polygonCollisionMap.defaultAssociatedObject.getTopLeftBaseY()+" )");
			
			Log.info("(Collided with scene : map - "+collision.getAsSVGPath()+" )");
			
			return collision;
		} else {
			Log.info("(did not collide with scene)");
			
		}
		
		

		
		// Log.info("testing scene objects for collision at " + dx + "," + dy);
		// now we test all the scenes objects objects (note, this overrides the collision above if one was found above)

		Iterator<SceneObject> soit = sourcescene.getScenesData().scenesOriginalObjects.iterator(); //TODO:original? shouldnt it be current objects?

		//loop over all the scenes objects
		while (soit.hasNext()) {
			SceneObject so = (SceneObject) soit.next();
			
			//ignore if hidden
			if (!so.isVisible()){
				continue;
			}
			
			//or if polygon is from the same source as the current object
			//ie. don't collide with ourselves
			if (polygonCollisionMap.defaultAssociatedObject==so){
				continue;
				
			}
			
			//we also currently don't collide with objects that are positioned relative to the source object,or has the source object anywhere in its parent aancestors
			//There is a potential edge-case in future where a object selectively inheriting position (say x/y) might collide in the other axis (say z).
			//If thats ever needed, this will need to be a option
			//if (so.getObjectsCurrentState().positionedRelativeToo == polygonCollisionMap.associatedObject){
			if (so.getObjectsCurrentState().isRelativeToAncestor(polygonCollisionMap.defaultAssociatedObject)){
			//	polygonCollisionMap.defaultAssociatedObject.ObjectsLog("Hit child"+so.getName()+", ignoring", "Orange");
				
				continue;
			}
			

			//test if this object has a collision map (if not we do nothing and goto the next object)
			if (so.cmap.isPresent()) {

				collision = so.cmap.get().isCollidingWith(polygonCollisionMap, tlx,  tly,overrideAssociatedObjectsDisplacement);
				if (collision != null){

					Log.info("(cCollided with object) "+so.getName()+" - "+collision.getName());

					return collision;
				}
				

			}

		}
		//return any collisions found. This will be null if none are found.
		return null;

		
	}



	/** 
	 * Test if the specified box collides with anything on the current scene
	 * 
	 * TODO: check height in future 
	 * @param tlx - top left x of box
	 * @param tly - top left y of box
	 * @param brx - bottom right x of box
	 * @param bry - bottom right y of box 
	 * @param returnHighestZindex - if too return the highest object in terms of z-index out of any colliding (a bit slower)
	 * @return
	 */	
	public Polygon isBoxColliding(int tlx, int tly, int brx,
			int bry, boolean returnHighestZindex) {
		
		HashSet<SceneObject> objectsToCheck =  sourcescene.getScenesData().scenesOriginalObjects; //probably should be current objects
	
		return	isBoxColliding( tlx,  tly,  brx, bry,  returnHighestZindex,objectsToCheck) ;
		
	}

	/** 
	 * Test if the specified box collides with anything in either the scenes own polygon map, or in the list of objects supplied
	 * 
	 * TODO: check height in future 
	 * @param tlx - top left x of box
	 * @param tly - top left y of box
	 * @param brx - bottom right x of box
	 * @param bry - bottom right y of box 
	 * @param returnHighestZindex - if too return the highest object in terms of z-index out of any colliding (a bit slower)
	 * @param collisionCheckPool - list of objects on the scene you want to check
	 * 
	 * @return
	 */
	public Polygon isBoxColliding(int tlx, int tly, int brx,
			int bry, boolean returnHighestZindex, Set<? extends SceneObject> collisionCheckPool) {
		
		//any collisions found will be put into the variable col
		//PolygonCollisionMap col = null;
		Polygon col = null;

		//if theres no collision map for this scene we just return null - ie, the box didnt hit anything
		if (scenesOwnMap == null) {
			Log.info("no collision map set");
			return null;
		}

		//first we loop over the scenes objects
		Iterator<Polygon> sit = scenesOwnMap.iterator();

		while (sit.hasNext()) {

			Polygon polygon = (Polygon) sit.next();

			//skip if incomporal
			if (polygon.incorporeal){
				continue;
			}

			//test if any of the box is inside the polygon
			if (polygon.isBoxInside(tlx, tly, brx, bry)) {
				Log.info("box is inside.");

				//if we are not looking for the highest zindex collision
				//we can return what we found straight away
				if (!returnHighestZindex) {
					return polygon;//  this.scenesOwnMap);
				} else {
					//else we remember it for testing later
					col = polygon;//this.scenesOwnMap;
				}



			}

		}

		//now we test the scenes objects
	//	Iterator<SceneObject> soit = sourcescene.getScenesData().scenesOriginalObjects.iterator(); //probably should be current objects
//objectsToCheck
		Iterator<? extends SceneObject> soit = collisionCheckPool.iterator(); 
				
		//loop over all it objects
		while (soit.hasNext()) {
			
			SceneObject so = (SceneObject) soit.next();

			//if the object has a cmap
			if (so.cmap.isPresent()) {

				// Log.info("testing scene objects: " +
				// so.objectsCurrentState.ObjectsName);

				//loop over all the polygons on the objects collision map
				Iterator<Polygon> cit = so.cmap.get().iterator();

				while (cit.hasNext()) {

					Polygon npolygon = (Polygon) cit.next();

					//skip if incomporal
					if (npolygon.incorporeal){
						continue;
					}

					//skip if polygons associated object not visible
					if (!npolygon.associatedObject.isVisible()){ //sourceMap.defaultAssociatedObject 
						continue;
					}
					
					

					// Log.info("testing polygon: "
					// + npolygon.getAsDisplacedSVGPath(so.objectsCurrentState.X,
					// so.objectsCurrentState.Y));

					//test if any of the box is inside the polygon
					if (npolygon.isBoxInside(tlx, tly, brx, bry)) {

						//if we arnt looking for the highest zindex, we return this collision we found
						//as any collision is good
						if (!returnHighestZindex) {
							col = npolygon;//so.cmap;
							Log.info("box is inside! "
									+ col.associatedObject.getObjectsCurrentState().ObjectsName); //sourceMap.defaultAssociatedObject 

							return col;
						} else {

							// if we are looking for the highest colliding object
							// we compare the newly found collision
							if (col == null) {
								//if no previous collision found we save it in col

								col = npolygon;//so.cmap;
								Log.info("first collision set");
								Log.info("box is inside "
										+ col.associatedObject.getObjectsCurrentState().ObjectsName); //sourceMap.defaultAssociatedObject 
							} else if (col.associatedObject == null) { //sourceMap.defaultAssociatedObject 
								//if no previous object associated (ie, it was a scene collision) found we save it in col
								col = npolygon;// so.cmap;
								Log.info("first collision set");
								Log.info("box is inside "
										+ col.associatedObject.getObjectsCurrentState().ObjectsName);//sourceMap.defaultAssociatedObject 

							} else {
								Log.info("testing for higher zindex");
								// we compare the newly found collision (so)
								//to the last one found
								//if its attached to a scene object, we save if its higher 
								//then the last collision found.
								if (col.associatedObject.getZindex() < so.cmap.get().defaultAssociatedObject 
										.getZindex()) { //sourceMap.defaultAssociatedObject 
									//as its higher then the last col we save it in col
									col = npolygon;//so.cmap;
									Log.info("box is inside!! "
											+ col.associatedObject.getObjectsCurrentState().ObjectsName); //sourceMap.defaultAssociatedObject 

								}

							}

						}
					}

				}
			}

		}

		//return the found collision
		return col;
	}


	
	//If the requested destination is in a object, rather then pulling back the whole way to get a new destination
	//we instead remember the second from last point calculated when moving back, and use this as
	//the penultimate way point to get a slight "bounce" effect near the end.

	public MovementPath getSafePath(int dx, int dy, SceneObject object) {

		boolean bounceOn = true;

		/*
		if (this == null) {
			SceneWidgetVisual.Log.info("collision map not set, so we assume we can go in a straight line");

			MovementPath np = new MovementPath("", "_internal_");

			np.add(new MovementWaypoint(object.getX(), object.getY(),MovementType.Move)); //

			np.add(new MovementWaypoint(dx, dy, MovementType.LineTo));

			return np;
		}*/

		Log.info("testing for safe path...");

		//	int brdy = object.getOffsetHeight() - object.objectsCurrentState.PinPointY;
		//	int brdx = -object.objectsCurrentState.PinPointX;

		//umm...isn't this left?
		Simple2DPoint bottomrightDisplacement = object.getLowerLeftDisplacement(); //new SimplePoint(brdx, brdy);

		//Log.info("________________________________lower corner of object is..."
		//		+ bottomrightDisplacement.x + "," + bottomrightDisplacement.y);

		// first test endpoint line is not colliding anywhere
		// while (scenesCmap.isPointColliding(dx, dy) != null) {
	//	int brx = dx + bottomrightDisplacement.x;
	//	int bry = dy + bottomrightDisplacement.y;

		//Log.info("________________________________bottom left of destintion is..."
		//		+ brx + "," + bry);
		//Log.info("________________________________bottom right of destintion is..."
		//		+ (brx + object.getOffsetWidth()) + "," + bry);


		addToSketch("M "+dx+","+dy+" l 0 -5", "purple");
	//	addToSketch("M "+brx+","+bry+" l "+object.getObjectWidth()+",0", "pink");

		// while (scenesCmap.isPointColliding(dx, dy) != null) { //brx,
		// bry,brx+object.getOffsetWidth(),bry

		// get the collision line at destination, if colliding move back a bit and try again

		int prevEndX = -1;
		int prevEndY = -1;

		//TODO: currently we look at the requested point to figure out if its allowed.
		//if not we move backwards alone the line between the requested point and the start point till we find a destination
		//outside the polygon.
		//In future we should instead look for the nearest outside point on the polygon.
		//http://stackoverflow.com/questions/19048122/how-to-get-the-nearest-point-outside-a-polygon-from-a-point-inside-a-polygon
		//http://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
		//(maybe even https://en.wikipedia.org/wiki/JTS_Topology_Suite)
		//
		//1. if inside trigger new function to find nearest point
		//2. new function could then;
		//3. look for closest side in crude way and then work on closest point on that side not colliding with anything else
		//OR look for closest point on all sides, and pick shortest.
		//We then need to move outsides slightly from that point, however so there is a decent margin
		//

		//New System , we use isObjectColliding which takes into account the objects own collision type

		//Temp (just get rid of brx/bry in future, as they arnt used now)
		//brx = dx;
		//bry = dy;

		Log.info("Testing for collisision at:"+dx+","+dy+",0"); //z currently zero

			Polygon objectCollisionAtDestination = isObjectColliding(object,dx,dy,0); //really we should use this rather the a point test
		//Polygon objectCollisionAtDestination = isPointColliding(dx,dy);

		//NOTE: there might be issues when the pinpoint of a object is not within the collision region
		//This might happen if you use the default lower line, but the pin is in the middle
		if (objectCollisionAtDestination!=null){

			Log.info("detected that requested point is inside a object");
			CollisionLogInfo("detected that requested point is inside a object, moving outside");
			
			SpiffyPolygonCollision NearestPointOutsideCollisionPolygon = objectCollisionAtDestination.getNearestOutsidePoint(dx,dy,true);

			// //set the previous x/y to this point;				
			prevEndX = NearestPointOutsideCollisionPolygon.X;
			prevEndY = NearestPointOutsideCollisionPolygon.Y;

			// //then move further outwards a bit from it;
			// 
			int DistanceToMoveBack = 35;//must be at least enough for the objects collision test to miss if it was standing next to a verticle line
			//(this would be the largest distance from the pin to either the left or right edge if using lower edge pathfinding)

			PolySide side = NearestPointOutsideCollisionPolygon.side;


			Simple3DPoint linestart = side.startPoint;
			Simple3DPoint lineend   =   side.endPoint;

			double diff_x = linestart.x - lineend.x;
			double diff_y = linestart.y - lineend.y;


			//double dist = Math.sqrt((diff_x * diff_x) + (diff_y * diff_y));
			double dist = Math.hypot(diff_x,diff_y );

			double normX = diff_x / dist;
			double normY = diff_y / dist;

			double xPerp = DistanceToMoveBack * normX;
			double yPerp = DistanceToMoveBack * normY;

			int dis_x =  (int) (NearestPointOutsideCollisionPolygon.X + yPerp); //(int) ((DistanceToMoveBack * (1.0/ratio)    ) + NearestPointOutsideCollisionPolygon.X);
			int dis_y =  (int) (NearestPointOutsideCollisionPolygon.Y - xPerp); //(int) ((DistanceToMoveBack * (ratio)        ) + NearestPointOutsideCollisionPolygon.Y);


			dx = dis_x;
			dy = dis_y;

			if (SceneObjectDatabase.currentScene.scenesCmap.isPresent()){
				SceneObjectDatabase.currentScene.scenesCmap.get().addToSketch("M "+NearestPointOutsideCollisionPolygon.X+","+NearestPointOutsideCollisionPolygon.Y+" L "+(dis_x)+","+dis_y,"RED");

			}


		} else {

			CollisionLogInfo("requested point "+dx+","+dy+",0 is safe");
		}


		//if the newly calculated endpoint is still colliding, we have to use the more drastic method of
		//walking back along the path to find a safe point.
		
		
		
		

		// work out the angle of the line we are moving back along
		int startx = object.getX();
		int starty = object.getY();
		
		
		//made the following ""move back along line"" bit into a separate function		
		//the result is a waypoint array of  bx,by, dx/dy where bx/by is a optional bounce
		//this path is not used directly, merely the last two co-ordinates taken from it
		ArrayList<MovementWaypoint> pathWithSafeEnding = findSafeEndpointOnLine(startx, starty, dx, dy, object);
	
		//We now use the co-ordinates found in the above function
		if (pathWithSafeEnding.size()>1){
			prevEndX = pathWithSafeEnding.get(0).pos.x;	
			prevEndY = pathWithSafeEnding.get(0).pos.y;
			dx = pathWithSafeEnding.get(1).pos.x;	
			dy = pathWithSafeEnding.get(1).pos.y;			
		} else {
			//else just update the dx/dy
			dx = pathWithSafeEnding.get(0).pos.x;	
			dy = pathWithSafeEnding.get(0).pos.y;			
		}
		
		//update dx/dy
		
		
		/*

		while ((scenesCmap.findFirstCollision(brx, bry,
				brx + object.getOffsetWidth(), bry) != null)
				|| (scenesCmap.isPointColliding(dx, dy) != null)) {



			// if it is then find nearest safe spot by working backwards

			SpiffyPolygonCollision col = scenesCmap.findFirstCollision(dx, dy,
					object.getX(), object.getY(), bottomrightDisplacement,
					object.getOffsetWidth());

			//	SpiffyPolygonCollision col = scenesCmap.findFirstCollision(dx, dy,
			//			object.getX(), object.getY());


			if (col == null) {
				Log.info("__________________________________________no collision found");
				break;
			}

			// move back along path by 10 pixels
			double distanceX = Math.abs(dx - object.getX());
			double distanceY = Math.abs(dy - object.getY());

			Log.info("distanceX=" + distanceX);
			Log.info("distanceY=" + distanceY);

			double ratio = (distanceX * 1.0) / ((distanceY + distanceX) * 1.0);
			double ratio2 = (distanceY * 1.0) / ((distanceX + distanceY) * 1.0);

			if (distanceX == 0) {
				ratio = 0;
				ratio2 = 1;
			}
			if (distanceY == 0) {
				ratio = 1;
				ratio2 = 0;
			}

			// Log.info("ratio1="+ratio);
			// Log.info("ratio2="+ratio2);

			int disx = (int) (ratio * 40 * (Math.signum(dx - object.getX())));
			int disy = (int) ((ratio2) * 40 * (Math.signum(dy - object.getY())));

			Log.info("move back by =" + disx);
			Log.info("move back by =" + disy);

			dx = col.X - (disx);
			dy = col.Y - (disy);

			Log.info("new end set too:" + dx + "," + dy);

			// go from end to start using collision
			brx = dx + bottomrightDisplacement.x;
			bry = dy + bottomrightDisplacement.y;

			Log.info("________________________________bottom left of destintion is..."
					+ brx + "," + bry);
			Log.info("________________________________bottom right of destintion is..."
					+ (brx + object.getOffsetWidth()) + "," + bry);

			EmergancyExit--;

			if (EmergancyExit < 0) {
				Log.info("________________________________too many loops when finding safe spot");
				break;
			}

		}
		 */

		addToSketch("M "+dx+","+dy+" l 0 -5", "yellow");

		//This shouldn't fire if the above loop ran correctly
		if (isPointColliding(dx, dy) != null) {

			//if (isObjectColliding(object,dx,dy)!=null){
			Log.info("__________________________________________collision at pin point found, canceling movement");
			CollisionLogLog( "_collision at pin point found, canceling movement:"+dx+","+dy,"RED");
			

			MovementPath np = new MovementPath("", "_internal error_");

			np.add(new MovementWaypoint(startx, starty,
					MovementType.AbsoluteMove)); //

			np.pathFindingBroke = true;

			return np;

		}

		/*
		 * //Now we test the lower edge of the bounding box and move back
		 * further along path, if required. //get the short line between
		 * collision and BBs end point //note;; this should really be part of
		 * the overall pathfinding in the "find first collision int
		 * blx=dx-object.objectsCurrentState.PinPointX; int
		 * bly=dy-object.objectsCurrentState.PinPointY+object.getOffsetHeight(); //not
		 * real bb yet int
		 * brx=dx-object.objectsCurrentState.PinPointX+object.getOffsetWidth(); int
		 * bry=dy-object.objectsCurrentState.PinPointY+object.getOffsetHeight(); //not
		 * real bb yet
		 * 
		 * SpiffyPolygonCollision linecol = scenesCmap.findFirstCollision(blx,
		 * bly, brx, bry); //might need to find collision furest "into" the
		 * object
		 * 
		 * Log.info("_____________________________testing line: "+blx+","+bly+" to "
		 * +brx+","+bry);
		 * 
		 * 
		 * if (linecol!=null){
		 * 
		 * Window.setTitle("__COLLISION!!!____________");
		 * Log.info("_____________________________collision detected at "
		 * +linecol.X+","+linecol.Y);
		 * 
		 * } else { Window.setTitle("______________");
		 * Log.info("_____________________________");
		 * 
		 * }
		 * 
		 * //find collision displacement from objects pin location int cdx =
		 * linecol.X-(dx); int cdy = linecol.Y-(dy);
		 * 
		 * Log.info("_____________________________collision displacement: "+cdx+","
		 * +cdy);
		 * 
		 * //find collision again using this displacement
		 */

		MovementPath np = new MovementPath("", "_internal_");

		np.add(new MovementWaypoint(startx, starty, MovementType.AbsoluteMove)); 
		
		CollisionLogLog( "<------- starting at:"+startx+","+starty,"green");
		CollisionLogLog( "<------- heading to:"+dx+","+dy,"grey");
		

		// temp box is just the size, in future we will have this stored
		//	SceneCollisionMap.currentIteration=0; //used for emergency exits

		object.objectsCurrentPathfindingIterator=0; //reset iteration count
		MovementPath safepath = findPathBetweenAndAddToExistingPath(dx, dy, np, searchdirection.both,object,null);





		safepath = refinePath(safepath,object);

		//if we have bounce on, and previously we overshot the end, we add the little bounce effect here
		if (bounceOn && (prevEndX!=-1) && (prevEndY!=-1) ){
			//rather then going fully to the previous point we go half way between to soften the effect
			//(and also ensure we don't go into the scenery more then a few pixels)
			prevEndX =  (int) ((prevEndX + dx)/2.0);
			prevEndY =  (int) ((prevEndY + dy)/2.0);
			

			CollisionLogLog( "adding bounce:"+prevEndX+","+prevEndY+">>>"+dx+","+dy,"green");
			

			//add the extra endpoints to make the bounce
			safepath.add(new MovementWaypoint(prevEndX,prevEndY, MovementType.AbsoluteLineTo));
			safepath.add(new MovementWaypoint(dx, dy, MovementType.AbsoluteLineTo));

		}

		if (CMapVisualiser.isPresent()){

			CMapVisualiser.get().svgSmoothPathUpdate(safepath.getAsSVGPath());
			CMapVisualiser.get().generatePreviewWidget();			
		}

		return safepath;

	}




	

	public ArrayList<MovementWaypoint> findSafeEndpointOnLine(int startx, int starty, int destinationx,
			int destinationy, SceneObject object) {
		
		ArrayList<MovementWaypoint> pathWithSafeEnding = new ArrayList<MovementWaypoint>();
		
		int destx = destinationx;
		int desty = destinationy;
		
		double distanceX = Math.abs(destx - startx);	
		double distanceY = Math.abs(desty - starty);

		int EmergancyExit = 15; //should should have a option to vary this
		
		//this angle is expressed as the ratio of x to y and y to x
		//in future we might need this in 3D
		double ratio  = (distanceX * 1.0) / ((distanceY + distanceX) * 1.0);
		double ratio2 = (distanceY * 1.0) / ((distanceX + distanceY) * 1.0);
		
		//its colliding so move back
		if (distanceX == 0) {
			ratio  = 0;
			ratio2 = 1;
		}
		if (distanceY == 0) {
			ratio  = 1;
			ratio2 = 0;
		}

		int previousEndx=-1;
		int previousEndy=-1;
		
		while (	(isObjectColliding(object, destx, desty,0)!=null)) //z not used yet hence zero
		{
	
			//move back by 40 pixels, making sure to go in the correct direction (ie, positive or negative)
			//Thats what the signum bit does
			int disx = (int) (ratio    * 40 * (Math.signum(destx - startx)));
			int disy = (int) ((ratio2) * 40 * (Math.signum(desty - starty)));

			Log.info("move back by =" + disx + "," + disy);

			//remember previous end points (needed for bounce effect as we need the penulitmate ones once we find the real end)
			previousEndx = destx;
			previousEndy = desty;
			//--

			destx = destx - (disx);
			desty = desty - (disy);


			Log.info("new requested end set too:" + destx + "," + desty);


			EmergancyExit--;

			if (EmergancyExit < 0) {
				Log.info("________________________________too many loops when finding safe spot");
				break;
			}



		}
		if (previousEndx!=-1 && previousEndy!=-1 ){
			pathWithSafeEnding.add(new MovementWaypoint(previousEndx,previousEndy,MovementType.AbsoluteLineTo));
		}
		pathWithSafeEnding.add(new MovementWaypoint(destx,desty,MovementType.AbsoluteLineTo));
		return pathWithSafeEnding;
	}

	/**guess what this does? */
	static public void openLog(){

		//	CollisionLog.show();
		//	CollisionLog.log("opening log", "#00FF00");

		if (CollisionLogger.isPresent()){
			Log.info("opening collision log");
			CollisionLogger.get().show();
			CollisionLogger.get().log("opening log", "#00FF00");
			Log.info("opened collision log");
			
		} else {
			Log.info("no collision log present");
			
		}

	}
	/**too tricky to explain **/
	static public void hideLog(){
		//CollisionLog.hide();

		if (CollisionLogger.isPresent()){
			CollisionLogger.get().hide();
		}

	}


	public boolean isPathVisible() {

		if (CMapVisualiser.isPresent()){
			return CMapVisualiser.get().isPathVisible();
		}

		return false;
	}

	public boolean isCmapVisible() {

		if (CMapVisualiser.isPresent()){
			return CMapVisualiser.get().isCmapVisible();
		}
		return false;
		//return CMapVisualiser.isCmapVisible();
	}
	public void showPath(boolean b) {

		if (CMapVisualiser.isPresent()){
			CMapVisualiser.get().showPath(b);	
		}

	}

	public void showCmap(boolean b) {

		if (CMapVisualiser.isPresent()){
			CMapVisualiser.get().showCmap(b);	
		}

		//CMapVisualiser.showCmap(b);	

	}

	public void removeFromSketch(String asSVGPath, String color) {
		if (CMapVisualiser.isPresent()){
			CMapVisualiser.get().removeFromSketch(asSVGPath, color);	
		}
		//CMapVisualiser.removeFromSketch(asSVGPath, color);		
	}

	public void clearCalculatedPath() {
		if (CMapVisualiser.isPresent()){
			CMapVisualiser.get().clearCalculatedPath();
		}
		//	CMapVisualiser.clearCalculatedPath();	
	}

	//will have to be changed to object?
	//alternatively we could cast within SceneWidgetVisualiser the CmapVisualising class to its implementation
	//then get the widget from there?
	/**
	 * returns the object used to visualize ethe CMap
	 * It should be cast only to whats returned by getCollisionMapPreviewWidget();
	 * @return
	 */
	public Object getCMapPreviewWidget() {

		if (CMapVisualiser.isPresent()){
			return CMapVisualiser.get().getCollisionMapPreviewWidget();
		}
		//return CMapVisualiser.getCollisionMapPreviewWidget();
		return null;

	}
}
