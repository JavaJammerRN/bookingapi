package com.webservice;

import java.sql.SQLException;
import java.text.ParseException;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BookingController {

    @RequestMapping(value = "/booking", method=RequestMethod.GET)
    public String getAllBookingsForUser(@RequestParam(value="name", defaultValue="") String name) {
     return "hi";
    }
    
    //POST - create new booking
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/booking", method=RequestMethod.POST)
    public @ResponseBody String createBooking(@RequestParam int userID, @RequestParam int deskID, @RequestParam String startDate, @RequestParam String endDate) throws SQLException, ParseException  {
    	return BookingDAO.createBooking(userID, deskID, startDate, endDate);
    }
    
    //PUT - update existing booking
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/booking/{bookingID}", method=RequestMethod.PUT)
    public String updateBooking(@PathVariable int bookingID, @RequestParam String newStartDate, @RequestParam String newEndDate) throws SQLException, ParseException  {
    	return BookingDAO.updateBooking(bookingID, newStartDate, newEndDate);
    }
    
    //DELETE - delete existing booking
    @CrossOrigin(origins = "*")
    @RequestMapping(value = "/booking/{bookingID}", method=RequestMethod.DELETE)
    public @ResponseBody String deleteBooking(@PathVariable int bookingID)   {
    	return BookingDAO.deleteBooking(bookingID);
    }
    
    
}


