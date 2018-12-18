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
	
	private static RelDef relDef;
	private HeaderPageInfo headerPageInfo;
	
	
	public HeapFile(RelDef relDefx) {
		relDef = relDefx;
		this.headerPageInfo = new HeaderPageInfo();
	}

	
	
	public HeapFile() {
		this(null);
	}



	/**
	 * 
	 * @throws IOException
	 */
	public void createNewOnDisk() throws IOException {
		
		try {
			DiskManager.createFile(relDef.getfileIdx());
			System.out.println( "42 " + relDef.getfileIdx());
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
		HeapFileTreatment.getHPI(headerPageInfo_2, relDef);
		
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
		
		HeapFileTreatment.miseAjourHPI(newPage,relDef);
		
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
	
	/**
	 * 
	 * @param iRecord
	 * @return
	 * @throws IOException
	 */
	public Rid  insertRecord(Record iRecord) throws IOException {
		PageId pageWhereRecordSaved = getFreePageId();

		Rid rid = new Rid();
		rid = insertRecordInPage(iRecord, pageWhereRecordSaved);
		
		updateHeaderWithTakenSlot(pageWhereRecordSaved);
		
		return rid;

	}
	
/**
 * 
 * @param iBuffer
 * @param iSlotIdx
 * @return
 */
	public static Record readRecordFromBuffer(byte[] iBuffer,int iSlotIdx) {
		Record record = new Record();
		
		RelDefShema schema = relDef.getrelDef();
		
		ArrayList<String> typeCol = schema.gettypeDeColonne();
		ArrayList<String> listVal = new ArrayList<String>();
		
		ByteBuffer buffer = ByteBuffer.wrap(iBuffer);
		
		buffer.position(iSlotIdx);
		
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
		record.setValue(listVal);
		
		return record;
	}
	
	
	/**
	 * 
	 * @param iPageId
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<Record> getRecordsOnPage(PageId iPageId) throws IOException {
		ArrayList<Record> listeDesRecords = new ArrayList<Record>(0);
	
		int slotCpt = relDef.getslotCount();
		int sizeOfRecord = relDef.getrecordSize();
		
		HeaderPageInfo hpi = new HeaderPageInfo();
		HeapFileTreatment.getHPI(hpi, relDef);

		byte[] bufferPage = BufferManager.getPage(iPageId);
			
		Bytemap bytemapPage = new Bytemap();
				
		ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
		for(int i = 0; i<slotCpt; i++) {
			Byte indi = new Byte(buffer.get());
			bytemapPage.addindiceDuSlot(indi);
		}
			
		
		ArrayList<Byte> findSlotPage = bytemapPage.getIndiceDuSlot();
			
		for(int j = 0; j<findSlotPage.size(); j++) {
			if(findSlotPage.get(j).byteValue() == (byte)1) {
				int indicej = j;

				Record recordAjoute = new Record(iPageId,slotCpt + indicej*sizeOfRecord);
				recordAjoute = readRecordFromBuffer(bufferPage, slotCpt + indicej*sizeOfRecord);
				listeDesRecords.add(recordAjoute);
			}
		}


		BufferManager.freePage(iPageId, 0);

		return listeDesRecords;
	}
	
	
	/**
	 * 
	 * @return
	 * @throws IOException
	 */
	public ArrayList<PageId> getDataPagesIds() throws IOException {
		
		ArrayList<PageId> listPageId = new ArrayList<PageId>();
		for (HeapFile hp : FileManager.getListeHeapFile()) {
			listPageId.add(hp.getFreePageId());
			
		}
		return listPageId;		
	}
	
	
	
	
	
	public RelDef getrelDef() {
		return relDef;
	}


	public static void setrelDef(RelDef relDefx) {
		relDef = relDefx;
	}




	public static void setRelDef(RelDef relDefx) {
		relDef = relDefx;
	}




	public HeaderPageInfo getHeaderPageInfo() {
		return headerPageInfo;
	}




	public void setHeaderPageInfo(HeaderPageInfo headerPageInfo) {
		this.headerPageInfo = headerPageInfo;
	}

	
	
	
}
