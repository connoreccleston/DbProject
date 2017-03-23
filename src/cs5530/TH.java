package cs5530;

import java.sql.ResultSet;
import java.sql.Statement;
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
			System.out.print("Please enter start date in yyyy-mm-dd format:"); // Use some sort of date class?
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
}
