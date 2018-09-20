package com.chatapp.chatapp.model.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    //region Attributes and Constructor

    /* TODO
    private Player Player1;
    private Player Player2;
    private Game game:
    */

    private OutputStreamWriter writer1;
    private OutputStreamWriter writer2;

    private ServerSocket serverSocket;
    private int port = 8080;

    private int numberOfPlayers = 0;

    //endregion

    public void run(){
        try{
            serverSocket = new ServerSocket(port);
            while (numberOfPlayers < 2){
                try {
                    Socket clientSocket = serverSocket.accept();
                    numberOfPlayers++;
                    Thread clientThread = new Thread(new ClientHandler(clientSocket, this, numberOfPlayers));
                    //numberOfPlayers serves here as clientID (Can be 1 or 2)
                    clientThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            serverSocket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    //region Communication with Clients

    public void sendToClient(int clientID, JsonObject jsonObject){
        try {
            if(clientID==1){
                writer1.write(jsonObject.toString() + "\n");
                writer1.flush();
            } else if (clientID==2){
                writer2.write(jsonObject.toString() + "\n");
                writer2.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendToAllClients(JsonObject jsonObject){
        try {
            writer1.write(jsonObject.toString() + "\n");
            writer1.flush();
            writer2.write(jsonObject.toString() + "\n");
            writer2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    private class ClientHandler implements Runnable {

        //region Attributes and Constructor

        /**
         * The Socket used by the Client
         */
        Socket clientSocket;

        /**
         * BufferedReader for the messages of the Client
         */
        BufferedReader reader;

        /**
         * The Server
         */
        Server server;

        private int clientID;

        /**
         * Constructor for the ClientHandler
         *
         * @param clientSocket the socket of the client
         * @param server       the server
         * @param clientID    the id of the client
         * @author Hasan, Nina
         */
        public ClientHandler(Socket clientSocket, Server server, int clientID) {
            try {
                this.server = server;
                this.clientID = clientID;
                this.clientSocket = clientSocket;
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                if(clientID == 1) {
                    writer1 = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
                } else {
                    writer2 = new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        //endregion

        @Override
        public void run() {
                try {
                    System.out.println("Client " + numberOfPlayers + " connected!");
                    while (true) {
                        String message = "";
                        message = reader.readLine();
                        System.out.println(message);
                        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
                        if (jsonObject.has("Question")){
                            //TODO
                        } else if (jsonObject.has("Answer")){
                            //TODO
                        } else {
                            sendToClient(clientID, jsonObject); //Just testing
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }

    }
}

