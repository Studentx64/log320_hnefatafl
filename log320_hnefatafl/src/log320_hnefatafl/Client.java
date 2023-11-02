package log320_hnefatafl;
import java.io.*;
import java.net.*;


class Client {
	
    static Equipe equipe = Equipe.UNDEFINED;
	
	public static void main(String[] args) {
         
	Socket MyClient;
	BufferedInputStream input;
	BufferedOutputStream output;
    final Board board = new Board();
    final IA IA = new IA();
	
	try {
		MyClient = new Socket("localhost", 8888);

	   	input    = new BufferedInputStream(MyClient.getInputStream());
		output   = new BufferedOutputStream(MyClient.getOutputStream());
		BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
	   	while(1 == 1){
			char cmd = 0;
		   	
            cmd = (char)input.read();
            System.out.println(cmd);
            // Debut de la partie en joueur blanc
            if(cmd == '1'){
            	equipe = Equipe.ROUGE;
            	
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size :" + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                //System.out.println(s);
                board.fullUpdate(s);
                board.draw();
                System.out.println("Nouvelle partie! Vous jouer blanc, entrez votre premier coup : ");
                boolean moveSent = false;
    			boolean joueurIA = true;
    			if(!joueurIA) {
    				//Boucle tant que le coup choisi n'est pas valide (ne pas envoyer de coup invalide car ça ferait perdre la partie)
    				while (!moveSent) {
    					System.out.println("Entrez votre coup : ");
    					String move = null;
    					move = console.readLine();				
    					if(board.update(move, equipe)) {
    						System.out.println("Le coup choisi est valide.");
    						output.write(move.getBytes(),0,move.length());
    						output.flush();
    						//TODO : vérifier ici en fonction des règles si une pièce doit être retirée
    						
    						board.draw();
    						moveSent = true;
    					}
    					else {
    						System.out.println("Le coup choisi est invalide.");
    					}
    					//System.out.println("move " + move);
    				}
    			}
    			else {
    				String move = IA.jouer(board, equipe);
    				if(board.update(move, equipe)) {
    					System.out.println("Le coup choisi est valide.");
    					output.write(move.getBytes(),0,move.length());
    					output.flush();
    					//TODO : vérifier ici en fonction des règles si une pièce doit être retirée
    					board.draw();
    				}
    			}
            }
            // Debut de la partie en joueur Noir
            if(cmd == '2'){
            	equipe = Equipe.NOIR;
                System.out.println("Nouvelle partie! Vous jouer noir, attendez le coup des blancs");
                byte[] aBuffer = new byte[1024];
				
				int size = input.available();
				//System.out.println("size " + size);
				input.read(aBuffer,0,size);
                String s = new String(aBuffer).trim();
                //System.out.println(s);
                board.fullUpdate(s);
                board.draw();
            }


			// Le serveur demande le prochain coup
			// Le message contient aussi le dernier coup joue.
	    if(cmd == '3'){
			byte[] aBuffer = new byte[16];
					
			int size = input.available();
			//System.out.println("size :" + size);
			input.read(aBuffer,0,size);
					
			String s = new String(aBuffer);
			System.out.println("Dernier coup :"+ s);
			if(board.update(s, equipe.opposite(), true)) {
				System.out.println("Le dernier coup est valide.");
				//TODO : vérifier ici en fonction des règles si une pièce doit être retirée
				board.draw();
			}
			else {
				System.out.println("Le dernier coup est invalide.");
			}
			boolean moveSent = false;
			boolean joueurIA = true;
			if(!joueurIA) {
				//Boucle tant que le coup choisi n'est pas valide (ne pas envoyer de coup invalide car ça ferait perdre la partie)
				while (!moveSent) {
					System.out.println("Entrez votre coup : ");
					String move = null;
					move = console.readLine();				
					if(board.update(move, equipe,true)) {
						System.out.println("Le coup choisi est valide.");
						output.write(move.getBytes(),0,move.length());
						output.flush();
						//TODO : vérifier ici en fonction des règles si une pièce doit être retirée
						
						board.draw();
						moveSent = true;
					}
					else {
						System.out.println("Le coup choisi est invalide.");
					}
					//System.out.println("move " + move);
				}
			}
			else {
				String move = IA.jouer(board, equipe);
				if(board.update(move, equipe, true)) {
					System.out.println("Le coup choisi est valide.");
					output.write(move.getBytes(),0,move.length());
					output.flush();
					//TODO : vérifier ici en fonction des règles si une pièce doit être retirée
					board.draw();
				}
			}
	    }
			
			// Le dernier coup est invalide
			if(cmd == '4'){
				System.out.println("Coup invalide, entrez un nouveau coup : ");
		       		String move = null;
				move = console.readLine();
				output.write(move.getBytes(),0,move.length());
				output.flush();
				
			}
            // La partie est terminée
	    if(cmd == '5'){
                byte[] aBuffer = new byte[16];
                int size = input.available();
                input.read(aBuffer,0,size);
		String s = new String(aBuffer);
		System.out.println("Partie Terminé. Le dernier coup joué est: "+s);
		String move = null;
		move = console.readLine();
		output.write(move.getBytes(),0,move.length());
		output.flush();
				
	    }
        }
	}
	catch (IOException e) {
   		System.out.println(e);
	}
	
    }
}