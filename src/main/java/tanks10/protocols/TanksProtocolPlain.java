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

import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.*;
import java.nio.charset.*;


/**
 * Protokół przygotowujący dane do wysłania w formacie:
 * NazwaWiadomości:NazwaPola1=WartośćPola1,NazwaPola2=WartośćPola2;
 * 
 * Klasa musi być rozszerzona o metody kodujące/dekodujące obiekty
 * pakietu na pola protokołu.
 * Klasy które rozszerzają tę klasę prawdopodobnie będą miały własne
 * pola do komunikowania się z resztą systemu.
 * @author Macvek
 *
 */

abstract public class TanksProtocolPlain extends TanksProtocol{
	// ustalenie trybu kodowania znaków (US-ASCII bo każdy bajt zajmuje tyle samo)
	final static private Charset coder = Charset.availableCharsets().get("ISO-8859-2");
	final static char END_OF_PACKET=';';
	private HashMap<String,Object> readFields = new HashMap<String,Object>();
	
	// odczytaj pakiet
	private void parsePacket(String in) {
		String[] parts;
		String[] fields;
		String name;
		readFields.clear();
		
		// pierwsza część to nazwa wiadomości, druga to pola
		parts = in.split(":",2);
		
		// mniej niż jeden dwókropek
		if (parts.length != 2)
			return;
		
		// brak nazwy
		if (parts[0].length() == 0)
			return;
		
		// ustaw nazwę pakietu
		name = parts[0].trim();

		// wysublimuj wszystkie atrybuty
		fields = parts[1].split(",");
		for(String attr : fields) {
			String[] attrParts;
			attrParts = attr.split("=");
			
			// błędnie przesłane, więc omiń
			if (attrParts.length != 2)
				continue;
			
			if (attrParts[0].length() == 0 || attrParts[1].length() == 0)
				continue;
			
			// ustaw wartośći pól
			readFields.put(attrParts[0], attrParts[1]);
		}

		decode(name,readFields);
	}
	
	// Ustawia pozycję w miejscu gdzie jest ostatni znak
	private boolean findNextEnd(CharBuffer in) {
		while(in.remaining() > 0) {
			if (in.get() == END_OF_PACKET)
				return true;
		}
		
		return false;
		
	}
	
	@Override
	public void receive(ByteBuffer in) {
		int cursor=0,offset=0;
		
		// Sprowadz kod do ustalonego kodowania
		cursor = in.position();
		CharBuffer input = coder.decode(in);
		
		in.position(cursor);
		
		while(input.remaining() > 0) {
			cursor = input.position();
			
			// obecny pakiet jest niepełny
			if (! findNextEnd(input) )
				break;

			offset = input.position() - cursor;
			
			// wywołuje decode
			input.position(cursor);
			parsePacket(input.subSequence(0, offset - 1).toString());
			input.position(cursor+offset);
		}
		//zasygnalizowanie zakończenia odczytywania grupy pakietów (używane przez JS)
		decodeEnd();
		
		//przesunięcie bufora o odczytane bajty
		in.position( in.position() + cursor );
	}
	
	/**
	 * Buduje binarną interpretację pakietu
	 * @param out Bufor w którym ma umieścić dane
	 * @param in Natywny obiekt pakietu rozumiany przez encode
	 * @return True jeżeli udało się zrozumieć wiadomość
	 */
	@Override
	public boolean send(ByteBuffer out,Object in) {
		HashMap<String,Object> sendFields = new HashMap<String,Object>();
		String name = encode(in,sendFields);	// zamień pakiet na formę przejściową
		
		// Jeżeli podane dane nie są możliwe do zakodowania, to zakończ porażką
		if (name == UNSUPPORTED)
			return false;
		
		// Zakoduj dane do przesłania przez sieć
		
		// podobnie jak w receive, czytelnie a nie wydajnie
		
		//określenie nazwy pola
		StringBuilder packet = new StringBuilder();
		packet.append(name);
		packet.append(":");

		// do zliczania, dla przecinka
		int i=0, limit=sendFields.size();

		//prześledź wszystkie wpisy w field i dodaj je do pakietu
		for(Map.Entry<String,Object> pair : sendFields.entrySet()) {
			i++;
			Object value = pair.getValue();
			
			// optymalizacja wysyłania wartości zmiennoprzecinkowych
			if (value instanceof Double) {
				Double d = (Double)value;
				
				// zamiana 0.0 na 0
				if (d == 0) {
					value = "0";
				}
				// ograniczenie liczby miejsc po przecinku
				else {
					value = String.format("%.4f", d).replace(",", ".");
					// w zależności od ustawień systemu, jest budowany przecinek zamiast kropki
				}
			}
			
			packet.append(pair.getKey() + "=" + value);
			if (limit != i)
				packet.append(",");
		}
		
		// dodanie znaku końca pakietu
		
		packet.append(END_OF_PACKET);
		
		// dodanie znaku końca linii dla czytelności w konsoli
		packet.append("\r\n");

		// dodanie pakietu do bufora
		try {
			out.put(coder.encode(packet.toString()));
		}catch(BufferOverflowException e) {
			return false;
		}
		
		return true;
	}
	

}
