package app.dao;

import app.core.Db;
import app.models.TaskAssignee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskAssigneeDao {
	public List<TaskAssignee> listByTask(String taskId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT task_id, user_id FROM task_assignees WHERE task_id = ?
			""")) {
			ps.setString(1, taskId);
			ResultSet rs = ps.executeQuery();
			List<TaskAssignee> list = new ArrayList<>();
			while (rs.next()) {
				TaskAssignee ta = new TaskAssignee();
				ta.task_id = rs.getString("task_id");
				ta.user_id = rs.getString("user_id");
				list.add(ta);
			}
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(String taskId, String userId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO task_assignees(task_id, user_id) VALUES(?, ?)
			""")) {
			ps.setString(1, taskId);
			ps.setString(2, userId);
			ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("User đã được assign vào task");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String taskId, String userId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM task_assignees WHERE task_id = ? AND user_id = ?
			""")) {
			ps.setString(1, taskId);
			ps.setString(2, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}


