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

import java.util.Map;

import tanks10.world.Entity;

// Pobierz wartość jakiejś zmiennej. Działa tylko do serwera, odpowiedzią jest Attribute
public class GetAttribute implements Packet{
	private String name;
	@Override public String getPacketName() { return "GetAttribute"; }
	@Override public void onReceive(Entity host) {
		Object value = host.getAttribute(name);

		// Odpowiedzią na nieznany argument jest pusty string
		if ( null == value )
			value = "";
		
		// Pobierz wartość i odpowiedź
		host.getProxy().send(new Attribute(name,value.toString()));
	}
	
	@Override 
	public boolean setPacketFields(Map<String, Object> in) {
		name = (String)in.get("name");
		if (null == name)
			return false;
		
		return true;
	}

	@Override public Map<String, Object> getPacketFields() {return null;}
	
}