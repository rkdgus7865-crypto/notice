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
	<%
request.setAttribute("currentPage", "join");
%>
	<%@ include file="navbar.jsp"%>

	<div class="container">
		<div class="col-lg-4"></div>
		<div class="col-lg-4">
			<div class="jumbotron" style="padding-top: 20px">

				<% if (request.getAttribute("errorMsg") != null) { %>
				<script>alert('<%= request.getAttribute("errorMsg") %>')</script>
				<% } %>

				<form method="post" action="joinAction"
					onsubmit="return validateJoin()">
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

			</div>
		</div>
		<div class="col-lg-4"></div>
	</div>
	
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
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
					document.getElementsByName(fields[i].name)[0].focus();
					return false;
				}
			}
			return true;
		}
	</script>

</body>
</html>



