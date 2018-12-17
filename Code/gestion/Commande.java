package gestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ManagerFolder.DBManager;
import ManagerFolder.FileManager;
import ManagerFolder.HeapFile;
import shema.RelDefShema;
import constants.Constants;

public class Commande {
	
	public static String listCommande(String c) throws IOException {		
		String action;
		 
		StringTokenizer st = new StringTokenizer(c," ");
		action = st.nextToken();
		
		switch(action) {
		
		case "create":
			System.out.println("create methode");
			Create(c);
			
			
			
			break;
		case "insert" : 
			System.out.println("insert methode");
			inert(c);	
			break;
		case "selectall" : 
			System.out.println("selectAll methode");
			selectAll(c);	
			break;
		case "clean" : 
			System.out.println("clean methode");
			clean(c);	
		break;
		case "select" : 
			System.out.println("select methode");
			select(c);	
		break;
		case "fill" : 
			System.out.println("fill methode");
			fill(c);
		break;
		default: 
			System.out.println("don't know the commande ");
			break;
		}
		
		return c;
	}


		/**
		 * 
		 * @param command
		 * @throws IOException
		 */
	public static void Create(String commande) throws IOException{
		
		RelDefShema relation = new RelDefShema();
	
		try {
			relation = definitionDeLaRelation(commande.substring(7));
		}catch(IllegalArgumentException e) {
			System.out.println("\n*** " + e.getMessage()+ " ***\n");
		}
		DBManager.createRelation(relation.getnomDeRelation(), relation.getnbDeColonne(), relation.gettypeDeColonne());
	}
	
	
/**
 * 
 * @param command
 */
	public static void inert(String commande) {
		
		StringTokenizer st = new StringTokenizer(commande," ");
		
		st.nextToken();
		
		String nomDeRelation = st.nextToken();
		
		ArrayList<String> listeDeRelation = new ArrayList<String>(0);
		
		while(st.hasMoreTokens()) {
			listeDeRelation.add(st.nextToken());
		}
		try {
			DBManager.insert(nomDeRelation, listeDeRelation);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	
		/**
		 * 
		 * @param command
		 */
	public static void select(String commande) {
		
		StringTokenizer st = new StringTokenizer(commande.substring(7), " ");
		String nomRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		String condition = st.nextToken();
		
		ArrayList<HeapFile> list = FileManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list,nomRelation);
		
		if(relFind != null) {
			try {
				relFind.printAllRecordsWithFilter(indiceCol, condition);
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de l'affichage des records !");
				System.out.println("Détails : " + e.getMessage());
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}
	

	
	

	private static void fill(String command) {
		
		StringTokenizer commande = new StringTokenizer(command.substring(5)," ");
		String nomRel = commande.nextToken();
		String nomFichier = commande.nextToken();
		
		ArrayList<String> contenuCSV = null;
		
		try {
			contenuCSV = extractContenuCSV(nomFichier);
			try{
				extractRecords(contenuCSV,nomRel);
			}catch(IOException e) {
				System.out.println("Une erreur est survenue !");
				System.out.println("Détails : " + e.getMessage());
			}
		}catch(IOException e) {
			System.out.println("Une erreur est survenue !");
			System.out.println("Détails : " + e.getMessage());
		}
	}

	
	
	
	public static ArrayList<String> extractContenuCSV(String nomFichier) throws IOException{
		
		ArrayList<String> contenuCSV = new ArrayList<String>();
        try(FileReader fr = new FileReader(Constants.CSVPATH + nomFichier);BufferedReader br = new BufferedReader(fr)){
        	String ligne;
        	do{
        		ligne = br.readLine();
        		if(ligne != null) {
        			contenuCSV.add(ligne);
        		}
        	}while(ligne != null);
        }catch(IOException e) {
        	System.out.println("Problème de lecture : " + e.getMessage());
        }
        return contenuCSV;
	}


	
	
	
	

	public static void clean(String command) {
		
		try {
			DBManager.clean();
		}catch(Exception e) {
			System.out.println("Une erreur est survenue lors de la suppression ! ");
			System.out.println("Détails : " + e.getMessage());
		}
	}
	
	
	public static void selectAll(String command) {
		
		String nomRelation = command.substring(10);
		ArrayList<HeapFile> list = FileManager.getListeHeapFile();
		HeapFile relFind = findHeapFile(list,nomRelation);
		
		if(relFind != null) {
			try {
				relFind.printAllRecords();
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de l'affichage des records !");
				System.out.println("Détails : " + e.getMessage());
			}
		}
		else {
			System.out.println("*** \"" +nomRelation + "\" n'existe pas dans la base de données ! ***\n");
		}
	}
	

	
	
	
	public static void extractRecords(ArrayList<String> contenuCSV, String nomRel) throws IOException {
		for(int i = 0; i<contenuCSV.size(); i++) {
			String ligne = contenuCSV.get(i);
			StringTokenizer st = new StringTokenizer(ligne, ",");
			
			ArrayList<String> listValeurs = new ArrayList<String>(0);
			
			while(st.hasMoreTokens()) {
				listValeurs.add(st.nextToken());
			}
			
			DBManager.insert(nomRel, listValeurs);
		}
	}
	
	public static HeapFile findHeapFile(ArrayList<HeapFile> list,String nomRelation) {
		HeapFile relFind = null;
		
		for(int i =0 ;i<list.size();i++) {
			String nomRelCourant = list.get(i).getrelDef().getrelDef().getnomDeRelation();
			HeapFile relCourant = list.get(i);
			if(nomRelCourant.equals(nomRelation)) {
				relFind = relCourant;
			}
		}
		return relFind;
	}
	
	
	
	
	/**
	 * 
	 * @param commandeComplete
	 * @return
	 * @throws IllegalArgumentException
	 */
	public static RelDefShema definitionDeLaRelation(String commandeComplete){
		String commande = commandeComplete.trim();
		
		StringTokenizer st = new StringTokenizer(commande," ");
		
		RelDefShema relDef = new RelDefShema();
		ArrayList<String> typeDeColonne = new ArrayList<String>(0);
		
		String nomDeRelation = st.nextToken();
		
		relDef.setnomDeRelation(nomDeRelation);
		
		int nbDeColonne = Integer.parseInt(st.nextToken());
		
		relDef.setnbDeColonne(nbDeColonne);
		
		while (st.hasMoreTokens()) {
			String typeDeColonnes = st.nextToken().toLowerCase();
			if(typeDeColonnes.equals("int") || typeDeColonnes.equals("float") || typeDeColonnes.contains("string")) {
				typeDeColonne.add(typeDeColonnes);
			}
			else {
				System.out.println("ERROR");
			}
		}
		relDef.settypeDeColonne(typeDeColonne);
		
		return relDef;
	}
}
