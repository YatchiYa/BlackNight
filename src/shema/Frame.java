package shema;

import java.util.Date;


public class Frame {
	private PageId page;
	private int pinCount;
	private int dirtyFlag;
	private Date timePinCountAtZero;
	
	public Frame(PageId page) {
		this.page = page;
		this.pinCount = 0;
		this.dirtyFlag = 0;
		this.timePinCountAtZero = null;
	}
	
	public Frame() {
		this(null);
	}

	public PageId getPage() {
		return page;
	}

	public void setPage(PageId page) {
		this.page = page;
	}

	public int getPinCount() {
		return pinCount;
	}

	public void setPinCount(int pinCount) {
		this.pinCount = pinCount;
	}

	public int getDirtyFlag() {
		return dirtyFlag;
	}

	public void setDirtyFlag(int dirtyFlag) {
		this.dirtyFlag = dirtyFlag;
	}
	
	public Date getTimePinCountAtZero() {
		return timePinCountAtZero;
	}

	public void setTimePinCountAtZero(Date timePinCountAtZero) {
		this.timePinCountAtZero = timePinCountAtZero;
	}


	public void decrementPinCount() {
		this.pinCount--;
	}


	public void incrementPinCount() {
		this.pinCount++;
	}


	public String toString() {
		return("<page=" + this.page + ",pin=" + this.pinCount + ",dirty=" + this.dirtyFlag + ",time=" + this.timePinCountAtZero + ">\n");
	}
}
