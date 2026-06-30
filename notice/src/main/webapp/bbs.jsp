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
<link rel="stylesheet" href="./css/bootstrap.css">
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
	
	//  게시판 그룹명 상단 고정 
	String groupName = request.getParameter("group");
	if (groupName == null) groupName = "자유게시판";
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

		<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1"> <!-- bbs.jsp 페이지에서도 게시판 드롭다운 생성  -->
			<ul class="nav navbar-nav">
				<li><a href="main.jsp">메인</a></li>
					<li class="active dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"> 게시판<span class="caret"></span></a>
							  <ul class="dropdown-menu">
									<li><a href="bbs.jsp?group=자유게시판">자유게시판</a></li>
									<li><a href="bbs.jsp?group=공지게시판">공지게시판</a></li>
						 			<li><a href="bbs.jsp?group=질문게시판">질문게시판</a></li>
							</ul>
					</li>
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


	<!--  게시판 그룹명 상단 고정 -->
	<%-- <div
		style="background-color: white; color: black; border: 1px solid #ddd; padding: 10px; text-align: center; font-size: 18px; font-weight: bold; margin-top: 10px; margin-bottom: 10px;">
		📋
		<%=groupName%>
	</div> --%>
	<%@ include file="groupHeader.jsp" %>

	<div class="container">
		<!-- 게시판 attribute(속성) -->
		<div class="row">
			<table class="table table-striped"
				style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th style="background-color: #eeeeee; text-align: center;">번호</th>
						<th style="background-color: #eeeeee; text-align: center;">제목</th>
						<th style="background-color: #eeeeee; text-align: center;">작성자</th>
						<th style="background-color: #eeeeee; text-align: center;">작성일</th>
						<th style="background-color: #eeeeee; text-align: center;">조회수</th>
						<th style="background-color: #eeeeee; text-align: center;">추천수</th>
						<th style="background-color: #eeeeee; text-align: center;">댓글수</th>
						<th style="background-color: #eeeeee; text-align: center;">공개여부</th>
					</tr>
				</thead>
				<tbody>

					<%
						BbsDAO bbsDAO = new BbsDAO();
						ArrayList<Bbs> list = bbsDAO.getList(pageNumber, groupName);

						// 전체 페이지 수 계산
						int totalCount = bbsDAO.getTotalCount();
						int totalPages = (int) Math.ceil((double) totalCount / 20);

						// 현재 페이지 그룹 계산 (1~10, 11~20 ...)
						int startPage = ((pageNumber - 1) / 10) * 10 + 1;
						int endPage = Math.min(startPage + 9, totalPages);

						for (int i = 0; i < list.size(); i++) {
							
							String bbsDate = list.get(i).getBbsDate(); // 게시글의 작성일 가져옴
							String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()); //오늘 날짜를 "yyyy-MM-dd" 형식으로 만들어줌
							String displayDate; // 들어갈 값
							if (bbsDate.startsWith(today)) { // 게시글 작성일이 오늘 날짜로 시작하는지
								displayDate = bbsDate.substring(11, 13) + "시 " + bbsDate.substring(14, 16) + "분"; // 오늘 작성 글이면 시간과 분만 잘라서 표시
							} else {
								displayDate = bbsDate.substring(0, 10); // 오늘이 아니라면 날짜만 표시
							}
					%>

					<tr>
						<!-- 게시판 attribute(속성) 값  -->
						<td><%=list.get(i).getBbsID()%></td>
						<td><a
							href="view.jsp?bbsID=<%=list.get(i).getBbsID()%>&group=<%=groupName%>">
								<%=list.get(i).getBbsTitle()%></a></td>
						<td><%=list.get(i).getUserID()%></td>

						<td><%=displayDate%></td>
						<td><%=list.get(i).getInquiry()%></td>
						<td><%=list.get(i).getRecommendation()%></td>
						<td><%=list.get(i).getComments()%></td>
						<td><%=list.get(i).getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
			<!-- 페이징 -->
			<div style="text-align: center; margin-top: 10px;">

				<!-- 이전 화살표 -->
				<%
				if (startPage > 1) { // 현재 페이지 숫자
				%>
				<a href="bbs.jsp?pageNumber=<%=startPage - 1%>&group=<%=groupName%>"
					class="btn btn-default">◀</a>
				<%
				}
				%>

				<!-- 페이지 번호 -->
				<%
				for (int i = startPage; i <= endPage; i++) {
				%>
				<%
				if (i == pageNumber) {
				%>
				<a href="bbs.jsp?pageNumber=<%=i%>&group=<%=groupName%>"
					class="btn btn-primary"><%=i%></a>
				<%
				} else {
				%>
				<a href="bbs.jsp?pageNumber=<%=i%>&group=<%=groupName%>"
					class="btn btn-default"><%=i%></a>
				<%
				}
				%>
				<%
				}
				%>
				<!-- 다음 화살표 -->
				<%
				if (endPage < totalPages) {
				%>
				<a href="bbs.jsp?pageNumber=<%=endPage + 1%>&group=<%=groupName%>"
					class="btn btn-default">▶</a>
				<%
				}
				%>

			</div>

			<!-- 글쓰기 버튼 등급별 분기 -->
			<% if (isVerified) { %>
			<a href="write.jsp?group=<%=groupName%>" class="btn btn-primary pull-right">글쓰기</a>
			
			<% } else if (isGuest) {%>
			<a href="#" class="btn btn-default pull-right" onclick="alert('로그인 후 이용 가능합니다.'); return false;">글쓰기</a>
				
			<% } else { %>
			<a href="#" class="btn btn-default pull-right" onclick="alert('실명인증 후 글쓰기가 가능합니다.'); return false;">글쓰기</a>
			
			<% } %>
			
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
		<script src="/notice/js/bootstrap.js"></script>
</body>
</html>