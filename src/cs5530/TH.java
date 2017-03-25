package cs5530;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class TH {
	public static void new_listing(Scanner sc, Statement stmt, Session session) {
		ResultSet results = null;
		System.out.println("Here's a listing of your TH's, please enter id of one to setup a listing, or type back to go back");
		String query = "SELECT * FROM TH WHERE login='" + session.getLogin() + "'";
		try {
			results = stmt.executeQuery(query);
			while (results.next()) {
				String line = String.format("ID: %s NAME: %s",
						results.getString("hid"), results.getString("name"));
				System.out.println(line);
			}
			System.out.print("Please enter the id of the TH you'd like to setup a listing for:");
			int hid = sc.nextInt(); //Integer.parseInt(sc.nextLine());
			System.out.print("Please enter start date in yyyy-mm-dd format:"); 
			String start_date = sc.nextLine();
			System.out.print("Please enter end date in yyyy-mm-dd format:");
			String end_date = sc.nextLine();
			System.out.print("Please enter price-per-night:");
			double price = sc.nextDouble();
			
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
			System.out.println(e);
		}
	}
	
	public static void reserveListing(int hid, Scanner sc, Statement stmt, Session session) {
		ResultSet results = null;
		System.out.println("");
		System.out.print("Please enter start date for reservation in yyyy-mm-dd format:"); 
		sc.nextLine();
		String startDate = sc.nextLine();
		System.out.print("Please enter end date for reservation in yyyy-mm-dd format:");
		String endDate = sc.nextLine();
		
		//TODO: Display a listing of all dates this TH is available
		//TODO: Make sure a user can't enter a reservation that's in between a date multiple times.
		String query = "SELECT p.pid, from_date, to_date, a.price, datediff(p.to_date, p.from_date) * a.price as cost"
				+ " FROM Available a, Period p"
				+ " where a.pid = p.pid"
				+ " and a.hid=" + hid 
				+ " and p.from_date <= '" + startDate + "' and p.to_date >= '" + endDate + "'" ;
		System.out.println(query);
		try {
			int price = 0;
			int pid = 0;
			String originalStartDate = "";
			String originalEndDate = "";
			results = stmt.executeQuery(query);
			while(results.next()) {
				String display = String.format("Price per night: %f for this date range, type yes to reserve or no to go back",
						results.getFloat("price"));
				pid = results.getInt("pid");
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
					//cal.setTime(df.parse(startDate));
					//cal.add(Calendar.DATE, 1);
					//Update the availability period
					if (!startDate.equals(originalStartDate) && 
							!endDate.equals(originalEndDate)) { 
						//First update existing date range to end on the start date - 1
						cal.setTime(df.parse(startDate));
						cal.add(Calendar.DATE, -1);
						String query1 = "update Period set to_date=" + df.format(cal.getTime()) + " where pid=" + pid;
						stmt.execute(query1);
						//Insert a new available range from end date + 1 to the original posting end date
						cal.setTime(df.parse(endDate));
						cal.add(Calendar.DATE, 1);
						query = String.format("INSERT INTO Period (from_date, to_date) VALUES ('%s', '%s')",
							endDate, originalEndDate);
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
				}
			}

			
			System.out.println("TH has been listed!");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void new_th(Scanner sc, Statement stmt, Session session) { // Is this only supporting new? How can we allow modification of old?
		System.out.println("Record details of a new TH or update info of an existing owned one.");
		System.out.print("Enter the category: ");
		String category = sc.nextLine();
		System.out.print("Enter the address: ");
		String address = sc.nextLine();
		System.out.print("Enter the phone number: ");
		String phone_num = sc.nextLine();
		System.out.print("Enter the name: ");
		String name = sc.nextLine();
		System.out.print("Enter the year house was built: ");
		int year_built = sc.nextInt(); //Integer.parseInt(sc.nextLine());
		System.out.print("Enter the URL for the TH:");
		String url = sc.nextLine();
		String login = session.getLogin();
		
		String query = String.format("INSERT INTO TH (category, address, phone_num, name, year_built, url, login)"
				+ " VALUES ('%s', '%s', '%s', '%s', %d, '%s', '%s')",
				category, address, phone_num, name, year_built, url, login);
		
		try {
			stmt.execute(query);
			System.out.println("TH has been added!");
		} catch(Exception e) {
			System.out.println(e);
		}
	}
	
	public static void browseListings() {
		String query = "Select * from TH t, Available a, Period p where "
				+ " t.hid = a.hid and a.pid = p.pid";
		
	}
}
