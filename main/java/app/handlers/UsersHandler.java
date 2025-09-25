package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.UserDao;
import app.models.User;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsersHandler {
	private final UserDao dao = new UserDao();

	public void list(HttpExchange ex) throws Exception {
		List<User> users = dao.list();
		Router.okJson(ex, Json.stringify(users));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		User u = dao.getById(id);
		if (u == null) {
			Router.notFound(ex, "User không tồn tại");
			return;
		}
		Router.okJson(ex, Json.stringify(u));
	}

	public void create(HttpExchange ex) throws Exception {
		User body = Json.parse(Router.body(ex), User.class);
		if (body == null || isBlank(body.full_name) || isBlank(body.email) || isBlank(body.password_hash)) {
			Router.badRequest(ex, "Thiếu dữ liệu");
			return;
		}
		if (dao.getByEmail(body.email) != null) {
			Router.badRequest(ex, "Email đã tồn tại");
			return;
		}
		body.user_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("user_id", body.user_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		User existing = dao.getById(id);
		if (existing == null) {
			Router.notFound(ex, "User không tồn tại");
			return;
		}
		User body = Json.parse(Router.body(ex), User.class);
		if (body == null) {
			Router.badRequest(ex, "Body không hợp lệ");
			return;
		}
		body.full_name = coalesce(body.full_name, existing.full_name);
		body.email = coalesce(body.email, existing.email);
		body.password_hash = coalesce(body.password_hash, existing.password_hash);

		boolean ok = dao.update(id, body);
		if (!ok) {
			Router.notFound(ex, "User không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) {
			Router.notFound(ex, "User không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	private static boolean isBlank(String s) { return s == null || s.trim().isEmpty(); }
	private static String coalesce(String a, String b) { return (a == null || a.isBlank()) ? b : a; }
}


