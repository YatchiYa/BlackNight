package index;

import java.util.ArrayList;

import shema.PageId;

public class Noeud {
	
	private ArrayList<String> valeurs;
	private ArrayList<Noeud> fils;
	
	private Noeud pere;
	private Noeud frereG;
	private Noeud frereD;
	
	private int ordre;
	
	private int nbVal;
	private int nbFils;
	
	/**
	 * correspond au rid de chaque valeur dans une feuille
	 */
	private ArrayList<PageId> pages;
	private ArrayList<Integer> numSlot;
	
	public Noeud(int ordre) {
		this.valeurs = new ArrayList<String>(0);
		this.fils = new ArrayList<Noeud>(0);
		this.pere = null;
		this.frereG = null;
		this.frereD = null;
		
		this.ordre = ordre;
		this.nbVal = 0;
		this.nbFils = 0;
		
		this.pages = new ArrayList<PageId>(0);
		this.numSlot = new ArrayList<Integer>(0);
	}
	
	public Noeud getFrereG() {
		return frereG;
	}

	public void setFrereG(Noeud frereG) {
		this.frereG = frereG;
	}

	public Noeud getFrereD() {
		return frereD;
	}

	public void setFrereD(Noeud frereD) {
		this.frereD = frereD;
	}

	public ArrayList<PageId> getPages() {
		return pages;
	}

	public void setPages(ArrayList<PageId> pages) {
		this.pages = pages;
	}

	public ArrayList<Integer> getNumSlot() {
		return numSlot;
	}

	public void setNumSlot(ArrayList<Integer> numSlot) {
		this.numSlot = numSlot;
	}

	public void incrementerNbVal(){
		this.nbVal++;
	}
	
	public void decrementerNbVal(){
		this.nbVal--;
	}
	
	public void incrementerNbFils(){
		this.nbFils++;
	}
	
	public void decrementerNbFils(){
		this.nbFils--;
	}

	public ArrayList<String> getValeurs() {
		return valeurs;
	}

	public void setValeurs(ArrayList<String> valeurs) {
		this.valeurs = valeurs;
	}
	
	public void addVal(String val) {
		this.valeurs.add(val);
	}
	
	public void removeVal(int i) {
		this.valeurs.remove(i);
	}

	public ArrayList<Noeud> getFils() {
		return fils;
	}

	public void setFils(ArrayList<Noeud> fils) {
		this.fils = fils;
	}
	
	public void addFils(Noeud fils) {
		this.fils.add(fils);
	}
	
	public void removeFils(int i) {
		this.fils.remove(i);
	}

	public Noeud getPere() {
		return pere;
	}

	public void setPere(Noeud pere) {
		this.pere = pere;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}

	public int getNbVal() {
		return nbVal;
	}

	public void setNbVal(int nbVal) {
		this.nbVal = nbVal;
	}

	public int getNbFils() {
		return nbFils;
	}

	public void setNbFils(int nbFils) {
		this.nbFils = nbFils;
	}

	public boolean isFull() {
		if(nbVal == 2*ordre && nbFils == 2*ordre+1) {
			return true;
		}
		return false;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("|");
		for(int i =0; i<valeurs.size();i++) {
			sb.append(valeurs.get(i)+"|");
		}
		sb.append("\t");
		return sb.toString();
	}
}
