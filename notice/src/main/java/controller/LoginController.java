package controller;

import javax.servlet.*;
import javax.servlet.http.*;

import dao.UserDAO;

import javax.servlet.annotation.*;
import java.io.*;

@WebServlet("/loginAction")
public class LoginController extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) // HTTP POST 요청(회원가입, 로그인 등)이 들어왔을 때 톰캣이 자동 호출하는 메서드
			throws ServletException, IOException {									// 서블릿 규약에 의해 doPost() 메서드명을 사용해야 하며 임의로 변경할 수 없음 

		request.setCharacterEncoding("UTF-8"); // 요청 데이터를 UTF-8(한글) 인코딩으로 처리
		response.setContentType("text/html; charset=UTF-8"); // 응답 데이터를 HTML 형식, UTF-8(한글) 인코딩으로 브라우저에 전송

		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");

		UserDAO userDAO = new UserDAO();
		int result = userDAO.login(userID, userPassword);

		HttpSession session = request.getSession();

		if (result == 1) {
			session.setAttribute("userID", userID);
			session.setAttribute("userGrade", userDAO.getGrade(userID));
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