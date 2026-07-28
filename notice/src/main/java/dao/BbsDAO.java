package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import dto.Bbs;

public class BbsDAO extends BaseDAO { // BaseDAO 메소드 상속 받음
	
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

	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    ResultSet rs1 = null;
	    try {
	        int nextID = getNext();   // 다음 게시글 번호 조회
	        String date = getDate();  // 현재 날짜 및 시간 조회
	        conn = getConnection();

	        // 지금 가장 작은 replyOrder 값을 조회해서, 그보다 1 작은 값을 새 글에 부여
	        String minOrderSQL = "SELECT IFNULL(MIN(replyOrder), 1000000) FROM BBS WHERE groupName = ?";
	        pstmt1 = conn.prepareStatement(minOrderSQL);
	        pstmt1.setString(1, bbsgroupName);
	        rs1 = pstmt1.executeQuery();
	        int minOrder = 1000000;
	        if (rs1.next()) {
	            minOrder = rs1.getInt(1);
	        }
	        int newOrder = minOrder - 1;   // 지금 가장 작은 값보다 1 작게 → 항상 맨 앞자리

	        String SQL = "INSERT INTO BBS VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	        pstmt2 = conn.prepareStatement(SQL);
	        pstmt2.setInt(1, nextID);
	        pstmt2.setString(2, bbsTitle);
	        pstmt2.setString(3, userID);
	        pstmt2.setString(4, date);
	        pstmt2.setString(5, bbsContent);
	        pstmt2.setInt(6, 1);
	        pstmt2.setInt(7, 0);
	        pstmt2.setInt(8, 0);
	        pstmt2.setInt(9, 0);
	        pstmt2.setInt(10, bbsPublic);
	        pstmt2.setString(11, bbsgroupName);
	        pstmt2.setString(12, originalFileName);
	        pstmt2.setString(13, savedFileName);
	        pstmt2.setInt(14, isNotice);

	        pstmt2.setNull(15, java.sql.Types.INTEGER);   // parentBbsID: 원글은 부모 없음
	        pstmt2.setInt(16, 0);                          // replyStep: 원글은 0단계
	        pstmt2.setInt(17, newOrder);                   // replyOrder: 계산된 새 맨앞 순번

	        return pstmt2.executeUpdate();
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(null, pstmt1, rs1);
	        close(conn, pstmt2, null);
	    }
	    return -1;
	}

	/**
	 * 게시판 그룹(groupName)에 해당하는 게시글의 전체 개수를 조회하는 메서드
	 * 
	 */

	public int getTotalCount(String groupName) {
		String SQL = "SELECT COUNT(*) FROM BBS WHERE bbsAvailable = 1 AND groupName = ? AND isNotice = 0";   // 삭제되지 않은(bbsAvailable = 1)
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
	 *  DB에서 조회한 게시글 정보를 Bbs 객체에 저장하고 추천수 10개 이상 또는 공지글이면 제목을 굵게 시하도록 설정한 후 목록(ArrayList)에 추가하는 로직
	 */

	public ArrayList<Bbs> getList(int pageNumber, String groupName) {
		String SQL = "SELECT * FROM BBS WHERE bbsAvailable = 1 AND groupName = ? AND isNotice = 0 "// 해당 게시판의 게시글을 최신순으로 20개 조회
		        + "ORDER BY replyOrder ASC LIMIT 20 OFFSET ?";
		
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
				bbs.setParentBbsID(rs.getInt("parentBbsID"));  		// 부모글 ID
				bbs.setReplyStep(rs.getInt("replyStep"));      		// 들여쓰기 단계
				bbs.setReplyOrder(rs.getInt("replyOrder"));         // 정렬 순서
				
				boolean isBold = (bbs.getRecommendation() >= 10);  // 추천수 10개 이상이면 isBold = ture 아니면 false 
					
				bbs.setIsBold(isBold); //  Bbs 객체에 결과 저장 setIsBold 에 isBold = true 값 저장 
					
				list.add(bbs); // bbs 객체를 list에 넣음
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn, pstmt, rs);
		}
		return list; // 컨트롤러에서 ArrayList<Bbs> list = bbsDAO.getList(pageNumber, groupName) 이렇게 값 받음
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
	 * 게시글 댓글 추천 중복 확인
	 */
	
	public boolean hasRecommended(int bbsID, String userID) {
	    String SQL = "SELECT * FROM BbsRecommend WHERE bbsID = ? AND userID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setInt(1, bbsID);
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
	 * 게시글 추천수 증가 7-7
	 */

	public int recommend(int bbsID, String userID) {
	    String insertSQL = "INSERT INTO BbsRecommend (bbsID, userID) VALUES (?, ?)";
	    String updateSQL = "UPDATE BBS SET recommendation = recommendation + 1 WHERE bbsID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    try {
	        conn = getConnection();
	        pstmt1 = conn.prepareStatement(insertSQL);
	        pstmt1.setInt(1, bbsID);
	        pstmt1.setString(2, userID);
	        pstmt1.executeUpdate();

	        pstmt2 = conn.prepareStatement(updateSQL);
	        pstmt2.setInt(1, bbsID);
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
	 * 게시글 추천 취소
	 */
	
	public int cancelRecommend(int bbsID, String userID) {
	    String deleteSQL = "DELETE FROM BbsRecommend WHERE bbsID = ? AND userID = ?";
	    String updateSQL = "UPDATE BBS SET recommendation = recommendation - 1 WHERE bbsID = ?";
	    Connection conn = null;
	    PreparedStatement pstmt1 = null;
	    PreparedStatement pstmt2 = null;
	    try {
	        conn = getConnection();
	        pstmt1 = conn.prepareStatement(deleteSQL);
	        pstmt1.setInt(1, bbsID);
	        pstmt1.setString(2, userID);
	        pstmt1.executeUpdate();

	        pstmt2 = conn.prepareStatement(updateSQL);
	        pstmt2.setInt(1, bbsID);
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
	
	/**
	 *  최근 공지게시글 최상단에 3개 공지
	 */
	
	public ArrayList<Bbs> getNoticeList(String groupName) {
	    String SQL = "SELECT * FROM BBS WHERE groupName = ? AND isNotice = 1 AND bbsAvailable = 1 ORDER BY bbsID DESC LIMIT 3";
	    
	    ArrayList<Bbs> list = new ArrayList<Bbs>();
	    
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, groupName);
	        rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Bbs bbs = new Bbs();
	            bbs.setBbsID(rs.getInt("bbsID"));
	            bbs.setBbsTitle(rs.getString("bbsTitle"));
	            bbs.setUserID(rs.getString("userID"));
	            bbs.setBbsDate(rs.getString("bbsDate"));
	            bbs.setInquiry(rs.getInt("inquiry"));
	            bbs.setRecommendation(rs.getInt("recommendation"));
	            bbs.setComments(rs.getInt("comments"));
	            bbs.setIsPublic(rs.getInt("bbsPublic"));
	            bbs.setIsNotice(1);   // 이 메서드는 공지글만 조회하니까 무조건 1
	            bbs.setIsBold(bbs.getRecommendation() >= 10); 

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
	 *  공지글만 조회
	 */
	
	public ArrayList<Bbs> getNoticeOnlyList(String groupName) {
	    String SQL = "SELECT * FROM BBS WHERE groupName = ? AND isNotice = 1 AND bbsAvailable = 1 ORDER BY bbsID DESC";
	    ArrayList<Bbs> list = new ArrayList<Bbs>();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL);
	        pstmt.setString(1, groupName);
	        rs = pstmt.executeQuery();
	        
	        while (rs.next()) {
	            Bbs bbs = new Bbs();
	            bbs.setBbsID(rs.getInt("bbsID"));
	            bbs.setBbsTitle(rs.getString("bbsTitle"));
	            bbs.setUserID(rs.getString("userID"));
	            bbs.setBbsDate(rs.getString("bbsDate"));
	            bbs.setInquiry(rs.getInt("inquiry"));
	            bbs.setRecommendation(rs.getInt("recommendation"));
	            bbs.setComments(rs.getInt("comments"));
	            bbs.setIsPublic(rs.getInt("bbsPublic"));
	            bbs.setIsNotice(1);   // 이 메서드는 공지글만 조회하니까 무조건 1
	            bbs.setIsBold(bbs.getRecommendation() >= 10);  

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
	 *  검색 유형에 따라 WHERE 조건 다르게 구성
	 */
	
	public ArrayList<Bbs> searchList(int pageNumber, String groupName, String searchType, String keyword) {
	    StringBuilder SQL = new StringBuilder(); 
	    
	    SQL.append("SELECT * FROM BBS WHERE bbsAvailable = 1 AND groupName = ? AND isNotice = 0"); // 검색 조건과 상관없이 항상 붙는 공통 필터    

	    ArrayList<String> conditions = new ArrayList<String>(); // 제목/댓글/작성자 검색 조건을 저장하는 리스트
	    ArrayList<String> params = new ArrayList<String>();		// 위 조건문의 ? 자리에 실제로 검색어를 순서대로 담을 리스트
	    String likeKeyword = "%" + keyword + "%";

	    boolean useTitle = searchType.contains("title");     // bbs.jsp 에 드롭다운 받아온 값 유효성 검사 // contains로 판단하는 이유 드롭다운이 제목,댓글,작성자, 등등 6가지 조합인데 값 하나 titleComment에 title과 Comment가 동시에 들어있으니 문자열 안에 특정 단어가 포함되어 있는지만 검사하면 6개 옵션을 하나의 로직으로 다 처리 가능
	    boolean useComment = searchType.contains("comment");
	    boolean useWriter = searchType.contains("writer");

	    if (useTitle) { // 제목
	        conditions.add("bbsTitle LIKE ?");
	        params.add(likeKeyword);
	    }
	    if (useComment) { // 댓글 EXISTS  댓글 내용은 BBS 테이블이 아니라 별개의 Comment 테이블에 있음 이 게시글에 달린 댓글들 중에 키워드가 있는 게 하나라도 있는지 확인해야 하는데 단순 LIKE로는 다른 테이블 값을 검사할 수 없음 그래서 서브쿼리로 이 BBS.bbsID에 연결된 Comment 중에 조건 맞는 게 존재하냐 EXISTS 를 물어봄
	        conditions.add("EXISTS (SELECT 1 FROM Comment c WHERE c.bbsID = BBS.bbsID " + "AND c.commentAvailable = 1 AND c.secretComment = 0 AND c.commentContent LIKE ?)");
	        params.add(likeKeyword);
	    }
	    if (useWriter) { // 작성자 
	        conditions.add("userID LIKE ?");
	        params.add(likeKeyword);
	    }

	    if (!conditions.isEmpty()) {  // 선택된 조건이 하나라도 있으면, 그것들을 OR로 묶어서 SQL에 붙임
	        SQL.append(" AND (");
	        for (int i = 0; i < conditions.size(); i++) {
	            SQL.append(conditions.get(i));
	            if (i < conditions.size() - 1) SQL.append(" OR "); // 마지막 조건 뒤에는 OR 안 붙임
	        }
	        SQL.append(")");
	    }

//	    SQL.append(" ORDER BY bbsID DESC LIMIT 20 OFFSET ?"); // 최신글 먼저, 페이지당 20개
	    
	    SQL.append(" ORDER BY replyOrder ASC LIMIT 20 OFFSET ?");

	    ArrayList<Bbs> list = new ArrayList<Bbs>();
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = getConnection();
	        int offset = (pageNumber - 1) * 20; // 몇 번째 게시글부터 가져올지 계산 (1페이지=0부터 2페이지=20부터)
	        pstmt = conn.prepareStatement(SQL.toString()); // 위에서 만든 SQL 문장을 실행 준비 상태로 만들고 지금까지 append한 걸 다 합쳐서 진짜 String으로 뽑아냄
	        int idx = 1; // 지금 몇 번째 ? 빈칸을 채우고 있는지 세는 번호표 1번부터 시작
	        pstmt.setString(idx++, groupName); // 1번째 (=groupName 자리)에 값을 넣음 idx++는 넣고 나서 idx를 1 증가시킴 -> 다음 줄부터는 idx가 2가 됨
	        
	        for (int i = 0; i < params.size(); i++) {
	            String p = params.get(i);      // i번째 값을 꺼내서 p에 담음  params 리스트에 있는 값 들을 하나씩 꺼내서, 2번째, 3번째 ?에 순서대로 채움
	            pstmt.setString(idx++, p);	   // 제목+작성자 검색이면 params에 값이 2개 들어있어서 이 반복문이 2번 돎 -> idx는 2->3->4로 증가
	        }

	        
	        pstmt.setInt(idx++, offset); // 마지막 ? 이 예시에서는 4번째에 페이징 offset 값을 넣음

	        rs = pstmt.executeQuery();
	        
	        while (rs.next()) { // 검색 결과로 나온 게시글들을 한 줄씩 확인하면서 DB 컬럼값을 하나씩 꺼내 Bbs 객체에 채우고 다 채운 객체를 리스트에 쌓는 작업을 게시글 개수만큼 반복
	            Bbs bbs = new Bbs();
	            bbs.setBbsID(rs.getInt("bbsID"));
	            bbs.setBbsTitle(rs.getString("bbsTitle"));
	            bbs.setUserID(rs.getString("userID"));
	            String rawDate = rs.getString("bbsDate");
	            String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
	            String displayDate;
	            if (rawDate.startsWith(today)) {
	                displayDate = rawDate.substring(11, 13) + "시 " + rawDate.substring(14, 16) + "분";
	            } 
	            else {
	                displayDate = rawDate.substring(0, 10);
	            }
	            
	            bbs.setBbsDate(displayDate);
	            bbs.setBbsContent(rs.getString("bbsContent"));
	            bbs.setBbsAvailable(rs.getInt("bbsAvailable"));
	            bbs.setInquiry(rs.getInt("inquiry"));
	            bbs.setRecommendation(rs.getInt("recommendation"));
	            bbs.setComments(rs.getInt("Comments"));
	            bbs.setIsPublic(rs.getInt("bbsPublic"));
	            bbs.setIsNotice(rs.getInt("isNotice"));
	            bbs.setIsBold(bbs.getRecommendation() >= 10);
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
	 *  검색 결과 전체 개수 (페이징 계산용)
	 */
	
	public int getSearchTotalCount(String groupName, String searchType, String keyword) {
	    StringBuilder SQL = new StringBuilder();
	    SQL.append("SELECT COUNT(*) FROM BBS WHERE bbsAvailable = 1 AND groupName = ? AND isNotice = 0");

	    ArrayList<String> conditions = new ArrayList<String>(); // 제목/댓글/작성자 검색 조건을 저장하는 리스트
	    ArrayList<String> params = new ArrayList<String>();		// 위 조건문의 ? 자리에 실제로 검색어를 순서대로 담을 리스트
	    String likeKeyword = "%" + keyword + "%";

	    boolean useTitle = searchType.contains("title");
	    boolean useComment = searchType.contains("comment");
	    boolean useWriter = searchType.contains("writer");

	    if (useTitle) {
	        conditions.add("bbsTitle LIKE ?");
	        params.add(likeKeyword);
	    }
	    if (useComment) {
	        // 삭제되지 않고(commentAvailable=1), 비밀댓글이 아닌(secretComment=0) 댓글 중
	        // 키워드를 포함한 댓글이 하나라도 있으면 이 게시글을 검색 결과에 포함
	        conditions.add("EXISTS (SELECT 1 FROM Comment c WHERE c.bbsID = BBS.bbsID " + "AND c.commentAvailable = 1 AND c.secretComment = 0 AND c.commentContent LIKE ?)"); 
	        params.add(likeKeyword);
	    }
	    if (useWriter) {
	        conditions.add("userID LIKE ?");
	        params.add(likeKeyword);
	    }

	    if (!conditions.isEmpty()) {
	        SQL.append(" AND (");
	        for (int i = 0; i < conditions.size(); i++) {
	            SQL.append(conditions.get(i));
	            if (i < conditions.size() - 1) SQL.append(" OR ");
	        }
	        SQL.append(")");
	    }

	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    ResultSet rs = null;
	    
	    try {
	        conn = getConnection();
	        pstmt = conn.prepareStatement(SQL.toString());
	        int idx = 1; // 첫 번째 ?부터 값을 넣기 위해 번호를 1로 시작
	        pstmt.setString(idx++, groupName);
	        
	        for (int i = 0; i < params.size(); i++) {
	            String p = params.get(i);      // i번째 값을 꺼내서 p에 담음  params 리스트에 있는 값 들을 하나씩 꺼내서, 2번째, 3번째 ?에 순서대로 채움
	            pstmt.setString(idx++, p);	   // 제목+작성자 검색이면 params에 값이 2개 들어있어서 이 반복문이 2번 만듬 → idx는 2→3→4로 증가
	        }
	        
	        rs = pstmt.executeQuery();
	        if (rs.next()) return rs.getInt(1); // SELECT COUNT(*) 결과 값 반환
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        close(conn, pstmt, rs);
	    }
	    return 0;
	}
	
}