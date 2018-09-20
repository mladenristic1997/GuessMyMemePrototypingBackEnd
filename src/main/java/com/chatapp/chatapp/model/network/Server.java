package com.chatapp.chatapp.model.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    /* TODO
    private Player Player1;
    private Player Player2;
    private Game game:
    */

    private ServerSocket serverSocket;
    private int port = 8080;

    private int numberOfPlayers = 0;

    public void run(){
        try{
            serverSocket = new ServerSocket(port);
            while (numberOfPlayers < 2){
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client " + numberOfPlayers + " connected!");
                    numberOfPlayers++;
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, this, numberOfPlayers));
                    //numberOfPlayers serves here to know if the ClientHandler is handling Player1 or Player2
                    clientThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        /**
         * The Socket used by the Client
         */
        Socket clientSocket;

        /**
         * BufferedReader for the messages of the Client
         */
        BufferedReader reader;

        /**
         * Writer for sending messages to the Client
         */
        OutputStreamWriter writer;

        /**
         * The Server
         */
        Server server;

        /**
         * Constructor for the ClientHandler
         *
         * @param clientSocket the socket of the client
         * @param server       the server
         * @param playerID    the id of the player
         * @author Hasan, Nina
         */
        public ClientHandler(Socket clientSocket, Server server, int playerID) {
            try {
                this.server = server;
                this.clientSocket = clientSocket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public void run() {

        }

    }
}

