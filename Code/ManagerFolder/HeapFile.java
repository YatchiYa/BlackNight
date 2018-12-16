package ManagerFolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import shema.Bytemap;
import shema.HeaderPageInfo;
import shema.PageId;
import shema.Record;
import shema.RelDef;
import shema.RelDefShema;

public class HeapFile {
	
	private RelDef relDef;
	private HeaderPageInfo headerPageInfo;
	
	
	public HeapFile(RelDef relDef) {
		this.relDef = relDef;
		this.headerPageInfo = new HeaderPageInfo();
	}

	
	
	/**
	 * 
	 * @throws IOException
	 */
	public void createNewOnDisk() throws IOException {
		
		try {
			DiskManager.createFile(relDef.getfileIdx());
		}catch(IOException e) {
			e.printStackTrace();
		}
		try {
			DiskManager.addPage(relDef.getfileIdx());
	}catch(IOException e) {
		e.printStackTrace();
	}
		int fileIdx = relDef.getfileIdx();
		PageId hp = new PageId(fileIdx, 0);
		
		HeaderPageInfo newHeaderPageInfo = new HeaderPageInfo();
		byte[] bhp = BufferManager.getPage(hp);
		this.headerPageInfo.readFromBuffer(bhp, newHeaderPageInfo);
		BufferManager.freePage(hp, 1);
	}



/**
 * 
 * @return
 * @throws IOException
 */
	public PageId getFreePageId() throws IOException {
	
		int fileIndex = relDef.getfileIdx();
		
		HeaderPageInfo headerPageInfo_2 = new HeaderPageInfo();
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(headerPageInfo_2, relDef);
		
		ArrayList<Integer> listIndex = headerPageInfo.getpageIdx();
		ArrayList<Integer> freeSlot = headerPageInfo.getfreeSlots();
		
		for(int i = 0;i<listIndex.size();i++) {
			int index = listIndex.get(i).intValue();
			int	availableSlot = freeSlot.get(i).intValue();
			
			if(availableSlot > 0) {
				return new PageId(fileIndex,index); 
			}
		}
		PageId newPage = DiskManager.addPage(relDef.getfileIdx());
		
		hfm.miseAjourHPI(newPage,relDef);
		
		return newPage;
	}
	
	
	
	
	
	/**
	 * 
	 * @param iPageId
	 * @throws IOException
	 */
	public void updateHeaderWithTakenSlot(PageId iPageId) throws IOException {

		int fileIndex = relDef.getfileIdx();

		PageId headerPage = new PageId(fileIndex, 0);
				
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
				
		this.headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);


		Integer searchIdx = new Integer(iPageId.getPageIdx());
		
		if(!hpi.decrementfreeSlots(searchIdx)) {
			BufferManager.freePage(headerPage, 0);
		}
		else {
			this.headerPageInfo.writeToBuffer(bufferHeaderPage, hpi);

			BufferManager.freePage(headerPage, 1);
		}
	}

	
	
	
	
	
	public void readPageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {

		int nbSlot = relDef.getslotCount();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nbSlot; i++) {
			Byte status = new Byte(buffer.get());
			bmpi.addindiceDuSlot(status);
		}
	}


	public void writePageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {



		int nbSlot = relDef.getslotCount();
		
		ArrayList<Byte> bitMap = bmpi.getIndiceDuSlot();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nbSlot; i++){
			buffer.put(bitMap.get(i).byteValue());
		}
	}

	 Record readRecordFromBuffer(byte[] Buffer, int SlotIdx) {
		
		 
		 return null;
		 
	 }

	public void writeRecordInBuffer(Record r, byte[] bufferPage,int offset) {

		RelDefShema schema = relDef.getrelDef();	


		ArrayList<String> typeCol = schema.gettypeDeColonne();

		ArrayList<String> listVal = r.getvalues();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		buffer.position(offset);


		for(int i = 0; i<typeCol.size(); i++) {
			String type = typeCol.get(i);
			String val = listVal.get(i);
			
			int valToInt;
			float valtoFloat;
			String valToString;
			
			switch(type.toLowerCase()) {
				case "int" : 
					valToInt = Integer.parseInt(val);
					buffer.putInt(valToInt);
					break;
				case "float" : 
					valtoFloat = Float.parseFloat(val);
					buffer.putFloat(valtoFloat);
					break;
				default : 
					int longueurString = Integer.parseInt(type.substring(6));
					valToString = val;
					for(int j = 0; j<longueurString; j++) {
						buffer.putChar(valToString.charAt(j));
					}
			}
		}
	}

	public void readRecordFromBuffer(Record r, byte[] bufferPage,int offset) {
		RelDefShema schema = relDef.getrelDef();
		
		ArrayList<String> typeCol = schema.gettypeDeColonne();
		ArrayList<String> listVal = new ArrayList<String>();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		
		buffer.position(offset);
		
		for(int i = 0; i<typeCol.size(); i++) {
			String type = typeCol.get(i);
			
			int valInt;
			float valFloat;
			StringBuffer valString = new StringBuffer();
			
			switch(type.toLowerCase()) {
				case "int" : 
					valInt = buffer.getInt();
					listVal.add(String.valueOf(valInt));
					break;
				case "float" : 
					valFloat = buffer.getFloat();
					listVal.add(String.valueOf(valFloat));
					break;
				default : 
					int longueurString = Integer.parseInt(type.substring(6));

					for(int j = 0; j<longueurString; j++) {
						valString.append(buffer.getChar());
					}
					listVal.add(valString.toString());
			}
		}
		r.setValue(listVal);
	}
	
/*	public PageId addDataPage() throws IOException {

		PageId newPage = DiskManager.addPage(relDef.getfileIdx());
		
		updateHeaderNewDataPage(newPage);
		
		return newPage;
	}
*/



	public void insertRecordInPage(Record r, PageId page) throws IOException {
		byte[] bufferPage = BufferManager.getPage(page);
		
		Bytemap bitMap = new Bytemap();
		
		readPageBitmapInfo(bufferPage, bitMap);
		
		ArrayList<Byte> slotStatus = bitMap.getIndiceDuSlot();


		int caseIdX = slotStatus.indexOf(new Byte((byte)0));
		
		if(caseIdX == -1) {
			BufferManager.freePage(page, 0);
		}
		else {
			int slotCount = relDef.getslotCount();
			int recordSize = relDef.getrecordSize();


			writeRecordInBuffer(r, bufferPage, slotCount + caseIdX*recordSize);


			bitMap.setStatusOccup(caseIdX);
			
			writePageBitmapInfo(bufferPage, bitMap);
			
			BufferManager.freePage(page, 1);
		}
	}
	
	public void insertRecord(Record r) throws IOException {
		PageId pageWhereRecordSaved = getFreePageId();
		insertRecordInPage(r, pageWhereRecordSaved);
		
		updateHeaderWithTakenSlot(pageWhereRecordSaved);
	}


	public void printAllRecords() throws IOException {

		int fileIdHP = relDef.getfileIdx();
		int slotCount = relDef.getslotCount();
		int recordSize = relDef.getrecordSize();
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();

		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(hpi, relDef);
		
		ArrayList<Integer> listIdxPage = hpi.getpageIdx();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;

					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + caseIdX*recordSize);
					System.out.println(recordToPrint.toString());
					totalRecordPrinted++;
				}
			}

			BufferManager.freePage(pageCourante, 0);
		}
		System.out.println("Total records : " + totalRecordPrinted);
	}


	public void printAllRecordsWithFilter(int indiceColonne, String condition) throws IOException {

		int fileIdHP = relDef.getfileIdx();
		int slotCount = relDef.getslotCount();
		int recordSize = relDef.getrecordSize();
		
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(hpi, relDef);
		
		ArrayList<Integer> listIdxPage = hpi.getpageIdx();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;

					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + caseIdX*recordSize);

					if(recordToPrint.getvalues().get(indiceColonne-1).equals(condition)) {
						System.out.println(recordToPrint.toString());
						totalRecordPrinted++;
					}
				}
			}

			BufferManager.freePage(pageCourante, 0);
		}
		System.out.println("Total records : " + totalRecordPrinted);
	}


	public ArrayList<Record> getAllRecords() throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);


		int fileIdHP = relDef.getfileIdx();
		int slotCount = relDef.getslotCount();
		int recordSize = relDef.getrecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(hpi, relDef);
		
		ArrayList<Integer> listIdxPage = hpi.getpageIdx();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<slotStatusPageCourante.size(); j++) {
				if(slotStatusPageCourante.get(j).byteValue() == (byte)1) {
					int caseIdX = j;

					Record recordToAdd = new Record(pageCourante,slotCount + caseIdX*recordSize);
					readRecordFromBuffer(recordToAdd, bufferPageCourante, slotCount + caseIdX*recordSize);
					listRecords.add(recordToAdd);
				}
			}
			BufferManager.freePage(pageCourante, 0);
		}
		return listRecords;
	}


	public ArrayList<Record> getAllRecordsPage(PageId page) throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);
	
		int slotCount = relDef.getslotCount();
		int recordSize = relDef.getrecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(hpi, relDef);

		byte[] bufferPage = BufferManager.getPage(page);
			
		Bytemap bitmapPage = new Bytemap();
		readPageBitmapInfo(bufferPage, bitmapPage);
			
		ArrayList<Byte> slotStatusPage = bitmapPage.getIndiceDuSlot();
			
		for(int j = 0; j<slotStatusPage.size(); j++) {
			if(slotStatusPage.get(j).byteValue() == (byte)1) {
				int caseIdX = j;

				Record recordToAdd = new Record(page,slotCount + caseIdX*recordSize);
				readRecordFromBuffer(recordToAdd, bufferPage, slotCount + caseIdX*recordSize);
				listRecords.add(recordToAdd);
			}
		}


		BufferManager.freePage(page, 0);

		return listRecords;
	}
	
	
	public RelDef getrelDef() {
		return relDef;
	}


	public void setrelDef(RelDef relDef) {
		this.relDef = relDef;
	}
	public RelDef getRelDef() {
		return relDef;
	}




	public void setRelDef(RelDef relDef) {
		this.relDef = relDef;
	}




	public HeaderPageInfo getHeaderPageInfo() {
		return headerPageInfo;
	}




	public void setHeaderPageInfo(HeaderPageInfo headerPageInfo) {
		this.headerPageInfo = headerPageInfo;
	}

	
	
	
}
