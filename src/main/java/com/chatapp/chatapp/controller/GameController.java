package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.Game;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.Map;


@RestController
public class GameController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/makeAMove")
    @CrossOrigin(origins = "http://localhost:4200")
    public void makeAMove(@Payload String move) throws InterruptedException {
        //move can be either a question, yes/no answer or empty string meaning end turn (received answer state)
        String player = new Gson().fromJson(move, Map.class).get("username").toString();
        String roomId = new Gson().fromJson(move, Map.class).get("roomId").toString();
        String sendMove = new Gson().fromJson(move, Map.class).get("move").toString();

        String url = "/topic/reply/" + roomId + "/" + player;
        messagingTemplate.convertAndSend(url, sendMove);
    }



    //implement cleanup method that sets user inGame states to false

}
