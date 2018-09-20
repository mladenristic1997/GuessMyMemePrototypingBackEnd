package com.chatapp.chatapp.model.network;

//This Class is just for testing, it will be deleted as soon as we implement the Network to the game

import java.io.IOException;

public class ClientDemo {

    public static void main(String[] args) throws IOException {
        Client client = new Client("localhost", 8080);
        client.run();
    }
}