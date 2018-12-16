package ManagerFolder;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import constants.Constants;
import shema.PageId;

public class DiskManager {
	


	public static void createFile(int fileId) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(Constants.PATH+fileId+".rf", "rw");	
		rAf.close();
	}


	public static PageId addPage(int fileId) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(Constants.PATH+fileId+".rf", "rw");
		long longueur = rAf.length();
		int id = (int)(longueur/Constants.pageSize);
		rAf.seek((long)longueur);
		for (int i=0; i<Constants.pageSize; i++){
			rAf.writeByte(0);
		}
		rAf.close();
		return new PageId(fileId,id);
	}


	public static void readPage(PageId page, byte[] buffer) throws IOException{
		RandomAccessFile rAf = new RandomAccessFile(Constants.PATH+page.getFileIdx()+".rf", "rw");
		rAf.seek(page.getPageIdx()*Constants.pageSize);
		rAf.readFully(buffer);
		rAf.close();
	}


	public static void writePage(PageId page, byte[] buffer) throws IOException {
		RandomAccessFile rAf = new RandomAccessFile(Constants.PATH+page.getFileIdx()+".rf", "rw");
		rAf.seek(page.getPageIdx()*Constants.pageSize);
		rAf.write(buffer);
		rAf.close();
	}
}