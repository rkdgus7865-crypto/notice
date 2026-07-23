package controller;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLEncoder;
import dao.BbsDAO;
import dao.CommentDAO;

@WebServlet(urlPatterns = {"/commentWrite", "/commentUpdate", "/commentDelete", "/commentRecommend", "/recommendAction"})
public class CommentController extends HttpServlet { 
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if (path.equals("/commentWrite")) {			
            writeComment(request, response);		 // 댓글 등록
        } else if (path.equals("/commentUpdate")) {
            updateComment(request, response);		 // 댓글 수정
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if (path.equals("/commentDelete")) {
            deleteComment(request, response);		  // 댓글 삭제
        } else if (path.equals("/commentRecommend")) {
            recommendComment(request, response); 	  // 댓글 추천
        } else if (path.equals("/recommendAction")) {
            recommendBbs(request, response);   		  // 게시글 추천
        }
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * 댓글 등록 (실명인증 회원만)
     */
    
    private void writeComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
    	
        HttpSession session = request.getSession(); 					// 현재 사용자의 세션 로그인 정보가 저장된 공간을 가져옴
        String userID = (String) session.getAttribute("userID"); 		// 세션에서 로그인한 사용자의 아이디를 꺼냄
        String userGrade = (String) session.getAttribute("userGrade");  // 세션에서 사용자 등급을 꺼냄  VERIFIED=실명인증회원, NORMAL=일반회원
        
        int bbsID = Integer.parseInt(request.getParameter("bbsID")); 	// 댓글을 달 게시글의 번호를 URL 폼 파라미터에서 받아옴
        
        String groupName = request.getParameter("groupName");
        String commentContent = request.getParameter("commentContent");
        String secretParam = request.getParameter("secretComment");
        
        int secretComment = (secretParam != null) ? 1 : 0; // 체크했으면 1 비밀댓글, 안 했으면 0 공개댓글로 변환

        // 검증 1 실명인증 회원인지 확인 
        if (userID == null || !"VERIFIED".equals(userGrade)) {
            response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="			  // 댓글 등록 안 하고 그냥 상세페이지로 돌려보냄
                + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true");	  // (fromRecommend=true는 조회수 증가 안 시키기 용도로 재사용 중인 파라미터
            return;
        }
        
        // 검증 2: 댓글 내용이 비어있는지 확인 
        if (commentContent == null || commentContent.isBlank()) {
            response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
                + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); 	  // 빈 댓글이면 저장 안 하고 상세페이지로 돌려보냄
            return;
        }

        CommentDAO commentDAO = new CommentDAO();
        commentDAO.write(bbsID, userID, commentContent, secretComment);

        BbsDAO bbsDAO = new BbsDAO();
        int count = commentDAO.getCommentCount(bbsID);
        // 이 게시글에 달린 댓글 총 개수를 다시 세어옴
        
        bbsDAO.updateCommentCount(bbsID, count);
        // 게시글 목록에 보여줄 댓글수 값을 최신 개수로 갱신 BBS 테이블의 Comments 컬럼 업데이트

        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true");
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * 댓글 수정 (본인만)
     */
    
    private void updateComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("userID");
        int commentID = Integer.parseInt(request.getParameter("commentID"));
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("groupName");
        String commentContent = request.getParameter("commentContent");
        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.update(commentID, userID, commentContent);
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true" );
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     * 댓글 삭제 (본인만)
     */
    
    private void deleteComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("userID");
        int commentID = Integer.parseInt(request.getParameter("commentID"));
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");
        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        CommentDAO commentDAO = new CommentDAO();
        commentDAO.delete(commentID, userID);
        BbsDAO bbsDAO = new BbsDAO();
        int count = commentDAO.getCommentCount(bbsID);
        bbsDAO.updateCommentCount(bbsID, count);
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true");
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     *  댓글 추천, 추천 취소
     */
    
    private void recommendComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("userID");
        int commentID = Integer.parseInt(request.getParameter("commentID"));
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");
        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        CommentDAO commentDAO = new CommentDAO();
        
        if (commentDAO.hasRecommended(commentID, userID)) {
            commentDAO.cancelRecommend(commentID, userID);   // 이미 추천했으면 취소
        } else {
            commentDAO.recommend(commentID, userID);          // 안 했으면 추천
        }
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8"));
    }

    /**
     * @param request
     * @param response
     * @throws IOException
     *  게시글 추천 
     */
    
    private void recommendBbs(HttpServletRequest request, HttpServletResponse response)
            throws IOException { 
        String userID = (String) request.getSession().getAttribute("userID");

        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");
        
        BbsDAO bbsDAO = new BbsDAO();
        if (bbsDAO.hasRecommended(bbsID, userID)) {
            bbsDAO.cancelRecommend(bbsID, userID);   // 이미 추천했으면 취소
        } else {
            bbsDAO.recommend(bbsID, userID);          // 안 했으면 추천
        }
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true");
    }
}