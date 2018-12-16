package index;

public class Index {
	private Arbre arbre;
	private String nomRel;
	private int numCol;
	
	public Index(String nomRel, int numCol, Arbre arbre) {
		this.nomRel = nomRel;
		this.numCol = numCol;
		this.arbre = arbre;
	}

	public Arbre getArbre() {
		return arbre;
	}

	public void setArbre(Arbre arbre) {
		this.arbre = arbre;
	}

	public String getNomRel() {
		return nomRel;
	}

	public void setNomRel(String nomRel) {
		this.nomRel = nomRel;
	}

	public int getNumCol() {
		return numCol;
	}

	public void setNumCol(int numCol) {
		this.numCol = numCol;
	}
}
