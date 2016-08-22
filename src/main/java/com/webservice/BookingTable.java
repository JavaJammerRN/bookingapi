package com.webservice;

import java.util.ArrayList;
import java.util.List;

public class BookingTable {
	private int deskID;
	private List<java.sql.Date> dates;
	
	public BookingTable(){
	}
	public BookingTable(int id, List<java.sql.Date> dates){
		this.deskID=id;
		this.dates=dates;
	}
	
	public void setDeskId(int id){
		this.deskID=id;
	}
	public int getDeskId(){
		return deskID;
	}
	public void setDates(List<java.sql.Date> dates){
		this.dates=dates;
	}
	public void addDate(java.sql.Date date){
		if(dates==null){
			dates=new ArrayList<java.sql.Date>();
		}
		dates.add(date);
	}
	public List<java.sql.Date> getDates(){
		return dates;
	}
	public boolean datesValidity(){
		return (dates!=null)? true:false;
	}
}
