package com.webservice;

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
     return "hi";
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/booking/{userID}")
    public List<Booking> userBookings(@PathVariable String userID){
    	try{
    		int userIdentification=Integer.parseInt(userID);
    		return BookingDAO.getAllBookingsForSpecificUser(userIdentification);
    	}catch(Exception e){
    		return null;
    	}
    }
    
}
