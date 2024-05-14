package edu.eci.arsw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoardServices {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String nameBoard;

    @Autowired
    public BoardServices(RedisTemplate<String, Object> redisTemplate){
        this.redisTemplate = redisTemplate;
        this.nameBoard = "board_Grid";
    }

    public void saveBoardGrid() {
        int rows = 50;
        int columns = 50;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                String key = i + "," + j;
                Integer value = 0;
                if (i == 0 || j == 0 || i == 49 || j == 49) {
                    value = null;
                }
                redisTemplate.opsForHash().put(nameBoard, key, (value != null) ? value.toString() : "null");
            }
        }
    }


    public void updateBoard(List<String> keys, Integer value){
        for(String key : keys){
            redisTemplate.opsForHash().put(nameBoard,key,String.valueOf(value));
        }
    }

    public void updatePixelBoardGrid(String key, Integer value){
        redisTemplate.opsForHash().put(nameBoard,key,value);
    }

    public Integer[][] getBoard() {
        Integer[][] grid = new Integer[50][50];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                String key = i + "," + j;
                Object value = redisTemplate.opsForHash().get(nameBoard, key);
                if (value instanceof String) {
                    String stringValue = (String) value;
                    if (!stringValue.equals("null")) {
                        grid[i][j] = Integer.parseInt(stringValue);
                    } else {
                        grid[i][j] = null;
                    }
                } else if (value instanceof Integer) {
                    grid[i][j] = (Integer) value;
                } else {
                    grid[i][j] = null;
                }
            }
        }
        return grid;
    }


    public Integer getPixelBoard(String positionValue) {
        Integer realdValue=  null;
        Object value = redisTemplate.opsForHash().get(nameBoard, positionValue);
        if(value instanceof Integer){
            realdValue = (Integer) value;
        } else if (value instanceof String) {
            realdValue = Integer.parseInt((String) value);
        }
        return realdValue;
    }
}
