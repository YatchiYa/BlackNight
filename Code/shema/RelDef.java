package shema;


public class RelDef{

	private RelDefShema relDef;
	private int fileIdx, recordSize, slotCount;
	
	public RelDef(RelDefShema relDef) {
		this.relDef = relDef;
	}

	public RelDefShema getrelDef() {
		return relDef;
	}

	public int getfileIdx() {
		return fileIdx;
	}
	
	public int getrecordSize() {
		return recordSize;
	}
	
	public int getslotCount() {
		return slotCount;
	}
	
	public void setfileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}
	
	public void setrelDef(RelDefShema relDef) {
		this.relDef = relDef;
	}
	
	public void setrecordSize(int recordSize) {
		this.recordSize = recordSize;
	}
	
	public void setslotCount(int slotCount) {
		this.slotCount = slotCount;
	}
}
