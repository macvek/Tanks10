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

/**
 * Interface pojedynczego pakietu.
 * @author Macvek
 *
 */
public interface Packet {
	/**
	 * Pobierz nazwę pakietu, jest to potrzebne do przyporządkowania klasy do nazwy
	 */
	public String getPacketName();

	/**
	 * Zwróć mapę nazwa=>obiekt które są wykorzystywane przy budowie pakietu do wysłania 
	 */
	public Map<String,Object> getPacketFields();
	
	/**
	 * Wywoływane przy odbieraniu pakietu od klienta.
	 * In jest to zbudowana przez protokół mapa nazwa=>obiekt.
	 * Ta metoda powinna sprawdzić poprawność otrzymanych danych i jeżeli uzna, że są poprawne
	 * zwrócić true 
	 */
	public boolean setPacketFields(Map<String,Object> in);	// zwraca czy podane pola się zgadzają

	/**
	 * Jeżeli setPacketFields zwróci true, to po otrzymaniu pakietu jest wywoływana ta metoda.
	 * Entity host : obiekt do którego wysłano komunikat
	 */
	public void onReceive(Entity host);
}
