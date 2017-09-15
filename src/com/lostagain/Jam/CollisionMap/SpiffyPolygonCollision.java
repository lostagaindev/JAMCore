package com.lostagain.Jam.CollisionMap;

/**
 * This specifies a collision that has occurred. So where it's colliding, what it is colliding with.
 * 
 * And distance, but I'll have to check what it does.
 * 
 * Side is the side number it has collided with. All sides of the polygons are numbered.
 * 
 * @author Bertine
 *
 */
public class SpiffyPolygonCollision {
	

	public SpiffyPolygonCollision(int x, int y,Polygon collidingObject,
			PolySide side, int distance) {
		
		X = x;
		Y = y;
		
		this.side=side;
		this.collidingObject =collidingObject;
		this.distance = distance;
	}

	public int X = -1; // x location of collision
	
	
	public int Y = -1; // y location of collision
	
	public Polygon collidingObject;
	
	public int distance = 0;
	PolySide side;
	
	@Override
	public String toString() {
		
		String collidingObjectString = "(no object)";
		
		if (collidingObject!=null){
			collidingObjectString = collidingObject.getName();
		}
		
		
		return " pos=" + X + "," + Y + " (dis: "+distance+") " + collidingObjectString + "";
	}

	public PolySide getHitSide() {
		return side;
	}
	
	
	
}