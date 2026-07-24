package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import dto.User;

public class UserDAO {
	
	/**
	 * 
	 * MySql DB 연결
	 * 
	 */

	private Connection getConnection() throws Exception {
		String dbURL = "jdbc:mysql://localhost:3306/BBS";
		String dbID = "root";
		String dbPassword = "050700";
		Class.forName("com.mysql.cj.jdbc.Driver");
		return DriverManager.getConnection(dbURL, dbID, dbPassword);
	}

	/**
	 * 
	 * 로그인 
	 * 
	 */
	
	public int login(String userID, String userPassword) {
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
			return -1; // 아이디 없음
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return -2; // DB 오류
	}

	/**
	 * 
	 * 회원가입
	 * 
	 */
	
	public int join(User user) {
		String SQL = "INSERT INTO USER " + "(userID, userPassword, userName, userGender, userEmail, "
				+ "userAddress, userPhone, userDateOfBirth, userDateOfJoining, userGrade) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		
		String joinDate = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
		
		Connection conn = null;
		PreparedStatement pstmt = null;
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
		    pstmt.setString(9, joinDate); // 사용자 입력값 받지 않고 joinDate로  처리 
			pstmt.setString(10, user.getUserGrade());
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, null); 
		}
		return -1;
	}

	/**
	 * 
	 *  회원 등급 조회
	 * 
	 */
	
	public String getGrade(String userID) {
		String SQL = "SELECT userGrade FROM USER WHERE userID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
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
			close(conn, pstmt, rs);
		}
		return null;
	}
	
	/**
	 * 	
	 *  로그인 후 상단에 이름 표시 메서드
	 * 
	 */
	
	public String getName(String userID) {
	    String SQL = "SELECT userName FROM USER WHERE userID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, userID);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getString("userName");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return null;
	}
	
	/**
	 * 	
	 * 아이디,이름 일치 확인 후 임시비밀번호 발급
	 * 
	 */
	
	public String findPasswordAndReset(String userID, String userName) {
	    String SQL = "SELECT * FROM USER WHERE userID = ? AND userName = ?";
	    
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, userID);
	        pstmt.setString(2, userName);
	        rs = pstmt.executeQuery();

	        if (rs.next()) {
	            // 일치하면 임시비밀번호 생성
	            String tempPassword = generateTempPassword();

	            close(conn, pstmt, rs);

	            // 임시비밀번호로 업데이트 + isTempPassword = 1
	            String updateSQL = "UPDATE USER SET userPassword = ?, isTempPassword = 1 WHERE userID = ?";
	            conn = getConnection();
	            pstmt = conn.prepareStatement(updateSQL);
	            pstmt.setString(1, tempPassword);
	            pstmt.setString(2, userID);
	            pstmt.executeUpdate();

	            return tempPassword;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return null; // 일치하는 회원 없음
	}

	/**
	 * 	
	 * 임시비밀번호 랜덤 생성 (8자리)
	 * 
	 */
	
	private String generateTempPassword() {
		
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"; // 비밀번호에 사용할 수 있는 문자들 (대문자+소문자+숫자)
	    StringBuilder sb = new StringBuilder(); 
	    java.util.Random random = new java.util.Random(); // 랜덤 값을 뽑아줌
	    
	    for (int i = 0; i < 8; i++) { // 8번 반복하면서 글자 하나씩 랜덤으로 뽑아 붙이기
	        sb.append(chars.charAt(random.nextInt(chars.length()))); // chars.length() = 62개  -> random.nextInt(62)  0~61 사이의 랜덤 숫자 하나 뽑음 -> chars.charAt(그 숫자) → 그 위치에 있는 문자 하나 꺼냄
	    }
	    return sb.toString(); //  완성된 8자리 임시비밀번호 문자열 반환
	}
	
	/**
	 * 	
	 * 새 비밀번호로 변경 + isTempPassword = 0
	 * 
	 */
	
	public int changePassword(String userID, String newPassword) {
	    String SQL = "UPDATE USER SET userPassword = ?, isTempPassword = 0 WHERE userID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, newPassword);
	        pstmt.setString(2, userID);
	        
	        return pstmt.executeUpdate(); 
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, null);
	    }
	    return -1;
	}

	/**
	 * 	
	 *  임시비밀번호 상태 확인 (로그인 후 체크용)
	 * 
	 */
	
	public boolean isTempPassword(String userID) {
	    String SQL = "SELECT isTempPassword FROM USER WHERE userID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, userID);
	        rs = pstmt.executeQuery();
	        if (rs.next()) {
	            return rs.getInt("isTempPassword") == 1;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return false;
	}
	
	/**
	 * 	
	 *  자원 해제 메서드
	 * 
	 */
	 
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