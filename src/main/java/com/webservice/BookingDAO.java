package com.webservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
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
		if(userId<0)
			return null;
		List<Booking> userBookings=new ArrayList<Booking>();
		//Instantiate a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` LEFT JOIN desk on booking.deskID_FK=desk.deskID LEFT JOIN bookingdate on booking.bookingID=bookingdate.bookingID_FK WHERE userID_FK='"+userId+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						int bookingId=(Integer.parseInt(rs.getString("bookingID")));
						//int userId=(Integer.parseInt(rs.getString("userID_FK")));
						int deskId=(Integer.parseInt(rs.getString("deskID_FK")));
						int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
						String deskLetter=rs.getString("deskLetter");
						String location=rs.getString("location");
						String date=rs.getString("date");
						//Create a temporary booking object that will be added to the vector
						Booking bookingTemp=new Booking(bookingId,userId,deskId,date,date,deskBlockN,deskLetter,location);
						//Add the temporary booking to the vector
						userBookings.add(bookingTemp);
					}
					//Close the connection with the database
					rs.close();
					//Return all the user information
					return userBookings;
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
