package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import dto.Comment;

public class CommentDAO {

	private Connection getConnection() throws Exception {
		String dbURL = "jdbc:mysql://localhost:3306/BBS";
		String dbID = "root";
		String dbPassword = "050700";
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(dbURL, dbID, dbPassword);
	}

	/**
	 *  댓글 등록
	 */
	
	public int write(int bbsID, String userID, String commentContent, int secretComment) {
		String SQL = "INSERT INTO Comment (bbsID, userID, commentContent, secretComment) VALUES (?, ?, ?, ?)";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			pstmt.setString(2, userID);
			pstmt.setString(3, commentContent);
			pstmt.setInt(4, secretComment);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, null);
		}
	}

	/**
	 *  게시글별 댓글 목록 조회
	 */
	
	public ArrayList<Comment> getList(int bbsID) {
		String SQL = "SELECT * FROM Comment WHERE bbsID = ? AND commentAvailable = 1 ORDER BY commentID ASC";
		ArrayList<Comment> list = new ArrayList<Comment>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Comment comment = new Comment();
				comment.setCommentID(rs.getInt("commentID"));
				comment.setBbsID(rs.getInt("bbsID"));
				comment.setUserID(rs.getString("userID"));
				comment.setCommentContent(rs.getString("commentContent"));
				comment.setCommentDate(rs.getString("commentDate"));
				comment.setCommentUpdateDate(rs.getString("commentUpdateDate"));
				comment.setCommentAvailable(rs.getInt("commentAvailable"));
				comment.setSecretComment(rs.getInt("secretComment"));
				comment.setRecommendCount(rs.getInt("recommendCount"));
				list.add(comment);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}

	/**
	 *  댓글 수정 (본인 확인 포함)
	 */
	
	public int update(int commentID, String userID, String commentContent) {
		String SQL = "UPDATE Comment SET commentContent = ?, commentUpdateDate = NOW() WHERE commentID = ? AND userID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, commentContent);
			pstmt.setInt(2, commentID);
			pstmt.setString(3, userID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, null);
		}
	}

	/**
	 *  댓글 삭제 (소프트 삭제, 본인 확인 포함)
	 */
	
	public int delete(int commentID, String userID) {
		String SQL = "UPDATE Comment SET commentAvailable = 0 WHERE commentID = ? AND userID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, commentID);
			pstmt.setString(2, userID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, null);
		}
	}

	/**
	 *  게시글의 댓글 수 카운트
	 */
	
	public int getCommentCount(int bbsID) {
		String SQL = "SELECT COUNT(*) FROM Comment WHERE bbsID = ? AND commentAvailable = 1";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			rs = pstmt.executeQuery();
			if (rs.next())
				return rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return 0;
	}
	
	/**
	 * 댓글 추천 중복 확인 7-9
	 */
	
	public boolean hasRecommended(int commentID, String userID) {
	    String SQL = "SELECT * FROM CommentRecommend WHERE commentID = ? AND userID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, commentID);
	        pstmt.setString(2, userID);
	        rs = pstmt.executeQuery();
	        return rs.next();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return false;
	}

	/**
	 * 댓글 추천 7-9
	 */
	
	public int recommend(int commentID, String userID) {
	    String insertSQL = "INSERT INTO CommentRecommend (commentID, userID) VALUES (?, ?)";
	    String updateSQL = "UPDATE Comment SET recommendCount = recommendCount + 1 WHERE commentID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    try {
	        conn = getConnection();
	        pstmt1 = conn.prepareStatement(insertSQL);
	        pstmt1.setInt(1, commentID);
	        pstmt1.setString(2, userID);
	        pstmt1.executeUpdate();

	        pstmt2 = conn.prepareStatement(updateSQL);
	        pstmt2.setInt(1, commentID);
	        return pstmt2.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } finally {
	        close(null, pstmt1, null);   // PreparedStatement가 2개라서 한 번에 못 닫으니 두 번 나눠서 호출
	        close(conn, pstmt2, null);   
	    }
	}

	private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null)
				rs.close();
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}