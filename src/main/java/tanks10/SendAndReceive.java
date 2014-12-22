/*
 This file is part of Tanks10 Project (http://tanks10.sourceforge.net/).

 Tanks10 Project is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Tanks10 Project is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Tanks10 Project.  If not, see <http://www.gnu.org/licenses/>.
 */
package tanks10;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import tanks10.protocols.TanksProtocol;

/**
 * Klasa zarządzająca wysyłaniem i odbieraniem wiadomości. Dziedziczą po niej:
 * <ul>
 * <li>ClientHandler - serwer</li>
 * <li>ConnectionManager - applet</li>
 * </ul>
 *
 * @author Macvek
 *
 */
public class SendAndReceive {

    private final ByteBuffer[] sendBuffers = new ByteBuffer[SENDLIMIT];

    private static final Logger LOG = Logger.getLogger(SendAndReceive.class.getName());

    final static int BUFFERSIZE = 8196;
    final static int SENDLIMIT = 128;

    protected WebSocketSession session;
    protected TanksProtocol protocol;

    public boolean kill = false;
    private boolean closed = false;

    /**
     * Umieszczenie bufora w kolejce do wysłania
     */
    public void addToSendBuffers(ByteBuffer in) {
        if (closed) {
            return;
        }
        if (kill) {
            closeSession();
        } else {

            // blokada tylko dotyczy operowania na wskaźnikach bufora
            synchronized (sendBuffers) {
                final TextMessage message;
                try {
                    byte[] arrayContent = new byte[in.limit()];
                    in.get(arrayContent);
                    String content = new String(arrayContent, "UTF-8");
                    message = new TextMessage(content);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }

                try {
                    session.sendMessage(message);
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "send message failed to " + session.getRemoteAddress().toString(), e);
                    closeSession();
                }
            }
        }

    }

    private void closeSession() throws RuntimeException {
        try {
            closed = true;
            protocol.connectionLost();
            session.close();
        } catch (Exception e) {
            LOG.log(Level.WARNING, "close session failed for " + session.getRemoteAddress().toString(), e);
        }
    }

    public void handleMessage(String msg) {
        try {
            byte[] backingArray = msg.getBytes("UTF-8");
            ByteBuffer readBuffer = ByteBuffer.wrap(backingArray);
            protocol.receive(readBuffer);
        } catch (UnsupportedEncodingException ex) {
            LOG.log(Level.SEVERE, "message parsing failed for: " + session.getRemoteAddress().toString() + " msg:" + msg, ex);
        }

    }

}
