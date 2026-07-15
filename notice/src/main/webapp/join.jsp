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
				<li><a href="main.jsp">메인 </a></li>

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
			<ul class="nav navbar-nav navbar-right">
				<li class="dropdown"><a href="#" class="dropdown-toggle"
					data-toggle="dropdown" role="button" aria-haspopup="true"
					aria-expanded="false">접속하기<span class="caret"></span></a>
					<ul class="dropdown-menu">
						<li class="active"><a href="login.jsp">로그인</a></li>
						<li><a href="join.jsp">회원가입</a></li>
					</ul></li>
			</ul>
		</div>
	</nav>

	<div class="container">
		<div class="col-lg-4"></div>
		<div class="col-lg-4">
			<div class="jumbotron" style="padding-top: 20px">

				<!--  에러 메시지 추가 -->
				<% if (request.getAttribute("errorMsg") != null) { %>
				<script>alert('<%= request.getAttribute("errorMsg") %>')</script>
				<% } %>

				<!--  action 변경 -->
				<form method="post" action="joinAction" onsubmit="return validateJoin()">
					<!-- 회원가입 폼 입력값을 JoinController로 전송 및 joinController 호출-->
					<h3 style="text-align: center;">회원가입</h3>
					<div class="form-group">
						<input type="text" class="form-control" placeholder="아이디"
							name="userID" maxlength="20">
					</div>

					<div class="form-group">
						<input type="password" class="form-control" placeholder="비밀번호"
							name="userPassword" maxlength="20">
					</div>

					<div class="form-group">
						<input type="text" class="form-control" placeholder="이름"
							name="userName" maxlength="20">
					</div>

					<div class="form-group" style="text-align: center;">
						<div class="btn-group" data-toggle="buttons">
							<label class="btn btn-primary active"> <input
								type="radio" name="userGender" autocomplete="off" value="남자"
								checked>남자
							</label> <label class="btn btn-primary active"> <input
								type="radio" name="userGender" autocomplete="off" value="여자"
								checked>여자
							</label>
						</div>
					</div>

					<div class="form-group" style="text-align: center;">
						<label style="display: block; text-align: center;">실명인증 체크</label>
						<input type="checkbox" name="isVerified" value="Y"
							onclick="if(this.checked) alert('실명인증이 완료되었습니다.')">
						<!--실명인증 동의 )  -->
					</div>

					<div class="form-group">
						<input type="email" class="form-control" placeholder="이메일"
							name="userEmail" maxlength="20">
					</div>

					<div class="form-group">
						<input type="text" class="form-control" placeholder="주소"
							name="userAddress" maxlength="20">
					</div>

					<div class="form-group">
						<input type="tel" class="form-control" placeholder="전화번호"
							name="userPhone" maxlength="20">
					</div>
					<div class="form-group">
						<label style="display: block; text-align: center;">생년월일</label> <input
							type="date" class="form-control" placeholder="생년월일"
							name="userDateOfBirth" maxlength="20">
					</div>

					<input type="submit" class="btn btn-primary form-control"
						value="회원가입">
				</form>

				<script>
					function validateJoin() {
						var fields = [ {
							name : "userID",
							label : "아이디"
						}, {
							name : "userPassword",
							label : "비밀번호"
						}, {
							name : "userName",
							label : "이름"
						}, {
							name : "userEmail",
							label : "이메일"
						}, {
							name : "userAddress",
							label : "주소"
						}, {
							name : "userPhone",
							label : "전화번호"
						}, {
							name : "userDateOfBirth",
							label : "생년월일"
						} ];

						for (var i = 0; i < fields.length; i++) {
							var value = document.getElementsByName(fields[i].name)[0].value;
							if (value.trim() === "") {
								alert(fields[i].label + "를 입력해주세요.");
								return false;
							}
						}
						return true;
					}
				</script>
			</div>
		</div>
		<div class="col-lg-4"></div>
	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>