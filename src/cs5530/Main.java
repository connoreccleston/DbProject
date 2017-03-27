package cs5530;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.util.Scanner;
import java.util.*;
//import java.util.ArrayList;

public class Main
{
        // Static Scanner since all methods need one.
	static Scanner sc = new Scanner(System.in);
	
        // Main method
	public static void main(String[] args)
	{
		Connector connector = null;
                Session session = null;
		
                // Retries connection until established.
                boolean connected = false;
                while(!connected)
                {
                    try 
                    {
                            connector = new Connector();
                            connected = true;
                    } 
                    catch(Exception e) 
                    {
                            System.err.println(e);
                            System.err.println("Database inaccessible.\n");
                            //System.exit(1);
                    }
                }		
		
		System.out.println("Welcome to Uotel. Please type \"register\" if you are a new user or \"login\" if you are a returning user.");
		
                // Loops until user has successfully registered or logged in.
		String input;
		boolean loopLogin = true;
		while(loopLogin)
		{
			input = sc.nextLine();			
			if(input.equalsIgnoreCase("login"))
			{
				session = Login(connector.stmt);
				
				if (session != null) 
                                {
					loopLogin = false;
				} 
                                else 
                                {
					loopLogin = true;
					System.err.println("Invalid username/password. Please type login or register:");
				}
			} 
                        else if(input.equalsIgnoreCase("register"))
			{
				session = Registration(connector.stmt);
				loopLogin = false;
			} 
                        else
			{
				System.err.println("Invalid input.");
				loopLogin = true;
			}
		}
		
		System.out.println("Welcome " + session.getLogin() + "!");

                // Loops while the user has not chosen to quit.
		input = "";                
                while(!input.equals("quit"))
		{
                        System.out.println("Please type a command or \"help\" for a list of commands.");
                        
                        // Switch on the user's command and call the appropriate method.
			input = sc.nextLine().toLowerCase();			
			switch(input)
			{
                                case "help":
                                        System.out.println("Available commands: help, reserve, new, stays, favorite, feedback, usefulness, trust, browse, useful feedback, separation, statistics, awards, quit.");
                                        break;

                                case "reserve":
                                        Reserve(connector.stmt, session);
                                        break;	

                                case "new":
                                        New(connector.stmt, session);
                                        break;	

                                case "stays":
                                        Stays(connector.stmt, session);
                                        break;	

                                case "favorite":
                                        Favorite(connector.stmt, session);
                                        break;	

                                case "feedback":
                                        Feedback(connector.stmt, session);
                                        break;	

                                case "usefulness":
                                        Usefulness(connector.stmt, session);
                                        break;	

                                case "trust":
                                        Trust(connector.stmt, session);
                                        break;	

                                case "browse":
                                        Browse(connector.stmt, session);
                                        break;	

                                case "useful feedback":
                                        UsefulFeedback(connector.stmt, session);
                                        break;	

                                case "separation":
                                        Separation(connector.stmt, session);
                                        break;	

                                case "statistics":
                                        Statistics(connector.stmt, session);
                                        break;	

                                case "awards":
                                        Awards(connector.stmt, session);
                                        break;

                                case "quit":
                                        System.out.println("Goodbye.");
                                        try {
                                                connector.closeConnection();
                                        } catch (Exception e) {
                                                System.err.println(e);
                                        }
                                        break;

                                default:
                                        System.err.println("Command not recognized.");
                                        break;
			}
		}
		sc.close();
	}
	
        // Allows a new user to register in the database.
	public static Session Registration(Statement stmt) // Done  
        {
                ResultSet results = null;
                Session session = new Session();
            
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
		
		String query = String.format("INSERT INTO Users VALUES ('%s', '%s', '%s', '%s', '%s')", login, name, address, password, phone_num);
		
		try 
                {
			stmt.execute(query);
		} 
                catch(Exception e) 
                {
			System.err.println(e);
		}
                
		System.out.println("You have successfully registered.\n");
                
                query = String.format("SELECT * FROM Users WHERE login='%s' AND password='%s';", login, password);
                
		try 
                {
			results = stmt.executeQuery(query);
			if (results.next() && results != null)
                        {
				System.out.println("You have been logged in.");
				session.setLogin(login);
				return session;
			}
		} 
                catch (Exception e)
                {
			System.err.println(e);
		}

		return null;
	}
	
        // Allows a returning user to login.
	public static Session Login(Statement stmt) // Done
        {
		ResultSet results = null;
		Session session = new Session();
		
		System.out.print("Enter your login username: ");
		String login = sc.nextLine();
                
		System.out.print("Enter your password: ");
		String password = sc.nextLine();
                
		String query = String.format("SELECT * FROM Users WHERE login='%s' AND password='%s';", login, password);
                
		try 
                {
			results = stmt.executeQuery(query);
			if (results.next() && results != null) 
                        {
				System.out.println("You have been logged in.");
				session.setLogin(login);
				return session;
			}
		} 
                catch (Exception e) 
                {
			System.err.println(e);
		}

		return null;
	}
	
        // Displays a number of the most rusted and most useful users.
	private static void Awards(Statement stmt, Session session) // Done
	{
                // Most trusted:
                // SELECT login2, COUNT(*) AS rating FROM Trust WHERE isTrusted = 1 GROUP BY login2 ORDER BY rating DESC
                
                // Most useful by average:
                // SELECT login, AVG(1.0 * rating) AS avgRate FROM (SELECT fid, rating FROM Rates)temp NATURAL JOIN Feedback GROUP BY login ORDER BY avgRate DESC
                
		System.out.println("This command displays a number of the most trusted and most useful users.");
                
                System.out.print("How many users do you want to see? ");
		int n = Integer.parseInt(sc.nextLine());	
                
                ResultSet results = null;
		//String query = "SELECT login2, COUNT(*) AS rating FROM Trust WHERE isTrusted = 1 GROUP BY login2 ORDER BY rating DESC";
                String query = "SELECT login2, SUM(isTrusted) AS rating FROM Trust GROUP BY login2 ORDER BY rating DESC"; 
                
                try
                {
                        results = stmt.executeQuery(query);
                        int i = 0;

                        while (i < n && results.next())
                        {
                                System.out.println("User " + results.getString("login2") + " has a trust rating of " + results.getString("rating") + ".");
                                i++;
                        }

                        System.out.println();
                        query = "SELECT login, AVG(1.0 * rating) AS avgRate FROM (SELECT fid, rating FROM Rates)temp NATURAL JOIN Feedback GROUP BY login ORDER BY avgRate DESC";
                        results = stmt.executeQuery(query);
                        i = 0;

                        while (i < n && results.next())
                        {
                                System.out.println("User " + results.getString("login") + " has an average usefulness of " + results.getString("avgRate") + ".");  
                                i++;
                        }                    
                } 
                catch(Exception e) 
                {
                        System.err.println(e);
                }
	}

        // Displays a number of the most popular, expensive, and highly rated THs.
	private static void Statistics(Statement stmt, Session session) // Done
	{
                // Most popular by number of visits:
                // String query = "SELECT hid, COUNT(*) AS number FROM Visit GROUP BY hid ORDER BY number DESC";
                
                // Most expensive by visit price:
                // String query = "SELECT hid, AVG(cost) AS avgCost FROM Visit GROUP BY hid ORDER BY avgCost DESC";

                // Most highly rated:
                // String query = "SELECT hid, AVG(1.0 * score) AS avgScore FROM (SELECT TH.hid, score FROM TH join Feedback WHERE TH.hid = Feedback.hid)temp GROUP BY hid ORDER BY avgScore DESC";
                
                System.out.println("This command displays a number of the most popular, expensive, and highly rated housing options.");
                
                System.out.print("How many housings do you want to see? ");
		int n = Integer.parseInt(sc.nextLine());
                
                ResultSet results = null;
                String query = "SELECT hid, COUNT(*) AS number FROM Visit GROUP BY hid ORDER BY number DESC";  
                
                try 
                {
                        results = stmt.executeQuery(query);
                        int i = 0;

                        while (i < n && results.next())
                        {
                                System.out.println("Housing ID " + results.getString("hid") + " has been visited " + results.getString("number") + " times.");
                                i++;
                        }

                        System.out.println();
                        query = "SELECT hid, AVG(cost) AS avgCost FROM Visit GROUP BY hid ORDER BY avgCost DESC";
                        results = stmt.executeQuery(query);
                        i = 0;

                        while (i < n && results.next())
                        {
                                System.out.println("Housing ID " + results.getString("hid") + " has an average cost of $" + results.getString("avgCost") + " per person."); 
                                i++;
                        }

                        System.out.println();
                        query = "SELECT hid, AVG(1.0 * score) AS avgScore FROM (SELECT TH.hid, score FROM TH join Feedback WHERE TH.hid = Feedback.hid)temp GROUP BY hid ORDER BY avgScore DESC";
                        results = stmt.executeQuery(query);
                        i = 0;

                        while (i < n && results.next())
                        {
                                System.out.println("Housing ID " + results.getString("hid") + " has an average score of " + results.getString("avgScore") + ".");                     
                                i++;
                        }                    
                } 
                catch(Exception e) 
                {
                        System.err.println(e);
                }                
    	}

        // Determines whether 2 users are separated by 2 degrees of separation.
	private static void Separation(Statement stmt, Session session) // Should work, but having issues due to server. Test again.
	{            
                // https://utah.instructure.com/courses/429026/discussion_topics/1959506
                // http://stackoverflow.com/questions/9468363/degrees-of-separation-query
                
                // Finds 1 degree:
                // select f1.login as login1, f2.login as login2 from Favorites f1 join Favorites f2 where f1.hid = f2.hid and f1.login != f2.login
            
		System.out.println("This command determines whether 2 users are separated by 2 degrees of separation based on favorite housing.");
                
                System.out.print("Username 1: ");		
                String user1 = sc.nextLine();
                
                System.out.print("Username 2: ");
		String user2 = sc.nextLine();
                
                String query1 = "CREATE OR REPLACE VIEW CommonFavs AS SELECT f1.login AS login1, f2.login AS login2 FROM Favorites f1 JOIN Favorites f2 WHERE f1.hid = f2.hid AND f1.login != f2.login";
                String query2 = String.format("SELECT COUNT(*) AS DoesExist FROM CommonFavs f1 JOIN CommonFavs f2 ON f2.login1 = f1.login2 WHERE f1.login1 = '%s' AND f2.login2 = '%s'", user1, user2);
                ResultSet results = null;
                int DoesExist = 0;
                
                // Still testing this to see which statement breaks it.
                try 
                {
                        stmt.execute(query1);
                        results = stmt.executeQuery(query2);
                        results.next();               
                        DoesExist = Integer.parseInt(results.getString("DoesExist"));
                        
                        if(DoesExist != 0)
                            System.out.println("Specified users are separated by 2 degrees of separation.");
                        else
                            System.out.println("Specified users are not separated by 2 degrees of separation.");                    
                } 
                catch (Exception e) 
                {
                        System.err.println("4 " + e);
                }             
	}

        // Displays the most useful feedbacks given to a TH.
	private static void UsefulFeedback(Statement stmt, Session session) // Done
	{
                // select * from Feedback natural join (select fid, avg(1.0 * rating) as avgRate from Rates group by fid)temp where hid = 6 order by avgRate desc
            
                System.out.println("This command displays a number of the most useful feedbacks for a housing.");
                
                System.out.print("Please enter the id of the housing you'd like to review: ");
                int hid = Integer.parseInt(sc.nextLine());
                
                System.out.print("How many feedbacks do you want to see? ");
		int n = Integer.parseInt(sc.nextLine());     

                ResultSet results = null;
                String query = String.format("SELECT * FROM Feedback NATURAL JOIN (SELECT fid, AVG(1.0 * rating) AS avgRate FROM Rates GROUP BY fid)temp WHERE hid = %d ORDER BY avgRate DESC", hid);  
                
                try 
                {
                        results = stmt.executeQuery(query);
                        int i = 0;

                        while (i < n && results.next()) 
                        {
                                System.out.println("\nUser " + results.getString("login") + " gave this housing a score of " + results.getString("score") + " on " + results.getString("fbdate") + ".");
                                System.out.println("Comment: \"" + results.getString("text") + "\"");
                                System.out.println("Other users gave this feedback (ID: " + results.getString("fid") + ") an average score of " + results.getString("avgRate") + ".");
                                i++;
                        }                    
                } 
                catch (Exception e) 
                {
                        System.err.println(e);
                }
	}

	private static void Browse(Statement stmt, Session session) 
	{
		double priceLow = 0.0;
		double priceHigh = 0.0;
		String query = "SELECT t.hid, t.name, t.category, t.address, t.phone_num, t.year_built, "
				+ "AVG(a.price) as average_price, t.URL, AVG(f.score) as average_score" + " FROM TH t"
				+ " left join Available a on t.hid = a.hid" + " left join HasKeywords hk on t.hid = hk.hid"
				+ " left join Feedback f on t.hid = f.hid where";
		String conditions = "";
		System.out.println("This command displays housing options that match specified criteria.");
		System.out.println("Press enter if you'd like to ignore that filter.");
		System.out.print("Lowest price: ");
		String priceLowString = sc.nextLine();
		System.out.print("Highest price: ");
		String priceHighString = sc.nextLine();
		System.out.print("City or State: ");
		String address = sc.nextLine();
		System.out.print("Comma separated list of keywords: ");
		String keywords = sc.nextLine();
		System.out.print("Category: ");
		String category = sc.nextLine();
		System.out.println("These following options must be filled in:");
		System.out.print("Which conjuction to use? (must use: and, or): ");
		String conjuction = sc.nextLine().trim();
		System.out.print("Sorting method (price, feedback, trusted feedback): ");
		String sortBy = sc.nextLine();

		if (!priceLowString.equals("") && !priceHighString.equals("")) {
			priceLow = Double.parseDouble(priceLowString);
			priceHigh = Double.parseDouble(priceHighString);
			conditions += String.format(" %s a.price >= %f and a.price <= %f", conjuction, priceLow, priceHigh);
		}

		if (!address.equals("")) {
			conditions += String.format(" %s t.address like '%%s%'", conjuction, address);
		}

		if (!category.equals("")) {
			conditions += String.format(" %s t.category = '%s'", conjuction, category);
		}

		if (!keywords.equals("")) {
			String keywordsQuoted = "";
			for (String keyword : keywords.split(",")) {
				keywordsQuoted += "'" + keyword.trim() + "',";
			}
			// Strip last comma
			keywordsQuoted = keywordsQuoted.substring(0, keywordsQuoted.length() - 1);
			conditions += String.format(" %s hk.wid in (SELECT wid FROM Keywords WHERE" + " word in (%s))", conjuction,
					keywordsQuoted);
		}
		
		if (sortBy.equals("trusted feedback")) {
			conditions += String.format(" %s f.login in (SELECT login2 FROM Trust WHERE"
					+ " login1='%s' AND isTrusted=1)", conjuction, session.getLogin());
		}

		// Remove the leading conjuction
		if (conditions.startsWith(" and"))
			conditions = conditions.substring(4);
		else if (conditions.startsWith(" or"))
			conditions = conditions.substring(3);
		// Add conditions and group by to avoid duplicate THs being listed
		query += conditions;
		query += " group by t.hid, t.name, t.category, t.address, t.phone_num, t.year_built, t.URL";

		if (sortBy.equals("price")) {
			query += " order by AVG(price)";
		} else if (sortBy.equals("feedback") || sortBy.equals("trusted feedback")) {
			query += " order by AVG(f.score)";
		} 

		try {
			ResultSet results = stmt.executeQuery(query);
			while (results.next()) {
				String resultString = String.format(
						"ID: %d, Name: %s, Category: %s, Address: %s, Phone Number: %s"
								+ " Year Built: %d, URL: %s, Average Price: %f, Average Score: %f",
						results.getInt("hid"), results.getString("name"), results.getString("category"),
						results.getString("address"), results.getString("phone_num"), results.getInt("year_built"),
						results.getString("URL"), results.getDouble("average_price"),
						results.getDouble("average_score"));
				System.out.println(resultString);
			}
		} catch (Exception e) {
			System.out.println("Unable to browse.");
		}
	}

        // Lets users declare whether or no they trust other users.
	private static void Trust(Statement stmt, Session session) // Done
	{
                int trusted;
                
		System.out.println("This command lets you declare other users as trustworthy or not.");
                
                System.out.println("Usernames: (Comma separated list.)");
		String usernames = sc.nextLine();
                
                if(usernames.contains(session.getLogin()))
                {
                        System.err.println("You cannot trust yourself.");
                        return;
                }
                                
                String[] userArr = usernames.split(",");
                               
                System.out.print("Trust these users? (yes/no) ");
                
                // 1 and -1 rather than 1 and 0 so the results can be added to get user's total trustworthyness.
                if(sc.nextLine().equalsIgnoreCase("yes"))
                        trusted = 1;
                else
                        trusted = -1;
		
                for(String user : userArr)
                {
                        String query = String.format("INSERT INTO Trust VALUES ('%s', '%s', %d)", session.getLogin(), user.trim(), trusted);

                        try 
                        {
                                stmt.execute(query);
                                System.out.println("Trust recorded.");
                        } 
                        catch(Exception e) 
                        {
                                System.err.println(e);
                        }
                }
	}

        // Lets users give a usefulness rating to a feedback.
	private static void Usefulness(Statement stmt, Session session) // Done
	{
                int score = 0;
            
		System.out.println("This command lets you assess a feedback record as useful or not.");
                
                System.out.print("Which feedback ID would you like to rate? ");
		int fid = Integer.parseInt(sc.nextLine());
                
                ResultSet results = null;
                String query = String.format("SELECT login FROM Feedback WHERE fid = %d", fid);  
                
                try 
                {
                        results = stmt.executeQuery(query);
                        results.next();               
                        if(results.getString("login").equals(session.getLogin()))
                        {
                                System.err.println("You cannot rate your own feedbacks.");
                                return;
                        }                
                } 
                catch (Exception e) 
                {
                        System.err.println(e);
                }
                
                System.out.println("How useful is it? (useless/useful/very useful)");
                String rating = sc.nextLine();
                
                if(rating.equalsIgnoreCase("useless"))
                        score = 0;
                else if(rating.equalsIgnoreCase("useful"))
                        score = 1;
                else if(rating.equalsIgnoreCase("very useful"))
                        score = 2;

                query = String.format("INSERT INTO Rates VALUES ('%s', %d, %d)", session.getLogin(), fid, score);
                
                try 
                {
                        stmt.execute(query);
                        System.out.println("Rating recorded.");
                        
                } 
                catch(Exception e) 
                {
                        System.err.println(e);
                }
	}

        // Lets users give feedback to a TH.
	private static void Feedback(Statement stmt, Session session) // Done
	{	
		System.out.println("This command lets you record feedback for a housing.");
                
                ResultSet results = null;
		System.out.println("Here are the housings where you had a reservation.");
		String query = String.format("SELECT * FROM Visit WHERE login = '%s'", session.getLogin());
                
                try 
                {
                        ArrayList<Integer> IDs = new ArrayList();
                    
                        results = stmt.executeQuery(query);
                        System.out.print("Housing IDs: ");                        
                        while (results.next())
                        {
                                System.out.print(results.getString("hid") + " ");
                                IDs.add(Integer.parseInt(results.getString("hid")));
                        }

                        System.out.print("\nPlease enter the id of the housing you'd like to review: ");
                        int hid = Integer.parseInt(sc.nextLine());
                        
                        if(!IDs.contains(hid))
                        {
                            System.err.println("You cannot favorite a housing you have not stayed at.");
                            return;                            
                        }
                        
                        String date = "CURDATE()"; // Tells SQL to get the current date.

                        System.out.print("Rate the housing on a scale of 0-10: ");
                        int score = Integer.parseInt(sc.nextLine());

                        System.out.println("Comment:");
                        String text = sc.nextLine();

                        query = String.format("INSERT INTO Feedback (text, fbdate, hid, login, score) VALUES ('%s', %s, %d, '%s', %d)", text, date, hid, session.getLogin(), score);

                        stmt.execute(query);
                        System.out.println("Feedback recorded.");
                        
                } 
                catch(Exception e) 
                {
                        System.err.println(e);
                }
	}

        // Lets users favorite a TH.
	private static void Favorite(Statement stmt, Session session) // Done
	{
		System.out.println("This command lets you declare a housing as a favorite place to stay.");
                                
                ResultSet results = null;
		System.out.println("Here are the housings you have visited.");
		String query = String.format("SELECT * FROM Visit WHERE login = '%s'", session.getLogin());

                try 
                {
                        ArrayList<Integer> IDs = new ArrayList();
                        results = stmt.executeQuery(query);
                        System.out.print("Housing IDs: ");
                        while (results.next())
                        {
                                System.out.print(results.getString("hid") + " ");
                                IDs.add(Integer.parseInt(results.getString("hid")));
                        }

                        System.out.print("\nPlease enter the id of the housing you'd like to favorite: ");
                        int hid = Integer.parseInt(sc.nextLine());
                        
                        if(!IDs.contains(hid))
                        {
                            System.err.println("You cannot favorite a housing you have not stayed at.");
                            return;                            
                        }

                        String date = "CURDATE()"; // Tells SQL to get the current date.

                        query = String.format("INSERT INTO Favorites VALUES (%d, '%s', %s)", hid, session.getLogin(), date);

                        stmt.execute(query);
                        System.out.println("Favorite recorded.");
                } 
                catch(Exception e) 
                {
                        System.err.println(e);
                }                
        }

        // Lets users record a stay at a TH.
	private static void Stays(Statement stmt, Session session) // Done
	{
		System.out.println("This command lets you record a stay at a housing.");
                
                ResultSet results = null;
		System.out.println("Here are the housings where you had a reservation.");
		String query = String.format("SELECT * FROM Reserve NATURAL JOIN Period WHERE login = '%s'", session.getLogin());
                
		try 
                {
                        Map<Integer, ArrayList<Integer>> valid = new HashMap();
                    
			results = stmt.executeQuery(query);
                        
			while (results.next()) 
                        {
				String line = String.format("Housing ID %s from %s to %s (period ID: %s).", results.getString("hid"), results.getString("from_date"), results.getString("to_date"), results.getString("pid"));
                                
                                ArrayList periods = valid.get(Integer.parseInt(results.getString("hid")));
                                if (periods == null) 
                                {
                                        periods = new ArrayList();
                                        valid.put(Integer.parseInt(results.getString("hid")), periods);
                                }
                                periods.add(Integer.parseInt(results.getString("pid")));
                                
				System.out.println(line);
			}
                        
                        System.out.print("Which housing ID did you stay at? ");
                        int hid = Integer.parseInt(sc.nextLine());
                        
                        if(!valid.containsKey(hid))
                        {
                            System.err.println("You cannot record a stay at a housing you didn't have a reservation for.");
                            return;
                        }
                        
                        System.out.print("For which period ID? ");
                        int pid = Integer.parseInt(sc.nextLine());
                        
                        ArrayList periods = valid.get(hid);
                        if(!periods.contains(pid))
                        {
                            System.err.println("You did not stay at that housing during that period.");
                            return;
                        }
                        
                        System.out.print("How much did it cost per person? $");
                        double cost = Double.parseDouble(sc.nextLine());
                        
                        query = String.format("INSERT INTO Visit (login, hid, pid, cost) VALUES ('%s', %d, %d, %f)", session.getLogin(), hid, pid, cost);
                        stmt.execute(query);
                        System.out.println("Stay recorded.");                        
		} 
                catch(Exception e) 
                {
			System.out.println(e);
		}                
	}

        // Switch for a new TH in the database or a new listing for a TH.
	private static void New(Statement stmt, Session session) // Done
	{
		System.out.println("Please use commands \"new listing\" to create a listing, \"new th\" for a new temporary housing entry, "
				+ "\"edit th\" to edit a TH.");
		String cmd = sc.nextLine();
		//ResultSet results = null;
		
		switch (cmd) 
                {
			case "new th":
				TH.new_th(sc, stmt, session);
				break;
			case "new listing":
				TH.newListing(sc, stmt, session);
				break;
			case "edit th":
				TH.updateListing(sc, stmt, session);
		}		
	}
        
    // Lets users reserve a TH.
	private static void Reserve(Statement stmt, Session session) // Done
	{				
		System.out.println("Record a reservation using TH id, if not type 0 to go back: (You can find ID's by using our \"browse\" command on the main screen.)");
                
		int hid = Integer.parseInt(sc.nextLine());		
		if (hid != 0) {
			TH.reserveListing(hid, sc, stmt, session);
		}
	}
}