package user;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/loginAction")
public class LoginController extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 이미 로그인된 경우
        HttpSession session = request.getSession();
        String sessionUserID = (String) session.getAttribute("userID");
        if (sessionUserID != null) {
            response.sendRedirect("main.jsp");
            return;
        }

        // 로그인 처리
        String userID = request.getParameter("userID");
        String userPassword = request.getParameter("userPassword");

        UserDAO userDAO = new UserDAO();
        int result = userDAO.login(userID, userPassword);

        if (result == 1) {
            session.setAttribute("userID", userID);
            response.sendRedirect("main.jsp");

        } else {
            String msg = "";
            if (result == 0)       msg = "비밀번호가 틀립니다.";
            else if (result == -1) msg = "존재하지 않는 아이디입니다.";
            else if (result == -2) msg = "데이터베이스 오류가 발생했습니다.";

            request.setAttribute("errorMsg", msg);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
}