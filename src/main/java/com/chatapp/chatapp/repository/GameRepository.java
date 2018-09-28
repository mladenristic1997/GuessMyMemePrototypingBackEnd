package com.chatapp.chatapp.repository;

import java.util.Map;
import com.chatapp.chatapp.model.Game;

public interface GameRepository {

    void save(Game game);
    Map<String, Game> findAll();
    Game findById(String id);
    void update(Game game);
    void delete(String id);

}