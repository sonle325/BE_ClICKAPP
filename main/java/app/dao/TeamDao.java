package app.dao;

import app.core.Db;
import app.models.Team;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamDao {
	public List<Team> list() {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT team_id, name, created_at FROM teams ORDER BY created_at DESC
			""")) {
			ResultSet rs = ps.executeQuery();
			List<Team> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Team getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT team_id, name, created_at FROM teams WHERE team_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Team t) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO teams(team_id, name) VALUES(?, ?)
			""")) {
			ps.setString(1, t.team_id);
			ps.setString(2, t.name);
			ps.executeUpdate();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Team t) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE teams SET name = ? WHERE team_id = ?
			""")) {
			ps.setString(1, t.name);
			ps.setString(2, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM teams WHERE team_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Team map(ResultSet rs) throws SQLException {
		Team t = new Team();
		t.team_id = rs.getString("team_id");
		t.name = rs.getString("name");
		t.created_at = rs.getString("created_at");
		return t;
	}
}


