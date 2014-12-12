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

import java.util.HashSet;

import tanks10.packets.Hello;
import tanks10.world.Entity;
import tanks10.world.PlayerBase;
import tanks10.world.TanksWorld;

/* Entity jakie jest przyporządkowywane na początku 
 * Prowadzi użytkownika przez utworzenie imienia, wybór klasy itp.
 */

public class Newbie extends PlayerBase {
	Integer playerType;
	static private HashSet<String> models = new HashSet<String>();
	
	static{
		models.add("Tiny");
		models.add("Light");
		models.add("Massive");
	}
	
	public Newbie() {
		type=TanksWorld.TYPE_NEWBIE;
	}
	
	@Override
	public void hello() {
		// Wyślij klientowi powiadomienie kim jest
		getProxy().send(new Hello(TanksWorld.TYPE_NEWBIE));
	}
	
	private String playername = null;
	private String model = null;
	private String color = null;
	
	@Override 
	public void setAttribute(String name, Object value) {
		if ("model".equals(name)) {
			model = (String)value;
			if (! models.contains(model)) {
				model = null;
				return;
			}
			return;
		}
		
		if ("color".equals(name)) {
			color = (String)value;
			return;
		}
		
		if ("name".equals(name)) {
			playername = (String)value;
			return;
		}
		
		// Przekazanie kontroli
		if ("type".equals(name)) {
			Integer val;
			
			try {
				val = new Integer((String)value);
			}catch(Exception e) {
				return;
			}
			
			Entity newone;

			switch(val) {
				case TanksWorld.TYPE_GOD:
					// Utwórz Entity admina i przekaż kontrolę
					newone = TanksWorld.newGod();
					break;
					
				case TanksWorld.TYPE_SPECTATOR:
					// Utwórz Entity obserwatora i przekaż mu kontrolę
					newone = TanksWorld.newSpectator();
					break;
			 
				case TanksWorld.TYPE_SOLDIER:
					if (model == null)
						return;
					
					if (color == null)
						return;
					
					// Utwórz zwykłego gracza
					newone = TanksWorld.newSoldier(model);	 //to wysyła ddEntity
					// Przekazanie kontroli nad nowym entity
					this.transferTo(newone);
					newone.setColor(color);
					
					if (playername != null)
						TanksWorld.changeName(newone, playername);
					
					newone.hello();
					TanksWorld.removeEntity(this);
					return;
					
				default:
					return;
			}
			if ( null == newone ) {
				return;
			}
			
			this.transferTo(newone);
			newone.hello();
			TanksWorld.removeEntity(this);
		}
	}
	
	@Override
	public Object getAttribute(String name) {
		return null;
	}
}
