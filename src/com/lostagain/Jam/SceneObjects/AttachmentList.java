package com.lostagain.Jam.SceneObjects;

import java.util.Collection;

import com.lostagain.Jam.Movements.MovementList;
import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Movements.MovementWaypoint;

/** used to specify points on an object things can be positioned relative too.
 * eg, "head" "feat" etc**/
public class AttachmentList {

	/**
	 * If a pin set exists with this name, it represents the point the object is positioned relative too.
	 * If no pin is set here, we use the one specified by the objects parameters.
	 * (most of the time thats good enough, but sometimes you need a animated pin)
	 */
	final public static String PrimaryPinName = "PRIME_PIN";
	
	MovementList attachmentPointLists;
	
	
	/** loads attachment points from a file
	 * The file is exactly like a path file
	 * Only use the word "-Points" rather then "-Path "
	 * 
	 * This is because while its not a SVG, it is a list of co-ordinates so it has similar processing of its file
	 * **/
	public AttachmentList(String SerialisedPoints) {
		
		SerialisedPoints=SerialisedPoints.replace("-Points","-Path");
		
		attachmentPointLists = new MovementList(SerialisedPoints);

	}

	/** creates an empty movement list **/
	public AttachmentList() {				
		attachmentPointLists = new MovementList();
	}
	
	public void addAttachmentList(String name, String points) {
		
		//convert the point list to an svg path
		//not that its in any way really a path, its just a collection of points
		//but seeing as a path is also, sort of, a collection of points we can use MovementPath 
		//to make this all work, provided we trick it into thinking its a path!
		points = "M "+points+" z";		
		
		attachmentPointLists.put(name, new MovementPath(points,name));
	}
	
	/** eg
	 * getPointsFor("Hand",3)
	 * @return MovementWaypoint **/
	public MovementWaypoint getPointsFor(String attachementName,int FrameNumber){
		
		MovementPath  wp = attachmentPointLists.get(attachementName);
		
		if (wp!=null){
			return wp.get(FrameNumber);
		} else {
			return null;
		}
		
	}

	public int numberOfSets() {
		
		return attachmentPointLists.size();
	}
	
	public String serialise(){
		String test="";
		Collection<MovementPath> points = attachmentPointLists.values();
		
		for (MovementPath movementPath : points) {
			
			String pathData = movementPath.getAsSVGPath(true);

			//remove svg bits
			pathData = pathData.replace("l", "");	
			pathData = pathData.replace("m", "");
			pathData = pathData.replace("z", "");
			pathData = pathData.replace("L", "");
			pathData = pathData.replace("M", "");
			pathData = pathData.replace("Z", "");
			
			//
			
			test=test+"\n"+movementPath.pathsName+"\n"+pathData;
			
		}
		
		
		return test;
		
	}

	/**
	 * does this file have data representing the objects primary pin?
	 * 
	 * @return
	 */
	public boolean hasPrimaryPinData(){
		return attachmentPointLists.containsKey(PrimaryPinName);
	}
	
	/**
	 * 
	 * @param FrameNumber
	 * @return either the pin data specified in the file, or null if there isnt any
	 */
	public MovementWaypoint getPrimaryPinDataFor(int FrameNumber){
		
		MovementPath  wp = attachmentPointLists.get(PrimaryPinName);
		
		if (wp!=null){
			return wp.get(FrameNumber);
		} else {
			return null;
		}
		
	}
	
	
}
