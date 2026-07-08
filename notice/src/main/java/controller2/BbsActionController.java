/*
 * package controller2;
 * 
 * import javax.servlet.*; import javax.servlet.http.*; import
 * javax.servlet.annotation.*; import java.io.*; import java.net.URLEncoder;
 * import dao.BbsDAO; import dto.Bbs;
 * 
 * @WebServlet("/bbsAction") public class BbsActionController extends
 * HttpServlet {
 * 
 * private static final long serialVersionUID = 1L;
 * 
 * // GET 요청 처리 (view - 조회는 로그인 안 해도 가능) protected void doGet(HttpServletRequest
 * request, HttpServletResponse response) throws ServletException, IOException {
 * 
 * request.setCharacterEncoding("UTF-8");
 * response.setContentType("text/html; charset=UTF-8");
 * 
 * String action = request.getParameter("action");
 * 
 * if ("view".equals(action)) { view(request, response); } }
 * 
 * // POST 요청 처리 (write 등 - 로그인 필요) protected void doPost(HttpServletRequest
 * request, HttpServletResponse response) throws ServletException, IOException {
 * 
 * request.setCharacterEncoding("UTF-8");
 * response.setContentType("text/html; charset=UTF-8");
 * 
 * String action = request.getParameter("action");
 * 
 * String userID = (String) request.getSession().getAttribute("userID"); if
 * (userID == null) { response.sendRedirect("login.jsp"); return; }
 * 
 * if ("write".equals(action)) { write(request, response, userID); } }
 * 
 * 
 * 
 * // view 게시글 상세 조회 private void view(HttpServletRequest request,
 * HttpServletResponse response) throws ServletException, IOException {
 * 
 * int bbsID = Integer.parseInt(request.getParameter("bbsID")); String groupName
 * = request.getParameter("group"); if (groupName == null) groupName = "자유게시판";
 * 
 * BbsDAO bbsDAO = new BbsDAO(); Bbs bbs = bbsDAO.getDetail(bbsID);
 * 
 * request.setAttribute("bbs", bbs); request.setAttribute("groupName",
 * groupName); request.getRequestDispatcher("view.jsp").forward(request,
 * response); }
 * 
 * // write 글쓰기 처리 private void write(HttpServletRequest request,
 * HttpServletResponse response, String userID) throws ServletException,
 * IOException {
 * 
 * String bbsTitle = request.getParameter("bbsTitle"); String bbsContent =
 * request.getParameter("bbsContent"); String groupName =
 * request.getParameter("groupName"); String bbsPublic =
 * request.getParameter("bbsPublic");
 * 
 * int publicValue = (bbsPublic != null) ? 1 : 0; // 체크 O → 1 (전체공개), 체크 X → 0
 * (회원공개)
 * 
 * // 빈값 체크 if (bbsTitle.isBlank() || bbsContent.isBlank()) {
 * request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
 * request.getRequestDispatcher("write.jsp").forward(request, response); return;
 * }
 * 
 * BbsDAO bbsDAO = new BbsDAO(); int result = bbsDAO.write(bbsTitle, userID,
 * bbsContent, publicValue, groupName);
 * 
 * try { if (result == -1) { request.setAttribute("errorMsg", "글쓰기에 실패했습니다.");
 * request.getRequestDispatcher("write.jsp").forward(request, response); } else
 * { response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName,
 * "UTF-8")); } } catch (Exception e) { e.printStackTrace(); } } }
 */