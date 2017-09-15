package com.lostagain.Jam.SceneObjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Logger;

import com.darkflame.client.semantic.SSSNode;
import com.darkflame.client.semantic.SSSProperty;
import com.darkflame.client.semantic.SSSUtilities;
import com.lostagain.Jam.GameStatistics;
import com.lostagain.Jam.JAMcore;

public class PropertySet  {
	public static Logger Log = Logger.getLogger("JAMCore.PropertySet");

	public HashSet<SSSProperty> properties = new HashSet<SSSProperty>();

	/**
	 * If we need to arrange the properties in a clear to humans predictable order use this.
	 * It arranges them alphabetically by their labels, which is probably the most intuitive.
	 * This should be just needed for testing, so the user can more quickly see if anything is missing
	 */
	static private Comparator<SSSProperty> sorter = new Comparator<SSSProperty>() {
		
		@Override
		public int compare(SSSProperty o1, SSSProperty o2) {
			String name1 =  "";
			if (o1.getPred()==SSSNode.SubClassOf){
				name1 = o1.getValue().getPLabel().toLowerCase();
			} else {
				name1 = o1.getPred().getPLabel().toLowerCase()+"_"+o1.getValue().getPLabel().toLowerCase();
			}
			
			
			String name2 = "";
			if (o2.getPred()==SSSNode.SubClassOf){
				name2 = o2.getValue().getPLabel().toLowerCase();
			} else {
				name2 = o2.getPred().getPLabel().toLowerCase()+"_"+o2.getValue().getPLabel().toLowerCase();
			}
			
			
			
			return name1.compareTo(name2);
		}
	};
	



	// example set:
	/*
	static SSSNode magnetic = new SSSNode("Magnetic", "Internal/magnetic",
			new SSSNode[] {});

	static SSSNode conductive = new SSSNode("Conductive",
			"Internal/conductive", new SSSNode[] {});

	static SSSNode metal = new SSSNode("Metal", "Internal/metal",
			new SSSNode[] { conductive });

	static SSSNode ferrismetal = new SSSNode("ferris metal",
			"Internal/ferrismetal", new SSSNode[] { magnetic, metal });

	static SSSNode iron = new SSSNode("iron", "Internal/iron",
			new SSSNode[] { ferrismetal });
	 */

	//new creation method to stop duplicates

	static SSSNode magnetic = SSSNode.createSSSNode
			("Magnetic", "Internal/magnetic",JAMcore.defaultNS,
					new SSSNode[] {});

	static SSSNode conductive = SSSNode.createSSSNode("Conductive",
			"Internal/conductive",JAMcore.defaultNS, new SSSNode[] {});

	static SSSNode metal =  SSSNode.createSSSNode("Metal", "Internal/metal",JAMcore.defaultNS,
			new SSSNode[] { conductive });

	static SSSNode ferrismetal =  SSSNode.createSSSNode("ferris metal",
			"Internal/ferrismetal",JAMcore.defaultNS, new SSSNode[] { magnetic, metal });

	static SSSNode iron =  SSSNode.createSSSNode("iron", "Internal/iron",JAMcore.defaultNS,
			new SSSNode[] { ferrismetal });


	public PropertySet(String[] propertys) {





		for (String stringp : propertys) {
			this.add(stringp);

		}


		// objectsProperties =new HashSet<String>(
		// Arrays.asList(propertys));

	}

	public PropertySet copy(){
		return new PropertySet(properties);
	}

	public PropertySet(PropertySet objectsProperties2) {

		this.properties = (HashSet<SSSProperty>) objectsProperties2.properties.clone(); //makes clone now
	}

	public PropertySet(HashSet<SSSProperty> objectsProperties2) {

		this.properties = (HashSet<SSSProperty>) objectsProperties2.clone(); //makes clone now
	}

	public PropertySet() {


	}

	/**
	 * adds a property. If no URI is supplied, it assumes the local one.
	 * Remember to use the correct URI if it isnt local (ie DBPedia for colour, for example)
	 * 
	 * @param property
	 * @return
	 */
	public boolean add(String property) {

		//createSSSProperty from string
		SSSProperty newProp = createStandardProperty(property);

		return add(newProp);
	}

	public boolean add(SSSProperty newProp) {

		Log.info("_________________adding property: "+newProp.getPred().getPLabel());
		Log.info("_________________adding value: "+newProp.getValue().getPLabel());
		
		return properties.add(newProp);
	}

	
	public static SSSProperty createStandardProperty(String property) {
		String svalue;
		String spredicate = SSSNode.SubClassOf.getPURI();

		Log.info("_________________adding property "+property);


		//if it contains a ; then split, else we assume its a SubClassOf
		if (property.contains(";")){

			spredicate = property.split(";")[0];
			svalue = property.split(";")[1];

		} else {
			svalue = property;
		}


		//GET VALUE NODE
		String rawValue = svalue; // used for the label only



		SSSNode value = SSSNode.createSSSNode(rawValue, svalue, JAMcore.defaultNS);

		Log.info("_________________adding property with value__"+value.getPURI());

		// add default namespace if theres none
		//		if ((!svalue.contains("#"))&&(!svalue.contains(":"))) {
		//		svalue = MyApplication.defaultNS + svalue;
		//	}

		// check global property set if it exists already was "Internal/"+
		//	SSSNode value = SSSNode.getNodeByUri(svalue);


		//	if (value != null) {

		//	} else {
		//		// else create new property
		//	value = new SSSNode(rawValue, svalue, new SSSNode[] {});

		//	}


		//GET PREDICATE NODE
		String rawPredicate = spredicate; // used for the label only

		// add default namespace if theres none
		//if ((!spredicate.contains("#")&&(!spredicate.contains(":")))) {
		//	spredicate = MyApplication.defaultNS + spredicate;
		//}

		// check global property set if it exists already was "Internal/"+
		Log.info("_________________adding property with predicate string__"+spredicate);
		SSSNode spred = SSSNode.createSSSNode(rawPredicate, spredicate, JAMcore.defaultNS);

		Log.info("_________________adding property with predicate__"+spred.getPURI());

		//if (spred != null) {

		//} else {
		// else create new property
		//	spred = new SSSNode(rawPredicate, spredicate, new SSSNode[] {});

		//}

		//shouldnt be needed. The add function checks for equality on all elements (EQUALS(..)) and returns false if a existing element matchs
		//if (this.hasProperty(spred, value)){	
		//	return false;			
		//}

		// make new property and add it (should really check for pre-existing property in SSSProperty instead of above,
		// then the above check would be handled automatically by the add statement below)		
	//	SSSProperty newProp = new SSSProperty(spred, value);
		SSSProperty newProp = SSSProperty.createSSSProperty(spred, value); //creates a property of returns a existing identical one
		return newProp;
	}

	public boolean remove(SSSProperty property) {
		return properties.remove(property);
	}

	public Iterator<SSSProperty> iterator() {

		return properties.iterator();

	}

	public String toString() {
		return toString(false,JAMcore.DebugMode,false ); //only reorder if on debug mode
	}

	/**
	 * 
	 * @param includeParentClasses
	 * @param reorder
	 * @param includeuri
	 * @return
	 */
	public String toString(boolean includeParentClasses,boolean reorder,boolean includeuri) {

		//we reorder alphabetically first if asked
		//This isn't strictly needed, but makes testing easier because without it the order
		Collection<SSSProperty> propertyCollection=properties;
		if (reorder){
		//	Log.info(" sort"+propertyCollection.toString());
			
			//we need to covert to a list, because that HAS a order
			//Collections dont necessarily even remember the order see. Like Hashsets don't.
			ArrayList<SSSProperty> propertyList = new ArrayList<SSSProperty>(properties);			
			Collections.sort(propertyList,sorter);
			propertyCollection=propertyList;
			
		//	Log.info("after sort"+propertyCollection.toString());
			
		}
		


		Iterator<SSSProperty> it = propertyCollection.iterator();
		String propertyLists = "";

		ArrayList<SSSProperty> allclasss = new ArrayList<SSSProperty>();


		while (it.hasNext()) {

			SSSProperty sssProp = (SSSProperty) it.next();

			allclasss.add(sssProp);

			if (includeParentClasses){
				// if its a class
				if (sssProp.getPred() == SSSNode.SubClassOf) {
					allclasss.addAll(sssProp.getValue()
							.getAllClassesThisBelongsToAsPropertys());
				}
			}
			// propertyLists=propertyLists+","+sssNode.getPLabel();

		}

		Iterator<SSSProperty> acit = allclasss.iterator();

		while (acit.hasNext()) {
			SSSProperty sssProp = (SSSProperty) acit.next();

			String valueLabel = sssProp.getValue().getAllPLabels();//we used to just get the main label; .getPLabel();
			String predLabel = sssProp.getPred().getAllPLabels();
			
			//if specified we also display the full uri
			if (includeuri){
				valueLabel = sssProp.getValue().getPURI() +"("+valueLabel+")";
				predLabel = sssProp.getPred().getPURI() +"("+predLabel+")";				
			}
			
			//only show pred if not a class
			if (sssProp.getPred() == SSSNode.SubClassOf){
				propertyLists = propertyLists + valueLabel + ","+"\n";
			} else {				
				propertyLists = propertyLists + predLabel
						+ ";" + valueLabel + ","+"\n";
			}
			
		}


		return propertyLists;

	}

	/** Returns an arraylist of all the objects propertys as strings.
	 * eg "blue" "visible" **/
	public ArrayList<String> getAllDirectPropertysAsStrings(){

		ArrayList<String> allprops = new ArrayList<String>();

		for (SSSProperty property : properties) {
			allprops.add(property.getValue().getPLabel());

		}

		return allprops;
	}

	public HashSet<SSSProperty> clone() {

		return (HashSet<SSSProperty>) properties.clone();

	}

	public int size() {
		return properties.size();

	}

	public void clear() {
		properties.clear();
		return;

	}

	public boolean isEmpty() {

		return properties.isEmpty();

	}

	/**
	 * Removes a property from this set.
	 * The property string can either specify to remove a class if its just a word on its own
	 * Or if the property string has a semicolon it will assume whats before the semicolon is the predicate
	 * ie.<br>
	 * "fruit" will assume to remove "subclassof:fruit"<br>
	 * "contains;paper" will remove "contains;paper" <br>
	 * <br>
	 * 
	 * TODO: like the add function, this can probably be optimised to use less string manipulation.
	 * 
	 * @param propertystring
	 * @return
	 */
	public boolean removeproperty(String propertystring) {

		//default pred
		String PropsPredURI = SSSNode.SubClassOf.getPURI(); // temp
		String PropsValueURI = "";
		//if triplet
		//if it contains a ; then split, else we assume its a SubClassOf
		if (propertystring.contains(";")){

					PropsPredURI = propertystring.split(";")[0];
					PropsValueURI = propertystring.split(";")[1];

		} else {
					PropsValueURI = propertystring;
		}

		
		//ensure they have NS
		if (!PropsPredURI.contains("#")) {
			PropsPredURI = JAMcore.defaultNS + PropsPredURI;
		}
		if (!PropsValueURI.contains("#")) {
			PropsValueURI = JAMcore.defaultNS + PropsValueURI;
		}
		
		Iterator<SSSProperty> opit = properties.iterator();


		while (opit.hasNext()) {

			SSSProperty cur_property = opit.next();

			if (cur_property.getPred().getPURI()
					.equalsIgnoreCase(PropsPredURI.trim())) {
				
				if (cur_property.getValue().getPURI()
						.equalsIgnoreCase(PropsValueURI.trim())) {

					this.remove(cur_property);

					return true;
				}
			}
		}
		return false;
	}

	/** 
	 * when you just ask for a value, we assume your asking for a subclass of that value 
	 ***/
	public boolean hasProperty(String valUri) {

		long TimeTakenStart = System.currentTimeMillis();	//for profiling
		//
		String PropsPredURI = SSSNode.SubClassOf.getPLabel();// .getPURI();
		//Log.info("testing properties (PropsPredURI):"+PropsPredURI);

		boolean hasp = hasProperty(PropsPredURI,valUri);

		long TimeTakenEnd = System.currentTimeMillis();	//for profiling
		GameStatistics.TotalPropertyCheckCheckTime = GameStatistics.TotalPropertyCheckCheckTime+(TimeTakenEnd-TimeTakenStart);


		return hasp;
	}


	/** when you just ask for a value, we assume your asking for a subclass of that value **/
	public boolean hasProperty(SSSNode value) {
		return hasProperty(SSSNode.SubClassOf,value);
	}
	
	
	public boolean hasPredicate(String predLabel) {		
		SSSNode pred = SSSNode.getNodeByLabel(predLabel);		
		return hasPredicate(pred);				
	}

	/** Does this object have any predicates of this type?
	 * ie. "contains" would return true if this object had properties
	 * like contains;water  **/
	public boolean hasPredicate(SSSNode value) {
		
		Iterator<SSSProperty> opit = properties.iterator();
		while (opit.hasNext()) {
			SSSProperty cur_property = opit.next();

			if (cur_property.getPred().isOrHasParentClass(value)) {
				return true;
			}

		}		
		return false;
	}



	public boolean hasProperty(SSSNode pred,SSSNode val) {
		Iterator<SSSProperty> opit = properties.iterator();

		while (opit.hasNext()) {

			SSSProperty cur_property = opit.next();

			Log.info("testing properties (predicate):" + cur_property.getPred().getPURI()+" (val):" + cur_property.getValue().getPURI());
			Log.info("against predicate :" + pred+" value :" + val);


			// if (cur_property.isOrHasParentClass("Internal/"+testThis)){
			if (cur_property.getPred().isOrHasParentClass(pred)) {

				if (cur_property.getValue().isOrHasParentClass(val)) {

					return true;
				}

			}
			// if (cur_property.getPLabel().equalsIgnoreCase(testThis.trim())) {
			// return true;
			// }

		}
		return false;
	}

	/**
	 * 
	 * @param predLabel
	 * @param valLabel
	 * @return
	 */
	public boolean hasProperty(String predLabel,String valLabel) {

		//Note; we could use the create function will return a existing node if one is found that matches
		//Thus we can use this safely to search for comparisons with nodes in this set
	//	SSSNode pred  = SSSNode.createSSSNode(predLabel, JAMcore.defaultNS);
	//	SSSNode val   = SSSNode.createSSSNode(valLabel,  JAMcore.defaultNS);
		
		//...however, using the getNode function is probably quicker
				
		SSSNode pred = SSSNode.getNodeByLabel(predLabel);
		SSSNode val =  SSSNode.getNodeByLabel(valLabel);
		
		return hasProperty(pred,val);				
	}
	
	
	//TODO: probably faster to covert to nodes first? (above)
	public boolean hasProperty_old(String predURI,String valUri) {
		valUri=valUri.trim();

		if (!valUri.contains("#")) {
			valUri = JAMcore.defaultNS + valUri;
		}
		if (!predURI.contains("#")) {
			predURI = JAMcore.defaultNS + predURI;
		}

		Iterator<SSSProperty> opit = properties.iterator();

		while (opit.hasNext()) {

			SSSProperty cur_property = opit.next();

			Log.info("testing properties (predicate):" + cur_property.getPred().getPURI()+" (val):" + cur_property.getValue().getPURI());
			Log.info("against predicate :" + predURI+" value :" + valUri);


			// if (cur_property.isOrHasParentClass("Internal/"+testThis)){
			if (cur_property.getPred().isOrHasParentClass(predURI)) {

				if (cur_property.getValue().isOrHasParentClass(valUri)) {

					return true;
				}

			}
			// if (cur_property.getPLabel().equalsIgnoreCase(testThis.trim())) {
			// return true;
			// }

		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((properties == null) ? 0 : properties
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		PropertySet other = (PropertySet) obj;
		
		if (properties == null) {
			if (other.properties != null){
				return false;
			}
		}
		//check lengths match
		if (properties.size() != other.properties.size()){
			return false;
		}
		//Finally check their contents match (as we know their the same size, this is all we need)
		return properties.containsAll(other.properties);
	}


	/**
	 * if there is a triplet with the specified predicate in this set, it returns the value associated with it
	 * 
	 * @param predicateToLookFor
	 * @return
	 */
	public SSSNode getValueForPredicate(String predicateToLookFor) {
		
		boolean searchbyURI=false;
		//we normally go by label, but if a colon or hash is present then we look for a uri
		if (predicateToLookFor.contains(":") || predicateToLookFor.contains("#")){
			
			//exapand prefix if needed
			predicateToLookFor = SSSUtilities.getURI(predicateToLookFor);
			
			searchbyURI=true;			
		}
		//

		Iterator<SSSProperty> opit = properties.iterator();

		//Log.info(" number of propertys to test:"+objectsProperties.size());


		while (opit.hasNext()) {

			SSSProperty cur_property = opit.next();

			//	Log.info(" Testing "+cur_property.getPred().getPLabel()+" against: "+predicateToLookFor);

			

			boolean matchs = false;
			

			if (searchbyURI){
				matchs = cur_property.getPred().hasURI(predicateToLookFor);				
			} else {
				matchs = cur_property.getPred().hasLabel(predicateToLookFor);
			}
			
			
			if (matchs){

				//	Log.info(" found match:"+cur_property.getPred());
				//	Log.info(" which has labels:"+cur_property.getPred().getAllPLabels());

				return cur_property.getValue();

			}

			//	Log.info(" (looping) ");

		}
		//	Log.info(" ran loop and nothing was found ");

		return SSSNode.NOTFOUND;
	}



}
