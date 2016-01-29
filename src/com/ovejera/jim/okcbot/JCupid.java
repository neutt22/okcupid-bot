package com.ovejera.jim.okcbot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import javax.swing.Timer;

import com.gargoylesoftware.htmlunit.AjaxController;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.WebWindowEvent;
import com.gargoylesoftware.htmlunit.WebWindowListener;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;
import com.gargoylesoftware.htmlunit.html.HtmlTextArea;
import com.gargoylesoftware.htmlunit.util.NameValuePair;


public class JCupid {
	
	// -Xmx256m -Xmx1024m
	// aurelius_ok
	// 180096555a
	
	public static void main(String args[]){
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF); 
		System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
		new JCupid(1, "neutt22", "88888888j", false, 1000);
	}
	
	private WebClient web;
	private List<String> usernames, blacklists;
	
	private int harvestCount, fetchCount = 0;
	private int count = 0;
	private int minute = 0;
	private String username, password = "";
	private String lastOpener = "";
	private boolean pro = true;
	boolean fetch = true;
	
	private Timer fetchTimer;
	
	// PROFILE FETCHER
	public JCupid(int harvestCount, String username, String password, boolean pro, int minute){
		this.harvestCount = harvestCount;
		web = new WebClient(BrowserVersion.CHROME);
		usernames = new ArrayList<String>();
		this.username = username;
		this.password = password;
		this.pro = pro;
		this.minute = minute * 60000;
		
		fetchTimer  = new Timer(this.minute, new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				System.out.println("TIME OUT. Exiting harvest...");
				fetch = false;
				fetchTimer.stop();
			}
		});
		
		fetchTimer.start();
		
		setup();
		login();
		profileFetcher();
		close();
	}
	
	// PROFILE SENDER
	public JCupid(String username, String password, int count){
		web = new WebClient(BrowserVersion.CHROME);
		usernames = new ArrayList<String>();
		this.username = username;
		this.password = password;
		this.count = count;
		setup();
		login();
		profileSender();
		close();
	}
		
	// PROFILE VISITOR
	public JCupid(String username, String password, List<String> usernames){
		web = new WebClient(BrowserVersion.CHROME);
		this.usernames = usernames;
		this.username = username;
		this.password = password;
		setup();
		login();
		profileVisitor();
		close();
	}
	
	public void profileSender(){
		try{
			JCupidConnection jCupidConnection = new JCupidConnection();
			Connection con = jCupidConnection.connect();
			
			List<String> _usernames = jCupidConnection.profiles("_new"); // TRUE if you want fresh harvest (not touched yet)
			System.out.println("Available fresh harvest: " + _usernames.size());
			
			List<String> openers = jCupidConnection.openers();
			int size = openers.size();
			
			int c = 0;

			for(String username : _usernames){
				
				if(!jCupidConnection.isSent(username)){
					
					String opnr = openers.get(new Random().nextInt(size));
					
					while(true){
						if(opnr.equals(lastOpener)){
							opnr = openers.get(new Random().nextInt(size));
						}else{
							lastOpener = opnr;
							break;
						}
					}

					String link = "http://m.okcupid.com/messages?compose=1&r1=" + username;
					System.out.println("[" + (c+1) + "] Sending opener to: " + link);
					
					URL profile = new URL(link);
					
					WebRequest profileRequest = new WebRequest(profile, HttpMethod.GET);
					
					HtmlPage profilePage = web.getPage(profileRequest);
					
					HtmlTextArea textArea = (HtmlTextArea) profilePage.getElementById("body");
					textArea.setText(opnr);
					HtmlInput button = (HtmlInput) profilePage.getElementById("send");
					button.click();
					
					web.close();
					
					c += 1;
				}
				
				if(c == count) break;
			}
			
			con.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void profileFetcher(){
		
		JCupidConnection jCupidConnection = new JCupidConnection();
		Connection con = jCupidConnection.connect();
		
		blacklists = jCupidConnection.blacklist();

		while(fetch){
			
			List<String> usrs = null;
			
			if(pro){
				System.out.println("Using NON PRO method...");
				usrs = fetchMatchesPro();
			}else{
				System.out.println("Using PRO method...");
				usrs = fetchMatches();
			}
			
			for(String username : usrs){
				usernames.add(username);
			}
			
			System.out.println("Profiles grabbed: " + usernames.size());
		}
		
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println(usernames.size() + " profiles collected. Writing to database...");
		
		fetchTimer.stop();
		
		write(usernames);
	}
	
	public void profileVisitor(){
		try{
			JCupidConnection jCupidConnection = new JCupidConnection();
			Connection con = jCupidConnection.connect();

			int c = 0;
			
			for(String username : usernames){
		
				String link = "http://m.okcupid.com/profile/" + username;
				System.out.println("[" + (c+1) + "] Visiting: " + link);
				
				URL profile = new URL(link);
				
				WebRequest profileRequest = new WebRequest(profile, HttpMethod.GET);
				
				web.getPage(profileRequest);
				
				Thread.sleep(2000);
				
				web.close();
				
				c += 1;
			}
			
			con.close();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void setup(){
		try{
			web.setAjaxController(new AjaxController(){
				private static final long serialVersionUID = 1L;

				@Override
			    public boolean processSynchron(HtmlPage page, WebRequest request, boolean async)
			    {
			        return true;
			    }
			});
			web.addWebWindowListener( new WebWindowListener() {
			    public void webWindowContentChanged(WebWindowEvent event) {
//			        System.out.println("2nd Page : " +event.getNewPage().getWebResponse().getContentAsString());
			        
			    }

				@Override
				public void webWindowClosed(WebWindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void webWindowOpened(WebWindowEvent arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			web.getOptions().setJavaScriptEnabled(false);
//			web.setJavaScriptEnabled(true); // must be TRUE - FB doesn't work w/o JS
			web.getOptions().setThrowExceptionOnFailingStatusCode(false);
			web.getOptions().setThrowExceptionOnScriptError(false);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void login(){
		try{
			
			URL loginUrl = new URL("https://www.okcupid.com/login");
			
			WebRequest loginRequest = new WebRequest(loginUrl, HttpMethod.POST);
			loginRequest.setRequestParameters(new ArrayList<NameValuePair>());
			loginRequest.getRequestParameters().add(new NameValuePair("username", username));
			loginRequest.getRequestParameters().add(new NameValuePair("password", password));
			
			web.getPage(loginRequest);
			
			System.out.println("Logged in successfully.");
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	// NON PRO ACCOUNT FETCH METHOD
	private List<String> fetchMatches(){
		
		List<String> _usernames = new ArrayList<String>();
		
		try{
			
			URL matchesUrl = new URL("https://www.okcupid.com/match");
			WebRequest matchesRequest = new WebRequest(matchesUrl, HttpMethod.GET);
			
			HtmlPage matchesPage = web.getPage(matchesRequest);
			
			String source = matchesPage.getDocumentElement().asXml();
			
			String[] lines = source.split("\n");
			
//			System.out.println("JSON found at line: " + lines.length);
			
			// Matches tab HTML source
			for(String line : lines){
				
				if(line.contains("stoplight")){
//					System.out.println("JSON: " + line.trim());
					String[] jsons = line.split(" ");
					for(int m = 0; m < jsons.length; m++){
						if(jsons[m].contains("username")){
							String usr = "" + jsons[m + 2].replace("\"", "").replace(",", "");
							
							boolean duplicate = false;
							
							for(String username : usernames){
//								System.out.println("Global: " + usernames.size() + " | " + username + " vs " + usr);
								if(username.equals(usr)){
									System.out.println("Duplicate detected: " + username);
									duplicate = true;
								}
							}
							
							if(duplicate == false){
								usr = usr.replace("}", "").replace("]", "");
								System.out.println("[" + (fetchCount + 1) + "] requesting profile of " + usr);
								URL profile = new URL("https://www.okcupid.com/profile/" + usr);
								WebRequest profileRequest = new WebRequest(profile, HttpMethod.GET);
								HtmlPage profilePage = web.getPage(profileRequest);
								HtmlTable table = (HtmlTable) profilePage.getElementsByTagName("table").get(0);

								String gender = table.asText().trim();
								boolean add = true;
								
								web.closeAllWindows();
								
								for(String keyword : blacklists){
									if(gender.contains(keyword)){
										System.out.println("Blacklist detected: " + keyword);
										add = false;
										break;
									}
								}
								
								if(add){
									if(++fetchCount == harvestCount){
										fetch = false;
										_usernames.add(usr);
										return _usernames;
									}else{
										_usernames.add(usr);
									}
								}
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return _usernames;
	}

	// PRO ACCOUNT FETCH METHOD
	private List<String> fetchMatchesPro(){
		
		List<String> _usernames = new ArrayList<String>();
		
		try{
			
			URL matchesUrl = new URL("https://www.okcupid.com/quickmatch");
			WebRequest matchesRequest = new WebRequest(matchesUrl, HttpMethod.GET);
			
			HtmlPage matchesPage = web.getPage(matchesRequest);
			
			String source = matchesPage.getDocumentElement().asXml();
			
			String[] lines = source.split("\n");
			
			// Matches tab HTML source
			for(String line : lines){

				if(line.contains("QuickMatchParams")){
//					System.out.println("quickmatch json: " + line);

					String[] jsons = line.split(" ");
					for(int m = 0; m < jsons.length; m++){
						if(jsons[m].contains("username")){
							String usr = "" + jsons[m + 2].replace("\"", "").replace(",", "");
						
							boolean duplicate = false;
						
							for(String username : usernames){

								if(username.equals(usr)){
									System.out.println("Duplicate detected: " + username);
									duplicate = true;
								}
							}
						
							if(duplicate == false){
								usr = usr.replace("}", "").replace("]", "");
								System.out.println("[" + (fetchCount + 1) + "] requesting profile of " + usr);
								URL profile = new URL("https://www.okcupid.com/profile/" + usr);
								WebRequest profileRequest = new WebRequest(profile, HttpMethod.GET);
								HtmlPage profilePage = web.getPage(profileRequest);
								HtmlTable table = (HtmlTable) profilePage.getElementsByTagName("table").get(0);

								String gender = table.asText().trim();
								boolean add = true;
								
								web.closeAllWindows();
								
								for(String keyword : blacklists){
									if(gender.contains(keyword)){
										System.out.println("Gay alert: " + keyword);
										add = false;
										break;
									}
								}
								
								if(add){
									if(++fetchCount == harvestCount){
										fetch = false;
										_usernames.add(usr);
										return _usernames;
									}else{
										_usernames.add(usr);
									}
								}
							}
						}
					}
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return _usernames;
	}
	
	private void write(List<String> usernames){
		
		JCupidConnection connection = new JCupidConnection("jdbc:mysql://localhost/jcupid?user=jcupidadmin&password=jcupidpassword");
		
		for(String username : usernames){
			connection.addBitch(username, "_new");
		}
		
		connection.close();
	}

	public void close(){
		web.close();
	}
}
