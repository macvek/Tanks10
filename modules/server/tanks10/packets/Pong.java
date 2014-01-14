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
import java.util.Date;

import tanks10.world.Entity;
import tanks10.world.TanksWorld;

/**
 * Odpowiedź na Ping. Powinna zawsze zwracać tą samą wiadomość, ale tego nie sprawdzamy
 */
public class Pong extends Ping {
	public Pong(){}
	public Pong(String msg) {
		this.msg = msg;
	}
	
	@Override
	public String getPacketName() {
		return "Pong";
	}
	
	@Override
	public void onReceive(Entity host) {
		// Tutaj powinien być pomiar czasu i skojarzenie sprawdzenie kiedy wysłano Ping
		int ping =(int)( new Date().getTime() - host.getPingTime() + TanksWorld.PING_TIMEOUT );
		host.getScore().ping = ping;
	}
}
