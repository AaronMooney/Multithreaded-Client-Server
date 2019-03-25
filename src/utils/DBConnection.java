package utils;

import java.sql.*;
import java.util.Properties;

/**
 * DBConnection class which handles communication with the database
 * 
 * @author Aaron Mooney
 * Date Created: 08/11/2018
 * Last Modified: 11/11/2018 
 *
 */

public class DBConnection {

	/** The name of the MySQL account to use (or empty for anonymous) */
	private final String userName = "root";

	/** The password for the MySQL account (or empty for anonymous) */
	private final String password = "";

	/** The name of the computer running MySQL */
	private final String serverName = "localhost";

	/** The port of the MySQL server (default is 3306) */
	private final int portNumber = 3306;

	/**
	 * The name of the database we are testing with (this default is installed with
	 * MySQL)
	 */
	private final String dbName = "assign2";

	/** The name of the table we are testing with */
	private final String tableName = "myStudents";

	private PreparedStatement statement = null;

	/**
	 * Get a new database connection
	 * 
	 * @return nothing
	 */
	public Connection getConnection() {
		Connection conn = null;
		Properties connectionProps = new Properties();
		connectionProps.put("user", this.userName);
		connectionProps.put("password", this.password);

		try {
			conn = DriverManager.getConnection(
					"jdbc:mysql://" + this.serverName + ":" + this.portNumber + "/" + this.dbName + "?useSSL=false",
					connectionProps);
		} catch (SQLException e) {
			System.err.println("Exception occured in method 'getConnection' in class 'DBConnection'");
			e.printStackTrace();
		}

		return conn;
	}

	/**
	 * Fetch ResultSet from database for all records
	 * 
	 * @return ResultSet - The result of the query
	 */
	public ResultSet getResult() {
		String query = "SELECT * FROM " + tableName;
		ResultSet rs = null;
		try {
			statement = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			System.err.println("Exception occured in method 'getResult' in class 'DBConnection'");
			e.printStackTrace();
		}
		return rs;
	}

	/**
	 * Fetch ResultSet from database for a specific student ID
	 * @param studId - the students ID 
	 * @return ResultSet - The result of the query
	 */
	
	public ResultSet getItem(String studId) {
		String query = "SELECT * FROM myStudents WHERE STUD_ID = '" + studId + "'";
		ResultSet rs = null;
		try {
			statement = getConnection().prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE);
			rs = statement.executeQuery();
		} catch (SQLException e) {
			System.err.println("Exception occured in method 'getItem' in class 'DBConnection'");
			e.printStackTrace();
		}
		return rs;
	}
}
