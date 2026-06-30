<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%
String groupName = request.getParameter("group");
if (groupName == null)
	groupName = "자유게시판";
%>  


<div style="background-color: white; color: black; border: 1px solid #ddd; padding: 10px; text-align: center; font-size: 18px; font-weight: bold; margin-top: 10px; margin-bottom: 10px;">
    <%=groupName%>
</div>