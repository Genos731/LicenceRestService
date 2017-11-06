package au.edu.unsw.soacourse.licence;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import au.edu.unsw.soacourse.database.DBConnection;


public enum LicenceDao {
	instance;

	Connection dbConnection;

	private LicenceDao() {
		try {
			dbConnection = DBConnection.getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve a licence belonging to "id"
	 * @param id ID of licence
	 * @return Licence object associated with id
	 * @throws SQLException If a database error occurs
	 */
	public Licence get(int id) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM licences "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);

		ResultSet result = statement.executeQuery();

		Licence l = null;
		if (result.next()) {
			l = new Licence();
			l.setAddress(result.getString("address"));
			l.setEmail(result.getString("email"));
			l.setLicenceClass(result.getString("class"));
			l.setId(result.getInt("id"));
			l.setName(result.getString("name"));
			l.setNumber(result.getString("number"));
			l.setExpiryDate(result.getDate("expiry_date"));
		}

		statement.close();
		return l;
	}

	/**
	 * Retrieve all licence resources whose expiry date is before the input
	 * @param expiryDate Last day for dates about to expire
	 * @return List of all licence resource whose expiray date is before the input
	 * @throws SQLException If a database error occurs
	 */
	public List<Licence> getExpiring(Calendar expiryDate) throws SQLException {		
		Date sqlExpiryDate = new Date(expiryDate.getTimeInMillis());

		String sqlQuery = "SELECT * "
				+ "FROM licences "
				+ "WHERE expiry_date < ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setDate(1, sqlExpiryDate);

		ResultSet result = statement.executeQuery();

		//System.out.println(statement);

		List<Licence> list = new ArrayList<Licence>();
		while (result.next()) {
			Licence l = new Licence();
			l = new Licence();
			l.setAddress(result.getString("address"));
			l.setEmail(result.getString("email"));
			l.setLicenceClass(result.getString("class"));
			l.setId(result.getInt("id"));
			l.setName(result.getString("name"));
			l.setNumber(result.getString("number"));
			l.setExpiryDate(result.getDate("expiry_date"));
			list.add(l);
		}

		statement.close();
		if (list.size() == 0)
			return null;

		return list;
	}

	public void updateAddress(int id, String address) throws SQLException {
		// Prepare sql
		String sqlQuery = "UPDATE licences "
				+ "SET address = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, address);
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	public void updateEmail(int id, String email) throws SQLException {
		// Prepare sql
		String sqlQuery = "UPDATE licences "
				+ "SET email = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, email);
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	public void updateExpiryDate(int id, Calendar date) throws SQLException {
		// Prepare sql
		String sqlQuery = "UPDATE licences "
				+ "SET expiry_date = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setDate(1, new Date(date.getTimeInMillis()));
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}
}
