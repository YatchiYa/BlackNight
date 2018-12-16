package shema;

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
}
