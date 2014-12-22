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
package tanks10.packets;
import java.util.Map;

import tanks10.world.Entity;
import tanks10.world.ScoreBoard;
import tanks10.world.TanksWorld;

/**
 * Odpowiedź na Ping. Powinna zawsze zwracać tą samą wiadomość, ale tego nie sprawdzamy
 */
public class RekordyWynikow implements Packet {

	@Override
	public Map<String, Object> getPacketFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPacketName() {
		// TODO Auto-generated method stub
		return "RekordyWynikow";
	}

	@Override
	public void onReceive(Entity host) {
		ScoreBoard[] tablica = TanksWorld.getScoreBoard();

		for ( ScoreBoard x: tablica)
		{
			//System.out.println(x.imie);
			host.getProxy().send(new TabelaWynikow(x.imie,x.punkty, x.smierc, x.ping));
			
		}
		host.getProxy().send(new TabelaWynikowKoniec());
	}

	@Override
	public boolean setPacketFields(Map<String, Object> in) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	
}
