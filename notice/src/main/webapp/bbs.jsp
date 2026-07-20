<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Bbs" %> <!--이건 필요해 — ArrayList<Bbs> list 타입 캐스팅  -->
<%@ page import="java.util.ArrayList" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>게시판</title>
</head>
<body>
	<%
	String userGrade = (String) session.getAttribute("userGrade");
	boolean isVerified = "VERIFIED".equals(userGrade);

	ArrayList<Bbs> list = (ArrayList<Bbs>) request.getAttribute("list");
	int totalPages = (Integer) request.getAttribute("totalPages");
	int startPage = (Integer) request.getAttribute("startPage");
	int endPage = (Integer) request.getAttribute("endPage");
	int pageNumber = (Integer) request.getAttribute("pageNumber");
	int startNumber = (Integer) request.getAttribute("startNumber");

	request.setAttribute("currentPage", "board");
	%>
	
	<%@ include file="navbar.jsp"%>
	<%@ include file="groupHeader.jsp"%>

    <div class="container">
        <div class="row">
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
				<tbody id="bbsTableBody">
					<%
				    for (int i = 0; i < list.size(); i++) {
					%>
					<tr>
						<!-- 게시글 목록 화면 출력  -->
						<td><%=startNumber - i%></td>
						<td>
						  <% if (list.get(i).getIsNotice() == 1) { %>
       						 <span style="color: red; font-weight: bold;">[공지]</span> <!-- 공지글은 빨간색으로 표시  -->
  						  <% } %>
							<!-- 게시판 목록 제목 클릭 시 ViewController(viewDetail) 호출, 추천수 10개 이상이면 제목을 굵게 표시 7-7 -->
							<a href="viewDetail?bbsID=<%=list.get(i).getBbsID()%>&group=<%=groupName%>"
							style="<%=list.get(i).getIsBold() ? "font-weight: bold; font-size:16px; color:black;" : "color:black;"%>">
								<%=list.get(i).getBbsTitle()%>
						</a>
						</td>
						<td><%=list.get(i).getUserID()%></td>
						<td><%=list.get(i).getBbsDate()%></td>
						<td><%=list.get(i).getInquiry()%></td>
						<td><%=list.get(i).getRecommendation()%></td>
						<td><%=list.get(i).getComments()%></td>
						<td><%=list.get(i).getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
					</tr>
					<%
					}
					%>
				</tbody>
			</table>

            <!-- 페이징 -->
            <div id="pagingArea" style="text-align: center; margin-top: 10px;">

		    <!-- 이전 화살표 -->
		    <% if (startPage > 1) { %>
		        <a href="#" onclick="loadPage(<%= startPage - 1 %>); return false;"
		           class="btn btn-default">◀</a>
		    <% } %>
		
		    <!-- 페이지 번호 -->
		    <% for (int i = startPage; i <= endPage; i++) { %>
		        <% if (i == pageNumber) { %>
		            <a href="#" onclick="loadPage(<%= i %>); return false;"
		               class="btn btn-primary"><%= i %></a>
		        <% } else { %>
		            <a href="#" onclick="loadPage(<%= i %>); return false;"
		               class="btn btn-default"><%= i %></a>
		        <% } %>
		    <% } %>
		
		    <!-- 다음 화살표 -->
		    <% if (endPage < totalPages) { %>
		        <a href="#" onclick="loadPage(<%= endPage + 1 %>); return false;"
		           class="btn btn-default">▶</a>
		    <% } %>
		
		</div>

            <!-- 글쓰기 버튼 등급별 분기 -->
            <% if (isVerified) { %>
                <a href="write.jsp?group=<%= groupName %>" class="btn btn-primary pull-right">글쓰기</a>
            <% } else if (isGuest) { %>
                <a href="#" class="btn btn-default pull-right"
                   onclick="alert('로그인 후 이용 가능합니다.'); return false;">글쓰기</a>
            <% } else { %>
                <a href="#" class="btn btn-default pull-right"
                   onclick="alert('실명인증 후 글쓰기가 가능합니다.'); return false;">글쓰기</a>
            <% } %>

        </div>
    </div>
    <script src="https://code.jquery.com/jquery-3.1.1.min.js"></script>
    <script src="/notice/js/bootstrap.js"></script>

	<script>
	// 현재 게시판 그룹명
	var currentGroup = "<%=groupName%>";

		function loadPage(pageNumber) {
					$.ajax({
						url : "bbsList",
						type : "GET",
						data : {
							pageNumber : pageNumber,
							group : currentGroup,
							ajax : "true" // Ajax 요청 표시
						},
						success : function(data) {
							 var startNumber = data.startNumber;  // 자유,공지,질문 게시판 목록 각각 번호가 독립
							// tbody 내용 교체
							var tbody = "";
							for (var i = 0; i < data.list.length; i++) {
								var bbs = data.list[i];
								tbody += "<tr>";
								/* tbody += "<td>" + bbs.bbsID + "</td>"; */
								tbody += "<td>" + (startNumber - i) + "</td>";   // 자유,공지,질문 게시판 목록 각각 번호가 독립
								// 비동기 방식으로 페이징 넘길때 추천수 10개 이상이면 제목 굶게 수정 7-7
							    var noticeTag = bbs.isNotice == 1 ? "<span style='color:red; font-weight:bold;'>[공지]</span> " : "";
							    var style = bbs.isBold ? "font-weight: bold; font-size:16px; color:black;" : "color:black;";
								tbody += "<td>" + noticeTag + "<a href='viewDetail?bbsID=" + bbs.bbsID + "&group=" + currentGroup 
  									  + "' style='" + style + "'>" + bbs.bbsTitle + "</a></td>";
								tbody += "<td>" + bbs.userID + "</td>";
								tbody += "<td>" + bbs.bbsDate + "</td>";
								tbody += "<td>" + bbs.inquiry + "</td>";
								tbody += "<td>" + bbs.recommendation + "</td>";
								tbody += "<td>" + bbs.comments + "</td>";
								tbody += "<td>" + (bbs.isPublic == 1 ? "전체공개" : "회원공개")+ "</td>";
								tbody += "</tr>";
							}
							$("#bbsTableBody").html(tbody);

							// 페이징 교체
							var paging = "";
							if (data.startPage > 1) {
								paging += "<a href='#' onclick='loadPage("
										+ (data.startPage - 1)
										+ "); return false;' class='btn btn-default'>◀</a>";
							}
							for (var i = data.startPage; i <= data.endPage; i++) {
								if (i == data.pageNumber) {
									paging += "<a href='#' onclick='loadPage("
											+ i
											+ "); return false;' class='btn btn-primary'>"
											+ i + "</a>";
								} else {
									paging += "<a href='#' onclick='loadPage("
											+ i
											+ "); return false;' class='btn btn-default'>"
											+ i + "</a>";
								}
							}
							if (data.endPage < data.totalPages) {
								paging += "<a href='#' onclick='loadPage("
										+ (data.endPage + 1)
										+ "); return false;' class='btn btn-default'>▶</a>";
							}
							$("#pagingArea").html(paging);
						},
						error : function() {
							alert("게시글을 불러오는데 실패했습니다.");
						}
					});
		}
	</script>

</body>
</html>