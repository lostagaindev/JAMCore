package com.lostagain.Jam;

import java.util.ArrayList;

import com.google.common.base.Optional;

public abstract class FeedbackHistoryCore {
	

	protected static final ArrayList<String> messagehistory = new ArrayList<String>();
	protected static int currentmessage;
	
	public static Optional<FeedbackHistoryCore> feebackHistoryVisualiser = Optional.absent();
	
	
	
	public static void setFeebackHistoryVisualiser(FeedbackHistoryCore	feebackHistoryVisualiser) {
				
		FeedbackHistoryCore.feebackHistoryVisualiser= Optional.fromNullable(feebackHistoryVisualiser);
					
	}
	
	public static String getlastmessage(int Steps) {
		return messagehistory.get(messagehistory.size()-(Steps+1));
	}
	
	
	public static String getprevmessage() {
		currentmessage=currentmessage-1;
		if (currentmessage<=0){
			currentmessage=0;
		}
		return messagehistory.get(currentmessage-1);
	}
	
	
	public static String getnextmessage() {
		currentmessage=currentmessage+1;
		if (currentmessage>messagehistory.size()){
			currentmessage=messagehistory.size();
		}
		return messagehistory.get(currentmessage-1);
		
	}

	

	public abstract void AddNewMessage_notrecorded(String newMessage);
	
	public static void AddNewMessage(String newMessage){

		messagehistory.add(newMessage);
		
		//messageslist.setHTML( messageslist.getHTML()+"<br>"+newMessage );
		if (feebackHistoryVisualiser.isPresent()){
			feebackHistoryVisualiser.get().addToVisualRepresentationOfHistory(newMessage);
		}
		
		
		currentmessage=messagehistory.size();

	}
	
	public abstract void addToVisualRepresentationOfHistory(String addThis);
	
	

}
