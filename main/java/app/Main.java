package app;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import app.core.Db;
import app.core.Router;
import app.handlers.RegisterRoutes;
import java.sql.Connection;
import java.sql.Statement;

public class Main {
	public static void main(String[] args) throws Exception {
		String dbPath = System.getProperty("user.dir") + "/data.db";
		Db.init("jdbc:sqlite:" + dbPath);
		runMigrations("schema.sql");

		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		Router router = new Router(server);

		router.register("/health", "GET", exchange -> Router.okJson(exchange, "{\"ok\":true}"));

		RegisterRoutes.registerAll(router);

		server.setExecutor(java.util.concurrent.Executors.newCachedThreadPool());
		server.start();
		System.out.println("Server started at http://localhost:8080");
	}

	private static void runMigrations(String schemaFile) throws Exception {
		String sql = Files.readString(Path.of(schemaFile));
		try (Connection c = Db.get(); Statement st = c.createStatement()) {
			st.executeUpdate("PRAGMA foreign_keys = ON;");
			for (String stmt : sql.split(";\\s*\\n")) {
				String trimmed = stmt.trim();
				if (!trimmed.isEmpty()) {
					st.executeUpdate(trimmed + ";");
				}
			}
		}
	}
}


