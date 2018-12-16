package shema;

import java.util.ArrayList;

public class HeaderPageInfo {
	private int nbPagesDeDonnees;
	private ArrayList<Integer> idxPageTab;
	private ArrayList<Integer> nbSlotsRestantDisponibles;
	
	public HeaderPageInfo(int nbPagesDeDonnees) {
		this.nbPagesDeDonnees = nbPagesDeDonnees;
		idxPageTab = new ArrayList<Integer>(nbPagesDeDonnees);
		nbSlotsRestantDisponibles = new ArrayList<Integer>(nbPagesDeDonnees);
	}
	
	public HeaderPageInfo() {
		this(0);
	}
	
	public void incrementNbPage() {
		nbPagesDeDonnees++;
	}

	public int getNbPagesDeDonnees() {
		return nbPagesDeDonnees;
	}

	public void setNbPagesDeDonnees(int nbPagesDeDonnees) {
		this.nbPagesDeDonnees = nbPagesDeDonnees;
	}

	public ArrayList<Integer> getIdxPageTab() {
		return idxPageTab;
	}

	public void setIdxPageTab(ArrayList<Integer> idxPageTab) {
		this.idxPageTab = idxPageTab;
	}
	
	public void addIdxPage(Integer i) {
		idxPageTab.add(i);
	}

	public ArrayList<Integer> getNbSlotsRestantDisponibles() {
		return nbSlotsRestantDisponibles;
	}

	public void setNbSlotsRestantDisponibles(ArrayList<Integer> nbSlotsRestantDisponibles) {
		this.nbSlotsRestantDisponibles = nbSlotsRestantDisponibles;
	}
	
	public void addNbSlotDispo(Integer i) {
		nbSlotsRestantDisponibles.add(i);
	}
	
	public boolean decrementNbSlotDispo(Integer id) {
		boolean find = false;
		
		int indice = idxPageTab.indexOf(id);
		
		if(indice!=-1) {
			int nb = nbSlotsRestantDisponibles.get(indice).intValue();
			nb--;
			Integer newNb = new Integer(nb);
			nbSlotsRestantDisponibles.set(indice, newNb);
			find = true;
		}
		
		return find;
	}
}
