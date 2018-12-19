package ManagerFolder;

import java.io.IOException;
import java.io.RandomAccessFile;
import constants.Constants;
import shema.PageId;

public class DiskManager {
	
	// singleton

	/**
	 * 
	 * @param iFileIdx
	 * @throws IOException
	 */
	public static void createFile(int iFileIdx) throws IOException {
			RandomAccessFile randomAccessFile = new RandomAccessFile(Constants.PATH+ iFileIdx +".rf", "rw");
			randomAccessFile.close();
	}

	/**
	 * 
	 * @param fileId
	 * @return
	 * @throws IOException
	 */
	public static PageId addPage(int iFileIdx) throws IOException {
		RandomAccessFile randomAccessFile = new RandomAccessFile(constants.Constants.PATH + iFileIdx + ".rf", "rw");
		long taille = randomAccessFile.length();
		int id = (int)(taille/constants.Constants.pageSize);
		randomAccessFile.seek((long)taille);
		for (int i=0; i<constants.Constants.pageSize; i++){
			randomAccessFile.writeByte(0);
		}
		randomAccessFile.close();
		return new PageId(iFileIdx,id);
	}


	
	
	public static void readPage(PageId iPageId, byte[] oBuffer) {
		RandomAccessFile randomAccessFile;
		try {
			randomAccessFile = new RandomAccessFile(Constants.PATH+  iPageId.getFileIdx()+".rf", "rw");
			randomAccessFile.seek(iPageId.getPageIdx()*Constants.pageSize);
			randomAccessFile.readFully(oBuffer);
			randomAccessFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		


	public static void writePage(PageId iPageId, byte[] iBuffer) {
		RandomAccessFile randomAccessFile;
		try {
			randomAccessFile = new RandomAccessFile(Constants.PATH+ iPageId.getFileIdx()+".rf", "rw");
			randomAccessFile.seek(iPageId.getPageIdx()*Constants.pageSize);
			randomAccessFile.write(iBuffer);
			randomAccessFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}