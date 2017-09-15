package com.lostagain.Jam.InstructionProcessing;

import java.util.logging.Logger;

import lostagain.nl.spiffyresources.client.spiffycore.SpiffyGenericCommand;

/** 
 * A single command and set of parameters that represent either an action for the engine to perform
 * or a conditional which will block following actions unless its met 
 ***/
public class CommandLine {

	public static Logger Log = Logger.getLogger("JAMcore.CommandLine");
	

	//command lines command
	public GameCommands        TheCommand  = GameCommands.commandnotset;
	public CommandParameterSet TheParameters;
	
	//conditionalline, if applicable
	public ConditionalList CONDITIONS = null;


	//the raw line received from the file
	public String basicCommandLine="";

	/**
	 * directly create a command with params
	 * @param command
	 * @param params
	 */
	public CommandLine(GameCommands command, String params) {
		
		TheCommand   =command;		
		TheParameters=new CommandParameterSet(params); 
		
		if (TheParameters.isEmpty() && TheCommand.usesParameters()){
			
			Log.warning("No parameters specified for "+TheCommand.name()+". For a few things (like NextParagraph..) this is fine, but most commands will have a parameter so if errors follow this statement this is probably why"); 
			
		}
		
		
		
	}
	/** converts a command line string to a CommandLine object<br>
	 * <br>
	 * example commandline;<br>
	 * - SetObjectURL = wobble0.png,15<br>
	 * <br>
	 * Conditionals are automatically converted to a special command of type "CONDITIONAL" with
	 * the property as the whole conditional line<br>
	 **/

	public CommandLine(String basicCommandLine) {

		basicCommandLine=basicCommandLine.trim();

		if (basicCommandLine.length()<2){
			Log.severe("Empty command line, ignoring");
			return;
		}

		this.basicCommandLine=basicCommandLine;

		//if it starts with - ( or  -( or ( then its a conditional
		if ((   basicCommandLine.startsWith("(") ||
				basicCommandLine.startsWith("-(")||
				basicCommandLine.startsWith("- ("))){

			TheCommand   = GameCommands.CONDITIONAL;
			TheParameters= new CommandParameterSet(basicCommandLine); //for conditions we just preserve it as one string?

			basicCommandLine=basicCommandLine.trim().substring(
					basicCommandLine.indexOf("(")+1,
					basicCommandLine.lastIndexOf(")")).trim();

			CONDITIONS = new ConditionalList(basicCommandLine);

			//done
			return;
		}


		//else if its a normal command we split and save it
		if (basicCommandLine.startsWith("-")){

			//remove -
			basicCommandLine=basicCommandLine.substring(1).trim();
			String stringCommand = basicCommandLine.trim();


			//split by equals if theres one
			//Note we only split by the first =s because the paramters might have one or more equals themselves
			//eg, Message = "hello world did you know that 1+1=2"
			//it should use the first = to divide the command (Message)
			//from the paramters ("hello world did you know that 1+1=2")
			//but it should not use the other = to divide it further up
			if (basicCommandLine.contains("=")&&(!basicCommandLine.endsWith("="))){

				stringCommand = basicCommandLine.split("=",2)[0].trim();
				TheParameters = new CommandParameterSet(basicCommandLine.split("=",2)[1].trim());

			}else{

				TheParameters = new CommandParameterSet("");
			}

			
			//remove = if needed
			if (basicCommandLine.endsWith("=")){
				stringCommand = basicCommandLine.split("=",2)[0].trim();
			}

			//get command as enum
			try {
				TheCommand = Enum.valueOf(CommandLine.GameCommands.class,stringCommand.toLowerCase());
			} catch (Exception e) {			
				Log.severe("not a recognised command '"+stringCommand.toLowerCase()+"' so we return");
				return;
			}


			if (TheParameters.isEmpty() && TheCommand.usesParameters()){
				Log.warning("No parameters specified for "+basicCommandLine+". For a few things (NextParagraph..) this is fine, but most commands will have a parameter so if errors follow this statement this is probably why"); 
			}
		} else {

			Log.info("Command not recognised. Maybe it didnt start with \"-\" ?");
			Log.info("Command was "+basicCommandLine);


		}


	}

	
	@Override
	public String toString(){

		//Log.info("old method:"+basicCommandLine);
		if (TheCommand==GameCommands.CONDITIONAL){
			//Log.info("new method:"+TheParameters);			
			return TheParameters.toString();
		} 
		
		//basicCommandLine;//
		//Log.info("new method:"+ TheCommand.toString()+" = "+TheParameters);
		if (TheParameters==null){
			return "- "+TheCommand.name();
		}

		return  "- "+TheCommand.name() +" = "+TheParameters.toString();
	}



	/** all scene commands as a enum 
	 * NOTE: Ones in capitals are for internal use only, and can not come from external files 
	 * 
	 * TODO: Fully implement SpiffyGenericCommands **/	
	public enum GameCommands implements SpiffyGenericCommand {	

		//ERROR REPORTING
		commandnotset,
		notarecognisedcommand,
		
		//Flow control
		exitactions,
		CONDITIONAL,
		
		/**
		 * turns the command log off.
		 * Commands will no longer be logged to the inspector until the end of the current action set
		 **/
		command_log_off,
		
		//Click forwarders
		sendclickto,
		sendrightclickto,

		//Clear variable commends
		/** clears the <LASTSCENEITEM> variable **/
		clearlastsceneobject,
		
		//Scene commands
		displaymenu,
		applyaction,
		toggleinventory,
		/** manages loading save games **/
		toggleloadgame,
		displayinventorylist,
		/** Briefly displays a objects image at a specific location, fading it in and out **/
		bamfobject,
		/** Briefly displays a inventory items image at a specific location, fading it in and out **/
		bamfitem,		
		settext,
		setcurrenttext,
		settexturl,
		setobjecturl,
		addobjecttouching,
		removeobjecttouching,
		setobjectpin,
		setobjecttoscene,
		/** sets one object to another objects position. Don't get mixed up with SetObjectPosition which sets it to a x/y position 
		 * used to be setobjecttoposition **/
		setobjecttopositionof,
		setobjectcss,
		addobjectcss,
		removeobjectcss,
		/** fills in a list of options in a predefined div, these options work much like the display menu function **/
		filloptionbox,
		/** adds options to the filloptionbox **/
		addboxoptions,
		moveobjectto,moveobject,
		/**
		 * drops the objects to the nearest surface directly under it under the normal acceleration of gravity
		 * assumes zero is the ground
		 * (WIP)
		 */
		droptofloor,
		droptoparentsbase,
		/**
		 * adds some speed to the current object.
		 * (as a x/y/z vector)
		 */
		addimpulse,
		addlink,removelink,
		/**
		 * wip. will clone many copys over a area
		 */
		cloneover,
		/** Clone a object at a specified position [objecttoclone,x,y,z,newobjectname]**/
		cloneat,
		setobjectposition,
		/**
		 * sets the z position without effecting its other co-ordinates or changing its relative status.
		 *  (ie, if its relative it will remain so with this as its new relz value, 
		 * if its absolute it will remain so with this as its new abs z position)
		 */
		setobjectzposition,
		setobjectzindex,
		setvariablezindex,
		linkzindex,unlinkzindex,
		addobjectproperty,removeobjectproperty,removeallobjectproperties,swapobjectproperty, 
		/** same as addobjectproperty, but we dont process any propertyadded actions, or touchingchange actions of naboughing objects**/
		addobjectpropertyforconditional,removeobjectpropertyforconditional,
		saveobjectstate,restoreobjectstate,
		gotoobjectmovement,
		chuckobjectat,
		chuckobjectintoinventory,
		setobjectmovement,stopobjectmovement,
		previousparagraph,setparagraph,
		settextcss,addtextcss,removetextcss,
		nextparagraph, 
		setobjectframegap, setframegap,
		subtractobjectvariable,addobjectvariable,setobjectvariable, 
		setobjectstate,	setanimationmode,
		/** sets the position we are looking at within the scene (ie, in a scene larger then display) **/
		setsceneposition, 
		/** scrolls the position we are looking at within the scene (ie, in a scene larger then display) **/
		scrollscenetoposition, 
		/** shakes the scene by moving its position randomly in x/y the amount you specify for the duration you specify **/
		setsceneshake,
		setscenebackground,
		/** opens the save game box **/
		savegame,
		/** silently saves a autosave game, replacing the last one if no new savename was set by a manual save**/
		autosavegame,
		/** replaces the current vector string with another **/
		setvectorstring,
		/**
		 * Loads the state of the object. Data should be between xml style tags <object>
		 */
		loadobjectdata,
		/** Load the state of the scene, including all the objects within it **/
		loadscenedata, 
		loadscenestate, savescenestate, 
		/**
		 * function for save loading only, should not be used in gameplay			
		 */
		resumetimerstatesafterload,
		setcurrentscenescroll, 
		cleargamedata,resetscene, resetobject, 
		fadeout, fadein,setobjectopacity, objectvisible,
		removeobject, runnamedcommandsafter, 
		stopnamedcommands, 
		runnamedcommandsevery,
		runnamedcommands, 
		/**deprectated, use gamemode overlay instead**/		
		setscenestaticoverlay, 
		/**deprectated, use gamemode overlay instead**/
		setscenedynamicoverlay,	//

		//general commands
		setmouseimage,
		addvariable,
		subvariable,
		setvariable,
		setmessagedelay,
		previousmessage,
		message,
		setlocation,		
		enterans,
		setcluebox,		
		addscore,
		setscore,
		/**
		 * sets the score box visible (true) of invisible (false)
		 */
		setscorevisible,
		pointsawardedfor,
		setmessagespeed,
		addinventory,
		pocketobject,
		additem,
		holditem, unholditem,
		clearinventory,
		removeitem,
		specialeffect,
		loadscene,
		/** Will load a scene without triggering any scenetofront,scenedebut,or sceneload commands
		 * Designed to be used as part of a save system, where all variable states would be saved seperately
		 * so the loadscene is purely for loading the scenes files **/
		loadscenesilently,
		gotoscene,
		storybox,
		selectscene,
		/** Will select a scene without triggering any scenetofront,scenedebut,or sceneload commands
		 * Designed to be used as part of a save system, where all variable states would be saved seperately
		 * so the loadscene is purely for loading the scenes files **/
		selectscenesilently,
		selectpage,
		removelocation,
		openitem,
		/**
		 * sets the inventory items state to the following data
		 **/
		setitemstate,
		
		openurl,
		newchapter,
		addprofile,
		settigitem,
		checktig,
		disabletig,
		tigmessage,
		popupmessage,
		popupadvert,
		popupimage,
		stopsounds,
		setdefaultsound,
		stopobjectsound,
		playobjectsound,
		playsound,
		cacheaudiotrack,
		cachemusictrack,
		addmusictrack,
		playmusictrack,
		setcurrentmusictrackvolume,
		addsecret,
		openpanel,
		sendemail,
		setstorypagescrollbars,
		setsoldiericon,
		setclockladyicon,
		setinventoryicon,
		/**
		 * shows the inventory buttons
		 */
		showinventorybuttons,
		/**
		 * hides the inventory buttons
		 */
		hideinventorybuttons,
		/**
		 * shows the inventory button
		 */
		showinventorybutton,
		/**
		 * hides the inventory button
		 */
		hideinventorybutton,
		setstoryboxbackgroundclass,
		removeclassfromelement,
		addclasstoelement,
		setclassonelement,
		showelement,
		hideelement,
		/** note, fade in and out elements have a maximum duration of 10,000ms (100ms interval x 100 intervals)**/
		fadeoutelement,
		/** note, fade in and out elements have a maximum duration of 10,000ms (100ms interval x 100 intervals)**/
		fadeinelement,
		setstyleonelement,
		replacediv,
		setbackgroundclass,
		setbackgroundimage,
		setclockmode,
		debug_image_urls,
		setgamemode, currentparagraph, clearmenu, copymovementstate, showinventory, hideinventory;
		
		

		//-----------------------------------------
		//-----------------------------------------
		//now we have defined all the enums, we define a extra information each one of them can have.
		//-----------------------------------------
		//-----------------------------------------		
		//Optional extra information the above  commands can have
		//These help with script writing and debugging but arnt used at all (yet?) in the actually generation
		//of story's
		/** description of the enum (first optional param) **/
		String description = ".";

		String scriptMarkStart =  "-"; //what marks the command in the script
		String scriptMarkEnd   = "\n"; //what marks the end of the command in the script (note; currently set to newline might not work?)

		String beforeCursor = "";
		String afterCursor = " "; //crude fix by putting a space here the system will always expect parameters despite the fact we have no real symbol making the end of parameters other then the end tag. Really we should have a boolean to determain if theres params or not rather then just looking if this string exists
		
		
		//by default everything gets put before the cursor
		
		String parameterSeperator = ",";

		//(almost all commands use the above, only a few use [] brackets



		GameCommands (){		
			beforeCursor = toString().toLowerCase();
		}


		//we can optionally give the enums a description variable
		//this is purely used to help with interfaces that help make scripts
		GameCommands (String setdescription){			
			this.description=setdescription; 
			beforeCursor = toString().toLowerCase();
		}

		GameCommands (String setdescription,String before,String after, String scriptMarkStart,String scriptMarkEnd, String parameterSeperator)
		{			
			this.description=setdescription; 
			this.beforeCursor = before;
			this.afterCursor = after;
			this.scriptMarkStart = scriptMarkStart;
			this.scriptMarkEnd = scriptMarkEnd;

			if (parameterSeperator!=null && parameterSeperator.length()>0){
				this.parameterSeperator = parameterSeperator;
			}

		}


		public String getDiscription(){		
			if (description.length()>2){
				return description;
			} else {
				return toString().toLowerCase();
			}
		}

		public String getBeforeCursor(){
			return beforeCursor;
		}

		public String getAfterCursor(){
			return afterCursor;
		}
		public String getScriptMarkStart(){
			return scriptMarkStart;
		}

		public String getScriptMarkEnd(){
			return scriptMarkEnd;
		}


		public String getTotalText() {
			return scriptMarkStart+beforeCursor+afterCursor+scriptMarkEnd;
		}

		@Override
		public String getParamterSeperator() {
			return parameterSeperator;
		}

		
		/**
		 *  does this command type support maths in its parameters?
		 * @return
		 */
	
		public boolean usesMaths(){
			
			if (this == loadscenedata){
				return false;
			}
			if (this == loadobjectdata){
				return false;
			}
			if (this == resumetimerstatesafterload){
				return false;
			}
			return true;
		}
		
		/**
		 *  does this command type support variables in its parameters?
		 * @return
		 */
		public boolean usesVariables() {
			if (this == loadscenedata){
				return false;
			}
			if (this == loadobjectdata){
				return false;
			}
			if (this == resumetimerstatesafterload){
				return false;
			}
			return true;
		}
		/**
		 * Does this command type support the parameter string being split by | and a random result from that used?
		 * NOTE: this requires the | string not to be used in any parameter component used by the command except in variables (which are processed first)
		 * @return
		 */
		public boolean usesRandomSplit() {
			if (this == loadscenedata){
				return false;
			}	
			if (this == loadobjectdata){
				return false;
			}
			if (this == resumetimerstatesafterload){
				return false;
			}
			return true;
		}


		/**
		 *  does this enum type support parameters? (Note; this doesn't necessarily mean it requires them)
		 * @return
		 */
		public boolean usesParameters() {
			if (this == clearlastsceneobject ||					
				this == exitactions ||
				this == unholditem ||
				this == toggleloadgame ||
				this == cleargamedata ||
				this == loadscenestate ||
				this == savegame ||
				this == autosavegame ||
				this == loadobjectdata ||
				this == loadscenedata ){
				
				return false;
			}	
			return true;
		}

		

	
	}


}
