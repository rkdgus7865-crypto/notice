<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dao.BbsDAO" %>
<%@ page import="dto.Bbs" %>
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
    /* 세션에서 값 가져오기 */
    String userID    = (String) session.getAttribute("userID");
    String userGrade = (String) session.getAttribute("userGrade");
    boolean isGuest    = (userID == null);
    boolean isVerified = "VERIFIED".equals(userGrade);

    /* BbsController 에서 전달받은 값 */
    ArrayList<Bbs> list = (ArrayList<Bbs>) request.getAttribute("list");
    int totalPages      = (Integer) request.getAttribute("totalPages");
    int startPage       = (Integer) request.getAttribute("startPage");
    int endPage         = (Integer) request.getAttribute("endPage");
    int pageNumber      = (Integer) request.getAttribute("pageNumber");
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
        <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
            <ul class="nav navbar-nav">
                <li><a href="main.jsp">메인</a></li>
                <li class="active dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        게시판<span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="bbsList?group=자유게시판">자유게시판</a></li>
                        <li><a href="bbsList?group=공지게시판">공지게시판</a></li>
                        <li><a href="bbsList?group=질문게시판">질문게시판</a></li>
                    </ul>
                </li>
            </ul>

            <!-- 비회원 -->
            <% if (isGuest) { %>
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        접속하기<span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="login.jsp">로그인</a></li>
                        <li><a href="join.jsp">회원가입</a></li>
                    </ul>
                </li>
            </ul>
            <% } else { %>
            <!-- 로그인 회원 -->
            <ul class="nav navbar-nav navbar-right">
                <li class="dropdown">
                    <a href="#" class="dropdown-toggle" data-toggle="dropdown"
                       role="button" aria-haspopup="true" aria-expanded="false">
                        회원관리<span class="caret"></span>
                    </a>
                    <ul class="dropdown-menu">
                        <li><a href="logoutAction.jsp">로그아웃</a></li>
                    </ul>
                </li>
            </ul>
            <% } %>
        </div>
    </nav>

    <!-- 게시글 상단 고정 groupHeader 내용 가져옴 -->
    <%@ include file="groupHeader.jsp" %>

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
                <tbody id ="bbsTableBody">
                <% /* 컨트롤러 dao 에서 처리 하도록 수정  */
                for (int i = 0; i < list.size(); i++) {
                    String bbsDate = list.get(i).getBbsDate();
                    String today = new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
                    String displayDate;
                    if (bbsDate.startsWith(today)) {
                        displayDate = bbsDate.substring(11, 13) + "시 " + bbsDate.substring(14, 16) + "분";
                    } else {
                        displayDate = bbsDate.substring(0, 10);
                    }
                %>
                    <tr>  <!-- 게시글 목록 화면 출력  -->
                        <td><%= list.get(i).getBbsID() %></td> <!-- 작성자 ID 출력  -->
                        
						<td> <!-- 게시판 목록 제목 클릭 시 ViewController(viewDetail) 호출, 추천수 10개 이상이면 제목을 굵게 표시 7-7 -->
						<a href="viewDetail?bbsID=<%=list.get(i).getBbsID()%>&group=<%=groupName%>"
							style="<%=list.get(i).getIsBold() ? "font-weight: bold; font-size:16px; color:black;" : "color:black;"%>">
								<%=list.get(i).getBbsTitle()%>
						</a></td> <!--  -->
						
						<td><%=list.get(i).getUserID()%></td>
                        <td><%= displayDate %></td>
                        <td><%= list.get(i).getInquiry() %></td>
                        <td><%= list.get(i).getRecommendation() %></td>
                        <td><%= list.get(i).getComments() %></td>
                        <td><%= list.get(i).getIsPublic() == 1 ? "전체공개" : "회원공개" %></td>
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
							// tbody 내용 교체
							var tbody = "";
							for (var i = 0; i < data.list.length; i++) {
								var bbs = data.list[i];
								tbody += "<tr>";
								tbody += "<td>" + bbs.bbsID + "</td>";
										
								// 비동기 방식으로 페이징 넘길때 추천수 10개 이상이면 제목 굶게 수정 7-7
							    var style = bbs.isBold ? "font-weight: bold; font-size:16px; color:black;" : "color:black;";
								tbody += "<td><a href='viewDetail?bbsID=" + bbs.bbsID + "&group=" + currentGroup 
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