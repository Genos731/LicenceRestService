package au.edu.unsw.soacourse.payment;

import java.net.URI;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import au.edu.unsw.soacourse.database.ServiceCounter;
import au.edu.unsw.soacourse.database.Verification;

@Path("/payments")
public class PaymentResource {
	private static final int DATE_LENGTH = 8;
	
	@Context
	UriInfo uriInfo;
	@Context 
	HttpHeaders header;

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getPayment(@PathParam("id") int id) throws SQLException  {
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

		// Attempt to retrieve the payment
		// If database error occurs
		// Throws SQLException(500 Internal Server Error)
		Payment p = null;
		try {
			p = PaymentDao.instance.get(id);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// If p == null, did not find any results
		// Return 404 Not Found Exception
		if(p == null) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		// Return payment
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(p).build();
	}

	/**
	 * 
	 * @param renewalId
	 * @param amount
	 * @return
	 * @throws SQLException
	 */
	@POST
	public Response createPayment(
			@FormParam("renewalId") int renewalId,
			@FormParam("amount") double amount ) throws SQLException {

		long myRequestId = ServiceCounter.id++;
		System.out.println(ServiceCounter.logHeader(myRequestId) + "@POST " + uriInfo.getAbsolutePath());

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

		// Attempt to insert payment
		int paymentID = 0;
		try {
			paymentID = PaymentDao.instance.create(renewalId, amount);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}
		
		if (paymentID == 0)
			throw new InternalServerErrorException("id returned 0, unknown error");


		// No errors, successful
		// Add new uri to response
		URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(paymentID)).build();
		System.out.println(ServiceCounter.logHeader(myRequestId) + "201 Created");
		return Response.created(uri).build();
	}

	/**
	 * 
	 * @param id
	 * @param amount
	 * @param paidDate
	 * @return
	 * @throws SQLException
	 */
	@PUT
	@Path("{id}")
	public Response updatePayment(@PathParam("id") int id,
			@FormParam("amount") Double amount,
			@FormParam("paidDate") String paidDate) throws SQLException {
		long myRequestId = ServiceCounter.id++;
		System.out.println(ServiceCounter.logHeader(myRequestId) + "@PUT " + uriInfo.getAbsolutePath() + " for licence id " + id);

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

		// Attempt to update
		try {
			if (amount != null)
				PaymentDao.instance.updateAmount(id, amount.doubleValue());
			if (paidDate != null)
				PaymentDao.instance.updatePaidDate(id, getDate(paidDate));
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}

		// No error, return OK
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