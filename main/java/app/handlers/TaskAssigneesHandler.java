package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.TaskAssigneeDao;
import app.models.TaskAssignee;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskAssigneesHandler {
	private final TaskAssigneeDao dao = new TaskAssigneeDao();

	public void listByTask(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String taskId = qp.get("task_id");
		if (isBlank(taskId)) { Router.badRequest(ex, "Thiếu task_id"); return; }
		List<TaskAssignee> list = dao.listByTask(taskId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void add(HttpExchange ex) throws Exception {
		@SuppressWarnings("unchecked")
        Map<String, String> body = (Map<String, String>) Json.parse(Router.body(ex), Map.class);
		String taskId = body == null ? null : body.get("task_id");
		String userId = body == null ? null : body.get("user_id");
		if (isBlank(taskId) || isBlank(userId)) { Router.badRequest(ex, "Thiếu task_id hoặc user_id"); return; }
		dao.add(taskId, userId);
		Router.created(ex, Json.stringify(Map.of("ok", true)));
	}

	public void remove(HttpExchange ex) throws Exception {
		Map<String, String> params = Router.pathParams(ex);
		boolean ok = dao.remove(params.get("task_id"), params.get("user_id"));
		if (!ok) { Router.notFound(ex, "Chưa assign user này vào task"); return; }
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