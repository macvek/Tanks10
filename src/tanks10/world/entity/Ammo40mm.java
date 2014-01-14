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

import tanks10.world.TanksWorld;

public class Ammo40mm extends Bullet{
	
	public Ammo40mm(int nId) {
		this.id = nId;
		this.type = TanksWorld.TYPE_BOX;
		this.model = "40mm";
		this.solid = false;
		this.size[0] = 1;
		this.size[1] = 1;
		this.acc = 0;
		this.speed = 8;
		this.maxSpeed = 5;
		this.damage = 20;
		this.distance = 0;
		this.range = 300;
		this.explodeAnim = "Explode40mm";
		this.fireAnim = "Fire40mm";
	}
	
	@Override
	public void onTouch(Box other) {
		if (other instanceof Bullet)
			return;
		
		if (other instanceof Soldier) {
			Soldier s = (Soldier) other;
			
			if (owner == s)
				return;
			
			s.takeHp(damage * owner.damageMult, owner);
		}
		
		readyToRemove = true;
		TanksWorld.Spawn(explodeAnim, vector);
	}		
}
