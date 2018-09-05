package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.Scene.SceneWidget;

/** A tabpanel, designed for all the pages and scenes at the current location.
 * It should allow fast switching between them, so everything in the set should be kept loaded unless removePage is used
 * 
 * In the cuypers code, this set was pages in the current chapter **/
public abstract class SceneAndPageSet {

	protected int PageCount = 0;

	public static Logger Log = Logger.getLogger("JAMCore.SceneAndPageSet");

	String chapterName = "";
	
	/**
	 * array we keep list of open pages in this set.
	 * (page can be a html page or a scene page)
	 ***/
	public final ArrayList<String> OpenPagesInSet = new ArrayList<String>();


	/**
	 * Old system that runs commands when a page is open
	 * Used for old HTML based games.
	 * Scene based games instead use onSceneToFront
	 */
	public static AssArray PageCommandStore = new AssArray();


	public SceneAndPageSet(String name) {
		chapterName = name;
	}

	/**
	 * Adds a newly created scene to this set
	 * 
	 * location is the location associated with the scene (the player should will automatically go to this when its to front)
	 * "make active" makes it the current active scene
	 * and silent mode determines if OnSceneDebut SceneToFront and OnSceneLoad commands should be suppressed. 
	 * This is mostly for loading from a save state, where none of that stuff should be run. 
	 *
	 * The subtype should handle the actual attachment/visual handling, with (eventually) this class handling
	 * the logical stuff (In gwt the supertype does the adding it to a tabpanel)
	 * 
	 * @param scene
	 * @param name
	 * @param location
	 * @param makeactive
	 * @param silentmode
	 */
	public void addNewSceneToSet(final SceneWidget scene, String name, String location,boolean makeactive,boolean silentmode){

		PageCount=PageCount+1;


		//we make sure _scene is added to the name, to prevent ambiguity's with html pages.
		if (!name.endsWith("__scene")){
			name=name+"__scene";
		}

		OpenPagesInSet.add(name);


		Log.info("adding to active location puzzle list:"+name);

		// add opened chapter name to list
		JAMcore.currentOpenPages.add(name); //WAIT should we be using this for the ID? why not OpenPagesInSet?
		
		// set chapter name to match
		JAMcore.locationpuzzlesActive.add(location);

		if (JAMcore.AnswerBox.isPresent()){
			JAMcore.AnswerBox.get().setEnabled(true);
			JAMcore.AnswerBox.get().setFocus(true);
			JAMcore.AnswerBox.get().setText("");
		}





		addNewSceneToSetPhysically(scene,name);

		//Log.info("index=" + ((GWTSceneAndPageSet)JAMcore.CurrentScenesAndPages).visualContents.getWidgetIndex(container) + " \n"); 
		//Log.info("selecting tab2 num of widgets= \n"+JAMcore.CurrentScenesAndPages.getPageCount()); 


		if (makeactive){
			Log.info(" making tab active "+scene.SceneFileName);					
			SceneWidget.setActiveScene(scene,silentmode);
		}

	}

	/**
	 * has to be overriden by superclass to provide a visual implementation of adding a html page to the set
	 * (In gwt this is adding it to a tabpanel)
	 * 
	 * @param NewMessageURL
	 */
	public void addNewHTMLPage(final String NewMessageURL)
	{
		Log.info("setting page"+NewMessageURL);
		OpenPagesInSet.add(NewMessageURL);
		PageCount=PageCount+1;
		
		// First we check if its on the page list already.
				// ==		
		int PageNum = getPageNumber(NewMessageURL);

		if (PageNum!=-1){
			selectPage_implementation(PageNum,NewMessageURL); //select it if so
			return;
		}
		
		/*
				int TabCount = this.visualContents.getWidgetCount();
				int cindex =0;
				
				//check if its already added, if so select it
			        while(cindex<TabCount)
			        {
			        	if (JAMcore.currentOpenPages.get(cindex).indexOf(NewMessageURL)>-1) {
			        		System.out.println("/n match. Already Open.");
			        		//we set it to front and return
			        		//this.visualContents.selectTab(cindex);
			        		selectPage(cindex);
				    		
			        		return;
			        	}
			        	
			        	
			        	cindex++; 
			        }
		*/

		addNewHTMLPagePhysical(NewMessageURL);

	}

	public abstract void addNewHTMLPagePhysical(final String NewMessageURL);
	
	/**
	 * must be overriden by subclasses to physically remove it from the pag
	 * @param removeLocation
	 */
	public void removePageFromSet(String PageName) {
		
		int PageNum = getPageNumber(PageName);
		OpenPagesInSet.remove(PageNum);
		//loop over page list and remove this
		/*
		for (Iterator<String> it = OpenPagesInSet.iterator(); it.hasNext(); ) {

			String currentItem = it.next(); 			  

			if (currentItem.compareTo(PageName)==0){
				OpenPagesInSet.remove(currentItem);
			}

		}*/

		JAMcore.currentOpenPages.remove(PageNum);
		// set chapter name to match
		JAMcore.locationpuzzlesActive.remove(PageNum);
		
		PageCount=PageCount-1;

		
		removePageFromSetPhysically(PageNum,PageName);
	}
	/**
	 * Select the page from this set
	 * You can use either the page name or number for identification purpose's, whatever is easier
	 * 
	 * @param pageNum
	 * @param scenename
	 */
	protected abstract void removePageFromSetPhysically(int PageNum,String PageName);

	/**
	 * 
	 * Selects a page within this set.
	 * This page should be made visible, hiding any others
	 * 
	 * must be overridden by subclasses atm
	 */
	public void selectPage(String scenename) {
		
		Log.info("selectPage_implementation---"+scenename);
		int PageNum = getPageNumber(scenename);

		if (PageNum!=-1){
			selectPage(PageNum,scenename);
		}

	}

	public void selectPage(int PageNum,String scenename) {	

		Log.info("selectPage_implementation 0-"+scenename);
		selectPage_implementation(PageNum,scenename);
		this.onPageSelected(PageNum);
		
	}
	/** Visual implementation
	 * Select the current page to move to the front.
	 * You can use either the page name or number for identification purpose's, whatever is easier
	 * 
	 * @param pageNum
	 * @param scenename
	 */
	protected abstract void selectPage_implementation(int pageNum,String name);

	/**
	 * gets the ID number of the page matching the specified string
	 * @param pageName
	 * @return
	 */
	public int getPageNumber(String pageName){

		int TabCount = this.getPageCount();
		Log.info("searching for:"+pageName+" out of:"+TabCount);

		int cindex =0;

		while(cindex<TabCount){
			Log.info("testing:"+JAMcore.currentOpenPages.get(cindex));			 
			if (JAMcore.currentOpenPages.get(cindex).equalsIgnoreCase(pageName)) {	        		        		
				return cindex;
			}
			cindex++;
		}
		return -1;
	}
	
	/**
	 * gets the ID number of the page matching the specified string
	 * @param pageName
	 * @return
	 */
	public String getPageName(int Num){
		return JAMcore.currentOpenPages.get(Num);
	}
	

	/**
	 * sets the background class of the box containing the games story(html) stuff
	 * This is mostly for cuypers code support, for 3d implementations (GDX) etc it can just be a null method
	 * @param currentProperty
	 */
	public abstract void setstoryboxbackgroundclass(String currentProperty);

	protected abstract void addNewSceneToSetPhysically(SceneWidget scene, String name);


	/**
	 * a mess. Should select the scene (like selectPage) for JAMcore.CurrentScenesAndPages, but if its a html page it instead waits till its loaded, flagging
	 * variables to run this function again later after is loaded
	 * 
	 * @param currentProperty
	 */
	public static void selectPageIfLoadedOrSceneStraightAway(String currentProperty) {
		JAMcore.CurrentScenesAndPages.selectPageIfLoadedOrSceneStraightAwayImplementation(currentProperty);

	}

	//public abstract void selectPageIfLoadedOrSceneStraightAwayImplementation(String currentProperty);


	public abstract void clearVisuals();

	public int getPageCount() {
		return PageCount;//visualContents.getWidgetCount();
	}

	/**
	 * A horrible mess, sorry.
	 * If a scene is requested by its name (detected by having __scene as a suffix) it just runs bringSceneToFront 
	 * If not, it assumes its a html page, and then looks to see if it can select it. IF so, it does.
	 * If not, it flags some variables to recheck after loading (triggerSelectCheck and pagetoselect)
	 * 
	 * @param NewMessageURL
	 * 
	 * 
	 */
	//TODO: tidy this up a bit, update comments, remove old code etc
	public void selectPageIfLoadedOrSceneStraightAwayImplementation(String NewMessageURL) {
	
		Log.info("Trying to set page to :" + NewMessageURL + ":");
	
		//if its a scene, popup straight away, as that has its own load handling methods
		if (NewMessageURL.endsWith("__scene")){
			Log.info("Scene detected :" + NewMessageURL + ":");
			InstructionProcessor.bringSceneToFront(JAMcore.pageToSelect,false);
	
			return;
		}
	
		// if not this command has to be delayed until all chapters pending to be
		// loaded are loaded.	
		if (JAMcore.NumberOfHTMLsLeftToLoad == 0) {
	
		//	int TabCount = JAMcore.CurrentScenesAndPages.OpenPagesInSet.size();
		//	int cindex = 0;
	
			// remove html if present
			int index = NewMessageURL.indexOf(".html");
			if (index > 0) {
				NewMessageURL = NewMessageURL.substring(0, index);
			}
	
	
			//this replaces old while code beliw
			this.selectPage(NewMessageURL);
			
			/*
			while (cindex < TabCount) {
	
				Log.info("/n -=-=-=-");
				Log.info("/n -=-=-=-" + cindex);
				Log.info("/n -=-=-=-"
						+ JAMcore.CurrentScenesAndPages.OpenPagesInSet.get(cindex) + "::"
						+ NewMessageURL);
	
				if (JAMcore.CurrentScenesAndPages.OpenPagesInSet.get(cindex).indexOf(
						NewMessageURL) > -1) {
					Log.info("Match. Already Open."); //$NON-NLS-1$
					// we set it to front and return
					Log.info("Current LocationTabs ="+ JAMcore.CurrentScenesAndPages.getPageCount());
	
					((GWTSceneAndPageSet)JAMcore.CurrentScenesAndPages).visualContents.selectTab(cindex);
	
					return;
				}
	
				cindex++;
			}
			*/
			
			JAMcore.triggerSelectCheck = false;
			JAMcore.pageToSelect = "";
		} else {
			Log.info("/n -=-=-=-");
			Log.info("/n -=-=-=- Page has to be brought to front, but there is still stuff left to load");
			JAMcore.triggerSelectCheck = true;
			JAMcore.pageToSelect = NewMessageURL;
		}
	
	}

	/**
	 * should be fired when a page is selected, either manually or automatically
	 * @param tabIndex
	 */
	public void onPageSelected(int tabIndex) {
	
		Log.info("getting chap name");
		String ChapName = JAMcore.locationpuzzlesActive.get(tabIndex);
		
		Log.info("chapter set to-"+ChapName);
		JAMcore.usersCurrentLocation = JAMcore.locationpuzzlesActive.get(tabIndex);
	
		//update window title
		RequiredImplementations.setWindowTitle(" - " + JAMcore.Username + GamesInterfaceTextCore.MainGame_is_on_chapter + JAMcore.usersCurrentLocation+" - ");
		//-------------
		
		Log.info("getting pagename?");
	
		//old method;
		//Log.info("getting pagename?");
		//String pagename_fixed = visualContents.getTabBar().getTabHTML(tabIndex);
	
		//Log.info("tab label 1:"+pagename+"|");				
		String pagename_fixed = getPageName(tabIndex);
		
		//Log.info("tab label 2:"+pagename2+"|");
		Log.info("tab label (real):"+pagename_fixed+"|");
	
	
		Log.info("triggering process instructions-"+pagename_fixed);
	
		if (SceneAndPageSet.PageCommandStore.GetItem(pagename_fixed)!=""){
			
			Log.info("instructions="+SceneAndPageSet.PageCommandStore.GetItem(pagename_fixed));
			InstructionProcessor.processInstructions(SceneAndPageSet.PageCommandStore.GetItem(pagename_fixed), "TurnedPageTrigger",null);
	
		}
	
	}

	public String getChapterName() {
		return this.chapterName;
	}


}
