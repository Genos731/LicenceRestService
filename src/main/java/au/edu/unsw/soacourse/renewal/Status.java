package au.edu.unsw.soacourse.renewal;

public enum Status {
	PENDING, 				// Waiting for driver to validate details
	VALIDATION_ERROR, 		// Validation error occured, awaiting officer to fix
	EXTENSION_REQUESTED,	// Requesting extension, awaiting officer to pick up
	EVALUATING_EXTENSION,	// Officer has picked up, driver can not backtrack
	EXTENSION_APPROVED,		// Extension approved, fee has been updated
	COMPLETED;				// Renewal completed
	
	public static Status toStatus(String s) {
		switch (s.toUpperCase()) { 
		case "PENDING":
			return PENDING;
		case "VALIDATION_ERROR":
			return VALIDATION_ERROR;
		case "EXTENSION_REQUESTED":
			return EXTENSION_REQUESTED;
		case "EVALUATING_EXTENSION":
			return EVALUATING_EXTENSION;
		case "EXTENSION_APPROVED":
			return EXTENSION_APPROVED;
		case "COMPLETED":
			return COMPLETED;
		default:
			return null;
		}
	}

	public String toString() {
		switch (this) {
		case PENDING:
			return "PENDING";
		case VALIDATION_ERROR:
			return "VALIDATION_ERROR";
		case EXTENSION_REQUESTED:
			return "EXTENSION_REQUESTED";
		case EVALUATING_EXTENSION:
			return "EVALUATING_EXTENSION";
		case EXTENSION_APPROVED:
			return "EXTENSION_APPROVED";
		case COMPLETED:
			return "COMPLETED";
		default:
			return null;
		}
	}
}
