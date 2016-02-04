import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	private static Connection con;
	private static Statement st;
	private static ResultSet rs;
	private static String hostname = "www.db4free.net"; //Enter hostname for database here as hostname
	private int port = 3306;
	private String dbUsername = "dwatring"; //Enter username for hostname here
	private String dbPassword = "PASSWORD"; //Enter password for hostname here. Unless no pass then leave blank
	private String dbName = "todo"; //Intended name of the database in the DB to be created
	private static String username;
	private static String password;
	private static String usernameDB;
	
	public DBConnect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+dbName+"?autoReconnect=true&useSSL=false",dbUsername,dbPassword);
			st = con.createStatement();
			System.out.println("Connected to: "+hostname);
		}catch(Exception ex){
		}
		username = Login.username;
		password = Login.password;
		usernameDB = username;
		setTable();
	}
	
	public void setTable(){
		try{
			String query = ("CREATE Table " + username
					+ " (password varchar(20) NOT NULL, "
					+ "creationDate DATE NOT NULL, "
					+ "content varchar(255) NOT NULL, "
					+ "date DATE NOT NULL, "
					+ "PRIMARY KEY (date));");
			st.executeUpdate(query);
			System.out.println("creating table for \""+username+"\" in "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("already exists"))
				System.out.println("Did not create table for \""+username+"\".  Table already exists.");
		}
		try{
			String query = ("CREATE Table users"
					+ " (username varchar(20) NOT NULL, "
					+ "password varchar(20) NOT NULL, "
					+ "PRIMARY KEY (username));");
			st.executeUpdate(query);
			System.out.println("creating table for users in "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("already exists"))
				System.out.println("Did not create users table.  Table already exists.");
		}
		try{
			String query = "INSERT INTO users (username, password) VALUES ('"+username+"', '"+password+"')";
			st.executeUpdate(query);
			System.out.println("Added "+username+" to users.");
		}catch(Exception ex){
			if(ex.toString().contains("Duplicate entry")){
				System.out.println(username+" already registered.");
			}
		}
	}
	
	public static int[] getDates(MemoCalendar calendar){
		int year = format(calendar.year);
		int month = format(calendar.month) + 1;
		try{
			String query = ("SELECT DAY(date) FROM "+usernameDB+" WHERE content<>'' AND MONTH(date) = "+month+"  AND YEAR(date) = "+year);
			rs = st.executeQuery(query);
			System.out.println("Getting days with memos from this month");
			int[] days = new int[31];
			int i=0;
			while(rs.next()){
				days[i] = rs.getInt("DAY(date)");
				i++;
			}
			return days;
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Could not retrieve data from database");
		}
		return null;
	}
	
	public static String getContentData(MemoCalendar calendar){
		getDates(calendar);
		int year = format(calendar.year);
		int month = format(calendar.month) + 1;
		int day = format(calendar.day);
		String date = year+"-"+month+"-"+day;
		try{
			String query = ("SELECT content FROM "+usernameDB+" WHERE date = '"+date+"'");
			rs = st.executeQuery(query);
			System.out.println("Gathering data from: "+hostname);
			while(rs.next()){
				String content = rs.getString("content");
				return content;
			}
		}catch(Exception ex){
			System.out.println("Could not retrieve data from database");
		}
		return "";
	}

	public static boolean isPasswordCorrect(String password){
		try{
			String query = ("SELECT password FROM users WHERE username = '"+username+"'");
			rs = st.executeQuery(query);
			System.out.println("Authenticating password for "+username+".");
			while(rs.next()){
				String output = rs.getString("password");
				if(output.equals(password))
					return true;
				else return false;
			}
		}catch(Exception ex){
			System.out.println("Could not check password for "+username+".");
		}
		return false;
	}
	
	public static void setContentData(MemoCalendar calendar, String str){
		int year = format(calendar.year);
		int month = format(calendar.month) + 1;
		int day = format(calendar.day);
		int todayYear = format(calendar.today.get(1));
		int todayMonth = format(calendar.today.get(2) + 1);
		int todayDay = format(calendar.today.get(5));
		String currentDate = todayYear+"-"+todayMonth+"-"+todayDay;
		String date = year+"-"+month+"-"+day;
		try{
			String query = "INSERT INTO "+usernameDB+" (password, creationDate, content, date) VALUES ('"+password+"', '"+
					currentDate+"', '"+
					str+"', '"+date+"')";
			st.executeUpdate(query);
			System.out.println("Wrote data to: "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("Duplicate entry")){
				System.out.println("Could not create table for this date, updating current");
				try {
					String query = "UPDATE "+usernameDB+" SET creationDate = '"+currentDate+"', content = '"+str+"' WHERE date = '"+date+"'";
					st.executeUpdate(query);
				} catch (SQLException e) {
					System.out.println("Could not update table for  \""+usernameDB+"\".");
				}
			}
		}
	}
	
	public static int format(int val){
		if(val < 10){
			String temp = "0" + val;
			return Integer.parseInt(temp);
		}
		else return val;
	}
}
