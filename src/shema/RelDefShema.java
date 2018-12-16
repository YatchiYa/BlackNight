package shema;

import java.io.Serializable;
import java.util.ArrayList;


public class RelDefShema implements Serializable{

	private String nom_rel;
	private int nb_col;
	private ArrayList<String> type_col;


	public RelDefShema(String nom_rel, int nb_col) {
		this.nom_rel = nom_rel;
		this.nb_col = nb_col;
		type_col = new ArrayList<String>(nb_col);
	}
	
	public RelDefShema() {
		this(null,0);
	}


	public String getNom_rel() {
		return nom_rel;
	}


	public void setNom_rel(String nom_rel) {
		this.nom_rel = nom_rel;
	}


	public int getNb_col() {
		return nb_col;
	}


	public void setNb_col(int nb_col) {
		this.nb_col = nb_col;
	}


	public ArrayList<String> getType_col() {
		return type_col;
	}


	public void setType_col(ArrayList<String> type_col) {
		this.type_col = type_col;
	}
}