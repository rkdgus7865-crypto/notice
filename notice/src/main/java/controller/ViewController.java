package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.util.ArrayList;
import java.io.*;
import dao.BbsDAO;
import dto.Bbs;
import dao.CommentDAO;
import dto.Comment;
 
@WebServlet("/viewDetail") // 사용자가 게시판에서 제목을 클릭하면 실행되는 Controller(게시글 상세 보기 컨트롤러)
public class ViewController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        int bbsID = Integer.parseInt(request.getParameter("bbsID")); //URL에서 게시글 번호(bbsID)를 가져와 정수형으로 변환
        String groupName = request.getParameter("group");  // URL에서 게시판 그룹명(group) 가져오기
        
        if (groupName == null) {
        	groupName = "자유게시판";
        }
        
        // 회원공개 게시글인데 비회원이면 접근 차단
        HttpSession session = request.getSession(); 
        String userID = (String) session.getAttribute("userID");
        boolean isGuest = (userID == null);
        
		/*
		 * String viewedKey = "viewed_" + bbsID; if (session.getAttribute(viewedKey) ==
		 * null) { bbsDAO.Inquiry(bbsID); // 조회수 증가 Inquiry Dao 메소드 호출
		 * session.setAttribute(viewedKey, true); }
		 */
        
        String fromRecommend = request.getParameter("fromRecommend");
        BbsDAO bbsDAO = new BbsDAO();      // 게시글 조회를 위해 DAO 객체 생성

        if (!"true".equals(fromRecommend)) {
            bbsDAO.Inquiry(bbsID);
        }
        
        Bbs bbs = bbsDAO.getDetail(bbsID); // 게시글 번호로 DB에서 게시글 상세 정보 조회
        
        // 회원공개 게시글인데 비회원이면 접근 차단 
        if (bbs.getIsPublic() == 0 && isGuest) {
            request.setAttribute("errorMsg", "회원만 열람 가능한 게시글입니다.");// login.jsp 에서 alert 띄우는거 재사용
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return; 
        }

        request.setAttribute("bbs", bbs); // 조회한 게시글 정보를 JSP로 전달
        request.setAttribute("groupName", groupName);  // 게시판 그룹명을 JSP로 전달
        
        // 댓글 목록 조회
        CommentDAO commentDAO = new CommentDAO();
        ArrayList<Comment> commentList = commentDAO.getList(bbsID);
        request.setAttribute("commentList", commentList);
        
        // 하단 게시글 목록 조회 (같은 그룹, 1페이지) 7-9
        int bottomPageNumber = 1; // 하단 게시글 목록의 현재 페이지 defalt 1
        
        if (request.getParameter("bottomPage") != null) {
            bottomPageNumber = Integer.parseInt(request.getParameter("bottomPage"));
        }

        ArrayList<Bbs> bbsList = bbsDAO.getList(bottomPageNumber, groupName);
        int totalBottomCount = bbsDAO.getTotalCount(groupName); // 전체 게시글 개수 조회
        int totalBottomPages = (int) Math.ceil((double) totalBottomCount / 20); // 전체 페이지 수 계산 (페이지당 20개
        int bottomStartPage = ((bottomPageNumber - 1) / 5) * 5 + 1; // 페이지 번호 시작 계산 (1~5)
        int bottomEndPage = Math.min(bottomStartPage + 4, totalBottomPages); // 페이지 번호 끝 계산
        int bottomStartNumber = totalBottomCount - ((bottomPageNumber - 1) * 20);// 게시글 상세 목록 하단


        request.setAttribute("bbsList", bbsList); // 게시글 목록을 JSP로 전달
        request.setAttribute("bottomPageNumber", bottomPageNumber); // 현재 페이지 번호 전달
        request.setAttribute("totalBottomPages", totalBottomPages); // 전체 페이지 수 전달
        request.setAttribute("bottomStartPage", bottomStartPage); // 페이지 번호 시작 전달
        request.setAttribute("bottomEndPage", bottomEndPage); // 페이지 번호 끝 전달
        request.setAttribute("bottomStartNumber", bottomStartNumber);  // 게시글 상세 목록 하단
        
        request.setAttribute("bbsList", bbsList); // Controller에서 조회한 게시글 목록(bbsList)을 view.jsp로 전달
        
        request.getRequestDispatcher("view.jsp").forward(request, response);  // view.jsp로 이동하여 게시글 상세 내용 출력
        
    }
}