package shema;


public class PageId {

	private int FileIdx;
	private int PageIdx;


	public PageId(int FileIdx, int PageIdx) {
		this.FileIdx = FileIdx;
		this.PageIdx = PageIdx;
	}

	public void setFileIdx(int fileIdx) {
		FileIdx = fileIdx;
	}

	public void setPageIdx(int pageIdx) {
		PageIdx = pageIdx;
	}

	public int getFileIdx() {
		return FileIdx;
	}

	public int getPageIdx() {
		return PageIdx;
	}

	
}