package au.edu.unsw.soacourse.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";
	private static final String DB_CONNECTION = "jdbc:mysql://localhost:3306/licencedb?autoReconnect=true&useSSL=false";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "root";

	/**
	 * Get a connetion to the database
	 * @return Connection to the database
	 * @throws ClassNotFoundException ..
	 * @throws SQLException If a database error occurs
	 */
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		Connection dbConnection = null;

		Class.forName(DB_DRIVER);

		dbConnection = DriverManager.getConnection(DB_CONNECTION, DB_USER, DB_PASSWORD);
		return dbConnection;
	}
}
