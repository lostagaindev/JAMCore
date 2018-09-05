package com.lostagain.Jam.Interfaces;

import com.lostagain.Jam.SceneAndPageSet;

/**
 * A chapter is a SceneAndPageSet
 * This controller managers a possible interface to displaying a specific set, or letting the user choose a set.
 * Think of it as a tab panel, with the SceneAndPage set itself a tab panel on each tab.
 * 
 * Chapter1;
 * - Home (angle 1)
 * - Home (angle 2)
 * - Newspaper
 * - Window
 * Chapter2;
 * - Church
 * - Road
 * Chapter3:
 * -Train
 * etc.
 * 
 * This would be the collection of Chapter1,2 and 3.
 * 
 * 
 * @author darkflame
 *
 */
public abstract class JamChapterControl {

	public static boolean SetStoryPageScrollbars = true;


	public abstract void setTabBarVisible(boolean b);

	/**
	 * adds a new SceneAndPageSet to the panel
	 * @param gwtSceneAndPageSet
	 * @param currentProperty
	 */
	public abstract void add(SceneAndPageSet gwtSceneAndPageSet, String name);
	
	/**
	 * Clear all chapters
	 */
	public abstract void clear();
	
	/**
	 * returns the index of the set
	 * if the implementation is visual this should be its position in the tabs
	 * @param gwtSceneAndPageSet
	 * @return
	 */
	public abstract int getWidgetIndex(SceneAndPageSet gwtSceneAndPageSet);

	//rename to select chapter
	public abstract void selectTab(int widgetIndex);

	//If there is animation effects when switching between chapters this turns them on/off
	public abstract void setAnimationEnabled(boolean animationEffectsOn);

	
	/**
	 * legacy support for scroll bar changing in cuypers code and other text games
	 * @param b
	 */
	public abstract void setStoryPageScrollBar(boolean b);


	public void newchapter(String name){
		newchapter_specificImplementation(name);
	}
	
	
	public abstract void newchapter_specificImplementation(String name);
	

}
