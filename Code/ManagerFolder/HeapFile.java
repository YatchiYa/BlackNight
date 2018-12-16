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
	
	private RelDef relation;
	private HeaderPageInfo headerPageInfo;
	
	public HeapFile(RelDef relation) {
		this.relation = relation;
		this.headerPageInfo = new HeaderPageInfo();
	}


	public void createHeader() throws IOException {
		DiskManager.addPage(relation.getfileIdx());
	}


	

	public void getHeaderPageInfo(HeaderPageInfo hpi) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);


		this.headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);
		
		BufferManager.freePage(headerPage, 0);
	}


	public void updateHeaderNewDataPage(PageId newpid) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		
		this.headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);

		Integer idx = new Integer(newpid.getPageIdx());
		hpi.adddx_page_données(idx);

		hpi.addNbSlotDispo(relation.getslotCount());

		hpi.incrementnombreDePage();
		
		this.headerPageInfo.writeToBuffer(bufferHeaderPage, hpi);

		BufferManager.freePage(headerPage, 1);
	}


	public void updateHeaderTakenSlot(PageId pid) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
				
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
				
		this.headerPageInfo.readFromBuffer(bufferHeaderPage, hpi);


		Integer idChercher = new Integer(pid.getPageIdx());
		boolean find = hpi.decrementfreeSlots(idChercher);
		if(!find) {
			System.out.println("*** Erreur ! Cette page n'est pas présente ! ***\n");
			BufferManager.freePage(headerPage, 0);
		}
		else {
			this.headerPageInfo.writeToBuffer(bufferHeaderPage, hpi);

			BufferManager.freePage(headerPage, 1);
		}
	}


	public void readPageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {

		int nbSlot = relation.getslotCount();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nbSlot; i++) {
			Byte status = new Byte(buffer.get());
			bmpi.addindiceDuSlot(status);
		}
	}


	public void writePageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {



		int nbSlot = relation.getslotCount();
		
		ArrayList<Byte> bitMap = bmpi.getIndiceDuSlot();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nbSlot; i++){
			buffer.put(bitMap.get(i).byteValue());
		}
	}


	public void writeRecordInBuffer(Record r, byte[] bufferPage,int offset) {

		RelDefShema schema = relation.getrelDef();


		ArrayList<String> typeCol = schema.gettypeDeColonne();

		ArrayList<String> listVal = r.getListValues();
		
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
		RelDefShema schema = relation.getrelDef();
		
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
	
	public PageId addDataPage() throws IOException {

		PageId newPage = DiskManager.addPage(relation.getfileIdx());
		
		updateHeaderNewDataPage(newPage);
		
		return newPage;
	}


	public PageId getFreePageId() throws IOException {
	
		int idFile = relation.getfileIdx();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> idxList = hpi.getpageIdx();
		ArrayList<Integer> slotDispoList = hpi.getfreeSlots();
		
		for(int i = 0;i<idxList.size();i++) {
			int idx = idxList.get(i).intValue();
			int nbSlot = slotDispoList.get(i).intValue();
			
			if(nbSlot > 0) {
				return new PageId(idFile, idx); 
			}
		}
		return addDataPage();
	}


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
			int slotCount = relation.getslotCount();
			int recordSize = relation.getrecordSize();


			writeRecordInBuffer(r, bufferPage, slotCount + caseIdX*recordSize);


			bitMap.setStatusOccup(caseIdX);
			
			writePageBitmapInfo(bufferPage, bitMap);
			
			BufferManager.freePage(page, 1);
		}
	}
	
	public void insertRecord(Record r) throws IOException {
		PageId pageWhereRecordSaved = getFreePageId();
		insertRecordInPage(r, pageWhereRecordSaved);
		
		updateHeaderTakenSlot(pageWhereRecordSaved);
	}


	public void printAllRecords() throws IOException {
		//on recupere la header page du heapfile
		int fileIdHP = relation.getfileIdx();
		int slotCount = relation.getslotCount();
		int recordSize = relation.getrecordSize();
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
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

		int fileIdHP = relation.getfileIdx();
		int slotCount = relation.getslotCount();
		int recordSize = relation.getrecordSize();
		
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
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

					if(recordToPrint.getListValues().get(indiceColonne-1).equals(condition)) {
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


		int fileIdHP = relation.getfileIdx();
		int slotCount = relation.getslotCount();
		int recordSize = relation.getrecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
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
	
		int slotCount = relation.getslotCount();
		int recordSize = relation.getrecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);


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
	
	
	public RelDef getRel() {
		return relation;
	}

	
	
	
}
