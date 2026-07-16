/*
 * package backupController; import javax.servlet.*; import
 * javax.servlet.http.*; import javax.servlet.annotation.*; import java.io.*;
 * import java.net.URLEncoder; import dao.BbsDAO; import dao.CommentDAO;
 * 
 * @WebServlet(urlPatterns = {"/commentWrite", "/commentUpdate",
 * "/commentDelete", "/commentRecommend", "/recommendAction"}) public class
 * CommentController extends HttpServlet { private static final long
 * serialVersionUID = 1L;
 * 
 * protected void doPost(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * request.setCharacterEncoding("UTF-8"); String path =
 * request.getServletPath();
 * 
 * if (path.equals("/commentWrite")) { writeComment(request, response); } else
 * if (path.equals("/commentUpdate")) { updateComment(request, response); } }
 * 
 * protected void doGet(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * request.setCharacterEncoding("UTF-8"); String path =
 * request.getServletPath();
 * 
 * if (path.equals("/commentDelete")) { deleteComment(request, response); } else
 * if (path.equals("/commentRecommend")) { recommendComment(request, response);
 * } else if (path.equals("/recommendAction")) { recommendBbs(request,
 * response); // ← 추가 } }
 * 
 * // 댓글 등록 (실명인증 회원만) private void writeComment(HttpServletRequest request,
 * HttpServletResponse response) throws IOException { HttpSession session =
 * request.getSession(); String userID = (String)
 * session.getAttribute("userID"); String userGrade = (String)
 * session.getAttribute("userGrade"); int bbsID =
 * Integer.parseInt(request.getParameter("bbsID")); String groupName =
 * request.getParameter("groupName"); String commentContent =
 * request.getParameter("commentContent"); String secretParam =
 * request.getParameter("secretComment"); int secretComment = (secretParam !=
 * null) ? 1 : 0;
 * 
 * if (userID == null || !"VERIFIED".equals(userGrade)) {
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); return; } if
 * (commentContent == null || commentContent.isBlank()) {
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); return; }
 * 
 * CommentDAO commentDAO = new CommentDAO(); commentDAO.write(bbsID, userID,
 * commentContent, secretComment);
 * 
 * BbsDAO bbsDAO = new BbsDAO(); int count = commentDAO.getCommentCount(bbsID);
 * bbsDAO.updateCommentCount(bbsID, count);
 * 
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); }
 * 
 * // 댓글 수정 (본인만) private void updateComment(HttpServletRequest request,
 * HttpServletResponse response) throws IOException { HttpSession session =
 * request.getSession(); String userID = (String)
 * session.getAttribute("userID"); int commentID =
 * Integer.parseInt(request.getParameter("commentID")); int bbsID =
 * Integer.parseInt(request.getParameter("bbsID")); String groupName =
 * request.getParameter("groupName"); String commentContent =
 * request.getParameter("commentContent"); if (userID == null) {
 * response.sendRedirect("login.jsp"); return; } CommentDAO commentDAO = new
 * CommentDAO(); commentDAO.update(commentID, userID, commentContent);
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true" ); }
 * 
 * // 댓글 삭제 (본인만) private void deleteComment(HttpServletRequest request,
 * HttpServletResponse response) throws IOException { HttpSession session =
 * request.getSession(); String userID = (String)
 * session.getAttribute("userID"); int commentID =
 * Integer.parseInt(request.getParameter("commentID")); int bbsID =
 * Integer.parseInt(request.getParameter("bbsID")); String groupName =
 * request.getParameter("group"); if (userID == null) {
 * response.sendRedirect("login.jsp"); return; } CommentDAO commentDAO = new
 * CommentDAO(); commentDAO.delete(commentID, userID); BbsDAO bbsDAO = new
 * BbsDAO(); int count = commentDAO.getCommentCount(bbsID);
 * bbsDAO.updateCommentCount(bbsID, count);
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); }
 * 
 * // 댓글 추천 private void recommendComment(HttpServletRequest request,
 * HttpServletResponse response) throws IOException { HttpSession session =
 * request.getSession(); String userID = (String)
 * session.getAttribute("userID"); int commentID =
 * Integer.parseInt(request.getParameter("commentID")); int bbsID =
 * Integer.parseInt(request.getParameter("bbsID")); String groupName =
 * request.getParameter("group"); if (userID == null) {
 * response.sendRedirect("login.jsp"); return; } CommentDAO commentDAO = new
 * CommentDAO(); if (!commentDAO.hasRecommended(commentID, userID)) {
 * commentDAO.recommend(commentID, userID); }
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8")); }
 * 
 * // 게시글 추천 (원래 RecommendController) private void
 * recommendBbs(HttpServletRequest request, HttpServletResponse response) throws
 * IOException { // try-catch는 throws IOException 대체 String userID = (String)
 * request.getSession().getAttribute("userID");
 * 
 * if (userID == null) { response.sendRedirect("login.jsp"); return; } int bbsID
 * = Integer.parseInt(request.getParameter("bbsID")); String groupName =
 * request.getParameter("group");
 * 
 * BbsDAO bbsDAO = new BbsDAO(); bbsDAO.recommend(bbsID);
 * 
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true"); } }
 */