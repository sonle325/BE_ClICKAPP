package app.dao;

import app.core.Db;
import app.models.Comment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDao {
	public List<Comment> listByTask(String taskId) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT comment_id, task_id, user_id, body, created_at
				FROM comments WHERE task_id = ?
				ORDER BY created_at ASC
			""")) {
			ps.setString(1, taskId);
			ResultSet rs = ps.executeQuery();
			List<Comment> list = new ArrayList<>();
			while (rs.next()) list.add(map(rs));
			return list;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Comment getById(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				SELECT comment_id, task_id, user_id, body, created_at
				FROM comments WHERE comment_id = ?
			""")) {
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? map(rs) : null;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void create(Comment cm) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				INSERT INTO comments(comment_id, task_id, user_id, body)
				VALUES(?, ?, ?, ?)
			""")) {
			ps.setString(1, cm.comment_id);
			ps.setString(2, cm.task_id);
			ps.setString(3, cm.user_id);
			ps.setString(4, cm.body);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean update(String id, Comment cm) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				UPDATE comments SET body = ?
				WHERE comment_id = ?
			""")) {
			ps.setString(1, cm.body);
			ps.setString(2, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String id) {
		try (Connection c = Db.get();
		     PreparedStatement ps = c.prepareStatement("""
				DELETE FROM comments WHERE comment_id = ?
			""")) {
			ps.setString(1, id);
			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private Comment map(ResultSet rs) throws SQLException {
		Comment c = new Comment();
		c.comment_id = rs.getString("comment_id");
		c.task_id = rs.getString("task_id");
		c.user_id = rs.getString("user_id");
		c.body = rs.getString("body");
		c.created_at = rs.getString("created_at");
		return c;
	}
}


