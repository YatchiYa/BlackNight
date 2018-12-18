package ManagerFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import gestion.Commande;
import shema.Bytemap;
import shema.DBDef;
import shema.HeaderPageInfo;
import shema.PageId;
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
			DiskManager.createFile(newRelDef.getfileIdx());
		}catch(IOException e) {
			System.out.println("*** Une ereur s'est produite lors de la création du fichier ! ***");
			System.out.println("Détails : " + e.getMessage());
		}
		
		try {
			FileManager.createNewHeapFile(newRelDef);
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
		try {
			catalogDef.delete();
			System.out.println("Suppression du fichier catalog.def ");
			
		}catch(Exception e) {
			System.out.println(" le fichier catalog.def n'existe pas ");
			e.printStackTrace();
		}


		for(int i = 0; i<FileManager.getListeHeapFile().size(); i++) {
			File file = new File("."+File.separatorChar+"DB"+File.separatorChar+"Data_"+i+".rf");
			try {
				file.delete();
				System.out.println("relation supprimé");
			}catch(Exception e) {
				System.out.println("probleme de suppression de la relation");
				e.printStackTrace();
			}
		}

		db.raz();
		FileManager.setListeHeapFile(new ArrayList<HeapFile>(0));
		System.out.println(" la base de donnée est supprimer !!! ");
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
		boolean check = false;
		
		for(int i = 0;i<listeDeRelation.size();i++) {
			String nomDeRelation = listeDeRelation.get(i).getrelDef().getnomDeRelation();
			if(nomRelation.equals(nomDeRelation)) {
				pointerRelation = listeDeRelation.get(i);
				check = true;
				break;
			}
		}
		
		try {
			if(check) {
				fileManager.insertRecordInRelation(pointerRelation, record);
			}
			else {
				System.out.println("error ");
			}
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	
	
	

	public static void affichageRecords(RelDef relDef) throws IOException {

		int fileHeaderPage = relDef.getfileIdx();
		int slotCpt = relDef.getslotCount();
		int sizeOfRecord = relDef.getrecordSize();
		int tot = 0;
		
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();

		HeapFileTreatment.getHPI(headerPageInfo, relDef);
		
		ArrayList<Integer> listPage = headerPageInfo.getpageIdx();
		
		for(int i=0; i<listPage.size(); i++) {
			int iPageC = listPage.get(i).intValue();
			PageId pageC = new PageId(fileHeaderPage, iPageC); 


			byte[] bufferPage = BufferManager.getPage(pageC);
			
			Bytemap bytemapPageC = new Bytemap();
			int nombreDeSlot = relDef.getslotCount(); // = slotCount
			
			ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


			for(int j = 0; j<nombreDeSlot; j++) {
				Byte indi = new Byte(buffer.get());
				bytemapPageC.addindiceDuSlot(indi);
			}
			
			ArrayList<Byte> indicePlageC = bytemapPageC.getIndiceDuSlot();
			
			for(int j = 0; j<indicePlageC.size(); j++) {
				if(indicePlageC.get(j).byteValue() == (byte)1) {
					int indicej = j;

					Record recordAjoute = new Record();
					recordAjoute = HeapFile.readRecordFromBuffer(bufferPage, slotCpt + indicej*sizeOfRecord);
					System.out.println(recordAjoute.toString());
					tot++;
				}
			}

			BufferManager.freePage(pageC, 0);
		}
		System.out.println("records : " + tot);
	}

	
	
	public static void affichageRecordsAvecFiltre(RelDef relDef, int iIdxCol, String iValeur) throws IOException {

		int fileHeaderPage = relDef.getfileIdx();
		int slotCpt = relDef.getslotCount();
		int sizeOfRecord = relDef.getrecordSize();
		
		int tot = 0;
		
		HeaderPageInfo headerPageInfo = new HeaderPageInfo();
		HeapFileTreatment.getHPI(headerPageInfo, relDef);
		
		ArrayList<Integer> listPage = headerPageInfo.getpageIdx();
		
		for(int i=0; i<listPage.size(); i++) {
			int iPageC = listPage.get(i).intValue();
			PageId pageC = new PageId(fileHeaderPage, iPageC); 


			byte[] bufferPage = BufferManager.getPage(pageC);
			
			Bytemap bytemapPageC = new Bytemap();
			int nombreDeSlot = relDef.getslotCount(); // = slotCount
						
			ByteBuffer buffer = ByteBuffer.wrap(bufferPage);


			for(int j = 0; j<nombreDeSlot; j++) {
				Byte indi = new Byte(buffer.get());
				bytemapPageC.addindiceDuSlot(indi);
			}
			
			ArrayList<Byte> indicePlageC = bytemapPageC.getIndiceDuSlot();
			
			for(int j = 0; j<indicePlageC.size(); j++) {
				if(indicePlageC.get(j).byteValue() == (byte)1) {
					int indicej = j;

					Record recordAjoute = new Record();
					recordAjoute = HeapFile.readRecordFromBuffer(bufferPage, slotCpt + indicej*sizeOfRecord);

					if(recordAjoute.getvalues().get(iIdxCol-1).equals(iValeur)) {
						System.out.println(recordAjoute.toString());
						tot++;
					}
				}
			}

			BufferManager.freePage(pageC, 0);
		}
		System.out.println("records : " + tot);
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
