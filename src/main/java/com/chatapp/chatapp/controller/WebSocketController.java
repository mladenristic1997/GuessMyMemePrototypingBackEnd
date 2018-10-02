package com.chatapp.chatapp.controller;

import java.util.Map;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import com.google.gson.Gson;

import javax.annotation.Resource;

@Controller
public class WebSocketController {

    @Resource
    SimpMessageSendingOperations simpMessageSendingOperationsWebSocketController;

    @MessageMapping("/message")
    public String processMessageFromClient(@Payload String message) {
        String name = new Gson().fromJson(message, Map.class).get("message").toString();
        String destination = new Gson().fromJson(message, Map.class).get("id").toString();
        String url = "/topic/reply/" + destination;
        simpMessageSendingOperationsWebSocketController.convertAndSend(url, name);
        return name;
    }



    @MessageExceptionHandler
    public String handleException(Throwable exception) {
        simpMessageSendingOperationsWebSocketController.convertAndSend("/errors", exception.getMessage());
        return exception.getMessage();
    }

}