package com.lostagain.Jam.SceneObjects;

import java.util.logging.Logger;

import com.lostagain.Jam.CollisionMap.Polygon;


/** Will replace 2D one? maybe?
 * 
 * 
 * upgrade needed;
 * 
 * is currently 2d only :-/
make 3d compatible with 2d default:


2D assumptions;
1 infinitely thin vertical aligned box. Width refers to the line at its base, Height to the vertical Z axis.
Therefor
CollisionBox(width,height)

would give
MinXYZ = Vector3(0,0,0)
MaxXYZ = Vector3(width,0,height);
as its size.

with no depth in y ("into the screen" if viewed from the front (so 'into the monitor').
We then have functions to return getHeight and getWidth carefully explaining what they are.

We also need to transfer the intersection function, which is strictly 2D. Again, carefully explain its for 2d use only (that is, takes no account of "y"). Its still required for z-index decuding on 2d games, however.
This is because we need to know what visually overlaps in order to work out zindexs, even if gameplay wise they are seperated by depth. (say, if the games isometrix).


 * 
 * @author darkflame
 *
 */

public class CollisionBox3D {
	
	public static Logger Log = Logger.getLogger("JAMCore.CollisionBox3D");

	/*

	public Simple3DPoint minXYZ = new Simple3DPoint(0,0,0);
	public Simple3DPoint maxXYZ  = new Simple3DPoint(0,0,0);

	public CollisionBox3D(Simple2DPoint topleft, Simple2DPoint bottomright) {
		super();
		
		this.topleft = topleft;
		this.bottomright = bottomright;
	}


	/**
	 * parameters should be in the format  (tlx,tly,brx,bry for 2d)
	 *
	public CollisionBox3D(String parameters) {

		String coordinates[] = parameters.split(",");

		topleft.x     = Integer.parseInt(coordinates[0]);
		topleft.y     = Integer.parseInt(coordinates[1]);
		bottomright.x = Integer.parseInt(coordinates[2]);
		bottomright.y = Integer.parseInt(coordinates[3]);

	}


	public boolean isPointInside(int x, int y) {

		if (x>topleft.x && x<bottomright.x){
			if (y>topleft.y && y<bottomright.y){
				return true;
			}	
		}

		return false;
	}
	/**
	 * returns this box in string format;
	 * topleftx,toplefty,bottomright.x,bottomright.y
	 *
	public String toString(){
		return topleft.x + ","+topleft.y+","+bottomright.x+","+bottomright.y;
	}


	/**
	 * returns a svg path representing this box
	 * 
	 * @return
	 *
	public String getAsSVGPath() {

		String d="M "+topleft.x    +","+topleft.y+" "
				+"L "+bottomright.x+","+topleft.y+" "
				+"L "+bottomright.x+","+bottomright.y+" "
				+"L "+topleft.x    +","+bottomright.y+" z";


		return d;
	}


	//This only works in 2d space, not 3d.
	// future; The "box" tested against is the vertical rectangle of (0,0,0) to (X,0,Z). 
	// 
	/**
	 * checks if the line cross's or intersects with this box.
	 * 
	 * @param start
	 * @param end
	 * @return 
	 *
	public boolean isLineCrossing(Simple2DPoint start, Simple2DPoint end) {

		Log.info("Does line:"+start+">"+end+" cross or is within box "+this.toString());

		//TODO: we need to write a method that returns true or false depending if the line crosses this box

		//first check if either end of the line is inside
		//(in most cases this will be good enough)
		if (isPointInside(topleft.x, topleft.y)){
			Log.info("___________________contained within");
			return true;
		}
		if (isPointInside(end.x, end.y)){
			Log.info("___________________contained within");
			return true;
		}

		//Now we check if the line cross's the box, but doesnt end in it.
		//For collision detection this should be a lot less likely then the above, as it means a character has somehow ended up on either
		//side of a objects collision map at the same time.

		//We check for crossing lines by checking each side of this box as its own line against the supplied line.
		//Effectively making 4 separate "is line crossing line" tests

		//first we get the 4 corners of the box from the two we know already
		Simple2DPoint topLeft = topleft;
		Simple2DPoint topRight =  new Simple2DPoint(bottomright.x,topleft.y);
		Simple2DPoint bottomLeft =  new Simple2DPoint(topleft.x,bottomright.y);
		Simple2DPoint bottomRight =  bottomright;

		//then we test each side against the supplied line
		Simple2DPoint intersectPoint = Polygon.lineIntersect(start, end, topLeft, topRight);
		if (intersectPoint!=null){
			return true;
		}

		intersectPoint =Polygon.lineIntersect(start, end, topRight, bottomRight);
		if (intersectPoint!=null){
			return true;
		}

		intersectPoint = Polygon.lineIntersect(start, end, bottomRight, bottomLeft);
		if (intersectPoint!=null){
			return true;
		}

		intersectPoint = Polygon.lineIntersect(start, end, bottomLeft, topLeft);
		if (intersectPoint!=null){
			return true;
		}

		return false;
		
	}


*/


}