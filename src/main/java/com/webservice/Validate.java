package com.webservice;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.mysql.jdbc.Connection;

public class Validate {
	private enum idType{
		USER,
		DESK,
		BOOKING
	}


	public static Validator validateCreateBooking(int userID, int deskID, String inputStartDate, String inputEndDate){
		Validator val = null;
		val = validateDates(inputStartDate, inputEndDate);
		if(!val.pass)
			return val;
		
		val = validateID(idType.USER, userID);
		if(!val.pass)
			return val;
		
		val = validateID(idType.DESK, deskID);
		if(!val.pass)
			return val;
		
		return new Validator(true,"");
	}
	
	public static Validator validateUpdateBooking(int bookingID, String inputStartDate, String inputEndDate){
		Validator val = null;
		val = validateDates(inputStartDate, inputEndDate);
		if(!val.pass)
			return val;
		
		val = validateID(idType.BOOKING, bookingID);
		if(!val.pass)
			return val;

		return new Validator(true,"");
	}
	
	public static Validator validateDeleteBooking(int bookingID){
		Validator val = null;
		val = validateID(idType.BOOKING, bookingID);
		if(!val.pass)
			return val;

		return new Validator(true,"");
	}
	
	private static Validator validateDates(String inputStartDate, String inputEndDate){
		LocalDate startDate = LocalDate.parse(inputStartDate);
		LocalDate endDate = LocalDate.parse(inputEndDate);
		LocalDate today = LocalDate.now();
		if(endDate.isBefore(startDate)){
			 return new Validator(false,"End date can not be before start date.");
		}
		if(startDate.isBefore(today)){
			 return new Validator(false,"Booking can not be booked in the past.");
		}
		return new Validator(true,"");
	}
	
	private static Validator validateID(idType type, int id){
		try{
			Connection conn = BookingDAO.establishConnection();
			PreparedStatement stmt = null;
			switch(type){
			case USER:
				stmt = conn.prepareStatement("SELECT * FROM user WHERE userID = ?");
				break;
			case DESK:
				stmt = conn.prepareStatement("SELECT * FROM desk WHERE deskID = ?");
				break;
			case BOOKING:
				stmt = conn.prepareStatement("SELECT * FROM booking WHERE bookingID = ?");
				break;
			}
			stmt.setInt(1, id);
			ResultSet rs = stmt.executeQuery();
			if(!rs.next()){
				return new Validator(false,"This " + type + "ID does not exist.");
				}
			return new Validator(true,"");
		}catch(SQLException SLQe){
			return new Validator(false,"Something went wrong, please try again later.");
		}

	}
	
	
	
	static class Validator{
		public boolean pass;
		public String message;
		
		public Validator(boolean pass, String message){
			this.pass = pass;
			this.message = message;
		}
		
		public Validator(){
			this.pass = true;
			this.message = "";
		}
		
		public void setValidator(boolean pass, String message){
			this.pass = pass;
			this.message = message;
		}
	}
	
}

