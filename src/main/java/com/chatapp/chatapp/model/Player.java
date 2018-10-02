package com.chatapp.chatapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Player implements Serializable {
    
    /*
    *   Game states:
    *   0 - asking question
    *   1 - receiving answer
    *   2 - received answer
    *   3 - await question
    *   4 - got question
    *   5 - answered question
    */
    private static Random rand = new Random();
    private String playerName;
    private String id;
    private String opponentName;
    private int playerMeme;
    private int playerState;
    private ArrayList<Integer> flippedMemes = new ArrayList<>();
    private ArrayList<Integer> opponentFlippedMemes = new ArrayList<>();

    public Player(){

    }

    public Player(String name){
        this.playerName = name;
        this.playerMeme = Player.rand.nextInt(24);
    }

    public static Random getRand() {
        return rand;
    }

    public static void setRand(Random rand) {
        Player.rand = rand;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPlayerMeme() {
        return playerMeme;
    }

    public void setPlayerMeme(int playerMeme) {
        this.playerMeme = playerMeme;
    }

    public int getPlayerState() {
        return playerState;
    }

    public void setPlayerState(int playerState) {
        this.playerState = playerState;
    }

    public ArrayList<Integer> getFlippedMemes() {
        return flippedMemes;
    }

    public void setFlippedMemes(ArrayList<Integer> flippedMemes) {
        this.flippedMemes = flippedMemes;
    }

    public ArrayList<Integer> getOpponentFlippedMemes() {
        return opponentFlippedMemes;
    }

    public void setOpponentFlippedMemes(ArrayList<Integer> opponentFlippedMemes) {
        this.opponentFlippedMemes = opponentFlippedMemes;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}