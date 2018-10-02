package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.Game;
import com.chatapp.chatapp.model.Player;
import com.chatapp.chatapp.repository.GameRepository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
        game.getP1().setOpponentFlippedMemes(game.getP2().getFlippedMemes());
        game.getP2().setOpponentFlippedMemes(game.getP1().getFlippedMemes());
        gameRepositoryGameController.save(game);
        Player sendTo = new Player();
        for(Player playerTemp : game.getPlayers()) {
            if (!playerTemp.getPlayerName().equals(player.getPlayerName())) {
                sendTo = playerTemp;
            }
        }
        Gson response = new GsonBuilder().create();
        JSONObject params = new JSONObject();
        params.put("player", sendTo);
        params.put("message", message);
        String url = "/topic/reply/" + sendTo.getId() + "/" + sendTo.getPlayerName();
        simpMessageSendingOperationsGameController.convertAndSend(url, response);
        
    }



    //implement cleanup method that sets user inGame states to false

}
