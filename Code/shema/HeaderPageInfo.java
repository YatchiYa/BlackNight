package shema;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class HeaderPageInfo {
	private int dataPageCount;
	private ArrayList<Integer> pageIdx;
	private ArrayList<Integer> freeSlots;
	
	public HeaderPageInfo(int dataPageCount) {
		this.dataPageCount = dataPageCount;
		pageIdx = new ArrayList<Integer>(dataPageCount);
		freeSlots = new ArrayList<Integer>(dataPageCount);
	}
	public HeaderPageInfo() {
		this.dataPageCount = 0;
		pageIdx = new ArrayList<Integer>(dataPageCount);
		freeSlots = new ArrayList<Integer>(dataPageCount);
	}
	
	/*public HeaderPageInfo() {
		this(0);
	}*/
	
	

	public int getdataPageCount() {
		return dataPageCount;
	}

	public void setdataPageCount(int dataPageCount) {
		this.dataPageCount = dataPageCount;
	}
	public void incrementnombreDePage() {
		dataPageCount++;
	}
	
	
	
	
	
	public ArrayList<Integer> getpageIdx() {
		return pageIdx;
	}

	public void setpageIdx(ArrayList<Integer> pageIdx) {
		this.pageIdx = pageIdx;
	}
	
	public void adddx_page_données(Integer i) {
		pageIdx.add(i);
	}

	
	
	public ArrayList<Integer> getfreeSlots() {
		return freeSlots;
	}

	public void setfreeSlots(ArrayList<Integer> freeSlots) {
		this.freeSlots = freeSlots;
	}
	
	public void addNbSlotDispo(Integer i) {
		freeSlots.add(i);
	}
	
	public boolean decrementfreeSlots(Integer i) {
		boolean find = false;
		
		int indice = pageIdx.indexOf(i);
		
		if(indice!=-1) {
			int nb = freeSlots.get(indice).intValue();
			nb--;
			Integer newNb = new Integer(nb);
			freeSlots.set(indice, newNb);
			find = true;
		}
		
		return find;
	}
	
	
	
	
	
	
	
	// TRY 
	public void readFromBuffer(byte[] headerPage, HeaderPageInfo headerPageInfo) throws IOException {
		
		ByteBuffer buffer = ByteBuffer.wrap(headerPage);


		int nombreDePage = buffer.getInt();
		headerPageInfo.setdataPageCount(nombreDePage);


		for(int i = 0; i<nombreDePage; i++) {
			Integer indiceX = new Integer(buffer.getInt());
			Integer nbDeSlot = new Integer(buffer.getInt());

			headerPageInfo.addNbSlotDispo(nbDeSlot);
			headerPageInfo.adddx_page_données(indiceX);
		}
	}


	public void writeToBuffer(byte[] headerPage, HeaderPageInfo headerPageInfo) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(headerPage);


		buffer.putInt(headerPageInfo.getdataPageCount());

		ArrayList<Integer> nbDeSlot = headerPageInfo.getfreeSlots();
		ArrayList<Integer> indiceX = headerPageInfo.getpageIdx();


		for(int i = 0; i<indiceX.size(); i++) {
			buffer.putInt(indiceX.get(i).intValue());
			buffer.putInt(nbDeSlot.get(i).intValue());
		}
	}
	
	
	
	
}
