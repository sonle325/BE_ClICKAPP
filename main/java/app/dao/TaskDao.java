package app.dao;

import app.core.Db;
import app.models.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskDao {
	public List<Task> listByProject(String projectId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT task_id, project_id, title, description, status, priority, due_date, created_by, created_at
				FROM tasks WHERE project_id = ?
				ORDER BY created_at DESC
			""")) {
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			List<Task> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Task getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT task_id, project_id, title, description, status, priority, due_date, created_by, created_at
				FROM tasks WHERE task_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Task t) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO tasks(task_id, project_id, title, description, status, priority, due_date, created_by)
				VALUES(?, ?, ?, ?, ?, ?, ?, ?)
			""")) {
			ps.setString(1, t.task_id);
			ps.setString(2, t.project_id);
			ps.setString(3, t.title);
			ps.setString(4, t.description);
			ps.setString(5, t.status);
			ps.setString(6, t.priority);
			ps.setString(7, t.due_date);
			ps.setString(8, t.created_by);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Task t) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE tasks SET title = ?, description = ?, status = ?, priority = ?, due_date = ?
				WHERE task_id = ?
			""")) {
			ps.setString(1, t.title);
			ps.setString(2, t.description);
			ps.setString(3, t.status);
			ps.setString(4, t.priority);
			ps.setString(5, t.due_date);
			ps.setString(6, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM tasks WHERE task_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Task map(ResultSet rs) throws SQLException {
		Task t = new Task();
		t.task_id = rs.getString("task_id");
		t.project_id = rs.getString("project_id");
		t.title = rs.getString("title");
		t.description = rs.getString("description");
		t.status = rs.getString("status");
		t.priority = rs.getString("priority");
		t.due_date = rs.getString("due_date");
		t.created_by = rs.getString("created_by");
		t.created_at = rs.getString("created_at");
		return t;
	}
}


