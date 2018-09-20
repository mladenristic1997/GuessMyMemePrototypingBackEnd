package com.chatapp.chatapp.model.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Client {

    /* TODO
    private Player player;
    private Game game;
    */

    //region Attributes and Constructor

    /**
     * The ServerSocket
     */
    private Socket socket;

    /**
     * The writer used for sending JSONObject to the Server
     */
    private OutputStreamWriter writer;

    /**
     * Thread used to listen to the server
     */
    private Thread listenToServer;

    private String host;
    private int port;

    /**
     * Constructor
     *
     * @param host
     * @param port
     * @throws IOException
     */
    public Client(String host, int port) throws IOException {
        this.host = host;
        this.port = port;

        try {
            socket = new Socket(host, port);
            System.out.println("You succesfully connected to the server!");
        } catch (IOException e) {
            e.printStackTrace();
        }

        listenToServer = new Thread(new ListenToServer(socket, this));
    }

    public void run(){
        try{
            writer = new OutputStreamWriter(socket.getOutputStream(), "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region Client Commands

    public void sendQuestion(String msg){

    }

    public void Answer(String msg){

    }

    public void guess(){

    }

    public void flipMeme(){

    }

    //endregion

    private class ListenToServer implements Runnable{

        /**
         * The Socket of the Server
         */
        private Socket socket;

        /**
         * Reader connected to the server
         */
        private BufferedReader reader;

        /**
         * This client
         */
        private Client client;

        /**
         * Constructor
         *
         * @param socket the socket
         * @param client the client
         */
        public ListenToServer(Socket socket, Client client) {
            this.socket = socket;
            this.client = client;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (true) {
                try {
                    String line = reader.readLine();

                    // Do Stuff

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}