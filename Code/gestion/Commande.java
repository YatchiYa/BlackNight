package gestion;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ManagerFolder.DBManager;
import ManagerFolder.FileManager;
import ManagerFolder.HeapFile;
import shema.RelDefShema;
import constants.Constants;

public class Commande {
	
	public String listCommande(String c) throws IOException {		
		String action;
		 
		StringTokenizer st = new StringTokenizer(c," ");
		action = st.nextToken();
		
		switch(action) {
		
		case "create":
			System.out.println(" Appel A la methode Create");
			Create(c);
			break;
		case "join":
			System.out.println(" Appel A la methode join");
			join(c);
			break;
		case "insert" : 
			System.out.println("Appel A la methode insert");
			inert(c);	
			break;
		case "selectall" : 
			System.out.println("Appel A la methode selectAll");
			selectAll(c);	
			break;
		case "clean" : 
			System.out.println(" Appel A la methode clean");
			clean(c);	
		break;
		case "select" : 
			System.out.println("Appel A la methode select");
			select(c);	
		break;
		case "fill" : 
			System.out.println("Appel A la methode fill ");
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
			e.printStackTrace();
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
	
	
	
	
	

	private static void fill(String commande) throws IOException {
		
		StringTokenizer commandx = new StringTokenizer(commande.substring(5)," ");
		String nomDeRelation = commandx.nextToken();
		String nomDuFichier = commandx.nextToken();
		
		ArrayList<String> contenuCSV = null;
		
		try {
			contenuCSV = new ArrayList<String>();
			    try(
				    	FileReader fileReader = new FileReader(Constants.CSVPATH + nomDuFichier);
			    		BufferedReader bufferReader = new BufferedReader(fileReader))
			    
			    
			    {
			    	String line;
			    	do{
			    		line = bufferReader.readLine();
			    		if(line != null) {
			    			contenuCSV.add(line);
			    		}
			    	}while(line != null);
			    }catch(IOException e) {
			    	e.printStackTrace();
			    }
			    
				try{
					
					for(int i = 0; i<contenuCSV.size(); i++) {
						String line = contenuCSV.get(i);
						StringTokenizer st = new StringTokenizer(line, ",");
						
						ArrayList<String> valeurs = new ArrayList<String>(0);
						
						while(st.hasMoreTokens()) {
							valeurs.add(st.nextToken());
						}
						
						DBManager.insert(nomDeRelation, valeurs);
					}
					
				}catch(IOException e) {
					e.printStackTrace();
				}
				
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	
	
	
	
	

	public static void clean(String command) {
		
		try {
			DBManager.clean();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void selectAll(String commande) {
		
		String nomDeLaRelation = commande.substring(10);
		ArrayList<HeapFile> listHeapFile = FileManager.getListeHeapFile();
		HeapFile iSrelation = null;
						
		for(int i =0 ;i<listHeapFile.size();i++) {
			String nomRelationC = listHeapFile.get(i).getrelDef().getrelDef().getnomDeRelation();
			HeapFile relationC = listHeapFile.get(i);
			if(nomRelationC.equals(nomDeLaRelation)) {
				iSrelation = relationC;
			}
		}
		
		if(iSrelation != null) {
			try {
				DBManager.affichageRecords(iSrelation.getrelDef());
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("199 ERROR");
		}
	}
	
	/**
	 * 
	 * @param command
	 */
	public static void select(String commande) {
		
		StringTokenizer st = new StringTokenizer(commande.substring(7), " ");
		String nomDeLaRelation = st.nextToken();
		int indiceCol = Integer.parseInt(st.nextToken());
		String condition = st.nextToken();
		
		ArrayList<HeapFile> listHeapFile = FileManager.getListeHeapFile();
		HeapFile iSrelation = null;
						
		for(int i =0 ;i<listHeapFile.size();i++) {
			String nomRelationC = listHeapFile.get(i).getrelDef().getrelDef().getnomDeRelation();
			HeapFile relationC = listHeapFile.get(i);
			if(nomRelationC.equals(nomDeLaRelation)) {
				iSrelation = relationC;
			}
		}
		
		if(iSrelation != null) {
			try {
				DBManager.affichageRecordsAvecFiltre(iSrelation.getrelDef(), indiceCol, condition);
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.out.println("ERROR");
		}
	}


	
public static void join(String command) {
		
		StringTokenizer st = new StringTokenizer(command.substring(4), " ");
		String nomRelation1 = st.nextToken();
		String nomRelation2 = st.nextToken();
		int indiceCol1 = Integer.parseInt(st.nextToken());
		int indiceCol2 = Integer.parseInt(st.nextToken());
		
		//Recuperer la list des heapfile puis chercher le nom de la rel
		ArrayList<HeapFile> list = FileManager.getListeHeapFile();
		
		
		HeapFile relFind1 = null;
				
		for(int i =0 ;i<list.size();i++) {
			String nomRelCourant = list.get(i).getrelDef().getrelDef().getnomDeRelation();
			HeapFile relCourant = list.get(i);
			if(nomRelCourant.equals(nomRelation1)) {
				relFind1 = relCourant;
			}
		}


		HeapFile relFind2 = null;
		
		for(int i =0 ;i<list.size();i++) {
			String nomRelCourant = list.get(i).getrelDef().getrelDef().getnomDeRelation();
			HeapFile relCourant = list.get(i);
			if(nomRelCourant.equals(nomRelation2)) {
				relFind2 = relCourant;
			}
		}
		
		if(relFind1 != null && relFind2 != null) {
			try {
				FileManager.join(relFind1,relFind2, indiceCol1, indiceCol2);
				
			}catch(IOException e) {
				System.out.println("Une erreur s'est produite lors de la jointure !");
				System.out.println("Détails : " + e.getMessage());
			}
			
		}
		else {
			System.out.println("*** Une des 2 relations n'existe pas dans la base de données ! ***\n");
		}
		
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
