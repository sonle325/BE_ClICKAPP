package app.dao;

import app.core.Db;
import app.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {

	public List<User> list() {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT user_id, full_name, email, password_hash, created_at
				FROM users ORDER BY created_at DESC
			""")) {
			ResultSet rs = ps.executeQuery();
			List<User> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public User getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT user_id, full_name, email, password_hash, created_at
				FROM users WHERE user_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public User getByEmail(String email) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT user_id, full_name, email, password_hash, created_at
				FROM users WHERE email = ?
			""")) {
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void create(User u) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO users(user_id, full_name, email, password_hash)
				VALUES(?, ?, ?, ?)
			""")) {
			ps.setString(1, u.user_id);
			ps.setString(2, u.full_name);
			ps.setString(3, u.email);
			ps.setString(4, u.password_hash);
			ps.executeUpdate();
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("Email đã tồn tại");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, User u) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE users SET full_name = ?, email = ?, password_hash = ?
				WHERE user_id = ?
			""")) {
			ps.setString(1, u.full_name);
			ps.setString(2, u.email);
			ps.setString(3, u.password_hash);
			ps.setString(4, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			if (e.getMessage() != null && e.getMessage().contains("UNIQUE")) {
				throw new RuntimeException("Email đã tồn tại");
			}
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM users WHERE user_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private User map(ResultSet rs) throws SQLException {
		User u = new User();
		u.user_id = rs.getString("user_id");
		u.full_name = rs.getString("full_name");
		u.email = rs.getString("email");
		u.password_hash = rs.getString("password_hash");
		u.created_at = rs.getString("created_at");
		return u;
	}
}


