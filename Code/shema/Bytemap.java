package shema;

import java.util.ArrayList;

public class Bytemap {
	private ArrayList<Byte> indiceDuSlot;
	
	public Bytemap() {
		indiceDuSlot = new ArrayList<Byte>(0);
	}

	public ArrayList<Byte> getindiceDuSlot() {
		return indiceDuSlot;
	}

	public void setindiceDuSlot(ArrayList<Byte> indiceDuSlot) {
		this.indiceDuSlot = indiceDuSlot;
	}

	public void addindiceDuSlot(Byte i) {
		indiceDuSlot.add(i);
	}
	
	public void setStatusOccup(int i) {
		indiceDuSlot.set(i, new Byte((byte)1));
	}

	public ArrayList<Byte> getIndiceDuSlot() {
		return indiceDuSlot;
	}

	public void setIndiceDuSlot(ArrayList<Byte> indiceDuSlot) {
		this.indiceDuSlot = indiceDuSlot;
	}
	
	
}
