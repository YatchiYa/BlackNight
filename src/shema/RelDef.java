package shema;


public class RelDef{

	private RelDefShema rS;

	private int fileId;

	private int recordSize;

	private int slotCount;
	
	public RelDef(RelDefShema rS, int fileId, int recordSize, int slotCount) {
		this.rS = rS;
		this.fileId = fileId;
		this.recordSize = recordSize;
		this.slotCount = slotCount;
	}

	public RelDefShema getrS() {
		return rS;
	}

	public int getFileId() {
		return fileId;
	}
	
	public int getRecordSize() {
		return recordSize;
	}
	
	public int getSlotCount() {
		return slotCount;
	}
	
	public void setFileId(int fileId) {
		this.fileId = fileId;
	}
	
	public void setrS(RelDefShema rS) {
		this.rS = rS;
	}
	
	public void setRecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
	
	public void setSlotCount(int slotCount) {
		this.slotCount = slotCount;
	}
}
