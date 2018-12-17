package ManagerFolder;

import java.io.IOException;
import java.util.ArrayList;

import shema.DBDef;
import shema.RelDef;

public class FileManager {
	// singleton
	
	private static ArrayList<HeapFile> listeHeapFile;

	
	
	
	public static void init() {
		DBDef db = new DBDef();
		listeHeapFile = new ArrayList<HeapFile>(0);
		for(RelDef r : db.getlistRelDef()) {
			listeHeapFile.add(new HeapFile(r));
		}
	}
	
	public void createNewHeapFile(RelDef iRelDef) throws IOException{
		HeapFile heapFile = new HeapFile(iRelDef);
		listeHeapFile.add(heapFile);
		heapFile.createNewOnDisk();
	}
	
	
	
	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}

	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
	
	
	
	
}
