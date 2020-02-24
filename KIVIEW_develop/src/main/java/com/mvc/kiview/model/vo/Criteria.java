package com.mvc.kiview.model.vo;

public class Criteria {

	private int page; // 페이지 번호
	private int perPageNum; // 한 페이지에 게시글 몇개 보여줄건지
	private int rowStart; // 한 페이지당 게시글 개수 start
	private int rowEnd; // 한 페이지당 게시글 개수 end
	// ----> start + end 합쳐서 한 페이지 당 총 10개 보여주기

	// searchType, keyword, sort, catd 추가
	private String searchType;
	private String keyword;
	private String sort;
	private String cat_detail;
	private String faqcatd;

	// 생성자
	public Criteria() {
		this.page = 1; // 첫 페이지 번호 1로 셋팅
		this.perPageNum = 10; // 게시글 10개로 셋팅
		this.searchType = null;
		this.keyword = null;
		this.sort = null;
		this.cat_detail = null;
		this.faqcatd = null;
	}

	public void setPage(int page) {

		// 페이지 번호가 없을 때 1로 맞춰줌
		if (page <= 0) {
			this.page = 1;
			return;
		}
		this.page = page;
	}

	// 페이지 개수
	public void setPerPageNum(int pageCount) {

		int cnt = this.perPageNum;
		if (pageCount != cnt) {
			this.perPageNum = cnt;
		} else {
			this.perPageNum = pageCount;
		}
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	public void setCat_detail(String cat_detail) {
		this.cat_detail = cat_detail;
	}
	public void setFaqcatd(String faqcatd) {
		this.faqcatd = faqcatd;
	}

	public String getSearchType() {
		return searchType;
	}

	public String getKeyword() {
		return keyword;
	}

	public String getSort() {
		return sort;
	}
	
	public String getCat_detail() {
		return cat_detail;
	}
	
	public String getFaqcatd() {
		return faqcatd;
	}

	public int getPage() {
		return page;
	}

	// 페이지 시작 번호
	public int getPageStart() {

		return ((this.page - 1) * perPageNum) + 1;
	}

	public int getPerPageNum() {
		return this.perPageNum;
	}

	// 한 페이지당 게시글 수 start
	public int getRowStart() {
		rowStart = ((page - 1) * perPageNum) + 1;
		return rowStart;
	}

	// 한 페이지당 게시글 수 end
	public int getRowEnd() {
		rowEnd = rowStart + perPageNum - 1;
		return rowEnd;
	}

}