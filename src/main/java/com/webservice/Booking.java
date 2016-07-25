package com.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Booking {
	
	//Global Variables
	private int bookingID, userID, deskID, deskBlock;
	private java.sql.Date startDate, endDate;
	private final String DATE_PATTERN="yyyy-MM-dd";
	private SimpleDateFormat dateFormatter;
	private String deskLetter, location;
	
	//Empty Constructor
	public Booking(){
		bookingID=0;
		userID=0;
		deskID=0;
		startDate=null;
		endDate=null;
		deskBlock=0;
		deskLetter="";
		location="";
	}
	//Constructor with Parameters
	public Booking(int bookingId, int userId, int deskId, String sDate, String eDate, int deskBlock, String deskLet, String loc){
		this.setBookingID(bookingId);
		this.setUserIDFK(userId);
		this.setDeskID(deskId);
		this.setStartDate(sDate);
		this.setEndDate(eDate);
		this.setDeskBlock(deskBlock);
		this.setDeskLetter(deskLet);
		this.setLocation(loc);
	}
	
	//Gets and Setters
	public void setBookingID(int id){
		if(id>0)
			bookingID=id;
		else
			bookingID=-1;
	}
	public int getBookingID(){
		return bookingID;
	}
	public void setUserIDFK(int userId){
		if(userId>0)
			userID=userId;
		else
			userID=-1;
	}
	public int getUserID(){
		return userID;
	}
	public void setDeskID(int deskId){
		if(deskId>0)
			deskID=deskId;
		else
			deskID=-1;
	}
	public int getDeskID(){
		return deskID;
	}
	public void setStartDate(String date){
		//Verify that the date received is structured as it should be and cast it to a sql date object
		startDate=this.convertStringToSQLDate(date);	
	}
	public java.sql.Date getStartDate(){
		return startDate;
	}
	public void setEndDate(String date){
		//Verify that the date received is structured as it should be and cast it to a sql date object
		endDate=this.convertStringToSQLDate(date);		
	}
	public java.sql.Date getEndDate(){
		return endDate;
	}
	public void setDeskBlock(int blockNum){
		if(blockNum>0)
			deskBlock=blockNum;
		else
			deskBlock=-1;
	}
	public int getDeskBlock(){
		return deskBlock;
	}
	public void setDeskLetter(String blockLetter){
		//Make sure the blockLetter is not empty and its length does not exceed 2 chars
		if(!blockLetter.equals("")  && blockLetter.length()<2)
			deskLetter=blockLetter;
		else
			deskLetter="InvalidDeskLetter";
	}
	public String getDeskLetter(){
		return deskLetter;
	}
	public void setLocation(String loc){
		if(!loc.equals(""))
			location=loc;
		else
			location="InvalidLocation";
	}
	public String getLocation(){
		return location;
	}
	/*
	 * This method defines the structure of the Date variables
	 */
	private java.sql.Date convertStringToSQLDate(String date){
		dateFormatter=new SimpleDateFormat(DATE_PATTERN);
		try{
			Date dateConverted=dateFormatter.parse(date);
			return new java.sql.Date(dateConverted.getTime());
		}
		catch(ParseException ex){}
		return null;
	}
	public String toString(){
		String s="";
		s+="BookingID: "+this.getBookingID()+"\nUserID: "+this.getUserID()+"\nDeskID: "+this.getDeskID()+"\nStartDate: "+this.getStartDate()+"\nEndDate: "+this.getEndDate()+"\nDeskBlock: "+this.getDeskBlock()+"\nDeskLetter: "+this.getDeskLetter()+"\nLocation: "+this.getLocation()+"\n---------------------------------";
		return s;
	}
	
	public static Booking cloneBooking(Booking b){
		Booking obj=new Booking();
		obj.setBookingID(b.getBookingID());
		obj.setUserIDFK(b.getUserID());
		obj.setDeskID(b.getDeskID());
		obj.setStartDate(b.getStartDate().toString());
		obj.setEndDate(b.getEndDate().toString());
		obj.setDeskBlock(b.getDeskBlock());
		obj.setDeskLetter(b.getDeskLetter());
		obj.setLocation(b.getLocation());
		return obj;
	}

}
