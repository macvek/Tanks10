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
package tanks10;

import java.util.*;
import tanks10.mocked.JSObject;

/**
 * @author Macvek
 * 
 * Klasa debugerska, do zapamiętania komunikatów wysyłanych przez applet
 * i gdy zostanie do niej podłączony obiekt wyjściowy, to wysyła do niego
 * informacje. W tym przypadku wyjściem będzie obiekt javascriptu
 * w przeglądarce
 */
public class Logger {
	private ArrayList<String> buffer = new ArrayList<String>();
	private JSObject console;
	
	/**
	 * Wywoływane po dołączeniu konsoli.
	 * Wysyła całą zawartość bufora do konsoli
	 */
	private void flushBuffer() {
		for(String tmp : this.buffer) {
			writeToConsole(tmp);
		}
	}
	
	private void writeToConsole(String in) {
		// Wywoływane zawsze z ustawioną konsolą
		console.call("write", new String[]{in});
	}
	
	/**
	 * Dodanie konsoli i wysłanie jej zawartości bufora.
	 * Konsola to obiekt JS z zaimplementowaną metodą write()
	 * @param inConsole
	 */
	public void attachConsole(JSObject inConsole) {
		if (inConsole == null)
			return;
		console = inConsole;
		flushBuffer();
	}
	
	/**
	 * Odłączenie konsoli
	 */
	public void detachConsole() {
		console = null;
	}
	
	/**
	 * Dodanie tekstu, zostanie wysłany do konsoli lub zapamiętany
	 * w buforze
	 * @param in
	 */
	public void log(String in) {
		
		// brak konsoli, dodaj wpis do bufora
		if (console == null) {
			buffer.add(in);
		}
		// jest podłączona konsola, więc wywołaj jej metodę write()
		else {
			writeToConsole(in);
		}
		
	}
}
