package controller;

import javax.servlet.*;
import javax.servlet.http.*;

import dao.UserDAO;
import dto.User;

import javax.servlet.annotation.*;
import java.io.*;

@WebServlet("/joinAction") // joinAction 이라는 URL 요청이 오면 joinAction 컨트롤러에서 처리
public class JoinController extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)// HTTP POST 요청(회원가입, 로그인 등)이 들어왔을 때 톰캣이 자동 호출하는 메서드
            throws ServletException, IOException {							       // 서블릿 규약에 의해 doPost() 메서드명을 사용해야 하며 임의로 변경할 수 없음

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        // 이미 로그인된 경우
        HttpSession session = request.getSession();
        String sessionUserID = (String) session.getAttribute("userID");
        if (sessionUserID != null) {
            response.sendRedirect("main.jsp");
            return;
        }

        // join.jsp -> 넘겨준 파라미터 입력값 받기
        String userID       = request.getParameter("userID");
        String userPassword = request.getParameter("userPassword");
        String userName     = request.getParameter("userName");
        String userGender   = request.getParameter("userGender");
        String userEmail    = request.getParameter("userEmail");
        String userAddress  = request.getParameter("userAddress");
        String userPhone    = request.getParameter("userPhone");
        String userDateOfBirth = request.getParameter("userDateOfBirth");
        String userDateOfJoining = request.getParameter("userDateOfJoining");
        
        // isBlank 호출 후 빈값 체크 isEmpty = "" 공백을 입력으로 처리 X isBlank = 공백도 빈값으로 처리 O
        if (isBlank(userID)     || isBlank(userPassword)    || isBlank(userName)   || 
        	isBlank(userGender) || isBlank(userEmail)       || isBlank(userAddress)|| 
        	isBlank(userPhone)  || isBlank(userDateOfBirth) || isBlank(userDateOfJoining))
        	
        {
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
        user.setUserAddress(userAddress);      
        user.setUserPhone(userPhone);           
        user.setUserDateOfBirth(userDateOfBirth); 
        user.setUserDateOfJoining(userDateOfJoining); 
        
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
            response.sendRedirect("main.jsp");
        }
    }
    
    private boolean isBlank(String userInfo) { // 회원가입 파라미터 매개변수로 받아와서 null,isBlank 체크
        return userInfo == null || userInfo.isBlank();
    }
    
}