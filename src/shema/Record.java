package shema;

import java.util.ArrayList;

public class Record {
	private ArrayList<String> listValues;
	private PageId page;
	private int numSlot;
	
	public Record() {
		listValues = new ArrayList<String>(0);
		this.page = null;
	}
	
	public Record(PageId page,int numSlot) {
		listValues = new ArrayList<String>(0);
		this.page = page;
		this.numSlot = numSlot;
	}
	
	public void setValue(ArrayList<String> liste) {
		listValues = liste;
	}
	
	public PageId getPage() {
		return page;
	}

	public void setPage(PageId page) {
		this.page = page;
	}

	public int getNumSlot() {
		return numSlot;
	}

	public void setNumSlot(int numSlot) {
		this.numSlot = numSlot;
	}

	public ArrayList<String> getListValues() {
		return listValues;
	}

	public String toString() {
		StringBuffer rec = new StringBuffer();
		for(String val : listValues) {
			rec.append(val + "\t|\t");
		}
		return rec.toString();
	}
}


