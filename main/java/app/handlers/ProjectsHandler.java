package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.ProjectDao;
import app.models.Project;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectsHandler {
	private final ProjectDao dao = new ProjectDao();

	public void listByTeam(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String teamId = qp.get("team_id");
		if (teamId == null || teamId.isBlank()) {
			Router.badRequest(ex, "Thiếu team_id");
			return;
		}
		List<Project> list = dao.listByTeam(teamId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Project p = dao.getById(id);
		if (p == null) {
			Router.notFound(ex, "Project không tồn tại");
			return;
		}
		Router.okJson(ex, Json.stringify(p));
	}

	public void create(HttpExchange ex) throws Exception {
		Project body = Json.parse(Router.body(ex), Project.class);
		if (body == null || isBlank(body.team_id) || isBlank(body.name)) {
			Router.badRequest(ex, "Thiếu dữ liệu");
			return;
		}
		body.project_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("project_id", body.project_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Project existing = dao.getById(id);
		if (existing == null) {
			Router.notFound(ex, "Project không tồn tại");
			return;
		}
		Project body = Json.parse(Router.body(ex), Project.class);
		body.name = coalesce(body.name, existing.name);
		body.description = coalesce(body.description, existing.description);
		boolean ok = dao.update(id, body);
		if (!ok) {
			Router.notFound(ex, "Project không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) {
			Router.notFound(ex, "Project không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.isBlank(); }
	private static String coalesce(String a, String b) { return (a == null || a.isBlank()) ? b : a; }

	private static Map<String, String> parseQuery(String raw) {
		Map<String, String> m = new HashMap<>();
		if (raw == null || raw.isBlank()) return m;
		for (String kv : raw.split("&")) {
			int i = kv.indexOf('=');
			if (i > 0) m.put(urlDecode(kv.substring(0, i)), urlDecode(kv.substring(i + 1)));
		}
		return m;
	}

	private static String urlDecode(String s) {
		try {
			return java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8);
		} catch (Exception e) {
			return s;
		}
	}
}


