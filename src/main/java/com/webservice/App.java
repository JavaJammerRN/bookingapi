package com.webservice;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mysql.jdbc.Connection;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
		
		/*List<Integer> userBookingIDs=new ArrayList<Integer>();
		List<Booking> userBookings=new ArrayList<Booking>();
		//Instantiate a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` WHERE userID_FK='"+userId+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						int bookingId=(Integer.parseInt(rs.getString("bookingID")));
						//Add bookingID to the List, if not already inserted
						if(!userBookingIDs.contains(bookingId))
							userBookingIDs.add(bookingId);
					}
					//Close the connection with the database
					rs.close();
					
					//Now that all the bookings IDs have been found for a specific user, let's group them into single bookings with a start and end date
					for(int i=0; i<userBookingIDs.size(); i++){
						userBookings.add(getSingleBookingForSpecificUser(userId, userBookingIDs.get(i)));
					}
					//Return all the bookings
				}
			}catch(Exception e){}
		}*/

		
	}
}