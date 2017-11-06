package au.edu.unsw.soacourse.licence;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import au.edu.unsw.soacourse.database.ServiceCounter;
import au.edu.unsw.soacourse.database.Verification;

@Path("/licences")
public class LicenceResource {
	private static final int DATE_LENGTH = 8;

	@Context
	UriInfo uriInfo;
	@Context 
	HttpHeaders header;

	/**
	 * Return an individual licence
	 * Officer & Driver access only
	 * 200 - Returned licence resource
	 * 401 - Requires authentication
	 * 403 - Requires driver or officer authorization
	 * 404 - Licence not found
	 * 500 - SQLException
	 * @param id ID of licence resource
	 * @return Licence resource
	 * @throws SQLException If a database error occurs
	 */
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getLicence(@PathParam("id") int id) throws SQLException {
		long myRequestId = ServiceCounter.id++;
		System.out.println(ServiceCounter.logHeader(myRequestId) + "@GET " + uriInfo.getAbsolutePath());

		// Check authentication & authorization
		// throws 401, 403
		try {
			Verification.checkAuthorization(header.getRequestHeader(HttpHeaders.AUTHORIZATION), true, true);
		}
		catch (NotAuthorizedException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "401 Unauthorized");
			return Response.status(401).build();
		}
		catch (ForbiddenException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "403 Forbidden");
			return Response.status(403).build();
		}

		// Attempt to retrieve the licence
		// If database error occurs
		// Throws SQLException(500 Internal Server Error)
		Licence l = null;
		try {
			l = LicenceDao.instance.get(id);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// If l == null, did not find any results
		// Return 404
		if(l == null) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		// Return licence
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(l).build();
	}

	/**
	 * Return a list of all licences expiring up to date
	 * Date must be in format DDMMYYYY
	 * Officer access only	
	 * 200 - Returned list of licence resource
	 * 400 - Date is invalid
	 * 401 - Requires authentication
	 * 403 - Requires officer account
	 * 404 - Licences not found
	 * 500 - SQLException
	 * @param date
	 * @return List of licences
	 * @throws SQLException If a database error occurs
	 */
	@GET
	@Path("expiring/{date}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getExpiring(@PathParam("date") String date) throws SQLException {
		long myRequestId = ServiceCounter.id++;
		System.out.println(ServiceCounter.logHeader(myRequestId) + "@GET " + uriInfo.getAbsolutePath());

		// Check authentication & authorization
		// throws 401, 403
		try {
			Verification.checkAuthorization(header.getRequestHeader(HttpHeaders.AUTHORIZATION), false, true);
		}
		catch (NotAuthorizedException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "401 Unauthorized");
			return Response.status(401).build();
		}
		catch (ForbiddenException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "403 Forbidden");
			return Response.status(403).build();
		}

		// Get date as calendar
		// throws 400
		Calendar cal = null;
		try {
			cal = getDate(date);
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}

		// Attempt to retrieve the licences
		// If database error occurs
		// Throws SQLException(500 Internal Server Error)
		List<Licence> list = null;
		try {
			list = LicenceDao.instance.getExpiring(cal);
		}
		catch (Exception e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// If l == null, did not find any results
		// Return 404 Not Found Exception
		if (list == null) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		GenericEntity<List<Licence>> l = new GenericEntity<List<Licence>>(list) {};

		// Return list of licences
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(l).build();
	}

	/**
	 * Update licence resource
	 * Officer & Driver access only	
	 * 200 - Update successful
	 * 401 - Requires authentication
	 * 403 - Requires officer or driver account
	 * 500 - SQLException If a database error occurs
	 * @return HTTP response OK
	 * @throws SQLException If a database error occurs
	 */
	@PUT
	@Path("{id}")
	public Response putLicence(@PathParam("id") int id,
			@FormParam("address") String address,
			@FormParam("email") String email,
			@FormParam("expiryDate") String expiryDate) throws SQLException {
		long myRequestId = ServiceCounter.id++;
		System.out.println(ServiceCounter.logHeader(myRequestId) + "@PUT " + uriInfo.getAbsolutePath());

		// Check authentication & authorization
		// throws 401, 403
		try {
			Verification.checkAuthorization(header.getRequestHeader(HttpHeaders.AUTHORIZATION), true, true);
		}
		catch (NotAuthorizedException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "401 Unauthorized");
			return Response.status(401).build();
		}
		catch (ForbiddenException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "403 Forbidden");
			return Response.status(403).build();
		}

		// Test syntax
		try {
			Licence l = new Licence();
			l.setId(id);
			l.setAddress(address);
			l.setEmail(email);
			l.setExpiryDate(getDate(expiryDate).getTime());
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}
		catch (NullPointerException e) {
		}

		// Attempt update
		// If database error occurs
		// Throws SQLException(500 Internal Server Error)
		try {
			if (address != null)
				LicenceDao.instance.updateAddress(id, address);
			if (email != null)
				LicenceDao.instance.updateEmail(id, email);
			if (expiryDate != null)
				LicenceDao.instance.updateExpiryDate(id, getDate(expiryDate));
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}

		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().build();
	}


	//	------------------------------------------------------------------------
	//	PRIVATE FUNCTIONS

	/**
	 * Get date from string in format DDMMYYYY
	 * @param date Date to format
	 * @return Calendar representation of "date"
	 * @throws BadRequestException Date format is incorrect
	 */
	private Calendar getDate(String date) throws IllegalArgumentException {
		// Check length
		if (date.length() != DATE_LENGTH)
			throw new IllegalArgumentException("Date incorrect length");

		// Get date values as ints
		int day = Integer.parseInt(date.substring(0, 2));
		int month = Integer.parseInt(date.substring(2, 4));
		month--;
		int year = Integer.parseInt(date.substring(4, 8));

		// Create calendar
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.setLenient(false);

		// Check for any error with date
		// Throws IllegalArgumentException if found
		cal.getTime();

		return cal;
	}
}
