package shema;


public class PageId {

	private int FileIdx;

	private int PageIdx;


	public PageId(int FileIdx, int PageIdx) {
		this.FileIdx = FileIdx;
		this.PageIdx = PageIdx;
	}

	public int getFileIdx() {
		return FileIdx;
	}

	public int getPageIdx() {
		return PageIdx;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageId other = (PageId) obj;
		if (FileIdx != other.FileIdx)
			return false;
		if (PageIdx != other.PageIdx)
			return false;
		return true;
	}
}