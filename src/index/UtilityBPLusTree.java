package index;

import java.io.IOException;
import java.util.ArrayList;

import ManagerFolder.BufferManager;
import ManagerFolder.HeapFile;
import shema.PageId;
import shema.Record;

public class UtilityBPLusTree {
	
	public static Noeud courant;
	
	public static void bulkLoading(Arbre arbre,ArrayList<Noeud> listeFeuilles){
		int ordreArbre = arbre.getOrdre(); 

		//debut remplissage
		int i = 0;
		//on parcourt toutes les feuilles
		while(i != listeFeuilles.size()) {
			if(i==0) {
				//on cree un premier noeud qui sera la potentielle racine de l'arbre 
				Noeud newNoeud = new Noeud(ordreArbre);
				arbre.setRacine(newNoeud);
				listeFeuilles.get(i).setPere(newNoeud);
				newNoeud.addFils(listeFeuilles.get(i));
				newNoeud.incrementerNbFils();
				courant = newNoeud;
			}	
			else {
				if(courant.isFull()) {
					courant = split(courant, arbre);
					insererDansNoeud(courant, listeFeuilles.get(i));
				}
				else {
					insererDansNoeud(courant, listeFeuilles.get(i));
				}
			}
			i++;
		}
	
	}
	
	public static void insererDansNoeud(Noeud noeud, Noeud feuille) {	
		noeud.addFils(feuille);
		noeud.incrementerNbFils();
		
		String val = feuille.getValeurs().get(0);
		noeud.addVal(val);
		noeud.incrementerNbVal();
		
		feuille.setPere(noeud);
	}
	
	public static Noeud split(Noeud noeud, Arbre arbre) {
		int ordreArbre = arbre.getOrdre();
		Noeud pereDuNoeud = noeud.getPere();
	
			if(pereDuNoeud == null) {
				//on cree un nouveau frere et un nouveau pere
				Noeud newFrere = new Noeud(ordreArbre);
				Noeud newPere = new Noeud(ordreArbre);
				
				//cle du milieu = d+1 eme valeur
				String cleMilieu = noeud.getValeurs().get(ordreArbre);
				
				//on ne garde pas la cle du mileu !
				noeud.removeVal(ordreArbre);
				noeud.decrementerNbVal();
				
				ArrayList<Noeud> filsRecup = new ArrayList<Noeud>(0);
				for(int i = ordreArbre+1; i<noeud.getFils().size();i++) {
					filsRecup.add(noeud.getFils().get(i));
				}
				newFrere.setFils(filsRecup);
				newFrere.setNbFils(filsRecup.size());
				
				//suppression des fils
				int j = 0;
				while(j != filsRecup.size()) {
					int i = noeud.getFils().size()-1;
					noeud.removeFils(i);
					noeud.decrementerNbFils();
					j++;
				}
				
				//on coupe le noeud en 2 partitions 
				//1ere partition = d premiere val
				//2eme partition = les d+2 derniere val
				ArrayList<String> firstPart = new ArrayList<String>(0);
				ArrayList<String> secondPart = new ArrayList<String>(0);
				for(int i=0;i<ordreArbre;i++) {
					firstPart.add(noeud.getValeurs().get(i));
				}
				for(int i=ordreArbre;i<noeud.getValeurs().size();i++) {
					secondPart.add(noeud.getValeurs().get(i));
				}
				
				noeud.setValeurs(firstPart);
				noeud.setNbVal(firstPart.size());
				newFrere.setValeurs(secondPart);
				newFrere.setNbVal(secondPart.size());
				
				newPere.addFils(noeud);
				newPere.incrementerNbFils();
				newPere.addFils(newFrere);
				newPere.incrementerNbFils();
				newPere.addVal(cleMilieu);
				newPere.incrementerNbVal();
				arbre.setRacine(newPere);
				
				noeud.setPere(newPere);
				newFrere.setPere(newPere);
				
				return newFrere;
			}
			else {
				if(pereDuNoeud.isFull()) {
					Noeud n = split(pereDuNoeud,arbre);
					
					noeud.setPere(n);
					
					return split(noeud, arbre);
				}
				else {
					//on cree un nouveau frere
					Noeud newFrere = new Noeud(ordreArbre);
					
					//cle du milieu = d+1 eme valeur
					String cleMilieu = noeud.getValeurs().get(ordreArbre);
					
					//on ne garde pas la cle du mileu !
					noeud.removeVal(ordreArbre);
					noeud.decrementerNbVal();
					
					ArrayList<Noeud> filsRecup = new ArrayList<Noeud>(0);
					for(int i = ordreArbre+1; i<noeud.getFils().size();i++) {
						filsRecup.add(noeud.getFils().get(i));
					}
					newFrere.setFils(filsRecup);
					newFrere.setNbFils(filsRecup.size());
					
					//suppression des fils
					int j = 0;
					while(j != filsRecup.size()) {
						int i = noeud.getFils().size()-1;
						noeud.removeFils(i);
						noeud.decrementerNbFils();
						j++;
					}
					
					//on coupe le noeud en 2 partitions 
					//1ere partition = d premiere val
					//2eme partition = les d+1 derniere val
					ArrayList<String> firstPart = new ArrayList<String>(0);
					ArrayList<String> secondPart = new ArrayList<String>(0);
					for(int i=0;i<ordreArbre;i++) {
						firstPart.add(noeud.getValeurs().get(i));
					}
					for(int i=ordreArbre;i<noeud.getValeurs().size();i++) {
						secondPart.add(noeud.getValeurs().get(i));
					}
					
					noeud.setValeurs(firstPart);
					noeud.setNbVal(firstPart.size());
					newFrere.setValeurs(secondPart);
					newFrere.setNbVal(secondPart.size());
					
					pereDuNoeud.addFils(newFrere);
					pereDuNoeud.incrementerNbFils();
					pereDuNoeud.addVal(cleMilieu);
					pereDuNoeud.incrementerNbVal();
					
					newFrere.setPere(pereDuNoeud);
					
					return newFrere;
				}
			}
	}
	
	public static void selectInBtree(Noeud n, String val, String type, HeapFile hf) throws IOException {
		//il faut réussir à chainer toutes les feuilles entre elles
		ArrayList<Noeud> fils = n.getFils();
		ArrayList<String> valeurs = n.getValeurs();
		//cas racine
		if(n.getNbVal() == 0) {
			selectInBtree(fils.get(0), val, type, hf);
		}
		//cas feuille
		else if(n.getNbFils() == 0) {
			ArrayList<PageId> pages = n.getPages();
			ArrayList<Integer> numSlots = n.getNumSlot();
			
			int totalRecordPrinted = 0;
			//verification que val est présent dans le noeud courant
			for(int i = 0; i<valeurs.size(); i++) {
				if(val.equals(valeurs.get(i))) {
					PageId pageCourante = pages.get(i);
					Integer numSlotCourant = numSlots.get(i);
					
					//on recupere le contenu de la page courante
					byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
					
					Record recordToPrint = new Record(pageCourante, numSlotCourant.intValue());
					
					hf.readRecordFromBuffer(recordToPrint, bufferPageCourante, recordToPrint.getNumSlot());
					
					System.out.println(recordToPrint.toString());
					totalRecordPrinted++;
					
					BufferManager.freePage(pageCourante, 0);
				}
			}
			
			//Cas avec des doublons
			
			//verification que val est present dans les freres Droits Directs
			/*Noeud frereD = n.getFrereD();
	
			while(frereD != null) {
				if(frereD.getValeurs().contains(val)) {
					ArrayList<PageId> pagesFD = frereD.getPages();
					ArrayList<Integer> numSlotsFD = frereD.getNumSlot();
					
					ArrayList<String> valeursFD = frereD.getValeurs();
					
					for(int i =0; i<valeursFD.size(); i++) {
						if(val.equals(valeursFD.get(i))) {
							PageId pageCourante = pagesFD.get(i);
							Integer numSlotCourant = numSlotsFD.get(i);
							
							//on recupere le contenu de la page courante
							byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
							
							Record recordToPrint = new Record(pageCourante, numSlotCourant.intValue());
							
							hf.readRecordFromBuffer(recordToPrint, bufferPageCourante, recordToPrint.getNumSlot());
							
							System.out.println(recordToPrint.toString());
							totalRecordPrinted++;
							
							BufferManager.freePage(pageCourante, 0);
						}
					}
					frereD = frereD.getFrereD();
				}
				else {
					frereD = null;
				}
			}*/
			
		
			//verification que val est present dans les Freres Gauches Directes
			/*Noeud frereG = n.getFrereG();

			while(frereG != null ) {
				if(frereG.getValeurs().contains(val)) {
					ArrayList<PageId> pagesFG = frereG.getPages();
					ArrayList<Integer> numSlotsFG = frereG.getNumSlot();
					
					ArrayList<String> valeursFG = frereG.getValeurs();
					
					for(int i =0; i<valeursFG.size(); i++) {
						if(valeursFG.get(i).equals(val)) {
							PageId pageCourante = pagesFG.get(i);
							Integer numSlotCourant = numSlotsFG.get(i);
							
							//on recupere le contenu de la page courante
							byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
							
							Record recordToPrint = new Record(pageCourante, numSlotCourant.intValue());
							
							hf.readRecordFromBuffer(recordToPrint, bufferPageCourante, recordToPrint.getNumSlot());
							
							System.out.println(recordToPrint.toString());
							totalRecordPrinted++;
							
							BufferManager.freePage(pageCourante, 0);
						}
					}
					frereG = frereG.getFrereG();
				}
				else {
					frereG = null;
				}
			}*/
			System.out.println("Total records : " + totalRecordPrinted);
		}
		else {
			Noeud find = null;
			
			for(int i = 0; i<fils.size();i++) {
				
				switch(type.toLowerCase()) {
				
					case "int" :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(Integer.parseInt(val) < Integer.parseInt(valeurs.get(i))) {
								find = fils.get(i);

								selectInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(Integer.parseInt(val) >= Integer.parseInt(valeurs.get(i-1))) {
								find = fils.get(i);
								
								selectInBtree(find, val, type, hf);
							}
						}
						else {
							if(Integer.parseInt(val) >= Integer.parseInt(valeurs.get(i-1)) && Integer.parseInt(val) < Integer.parseInt(valeurs.get(i))) {
								find = fils.get(i);
								
								selectInBtree(find, val, type, hf);
							}
						}
						
					break;
					
					case "float" :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(Float.parseFloat(val) < Float.parseFloat(valeurs.get(i))) {
								find = fils.get(i);

								selectInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(Float.parseFloat(val) >= Float.parseFloat(valeurs.get(i-1))) {
								find = fils.get(i);
								
								selectInBtree(find, val, type, hf);
							}
						}
						else {
							if(Float.parseFloat(val) >= Float.parseFloat(valeurs.get(i-1)) && Float.parseFloat(val) < Float.parseFloat(valeurs.get(i))) {
								find = fils.get(i);
								
								selectInBtree(find, val, type, hf);
							}
						}
						
					break;
					
					default :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(val.compareTo(valeurs.get(i))<0) {
								find = fils.get(i);

								selectInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(val.compareTo(valeurs.get(i-1)) >= 0) {
								find = fils.get(i);

								selectInBtree(find, val, type, hf);
							}
						}
						else {
							if(val.compareTo(valeurs.get(i-1)) >= 0 && val.compareTo(valeurs.get(i)) < 0) {
								find = fils.get(i);

								selectInBtree(find, val, type, hf);
							}
						}
				}
			}
		}
	}
	
	public static Record getRecordInBtree(Noeud n, String val, String type, HeapFile hf) throws IOException {
		//il faut réussir à chainer toutes les feuilles entre elles
		ArrayList<Noeud> fils = n.getFils();
		ArrayList<String> valeurs = n.getValeurs();
		//cas racine
		if(n.getNbVal() == 0) {
			return getRecordInBtree(fils.get(0), val, type, hf);
		}
		//cas feuille
		else if(n.getNbFils() == 0) {
			ArrayList<PageId> pages = n.getPages();
			ArrayList<Integer> numSlots = n.getNumSlot();
			
			//verification que val est présent dans le noeud courant
			for(int i = 0; i<valeurs.size(); i++) {
				if(val.equals(valeurs.get(i))) {
					PageId pageCourante = pages.get(i);
					Integer numSlotCourant = numSlots.get(i);
					
					//on recupere le contenu de la page courante
					byte[] bufferPageCourante = BufferManager.getPage(pageCourante);
					
					Record recordToPrint = new Record(pageCourante, numSlotCourant.intValue());
					
					hf.readRecordFromBuffer(recordToPrint, bufferPageCourante, recordToPrint.getNumSlot());
					
					BufferManager.freePage(pageCourante, 0);
					
					return recordToPrint;
				}
			}
		}
		else {
			Noeud find = null;
			
			for(int i = 0; i<fils.size();i++) {
				
				switch(type.toLowerCase()) {
				
					case "int" :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(Integer.parseInt(val) < Integer.parseInt(valeurs.get(i))) {
								find = fils.get(i);

								return getRecordInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(Integer.parseInt(val) >= Integer.parseInt(valeurs.get(i-1))) {
								find = fils.get(i);
								
								return getRecordInBtree(find, val, type, hf);
							}
						}
						else {
							if(Integer.parseInt(val) >= Integer.parseInt(valeurs.get(i-1)) && Integer.parseInt(val) < Integer.parseInt(valeurs.get(i))) {
								find = fils.get(i);
								
								return getRecordInBtree(find, val, type, hf);
							}
						}
						
					break;
					
					case "float" :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(Float.parseFloat(val) < Float.parseFloat(valeurs.get(i))) {
								find = fils.get(i);

								return getRecordInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(Float.parseFloat(val) >= Float.parseFloat(valeurs.get(i-1))) {
								find = fils.get(i);
								
								return getRecordInBtree(find, val, type, hf);
							}
						}
						else {
							if(Float.parseFloat(val) >= Float.parseFloat(valeurs.get(i-1)) && Float.parseFloat(val) < Float.parseFloat(valeurs.get(i))) {
								find = fils.get(i);
								
								return getRecordInBtree(find, val, type, hf);
							}
						}
						
					break;
					
					default :
						//parcours dans les noeuds intermédiaires
						if(i==0) {
							if(val.compareTo(valeurs.get(i))<0) {
								find = fils.get(i);

								return getRecordInBtree(find, val, type, hf);
							}
						}
						else if(i == fils.size()-1) {
							if(val.compareTo(valeurs.get(i-1)) >= 0) {
								find = fils.get(i);

								return getRecordInBtree(find, val, type, hf);
							}
						}
						else {
							if(val.compareTo(valeurs.get(i-1)) >= 0 && val.compareTo(valeurs.get(i)) < 0) {
								find = fils.get(i);

								return getRecordInBtree(find, val, type, hf);
							}
						}
				}
			}
		}
		return null;
	}

	/**
	 * Trie les records selon le type de la colonne
	 * @param list
	 * @param nbCol
	 * @param type
	 */
	public static void sortRecord(ArrayList<Record> list, int nbCol, String type) {
		switch(type.toLowerCase()) {
			case "int" :
				for(int i = 0; i<list.size()-1; i++) {
					for(int j = i+1; j<list.size(); j++) {
						int val1 = Integer.parseInt(list.get(i).getListValues().get(nbCol-1));
						int val2 = Integer.parseInt(list.get(j).getListValues().get(nbCol-1));
						
						if(val1>val2) {
							Record temp = list.get(i);
							list.set(i, list.get(j));
							list.set(j, temp);
						}
					}
				}
				
			break;
			case "float" :
				for(int i = 0; i<list.size()-1; i++) {
					for(int j = i+1; j<list.size(); j++) {
						float val1 = Float.parseFloat(list.get(i).getListValues().get(nbCol-1));
						float val2 = Float.parseFloat(list.get(j).getListValues().get(nbCol-1));
						
						if(val1>val2) {
							Record temp = list.get(i);
							list.set(i, list.get(j));
							list.set(j, temp);
						}
					}
				}
				
				
			break;
			//type = stringX
			default :
				for(int i = 0; i<list.size()-1; i++) {
					for(int j = i+1; j<list.size(); j++) {
						String val1 = list.get(i).getListValues().get(nbCol-1);
						String val2 = list.get(j).getListValues().get(nbCol-1);
						
						if(val1.compareTo(val2) > 0) {
							Record temp = list.get(i);
							list.set(i, list.get(j));
							list.set(j, temp);
						}
					}
				}
				
			break;	
		}
	}
	

	public static ArrayList<Noeud> getListeFeuilles(ArrayList<Record> listeRecords, int ordre, int nbCol) {
		int max_entries= 2*ordre;
		
		ArrayList<Noeud> feuilles = new ArrayList<Noeud>(0);
		
		int i = 0;
		
		while(i != listeRecords.size()) {
			Noeud newFeuille = new Noeud(ordre);
			
			ArrayList<String> listValeursNewFeuille = new ArrayList<String>(max_entries);
			ArrayList<Integer> listNumSlotNewFeuille = new ArrayList<Integer>(max_entries);
			ArrayList<PageId> listPageIdNewFeuille = new ArrayList<PageId>(max_entries);
			
			for(int j = 0; j<max_entries; j++) {
				if(i != listeRecords.size()) {
					
					listValeursNewFeuille.add(listeRecords.get(i).getListValues().get(nbCol-1));
					listNumSlotNewFeuille.add(new Integer(listeRecords.get(i).getNumSlot()));
					listPageIdNewFeuille.add(listeRecords.get(i).getPage());
					
					newFeuille.incrementerNbVal();
					i++;
				}
			}
			
			newFeuille.setValeurs(listValeursNewFeuille);
			newFeuille.setNumSlot(listNumSlotNewFeuille);
			newFeuille.setPages(listPageIdNewFeuille);
			
			feuilles.add(newFeuille);
		}
		
		chainerFeuilles(feuilles);
		 
		return feuilles;
	}
	
	/**
	 * Cette méthode permet de chainer toutes les feuilles entre elles.<br>
	 * 
	 * Un frere Gauche et/ou un frere Droit
	 * 
	 * @param feuilles liste des feuilles à chainer
	 */
	public static void chainerFeuilles(ArrayList<Noeud> feuilles) {
		Noeud feuille = feuilles.get(0);
			
		for(int i = 1; i<feuilles.size(); i++) {
			feuille.setFrereD(feuilles.get(i));
			feuilles.get(i).setFrereG(feuille);

			feuille = feuilles.get(i);
		}
	}
}
