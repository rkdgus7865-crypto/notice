<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
			<% 
			}   
			%>

				<form method="post" action="writeAction" enctype="multipart/form-data" onsubmit="return validateWrite()"> <!--브라우저가 폼을 제출하기 직전 form의 onsubmit 속성을 확인 후  enctype="multipart/form-data"-->
																						<!--submit 이벤트가 발생했을 때 실행할 코드를 지정하는 HTML 속성 // 폼이 제출 submit 될 때 validateWrite()를 실행  -->
				<input type="hidden" name="groupName" value="<%=groupName%>"> 	<!-- 어떤 게시판(그룹)에 글을 쓰는지 writeController로 전달하기 위한 hidden 값 -->
				
				<table class="table table-striped" style="text-align: center; border: 1px solid #dddddd">
				<thead>
					<tr>
						<th colspan="2" style="background-color: #eeeeee; text-align: center;">게시판 글쓰기 양식</th>
					</tr>
				</thead>
				  <tbody>
					  <tr>
							<td><input type="text" id="bbsTitle" class="form-control" placeholder="글 제목" name="bbsTitle" maxlength="50"></td>
					  </tr> <!--input 한줄 입력가능  -->	
					  <tr>
					  		<td><textarea id="bbsContent" class="form-control" placeholder="글 내용" name="bbsContent" maxlength="2048" style="height: 350px;"></textarea></td>
					  </tr>	<!--textarea 여러줄 입력가능  -->	
					  
				  </tbody>
			</table>

				<div class="form-group">
					<label>첨부파일</label> <input type="file" name="uploadFile"
						class="form-control">
				</div>

				<div class="form-group" style="text-align: center;">
					<label> <input type="checkbox" name="bbsPublic" value="1"> 비회원 열람</label>
					<label> <input type="checkbox" name="isNotice"  value="1"> 공지용 게시글</label>
				</div>

			<input type="submit" class="btn btn-primary pull-right" value="글쓰기">
			<a href="bbsList?group=<%=groupName%>" class="btn btn-default pull-right" style="margin-right: 5px;">목록</a>
		</form>

			<script>
				function validateWrite() {
					/* var title 	= document.getElementsByName("bbsTitle")[0].value; 
					var content = document.getElementsByName("bbsContent")[0].value; */
					
					var title   = document.getElementById("bbsTitle").value; // 이름이 bbsTitle인 요소 id 찾아서 그 안에 사용자가 입력한 값을 가져옴
					var content = document.getElementById("bbsContent").value;
					
					if (title.trim() === "") { 
						alert("제목을 입력해주세요.");
						return false;
					}
					if (content.trim() === "") {
						alert("내용을 입력해주세요.");
						return false;
					}
					return true;
				}
			</script>


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