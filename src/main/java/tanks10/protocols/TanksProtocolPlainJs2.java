/*
 This file is part of Tanks10 Project (https://github.com/macvek/Tanks10).

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import tanks10.mocked.JSException;
import tanks10.mocked.JSObject;

/**
 * Realizacja protokołu po stronie apletu. (wersja druga, która nie korzysta z messageBody)
 *
 * @author Macvek
 *
 */
public class TanksProtocolPlainJs2 extends TanksProtocolPlain {

    public JSObject listener;
    public JSObject messageBody;

    @Override
    /**
     * W przypadku przeładowania bufora, informacja do skryptu o zdarzeniu resetu
     */
    public void reset() {
        listener.call("onMessageReset", null);
    }

    private ArrayList<String> arguments = new ArrayList<String>();

    @Override
    protected void decode(String name, HashMap<String, Object> field) {
        arguments.add(name);	// pierwszy argument to nazwa pakietu
        for (Map.Entry<String, Object> pair : field.entrySet()) {
            arguments.add(pair.getKey());	// następnie nazwa pola
            arguments.add(pair.getValue().toString());	// i wartość pola
        }

        arguments.add("\n");	// po zakończeniu pakietu dodajemy znacznik końca
    }

    @Override
    protected String encode(Object in, HashMap<String, Object> field) {
        JSObject src = (JSObject) in, messageParams, messageHead;
        String messageName, fieldName;
        Object fieldValue;

        /*
         * Pakiet kodowany w javascript ma pola messageName i messageParams
         */
        try {
            messageParams = (JSObject) src.getMember("messageParams");
            messageHead = (JSObject) src.getMember("messageHead");
            messageName = (String) src.getMember("messageName");
        } catch (JSException e) {
		// jeżeli nie ma wymaganego pola to ustaw błąd

            return UNSUPPORTED;
        }
        // wyczyść pola, żeby nie zostało nic po poprzednich obiektach
        field.clear();

        // Przeszukaj obiekt JS i ustaw odpowiednie wartości dla elementu przejściowego
        int i = 0;
        do {
            try {
                fieldName = (String) messageHead.getSlot(i);
                i++;
            } catch (JSException e) {
                // Nie znaleziono więcej pól
                break;
            }

            try {
                fieldValue = (String) messageParams.getMember(fieldName);
            } catch (JSException e) {
                // Pole zadeklarowane w informacji o pakiecie, ale nie ma go w obiekcie
                continue;
            }

            // ustaw wartość tego pola
            field.put(fieldName, fieldValue);

        } while (true);

        // zwróć nazwę pakietu
        return messageName;
    }

    // Nie używane tutaj, bo rozłączenie jest obsługiwane przez ConnectionManager
    @Override
    public void connectionLost() {
    }

    @Override
    protected void decodeEnd() {
        // zakończono odczytywanie grupy wiadomości, więc wywołaj zapytanie w JS

        try {
            listener.call("onMessage", arguments.toArray());
        } catch (Exception e) {
        }
        arguments.clear();
    }
}
