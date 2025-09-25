package app.dao;

import app.core.Db;
import app.models.ChecklistItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChecklistItemDao {
	public List<ChecklistItem> listByChecklist(String checklistId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT item_id, checklist_id, content, is_done
				FROM checklist_items WHERE checklist_id = ?
				ORDER BY item_id ASC
			""")) {
			ps.setString(1, checklistId);
			ResultSet rs = ps.executeQuery();
			List<ChecklistItem> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public ChecklistItem getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT item_id, checklist_id, content, is_done
				FROM checklist_items WHERE item_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(ChecklistItem it) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO checklist_items(item_id, checklist_id, content, is_done)
				VALUES(?, ?, ?, ?)
			""")) {
			ps.setString(1, it.item_id);
			ps.setString(2, it.checklist_id);
			ps.setString(3, it.content);
			ps.setBoolean(4, it.is_done);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, ChecklistItem it) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE checklist_items SET content = ?, is_done = ?
				WHERE item_id = ?
			""")) {
			ps.setString(1, it.content);
			ps.setBoolean(2, it.is_done);
			ps.setString(3, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM checklist_items WHERE item_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private ChecklistItem map(ResultSet rs) throws SQLException {
		ChecklistItem i = new ChecklistItem();
		i.item_id = rs.getString("item_id");
		i.checklist_id = rs.getString("checklist_id");
		i.content = rs.getString("content");
		i.is_done = rs.getBoolean("is_done");
		return i;
	}
}


