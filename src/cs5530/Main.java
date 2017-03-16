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
				New();
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
		Scanner sc = new Scanner(System.in);
		int m = 0;
		
		System.out.println("Displays the top m most trusted and most useful users.");
		
		sc.close();
	}

	private static void Statistics() 
	{
		Scanner sc = new Scanner(System.in);
		int m = 0;
		
		System.out.println("Displays the m most popular THs, m most expensive THs, and m most highly rated THs.");
		
		sc.close();
	}

	private static void Separation() 
	{
		Scanner sc = new Scanner(System.in);
		String user1 = "";
		String user2 = "";
		
		System.out.println("Determines the degree of separation between two users.");
		
		sc.close();
	}

	private static void Suggestions() 
	{
		// Displays a list of suggested THs based on current reservation.
		
	}

	private static void UsefulFeedback() 
	{
		Scanner sc = new Scanner(System.in);
		int n = 0;
		
		System.out.println("Displays the top n most useful feedbacks for a TH.");
		
		sc.close();
	}

	private static void Browse() 
	{
		Scanner sc = new Scanner(System.in);
		
		double priceLow = 0.0;
		double priceHi  = 0.0;
		String address  = "";
		String keywords = "";
		String category = "";
		String sortBy   = "";
		
		System.out.println("Displays THs that match the specified criteria.");
		
		sc.close();
	}

	private static void Trust() 
	{
		Scanner sc = new Scanner(System.in);
		String user = "";
		boolean trusted;
		
		System.out.println("Declare other users at trustworthy or not.");
		
		sc.close();
	}

	private static void Usefullness() 
	{
		Scanner sc = new Scanner(System.in);
		String record = "";
		int score = 0;
		
		System.out.println("Assess a feedback record as useful or not.");
		
		sc.close();
	}

	private static void Feedback() 
	{
		Scanner sc = new Scanner(System.in);
		
		String date = "";
		int score = 0;
		String text = "";
		
		System.out.println("Record feedback for a TH.");
		
		sc.close();
	}

	private static void Favorite() 
	{
		Scanner sc = new Scanner(System.in);
		String TH = "";
		
		System.out.println("Declare a TH as a favorite place to stay.");
		
		sc.close();
	}

	private static void Stays() 
	{
		Scanner sc = new Scanner(System.in);
		String TH = "";
		
		System.out.println("Record a stay at a TH.");
		
		sc.close();
	}

	private static void New() 
	{
		Scanner sc = new Scanner(System.in);
		
		// Lots of stuff.
		
		System.out.println("Record details of a new TH or update info of an existing owned one.");
		
		sc.close();
	}

	private static void Reserve() 
	{
		Scanner sc = new Scanner(System.in);
		
		// Lots of stuff.
		
		System.out.println("Record a reservation to stay at a TH.");
		
		// Happens at the end of a reservation, not its own command.
		Suggestions();
		
		sc.close();
	}
}
