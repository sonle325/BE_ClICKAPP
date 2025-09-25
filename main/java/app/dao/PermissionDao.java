package app.dao;

import app.core.Db;
import app.models.Permission;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PermissionDao {
	public List<Permission> list() {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT permission_id, name, description FROM permissions ORDER BY name ASC
			""")) {
			ResultSet rs = ps.executeQuery();
			List<Permission> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Permission getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT permission_id, name, description FROM permissions WHERE permission_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Permission p) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO permissions(permission_id, name, description) VALUES(?, ?, ?)
			""")) {
			ps.setString(1, p.permission_id);
			ps.setString(2, p.name);
			ps.setString(3, p.description);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Permission p) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE permissions SET name = ?, description = ? WHERE permission_id = ?
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
				DELETE FROM permissions WHERE permission_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Permission map(ResultSet rs) throws SQLException {
		Permission p = new Permission();
		p.permission_id = rs.getString("permission_id");
		p.name = rs.getString("name");
		p.description = rs.getString("description");
		return p;
	}
}


