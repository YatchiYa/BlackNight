package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import ManagerFolder.DBManager;
import constants.Constants;
import gestion.Commande;


public class AppLaunch {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		
		System.out.println("Feel welcome to mini_SGBD");
		System.out.println(" tappez votre commande ");
		System.out.println("  \t /) /)\r\n" + 
				" \t (+.+)\r\n" + 
				"\t(\")_(\")");

		
		
		
		DBManager dbManager = new DBManager();
		dbManager.init();
		
		
		
		Scanner sc = new Scanner(System.in);
		String commande;
		
		
		do {
			System.out.println("cammande : ");
			
			commande = sc.nextLine();
			// int nbc = sc.nextInt();
			// String typeC = sc.nextLine();
			if(commande.equals(Constants.exit)) {

				System.out.println("sortie");
			}
			else {
				
				dbManager.processCommande(commande);
			}
			
		}while(!commande.equals(Constants.exit));	

		
	}

}
