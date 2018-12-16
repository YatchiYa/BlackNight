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
import shema.Rid;

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

	 
	 /**
	  * 
	  * @param iRecord
	  * @param ioBuffer
	  * @param iSlotIdx
	  */
	public void writeRecordInBuffer(Record iRecord, byte[] ioBuffer,int iSlotIdx) {

		RelDefShema RelationMap = relDef.getrelDef();	
		ArrayList<String> typeDeColonne = RelationMap.gettypeDeColonne();
		ArrayList<String> listeDeValeurs = iRecord.getvalues();
		ByteBuffer buffer = ByteBuffer.wrap(ioBuffer);
		buffer.position(iSlotIdx);


		for(int i = 0; i<typeDeColonne.size(); i++) {
			String type = typeDeColonne.get(i);
			String val = listeDeValeurs.get(i);
			
			int convertToInt;
			float convertToFloat;
			String convertToString;
			
			switch(type.toLowerCase()) {
				case "int" : 
					convertToInt = Integer.parseInt(val);
					buffer.putInt(convertToInt);
					break;
				case "float" : 
					convertToFloat = Float.parseFloat(val);
					buffer.putFloat(convertToFloat);
					break;
				default : 
					int taille = Integer.parseInt(type.substring(6));
					convertToString = val;
					for(int j = 0; j<taille; j++) {
						buffer.putChar(convertToString.charAt(j));
					}
			}
		}
	}

	
/**
 * 
 * @param iRecord
 * @param iPageId
 * @throws IOException
 */
	public Rid insertRecordInPage(Record iRecord, PageId iPageId) throws IOException {
		
		byte[] bufferPage = BufferManager.getPage(iPageId);
		Bytemap bytemap = new Bytemap();
		int nombreDeSlot = relDef.getslotCount();
				
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);

		for(int i = 0; i<nombreDeSlot; i++) {
			Byte indice = new Byte(buffer.get());
			bytemap.addindiceDuSlot(indice);
		}
		
		ArrayList<Byte> indiceSlot = bytemap.getIndiceDuSlot();


		int indiceIdx = indiceSlot.indexOf(new Byte((byte)0));
		
		if(indiceIdx == -1) {
			BufferManager.freePage(iPageId, 0);
			return null;
		}
		else {
			int slotCpt = relDef.getslotCount();
			int SizeOfRecord = relDef.getrecordSize();

			writeRecordInBuffer(iRecord, bufferPage, slotCpt + indiceIdx*SizeOfRecord);
			
			bytemap.setStatusOccup(indiceIdx);			
			ArrayList<Byte> x = bytemap.getIndiceDuSlot();
			ByteBuffer z = ByteBuffer.wrap(bufferPage);

			for(int i = 0; i<nombreDeSlot; i++){
				z.put(x.get(i).byteValue());
			}
			
			Rid rid = new Rid();
			rid.setPageId(iPageId);
			rid.setSlotIdx(nombreDeSlot);
			
			BufferManager.freePage(iPageId, 1);
			return rid;
		}
	}
	
	
	public Rid  insertRecord(Record iRecord) throws IOException {
		PageId pageWhereRecordSaved = getFreePageId();

		Rid rid = new Rid();
		rid = insertRecordInPage(iRecord, pageWhereRecordSaved);
		
		updateHeaderWithTakenSlot(pageWhereRecordSaved);
		
		return rid;

	}
	

	
	
	// a supprimer
	
	
	public void readPagebytemapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {

		int nombreDeSlot = relDef.getslotCount();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nombreDeSlot; i++) {
			Byte indice = new Byte(buffer.get());
			bmpi.addindiceDuSlot(indice);
		}
	}


	public void writePagebytemapInfo(byte[] bufferPage, Bytemap bmpi) throws IOException {



		int nombreDeSlot = relDef.getslotCount();
		
		ArrayList<Byte> bytemap = bmpi.getIndiceDuSlot();
		
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


		for(int i = 0; i<nombreDeSlot; i++){
			buffer.put(bytemap.get(i).byteValue());
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
			
			Bytemap bytemapPageCourante = new Bytemap();
			readPagebytemapInfo(bufferPageCourante, bytemapPageCourante);
			
			ArrayList<Byte> indiceSlotPageCourante = bytemapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<indiceSlotPageCourante.size(); j++) {
				if(indiceSlotPageCourante.get(j).byteValue() == (byte)1) {
					int indiceIdx = j;

					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + indiceIdx*recordSize);
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
			
			Bytemap bytemapPageCourante = new Bytemap();
			readPagebytemapInfo(bufferPageCourante, bytemapPageCourante);
			
			ArrayList<Byte> indiceSlotPageCourante = bytemapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<indiceSlotPageCourante.size(); j++) {
				if(indiceSlotPageCourante.get(j).byteValue() == (byte)1) {
					int indiceIdx = j;

					Record recordToPrint = new Record();
					readRecordFromBuffer(recordToPrint, bufferPageCourante, slotCount + indiceIdx*recordSize);

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
			
			Bytemap bytemapPageCourante = new Bytemap();
			readPagebytemapInfo(bufferPageCourante, bytemapPageCourante);
			
			ArrayList<Byte> indiceSlotPageCourante = bytemapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<indiceSlotPageCourante.size(); j++) {
				if(indiceSlotPageCourante.get(j).byteValue() == (byte)1) {
					int indiceIdx = j;

					Record recordToAdd = new Record(pageCourante,slotCount + indiceIdx*recordSize);
					readRecordFromBuffer(recordToAdd, bufferPageCourante, slotCount + indiceIdx*recordSize);
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
			
		Bytemap bytemapPage = new Bytemap();
		readPagebytemapInfo(bufferPage, bytemapPage);
			
		ArrayList<Byte> indiceSlotPage = bytemapPage.getIndiceDuSlot();
			
		for(int j = 0; j<indiceSlotPage.size(); j++) {
			if(indiceSlotPage.get(j).byteValue() == (byte)1) {
				int indiceIdx = j;

				Record recordToAdd = new Record(page,slotCount + indiceIdx*recordSize);
				readRecordFromBuffer(recordToAdd, bufferPage, slotCount + indiceIdx*recordSize);
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
