package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.RoleDao;
import app.models.Role;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RolesHandler {
	private final RoleDao dao = new RoleDao();

	public void list(HttpExchange ex) throws Exception {
		List<Role> list = dao.list();
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Role r = dao.getById(id);
		if (r == null) { Router.notFound(ex, "Role không tồn tại"); return; }
		Router.okJson(ex, Json.stringify(r));
	}

	public void create(HttpExchange ex) throws Exception {
		Role body = Json.parse(Router.body(ex), Role.class);
		if (body == null || isBlank(body.name)) { Router.badRequest(ex, "Thiếu name"); return; }
		body.role_id = Ids.newId();
		dao.create(body);
		Router.created(ex, Json.stringify(Map.of("role_id", body.role_id)));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Role exist = dao.getById(id);
		if (exist == null) { Router.notFound(ex, "Role không tồn tại"); return; }
		Role body = Json.parse(Router.body(ex), Role.class);
		exist.name = coalesce(body.name, exist.name);
		exist.description = coalesce(body.description, exist.description);
		boolean ok = dao.update(id, exist);
		if (!ok) { Router.notFound(ex, "Role không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) { Router.notFound(ex, "Role không tồn tại"); return; }
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.isBlank(); }
	private static String coalesce(String a, String b) { return (a == null || a.isBlank()) ? b : a; }
}


