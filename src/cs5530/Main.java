package cs5530;

import java.util.Scanner;

public class Main
{
	public static void main(String[] args)
	{
		Scanner sc = new Scanner(System.in);
		String res1 = "";
		String res2 = "";
		boolean do1 = true;
		
		System.out.println("Welcome to Uotel. Please type \"register\" if you are a new user or \"login\" if you are a returning user.");
		
		while(do1)
		{
			res1 = sc.nextLine();
			
			if(res1 == "login")
			{
				// do login routine
				do1 = false;
			}
			else if(res1 == "register")
			{
				// do register routine
				do1 = false;
			}
			else
			{
				System.err.println("Invalid input.");
				do1 = true;
			}
		}
		
		System.out.println("Welcome [NAME]! Please type a command or \"help\" for a list of commands.");

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
}
