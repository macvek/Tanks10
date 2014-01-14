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
package tanks10.protocols;

import java.util.HashMap;
import java.util.Map;



import netscape.javascript.*;
/**
 * Realizacja protokołu po stronie apletu.
 * @author Macvek
 *
 */
public class TanksProtocolPlainJs extends TanksProtocolPlain {
	public JSObject listener;
	public JSObject messageBody;

	
	@Override
	/**
	 * W przypadku przeładowania bufora, informacja do skryptu o zdarzeniu resetu
	 */
	public void reset() {
		listener.call("onMessageReset",null);
	}
	
	@Override
	protected void decode(String name, HashMap<String, Object> field) {
		// pobierz obiekt, do którego będą zapisywane odczytane dane
	
		/* iteracja po ustawionych obiektach i przypisanie ich odpowiednikom
		 * w JS
		 */
		for(Map.Entry<String,Object> pair : field.entrySet()) {
			messageBody.setMember(pair.getKey(), pair.getValue());
		}
		
		// Wywołaj zdarzenie w JS z parametrem nazwy pakietu który przyszedł
		listener.call("onMessage", new String[]{name});
	}

	@Override
	protected String encode(Object in, HashMap<String, Object> field) {
		JSObject src = (JSObject)in, messageParams,messageHead;
		String messageName,fieldName;
		Object fieldValue;
		
		/*
		 * Pakiet kodowany w javascript ma pola messageName i messageParams
		 */
		
		try {
			messageParams = (JSObject)src.getMember("messageParams");
			messageHead = (JSObject)src.getMember("messageHead");
			messageName = (String)src.getMember("messageName");
		}catch(JSException e)
		{
		// jeżeli nie ma wymaganego pola to ustaw błąd
		
			return UNSUPPORTED;
		}
		// wyczyść pola, żeby nie zostało nic po poprzednich obiektach
		field.clear();
		
		// Przeszukaj obiekt JS i ustaw odpowiednie wartości dla elementu przejściowego
		int i=0;
		do {
			try {
				fieldName = (String)messageHead.getSlot(i);
				i++;
			}catch(JSException e) {
				// Nie znaleziono więcej pól
				break;
			}
			

			try {
				fieldValue = (String)messageParams.getMember(fieldName);
			}catch(JSException e) {
				// Pole zadeklarowane w informacji o pakiecie, ale nie ma go w obiekcie
				continue;
			}
			
			// ustaw wartość tego pola
			field.put(fieldName, fieldValue);
		
		}while(true);
		
		// zwróć nazwę pakietu
		return messageName;
	}

	// Nie używane tutaj, bo rozłączenie jest obsługiwane przez ConnectionManager
	@Override
	public void connectionLost() {}

	@Override
	protected void decodeEnd() {
	// odczytano grupę wiadomości, więc można je wysłać do przeglądarki
	}


}
