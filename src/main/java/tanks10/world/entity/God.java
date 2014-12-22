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
package tanks10.world.entity;

import tanks10.packets.Hello;
import tanks10.world.PlayerBase;
import tanks10.world.TanksWorld;

/* God jest to entity które jest adminem systemu.
 * Żeby było przypisane do połaczenia
 * Wszystkie operacje są wykonywane przez setAttribute i getAttribute
 */
public class God extends PlayerBase{
	public God(int nId) {
		this.id = nId;
		type = TanksWorld.TYPE_GOD;
	}
	
	@Override public void kill() {TanksWorld.removeEntity(this);}
	@Override public boolean humanControl() { return true;}
	
	@Override public void hello() {
		getProxy().send(new Hello(TanksWorld.TYPE_GOD));
	};
	
	@Override 
	public void setAttribute(String name, Object value) {
		if ("KillServer".equals(name)) {
			TanksWorld.end();
			System.out.println("Wysłano komunikat: KillServer > "+value);
			System.out.println("Wyłączanie...");
		}
	}
	
	@Override
	public Object getAttribute(String name) {
		return null;
	}
}
