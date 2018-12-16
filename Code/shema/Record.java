package shema;

import java.util.ArrayList;

public class Record {
	private ArrayList<String> values;
	private PageId pageId;
	private int numeroSlot;
	
	public Record() {
		values = new ArrayList<String>(0);
		this.pageId = null;
	}
	
	public Record(PageId pageId,int numeroSlot) {
		values = new ArrayList<String>(0);
		this.pageId = pageId;
		this.numeroSlot = numeroSlot;
	}
	
	public void setValue(ArrayList<String> liste) {
		values = liste;
	}
	
	public PageId getpageId() {
		return pageId;
	}

	public void setpageId(PageId pageId) {
		this.pageId = pageId;
	}

	public int getnumeroSlot() {
		return numeroSlot;
	}

	public void setnumeroSlot(int numeroSlot) {
		this.numeroSlot = numeroSlot;
	}

	public ArrayList<String> getvalues() {
		return values;
	}

	public String toString() {
		StringBuffer rec = new StringBuffer();
		for(String val : values) {
			rec.append(val + "\t|\t");
		}
		return rec.toString();
	}
}


