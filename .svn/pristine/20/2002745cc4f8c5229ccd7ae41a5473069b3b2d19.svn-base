package com.lostagain.Jam.GwtLegacySupport;

import java.util.HashSet;

/**
 * Manages a screen for game secrets.
 * Currently just here for CuypersCode2 compatibility
 * @author darkflame
 *
 */
public abstract class secretsPanelCore {

	public HashSet<linkItem> allLinks = new HashSet<linkItem>();
	
	public class linkItem {
		public String name; 
		public String data;
		
		
		public linkItem(String name, String data) {
			super();
			this.name = name;
			this.data = data;
		}
		
	}
	
	public void addLinkItem(String name, String link){
		allLinks.add(new linkItem(name,link));
		addLinkItem_impl(name, link);
	}
	
	/**
	 * Adds a weblink to the secret panel
	 * 
	 * @param name
	 * @param link
	 */
	public abstract void addLinkItem_impl(String name, String link);

}
