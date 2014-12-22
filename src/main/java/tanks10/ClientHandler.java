/*
 This file is part of Tanks10 Project (https://github.com/macvek/Tanks10).

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

import tanks10.protocols.*;

import java.io.*;
import javax.websocket.Session;
import org.springframework.web.socket.WebSocketSession;

import tanks10.world.Entity;
import tanks10.world.TanksWorld;

/**
 * Klasa zarządzająca każdym połączeniem
 *
 * @author Macvek
 */
public class ClientHandler extends SendAndReceive {

    public ClientHandler(WebSocketSession session) {
        this.session = session;
 
        TanksProtocolTiger proto = new TanksProtocolTiger();
        Entity newbie = TanksWorld.newNewbie();

        newbie.setProxy(new SendProxy(proto, this, System.out));	// utworzenie dowiązania do protokołu
        proto.setEntity(newbie);

        // wysłanie pierwszego pakietu do klienta
        newbie.hello();

        protocol = proto;
    }
}
