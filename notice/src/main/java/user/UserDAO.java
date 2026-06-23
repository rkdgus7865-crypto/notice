package user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

	private Connection conn; //  클래스 안에서만 사용 외부에서 직접 접근 못하게 막기
	private PreparedStatement pstmt;
	private ResultSet rs;

	public UserDAO() {
		try {
			// DB 연결 설정
			String dbURL = "jdbc:mysql://localhost:3306/BBS";
			String dbID = "root";
			String dbPassword = "050700";
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(dbURL, dbID, dbPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int login(String userID, String userPassword) { // 로그인 기능 구현
		String SQL = "SELECT userPassword FROM USER WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if (rs.getString(1).equals(userPassword)) {
					return 1; // 로그인 성공
				} else
					return 0; // 비밀번호 불일치
			}
			return -1; // 아이디가 없음
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -2; // 데이터베이스 오류

	} // 회원가입: 1=성공, -1=실패(중복 아이디 등)

	public int join(User user) {
		String SQL = "INSERT INTO USER (userID, userPassword, userName, userGender, userEmail, userAddress, userPhone, userDateOfBirth, userDateOfJoining, userGrade) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		try {
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

			return pstmt.executeUpdate(); // 성공시 1 반환
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // 중복 아이디 또는 DB 오류
	}

	// 로그인시 userGrade도 같이 가져오기
	public String getGrade(String userID) {
		String SQL = "SELECT userGrade FROM USER WHERE userID = ?";
		try {
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, userID);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				return rs.getString("userGrade");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
