package ManagerFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import constants.Constants;
import shema.BufferTable;
import shema.PageId;


public class BufferManager {
	// singleton
	
	
	private static BufferTable[] bufferPool;
	
	// à vérifié apres !!
	static {
		
		bufferPool = new BufferTable[Constants.frameCount];
		
		for(int i = 0; i<Constants.frameCount ; i++) {
			bufferPool[i] = new BufferTable();
		}
	}
	
	
	
	/**
	 * 
	 * @param iPageId
	 * @return
	 * @throws IOException
	 */
	public static byte[] getPage(PageId iPageId) throws IOException{
		
		
		for(int i = 0; i<Constants.frameCount ; i++) {
			if(iPageId.equals(bufferPool[i].getframe().getPage())) {
				bufferPool[i].getframe().incrementpincount();
				return bufferPool[i].getBuffer();
			}
			if(bufferPool[i].getframe().getPage() == null) {
				byte[] newBuffer = new byte[(int)Constants.pageSize];
				DiskManager.readPage(iPageId, newBuffer);
				bufferPool[i].setBuffer(newBuffer);
				bufferPool[i].getframe().incrementpincount();
				bufferPool[i].getframe().setPage(iPageId);
				return newBuffer;
			}
		}
		
		// a faire une fonction secondaire pour alléger la fonction principale
			return lruSystem(iPageId);
		
	}
	
	public static byte[] lruSystem(PageId iPageId) throws IOException{
		ArrayList<BufferTable> pincount_zero = new ArrayList<BufferTable>(0);
		for(int k = 0; k<Constants.frameCount; k++) {
			if(bufferPool[k].getframe().getpincount() == 0) {
				pincount_zero.add(bufferPool[k]);
			}
		}
		
		if(pincount_zero.size() != 0) {
			BufferTable nouvelleFrame = pincount_zero.get(0);
			
			for(int i = 1; i<pincount_zero.size(); i++) {
				Date tempsCourant = pincount_zero.get(i).getframe().getpincoutInitial();
				Date Lru = nouvelleFrame.getframe().getpincoutInitial();
				
				if(tempsCourant.before(Lru)) {
					nouvelleFrame = pincount_zero.get(i);
				}
			}
			
			if(nouvelleFrame.getframe().getflagDirty() == 1) {
				DiskManager.writePage(nouvelleFrame.getframe().getPage(),nouvelleFrame.getBuffer());
			}
			nouvelleFrame.getframe().setPage(iPageId);
			nouvelleFrame.getframe().incrementpincount();
			byte[] field = new byte[(int)Constants.pageSize];
			DiskManager.readPage(iPageId, field);
			nouvelleFrame.setBuffer(field);
			return field;
		}
		else {
			return null;
		}
		
	}
	
	
	
	
	
	public static void freePage(PageId iPageId,int iIsDirty) {
		for(int i = 0;i<Constants.frameCount;i++) {
			
			if(iPageId.equals(bufferPool[i].getframe().getPage())) {
				bufferPool[i].getframe().decrementpincount();
				
				if(bufferPool[i].getframe().getpincount() == 0) {
					
					bufferPool[i].getframe().setpincoutInitial(new Date());
				}
				if(iIsDirty == 1) {
					
					bufferPool[i].getframe().setflagDirty(iIsDirty);
				}
			}
		}
	}
	
	
	
	
	
	public static void flushAll() throws IOException {
	
		boolean checkSum = true;
		for(int i = 0; i<Constants.frameCount; i++) {
			if(bufferPool[i].getframe().getpincount() != 0) {
				checkSum = false;
			}
		}
		
		if(checkSum) {
			for(int i = 0; i<Constants.frameCount; i++) {
				if(bufferPool[i].getframe().getflagDirty() == 1) {
					DiskManager.writePage(bufferPool[i].getframe().getPage(), bufferPool[i].getBuffer());
				}
				bufferPool[i] = new BufferTable();
			}
		}
		else {
			System.exit(-999);
		}
	}
	
	
	
	public static BufferTable[] getBufferPool() {
		return bufferPool;
	}

	public static void setBufferPool(BufferTable[] bufferPool) {
		BufferManager.bufferPool = bufferPool;
	}
}
