package tanks10.servlet;

import java.io.IOException;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 *
 * @author maleksandrowicz
 */

public class ServerHandler  extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        System.out.println(message);
        session.sendMessage(new TextMessage("Response to:"+message.getPayload()+", from session:"+session.getId()));
    }

}
