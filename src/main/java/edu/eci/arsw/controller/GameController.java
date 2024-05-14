package edu.eci.arsw.controller;

import edu.eci.arsw.model.*;
import edu.eci.arsw.service.BoardServices;
import edu.eci.arsw.service.GameServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/eciPixelsGame")
@CrossOrigin(origins = "http://localhost:4200")
public class GameController {

    private final GameServices gameServices;
    private final BoardServices boardServices;

    @Autowired
    public GameController(GameServices gameServices, BoardServices boardServices){
        this.gameServices = gameServices;
        this.boardServices = boardServices;
    }

    @GetMapping(value = "/board")
    public ResponseEntity<Integer[][]> getBoard(){
        try{
            Integer[][] board  = boardServices.getBoard();
            return new ResponseEntity<>(board, HttpStatus.ACCEPTED);
        } catch (Exception ex){
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(value = "/players")
    public ResponseEntity<List<Player>> getPlayers(){
        try{
            List<Player> players = gameServices.getGame().getPlayers();
            for(Player p: players){
                gameServices.updatePlayer(p);
            }
            return new ResponseEntity<>(gameServices.getPlayers(), HttpStatus.ACCEPTED);
        } catch (Exception ex){
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping(value = "/addPlayer")
    public ResponseEntity<Player> addPlayer(@RequestBody String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        try{
            Player player = new Player(name);
            gameServices.addNewPlayer(player);
            return new ResponseEntity<>(player, HttpStatus.CREATED);
        } catch (Exception ex){
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping(value = "/deletePlayer/{playerId}")
    public ResponseEntity<Void> deletePlayer(@PathVariable String playerId) {
        Player player = gameServices.getPlayer(playerId);
        if (player == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        try{
            gameServices.deletePlayer(player);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception ex){
            Logger.getLogger(GameController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

}
