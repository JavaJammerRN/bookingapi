package com.webservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import com.mysql.jdbc.Connection;

public class UserDAO {

	public static User getUserDataByUsername(String userInfo){
		//Create a temporary user object to store the user information
		if(!userInfo.equals("")){
			//Establish a connection with the database
			Connection connectionDB=establishConnection();
			if(connectionDB!=null){
				try{
					Statement stmt = connectionDB.createStatement();
					//Adjust the query
					String query = "select * from user where username='"+userInfo+"'";
					//Execute the query
					boolean status = stmt.execute(query);
					if(status){
						//Extract the data from the resultset object
						ResultSet rs = stmt.getResultSet();
						User userTemp=new User(userInfo);
						while(rs.next()){
							userTemp.setUserID((Integer.parseInt(rs.getString("userID"))));
							userTemp.setForename(rs.getString("forename"));
							userTemp.setSurname(rs.getString("surname"));
							userTemp.setUsername(rs.getString("username"));
						}
						//Close the connection with the database
						rs.close();
						//Return all the user information
						return userTemp;
					}
				}
				catch(Exception e){
					return new User(e.toString());
				}
			}
		}
		return null;
	}

	private static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Use for Michaels DB
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195GRV:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
			//Use for Reds DB
			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}
}
