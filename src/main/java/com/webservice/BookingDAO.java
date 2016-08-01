package com.webservice;

import java.sql.BatchUpdateException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import com.mysql.jdbc.Connection;


public class BookingDAO {

	public List<Booking> getAllBookings(){
		Connection connectionDB=BookingDAO.establishConnection();
		//Create and initialise an object that will store all the bookings within the system
		List<Booking> allBookings=new ArrayList<Booking>();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Adjust the query
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
						String startD=rs.getString("startDate");
						String endD=rs.getString("endDate");
						int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
						String deskLetter=rs.getString("deskLetter");
						String location=rs.getString("location");
						//Create a temporary booking object that will be added to the vector
						Booking bookingTemp=new Booking(bookingId,userId,deskId,startD,endD,deskBlockN,deskLetter,location);
						//Add the temporary booking to the vector
						allBookings.add(bookingTemp);
					}
					//Close the connection with the database
					rs.close();
					//Return all the user information
					return allBookings;
				}
			}
			catch(Exception e){}
		}
		return null;
	}
	
	//Method to create booking for a specified user
	public static String createBooking(int userID, int deskID, String inputStartDate, String inputEndDate) throws SQLException, ParseException{

		try{
			//Get connection & disable auto commit for batch excecution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);
			
			//Instert into booking table, return auto generated ID
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO booking (userID_FK, deskID_FK) VALUES(?,?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, userID);
			stmt.setInt(2, deskID);
			stmt.execute();
			
			//get auto generated ID for bookingdate table
			int generatedUserID = 0;
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                generatedUserID = generatedKeys.getInt(1);
	            }
	        }
			
			//Insert bookin dates records an commit
			instertBookingDates(conn, stmt, generatedUserID, inputStartDate, inputEndDate);
			conn.commit();
			return "Booking has been created";
		} catch(SQLException ex) {
	        return ex.toString();
	    }
		
	}
	
	//Method to update booking for a specified user
	public static String updateBooking(int bookingID, String newStartDate, String newEndDate) throws SQLException, ParseException{
		try{
			//Get connection & disable auto commit for batch excecution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);
			
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookingdate WHERE bookingID_FK = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();
			
			//Insert bookin dates records an commit
			instertBookingDates(conn, stmt, bookingID, newStartDate, newEndDate);
			conn.commit();
			return "Booking: " + bookingID + " has been updated.";
		}catch(SQLException e){
			return e.toString();
		}
		
	}
	
	public static String deleteBooking(int bookingID) {
		try{
			//Get connection & disable auto commit for batch excecution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);
			
			PreparedStatement stmt = conn.prepareStatement("DELETE FROM booking WHERE bookingID = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();
			conn.commit();
			return "Booking " + bookingID + " has been deleted.";
		}catch(SQLException e){
			return e.toString();
		}
	}
	
	
	private static void instertBookingDates(Connection conn, PreparedStatement stmt, int generatedUserID, String inputStartDate, String inputEndDate) throws SQLException, ParseException{
		stmt = conn.prepareStatement("INSERT INTO bookingdate (bookingID_FK, date) VALUES(?,?)");
		LocalDate startDate = LocalDate.parse(inputStartDate);
		LocalDate endDate = LocalDate.parse(inputEndDate);
		for(LocalDate ld = startDate; ld.isBefore(endDate.plusDays(1)); ld = ld.plusDays(1)){
			stmt.setInt(1, generatedUserID);
			stmt.setDate(2, stringToSQLDate(ld.toString()));
			stmt.addBatch();
		 }
		 stmt.executeBatch();
	}
	
	private static java.sql.Date stringToSQLDate(String stringDate) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date dateStr = formatter.parse(stringDate);
		return  new java.sql.Date(dateStr.getTime());
	}
	
	private static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Use for Michaels DB
//			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195GRV:3306/hotdesk_db?" +"user=hotdesk&password=hotdesk");
			//Use for Reds DB
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}


}
