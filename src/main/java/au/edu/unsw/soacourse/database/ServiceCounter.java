package au.edu.unsw.soacourse.database;

import java.util.Calendar;

public class ServiceCounter {
	public static long id = 0;
	
	public static String logHeader(long id) {
		return Calendar.getInstance().getTime() + " Rest Service(ID[" + id + "]): ";
	}
}
