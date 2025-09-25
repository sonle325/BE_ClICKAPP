package app.dao;

import app.core.Db;
import app.models.Project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectDao {
	public List<Project> listByTeam(String teamId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT project_id, team_id, name, description, created_at
				FROM projects WHERE team_id = ?
				ORDER BY created_at DESC
			""")) {
			ps.setString(1, teamId);
			ResultSet rs = ps.executeQuery();
			List<Project> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Project getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT project_id, team_id, name, description, created_at
				FROM projects WHERE project_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Project p) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO projects(project_id, team_id, name, description)
				VALUES(?, ?, ?, ?)
			""")) {
			ps.setString(1, p.project_id);
			ps.setString(2, p.team_id);
			ps.setString(3, p.name);
			ps.setString(4, p.description);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Project p) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE projects SET name = ?, description = ?
				WHERE project_id = ?
			""")) {
			ps.setString(1, p.name);
			ps.setString(2, p.description);
			ps.setString(3, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM projects WHERE project_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Project map(ResultSet rs) throws SQLException {
		Project p = new Project();
		p.project_id = rs.getString("project_id");
		p.team_id = rs.getString("team_id");
		p.name = rs.getString("name");
		p.description = rs.getString("description");
		p.created_at = rs.getString("created_at");
		return p;
	}
}


