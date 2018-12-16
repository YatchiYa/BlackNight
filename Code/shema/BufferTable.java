package shema;

public class BufferTable {
	
	private Frame frame;
	private byte[] buffer;
	
	public BufferTable() {
		this.frame = new Frame();
		this.buffer = new byte[(int)constants.Constants.pageSize];
	}

	public Frame getframe() {
		return frame;
	}
	
	
	
	public void setBuffer(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public String toString() {
		return(frame.toString());
	}
	
	
	public void setframe(Frame frame) {
		this.frame = frame;
	}

	public byte[] getBuffer() {
		return buffer;
	}

	
}
