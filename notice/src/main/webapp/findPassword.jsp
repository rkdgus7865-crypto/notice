<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>비밀번호 찾기</title>
</head>
<body>
<div class="container">
    <div class="col-lg-4"></div>
    <div class="col-lg-4">
        <div class="jumbotron" style="padding-top: 20px">

            <h3 style="text-align: center;">비밀번호 초기화</h3>

            <!-- 에러 메시지 -->
            <% if (request.getAttribute("errorMsg") != null) { %>
                <script>alert('<%=request.getAttribute("errorMsg")%>')</script>
            <% } %>

            <!-- 임시비밀번호 발급 성공시 -->
            <% if (request.getAttribute("tempPassword") != null) { %>
                <div class="alert alert-success" style="text-align: center;">
                    임시 비밀번호가 발급되었습니다.<br>
                    <b><%= request.getAttribute("tempPassword") %></b><br>
                    로그인 후 비밀번호를 변경해주세요.
                </div>
                <a href="login.jsp" class="btn btn-primary form-control">로그인 하러 가기</a>

            <% } else { %>
                <!-- 입력 폼 -->
                <form method="post" action="findPasswordAction">
                    <div class="form-group">
                        <input type="text" class="form-control" placeholder="아이디" name="userID" maxlength="20">
                    </div>
                    <div class="form-group">
                        <input type="text" class="form-control" placeholder="이름" name="userName" maxlength="20">
                    </div>
                    <input type="submit" class="btn btn-primary form-control" value="임시비밀번호 발급">
                </form>
            <% } %>

        </div>
    </div>
    <div class="col-lg-4"></div>
</div>
</body>
</html>