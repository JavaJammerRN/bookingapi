package com.webservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookingTable {

	//Constants
	private final String DATE_PATTERN="yyyy-MM-dd";
	private final SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);

	private int deskID;
	private List<java.sql.Date> dates;

	public BookingTable(){}
	
	public BookingTable(int id, List<String> datesString){
		this.deskID=id;
		for(String element: datesString){
			this.addDate(element);
		}
	}

	public void setDeskId(int id){
		this.deskID=id;
	}
	public int getDeskId(){
		return deskID;
	}
	public void setDates(List<String> dateString){
		for(String element: dateString){
			this.addDate(element);
		}
	}
	public void addDate(String date){
		if(dates==null){
			dates=new ArrayList<java.sql.Date>();
		}
		dates.add(this.convertStringToSQLDate(date));
	}
	public List<java.sql.Date> getDates(){
		return dates;
	}
	/*
	 * This method defines the structure of the Date variables
	 */
	private java.sql.Date convertStringToSQLDate(String date){
		try{
			Date dateConverted=dateFormatter.parse(date);
			return new java.sql.Date(dateConverted.getTime());
		}
		catch(ParseException ex){}
		return null;
	}
	public boolean datesValidity(){
		return (dates!=null)? true:false;
	}
}
