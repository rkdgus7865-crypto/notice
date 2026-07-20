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
	request.setAttribute("currentPage", "board");
	%>
	
	<%@ include file="navbar.jsp"%>
	<%@ include file="groupHeader.jsp"%>

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

			<form method="post" action="writeAction"
				enctype="multipart/form-data" onsubmit="return validateWrite()">
				<!--브라우저가 폼을 제출하기 직전 form의 onsubmit 속성을 확인 후  enctype="multipart/form-data"-->
				<!--submit 이벤트가 발생했을 때 실행할 코드를 지정하는 HTML 속성 // 폼이 제출 submit 될 때 validateWrite()를 실행  -->
				<input type="hidden" name="groupName" value="<%=groupName%>">
				<!-- 어떤 게시판(그룹)에 글을 쓰는지 writeController로 전달하기 위한 hidden 값 -->

				<table class="table table-striped"
					style="text-align: center; border: 1px solid #dddddd">
					<thead>
						<tr>
							<th colspan="2"
								style="background-color: #eeeeee; text-align: center;">게시판
								글쓰기 양식</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="text" id="bbsTitle" class="form-control"
								placeholder="글 제목" name="bbsTitle" maxlength="50"></td>
						</tr>
						<!--input 한줄 입력가능  -->
						<tr>
							<td><textarea id="bbsContent" class="form-control"
									placeholder="글 내용" name="bbsContent" maxlength="2048"
									style="height: 350px;"></textarea></td>
						</tr>
						<!--textarea 여러줄 입력가능  -->

					</tbody>
				</table>

				<div class="form-group">
					<label>첨부파일</label> <input type="file" name="uploadFile"
						class="form-control">
				</div>

				<div class="form-group" style="text-align: center;">
					<label> <input type="checkbox" name="bbsPublic" value="1">
						비회원 열람
					</label> <label> <input type="checkbox" name="isNotice" value="1">
						공지용 게시글
					</label>
				</div>

				<input type="submit" class="btn btn-primary pull-right" value="글쓰기">
				<a href="bbsList?group=<%=groupName%>"
					class="btn btn-default pull-right" style="margin-right: 5px;">목록</a>
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

		</div>
	</div>
	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>