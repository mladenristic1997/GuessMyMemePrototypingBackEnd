package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.Game;
import com.chatapp.chatapp.model.Player;
import com.chatapp.chatapp.model.User;
import com.chatapp.chatapp.repository.GameRepository;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import com.google.gson.Gson;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;

@RestController
public class IdAssignmentController {

    static ArrayList<User> usernames = new ArrayList<>();
    static ArrayList<User> lobby = new ArrayList<>();
    static ArrayList<Player> inGame = new ArrayList<>();

    @Resource
    SimpMessageSendingOperations simpMessageSendingOperationsIdAssignmentController;

    @Resource
    GameRepository gameRepositoryIdAssignmentController;

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
            Game game = new Game(user.getName(), username);
            //send to player who was in the lobby
            String url1 = "/topic/reply/" + user.getName();
            //send to other player
            String url2 = "/topic/reply/" + username;
            this.inGame.add(game.getP1());
            this.inGame.add(game.getP2());
            lobby.remove(user);
            Gson gson = new Gson();
            String jsonP1 = gson.toJson(game.getP1());
            String jsonP2 = gson.toJson(game.getP2());
            gameRepositoryIdAssignmentController.save(game);
            simpMessageSendingOperationsIdAssignmentController.convertAndSend(url1, jsonP1);
            simpMessageSendingOperationsIdAssignmentController.convertAndSend(url2, jsonP2);
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
    @MessageMapping("/sendInvitation")
    @CrossOrigin(origins = "http://localhost:4200")
    public void sendInvitation(@Payload String data) throws InterruptedException{
        String requestingPlayer = new Gson().fromJson(data, Map.class).get("requestingPlayer").toString();
        String otherPlayer = new Gson().fromJson(data, Map.class).get("otherPlayer").toString();
        String url = "/topic/reply/" + otherPlayer;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("requestingPlayer", requestingPlayer);
        String json = jsonObject.toString();
        simpMessageSendingOperationsIdAssignmentController.convertAndSend(url, json);
    }

    @MessageMapping("/removePlayer")
    @CrossOrigin(origins = "http://localhost:4200")
    public void removeFromLobby(@Payload String data) throws InterruptedException{
        String player = new Gson().fromJson(data, Map.class).get("player").toString();
        for(User user : lobby){
            if(user.getName().equals(player)){
                lobby.remove(user);
                break;
            }
        }
        for(User user : usernames){
            if(user.getName().equals(player)){
                usernames.remove(user);
                break;
            }
        }
    }

    @MessageMapping("/acceptInvitation")
    @CrossOrigin(origins = "http://localhost:4200")
    public void acceptInvitation(@Payload String data){
        String p1 = new Gson().fromJson(data, Map.class).get("playerOne").toString();
        String p2 = new Gson().fromJson(data, Map.class).get("playerTwo").toString();
        Game game = new Game(p1, p2);
        gameRepositoryIdAssignmentController.save(game);
        //send to player who was in the lobby
        String url1 = "/topic/reply/" + p1;
        //send to other player
        String url2 = "/topic/reply/" + p2;
        this.inGame.add(game.getP1());
        this.inGame.add(game.getP2());
        Gson gson = new Gson();
        String jsonP1 = gson.toJson(game.getP1());
        String jsonP2 = gson.toJson(game.getP2());
        simpMessageSendingOperationsIdAssignmentController.convertAndSend(url1, jsonP1);
        simpMessageSendingOperationsIdAssignmentController.convertAndSend(url2, jsonP2);
    }

    @MessageMapping("/refuseInvitation")
    @CrossOrigin(origins = "http://localhost:4200")
    public void refuseInvitation(@Payload String data){
        String p1 = new Gson().fromJson(data, Map.class).get("otherPlayer").toString();
        //send to player who was in the lobby
        String url1 = "/topic/reply/" + p1;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("refuseInvitation", true);
        simpMessageSendingOperationsIdAssignmentController.convertAndSend(url1, jsonObject.toString());
    }

    @MessageMapping("/isOtherPlayerAvailable")
    @CrossOrigin(origins = "http://localhost:4200")
    public void isOtherPlayerAvailable(@Payload String data){
        String requestingPlayer = new Gson().fromJson(data, Map.class).get("requestingPlayer").toString();
        String otherPlayer = new Gson().fromJson(data, Map.class).get("otherPlayer").toString();
        String url = "/topic/reply/" + requestingPlayer;
        boolean isAvailable = true;
        boolean isPlayerExists = false;
        for(User user : usernames){
            if(user.getName().equals(otherPlayer)){
                isPlayerExists = true;
                break;
            }
        }
        if(!isPlayerExists){
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("playerExists", false);
            simpMessageSendingOperationsIdAssignmentController.convertAndSend(url, jsonObject.toString());
            return;
        }
        for(User user : lobby){
            if(user.getName().equals(otherPlayer)){
                isAvailable = false;
                break;
            }
        }
        for(Player player : inGame){
            if(player.getPlayerName().equals(otherPlayer)){
                isAvailable = false;
                break;
            }
        }
        JsonObject jsonObject = new JsonObject();
        if(isAvailable){
            jsonObject.addProperty("playerAvailable", true);
            simpMessageSendingOperationsIdAssignmentController.convertAndSend(url, jsonObject.toString());
        }
        else{
            jsonObject.addProperty("playerAvailable", false);
            simpMessageSendingOperationsIdAssignmentController.convertAndSend(url, jsonObject.toString());
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
