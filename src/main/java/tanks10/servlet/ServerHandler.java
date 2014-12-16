package tanks10.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tanks10.ClientHandler;
import tanks10.world.TanksWorld;

/**
 *
 * @author maleksandrowicz
 */
public class ServerHandler extends TextWebSocketHandler {

    private HashMap<WebSocketSession, ClientHandler> sessionMap = new HashMap<>();
    private static Thread tanksWorldThread;

    public ServerHandler() {
        tanksWorldThread = new Thread(new TanksWorld(), "TanksWorld");
        // Uruchomienie silnika gry
        tanksWorldThread.start();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessionMap.remove(session);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        final ClientHandler clientHandler = new ClientHandler(session);
        sessionMap.put(session, clientHandler);
    }

    @Override
    public void handleTextMessage(final WebSocketSession session, TextMessage message) throws IOException {
        final ClientHandler clientHandler = sessionMap.get(session);
        clientHandler.handleMessage(message.getPayload());
    }

}
