package app.dao;

import app.core.Db;
import app.models.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDao {
	public List<Role> list() {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT role_id, name, description FROM roles ORDER BY name ASC
			""")) {
			ResultSet rs = ps.executeQuery();
			List<Role> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Role getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT role_id, name, description FROM roles WHERE role_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exists(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT 1 FROM roles WHERE role_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Role r) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO roles(role_id, name, description) VALUES(?, ?, ?)
			""")) {
			ps.setString(1, r.role_id);
			ps.setString(2, r.name);
			ps.setString(3, r.description);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Role r) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE roles SET name = ?, description = ? WHERE role_id = ?
			""")) {
			ps.setString(1, r.name);
			ps.setString(2, r.description);
			ps.setString(3, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM roles WHERE role_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Role map(ResultSet rs) throws SQLException {
		Role r = new Role();
		r.role_id = rs.getString("role_id");
		r.name = rs.getString("name");
		r.description = rs.getString("description");
		return r;
	}
}


