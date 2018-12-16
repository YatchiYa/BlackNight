package shema;

import java.util.ArrayList;

public class HeaderPageInfo {
	private int NbPagesDeDonnées;
	private ArrayList<Integer> Idx_page_données;
	private ArrayList<Integer> NbSlotsRestantDisponiblesSurLaPage;
	
	public HeaderPageInfo(int NbPagesDeDonnées) {
		this.NbPagesDeDonnées = NbPagesDeDonnées;
		Idx_page_données = new ArrayList<Integer>(NbPagesDeDonnées);
		NbSlotsRestantDisponiblesSurLaPage = new ArrayList<Integer>(NbPagesDeDonnées);
	}
	
	public HeaderPageInfo() {
		this(0);
	}
	
	

	public int getNbPagesDeDonnées() {
		return NbPagesDeDonnées;
	}

	public void setNbPagesDeDonnées(int NbPagesDeDonnées) {
		this.NbPagesDeDonnées = NbPagesDeDonnées;
	}
	public void incrementNbPage() {
		NbPagesDeDonnées++;
	}
	
	
	
	
	
	public ArrayList<Integer> getIdx_page_données() {
		return Idx_page_données;
	}

	public void setIdx_page_données(ArrayList<Integer> Idx_page_données) {
		this.Idx_page_données = Idx_page_données;
	}
	
	public void adddx_page_données(Integer i) {
		Idx_page_données.add(i);
	}

	
	
	public ArrayList<Integer> getNbSlotsRestantDisponiblesSurLaPage() {
		return NbSlotsRestantDisponiblesSurLaPage;
	}

	public void setNbSlotsRestantDisponiblesSurLaPage(ArrayList<Integer> NbSlotsRestantDisponiblesSurLaPage) {
		this.NbSlotsRestantDisponiblesSurLaPage = NbSlotsRestantDisponiblesSurLaPage;
	}
	
	public void addNbSlotDispo(Integer i) {
		NbSlotsRestantDisponiblesSurLaPage.add(i);
	}
	
	public boolean decrementNbSlotsRestantDisponiblesSurLaPage(Integer i) {
		boolean find = false;
		
		int indice = Idx_page_données.indexOf(i);
		
		if(indice!=-1) {
			int nb = NbSlotsRestantDisponiblesSurLaPage.get(indice).intValue();
			nb--;
			Integer newNb = new Integer(nb);
			NbSlotsRestantDisponiblesSurLaPage.set(indice, newNb);
			find = true;
		}
		
		return find;
	}
}
