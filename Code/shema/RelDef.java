package shema;


public class RelDef{

	private RelDefShema relDef;
	private int fileIdx, sizeOfRecord, count_slot;
	
	public RelDef(RelDefShema relDef, int fileIdx, int sizeOfRecord, int count_slot) {
		this.relDef = relDef;
		this.fileIdx = fileIdx;
		this.sizeOfRecord = sizeOfRecord;
		this.count_slot = count_slot;
	}

	public RelDefShema getrelDef() {
		return relDef;
	}

	public int getfileIdx() {
		return fileIdx;
	}
	
	public int getsizeOfRecord() {
		return sizeOfRecord;
	}
	
	public int getcount_slot() {
		return count_slot;
	}
	
	public void setfileIdx(int fileIdx) {
		this.fileIdx = fileIdx;
	}
	
	public void setrelDef(RelDefShema relDef) {
		this.relDef = relDef;
	}
	
	public void setsizeOfRecord(int sizeOfRecord) {
		this.sizeOfRecord = sizeOfRecord;
	}
	
	public void setcount_slot(int count_slot) {
		this.count_slot = count_slot;
	}
}
