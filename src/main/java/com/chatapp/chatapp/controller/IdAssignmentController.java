package com.chatapp.chatapp.controller;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.chatapp.chatapp.model.Game;
import com.chatapp.chatapp.model.User;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import com.google.gson.Gson;


import java.util.ArrayList;
import java.util.Map;

@RestController
public class IdAssignmentController {

    static ArrayList<User> usernames = new ArrayList<>();
    static ArrayList<User> lobby = new ArrayList<>();
    static ArrayList<String> keys = new ArrayList<>();

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;


    @MessageMapping("/getRoomId")
    @CrossOrigin(origins = "http://localhost:4200")
    public void uniqueRoomId(@Payload String player) throws InterruptedException {
        //if someone is in the lobby then generate a game with the first
        String username = new Gson().fromJson(player, Map.class).get("username").toString();

        //checks if user already applied
        for(User user : usernames){
            if(user.getName().equals(username)){
                if(lobby.contains(user)) return;
            }
        }

        for(User user : lobby){
            //generate room here
            //now we hardcode 100 as the number of memes, but when we implement a database we will insert that number dynamically
            Game game = new Game(user.getName(), username, 100);
            String gameJson = new Gson().toJson(game);
            //send to player who was in the lobby
            String url1 = "/topic/reply/" + user.getName();
            //send to other player
            String url2 = "/topic/reply/" + username;
            messagingTemplate.convertAndSend(url1, gameJson);
            messagingTemplate.convertAndSend(url2, gameJson);
            setUsersToIngame(user.getName(), username);
            lobby.remove(user);
            return;
        }
        //If no one is in the lobby, add the player to lobby
        for(User user : usernames){
            if(user.getName().equals(username)){
                lobby.add(user);
                return;
            }
        }
    }

    @GetMapping
    @RequestMapping("/sendInvitation")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public void sendInvitation(@RequestParam("requestingPlayer") String requestingPlayer, @RequestParam("otherPlayer") String otherPlayer){
        String url = "/topic/reply/" + otherPlayer;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("requestingPlayer", requestingPlayer);
        String json = jsonObject.toString();
        messagingTemplate.convertAndSend(url, json);
    }

    @GetMapping
    @RequestMapping("/acceptInvitation")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public void acceptInvitation(@RequestParam("playerOne") String playerOne, @RequestParam("playerTwo") String playerTwo){
        Game game = new Game(playerOne, playerTwo, 100);
        game.setRoomId("" + System.currentTimeMillis());
        String gameJson = new Gson().toJson(game);
        //send to player who was in the lobby
        String url1 = "/topic/reply/" + playerOne;
        //send to other player
        String url2 = "/topic/reply/" + playerTwo;
        messagingTemplate.convertAndSend(url1, gameJson);
        messagingTemplate.convertAndSend(url2, gameJson);
        setUsersToIngame(playerOne, playerTwo);
    }

    private void setUsersToIngame(String playerOne, String playerTwo){
        for(User player : usernames) {
            if (player.getName().equals(playerOne)){
                player.setInGame(true);
                break;
            }
        }
        for(User player : usernames){
            if(player.getName().equals(playerTwo)){
                player.setInGame(true);
                break;
            }
        }
    }

    @GetMapping
    @RequestMapping("/checkUsername")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public boolean checkUsername(@RequestParam("username") String username) {
        for(User user : usernames){
            if(user.getName().equals(username)){
                return true;
            }
        }
        User user = new User();
        user.setName(username);
        usernames.add(user);
        return false;
    }

}
