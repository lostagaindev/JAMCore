package com.lostagain.Jam.InstructionProcessing;

/** Stores answers and associative code to run if they are found **/
public class Answer {
	
	/** The string that has to be typed to get this answer **/
	public String Answer;
	/** The chapter this answer is accepted for **/
	public String Chapter;
	
	/** The commands to run when the correct answer is specified on the correct chapter **/
	public CommandList ScriptCodeBlock;
	
	
	public Answer(String Answer,String Chapter,String ScriptCodeBlock){
		
		this.Answer = Answer;
		this.Chapter = Chapter;
		this.ScriptCodeBlock = InstructionProcessor.StringToCommandList(ScriptCodeBlock);
		
	}
 
	
	
}
