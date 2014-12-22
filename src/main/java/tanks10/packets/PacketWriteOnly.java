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

import java.util.HashMap;
import java.util.Map;

import tanks10.world.Entity;

abstract public class PacketWriteOnly implements Packet {

	protected HashMap<String,Object> fields = new HashMap<String,Object>();
	
	@Override
	public Map<String, Object> getPacketFields() {
		return fields;
	}

	@Override public void onReceive(Entity host) {}
	@Override public boolean setPacketFields(Map<String, Object> in) {return false;}

}
