package edu.eci.arsw.model;




import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.eci.arsw.service.GameServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.stereotype.Component;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;


@RedisHash
public class Game implements Serializable {
    @Id
    private String id;
    private List<Player> players;
    private String[] colors;
    private List<String> usedColors;
    @JsonIgnore

    private GameServices gameServices;

    public Game(){
        this.id ="game";
        this.players= new ArrayList<>();
        this.usedColors = new ArrayList<>();
        this.colors = new String[]{"blue","red","yellow","green","purple"};
    }

    private void addNewPlayer(Player player){
        players.add(player);
    }

    public void configurePlayer(Player player){
        player.setColor(chooseRandomColor());
        locatePlayer(player);
        addNewPlayer(player);
    }

    public void locatePlayer(Player player){
        placeRandomPlayer(player);
    }

    public void deletePlayer(Player player){
        List<String> pixelsOwned = player.getPixelsOwned();
        gameServices.updateBoardGrid(pixelsOwned,0);
        usedColors.remove(player.getColor());
        players.remove(player);
        gameServices.deletePlayer(player);
        gameServices.updateGame(this);
    }


    public Integer getPixel(int x, int y){
        String positionValue = x +","+ y;
        Integer value = gameServices.getPixelBoard(positionValue);
        return value;
    }

    public void updatePlayer(Player player){
        // Itera sobre la lista de jugadores
        Iterator<Player> iterator = players.iterator();
        while (iterator.hasNext()) {
            Player p = iterator.next();
            // Comprueba si el jugador que deseas actualizar está en la lista
            if (p.getPlayerId().equals(player.getPlayerId())) {
                // Si lo encuentra, elimina el jugador de la lista
                iterator.remove();
                break;
            }
        }
        // Agrega el jugador actualizado a la lista
        players.add(player);
    }


    public List<Player> getPlayers() {
        return players;
    }

    public Integer getAreaByPlayer(Player p){
        Integer area = 0;
        if(players.contains(p)){
            for(Player player: players){
                if(player.equals(p)){
                    area = player.getGainedArea();
                    break;
                }
            }
        }
        return area;
    }

    private String chooseRandomColor() {
        Random random = new Random();
        int index = random.nextInt(colors.length);
        String color = colors[index];
        if (usedColors.contains(color)) {
            return chooseRandomColor();
        } else {
            usedColors.add(color);
            return color;
        }
    }

    private void placeRandomPlayer(Player player) {
        Random random = new Random();
        boolean playerPlaced = false;
        Integer[][] grid = gameServices.getBoard();

        while (!playerPlaced) {

            int x = random.nextInt(40) + 5;
            int y = random.nextInt(40) + 5;

            if (!isPlayerOccupied(x, y, grid)) {
                player.setHead(new Head(x,y));
                for(int i = x-1; i <= x+1;i++){
                    for(int j = y-1; j <= y+1;j++){
                        player.addPixelOwned(i,j);
                    }
                }
                player.setGainedArea(player.getPixelsOwned().size());
                gameServices.updateBoardGrid(player.getPixelsOwned(), player.getPlayerId());
                playerPlaced = true;
            }
        }
    }

    private boolean isPlayerOccupied(int x, int y, Integer[][] grid) {
        boolean flag = false;
        Integer pixel = grid[x][y];
        if (pixel == 0) {
            for (int i = x - 4 ; i <= x + 4; i++) {
                for (int j = y - 4; j <= y + 4; j++) {
                    int adjacentPixel = grid[i][j];
                    if (adjacentPixel != 0) {
                        flag = true;
                        break;
                    }
                }
            }
        }
        return flag;
    }

    public String getId() {
        return id;
    }

    public String[] getColors() {
        return colors;
    }

    public List<String> getUsedColors() {
        return usedColors;
    }

    public GameServices getGameServices() {
        return gameServices;
    }

    public void setGameServices(GameServices gameServices) {
        this.gameServices = gameServices;
    }
}



