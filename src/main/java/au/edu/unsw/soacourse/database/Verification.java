package au.edu.unsw.soacourse.database;

import java.util.List;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotAuthorizedException;

public class Verification {
	private static final String DRIVER_KEY = "DRIVER@#$";
	private static final String OFFICER_KEY = "OFFICER@#$";

	/**
	 * Does nothing if access is valid
	 * @throws NotAuthorizedException if not authenticated
	 * @throws ForbiddenException if not authorized
	 */
	public static void checkAuthorization(List<String> authoHeader, boolean driver, boolean officer) throws NotAuthorizedException, ForbiddenException {		
		// if not authenticated, throw 401 unauthenticated
		if (authoHeader == null || (!authoHeader.contains(Verification.OFFICER_KEY) && !authoHeader.contains(Verification.DRIVER_KEY)))
			throw new NotAuthorizedException("Requires Authentication");
		
		// Driver or Officer is valid, return
		if (officer && driver)
			return;
		
		// If only officer valid & not an officer, throw 403 forbidden access
		if (officer)
			if (!authoHeader.contains(OFFICER_KEY))
				throw new ForbiddenException("Requries proper authorization");
		
		// If only driver valid & not a driver, throw 403 forbidden access
		if (driver)
			if (!authoHeader.contains(DRIVER_KEY))
				throw new ForbiddenException("Requries proper authorization");
	}
}
