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

public class Spawn extends PacketWriteOnly{
	
	public Spawn(){}
	public Spawn(String model, double[] vector){
		fields.put("model", model);
		fields.put("x", vector[0]);
		fields.put("y", vector[1]);
		fields.put("id", -1);
	}
	
	public Spawn(String model, int id){
		this(model, new double[]{0,0});
		fields.put("model", model);
		fields.put("id", id);
	}	

	
	@Override
	public String getPacketName() {
		return "Spawn";
	}

}
