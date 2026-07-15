package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.util.ArrayList;
import dao.BbsDAO;
import dto.Bbs;

@WebServlet("/bbsList")
public class BbsController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	request.setCharacterEncoding("UTF-8"); // 요청 데이터를 UTF-8(한글) 인코딩으로 처리
        response.setContentType("text/html; charset=UTF-8"); // 응답 데이터를 HTML 형식, UTF-8(한글) 인코딩으로 브라우저에 전송

        // 페이지 번호 받기
        int pageNumber = 1;
        if (request.getParameter("pageNumber") != null) {
            pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
        }

        // 게시판 그룹명 받기
        String groupName = request.getParameter("group");
        if (groupName == null) groupName = "자유게시판";
        
        String ajax = request.getParameter("ajax");

        BbsDAO bbsDAO = new BbsDAO();

        // 게시글 목록 가져오기
        ArrayList<Bbs> list = bbsDAO.getList(pageNumber, groupName);

        // 페이징 계산
        int totalCount = bbsDAO.getTotalCount(groupName); // bbsDAO 클래스에 totalCount 값 전달
        int totalPages = (int) Math.ceil((double) totalCount / 20); // 한 페이지에 보이는 게시글 개수
        int startPage  = ((pageNumber - 1) / 5) * 5 + 1; //페이지 번호 그룹 개수 1,2,3,4,5
        int endPage    = Math.min(startPage + 4, totalPages);
         
        // 그룹 내 순번 계산  // 자유,공지,질문 게시판 목록 각각 번호가 독립
        int startNumber = totalCount - ((pageNumber - 1) * 20);
        
        if ("true".equals(ajax)) {
            // ✅ Ajax 요청이면 JSON 으로 응답
            response.setContentType("application/json; charset=UTF-8");
            PrintWriter out = response.getWriter();

            StringBuilder json = new StringBuilder(); // JSON 문자열 생성
            json.append("{");
            
            json.append("\"totalPages\":" + totalPages + ","); // 페이징 정보 추가
            json.append("\"startPage\":" + startPage + ",");
            json.append("\"endPage\":" + endPage + ",");
            json.append("\"pageNumber\":" + pageNumber + ",");
            json.append("\"startNumber\":" + startNumber + ","); // 자유,공지,질문 게시판 목록 각각 번호가 독립
            json.append("\"groupName\":\"" + groupName + "\","); // 게시판 그룹명 추가
            json.append("\"list\":["); // 게시글 목록 시작 

          for (int i = 0; i < list.size(); i++) { 
                Bbs bbs = list.get(i);
                json.append("{");
                json.append("\"bbsID\":" 	  	  + bbs.getBbsID() + ",");
                json.append("\"bbsTitle\":\""     + bbs.getBbsTitle() + "\",");
                json.append("\"userID\":\""  	  + bbs.getUserID() + "\",");
                json.append("\"bbsDate\":\"" 	  + bbs.getBbsDate() + "\",");
                json.append("\"inquiry\":"        + bbs.getInquiry() + ",");
                json.append("\"recommendation\":" + bbs.getRecommendation() + ",");
                json.append("\"comments\":" 	  + bbs.getComments() + ",");
                json.append("\"isPublic\":" 	  + bbs.getIsPublic() + ",");   
                json.append("\"isBold\":" 	      + bbs.getIsBold() + ",");     // 추천수 10개 이상이면 제목 굶게 수정 7-7
                json.append("\"isNotice\":" 	  + bbs.getIsNotice());        
                json.append("}");
                
				if (i < list.size() - 1)
					json.append(",");
			}

			json.append("]}");
			out.print(json.toString()); // 생성한 JSON 데이터를 클라이언트에 응답

		} else {
         
        // AJAX 요청이 아닌 경우 bbs.jsp 로 전달
        request.setAttribute("list", list);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("startPage", startPage);
        request.setAttribute("endPage", endPage);
        request.setAttribute("pageNumber", pageNumber);
        request.setAttribute("startNumber", startNumber);// 자유,공지,질문 게시판 목록 각각 번호가 독립   
        request.setAttribute("groupName", groupName);

        request.getRequestDispatcher("bbs.jsp").forward(request, response);
        }
    }
}