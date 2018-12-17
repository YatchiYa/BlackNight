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
	private static FileManager fileManager;

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

		
		try {
			fileManager.createNewHeapFile(newRelDef);
		}catch(IOException e) {
			e.printStackTrace();
		}
/*
		
 		*/
	}

	
	

	public static void init() throws FileNotFoundException, IOException, ClassNotFoundException { 
		db = new DBDef();
		fileManager = new FileManager();
		db.init();
		FileManager.init();
		
		
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


		for(int i = 0; i<FileManager.getListeHeapFile().size(); i++) {
			File dataRf = new File("."+File.separatorChar+"DB"+File.separatorChar+"Data_"+i+".rf");
			if(dataRf.delete()) {
				System.out.println("Suppression ... Relation supprimée !");
			}
		}


		db.raz();

		FileManager.setListeHeapFile(new ArrayList<HeapFile>(0));
		
		System.out.println("Suppression ... La base de données a été supprimée avec succès !\n");
	}




	public static void finish() throws FileNotFoundException, IOException {

		db.finish();		
		BufferManager.flushAll();
	}
	
	
	
	
	
	
	public static void insert(String nomRelation,ArrayList<String> listValeurs) throws IOException {
		Record record = new Record();
		record.setValue(listValeurs);
		
		ArrayList<RelDef> listeDeRelation = db.getlistRelDef();
		RelDef pointerRelation = null;
		
		for(int i = 0;i<listeDeRelation.size();i++) {
			String nomDeRelation = listeDeRelation.get(i).getrelDef().getnomDeRelation();
			if(nomRelation.equals(nomDeRelation)) {
				pointerRelation = listeDeRelation.get(i);
				break;
			}
		}
		
		try {
			fileManager.insertRecordInRelation(pointerRelation, record);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	public static Commande getCmd() {
		return cmd;
	}

	public static void setCmd(Commande cmd) {
		DBManager.cmd = cmd;
	}

	public static DBDef getDb() {
		return db;
	}

	public static void setDb(DBDef db) {
		DBManager.db = db;
	}

	
}
