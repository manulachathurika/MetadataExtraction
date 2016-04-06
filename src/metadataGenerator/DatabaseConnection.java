package metadataGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

	final static String JDBC_DRIVER = "oracle.jdbc.driver.OracleDriver";
	final static String DB_URL = "jdbc:oracle:thin:@//scan.sguat1:1526/sgaoprd";
	
	final static String USER = "IDXDTA";
	final static String PASS = "idxdta1406";
	
	public static Connection getConnection() {
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your Oracle JDBC Driver?");
			e.printStackTrace();
			return null;
		}

		//System.out.println("Oracle JDBC Driver Registered!");

		Connection connection = null;
		try {
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return null;
		}

		if (connection != null) {
			//System.out.println("You made it, take control your database now!");
			return connection;
		} else {
			System.out.println("Failed to make connection!");
			return null;
		}
	}
}
