package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.ChecklistItemDao;
import app.models.ChecklistItem;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecklistItemsHandler {
	private final ChecklistItemDao dao = new ChecklistItemDao();

	public void listByChecklist(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String checklistId = qp.get("checklist_id");
		if (isBlank(checklistId)) { Router.badRequest(ex, "Thiếu checklist_id"); return; }
		List<ChecklistItem> list = dao.listByChecklist(checklistId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		ChecklistItem it = dao.getById(id);
		if (it == null) { Router.notFound(ex, "Item không tồn tại"); return; }
		Router.okJson(ex, Json.stringify(it));
	}

	public void create(HttpExchange ex) throws Exception {
		ChecklistItem body = Json.parse(Router.body(ex), ChecklistItem.class);
		if (body == null || isBlank(body.checklist_id) || isBlank(body.content)) {
			Router.badRequest(ex, "Thiếu dữ liệu"); return;
		}
		body.item_id = Ids.newId();
		boolean initialDone = body.is_done;
		body.is_done = initialDone;
		dao.create(body);
		Router.created(ex, Json.stringify(Map.of("item_id", body.item_id)));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		ChecklistItem exist = dao.getById(id);
		if (exist == null) { Router.notFound(ex, "Item không tồn tại"); return; }
		ChecklistItem body = Json.parse(Router.body(ex), ChecklistItem.class);
		exist.content = coalesce(body.content, exist.content);
		exist.is_done = body != null ? body.is_done : exist.is_done;
		boolean ok = dao.update(id, exist);
		if (!ok) { Router.notFound(ex, "Item không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) { Router.notFound(ex, "Item không tồn tại"); return; }
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


