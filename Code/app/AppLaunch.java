package app;

import java.io.IOException;
import java.util.Scanner;

import ManagerFolder.DBManager;
import constants.Constants;


public class AppLaunch {

	public static void main(String[] args) throws IOException, ClassNotFoundException {

		
		System.out.println("Feel welcome to mini_SGBD");
		System.out.println(" tappez votre commande ");
		System.out.println("  \t /) /)\r\n" + 
				" \t (+.+)\r\n" + 
				"\t(\")_(\")");

		
		
		
		DBManager.init();
		
		
		
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
				
				DBManager.processCommande(commande);
			}
			
		}while(!commande.equals(Constants.exit));	
		sc.close();
		
	}

}
