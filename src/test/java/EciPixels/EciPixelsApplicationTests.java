package EciPixels;

import edu.eci.arsw.EciPixelsApplication;
import edu.eci.arsw.controller.GameController;
import edu.eci.arsw.controller.WebSocketController;
import edu.eci.arsw.model.GameState;
import edu.eci.arsw.model.Head;
import edu.eci.arsw.model.Player;
import edu.eci.arsw.service.BoardServices;
import edu.eci.arsw.service.GameServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(classes = EciPixelsApplication.class)
@ExtendWith(SpringExtension.class)
class EciPixelsApplicationTests {

    @Autowired
    private GameController gameController;

    @Autowired
    private WebSocketController webSocketController;

    private GameServices gameServices;
    private BoardServices boardServices;

    @BeforeEach
    void setUp() {
        gameServices = mock(GameServices.class);
        boardServices = mock(BoardServices.class);
        gameController = new GameController(gameServices, boardServices);
        webSocketController = new WebSocketController(gameServices);
    }

    @Test
    void testGetBoard() {
        // Mock del tablero esperado
        Integer[][] expectedBoard = new Integer[10][10];
        when(boardServices.getBoard()).thenReturn(expectedBoard);

        // Llamada al método del controlador para obtener el tablero
        ResponseEntity<Integer[][]> response = gameController.getBoard();

        // Verificar que la respuesta tiene un estado OK
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta no es nulo
        assertNotNull(response.getBody());

        // Verificar que el cuerpo de la respuesta es igual al tablero esperado
        assertArrayEquals(expectedBoard, response.getBody());
    }


    @Test
    void testDeletePlayer() {
        String playerId = "1";
        Player playerToDelete = new Player(playerId);
        when(gameServices.getPlayer(playerId)).thenReturn(playerToDelete);

        ResponseEntity<Void> response = gameController.deletePlayer(playerId);

        assert response.getStatusCode().equals(HttpStatus.OK);
    }

    @Test
    void testHandleNewPlayer() {
        // Mock de la lista de jugadores esperada
        List<Player> expectedPlayers = new ArrayList<>();
        expectedPlayers.add(new Player("1"));
        expectedPlayers.add(new Player("2"));
        when(gameServices.getBoard()).thenReturn(new Integer[10][10]);
        when(gameServices.getPlayers()).thenReturn(expectedPlayers);

        // Llamada al método del controlador para manejar un nuevo jugador
        GameState gameState = webSocketController.handleNewPlayer("New player joined");

        // Verificar que el estado del juego devuelto no es nulo
        assertNotNull(gameState);

        // Verificar que el tablero del estado del juego no es nulo
        assertNotNull(gameState.getBoard());

        // Verificar que la lista de jugadores del estado del juego no es nula
        assertNotNull(gameState.getPlayers());

        // Verificar que la lista de jugadores del estado del juego es igual a la lista esperada
        assertEquals(expectedPlayers, gameState.getPlayers());
    }

    @Test
    void testMovePlayer() {
        // Crear un jugador
        Player player = new Player("1");

        // Configurar el comportamiento del método addNewPlayer para que no haga nada y simplemente retorne null
        doNothing().when(gameServices).addNewPlayer(any(Player.class));

        // Configurar el comportamiento del método getPlayer para que retorne el jugador creado
        when(gameServices.getPlayer(player.getPlayerId().toString())).thenReturn(player);

        // Mock de la lista de jugadores esperada
        List<Player> expectedPlayers = new ArrayList<>();
        expectedPlayers.add(player);
        when(gameServices.getBoard()).thenReturn(new Integer[10][10]);
        when(gameServices.getPlayers()).thenReturn(expectedPlayers);

        // Llamada al método del controlador para mover un jugador
        GameState gameState = webSocketController.movePlayer(player.getPlayerId(), 5, 5);

        // Verificar que el estado del juego devuelto no es nulo
        assertNotNull(gameState);

        // Verificar que el tablero del estado del juego no es nulo
        assertNotNull(gameState.getBoard());

        // Verificar que la lista de jugadores del estado del juego no es nula
        assertNotNull(gameState.getPlayers());

        // Verificar que la lista de jugadores del estado del juego es igual a la lista esperada
        assertEquals(expectedPlayers, gameState.getPlayers());
    }


}
