package com.lostagain.Jam.CollisionMap;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lostagain.Jam.Movements.MovementWaypoint;
import com.lostagain.Jam.Movements.SimpleVector3;
import com.lostagain.Jam.Movements.MovementWaypoint.MovementType;

import lostagain.nl.spiffyresources.client.spiffycore.Simple2DPoint;
import lostagain.nl.spiffyresources.client.spiffycore.Simple3DPoint;

/** defines a single edge of a polygon **/
public class PolySide {

	
	public static Logger Log = Logger.getLogger("JAMCore.PolySide");
	int startVertexNumber = -1;
	int endVertexNumber = -1;
	
	Simple3DPoint startPoint;
	Simple3DPoint endPoint;
	
	Polygon source;

	/**
	 * assumes a ground level line (z=0)
	 * 
	 * @param startPoint
	 * @param endPoint
	 * @param startVertexNumber
	 */
	public PolySide(Simple2DPoint startPoint, Simple2DPoint endPoint,
			int startVertexNumber) {
		
		this(new Simple3DPoint(startPoint.x,startPoint.y,0), 
			 new Simple3DPoint(endPoint.x,endPoint.y,0), 
			 startVertexNumber);
		
	}
	
	public PolySide(Simple3DPoint startPoint, Simple3DPoint endPoint,
			int startVertexNumber) {
		super();
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startVertexNumber = startVertexNumber;
		this.endVertexNumber = startVertexNumber + 1;
	}
	
	public PolySide(Simple3DPoint startPoint, Simple3DPoint endPoint,
			int startVertexNumber,Polygon source) {
		super();
		this.source=source; //the polygon this side belongs too
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.startVertexNumber = startVertexNumber;
		this.endVertexNumber = startVertexNumber + 1;
	}

	/** returns a string in the form
	 * x,y --> x2,y2 
	 * where x,y is the start point of this side and x2,y2 is the end **/
	public String toString(){
		return startPoint.x+","+startPoint.y+"-->"+endPoint.x+","+endPoint.y;
	}

	/**
	 * Returns the inner or outer normal 
	 * @param inner- true for inner false for outer (assuming anti-clockwise construction)
	 * @return 
	 */
	public SimpleVector3 getNormal(boolean inner){
		
		//work out normals
		int dx = endPoint.x-startPoint.x;
		int dy = endPoint.y-startPoint.y;

		SimpleVector3 normal1 = new SimpleVector3(-dy,dx,0); //looks weird, but yes, swapping the x and y is correct here
		SimpleVector3 normal2 = new SimpleVector3(dy,-dx,0); //looks weird, but yes, swapping the x and y is correct here

		if (!inner){
			return normal1;
		} else {
			return normal2;
		}
	}
	
	
	
	public MovementWaypoint nearestPointOnThisTo(int targetX, int targetY){//,int x1, int y1, int x2,int y2) {
		//----
		int x1 = this.startPoint.x;
		int y1 = this.startPoint.y;
		int x2 = this.endPoint.x;
		int y2 = this.endPoint.y;
		//----

		int A = targetX - x1; //difference between start and target
		int B = targetY - y1;
		int C = x2 - x1;
		int D = y2 - y1;

		double dot = A * C + B * D;
		double len_sq = C * C + D * D;
		double param = -1;

		if (len_sq != 0){ //in case of 0 length line
			param = dot / len_sq;
		}

		double xx, yy;

		if (param < 0) {
			xx = x1;
			yy = y1;
		} else if (param > 1) {
			xx = x2;
			yy = y2;
		} else {
			xx = x1 + param * C;
			yy = y1 + param * D;
		}

		MovementWaypoint point = new MovementWaypoint((int)xx,(int)yy,MovementType.AbsoluteMove);

		return point;
	}
	public void displaceBy(int dx, int dy) {
		
		startPoint.x = startPoint.x + dx;
		startPoint.y = startPoint.y + dy;
		endPoint.x = endPoint.x + dx;
		endPoint.y = endPoint.y + dy;
		
	}
	
	public Simple3DPoint getStart() {
		return startPoint;
	}
	public Simple3DPoint getEnd() {
		return endPoint;
	}
	
	/**
	 * given a list of sides this will return points evenly spaced along them.
	 * sides that touch will act continuous
	 * sides that do not will start from their start point
	 * 
	 * @param edges
	 * @return
	 */
	public static ArrayList<Simple3DPoint> getEvenlySpacedPointsAlong(ArrayList<PolySide> edges, int spaceing){
		

		Log.info("Getting points along  "+edges.size()+" sides at interval "+spaceing);
		ArrayList<Simple3DPoint> points = new ArrayList<Simple3DPoint> ();
		
		PolySide lastside=null;
		double leftover = spaceing; //as we start at zero
		double rem=0;
		for (PolySide polySide : edges) {
			
			//touchs last side?
			if (lastside!=null && lastside.endPoint.equals(polySide.startPoint) ){
				Log.info("side connects to last which had "+rem+" left over");
				leftover = rem; //if we dont start from zero, we also alter the length when calculating how many more we can fit
			} else {
				leftover = spaceing;
			}
			
			
			//get dy/dx for polySide
			double dx = polySide.endPoint.x-polySide.startPoint.x;
			double dy = polySide.endPoint.y-polySide.startPoint.y;
			
			
			//how many points fit on this line?(round down,remember difference)
			double length = Math.hypot(dx, dy);
			double numOfPoints = Math.floor((length-rem)/spaceing); //(always 1 at start)
			rem = ((length-rem) % spaceing);
			
			//then normalize (so how much vert/hor displacement per pixel of spacing along the diagonal
			//double totalMagnitude = Math.abs(dx)+Math.abs(dy);
			double dxn = dx / length;
			double dyn = dy / length;
			
			Log.info("# points that fit along side:"+polySide.toString()+" = "+numOfPoints+"   (rem="+rem+", len="+length+")");
			Log.info("dxn:"+dxn+" dyn:"+dyn);

			double gap = spaceing;
			double pixelsAlongLine = spaceing-leftover; //needs to be worked out still. should this adjust length?
			//first point on line used should be (spaceing-the last rem)
			
			for (int i = 0; i <= numOfPoints; i++) {
								
				
				int newpointx = (int) Math.round(  dxn * pixelsAlongLine)+polySide .startPoint.x;
				int newpointy = (int) Math.round(  dyn * pixelsAlongLine)+polySide.startPoint.y;
				int newpointz = 0;
				
				Log.info("adding point at:"+newpointx+","+newpointy+","+newpointz);
				
				Simple3DPoint pointAlongLine = new Simple3DPoint(newpointx, newpointy,newpointz);
				
				points.add(pointAlongLine);
				
				pixelsAlongLine =  pixelsAlongLine + gap;
				
			}
			
			
			
			//work out new point based on above
			
			
			lastside = polySide;
			
		}
		
		
		return points;		
	}

}