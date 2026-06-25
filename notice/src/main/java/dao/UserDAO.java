package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import dto.User;

public class UserDAO {

	private Connection conn; // 자바 DB 연결 통로
	private PreparedStatement pstmt; // SQL 문장을 DB 보냄
	private ResultSet rs; // SELECT문 실행 결과

	// DB 연결 메서드
	private Connection getConnection() throws Exception {
		String dbURL = "jdbc:mysql://localhost:3306/BBS";
		String dbID = "root";
		String dbPassword = "050700";

		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(dbURL, dbID, dbPassword); //MySQL에 접속
	}

	// 로그인
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				if (rs.getString(1).equals(userPassword)) {
					return 1; // 로그인 성공
				} else {
					return 0; // 비밀번호 불일치
				}
			}
			return -1; // 아이디가 없음

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -2; // 데이터베이스 오류
	}
	
	// 회원가입
	public int join(User user) {
		String SQL = "INSERT INTO USER " + "(userID, userPassword, userName, userGender, userEmail, "
				+ "userAddress, userPhone, userDateOfBirth, userDateOfJoining, userGrade) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, user.getUserID());
			pstmt.setString(2, user.getUserPassword());
			pstmt.setString(3, user.getUserName());
			pstmt.setString(4, user.getUserGender());
			pstmt.setString(5, user.getUserEmail());
			pstmt.setString(6, user.getUserAddress());
			pstmt.setString(7, user.getUserPhone());
			pstmt.setString(8, user.getUserDateOfBirth());
			pstmt.setString(9, user.getUserDateOfJoining());
			pstmt.setString(10, user.getUserGrade());

			return pstmt.executeUpdate(); // 성공 시 1 반환

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return -1; // 중복 아이디 또는 DB 오류
	}

	// 회원 등급 조회
	public String getGrade(String userID) {
		String SQL = "SELECT userGrade FROM USER WHERE userID = ?";
		try {
			conn = getConnection(); 
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getString("userGrade");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		return null;
	}

	// 자원 해제 메서드
	public void close() {
	    try {
	        if (rs != null) rs.close();
	        if (pstmt != null) pstmt.close();
	        if (conn != null) conn.close();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}