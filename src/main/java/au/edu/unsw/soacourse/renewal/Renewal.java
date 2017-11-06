package au.edu.unsw.soacourse.renewal;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Renewal {
	private static final int MAX_STRING_SIZE = 45;
	private static final int MAX_ADDRESS_SIZE = 100;

	private int id;
	private int licenceId;
	private String address;
	private String email;
	private String status;
	private String ownedBy;
	private int paymentId;

	public Renewal() {};

	public Renewal(int id, int licenceId, String address, String email,
			String status, String ownedBy, int paymentId) {
		setId(id);
		setLicenceId(licenceId);
		setAddress(address);
		setEmail(email);
		setStatus(status);
		setOwnedBy(ownedBy);
		setPaymentId(paymentId);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getLicenceId() {
		return licenceId;
	}
	
	public void setLicenceId(int licenceId) {
		this.licenceId = licenceId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if (address.length() > MAX_ADDRESS_SIZE)
			throw new IllegalArgumentException("address can not be longer than " + MAX_ADDRESS_SIZE + " characters");
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		if (email.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("email can not be longer than " + MAX_STRING_SIZE + " characters");
		this.email = email;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("status can not be longer than " + MAX_STRING_SIZE + " characters");
		this.status = status;
	}

	public String getOwnedBy() {
		return ownedBy;
	}

	public void setOwnedBy(String ownedBy) {
		if (ownedBy == null)
			return;
		if (ownedBy.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("ownedBy can not be longer than " + MAX_STRING_SIZE + " characters");
		this.ownedBy = ownedBy;
	}
	
	public int getPaymentId() {
		return paymentId;
	}
	
	public void setPaymentId(int paymentId) {
		this.paymentId = paymentId;
	}

	public static int getMaxStringSize() {
		return MAX_STRING_SIZE;
	}
}
