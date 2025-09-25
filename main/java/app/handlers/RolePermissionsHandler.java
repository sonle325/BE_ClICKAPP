package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.RolePermissionDao;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolePermissionsHandler {
	private final RolePermissionDao dao = new RolePermissionDao();

	public void listByRole(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String roleId = qp.get("role_id");
		if (isBlank(roleId)) { Router.badRequest(ex, "Thiếu role_id"); return; }
		List<String> permissionIds = dao.listPermissionIdsByRole(roleId);
		Router.okJson(ex, Json.stringify(permissionIds));
	}

	public void add(HttpExchange ex) throws Exception {
		Map<String, String> body = Json.parse(Router.body(ex), Map.class);
		String roleId = body == null ? null : body.get("role_id");
		String permissionId = body == null ? null : body.get("permission_id");
		if (isBlank(roleId) || isBlank(permissionId)) { Router.badRequest(ex, "Thiếu role_id hoặc permission_id"); return; }
		dao.add(roleId, permissionId);
		Router.created(ex, Json.stringify(Map.of("ok", true)));
	}

	public void remove(HttpExchange ex) throws Exception {
		Map<String, String> params = Router.pathParams(ex);
		boolean ok = dao.remove(params.get("role_id"), params.get("permission_id"));
		if (!ok) { Router.notFound(ex, "Không tồn tại liên kết"); return; }
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.isBlank(); }

	private static Map<String,String> parseQuery(String raw) {
		Map<String,String> m = new HashMap<>();
		if (raw == null || raw.isBlank()) return m;
		for (String kv : raw.split("&")) {
			int i = kv.indexOf('=');
			if (i > 0) m.put(urlDecode(kv.substring(0, i)), urlDecode(kv.substring(i + 1)));
		}
		return m;
	}

	private static String urlDecode(String s) {
		try { return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8); }
		catch (Exception e) { return s; }
	}
}


