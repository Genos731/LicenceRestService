package au.edu.unsw.soacourse.payment;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import au.edu.unsw.soacourse.database.DBConnection;

public enum PaymentDao {
	instance;

	Connection dbConnection;

	private PaymentDao() {
		try {
			dbConnection = DBConnection.getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieve a payment belonging to id
	 * @param id id of payment resource
	 * @return Payment resource associated with id
	 * @throws SQLException If a database error occurs
	 */
	public Payment get(int id) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM payments "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);

		ResultSet result = statement.executeQuery();

		Payment p = null;
		if (result.next()) {
			p = new Payment();
			p.setId(result.getInt("id"));
			p.setAmount(result.getDouble("amount"));
			p.setPaidDate(result.getDate("paid_date"));
		}

		return p;
	}

	/**
	 * Create a new payment if renewalsId exists
	 * And a payment does not already exists for this
	 * @param renewalsId Id of renewal notice that payment belongs to
	 * @param amount Amount to be paid\
	 * @return ID of payment resource
	 * @throws SQLException If a database error occurs
	 */
	public int create(int renewalsId, double amount) throws SQLException {		
		if (!isRenewal(renewalsId))
			throw new IllegalArgumentException("renewals_id does not exist");

		if (isPayment(renewalsId))
			throw new IllegalArgumentException("payment resource already exists for this renewal");

		// Create sql statement
		String sqlQuery = "INSERT INTO payments "
				+ "(amount) VALUES (?)";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
		statement.setDouble(1, amount);

		// Execute, throws error if failed
		statement.executeUpdate();

		// Retrieve row id
		ResultSet generatedKeys = statement.getGeneratedKeys();
		int paymentId = 0;
		if (generatedKeys.next())
			paymentId = generatedKeys.getInt(1);
		else
			throw new SQLException("Error retrieving payment id, unable to attach to Renewal resource");
		if (paymentId == 0)
			throw new SQLException("Error retrieving payment id, unable to attach to Renewal resource");

		// Put id into associated renewal
		insertRenewalPayment(renewalsId, paymentId);
		statement.close();

		return paymentId;
	}

	public void updateAmount(int id, double amount) throws SQLException {
		String sqlQuery = "UPDATE payments "
				+ "SET amount = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setDouble(1, amount);
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	public void updatePaidDate(int id, Calendar paidDate) throws SQLException {
		String sqlQuery = "UPDATE payments "
				+ "SET paid_date = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setDate(1, new Date(paidDate.getTimeInMillis()));
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	// HELPER FUNCTIONS -----------------------------------	
	/**
	 * Return true if renewalId exists
	 * @param renewalsId Id of renewal resource
	 * @return True if renewalId exists, false otherwise
	 * @throws SQLException If a database error occurs
	 */
	private boolean isRenewal(int id) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM renewals "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);

		ResultSet result = statement.executeQuery();
		if (result.next())
			return true;
		return false;
	}

	/**
	 * Return true if the renewal resource already contains a payment id
	 * @param renewalsId Id of renewal resource
	 * @return True if renewal resource contains a payment resource, false otherwise
	 * @throws SQLException 
	 */
	private boolean isPayment(int renewalsId) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM renewals "
				+ "WHERE id = ? AND payments_id IS NOT null";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, renewalsId);

		ResultSet result = statement.executeQuery();
		if (result.next())
			return true;
		return false;
	}

	/**
	 * Update renewal resource with paymendId
	 * @param renewalsId Renewal resource to update
	 * @param paymentId id being assigned
	 * @throws SQLException If a database error occurs
	 */
	private void insertRenewalPayment(int renewalsId, int paymentId) throws SQLException {
		String sqlQuery = "UPDATE renewals "
				+ "SET payments_id = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, paymentId);
		statement.setInt(2, renewalsId);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}
}
