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
	public void incrementNbPage() {
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


		int nbPage = buffer.getInt();
		headerPageInfo.setdataPageCount(nbPage);


		for(int i = 0; i<nbPage; i++) {
			Integer idx = new Integer(buffer.getInt());
			Integer nbSlot = new Integer(buffer.getInt());
			
			headerPageInfo.adddx_page_données(idx);
			headerPageInfo.addNbSlotDispo(nbSlot);
		}
	}


	public void writeToBuffer(byte[] headerPage, HeaderPageInfo headerPageInfo) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(headerPage);


		buffer.putInt(headerPageInfo.getdataPageCount());
		
		ArrayList<Integer> idxTab = headerPageInfo.getpageIdx();
		ArrayList<Integer> nbSlotTab = headerPageInfo.getfreeSlots();


		for(int i = 0; i<idxTab.size(); i++) {
			buffer.putInt(idxTab.get(i).intValue());
			buffer.putInt(nbSlotTab.get(i).intValue());
		}
	}
	
	
	
	
}
