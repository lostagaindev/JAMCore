package com.lostagain.Jam.Factorys;

import java.util.logging.Logger;

import com.lostagain.Jam.Interfaces.IsPopupPanel;
import com.lostagain.Jam.Interfaces.hasCloseDefault;
import com.lostagain.Jam.Interfaces.hasOpenDefault;
import com.lostagain.Jam.Interfaces.PopupTypes.IsPopupContents;
import com.lostagain.Jam.Scene.SceneMenuWithPopUp;
import com.lostagain.Jam.SceneObjects.Interfaces.hasUserActions;

/**
 * A vertical menu bar for the selection of actions
 * Must be ended to provide a visual implementation of a clickable list.
 * 
 * @author darkflame *
 */
public abstract class JamMenuBar  implements IsPopupContents,hasCloseDefault,hasOpenDefault {
	SceneMenuWithPopUp associatedMenu;

	static Logger Log = Logger.getLogger("JAMCore.JamMenuBar");
	
	public JamMenuBar(SceneMenuWithPopUp associatedMenu) {
		super();
		this.associatedMenu = associatedMenu;
	}
	@Override
	public void OpenDefault() {
		associatedMenu.OpenDefault();
	}
	@Override
	public void CloseDefault() {
		associatedMenu.CloseDefault();
	}
	@Override
	public boolean POPUPONCLICK() {
		return false;
	}
	@Override
	public boolean DRAGABLE() {
		return false;
	}
	@Override
	public String POPUPTYPE() {
		return null;
	}

	@Override
	public void RecheckSize() {			
	}
	
	public abstract void setVisible(boolean b);
	
	
	public void addMenuItem(String name, hasUserActions sourceObject) {
		
		runActions runOnMenuClick =  new runActions(name,sourceObject) ;
		
		addMenuItem_impl(name, runOnMenuClick);
		
	}
	
	/**
	 * Add a new menu item to the menubar
	 * 
	 * @param name - the name of the new menu item to add
	 * @param actions - actions that must be executed when the menu item is clicked 
	 */
	public abstract void addMenuItem_impl(String name, runActions actions);
	

	
	public class runActions  {

			
		hasUserActions sourceobject;
		String useractionname;

		public runActions(String action, hasUserActions object) {

			sourceobject = object;
			useractionname = action;
		}

		//@Override
		public void execute() {
		//	visualRepresentation.hide();
			//visualRepresentation.CloseDefault();
			//SceneMenuPopUp.currentMenu.hide();
			if (SceneMenuWithPopUp.currentMenu!=null){				
				Log.info("GwtMenuBar  CloseDefault running;");	
				
				SceneMenuWithPopUp currentMenu = SceneMenuWithPopUp.currentMenu;
				IsPopupPanel visualRepresentation = currentMenu.visualRepresentation;  //current menu can be null
				visualRepresentation.CloseDefault();	
				if (sourceobject==null){ //should be impossible
					Log.severe("object was null on text option");				
				}
				sourceobject.userActionTriggeredOnObject(useractionname);
			}
		}
		
		
		
	}
	
}
