package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.ProjectMemberDao;
import app.dao.RoleDao;
import app.models.ProjectMember;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectMembersHandler {
	private final ProjectMemberDao dao = new ProjectMemberDao();
	private final RoleDao roleDao = new RoleDao();

	public void listByProject(HttpExchange ex) throws Exception {
		Map<String, String> qp = parseQuery(ex.getRequestURI().getRawQuery());
		String projectId = qp.get("project_id");
		if (isBlank(projectId)) { Router.badRequest(ex, "Thiếu project_id"); return; }
		List<ProjectMember> list = dao.listByProject(projectId);
		Router.okJson(ex, Json.stringify(list));
	}

	public void add(HttpExchange ex) throws Exception {
		ProjectMember body = Json.parse(Router.body(ex), ProjectMember.class);
		if (body == null || isBlank(body.project_id) || isBlank(body.user_id) || isBlank(body.role_id)) {
			Router.badRequest(ex, "Thiếu dữ liệu"); return;
		}
		if (!roleDao.exists(body.role_id)) { Router.badRequest(ex, "role_id không hợp lệ"); return; }
		dao.add(body);
		Router.created(ex, Json.stringify(Map.of("ok", true)));
	}

	public void updateRole(HttpExchange ex) throws Exception {
		Map<String, String> params = Router.pathParams(ex);
		String projectId = params.get("project_id");
		String userId = params.get("user_id");
		ProjectMember body = Json.parse(Router.body(ex), ProjectMember.class);
		if (body == null || isBlank(body.role_id)) { Router.badRequest(ex, "Thiếu role_id"); return; }
		if (!roleDao.exists(body.role_id)) { Router.badRequest(ex, "role_id không hợp lệ"); return; }
		boolean ok = dao.updateRole(projectId, userId, body.role_id);
		if (!ok) { Router.notFound(ex, "Thành viên không tồn tại"); return; }
		Router.noContent(ex);
	}

	public void remove(HttpExchange ex) throws Exception {
		Map<String, String> params = Router.pathParams(ex);
		boolean ok = dao.remove(params.get("project_id"), params.get("user_id"));
		if (!ok) { Router.notFound(ex, "Thành viên không tồn tại"); return; }
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


