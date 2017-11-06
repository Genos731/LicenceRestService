package au.edu.unsw.soacourse.renewal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import au.edu.unsw.soacourse.database.DBConnection;

public enum RenewalDao {
	instance;

	Connection dbConnection;

	private RenewalDao() {
		try {
			dbConnection = DBConnection.getConnection();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Get a renewal resource by it's id
	 * @param id ID of renewal resource
	 * @return Renewal resource
	 * @throws SQLException If a database error occurs
	 */
	public Renewal getById(int id) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM renewals "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);

		ResultSet result = statement.executeQuery();

		Renewal r = null;
		if (result.next()) {
			r = new Renewal();
			r.setId(result.getInt("id"));
			r.setLicenceId(result.getInt("licences_id"));
			r.setAddress(result.getString("address"));
			r.setEmail(result.getString("email"));
			r.setStatus(result.getString("status"));
			r.setOwnedBy(result.getString("owned_by"));
			r.setPaymentId(result.getInt("payments_id"));
		}

		return r;
	}

	/**
	 * Get a non-completed renewal resource by it's corresponding licence id
	 * @param id ID of licence
	 * @return Renewal resource
	 * @throws SQLException If a database error occurs
	 */
	public Renewal getByLicence(int id) throws SQLException {
		String sqlQuery = "SELECT * "
				+ "FROM renewals "
				+ "WHERE licences_id = ? AND status NOT LIKE ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);
		statement.setString(2, Status.COMPLETED.toString());

		ResultSet result = statement.executeQuery();

		Renewal r = null;
		if (result.next()) {
			r = new Renewal();
			r.setId(result.getInt("id"));
			r.setLicenceId(result.getInt("licences_id"));
			r.setAddress(result.getString("address"));
			r.setEmail(result.getString("email"));
			r.setStatus(result.getString("status"));
			r.setOwnedBy(result.getString("owned_by"));
			r.setPaymentId(result.getInt("payments_id"));
		}

		return r;
	}

	/**
	 * Get a list of renewal notices by it's status
	 * @param status Status of renewal notices
	 * @return List of renewal resource
	 * @throws SQLException If a database error occurs
	 */
	public List<Renewal> getByStatus(Status status) throws SQLException {
		if (status == null)
			throw new IllegalArgumentException("Status can not be null");
		String sqlQuery = "SELECT * "
				+ "FROM renewals "
				+ "WHERE status LIKE ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, status.toString());

		ResultSet result = statement.executeQuery();

		List<Renewal> list = new ArrayList<Renewal>();
		while (result.next()) {
			Renewal r = new Renewal();
			r.setId(result.getInt("id"));
			r.setLicenceId(result.getInt("licences_id"));
			r.setAddress(result.getString("address"));
			r.setEmail(result.getString("email"));
			r.setStatus(result.getString("status"));
			r.setOwnedBy(result.getString("owned_by"));
			r.setPaymentId(result.getInt("payments_id"));

			list.add(r);
		}

		if (list.size() == 0)
			return null;

		return list;
	}

	/**
	 * Create a new renewal resource for a licence
	 * @param licenceId ID of licence
	 * @param address Address of licence
	 * @param email Email of licence
	 * @return ID of renewal resource
	 * @throws SQLException
	 */
	public int create(int licenceId, String address, String email) throws SQLException {
		if (!isLicence(licenceId))
			throw new IllegalArgumentException("licenceId does not exists");

		if (isUncompletedRenewal(licenceId))
			throw new IllegalArgumentException("Non-completed renewal already exists for this licence");

		// Create sql statement
		String sqlQuery = "INSERT INTO renewals "
				+ "(licences_id, address, email, status) "
				+ "VALUES "
				+ "(?, ?, ?, ?)";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);
		statement.setInt(1, licenceId);
		statement.setString(2, address);
		statement.setString(3,  email);
		statement.setString(4, Status.PENDING.toString());

		// Execute, throws error if failed
		statement.executeUpdate();

		// Retrieve row id
		ResultSet generatedKeys = statement.getGeneratedKeys();
		int renewalId = 0;
		if (generatedKeys.next())
			renewalId = generatedKeys.getInt(1);
		else
			throw new SQLException("Error retrieving renewal id");
		if (renewalId == 0)
			throw new SQLException("Error retrieving renewal id");

		statement.close();
		return renewalId;
	}

	public void updateAddress(int id, String address) throws SQLException {
		String sqlQuery = "UPDATE renewals "
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
		String sqlQuery = "UPDATE renewals "
				+ "SET email = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, email);
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	public void updateStatus(int id, Status status) throws SQLException {
		if (status == null)
			throw new IllegalArgumentException("Status can not be null");
		String sqlQuery = "UPDATE renewals "
				+ "SET status = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, status.toString());
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	public void updateOwnedBy(int id, String ownedBy) throws SQLException {
		String sqlQuery = "UPDATE renewals "
				+ "SET owned_by = ? "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setString(1, ownedBy);
		statement.setInt(2, id);

		// Execute, throws error if failed
		statement.executeUpdate();
		statement.close();
	}

	// HELPER FUNCTIONS -------------------------------------------------

	/**
	 * Return true if the licence exists
	 * @param id id of licence
	 * @return True if licence exist
	 * @throws SQLException If a database error occurs
	 */
	private boolean isLicence(int id) throws SQLException {
		String sqlQuery = "Select * "
				+ "FROM licences "
				+ "WHERE id = ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, id);

		ResultSet result = statement.executeQuery();
		if (result.next())
			return true;
		return false;
	}

	/**
	 * Return true if renewal already exists, which status is not completed
	 * @param licenceId id of licence associated with renewal resource
	 * @return True if renewal exists which is non-completed, false otherwise
	 * @throws SQLException If a database error occurs
	 */
	private boolean isUncompletedRenewal(int licenceId) throws SQLException {
		String sqlQuery = "Select * "
				+ "FROM renewals "
				+ "WHERE licences_id = ? AND status NOT LIKE ?";
		PreparedStatement statement = dbConnection.prepareStatement(sqlQuery);
		statement.setInt(1, licenceId);
		statement.setString(2, Status.COMPLETED.toString());

		ResultSet result = statement.executeQuery();
		if (result.next())
			return true;
		return false;
	}
}
