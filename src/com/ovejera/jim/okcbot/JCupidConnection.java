package com.ovejera.jim.okcbot;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JCupidConnection {

	private String url = "jdbc:mysql://localhost:8888/jcupid?user=jcupidadmin&password=jcupidpassword";
	
	private Connection connection;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	
	// FOR PROFILE FETCHER
	public JCupidConnection(String url){
		this.url = url;
		connect();
	}
	
	// FOR PROFILE SENDER
	public JCupidConnection(){
		
	}
	
	public List<String> blacklist(){
		List<String> blacklists = new ArrayList<String>();
		
		try{
			preparedStatement = connection.prepareStatement("select * from jcupid.blacklist");
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){
				blacklists.add(resultSet.getString("keyword"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return blacklists;
	}
	
	public List<String> profiles(String q){
		List<String> usernames = new ArrayList<String>();
		
		try{
			String sql;
			if(q.equalsIgnoreCase("_new")){
				sql = "select username from jcupid.users where remark='_new'";
			}else if(q.equalsIgnoreCase("sent")){
				sql = "select username from jcupid.users where remark='sent'";
			}else{
				sql = "select username from jcupid.users";
			}
			preparedStatement = connection.prepareStatement(sql);
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()){
				usernames.add(resultSet.getString("username"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return usernames;
	}
	
	public List<String> openers(){
		List<String> openers = new ArrayList<String>();
		
		try{
			preparedStatement = connection.prepareStatement("select message from jcupid.opener");
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()){
				openers.add(resultSet.getString("message"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return openers;
	}
	
	public boolean isSent(String username){
		try{
			preparedStatement = connection.prepareStatement("select remark from jcupid.users where username=?");
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			
			resultSet.next();

			String remark = resultSet.getString("remark");
			
			if(remark.equalsIgnoreCase("sent")){
				return true;
			}else{
				updateBitch(username, "sent");
				return false;
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void updateBitch(String username, String update){
		try {
			preparedStatement = connection.prepareStatement("update jcupid.users set remark=? where username=?");
			preparedStatement.setString(1, update);
			preparedStatement.setString(2, username);
			preparedStatement.executeUpdate();
			
			System.out.println("User: " + username + "'s remark has been updated successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void addBitch(String username, String remark){
		try {
			if(isExist(username)){
				System.out.println("User already exists, skipping saving...");
				return;
			}
			preparedStatement = connection.prepareStatement("insert into jcupid.users (username, remark) values(?,?)");
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, remark);
			preparedStatement.executeUpdate();
			
			System.out.println("User: " + username + " has been saved successfully.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isExist(String username){
		try{
			preparedStatement = connection.prepareStatement("select username from jcupid.users where username=?");
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			
			resultSet.last();
			if(resultSet.getRow() == 0) return false;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return true;
	}
	
	public Connection connect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			connection = DriverManager.getConnection(url);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return connection;
	}
	
	public void close(){
		try {
			resultSet.close();
			preparedStatement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void main(String args[]){
		JCupidConnection connection = new JCupidConnection();
		Connection x = connection.connect();
		List<String> us = connection.profiles("both");
		
		for(String username : us){
			System.out.println(username);
		}
		
		try {
			x.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
