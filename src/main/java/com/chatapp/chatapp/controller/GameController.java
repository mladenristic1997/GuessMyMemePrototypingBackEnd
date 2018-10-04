package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.Game;
import com.chatapp.chatapp.model.Player;
import com.chatapp.chatapp.model.User;
import com.chatapp.chatapp.repository.GameRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.jboss.logging.Message;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;


@Controller
@ComponentScan("repository")
public class GameController {

    @Resource
    private GameRepository gameRepositoryGameController;

    @Resource
    SimpMessageSendingOperations simpMessageSendingOperationsGameController;

    @MessageMapping("/makeAMove")
    @CrossOrigin(origins = "http://localhost:4200")
    public void makeAMove(@Payload String move) throws InterruptedException, JSONException {
        String playerData = new Gson().fromJson(move, Map.class).get("player").toString();
        String message = new Gson().fromJson(move, Map.class).get("message").toString();
        Gson gson = new Gson();
        Player player = gson.fromJson(playerData, Player.class);
        Game game = gameRepositoryGameController.findById(player.getId());
        game.getP1().setPlayerState((game.getP1().getPlayerState() + 1) % 6);
        game.getP2().setPlayerState((game.getP2().getPlayerState() + 1) % 6);
        gameRepositoryGameController.save(game);
        Player sendTo = new Player();
        for(Player playerTemp : game.getPlayers()) {
            if (!playerTemp.getPlayerName().equals(player.getPlayerName())) {
                sendTo = playerTemp;
                break;
            }
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("message", message);
        jo.addProperty("player", gson.toJson(sendTo));
        String jsonFormattedString = jo.toString().replace("\\\"", "\"");
        String tempA = jsonFormattedString.replace("player\":\"", "player\":");
        String tempB = tempA.replace("}\"}", "}}");
        String url = "/topic/reply/" + sendTo.getId() + "/" + sendTo.getPlayerName();
        simpMessageSendingOperationsGameController.convertAndSend(url, tempB);
    }

    @MessageMapping("/endTurn")
    @CrossOrigin(origins = "http://localhost:4200")
    public void endTurn(@Payload String endTurn) {
        String playerData = new Gson().fromJson(endTurn, Map.class).get("player").toString();
        Player player = new Gson().fromJson(playerData, Player.class);
        Game game = gameRepositoryGameController.findById(player.getId());
        game.getP1().setPlayerState((game.getP1().getPlayerState() + 1) % 6);
        game.getP2().setPlayerState((game.getP2().getPlayerState() + 1) % 6);
        for(Player temp : game.getPlayers()) { //set flipped memes to be like we get them in Payload
            if(temp.getPlayerName().equals(player.getPlayerName())) {
                temp.setFlippedMemes(player.getFlippedMemes());
                break;
            }
        }
        game.getP1().setOpponentFlippedMemes(game.getP2().getFlippedMemes());
        game.getP2().setOpponentFlippedMemes(game.getP1().getFlippedMemes());
        //check if player flipped the winning meme
        Player winningPlayer = new Player();
        Player losingPlayer = new Player();
        ArrayList<Player> playerList = new ArrayList<>(game.getPlayers());
        ArrayList<Player> playerListAux = new ArrayList<>(game.getPlayers());
        for(Player temp : playerList){
            for(Integer i : temp.getOpponentFlippedMemes()){
                if(i.intValue() == temp.getPlayerMeme()){
                    winningPlayer = temp;
                    playerListAux.remove(temp);
                    losingPlayer = playerListAux.remove(0);
                    break;
                }
            }
        }
        //if player flipped the winning meme
        if(playerListAux.size() == 0){
            String wonUrl = "/topic/reply/" + winningPlayer.getId() + "/" + winningPlayer.getPlayerName();
            String lostUrl = "/topic/reply/" + losingPlayer.getId() + "/" + losingPlayer.getPlayerName();
            JsonObject won = new JsonObject();
            won.addProperty("endGameStatus", "You won!");
            won.addProperty("endGameMessage", "Your opponent flipped the winning meme!");
            JsonObject lost = new JsonObject();
            lost.addProperty("endGameStatus", "You lost!");
            lost.addProperty("endGameMessage", "You flipped the winning meme!");
            JsonObject sendWon = new JsonObject();
            sendWon.add("gameOver", won);
            JsonObject sendLost = new JsonObject();
            sendLost.add("gameOver", lost);
            gameRepositoryGameController.delete(game.getId());
            //removePlayersFromUsernames(game.getP1().getPlayerName());
            //removePlayersFromUsernames(game.getP2().getPlayerName());
            sendOutMessage(wonUrl, sendWon.toString());
            sendOutMessage(lostUrl, sendLost.toString());
            return;
        }
        gameRepositoryGameController.save(game);
        Player sendTo = new Player();
        for(Player playerTemp : game.getPlayers()) {
            if (!playerTemp.getPlayerName().equals(player.getPlayerName())) {
                sendTo = playerTemp;
                break;
            }
        }
        JsonObject jo = new JsonObject();
        jo.addProperty("playerEndTurn", new Gson().toJson(sendTo));
        String jsonFormattedString = jo.toString().replace("\\\"", "\"");
        String tempA = jsonFormattedString.replace("playerEndTurn\":\"", "playerEndTurn\":");
        String tempB = tempA.replace("}\"}", "}}");
        String url = "/topic/reply/" + sendTo.getId() + "/" + sendTo.getPlayerName();
        simpMessageSendingOperationsGameController.convertAndSend(url, tempB);
    }

    @MessageMapping("/guessMeme")
    @CrossOrigin(origins = "http://localhost:4200")
    public void guessMeme(@Payload String guess) {
        String playerData = new Gson().fromJson(guess, Map.class).get("player").toString();
        int guessedMemeId = (int)Double.parseDouble(new Gson().fromJson(guess, Map.class).get("guessedMemeId").toString());
        Player player = new Gson().fromJson(playerData, Player.class);
        Game game = gameRepositoryGameController.findById(player.getId());
        game.getP1().setPlayerState((game.getP1().getPlayerState() + 1) % 6);
        game.getP2().setPlayerState((game.getP2().getPlayerState() + 1) % 6);
        Player winningPlayer = new Player();
        Player losingPlayer = new Player();
        boolean guessedCorrectly = false;
        ArrayList<Player> players = new ArrayList<>(game.getPlayers());
        for(Player temp : players) {
            if(!temp.getPlayerName().equals(player.getPlayerName())) {
                //check if correct meme has been guessed and send out win/lose messages
                if(temp.getPlayerMeme() == guessedMemeId){
                    winningPlayer = player;
                    guessedCorrectly = true;
                    losingPlayer = temp;
                } else {
                    losingPlayer = player;
                    winningPlayer = temp;
                }
                break;
            }
        }
        String wonUrl = "/topic/reply/" + winningPlayer.getId() + "/" + winningPlayer.getPlayerName();
        String lostUrl = "/topic/reply/" + losingPlayer.getId() + "/" + losingPlayer.getPlayerName();
        if(guessedCorrectly) {
            JsonObject won = new JsonObject();
            won.addProperty("endGameStatus", "You won!");
            won.addProperty("endGameMessage", "You guessed correctly!");
            JsonObject lost = new JsonObject();
            lost.addProperty("endGameStatus", "You lost!");
            lost.addProperty("endGameMessage", "Your opponent guessed the correct meme!");
            JsonObject sendWon = new JsonObject();
            sendWon.add("gameOver", won);
            JsonObject sendLost = new JsonObject();
            sendLost.add("gameOver", lost);
            sendOutMessage(wonUrl, sendWon.toString());
            sendOutMessage(lostUrl, sendLost.toString());
        }
        else {
            JsonObject won = new JsonObject();
            won.addProperty("endGameStatus", "You won!");
            won.addProperty("endGameMessage", "Your opponent guessed incorrectly!");
            JsonObject lost = new JsonObject();
            lost.addProperty("endGameStatus", "You lost!");
            lost.addProperty("endGameMessage", "You guessed the incorrect meme!");
            JsonObject sendWon = new JsonObject();
            sendWon.add("gameOver", won);
            JsonObject sendLost = new JsonObject();
            sendLost.add("gameOver", lost);
            sendOutMessage(wonUrl, sendWon.toString());
            sendOutMessage(lostUrl, sendLost.toString());
        }
        gameRepositoryGameController.delete(game.getId());
        //removePlayersFromUsernames(game.getP1().getPlayerName());
        //removePlayersFromUsernames(game.getP2().getPlayerName());
    }

    @MessageMapping("/quit")
    @CrossOrigin(origins = "http://localhost:4200")
    public void quit(@Payload String playerJson) {
        String playerData = new Gson().fromJson(playerJson, Map.class).get("player").toString();
        Gson gson = new Gson();
        Player player = gson.fromJson(playerData, Player.class);
        Game game = gameRepositoryGameController.findById(player.getId());
        removePlayersFromInGame(player);
        //removePlayersFromUsernames(player.getPlayerName());
        Player sendTo = new Player();
        for(Player playerTemp : game.getPlayers()) {
            if (!playerTemp.getPlayerName().equals(player.getPlayerName())) {
                sendTo = playerTemp;
                break;
            }
        }
        String wonUrl = "/topic/reply/" + sendTo.getId() + "/" + sendTo.getPlayerName();
        JsonObject won = new JsonObject();
        won.addProperty("endGameStatus", "You won!");
        won.addProperty("endGameMessage", "Your opponent quit the game!");
        JsonObject sendWon = new JsonObject();
        sendWon.add("gameOver", won);
        sendOutMessage(wonUrl, sendWon.toString());
    }

    @MessageMapping("/cleanMe")
    @CrossOrigin(origins = "http://localhost:4200")
    public void cleanMe(@Payload String playerJson) {
        String playerData = new Gson().fromJson(playerJson, Map.class).get("player").toString();
        Gson gson = new Gson();
        Player player = gson.fromJson(playerData, Player.class);
        removePlayersFromLobby(player.getPlayerName());
        removePlayersFromUsernames(player.getPlayerName());
        removePlayersFromInGame(player);
    }

    @MessageMapping("/removeFromInGame")
    @CrossOrigin(origins = "http://localhost:4200")
    public void removeFromInGame(@Payload String playerJson) {
        String playerData = new Gson().fromJson(playerJson, Map.class).get("player").toString();
        Gson gson = new Gson();
        Player player = gson.fromJson(playerData, Player.class);
        removePlayersFromInGame(player);
    }

    @MessageMapping("/playerReloaded")
    @CrossOrigin(origins = "http://localhost:4200")
    public void playerReloaded(@Payload String playerJson) {
        String playerData = new Gson().fromJson(playerJson, Map.class).get("player").toString();
        Gson gson = new Gson();
        Player player = gson.fromJson(playerData, Player.class);
        removePlayersFromUsernames(player.getPlayerName());
        removePlayersFromLobby(player.getPlayerName());
        Game game = gameRepositoryGameController.findById(player.getId());
        Player sendTo = new Player();
        for(Player playerTemp : game.getPlayers()) {
            if (!playerTemp.getPlayerName().equals(player.getPlayerName())) {
                sendTo = playerTemp;
                break;
            }
        }
        gameRepositoryGameController.delete(game.getId());
        removePlayersFromInGame(player);
        String wonUrl = "/topic/reply/" + sendTo.getId() + "/" + sendTo.getPlayerName();
        JsonObject won = new JsonObject();
        won.addProperty("endGameStatus", "You won!");
        won.addProperty("endGameMessage", "Your opponent quit the game!");
        JsonObject sendWon = new JsonObject();
        sendWon.add("gameOver", won);
        sendOutMessage(wonUrl, sendWon.toString());
    }

    private void sendOutMessage(String url, String message){
        simpMessageSendingOperationsGameController.convertAndSend(url, message);
    }

    private void removePlayersFromLobby(String p1){
        ArrayList<User> temp = new ArrayList<>(IdAssignmentController.lobby);
        for(User user : IdAssignmentController.lobby){
            if(user.getName().equals(p1))
                temp.remove(user);
        }
        IdAssignmentController.lobby = temp;
    }

    private void removePlayersFromUsernames(String p1){
        ArrayList<User> temp = new ArrayList<>(IdAssignmentController.usernames);
        for(User user : IdAssignmentController.usernames){
            if(user.getName().equals(p1)) {
                temp.remove(user);
                break;
            }
        }
        IdAssignmentController.usernames = temp;
    }

    private void removePlayersFromInGame(Player player){
        ArrayList<Player> temp = new ArrayList<>(IdAssignmentController.inGame);
        for(Player ptemp : IdAssignmentController.inGame){
            if(player.getPlayerName().equals(ptemp.getPlayerName())) {
                temp.remove(ptemp);
                break;
            }
        }
        IdAssignmentController.inGame = temp;
    }
    //implement cleanup method that sets user inGame states to false

}
