package com.lostagain.Jam.CollisionMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.SceneObjects.CollisionBox;
import com.lostagain.Jam.SceneObjects.SceneObject;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/** A polygon collision map is an arraylist of polygons <br>
 * It is used for two things;<br>
 * a) defining the collidable regions within an object, for the pathfinding system.<br>
 * b) defining named regions for other purpose's, which are "incomporal" but can still
 * be tested with the "iswithin" conditional check. This could be used to test FOV on a character,
 * for example <br>**/
public class PolygonCollisionMap extends ArrayList<Polygon> {


	
	public static Logger GreyLog = Logger.getLogger("JAMCore.PolygonCollisionMap");
	String PathColor = "red";

	//public HTML svgPreview = new HTML();

	/** if this map is associated with an object, this is the default Object its associated with.
	 * The majority of maps will be linked to an object, and move with them.
	 * 	 * 
	 * Note; While common,this is not necessarily the same object as associated with the polygons that make the map up.
	 * This is because a PolygonCollisionMap may be generated as a "holder" for maps linked to other objects,
	 * allowing a whole set of connected objects to be tested at once.
	 * 
	 * For this reason the polygons own associated object referance should be used
	 * see getCompoundCmap() in sceneobject **/
	public SceneObject defaultAssociatedObject = null; 

	/** used for debugging purposes, ie, for highlighting specific sides which have been collided with **/
	public int currentPoly=0;
	/** used for debugging purposes, ie, for highlighting specific sides which have been collided with **/
	public int currentSide=0;

	boolean hasCoporalPart = false;
	
	String DefaultPolygonName = "sceneobject";

	
	
	public PolygonCollisionMap(String rawData, SceneObject associatedObject) {	
		this( rawData,  associatedObject,  "sceneobject");
		
	}
	
	/** A polygon collision map is an arraylist of polygons <br>
	 * It is used for two things;<br>
	 * a) defining the collidable regions within an object, for the pathfinding system.<br>
	 * b) defining named regions for other purpose's, which are "incomporal" but can still
	 * be tested with the "iswithin" conditional check. This could be used to test FOV on a character,
	 * for example. <br>
	 * <br>
	 * The raw data you supply to set up the map is of the form:<br>
	 * 	 M 66,365 138,376 431,200 315,136 z<br>
	 * Essentially an svg map <br>
	 * <br>
	 * to specify a named region use:<br>
	 *  fov=M 25,76 -379,45 -407,-97 -14,-326 67,42 z<br>
	 *  where fov is the name<br>
	 *  <br>
	 * @param rawData - a collection of paths representing the object
	 * @param associatedObject - the object this map is linked too
	 * 
	 * @param rawData
	 * @param associatedObject
	 * @param defaultPolyName - if null associatedObject then supply name
	 */
	public PolygonCollisionMap(String rawData, SceneObject associatedObject, String defaultPolyName) {

		//save the associated object
		this.defaultAssociatedObject = associatedObject;

		//set the name
		DefaultPolygonName = defaultPolyName;//"sceneobject";

		//set the name to match any associated objects name
		if (associatedObject!=null){
			DefaultPolygonName=associatedObject.getObjectsCurrentState().ObjectsName;
		}


		// Divide the raw data by lines
		String lines[] = rawData.trim().split("\r");

		for (String line : lines) {

			// line should be a SVG line format.
			// If there is a equals however, the words before are assumed to 
			// be a region name. In which case, its cropped off and the new polygon made
			// incomporal.

			if (line.length()>3){

				String polyname = DefaultPolygonName;
				Boolean ghost = false;

				if (line.contains("=")){
					//eg 
					// wendyssight=M 34,34 45,45 34,56 z	

					//we separate of the name, and turn ghostmode on....owww...spooky ghost mode :)
					polyname = line.split("=")[0].trim();				
					line     = line.split("=")[1];	
					ghost 	 = true;

				}

				//create a new polygon from the line data
				Polygon np = new Polygon(line.trim(), polyname,this,ghost);

				//generate outset path assuming its not incomporal
				if (!np.incorporeal){
					//the outside path is the collision map expanded outwards a bit
					//this is to let characters walk around it, leaving a bit of a margin
					//rather then hugging infinitely close
					np.generateOutSetPath();
					hasCoporalPart = true; //if any part of this is corporal this should be true
				} else {
					
				}

				//add the polygon to this maps list of polygons
				super.add(np);

			}




		}

		//create a new preview
		//svgPreview = new HTML(
		//		"<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" height=\"40000\" width=\"40000\">"+this.getPath()+"</svg>");

		//if theres an associated object, add something to that objects log, so we can check
		//the path easily in the inspector
		if (associatedObject!=null){
			associatedObject.ObjectsLog("svg path is:"+this.getPath(), "blue");
		}
		//	Log.info("svg="+svgPreview.getHTML());


	}

	/** returns the path of the current highlighted side.
	 * Used for collision debugging, where the highlighted side is typically the side collided into last **/
	public String getCurrentHighlightedSide(){

		return getSidesPath(currentPoly, currentSide);

	}

	/** the the svg path of a specified polygon and side number **/
	public String getSidesPath(int PolygomNum,int sidenumber) {
		String Path="";

		Path="<path id=\"linesSide\" d=\""
				+ this.get(PolygomNum).getSideAsPath(sidenumber).getAsSVGPath()
				+ "\" stroke=\""
				+ "green"
				+ "\" stroke-width=\"3\" fill=\"rgba(5,65,5,0.5)\" />";


		return Path;

	}


	String COLLISIONMAPNAME = "COLLISIONMAP";

	/** check if a specified point is within the specified region **/
	public boolean checkWithinRegion(String regionName, int xToTest, int yToTest)
	{		
		boolean isInside = false;
		
		if (regionName.equals(COLLISIONMAPNAME)){
			regionName = DefaultPolygonName;
		}
		
		//get the right polygon
		for (Polygon poly : this) {			
			//if its name matches the name we are looking for
			//then we test it
			if (poly.getName().equalsIgnoreCase(regionName)){
				isInside= poly.isPointInside(xToTest,yToTest);
			//	GreyLog.info("tested region:"+regionName+" contains "+xToTest+","+yToTest+":"+isInside);
				if (isInside){
					return true; //we exit on first one found inside. This is not just more efficient, but needed as many polygons might have the DefaultPolygon name, and thus a true will be overwritten with a later false
				}
				
			}

		}
		//returns true if the tested x and y is within the specified region
		return isInside;		
	}

	/**
	 * tests all the polygons in this map against the specified line, assuming the line is at ground level.
	 * 
	 * @param map
	 * @return
	 */
	public boolean isCollidingWith(Simple2DPoint linestartatground,Simple2DPoint lineendatground){

		//convert to 3d points at zero z
		Simple3DPoint linestart = new Simple3DPoint(linestartatground.x,linestartatground.y,0);
		Simple3DPoint lineend = new Simple3DPoint(lineendatground.x,lineendatground.y,0);
		
		
		//all polygon
		for (Polygon polygon : this) {
			
			if (polygon.incorporeal){
				continue;
			}

			if (polygon.isLineTouching(linestart, lineend)){
				//GreyLog.info("line was touching");
				
				return true;
			} else {
				//if no lines are touching theres a chance its fully enclosed by this polygon
				//to work this out we test a single endpoint of the line
				//if the end is within and nothing touches then it all must be within
				if (polygon.isPointInside(linestart.x, linestart.y)){
				//GreyLog.info("point was inside");
					
					return true;
				}
			}

		}

		return false;				
	}

	public Polygon isCollidingWith(PolygonCollisionMap map)
	{
		
	return	isCollidingWith(map, 0, 0, false);
		
	}
	/**
	 * tests all the polygons in this map against all in the specified map
	 * if any touch returns true
	 * 
	 * @param map
	 * @param tly - displacement X
	 * @param tlx - displacement X
	 * 
	 * NOTE: If you supply your own displacement you should also override the natural displacement of the associated object
	 * This is the default if no values are supplied at all.
	 * Displacement values generally are only needed if you are predicting a path for a object, and thus its real position
	 * is irrelevant
	 * @return
	 * 
	 */
	public Polygon isCollidingWith(PolygonCollisionMap map, int dx, int dy, boolean overrideAssociatedObjectsDisplacement)
	{
		
		//all polygon
		for (Polygon polygon : this) {
			
			//skip incoporal
			if (polygon.incorporeal){
				continue;
			}
						

			for (Polygon testpolygon : map) {
				//skip incoporal
				if (testpolygon.incorporeal){
					continue;
				}
				
				//skip if we are colliding with ourselves
				if (testpolygon==polygon){
					continue;
				}
				
				//first test for fully enclosed;
				
				//if any point of testpolygon is inside polygon then we are colliding
				int testCorner0X = testpolygon.getWaypointX(0,overrideAssociatedObjectsDisplacement)+dx;
				int testCorner0Y = testpolygon.getWaypointY(0,overrideAssociatedObjectsDisplacement)+dy;
				
				if (map.defaultAssociatedObject!=null){
				  GreyLog.info("   testing first cornner is inside this map: "+map.defaultAssociatedObject.getName()+" - "+testCorner0X+","+testCorner0Y+"   (dis:"+dx+","+dy+") "+overrideAssociatedObjectsDisplacement);
				} else {
				  GreyLog.info("   testing first cornner is inside this map:  - "+testCorner0X+","+testCorner0Y+"   (dis:"+dx+","+dy+") "+overrideAssociatedObjectsDisplacement);
				}
				
				if (polygon.isPointInside(testCorner0X,testCorner0Y)){
					map.defaultAssociatedObject.ObjectsLog("Hit was way0 of:"+testpolygon.getName()+">with>"+polygon.getName(), "Orange");
					
					return polygon;					
				}
				
				
				
				//if any point polygon of is inside testpolygon then we are inside
				int fx = polygon.getWaypointX(0)-dx;
				int fy = polygon.getWaypointY(0)-dy;
				
					GreyLog.info("   testing first corner of this map at - "+fx+","+fy+"   (dis:"+dx+","+dy+") "+overrideAssociatedObjectsDisplacement);
				
				if (testpolygon.isPointInside(fx,fy)){ //TODO:testpolygon needs to be displaced
					//Or maybe we can displace the point the other way to compensate? Probably easier.
					//however, it will still have its normal displacement, so we dont turn that off
					//0,false,-dx ? 
					//0,false,-dy ? 
					map.defaultAssociatedObject.ObjectsLog("Hit was way1 of:"+testpolygon.getName()+">with>"+polygon.getName(), "Orange");
					
					
					
					return polygon;					
				}
				
				//else we check the whole polygon
				boolean test = polygon.isPolygonTouching(testpolygon,dx,dy,overrideAssociatedObjectsDisplacement);
				if (test){
					
					map.defaultAssociatedObject.ObjectsLog("Hit was poly of:"+testpolygon.getName()+">with>"+polygon.getName(), "Orange");
					
					return polygon;
				}
				
				
				
			}
			

		}

		return null;		
	}

	public boolean isCollidingWith(CollisionBox ourbox) {

		//test all the sides of the box
		Simple2DPoint topleft = ourbox.topleft;
		Simple2DPoint bottomright = ourbox.bottomright;
		
		//deduce the other corner
		Simple2DPoint topRight = new Simple2DPoint(bottomright.x,topleft.y);
		Simple2DPoint bottomleft = new Simple2DPoint(topleft.x,bottomright.y);

		//test each side
		if (isCollidingWith(topleft, topRight) ) {
		//	GreyLog.info("isCollidingWith topleft topRight");			
			return true;
		}

		if (isCollidingWith(topRight, bottomright) ) {
		//	GreyLog.info("isCollidingWith topRight bottomright");	
			return true;
		}
		if (isCollidingWith(bottomright, bottomleft) ) {
		//	GreyLog.info("isCollidingWith bottomright bottomleft");	
			return true;
		}

		if (isCollidingWith(bottomleft, topleft) ) {
		//	GreyLog.info("isCollidingWith bottomleft topleft");	
			return true;
		}
		return false;
	}
	
	public String getPath() {
		String color = "rgba(65,5,5,0.5)";
		return getPath(color,true, new SimpleVector3(1.0,1.0,1.0));
	}

	
	public String getPath(SimpleVector3 scaleNorm) {
		String color = "rgba(65,5,5,0.5)";
		return getPath(color,true,scaleNorm);
	}

	/**
	 * get the path of this collision map at a svg path
	 * (which can be many paths added together)
	 * most useful for debugging/visualizing the path
	 **/
	public String getPath(String color, boolean includeNormals, SimpleVector3 scaleNorm) {
		GreyLog.info("getting path from "+this.size()+" polys");
		
		//setup preview (alter to support full array)
		String Path="";
		String usecolor=color;
		Iterator<Polygon> pit= super.iterator();

		GreyLog.info("iteration....");
		
		
		while (pit.hasNext()) {

			String id ="lineBC";
			Polygon polygon = (Polygon) pit.next();
			
			if (polygon.incorporeal){
				usecolor = "rgba(5,65,5,0.5)";
				id=id+"_inc_";
			} else {
				usecolor = color;// "rgba(65,5,5,0.5)";
				id="lineBC";
			}


			
			if (polygon.associatedObject!=null){	
				

				GreyLog.info("getting path from poly with associated object");
				
				
				id=id+"_"+polygon.associatedObject.getName();
				
				Path=Path+"<path id=\""+id+"\" d=\""
						//	+ polygon.getAsDisplacedSVGPath(associatedObject.getTopLeftX() , associatedObject.getTopLeftY() )
						+ polygon.getObjectRelativeSVGPath()
						+ "\" stroke=\""
						+ PathColor
						+ "\" stroke-width=\"3\" fill=\""+usecolor+"\" />\n";
				
			} else {

				GreyLog.info("getting path from poly");
				
				Path=Path+"<path id=\""+id+"\" d=\""
						+ polygon.getAsSVGPath()
						+ "\" stroke=\""
						+ PathColor
						+ "\" stroke-width=\"3\" fill=\""+usecolor+"\" style=\"opacity:0.5\" />\n";

			}
			
			//also display the normal lines if requested
			if (includeNormals){
				
				/*
				SimpleVector3 scaleNorm= new SimpleVector3(1.0,1.0,1.0);
				
				if (polygon.associatedObject!=null){	
					
					if (polygon.associatedObject.getParentScene()!=null){	
						
						scaleNorm=polygon.associatedObject.getParentScene().getNormalScaleingForReflections();
						
					}
					
				}*/
					
				
				
				Path=Path+"<path id=\"lineNorm\" d=\""
						+ polygon.getPathsOfAllNormals(scaleNorm)
						+ "\" stroke=\""
						+ "Green"
						+ "\" stroke-width=\"3\" />\n";

			}


		}
		return Path;
	}

	/**
	 * Returns true if the point is inside the polygon
	 * @param point
	 * @return
	 */
	public boolean isCollidingWith(Simple2DPoint point) {


		//all polygons
		for (Polygon polygon : this) {
			//skip incoporal 
			if (polygon.incorporeal ){
				continue;
			}

			if (polygon.isPointInside(point.x, point.y)){
				GreyLog.info("point "+point+" was inside polygon");
				
				
				return true;
			}


		}

		return false;
	}

	/**
	 * loops over all our polygons clearing their touching cache
	 */
	public void clearTouchingCache() {

		for (Polygon poly : this) {
			poly.clearKnownPolygonCache();
		}

	}

	/**
	 * returns false only if all parts of this polygon are non-corporal or we are completely empty
	 * This result is worked out on loading and never changed. adding/removing polys might result in it inconsistant
	 * @return
	 */
	public boolean hasCoporalPart() {
		if (isEmpty()){
			return false;
		}		
		
		
		return hasCoporalPart;
	}

	/**
	 * removes all non-corporal polygons in this map.
	 * use with care
	 */
	public void removeIncoporalBits() {
		
		for (Iterator<Polygon> iterator = this.iterator(); iterator.hasNext();) {
			Polygon poly = (Polygon) iterator.next();			
			if (poly.incorporeal){
				iterator.remove();
			}						
		}
		
		
		
	}
	
	

	@Override
	public boolean add(Polygon e) {
		if (!e.incorporeal){
			hasCoporalPart=true;
		}
		return super.add(e);
	}

	/**
	 * untested
	 * 
	 * @param topLeft
	 * @param bottomRight
	 * @return
	 */
	public static PolygonCollisionMap generateRect(Simple2DPoint topLeft, Simple2DPoint bottomRight, SceneObject associatedObject) {
	
		PolygonCollisionMap newmap = new PolygonCollisionMap("",associatedObject,"generatedRect");
		Polygon poly = new Polygon("","GeneratedRect",newmap);
		poly.associatedObject=associatedObject;
		poly.addMovementWayPoint(MovementType.AbsoluteMove, topLeft.x, bottomRight.y); //left bottom
		poly.addMovementWayPoint(MovementType.AbsoluteLineTo, bottomRight.x, bottomRight.y); //right bottom
		poly.addMovementWayPoint(MovementType.AbsoluteLineTo, bottomRight.x, topLeft.y); //right top
		poly.addMovementWayPoint(MovementType.AbsoluteLineTo, topLeft.x, topLeft.y); //left top
		
		poly.addMovementWayPoint(MovementType.AbsoluteLoopToPathStart, -1,-1);

		GreyLog.info("made rectangle poly:"+poly.getAsSVGPath());
		
		poly.generateOutSetPath(); //ALWAYS TRIGGER THIS AFTER PATH CREATION!
		
		newmap.add(poly);

		GreyLog.info("made rectangle cmap:"+newmap.size());
		
		return newmap;
	}

	/*
	/**
	 * loops over all our polygons clearing their touching cache
	 *
	public void removeFromCache(ArrayList<Polygon> objects) {

		for (Polygon poly : this) {
			poly.removeFromCache(objects);
		}

	}*/


}
