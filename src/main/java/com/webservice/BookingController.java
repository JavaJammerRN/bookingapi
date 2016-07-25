package com.webservice;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

	@RequestMapping("/booking")
	public String booking(@RequestParam(value="name", defaultValue="") String name) {
		return name;
	}

	//Methods implemented by Michael to add to the actual Webservice on Red's Laptop

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
	public Booking userSpecificBooking(@PathVariable("userID") String userID, @PathVariable("bookingID") String bookingID){
		try{
			int userIdentification=Integer.parseInt(userID);
			int bookingIdentification=Integer.parseInt(bookingID);
			return BookingDAO.getSingleBookingForSpecificUser(userIdentification, bookingIdentification);
		}
		catch(Exception e){
			return null;
		}
	}

}
