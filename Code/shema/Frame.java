package shema;

import java.util.Date;


public class Frame {
	private PageId pageId;
	private int pincount;
	private int flagDirty;
	private Date pincoutInitial;
	
	public Frame(PageId p) {
		this.pageId = p;
		this.pincount = 0;
		this.flagDirty = 0;
		this.pincoutInitial = null;
	}
	
	public Frame() {
		this(null);
	}

	public int getflagDirty() {
		return flagDirty;
	}

	public void setflagDirty(int flagDirty) {
		this.flagDirty = flagDirty;
	}
	

	public int getpincount() {
		return pincount;
	}

	public void setpincount(int pincount) {
		this.pincount = pincount;
	}


	public PageId getPage() {
		return pageId;
	}

	public void setPage(PageId page) {
		this.pageId = page;
	}
	
	
	
	
	
	public Date getpincoutInitial() {
		return pincoutInitial;
	}

	public void setpincoutInitial(Date pincoutInitial) {
		this.pincoutInitial = pincoutInitial;
	}


	public void decrementpincount() {
		this.pincount--;
	}


	public void incrementpincount() {
		this.pincount++;
	}


}
