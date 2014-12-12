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

import tanks10.world.Triangle;

// Obiekt który służy jako przeszkoda
public class StaticBlock extends Box{
	public boolean simulate() {return false;}
	public boolean needsUpdate(long frame) {return false;}
	public StaticBlock(){}
	public StaticBlock(double x,double y, double sX, double sY) {
		vector[0] = x;
		vector[1] = y;
		size[0] = sX;
		size[1] = sY;
		model = "void";
		id = -1;
		mesh = new Triangle[] {
			new Triangle(new double[][] {{-sX, -sY}, {sX, -sY}, {-sX, sY}}),
			new Triangle(new double[][] {{-sX, sY }, {sX, sY }, {sX, -sY}})
		};
	}
	
	@Override
	public void onTrigger(Box other) {
		if (other instanceof Soldier)
			( (Soldier)other).takeHp(1,null);	// uderzanie o ścianę troszkę boli
	}
}
