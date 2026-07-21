package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import dao.UserDAO;
import dto.User;

@WebServlet(urlPatterns = { "/joinAction", "/loginAction", "/findPasswordAction", "/changePasswordAction" }) // 이 UserController 클래스는  /joinAction 으로 오는 요청도 받고,  /loginAction 으로 오는 요청도 받겠다" 즉, 하나의 클래스가 URL 2개를 동시에 처리하도록 등록
public class UserController extends HttpServlet {
	private static final long serialVersionUID = 1L; // 무에서 큰 의미 없고 그냥 관례로 붙이는 코드예요.
	
	
	/*
	 * "브라우저가 POST 방식으로 요청을 보내면, Tomcat이 이 doPost 메서드를 자동으로 호출해서 request(받은 정보)와
	 * response(보낼 정보)를 넘겨주고, 혹시 에러 나면 Tomcat한테 처리를 맡긴다"
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) // protected  → 접근 제한자. 같은 패키지나 상속받은 클래스에서 호출 가능 , doPost  → 메서드 이름. HttpServlet이 정해놓은 이름(고정, 변경 불가)
			throws ServletException, IOException {									// 브라우저가 POST 방식으로 요청 보내면 Tomcat이 자동으로 이 메서드를 호출함 
																					// 매개변수 2개: HttpServletRequest request   → 브라우저가 보낸 요청 정보 (파라미터, 세션 등) HttpServletResponse response → 브라우저로 보낼 응답 정보 (redirect, forward 등)
		String path = request.getServletPath(); // 브라우저가 "joinAction" 으로 요청 보냄 -> request 객체 안에 "이 요청 경로는 /joinAction 이야" 라고 자동 기록됨 -> request.getServletPath() 로 그 기록을 꺼내 보는 것

		  if (path.equals("/joinAction")) {
	            join(request, response);
	      } else if (path.equals("/loginAction")) {
	            login(request, response);
	      } else if (path.equals("/findPasswordAction")) {
	            findPassword(request, response);
	      } else if (path.equals("/changePasswordAction")) {
	            changePassword(request, response);
	      }
	   }

	/**
	 * 회원가입 처리
	 */
	
	private void join(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { //private  → 이 UserController 클래스 안에서만 호출 가능 (외부에서 못 부름) 
		request.setCharacterEncoding("UTF-8"); // 브라우저에서 서버로 들어오는 데이터(요청)의 인코딩을 UTF-8로 설정 회원가입 폼에서 "홍길동" 입력하면 → 깨지지 않고 정상적으로 서버가 받음 # 들어오는 데이터(한글 입력값) 안 깨지게 #
		response.setContentType("text/html; charset=UTF-8"); // 서버에서 브라우저로 나가는 응답(HTML)의 타입과 인코딩을 지정 "이 응답은 HTML 문서고, UTF-8로 인코딩되어 있어" 라고 브라우저에게 알려줌  #나가는 응답(HTML 화면)의 한글 안 깨지게#

		HttpSession session = request.getSession(); // request 객체를 통해 현재 사용자의 세션(session)을 가져옴 세션이 이미 있으면 → 그 세션을 가져옴 세션이 없으면 → 새로 하나 만들어서 가져옴
		String sessionUserID = (String) session.getAttribute("userID"); // session(사물함) 안에서 "userID" 라는 이름표가 붙은 값을 꺼내옴 로그인했을 때 session.setAttribute("userID", userID) 로 저장해뒀던 값
																		//  로그인 안 한 상태라면 → sessionUserID = null 로그인 한 상태라면    → sessionUserID = "강현" 같은 값
		if (sessionUserID != null) { // sessionUserID 가 null 이 아니면 (이미 로그인 되어 있는 사용자가 로그인 페이지나 회원가입 페이지에 다시 접근하려고 하면 그럴 필요 없으니깐 main.jsp 로 그냥 보내라)
			response.sendRedirect("main.jsp"); // main.jsp 로 리다이렉트 시킴
			return; //return; 으로 메서드 실행을 여기서 끝냄 (아래 로직 실행 안 함)
		}
		
		// join.jsp 에서 form 태그의 input name 값 들을 받아서 request 요청 해서 getParameter로 받아옴
		String userID = request.getParameter("userID");
		String userPassword = request.getParameter("userPassword");
		String userName = request.getParameter("userName");
		String userGender = request.getParameter("userGender");
		String userEmail = request.getParameter("userEmail");
		String userAddress = request.getParameter("userAddress");
		String userPhone = request.getParameter("userPhone");
		String userDateOfBirth = request.getParameter("userDateOfBirth");

		if (userID.isBlank() || userPassword.isBlank() || userName.isBlank() || userGender.isBlank() // join.jsp 에서 form 태그의 input name 값 null 체크
				|| userEmail.isBlank() || userAddress.isBlank() || userPhone.isBlank() || userDateOfBirth.isBlank()) 
			
		{
			request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다."); // request 객체 안에 "errorMsg" 라는 이름표로 문자열을 담아둠 이제 이 request 객체를 다른 jsp로 넘기면 그 jsp에서도 "errorMsg" 라는 이름으로 이 값을 꺼내 쓸 수 있음
			request.getRequestDispatcher("join.jsp").forward(request, response); //  딱 이번 요청 한 번만 유효 (forward 할 때만 넘어감) 실제로 request, response 를 들고 join.jsp 로 이동
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

	        //  임시비밀번호 여부 체크해서 분기
	        if (userDAO.isTempPassword(userID)) {
	            response.sendRedirect("changePassword.jsp");
	        } else {
	            response.sendRedirect("main.jsp");
	        }

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
	
	/**
	 * 비밀번호 찾기 처리
	 */
	
    private void findPassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String userID    = request.getParameter("userID");
        String userName  = request.getParameter("userName");

        UserDAO userDAO = new UserDAO();
        String tempPassword = userDAO.findPasswordAndReset(userID, userName);
        
        if (tempPassword == null) {
            request.setAttribute("errorMsg", "일치하는 회원 정보가 없습니다.");
            request.getRequestDispatcher("findPassword.jsp").forward(request, response);
        } else {
            request.setAttribute("tempPassword", tempPassword);
            request.getRequestDispatcher("findPassword.jsp").forward(request, response);
        }
    }
    
    /**
	 *  새 비밀번호 설정 처리
	 */
    
    private void changePassword(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");

        String userID = (String) request.getSession().getAttribute("userID");
        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String newPasswordCheck = request.getParameter("newPasswordCheck");
        
        if (newPassword == null || newPassword.isBlank()) {
            request.setAttribute("errorMsg", "새 비밀번호를 입력해주세요.");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }

        //  서버단에서도 비밀번호 일치 확인
        if (!newPassword.equals(newPasswordCheck)) {
            request.setAttribute("errorMsg", "비밀번호가 일치하지 않습니다.");
            request.getRequestDispatcher("changePassword.jsp").forward(request, response);
            return;
        }
        
        UserDAO userDAO = new UserDAO();
        userDAO.changePassword(userID, newPassword);

        response.sendRedirect("main.jsp");
    }
	
}