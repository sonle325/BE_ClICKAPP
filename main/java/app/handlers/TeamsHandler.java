package app.handlers;

import app.core.Json;
import app.core.Router;
import app.dao.TeamDao;
import app.models.Team;
import app.util.Ids;
import com.sun.net.httpserver.HttpExchange;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeamsHandler {
	private final TeamDao dao = new TeamDao();

	public void list(HttpExchange ex) throws Exception {
		List<Team> teams = dao.list();
		Router.okJson(ex, Json.stringify(teams));
	}

	public void getById(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Team t = dao.getById(id);
		if (t == null) {
			Router.notFound(ex, "Team không tồn tại");
			return;
		}
		Router.okJson(ex, Json.stringify(t));
	}

	public void create(HttpExchange ex) throws Exception {
		Team body = Json.parse(Router.body(ex), Team.class);
		if (body == null || body.name == null || body.name.isBlank()) {
			Router.badRequest(ex, "Thiếu tên");
			return;
		}
		body.team_id = Ids.newId();
		dao.create(body);
		Map<String, Object> resp = new HashMap<>();
		resp.put("team_id", body.team_id);
		Router.created(ex, Json.stringify(resp));
	}

	public void update(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		Team existing = dao.getById(id);
		if (existing == null) {
			Router.notFound(ex, "Team không tồn tại");
			return;
		}
		Team body = Json.parse(Router.body(ex), Team.class);
		if (body == null || body.name == null || body.name.isBlank()) {
			Router.badRequest(ex, "Thiếu tên");
			return;
		}
		boolean ok = dao.update(id, body);
		if (!ok) {
			Router.notFound(ex, "Team không tồn tại");
			return;
		}
		Router.noContent(ex);
	}

	public void delete(HttpExchange ex) throws Exception {
		String id = Router.pathParams(ex).get("id");
		boolean ok = dao.delete(id);
		if (!ok) {
			Router.notFound(ex, "Team không tồn tại");
			return;
		}
		Router.noContent(ex);
	}
}


