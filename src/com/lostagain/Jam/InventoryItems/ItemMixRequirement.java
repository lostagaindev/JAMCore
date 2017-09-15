package com.lostagain.Jam.InventoryItems;

import java.util.ArrayList;
import java.util.logging.Logger;

import com.lostagain.Jam.InstructionProcessing.CommandList;
import com.lostagain.Jam.InstructionProcessing.ConditionalLine;
import com.lostagain.Jam.InstructionProcessing.ConditionalLine.ConditionType;
import com.lostagain.Jam.InstructionProcessing.ConditionalList;
import com.lostagain.Jam.InstructionProcessing.InstructionProcessor;
import com.lostagain.Jam.SceneObjects.SceneObject;
import com.lostagain.Jam.SceneObjects.Interfaces.IsInventoryItem;

/**
 * When mixing two items in the inventory, a array of all "ItemMixRequirements" is checked.<br>
 * A ItemMixRequirement is a set of conditions that both items have to meet in order to trigger the command script<br>
 * At its most basic, these requirements are items being specific things, 
 * eg,<br>
 * The following in the ItemControllScript.txt ;<br>
 *  <br>
 *  Mix: ItemName1 + ItemName2 <br>
 *  - message<br>
 *  - blah<br>
 *  - blah <br>
 *<br>
 * Becomes two conditionals of the form.. <br>
 *  (isCalled,ItemName1) <br>
 *  (isCalled,ItemName2) <br>
 * ...and stored in a instance of this class. <br>
 * If, when two items are dragged over eachother, they meet both these conditions then the script associated with it stored in<br>
 *   CommandList runThis;<br>
 * is run. <br>
 * <br>
 * Mix requirements can by more complex, however, like a item mixed with a specific condition: <br> 
 *<br>
 * Mix: ItemName1 + (hasProperty = Flamable)<br>
 * - message<br>
 * - blah<br>
 * - blah<br>
 *<br>
 * This becomes two conditions like: <br>
 *  (isCalled,ItemName1) <br>
 *  (hasProperty = Flamable) <br>
 *  <br> 
 * Like before, both conditions have to be met in order to run the CommandList associated with it.  <br> 
 * You could even compare two specific conditions:  <br>
 * <br>
 * Mix: (hasProperty = Flamable) + (hasProperty = onFire)<br>
 * - message<br>
 * - blah<br>
 * - blah<br>
 * <br> <br>
 *  As before, the items dragged over eachother must meet the conditions. Note, in all cases, either item can match either condition, however.
 *  Item1 + Item2 is thus always the same as Item2 + Item1. <br>
 *  <br>
 * @author darkflame
 *
 */
public class ItemMixRequirement {
	public final static Logger Log = Logger.getLogger("JAMCore.ItemMixRequirement");
	
	
	//conditions that both items must meet
	ConditionalList ItemCondition1;
	ConditionalList ItemCondition2;

	//if they do, run this
	CommandList runThisIfMixConditionsMet;

	
	//static values that determine the priority of certain types of matches over others
	//Think of these values sort of as weights, with the heaviest result being the one picked
	final static int SPECIFIC_BOTH_MATCH_PRIORITY = 100;
	final static int SINGLE_OBJECT_AND_CONDITION = 50;
	final static int CONDITION_AND_CONDITION = 25;	
	final static int SINGLE_OBJECT_MATCH = 10;
	final static int SINGLE_CONDITION = 5;		
	final static int DEFAULT = 1;
		
	int Priority = DEFAULT; //What priority should this mixtype have over any other mixs found (higher number = higher priority)
	
	//Setup a new MixRequirement from known bits
	public ItemMixRequirement(ConditionalList itemCondition1, ConditionalList itemCondition2,
			CommandList runThisIfMixConditionsMet) {
		ItemCondition1 = itemCondition1;
		ItemCondition2 = itemCondition2;
		this.runThisIfMixConditionsMet = runThisIfMixConditionsMet;
	}

	/**
	 * Creates a ItemMixRequirement from a single mix and command set in the script:
	 * 
	 * Mix: INV_wine+bluesweet
	 *
	 * - RemoveItem = bluesweet
	 * - Message = The wine is now sweeter
	 * - AddObjectProperty = wine,sweet
	 * 
	 * @param ItemControllFile
	 */
	public ItemMixRequirement(String ItemControllFile){
		
		Log.info(" ItemControllFile:\n"+ItemControllFile);
		
		//trim
		ItemControllFile = ItemControllFile.trim();

		//Separate first line (which defines the mix conditions)
		int indexOfNewline = ItemControllFile.indexOf("\n",1);

		String lineOne = ItemControllFile.substring(0,indexOfNewline).trim();		
		String rest    = ItemControllFile.substring(indexOfNewline);

		//line one is Mix: followed by two conditions
		//remove Mix:
		lineOne = lineOne.substring(4).trim();
		
		//Variables to help work out the priority
		int SpecificObjectsSpecified = 0;
		int ConditionalsSpecified = 0;
		
		//if there is nothing left we store as a default requirement (there really should only be one of these per file, 
		//and only at the end - but thats the script writters job)
		if (lineOne.isEmpty()){
			Log.info("adding default requirement");
			Priority = DEFAULT;
			
			//create default
			ConditionalLine defaultNameCheck = new ConditionalLine(ConditionType.TRUE,"",null); 
			ItemCondition1 = new ConditionalList();
			ItemCondition1.add(defaultNameCheck);
			
			ConditionalLine defaultNameCheck2 = new ConditionalLine(ConditionType.TRUE,"",null); 
			ItemCondition2 = new ConditionalList();
			ItemCondition2.add(defaultNameCheck2);
			
		} else {	
			//split by +		
			String[] mixRequirements = lineOne.split("\\+");

			String conditional1 = "";
			String conditional2 = "";
		
			
			//if theres not a plus, but the strings not empty, we have one requirement
			if (mixRequirements.length==1){				
				conditional1 = mixRequirements[0].trim();
				conditional2 = "";
			} else {			
				//if there is a plus, then we have two requirements (as normal)		
				 conditional1 = mixRequirements[0].trim();
				 conditional2 = mixRequirements[1].trim();
			}
			
			

		Log.info(" lineOne:"+lineOne);
		Log.info(" conditional1:"+conditional1);
		Log.info(" conditional2:"+conditional2);
		Log.info(" rest:\n"+rest);
			
		//if conditions are bracketed then we can directly create them
		//else we assume we are just looking for a item name match and instead create a default "isCalled" condition
		if (conditional1.startsWith("("))
		{	
			//remove brackets (only the outer ones)
			//this ensure conditionalline doesnt interpret this as a sublist (if you need subconditions, youd have inner brackets too)
			conditional1 =conditional1.trim().substring(conditional1.indexOf("(")+1,
					conditional1.indexOf(")")).trim();

					
			ItemCondition1 = new ConditionalList(conditional1);
			ConditionalsSpecified++;
			Log.info("conditionlist created:"+conditional1.length()+"");
			Log.info("condition 0 in list:"+ItemCondition1.get(0).toString()+"");
			
		} else if (conditional1.isEmpty()){
			//Conditional 1 cant be empty. This must be a error
			Log.severe("Conditional1 empty error");
			
		} else {
			SpecificObjectsSpecified++;
			
			//create default
			ConditionalLine defaultNameCheck = new ConditionalLine(ConditionType.iscalled,conditional1,null); 
			ItemCondition1 = new ConditionalList();
			ItemCondition1.add(defaultNameCheck);

		}

		if (conditional2.startsWith("("))
		{	
			ConditionalsSpecified++;
			
			//remove brackets (only the outer ones)
			//this ensure conditionalline doesnt interpret this as a sublist (if you need subconditions, youd have inner brackets too)			
			conditional2 =conditional2.trim().substring(conditional2.indexOf("(")+1,
					conditional2.indexOf(")")).trim();

			ItemCondition2 = new ConditionalList(conditional2);

			Log.info("conditionlist created:"+conditional2.length()+"");
			Log.info("condition 0 in list:"+ItemCondition2.get(0).toString()+"");
			
		}  else if (conditional2.isEmpty()){
			ConditionalLine any = new ConditionalLine(ConditionType.TRUE,conditional2,null); 
			ItemCondition2 = new ConditionalList();
			ItemCondition2.add(any);
		} else {
			SpecificObjectsSpecified++;
			
			//create default
			ConditionalLine defaultNameCheck2 = new ConditionalLine(ConditionType.iscalled,conditional2,null); 
			ItemCondition2 = new ConditionalList();
			ItemCondition2.add(defaultNameCheck2);
		}

		}
		
		
		//assign priority
		if (SpecificObjectsSpecified == 2) {
			Priority = SPECIFIC_BOTH_MATCH_PRIORITY;		
		} else if (SpecificObjectsSpecified == 1 && ConditionalsSpecified ==1) {
			Priority = SINGLE_OBJECT_AND_CONDITION;			
		} else if (ConditionalsSpecified ==2) {	
			Priority = CONDITION_AND_CONDITION;			
		} else if (SpecificObjectsSpecified==1) {
			Priority = SINGLE_OBJECT_MATCH;			
		} else if (ConditionalsSpecified ==1) {
			Priority = SINGLE_CONDITION;			
		} else {
			Priority = DEFAULT;
			
		}
			
		
		//the rest is the commands
		runThisIfMixConditionsMet = new CommandList(rest);


	}

	@Override
	public String toString() {
		return "Mix:" + ItemCondition1.toString() + " + " + ItemCondition2.toString()
				+ "\n Priority:" + this.Priority
				+ "\n " + runThisIfMixConditionsMet;
	}

	/**<br>
	 * Parses over the ItemControllFile splitting it into a array of ItemMixRequirements<br>
	 * (each ItemMixRequirement stores the commands to run if the mix is detected too) <br>
	 * @param ItemControll
	 * @return
	 */
	static public ArrayList<ItemMixRequirement> getAllItemMixsFromFile(String ItemControll){
		ArrayList<ItemMixRequirement> mixCommandSet = new  ArrayList<ItemMixRequirement>();

		//crop off "ItemControllStartsHere;" on first line then trim
		ItemControll = ItemControll.substring("ItemControllStartsHere;".length()).trim()+"\n Mix:"; 
		
		//adding mix at the end makes it easier to split
		if (!ItemControll.endsWith("Mix:")){
			ItemControll=ItemControll+"\n Mix:"; 
		}
						
		//split by Mix:
		int i = 0;				
		int mixpos     =  ItemControll.indexOf("Mix:",i);
		int nextmixpos =  ItemControll.indexOf("Mix:",mixpos+1);

		while (nextmixpos!=-1){

			String mixCommandSetString = ItemControll.substring(mixpos, nextmixpos);
			ItemMixRequirement newReq = new ItemMixRequirement(mixCommandSetString);
			mixCommandSet.add(newReq);

			mixpos = nextmixpos;
			i = nextmixpos+4;
			nextmixpos =  ItemControll.indexOf("Mix:",mixpos+1);

		}

		return mixCommandSet;
	}


	/**
	 * Will test if the two items specified pass these requirements <br>
	 * NOTE: either item can pass either condition, provided 1 passes each.<br>
	 * 
	 * If the items do pass the requirements we run the associated commands
	 *   
	 * <br>
	 * @param ItemA  
	 * @param ItemB
	 * @param b 
	 * @return
	 */
	public boolean testMix(IsInventoryItem ItemA, IsInventoryItem ItemB, boolean runCommands) {
		

		//in order to test both permutations we test one permutation:		
		boolean success = testItems(ItemA,ItemB);
		
		//then if that fails we test the other
		if (!success){
			success = testItems(ItemB,ItemA);
		}    

		//if either is successful we run our commands
		if (success && runCommands){
			InstructionProcessor.processInstructions(runThisIfMixConditionsMet,"ItemMix:"+ItemA.getName()+"+"+ItemB.getName(),null);
		}

		return success;
	}

	/**
	 * Test items pass conditions.<br>
	 * <br>
	 * @param Item1 - will be tested against condition1<br>
	 * @param Item2 - will be tested against condition2<br>
	 * @return
	 */
	private boolean testItems(IsInventoryItem Item1, IsInventoryItem Item2){

		//test condition1
		boolean success = ConditionalList.checkConditionals(ItemCondition1, (SceneObject) Item1);
		if (!success){
			return false;
		}

		//test condition2
		success = ConditionalList.checkConditionals(ItemCondition2, (SceneObject) Item2);
		if (!success){
			return false;
		}

		return success;

	}

	public int getPriority() {
		
		return this.Priority;
	}

	
	
	public CommandList getCommands() {
		return this.runThisIfMixConditionsMet;
	}

	








}
