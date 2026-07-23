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
	String userGrade = (String) session.getAttribute("userGrade"); // 세션에서 현재 로그인한 사용자의 등급을 가져옴
	boolean isVerified = "VERIFIED".equals(userGrade);

	//  getAttribute 로 Controller가 다 계산해서 넘겨줌
	ArrayList<Bbs> list       = (ArrayList<Bbs>) request.getAttribute("list");   	  // Controller가 전달한 일반 게시글 목록을 가져옴
	ArrayList<Bbs> noticeList = (ArrayList<Bbs>) request.getAttribute("noticeList");  // Controller가 전달한 공지 게시글 목록을 가져옴
	
	int totalPages   = (Integer) request.getAttribute("totalPages");  // 전체 게시글 페이지 수
	int startPage    = (Integer) request.getAttribute("startPage");   // 현재 페이지 그룹의 시작 페이지 번호
	int endPage      = (Integer) request.getAttribute("endPage");	  // 현재 페이지 그룹의 마지막 페이지 번호
	int pageNumber   = (Integer) request.getAttribute("pageNumber");  // 현재 보고 있는 페이지 번호
	int startNumber  = (Integer) request.getAttribute("startNumber"); // 현재 페이지에서 게시글 시작 번호
	
	String noticeOnly = (String) request.getAttribute("noticeOnly");  // Controller가 전달한 공지글만 보기 여부
	boolean isNoticeOnly = "true".equals(noticeOnly);				  // noticeOnly 값이 "true"이면 공지글만 표시
	
	String searchType = (String) request.getAttribute("searchType"); // 검색 타입 (드롭다운 값)
 	String keyword = (String) request.getAttribute("keyword");       // 검색어
	
	request.setAttribute("currentPage", "board");   // 현재 페이지가 게시판임을 표시
	%>
	
	<%@ include file="navbar.jsp"%>
	<%@ include file="groupHeader.jsp"%>
	
    <div class="container">
        <div class="row">
         <div style="text-align: left; margin-bottom: 10px;">
         <!--  공지만 보기 / 전체 목록 버튼 -->
				<a href="bbsList?group=<%=groupName%>&noticeOnly=true"class="btn btn-sm <%=isNoticeOnly ? "btn-info" : "btn-default"%>">공지글</a> <!--현재 게시판의 공지글만 보도록 bbsList Controller에 noticeOnly=true를 전달 공지글만 보는 상태라면 btn-info로 버튼을 강조 아니면 btn-default  -->
			    <a href="bbsList?group=<%=groupName%>"class="btn btn-sm <%=!isNoticeOnly ? "btn-info" : "btn-default"%>">전체글</a>
			</div>
			
						<!-- 검색 UI -->
				<div style="text-align: right; margin-bottom: 10px;">
				    <form action="bbsList" method="get" style="display:inline-block;">
				        <input type="hidden" name="group" value="<%=groupName%>">
				        <input type="hidden" name="pageNumber" value="1">
				
				        <select name="searchType" style="margin-right:5px;">
				            <option value="title" selected>제목</option>
				            <!-- 나중에 추가 예정
				            <option value="writer">작성자</option>
				            <option value="titleWriter">제목+작성자</option>
				            -->
				        </select>
				
				        <input type="text" name="keyword" value="<%=keyword != null ? keyword : ""%>"
				               placeholder="검색어 입력" style="width:180px;">
				        <button type="submit" class="btn btn-sm btn-primary">검색</button>
				    </form>
				</div>
			
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

			   <%-- 상단 고정 공지글 3개 --%>
				<%
				for (int i = 0; i < noticeList.size(); i++) {
				%>
				<tr style="background-color: #f9f9f9;">
				    <td>공지</td>
				    <td>
				        <% if (noticeList.get(i).getIsBold()) { %>
				            <span style="color: red;">[추천]</span>
				        <% } %>
				        <a href="viewDetail?bbsID=<%=noticeList.get(i).getBbsID()%>&group=<%=groupName%>"
				           style="font-weight: bold; color: black;"> <!-- 공지글이니까 항상 굵게만, 빨간색은 제거 -->
				            <%=noticeList.get(i).getBbsTitle()%>
				        </a>
				    </td>
				    <td><%=noticeList.get(i).getUserID()%></td>
				    <td><%=noticeList.get(i).getBbsDate()%></td>
				    <td><%=noticeList.get(i).getInquiry()%></td>
				    <td><%=noticeList.get(i).getRecommendation()%></td>
				    <td><%=noticeList.get(i).getComments()%></td>
				    <td><%=noticeList.get(i).getIsPublic() == 1 ? "전체공개" : "회원공개"%></td>
				</tr>
				<%
				}
				%>
			
			    <%-- 일반 게시글 목록 --%>
			    <%
			    for (int i = 0; i < list.size(); i++) { // list = 게시글 여러 개가 들어있는 목록 (ArrayList) / 0번째 게시글부터 19번째 게시글까지, 한 번에 하나씩 화면에 보여줌
			    %>
					<tr>
					    <td>
					            <%
					            if ("true".equals(noticeOnly)) {
					            %>
					                공지
					            <%
					            } else {
					            %>
					                <%=startNumber - i%>
					            <%
					            }
					            %>
					   </td>
			        <td>
			            <%
			            if (list.get(i).getIsBold()) { // getIsBold()<- BbsDao에 넣어둔 값  / list에 있는 게시글 목록의 i번째가 추천수가 10개 이상인지 확인 / isBold는 추천수 10개 이상을 값 (DAO에서 계산됨)
			            %> <span style="color: red;">[추천]</span>   <!-- if문 조건이 맞으면 게시글에 제목에 [추천]을 추가   -->
			            <%
			            }
			            %>
			            <a href="viewDetail?bbsID=<%=list.get(i).getBbsID()%>&group=<%=groupName%>"
			                style="<%=list.get(i).getIsNotice() == 1 ? "font-weight: bold; font-size:16px; color:black;" : "color:black;"%>"> <!-- i번째 게시글 제목을 i번째 게시글 번호로 가는 링크를 걸어서 i번째 게시글이 공지글이면 굵게 아니면 평소대로 화면에 출력-->
			                <%=list.get(i).getBbsTitle()%> <!-- i번째 게시글의 제목을 화면에 그대로 출력  -->
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
		var currentGroup = "<%=groupName%>";
		var currentNoticeOnly = "<%=noticeOnly%>"; 
		function loadPage(pageNumber) {
		    $.ajax({
		        url : "bbsList",
		        type : "GET",
		        data : {
		            pageNumber : pageNumber,
		            group : currentGroup,
		            noticeOnly : currentNoticeOnly,
		            ajax : "true" // Ajax 요청 표시
		        },
		        success : function(data) {
		            var startNumber = data.startNumber;  // 자유,공지,질문 게시판 목록 각각 번호가 독립
	
		            var tbody = "";
	
		            //  상단 고정 공지글 3개 먼저 출력
		            for (var i = 0; i < data.noticeList.length; i++) {
		                var notice = data.noticeList[i];
		                tbody += "<tr style='background-color: #f9f9f9;'>";
		                tbody += "<td>공지</td>";
		                tbody += "<td><a href='viewDetail?bbsID=" + notice.bbsID + "&group=" + currentGroup
		                       + "' style='font-weight: bold; color: #d9534f;'>" + notice.bbsTitle + "</a></td>";
		                tbody += "<td>" + notice.userID + "</td>";
		                tbody += "<td>" + notice.bbsDate + "</td>";
		                tbody += "<td>" + notice.inquiry + "</td>";
		                tbody += "<td>" + notice.recommendation + "</td>";
		                tbody += "<td>" + notice.comments + "</td>";
		                tbody += "<td>" + (notice.isPublic == 1 ? "전체공개" : "회원공개") + "</td>";
		                tbody += "</tr>";
		            }
	
		            // 일반 게시글 목록 교체
		            for (var i = 0; i < data.list.length; i++) {
		                var bbs = data.list[i];
		                tbody += "<tr>";
		                tbody += "<td>" + (startNumber - i) + "</td>";
		                var recommendTag = bbs.isBold ? "<span style='color:black; font-weight:bold;'>[추천]</span> " : "";
		                var style = bbs.isNotice == 1 ? "font-weight: bold; font-size:16px; color:black;" : "color:black;";
		                tbody += "<td>" + recommendTag + "<a href='viewDetail?bbsID=" + bbs.bbsID + "&group=" + currentGroup 
		                      + "' style='" + style + "'>" + bbs.bbsTitle + "</a></td>";
		                tbody += "<td>" + bbs.userID + "</td>";
		                tbody += "<td>" + bbs.bbsDate + "</td>";
		                tbody += "<td>" + bbs.inquiry + "</td>";
		                tbody += "<td>" + bbs.recommendation + "</td>";
		                tbody += "<td>" + bbs.comments + "</td>";
		                tbody += "<td>" + (bbs.isPublic == 1 ? "전체공개" : "회원공개") + "</td>";
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