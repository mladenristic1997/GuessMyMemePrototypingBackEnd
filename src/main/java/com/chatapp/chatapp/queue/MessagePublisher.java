package com.chatapp.chatapp.queue;

public interface MessagePublisher{
    
    void publish(final String message, ChannelTopic topic);

}