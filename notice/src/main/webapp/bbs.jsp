<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.io.PrintWriter" %>
<%@ page import="dao.BbsDAO" %>
<%@ page import="dto.Bbs" %>
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>게시판</title>
</head>
<body>
	<% /* 세션에서 값 가져오기  26-6-23*/
	String userID = (String) session.getAttribute("userID");/*로그인한 사용자 아이디 가져옴 */
	String userGrade = (String) session.getAttribute("userGrade");/*로그인시 저장한 등급 가져옴 (NORMAL or VERIFIED) */
	boolean isGuest = (userID == null);/*userID가 null이면 비회원 true */
	boolean isVerified = "VERIFIED".equals(userGrade);/* userGrade가 VERIFIED면 인증회원 true */
	
	int pageNumber = 1;
	if (request.getParameter("pageNumber") != null){
		pageNumber = Integer.parseInt(request.getParameter("pageNumber"));
	}
	
	%>
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
				aria-expanded="false">
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span> 
				<span class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="main.jsp">JSP 게시판 웹 사이트</a>
		</div>
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li><a href="main.jsp">메인</a></li>
				<li class="active"><a href="bbs.jsp">게시판</a></li>
			</ul>

			<!-- 비회원 -->
			<%
			if (isGuest) {
			%>
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false"> 접속하기<span class="caret"></span>
				</a>
					<ul class="dropdown-menu">
						<li><a href="login.jsp">로그인</a></li>
						<li><a href="join.jsp">회원가입</a></li>
					</ul></li>
			</ul>
			<%
			} else {
			%>
			<!-- 로그인 회원 -->
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false"> 회원관리<span class="caret"></span>
				</a>
					<ul class="dropdown-menu">
						<li><a href="logoutAction.jsp">로그아웃</a></li>
					</ul></li>
			</ul>
			<%
			}
			%>
		</div>
	</nav>

	<div class="container">
		<div class="row">
			<table class="table table-striped"
				style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align: center;">번호</th>
						<th style="background-color: #eeeeee; text-align: center;">제목</th>
						<th style="background-color: #eeeeee; text-align: center;">작성자</th>
						<th style="background-color: #eeeeee; text-align: center;">작성일</th>
						<th style="background-color: #eeeeee; text-align: center;">조회 수</th>
						<th style="background-color: #eeeeee; text-align: center;">추천 수</th>
						<th style="background-color: #eeeeee; text-align: center;">댓글 수</th>
						
					</tr>
				</thead>
				<tbody>
				<% 
					BbsDAO bbsDAO = new BbsDAO();
					ArrayList<Bbs> list = bbsDAO.getList(pageNumber);
					for(int i = 0; i < list.size(); i++) {
				%>
					<tr> 
						 <td><%= list.get(i).getBbsID() %> </td> <%-- 게시글 번호 --%>
						 <td><a href="view.jsp?bbsID=<%= list.get(i).getBbsID() %>"><%= list.get(i).getBbsTitle()%></a></td> <%-- 게시글 제목 클릭시 view.jsp 로 이동, bbsID를 URL 파라미터로 전달 --%>
						 <td><%= list.get(i).getUserID() %> </td> <%-- 작성자 --%>
						 <td><%= list.get(i).getBbsDate().substring(0, 11) + list.get(i).getBbsDate().substring(11, 13) + "시 " + list.get(i).getBbsDate().substring(14, 16) + "분" %></td> <%-- 작성일 --%>
						 <td><%=list.get(i).getInquiry()%></td>
						 <td><%=list.get(i).getRecommendation()%></td>
						 <td><%=list.get(i).getComments()%></td>
					</tr>
				<%
					}
				%>
				</tbody>
			</table>
			
		<%-- 	<%
			 	if(pageNumber != 1){
			%>
				<a href="bbs.jsp?pageNumber=<%=pageNumber - 1 %>" class="btn btn-success btn-arraw-Left">이전</a>
			<%
			 	} if(bbsDAO.nextPage(pageNumber + 1)){
			%>	
				<a href="bbs.jsp?pageNumber=<%=pageNumber + 1 %>" class="btn btn-success btn-arraw-Left">다음</a>
			<%
			 	}
			%>	 --%>
			<!-- 글쓰기 버튼 등급별 분기 -->
			<%
			if (isVerified) {
			%>
			<a href="write.jsp" class="btn btn-primary pull-right">글쓰기</a>
			<%
			} else if (isGuest) {
			%>
			<a href="#" class="btn btn-default pull-right"
				onclick="alert('로그인 후 이용 가능합니다.'); return false;">글쓰기</a>
			<%
			} else {
			%>
			<a href="#" class="btn btn-default pull-right"
				onclick="alert('실명인증 후 글쓰기가 가능합니다.'); return false;">글쓰기</a>
			<%
			}
			%>
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>