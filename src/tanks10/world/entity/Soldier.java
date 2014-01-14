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

import java.util.Date;

import tanks10.packets.Hello;
import tanks10.packets.SetAttribute;
import tanks10.world.TanksWorld;
import tanks10.world.Triangle;

public class Soldier extends Box{
	protected double hp = 0;
	protected double maxHp;
	protected double canonRotateSpeed;
	protected double damageMult;
	protected double armor;
	protected long deathTime;
	protected int ammo;
	protected String nextModel = null;
	protected int nextAmmo = -1;
	private int ammoDelay=10;
	private boolean attackState = false;
	private long attackTimeout = 0;


	public Soldier(int nId) {
		id = nId;
		type = TanksWorld.TYPE_SOLDIER;

		this.enabled = false;
		this.solid = false;
		this.vector[0] = -100000;
		this.vector[1] = -100000;
	}
	
	@Override public void setModel(String model) {
		if ("Tiny".equals(model))
			setTiny();
		
		if ("Light".equals(model))
			setLight();

		if ("Massive".equals(model))
			setMassive();
	}
	
	public void setTiny() {
		model = "Tiny";
		size[0] = 20;
		size[1] = 20;
		acc = 0.1;
		maxSpeed = 2;
		stopAcc = 0.02;
		rotateSpeed = 4.0 * Math.PI / 180;	
		
		maxHp = 200;
		canonRotateSpeed = 85;
		damageMult = 0.5;
		armor = 1;
		deathTime = 0;
		
		mesh = new Triangle[] {
				new Triangle( new double[][] {{-11,-7},{11,-7},{-11,7}}),
				new Triangle( new double[][] {{-11, 7}, {11,7}, {11,-7}})
		};
	}	
	
	public void setLight() {
		model = "Light";
		size[0] = 50;
		size[1] = 50;
		acc = 0.05;
		maxSpeed = 1.5;
		stopAcc = 0.01;
		rotateSpeed = 4.0 * Math.PI / 180;	
		
		maxHp = 250;
		canonRotateSpeed = 85;
		damageMult = 0.75;
		armor = 1;
		deathTime = 0;
		
		mesh = new Triangle[] {
				new Triangle( new double[][] {{-9,-13},{9,-13},{-9,11}}),
				new Triangle( new double[][] {{-9, 11}, {9,11}, {9,-13}})
			};
	}
	
	public void setMassive() {
		model = "Massive";
		size[0] = 22;
		size[1] = 22;
		acc = 0.025;
		maxSpeed = 1;
		stopAcc = 0.05;
		rotateSpeed = 4.0 * Math.PI / 180;	
		
		maxHp = 300;
		canonRotateSpeed = 85;
		damageMult = 1;
		armor = 1;
		deathTime = 0;
		
		mesh = new Triangle[] {
				new Triangle( new double[][] {{-12,-13},{12,-13},{-12,13}}),
				new Triangle( new double[][] {{-12, 13}, {12,13}, {12,-13}})
			};
	}	
	
	private void spawnPosition() {
		TanksWorld.getWorld().randomSpawnPoint(this);
		
		immediateUpdate = true;
		TanksWorld.Spawn("SpawnSoldierAnim",vector);
		solid = true;
	}
	
	public void setAmmo(int ammo) {
		switch(ammo) {
		case Bullet.AMMO_9MM: ammoDelay=10; break;
		case Bullet.AMMO_40MM: ammoDelay=15; break;
		case Bullet.AMMO_ROCKET: ammoDelay=40; break;
		case Bullet.AMMO_TESLA: ammoDelay=30; break;
		case Bullet.AMMO_SHELL: ammoDelay=50; break;
		default:
			ammoDelay=1000000; break;
		}
		
		this.ammo = ammo;
	}
	
	public int getAmmo() {
		return ammo;
	}
	
	public void setVector(double[] v) {
		vector = v;
	}	
	
	public double[] getVector() {
		return vector;
	}
	
	@Override
	public void think(long frame) {
		long now = new Date().getTime();
		// zdarzenie respawnu (pojawienie się gracza po raz pierwszy też)
		if (hp <= 0 && now > deathTime) {
			enabled = true;
			solid = true;

			if (nextModel != null) {
				TanksWorld.changeModel(this,nextModel);
				nextModel = null;
			}
			
			if (nextAmmo != -1) {
				TanksWorld.changeAmmo(this,nextAmmo);
				nextAmmo = -1;
			}
			
			hp = 0;
			giveHp(maxHp);	// to wysyła informacje o hp
			spawnPosition();
			
			speed = 0;	// zatrzymaj się i wyłącz przyciski
			this.move(0);
			
		}
		
		if (attackState && frame>attackTimeout) {
			shoot();
			attackTimeout = frame + ammoDelay; 
		}
	}
	
	@Override
	public void hello() {
		// Wyślij klientowi powiadomienie kim jest
		getProxy().send(new Hello(TanksWorld.TYPE_SOLDIER));
	}
	
	@Override public void setAttribute(String name, Object value) {
		if ("model".equals(name)) {	// zmiana modelu
			nextModel = (String)value;
		}
		
		if ("ammo".equals(name)) {
			try {
				int tmp = Integer.parseInt((String)value);
				if ( tmp >=0 && tmp <=4 )
					nextAmmo = tmp;
			}catch(Exception e){}
		}
		
		// samobójstwo
		if ("Kill".equals(name)) {
			if (hp > 0)
				takeHp(hp,this);
		}
		
		super.setAttribute(name, value);
	}
	
	public void shoot() {
		Bullet b;
		int nId = TanksWorld.newWorldId();
		int offset = 0;
		
		switch (ammo) {
			case Bullet.AMMO_9MM : b = new Ammo9mm(-1); break;
			case Bullet.AMMO_40MM : b = new Ammo40mm(-1); break;
			case Bullet.AMMO_ROCKET : b = new AmmoRocket(nId); offset = 15; break;
			case Bullet.AMMO_TESLA : b = new AmmoTesla(nId); offset = 16; break;
			case Bullet.AMMO_SHELL : b = new AmmoShell(nId); offset = 20; break;
			default: 
				return;
		}
		
		b.owner = this;
		
		b.vector[0] = this.vector[0] + this.direction[0] * offset;
		b.vector[1] = this.vector[1] + this.direction[1] * offset;
		
		b.angle = this.angle;
		
		if ((b instanceof Ammo9mm) || (b instanceof Ammo40mm)) {
			int v = (Math.random() > 0.5) ? -1 : 1;
			double dist = Math.random() * Math.PI/20 * v;
			b.angle += dist;
		}

		b.move(0);
		TanksWorld.SpawnBox(b);
		TanksWorld.Spawn(b.fireAnim, id);
	}
	
	@Override
	public void attack(boolean state) {
		if (false == enabled)
			return;
		
		attackState = state;
	}
	
	public void takeHp(double damage, Soldier owner) {
		if (hp == 0)
			return;
		
		hp -= damage;
		
		if (hp <= 0) {
			hp = 0;
			kill();
			if (owner != null && owner != this) {
				owner.getScore().punkty++;
				TanksWorld.say(owner.getName()+" unieszkodliwił "+getName());
			}
			else {
				TanksWorld.say(getName() + " zakończył pancerną przygodę ..");
			}
		}
		getProxy().send(new SetAttribute("hp",new Double(hp)));
		
	}
	
	@Override
	public void kill() {
		deathTime = new Date().getTime()+3000;
		TanksWorld.Spawn("Explode", vector);
		// ustaw poza ekranem
		vector[0] = -100000;
		vector[1] = -100000;
		speed = 0;
		immediateUpdate = true;
		
		solid = false;	// wyłączenie kolizji
		enabled = false;	// wyłącznie kontroli
		attackState = false;	// wyłączenie strzelania
		
		getScore().smierc++;
	}

	public void giveHp(double powerup) {
		hp += powerup;
		getProxy().send(new SetAttribute("hp",new Double(hp)));
	}
	
	@Override
	public void onTouch(Box other) {
	}
	
	@Override
	public void onTrigger(Box other) {
	}
}
