package com.chatapp.chatapp.repository;

import com.chatapp.chatapp.model.Game;
import com.chatapp.chatapp.model.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

@Service
@ComponentScan(basePackages = {"com.chatapp.chatapp.config"})
public class GameRepositoryImpl implements GameRepository {
    private static final String KEY = "game";

    private RedisTemplate<String, Game> redisTemplate;

    private HashOperations<String, String, Game> hashOperations;

    @Autowired
    public GameRepositoryImpl(RedisTemplate<String, Game> redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init(){
        hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void save(Game game){
        hashOperations.put(KEY, game.getId(), game);
    }

    @Override
    public Map<String, Game> findAll(){
        return hashOperations.entries(KEY);
    }

    @Override
    public Game findById(String id){
        return (Game)hashOperations.get(KEY, id);
    }

    @Override
    public void update(Game game){
        save(game);
    }

    @Override
    public void delete(String id){
        hashOperations.delete(KEY, id);
    }



}