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

/**
 * Najbardziej podstawowy element dynamiczny.
 * @author Macvek
 *
 */
public interface Entity {
	// metody bezpośredniego dostępu do parametrów obiektu (niestandardowe jak np. kolor)
	public void setAttribute(String name, Object value);
	public Object getAttribute(String name);
	
	// identyfikator do skojarzenia dla klienta
	public int getId();
	
	// typ określony przez świat
	public int getType();
	
	// zwraca nazwę modelu / ikonki
	public String getModel();
	public void setModel(String model);	// używane przy tworzeniu soldiera
	
	// obsługa poruszania (wektor w którym oczekujemy ruchu) (o długości zależnej od obiektu)
	// jest to wykorzystywane w obiektach, takich jak pociski, paczki które spadają itp.
	public void moveVector(double x, double y, double z);
	
	// obsługa standardowych akcji
	// kąt rozpoczęcia obracania działem
	public void aim(double x, double y, double z);
	
	// poruszanie się czołgiem
	public void move(int where);
	
	// akcje
	public void attack(boolean state);
	public void reload(int weapon);
	
	// komunikacja
	public void sendMessage(String msg);
	public void setName(String name);
	public String getName();
	
	public void setColor(String color);
	public String getColor();
	
	// kwestie życia i śmierci
	public void kill();
	
	// kwestie sieciowe i kontroli
	public boolean humanControl();	// zwraca True jeżeli jest kontrolowany przez gracza
	public void transferTo(Entity newControl);	// przekazanie właściwości kontrolnych innemu Entity
	public void setProxy(SendProxy in);
	public SendProxy getProxy();
	public void hello();	// wysyła pakiet informujący jakim Entity się jest
	public boolean freshMeat();	// czy to jest świeży entity
	public void noFreshMeat();	// czy to jest świeży entity
	
	public Long getPingTime();	// do magazynowania czasu ostatniego wysłania pinga
	public void setPingTime(long nPing);
	
	public ScoreBoard getScore();
	
	public void think(long frame);	// działanie tej postaci
	public boolean removeMe();	// czy usunąć ten entity
	
}
