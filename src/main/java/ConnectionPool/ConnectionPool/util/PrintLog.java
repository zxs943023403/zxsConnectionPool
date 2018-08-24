package ConnectionPool.ConnectionPool.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PrintLog {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static void log(String msg) {
		System.out.println("["+sdf.format(new Date())+"]"+msg);
	}
}
