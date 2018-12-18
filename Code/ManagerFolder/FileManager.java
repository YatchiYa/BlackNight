package ManagerFolder;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import shema.Bytemap;
import shema.DBDef;
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
	
	public void createNewHeapFile(RelDef iRelDef) throws IOException{
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
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(headerPageInfo, iRelationName);
		
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
		HeapFileTreatment hfm = new HeapFileTreatment();
		hfm.getHPI(headerPageInfo, iRelationName);
		
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
	
	
	
	
	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}

	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
	
	
	
	
}
