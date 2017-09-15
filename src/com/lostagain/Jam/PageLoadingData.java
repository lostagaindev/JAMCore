package com.lostagain.Jam;

import java.util.ArrayList;

public class PageLoadingData {
	public String pageURL;
	
	/**
	 * a chapter is a set of scenes & pages
	 */
	SceneAndPageSet chapterToPlaceItOn;

	/**
	 * the page loading queue is the pages yet to be loaded and the chapter they
	 * should be put on
	 **/
	public static ArrayList<PageLoadingData> PageLoadingQueue = new ArrayList<PageLoadingData>();
	
	public PageLoadingData(String pageURL, SceneAndPageSet chapterToPlaceItOn) {
		this.pageURL = pageURL;
		this.chapterToPlaceItOn = chapterToPlaceItOn;
	}


	public String getPageURL() {
		return pageURL;
	}

	public void setPageURL(String pageURL) {
		this.pageURL = pageURL;
	}

	public SceneAndPageSet getChapterToPlaceItOn() {
		return chapterToPlaceItOn;
	}

	public void setChapterToPlaceItOn(SceneAndPageSet chapterToPlaceItOn) {
		this.chapterToPlaceItOn = chapterToPlaceItOn;
	}
}