package EciPixels;

import edu.eci.arsw.EciPixelsApplication;
import edu.eci.arsw.controller.GameController;
import edu.eci.arsw.controller.WebSocketController;
import edu.eci.arsw.model.GameState;
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

@SpringBootTest(classes = EciPixelsApplication.class)
@ExtendWith(SpringExtension.class)
class EciPixelsApplicationTests {

    @Autowired
    private GameController gameController;

    @Autowired
    private WebSocketController webSocketController;

    @Autowired
    private GameServices gameServices;

    @Autowired
    private BoardServices boardServices;

    @BeforeEach
    void setUp() {
        // Puedes configurar cualquier comportamiento adicional aquí si es necesario
    }

    @Test
    void testGetBoard() {
        // Llamada al método del controlador para obtener el tablero
        ResponseEntity<Integer[][]> response = gameController.getBoard();

        // Verificar que la respuesta tiene un estado OK
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());

        // Verificar que el cuerpo de la respuesta no es nulo
        assertNotNull(response.getBody());
    }


    @Test
    void testDeletePlayer() {
        String playerId = "1";
        ResponseEntity<Void> response = gameController.deletePlayer(playerId);

        // Verificar que la respuesta no es nula y que el estado sea NotFound
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    void testHandleNewPlayer() {
        // Llamada al método del controlador para manejar un nuevo jugador
        GameState gameState = webSocketController.handleNewPlayer("New player joined");

        // Verificar que el estado del juego devuelto no es nulo
        assertNotNull(gameState);

        // Verificar que el tablero del estado del juego no es nulo
        assertNotNull(gameState.getBoard());

        // Verificar que la lista de jugadores del estado del juego no es nula
        assertNotNull(gameState.getPlayers());
    }

    @Test
    void testMovePlayer() {
        // Crear un jugador
        Player player = new Player("1");

        // Agregar el jugador al servicio de juegos
        gameServices.addNewPlayer(player);

        // Llamada al método del controlador para mover un jugador
        GameState gameState = webSocketController.movePlayer(player.getPlayerId(), 5, 5);

        // Verificar que el estado del juego devuelto no es nulo
        assertNotNull(gameState);

        // Verificar que el tablero del estado del juego no es nulo
        assertNotNull(gameState.getBoard());

        // Verificar que la lista de jugadores del estado del juego no es nula
        assertNotNull(gameState.getPlayers());
    }
}
