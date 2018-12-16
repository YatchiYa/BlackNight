package shema;

import java.util.ArrayList;

public class Bytemap {
	private ArrayList<Byte> slotStatus;
	
	public Bytemap() {
		slotStatus = new ArrayList<Byte>(0);
	}

	public ArrayList<Byte> getSlotStatus() {
		return slotStatus;
	}

	public void setSlotStatus(ArrayList<Byte> slotStatus) {
		this.slotStatus = slotStatus;
	}

	public void addSlotStatus(Byte status) {
		slotStatus.add(status);
	}
	
	public void setStatusOccup(int indice) {
		slotStatus.set(indice, new Byte((byte)1));
	}
	
	
}
