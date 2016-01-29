package com.ovejera.jim.okcbot;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

public class Main {
	
	public static String VERSION = "1.5.1";
	
	private static Scanner scanner = new Scanner(System.in);
	
	public Main(String username, String password){
		
		while(true){
			System.out.println("\n\nPLEASE CHOOSE AN ACTION:\n");
			System.out.println("[A] Harvest OKC Profiles");
			System.out.println("[B] Send OKC Opener");
			System.out.println("[C] Visit All Bitches");
			System.out.println("[X] Exit");
			
			System.out.println("Action: ");
			String action = scanner.next();
			
			if(action.equalsIgnoreCase("A")){
				System.out.println("Harvest OKC Profiles. Please fillin neccessary information:\n");
				System.out.println("Harvest count: ");
				int harvestCount = Integer.parseInt(scanner.next());
				System.out.println("Pro Account? (y/n): ");
				String pro = scanner.next();
				System.out.println("Harvest Timer (in min.): ");
				String minute = scanner.next();
				System.out.println("Please wait...");
				
				boolean bPro;
				if(pro.equalsIgnoreCase("y")){
					bPro = true;
				}else{
					bPro = false;
				}
				new JCupid(harvestCount, username, password, !bPro, Integer.parseInt(minute));
				System.out.println("jCupid harvesting is completed.");
			}else if(action.equalsIgnoreCase("B")){
				Scanner scanner = new Scanner(System.in);
				System.out.println("Send Your Opener. Please fillin neccessary information:\n");
				
				System.out.println("Remark (n/s/b): ");
				String fresh = scanner.next();
			
				if(fresh.equalsIgnoreCase("n")){
					fresh = "_new";
				}else if(fresh.equalsIgnoreCase("s")){
					fresh = "sent";
				}else{
					fresh = "both";
				}
				
				JCupidConnection jCupidConnection = new JCupidConnection();
				Connection con = jCupidConnection.connect();
				
				System.out.println("Sending Count | MAX(" + jCupidConnection.profiles(fresh).size() + "): ");
				
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				int count = scanner.nextInt();
				System.out.println("Please wait...");
				
				new JCupid(username, password, count);
				System.out.println("jCupid opener is completed.");
			}else if(action.equalsIgnoreCase("C")){
				
				JCupidConnection jCupidConnection = new JCupidConnection();
				Connection con = jCupidConnection.connect();
				
				List<String> usernames = jCupidConnection.profiles("both");
				
				System.out.println("Profiles to visit: " + usernames.size());
				
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				System.out.println("Type any key and press enter to continue...");
				scanner.next();
				
				new JCupid(username, password, usernames);
				System.out.println("jCupid visit is completed. \nTotal Visit: " + usernames.size());
			}else if(action.equalsIgnoreCase("X")){
				System.out.println("System exiting...");
				System.exit(0);
			}
			
//			break;
		}
	}
	
	public static void main(String args[]){
		
		
		
		System.out.println("Welcome to jCupid " + VERSION + "\n");
		System.out.println("OKCupid credentials needed. Please double check before continuing...\n");
		System.out.println("Username: ");
		String username = scanner.nextLine();
		System.out.println("Password: (NOTE: plain text)");
		String password = scanner.nextLine();
		
		disableLogger();
		
		new Main(username, password);
	}
	
	public static void disableLogger(){
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
	}

}
