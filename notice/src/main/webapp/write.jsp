<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
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
	
	<%@ include file="groupHeader.jsp" %> <!-- 게시글 상단 고정 groupHeader 내용 가져옴  -->
	
	<div class="container">
		<div class="row">

			<!-- WriteController 에서 전달한 errorMsg 를 받아서 alert 띄움 -->
			<%
			if (request.getAttribute("errorMsg") != null) {
			%>
			<script>alert('<%=request.getAttribute("errorMsg")%>')</script>
			<%  }   %>
			
			

			<form method="post" action="writeAction">
				
				<input type="hidden" name="groupName" value="<%=groupName%>"> 	<!-- 어떤 게시판(그룹)에 글을 쓰는지 writeController로 전달하기 위한 hidden 값 -->
				
				<table class="table table-striped" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="2" style="background-color: #eeeeee; text-align: center;">게시판 글쓰기 양식</th>
					</tr>
				</thead>
				  <tbody>
					  <tr>
							<td><input type="text" class="form-control" placeholder="글 제목" name="bbsTitle" maxlength="50"></td>
					  </tr>
					  <tr>
					  		<td><textarea class="form-control" placeholder="글 내용" name="bbsContent" maxlength="2048" style="height: 350px;"></textarea></td>
					  </tr>
				  </tbody>
			</table>
			
				<div class="form-group" style="text-align: center;">
					<label> <input type="checkbox" name="bbsPublic" value="1"> 비회원 열람 허용</label>
				</div>
				    
			<input type="submit" class="btn btn-primary pull-right" value="글쓰기">
		</form>

		<%-- 	<!-- 글쓰기 버튼 등급별 분기 -->
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
			%> --%>
		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>