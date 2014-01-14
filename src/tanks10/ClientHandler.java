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

import tanks10.protocols.*;

import java.nio.channels.*;
import java.io.*;

import tanks10.world.Entity;
import tanks10.world.TanksWorld;

/**
 * Klasa zarządzająca każdym połączeniem
 * @author Macvek
 */

public class ClientHandler extends SendAndReceive implements Runnable {
	private PrintStream log;
	
	ClientHandler(SocketChannel client,PrintStream log) {
		try {
			client.socket().setTcpNoDelay(true);
			client.configureBlocking(true);
		}
		catch(Exception e) {
			log.println(this.hashCode()+":Błąd podczas wyłączania blokowania gniada");
		}
		this.socket = client;
		this.log = log;
		
		TanksProtocolTiger proto = new TanksProtocolTiger();
		Entity newbie = TanksWorld.newNewbie();
		
		newbie.setProxy(new SendProxy(proto,this,log));	// utworzenie dowiązania do protokołu
		proto.setEntity(newbie);
		
		// wysłanie pierwszego pakietu do klienta
		newbie.hello();
		
		protocol = proto;
	}
	
	private boolean startSendLoop = false;	// zmienna pomocnicza określająca działanie wątku
	
	/**
	 * Obsługa połączenia, w zależności od startSendLoop wywołuje obsługę wysyłania
	 * lub odczytywania
	 */
	@Override
	public void run() {
		if (startSendLoop) {
			sendLoop();
			return;
		}
		// uruchomienie wątku wysyłającego
		startSendLoop = true;
		new Thread(this).start();	
		
		try {
			receiveLoop();
		}catch(IOException e) {
			log.println(this.hashCode()+" >Zerwano połączenie");
			protocol.connectionLost();
		}
		try {
			socket.close();
		}catch(IOException e) {}
	}
	
}
