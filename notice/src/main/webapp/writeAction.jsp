<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="dao.BbsDAO" %>
<% request.setCharacterEncoding("UTF-8"); %>
<jsp:useBean id="dto" class="dto.Bbs" scope="page" /> <!--받는 값 -->  
<jsp:setProperty name="dto" property="bbsTitle" />
<jsp:setProperty name="dto" property="bbsContent" />
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>게시판</title>
</head>
<body>
	<%
		String userID = null;
		if(session.getAttribute("userID") != null){
			userID = (String) session.getAttribute("userID");
		}
		if(userID == null){
			PrintWriter script = response.getWriter();
			script.println("<script>");
			script.println("alert('로그인을 하세요.')");
			script.println("location.href = 'login.jsp'");
			script.println("</script>");
		} else{
			if(dto.getBbsTitle() == null || dto.getBbsContent() == null ){
						PrintWriter script = response.getWriter();
						script.println("<script>");
						script.println("alert('입력이 안 된 사항이 있습니다.')");
						script.println("history.back()");
						script.println("</script>");
					}else {
						BbsDAO bbsDAO = new BbsDAO();
						int result = bbsDAO.write(dto.getBbsTitle(), userID, dto.getBbsContent());
						
						out.println("result = " + result);
						if (result == -1){
							PrintWriter script = response.getWriter();
							script.println("<script>");
							script.println("alert('글쓰기에 실패했습니다.')");
							script.println("history.back()");
							script.println("</script>");
						}
						else {
							PrintWriter script = response.getWriter();
							script.println("<script>");
							script.println("location.href = 'bbs.jsp'");
							script.println("</script>");
							
						}
						
					}
		}
	
		
		
	
	%>
</body>
</html>