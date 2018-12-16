package shema;

import java.io.Serializable;
import java.util.ArrayList;


public class RelDefShema implements Serializable{

	private String nomDeRelation;
	private int nbDeColonne;
	private ArrayList<String> typeDeColonne;


	public RelDefShema(String nomDeRelation, int nbDeColonne) {
		this.nomDeRelation = nomDeRelation;
		this.nbDeColonne = nbDeColonne;
		typeDeColonne = new ArrayList<String>(nbDeColonne);
	}
	
	public RelDefShema() {
		this(null,0);
	}


	public void setnbDeColonne(int nbDeColonne) {
		this.nbDeColonne = nbDeColonne;
	}


	public ArrayList<String> gettypeDeColonne() {
		return typeDeColonne;
	}
	

	public int getnbDeColonne() {
		return nbDeColonne;
	}

	public void settypeDeColonne(ArrayList<String> typeDeColonne) {
		this.typeDeColonne = typeDeColonne;
	}

	public String getnomDeRelation() {
		return nomDeRelation;
	}


	public void setnomDeRelation(String nomDeRelation) {
		this.nomDeRelation = nomDeRelation;
	}

}