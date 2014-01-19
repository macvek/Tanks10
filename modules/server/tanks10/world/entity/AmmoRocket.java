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

public class AmmoRocket extends Bullet{
	
	public AmmoRocket(int nId){
		this.id = nId;
		this.type = TanksWorld.TYPE_BOX;
		this.model = "Rocket";
		this.solid = false;
		this.size[0] = 2;
		this.size[1] = 2;
		this.acc = 0.01;
		this.speed = 4;
		this.maxSpeed = 10;
		this.damage = 40;
		this.distance = 0;
		this.range = 300;
		this.explodeRange = 100;
		this.explodeAnim = "ExplodeRocket";
		this.fireAnim = "FireRocket";
	}
	
	@Override
	public void onTouch(Box other) {
		if (other instanceof Bullet)
			return;
		
		TanksWorld.getWorld().checkExplode(vector, explodeRange, damage * owner.damageMult, owner);
		
		readyToRemove = true;
		TanksWorld.spawn(explodeAnim, vector);
	}

}
