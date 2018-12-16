package gestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import ManagerFolder.DBManager;
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
			System.out.print("\\");
			System.out.println("(*-*)/");
			
			
			
			break;
		case "insert" : inert(c);	
			break;
		case "selectall" : selectAll(c);	
			break;
		case "clean" : clean(c);	
		break;
		case "select" : select(c);	
		break;
		case "fill" : fill(c);
		break;
		default: 
			System.out.println("don t know the commande ");
			break;
		}
		
		return c;
	}

	


	private static void fill(String command) {
		// TODO Auto-generated method stub
		
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


	public static void select(String command) {
		
		StringTokenizer st = new StringTokenizer(command.substring(7), " ");
		String nomRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		String condition = st.nextToken();
		
		ArrayList<HeapFile> list = DBManager.getListeHeapFile();
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
	
	
	
	public static void Create(String command) throws IOException{
		
		RelDefShema relation = new RelDefShema();
	
		try {
			relation = extractRel(command.substring(7));
		}catch(IllegalArgumentException e) {
			System.out.println("\n*** " + e.getMessage()+ " ***\n");
		}
		DBManager.createRelation(relation.getnomDeRelation(), relation.getnbDeColonne(), relation.gettypeDeColonne());
	}
	

	public static void clean(String command) {
		
		try {
			DBManager.clean();
		}catch(Exception e) {
			System.out.println("Une erreur est survenue lors de la suppression ! ");
			System.out.println("Détails : " + e.getMessage());
		}
	}
	
	public static void inert(String command) {
		
		StringTokenizer st = new StringTokenizer(command," ");
		st.nextToken();
		String name = st.nextToken();
		ArrayList<String> l = new ArrayList<String>(0);
		while(st.hasMoreTokens()) {
			l.add(st.nextToken());
		}
		try {
			DBManager.insert(name, l);
		}catch(IOException e) {
			
		}
	}
	
	public static void selectAll(String command) {
		
		String nomRelation = command.substring(10);
		ArrayList<HeapFile> list = DBManager.getListeHeapFile();
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
	

	
	public static RelDefShema extractRel(String chaine) throws IllegalArgumentException{
		//commande de l'user
		String commande = chaine.trim();
		
		//on récupère tous les mots séparés par un espace dans la commande de l'user
		StringTokenizer st = new StringTokenizer(commande," ");
		
		RelDefShema relation = new RelDefShema();
		ArrayList<String> typeCol = new ArrayList<String>(0);
		
		String nom = st.nextToken();
		relation.setnomDeRelation(nom);
		int nbCol = Integer.parseInt(st.nextToken());
		relation.setnbDeColonne(nbCol);
		while (st.hasMoreTokens()) {
			String type = st.nextToken().toLowerCase();
			if(type.equals("int") || type.equals("float") || type.contains("string")) {
				typeCol.add(type);
			}
			else {
				throw new IllegalArgumentException("Ce type n'est pas autorisé !");
			}
		}
		relation.settypeDeColonne(typeCol);
		
		return relation;
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
	
}
