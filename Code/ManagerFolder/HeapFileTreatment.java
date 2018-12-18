package ManagerFolder;

import java.io.IOException;

import shema.HeaderPageInfo;
import shema.PageId;
import shema.RelDef;

public class HeapFileTreatment {
	
	
	public static void getHPI(HeaderPageInfo hpi, RelDef relDef) throws IOException {

		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		int fileHeaderPage = relDef.getfileIdx();

		PageId headerPage = new PageId(fileHeaderPage, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);


		headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);
		
		BufferManager.freePage(headerPage, 0);
	}
	
	public static void miseAjourHPI(PageId newPageId, RelDef relDef) throws IOException {

		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		int fileHeaderPage = relDef.getfileIdx();

		PageId headerPage = new PageId(fileHeaderPage, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		
		headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);

		Integer indice = new Integer(newPageId.getPageIdx());
		hpi.adddx_page_données(indice);

		hpi.addNbSlotDispo(relDef.getslotCount());

		hpi.incrementnombreDePage();
		
		headerPageInfo.writeToBuffer(bufferHeaderPage, hpi);

		BufferManager.freePage(headerPage, 1);
	}
	
	
}
