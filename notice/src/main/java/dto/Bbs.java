package dto;

public class Bbs {
	private int bbsID; 					// 게시글 ID PK값
	private String bbsTitle; 			// 게시글 제목
	private String userID;	    		// 게시글 작성자 ID
	private String bbsDate;				// 게시글 작성 날짜 및 시간
	private String bbsContent;			// 게시글 내용
	private int bbsAvailable;			// 게시글 삭제 여부 (0 = 비활성화), (1 = 활성화)
	private int inquiry;				// 게시글 조회수 
	private int recommendation; 		// 게시글 추천수
	private int Comments;				// 게시글 댓글수
	private int isPublic;				// 게시글 회원 비회원 공개 여부 (0 = 회원 공개), (1 = 전체 공개)
	private String groupName;			// 게시판 종류 (자유,공지,질문)
	private boolean isBold; 			// 게시글의 추천수 10개 이상 시 게시글 제목 굶게 표시 하기 위한 필드  (DB에 없고 recommendation, groupName 값으로 그때그때 Java 코드에서만  산되는 값
	private String originalFileName;	// 사용자가 업로드한 원래 파일명 (다운로드 시 표시)
	private String savedFileName;		// 서버에 저장된 실제 파일명 (중복 방지용 UUID 등)

	public int getBbsID() {
		return bbsID;
	}
	public void setBbsID(int bbsID) {
		this.bbsID = bbsID;
	}
	public String getBbsTitle() {
		return bbsTitle;
	}
	public void setBbsTitle(String bbsTitle) {
		this.bbsTitle = bbsTitle;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getBbsDate() {
		return bbsDate;
	}
	public void setBbsDate(String bbsDate) {
		this.bbsDate = bbsDate;
	}
	public String getBbsContent() {
		return bbsContent;
	}
	public void setBbsContent(String bbsContent) {
		this.bbsContent = bbsContent;
	}
	public int getBbsAvailable() {
		return bbsAvailable;
	}
	public void setBbsAvailable(int bbsAvailable) {
		this.bbsAvailable = bbsAvailable;
	}
	public int getInquiry() {
		return inquiry;
	}
	public void setInquiry(int inquiry) {
		this.inquiry = inquiry;
	}
	public int getRecommendation() {
		return recommendation;
	}
	public void setRecommendation(int recommendation) {
		this.recommendation = recommendation;
	}
	public int getComments() {
		return Comments;
	}
	public void setComments(int comments) {
		Comments = comments;
	}
	public int getIsPublic() {
		return isPublic;
	}
	public void setIsPublic(int isPublic) {
		this.isPublic = isPublic;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public boolean getIsBold() { 
	    return isBold; 
	}
	public void setIsBold(boolean isBold) { 
	    this.isBold = isBold; 
	}

	public String getOriginalFileName() {
		return originalFileName;
	}
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}
	public String getSavedFileName() {
		return savedFileName;
	}
	public void setSavedFileName(String savedFileName) {
		this.savedFileName = savedFileName;
	}
}
