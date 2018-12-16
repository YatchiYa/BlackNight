package index;


public class Arbre {
	private Noeud racine;
	private int ordre;
	
	public Arbre(int ordre) {
		this.ordre = ordre;
		this.racine = null;
	}

	public Noeud getRacine() {
		return racine;
	}

	public void setRacine(Noeud racine) {
		this.racine = racine;
	}

	public int getOrdre() {
		return ordre;
	}

	public void setOrdre(int ordre) {
		this.ordre = ordre;
	}
}
