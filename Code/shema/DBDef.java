package shema;

import java.io.Serializable;
import java.util.ArrayList;


public class DBDef implements Serializable{

	private ArrayList<RelDef> listRelDef;

	private int count;
	
	public DBDef(int count) {
		this.count = count;
		listRelDef = new ArrayList<RelDef>(count);
	}
	
	public DBDef() {
		this(0);
	}

	public ArrayList<RelDef> getL() {
		return listRelDef;
	}

	public void setL(ArrayList<RelDef> l) {
		this.listRelDef = l;
	}


	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	public void ajouterRelation(RelDef rd) {
		listRelDef.add(rd);
	}
	
	public void incrementCount() {
		this.count++;
	}


	public void reset() {
		this.count = 0;
		this.listRelDef = new ArrayList<RelDef>(0);
	}
}
