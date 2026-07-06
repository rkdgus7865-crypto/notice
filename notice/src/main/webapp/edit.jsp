<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="dto.Bbs" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="/notice/css/bootstrap.css">
<title>게시글 수정</title>
</head>
<body>
<%
    Bbs bbs = (Bbs) request.getAttribute("bbs");
    String groupName = (String) request.getAttribute("groupName");
%>
<div class="container" style="margin-top: 20px;">

    <% if (request.getAttribute("errorMsg") != null) { %>
        <script>alert('<%=request.getAttribute("errorMsg")%>')</script>
    <% } %>

    <form method="post" action="editAction">
        <input type="hidden" name="bbsID" value="<%=bbs.getBbsID()%>">
        <input type="hidden" name="groupName" value="<%=groupName%>">

        <table class="table table-bordered">
            <tr>
                <th style="background-color: #eeeeee; text-align: center;">게시글 수정</th>
            </tr>
            <tr>
                <td>
                    <input type="text" class="form-control" name="bbsTitle"
                           value="<%=bbs.getBbsTitle()%>" maxlength="50">
                </td>
            </tr>
            <tr>
                <td>
                    <textarea class="form-control" name="bbsContent"
                              style="height: 350px;"><%=bbs.getBbsContent()%></textarea>
                </td>
            </tr>
        </table>

        <div style="text-align: right;">
            <input type="submit" class="btn btn-primary" value="수정 완료">
            <a href="viewDetail?bbsID=<%=bbs.getBbsID()%>&group=<%=groupName%>" class="btn btn-default">취소</a>
        </div>
    </form>
</div>
</body>
</html>