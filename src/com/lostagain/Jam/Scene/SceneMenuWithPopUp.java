package com.lostagain.Jam.Scene;

import java.util.logging.Logger;

import com.lostagain.Jam.Factorys.JamMenuBar;
import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.IsPopupPanel.JamPositionCallback;
import com.lostagain.Jam.Interfaces.hasCloseDefault;
import com.lostagain.Jam.Interfaces.hasOpenDefault;
import com.lostagain.Jam.Interfaces.hasVisualRepresentation;
import com.lostagain.Jam.SceneObjects.SceneObjectFactory;
import com.lostagain.Jam.SceneObjects.Interfaces.hasUserActions;

/**
 * designed mostly for context menu replacement, or when one thing has many
 * possible "use" actions
 **/
public class SceneMenuWithPopUp implements hasVisualRepresentation,hasCloseDefault,hasOpenDefault { //extends DecoratedPopupPanel

	public static Logger Log = Logger.getLogger("JAMCore.SceneMenuWithPopUp");
	
	// TODO: change to popup interface
	// (add necessary functions to that interface)
	// DecoratedPopupPanel visualRepresentation = new DecoratedPopupPanel();
	
	public IsPopupPanel visualRepresentation;// = SceneObjectFactory.createPopUp(null, X, Y, title, SetContents)
	
	//This class would then be responsible to both make the popup, and fill it with a IsPopupContents
	//which a subclass has to make.
	//That contents being the equivalent of the MenuBar	
	
	
	//DecoratedPopupPanel thisPanel = visualRepresentation;

	public static SceneMenuWithPopUp currentMenu = null;

	public JamMenuBar popupMenuBar; 
	public hasUserActions initialSourceobject; //this might not be needed

	/**
	 * Is a menu currently showing? 
	 */
	public static Boolean menuShowing = false;
	
	public SceneMenuWithPopUp(String[] items, hasUserActions sourceObject) {
		
		initialSourceobject = sourceObject;

		popupMenuBar = SceneObjectFactory.createJamMenuBar(this);
		
		visualRepresentation = SceneObjectFactory.createBasicPopUp(popupMenuBar);
		
		
		visualRepresentation.setModal(true);
		visualRepresentation.setAutoHideEnabled(true);


		// add items
		addItems(items,sourceObject);

		visualRepresentation.setStyleName("menupopup"); 
		
		popupMenuBar.setVisible(true);
		//visualRepresentation.add(popupMenuBar);

		//temp disabled during testing
		/*
		visualRepresentation.addCloseHandler(new CloseHandler<PopupPanel>() {
			@Override
			public void onClose(CloseEvent<PopupPanel> event) {
				InventoryPanelCore.menuShowing = false;
				currentMenu = null;
			}
		});*/
		//Log.info("adding run on close");

		//NOTE: CloseDefault should  automatically run when a popup is closed anyway
		
		visualRepresentation.addRunOnClose(new Runnable(){
			@Override
			public void run() {

				Log.info("running run on close...");
				menuShowing = false;
				currentMenu = null;
			}		
		});
		
	}

	public void addItems(String[] items,hasUserActions sourceObject) {
		int itemnum = 0;
		while (itemnum < items.length) {

			//MenuItem menuItems = new MenuItem(items[itemnum].trim(), true,
			//		           new runActions(items[itemnum].trim(), sourceObject));

			//menuItems.addStyleName("popup-item");
			//popupMenuBar.addItem(menuItems);
			
			popupMenuBar.addMenuItem(items[itemnum].trim(), sourceObject);
			
			itemnum++;
		}
	}

	
	//@Override
	/*
	public void show() {
		//visualRepresentation.show();
		visualRepresentation.OpenDefault();
		
		SceneMenuPopUp.menuShowing = true;
		currentMenu = this;

	}

	//@Override
	public void hide() {
		//visualRepresentation.hide();
		visualRepresentation.CloseDefault();
		
		SceneMenuPopUp.menuShowing = false;
		currentMenu = null;

	}
*/
	
	//@Override
	public void setPopupPositionAndShow(JamPositionCallback callback) {
		
		visualRepresentation.setPopupPositionAndShow(callback);
		
		
		SceneMenuWithPopUp.menuShowing = true; //has to be run when its shown
		currentMenu = this; //has to be run when its shown
		
	}

	public static void addItemsToCurrentMenu(String[] items,hasUserActions sourceObject) {
		currentMenu.addItems(items,sourceObject);

	}

	public void setPopupPosition(int x, int y) {
		visualRepresentation.setPopupPosition(x, y);
		
	}

	@Override
	public Object getVisualRepresentation() {
		return visualRepresentation;
	}

	@Override
	public void OpenDefault() {
		//if (!visualRepresentation.isShowing()){
		//	visualRepresentation.OpenDefault();
	//	}
		Log.info("running open default:");

		SceneMenuWithPopUp.menuShowing = true;
		currentMenu = this;
	}

	@Override
	public void CloseDefault() {
		Log.info("running CloseDefault on menu popup");
		
		//if (visualRepresentation.isShowing()){
		//	visualRepresentation.CloseDefault();
		//}
		
		SceneMenuWithPopUp.menuShowing = false;
		currentMenu = null;

	}

}
