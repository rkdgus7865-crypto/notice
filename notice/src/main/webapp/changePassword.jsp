<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>비밀번호 변경</title>
</head>
<body>
<div class="container">
    <div class="col-lg-4"></div>
    <div class="col-lg-4">
        <div class="jumbotron" style="padding-top: 20px">

            <h3 style="text-align: center;">새 비밀번호 설정</h3>

            <!-- 에러 메시지 -->
            <% if (request.getAttribute("errorMsg") != null) { %>
                <script>alert('<%=request.getAttribute("errorMsg")%>')</script>
            <% } %>

				<form method="post" action="changePasswordAction" onsubmit="return validatePassword()">
					
					<div class="form-group">
						<input type="password" class="form-control" placeholder="새 비밀번호" name="newPassword" id="newPassword" maxlength="20">
					</div>
					
					<div class="form-group">
						<input type="password" class="form-control" placeholder="새 비밀번호 확인" name="newPasswordCheck"id="newPasswordCheck" maxlength="20">
					</div>
					
					<input type="submit" class="btn btn-primary form-control" value="비밀번호 변경">
				</form>

				<script>
					function validatePassword() {
						var newPassword = document.getElementById("newPassword").value;
						var newPasswordCheck = document.getElementById("newPasswordCheck").value;

						if (newPassword !== newPasswordCheck) {
							alert("비밀번호가 일치하지 않습니다.");
							return false;
						}
						return true;
					}
				</script>
			</div>
    </div>
    <div class="col-lg-4"></div>
</div>
</body>
</html>