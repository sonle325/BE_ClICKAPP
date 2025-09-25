package app.dao;

import app.core.Db;
import app.models.ProjectMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectMemberDao {
	public List<ProjectMember> listByProject(String projectId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT project_id, user_id, role_id
				FROM project_members WHERE project_id = ?
			""")) {
			ps.setString(1, projectId);
			ResultSet rs = ps.executeQuery();
			List<ProjectMember> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(ProjectMember pm) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO project_members(project_id, user_id, role_id)
				VALUES(?, ?, ?)
			""")) {
			ps.setString(1, pm.project_id);
			ps.setString(2, pm.user_id);
			ps.setString(3, pm.role_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("Thành viên đã tồn tại trong project");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean updateRole(String projectId, String userId, String roleId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE project_members SET role_id = ?
				WHERE project_id = ? AND user_id = ?
			""")) {
			ps.setString(1, roleId);
			ps.setString(2, projectId);
			ps.setString(3, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String projectId, String userId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM project_members WHERE project_id = ? AND user_id = ?
			""")) {
			ps.setString(1, projectId);
			ps.setString(2, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private ProjectMember map(ResultSet rs) throws SQLException {
		ProjectMember pm = new ProjectMember();
		pm.project_id = rs.getString("project_id");
		pm.user_id = rs.getString("user_id");
		pm.role_id = rs.getString("role_id");
		return pm;
	}
}


