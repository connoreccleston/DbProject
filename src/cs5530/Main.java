package cs5530;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class Main
{
	static Scanner sc = new Scanner(System.in);
	
	public static void main(String[] args)
	{
		Connector conn = null;
		Session session = null;
		
		try {
			conn = new Connector();
		} catch(Exception e) {
			System.out.println("Database inaccessible");
			System.exit(1);
		}
		
		String res1 = "";
		String res2 = "";
		boolean do1 = true;
		
		System.out.println("Welcome to Uotel. Please type \"register\" if you are a new user or \"login\" if you are a returning user.");
		
		while(do1)
		{
			res1 = sc.nextLine();
			
			if(res1.equals("login"))
			{
				session = login(conn.stmt);
				
				if (session != null) {
					do1 = false;
				} else {
					do1 = true;
					System.out.println("Invalid username/password. Please type login or register:");
				}
			}
			else if(res1.equals("register"))
			{
				registration(conn.stmt);
				do1 = false;
			}
			else
			{
				System.err.println("Invalid input.");
				do1 = true;
			}
		}
		
		System.out.println("Welcome " + session.getLogin() + "! Please type a command or \"help\" for a list of commands.");

		while(!(res2.equals("quit") || res2.equals("exit")))
		{
			res2 = sc.nextLine();
			
			switch(res2)
			{
			case "help":
				System.out.println("Available commands: help, reserve, new, stays, favorite, feedback, usefullness, trust, browse, useful feedback, separation, statistics, awards, quit.");
				break;
				
			case "reserve":
				Reserve();
				break;	
				
			case "new":
				New(conn.stmt, session);
				break;	
				
			case "stays":
				Stays();
				break;	
				
			case "favorite":
				Favorite();
				break;	
				
			case "feedback":
				Feedback();
				break;	
				
			case "usefullness":
				Usefullness();
				break;	
				
			case "trust":
				Trust();
				break;	
				
			case "browse":
				Browse();
				break;	
				
			case "useful feedback":
				UsefulFeedback();
				break;	
				
			case "separation":
				Separation();
				break;	
				
			case "statistics":
				Statistics();
				break;	
				
			case "awards":
				Awards();
				break;
				
			case "quit":
			case "exit":
				System.out.println("Goodbye.");
				try {
					conn.closeConnection();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
				
			default:
				System.err.println("Command not recognized.");
				break;
			}
		}
		sc.close();
	}
	
	public static void registration(Statement stmt) {
		System.out.print("Enter your login username: ");
		String login = sc.nextLine();
                
		System.out.print("Enter your name: ");
		String name = sc.nextLine();
                
		System.out.print("Enter your address: ");
		String address = sc.nextLine();
                
		System.out.print("Enter your password: ");
		String password = sc.nextLine();
                
		System.out.print("Enter your phone number: ");
		String phone_num = sc.nextLine();
		
		String query = String.format("INSERT INTO Users VALUES ('%s', '%s', '%s', '%s', '%s')", 
				login, name, address, password, phone_num);
		
		try {
			stmt.execute(query);
		} catch(Exception e) {
			System.out.println(e);
		}
		System.out.println("You have successfully registered\n");
	}
	
	public static Session login(Statement stmt) {
		ResultSet results = null;
		Session session = new Session();
		
		System.out.print("Enter your login username: ");
		String login = sc.nextLine();
                
		System.out.print("Enter your password: ");
		String password = sc.nextLine();
                
		String query = String.format("SELECT * FROM Users WHERE login='%s' AND password='%s';", login, password);
		try {
			results = stmt.executeQuery(query);
			if (results.next() && results != null) {
				System.out.println("You have been logged in.");
				session.setLogin(login);
				return session;
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		return null;
	}
	
	private static void Awards() 
	{
		System.out.println("This command displays a number of the most trusted and most useful users.");
                
                System.out.print("How many users do you want to see? ");
		int n = sc.nextInt();		
	}

	private static void Statistics() 
	{
                System.out.println("This command displays a number of the most popular, expensive, and highly rated housing options.");
                
                System.out.print("How many housings do you want to see? ");
		int n = sc.nextInt();	
    	}

	private static void Separation() 
	{
		System.out.println("This command determines the degree of separation between two users based on favorite housing.");
                
                System.out.print("Username 1: ");		
                String user1 = sc.nextLine();
                
                System.out.print("Username 2: ");
		String user2 = sc.nextLine();		
	}

	private static void Suggestions() 
	{
		// Displays a list of suggested THs based on current reservation.
		
	}

	private static void UsefulFeedback() 
	{
                System.out.println("This command displays a number of the most useful feedbacks for a housing.");
                
                System.out.print("How many feedbacks do you want to see? ");
		int n = sc.nextInt();                		
	}

	private static void Browse() 
	{		
		System.out.println("This command displays housing options that match specified criteria.");
                
                System.out.print("Lowest price: ");
		double priceLow = sc.nextDouble();
                
                System.out.print("Highest price: ");                
		double priceHigh = sc.nextDouble();
                
                System.out.print("City or State: ");
		String address = sc.nextLine();
                
                System.out.println("Comma separated list of keywords: ");
		String keywords = sc.nextLine();
                
                System.out.print("Category: ");
		String category = sc.nextLine();
                
                System.out.print("Sorting method (price, feedback, trusted feedback): ");
		String sortBy = sc.nextLine();
		
	}

	private static void Trust() 
	{
                boolean trusted;
                
		System.out.println("This command lets you declare other users at trustworthy or not.");
                
                System.out.print("Username: ");
		String user = sc.nextLine();
                
                System.out.print("Trust this user? (yes/no) ");
                if(sc.nextLine().equalsIgnoreCase("yes"))
                    trusted = true;
                else
                    trusted = false;
		
	}

	private static void Usefullness() 
	{
		String record = "";
		int score = 0;
		
		System.out.println("Assess a feedback record as useful or not.");
	}

	private static void Feedback() 
	{		
		String date = "";
		int score = 0;
		String text = "";
		
		System.out.println("Record feedback for a TH.");
	}

	private static void Favorite() 
	{
		String TH = "";
		
		System.out.println("Declare a TH as a favorite place to stay.");
	}

	private static void Stays() 
	{
		String TH = "";
		
		System.out.println("Record a stay at a TH.");
	}

	private static void New(Statement stmt, Session session) 
	{
		System.out.println("Please use commands \"new listing\" to create a listing "
				+ "or \"new th\" for a new temporary housing entry");
		String cmd = sc.nextLine();
		ResultSet results = null;
		
		switch (cmd) {
			case "new th":
				TH.new_th(sc, stmt, session);
				break;
			case "new listing":
				TH.new_listing(sc, stmt, session);
				break;
		}
		
	}

	private static void Reserve() 
	{		
		// Lots of vars.
		
		System.out.println("Record a reservation to stay at a TH.");
		
		// Happens at the end of a reservation, not its own command.
		Suggestions();
	}
}
