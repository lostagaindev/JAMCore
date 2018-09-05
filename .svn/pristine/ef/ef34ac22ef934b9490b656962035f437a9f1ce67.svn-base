package com.lostagain.Jam.Scene;

import com.lostagain.Jam.Movements.SimpleVector3;

public abstract class CoordinateSpaceConvertor {

	public enum CordinateInterpretation{
		/**
		 * To display a gwt designed web scene. 
		 * Y is interpreted as top down coordinates.
		 * Z is interpreted as a vertical  displacement in Y.
		 * Effective this produces a "3/4ths" or fake "isometrix" view. It should be used with a outhgraphic camera directly overhead.
		 */
		Web2D,
		//todo; webisometrix with y scaling. same as web2d, but takes into account that Y is squashed in isometrix views
		//(true iso is half y) http://stackoverflow.com/questions/10506502/what-is-the-connection-between-an-isometric-angle-and-scale 
		/**
		 * Standard libgdx uses. No conversion will take place
		 */
		Gdx3d
	}


	private static CoordinateSpaceConvertor implemenation;
	

	
	
	/**
	 * must be initialised with a implementation extending this class
	 * 
	 * @param implemenation
	 */
	public static void setup (CoordinateSpaceConvertor implemenation) {
		CoordinateSpaceConvertor.implemenation=implemenation;
	}
	
	
	/**
	 * must be implemented
	 * 
	 * 
	 * @param incomingCordinates - coordinates from jam file
	 * @param mode - how to interpret the co-ordinates
	 * @return - coordinates suitable for the display implementation
	 */
	public abstract SimpleVector3 convert_impl(SimpleVector3 incomingCordinates,CordinateInterpretation mode);
	
		 
	
	public static  SimpleVector3 convert(SimpleVector3 incomingCordinates,CordinateInterpretation mode){
		return implemenation.convert_impl(incomingCordinates, mode);
	}
	

}