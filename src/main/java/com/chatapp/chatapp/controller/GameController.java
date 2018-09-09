package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.Game;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;


@RestController
public class GameController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @GetMapping
    @RequestMapping("/makeAMove")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public void makeAMove(@RequestParam("opponent") String opponent, @RequestParam("move") String move) throws InterruptedException {
        //move can be either a question, yes/no answer or empty string meaning end turn (received answer state)
        String url = "/topic/reply/" + opponent;
        messagingTemplate.convertAndSend(url, move);
    }



    //implement cleanup method that sets user inGame states to false

}
