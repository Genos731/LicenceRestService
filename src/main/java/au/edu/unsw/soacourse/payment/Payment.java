package au.edu.unsw.soacourse.payment;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Payment {
	private int id;
	private double amount;
	private Date paidDate;

	public Payment() {}

	public Payment(int id, double amount, Date paidDate) {
		this.id = id;
		this.amount = amount;
		this.paidDate = paidDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaidDate() {
		return paidDate;
	}

	public void setPaidDate(Date paidDate) {
		this.paidDate = paidDate;
	}
}
