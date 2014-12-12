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
package tanks10.packets;

import java.util.HashMap;
import java.util.Map;

import tanks10.world.Entity;

/**
 * Ten pakiet jest wykorzystywany jako odpowiedź do klienta na zapytanie GetAttribute. 
 * Różni się w działaniu od SetAttribute brakiem wymuszenia ustawienia wartości.
 */
public class Attribute implements Packet {
	protected String name;
	protected String value;
	
	public Attribute(){};
	public Attribute(String name, String value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public Map<String, Object> getPacketFields() {
		HashMap<String,Object> fields = new HashMap<String,Object>();
		fields.put("name", name);
		fields.put("value", value);
		
		return fields;
	}
	
	@Override public void onReceive(Entity host) {}
	@Override public String getPacketName() { return "Attribute"; }
	
	@Override 
	public boolean setPacketFields(Map<String, Object> in) {
		try {
			name = (String)in.get("name");
			value = (String)in.get("value");

			if ( null == name || null == value )
				return false;
			
		}catch(Exception e){
			return false;
		}
		
		return true;
	}
}
