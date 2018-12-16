package shema;

import java.util.ArrayList;

public class Record {
	private ArrayList<String> values;
	
	public Record() {
		values = new ArrayList<String>(0);
	}
	
	public Record(PageId pageId,int numeroSlot) {
		values = new ArrayList<String>(0);
	}
	
	public void setValue(ArrayList<String> liste) {
		values = liste;
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


