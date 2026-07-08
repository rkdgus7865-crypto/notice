package dto;

public class Comment {
	private int commentID;
	private int bbsID;
	private String userID;
	private String commentContent;
	private String commentDate;
	private String commentUpdateDate;
	private int commentAvailable;
	private int secretComment;
	private int recommendCount;

	public int getCommentID() {
		return commentID;
	}

	public void setCommentID(int commentID) {
		this.commentID = commentID;
	}

	public int getBbsID() {
		return bbsID;
	}

	public void setBbsID(int bbsID) {
		this.bbsID = bbsID;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getCommentContent() {
		return commentContent;
	}

	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}

	public String getCommentDate() {
		return commentDate;
	}

	public void setCommentDate(String commentDate) {
		this.commentDate = commentDate;
	}

	public String getCommentUpdateDate() {
		return commentUpdateDate;
	}

	public void setCommentUpdateDate(String commentUpdateDate) {
		this.commentUpdateDate = commentUpdateDate;
	}

	public int getCommentAvailable() {
		return commentAvailable;
	}

	public void setCommentAvailable(int commentAvailable) {
		this.commentAvailable = commentAvailable;
	}

	public int getSecretComment() {
		return secretComment;
	}

	public void setSecretComment(int secretComment) {
		this.secretComment = secretComment;
	}

	public int getRecommendCount() {
		return recommendCount;
	}

	public void setRecommendCount(int recommendCount) {
		this.recommendCount = recommendCount;
	}
}