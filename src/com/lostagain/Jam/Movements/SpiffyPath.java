/**
 * 
 */
package com.lostagain.Jam.Movements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

/**
 * 
 * Will eventually replace MovementPath
 * (or at least, a significant amount of its functionality. We might leave movementpath as a subclass of this with movement specific features)
 * 
 * Polygon meanwhile will also be a subclass of this, adding collision testing and side retrieving functions
 * 
 * Example path with 2 segments;
 * "M 918,578 L 817,661 L 906,779 L 1019,684 z M 915,600 L 989,686 L 905,755 L 834,660 z"
 * 
 * 
 * Each segment must start with a M
 * 
 * @author darkflame
 *
 */
public class SpiffyPath extends ArrayList<SpiffyPathSegment> {

	public static Logger Log = Logger.getLogger("JAMCore.SpiffyPath");

	public String pathsName = "";
	public int PathLength = 0; // length of path in pixels
	
	public String getName() {
		return pathsName;
	}

	public void setName(String pathsName) {
		this.pathsName = pathsName;
	}
	
	public SpiffyPath(String pathsName) {
		super();
		this.pathsName = pathsName;
	}
	
	public SpiffyPath(SpiffyPath cloneThisPath, String pathsName) {
		super(cloneThisPath);
		this.pathsName = pathsName;
		//refresh length
	}
	
	public SpiffyPath(String pathString, String name) {
		super(getPathSegmentsFromPathString(pathString));
		pathsName = name;
		
	}

	private static Collection<? extends SpiffyPathSegment> getPathSegmentsFromPathString(String pathString) {
		//String lccopy = pathString.toLowerCase(); //using a lowercase copy is easier to detect segment start/end, but the content is still case sensative
		//int SegmentStart = 0;
		//int SegmentEnd = pathString.length();		//
		//int PotentialSegmentEnd = lccopy.indexOf("M");		
		//split around the Ms
		//String segments[] = pathString.split("M|m");
		
		//-
		//---
		//-------
		//a bit silly
		//we replace M or m with M| or m|
		//Then split around the |
		//It would be easier if we could split around Mm without removing the M or m from the results.
		pathString = pathString.replace("M", "|M");
		pathString = pathString.replace("m", "|m");
		
		Iterable<String> segmentStrings = Splitter.on(CharMatcher.anyOf("|"))
	       .trimResults()
	       .omitEmptyStrings()
	       .split(pathString);
		
		ArrayList<SpiffyPathSegment> newsegments = new ArrayList<SpiffyPathSegment>();
		for (String segmentString : segmentStrings) {
			
			Log.info("segmentString="+segmentString);
			
			SpiffyPathSegment newSegment = new SpiffyPathSegment(segmentString);
			newsegments.add(newSegment);
		}
		
		
		return newsegments;
	}
	
	/**
	 * displays a summary of pathsegments
	 */
	public String toString() {
		String info = "";
		info =  info + this.size()+" \n Segments:\n";
		for (SpiffyPathSegment segment : this) {
			info =  info + "Segment:"+ segment.getAsSVGPath(true)+"\n";			
		}
		
		return info;
	}
	
	
	public String getAsSVGPath() {
		return getAsSVGPath(false);
	}
	public String getAsSVGPath(boolean insertzvalues) {
		String SVGString = "";
		
		for (SpiffyPathSegment segment : this) {
			SVGString =  SVGString + segment.getAsSVGPath(insertzvalues);			
		}
		
		return SVGString;
	}
	
	
}
