package ManagerFolder;

import java.io.IOException;
import java.util.ArrayList;

import shema.DBDef;
import shema.Record;
import shema.RelDef;
import shema.Rid;

public class FileManager {
	// singleton
	
	private static ArrayList<HeapFile> listeHeapFile;

	
	
	
	public static void init() {
		listeHeapFile = new ArrayList<HeapFile>(0);
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
	
	
	
	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}

	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
	
	
	
	
}
