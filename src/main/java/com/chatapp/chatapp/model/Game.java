package com.chatapp.chatapp.model;

import java.util.Random;

public class Game {

    /*
    *   Game states:
    *   0 - asking question
    *   1 - receiving answer
    *   2 - received answer
    *   3 - await question
    *   4 - got question
    *   5 - answered question
    */

    String playerOneName;
    String playerTwoName;
    int playerOneMeme;
    int playerTwoMeme;
    int playerOneState;
    int playerTwoState;
    String roomId;

    public String getMove() {
        return move;
    }

    public void setMove(String move) {
        this.move = move;
    }

    String move;

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public int getPlayerOneMeme() {
        return playerOneMeme;
    }

    public void setPlayerOneMeme(int playerOneMeme) {
        this.playerOneMeme = playerOneMeme;
    }

    public int getPlayerTwoMeme() {
        return playerTwoMeme;
    }

    public void setPlayerTwoMeme(int playerTwoMeme) {
        this.playerTwoMeme = playerTwoMeme;
    }

    public int getPlayerOneState() {
        return playerOneState;
    }

    public void setPlayerOneState(int playerOneState) {
        this.playerOneState = playerOneState;
    }

    public int getPlayerTwoState() {
        return playerTwoState;
    }

    public void setPlayerTwoState(int playerTwoState) {
        this.playerTwoState = playerTwoState;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Game(){

    }

    public Game (String playerOne, String playerTwo, int numberOfMemes){
        Random rand = new Random();
        this.playerOneName = playerOne;
        this.playerTwoName = playerTwo;
        this.roomId = "" + System.currentTimeMillis();
        this.playerOneMeme = rand.nextInt(numberOfMemes);
        this.playerTwoMeme = rand.nextInt(numberOfMemes);
        this.playerOneState = 0;
        this.playerTwoState = 3;
    }





}
