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

import tanks10.world.PlayerBase;
import tanks10.world.Triangle;

/**
 * Entity, które obsługuje kolizje
 */
public class Box extends PlayerBase{
	static final public int FRONT = 1 << 0;
	static final public int BACK = 1 << 1;
	static final public int RIGHT = 1 << 2;
	static final public int LEFT = 1 << 3;
	
	protected double[] vector = new double[2];	// x,y
	protected double[] size = new double[2];	// wielkość boundingbox
	protected double[] direction = new double[]{ 0, 1 };	// wektor o długości 1
	protected Triangle[] mesh;	// grupa obiektów tworzących trójkąty
	protected double speed = 0;
	protected double acc;
	protected double rotateSpeed;
	protected double maxSpeed;
	protected double angle = 0;	// RADIANY a NIE stopnie !!! :)
	protected double stopAcc;
	private long frame = 0;
	protected long framesPerRefresh = 1000000;
	protected boolean immediateUpdate = true;	// pierwszy update
	protected boolean solid = true;		// reakcja na kolizje
	protected int id = 0;
	protected boolean force = true;
	protected boolean readyToRemove = false;
	protected boolean enabled = true;	// czy może reagować na input
	
	protected int change_speed = 0;	//1 ruch do przodu, -1 ruch do tylu, 0 - stop
	protected int change_angle = 0;	//1 w prawo, -1 lewo
	protected int flags = 0;
	
	@Override
	public boolean removeMe() {
		return readyToRemove;
	}
	
	@Override
	public int getId() {
		return id;
	}
	
	
	@Override
	public void move(int where) {
		if (false == enabled)
			return;
		
		int x=0, y=0;
		
		if ( (where & FRONT) != 0) y=1;
		if ( (where & BACK) != 0) y=-1;
		if ( (where & RIGHT) != 0) x=1;
		if ( (where & LEFT) != 0) x=-1;
		
		this.change_angle = x;
		this.change_speed = y;
		this.flags = where;
		
		immediateUpdate=true;
	}
	
	
	public void moveVector(double x, double y, double z) {
		direction[0] = x;
		direction[1] = y;

		speed = length(direction);
		normalize(direction);
		
		immediateUpdate=true;
	}
	
	// Ustala co ile klatek ma być wysyłany update tego elementu (bez względu na zmiany)
	public boolean needsUpdate(long frame) {
		if (immediateUpdate || frame - this.frame >= framesPerRefresh) {
			this.frame = frame;
			immediateUpdate = false;
			return true;
		}
		
		return false;
	}
	
	public double[] getUpdate() {
		return new double[]{vector[0],vector[1],speed,angle,flags};
	}
	
	private double length( double[] v ) {
		return Math.sqrt( v[0]*v[0] + v[1]*v[1] );
	}
	
	private void normalize( double[] v ) {
		double len = length(v);
		if (len == 0)
			return;
		
		for (int i=0;i<2;i++)
			v[i] /= len;
	}
	
	// Prototyp obsługi kolizji, narazie zderzenie to cofniecie ruchu i stop
	public void stop() {
		speed = 0;
		immediateUpdate = true;
	}
	
	// True oznacza, że wykonano ruch
	public boolean simulate() {

		if (change_angle != 0 || force) {
			
			angle += change_angle * rotateSpeed;
			
			// dla wektora kierunkowego [0,1]
			direction[0] = -Math.sin(angle);
			direction[1] = Math.cos(angle);
			
			if (angle > Math.PI)
				angle -= Math.PI*2;
			
			if (angle < -Math.PI)
				angle += Math.PI*2;
			
		}	
			
		if (change_speed != 0 || force) {

			speed += change_speed * acc;
			
			if (speed > maxSpeed)
				speed = maxSpeed;
			
			if (speed < -maxSpeed) 
				speed = -maxSpeed;
			
		}else {
			if (speed != 0) {
				
				if (Math.abs(speed) <= stopAcc * 1.5) {
					speed = 0;
					immediateUpdate=true;
				}
				else if (speed < 0)
					speed += stopAcc;
				else {
					speed -= stopAcc;
				}
			
			}
		}
		
		vector[0] += direction[0] * speed;
		vector[1] += direction[1] * speed;
		
		force = false;
		
		return true;
	}
	
	// ja dotknąłem other
	public void onTouch(Box other) {}	
	
	// other dotknął mnie
	public void onTrigger(Box other) {}	
	
	public static void collision(Box self, Box other){
		if ( !(self.solid && other.solid) )
			return;

		self.force = true;
		self.immediateUpdate = true;

		double random = (Math.random() * 5 - 2.5)/ Math.PI;
		self.speed *= 2.0;
		self.angle += Math.PI + random;
		self.simulate();
	}
	
	public static boolean boundingTest(Box self, Box other){	// sprawdza czy koliduje z innym obiektem
		// Sprawdzamy odległości na osiach
		
			double distanceX = Math.abs(self.vector[0] - other.vector[0]);
			double lengthX = self.size[0] + other.size[0];
			
			double distanceY = Math.abs(self.vector[1] - other.vector[1]);
			double lengthY = self.size[1] + other.size[1];
	
			boolean kolizjaX = (lengthX <= distanceX) ? false : true;
			boolean kolizjaY = (lengthY <= distanceY) ? false : true;

			// Jeżeli odległośc między punktami jest większa niż odcinek między nimi to znaczy, że się nie stykają
			if (kolizjaX && kolizjaY) {
				// sprawdzenie kolizji poprzez trójkąty
				
				// jeden z obiektów nie posiada siatki więc zakończ tutaj sprawdzanie
				if (self.mesh == null || other.mesh == null) { 
					return true;
				}
				
				return Triangle.meshTest(self.mesh, self.vector, self.angle,
						other.mesh, other.vector, other.angle);
				
			}
			return false;
	}
	
}
