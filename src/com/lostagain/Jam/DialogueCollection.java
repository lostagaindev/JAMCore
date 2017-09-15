package com.lostagain.Jam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;


public class DialogueCollection {
	
	//Logger	
	 public static Logger Log = Logger.getLogger("JAMCore.DialogueCollection");
	 
	
	 
	 //DialogueCollection is a arbitrary collection of Sections
	 
	 //A Section is a set of paragraphs (all with the same ID).  (designed to be said by a single character in sequence)
	 //A paragraph is a single page/screen of text.
	 
	 
	 public static class Section extends ArrayList<String> {
		public String associatedTextboxName = null; //none set		 
	 }
	 
	HashMap<String,Section> collections = new HashMap<String,Section>();
	
	
	/**
	 * A Paragraph collection is a collection of paragraphs on different topics, normally<br>
	 * loaded from an external file.<br>
	 * ParagrapaCollections have namedsets (intended to be the name of the topic of conversation)<br>
	 * and Numbers (the specific paragraph within that named set) <br>
	 ***/
	public DialogueCollection(String parseString){
	
		//expand all abbreviations
		//parseString = expandAbbreviations(parseString);
		
		storeData(parseString);
		/*
		if (true){
			return;
		}
		
		//split into paragraphs
		String[] paragraphs = parseString.split("Paragraph=");

		Log.info("paragraph split"+paragraphs.length);

		Log.info("split too: "+paragraphs[1]);
		
		for (int i = 0; i < paragraphs.length; i++) {
			String cp = paragraphs[i];			
	
				if (cp.isEmpty()){
					continue;
				}
				
			Log.info("_______________________________________________"+cp);
			//get name and number
			int parastart = cp.indexOf(":");

			Log.info("________"+parastart);
			String nameAndNumber = cp.substring(0, parastart).trim();
			Log.info("________nameAndNumber"+nameAndNumber);
			//get name and number
			String name = nameAndNumber.split(",")[0].trim();

			String num = nameAndNumber.split(",")[1].substring(0, nameAndNumber.length()-1).trim();
			
			int number = Integer.parseInt(num);
						
			String contents = cp.substring(parastart+1).trim(); //change to -1 to debug paragraph num
					
			
			if (collections.containsKey(name)){
				collections.get(name).add(number, contents);				
			} else {				
				ArrayList<String> ParagraphSet = new ArrayList<String>();
				ParagraphSet.add(contents);
				collections.put(name, ParagraphSet);
			}
			
			Log.info("paragraph added");
			
			
		}
		
			*/
		
		
	}
	/** 
	 * parses the data from the file into paragraphs
	 * this can be optimized a lot, as its a bit of a hack job sorry
	 * **/
	private void storeData(String parseString) {
		
		//start parsing
		int loc=0;
		String lastID="";
		String lastTextBox="";
		int lastnum=0;
		
		while(loc<parseString.length()){
			
			//very crude atm
			Log.info("______Parsing from:"+loc);
			
			//location of next paragraph
			int nextParagraph = parseString.indexOf("Paragraph=",loc);
			int nextShortcut  = parseString.indexOf("<p:>",loc);
			
			//if both are -1 exit
			if ((nextParagraph==-1)&&(nextShortcut==-1) ){
				break;
			}		
			
			//end
			int startendat =0;			
			
			if ((nextShortcut<nextParagraph)&&(nextShortcut!=-1)){
				startendat = nextShortcut+4;						
			} else if (nextParagraph==-1){
				startendat = nextShortcut+4;
			} else {
				startendat = nextParagraph+9;
			}
			
			int endAtP = parseString.indexOf("Paragraph=",startendat);
			int endAtS = parseString.indexOf("<p:>",startendat);
			if ((endAtP==-1)&&(endAtS==-1)){
				endAtP=parseString.length();
			}
			endAtP = minimumNonNegative(endAtP,endAtS);
			
			
			
			
			String paragraphID = "(no name found error)";
			int number =0;
			String contents = "(no contents found error)";
			//optional params
			String TextboxName = null; //not specified
			
			//get whatevers first and act on it;
			if (((nextShortcut<nextParagraph)&&(nextShortcut!=-1))||(nextParagraph==-1)){
				
				//get shortcut settings
				int parastart = nextShortcut+5;
				Log.info("______Paragraph shortcut starts at "+parastart+" and ends at "+endAtP);
				paragraphID = lastID;
				TextboxName=lastTextBox;
				number = lastnum+1;
				lastnum=number;
				
				contents = parseString.substring(parastart+1,endAtP).trim(); //change to -1 to debug paragraph num
				
				loc=endAtP;	
			} else {
				if (nextParagraph==-1){
					Log.info("nextParagraph ==-1");
					break;
				}
				
				int parastart = parseString.indexOf(":",nextParagraph+10);
				if (parastart==-1){
					Log.info("______Paragraph missing end colon ");
					break;
				}
				
				
				Log.info("______Paragraph starts at "+parastart+" and ends at "+endAtP);
				String nameAndNumber = parseString.substring(nextParagraph+10, parastart).trim();
				Log.info("________nameAndNumber:"+nameAndNumber);
				
				//get name and number
				String params[] =  nameAndNumber.split(",");
				paragraphID = params[0].trim();
				Log.info("name:"+paragraphID);				
				String num = params[1].trim();
				Log.info("num0: "+num);	
				Log.info("nameAndNumber length -1: "+(nameAndNumber.length()-1));				
			//	num = num.substring(0, nameAndNumber.length()-1).trim(); //seems to be to ensure it stops at the  end of the line 
								
				//optional parameters
				if (params.length>=3){
					//textbox this paragraph belongs too
					TextboxName=params[2];
					Log.info("TextboxName: "+TextboxName);	
				} else {
					//Removed the num thing below...seemed pointless and caused crashs if out of range
					//
					//num = num.substring(0, nameAndNumber.length()-1).trim(); //seems to be to ensure it stops at the  end of the line 
					//This is only needed if there wasnt another parameter after the number
					Log.info("num1: "+num);	
				}
				
				
				lastID = paragraphID;
				number = Integer.parseInt(num);		
				lastnum=number;
				lastTextBox=TextboxName;
				contents = parseString.substring(parastart+1,endAtP).trim(); //change to -1 to debug paragraph num
				
				if(endAtP<loc){
					Log.info("endAtP value wrong error ");
					break;
				}
				
				loc=endAtP;		
				
			}
			contents=expandAbbreviations(contents);
			
			Log.info("adding:"+contents+" as "+paragraphID+"#"+number);
			
			if (collections.containsKey(paragraphID)){
				
				collections.get(paragraphID).add(number, contents);	
				Log.info("paragraph added to "+paragraphID);
				
			} else {				
				Section ParagraphSet = new Section();
				if (TextboxName!=null){
					ParagraphSet.associatedTextboxName=TextboxName;
				}
				
				
				ParagraphSet.add(contents);
				collections.put(paragraphID, ParagraphSet);
				Log.info("paragraph created with "+paragraphID);
			}
			
			Log.info("paragraph added. Collection now has "+collections.size()+" entries" );
			
			
				
			
		}
		
		
		
	}
private static int minimumNonNegative(int wordIndex, int wordIndex2) {
		
		if (wordIndex<0){
			return wordIndex2;
		}
		if (wordIndex2<0){
			return wordIndex;
		}
		
		return Math.min(wordIndex, wordIndex2);
		
	}


//expands internal abbreviations.
//specific this swaps "<:" shortcuts to full "SetParagraph" commands in the code
	private String expandAbbreviations(String parseString) {
			

		
		int loc=0;
		while (loc<parseString.length())
		{
		//get next <:
		int nextShortCut = parseString.indexOf("<:",loc);		
		//find next >
		int nextShortCutEnd = parseString.indexOf(">",nextShortCut);
		
		if ((nextShortCut==-1)||(nextShortCutEnd==-1)){
			return parseString;
		}
		loc= nextShortCutEnd+1;
		
		//replace middle 		
		//<:Text_Meryll_1,reply> is
		//<!--run: - SetParagraph = Text_Meryll_1,reply,false -->

		String before = parseString.substring(0, nextShortCut);
		String middle = parseString.substring(nextShortCut+2,nextShortCutEnd);
		String after = parseString.substring(nextShortCutEnd+1, parseString.length()); //plus one was missing
		
		//add ",false" on the end if it does not end with a autopop specification
		if (!(middle.endsWith(",false")||middle.endsWith(",true"))){
			middle = middle+",false";
		}
		
		Log.info("commands found="+middle);
		
		
		parseString=before+"<!--run: - SetParagraph = "+middle+" -->"+after;
		
		}
		
		//process command in between based on type
		
		
		return parseString;
	}
	
	/** Returns the text for a given paragraphset name and the paragraph number within it **/
	public String getText(String paragraphSetName, int Position){
		
		if (collections==null){
			Log.severe("paragraph not setup yet");
			
			return "";
		}
		
		ArrayList<String> paragraphSet = collections.get(paragraphSetName);
		
		if (paragraphSet==null){
						
			if (paragraphSetName.equalsIgnoreCase("default")) {
				//if its only a default request we dont error, as default is requested by..well..default. 
				//And the developer/scripter might not have wanted a default paragraph at all, so there is potentially no error
				Log.info("no paragraphset of name "+paragraphSetName+" to get");
				
			} else {
				//On  the other hand, any other named paragraph set requested that isnt present probably is a unintended effect
				//So we error
				Log.severe("no paragraphset of name "+paragraphSetName+" to get");
				
			}
			
			
			return "";
			
		}
		
		if (paragraphSet.size()<=Position){
			Log.severe("paragraphset contains only "+paragraphSet.size()+" paragraphs, yet you asked for number "+Position);
			return "";
		}
		
		if (Position<0){
			Log.severe("Why on earth you looking for negative numbers here? Theres no paragraph number: "+Position);
			return "";
		}
		
		
		
		return collections.get(paragraphSetName).get(Position);	
	}
	
	/** Returns the current number of paragraphs for a set of a given name **/
	public int getNumberOfParagraphs(String paragraphSetName){
		
		//Log.info("getNumberOfParagraphs of "+paragraphSetName);
		
		if (collections.get(paragraphSetName)==null){
			Log.info("no paragraph of that name ..");
			return 0;
			
		}
		
		return collections.get(paragraphSetName).size();
	}
	
	/** Returns a Set (welluh, list really) of the IDs of the dialogue paragraphs. This is useful if you want to iterate over all the paragraphs in ParagraphCollections.
	 * 
	 * 
	 * @return
	 */
	
	public Set<String> getIDlist() {
		
		
		
		return collections.keySet();
		
	}
	
	public Section GetParagraphSet(String ID) {
		
		return collections.get(ID);
		
	} 
}
