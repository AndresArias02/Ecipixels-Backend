package edu.eci.arsw.model;

import java.util.List;

public class GameState {

    private Integer[][] board;
    private List<Player> players;

    public GameState(Integer[][] board, List<Player> players) {
        this.board = board;
        this.players = players;
    }

    public GameState() {
    }

    public Integer[][] getBoard() {
        return board;
    }

    public void setBoard(Integer[][] board) {
        this.board = board;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
}
