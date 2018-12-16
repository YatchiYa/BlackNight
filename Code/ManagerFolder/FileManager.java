package ManagerFolder;

import java.util.ArrayList;

import shema.RelDef;

public class FileManager {
	// singleton
	
	private static ArrayList<HeapFile> listeHeapFile;

	
	
	
	public static void init() {
		
	}
	
	
	
	
	
	
	
	public static ArrayList<HeapFile> getListeHeapFile() {
		return listeHeapFile;
	}

	public static void setListeHeapFile(ArrayList<HeapFile> listeHeapFile) {
		FileManager.listeHeapFile = listeHeapFile;
	}
	
	
	
	
}
