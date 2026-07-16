/*
 * package controller;
 * 
 * import javax.servlet.*; import javax.servlet.http.*; import
 * javax.servlet.annotation.*; import java.io.*; import dao.BbsDAO; import
 * java.net.URLEncoder; import dto.Bbs;
 * 
 * @WebServlet("/editView") // 수정 화면 보여주기 public class EditController extends
 * HttpServlet {
 * 
 * private static final long serialVersionUID = 1L;
 * 
 * protected void doGet(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * 
 * request.setCharacterEncoding("UTF-8");
 * response.setContentType("text/html; charset=UTF-8");
 * 
 * String userID = (String) request.getSession().getAttribute("userID"); if
 * (userID == null) { response.sendRedirect("login.jsp"); return; }
 * 
 * int bbsID = Integer.parseInt(request.getParameter("bbsID")); String groupName
 * = request.getParameter("group");
 * 
 * BbsDAO bbsDAO = new BbsDAO(); Bbs bbs = bbsDAO.getDetail(bbsID);
 * 
 * try { // 작성자 본인 확인 if (!userID.equals(bbs.getUserID())) {
 * response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName,
 * "UTF-8")); return; } request.setAttribute("bbs", bbs);
 * request.setAttribute("groupName", groupName);
 * request.getRequestDispatcher("edit.jsp").forward(request, response); } catch
 * (Exception e) { e.printStackTrace(); } } }
 */
package backupController;

