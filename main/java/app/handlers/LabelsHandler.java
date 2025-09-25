package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.LabelDao;
import app.models.Label;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LabelsHandler {
	private final LabelDao dao = new LabelDao();

	public void listByTeam(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String teamId = qp.get("team_id");
		if (isBlank(teamId)) { Router.badRequest(ex, "Thiếu team_id"); return; }
		List<Label> list = dao.listByTeam(teamId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Label l = dao.getById(id);
		if (l == null) { Router.notFound(ex, "Label không tồn tại"); return; }
		Router.okJson(ex, Json.stringify(l));
	}

	public void create(HttpExchange ex) throws Exception {
		Label body = Json.parse(Router.body(ex), Label.class);
		if (body == null || isBlank(body.team_id) || isBlank(body.name) || isBlank(body.color)) {
			Router.badRequest(ex, "Thiếu dữ liệu"); return;
		}
		body.label_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("label_id", body.label_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Label exist = dao.getById(id);
		if (exist == null) { Router.notFound(ex, "Label không tồn tại"); return; }
		Label body = Json.parse(Router.body(ex), Label.class);
		body.name = coalesce(body.name, exist.name);
		body.color = coalesce(body.color, exist.color);
		boolean ok = dao.update(id, body);
		if (!ok) { Router.notFound(ex, "Label không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) { Router.notFound(ex, "Label không tồn tại"); return; }
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.isBlank(); }
	private static String coalesce(String a, String b) { return (a == null || a.isBlank()) ? b : a; }

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


