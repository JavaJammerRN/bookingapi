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
		//SpringApplication.run(App.class, args);
		/*Calendar cal=Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		dateFormat.format(date);
		cal.setTime(date);
		//This specific line adds the 20 years to the current date
		cal.add(Calendar.YEAR, 20);
		Date d=cal.getTime();
		java.sql.Date startD=new java.sql.Date(d.getTime());
		System.out.print(startD);*/

		List<Booking> bookingList=new ArrayList<Booking>();
		//Create a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` LEFT JOIN desk on booking.deskID_FK=desk.deskID LEFT JOIN bookingdate on booking.bookingID=bookingdate.bookingID_FK WHERE userID_FK=68 AND bookingID=1";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
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
						Booking bookingTemp=new Booking(1,68,deskId,date,date,deskBlockN,deskLetter,location);
						bookingList.add(bookingTemp);
					}
					//Close the connection with the database
					rs.close();

					//The Booking objects inside the bookingList are all the same apart from the date of the booking.
					//The following code will find the first and last date for the given booking id, add them to the Booking object
					//and return it to the user

					//Create and instantiate the booking object that will be returned with all the information
					Booking userBooking=new Booking();
					//Create temporary start date;
					//Add 20 years to the current date
					Calendar cal=Calendar.getInstance();
					DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
					Date date = new Date();
					dateFormat.format(date);
					cal.setTime(date);
					//This specific line adds the 20 years to the current date
					cal.add(Calendar.YEAR, 20);
					java.sql.Date startDTemp=new java.sql.Date(cal.getTime().getTime());

					//java.sql.Date endD=null;
					//Loop that finds the start date of the booking
					for(int i=0; i<bookingList.size(); i++){
						//System.out.println(bookingList.get(i).toString());
						//Copy the content of the first Booking object of the list inside the booking object that has to
						//be returned
						if(i==0){
							userBooking.cloneBooking(bookingList.get(i));
						}
						if(bookingList.get(i).getStartDate().before(startDTemp)){
							startDTemp=bookingList.get(i).getStartDate();
						}
					}
					System.out.println(userBooking.toString());
					java.sql.Date endDTemp=null;
					//Loop that finds the end date of the booking
					for(int i=0; i<bookingList.size(); i++){
						if(bookingList.get(i).getEndDate().after(startDTemp)){
							endDTemp=bookingList.get(i).getEndDate();
						}
					}
					
					//Now that the start and end date of the booking have been found,
					//pass the data to the booking object and return it to the user
					userBooking.setStartDate(startDTemp.toString());
					userBooking.setEndDate(endDTemp.toString());
					System.out.println(userBooking.toString());
				}
			}catch(Exception e){}
		}
	}
}