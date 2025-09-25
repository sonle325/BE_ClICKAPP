package app.dao;

import app.core.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaskLabelDao {
	public void add(String taskId, String labelId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO task_labels(task_id, label_id) VALUES(?, ?)
			""")) {
			ps.setString(1, taskId);
			ps.setString(2, labelId);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String taskId, String labelId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM task_labels WHERE task_id = ? AND label_id = ?
			""")) {
			ps.setString(1, taskId);
			ps.setString(2, labelId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> listLabelIdsByTask(String taskId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT label_id FROM task_labels WHERE task_id = ?
			""")) {
			ps.setString(1, taskId);
			ResultSet rs = ps.executeQuery();
			List<String> list = new ArrayList<>();
			while (rs.next()) list.add(rs.getString("label_id"));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}


