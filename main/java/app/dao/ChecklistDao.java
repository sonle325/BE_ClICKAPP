package app.dao;

import app.core.Db;
import app.models.Checklist;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChecklistDao {
	public List<Checklist> listByTask(String taskId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT checklist_id, task_id, title
				FROM checklists WHERE task_id = ?
				ORDER BY title ASC
			""")) {
			ps.setString(1, taskId);
			ResultSet rs = ps.executeQuery();
			List<Checklist> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Checklist getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT checklist_id, task_id, title
				FROM checklists WHERE checklist_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Checklist cl) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO checklists(checklist_id, task_id, title)
				VALUES(?, ?, ?)
			""")) {
			ps.setString(1, cl.checklist_id);
			ps.setString(2, cl.task_id);
			ps.setString(3, cl.title);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Checklist cl) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE checklists SET title = ?
				WHERE checklist_id = ?
			""")) {
			ps.setString(1, cl.title);
			ps.setString(2, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM checklists WHERE checklist_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Checklist map(ResultSet rs) throws SQLException {
		Checklist c = new Checklist();
		c.checklist_id = rs.getString("checklist_id");
		c.task_id = rs.getString("task_id");
		c.title = rs.getString("title");
		return c;
	}
}