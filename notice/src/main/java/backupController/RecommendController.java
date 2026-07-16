/*
 * package controller;
 * 
 * import javax.servlet.*; import javax.servlet.http.*; import
 * javax.servlet.annotation.*; import java.io.*; import java.net.URLEncoder;
 * import dao.BbsDAO;
 * 
 * @WebServlet("/recommendAction") public class RecommendController extends
 * HttpServlet {
 * 
 * private static final long serialVersionUID = 1L;
 * 
 * protected void doGet(HttpServletRequest request, HttpServletResponse
 * response) throws ServletException, IOException {
 * 
 * request.setCharacterEncoding("UTF-8");
 * 
 * String userID = (String) request.getSession().getAttribute("userID");
 * 
 * if (userID == null) { response.sendRedirect("login.jsp"); return; }
 * 
 * int bbsID = Integer.parseInt(request.getParameter("bbsID")); String groupName
 * = request.getParameter("group");
 * 
 * BbsDAO bbsDAO = new BbsDAO(); bbsDAO.recommend(bbsID);
 * 
 * try { // 추천 완료 후 해당 게시글 상세보기로 이동 response.sendRedirect("viewDetail?bbsID=" +
 * bbsID + "&group=" + URLEncoder.encode(groupName, "UTF-8") +
 * "&fromRecommend=true"); } catch (Exception e) { e.printStackTrace(); } } }
 */
package backupController;

