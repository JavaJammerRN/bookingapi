package com.webservice;

import java.util.List;

public class BookingTable {
	private int deskID;
	private List<String> dates;

	
	public BookingTable(){}
	

	
	public void setDeskID(int id){
		this.deskID=id;
	}
	public int getDeskID(){
		return deskID;
	}
	public void setDates(List<String> dates){
		this.dates=dates;
	}
	public List<String> getDates(){
		return dates;
	}

}
