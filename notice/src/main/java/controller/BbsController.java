package controller;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.UUID;
import dao.BbsDAO;
import dao.CommentDAO;
import dto.Bbs;
import dto.Comment;

@WebServlet(urlPatterns = { "/bbsList", "/writeAction", "/viewDetail", "/editView", "/editAction", "/deleteAction" , "/recommendAction"}) // 이 클래스 하나가 게시글 관련 URL 7개를 전부 처리하겠다고 Tomcat에게 등록
@MultipartConfig(maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class BbsController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)  // GET 방식 요청(주소창 입력, 링크 클릭 등)이 오면 이 메서드가 실행됨
			throws ServletException, IOException {
		String path = request.getServletPath();  // 지금 들어온 요청이 6개 URL 중 정확히 어떤 URL인지 문자열로 확인
		// 단순히 정보를 요청라 Get 요청
		if (path.equals("/bbsList")) { 			 	 // 게시판 목록 조회
			list(request, response);
		} else if (path.equals("/viewDetail")) { 	 // 게시판 상세 조회
			view(request, response);
		} else if (path.equals("/editView")) {  	 // 게시글 수정 화면 보여주기
			editView(request, response);
		} else if (path.equals("/deleteAction")) {   // 게시글 삭제
			delete(request, response);
		} else if (path.equals("/recommendAction")) {
            recommendBbs(request, response);   		  // 게시글 추천
        }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)  // POST 방식 요청(폼 제출)이 오면 이 메서드가 실행됨
			throws ServletException, IOException {
		String path = request.getServletPath();
		//글쓰기/수정저장은 폼 데이터 제목, 내용을 전송해야 해서 POST
		if (path.equals("/writeAction")) {			 // 게시글 작성
			write(request, response);
		} else if (path.equals("/editAction")) {     // 게시글 수정
			editAction(request, response);
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 목록
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		int pageNumber = 1; // 페이지 번호 기본값
		if (request.getParameter("pageNumber") != null) { // URL에서 넘어온 페이지 번호로 덮어씀
			pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
		}

		String groupName = request.getParameter("group"); // 어떤 게시판인지 파라미터로 받음
		if (groupName == null)
			groupName = "자유게시판";  // 게시판 지정 안 하고 들어오면 기본값으로 자유게시판 보여줌

		String ajax = request.getParameter("ajax");
		String noticeOnly = request.getParameter("noticeOnly"); // 공지글만 보기 모드인지 여부
		
		String searchType = request.getParameter("searchType"); // 검색 조건 (제목/작성자/댓글 조합)
		String keyword = request.getParameter("keyword"); // 검색어
     	boolean isSearch = (keyword != null && !keyword.trim().isEmpty());  // 검색어가 실제로 입력됐는지 판단 (검색 모드인지 아닌지 결정)
		
		BbsDAO bbsDAO = new BbsDAO(); // 게시글 DB 작업을 담당할 DAO 객체 생성
		ArrayList<Bbs> list;  // 화면에 보여줄 게시글 목록 (아래에서 조건별로 채워질 예정)
		
		if ("true".equals(noticeOnly)) { // 화면에 보여줄 게시글 목록(list) 조회 - 상황별로 3가지 중 하나만 실행됨
	        list = bbsDAO.getNoticeOnlyList(groupName); // 1) 공지글만 보기 모드면 공지글만 조회
	    } else if (isSearch) {
	        list = bbsDAO.searchList(pageNumber, groupName, searchType, keyword);  // 검색어가 있으면 검색 결과 조회
	    } else {
	        list = bbsDAO.getList(pageNumber, groupName); //  둘 다 아니면 일반 전체 목록 조회 (기본값)
	    }

	    ArrayList<Bbs> noticeList = new ArrayList<Bbs>(); // 상단에 고정 표시할 공지글 목록
	    if (!"true".equals(noticeOnly)) {  // 공지글만 보기 모드가 아닐 때만 공지글 목록도 따로 조회
	        noticeList = bbsDAO.getNoticeList(groupName);   // (공지글만 보기 모드일 땐 이미 list 자체가 공지글이라 중복 조회 안 함)
	    }

	    int totalCount; // 페이징 계산에 쓸 전체 게시글 개수 조회
	    if (isSearch) {
	        totalCount = bbsDAO.getSearchTotalCount(groupName, searchType, keyword);  // 검색 중이면 검색 결과 개수로 계산
	    } else {
	        totalCount = bbsDAO.getTotalCount(groupName); // 검색 아니면 전체 게시글 개수로 계산
	    }

		int totalPages = (int) Math.ceil((double) totalCount / 20); // 게시글 20개를 한 페이지에 표시 전체 게시글 수를 20으로 나누어 전체 페이지 수 계산
		int startPage = ((pageNumber - 1) / 5) * 5 + 1;  // 페이지 번호 버튼을 5개 단위로 표시
		int endPage = Math.min(startPage + 4, totalPages); // 시작 페이지부터 최대 5개까지만 표시  마지막 페이지가 전체 페이지 수를 넘지 않도록 제한
		int startNumber = totalCount - ((pageNumber - 1) * 20); // 현재 페이지의 첫 번째 게시글 번호 계산 전체 23개일 때 1페이지 → 23번부터

		if ("true".equals(ajax)) {
	        response.setContentType("application/json; charset=UTF-8");
	        PrintWriter out = response.getWriter();
	        StringBuilder json = new StringBuilder();
	        json.append("{");
	        json.append("\"totalPages\":" + totalPages + ",");
	        json.append("\"startPage\":" + startPage + ",");
	        json.append("\"endPage\":" + endPage + ",");
	        json.append("\"pageNumber\":" + pageNumber + ",");
	        json.append("\"startNumber\":" + startNumber + ",");
	        json.append("\"groupName\":\"" + groupName + "\",");
	        json.append("\"noticeList\":[");
	        
	        for (int i = 0; i < noticeList.size(); i++) {
	            Bbs notice = noticeList.get(i);
	            json.append("{");
	            json.append("\"bbsID\":" + notice.getBbsID() + ",");
	            json.append("\"bbsTitle\":\"" + notice.getBbsTitle() + "\",");
	            json.append("\"userID\":\"" + notice.getUserID() + "\",");
	            json.append("\"bbsDate\":\"" + notice.getBbsDate() + "\",");
	            json.append("\"inquiry\":" + notice.getInquiry() + ",");
	            json.append("\"recommendation\":" + notice.getRecommendation() + ",");
	            json.append("\"comments\":" + notice.getComments() + ",");
	            json.append("\"isPublic\":" + notice.getIsPublic());
	            json.append("}");
	            if (i < noticeList.size() - 1) json.append(",");
	        }
	        json.append("],");

	        json.append("\"list\":[");
	        
	        for (int i = 0; i < list.size(); i++) {
	            Bbs bbs = list.get(i);
	            json.append("{");
	            json.append("\"bbsID\":" + bbs.getBbsID() + ",");
	            json.append("\"bbsTitle\":\"" + bbs.getBbsTitle() + "\",");
	            json.append("\"userID\":\"" + bbs.getUserID() + "\",");
	            json.append("\"bbsDate\":\"" + bbs.getBbsDate() + "\",");
	            json.append("\"inquiry\":" + bbs.getInquiry() + ",");
	            json.append("\"recommendation\":" + bbs.getRecommendation() + ",");
	            json.append("\"comments\":" + bbs.getComments() + ",");
	            json.append("\"isPublic\":" + bbs.getIsPublic() + ",");
	            json.append("\"isBold\":" + bbs.getIsBold() + ",");
	            json.append("\"isNotice\":" + bbs.getIsNotice());
	            json.append("}");
	            
	            if (i < list.size() - 1)
	                json.append(",");
	        }
	        json.append("]}");
	        out.print(json.toString());
	    } else {											 // Controller에서 계산한 데이터를 JSP로 전달
	        request.setAttribute("list", list);				 // 현재 페이지의 일반 게시글 목록 전달
	        request.setAttribute("noticeList", noticeList);  // 공지 게시글 목록 전달
	        request.setAttribute("totalPages", totalPages);  // 전체 페이지 수 전달
	        request.setAttribute("startPage", startPage);    // 페이지 버튼 시작 번호 전달
	        request.setAttribute("endPage", endPage);		 // 페이지 버튼 마지막 번호 전달
	        request.setAttribute("pageNumber", pageNumber);  // 현재 페이지 번호 전달
	        request.setAttribute("startNumber", startNumber);// 게시글 번호 계산용 시작 번호 전달
	        request.setAttribute("groupName", groupName);	 // 현재 게시판 그룹명 전달
	        request.setAttribute("noticeOnly", noticeOnly);  // 공지글만 보기 여부 전달	
	        request.getRequestDispatcher("bbs.jsp").forward(request, response); // request에 담은 데이터를 bbs.jsp로 전달 forward이므로 request 데이터가 유지된 상태로 JSP 실행
	    }
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 작성 
	 */
	
	private void write(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		HttpSession session = request.getSession();
		String userID = (String) session.getAttribute("userID");
		if (userID == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		String bbsTitle = request.getParameter("bbsTitle");
		String bbsContent = request.getParameter("bbsContent");
		String bbsPublic = request.getParameter("bbsPublic");
		String groupName = request.getParameter("groupName");
		String noticeParam = request.getParameter("isNotice");

		int publicValue = (bbsPublic != null) ? 1 : 0;
		int isNotice = (noticeParam != null) ? 1 : 0;

		if (bbsTitle.isBlank() || bbsContent.isBlank()) {
			request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
			request.getRequestDispatcher("write.jsp").forward(request, response);
			return;
		}

		String originalFileName = null;
		String savedFileName = null;

		Part filePart = request.getPart("uploadFile");
		if (filePart != null && filePart.getSize() > 0) {
			originalFileName = extractFileName(filePart);
			savedFileName = UUID.randomUUID().toString() + "_" + originalFileName;
			String uploadPath = getServletContext().getRealPath("/uploads");
			File uploadDir = new File(uploadPath);
			if (!uploadDir.exists())
				uploadDir.mkdirs();
			filePart.write(uploadPath + File.separator + savedFileName);
		}

		BbsDAO bbsDAO = new BbsDAO();
		int result = bbsDAO.write(bbsTitle, userID, bbsContent, publicValue, groupName, originalFileName, savedFileName,
				isNotice);

		if (result == -1) {
			request.setAttribute("errorMsg", "글쓰기에 실패했습니다.");
			request.getRequestDispatcher("write.jsp").forward(request, response);
		} else {
			response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName, "UTF-8"));
		}
	}

	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] tokens = contentDisp.split(";");
		for (String token : tokens) {
			if (token.trim().startsWith("filename")) {
				return token.substring(token.indexOf("=") + 2, token.length() - 1);
			}
		}
		return null;
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 상세보기
	 */
	
	private void view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		int bbsID = Integer.parseInt(request.getParameter("bbsID"));
		String groupName = request.getParameter("group");
		if (groupName == null)
			groupName = "자유게시판";

		HttpSession session = request.getSession();
		String userID = (String) session.getAttribute("userID");
		boolean isGuest = (userID == null);

		String fromRecommend = request.getParameter("fromRecommend");
		BbsDAO bbsDAO = new BbsDAO();
		if (!"true".equals(fromRecommend)) {
			bbsDAO.Inquiry(bbsID);
		}

		Bbs bbs = bbsDAO.getDetail(bbsID);

		if (bbs.getIsPublic() == 0 && isGuest) {
			request.setAttribute("errorMsg", "회원만 열람 가능한 게시글입니다.");
			request.getRequestDispatcher("login.jsp").forward(request, response);
			return;
		}
		request.setAttribute("bbs", bbs);
		request.setAttribute("groupName", groupName);
		
		boolean isRecommended = false;	// 현재 로그인한 사용자가 이 게시글을 추천했는지 여부 기본값 추천 안함
	    if (!isGuest) { // 로그인한 사용자인 경우에만 추천 여부를 확인
	        isRecommended = bbsDAO.hasRecommended(bbsID, userID); // BbsRecommend 테이블에 (bbsID, userID) 조합이 있는지 확
	    }
	    
	    request.setAttribute("isRecommended", isRecommended); // view.jsp로 전달 → 추천 버튼을 추천 또는 추천취소로 표시하는 데 사용
		
	    //  댓글 페이징 
	    int commentPageNumber = 1;
	    if (request.getParameter("commentPage") != null) {
	        commentPageNumber = Integer.parseInt(request.getParameter("commentPage"));
	    }

	    CommentDAO commentDAO = new CommentDAO();
	    ArrayList<Comment> commentList = commentDAO.getList(bbsID, commentPageNumber);
	    int totalCommentCount = commentDAO.getCommentCount(bbsID);
	    int totalCommentPages = (int) Math.ceil((double) totalCommentCount / 20);
	    int commentStartPage = ((commentPageNumber - 1) / 5) * 5 + 1;
	    int commentEndPage = Math.min(commentStartPage + 4, totalCommentPages);
	    
	    request.setAttribute("commentList", commentList);
	    request.setAttribute("commentPageNumber", commentPageNumber);
	    request.setAttribute("totalCommentPages", totalCommentPages);
	    request.setAttribute("commentStartPage", commentStartPage);
	    request.setAttribute("commentEndPage", commentEndPage);

		
		// 하단 게시글 목록 페이징  
		int bottomPageNumber = 1;
		if (request.getParameter("bottomPage") != null) {
			bottomPageNumber = Integer.parseInt(request.getParameter("bottomPage"));
		}

		ArrayList<Bbs> bbsList = bbsDAO.getList(bottomPageNumber, groupName);
		int totalBottomCount = bbsDAO.getTotalCount(groupName);
		int totalBottomPages = (int) Math.ceil((double) totalBottomCount / 20);
		int bottomStartPage = ((bottomPageNumber - 1) / 5) * 5 + 1;
		int bottomEndPage = Math.min(bottomStartPage + 4, totalBottomPages);
		int bottomStartNumber = totalBottomCount - ((bottomPageNumber - 1) * 20);

		request.setAttribute("bbsList", bbsList);
		request.setAttribute("bottomPageNumber", bottomPageNumber);
		request.setAttribute("totalBottomPages", totalBottomPages);
		request.setAttribute("bottomStartPage", bottomStartPage);
		request.setAttribute("bottomEndPage", bottomEndPage);
		request.setAttribute("bottomStartNumber", bottomStartNumber);

		request.getRequestDispatcher("view.jsp").forward(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 수정 화면 보여주기
	 */
	
	private void editView(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String userID = (String) request.getSession().getAttribute("userID");
		if (userID == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int bbsID = Integer.parseInt(request.getParameter("bbsID"));
		String groupName = request.getParameter("group");
		BbsDAO bbsDAO = new BbsDAO();
		Bbs bbs = bbsDAO.getDetail(bbsID);

		if (!userID.equals(bbs.getUserID())) {
			response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName, "UTF-8"));
			return;
		}
		request.setAttribute("bbs", bbs);
		request.setAttribute("groupName", groupName);
		request.getRequestDispatcher("edit.jsp").forward(request, response);
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 수정 저장 처리
	 */
	
	private void editAction(HttpServletRequest request, HttpServletResponse response)
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

		if (result == -1) {
			request.setAttribute("errorMsg", "수정에 실패했습니다.");
			request.getRequestDispatcher("edit.jsp").forward(request, response);
		} else {
			response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" + URLEncoder.encode(groupName, "UTF-8"));
		}
	}

	/**
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException
	 *  게시글 삭제
	 */
	
	private void delete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");

		String userID = (String) request.getSession().getAttribute("userID");
		if (userID == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		int bbsID = Integer.parseInt(request.getParameter("bbsID"));
		String groupName = request.getParameter("group");
		BbsDAO bbsDAO = new BbsDAO();
		Bbs bbs = bbsDAO.getDetail(bbsID);

		if (!userID.equals(bbs.getUserID())) {
			response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group=" + URLEncoder.encode(groupName, "UTF-8"));
			return;
		}
		bbsDAO.delete(bbsID);
		response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName, "UTF-8"));
	}
	

    /**
     * @param request
     * @param response
     * @throws IOException
     *  게시글 추천 
     */
    
    private void recommendBbs(HttpServletRequest request, HttpServletResponse response)
            throws IOException { 
        String userID = (String) request.getSession().getAttribute("userID");

        if (userID == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        int bbsID = Integer.parseInt(request.getParameter("bbsID"));
        String groupName = request.getParameter("group");
        
        BbsDAO bbsDAO = new BbsDAO();
        if (bbsDAO.hasRecommended(bbsID, userID)) {
            bbsDAO.cancelRecommend(bbsID, userID);   // 이미 추천했으면 취소
        } else {
            bbsDAO.recommend(bbsID, userID);          // 안 했으면 추천 
        }
        response.sendRedirect("viewDetail?bbsID=" + bbsID + "&group="
            + URLEncoder.encode(groupName, "UTF-8") + "&fromRecommend=true");
    }
}