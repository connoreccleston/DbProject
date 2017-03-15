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

		while(!(res2 == "quit" || res2 == "exit"))
		{
			res2 = sc.nextLine();
			
			switch(res2)
			{
			case "help":
				System.out.println("Available commands: \"help\", \"reserve\", \"new\", \"stays\", \"favorite\", \"feedback\", \"usefullness\"");
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
}
