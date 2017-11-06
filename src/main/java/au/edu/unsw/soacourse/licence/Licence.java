package au.edu.unsw.soacourse.licence;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Licence {
	private static final int MAX_STRING_SIZE = 45;
	private static final int MAX_ADDRESS_SIZE = 100;

	private int id;
	private String name;
	private String address;
	private String number;
	private String licenceClass;
	private String email;
	private Date expiryDate;

	public Licence() {}

	public Licence(int id, String name, String address, String number,
			String licenceClass, String email, Date expiryDate) {
		setId(id);
		setName(name);
		setAddress(address);
		setNumber(number);
		setLicenceClass(licenceClass);
		setEmail(email);
		setExpiryDate(expiryDate);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) throws IllegalArgumentException  {
		if (name.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("name can not be longer than " + MAX_STRING_SIZE + " characters");
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) throws IllegalArgumentException  {
		if (address.length() > MAX_ADDRESS_SIZE)
			throw new IllegalArgumentException("address can not be longer than " + MAX_ADDRESS_SIZE + " characters");
		this.address = address;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) throws IllegalArgumentException  {
		if (number.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("number can not be longer than " + MAX_STRING_SIZE + " characters");
		this.number = number;
	}

	public String getLicenceClass() {
		return licenceClass;
	}

	public void setLicenceClass(String licenceClass) throws IllegalArgumentException  {
		if (licenceClass.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("licenceClass can not be longer than " + MAX_STRING_SIZE + " characters");
		this.licenceClass = licenceClass;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) throws IllegalArgumentException  {
		if (email.length() > MAX_STRING_SIZE)
			throw new IllegalArgumentException("email can not be longer than " + MAX_STRING_SIZE + " characters");
		this.email = email;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

}
