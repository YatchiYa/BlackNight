package shema;

import java.io.Serializable;
import java.util.ArrayList;


public class DBDef implements Serializable{

	private ArrayList<RelDef> l;

	private int count;
	
	public DBDef(int count) {
		this.count = count;
		l = new ArrayList<RelDef>(count);
	}
	
	public DBDef() {
		this(0);
	}

	public ArrayList<RelDef> getL() {
		return l;
	}

	public void setL(ArrayList<RelDef> l) {
		this.l = l;
	}


	public int getCount() {
		return count;
	}

	/**
	 * Modifie le compteur par un entier passé en parametre
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
	/**
	 * ajoute rd à la liste de relation
	 * @param rd
	 */
	public void ajouterRelation(RelDef rd) {
		l.add(rd);
	}
	
	/**
	 * incremente de +1 le compteur de relation
	 */
	public void incrementCount() {
		this.count++;
	}


	public void reset() {
		this.count = 0;
		this.l = new ArrayList<RelDef>(0);
	}
}
