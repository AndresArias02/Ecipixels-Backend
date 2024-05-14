package edu.eci.arsw.controller;

import edu.eci.arsw.model.GameState;
import edu.eci.arsw.model.Head;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.service.GameServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class WebSocketController {

    @Autowired
    GameServices gameServices;

    @MessageMapping("/gameState")
    @SendTo("/topic/GameState")
    public GameState handleNewPlayer(String message) {
        //System.out.println("player name : " + " " + message);
        Integer[][] grid = gameServices.getBoard();
        List<Player> players = gameServices.getPlayers();
        return new GameState(grid,players);
    }

    @MessageMapping("/movePlayer/{playerId}/{row}/{col}")
    @SendTo("/topic/movePlayer")
    public GameState movePlayer(@DestinationVariable Integer playerId, @DestinationVariable Integer row, @DestinationVariable Integer col) {
        System.out.println("player id: " + playerId);
        System.out.println("row: " + row);
        System.out.println("col: " + col);

        Player player = gameServices.getPlayer(playerId.toString());
        player.setHead(new Head(row, col));
        this.gameServices.move(player);

        return new GameState(gameServices.getBoard(),gameServices.getPlayers());
    }

}
