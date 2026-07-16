/*
 * package controller;
 * 
 * import javax.servlet.*; import javax.servlet.http.*; import dao.BbsDAO;
 * import javax.servlet.annotation.*; import java.io.*; import
 * java.net.URLEncoder; import java.util.UUID;
 * 
 * @WebServlet("/writeAction")
 * 
 * @MultipartConfig( maxFileSize = 1024 * 1024 * 10, // 파일 하나 최대 10MB
 * maxRequestSize = 1024 * 1024 * 50 // 전체 요청 최대 50MB ) public class
 * WriteController extends HttpServlet {
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
 * request.getParameter("groupName"); // 게시판 종류 String noticeParam =
 * request.getParameter("isNotice"); // 공지용 게시글
 * 
 * int publicValue = (bbsPublic != null) ? 1 : 0; // 체크 O → 1 (전체공개), 체크 X → 0
 * (회원공개) int isNotice = (noticeParam != null) ? 1 : 0; // 빈값 체크 if
 * (bbsTitle.isBlank() || bbsContent.isBlank()) {
 * request.setAttribute("errorMsg", "입력이 안 된 사항이 있습니다.");
 * request.getRequestDispatcher("write.jsp").forward(request, response); return;
 * }
 * 
 * // 파일 업로드 처리를 위한 변수 String originalFileName = null; // uploadFile 여행사진.png
 * String savedFileName = null;
 * 
 * Part filePart = request.getPart("uploadFile"); // uploadFile -> 여행사진.png if
 * (filePart != null && filePart.getSize() > 0) { // 파일을 선택했는지 확인 파일을 안 올렸으면
 * 저장하지 않음 originalFileName = extractFileName(filePart); // 업로드한 파일 이름을 얻음 에를들어
 * 여행사진.png originalFileName -> 여행사진.png savedFileName =
 * UUID.randomUUID().toString() + "_" + originalFileName; // 파일명을 UUID를 붙여 바꾸는 것
 * 여행사진.png -> 6f64d09c-09be-4bcb-aaf7-2f5c83c84f62_여행사진.png // 왜? 같은 이름의 파일이
 * 올라와도 서로 덮어쓰지 않도록 하기 위해
 * 
 * String uploadPath = getServletContext().getRealPath("/uploads"); // 파일 저장 위치를
 * 결정하는 코드 File uploadDir = new File(uploadPath); // uploadPath 에 폴더가 생성되는 건 아니고
 * 이 경로를 다룰 객체만 만든 상태 if (!uploadDir.exists()) uploadDir.mkdirs(); // uploadDir이
 * 실제로 존재하는 폴더인지 확인 존재하지 않으면(!exists) → mkdirs()로 그 경로의 폴더를 새로 생성
 * 
 * filePart.write(uploadPath + File.separator + savedFileName); // 실제로 파일을 디스크에
 * 저장하는 코드 uploadPath(폴더 경로) + File.separator(경로 구분자 "\" 또는 "/") +
 * savedFileName(저장할 파일명) }
 * 
 * // 글쓰기 처리 BbsDAO bbsDAO = new BbsDAO(); //BbsDAO 객체 생성 (DB 연결 준비)
 * System.out.println("groupName: " + groupName);
 * 
 * int result = bbsDAO.write(bbsTitle, userID, bbsContent, publicValue,
 * groupName,originalFileName, savedFileName, isNotice);
 * System.out.println("result: " + result); if (result == -1) {
 * request.setAttribute("errorMsg", "글쓰기에 실패했습니다.");
 * request.getRequestDispatcher("write.jsp").forward(request, response); } else
 * { response.sendRedirect("bbsList?group=" + URLEncoder.encode(groupName,
 * "UTF-8")); } }
 * 
 * private String extractFileName(Part part) { String contentDisp =
 * part.getHeader("content-disposition"); String[] tokens =
 * contentDisp.split(";"); for (String token : tokens) { if
 * (token.trim().startsWith("filename")) { return
 * token.substring(token.indexOf("=") + 2, token.length() - 1); } } return null;
 * } }
 * 
 * 
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
 * 
 */
package backupController;

