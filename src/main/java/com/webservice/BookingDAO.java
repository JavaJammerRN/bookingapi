package com.webservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Connection;
import com.webservice.Validate.Validator;


public class BookingDAO {

	private final static String DATE_PATTERN="yyyy-MM-dd";

	/*
	 * This method returns all the bookings within the system
	 * 
	 */
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
	 * This method retrieves a specific booking provided a user and a booking id
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
	public static List<List<Integer>> getIndividualSeatsAvailabilityForLocationDateRange(String location, String startD, String endD) throws Exception{
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

				//Re-format the location string
				String loc=location.substring(0,1).toUpperCase()+location.substring(1);
				//Retrieve a list of all the seats for the given location
				List<Integer> allSeatsLocation=getSeatsLocation(loc);

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
							Statement stmt = connectionDB.createStatement();
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
						}
					}
					else{
						availableSeats.add(new ArrayList<Integer>());
					}
					//Add a day to the current date
					cal.add(Calendar.DATE,1);
				}
				return availableSeats;
			}catch(ParseException ex){
				throw ex;
			}catch(SQLException  mysqlE){
				throw mysqlE;
			}catch(Exception e){
				throw e;
			}
		}
		return null;
	}

	/*
	 * This method returns a list of seats which are available for each day of the date range at the location provided
	 */
	public static List<Integer> getAvailableSeatsLocationDateRange(String location, String startD, String endD) throws Exception{
		//Re-format the location string
		String loc=location.substring(0,1).toUpperCase()+location.substring(1);
		//Get the seats available for each day of the date range
		List<List<Integer>> availableSeats=getIndividualSeatsAvailabilityForLocationDateRange(loc, startD, endD);
		//Get all the seats for the location
		List<Integer> allSeatsLocation=getSeatsLocation(loc);
		//The application will return only the common seats available for the given date range
		//therefore, we need to create an array which contains the common seats number
		List<Integer> commonSeats=new ArrayList<Integer>();
		for(int i=0; i<allSeatsLocation.size(); i++){
			boolean isInserted=true;
			//Check if the element is contained in any of the array
			for(int j=0; j<availableSeats.size(); j++){
				//If the element is not contained, change the value of the flag
				if(!isElementInArray(allSeatsLocation.get(i), availableSeats.get(j)))
					isInserted=false;
			}
			//If the value of the flag is not changed, it means the value is in all of the arrays and can be added to the common list
			if(isInserted){
				commonSeats.add(allSeatsLocation.get(i));
			}
		}
		return commonSeats;
	}

	/*
	 * This method returns a list of seats ID for the location provided
	 */
	private static List<Integer> getSeatsLocation (String location){
		//Re-format the location string
		String loc=location.substring(0,1).toUpperCase()+location.substring(1);
		//Retrieve a list of all the seats for the given location
		List<Integer> allSeatsLocation=new ArrayList<Integer>();
		//Instantiate a connection with the database
		Connection connectionDB1=BookingDAO.establishConnection();
		if(connectionDB1!=null){
			try{
				Statement stmt = connectionDB1.createStatement();
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
				return allSeatsLocation;
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}

	/*
	 * This methods returns all the information for each seat on the location provided
	 */
	public static ResponseEntity<?> getSeatsInfoLocation(String location){
		//Retrieve a list of all the seats for the given location
		List<Desk> allSeats=new ArrayList<Desk>();
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
						Desk temp=new Desk();
						temp.setDeskID(Integer.parseInt(rs.getString("deskID")));
						temp.setDeskBlock(Integer.parseInt(rs.getString("deskBlock")));
						temp.setDeskLetter(rs.getString("deskLetter"));
						temp.setLocation(rs.getString("location"));
						//Add the desk to the List
						allSeats.add(temp);
					}
					//Close the connection with the database
					rs.close();
				}
				else{
					return ResponseEntity.badRequest().body("Something went wrong with the DB request");
				}
				return ResponseEntity.ok(allSeats);
			}catch(SQLException  mysqlE){
				return ResponseEntity.badRequest().body("DB Error");
			}catch(Exception e){
				return ResponseEntity.badRequest().body("Application Error");
			}
		}
		return ResponseEntity.badRequest().body("DB Connection Error");
	}

	/*
	 * This method returns info for a selected seat ID
	 */
	public static ResponseEntity<?> retrieveDeskInfo(String id){
		int idConverted;
		Desk deskData=new Desk();
		try{
			idConverted=Integer.parseInt(id);
			if(idConverted>0){
				//Instantiate a connection with the database
				Connection connectionDB=BookingDAO.establishConnection();
				if(connectionDB!=null){
					Statement stmt = connectionDB.createStatement();
					//Select query
					String query = "SELECT * FROM `desk` WHERE deskID='"+idConverted+"'";
					//Execute the query
					boolean status = stmt.execute(query);
					if(status){
						//Extract the data from the resultset object
						ResultSet rs = stmt.getResultSet();
						//Loop around the resultset
						if(rs.next()){
							deskData.setDeskID(Integer.parseInt(rs.getString("deskID")));
							deskData.setDeskBlock(Integer.parseInt(rs.getString("deskBlock")));
							deskData.setDeskLetter(rs.getString("deskLetter"));
							deskData.setLocation(rs.getString("location"));
						}
						//Close the connection with the database
						rs.close();
					}
				}
				else{
					return ResponseEntity.badRequest().body("DB Connection Error");
				}
			}
			else{
				return ResponseEntity.badRequest().body("DeskID must be a value > 0");
			}
		}catch(SQLException  mysqlE){
			return ResponseEntity.badRequest().body("DB Error");
		}catch(Exception e){
			return ResponseEntity.badRequest().body("The given DeskID contains invalid characters");
		}
		return ResponseEntity.ok(deskData);
	}

	/*
	 * This Method verify if an element is contained within an array and returns TRUE or FALSE
	 */
	private static boolean isElementInArray(int value, List<Integer>range){
		//If the array is empty, it may be because the selected day is a SaturdaySunday or because all the seats are taken,
		// in any case, the application 
		if(range.isEmpty())
			return true;
		else
			return range.contains((Integer)value);
	}

	/*
	 * This method is used to calculate how many days the booking is for
	 */
	private static int getBookingLength(Date startDate, Date endDate){
		int counter=0;
		long diff=endDate.getTime()-startDate.getTime();
		counter=(int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return counter;
	}

	//Method to create booking for a specified user
	public static ResponseEntity<String> createBooking(int userID, int deskID, String inputStartDate, String inputEndDate) throws SQLException, ParseException{
		Validator val = Validate.validateCreateBooking(userID, deskID, inputStartDate, inputEndDate);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);

			//Insert into booking table, return auto generated ID
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

			//Insert booking dates records and commit
			insertBookingDates(conn, stmt, generatedUserID, inputStartDate, inputEndDate);
			conn.commit();
			return ResponseEntity.ok("Booking has been created");
		} catch(SQLException SQLe) {
			return ResponseEntity.badRequest().body("Please make sure the data you have entered is correct.");
		}

	}

	//Method to update booking for a specified user
	public static ResponseEntity<String> updateBooking(int bookingID, String newStartDate, String newEndDate) throws SQLException, ParseException{
		Validator val = Validate.validateUpdateBooking(bookingID, newStartDate, newEndDate);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookingdate WHERE bookingID_FK = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();

			//Insert booking dates records an commit
			insertBookingDates(conn, stmt, bookingID, newStartDate, newEndDate);
			conn.commit();
			return ResponseEntity.ok("Booking: " + bookingID + " has been updated.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}

	}

	//Method to delete a booking
	public static ResponseEntity<String> deleteBooking(int bookingID) {
		Validator val = Validate.validateDeleteBooking(bookingID);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			//			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("DELETE FROM booking WHERE bookingID = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();
			//			conn.commit();
			return ResponseEntity.ok("Booking " + bookingID + " has been deleted.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}
	}


	private static void insertBookingDates(Connection conn, PreparedStatement stmt, int generatedUserID, String inputStartDate, String inputEndDate) throws SQLException, ParseException{
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

	static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Use for Michaels DB
//			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195GRV:3306/hotdesk_db?" +"user=hotdesk&password=hotdesk");
			//Use for Reds DB
//			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
//			Use for Local DB
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/hotdesk?" +"user=root&password=");

			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");

		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}


}
