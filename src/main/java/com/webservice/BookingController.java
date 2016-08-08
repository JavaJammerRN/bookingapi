package com.webservice;


import java.util.List;
import java.sql.SQLException;
import java.text.ParseException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

	@RequestMapping("/booking")
	public String booking(@RequestParam(value="name", defaultValue="") String name) {
		return name;
	}

	@RequestMapping(value="/booking/user/{userID}", method=RequestMethod.GET)
	public List<Booking> userBookings(@PathVariable String userID){
		try{
			int userIdentification=Integer.parseInt(userID);
			return BookingDAO.getAllBookingsForSpecificUser(userIdentification);
		}catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/user/{userID}/ref/{bookingID}", method=RequestMethod.GET)
	public Booking userSpecificBooking(
			@PathVariable("userID") String userID, 
			@PathVariable("bookingID") String bookingID){
		try{
			int userIdentification=Integer.parseInt(userID);
			int bookingIdentification=Integer.parseInt(bookingID);
			return BookingDAO.getSingleBookingForSpecificUser(userIdentification, bookingIdentification);
		}
		catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/checkAvailability", method=RequestMethod.GET)
	public List<Integer> retrieveSeatsAvailableOnPeriodOfTime(
			@RequestParam(value="location") String location, 
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try{
			return BookingDAO.getAvailableSeatsLocationDateRange(location, startDate, endDate);
		}catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/checkSingleAvailability", method=RequestMethod.GET)
	public List<List<Integer>> retrieveSingleSeatsAvailableOnPeriodOfTime(
			@RequestParam(value="location") String location, 
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try{
			return BookingDAO.getIndividualSeatsAvailabilityForLocationDateRange(location, startDate, endDate);
		}catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/seatsLocation", method=RequestMethod.GET)
	public List<Desk> retrieveAllSeatsLocation(
			@RequestParam(value="location") String location){
		try{
			return BookingDAO.getSeatsInfoLocation(location);
		}catch(Exception e){
			return null;
		}
	}

	@RequestMapping(value="/booking/seatInfo", method=RequestMethod.GET)
	public Desk retrieveSeatData(
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


