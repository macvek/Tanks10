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
package tanks10.protocols;

import java.util.HashMap;

import tanks10.packets.Packet;
import tanks10.world.Entity;
import tanks10.world.TanksWorld;

/**
 * Pierwszy produkcyjny protokół współpracujący z mechanizmem pakietów 
 * @author Macvek
 *
 */
public class TanksProtocolTiger extends TanksProtocolPlain {
	public static final String NAME="Tiger";
	public static final String VERSION="Plain";
	
	// HashMap jest tylko raz ustawiana przed pierwszą instancją
	private static HashMap<String,Class<Packet>> packets = new HashMap<String,Class<Packet>>();
	
	// Obiekt do którego są przesyłane pakiety
	private Entity self;
	
	public void setEntity(Entity self) {
		this.self = self;
	}
	
	// Pakiet jest kojarzony przez nazwę zwracaną przez getPacketName
	@SuppressWarnings("unchecked")
	public static void registerPacket(Packet in) {
		String name = in.getPacketName();
		Class<Packet> clazz = (Class<Packet>)in.getClass();
		
		// Pakiety są rejestrowane tylko raz i to na początku programu
		if ( null != packets.get(name) ) {
			throw new RuntimeException("Błąd podczas rejestracji protokołu:"+name+", próba nadpisania");
		}
		
		packets.put(name,clazz);
	}
	
	// obecnie nieużywany
	@Override
	public void reset() {}

	@Override
	protected void decode(String name, HashMap<String, Object> readFields) {
		Class<Packet> clazz = packets.get(name);

		// Nieznany pakiet
		if ( null == clazz )
			return;

		Packet packet;
		try {
			packet = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Błąd podczas tworzenia pakietu:"+name+", TanksProtocolTiger.decode");
		}
		
		// Jeżeli pakiet ma poprawne dane to wywołaj onReceive
		if (packet.setPacketFields(readFields)) {
			packet.onReceive(self);
		}
	}

	@Override
	protected String encode(Object in, HashMap<String, Object> sendFields) {
		Packet packet = (Packet)in;
		String name = packet.getPacketName();
		sendFields.putAll(packet.getPacketFields());
		return name;
	}

	// Wywoływane przy utracie połączenia. W tym przypadku skutkuje to usunięciem Entity
	@Override
	public void connectionLost() {
		TanksWorld.removeEntity(self);
	}

	@Override
	protected void decodeEnd() {
		// tutaj tego nie wykorzystuje
		
	}

}
