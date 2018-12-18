package ManagerFolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import shema.Bytemap;
import shema.HeaderPageInfo;
import shema.PageId;
import shema.Record;
import shema.RelDef;
import shema.Rid;

public class FileManager {
	// singleton
	
	private static ArrayList<HeapFile> listeHeapFile;

	
	
	
	public static void init() {
		listeHeapFile = new ArrayList<HeapFile>();
		for(RelDef r : DBManager.getDb().getlistRelDef()) {
			listeHeapFile.add(new HeapFile(r));
		}
	}
	
	public static void createNewHeapFile(RelDef iRelDef) throws IOException{
		HeapFile heapFile = new HeapFile(iRelDef);
		listeHeapFile.add(heapFile);
		heapFile.createNewOnDisk();
	}
	
	
	/**
	 * 
	 * @param iRelationName
	 * @param iRecord
	 * @return
	 * @throws IOException
	 */
	public Rid insertRecordInRelation(RelDef iRelationName, Record iRecord) throws IOException {
		Rid rid = new Rid();
		
		for (HeapFile hf : listeHeapFile) {
			if (hf.getrelDef().equals(iRelationName)) {
				rid = hf.insertRecord(iRecord);
				return rid;	
			}
		}
		return null;			
	}
	
	
	
	

	/**
	 * 
	 * @param iRelationName
	 * @return
	 * @throws IOException
	 */
	public ArrayList<Record> getAllRecords(RelDef iRelationName) throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);


		int fileHeaderPage = iRelationName.getfileIdx();
		int slotCpt = iRelationName.getslotCount();
		int sizeOfRecord = iRelationName.getrecordSize();
		
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		HeapFileTreatment.getHPI(headerPageInfo, iRelationName);
		
		ArrayList<Integer> listIdxPage = headerPageInfo.getpageIdx();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int pageIndexCourant = listIdxPage.get(i).intValue();
			PageId pageActuelle = new PageId(fileHeaderPage, pageIndexCourant); 


			byte[] bufferPage = BufferManager.getPage(pageActuelle);
			
			Bytemap bytemapPageCourante = new Bytemap();

			int nombreDeSlot = iRelationName.getslotCount(); // nombreDeSlot = slotCount
			ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
			for(int j = 0; j<nombreDeSlot; j++) {
				Byte indice = new Byte(buffer.get());
				bytemapPageCourante.addindiceDuSlot(indice);
			}
			
			ArrayList<Byte> iPageCourante = bytemapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<iPageCourante.size(); j++) {
				if(iPageCourante.get(j).byteValue() == (byte)1) {
					int indexj = j;

					Record recordAjoute = new Record(pageActuelle,slotCpt + indexj*sizeOfRecord);
					recordAjoute = HeapFile.readRecordFromBuffer(bufferPage, slotCpt + indexj*sizeOfRecord);
					listRecords.add(recordAjoute);
				}
			}
			BufferManager.freePage(pageActuelle, 0);
		}
		return listRecords;
	}
	
	
	/**
	 * 
	 * @param iRelationName
	 * @param iIdxCol
	 * @param iValeur
	 * @return
	 * @throws IOException
	 */
	public ArrayList<Record> getAllRecordsWithFilter(RelDef iRelationName, int iIdxCol, String iValeur) throws IOException {
		ArrayList<Record> listRecords = new ArrayList<Record>(0);


		int fileHeaderPage = iRelationName.getfileIdx();
		int slotCpt = iRelationName.getslotCount();
		int sizeOfRecord = iRelationName.getrecordSize();
		
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		HeapFileTreatment.getHPI(headerPageInfo, iRelationName);
		
		ArrayList<Integer> listIdxPage = headerPageInfo.getpageIdx();
		
		for(int i=0; i<listIdxPage.size(); i++) {
			int pageIndexCourant = listIdxPage.get(i).intValue();
			PageId pageActuelle = new PageId(fileHeaderPage, pageIndexCourant); 


			byte[] bufferPage = BufferManager.getPage(pageActuelle);
			
			Bytemap bytemapPageCourante = new Bytemap();

			int nombreDeSlot = iRelationName.getslotCount(); // nombreDeSlot = slotCount
			ByteBuffer buffer = ByteBuffer.wrap(bufferPage);
			for(int j = 0; j<nombreDeSlot; j++) {
				Byte indice = new Byte(buffer.get());
				bytemapPageCourante.addindiceDuSlot(indice);
			}
			
			ArrayList<Byte> iPageCourante = bytemapPageCourante.getIndiceDuSlot();
			
			for(int j = 0; j<iPageCourante.size(); j++) {
				if(iPageCourante.get(j).byteValue() == (byte)1) {
					int indexj = j;

					Record recordAjoute = new Record(pageActuelle,slotCpt + indexj*sizeOfRecord);
					recordAjoute = HeapFile.readRecordFromBuffer(bufferPage, slotCpt + indexj*sizeOfRecord);
					
					if(!recordAjoute.getvalues().get(iIdxCol-1).equals(iValeur)) {
						listRecords.add(recordAjoute);
					}
				}
			}
			BufferManager.freePage(pageActuelle, 0);
		}
		return listRecords;
	}
	
	
	
	public static void join(HeapFile relFind1, HeapFile relFind2, int col1, int col2) throws IOException {
		//on recupere les fileid des heapfiles
		int fileIdHP1 = relFind1.getrelDef().getfileIdx();
		int fileIdHP2 = relFind2.getrelDef().getfileIdx();
		
		int totalRecordPrinted = 0;
		
		//on recupere la header page des heapfiles
		HeaderPageInfo hpi1 = new HeaderPageInfo();
		HeapFileTreatment.getHPI(hpi1, relFind1.getrelDef());
		HeaderPageInfo hpi2 = new HeaderPageInfo();
		HeapFileTreatment.getHPI(hpi2, relFind2.getrelDef());
		
		ArrayList<Integer> listIdxPage1 = hpi1.getpageIdx();
		ArrayList<Integer> listIdxPage2 = hpi2.getpageIdx();
		
		//boucle de la relation1
		for(int i=0; i<listIdxPage1.size(); i++) {
			
			int idxPageCourante1 = listIdxPage1.get(i).intValue();
			PageId pageCourante1 = new PageId(fileIdHP1, idxPageCourante1); 
			
			ArrayList<Record> listRecords1 = HeapFile.getRecordsOnPage(pageCourante1);
			
			//boucle de la relation2
			for(int j=0; j<listIdxPage2.size(); j++) {
				
				int idxPageCourante2 = listIdxPage2.get(j).intValue();
				PageId pageCourante2 = new PageId(fileIdHP2, idxPageCourante2); 
				
				ArrayList<Record> listRecords2 = HeapFile.getRecordsOnPage(pageCourante2);
				
				for(int h=0; h<listRecords1.size();h++) {
					for(int k=0; k<listRecords2.size();k++) {
						Record r1 = listRecords1.get(h);
						Record r2 = listRecords2.get(k);
						
						String val1 = r1.getvalues().get(col1-1);
						String val2 = r2.getvalues().get(col2-1);
						
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
	
	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}

	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
	
	
	
	
}
