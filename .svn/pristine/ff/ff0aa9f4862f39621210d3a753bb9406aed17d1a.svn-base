package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;



//Note; This class could probably all be replaced with a extended multimap?
//Essentualy we need a hashmap with many items possible for a given key (name)
//Benchmarking, supprisingly, shows this class as only half the speed as a hashmap - a differance that only shows up after 10,000 or so calls. I expected worse. Given its not called that often optimisation seems pointless

public class AssArray {

public	static Logger Log = Logger.getLogger("JAMcore.AssArray");

	final  ArrayList<String> Items = new ArrayList<String>();
	final  ArrayList<String> Names = new ArrayList<String>();


	boolean maintainUpdateLog = true;
	//we also maintain a updateLog if this mode is turned on
	//this records the last 20 ItemNames added , set or removed
	final static int MAX_UPDATE_LOG_SIZE = 20;
	public static Queue <String> lastVariablesUpdated = new LinkedList<String>();
	String lastVariableUpdatedName = "";

	//An optional runable that runs on all updates above
	Runnable onUpdateRun = null;


	public void setOnUpdateRun(Runnable onUpdateRun) {
		this.onUpdateRun = onUpdateRun;
	}

	public AssArray(){
		//nothing
	}

	/**
	 * everything is stored as a string internally.
	 * This first converts the double to a string, cropping of any ".0" at the end if its a whole number
	 * 
	 * @param newitem
	 * @param newname
	 */
	public void AddItemUniquely(double newitem, String newname){
	
		String newitemasstring = ""+newitem;
		
		//convert to int if its a int. (seems a lot faster then cropping .0 from the string)
		if (newitem==(int)newitem){	
			newitemasstring = ""+((int)newitem);
		}		
	
		
		AddItemUniquely(newitemasstring,newname);
		
	}
	
	/** Adds a new item, but checks the name first and overwrites it if it exists already 
	 * Note, if more then one exists, it overwrites the last one **/
	public void AddItemUniquely(String newitem, String newname){

		if (Names.contains(newname)){
			int indextoremove = getItemIndex(newname);
			Items.set(indextoremove, newitem);
			Log.info("Setting existing variable "+indextoremove+" name="+newname+" value="+newitem);

		} else {
			Items.add(newitem);
			Names.add(newname);
			//	  Log.info("Setting variable name="+newname+" value="+newitem);
			//	  Log.info("name array now has = ("+Names.size()+") enteries");

		}


		addLastUpdated(newname);
	}

	public void AddItem(String newitem, String newname){

		Items.add(newitem);
		Names.add(newname);

		addLastUpdated(newname);

	}
	public void RemoveItem(String removethisitem){
		//find location
		int indextoremove = getItemIndex(removethisitem);
		//if present
		if (indextoremove>-1){
			Items.remove(indextoremove);
			Names.remove(indextoremove);
		}


		addLastUpdated(removethisitem);
	}

	private int getItemIndex(String thisitem) {
		int i=0;
		int index = -1;

		//	Log.info("testing for item ("+thisitem+")");
		//	Log.info("within names array, which has; ("+Names.size()+") enteries");

		for (Iterator<String>it = Names.iterator(); it.hasNext(); ) {
			String currentItem = it.next(); 
			//Log.info("testing against;"+currentItem+"|"+thisitem);			  
			if (currentItem.compareTo(thisitem)==0){
				//  Log.info("match found "+i);
				index=i;
			};

			i=i+1;

		}
		return index;
	}

	/** returns all the names in a string array */
	public ArrayList<String> getAllUniqueNames(){

		//loop adding unique names to list;		
		final  ArrayList<String> uniquenames = new ArrayList<String>();


		for (Iterator<String>it = Names.iterator(); it.hasNext(); ) {

			String currentItem = it.next(); 

			//test if not in uniquenames

			boolean present=false;
			//	  System.out.print( "\n -- array size :"+uniquenames.size());

			for (Iterator<String>uniquenamesit = uniquenames.iterator(); uniquenamesit.hasNext(); ) {
				String checkthis = uniquenamesit.next();
				//System.out.print( "\n inner loop:"+checkthis);

				if (checkthis.equals(currentItem)){
					present=true;
				}


			}

			if (!present){
				uniquenames.add(currentItem);
			}
		}




		return uniquenames;

	}

	/** tests all the items associated with the specified name for a match */
	public boolean TestForMatch(String ItemName,String TestAgainstThis){
		boolean match =false;

		//find location
		int i=0;
		//	int itemindex = -1;
		for (Iterator<String>it = Names.iterator(); it.hasNext(); ) {
			String currentItem = it.next(); 
			if (currentItem.compareTo(ItemName)==0){
				//we now test if the value matchs
				//   itemindex=i;
				String testthis = Items.get(i);
				if (testthis.equals(TestAgainstThis)){
					match=true;
				}

			};
			i=i+1;

		}


		return match;
	}

	/** gets all items of this name **/
	public ArrayList<String> GetAllItems(String ItemName){

		final ArrayList<String> ItemsToReturn = new ArrayList<String>();

		//find location
		int i=0;
		for (Iterator<String>it = Names.iterator(); it.hasNext(); ) {
			String currentItem = it.next(); 
			if (currentItem.compareTo(ItemName)==0){
				ItemsToReturn.add(Items.get(i));


			};
			i=i+1;


		}


		return ItemsToReturn;
	}

	/** gets the last item of this name **/	
	public boolean hasItem(String ItemName)
	{
		int itemindex = getItemIndex(ItemName);
		//Log.info("testing for item existing="+itemindex);
		//if present
		if (itemindex>-1){
			return true;
		} 		

		return false;
	}

	/** gets the last item of this name or "" if none found  **/	
	public String GetItem(String ItemName){
		String Item = "";
		int itemindex = getItemIndex(ItemName);
		//Log.info(ItemName+" has itemindex="+itemindex);
		//if present
		if (itemindex>-1){
			Item = Items.get(itemindex);
		} else {
			Item = "";
		}


		return Item;
	}

	/** Serialise the data into an array in the form &var=value&var2=value2 etc
	 * multiple names the same not correctly supported yet **/

	public String serialise() {


		String data = "";
		for (int i=0;i<Names.size();i++) {

			String currentItemName = Names.get(i);
			String currentItemValue = Items.get(i);

			data = data + currentItemName+"="+currentItemValue+"&";

		}		

		return data;
	}

	public String serialiseForSave(String commandPrefix) {


		String data = "";
		for (int i=0;i<Names.size();i++) {

			String currentItemName = Names.get(i);
			String currentItemValue = Items.get(i);

			data = data + commandPrefix+currentItemName+","+currentItemValue+" \n";

		}		

		return data;
	}

	/** if a variable is changed or added, its name gets added to the last updated list.
	 * This list keeps track of the last 20 or so variables or so updated (that 20 can be changed, see updateLogLimit)**/
	private void addLastUpdated(String lastVarName) {

		if (maintainUpdateLog){

			if (!lastVarName.equals(lastVariableUpdatedName))
			{
				lastVariablesUpdated.add(lastVarName);
				lastVariableUpdatedName = lastVarName;

				if (lastVariablesUpdated.size()>MAX_UPDATE_LOG_SIZE){
					lastVariablesUpdated.poll();
				}

			}

		}

		//if a onUpdateRun is set we run it here.
		if (onUpdateRun!=null){
			onUpdateRun.run();
		}
	}

	/** results a copy of the lastVariablesUpdateList**/
	public ArrayList<String> getLastUpdatedVariables() {
		return new ArrayList<String>(lastVariablesUpdated);
	}


}
