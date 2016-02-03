import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	private static Connection con;
	private static Statement st;
	private static ResultSet rs;
	private static String hostname = "sorrycounter.xyz"; //Enter hostname for database here as hostname
	private int port = 3306;
	private String dbUsername = "sorrycou_memo"; //Enter username for hostname here
	private String dbPassword = "PASSWORDHERE"; //Enter password for hostname here. Unless no pass then leave blank
	private String dbName = "sorrycou_todo"; //Intended name of the database in the DB to be created
	private String username;
	private static String password;
	private static String usernameDB;
	//NOTE: Only working for mysql currently
	
	public DBConnect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/"+dbName+"?autoReconnect=true&useSSL=false",dbUsername,dbPassword);
			st = con.createStatement();
			System.out.println("Connected to: "+hostname);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		this.username = Login.username;
		password = Login.username;
		usernameDB = this.username;
		setTable();
	}
	
	public void setTable(){
		try{
			String query = ("CREATE Table " + username
					+ "(password varchar(20) NOT NULL, "
					+ "creationDate DATE NOT NULL, "
					+ "content varchar(200) NOT NULL, "
					+ "date DATE NOT NULL, "
					+ "category varchar(20) NOT NULL, "
					+ "PRIMARY KEY (date));");
			st.executeUpdate(query);
			System.out.println("creating table for \""+username+"\" in "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("already exists"))
				System.out.println("Did not create table for \""+username+"\".  Table already exists.");
			else ex.printStackTrace();
		}
	}
	
	public static int getDates(MemoCalendar calendar){
		int year = format(calendar.year);
		int month = format(calendar.month) + 1;
		try{
			String query = ("SELECT DAY(date) FROM "+usernameDB+" WHERE content<>'' AND MONTH(date) = "+month+"  AND YEAR(date) = "+year);
			rs = st.executeQuery(query);
			System.out.println("Getting days with memos from this month");
			int days = rs.getInt("day(date)");
			System.out.println(days);
			System.out.println(rs);
			return days;
		}catch(Exception ex){
			ex.printStackTrace();
			System.out.println("Could not retrieve data from database");
		}
		return 0;
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
			return "";
		}
		return "";
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
			String query = "INSERT INTO "+usernameDB+" (password, creationDate, content, date, category) VALUES ('"+password+"', '"+
					currentDate+"', '"+
					str+"', '"+date+"', "+"'normal')";
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
