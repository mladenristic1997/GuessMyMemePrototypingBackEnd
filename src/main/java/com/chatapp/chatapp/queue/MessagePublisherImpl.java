package com.chatapp.chatapp.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class MessagePublisherImpl implements MessagePublisher {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    public MessagePublisherImpl(){

    }

    public MessagePublisherImpl(final RedisTemplate<String, Object> redistTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(final String message, ChannelTopic channelTopic){
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }

}