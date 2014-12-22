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

import tanks10.world.entity.Box;

public class Update extends PacketWriteOnly {
	@Override public String getPacketName() { return "Update"; }
	public Update(Box box) {
		double[] update = box.getUpdate();
		
		fields.put("id", new Integer(box.getId()));
		fields.put("x", new Double(update[0]));
		fields.put("y", new Double(update[1]));
		fields.put("s", new Double(update[2]));
		fields.put("a", new Double(update[3]* 180/Math.PI));
		fields.put("f", new Integer ( (int) update[4]));
	}
}
