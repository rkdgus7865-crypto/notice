<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Bbs"%>
<%@ page import="dto.Comment"%>
<%@ page import="java.util.ArrayList"%>



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
	String userGrade = (String) session.getAttribute("userGrade");
	boolean isGuest = (userID == null);
	Bbs bbs = (Bbs) request.getAttribute("bbs");
	ArrayList<Comment> commentList = (ArrayList<Comment>) request.getAttribute("commentList");
	
	ArrayList<Bbs> bbsList = (ArrayList<Bbs>) request.getAttribute("bbsList"); // 7-9
	int bottomPageNumber 	= (Integer) request.getAttribute("bottomPageNumber");
	int totalBottomPages 	= (Integer) request.getAttribute("totalBottomPages");
	int bottomStartPage 	= (Integer) request.getAttribute("bottomStartPage");
	int bottomEndPage 		= (Integer) request.getAttribute("bottomEndPage");
	
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

	<!-- 머리글: 제목, 조회수, 작성일, 작성자 -->
	<div class="container" style="margin-top: 20px;">

		<table class="table table-bordered">
			<tr>
				<td style="width: 10%;"><b>제목</b></td>
				<td style="width: 25%;"><%=bbs.getBbsTitle()%></td>
				<td style="width: 10%;"><b>조회 수</b></td>
				<td style="width: 25%;"><%=bbs.getInquiry()%></td>
			</tr>
			<tr>
				<td><b>작성일</b></td>
				<td><%=bbs.getBbsDate()%></td>
				<td><b>작성자</b></td>
				<td><%=bbs.getUserID()%></td>
			</tr>
		</table>

		<!-- 본문 -->
		<div
			style="min-height: 300px; border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column;">
			<div style="flex: 1;">
				<%=bbs.getBbsContent()%>
			</div>

			<!-- 추천 -->
			<div style="text-align: center; margin-top: 20px;">
				추천수: <b><%=bbs.getRecommendation()%></b>  
				<a href="recommendAction?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
					class="btn btn-success">추천</a>
			</div>
		</div>
		
		
			<!-- 첨부파일 -->
			<%
			if (bbs.getOriginalFileName() != null) {
			%>
			<div style="margin-top: 10px;">
				<b>첨부파일:</b> 
				<a href="fileDownload?fileName=<%=bbs.getSavedFileName()%>&originalName=<%=java.net.URLEncoder.encode(bbs.getOriginalFileName(), "UTF-8")%>">    <!-- 파일명을 클릭하면 FileDownloadController(fileDownload) 호출 -->
				<%=bbs.getOriginalFileName()%>  <!-- 사용자에게 보여줄 원본 파일명 -->
				</a>
			</div>
			<%
			}
			%>

		<!-- 게시판 내용 수정/삭제/목록 버튼 -->
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
	
	<!-- 댓글 영역 -->
	<div class="container" style="margin-top: 20px;">
		
			댓글 (<%=commentList.size()%>)
		

		<table class="table table-bordered">
			<%
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
        %>
			<tr id="commentRow<%=comment.getCommentID()%>">
				<td style="width: 15%;"><%=comment.getUserID()%></td>
				<td>
					<!-- 댓글 수정 후 댓글 content에 표시 평상시 보이는 내용 --> <span
					id="viewMode<%=comment.getCommentID()%>"> <% /*비밀 댓글   */
        boolean isSecret = (comment.getSecretComment() == 1);
        boolean canSeeSecret = !isGuest && (userID.equals(comment.getUserID()) || userID.equals(bbs.getUserID()));
        if (isSecret && !canSeeSecret) {
        %> 비밀댓글 <%
        } else {
        %> <%=comment.getCommentContent()%> <% if (comment.getCommentUpdateDate() != null) { %>
						<span style="color: gray; font-size: 12px;">(수정됨)</span> <% } %> <%
        }
      %>
				</span> <!-- 수정 모드 (평소엔 숨김) -->
					<form id="editForm<%=comment.getCommentID()%>" method="post"
						action="commentUpdate" style="display: none;">
						<input type="hidden" name="commentID"
							value="<%=comment.getCommentID()%>"> <input type="hidden"
							name="bbsID" value="<%=bbs.getBbsID()%>"> <input
							type="hidden" name="groupName" value="<%=groupName%>">
						<textarea name="commentContent" class="form-control" rows="2"
							maxlength="500"><%=comment.getCommentContent()%></textarea>
						<button type="submit" class="btn btn-xs btn-primary"
							style="margin-top: 5px;">저장</button>
						<button type="button" class="btn btn-xs btn-default"
							style="margin-top: 5px;"
							onclick="cancelEdit(<%=comment.getCommentID()%>)">취소</button>
					</form>
				</td>

				<!-- 댓글 내용 수정 삭제  -->
				<td style="width: 15%;"><%=comment.getCommentDate()%></td>
				<td style="width: 15%;">
					<%
					if (!isGuest && userID.equals(comment.getUserID())) {
					%> <a href="#" class="btn btn-xs btn-warning"
					onclick="showEdit(<%=comment.getCommentID()%>); return false;">수정</a>
					<a
					href="commentDelete?commentID=<%=comment.getCommentID()%>&bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
					class="btn btn-xs btn-danger"
					onclick="return confirm('댓글을 삭제하시겠습니까?')">삭제</a> <% } %>
				</td>

				<td style="width: 10%;">추천 <%=comment.getRecommendCount()%> <a
					href="commentRecommend?commentID=<%=comment.getCommentID()%>&bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
					class="btn btn-xs btn-success">👍</a>
				</td>

			</tr>
			<%
			}
			%>
		</table>

		<!-- 댓글 작성 폼 (실명인증 회원만) -->
		<%
    if (!isGuest && "VERIFIED".equals(userGrade)) {
    %>
		<form method="post" action="commentWrite">
			<input type="hidden" name="bbsID" value="<%=bbs.getBbsID()%>">
			<input type="hidden" name="groupName" value="<%=groupName%>">
			<div class="form-group"> <textarea name="commentContent" class="form-control" rows="3" placeholder="댓글을 입력하세요" maxlength="500"></textarea>
			</div>

			<label> <input type="checkbox" name="secretComment" value="1">
				비밀댓글
			</label>

			<button type="submit" class="btn btn-primary pull-right">댓글 작성</button>
		</form>
		<%
		} else if (isGuest) {
		%>
		<p style="color: gray;">로그인 후 댓글 작성이 가능합니다.</p>
		<%
		} else {
		%>
		<p style="color: gray;">실명인증 후 댓글 작성이 가능합니다.</p>
		<%
		}
		%>
	</div>


	<!-- 하단 게시글 목록 7-9 -->
		<div class="container" style="margin-top: 20px;">
			<h4>게시글 목록</h4>
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
					for (int i = 0; i < bbsList.size(); i++) {
						Bbs row = bbsList.get(i);
						boolean isCurrent = (row.getBbsID() == bbs.getBbsID());
					%>   
							 <!--  게시글 상세 목록 화면 출력  -->
					<tr <%if (isCurrent) {%> style="background-color: #fffbe6;"
						<%}%>>
						<td><%=row.getBbsID()%></td>
						<td><a
							href="viewDetail?bbsID=<%=row.getBbsID()%>&group=<%=groupName%>">
								<%=row.getBbsTitle()%>
						</a></td>
						<td><%=row.getUserID()%></td>
						<td><%=row.getBbsDate()%></td>
						<td><%=row.getInquiry()%></td>
						<td><%=row.getRecommendation()%></td>
						<td><%=row.getComments()%></td>
						<td><%=row.getIsPublic() == 1 ? "전체공개" : "회원공개" %></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>
		<!-- 게시글 상세 게시글 목록 페이징 7-9  -->
		<div style="text-align: center; margin-top: 10px;">
			<!-- 이전 화살표 -->
			<%
			if (bottomStartPage > 1) {
			%>
			<a
				href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&bottomPage=<%=bottomStartPage - 1%>"
				class="btn btn-default">◀</a>
			<%
			}
			%>

			<!-- 페이지 번호 -->
			<%
			for (int i = bottomStartPage; i <= bottomEndPage; i++) {
			%>
			<%
			if (i == bottomPageNumber) {
			%>
			<a
				href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&bottomPage=<%=i%>"
				class="btn btn-primary"><%=i%></a>
			<%
			} else {
			%>
			<a
				href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&bottomPage=<%=i%>"
				class="btn btn-default"><%=i%></a>
			<%
			}
			%>
			<%
			}
			%>

			<!-- 다음 화살표 -->
			<%
			if (bottomEndPage < totalBottomPages) {
			%>
			<a
				href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&bottomPage=<%=bottomEndPage + 1%>"
				class="btn btn-default">▶</a>
			<%
			}
			%>
		</div>
	</div>

<script>
function showEdit(commentID) {
    document.getElementById('viewMode' + commentID).style.display = 'none';
    document.getElementById('editForm' + commentID).style.display = 'block';
}
function cancelEdit(commentID) {
    document.getElementById('viewMode' + commentID).style.display = 'inline';
    document.getElementById('editForm' + commentID).style.display = 'none';
}
</script>
	
	

	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>
</body>
</html>