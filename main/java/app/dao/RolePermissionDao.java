package app.dao;

import app.core.Db;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RolePermissionDao {
	public void add(String roleId, String permissionId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO role_permissions(role_id, permission_id) VALUES(?, ?)
			""")) {
			ps.setString(1, roleId);
			ps.setString(2, permissionId);
			ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("Quyền đã tồn tại trong role");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean remove(String roleId, String permissionId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM role_permissions WHERE role_id = ? AND permission_id = ?
			""")) {
			ps.setString(1, roleId);
			ps.setString(2, permissionId);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> listPermissionIdsByRole(String roleId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT permission_id FROM role_permissions WHERE role_id = ?
			""")) {
			ps.setString(1, roleId);
			ResultSet rs = ps.executeQuery();
			List<String> list = new ArrayList<>();
			while (rs.next()) list.add(rs.getString("permission_id"));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}


