package shema;

import java.util.ArrayList;

public class HeaderPageInfo {
	private int NbPagesDeDonn�es;
	private ArrayList<Integer> Idx_page_donn�es;
	private ArrayList<Integer> NbSlotsRestantDisponiblesSurLaPage;
	
	public HeaderPageInfo(int NbPagesDeDonn�es) {
		this.NbPagesDeDonn�es = NbPagesDeDonn�es;
		Idx_page_donn�es = new ArrayList<Integer>(NbPagesDeDonn�es);
		NbSlotsRestantDisponiblesSurLaPage = new ArrayList<Integer>(NbPagesDeDonn�es);
	}
	
	public HeaderPageInfo() {
		this(0);
	}
	
	

	public int getNbPagesDeDonn�es() {
		return NbPagesDeDonn�es;
	}

	public void setNbPagesDeDonn�es(int NbPagesDeDonn�es) {
		this.NbPagesDeDonn�es = NbPagesDeDonn�es;
	}
	public void incrementNbPage() {
		NbPagesDeDonn�es++;
	}
	
	
	
	
	
	public ArrayList<Integer> getIdx_page_donn�es() {
		return Idx_page_donn�es;
	}

	public void setIdx_page_donn�es(ArrayList<Integer> Idx_page_donn�es) {
		this.Idx_page_donn�es = Idx_page_donn�es;
	}
	
	public void adddx_page_donn�es(Integer i) {
		Idx_page_donn�es.add(i);
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
		
		int indice = Idx_page_donn�es.indexOf(i);
		
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
