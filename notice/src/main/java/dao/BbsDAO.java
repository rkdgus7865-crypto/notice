package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BbsDAO {


	// DB 연결 메서드
	private Connection getConnection() throws Exception {
		String dbURL = "jdbc:mysql://localhost:3306/BBS";
		String dbID = "root";
		String dbPassword = "050700";

		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(dbURL, dbID, dbPassword); //MySQL에 접속
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
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
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
	        try {
	            if (rs != null) rs.close();
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return "";
	}

	public int write(String bbsTitle, String userID, String bbsContent) {
	    String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?)";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    try {
	        int nextID = getNext();   // 각자 독립적으로 conn 사용
	        String date = getDate();  // 각자 독립적으로 conn 사용

	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, nextID);
	        pstmt.setString(2, bbsTitle);
	        pstmt.setString(3, userID);
	        pstmt.setString(4, date);
	        pstmt.setString(5, bbsContent);
	        pstmt.setInt(6, 1);
	        return pstmt.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (pstmt != null) pstmt.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return -1;
	}
	
	// 자원 해제 메서드
	private void close() {
	    try {
	    	 Connection conn = null;
	 	    PreparedStatement pstmt = null;
	 	    ResultSet rs = null;
	        if (rs != null) rs.close();
	        if (pstmt != null) pstmt.close();
	        if (conn != null) conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}
