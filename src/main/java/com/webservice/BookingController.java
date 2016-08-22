package com.webservice;


import java.util.List;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin(origins = "*")
@RestController
public class BookingController {

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/booking/user/{userID}", method=RequestMethod.GET)
	public ResponseEntity<?> userBookings(@PathVariable String userID){
		try{
			int userIdentification=Integer.parseInt(userID);
			List<Booking> obj = BookingDAO.getAllBookingsForSpecificUser(userIdentification);
			return (obj!=null)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body("Invalid User ID");
		}catch(SQLException sqlE){
			return ResponseEntity.badRequest().body("DB Error");
		}catch(IndexOutOfBoundsException indexE){
			return ResponseEntity.badRequest().body("Internal Error (Index Error)");
		}catch(Exception e){
			return ResponseEntity.badRequest().body("General Exception");
		}
	}

	@RequestMapping(value="/booking/{userID}/ref/{bookingID}", method=RequestMethod.GET)
	public ResponseEntity<?> userSpecificBooking(
			@PathVariable("userID") String userID, 
			@PathVariable("bookingID") String bookingID){
		try {
			int userIdentification=Integer.parseInt(userID);
			int bookingIdentification=Integer.parseInt(bookingID);
			Booking obj = BookingDAO.getSingleBookingForSpecificUser(userIdentification, bookingIdentification);
			return (obj!=null)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body("Invalid Location");
			//Catch any exception (mysql, numconversion) threw by the method and output them into a bad request  
		}catch(SQLException sqlE){
			return ResponseEntity.badRequest().body("DB Error");
		}catch(IndexOutOfBoundsException indexE){
			return ResponseEntity.badRequest().body("Internal Error (Index Error)");
		}catch(DateTimeParseException dateParserE){
			return ResponseEntity.badRequest().body("Date Conversion Error");
		}catch(DateTimeException dateE){
			return ResponseEntity.badRequest().body("The provided date has generated an error");
		}catch(NumberFormatException numE){
			return ResponseEntity.badRequest().body("The booking ID or the User ID provided are incorrect");
		}catch(Exception e){
			return ResponseEntity.badRequest().body("General Exception");
		}
		
	}

	/*@RequestMapping(value="/booking/checkAvailability", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveSeatsAvailableOnPeriodOfTime(
			@RequestParam(value="location") String location, 
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try {
			Object obj = BookingDAO.getAvailableSeatsLocationDateRange(location, startDate, endDate);
			if(obj!=null){
				return ResponseEntity.ok((List<Integer>)obj);
			}
			else{
				return ResponseEntity.badRequest().body("Empty Object");
			}
			//Catch any exception (mysql, numconversion) threw by the method and output them into a bad request  
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.toString());
		}
	}*/

	@RequestMapping(value="/booking/checkSingleAvailability", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveSingleSeatsAvailableOnPeriodOfTime(
			@RequestParam(value="location") String location, 
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try {
			List<BookingTable> obj=BookingDAO.getIndividualSeatsAvailabilityForLocationDateRange(location, startDate, endDate);
			return (obj!=null && obj.size()>0)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body("Invalid Location");
			
			//Catch any exception (mysql, numconversion) threw by the method and output them into a bad request  
		}catch(ParseException ex){
			return ResponseEntity.badRequest().body("Error while finding the numbers of days between the given dates");
		}catch(SQLException  mysqlE){
			return ResponseEntity.badRequest().body(mysqlE.getMessage());
		}catch(Exception e){
			return ResponseEntity.badRequest().body(e.toString());
		}
	}
	
	@RequestMapping(value="/booking/bookingLength", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveBookingLength(
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try{
			int length=BookingDAO.getBookingLengthPublic(startDate, endDate);
			//Verify the integer value retrieved
			return (length>0)? ResponseEntity.ok(length) : ResponseEntity.badRequest().body("Invalid Dates");
		}catch(Exception e){
			return ResponseEntity.badRequest().body("Application Error");
		}
	}

	@RequestMapping(value="/booking/seatsLocation", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveAllSeatsLocation(
			@RequestParam(value="location") String location){
		try{
			return BookingDAO.getSeatsInfoLocation(location);
		}catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/seatInfo", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveSeatData(
			@RequestParam(value="deskId") String deskId){
		try{
			return BookingDAO.retrieveDeskInfo(deskId);
		}catch(Exception e){
			return null;
		}
	}


	//POST - create new booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/booking", method=RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> createBooking(@RequestParam int userID, @RequestParam int deskID, @RequestParam String startDate, @RequestParam String endDate) throws SQLException, ParseException  {
		return BookingDAO.createBooking(userID, deskID, startDate, endDate);
	}

	//PUT - update existing booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/booking/{bookingID}", method=RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateBooking(@PathVariable int bookingID, @RequestParam String newStartDate, @RequestParam String newEndDate) throws SQLException, ParseException  {
		return BookingDAO.updateBooking(bookingID, newStartDate, newEndDate);
	}

	//DELETE - delete existing booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/booking/{bookingID}", method=RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> deleteBooking(@PathVariable int bookingID)   {
		return BookingDAO.deleteBooking(bookingID);
	}


}


