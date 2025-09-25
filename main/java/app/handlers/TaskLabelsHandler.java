package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.TaskLabelDao;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskLabelsHandler {
	private final TaskLabelDao dao = new TaskLabelDao();

	public void listByTask(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String taskId = qp.get("task_id");
		if (isBlank(taskId)) { Router.badRequest(ex, "Thiếu task_id"); return; }
		List<String> labelIds = dao.listLabelIdsByTask(taskId);
		Router.okJson(ex, Json.stringify(labelIds));
	}

	public void add(HttpExchange ex) throws Exception {
		Map<String, String> body = Json.parse(Router.body(ex), Map.class);
		String taskId = body == null ? null : body.get("task_id");
		String labelId = body == null ? null : body.get("label_id");
		if (isBlank(taskId) || isBlank(labelId)) { Router.badRequest(ex, "Thiếu task_id hoặc label_id"); return; }
		dao.add(taskId, labelId);
		Router.created(ex, Json.stringify(Map.of("ok", true)));
	}

	public void remove(HttpExchange ex) throws Exception {
		Map<String, String> params = Router.pathParams(ex);
		String taskId = params.get("task_id");
		String labelId = params.get("label_id");
		boolean ok = dao.remove(taskId, labelId);
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


