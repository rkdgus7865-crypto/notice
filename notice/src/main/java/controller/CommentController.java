package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLEncoder;

import dao.BbsDAO;
import dao.CommentDAO;

@WebServlet(urlPatterns = {"/commentWrite", "/commentUpdate", "/commentDelete", "/commentRecommend"}) // 댓글 관련 URL 요청을 하나의 CommentController에서 처리
public class CommentController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath(); // 어떤 URL로 요청 왔는지 확인

        if (path.equals("/commentWrite")) {
            writeComment(request, response);
        } else if (path.equals("/commentUpdate")) {
            updateComment(request, response);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/commentDelete")) {
            deleteComment(request, response);
        } else if (path.equals("/commentRecommend")) {
            recommendComment(request, response);
        }
    }

    // 댓글 등록 (실명인증 회원만)
    private void writeComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("userID");
        String userGrade = (String) session.getAttribute("userGrade");

        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("groupName");
        String commentContent = request.getParameter("commentContent");
        String secretParam = request.getParameter("secretComment");
        int secretComment = (secretParam != null) ? 1 : 0;

        if (userID == null || !"VERIFIED".equals(userGrade)) {
            response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
                + URLEncoder.encode(groupName, "UTF-8"));
            return;
        }

        if (commentContent == null || commentContent.isBlank()) {
            response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
                + URLEncoder.encode(groupName, "UTF-8"));
            return;
        }

        CommentDAO commentDAO = new CommentDAO();
        commentDAO.write(bbsID, userID, commentContent, secretComment);

        // 댓글수 갱신 7-9
        BbsDAO bbsDAO = new BbsDAO();
        int count = commentDAO.getCommentCount(bbsID);
        bbsDAO.updateCommentCount(bbsID, count);
        
        // 게시글 상세보기 화면으로 이동
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8"));
    }

    // 댓글 수정 (본인만)
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
            + URLEncoder.encode(groupName, "UTF-8"));
    }

    // 댓글 삭제 (본인만)
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

        // 댓글수 갱신 7-9
        BbsDAO bbsDAO = new BbsDAO();
        int count = commentDAO.getCommentCount(bbsID);
        bbsDAO.updateCommentCount(bbsID, count);

        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8"));
    }
    
    // 게시글 댓글 추천
    private void recommendComment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        String userID = (String) session.getAttribute("userID");
        
        // URL에서 댓글 번호, 게시글 번호, 게시판 그룹명 가져오기
        int commentID = Integer.parseInt(request.getParameter("commentID"));
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");

        
        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        // 아직 추천하지 않은 경우에만 추천 처리
        CommentDAO commentDAO = new CommentDAO();
        if (!commentDAO.hasRecommended(commentID, userID)) {
            commentDAO.recommend(commentID, userID);
        }

        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8"));
    }
}