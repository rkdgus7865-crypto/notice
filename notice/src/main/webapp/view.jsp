<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Bbs"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>게시글 상세</title>
</head>
<body>
	<%
	String userID = (String) session.getAttribute("userID");
	boolean isGuest = (userID == null);

	Bbs bbs = (Bbs) request.getAttribute("bbs");
	%>
	<nav class="navbar navbar-default">
		<div class="navbar-header">
			<button type="button" class="navbar-toggle collapsed"
				data-toggle="collapse" data-target="#bs-example-navbar-collapse-1"
				aria-expanded="false">
				<span class="icon-bar"></span> <span class="icon-bar"></span> <span
					class="icon-bar"></span>
			</button>
			<a class="navbar-brand" href="main.jsp">JSP 게시판 웹 사이트</a>
		</div>
		<div class="collapse navbar-collapse"
			id="bs-example-navbar-collapse-1">
			<ul class="nav navbar-nav">
				<li class="active"><a href="main.jsp">메인</a></li>

				<!--  게시판 드롭다운 -->
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false"> 게시판<span class="caret"></span>
				</a>
					<ul class="dropdown-menu">
						<li><a href="bbsList?group=자유게시판">자유게시판</a></li>
						<li><a href="bbsList?group=공지게시판">공지게시판</a></li>
						<li><a href="bbsList?group=질문게시판">질문게시판</a></li>
					</ul></li>
			</ul>

		</div>
	</nav>
	<!-- 게시글 상단 고정 groupHeader 내용 가져옴 -->
	<%@ include file="groupHeader.jsp"%>

	<div class="container" style="margin-top: 20px;">

		<!-- 머리글: 제목, 작성자, 작성일, 조회수 -->
		<table class="table table-bordered">
			<tr>
				<th colspan="4" style="font-size: 18px; text-align: center;"><%=bbs.getBbsTitle()%></th>
			</tr>
			<tr>
				<td><b>작성자</b></td>
				<td><%=bbs.getUserID()%></td>
				<td><b>작성일</b></td>
				<td><%=bbs.getBbsDate()%></td>
			</tr>
			<tr>
				<td><b>조회수</b></td>
				<td><%=bbs.getInquiry()%></td>
				<td><b>공개여부</b></td>
				<td><%=bbs.getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
			</tr>
		</table>

		<!-- 본문 -->
		<div
			style="min-height: 300px; border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column;">
			<div style="flex: 1;">
				<%=bbs.getBbsContent()%>
			</div>
			<!-- 추천 -->
			<div
				style="text-align: center; margin-top: 20px; border-top: 1px solid #eee; padding-top: 15px;">
				<span style="font-size: 16px;">추천수: <b><%=bbs.getRecommendation()%></b></span>&nbsp;
				<a href="#" class="btn btn-success">추천</a>
			</div>
		</div>

		<!-- 수정/삭제/목록 버튼 -->
		<div style="text-align: right;">
			<%
					if (!isGuest && userID.equals(bbs.getUserID())) {
			%>
			<a href="editView?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
				class="btn btn-warning">수정</a> <a
				href="deleteAction?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
				class="btn btn-danger" onclick="return confirm('정말 삭제하시겠습니까?')">삭제</a>
			<%
					}
					%>
			<a href="bbsList?group=<%=groupName%>" class="btn btn-default">목록</a>
		</div>

	</div>

	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>