package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLEncoder;
import dao.BbsDAO;

@WebServlet("/editAction") // 수정 저장 처리
public class EditActionController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String userID = (String) request.getSession().getAttribute("userID");
		if (userID == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int bbsID = Integer.parseInt(request.getParameter("bbsID"));
		String bbsTitle = request.getParameter("bbsTitle");
		String bbsContent = request.getParameter("bbsContent");
		String groupName = request.getParameter("groupName");

		BbsDAO bbsDAO = new BbsDAO();
		int result = bbsDAO.update(bbsID, bbsTitle, bbsContent);

		try {
			if (result == -1) {
				request.setAttribute("errorMsg", "수정에 실패했습니다.");
				request.getRequestDispatcher("edit.jsp").forward(request, response);
			} else {
				response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" + URLEncoder.encode(groupName, "UTF-8"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * package controller;
	 * 
	 * import javax.servlet.*; import javax.servlet.http.*; import dao.BbsDAO;
	 * import javax.servlet.annotation.*; import java.io.*; import
	 * java.net.URLEncoder; import java.util.HashMap; import java.util.Map;
	 * 
	 * @WebServlet("/writeAction") public class WriteController extends HttpServlet
	 * {
	 * 
	 * private static final long serialVersionUID = 1L;
	 * 
	 * protected void doPost(HttpServletRequest request, HttpServletResponse
	 * response) throws ServletException, IOException {
	 * 
	 * request.setCharacterEncoding("UTF-8"); // 요청 데이터를 UTF-8(한글) 인코딩으로 처리
	 * response.setContentType("text/html; charset=UTF-8"); // 응답 데이터를 HTML 형식,
	 * UTF-8(한글) 인코딩으로 브라우저에 전송
	 * 
	 * // 로그인 안된 상태에서 게시판 글쓰기를 하면 login.jsp 이동 HttpSession session =
	 * request.getSession(); // 서버가 사용자마다 고유한 저장공간 세션 가져옴 String userID = (String)
	 * session.getAttribute("userID"); // 세션에서 userID 꺼냄 if (userID == null) {
	 * response.sendRedirect("login.jsp"); return; }
	 * 
	 * // write.jsp -> 넘겨준 파라미터 입력값 받기 String bbsTitle =
	 * request.getParameter("bbsTitle"); String bbsContent =
	 * request.getParameter("bbsContent"); String bbsPublic =
	 * request.getParameter("bbsPublic"); // 회원 비회원 게시글 공개여부 String groupName =
	 * request.getParameter("groupName"); // 게시판 종류
	 * 
	 * String action = request.getParameter("action"); //write, edit, delete
	 * 
	 * Map paramMap = new HashMap();
	 * 
	 * if(action.equals("write")) { write(paramMap); //작성 } else
	 * if(action.equals("edit")) { edit(paramMap); //수정 } else
	 * if(action.equals("delete")) { //삭제 delete(paramMap); }
	 * 
	 * }
	 * 
	 * private void write(Map param) {
	 * 
	 * int publicValue = (bbsPublic != null) ? 1 : 0; // 체크 O → 1 (전체공개), 체크 X → 0
	 * (회원공개) // 빈값 체크 if (bbsTitle.isBlank() || bbsContent.isBlank()) {
	 * request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
	 * request.getRequestDispatcher("write.jsp").forward(request, response); return;
	 * }
	 * 
	 * // 글쓰기 처리 BbsDAO bbsDAO = new BbsDAO(); //BbsDAO 객체 생성 (DB 연결 준비)S
	 * System.out.println("groupName: " + groupName);
	 * 
	 * int result = bbsDAO.write(bbsTitle, userID, bbsContent, publicValue,
	 * groupName); System.out.println("result: " + result); if (result == -1) {
	 * request.setAttribute("errorMsg", "글쓰기에 실패했습니다.");
	 * request.getRequestDispatcher("write.jsp").forward(request, response); } else
	 * { response.sendRedirect("bbs.jsp"); response.sendRedirect("bbsList?group=" +
	 * URLEncoder.encode(groupName, "UTF-8")); } } }
	 */
}