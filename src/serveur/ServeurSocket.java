package serveur;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.StringTokenizer;

public class ServeurSocket {

        // port to listen connection
        static final int PORT = 8085;

        // verbose mode

        public static void main(String[] args) {
            Socket socketCommunication = null;
            ServerSocket socketServeur = null;

            try {
                socketServeur = new ServerSocket(PORT);
                System.out.println("Serveur lancé.");
                System.out.println("En attente de connexion au port : " + PORT + " ...");
                System.out.println();

                // we listen until user halts server execution
                while (true) {
                    socketCommunication = socketServeur.accept();

                    // create dedicated thread to manage the client connection
                    ClientThread client = new ClientThread(socketCommunication);
                    client.start();
                }

            } catch (IOException e) {
                System.err.println("Erreur de connexsion : " + e.getMessage());
            }
        }
}
