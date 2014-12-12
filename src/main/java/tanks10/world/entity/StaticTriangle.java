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

public class StaticTriangle extends StaticBlock{
	
	public boolean simulate() {return false;}
	public boolean needsUpdate(long frame) {return false;}
	public StaticTriangle(double[] a, double[] b, double[] c) {
		
		double minX,maxX,minY,maxY;
		minX = getMinX(a[0],b[0],c[0]);
		maxX = getMaxX(a[0],b[0],c[0]);
		minY = getMinY(a[1],b[1],c[1]);
		maxY = getMaxY(a[1],b[1],c[1]);

		vector[0] = 0.5 * (maxX + minX);
		vector[1] = 0.5 * (maxY + minY);
		
		size[0] = maxX - vector[0];
		size[1] = maxY - vector[1];		
		
		id = -1;
		
		solid = true;
		
		model = "void";
		
		a[0] -= vector[0];
		b[0] -= vector[0];
		c[0] -= vector[0];
		a[1] -= vector[1];
		b[1] -= vector[1];
		c[1] -= vector[1];
		
		mesh = new Triangle[] {
				new Triangle(new double[][] {a, b, c})
		};
		
	}
	
	public double getMinX(double a, double b, double c) {
		double minX = a;	
		minX = Math.min(minX, b);
		minX = Math.min(minX, c);
		return minX;
	}
	public double getMinY(double a, double b, double c) {
		double minY = a;	
		minY = Math.min(minY, b);
		minY = Math.min(minY, c);
		return minY;
	}
	public double getMaxX(double a, double b, double c) {
		double maxX = a;	
		maxX = Math.max(maxX, b);
		maxX = Math.max(maxX, c);
		return maxX;
	}
	public double getMaxY(double a, double b, double c) {
		double maxY = a;	
		maxY = Math.max(maxY, b);
		maxY = Math.max(maxY, c);
		return maxY;
	}
	
	

}
