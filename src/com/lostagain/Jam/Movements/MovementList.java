package com.lostagain.Jam.Movements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;
import com.lostagain.Jam.JAMcore;
import com.lostagain.Jam.RequiredImplementations;
import com.lostagain.Jam.InstructionProcessing.ActionList;
import com.lostagain.Jam.Scene.SceneWidget;

/** stores and retrieves an objects list of movements **/
public class MovementList extends HashMap<String, MovementPath> {

	static Logger Log = Logger.getLogger("MovementList");
	/**
	 * creates a new movement list from a string of them laid out like; <br>
	 * <br>
	 * -Path <br>
	 * Name = default <br>
	 * Movement = M 1,2 97,2 97,96 8,99 z <br>
	 * <br>
	 * -Path <br>
	 * Name = square <br>
	 * Movement = M 1,2 97,2 97,96 8,99 z <br>
	 * <br>
	 * <br>
	 * <br>
	 * **/
	
	public MovementList(String newMovementList) {

		Log.info("processing movement: "+newMovementList);
		
		//split by movements
		String paths[] = newMovementList.split("-Path");
		int i = 0;
		int len = paths.length;
		while (i<len) {
			String currentPath = paths[i].trim();
			i++;
			
		//	Log.info("processingPath ="+currentPath);
			
			String PathMovement=null;
			String PathName=null;
			
			//split by line
			String pathsData[] = currentPath.split("\n");
			int pdp = 0;
			int pdlength = pathsData.length;
			while (pdp<pdlength){
				
				
				String currentline = pathsData[pdp].trim();
			//	Log.info("currentLine = "+currentline);
				pdp++;
				
				if (currentline.length()<2){
					continue;
				}
				
				String param = currentline.split("=",2)[0].trim();
				String value = currentline.split("=",2)[1].trim();

				if (param.equalsIgnoreCase("name")){
					PathName = value;
				}
				if (param.equalsIgnoreCase("movement")){
					PathMovement= value;
				}
			}
			
			//create and add the path
			if ((PathName!=null)&&(PathMovement!=null)){
				
		//		Log.info("PathName = "+PathName);
			//	Log.info("PathMovement = "+PathMovement);
				
				
			super.put(PathName, new MovementPath(PathMovement,PathName));
			
			}
			
		}
		
		
		
	}

	/** creates an empty movement list **/
	public MovementList() {
		
	}

	public void addMovement(String name, MovementPath path) {
		super.put(name, path);
	}

	public MovementPath getMovement(String name) {

		return super.get(name);
	}

	public void addMovement(String name, String path) {
		super.put(name, new MovementPath(path,name));
	}

	
	
	/** Global movements
	 * These actions are global to every object in the whole game.
	 * 
	 * 
	 * 
	 * Note the order of testing is;<br> 
	 * <br> 
	 *  We first look for object specific movements of the name requested <br> 
	 *  Then global movements <br> 
	 *
	 *  To prevent ambiguity as to which will run, prefix all your global actions with a name like "_global"
	 ***/
	public static MovementList globalMovements = new MovementList();


	public static void loadGlobalMovements() {

		String GLOBALMOVEMENTLOCATION = SceneWidget.SceneFileRoot + "GlobalMovements.ini";		

		//set what to do when we get the text data retrieved
		FileCallbackRunnable onResponse = new FileCallbackRunnable(){

			@Override
			public void run(String responseData, int responseCode) {

				SceneWidget.Log.info("loading global mopvements");
				
				//store them in globalMovements 
				globalMovements = new MovementList(responseData);


				SceneWidget.Log.info("loaded global mopvements:"+globalMovements.size());
			}

		};

		//what to do if theres an error
		FileCallbackError onError = new FileCallbackError(){
			@Override
			public void run(String errorData, Throwable exception) {
				SceneWidget.Log.info("error loading global movements"
						+ exception.getMessage());
			}					
		};

		//using the above, try to get the text!
		//used to be getTextFile in FileManager
		RequiredImplementations.getFileManager().getText(GLOBALMOVEMENTLOCATION,true,
				onResponse,
				onError,
				false);




	}

}
