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
package tanks10.world.entity;

import tanks10.packets.Hello;
import tanks10.world.PlayerBase;
import tanks10.world.TanksWorld;

public class Spectator extends PlayerBase{
	public Spectator(int nId) { this.id = nId; this.type = TanksWorld.TYPE_SPECTATOR; }

	@Override public void hello() {
		getProxy().send(new Hello(TanksWorld.TYPE_SPECTATOR));
	};
}

