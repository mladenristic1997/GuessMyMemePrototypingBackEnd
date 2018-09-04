package com.chatapp.chatapp.controller;

import com.chatapp.chatapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
public class IdAssignmentController {

    static ArrayList<User> usernames = new ArrayList<>();
    static ArrayList<User> lobby = new ArrayList<>();
    static ArrayList<String> keys = new ArrayList<>();

    @GetMapping
    @RequestMapping("/getRoomId")
    @ResponseStatus(HttpStatus.OK)
    @CrossOrigin(origins = "http://localhost:4200")
    public String uniqueRoomId(@RequestParam("username") String username) throws InterruptedException {
        for(User user : lobby){
            if(user.isHost()){
                lobby.remove(user);
                return user.getRoomId();
            }
        }
        for(User user : usernames){
            if(user.getName().equals(username)){
                user.setRoomId("" + System.currentTimeMillis());
                user.setHost(true);
                lobby.add(user);
                return user.getRoomId();
            }
        }
        return "";
    }

    private String generateRoom() throws InterruptedException {
        while(lobby.size() < 2) { Thread.sleep(100); }
        if(keys.size() == 0){
            int numOfPairs = lobby.size() % 2;
            for(int i = 0; i < numOfPairs; i++){
                String roomId = "" + System.currentTimeMillis();
                keys.add(roomId);
                keys.add(roomId);
                Thread.sleep(10);
            }
            return keys.remove(0);
        }
        else{
            return keys.remove(0);
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
