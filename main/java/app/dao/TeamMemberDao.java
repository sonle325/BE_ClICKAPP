package app.dao;

import app.core.Db;
import app.models.TeamMember;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TeamMemberDao {
	public List<TeamMember> listByTeam(String teamId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT team_id, user_id, role_id
				FROM team_members WHERE team_id = ?
			""")) {
			ps.setString(1, teamId);
			ResultSet rs = ps.executeQuery();
			List<TeamMember> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void add(TeamMember tm) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO team_members(team_id, user_id, role_id)
				VALUES(?, ?, ?)
			""")) {
			ps.setString(1, tm.team_id);
			ps.setString(2, tm.user_id);
			ps.setString(3, tm.role_id);
			ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("Thành viên đã tồn tại trong team");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean updateRole(String teamId, String userId, String roleId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE team_members SET role_id = ?
				WHERE team_id = ? AND user_id = ?
			""")) {
			ps.setString(1, roleId);
			ps.setString(2, teamId);
			ps.setString(3, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String teamId, String userId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM team_members WHERE team_id = ? AND user_id = ?
			""")) {
			ps.setString(1, teamId);
			ps.setString(2, userId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private TeamMember map(ResultSet rs) throws SQLException {
		TeamMember tm = new TeamMember();
		tm.team_id = rs.getString("team_id");
		tm.user_id = rs.getString("user_id");
		tm.role_id = rs.getString("role_id");
		return tm;
	}
}


