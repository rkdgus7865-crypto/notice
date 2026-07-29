<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<% 	String keyword = (String) request.getAttribute("keyword");       // 검색어 %>
<%  String groupName = (String) request.getAttribute("groupName"); %>
<!-- 검색 UI -->


<div style="text-align: right; margin-bottom: 10px;">
	<form action="bbsList" method="get" style="display: inline-block;">
		<input type="hidden" name="group" value="<%=groupName%>"> <input
			type="hidden" name="pageNumber" value="1"> <select
			name="searchType" style="margin-right: 5px;">
			<option value="title">제목</option>
			<option value="comment">댓글</option>
			<option value="writer">작성자</option>
			<option value="titlecomment">제목+댓글</option>
			<option value="commentwriter">댓글+작성자</option>
			<option value="titlecommentwriter">제목+댓글+작성자</option>
		</select> <input type="text" name="keyword"
			value="<%=keyword != null ? keyword : ""%>" placeholder="검색어 입력"
			style="width: 180px;">
		<button type="submit" class="btn btn-sm btn-primary">검색</button>
	</form>
</div>