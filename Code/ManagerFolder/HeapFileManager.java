package ManagerFolder;

import java.io.IOException;

import shema.HeaderPageInfo;
import shema.PageId;
import shema.RelDef;

public class HeapFileManager {
	
	
	public void getHeaderPageInfo(HeaderPageInfo hpi, HeaderPageInfo headerPageInfo, RelDef relDef) throws IOException {

		int fileIdHP = relDef.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);


		headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);
		
		BufferManager.freePage(headerPage, 0);
	}
	
	
}
