package shema;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ManagerFolder.DBManager;
import constants.Constants;


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
	
	public void init() throws ClassNotFoundException, IOException {
		File fichier =  new File(Constants.catalogRep);
		if(fichier.exists()) {

			try(FileInputStream fis = new FileInputStream(fichier);ObjectInputStream ois =  new ObjectInputStream(fis);){

				DBManager.setDb((DBDef)ois.readObject());
			}
		}
	}
	
	public void finish() {
		File fichier =  new File(Constants.catalogRep);


		try{
				FileOutputStream fos = new FileOutputStream(fichier);
				ObjectOutputStream oos =  new ObjectOutputStream(fos);
				oos.writeObject(DBManager.getDb());
				oos.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
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
