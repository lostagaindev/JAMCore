package com.lostagain.Jam;

import java.util.logging.Logger;

import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackError;
import com.darkflame.client.interfaces.SSSGenericFileManager.FileCallbackRunnable;


public abstract class GamesInterfaceTextCore {

	
	static Logger Log = Logger.getLogger("JAMCore.GamesInterfaceTextCore");
	
	
	//specific implementation
	
	
	//all the games strings here!;
	


	//all the strings used in MyApplication (aka, the main game loop)

	public static String MainGame_GeneralConfirm = "";
	public static String MainGame_GeneralOk= "";
	public static String MainGame_GeneralCancel= "";
	
	public static String MainGame_Submit = "";
	public static String MainGame_SaveOrLoadYourGame= "";
	
	//Load dialogue popup
	public static String MainGame_LoadSavedGame = "";
	public static String MainGame_FromThisBrowser = "";
	public static String MainGame_FromServer = "";
	public static String MainGame_FromText = "";
	public static String MainGame_NotLoggedIn = "";
	public static String MainGame_LoadDataPastedBelow = "";
	public static String MainGame_ClickBelowToLoadYourGame="";
	
	//------------
	//save dialogue popup
	public static String MainGame_SaveYourGame = "";
	public static String MainGame_ToThisBrowser = "";
	public static String MainGame_AsAWebLink = "";
	public static String MainGame_OnServer = "";
	public static String MainGame_AsText = "";
	
	public static String MainGame_NoteThisWillSaveYourGameOnThisPCAndBrowserOnly= "";
	public static String MainGame_SaveStateAsCompressedLink= "";
	public static String MainGame_UpdatingPleaseWait = "";
	public static String MainGame_Compressing = "";
	public static String MainGame_EmailSent = "";
	
	public static String MainGame_EmailLinkToMe = "";
	public static String MainGame_UpdateLinkFromBox = "";
	public static String MainGame_LoadBoxDataIntoGame = "";
	public static String MainGame_ResetFirst = "";
	public static String MainGame_SaveDataToBox = "";
	public static String MainGame_ResetData = "";
	public static String MainGame_ThisBigStringContainsYourSavegame = "";

	
	//----------
	
	public static String MainGame_Inventory= "";
	public static String MainGame_Charecter_Profiles= "";
	public static String MainGame_Secrets_Found= "";
	public static String MainGame_CloseAlWindows= "";
	public static String MainGame_Loading= "";
	public static String MainGame_WindowTitle_Loading= "";
	public static String MainGame_StoryText_Loading = "";
	public static String MainGame_Sending = "";	
	public static String MainGame_is_on_chapter = "";
	public static String MainGame_submit = "";
	public static String MainGame_YouAlreadyHaveThisItem = "";
	public static String MainGame_Welcome_Back = "";	
	public static String MainGame_LoadingNewGame = "";
	public static String MainGame_StartANewGame = "";
	//maths
	public static String MainGame_Mathsbeforeans = "mm..thats..";
	public static String MainGame_Mathsafterans = "..I think";
	//score
	public static String MainGame_Score_Awarded = "ScoreAwarded";
	
	//--------
	

	//Scene loading
	public static String Scene_LoadingLoadingTime = "";
	public static String Scene_SettingUpScene = "";
	public static String Scene_GettingScenesData = "";
	
	//all the strings used in the controll panel
	public static String ControllPanel_AnimationEffects = "";
	public static String ControllPanel_Big= "";
	public static String ControllPanel_ChangePassword= "";
	public static String ControllPanel_ClickHereToLogOut= "";
	public static String ControllPanel_ClickHereToSaveYourProgress= "";
	public static String ControllPanel_LoadFromServer = "";
	public static String ControllPanel_ControllPanel= "";
	public static String ControllPanel_DataSaved= "";
	public static String ControllPanel_Default= "";
	public static String ControllPanel_InterfaceSize = "";
	public static String ControllPanel_IECantDoLongLinks = "Note; InternetExplorer is unable to load savegames from links due to it lacking support for urls this long. If you wish to save this way, please load your links on browsers that do like Opera,Firefox,Chrome or Safari";
	
	
	public static String ControllPanel_LoadFromText = "";
	public static String ControllPanel_LoadOptions = "";
	public static String ControllPanel_SaveOptions = "";
	public static String ControllPanel_LoggedOut = "";
	public static String ControllPanel_loggingout = "";
	public static String ControllPanel_loggingout2 = "";
	public static String ControllPanel_Medium = "";	
	public static String ControllPanel_off = "";
	public static String ControllPanel_on = "";
	public static String ControllPanel_Small = "";
	public static String ControllPanel_SoundEffects = "";
	public static String ControllPanel_QualitySetting = "";
	public static String ControllPanel_CurrentMusic = "Current Music:";
	
	//in Gamereset box
	public static String GameReset_Title = "Reset Game";
	public static String GameReset_Button = "Start Game From Scratch";
	public static String GameReset_Warning = "";
	//--------
	//for magnifying item
	public static String Magnify_Magnify = "";	
	//LoginScreen text
	public static String Login_PleaseLogin = "Please Login";
	public static String Login_Username = "";
	public static String Login_Password = "Password:";
	public static String Login_Rememberme = "Remember me next time?";
	public static String Login_SignUp = "SignUp";
	public static String Login_GuestLogin = "Login As Guest";
	public static String Login_ForgotPassword = "Forgot Password?";
	public static String Login_ClearCookies= "";
	
	//For CluePanel
	
	public static String CluePanel_loading = "Loading...";
	public static String CluePanel_instructions = "";
	public static String CluePanel_instructions2 = "";
	public static String CluePanel_chapterheader = "";
	public static String CluePanel_buy = "buy";
	public static String CluePanel_cancel = "cancel";
	public static String CluePanel_purchasedclue = "";
	
	
	
	//array for loading text;
	//This probably could have been a hashmap, but I wrote
	//this before I knew hashmaps existed!
	//hence its an associative array...which is a class I made myself.
	public AssArray FileTemp = new AssArray();
	
	public abstract void assignStringsToInterface();

	protected void assignStringsToDatabase() {
		
		//JAM.DebugWindow.addText("\n assigning stings");
		
		//MyApplication Strings
		MainGame_GeneralConfirm = FileTemp.GetItem("MainGame_GeneralConfirm");
		MainGame_GeneralOk= FileTemp.GetItem("MainGame_GeneralOk");
		MainGame_GeneralCancel= FileTemp.GetItem("MainGame_GeneralCancel");
		//--
		MainGame_Submit                   = FileTemp.GetItem("MainGame_Submit");
		MainGame_SaveOrLoadYourGame       = FileTemp.GetItem("MainGame_SaveOrLoadYourGame");		
		MainGame_LoadSavedGame            = FileTemp.GetItem("MainGame_LoadSavedGame");
		//loading stuff
		MainGame_FromThisBrowser          = FileTemp.GetItem("MainGame_FromThisBrowser");
		MainGame_FromServer               = FileTemp.GetItem("MainGame_FromServer");
		MainGame_FromText                 = FileTemp.GetItem("MainGame_FromText");
		MainGame_NotLoggedIn              = FileTemp.GetItem("MainGame_NotLoggedIn");
		MainGame_LoadDataPastedBelow      = FileTemp.GetItem("MainGame_LoadDataPastedBelow");
		MainGame_ClickBelowToLoadYourGame = FileTemp.GetItem("MainGame_ClickBelowToLoadYourGame");
		
		//saving stuff
		MainGame_SaveYourGame  = FileTemp.GetItem("MainGame_SaveYourGame");
			
		MainGame_ToThisBrowser = FileTemp.GetItem("MainGame_ToThisBrowser");
		MainGame_AsAWebLink    = FileTemp.GetItem("MainGame_AsAWebLink");
		MainGame_OnServer      = FileTemp.GetItem("MainGame_OnServer");
		MainGame_AsText        = FileTemp.GetItem("MainGame_AsText");
		MainGame_NoteThisWillSaveYourGameOnThisPCAndBrowserOnly  = FileTemp.GetItem("MainGame_NoteThisWillSaveYourGameOnThisPCAndBrowserOnly");
		MainGame_SaveStateAsCompressedLink   = FileTemp.GetItem("MainGame_SaveStateAsCompressedLink");
		
		MainGame_UpdatingPleaseWait = FileTemp.GetItem("MainGame_UpdatingPleaseWait");
		MainGame_Compressing = FileTemp.GetItem("MainGame_Compressing");
		MainGame_EmailSent = FileTemp.GetItem("MainGame_EmailSent");
		
		MainGame_EmailLinkToMe = FileTemp.GetItem("MainGame_EmailLinkToMe");
		MainGame_UpdateLinkFromBox = FileTemp.GetItem("MainGame_UpdateLinkFromBox");
		MainGame_LoadBoxDataIntoGame = FileTemp.GetItem("MainGame_LoadBoxDataIntoGame");
		MainGame_ResetFirst = FileTemp.GetItem("MainGame_ResetFirst");
		MainGame_SaveDataToBox = FileTemp.GetItem("MainGame_SaveDataToBox");
		MainGame_ResetData = FileTemp.GetItem("MainGame_ResetData");
		MainGame_ThisBigStringContainsYourSavegame = FileTemp.GetItem("MainGame_ThisBigStringContainsYourSavegame");
	
	
		
		//-----------
		
		MainGame_Inventory = FileTemp.GetItem("MainGame_Inventory");
		MainGame_Charecter_Profiles = FileTemp.GetItem("MainGame_Charecter_Profiles");
		MainGame_Secrets_Found = FileTemp.GetItem("MainGame_Secrets_Found");
		MainGame_CloseAlWindows = FileTemp.GetItem("MainGame_CloseAlWindows");
		MainGame_Loading = FileTemp.GetItem("MainGame_Loading");
		MainGame_WindowTitle_Loading = FileTemp.GetItem("MainGame_WindowTitle_Loading");
		MainGame_StoryText_Loading = FileTemp.GetItem("MainGame_StoryText_Loading");
		MainGame_Sending = FileTemp.GetItem("MainGame_Sending");
		MainGame_is_on_chapter = FileTemp.GetItem("MainGame_is_on_chapter");
		MainGame_submit = FileTemp.GetItem("MainGame_submit");
		MainGame_YouAlreadyHaveThisItem = FileTemp.GetItem("MainGame_YouAlreadyHaveThisItem");
	
		MainGame_Welcome_Back = FileTemp.GetItem("MainGame_Welcome_Back");
		MainGame_LoadingNewGame = FileTemp.GetItem("MainGame_LoadingNewGame");
		
		MainGame_StartANewGame = FileTemp.GetItem("MainGame_StartANewGame");
		
	
	//maths
		MainGame_Mathsbeforeans  = FileTemp.GetItem("MainGame_Maths_Before_Ans");
		MainGame_Mathsafterans  = FileTemp.GetItem("MainGame_Maths_After_Ans");
		//score
		MainGame_Score_Awarded = FileTemp.GetItem("MainGame_ScoreAwarded");
	
		//scene loading
		Scene_LoadingLoadingTime = FileTemp.GetItem("Scene_LoadingLoadingTime");
		Scene_SettingUpScene = FileTemp.GetItem("Scene_SettingUpScene");
		Scene_GettingScenesData = FileTemp.GetItem("Scene_GettingScenesData");
		
		//Controll Panel strings
	    ControllPanel_AnimationEffects = FileTemp.GetItem("ControllPanel_AnimationEffects");
		ControllPanel_Big= FileTemp.GetItem("ControllPanel_Big");
		ControllPanel_ChangePassword= FileTemp.GetItem("ControllPanel_ChangePassword");
		ControllPanel_ClickHereToLogOut= FileTemp.GetItem("ControllPanel_ClickHereToLogOut");
		ControllPanel_ClickHereToSaveYourProgress= FileTemp.GetItem("ControllPanel_ClickHereToSaveYourProgress");
		ControllPanel_LoadFromServer = FileTemp.GetItem("ControllPanel_LoadFromServer");
		ControllPanel_ControllPanel= FileTemp.GetItem("ControllPanel_ControllPanel");
		ControllPanel_DataSaved= FileTemp.GetItem("ControllPanel_DataSaved");
		ControllPanel_Default= FileTemp.GetItem("ControllPanel_Default");
		ControllPanel_InterfaceSize = FileTemp.GetItem("ControllPanel_InterfaceSize");
		ControllPanel_LoadFromText = FileTemp.GetItem("ControllPanel_LoadFromText");
		ControllPanel_LoadOptions = FileTemp.GetItem("ControllPanel_LoadOptions");
		ControllPanel_SaveOptions = FileTemp.GetItem("ControllPanel_SaveOptions");
		ControllPanel_LoggedOut = FileTemp.GetItem("ControllPanel_LoggedOut");
		ControllPanel_loggingout = FileTemp.GetItem("ControllPanel_loggingout");
		ControllPanel_loggingout2 = FileTemp.GetItem("ControllPanel_loggingout2");
		ControllPanel_Medium = FileTemp.GetItem("ControllPanel_Medium");
		ControllPanel_off = FileTemp.GetItem("ControllPanel_off");
		ControllPanel_on = FileTemp.GetItem("ControllPanel_on");
		ControllPanel_Small = FileTemp.GetItem("ControllPanel_Small");
		ControllPanel_SoundEffects = FileTemp.GetItem("ControllPanel_SoundEffects");
		ControllPanel_CurrentMusic = FileTemp.GetItem("ControllPanel_CurrentMusic"); 
		ControllPanel_IECantDoLongLinks = FileTemp.GetItem("ControllPanel_IECantDontLongLinks");
		ControllPanel_QualitySetting = FileTemp.GetItem("ControllPanel_QualitySetting");
		
		//reset game box
		GameReset_Title = FileTemp.GetItem("ResetGame_Title");
		GameReset_Button =  FileTemp.GetItem("ResetGame_Button");
		GameReset_Warning =  FileTemp.GetItem("ResetGame_Warning");
		 
		//---for magnifying glass item		
		Magnify_Magnify = FileTemp.GetItem("Magnify_Magnify");
		//LoginScreen text
		Login_PleaseLogin = FileTemp.GetItem("Login_PleaseLogin");
		 Login_Username =FileTemp.GetItem("Login_Username");
		Login_Password = FileTemp.GetItem("Login_Password");
		 Login_Rememberme = FileTemp.GetItem("Login_Rememberme");
		 Login_SignUp = FileTemp.GetItem("Login_SignUp");
		 Login_GuestLogin = FileTemp.GetItem("Login_GuestLogin");
		 Login_ForgotPassword = FileTemp.GetItem("Login_ForgotPassword");
		Login_ClearCookies= FileTemp.GetItem("Login_ClearCookies");
		
		//JAM.DebugWindow.addText("\n assigning stings1");
		
		
		//Login Window CluePanel_instructions1
	
		CluePanel_loading = FileTemp.GetItem("CluePanel_loading");
		CluePanel_instructions =FileTemp.GetItem("CluePanel_instructions"); 
		CluePanel_instructions2 = FileTemp.GetItem("CluePanel_instructions2");
		CluePanel_chapterheader = FileTemp.GetItem("CluePanel_chapterheader");
		CluePanel_buy = FileTemp.GetItem("CluePanel_buy");
		CluePanel_cancel = FileTemp.GetItem("CluePanel_cancel");
		CluePanel_purchasedclue = FileTemp.GetItem("CluePanel_purchasedclue");
	
		
		//now update the boxes to match the strings
	
		assignStringsToInterface();
	
		Log.info("loaded games text");
		
		
		}

	/** Loads all the general interface text from the properties file. **/
	public void loadGamesText() {


		String GameInterfaceTextLocation =  RequiredImplementations.getHomedirectory() + "text/messages_"+JAMcore.LanguageExtension+".properties";
		
		
		FileCallbackRunnable onSuccess =  new FileCallbackRunnable() {
			
			@Override
			public void run(String responseData, int responseCode) {
				Log.info("loading games text");
				if(responseData.length()<3){
					Log.info("loading games text failed. Check that text/messages_dutch.properties exists");
					return;
				}
				// load the text
				LoadText(responseData);


				JAMcore.GameLogger.log("loaded games text","green");

			}
		};
		
		FileCallbackError onError = new FileCallbackError() {
			
			@Override
			public void run(String errorData, Throwable exception) {
				JAMcore.GameLogger.log("failed to get games interface text .property file ","red"); 
			}
		};
		
		
		RequiredImplementations.getFileManager().getText(GameInterfaceTextLocation,false,onSuccess,onError , false);
		
		
		
		
	}

	public void LoadText(String messages) {
		int newlinepos = 0;
		int curpos = 0;
		String currentline="";
		//JAM.DebugWindow.addText("\n loading text-"+messages);
		
		while (curpos+3<messages.length()){
			newlinepos = messages.indexOf("\n", curpos+1);
			if (newlinepos == -1)
			{
				newlinepos = messages.length();
			}
			//current line
			currentline=messages.substring(curpos, newlinepos);
			
			String currentmess = currentline.split("=")[0];
			String currentstring = currentline.split("=")[1];
			
			//put into ass
			FileTemp.AddItem(currentstring.trim(), currentmess.trim());
			
			//JAM.DebugWindow.addText("   \n"+currentstring.trim()+" "+currentmess.trim());
			
			curpos= newlinepos;
			
		}
			//now we use the array to assign the strings
		//JAM.DebugWindow.addText("\n assigning text");
		
		assignStringsToDatabase();
	}

}
