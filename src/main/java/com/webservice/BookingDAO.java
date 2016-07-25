package com.webservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import com.mysql.jdbc.Connection;

public class BookingDAO {

	public static List<Booking> getAllBookings(){
		Connection connectionDB=BookingDAO.establishConnection();
		//Create and initialise an object that will store all the bookings within the system
		List<Booking> allBookings=new ArrayList<Booking>();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` LEFT JOIN desk on booking.deskID_FK=desk.deskID";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						int bookingId=(Integer.parseInt(rs.getString("bookingID")));
						int userId=(Integer.parseInt(rs.getString("userID_FK")));
						int deskId=(Integer.parseInt(rs.getString("deskID_FK")));
						int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
						String deskLetter=rs.getString("deskLetter");
						String location=rs.getString("location");
						//Create a temporary booking object that will be added to the vector
						//The start and end date of the booking are currently set to null
						Booking bookingTemp=new Booking(bookingId,userId,deskId,null,null,deskBlockN,deskLetter,location);
						//Add the temporary booking to the vector
						allBookings.add(bookingTemp);
					}
					//Close the connection with the database
					rs.close();
					//Return all the user information
					return allBookings;
				}
			}catch(Exception e){}
		}
		return null;
	}

	/*
	 * This methods retrieves all the bookings linked to a specific userID.
	 * Future Developments: Retrieve only the bookings within a date range to limit the amount of data to elaborate
	 */
	public static List<Booking> getAllBookingsForSpecificUser(int userId){
		//Validate the userId
		if(userId<1)
			return null;
		List<Integer> userBookingIDs=new ArrayList<Integer>();
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
					return userBookings;
				}
			}catch(Exception e){}
		}
		return null;
	}
	
	public static Booking getSingleBookingForSpecificUser(int userId, int bookingId){
		//Validate the data
		if(userId<1 && bookingId<0)
			return null;
		//Create a temporary booking list
		List<Booking> bookingList=new ArrayList<Booking>();
		//Create a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` LEFT JOIN "
						+ "desk on booking.deskID_FK=desk.deskID LEFT JOIN bookingdate on "
						+ "booking.bookingID=bookingdate.bookingID_FK WHERE userID_FK='"+userId+"' AND bookingID='"+bookingId+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Create and instantiate the booking object that will be returned with all the information
					Booking userBooking=new Booking();
					java.sql.Date startDTemp=null;
					java.sql.Date endDTemp=null;
					
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						//int bookingId=(Integer.parseInt(rs.getString("bookingID")));
						//int userId=(Integer.parseInt(rs.getString("userID_FK")));
						int deskId=(Integer.parseInt(rs.getString("deskID_FK")));
						int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
						String deskLetter=rs.getString("deskLetter");
						String location=rs.getString("location");
						String date=rs.getString("date");
						//Create a temporary booking object that will be added to the vector
						Booking bookingTemp=new Booking(bookingId,userId,deskId,date,date,deskBlockN,deskLetter,location);
						bookingList.add(bookingTemp);
					}
					//Close the connection with the database
					rs.close();

					//The Booking objects inside the bookingList are all the same apart from the date of the booking.
					//The following code will find the first and last date for the given booking id, add them to the Booking object
					//and return it to the user

					//Create temporary start date;
					//Add 20 years to the current date
					Calendar cal=Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date();
					dateFormat.format(date);
					cal.setTime(date);
					//This specific line adds the 20 years to the current date
					cal.add(Calendar.YEAR, 20);
					//Convert the Date created to a SQL Date 
					startDTemp=new java.sql.Date(cal.getTime().getTime());

					//Loop that finds the start date of the booking
					for(int i=0; i<bookingList.size(); i++){
						//Copy the content of the first Booking object of the list inside the booking object that has to
						//be returned
						if(i==0)
							userBooking=Booking.cloneBooking(bookingList.get(i));
						if(bookingList.get(i).getStartDate().before(startDTemp))
							startDTemp=bookingList.get(i).getStartDate();
					}
					
					//Loop that finds the end date of the booking
					for(int i=0; i<bookingList.size(); i++){
						if(bookingList.get(i).getEndDate().after(startDTemp)){
							endDTemp=bookingList.get(i).getEndDate();
						}
					}
				
					//Now that the start and end date of the booking have been found,
					//pass the data to the booking object
					userBooking.setStartDate(startDTemp.toString());
					userBooking.setEndDate(endDTemp.toString());
					
					//The booking object now contains all the information required.
					//Return it to the user
					return userBooking;
				}
			}catch(Exception e){}
		}
		return null;
	}



	static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195GRV:3306/hotdesk_db?" +"user=hotdesk&password=hotdesk");
		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}
}
