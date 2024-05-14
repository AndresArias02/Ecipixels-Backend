package edu.eci.arsw.service;


import edu.eci.arsw.Configurations.RedisConfig;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerServices {


    @Autowired
    PlayerRepository playerRepository;

    @Cacheable(RedisConfig.cacheName)
    public void addPlayer(Player player) {
        playerRepository.save(player);
    }

    @Cacheable(RedisConfig.cacheName)
    public void updatePlayer(Player player){
        playerRepository.save(player);
    }

    @CacheEvict(RedisConfig.cacheName)
    public void deletePlayer(Player player){
        playerRepository.deleteById(player.getPlayerId());
    }

    public void move(Player player,GameServices gameServices){
        player.setGameServices(gameServices);
        player.updatePixelsRoute();
        updatePlayer(player);
    }

    public List<String> getPixelsOwned(String id){
        Optional<Player> optionalPlayer = getPlayer(Integer.parseInt(id));
        List<String> pixels = null;
        if (optionalPlayer.isPresent()) {
            pixels = optionalPlayer.get().getPixelsOwned();
        }
        return pixels;
    }

    @Cacheable(RedisConfig.cacheName)
    public Optional<Player> getPlayer(Integer idPlayer) {
        return playerRepository.findById(idPlayer);
    }

    public Iterable<Player> getPLayers(){
        return playerRepository.findAll();
    }
}