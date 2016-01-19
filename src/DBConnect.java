import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnect {
	private static Connection con;
	private static Statement st;
	private static ResultSet rs;
	private static String hostname = "db4free.net"; //Enter hostname for database here as hostname
	private int port = 3306;
	private String username = "YOURUSERNAME"; //Enter username for hostname here
	private String password = "YOURPASSWORD"; //Enter password for hostname here. Unless no pass then leave blank
	private String databaseName = "todo"; //Intended name of the table in the DB to be created
	//NOTE: Only working for mysql currently
	
	public DBConnect(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://"+hostname+":"+port+"/todo?autoReconnect=true&useSSL=false",username,password);
			st = con.createStatement();
			System.out.println("Connected to: "+hostname);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void setTable(){
		try{
			String query = ("CREATE Table " +databaseName
					+ ".todo"
					+ "(creationDate DATE NOT NULL, "
					+ "content varchar(200) NOT NULL, "
					+ "date DATE NOT NULL, "
					+ "category varchar(20) NOT NULL, "
					+ "PRIMARY KEY (date));");
			st.executeUpdate(query);
			System.out.println("creating table \"todo\" in "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("already exists"))
				System.out.println("Did not create table \"todo\". Table already exists.");
			else ex.printStackTrace();
		}
	}
	
	public static String getContentData(MemoCalendar calendar){
		int year = format(calendar.year);
		int month = format(calendar.month) + 1;
		int day = format(calendar.day);
		String date = year+"-"+month+"-"+day;
		try{
			String query = ("SELECT content FROM todo WHERE date = '"+date+"'");
			rs = st.executeQuery(query);
			System.out.println("Gathering data from: "+hostname);
			while(rs.next()){
				String content = rs.getString("content");
				return content;
			}
		}catch(Exception ex){
			System.out.println("Could not retrive data from database");
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
			String query = "INSERT INTO todo (creationDate, content, date, category) VALUES ('"+
					currentDate+"', '"+
					str+"', '"+date+"', "+"'normal')";
			st.executeUpdate(query);
			System.out.println("Wrote data to: "+hostname);
		}catch(Exception ex){
			if(ex.toString().contains("Duplicate entry")){
				System.out.println("Could not create, updating current");
				try {
					String query = "UPDATE todo SET creationDate = '"+currentDate+"', content = '"+str+"' WHERE date = '"+date+"'";
					st.executeUpdate(query);
				} catch (SQLException e) {
					System.out.println("Could not update table todo");
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
