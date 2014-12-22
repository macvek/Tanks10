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

import tanks10.world.TanksWorld;
import tanks10.world.Triangle;

public class Bullet extends Box{
	
	static final public int AMMO_9MM = 0;
	static final public int AMMO_40MM = 1;
	static final public int AMMO_ROCKET = 2;
	static final public int AMMO_TESLA = 3;
	static final public int AMMO_SHELL = 4;
	
	protected double damage;
	protected double distance;
	protected double range;
	protected double explodeRange;
	protected String explodeAnim;
	protected String fireAnim;
	public Soldier owner = null;
	
	{
		mesh = new Triangle[] {
				new Triangle( new double[][] {{-3,0},{3,0},{0,6}}),
			};
	}
	
	@Override 
	public void think(long frame) {
		distance += speed;
		if (distance > range) {
			readyToRemove = true;
			if ((this instanceof AmmoRocket) || (this instanceof AmmoTesla) || (this instanceof AmmoShell)) { 
				TanksWorld.spawn(explodeAnim, vector);
				TanksWorld.getWorld().checkExplode(vector, explodeRange, damage * owner.damageMult, owner);
			}
		}	
	}

}
