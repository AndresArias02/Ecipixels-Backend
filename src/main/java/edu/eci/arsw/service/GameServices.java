package edu.eci.arsw.service;


import edu.eci.arsw.Configurations.RedisConfig;
import edu.eci.arsw.model.Game;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public class GameServices {

    @Autowired
    private BoardServices boardServices;

    @Autowired
    private PlayerServices playerServices;

    @Autowired(required = true)
    private GameRepository gameRepository;


    public void setNewPlayer(Game game, Player player){
        game.configurePlayer(player);
    }


    @Cacheable(RedisConfig.cacheName)
    public void createNewGame() {
        Game game = new Game();
        gameRepository.save(game);
        this.boardServices.saveBoardGrid();
    }

    @Cacheable(RedisConfig.cacheName)
    public void updateGame(Game game){
        gameRepository.save(game);
    }


    public void updateBoardGrid(List<String> keys, Integer value){
        boardServices.updateBoard(keys,value);
    }

    public void updatePixelBoardGrid(String key, Integer value){
        boardServices.updatePixelBoardGrid(key,value);
    }

    @Cacheable(RedisConfig.cacheName)
    public Game getGame(){
        Game game = null;
        Optional<Game> optionalGame = gameRepository.findById("game");
        if (optionalGame.isPresent()) {
            game = optionalGame.get();
            game.setGameServices(this);
        }
        return game;
    }


    public void addNewPlayer(Player player){
        Game game = getGame();
        setNewPlayer(game,player);
        boardServices.updateBoard(player.getPixelsOwned(),player.getPlayerId());
        playerServices.addPlayer(player);
        updateGame(game);
    }


    public Player getPlayer(String playerId){
        Player player = null;
        Optional<Player> optionalPlayer = playerServices.getPlayer(Integer.parseInt(playerId));
        if (optionalPlayer.isPresent()) {
            player = optionalPlayer.get();
        }
        return player;
    }

    public List<Player> getPlayers(){
       return (List<Player>) playerServices.getPLayers();
    }

    public void deletePlayer(Player player){
        playerServices.deletePlayer(player);
    }

    public void updatePlayer(Player player){
        playerServices.updatePlayer(player);
    }

    public Integer[][] getBoard(){
        return boardServices.getBoard();
    }

    public Integer getPixelBoard(String positionValue){
        return boardServices.getPixelBoard(positionValue);
    }

    public void move(Player player) {
        this.playerServices.move(player,this);
    }
}