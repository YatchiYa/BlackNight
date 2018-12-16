package ManagerFolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import constants.Constants;
import shema.BufferTable;
import shema.PageId;


public class BufferManager {

	private static BufferTable[] bufferPool;
	
	static {
		
		bufferPool = new BufferTable[Constants.frameCount];
		
		for(int i = 0; i<Constants.frameCount ; i++) {
			bufferPool[i] = new BufferTable();
		}
	}
	
	
	
	
	public static byte[] getPage(PageId page) throws IOException{
		
		for(int j = 0; j<Constants.frameCount ; j++) {
			if(page.equals(bufferPool[j].getTi().getPage())) {
				bufferPool[j].getTi().incrementPinCount();
				return bufferPool[j].getBuffer();
			}
			if(bufferPool[j].getTi().getPage() == null) {
				byte[] bufferPage = new byte[(int)Constants.pageSize];
				DiskManager.readPage(page, bufferPage);
				bufferPool[j].setBuffer(bufferPage);
				bufferPool[j].getTi().incrementPinCount();
				bufferPool[j].getTi().setPage(page);
				return bufferPage;
			}
		}
		ArrayList<BufferTable> framePinCountZero = new ArrayList<BufferTable>(0);
		for(int i = 0; i<Constants.frameCount; i++) {
			if(bufferPool[i].getTi().getPinCount() == 0) {
				framePinCountZero.add(bufferPool[i]);
			}
		}

		if(framePinCountZero.size() != 0) {
			BufferTable frameAReplacer = framePinCountZero.get(0);
			
			for(int i = 1; i<framePinCountZero.size(); i++) {
				Date timePinCountZeroCourant = framePinCountZero.get(i).getTi().getTimePinCountAtZero();
				Date timeLru = frameAReplacer.getTi().getTimePinCountAtZero();
				
				if(timePinCountZeroCourant.before(timeLru)) {
					frameAReplacer = framePinCountZero.get(i);
				}
			}
			
			if(frameAReplacer.getTi().getDirtyFlag() == 1) {
				DiskManager.writePage(frameAReplacer.getTi().getPage(),frameAReplacer.getBuffer());
			}
			frameAReplacer.getTi().setPage(page);
			frameAReplacer.getTi().incrementPinCount();
			byte[] contenuPage = new byte[(int)Constants.pageSize];
			DiskManager.readPage(page, contenuPage);
			frameAReplacer.setBuffer(contenuPage);
			
			return contenuPage;
		}
		else {
			System.out.println("*** Les pages sont en cours d'utilisation ! ***");
			return null;
		}
	}
	
	
	
	
	
	
	
	public static void freePage(PageId page,int isDirty) {
		for(int i = 0;i<Constants.frameCount;i++) {
			if(page.equals(bufferPool[i].getTi().getPage())) {
				bufferPool[i].getTi().decrementPinCount();
				if(bufferPool[i].getTi().getPinCount() == 0) {
					bufferPool[i].getTi().setTimePinCountAtZero(new Date());
				}
				if(isDirty == 1) {
					bufferPool[i].getTi().setDirtyFlag(isDirty);
				}
			}
		}
	}
	
	
	
	
	
	public static void flushAll() {
	
		boolean pinCountZero = true;
		for(int i = 0; i<Constants.frameCount; i++) {
			if(bufferPool[i].getTi().getPinCount() != 0) {
				pinCountZero = false;
			}
		}
		
		if(pinCountZero) {
			for(int i = 0; i<Constants.frameCount; i++) {
				if(bufferPool[i].getTi().getDirtyFlag() == 1) {
					try{
						DiskManager.writePage(bufferPool[i].getTi().getPage(), bufferPool[i].getBuffer());
					}catch(IOException e) {
						System.out.println("Une erreur s'est produite lors de l'écriture sur disque !");
						System.out.println("Détails : " + e.getMessage());
					}
				}
				bufferPool[i] = new BufferTable();
			}
		}
		else {
			System.out.println("*** Erreur ! Les pages sont en cours d'utilisation ! ");
			System.exit(-1);
		}
	}
	
	public static BufferTable[] getBufferPool() {
		return bufferPool;
	}
}
