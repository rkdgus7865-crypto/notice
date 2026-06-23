package user;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import java.io.*;

@WebServlet("/joinAction")
public class JoinController extends HttpServlet {

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

        // 입력값 받기
        String userID       = request.getParameter("userID");
        String userPassword = request.getParameter("userPassword");
        String userName     = request.getParameter("userName");
        String userGender   = request.getParameter("userGender");
        String userEmail    = request.getParameter("userEmail");

        // 빈값 체크
        if (userID == null || userPassword == null || userName == null
                || userGender == null || userEmail == null
                || userID.isEmpty() || userPassword.isEmpty()
                || userName.isEmpty() || userGender.isEmpty() || userEmail.isEmpty()) {

            request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
            request.getRequestDispatcher("join.jsp").forward(request, response);
            return;
        }

        // 회원가입 처리
        User user = new User();
        user.setUserID(userID);
        user.setUserPassword(userPassword);
        user.setUserName(userName);
        user.setUserGender(userGender);
        user.setUserEmail(userEmail);

        UserDAO userDAO = new UserDAO();
        int result = userDAO.join(user);

        if (result == -1) {
            request.setAttribute("errorMsg", "이미 존재하는 아이디입니다.");
            request.getRequestDispatcher("join.jsp").forward(request, response);
        } else {
            session.setAttribute("userID", userID);
            response.sendRedirect("main.jsp");
        }
    }
}