package com.chatapp.chatapp.model;

//imprt Player class
import java.io.Serializable;

public class Game implements Serializable{

    private String id;
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<String, Stringd> chatHistory = new HashMap<>();

    public Game (String p1, String p2){
        Player player1 = new Player(p1);
        player1.setState = 0;
        Player player2 = new Player(p2);
        player2.setState = 3;
        this.id = "" + System.currentTimeMillis();
        players.add(player1);
        players.add(player2);
    }


}
