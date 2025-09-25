package app.core;

import java.sql.Connection;
import java.sql.DriverManager;

public final class Db {
    private static String url;
    private static String username;
    private static String password;

    public static void init(String jdbcUrl, String user, String pass) {
        url = jdbcUrl;
        username = user;
        password = pass;
    }

    public static Connection get() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}