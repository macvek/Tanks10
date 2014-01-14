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
package tanks10.world;

import tanks10.SendProxy;
import tanks10.protocols.TanksProtocolTiger;

/**
 * Klasa podstawowa, po której powinny dziedziczyć klasy sterowane przez gracza.
 * Obecnie także Box po tym dziedziczy.
 * @author Macvek
 *
 */
public class PlayerBase implements Entity{
	private SendProxy proxy;
	private String name = "FreshSoldier"+Math.floor(Math.random()*100);
	protected String model = "";
	protected String color = "czerwony";
	protected int type=-1;
	protected int id=0;
	protected long ping=0;
	private ScoreBoard scoreBoard = new ScoreBoard();
	
	@Override public Long getPingTime() { return ping; }
	@Override public void setPingTime(long nPing) {ping = nPing;} 
	@Override public void aim(double x, double y, double z) {}
	@Override public void attack(boolean state) {}
	
	@Override public String getModel() {return model; }
	@Override public void setModel(String model) {}
	@Override public void kill() {}
	@Override public void move(int where) {}
	@Override public void moveVector(double x, double y, double z) {}
	@Override public void reload(int weapon) {}
 	@Override public void sendMessage(String msg) {
 		TanksWorld.say(this,msg);
 	}
	
 	@Override public void setColor(String color) {this.color = color;}
 	@Override public String getColor(){ return color;};
 	
 	@Override public void setName(String name) {this.name = name;}
	@Override public String getName(){ return name;};
	
	@Override public void hello() {}
	
	@Override public boolean humanControl() {
		if ( null == this.proxy )
			return false;
		
		return true;
	}
	
	@Override public void transferTo(Entity newControl) {
		// Nie ma do czego się odnosić (tak jest jak nie uzyska się dostępu do danego Entity
		if (null == newControl)
			return;
	
		// Przekazanie obsługi do innego Entity
		SendProxy proxy = this.getProxy();

		// Ustawienie nowego Proxy i ustawienie protokołu 
		newControl.setProxy(proxy);
		((TanksProtocolTiger)proxy.getProtocol()).setEntity(newControl);
		
		// Usunięcie dowiązania do proxy
		this.setProxy(null);
	}
	
	// Praktycznie to nigdy nie będzie modyfikowane
	@Override public SendProxy getProxy() {return proxy;}
	@Override public void setProxy(SendProxy in) {proxy = in;}
	@Override public int getId() {return id;}
	@Override public int getType() {return type;}
	
	private boolean fresh = true;
	@Override public boolean freshMeat() { return fresh; }
	@Override public void noFreshMeat() { fresh = false; }
	
	@Override public ScoreBoard getScore() { return scoreBoard; }
	
	@Override public void setAttribute(String name, Object value) {
		if ("name".equals(name)) {
			TanksWorld.changeName(this, value.toString());
			return;
		}
		
		if ("color".equals(name)) {
			TanksWorld.changeColor(this, value.toString());
			return;
		}
	}
	
	@Override public Object getAttribute(String name) {
		if ("name".equals(name))
			return getName();
		
		return null;
	}
	@Override public void think(long frame) {}
	@Override public boolean removeMe() {return false;}
}

