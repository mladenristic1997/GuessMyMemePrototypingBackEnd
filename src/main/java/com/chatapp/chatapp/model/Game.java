package com.chatapp.chatapp.model;

//imprt Player class
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Game implements Serializable{

    private String id;
    private Player p1;
    private Player p2;
    private ArrayList<Player> players = new ArrayList<>();
    private HashMap<String, String> chatHistory = new HashMap<>();

    public Game (String p1, String p2){
        Player player1 = new Player(p1);
        player1.setPlayerState(0);
        Player player2 = new Player(p2);
        player2.setPlayerState(3);
        this.p1 = player1;
        this.p1.setOpponentName(p2);
        this.p2 = player2;
        this.p2.setOpponentName(p1);
        this.id = "" + System.currentTimeMillis();
        this.p1.setId(this.id);
        this.p2.setId(this.id);
        players.add(player1);
        players.add(player2);
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public String getId() {
        return id;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public HashMap<String, String> getChatHistory() {
        return chatHistory;
    }

    public void setChatHistory(HashMap<String, String> chatHistory) {
        this.chatHistory = chatHistory;
    }
}
