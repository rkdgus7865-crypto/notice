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
        
        BbsDAO bbsDAO = new BbsDAO();      // 게시글 조회를 위해 DAO 객체 생성
        Bbs bbs = bbsDAO.getDetail(bbsID); // 게시글 번호로 DB에서 게시글 상세 정보 조회

        request.setAttribute("bbs", bbs); // 조회한 게시글 정보를 JSP로 전달
        request.setAttribute("groupName", groupName);  // 게시판 그룹명을 JSP로 전달
        
        // 댓글 목록 조회
        CommentDAO commentDAO = new CommentDAO();
        ArrayList<Comment> commentList = commentDAO.getList(bbsID);
        request.setAttribute("commentList", commentList);
        
        request.getRequestDispatcher("view.jsp").forward(request, response);  // view.jsp로 이동하여 게시글 상세 내용 출력
        
        
        
        
        
    }
}