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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import tanks10.protocols.TanksProtocol;

/**
 * Klasa zarządzająca wysyłaniem i odbieraniem wiadomości.
 * Dziedziczą po niej:
 * <ul>
 * 	<li>ClientHandler - serwer</li>
 * 	<li>ConnectionManager - applet</li>
 * </ul>
 * @author Macvek
 *
 */
public class SendAndReceive {

	private ByteBuffer readBuffer = ByteBuffer.allocate(BUFFERSIZE);
	private ByteBuffer[] sendBuffers = new ByteBuffer[SENDLIMIT];	
	
	final static int BUFFERSIZE=8196;
	final static int SENDLIMIT=128;
	
	protected SocketChannel socket;
	protected TanksProtocol protocol;
	
	private int sendBegin = 0;
	private int sendEnd = 0;
	public boolean kill = false;
	
	/**
	 * Umieszczenie bufora w kolejce do wysłania
	 */
	public void addToSendBuffers(ByteBuffer in) {
		// blokada tylko dotyczy operowania na wskaźnikach bufora
		synchronized(sendBuffers) {
			// Nie ma miejsca w buforze do wysłania wiadomości.
			if (sendBegin == ( (sendEnd + sendBuffers.length + 1) % sendBuffers.length) )
				return;
			//FIXME: Proponuje tu wywalić wszystkie wiadomości i wysłać klientowi specjalną sprawdzającą
			
			sendBuffers[sendEnd++] = in;
			
			// jeżeli dotarliśmy do limitu tabeli, to przesuwamy się na początek
			if (sendEnd == sendBuffers.length)
				sendEnd = 0;
			
		}
		
		// należy obudzić wątek wysyłający
		synchronized(sendMonitor) {
			sendMonitor.notify();
		}
	}
	
	// pętla wysyłająca bufory z sendBuffers, zwraca indeks bufora, który nie został w pełni wysłany,
	// lub -1 jeżeli wysłał wszystkie
	private int flushBuffers(int begin, int end) throws IOException {
		socket.write(sendBuffers,begin, end - begin );
		
		for (;begin < end;begin++){
			if (sendBuffers[begin].remaining() > 0)
				return begin;
			else
				sendBuffers[begin] = null;
				// lub usuwamy ten bufor, bo jest już nieużyteczny
		}
		
		return -1;
	}
	
	private Object sendMonitor = new Object();
	protected void sendLoop() {
		// Wysyłanie, w bloku do while, żeby nie bawić się w IFy
		/* Wysyłanie jest operowane na kolejce o początku sterowanym przez READ i końcu
		 * sterowanym przez WRITE. Kolejka jest obrotowa, czyli jeżeli kursor dojdzie do końca
		 * to ustawia się na początku. znacznik poczatek = koniec oznacza pustą kolejkę.
		 * Przez to zawsze jeden slot jest nieużywany, nie wiem czy jest sens to poprawiać
		 */
		try {
		while(true) {
			int begin,end;
			// Wysłanie danych
			
			synchronized(sendBuffers) {
				// ustalamy granice działania dla tego wysyłania
				begin = sendBegin;
				end = sendEnd;
			}
			
			int newBegin;		// wynik flushBuffers

			// kolejka jest pusta, więc zatrzymujemy działanie
			if (begin == end) {
				synchronized(sendMonitor) {
					try { sendMonitor.wait(); }catch(Exception e) {}
					if (kill) {	// zakończenie działania
						try { socket.close(); } catch (IOException e) {}
						return;
					}
					continue;
				}
			}
			
			// kolejka jest obrócona, więc wysyłamy wszystko od kursora do końca
			if (end < begin) {
				newBegin = flushBuffers(begin,sendBuffers.length);
				
				// Wysłano wszystko, więc ustawiamy się na początku i wysyłamy dalej
				if (newBegin == -1) {
					begin = 0;
				}
				// Nie wysłano wszystkiego, więc wprowadzamy nową wartość begin i kończymy
				else {
					synchronized(sendBuffers) {
						sendBegin = newBegin;
						break;
					}
				}
			}
			
			newBegin = flushBuffers(begin, end);
			
			// Wysłano wszystkie, więc ustaw stan pustej kolejki
			if (newBegin == -1) {
				newBegin = end;
				
			}
			
			synchronized(sendBuffers) {
				sendBegin = newBegin;
			}
			
		}
		}catch(IOException e) {
			return;
		}
	}
	
	protected void receiveLoop() throws IOException{
		int count;	// naliczanie ile bajtów zostało odczytanych
		byte[] tmpBuffer;
		
		int rBegin = 0; // poczatek pakietów
		int rEnd = 0;	// koniec pakietów
		
		while(true) {
			
			// ustaw maksymalny limit
			readBuffer.limit(readBuffer.capacity());
			
			count = socket.read(readBuffer);
			rEnd = readBuffer.position();	// ustaw znacznik końca pakietó
			
			if (count != 0) {

				// Czy został rozłączony (nie obchodzi nas jak)
				if (count == -1)
					throw new IOException();

				// ustawienie przedziału do odczytu
				readBuffer.position(rBegin);
				readBuffer.limit(rEnd);
				
				protocol.receive(readBuffer);
				// pozycja jest jedno miejsce za ostatnim odczytanym pakietem
				
				rBegin = readBuffer.position();
				readBuffer.limit(readBuffer.capacity());
				
				// bufor jest pełny, kopiujemy niezidentyfikowaną cześć na początek
				if (rEnd == readBuffer.capacity()) {
					tmpBuffer = new byte[readBuffer.remaining()];

					readBuffer.get(tmpBuffer);
					readBuffer.position(0);
					readBuffer.put(tmpBuffer);
					
					rBegin = 0;
					rEnd = tmpBuffer.length;
				}
			}
			
		}
	}
}
