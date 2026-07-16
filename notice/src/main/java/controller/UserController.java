package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import dao.UserDAO;
import dto.User;

@WebServlet(urlPatterns = { "/joinAction", "/loginAction" })
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String path = request.getServletPath();

		if (path.equals("/joinAction")) {
			join(request, response);
		} else if (path.equals("/loginAction")) {
			login(request, response);
		}
	}

	/**
	 * 회원가입 처리
	 */
	
	private void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		HttpSession session = request.getSession();
		String sessionUserID = (String) session.getAttribute("userID");
		if (sessionUserID != null) {
			response.sendRedirect("main.jsp");
			return;
		}

		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");
		String userName = request.getParameter("userName");
		String userGender = request.getParameter("userGender");
		String userEmail = request.getParameter("userEmail");
		String userAddress = request.getParameter("userAddress");
		String userPhone = request.getParameter("userPhone");
		String userDateOfBirth = request.getParameter("userDateOfBirth");

		if (userID.isBlank() || userPassword.isBlank() || userName.isBlank() || userGender.isBlank()
				|| userEmail.isBlank() || userAddress.isBlank() || userPhone.isBlank() || userDateOfBirth.isBlank()) {
			request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
			request.getRequestDispatcher("join.jsp").forward(request, response);
			return;
		}

		User user = new User();
		user.setUserID(userID);
		user.setUserPassword(userPassword);
		user.setUserName(userName);
		user.setUserGender(userGender);
		user.setUserEmail(userEmail);
		user.setUserAddress(userAddress);
		user.setUserPhone(userPhone);
		user.setUserDateOfBirth(userDateOfBirth);

		String isVerified = request.getParameter("isVerified");
		if ("Y".equals(isVerified)) {
			user.setUserGrade("VERIFIED");
		} else {
			user.setUserGrade("NORMAL");
		}

		UserDAO userDAO = new UserDAO();
		int result = userDAO.join(user);

		if (result == -1) {
			request.setAttribute("errorMsg", "이미 존재하는 아이디입니다.");
			request.getRequestDispatcher("join.jsp").forward(request, response);
		} else {
			session.setAttribute("userID", userID);
			session.setAttribute("userGrade", user.getUserGrade());
			session.setAttribute("userName", userName);
			response.sendRedirect("main.jsp");
		}
	}

	/**
	 * 로그인 처리
	 */
	
	private void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");
		UserDAO userDAO = new UserDAO();
		int result = userDAO.login(userID, userPassword);
		HttpSession session = request.getSession();

		if (result == 1) {
			session.setAttribute("userID", userID);
			session.setAttribute("userGrade", userDAO.getGrade(userID));
			String name = userDAO.getName(userID);
			session.setAttribute("userName", name);
			response.sendRedirect("main.jsp");
		} else {
			String msg = "";
			if (result == 0)
				msg = "비밀번호가 틀립니다.";
			else if (result == -1)
				msg = "존재하지 않는 아이디입니다.";
			else if (result == -2)
				msg = "데이터베이스 오류가 발생했습니다.";
			request.setAttribute("errorMsg", msg);
			request.getRequestDispatcher("login.jsp").forward(request, response);
		}
	}
}