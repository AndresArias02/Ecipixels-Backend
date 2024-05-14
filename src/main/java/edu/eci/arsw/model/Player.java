package edu.eci.arsw.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import edu.eci.arsw.service.GameServices;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.util.*;

@RedisHash
public class Player implements Serializable{
    private Integer id;
    private String name;
    private Head head;
    private String color;
    private List<String> pixelsOwned;
    private List<String> pixelsRoute;
    private Integer gainedArea;
    private boolean isAlive = true;
    @Transient
    @JsonIgnore
    private transient GameServices gameServices;


    public Player(String name){
        setId();
        this.name = name;
        this.head = null;
        this.color = "";
        this.pixelsOwned = new ArrayList<>();
        this.pixelsRoute = new ArrayList<>();
        this.pixelsRoute.add("0,0");
        this.gainedArea = 0;
    }

    public Player() {

    }

    public void updatePixelsRoute() {
        Game game = gameServices.getGame(); // Obtener el juego actual

        // Comprobar si el jugador está vivo y no se encuentra en los bordes del tablero
        if (this.isAlive && head.getCol() != 0 && head.getRow() != 0 && head.getCol() != 49 && head.getRow() != 49 && !killHimself()) {
            String positionPixel = head.getRow() + "," + head.getCol(); // Obtener la posición de la cabeza del jugador
            checkIfPlayerKilledAnother(positionPixel); // Comprobar si con la nueva posición de la cabeza el jugador mató a alguien

            // Si la posición no está en los pixeles poseídos, agregarla al recorrido
            if (!pixelsOwned.contains(positionPixel)) {
                pixelsRoute.add(positionPixel);
            } else {
                // Si ya está en los pixeles poseídos, procesar el área ganada
                processGainedArea();
            }
        } else {
            // Si el jugador está muerto o en los bordes, marcarlo como muerto y eliminarlo del juego
            this.isAlive = false;
            this.pixelsRoute.clear();
            game.deletePlayer(this);
        }
    }

    private void processGainedArea() {
        Game game = gameServices.getGame();

        // Si el recorrido contiene más de una posición (excluyendo la posición predeterminada '0,0')
        if (pixelsRoute.size() > 1) {
            List<String> routeCopy = new ArrayList<>(pixelsRoute); // Sacar una copia de la lista de pixeles recorridos

            for (String routePixel : routeCopy) {
                String[] values = routePixel.split(",");
                int x = Integer.parseInt(values[0]);
                int y = Integer.parseInt(values[1]);

                if (x != 0 && y != 0) {
                    Integer gridValue = game.getPixel(x, y); // Obtener el valor del tablero en esa posición
                    checkIfPixelBelongsToSomeone(gridValue,routePixel,game); // Comprobar si el pixel ya esta ocupado por otro player
                    gameServices.updatePixelBoardGrid(routePixel, this.id); // Actualizar el pixel como nuevo pixel ganado en el área
                }
            }

            // Eliminar el valor predeterminado de los pixeles recorridos
            routeCopy.remove("0,0");
            // Añadir esos pixeles ganados al área nueva
            pixelsOwned.addAll(routeCopy);
            // Determinar el Area encerrada por el recorrido
            calcArea(game);
            // Determinar el nuevo área del jugador
            setGainedArea(pixelsOwned.size());
            // Limpiar la lista de recorrido del jugador y añadir la posición predeterminada '0,0'
            pixelsRoute.clear();
            pixelsRoute.add("0,0");
            // Actualizar el juego completo
            game.updatePlayer(this);
            gameServices.updateGame(game);

        }
    }


    private boolean killHimself() {
        String positionHead = head.getRow() + "," + head.getCol();
        Boolean killedHimself = false;
        if(pixelsRoute.contains(positionHead)){
            killedHimself = true;
        }
        return killedHimself;
    }


    private void checkIfPlayerKilledAnother(String pixel) {
        Game game = gameServices.getGame();
        List<Player>  players = gameServices.getPlayers();
        List<Player> playersToRemove = new ArrayList<>();
        for(Player p: players){
           if(!p.equals(this) && p.getPixelsRoute() != null && p.getPixelsRoute().contains(pixel)){
               playersToRemove.add(p);
           }
        }
        for(Player p: playersToRemove){
            if(p!=this){
                p.setAlive(false);
                p.getPixelsRoute().clear();
                game.deletePlayer(p);
                gameServices.updatePlayer(p);
            }
        }
    }

    private synchronized void checkIfPixelBelongsToSomeone(Integer gridValue, String routePixel,Game game){
        if (gridValue != 0) { // ¿Es el ID de otro jugador?
            Player player = gameServices.getPlayer(String.valueOf(gridValue));
            player.removePixel(routePixel);
            player.setGainedArea(player.getGainedArea() - 1);
            game.updatePlayer(player);
            gameServices.updatePlayer(player);
        }
    }

    public void addPixelOwned(int x, int y) {
        pixelsOwned.add(x + "," + y);
    }

    public void addPixelRoute(int x, int y) {
        pixelsRoute.add(x + "," + y);
    }

    public synchronized void removePixel(String p) {
        if(pixelsOwned.contains(p)){
            pixelsOwned.remove(p);
        }

    }

    private void calcArea(Game game) {
        Integer[][] grid = gameServices.getBoard();
        int id = this.id; // Cache this.id to avoid repeated field access

        // Calculate mins and maxs
        int minRow = Integer.MAX_VALUE;
        int minCol = Integer.MAX_VALUE;
        int maxRow = 0;
        int maxCol = 0;

        // Calculate minimum and maximum rows and columns
        for (int i = 1; i < grid.length - 1; i++) {
            for (int j = 1; j < grid[i].length - 1; j++) {
                if (grid[i][j].equals(id)) {
                    minRow = Math.min(minRow, i);
                    minCol = Math.min(minCol, j);
                    maxRow = Math.max(maxRow, i);
                    maxCol = Math.max(maxCol, j);
                }
            }
        }

        // Iterate over the area
        for (int r = minRow; r <= maxRow; r++) {
            for (int c = minCol; c <= maxCol; c++) {
                if (!grid[r][c].equals(id)) {
                    // Count the number of borders of the target color around the cell
                    int paso = 0;

                    // Check right
                    for (int c1 = c + 1; c1 <= maxCol; c1++) {
                        if (grid[r][c1].equals(id)) {
                            paso++;
                            break;
                        }
                    }

                    // Check left
                    for (int c1 = c - 1; c1 >= minCol; c1--) {
                        if (grid[r][c1].equals(id)) {
                            paso++;
                            break;
                        }
                    }

                    // Check down
                    for (int r1 = r + 1; r1 <= maxRow; r1++) {
                        if (grid[r1][c].equals(id)) {
                            paso++;
                            break;
                        }
                    }

                    // Check up
                    for (int r1 = r - 1; r1 >= minRow; r1--) {
                        if (grid[r1][c].equals(id)) {
                            paso++;
                            break;
                        }
                    }

                    // If all four borders are found, paint the cell
                    if (paso == 4) {
                        String pixel = r + "," + c;
                        Integer value = game.getPixel(r, c);
                        checkIfPixelBelongsToSomeone(value, pixel, game);
                        pixelsOwned.add(pixel);
                        gameServices.updatePixelBoardGrid(pixel, id);
                    }
                }
            }
        }
    }


    public void setHead(Head head) {
        this.head = head;
    }

    public Head getHead() {
        return this.head;
    }

    public String getPlayerName(){
        return this.name;
    }

    public String getColor() {
        return color;
    }

    public Integer getPlayerId(){
        return this.id;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public List<String> getPixelsOwned() {
        return pixelsOwned;
    }

    public List<String> getPixelsRoute() {
        return pixelsRoute;
    }

    public void setGameServices(GameServices gameServices) {
        this.gameServices = gameServices;
    }

    public void setPixelsOwned(List<String> pixelsOwned) {
        this.pixelsOwned = pixelsOwned;
    }

    public synchronized int getGainedArea() {
        return gainedArea;
    }

    public synchronized void setGainedArea(int gainedArea) {
        this.gainedArea = gainedArea;
    }

    public boolean getIsAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    private void setId(){
        UUID uuid = UUID.randomUUID();
        this.id = uuid.hashCode();
    }
}