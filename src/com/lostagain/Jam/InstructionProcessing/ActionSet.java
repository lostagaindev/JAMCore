package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import com.lostagain.Jam.InventoryPanelCore;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyTextUti;


public class ActionSet {

	public static Logger Log = Logger.getLogger("JAMCore.ActionSet");

	
	public CommandList CommandsInSet = new CommandList();


	public ArrayList<Trigger> Triggers = new ArrayList<Trigger>();


	/** 
	 * TriggerTypes are all the various ways actions can be triggered
	 ***/


	public enum TriggerType{
		MouseClickActions, 
		/** mouse double click: Experimental, might not work on not-gwt platforms**/
		MouseDoubleClickActions,
		MouseRightClickActions,
		MouseOverActions,
		MouseOutActions,
		PropertyAddedActions,
		PropertyRemovedActions,
		OnObjectVariableChanged,
		UserActionUsed,
		/**
		 * When all scene objects are logically loaded, the OnFirstLoads for all of them will be fired.
		 * 	* DONT USE THIS ON INVENTORY ITEMS. As they have no scene, it will never fire.
		 */
		OnFirstLoad,
		OnReload,//doesnt seem to be used
		UsedOnObject,
		/** OnTouchingChange is triggered when something touching this one changes in some way (ie properties) **/
		OnTouchingChange,
		/** only for inventory items **/
		DefaultActionFor,
		/** only for inventory items **/
		OnItemAdded,
		/** only for inventories themselves (in globalitemactions.ini in main inventory) **/
		OnInventoryOpen,
		/** only for inventories themselves (in globalitemactions.ini in main inventory)  **/
		OnInventoryClosed,
		
		OnAnimationEnd,
		NamedActionSet,
		/** Triggers when player stops using the item (like rightclick cancel). Only for inventory items **/
		OnDropped,
		OnKeyPress,
		/** Triggers when a label has been fired to start typing **/
		OnTextStart,
		/** Triggers when a label has finished typing **/
		OnTextEnd,		
		/** Triggers when a SetMessage has been fired to start typing **/
		OnMessageStart,
		/** Triggers when a SetMessage has finished typing **/
		OnMessageEnd,
		/** Triggers every time the direction of movement changes **/
		OnDirectionChanged,
		/** Triggers every so many movement update frames **/
		OnStep,
		/** Used purely in scene.jam files to detect clicks on the background, (using a normal mouseclick would trigger on all sceneobject clicks) **/
		BackgroundClickAction,
		/** Used purely in scene.jam files to detect right clicks on the background. (using a normal mouseclick would trigger on all sceneobject clicks)**/
		BackgroundRightClickActions,
		/** Used purely in scene.jam files trigger when a scene is made current **/
		SceneToFrontActions,
		/** Much like SceneToFrontActions, but these actions only happen the FIRST time a scene is shown ***/
		SceneDebut,
		/** Used purely in scene.jam files trigger when a scene is no longer current **/
		SceneToBackActions,	
		/** Triggers when a object is cloned, normally used to start any default animations playing **/
		OnCloned,
		/** Triggers at the end of any objects movement **/
		OnMovementEnd,
		/** Triggers when a item from the inventory is opened (should only be used on inventory items) **/
		OnItemOpen,
		/** Triggers when a item from the inventory is closed (should only be used on inventory items) **/
		OnItemClose,
		/** Triggers if this object is clicked while behind something else. Triggers instead of the normal click action. 
		 * Should be used somewhat sparingly, as each click will need to be tested against all the objects that have this set. **/
		OnClickedWhileBehind,
		/**
		 * Should fire after the score counter has finnished animating to the latest score
		 */
		OnScoreCountingEnd,
		/** only works from scene actions. Fires if a SceneScroll command successfully completes  **/
		OnScrollComplete,

	}


	public class Trigger{
		//each action has at least one trigger type
		public TriggerType triggertype;

		public String parameter = null;
		public Trigger (TriggerType type){
			this.triggertype = type;
		}


		public Trigger (TriggerType type, String param){
			this.triggertype = type;
			parameter = param;
		}


		@Override
		public String toString() {
			return "" + triggertype + "=" + parameter + ":";
		}
		
		
	}

	/** loads the actions from a string  
	 * - Message = "This is D"
        - PlaySound = noteD.mp3
        - SetTIGitem = D0.png,PlayBounce
	 * etc **/
	public ActionSet(String Actions, String Triggers){

		setActions(Actions);
		setTriggers(Triggers);

	}

	public void setActions(String ListOfActions){
		//split by -
		//assign into array

		//Log.info("action list:\n"+ListOfActions);

		String[] actionStrings = ListOfActions.split("\r");
		int len = actionStrings.length;
		int i = 0;

		while(i<len){

			String line = actionStrings[i].trim();

			//ignore empty lines (3 is the minimum length of a command line)
			if (line.length()<3){
				i++;
				continue;
			}			

			//ignore comments			
			if (line.startsWith("//")){
				i++;
				continue;
			} else {

				CommandLine newcommand = new CommandLine(actionStrings[i]);
				CommandsInSet.add(newcommand);

				//	Log.info(actionStrings[i]);

				i++;
			}


		}
	}

	/*	public ArrayList<String> getActions(){

		ArrayList<String> actions = new ArrayList<String>();

		Iterator<String> actionit = Actions.iterator();


		while(actionit.hasNext()){

			actions.add(actionit.next());


		}
		//assign all actions to array ready for processing by main code


		return actions;
	}*/
	
	
	/** 
	 * Loads the triggers from a string 
	 * These are all the things that trigger the associated set of actions
	 * Normally you would only have 1, but you can have more 
	 * 
	 * onMouseOver,onMouseOut etc
	 * 
	 * **/
	public void setTriggers(String ListOfTriggers){

		//split by comas not in quotes
		ArrayList<String> triggerstrings = SpiffyTextUti.splitNotWithinBrackets(ListOfTriggers, ",", '"', '"');
		Log.info("triggerstrings:"+triggerstrings);
		
		for (String currentTrigger : triggerstrings) {


			//Split parameter's (if any)
			String[] actionParams = currentTrigger.split("=");
			
			//trigger is bit before equals , if there was a =
			if (actionParams.length>1){
				currentTrigger=actionParams[0]; //first bit in array is trigger,rest are parameters
			}
			currentTrigger=currentTrigger.toLowerCase().trim();//easier comparison

			//  Log.info("action trigger:"+currentTrigger);

			//TODO: change to enum based, we might not even need this switch
			switch (currentTrigger) {
			case "mouseoveractions":
				Triggers.add(new Trigger(TriggerType.MouseOverActions));
				break;
			case "mouseoutactions":
				Triggers.add(new Trigger(TriggerType.MouseOutActions));
				break;
			case "mouseclickactions":
				Log.info("setting mouse click action trigger");
				if (actionParams.length>1){
					String params = actionParams[1].trim();	
					Triggers.add(new Trigger(TriggerType.MouseClickActions,params));
				} else {
					Triggers.add(new Trigger(TriggerType.MouseClickActions));					
				}
				
				break;
			case "onclickedwhilebehind":
				Log.info("setting onclickedwhilebehind action trigger");
				Triggers.add(new Trigger(TriggerType.OnClickedWhileBehind));
				break;
			case "backgroundclickactions":
				Log.info("setting BackgroundClick action trigger");
				Triggers.add(new Trigger(TriggerType.BackgroundClickAction));
				break;
			case "backgroundrightclickactions":
				Log.info("setting BackgroundRightClickActions action trigger");
				Triggers.add(new Trigger(TriggerType.BackgroundRightClickActions));
				break;
			case "scenetofrontactions":
				Log.info("setting SceneToFrontActions action trigger");
				Triggers.add(new Trigger(TriggerType.SceneToFrontActions));
				break;
			case "scenedebut":
				Log.info("setting SceneDebut action trigger");
				Triggers.add(new Trigger(TriggerType.SceneDebut));
				break;
			case "scenetobackactions":
				Log.info("setting SceneToBackActions action trigger");
				Triggers.add(new Trigger(TriggerType.SceneToBackActions));
				break;
			case "mousedoubleclickactions":
				Triggers.add(new Trigger(TriggerType.MouseDoubleClickActions));
				break;
			case "mouserightclickactions":

				Log.info("setting mouse right click action trigger");
				Triggers.add(new Trigger(TriggerType.MouseRightClickActions));
				break;
			case "useractionused":
			{
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.UserActionUsed,params));
				break;
			}
			case "propertyremovedactions":
			{
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.PropertyRemovedActions,params));
				break;
			}
			case "propertyaddedactions":
			{
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.PropertyAddedActions,params));
				break;
			}
			case "onobjectvariablechanged":
			{
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.OnObjectVariableChanged,params));	
			}
				break;
			case "onfirstload":
				Triggers.add(new Trigger(TriggerType.OnFirstLoad));
				break;
			case "onreload":		
				Triggers.add(new Trigger(TriggerType.OnReload));
				break;
			case "oncloned":	
				Triggers.add(new Trigger(TriggerType.OnCloned));
				break;
			case "ontouchingchange":
				Triggers.add(new Trigger(TriggerType.OnTouchingChange));
				break;
			case "usedonobject":	
				Triggers.add(new Trigger(TriggerType.UsedOnObject));
				break;
			case "ondropped":
				Triggers.add(new Trigger(TriggerType.OnDropped));
				break;
			case "onitemopen":	
				Triggers.add(new Trigger(TriggerType.OnItemOpen));
				break;
			case "onitemclose":
				Triggers.add(new Trigger(TriggerType.OnItemClose));
				break;
			case "ondirectionchanged":
				Log.info("on direction changed:");
				Triggers.add(new Trigger(TriggerType.OnDirectionChanged));
				break;
			case "onkeypress":
			{
				//get param
				String params = actionParams[1].trim();	//char ccode
				Log.info("onkeypress params:"+params);
				Triggers.add(new Trigger(TriggerType.OnKeyPress,params));
				break;
			}
			case "ontextstart":
				Log.info("ontextstart added");
				Triggers.add(new Trigger(TriggerType.OnTextStart));
				break;
			case "ontextend":
				Log.info("ontextend added");
				Triggers.add(new Trigger(TriggerType.OnTextEnd));
				break;
			case "onmessagestart":
				Log.info("onmessagestart added");
				Triggers.add(new Trigger(TriggerType.OnMessageStart));
				break;
			case "onmessageend":
				Log.info("onmessageend added");
				Triggers.add(new Trigger(TriggerType.OnMessageEnd));
				break;
			case "onscorecountingend":
				Log.info("onscorecountingend added");
				Triggers.add(new Trigger(TriggerType.OnScoreCountingEnd));
				break;
			case "onscrollcomplete":
				Log.info("onscrollcomplete added");
				Triggers.add(new Trigger(TriggerType.OnScrollComplete));
			
				break;
			case "onstep":
			{
				//get param
				String params = actionParams[1].trim();
				Log.info("on step:"+params);
				Triggers.add(new Trigger(TriggerType.OnStep,params));
				break;
			}
			case "onanimationend":
			{
				//get Param
				String params = actionParams[1].trim();
				Log.info("animation end params:"+params);
				Triggers.add(new Trigger(TriggerType.OnAnimationEnd,params));
				break;
			}
			case "onmovementend":
				Triggers.add(new Trigger(TriggerType.OnMovementEnd));
				break;
			case "namedactionset":
			{
				//get param
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.NamedActionSet,params));
				break;
			}			
			case "defaultactionfor":
			{
				//get param
				String params = actionParams[1].trim();
				Triggers.add(new Trigger(TriggerType.DefaultActionFor,params));
				break;
			}
			case "onitemadded":
			{
				Triggers.add(new Trigger(TriggerType.OnItemAdded));
				break;
			}
			case "oninventoryopen":
			{
				//should default to default inventory
				String inventoryName = InventoryPanelCore.defaultInventory.InventorysName.toLowerCase();
				if (actionParams.length>1){
					//...or if inventory name supplied
					inventoryName = actionParams[1].trim().toLowerCase();
				}

				Triggers.add(new Trigger(TriggerType.OnInventoryOpen,inventoryName));
				break;
			}
			case "oninventoryclosed":
			{

				//should default to default inventory
				String inventoryName = InventoryPanelCore.defaultInventory.InventorysName.toLowerCase();
				if (actionParams.length>1){
					//...or if inventory name supplied
					inventoryName = actionParams[1].trim().toLowerCase();
				}

				Triggers.add(new Trigger(TriggerType.OnInventoryClosed,inventoryName));				
				break;
			}
			
			//
			
			
			}
		}

	}


	public ArrayList<Trigger> getTriggers(){

		return Triggers;


	}
	public boolean hasTrigger(TriggerType type, String Parameter){

		//	Log.info("_________________checking for trigger:  "+type.toString());

		//iterate over its triggers looking for a match

		Iterator<Trigger> tit = Triggers.iterator();
		while (tit.hasNext()) {

			ActionSet.Trigger triggers = (ActionSet.Trigger) tit.next();

			if (triggers.triggertype == type){


				//check param matches if given
				if (Parameter !=null){

					if (triggers.parameter.equalsIgnoreCase(Parameter)){
						return true;
					}

				} else {
					return true;

				}


			}



		}

		return false;
	}

	public CommandList getActionsArray(){

		return CommandsInSet;
	}

	public String toString(){
		String str = "[no triggers]";

		if (Triggers.size()>0){
			str = Triggers.get(0).triggertype+" :\n "+CommandsInSet.toString();		
		} 

		return str;  

	}

	/** Returns the script code for this action set **/
	public String getCode() {
		String str = "";

		if (Triggers.size()>0){

			//get trigger specification
			String triggerType = Triggers.get(0).triggertype.name();
			if (Triggers.get(0).parameter!=null){
				triggerType=triggerType+"="+Triggers.get(0).parameter;
			}


			str = triggerType+":\n\n"+CommandsInSet.getCode();		


		} 

		return str;  
	}

}
