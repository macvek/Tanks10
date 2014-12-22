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

/**
 * Przemieszczaj entity o podany wektor
 * @author Macvek
 *
 */
public class Move implements Packet{
	
	private int where;

	@Override public Map<String, Object> getPacketFields() {return null;}
	@Override public String getPacketName() {
		return "Move";
	}

	@Override public void onReceive(Entity host) {
		
		host.move(where);

	}

	@Override
	public boolean setPacketFields(Map<String, Object> in) {
		try {
			where = Integer.parseInt((String)in.get("where"));
			
			return true;
		}catch(Exception e) {
			return false;
		}
	}

}
