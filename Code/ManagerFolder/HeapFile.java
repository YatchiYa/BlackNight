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
	
	public HeapFile(RelDef relation) {
		this.relation = relation;
	}


	public void createHeader() throws IOException {
		DiskManager.addPage(relation.getfileIdx());
	}


	public void readHeaderPageInfo(byte[] headerPage, HeaderPageInfo hfi) throws IOException {
	
		ByteBuffer buffer = ByteBuffer.wrap(headerPage);


		int nbPage = buffer.getInt();
		hfi.setNbPagesDeDonnees(nbPage);


		for(int i = 0; i<nbPage; i++) {
			Integer idx = new Integer(buffer.getInt());
			Integer nbSlot = new Integer(buffer.getInt());
			
			hfi.addIdxPage(idx);
			hfi.addNbSlotDispo(nbSlot);
		}
	}


	public void writeHeaderPageInfo(byte[] headerPage, HeaderPageInfo hfi) throws IOException {

		ByteBuffer buffer = ByteBuffer.wrap(headerPage);


		buffer.putInt(hfi.getNbPagesDeDonnees());
		
		ArrayList<Integer> idxTab = hfi.getIdxPageTab();
		ArrayList<Integer> nbSlotTab = hfi.getNbSlotsRestantDisponibles();


		for(int i = 0; i<idxTab.size(); i++) {
			buffer.putInt(idxTab.get(i).intValue());
			buffer.putInt(nbSlotTab.get(i).intValue());
		}
	}

	public void getHeaderPageInfo(HeaderPageInfo hpi) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);


		readHeaderPageInfo(bufferHeaderPage, hpi);
		
		BufferManager.freePage(headerPage, 0);
	}


	public void updateHeaderNewDataPage(PageId newpid) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
		
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		
		readHeaderPageInfo(bufferHeaderPage, hpi);

		Integer idx = new Integer(newpid.getIdX());
		hpi.addIdxPage(idx);

		hpi.addNbSlotDispo(relation.getcount_slot());

		hpi.incrementNbPage();
		
		writeHeaderPageInfo(bufferHeaderPage, hpi);

		BufferManager.freePage(headerPage, 1);
	}


	public void updateHeaderTakenSlot(PageId pid) throws IOException {

		int fileIdHP = relation.getfileIdx();

		PageId headerPage = new PageId(fileIdHP, 0);
				
		byte[] bufferHeaderPage = BufferManager.getPage(headerPage);
		
		HeaderPageInfo hpi = new HeaderPageInfo();
				
		readHeaderPageInfo(bufferHeaderPage, hpi);


		Integer idChercher = new Integer(pid.getIdX());
		boolean find = hpi.decrementNbSlotDispo(idChercher);
		if(!find) {
			System.out.println("*** Erreur ! Cette page n'est pas présente ! ***\n");
			BufferManager.freePage(headerPage, 0);
		}
		else {
			writeHeaderPageInfo(bufferHeaderPage, hpi);

			BufferManager.freePage(headerPage, 1);
		}
	}


	public void readPageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {

		int nbSlot = relation.getcount_slot();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nbSlot; i++) {
			Byte status = new Byte(buffer.get());
			bmpi.addSlotStatus(status);
		}
	}


	public void writePageBitmapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {



		int nbSlot = relation.getcount_slot();
		
		ArrayList<Byte> bitMap = bmpi.getSlotStatus();
		
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
		
		ArrayList<Integer> idxList = hpi.getIdxPageTab();
		ArrayList<Integer> slotDispoList = hpi.getNbSlotsRestantDisponibles();
		
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
		
		ArrayList<Byte> slotStatus = bitMap.getSlotStatus();


		int caseIdX = slotStatus.indexOf(new Byte((byte)0));
		
		if(caseIdX == -1) {
			BufferManager.freePage(page, 0);
		}
		else {
			int slotCount = relation.getcount_slot();
			int recordSize = relation.getsizeOfRecord();


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
		int slotCount = relation.getcount_slot();
		int recordSize = relation.getsizeOfRecord();
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
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
		int slotCount = relation.getcount_slot();
		int recordSize = relation.getsizeOfRecord();
		
		int totalRecordPrinted = 0;
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
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
		int slotCount = relation.getcount_slot();
		int recordSize = relation.getsizeOfRecord();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);
		
		ArrayList<Integer> listIdxPage = hpi.getIdxPageTab();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int idxPageCourante = listIdxPage.get(i).intValue();
			PageId pageCourante = new PageId(fileIdHP, idxPageCourante); 


			byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
			
			Bytemap bitmapPageCourante = new Bytemap();
			readPageBitmapInfo(bufferPageCourante, bitmapPageCourante);
			
			ArrayList<Byte> slotStatusPageCourante = bitmapPageCourante.getSlotStatus();
			
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
	
		int slotCount = relation.getcount_slot();
		int recordSize = relation.getsizeOfRecord();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		getHeaderPageInfo(hpi);


		byte[] bufferPage = BufferManager.getPage(page);
			
		Bytemap bitmapPage = new Bytemap();
		readPageBitmapInfo(bufferPage, bitmapPage);
			
		ArrayList<Byte> slotStatusPage = bitmapPage.getSlotStatus();
			
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

	public void join(HeapFile relFind2, int col1, int col2) throws IOException {

		int fileIdHP1 = relation.getfileIdx();
		int fileIdHP2 = relFind2.getRel().getfileIdx();
		
		int totalRecordPrinted = 0;


		HeaderPageInfo hpi1 = new HeaderPageInfo();
		getHeaderPageInfo(hpi1);
		HeaderPageInfo hpi2 = new HeaderPageInfo();
		relFind2.getHeaderPageInfo(hpi2);
		
		ArrayList<Integer> listIdxPage1 = hpi1.getIdxPageTab();
		ArrayList<Integer> listIdxPage2 = hpi2.getIdxPageTab();


		for(int i=0; i<listIdxPage1.size(); i++) {
			
			int idxPageCourante1 = listIdxPage1.get(i).intValue();
			PageId pageCourante1 = new PageId(fileIdHP1, idxPageCourante1); 
			
			ArrayList<Record> listRecords1 = getAllRecordsPage(pageCourante1);


			for(int j=0; j<listIdxPage2.size(); j++) {
				
				int idxPageCourante2 = listIdxPage2.get(j).intValue();
				PageId pageCourante2 = new PageId(fileIdHP2, idxPageCourante2); 
				
				ArrayList<Record> listRecords2 = relFind2.getAllRecordsPage(pageCourante2);
				
				for(int h=0; h<listRecords1.size();h++) {
					for(int k=0; k<listRecords2.size();k++) {
						Record r1 = listRecords1.get(h);
						Record r2 = listRecords2.get(k);
						
						String val1 = r1.getListValues().get(col1-1);
						String val2 = r2.getListValues().get(col2-1);
						
						if(val1.equals(val2)) {
							System.out.println(r1.toString() + r2.toString());
							totalRecordPrinted++;
						}	
					}
				}
				
			}
		}
						
		System.out.println("Total records : " + totalRecordPrinted);
	
	}
}
