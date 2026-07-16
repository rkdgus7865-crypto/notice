/*
 * package controller;
 * 
 * import javax.servlet.*; import javax.servlet.http.*; import
 * javax.servlet.annotation.*; import java.io.*; import java.net.URLEncoder;
 * import dao.BbsDAO;
 * 
 * @WebServlet("/editAction") // 수정 저장 처리 public class EditActionController
 * extends HttpServlet {
 * 
 * private static final long serialVersionUID = 1L;
 * 
 * protected void doPost(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * 
 * request.setCharacterEncoding("UTF-8");
 * response.setContentType("text/html; charset=UTF-8");
 * 
 * String userID = (String) request.getSession().getAttribute("userID"); if
 * (userID == null) { response.sendRedirect("login.jsp"); return; }
 * 
 * int bbsID = Integer.parseInt(request.getParameter("bbsID")); String bbsTitle
 * = request.getParameter("bbsTitle"); String bbsContent =
 * request.getParameter("bbsContent"); String groupName =
 * request.getParameter("groupName");
 * 
 * BbsDAO bbsDAO = new BbsDAO(); int result = bbsDAO.update(bbsID, bbsTitle,
 * bbsContent);
 * 
 * try { if (result == -1) { request.setAttribute("errorMsg", "수정에 실패했습니다.");
 * request.getRequestDispatcher("edit.jsp").forward(request, response); } else {
 * response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" +
 * URLEncoder.encode(groupName, "UTF-8")); } } catch (Exception e) {
 * e.printStackTrace(); } } }
 */
package backupController;

