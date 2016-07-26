package com.webservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.mysql.jdbc.Connection;

public class BookingDAO {

	private final static String DATE_PATTERN="yyyy-MM-dd";

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
	 * 
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

	/*
	 * This method retrieves a specific booking provided a user id and a booking id
	 * 
	 */
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
					DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
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

	/*
	 * This method returns all the available seats provided a location, a start date and an end date
	 * The results are stored into a List of Integers, since each seat is identified with an unique number
	 * 
	 */
	public static List<List<Integer>> getAvailableSeatsLocation(String location, String startD, String endD){
		if(!location.equals("")){
			//Convert the given strings into Date object
			SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd");
			try{
				Date startDateConverted=dateFormatter.parse(startD);
				Date endDateConverted=dateFormatter.parse(endD);
				//Now that the strings are converted, let's find the numbers of days between them
				int daysBetweenDates=getBookingLength(startDateConverted, endDateConverted);
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
								return null;
							}
						}
					}
					else{
						availableSeats.add(new ArrayList<Integer>());
					}
					//Add a day to the current date
					cal.add(Calendar.DATE,1);
				}
				return availableSeats;
			}
			catch(ParseException ex){
				return null;
			}
		}
		return null;
	}

	/*
	 * This method is used to calculate how many days the booking is for
	 * 
	 */
	static int getBookingLength(Date startDate, Date endDate){
		int counter=0;
		long diff=endDate.getTime()-startDate.getTime();
		counter=(int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return counter;
	}

	/*
	 * This method provides to establish a connection with the MySQL Database.
	 * Once the connection has been established, a reference object is returned so that the other parts of the application can
	 * use it to retrieve/create/modify data
	 */
	private static Connection establishConnection(){
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
