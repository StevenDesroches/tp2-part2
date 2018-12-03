package serveur;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.StringTokenizer;

public class ClientThread extends Thread {
    private Socket socketCommunication;
    private PrintWriter out = null; // le flux de sortie de socket
    private BufferedReader in = null;
    private String corps = "";

    ClientThread(Socket socketCommunication) {
        this.socketCommunication = socketCommunication;
        try {
            out = new PrintWriter(socketCommunication.getOutputStream());
            in = new BufferedReader(new InputStreamReader(socketCommunication.getInputStream()));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void getEntete() {
        String s = null;

        try {
            // lecture de l'entête http
            // http est un protocole structuré en lignes
            while ((s = in.readLine()).compareTo("") != 0) {
                System.out.println("reçu: " + s);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* le serveur prépare une réponse en format HTTP et L'envoie au client */
    void envoiReponse() {
        // longueur du corps de la réponse
        int len = corps.length();

        // envoie des entêtes
        out.print("HTTP-1.0 200 OK\r\n");
        out.print("Content-Length: " + len + "\r\n");
        out.print("Content-Type: text/html\r\n\r\n"); // envoie de la ligne vide
        // envoi de la réponse
        out.print(corps);

        out.flush();
    }

    /**
     * cette méthode ferme le flux
     */
    void fermetureFlux() {
        try {
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String cheminFromNavigateur;
        String retour;
        String input = "";
        Boolean checkExistant = true;

        // get first line of the request from the client
        try {
            input = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // we parse the request with a string tokenizer
        StringTokenizer parse = new StringTokenizer(input);
        String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
        // we get file requested
        cheminFromNavigateur = parse.nextToken().toLowerCase();
        retour = cheminFromNavigateur;
        // we support only GET and HEAD methods, we check
        if (!method.equals("GET") && !method.equals("HEAD")) {
            getEntete();

        } else {
            getEntete();

            if (cheminFromNavigateur.contains("accueil")) {
                cheminFromNavigateur = "/siteHTTP/accueil.html";
                ecritureCorps(cheminFromNavigateur);

            } else if (cheminFromNavigateur.contains("formulaire")) {
                cheminFromNavigateur = "/siteHTTP/formulaire.html";
                ecritureCorps(cheminFromNavigateur);

            } else if (cheminFromNavigateur.contains("contact")) {
                cheminFromNavigateur = "/siteHTTP/contact.html";
                ecritureCorps(cheminFromNavigateur);

            } else if (cheminFromNavigateur.contains(".ico") || cheminFromNavigateur.contains(".png")) {
                checkExistant = true;

            } else {
                checkExistant = ecritureFichierSiInexistant(cheminFromNavigateur);

            }

            if (method.equals("GET")) { // GET method so we return content
                envoiReponse();

            }

            if (checkExistant) {
                System.out.println("Fichier : " + retour + " à été retourné");
            } else {
                System.err.println("Fichier : " + retour + " n'existe pas");
            }

        }

        fermetureFlux();
        System.out.println("Fermeture de la connexion.\n");

    }

    private boolean ecritureFichierSiInexistant(String cheminFromNavigateur) {
        Boolean retour = false;
        File checkExist = new File(cheminFromNavigateur);

        if (checkExist.exists()) {
            retour = true;
            ecritureCorps(cheminFromNavigateur);

        } else {
            ecritureCorps("/Erreur/404.html");

        }

        return retour;
    }

    private void ecritureCorps(String cheminCompletFichier) {
        String ligne;
        //Récuperons le fichier dans le package siteHTTP, getResourceAsStream renvois un inputStream
        InputStream is = this.getClass().getResourceAsStream(cheminCompletFichier);

        try {
            //Pour pouvoir utiliser notre bufferedReader, il faut transformer notre InputStream
            //en InputStreamReader pour finalement le convertir en BufferedReader
            BufferedReader fichierLecture = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            while ((ligne = fichierLecture.readLine()) != null)
                corps += ligne;
        } catch (Exception e) {
            System.out.println("Erreur lecture fichier.");
        }
    }
}


