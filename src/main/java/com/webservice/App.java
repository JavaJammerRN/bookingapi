package com.webservice;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.mysql.jdbc.Connection;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		//SpringApplication.run(App.class, args);
		String location="edinburgh";
		//Convert the given strings into Date object
		SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date startDateConverted=dateFormatter.parse("2016-07-25");
			Date endDateConverted=dateFormatter.parse("2016-08-10");
			//Now that the strings are converted, let's find the numbers of days between them
			int daysBetweenDates=BookingDAO.getBookingLength(startDateConverted, endDateConverted);
			//For each day, verify which seats are available

			//Create the List that will contain all the bookings for each day
			//Each location in the outer part of the array indicate the day
			//Each location in the inner array contains a list of available seats
			List<List<Integer>> availableSeats=new ArrayList<List<Integer>>();

			//Retrieve a list of all the seats for the given location
			List<Integer> allSeatsLocation=new ArrayList<Integer>();
			//Instantiate a connection with the database
			Connection connectionDB1=BookingDAO.establishConnection();
			if(connectionDB1!=null){
				try{
					Statement stmt = connectionDB1.createStatement();
					//Re-format the location string
					String loc=location.substring(0,1).toUpperCase()+location.substring(1);
					//Select query
					String query = "SELECT * FROM `desk` WHERE location='"+loc+"'";
					//Execute the query
					boolean status = stmt.execute(query);
					if(status){
						//Extract the data from the resultset object
						ResultSet rs = stmt.getResultSet();
						//Loop around the resultset
						while(rs.next()){
							int deskId=(Integer.parseInt(rs.getString("deskID")));
							//Add the desk to the List
							allSeatsLocation.add(deskId);
						}
						//Close the connection with the database
						rs.close();
					}
				}catch(Exception e){}
			}

			//Verify which seats are still available
			Calendar cal=new GregorianCalendar();
			cal.setTime(startDateConverted);
			for(int i=0; i<daysBetweenDates+1; i++){
				//Make a copy of the all seats arraylist
				List<Integer> seats=new ArrayList<Integer>(allSeatsLocation.size());
				for(Integer val: allSeatsLocation){
					seats.add(val);
				}

				//Verify if the day is a Saturday or a Sunday
				int dayOfTheWeek=cal.get(Calendar.DAY_OF_WEEK);
				//If the day is a Saturday or a Sunday, skip it
				if(dayOfTheWeek!=Calendar.SATURDAY && dayOfTheWeek!=Calendar.SUNDAY){
					Connection connectionDB=BookingDAO.establishConnection();
					if(connectionDB!=null){
						try{
							Statement stmt = connectionDB.createStatement();
							//Re-format the location string
							String loc=location.substring(0,1).toUpperCase()+location.substring(1);
							//Convert the Java Date object to a SQL Date object
							java.sql.Date selectedDate=new java.sql.Date(cal.getTime().getTime());
							//Select query
							String query = "SELECT * FROM `bookingdate` LEFT JOIN booking on "
									+ "bookingdate.bookingID_FK=booking.bookingID WHERE date='"+selectedDate+"'";
							//Execute the query
							boolean status = stmt.execute(query);
							if(status){
								//Extract the data from the resultset object
								ResultSet rs = stmt.getResultSet();
								//Loop around the resultset
								while(rs.next()){
									int deskId=(Integer.parseInt(rs.getString("deskID_FK")));
									//Remove this element from the list of available seats, since it has
									//been reserved for a booking
									if(seats.contains((Integer)deskId))
										seats.remove((Integer)deskId);
								}
								//Close the connection with the database
								rs.close();
							}
							availableSeats.add(seats);
						}catch(Exception e){
							//Add and empty array in case of error
							availableSeats.add(new ArrayList<Integer>());
						}
					}
				}
				else{
					availableSeats.add(new ArrayList<Integer>());
				}
				//Add a day to the current date
				cal.add(Calendar.DATE,1);
			}
			//return availableSeats;
			for(int i=0;i<availableSeats.size(); i++){
				System.out.println(availableSeats.get(i).toString());
			}
		}
		catch(ParseException ex){
			//return null;
		}
	}
}