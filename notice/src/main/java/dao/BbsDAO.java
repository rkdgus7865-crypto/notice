package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.Bbs;



public class BbsDAO {

	private Connection getConnection() throws Exception {
		String dbURL = "jdbc:mysql://localhost:3306/BBS";
		String dbID = "root";
		String dbPassword = "050700";

		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(dbURL, dbID, dbPassword); // MySQL에 접속
	}

	public int getNext() {
		String SQL = "SELECT bbsID FROM BBS ORDER BY bbsID DESC";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) + 1;
			}
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return -1;
	}

	public String getDate() {
		String SQL = "SELECT NOW()";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return "";
	}

	public int write(String bbsTitle, String userID, String bbsContent, int bbsPublic, String bbsgroupName) {
		String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			int nextID = getNext();
			String date = getDate();
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, nextID);
			pstmt.setString(2, bbsTitle);
			pstmt.setString(3, userID);
			pstmt.setString(4, date);
			pstmt.setString(5, bbsContent);
			pstmt.setInt(6, 1);
			pstmt.setInt(7, 0);
			pstmt.setInt(8, 0); 
			pstmt.setInt(9, 0); 
			pstmt.setInt(10, bbsPublic);
			pstmt.setString(11, bbsgroupName);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null);
		}
		return -1;
	}
	 
	/**
	 * 게시판 그룹(groupName)에 해당하는 게시글의 전체 개수를 조회하는 메서드
	 * 
	 */
	
	public int getTotalCount(String groupName) {
	    String SQL = "SELECT COUNT(*) FROM BBS WHERE bbsAvailable = 1 AND groupName = ?"; // 삭제되지 않은(bbsAvailable = 1) 게시글 중 전달받은 게시판 그룹의 게시글 개수를 조회하는 SQL
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, groupName);  // ? 자리에 전달받은 groupName 값 설정
	        rs = pstmt.executeQuery();      // SQL 실행
	        if (rs.next()) { // 조회 결과가 있으면 게시글 개수 반환
	            return rs.getInt(1); // COUNT(*) 결과값
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return 0;
	}
	
	
	public ArrayList<Bbs> getList(int pageNumber, String groupName){
		String SQL = "SELECT * FROM BBS WHERE bbsID < ? AND bbsAvailable = 1 AND groupName = ? ORDER BY bbsID DESC LIMIT 20";
		ArrayList<Bbs> list = new ArrayList<Bbs>();
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, getNext() - (pageNumber - 1) * 20);
			pstmt.setString(2, groupName);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				Bbs bbs = new Bbs();
				bbs.setBbsID(rs.getInt(1));
				bbs.setBbsTitle(rs.getString(2));
				bbs.setUserID(rs.getString(3));
				bbs.setBbsDate(rs.getString(4));
				bbs.setBbsContent(rs.getString(5));
				bbs.setBbsAvailable(rs.getInt(6));
				bbs.setInquiry(rs.getInt(7));
				bbs.setRecommendation(rs.getInt(8)); 
				bbs.setComments(rs.getInt(9)); 
				bbs.setIsPublic(rs.getInt(10));
				list.add(bbs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return list;
	}
	
	/**
	 * 게시글 상세 조회 (수정 화면에 기존 데이터 보여줄 때 필요)
	 */
	
	public Bbs getDetail(int bbsID) {
	    String SQL = "SELECT * FROM BBS WHERE bbsID = ? AND bbsAvailable = 1";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, bbsID);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            Bbs bbs = new Bbs();
	            bbs.setBbsID(rs.getInt("bbsID"));
	            bbs.setBbsTitle(rs.getString("bbsTitle"));
	            bbs.setUserID(rs.getString("userID"));
	            bbs.setBbsDate(rs.getString("bbsDate"));
	            bbs.setBbsContent(rs.getString("bbsContent"));
	            bbs.setBbsAvailable(rs.getInt("bbsAvailable"));
	            bbs.setInquiry(rs.getInt("inquiry"));
	            bbs.setRecommendation(rs.getInt("recommendation"));
	            bbs.setComments(rs.getInt("Comments"));
	            bbs.setIsPublic(rs.getInt("bbsPublic"));
	            return bbs;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return null;
	}
	
	/**
	 * 게시글 수정
	 */
	
	public int update(int bbsID, String bbsTitle, String bbsContent) {
	    String SQL = "UPDATE BBS SET bbsTitle = ?, bbsContent = ? WHERE bbsID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, bbsTitle);
	        pstmt.setString(2, bbsContent);
	        pstmt.setInt(3, bbsID);
	        return pstmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, null);
	    }
	    return -1;
	}
	
	/**
	 * 게시글 삭제
	 */
	
	public int delete(int bbsID) {
	    String SQL = "UPDATE BBS SET bbsAvailable = 0 WHERE bbsID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, bbsID);
	        return pstmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, null);
	    }
	    return -1;
	}

	private void close(Connection conn, PreparedStatement pstmt, ResultSet rs) {
		try {
			if (rs != null) rs.close();
			if (pstmt != null) pstmt.close();
			if (conn != null) conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}