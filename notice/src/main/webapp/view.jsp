<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Bbs"%>
<%@ page import="dto.Comment"%>
<%@ page import="dao.CommentDAO"%>
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
	String userGrade = (String) session.getAttribute("userGrade");
		Bbs bbs = (Bbs) request.getAttribute("bbs");
		ArrayList<Comment> commentList = (ArrayList<Comment>) request.getAttribute("commentList");
		ArrayList<Bbs> bbsList  = (ArrayList<Bbs>) request.getAttribute("bbsList");
		ArrayList<Bbs> bottomNoticeList = (ArrayList<Bbs>) request.getAttribute("bottomNoticeList");
		int bottomPageNumber 	= (Integer) request.getAttribute("bottomPageNumber");
		int totalBottomPages 	= (Integer) request.getAttribute("totalBottomPages");
		int bottomStartPage 	= (Integer) request.getAttribute("bottomStartPage");
		int bottomEndPage 		= (Integer) request.getAttribute("bottomEndPage");
		int bottomStartNumber 	= (Integer) request.getAttribute("bottomStartNumber");
		
		int commentPageNumber = (Integer) request.getAttribute("commentPageNumber");
		int totalCommentPages = (Integer) request.getAttribute("totalCommentPages");
		int commentStartPage  = (Integer) request.getAttribute("commentStartPage");
		int commentEndPage    = (Integer) request.getAttribute("commentEndPage");
		String bottomNoticeOnly = (String) request.getAttribute("bottomNoticeOnly");
		String bottomKeyword = (String) request.getAttribute("bottomKeyword");
		boolean isRecommended = (Boolean) request.getAttribute("isRecommended"); // BbsController view 메소드에서  request.setAttribute("isRecommended", isRecommended); 한 값 받아옴
		 
		request.setAttribute("currentPage", "board");
		
		String listAction = "viewDetail";
	    String extraQuery = "&bbsID=" + bbs.getBbsID();
	    String extraHidden = "<input type='hidden' name='bbsID' value='" + bbs.getBbsID() + "'>";
	%>

	<%@ include file="navbar.jsp"%>
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
		<div style="min-height: 300px; border: 1px solid #ddd; padding: 20px; margin-bottom: 20px; display: flex; flex-direction: column;">
			<div style="flex: 1;">
				<%=bbs.getBbsContent()%>
			</div>

			<!-- 게시글 추천 누르면 추천 UI -> 추천 취소 변경 -->
			<div style="text-align: center; margin-top: 20px;">
				추천수: <b><%=bbs.getRecommendation()%></b> 
				 <a href="recommendAction?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"class="btn <%=isRecommended ? "btn-danger" : "btn-success"%>"><%=isRecommended ? "추천취소" : "추천"%>
				</a>

				<%--  게시글 답글쓰기 버튼 (실명인증 회원만) --%>
				<%
				if (!isGuest && "VERIFIED".equals(userGrade)) {
				%>
				<button type="button" class="btn btn-default"
					onclick="showBbsReplyForm()">답글쓰기</button>
				<%
				}
				%>
			</div>

			<%-- 게시글 답글 작성 폼 (평소엔 숨김, 버튼 누르면 JS로 보이게 함) --%>
			<%
			if (!isGuest && "VERIFIED".equals(userGrade)) {
			%>
			<div id="bbsReplyFormArea"
				style="display: none; margin-top: 15px; border-top: 1px solid #eee; padding-top: 15px;">
				<form method="post" action="writeAction">  <!-- 답글 등록 요청을 writeAction 컨트롤러로 보내는 폼 -->
					<input type="hidden" name="groupName" value="<%=groupName%>">  		 <!-- 어떤 게시판그룹에 등록할지 hidden 값으로 같이 전송 -->
					<input type="hidden" name="parentBbsID" value="<%=bbs.getBbsID()%>"> <!-- 이 답글이 어떤 원본 게시글에 대한 답글인지 원본 글 번호를 hidden 값으로 전송 -->

					<div class="form-group">
						<input type="text" id="bbsTitle" name="bbsTitle"
							class="form-control" placeholder="답글 제목" maxlength="50">
					</div>
					<div class="form-group">
						<textarea id="bbsContent" name="bbsContent" class="form-control"
							rows="4" placeholder="답글 내용을 입력하세요" maxlength="2048"></textarea>
					</div>
					<button type="submit" class="btn btn-primary">답글 등록</button>
					<button type="button" class="btn btn-default"
						onclick="hideBbsReplyForm()">취소</button>
				</form>
			</div>
			<%
			}
			%>

		</div>

	<%-- 	<!-- 첨부파일 -->
		<%
		if (bbs.getOriginalFileName() != null) {
		%>
		<div style="margin-top: 10px;">
			<b>첨부파일:</b> <a
				href="fileDownload?fileName=<%=bbs.getSavedFileName()%>&originalName=<%=java.net.URLEncoder.encode(bbs.getOriginalFileName(), "UTF-8")%>">
				<!-- 파일명을 클릭하면 FileDownloadController(fileDownload) 호출 --> <%=bbs.getOriginalFileName()%>
				<!-- 사용자에게 보여줄 원본 파일명 -->
			</a>
		</div>
		<%
			}
		%> --%>

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
		<h4>
			댓글 (<%=commentList.size()%>)
		</h4>

		<table class="table table-bordered">
		
		<%
   		 CommentDAO commentDAO = new CommentDAO();
		%>
			<%-- 댓글 목록을 순회하며 각 댓글 정보를 그리는 반복문 --%>
		<% 
        for (int i = 0; i < commentList.size(); i++) {
            Comment comment = commentList.get(i);
        %>
		<tr id="commentRow<%=comment.getCommentID()%>">
			 <%-- <td style="width: 10%; padding-center: <%=comment.getCommentStep() * 30%>px;">
  				  <%if (comment.getCommentStep() > 0) {%>ㄴ <%}%><%=comment.getUserID()%>
			</td> --%>
			<td style="width: 15%; padding-left: <%=15 + (comment.getCommentStep() * 30)%>px;
		    <%=comment.getCommentStep() > 0 ? "background-color: #f9f9f9;" : ""%>">
		    <%if (comment.getCommentStep() > 0) {%>
		        <span style="color: #6c7ae0; font-weight: bold;">ㄴ</span>
		    <%}%>
		    <%=comment.getUserID()%>
		</td>
				<td>
					<!-- 댓글 수정 후 댓글 content에 표시 평상시 보이는 내용 --> 
					
		<span id="viewMode<%=comment.getCommentID()%>">  <!-- 비밀 댓글   -->
		<% 
        boolean isSecret = (comment.getSecretComment() == 1);
        boolean canSeeSecret = !isGuest && (userID.equals(comment.getUserID()) || userID.equals(bbs.getUserID()));
        if (isSecret && !canSeeSecret) {
        %> 비밀댓글 
        <%
        } else {
        %> 
        
        <%=comment.getCommentContent()%> 
        <% if (comment.getCommentUpdateDate() != null) { %>
			<span style="color: gray; font-size: 12px;">(수정됨)</span> 
		<% 
		} 
		%>
		
		<%
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
				<td style="width: 15%;"><%=comment.getCommentDate()%></td><td style="width: 15%;">
					<%
					if (!isGuest && userID.equals(comment.getUserID())) {
					%> <a href="#" class="btn btn-xs btn-warning"
					onclick="showEdit(<%=comment.getCommentID()%>); return false;">수정</a>
					<a href="commentDelete?commentID=<%=comment.getCommentID()%>&bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
					class="btn btn-xs btn-danger"
					onclick="return confirm('댓글을 삭제하시겠습니까?')">삭제</a> <% } %>
				</td>
				<!-- 댓글 추천 -->
				
				 <%
      				  boolean isCommentRecommended = (userID != null) && commentDAO.hasRecommended(comment.getCommentID(), userID);
  				 %>
  				 
				<td style="width: 15%; white-space: nowrap;">
				    추천수: <%=comment.getRecommendCount()%>
				    <a href="commentRecommend?commentID=<%=comment.getCommentID()%>&bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
				       class="btn btn-xs <%=isCommentRecommended ? "btn-danger" : "btn-success"%>">
				        <%=isCommentRecommended ? "추천취소" : "추천"%>
				   </a>
				 <%-- 실명인증 회원만 답글 버튼 노출 --%>
			    <% if (!isGuest && "VERIFIED".equals(userGrade)) { %>
			        <a href="#" class="btn btn-xs btn-default"
			           onclick="showReplyForm(<%=comment.getCommentID()%>); return false;">답글</a>
			    <% } %>
				</td>
			</tr>
			
			<!-- 댓글 답글 작성 폼 추가 (</tr> 다음, for문 닫히기 전)  -->
			<% if (!isGuest && "VERIFIED".equals(userGrade)) { %>
			<tr id="replyFormRow<%=comment.getCommentID()%>" style="display:none;">
			    <td colspan="5" style="padding-left: <%=20 + (comment.getCommentStep() * 20)%>px;">
			        <form method="post" action="commentWrite">
			            <input type="hidden" name="bbsID" value="<%=bbs.getBbsID()%>">
			            <input type="hidden" name="groupName" value="<%=groupName%>">
			            <input type="hidden" name="parentCommentID" value="<%=comment.getCommentID()%>">
			            <div class="form-group">
			                <textarea name="commentContent" class="form-control" rows="2"
			                          placeholder="답글을 입력하세요" maxlength="500"></textarea>
			            </div>
			            <label>
			                <input type="checkbox" name="secretComment" value="1"> 비밀댓글
			            </label>
			            <button type="submit" class="btn btn-xs btn-primary">답글 작성</button>
			            <button type="button" class="btn btn-xs btn-default"
			                    onclick="hideReplyForm(<%=comment.getCommentID()%>)">취소</button>
			        </form>
			    </td>
			</tr>
			<% } %>
			
			<%
			}
			%>
		</table>
		
			<!-- 댓글 페이징 -->
			<div style="text-align: center; margin-top: 10px; margin-bottom: 20px;">
			    <!-- 이전 화살표 -->
			    <% if (commentStartPage > 1) { %>
			        <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&commentPage=<%=commentStartPage - 1%>"
			           class="btn btn-default btn-sm">◀</a>
			    <% } %>
			
			    <!-- 페이지 번호 -->
			    <% for (int i = commentStartPage; i <= commentEndPage; i++) { %>
			        <% if (i == commentPageNumber) { %>
			            <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&commentPage=<%=i%>"
			               class="btn btn-primary btn-sm"><%=i%></a>
			        <% } else { %>
			            <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&commentPage=<%=i%>"
			               class="btn btn-default btn-sm"><%=i%></a>
			        <% } %>
			    <% } %>
			
			    <!-- 다음 화살표 -->
			    <% if (commentEndPage < totalCommentPages) { %>
			        <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&commentPage=<%=commentEndPage + 1%>"
			           class="btn btn-default btn-sm">▶</a>
			    <% } %>
			</div>

		<!-- 댓글 작성 폼 (실명인증 회원만) -->
		<%
    if (!isGuest && "VERIFIED".equals(userGrade)) {
    %>
		<form method="post" action="commentWrite">
			<input type="hidden" name="bbsID" value="<%=bbs.getBbsID()%>">
			<input type="hidden" name="groupName" value="<%=groupName%>">
			<div class="form-group">
				<textarea name="commentContent" class="form-control" rows="3"
					placeholder="댓글을 입력하세요" maxlength="500"></textarea>
			</div>

			<label> <input type="checkbox" name="secretComment" value="1">
				비밀댓글
			</label>

			<button type="submit" class="btn btn-primary pull-right">댓글
				작성</button>
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

    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px;">
        <!-- 왼쪽: 공지글/전체글 버튼 -->
        <div>
            <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>&bottomNoticeOnly=true"
               class="btn btn-sm <%=("true".equals(bottomNoticeOnly)) ? "btn-info" : "btn-default"%>">공지글</a>
            <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>"
               class="btn btn-sm <%=(!"true".equals(bottomNoticeOnly)) ? "btn-info" : "btn-default"%>">전체글</a>
        </div>

        <!-- 오른쪽: 검색 폼 -->
        <div>
            <form action="viewDetail" method="get" style="display:inline-block;">
                <input type="hidden" name="bbsID" value="<%=bbs.getBbsID()%>">
                <input type="hidden" name="group" value="<%=groupName%>">
                <input type="hidden" name="bottomPage" value="1">

                <select name="bottomSearchType" style="margin-right:5px;">
                    <option value="title">제목</option>
                    <option value="comment">댓글</option>
                    <option value="writer">작성자</option>
                    <option value="titlecomment">제목+댓글</option>
                    <option value="commentwriter">댓글+작성자</option>
                    <option value="titlecommentwriter">제목+댓글+작성자</option>
                </select>

			                <input type="text" name="bottomKeyword" value="<%=bottomKeyword != null ? bottomKeyword : ""%>"
			                       placeholder="검색어 입력" style="width:180px;">
			                <button type="submit" class="btn btn-sm btn-primary">검색</button>
			            </form>
			        </div>
			    </div>
		
			   <table class="table table-striped"
			    style="text-align: center; border: 1px solid #dddddd; table-layout: fixed; width: 100%;">
			    <thead>
			        <tr>
			            <th style="background-color: #eeeeee; text-align: center; width: 7%;">번호</th>
			            <th style="background-color: #eeeeee; text-align: left; width: 51%;">제목</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 8%;">작성자</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 8%;">작성일</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 6%;">조회수</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 6%;">추천수</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 6%;">댓글수</th>
			            <th style="background-color: #eeeeee; text-align: center; width: 8%;">공개여부</th>
			        </tr>
			    </thead>
			    <tbody id="bbsTableBody">
			    
			<%
			for (int i = 0; i < bottomNoticeList.size(); i++) {
   			Bbs notice = bottomNoticeList.get(i);
			%>
			
		<tr style="background-color: #f9f9f9;">
		    <td>공지</td>
		    <td style="text-align: left; padding-left: 0px;">
		        <a href="viewDetail?bbsID=<%=notice.getBbsID()%>&group=<%=groupName%>" style="font-weight: bold; color: black;">
		            <%=notice.getBbsTitle()%>
		        </a>
		    </td>
		    <td><%=notice.getUserID()%></td>
		    <td><%=notice.getBbsDate()%></td>
		    <td><%=notice.getInquiry()%></td>
		    <td><%=notice.getRecommendation()%></td>
		    <td><%=notice.getComments()%></td>
		    <td><%=notice.getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
		</tr>
<%
}
%>    
			    
    <%
    int bottomReplyNumber = 0; // 하단 목록에서 원글만 세는 카운터
    for (int i = 0; i < bbsList.size(); i++) {// 하단에 보여줄 게시글 목록 원글 + 그 원글들의 답글이 섞여있음i번째 게시글 하나씩 꺼내서 화면에 그림
        Bbs row = bbsList.get(i); // i번째 게시글 객체를 row 라는 변수에 담음
								
        boolean isCurrent = (row.getBbsID() == bbs.getBbsID());  // 지금 상세보기로 보고 있는 게시글과 이 행이 같은 글인지 비교 같으면 true → 이 행을 강조 표시 배경색하기 위한 값
    %>
    <tr <%if (isCurrent) {%> style="background-color: #fffbe6;" <%}%>>
        <td>
        	<%
            if (row.getReplyStep() > 0) {  // replyStep 이 0보다 크면 = 이 글은 답글
            %> 
            <%
            } else {
            %>
               <%=bottomStartNumber - bottomReplyNumber%> 
          		  <%
           			 bottomReplyNumber++;  //  원글일 때만 증가
            	  %>
            <%
            }
            %>
        </td>
        
        <td style="text-align: left; padding-left: <%=(row.getReplyStep() > 0) ? (20 + (row.getReplyStep() - 1) * 20) : 0%>px; word-break: break-all;">
            <%
            if (row.getReplyStep() > 0) {
            %> <span style="color: #6c7ae0; font-weight: bold;">ㄴ</span> <%
            }
            %>
            <a href="viewDetail?bbsID=<%=row.getBbsID()%>&group=<%=groupName%>" style="color: black;">
                <%=row.getBbsTitle()%>
            </a>
        </td>
        <td><%=row.getUserID()%></td>
        <td><%=row.getBbsDate()%></td>
        <td><%=row.getInquiry()%></td>
        <td><%=row.getRecommendation()%></td>
        <td><%=row.getComments()%></td>
        <td><%=row.getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
    </tr>
    <%
    }
    %>
			</tbody>
		</table>
		<!-- 게시글 상세 게시글 목록 페이징  -->
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

	<script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
	<script src="/notice/js/bootstrap.js"></script>

<script>
function showEdit(commentID) {
    document.getElementById('viewMode' + commentID).style.display = 'none';
    document.getElementById('editForm' + commentID).style.display = 'block';
}

function cancelEdit(commentID) {
    document.getElementById('viewMode' + commentID).style.display = 'inline';
    document.getElementById('editForm' + commentID).style.display = 'none';
}

function showReplyForm(commentID) {
    document.getElementById('replyFormRow' + commentID).style.display = '';
}

function hideReplyForm(commentID) {
    document.getElementById('replyFormRow' + commentID).style.display = 'none';
}

function showBbsReplyForm() {
    document.getElementById('bbsReplyFormArea').style.display = '';
}

function hideBbsReplyForm() {
    document.getElementById('bbsReplyFormArea').style.display = 'none';
}

</script>

</body>
</html>