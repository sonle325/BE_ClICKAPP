package app.core;

import java.sql.Connection;
import java.sql.DriverManager;

public final class Db {
	private static String url;

	public static void init(String jdbcUrl) {
		url = jdbcUrl;
	}

	public static Connection get() {
		try {
			return DriverManager.getConnection(url);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}


