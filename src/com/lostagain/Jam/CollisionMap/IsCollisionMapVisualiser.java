package com.lostagain.Jam.CollisionMap;

import com.lostagain.Jam.Movements.MovementPath;
import com.lostagain.Jam.Scene.SceneWidget;

public interface IsCollisionMapVisualiser {
	
	/**
	 * The object used to preview path 
	 * in GWT implementations this will be a AbsolutePanel
	 * @return
	 */
	Object getSvgPathVisualisation();

	void setupVisualiser(SceneWidget sourceScene, PolygonCollisionMap scenesOwnMap);

	/** generate the preview widget from the maps path data 
	 * @param scenesOriginalObjects **/
	void generatePreviewWidget();

	/**
	 * The object used to preview the collision
	 * in GWT implementations this will be a AbsolutePanel
	 * @return
	 */
	Object getCollisionMapPreviewWidget();

	void clearSketch();

	void clearCalculatedPath();

	/** adds a path to the sketch object **/
	void addToSketch(String sketchSVGPath, String color,boolean rawpathsupplied);

	/** removes a path from the displayed list of paths
	 * the path has to exactly match for the remove to work **/
	void removeFromSketch(String sketchSVGPath, String color);

	/** is the collision map currently visible **/
	boolean isCmapVisible();

	/** is the pathfinding currently visible **/
	boolean isPathVisible();

	/** display this scenes collision map true/false**/
	void showCmap(boolean show);

	/** show the paths **/
	void showPath(boolean show);

	/** updates the visuals to display the path. - for debugging**/
	void updatePath(MovementPath np, PolygonCollisionMap lastCollision);

	void svgSmoothPathUpdate(String asSVGPath);
	

}