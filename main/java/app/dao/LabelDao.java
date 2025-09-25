package app.dao;

import app.core.Db;
import app.models.Label;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LabelDao {
	public List<Label> listByTeam(String teamId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT label_id, team_id, name, color
				FROM labels WHERE team_id = ?
				ORDER BY name ASC
			""")) {
			ps.setString(1, teamId);
			ResultSet rs = ps.executeQuery();
			List<Label> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Label getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT label_id, team_id, name, color
				FROM labels WHERE label_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Label l) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO labels(label_id, team_id, name, color)
				VALUES(?, ?, ?, ?)
			""")) {
			ps.setString(1, l.label_id);
			ps.setString(2, l.team_id);
			ps.setString(3, l.name);
			ps.setString(4, l.color);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Label l) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE labels SET name = ?, color = ?
				WHERE label_id = ?
			""")) {
			ps.setString(1, l.name);
			ps.setString(2, l.color);
			ps.setString(3, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM labels WHERE label_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Label map(ResultSet rs) throws SQLException {
		Label l = new Label();
		l.label_id = rs.getString("label_id");
		l.team_id = rs.getString("team_id");
		l.name = rs.getString("name");
		l.color = rs.getString("color");
		return l;
	}
}


