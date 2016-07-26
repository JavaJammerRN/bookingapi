package com.webservice;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
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
		SimpleDateFormat dateFormatter=new SimpleDateFormat("yyyy-MM-dd");
		try{
			Date startDateConverted=dateFormatter.parse("2016-07-21");
			Date endDateConverted=dateFormatter.parse("2016-07-25");
			//Now that the strings are converted, let's find the numbers of days between them
			int daysBetweenDates=BookingDAO.getBookingLength(startDateConverted, endDateConverted);
			//For each day, verify which seats are available
			System.out.println(daysBetweenDates);
		}
		catch(ParseException ex){
		}
	}
}