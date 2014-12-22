/*
This file is part of Tanks10 Project (https://github.com/macvek/Tanks10).

Foobar is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Foobar is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Tanks10 Project.  If not, see <http://www.gnu.org/licenses/>.
*/
package tanks10;

import java.io.PrintStream;
import java.nio.ByteBuffer;

import tanks10.packets.Packet;
import tanks10.protocols.TanksProtocol;

/**
 * Klasa pośrednicząca między warstwą protokołu i warstwą sieciową.
 * Korzysta z niej Entity do wysyłania pakietów.
 * 
 * @author Macvek
 *
 */
public class SendProxy {
	public static final int SENDBUFFER_SIZE = 1024;
	private TanksProtocol protocol;
	private ClientHandler client;
	private PrintStream log;
	
	SendProxy(TanksProtocol protocol, ClientHandler client,PrintStream log) {
		this.protocol = protocol;
		this.client = client;
		this.log = log;
	}
	
	public TanksProtocol getProtocol() {return protocol;}
	
	/**
	 * Przekazanie buforu do wysłania do klienta 
	 */
	public void sendFromBuffer(ByteBuffer in) {
		if (in.remaining() == 0)
			return;
		
		client.addToSendBuffers(in.asReadOnlyBuffer());
	}
	
	/**
	 * Zbudowanie komunikatu na podstawie pakietu i dodanie do kolejki do wysłania 
	 */
	public void send(Packet msg) {
		ByteBuffer sendBuffer = ByteBuffer.allocate(SENDBUFFER_SIZE);
		sendBuffer.rewind().mark();
		
		sendToBuffer(msg,sendBuffer);
		
		sendBuffer.limit(sendBuffer.position());
		sendBuffer.position(0);
		
		sendFromBuffer(sendBuffer);
	}
	
	public void sendToBuffer(Packet msg, ByteBuffer out) {
		if (!protocol.send(out,msg))
			log.println(this.hashCode()+":Błąd podczas dekodowania wiadomości do wysłania");
	}
	
	public void disconnect() {
		client.kill = true;
		client.addToSendBuffers(null);	// obudzenie wątku
	}
}
