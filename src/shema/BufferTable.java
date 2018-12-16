package shema;

public class BufferTable {
	private static final long K = 4096;
	
	private Frame ti;
	private byte[] buffer;
	
	public BufferTable() {
		this.ti = new Frame();
		this.buffer = new byte[(int)K];
	}

	public Frame getTi() {
		return ti;
	}

	public void setTi(Frame ti) {
		this.ti = ti;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public String toString() {
		return(ti.toString());
	}
}
