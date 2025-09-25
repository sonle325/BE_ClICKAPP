package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.TaskDao;
import app.models.Task;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TasksHandler {
	private final TaskDao dao = new TaskDao();

	public void listByProject(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String projectId = qp.get("project_id");
		if (projectId == null || projectId.isBlank()) {
			Router.badRequest(ex, "Thiếu project_id");
			return;
		}
		List<Task> list = dao.listByProject(projectId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Task t = dao.getById(id);
		if (t == null) {
			Router.notFound(ex, "Task không tồn tại");
			return;
		}
		Router.okJson(ex, Json.stringify(t));
	}

	public void create(HttpExchange ex) throws Exception {
		Task body = Json.parse(Router.body(ex), Task.class);
		if (body == null || isBlank(body.project_id) || isBlank(body.title) || isBlank(body.status) || isBlank(body.priority) || isBlank(body.created_by)) {
			Router.badRequest(ex, "Thiếu dữ liệu");
			return;
		}
		body.task_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("task_id", body.task_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Task existing = dao.getById(id);
		if (existing == null) {
			Router.notFound(ex, "Task không tồn tại");
			return;
		}
		Task body = Json.parse(Router.body(ex), Task.class);
		body.title = coalesce(body.title, existing.title);
		body.description = coalesce(body.description, existing.description);
		body.status = coalesce(body.status, existing.status);
		body.priority = coalesce(body.priority, existing.priority);
		body.due_date = coalesce(body.due_date, existing.due_date);
		boolean ok = dao.update(id, body);
		if (!ok) {
			Router.notFound(ex, "Task không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) {
			Router.notFound(ex, "Task không tồn tại");
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


