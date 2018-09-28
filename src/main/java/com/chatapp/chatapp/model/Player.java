package com.chatapp.chatapp.model;

import java.util.ArrayList;
import java.util.Random;

public class Player {
    
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
    private int playerMeme;
    private int playerState;
    private ArrayList<Integer> flippedMemes = new ArrayList<>();
    private ArrayList<Integer> opponentFlippedMemes = new ArrayList<>();

    //generate getters and setters
    //in constructor make playerMeme random from 0 to 23

    public Player(String name){
        this.playerName = name;
        this.playerMeme = Player.rand.nextInt(24);
    }

}