package au.edu.unsw.soacourse.renewal;

import java.net.URI;
import java.sql.SQLException;
import java.util.List;

import javax.ws.rs.DELETE;
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
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import au.edu.unsw.soacourse.database.ServiceCounter;
import au.edu.unsw.soacourse.database.Verification;

@Path("/renewals")
public class RenewalResource {
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
	public Response getById(@PathParam("id") int id) throws SQLException {
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
		Renewal r = null;
		try {
			r = RenewalDao.instance.getById(id);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// If r == null, did not find any results
		// Return 404 Not Found Exception
		if(r == null){
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		// Return licence
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(r).build();		
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("licenceId/{id}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getByLicenceId(@PathParam("id") int id) throws SQLException {
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
		Renewal r = null;
		try {
			r = RenewalDao.instance.getByLicence(id);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// If r == null, did not find any results
		// Return 404 Not Found Exception
		if(r == null){
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		// Return renewal
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(r).build();	
	}

	/**
	 * 
	 * @param s
	 * @return
	 * @throws SQLException
	 */
	@GET
	@Path("/status/{status}")
	@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
	public Response getByStatus(@PathParam("status") String s) throws SQLException {
		Status status = Status.toStatus(s);

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

		// Attempt to retrieve the payment
		// If database error occurs
		// Throws SQLException(500 Internal Server Error)
		List<Renewal> r = null;
		try {
			r = RenewalDao.instance.getByStatus(status);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}

		// If r == null, did not find any results
		// Return 404 Not Found Exception
		if(r == null){
			System.out.println(ServiceCounter.logHeader(myRequestId) + "404 Not Found");
			return Response.status(404).build();
		}

		// Return licence
		GenericEntity<List<Renewal>> list = new GenericEntity<List<Renewal>>(r) {};
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().entity(list).build();	
	}

	/**
	 * 
	 * @param address
	 * @param email
	 * @param licenceId
	 * @return
	 * @throws SQLException
	 */
	@POST
	public Response createRenewal(
			@FormParam("address") String address,
			@FormParam("email") String email,
			@FormParam("licenceId") int licenceId) throws SQLException {
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

		// Attempt to create Renewal
		int id = 0;
		try {
			id = RenewalDao.instance.create(licenceId, address, email);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}

		if (id == 0)
			throw new InternalServerErrorException("id returned 0, unknown error");

		// No errors, successful
		// Add new uri to response
		URI uri = uriInfo.getAbsolutePathBuilder().path(Integer.toString(id)).build();
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.created(uri).build();
	}

	/**
	 * 
	 * @param id
	 * @param address
	 * @param email
	 * @param status
	 * @param ownedBy
	 * @return
	 * @throws SQLException
	 */
	@PUT
	@Path("{id}")
	public Response updateRenewal(@PathParam("id") int id,
			@FormParam("address") String address,
			@FormParam("email") String email,
			@FormParam("status") String status,
			@FormParam("ownedBy") String ownedBy) throws SQLException {
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

		// Check syntax are correct
		try {
			Renewal r = new Renewal();
			r.setId(id);
			r.setAddress(address);
			r.setEmail(email);
			r.setStatus(status);
			r.setOwnedBy(ownedBy);
		}
		catch (IllegalArgumentException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "400 Bad Request");
			return Response.status(400).build();
		}
		catch (NullPointerException e) {
		}

		// Attempt to update
		try {
			if (address != null)
				RenewalDao.instance.updateAddress(id, address);
			if (email != null)
				RenewalDao.instance.updateEmail(id, email);
			if (status != null)
				RenewalDao.instance.updateStatus(id, Status.toStatus(status));
			if (ownedBy != null)
				RenewalDao.instance.updateOwnedBy(id, ownedBy);
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

	/**
	 * 
	 * @param id
	 * @return
	 * @throws SQLException
	 */
	@DELETE
	@Path("{id}")
	public Response deleteRenewal(@PathParam("id") int id) throws SQLException {
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

		// Attempt to update
		try {
			RenewalDao.instance.updateStatus(id, Status.COMPLETED);
		}
		catch (SQLException e) {
			System.out.println(ServiceCounter.logHeader(myRequestId) + "500 Internal Server Error");
			throw e;
		}

		// No error, return OK
		System.out.println(ServiceCounter.logHeader(myRequestId) + "200 OK");
		return Response.ok().build();
	}
}
