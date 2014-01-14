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
package tanks10.packets;

import java.util.HashMap;
import java.util.Map;

import tanks10.world.Entity;

/**
 * Działa w obie strony. Służy do pomiaru opóźnienia
 * Może nieść ze sobą wiadomość, używany do synchronizacji (domyślnie przesyła aktualną klatkę)
 *
 */
public class Ping implements Packet {
	protected String msg;
	
	public Ping(){};
	public Ping(String msg) {
		this.msg = msg;
	}
	
	@Override
	public Map<String, Object> getPacketFields() {
		HashMap<String,Object> x = new HashMap<String,Object>();
		x.put("f", msg);
		
		return x;
	}

	@Override
	public String getPacketName() {
		return "Ping";
	}

	@Override
	public void onReceive(Entity host) {
		// Wyślij Pong z tą samą wiadomością.
		//FIXME: Tutaj trzeba zrobić limit wielkości tej wiadomości
		
		host.getProxy().send(new Pong(msg));
	}

	@Override
	public boolean setPacketFields(Map<String, Object> in) {
		try {
			msg = (String)in.get("x");
			return true;
		}catch(Exception e) {
			return false;
		}
	}

}
