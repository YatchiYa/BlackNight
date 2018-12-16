package ManagerFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import gestion.Commande;
import shema.DBDef;
import shema.Record;
import shema.RelDef;
import shema.RelDefShema;
import constants.Constants;

public class DBManager{

	private static Commande cmd = new Commande();
	private static DBDef db;
	private static ArrayList<HeapFile> listeHeapFile;

	/**
	 * 
	 * @param commande
	 * @throws IOException 
	 */
	public static void processCommande(String commande) throws IOException {
		cmd.listCommande(commande);
		
	}
	
/**
 * 
 * @param nomRelation
 * @param nombreColonnes
 * @param typesDesColonnes
 * @throws IOException 
 */
	public static void createRelation(String nomRelation, int nombreColonnes, ArrayList<String> typesDesColonnes) throws IOException {
		RelDefShema newRelation = new RelDefShema(nomRelation, nombreColonnes);
		newRelation.settypeDeColonne(typesDesColonnes);
		
		int reconewRelDefSize = 0;

		for(String s : typesDesColonnes) {
			switch(s.toLowerCase()) {
				case "int" :  reconewRelDefSize += 4;
				break;
				case "float" : reconewRelDefSize += 4;
				break;
				default : reconewRelDefSize += 2*Integer.parseInt(s.substring(6));
			}
		}

		int slotCount = Constants.pageSize/(reconewRelDefSize+1);

		
		RelDef newRelDef = new RelDef(newRelation);
		newRelDef.setfileIdx(db.getcpt());
		newRelDef.setrecordSize(reconewRelDefSize);
		newRelDef.setslotCount(slotCount);

		db.AddRelation(newRelDef);

		db.incrCpt();

		HeapFile heapFile = new HeapFile(newRelDef);
		listeHeapFile.add(heapFile);
		
		try {
			heapFile.createNewOnDisk();
		}catch(IOException e) {
			e.printStackTrace();
		}
/*
		try {
			DiskManager.createFile(newRelDef.getfileIdx());
		}catch(IOException e) {
			e.printStackTrace();
		}
		
 		*/
	}

	
	

	public static void init() throws FileNotFoundException, IOException, ClassNotFoundException { 
		db = new DBDef();
		
		File fichier =  new File(Constants.catalogRep);
		if(fichier.exists()) {

			try(FileInputStream fis = new FileInputStream(fichier);ObjectInputStream ois =  new ObjectInputStream(fis);){

				db = (DBDef)ois.readObject();
			}
		}

		refreshHeapFiles();

	}

	public static void clean() throws FileNotFoundException, ClassNotFoundException, IOException {

		BufferManager.flushAll();


		File catalogDef = new File(Constants.catalogRep);
		if(catalogDef.delete()) {
			System.out.println("Suppression ... \"Catalog.def\" supprimé avec succès !");
		}
		else {
			System.out.println("*** Aucun fichier \"Catalog.def\" présent ! ***\n");
		}


		for(int i = 0; i<listeHeapFile.size(); i++) {
			File dataRf = new File("."+File.separatorChar+"DB"+File.separatorChar+"Data_"+i+".rf");
			if(dataRf.delete()) {
				System.out.println("Suppression ... Relation supprimée !");
			}
		}


		db.raz();

		listeHeapFile = new ArrayList<HeapFile>(0);
		
		System.out.println("Suppression ... La base de données a été supprimée avec succès !\n");
	}


	public static void refreshHeapFiles() {
		listeHeapFile = new ArrayList<HeapFile>(0);
		for(RelDef r : db.getlistRelDef()) {
			listeHeapFile.add(new HeapFile(r));
		}
	}


	public static void finish() throws FileNotFoundException, IOException {
		File fichier =  new File(Constants.catalogRep);


		try{
				FileOutputStream fos = new FileOutputStream(fichier);
				ObjectOutputStream oos =  new ObjectOutputStream(fos);
				oos.writeObject(db);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		BufferManager.flushAll();
	}
	
	
	
	
	
	
	public static void insert(String nomRelation,ArrayList<String> listValeurs) throws IOException {
		Record r = new Record();
		r.setValue(listValeurs);
		
		ArrayList<RelDef> listRelation = db.getlistRelDef();
		RelDef relFind = null;
		boolean find = false;
		
		for(int i = 0;i<listRelation.size();i++) {
			String nameRel = listRelation.get(i).getrelDef().getnomDeRelation();
			if(nomRelation.equals(nameRel)) {
				relFind = listRelation.get(i);
				find = true;
				break;
			}
		}
		
		if(find) {
			HeapFile hf = new HeapFile(relFind);
			hf.insertRecord(r);
		}
		else {
			System.out.println("*** Erreur ! Ce nom de relation n'existe pas ! Veuillez ressayer. ***\n");
		}	
	}

	
	public static DBDef getDB(){
		return db;
	}
	
	public static ArrayList<HeapFile> getListeHeapFile(){
		return listeHeapFile;
	}
	
	
}
