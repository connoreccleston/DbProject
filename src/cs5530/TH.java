package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

public class TH {
	
	public static void updateListing(Scanner sc, Statement stmt, Session session) {
		ResultSet results = null;
		System.out.println("Here's a listing of your TH's, please enter id of one to edit it.");
		String query = "SELECT * FROM TH WHERE login='" + session.getLogin() + "'";
		try {
			results = stmt.executeQuery(query);
			while (results.next()) {
				String line = String.format("ID: %s\tNAME: %s",
						results.getString("hid"), results.getString("name"));
				System.out.println(line);
			}
			System.out.print("Please enter the id of the TH you'd like to setup a listing for: ");
			int hid = Integer.parseInt(sc.nextLine());			

			System.out.println("Press enter for fields you don't want to update.");
            
			System.out.print("Enter the category: ");
			String category = sc.nextLine();
	                
			System.out.print("Enter the address: ");
			String address = sc.nextLine();
	                
			System.out.print("Enter the phone number: ");
			String phone_num = sc.nextLine();
	                
			System.out.print("Enter the name: ");
			String name = sc.nextLine();
	                
			System.out.print("Enter the year house was built: ");
			String yearBuiltString = sc.nextLine();
	                
			System.out.print("Enter the URL for the TH: ");
			String url = sc.nextLine();
			
			query = "UPDATE TH SET";
			if (!category.equals("")) {
				query += " category='" + category + "',";
			}
			if (!address.equals("")) {
				query += " address='" + address + "',";

			}
			if (!phone_num.equals("")) {
				query += " phone_num='" + phone_num + "',";
			}
			if (!name.equals("")) {
				query += " name='" + name + "',";
			}
			if (!yearBuiltString.equals("")) {
				int yearBuilt = Integer.parseInt(yearBuiltString);
				query += " year_built=" + yearBuilt + ",";
			}
			if (!url.equals("")) {
				query += " URL='" + url + "',";
			}
			if (query.endsWith(",")) {
				query = query.substring(0, query.length() - 1);
			}
			query += " WHERE login='" + session.getLogin() + "' AND hid=" + hid;
			System.out.println(query);
			stmt.execute(query);
					
		} catch(Exception e) {
			System.err.println("Editing has been cancelled.");
		}
	}
	
	public static void newListing(Scanner sc, Statement stmt, Session session) {
		ResultSet results = null;
		System.out.println("Here's a listing of your TH's, please enter id of one to setup a listing, or type back to go back.");
		String query = "SELECT * FROM TH WHERE login='" + session.getLogin() + "'";
		try {
			results = stmt.executeQuery(query);
			while (results.next()) {
				String line = String.format("ID: %s\tNAME: %s",
						results.getString("hid"), results.getString("name"));
				System.out.println(line);
			}
			

			System.out.print("Please enter the id of the TH you'd like to setup a listing for: ");
			int hid = Integer.parseInt(sc.nextLine());
			
			System.out.println("Following periods are already listed for this TH:");
			query = "SELECT * FROM Period p JOIN Available a"
					+ " ON p.pid = a.pid WHERE a.hid='" + hid + "'";

			results = stmt.executeQuery(query);
			while (results.next()) {
				String resultString = String.format("Date Range %s To  %s, Price Per Night: %f",
						results.getString("from_date"), results.getString("to_date"), results.getDouble("price"));
				System.out.println(resultString);
			}
			System.out.println("Please choose dates not within the ranges above.");
			System.out.print("Please enter start date in yyyy-mm-dd format: "); 
			String start_date = sc.nextLine();
                        
			System.out.print("Please enter end date in yyyy-mm-dd format: ");
			String end_date = sc.nextLine();
                        
			System.out.print("Please enter price-per-night: ");
			double price = Double.parseDouble(sc.nextLine());
			
			//First check to see if the period falls within another period's time frame
			query = "SELECT *"
					+ " FROM Available a, Period p"
					+ " where a.pid = p.pid"
					+ " and a.hid=" + hid 
					+ " and p.from_date <= '" + start_date + "' and p.to_date >= '" + end_date + "'" ;
			
			results = stmt.executeQuery(query);
			if (results.next()) {
				System.out.println("That listing has already been made during that time frame.");
				return;
			}
			
			query = String.format("INSERT INTO Period (from_date, to_date) VALUES ('%s', '%s')",
					start_date, end_date);
			stmt.execute(query);
			
			//Get last insert id so we have the period id for the reserve table
			results = stmt.getGeneratedKeys();
			results.last();
			int pid = results.getInt(1);
			
			query = String.format("INSERT INTO Available VALUES (%d, %d, %f)",
					hid, pid, price);
			stmt.execute(query);
			System.out.println("TH has been listed!");
		} catch(Exception e) {
			System.err.println("Listing has been cancelled.");
		}
	}
	
	public static void reserveListing(int hid, Scanner sc, Statement stmt, Session session) {
		ResultSet results = null;
		System.out.println("Following periods are available for this TH:");
		String query = "SELECT * FROM Period p JOIN Available a"
				+ " ON p.pid = a.pid WHERE a.hid='" + hid + "'";
		try {
			results = stmt.executeQuery(query);
			while (results.next()) {
				String resultString = String.format("Date Range %s To  %s, Price Per Night: %f",
						results.getString("from_date"), results.getString("to_date"),
						results.getDouble("price"));
				System.out.println(resultString);
			}
		} catch (SQLException e1) {
			System.err.println("Unable to display listings.");
		}
		
		System.out.print("Please enter start date for reservation in yyyy-mm-dd format: "); 
		String startDate = sc.nextLine();
		System.out.print("Please enter end date for reservation in yyyy-mm-dd format: ");
		String endDate = sc.nextLine();
		
		//TODO: Display a listing of all dates this TH is available
		//TODO: Make sure a user can't enter an availability that's in between a date multiple times.
		query = "SELECT p.pid, from_date, to_date, a.price, datediff(p.to_date, p.from_date) * a.price as cost"
				+ " FROM Available a, Period p"
				+ " where a.pid = p.pid"
				+ " and a.hid=" + hid 
				+ " and p.from_date <= '" + startDate + "' and p.to_date >= '" + endDate + "'" ;
		try {
			int price = 0;
			int pid = 0;
			String originalStartDate = "";
			String originalEndDate = "";
			double pricePerNight = 0.0;
			results = stmt.executeQuery(query);
			while(results.next()) {
				String display = String.format("Price per night: %f for this date range, type \"yes\" to reserve or \"no\" to go back.",
						results.getFloat("price"));
				pid = results.getInt("pid");
				pricePerNight = results.getDouble("price");
				price = (int) results.getDouble("cost");
				System.out.println(display);
				originalStartDate = results.getString("from_date");
				originalEndDate = results.getString("to_date");
			}
			
			if (!originalStartDate.equals("")) {
				String reply = sc.nextLine();
				
				if (reply.equalsIgnoreCase("yes")) {
					DateFormat df = new SimpleDateFormat("yyyy-mm-dd");
					Calendar cal = Calendar.getInstance();

					//Update the availability period
					if (!startDate.equals(originalStartDate) && 
							!endDate.equals(originalEndDate)) { 
						//First update existing date range to end on the start date - 1
						cal.setTime(df.parse(startDate));
						cal.add(Calendar.DATE, -1);
						String query1 = "update Period set to_date='" + df.format(cal.getTime()) + "' where pid=" + pid;
						stmt.execute(query1);
						//Insert a new available range from end date + 1 to the original posting end date
						cal.setTime(df.parse(endDate));
						cal.add(Calendar.DATE, 1);
						String query2 = String.format("INSERT INTO Period (from_date, to_date) VALUES ('%s', '%s')",
							df.format(cal.getTime()), originalEndDate);
						stmt.execute(query2);
						//Get period id so we can create a new availability entry
						results = stmt.getGeneratedKeys();
						results.last();
						int newPid = results.getInt(1);
						query = String.format("INSERT INTO Available VALUES (%d, %d, %f)",
								hid, newPid, pricePerNight);
					} else if (!startDate.equals(originalStartDate)) {
						// Start date is greater than original start date, so set the original end date
						// to start date - 1
						cal.setTime(df.parse(startDate));
						cal.add(Calendar.DATE, -1);
						query = "update Period set to_date='" + df.format(cal.getTime()) + "' where pid=" + pid;
					} else if (!endDate.equals(originalEndDate)) {
						// End date is less than original end date, so set the original start date
						// to end date + 1
						cal.setTime(df.parse(endDate));
						cal.add(Calendar.DATE, 1);
						query = "update Period set from_date='" + df.format(cal.getTime()) + "' where pid=" + pid;
					} else {
						//Nothing changed so remove availability since it's been reserved.
						query = "delete from Available where pid=" + pid;
					}
					stmt.execute(query);

					if (!startDate.equals(originalStartDate) || 
							!endDate.equals(originalEndDate)) {
						query = String.format("INSERT INTO Period (from_date, to_date) VALUES ('%s', '%s')",
								startDate, endDate);
						//Get last insert id so we have the period id for the reserve table
						stmt.execute(query);
						results = stmt.getGeneratedKeys();
						results.last();
						pid = results.getInt(1);
					}
					
					query = String.format("INSERT INTO Reserve VALUES ('%s', '%d', '%d', '%d')",
					session.getLogin(), hid, pid, price);
					stmt.execute(query);
					System.out.println("TH has been reserved!");
					suggestListings(hid, stmt, session);
				}
				
			// No available listings found
			} else {
				System.err.println("No reservations made.");
			}

		} catch(Exception e) {
			System.err.println("No reservations made.");
		}
	}
	
	public static void new_th(Scanner sc, Statement stmt, Session session) { 
		System.out.println("Record details of a new TH.");
                
		System.out.print("Enter the category: ");
		String category = sc.nextLine();
                
		System.out.print("Enter the address: ");
		String address = sc.nextLine();
                
		System.out.print("Enter the phone number: ");
		String phone_num = sc.nextLine();
                
		System.out.print("Enter the name: ");
		String name = sc.nextLine();
                
		System.out.print("Enter the year house was built: ");
		int year_built = Integer.parseInt(sc.nextLine());
                
		System.out.print("Enter the URL for the TH: ");
		String url = sc.nextLine();
		
		System.out.print("Enter a comma separated list of keywords(ex: fancy,expensive): ");
		String[] keywords = sc.nextLine().split(",");

		String login = session.getLogin();
		
		String query = String.format("INSERT INTO TH (category, address, phone_num, name, year_built, url, login)"
				+ " VALUES ('%s', '%s', '%s', '%s', %d, '%s', '%s')",
				category, address, phone_num, name, year_built, url, login);
		
		try {
			ResultSet results;
			stmt.execute(query);
			results = stmt.getGeneratedKeys();
			results.last();
			int hid = results.getInt(1);
			//Loop over each keyword and insert into Keywords and HasKeywords
			for (String keyword : keywords) {
				String keywordQuery = String.format("INSERT INTO Keywords (word) VALUES ('%s')", keyword.trim());
				stmt.execute(keywordQuery);
				results = stmt.getGeneratedKeys();
				results.last();
				int wid = results.getInt(1);
				String hasKeywordQuery = String.format("INSERT INTO HasKeywords VALUES (%d, %d)", hid, wid);
				stmt.execute(hasKeywordQuery);
			}
			System.out.println("TH has been added!");
		} catch(Exception e) {
			System.err.println("No TH has been added.");
		}
	}
	
	public static void suggestListings(int hid, Statement stmt, Session session) {
		String query = String.format("SELECT t.hid, t.name FROM Visit v"
				+ " JOIN TH t on t.hid = v.hid"
				+ " WHERE v.hid=%d and v.login <> '%s'"
				+ " group by t.hid, t.name"
				+ " order by COUNT(*) desc",
				hid, session.getLogin());
		System.out.println("Other suggested TH's to reserve:");
		try {
			ResultSet results = stmt.executeQuery(query);
			while (results.next()) {
				System.out.println(String.format("ID: %d, Name: %s",
						results.getInt("hid"), results.getString("name")));
			}
		} catch (SQLException e) {
			System.err.println("Could not retrieve suggestions.");
		}
	}
}
