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
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.swing.JApplet;

import netscape.javascript.*;
/**
 * Klasa pośrednicząca w komunikacji przeglądarki z serwerem, za pomocą
 * dowolnego protokołu komunikacji.
 * 
 * @author Macvek
 */

public class TanksSocket extends JApplet {

	private boolean registered = false;
	ConnectionManager connection;

	/**
	 * Obiekt do logowania zdarzeń,
	 */
	private Logger console=new Logger();
	
	private static final long serialVersionUID = 1L;
	
	// na potrzeby testów
	protected void setLogger(Logger newLogger) {
		console = newLogger;
	}
	
	/**
	 * Tablica błędów
	 */
	private String[] errorMsg = new String[]{
		"Operacja wykonana poprawnie",	// kod 0 
		"Nieprawidłowy obiekt konsoli",		// kod 1
		"Nieprawidłowy numer portu",		// kod 2
		"Nieprawidłowy obiekt listener",	// kod 3
		"Błąd podczas konwersji obiektu JS",	// kod 4
		"Nie zarejestrowano obiektu kontrolnego",	// kod 5
		"Już jest połączenie",	// kod 6
		"Obecnie jest wykonywana operacja na połączeniu",	// kod 7
		"Nie udało się nawiązać połączenia z hostem",	// kod 8
		"Wystąpił wyjątek bezpieczeństwa",	// kod 9
		"Nie jesteś połączony z serwerem",	// kod 11
	};
	
	final static int BLAD_KONSOLA = 1;
	final static int BLAD_PORT = 2;
	final static int BLAD_LISTENER = 3;
	final static int BLAD_KONWERSJI = 4;
	final static int BLAD_NIEZAREJESTROWANY = 5;
	final static int BLAD_JUZPOLACZONY = 6;
	final static int BLAD_CONNECTIONLOCKED = 7;
	final static int BLAD_IOEXCEPTION = 8;
	final static int BLAD_SECURITYEXCEPTION = 9;
	final static int BLAD_UNKNOWNHOST = 10;
	final static int BLAD_NIEPOLACZONY = 11;
	/**
	 * Wywoływany przez przeglądarkę: Rozpoczęcie działania appletu
	 */
	public void start() {
		super.start();
		console.log("start()");
	}
	/**
	 * Wywoływany przez przeglądarkę: Zatrzymanie działania appletu
	 */
	public void stop() {
		unregister();
		
		console.log("stop()");
		super.stop();
	}
	
	/**
	 * Wywoływany przez przeglądarkę: Wczytanie appletu
	 */
	public void init() {
		console.log("init()");
		super.init();
	
	}
	
	/**
	 * Wywoływany przez przeglądarkę: Żeby applet zwolnił swoje zasoby
	 */
	public void destory() {
		console.log("destroy()");
		super.destroy();

	}
	
	/**
	 * Rejestracja obiektu kontrolnego z javascriptu do komunikacji z apletem
	 * Obiekt w javascript musi postaci co najmniej: 
	 * {
	 * port:Numer portu do którego ma się łączyć serwer,
	 * host:Adres serwera
	 * listener: {
	 * 		messageLock:Obiekt True/False który jest blokowany na czas 
	 * 					modyfikacji messageBody przez javascript
	 * 		messageBody:Obiekt do którego będą zapisywane wiadomości
	 * 		onMessage(messageName),	// otrzymanie wiadomosci
	 * 		onMessageReset(),	// przepełnienie bufora odczytu (skutek jakiegoś błędu)
	 * 		onConnect(), // zdarzenie na rozpoczęcie połączenia
	 * 		onDisconnect(), // zdarzenie na zakończenie połączenia
	 * 		onTimeout(),	// zdarzenie na timeout
	 * 		onConnectionError(), // zdarzenie na błąd podczas połączenia
	 * 	}
	 * }
	 * @param JSObject input
	 * @return Integer, kod błędu, lub 0 jeżeli wszystko ok
	 */
	
	public int register(JSObject input) {
		if (registered)
			return 0;
		
		try {
			// pobranie numeru portu
                        double port = (double)input.getMember("port");
			if (port < 1024 || port > 65535)
				return BLAD_PORT;
			
			// pobranie hosta
			String host = (String)input.getMember("host");
			if (host == null)
				return BLAD_KONWERSJI;

			// pobranie listenera
			JSObject listener = (JSObject)input.getMember("listener");
			if (listener == null)
				return BLAD_KONWERSJI;
			// sprawdzenie czy listener jest poprawnie zdefiniowany
			if (checkListener(listener) == false)
				return BLAD_LISTENER;
			
			// Wszystko się zgadza więc tworzymy obsługę połączenia
			connection = new ConnectionManager(host,(int)port,listener,this,console);
			
		}catch(Exception e) {
			/*Jeżeli wystąpi jakiś wyjątek to prawdopodobnie będzie to
			 * błąd konwersji z typu javascript na typ java
			 */
			console.log(e.getMessage());
                        StringWriter writer = new StringWriter();
                        
                        e.printStackTrace(new PrintWriter(writer));
                        console.log(writer.toString());
                        return BLAD_KONWERSJI;
		}
		
		// Nie wystąpił wyjątek, więc uznajemy że jest zarejestrowany
		registered = true;
		console.log("register()");
		return 0;
	}
	
	/**
	 * Wywoływane zdalnie, lub przez disconnect lub nieudany connect
	 * Usuwa obiekt obsługujący połączenie
	 */
	public void unregister() {
		if (!registered)
			return;
		
		connection = null;
		registered = false;
	}
	/**
	 * Sprawdza czy podany obiekt pasuje do wzoru listenera
	 * @param input
	 * @return true : input spełnia wymogi listenera
	 */
	private Boolean checkListener(JSObject input) {
		if (input.getMember("messageLock") == null)
			return false;
		
		if (input.getMember("messageBody") == null)
			return false;
		
		if (input.getMember("onMessage") == null)
			return false;
		
		if (input.getMember("onMessageReset") == null)
			return false;
		
		if (input.getMember("onConnect") == null)
			return false;
		
		if (input.getMember("onDisconnect") == null)
			return false;
		
		if (input.getMember("onTimeout") == null)
			return false;
		
		if (input.getMember("onConnectionError") == null)
			return false;
		
		// Ma wszystkie powyższe elemeny więc jest ok
		return true;
	}
	
	/**
	 * Polecenie do nawiązania połączenia z serwerem.
	 * @return 0-wszystko w porządku, lub kod błędu 
	 */
	public int connect() {
		if (!registered)
			return BLAD_NIEZAREJESTROWANY;
		
		if (connection.connected)
			return BLAD_CONNECTIONLOCKED;
		
		if (connection.lock)
			return BLAD_JUZPOLACZONY;
			
		// Włącz blokowanie połączenia.
		connection.lock = true;
		// Ustaw listener dla klasy z połączeniami, żeby mogła odpalić zdarzenia
		console.log("connect()");
		new Thread(connection).start();
		return 0;
	}
	
	public int disconnect() {
		if (!registered)
			return BLAD_NIEZAREJESTROWANY;
		
		if (connection.lock)
			return BLAD_CONNECTIONLOCKED;
		
		if (!connection.connected) {
			return BLAD_NIEPOLACZONY;
		}
		console.log("disconnect()");

		connection.lock = true;
		connection.kill = true;
		connection.addToSendBuffers(null);
		return 0;
	}
	
	public int send(JSObject message) {
		if (!registered)
			return BLAD_NIEZAREJESTROWANY;
		
		if (!connection.connected)
			return BLAD_NIEPOLACZONY;
		
		if (connection.send(message) == false)
			return 1;
		
		return 0;
	}
	public String getErrorMsg(int errorCode) {
		if (errorCode>=0 && errorCode < errorMsg.length)
			return errorMsg[errorCode];
		
		return "Nieznany kod błędu";
	}
	
	public void registerConsole(JSObject inConsole) {
		console.attachConsole(inConsole);
	}
	
}
