package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.CommentDao;
import app.models.Comment;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentsHandler {
	private final CommentDao dao = new CommentDao();

	public void listByTask(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String taskId = qp.get("task_id");
		if (isBlank(taskId)) { Router.badRequest(ex, "Thiếu task_id"); return; }
		List<Comment> list = dao.listByTask(taskId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Comment c = dao.getById(id);
		if (c == null) { Router.notFound(ex, "Comment không tồn tại"); return; }
		Router.okJson(ex, Json.stringify(c));
	}

	public void create(HttpExchange ex) throws Exception {
		Comment body = Json.parse(Router.body(ex), Comment.class);
		if (body == null || isBlank(body.task_id) || isBlank(body.user_id) || isBlank(body.body)) {
			Router.badRequest(ex, "Thiếu dữ liệu"); return;
		}
		body.comment_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("comment_id", body.comment_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Comment exist = dao.getById(id);
		if (exist == null) { Router.notFound(ex, "Comment không tồn tại"); return; }
		Comment body = Json.parse(Router.body(ex), Comment.class);
		if (body == null || isBlank(body.body)) { Router.badRequest(ex, "Thiếu body"); return; }
		boolean ok = dao.update(id, body);
		if (!ok) { Router.notFound(ex, "Comment không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) { Router.notFound(ex, "Comment không tồn tại"); return; }
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


