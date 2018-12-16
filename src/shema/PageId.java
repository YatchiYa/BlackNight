package shema;


public class PageId {

	private int fileId;

	private int idX;


	public PageId(int fileId, int idX) {
		this.fileId = fileId;
		this.idX = idX;
	}

	public int getFileId() {
		return fileId;
	}

	public int getIdX() {
		return idX;
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
		if (fileId != other.fileId)
			return false;
		if (idX != other.idX)
			return false;
		return true;
	}
}