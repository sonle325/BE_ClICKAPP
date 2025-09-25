package app.handlers;

import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import app.core.Json;
import app.core.Router;
import app.dao.PermissionDao;
import app.models.Permission;
import app.util.Ids;

public class PermissionsHandler {
	private final PermissionDao dao = new PermissionDao();

	public void list(HttpExchange ex) throws Exception {
		List<Permission> list = dao.list();
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Permission p = dao.getById(id);
		if (p == null) { Router.notFound(ex, "Permission không tồn tại"); return; }
		Router.okJson(ex, Json.stringify(p));
	}

	public void create(HttpExchange ex) throws Exception {
		Permission body = Json.parse(Router.body(ex), Permission.class);
		if (body == null || isBlank(body.name)) { Router.badRequest(ex, "Thiếu name"); return; }
		body.permission_id = Ids.newId();
		dao.create(body);
		Router.created(ex, Json.stringify(Map.of("permission_id", body.permission_id)));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Permission exist = dao.getById(id);
		if (exist == null) { Router.notFound(ex, "Permission không tồn tại"); return; }
		Permission body = Json.parse(Router.body(ex), Permission.class);
		exist.name = coalesce(body.name, exist.name);
		exist.description = coalesce(body.description, exist.description);
		boolean ok = dao.update(id, exist);
		if (!ok) { Router.notFound(ex, "Permission không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) { Router.notFound(ex, "Permission không tồn tại"); return; }
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.isBlank(); }
	private static String coalesce(String a, String b) { return (a == null || a.isBlank()) ? b : a; }
}


