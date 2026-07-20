<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String userID = null;
String userName = null;
if (session.getAttribute("userID") != null) {
    userID = (String) session.getAttribute("userID");
    userName = (String) session.getAttribute("userName");
}
boolean isGuest = (userID == null);

// 각 JSP가 include 전에 세팅해주는 값 ("main" 또는 "board")
String currentPage = (String) request.getAttribute("currentPage");
if (currentPage == null) currentPage = "";
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
            <li class="<%="main".equals(currentPage) ? "active" : ""%>"><a href="main.jsp">메인</a></li>
            <li class="dropdown <%="board".equals(currentPage) ? "active" : ""%>">
                <a href="#" class="dropdown-toggle"
                data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"> 게시판<span class="caret"></span>
                </a>
                <ul class="dropdown-menu">
                    <li><a href="bbsList?group=자유게시판">자유게시판</a></li>
                    <li><a href="bbsList?group=공지게시판">공지게시판</a></li>
                    <li><a href="bbsList?group=질문게시판">질문게시판</a></li>
                </ul>
            </li>
        </ul>
        <%
        if (isGuest) {
        %>
        <ul class="nav navbar-nav navbar-right">
            <li class="dropdown"><a href="#" class="dropdown-toggle"
                data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"> 접속하기<span class="caret"></span>
            </a>
                <ul class="dropdown-menu">
                    <li><a href="login.jsp">로그인</a></li>
                    <li><a href="join.jsp">회원가입</a></li>
                </ul></li>
        </ul>
        <%
        } else {
        %>
        <ul class="nav navbar-nav navbar-right">
            <li style="padding: 15px 10px; color: black;"><%=userName%>님 환영합니다</li>
            <li class="dropdown"><a href="#" class="dropdown-toggle"
                data-toggle="dropdown" role="button" aria-haspopup="true"
                aria-expanded="false"> 회원관리<span class="caret"></span>
            </a>
                <ul class="dropdown-menu">
                    <li><a href="logoutAction.jsp">로그아웃</a></li>
                </ul></li>
        </ul>
        <%
        }
        %>
    </div>
</nav>