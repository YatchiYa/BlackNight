package shema;

import java.util.ArrayList;


public class DBDef{

	private ArrayList<RelDef> listRelDef;
	private int cpt;
	
	
	public DBDef(int cpt) {
		this.cpt = cpt;
		listRelDef = new ArrayList<RelDef>(cpt);
	}
	
	public DBDef() {
		this(0);
	}
	
	public void AddRelation(RelDef rd) {
		this.listRelDef.add(rd);
	}
	
	
	
	

	public ArrayList<RelDef> getlistRelDef() {
		return this.listRelDef;
	}

	public void setlistRelDef(ArrayList<RelDef> l) {
		this.listRelDef = l;
	}


	public int getcpt() {
		return cpt;
	}

	public void setcpt(int cpt) {
		this.cpt = cpt;
	}
	
	public void incrCpt() {
		this.cpt++;
	}
	public void decrcpt() {
		this.cpt--;
	}

	public void raz() {
		this.cpt = 0;
		this.listRelDef = new ArrayList<RelDef>(0);
	}
}
