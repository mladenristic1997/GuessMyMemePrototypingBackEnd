package com.chatapp.chatapp.queue;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
public class MessageSubscriber implements MessageListener {

    @Autowired
    @Qualifier("messageSubscriberMessagingTemplate")
    SimpMessageSendingOperations messagingTemplate;

    public void onMessage(final Message message, final byte[] pattern){
        //send the message to the user by extracting from the Player object (Message message)
    }

}