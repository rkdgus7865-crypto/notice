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
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();

	        // 이 게시글에서 현재 가장 큰 commentOrder 값을 조회 (없으면 0)
	        String maxOrderSQL = "SELECT IFNULL(MAX(commentOrder), 0) FROM Comment WHERE bbsID = ?";
	        pstmt1 = conn.prepareStatement(maxOrderSQL);
	        pstmt1.setInt(1, bbsID);
	        rs = pstmt1.executeQuery();
	        int maxOrder = 0;
	        if (rs.next()) {
	            maxOrder = rs.getInt(1);
	        }

	        // 원댓글은 parentCommentID 없이(NULL), commentStep=0, 맨 마지막 순서로 삽입
	        String SQL = "INSERT INTO Comment (bbsID, userID, commentContent, secretComment, commentStep, commentOrder) "
	                   + "VALUES (?, ?, ?, ?, 0, ?)";
	        pstmt2 = conn.prepareStatement(SQL);
	        pstmt2.setInt(1, bbsID);
	        pstmt2.setString(2, userID);
	        pstmt2.setString(3, commentContent);
	        pstmt2.setInt(4, secretComment);
	        pstmt2.setInt(5, maxOrder + 1);
	        return pstmt2.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } finally {
	        close(null, pstmt1, rs);
	        close(conn, pstmt2, null);
	    }
	}

	/**
	 *  게시글별 댓글 목록 조회
	 */
	
	public ArrayList<Comment> getList(int bbsID, int pageNumber) {
//	    String SQL = "SELECT * FROM Comment WHERE bbsID = ? AND commentAvailable = 1 "
//	        + "ORDER BY commentID ASC LIMIT 20 OFFSET ?";
		String SQL = "SELECT * FROM Comment WHERE bbsID = ? AND commentAvailable = 1 "
		    + "ORDER BY commentOrder ASC LIMIT 20 OFFSET ?"; 
//		String SQL = "SELECT * FROM Comment WHERE bbsID = ? AND commentAvailable = 1 "
//	        + "ORDER BY commentOrder DESC LIMIT 20 OFFSET ?";
	    ArrayList<Comment> list = new ArrayList<Comment>();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        int offset = (pageNumber - 1) * 20;
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, bbsID);
	        pstmt.setInt(2, offset);
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
	            comment.setParentCommentID(rs.getInt("parentCommentID"));  // 부모 댓글 ID (원댓글이면 0 또는 null)
	            comment.setCommentStep(rs.getInt("commentStep"));          // 들여쓰기 단계 (0=원댓글, 1=대댓글...)
	            comment.setCommentOrder(rs.getInt("commentOrder"));        // 화면 표시 순서

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
	        return rs.next(); //DB 조회 결과에 데이터가 있으면 true, 없으면 false를 반환
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
	
	/**
	 * 댓글 추천 취소
	 */
	
	public int cancelRecommend(int commentID, String userID) {
	    String deleteSQL = "DELETE FROM CommentRecommend WHERE commentID = ? AND userID = ?";
	    String updateSQL = "UPDATE Comment SET recommendCount = recommendCount - 1 WHERE commentID = ? AND recommendCount > 0";
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    try {
	        conn = getConnection();
	        pstmt1 = conn.prepareStatement(deleteSQL);
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
	        close(null, pstmt1, null);
	        close(conn, pstmt2, null);
	    }
	}
	
	/**
	 * 대댓글 작성 parentCommentID: 이 댓글이 답글로 달리는 대상 댓글의 ID
	 */
	
	public int writeReply(int bbsID, String userID, String commentContent, int secretComment, int parentCommentID) {
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    PreparedStatement pstmt3 = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();

	        // 1. 부모 댓글의 현재 정렬순서(commentOrder), 들여쓰기단계(commentStep) 조회
	        String selectParentSQL = "SELECT commentOrder, commentStep FROM Comment WHERE commentID = ?";
	        pstmt1 = conn.prepareStatement(selectParentSQL);
	        pstmt1.setInt(1, parentCommentID);
	        rs = pstmt1.executeQuery();

	        int parentOrder = 0;
	        int parentStep = 0;
	        if (rs.next()) {
	            parentOrder = rs.getInt("commentOrder");
	            parentStep = rs.getInt("commentStep");
	        }

	        // 2. 부모보다 순서가 뒤인 댓글들을 한 칸씩 뒤로 밀기
	        //    (새 대댓글이 부모 바로 다음 자리에 들어갈 공간을 만듦)
	        String shiftSQL = "UPDATE Comment SET commentOrder = commentOrder + 1 "
	                         + "WHERE bbsID = ? AND commentOrder > ?";
	        pstmt2 = conn.prepareStatement(shiftSQL);
	        pstmt2.setInt(1, bbsID);
	        pstmt2.setInt(2, parentOrder);
	        pstmt2.executeUpdate();

	        // 3. 새 대댓글 삽입 (부모 바로 다음 순서, 부모보다 들여쓰기 한 단계 깊게)
	        String insertSQL = "INSERT INTO Comment "
	                          + "(bbsID, userID, commentContent, secretComment, parentCommentID, commentStep, commentOrder) "
	                          + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	        pstmt3 = conn.prepareStatement(insertSQL);
	        pstmt3.setInt(1, bbsID);
	        pstmt3.setString(2, userID);
	        pstmt3.setString(3, commentContent);
	        pstmt3.setInt(4, secretComment);
	        pstmt3.setInt(5, parentCommentID);
	        pstmt3.setInt(6, parentStep + 1);
	        pstmt3.setInt(7, parentOrder + 1);
	        return pstmt3.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    } finally {
	        close(null, pstmt1, rs);
	        close(null, pstmt2, null);
	        close(conn, pstmt3, null);
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