package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.Bbs;

public class BbsDAO {
	
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
		return DriverManager.getConnection(dbURL, dbID, dbPassword); // MySQL에 접속
	}
	
	/**
	 * 
	 * 가장 큰 게시글 번호를 조회하여 다음 게시글 번호 반환 DESC 내림차순(큰 값 → 작은 값)
	 * 											  ASC 오름차순(작은 값 → 큰 값)
	 */

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
	
	/**
	 * 
	 * DB 서버의 현재 날짜 및 시간 조회
	 * 
	 */

	public String getDate() {
		String SQL = "SELECT NOW()";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			rs = pstmt.executeQuery(); //  쿼리 실행
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
	
	/**
	 * 
	 * 게시글(첨부파일, 공지 여부 포함)을 DB에 등록
	 * 
	 */

	public int write(String bbsTitle, String userID, String bbsContent, int bbsPublic, String bbsgroupName,
			String originalFileName, String savedFileName, int isNotice) {
		String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"; // 게시글 정보를 BBS 테이블에 저장하는 SQL
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			int nextID = getNext();   // 다음 게시글 번호 조회
			String date = getDate();  // 현재 날짜 및 시간 조회
			conn = getConnection();

			pstmt = conn.prepareStatement(SQL); // SQL 실행 준비
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
			pstmt.setString(12, originalFileName);
			pstmt.setString(13, savedFileName);
			pstmt.setInt(14, isNotice);

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
		String SQL = "SELECT COUNT(*) FROM BBS WHERE bbsAvailable = 1 AND groupName = ?";   // 삭제되지 않은(bbsAvailable = 1)
																							// 게시글 중 전달받은 게시판 그룹의 게시글
																							// 개수를 조회하는 SQL
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, groupName); // ? 자리에 전달받은 groupName 값 설정
			rs = pstmt.executeQuery(); // SQL 실행
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
	
	/**
	 * 게시판 그룹별 게시글 목록 조회(페이징)
	 *  DB에서 조회한 게시글 정보를 Bbs 객체에 저장하고, 추천수 10개 이상 또는 공지글이면 제목을 굵게 시하도록 설정한 후 목록(ArrayList)에 추가하는 로직
	 */

	public ArrayList<Bbs> getList(int pageNumber, String groupName) {
		String SQL = "SELECT * FROM BBS WHERE bbsAvailable = 1 AND groupName = ? "  // 해당 게시판의 게시글을 최신순으로 20개 조회
				+ "ORDER BY bbsID DESC LIMIT 20 OFFSET ?"; // LIMIT 20 OFFSET ? 20개씩 조회하고 offset부터 가져오기 LIMIT 20 OFFSET 40 41번째 게시글부터 20개 조회
		
		ArrayList<Bbs> list = new ArrayList<Bbs>();  // 게시글 목록을 저장할 리스트
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			int offset = (pageNumber - 1) * 20;  // 현재 페이지의 시작 위치 계산
			pstmt = conn.prepareStatement(SQL);
			pstmt.setString(1, groupName);		 // 게시판 그룹명 설정
			pstmt.setInt(2, offset);			 // 시작 위치 설정
			rs = pstmt.executeQuery();			 // SQL 실행
			
			while (rs.next()) {  // 조회된 게시글을 Bbs 객체에 저장
				Bbs bbs = new Bbs();
				
			    // 게시글 정보 저장
				bbs.setBbsID(rs.getInt("bbsID"));
				bbs.setBbsTitle(rs.getString("bbsTitle"));
				bbs.setUserID(rs.getString("userID"));
				// 작성일 조회
				String rawDate = rs.getString("bbsDate");
				// 오늘 날짜
				String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
				String displayDate;
				if (rawDate.startsWith(today)) { // 오늘 작성한 글이면 시간만 표시
					displayDate = rawDate.substring(11, 13) + "시 " + rawDate.substring(14, 16) + "분";
				} else {  // 오늘 이전 글이면 날짜 표시
					displayDate = rawDate.substring(0, 10);
				}
				
				bbs.setBbsDate(displayDate);						// 화면에 표시할 작성일 저장
				bbs.setBbsContent(rs.getString("bbsContent")); 	    // 게시글 내용 저장
				bbs.setBbsAvailable(rs.getInt("bbsAvailable"));	    // 게시글 사용 여부 저장
				bbs.setInquiry(rs.getInt("inquiry"));				// 조회수 저장	
				bbs.setRecommendation(rs.getInt("recommendation")); // 추천수 저장
				bbs.setComments(rs.getInt("Comments"));				// 댓글 수 저장
				bbs.setIsPublic(rs.getInt("bbsPublic"));			// 공개 여부 저장
				bbs.setIsNotice(rs.getInt("isNotice")); 			// 공지글 여부 저장
				
				boolean isBold = (bbs.getRecommendation() >= 10);  // 추천수 10개 이상이면 isBold = ture 아니면 false 
					
				bbs.setIsBold(isBold); //  Bbs 객체에 결과 저장 setIsBold 에 isBold = true 값 저장 
					
				list.add(bbs); // bbs 객체를 list에 넣음
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return list; // 컨트롤러에서 ArrayList<Bbs> list = bbsDAO.getList(pageNumber, groupName); 이렇게 값 받음
	}

	/**
	 * 게시글 번호(bbsID)로 게시글 상세 정보를 조회하여 반환
	 */

	public Bbs getDetail(int bbsID) {
		String SQL = "SELECT * FROM BBS WHERE bbsID = ? AND bbsAvailable = 1";
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID); // ? 자리에 매개변수로 받은 int bbsID 값을 넣음
			rs = pstmt.executeQuery();
			
			if (rs.next()) {									// 조회한 게시글이 있으면
				Bbs bbs = new Bbs();  							// 게시글 정보를 저장할 객체 생성
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
				bbs.setOriginalFileName(rs.getString("originalFileName"));
				bbs.setSavedFileName(rs.getString("savedFileName"));
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
	 * 게시글 삭제 상태(bbsAvailable = 0)로 변경
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

	/**
	 * 게시글 추천수 증가 7-7
	 */

	public int recommend(int bbsID) {
		String SQL = "UPDATE BBS SET recommendation = recommendation + 1 WHERE bbsID = ?";
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

	/**
	 * 게시글 조회수 증가 7-9
	 */

	public int Inquiry(int bbsID) {
		String SQL = "UPDATE BBS SET inquiry = inquiry + 1 WHERE bbsID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, null);
		}
	}

	/**
	 * 댓글수 증가 7-9
	 */

	public int updateCommentCount(int bbsID, int count) {
		String SQL = "UPDATE BBS SET Comments = ? WHERE bbsID = ?";
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(SQL);
			pstmt.setInt(1, count);
			pstmt.setInt(2, bbsID);
			return pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			close(conn, pstmt, null);
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