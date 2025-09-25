package app.core;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

public final class Router {
	private final HttpServer server;
	private final List<Route> routes = new ArrayList<>();

	public Router(HttpServer server) {
		this.server = server;
	}

	public void register(String pattern, String method, Handler handler) {
		Route r = new Route(pattern, method.toUpperCase(), handler);
		routes.add(r);
		server.createContext(basePath(pattern), (HttpHandler) exchange -> {
			try {
				dispatch(exchange);
			} catch (Exception e) {
				try {
					send(exchange, 500, json(Map.of("error", "internal_error", "message", e.getMessage())));
				} catch (Exception ex) {
					// Log error
				}
			}
		});
	}

	private String basePath(String pattern) {
		int idx = pattern.indexOf("/{");
		return idx > 0 ? pattern.substring(0, idx) : pattern;
	}

	private void dispatch(HttpExchange exchange) throws Exception {
		String method = exchange.getRequestMethod().toUpperCase();
		URI uri = exchange.getRequestURI();
		String path = uri.getPath();
		for (Route r : routes) {
			Map<String, String> params = r.match(path, method);
			if (params != null) {
				exchange.setAttribute("pathParams", params);
				r.handler.handle(exchange);
				return;
			}
		}
		send(exchange, 404, json(Map.of("error", "not_found")));
	}

	public static Map<String, String> pathParams(HttpExchange ex) {
		Object o = ex.getAttribute("pathParams");
		if (o instanceof Map<?, ?> m) {
			@SuppressWarnings("unchecked")
			Map<String, String> cast = (Map<String, String>) m;
			return cast;
		}
		return Map.of();
	}

	public static String body(HttpExchange ex) throws Exception {
		try (InputStream in = ex.getRequestBody()) {
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		}
	}

	public static void okJson(HttpExchange ex, String json) throws Exception {
		send(ex, 200, json);
	}

	public static void created(HttpExchange ex, String json) throws Exception {
		send(ex, 201, json);
	}

	public static void noContent(HttpExchange ex) throws Exception {
		ex.getResponseHeaders().add("Content-Type", "application/json");
		ex.sendResponseHeaders(204, -1);
		ex.close();
	}

	public static void badRequest(HttpExchange ex, String message) throws Exception {
		send(ex, 400, json(Map.of("error", "bad_request", "message", message)));
	}

	public static void notFound(HttpExchange ex, String message) throws Exception {
		send(ex, 404, json(Map.of("error", "not_found", "message", message)));
	}

	public static String json(Object obj) {
		return app.core.Json.stringify(obj);
	}

	public static void send(HttpExchange ex, int status, String body) throws Exception {
		byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
		ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
		ex.sendResponseHeaders(status, bytes.length);
		try (OutputStream os = ex.getResponseBody()) {
			os.write(bytes);
		}
	}

	public interface Handler {
		void handle(HttpExchange exchange) throws Exception;
	}

	private static final class Route {
		@SuppressWarnings("unused")
		private final String pattern;
		private final String method;
		private final List<String> parts;

		private Route(String pattern, String method, Handler handler) {
			this.pattern = pattern;
			this.method = method;
			this.handler = handler;
			this.parts = Arrays.stream(pattern.split("/")).
					filter(s -> !s.isEmpty()).toList();
		}

		private final Handler handler;

		Map<String, String> match(String path, String reqMethod) {
			if (!method.equals(reqMethod)) return null;
			List<String> p = Arrays.stream(path.split("/")).filter(s -> !s.isEmpty()).toList();
			if (p.size() != parts.size()) return null;
			Map<String, String> vars = new HashMap<>();
			for (int i = 0; i < parts.size(); i++) {
				String a = parts.get(i);
				String b = p.get(i);
				if (a.startsWith("{") && a.endsWith("}")) {
					vars.put(a.substring(1, a.length() - 1), b);
				} else if (!a.equals(b)) {
					return null;
				}
			}
			return vars;
		}
	}
}


