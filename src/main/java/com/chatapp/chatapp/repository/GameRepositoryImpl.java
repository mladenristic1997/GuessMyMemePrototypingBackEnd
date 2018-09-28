package com.chatapp.chatapp.repository;

@Repository
public class GameRepositoryImpl implements GameRepository {
    private static final String KEY = "game";

    private RedisTemplate<String, Game> redisTemplate;
    @Resource(name="redisTemplate")
    private HashOperations<String, String, Person> hashOps;

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