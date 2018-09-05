package com.lostagain.Jam.InstructionProcessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.logging.Logger;

//import com.google.gwt.gen2.logging.shared.Log;

/** A commandlist is....a list of commands!
 * or, well, a list of "CommandLine" objects to be exact.
 * 
 * Its really just a ArrayList, but with one added function; the ability to skip 
 * to the next conditional, as this is useful for the Instruction Processor **/
public class CommandList extends ArrayList<CommandLine> {
	public static Logger Log = Logger.getLogger("JAMCore.CommandList");

	Iterator<CommandLine> loop = this.iterator();


	public CommandList() {
		super();

	}

	public CommandList(Collection<? extends CommandLine> c) {
		super(c);
	}

	//lets you supply a coma seperated list of commands
	public CommandList(CommandLine... c) {
		super();
		//loop and add all supplied commands
		for (int i = 0; i < c.length; i++) {
			add(c[i]);
		}
	}

	/**
	 * Experimental - splits a string to commands
	 * It should split by newlines that are followed by - or (
	 * while also not adding any line that starts with //
	 * @param list
	 */
	public CommandList(String list) {
		super();


		Log.info("parsing command list:"+list);	
		
		String[] ListIT = list.split("\r?\n|\r"); 

		for (int i = 0; i < ListIT.length; i++) {
			
			String line = ListIT[i].trim();

			//Log.info("processing line:"+line);	
			
			if (line.startsWith("\\") || line.isEmpty()  || line.startsWith("//")){

			//	Log.info("sskipping line:"+line);	
				continue;		//skip	line	
			}

			//now test what follows if the commands ends or is split over many lines
			String nextLine = "";
			for (int nexti = (i+1); nexti < ListIT.length; nexti++) {

				nextLine = ListIT[nexti];

				if (nextLine.startsWith("\\") || nextLine.startsWith("//")){					
				//	Log.info("skipping line"+nexti+";"+nextLine);	
					continue;		//ip		
				}
				if (nextLine.trim().startsWith("-") || nextLine.trim().startsWith("(") ){		
					
					break; //line ready so we break out 
				} else {
					//i to last line 
					i = nexti;
					//add to existing line	

			//		Log.warning("adding nextLine:"+nextLine);	
					line=line+nextLine;
					
				}			

			}
			
			if (line.length()>2){	
			//	Log.warning("adding line:"+line);	
				CommandLine newcommand = new CommandLine(line);
				add(newcommand);
			}


		}




	}

	public CommandList getActions(){
		return this;
	}

	@Override
	public boolean add(CommandLine newcommand){
		if (newcommand!=null){
			return super.add(newcommand);
		} else {
			return false;
		}

	}

	public CommandLine next(){

		return loop.next();

	}

	public void resetIterator(){
		loop = this.iterator();
		return;

	}

	public CommandLine getNextConditional(){

		CommandLine currentLine;

		while (loop.hasNext())
		{

			currentLine =this.next();

			//Log.info("looking for next condition:"+currentLine.basicCommandLine);

			if (currentLine.TheCommand.equals(CommandLine.GameCommands.CONDITIONAL)){

				return currentLine;
			}


		}

		return null;


	}

	public boolean hasNext() {

		return loop.hasNext();
	}


	/** Returns the script code for this action set<br>
	 * eg.<br>
	 * - Message = "Scene state saved"<br>
	- StopObjectSound = fire.ogg<br>
	 **/
	public String getCode() {

		String str = "";


		for (CommandLine cline : this) {
			str=str+cline.toString()+"\n";

		}


		return str;
	}

	// function to convert a CommandList to a string. Rarely needed, as the instruction processor now works by
	// CommandLists directly
	public static String CommandListToString(CommandList list) {
		String instructions = "";

		Iterator<CommandLine> ListIT = list.iterator();

		while (ListIT.hasNext()) {

			String string = ListIT.next().toString();
			instructions = instructions + "\n\r" + string;

		}


		return instructions.trim();
	}

	/**
	 * returns the commands a newline separated list
	 * for debugging purposes
	 */
	@Override
	public String toString() {
		String string = "[";
		for (CommandLine cl : this) {
			string = string + cl.toString()+"\n";			
		}
		return string+"]";
	}
	
	
	//System to allow processing of commands to be stopped from within the loop
	//Be sure to reset the flag if you do stop
	//*********
	boolean stopAtNextIteration = false;

	public void stopProcessing() {
		stopAtNextIteration = true;
	}
	public void resetStopProcessingFlag() {
		stopAtNextIteration = false;
	}
	public boolean commandsAreSetToStop() {		
		return stopAtNextIteration;
	}
	//***************



}