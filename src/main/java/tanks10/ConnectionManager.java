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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import tanks10.mocked.JSObject;

import tanks10.protocols.TanksProtocolPlainJs2;

/**
 * Klasa obsługująca połączenie z serwerem
 *
 * @author Macvek
 *
 */
public class ConnectionManager extends SendAndReceive implements Runnable {
    public static final String version = "Tanks10 ConnectionManager version 0.22";
    JSObject listener; // obiekt współdzielony z javascriptem
    Boolean connected = new Boolean(false);

    //Wywołano connect albo disconnect, ale jeszcze nie zostało wykonane
    Boolean lock = new Boolean(false);
    private String host;
    private Integer port;
    private ByteBuffer packetBuffer;
    private Logger console;
    final static int BUFFERSIZE = 512;	// jest to maksymalna wielkość pojedynczego komunikatu
    private TanksSocket tanksSocket;

    /**
     * Zainicjowanie protokołu
     */
    ConnectionManager(String host, int port, JSObject listener, TanksSocket tanksSocket, Logger console) {
        System.out.println(version);
        this.host = host;
        this.port = port;
        this.listener = listener;
        this.tanksSocket = tanksSocket;

        TanksProtocolPlainJs2 proto = new TanksProtocolPlainJs2();
        proto.listener = listener;
        proto.messageBody = (JSObject) listener.getMember("messageBody");
        this.protocol = proto;
        this.console = console;
        console.log(ConnectionManager.version);
    }

    boolean send(JSObject msg) {
        // Umieść wiadomość w buforze
        packetBuffer = ByteBuffer.allocate(BUFFERSIZE);
        if (!protocol.send(packetBuffer, msg)) {
            return false;
        }

        packetBuffer.limit(packetBuffer.position());
        packetBuffer.position(0);

        addToSendBuffers(packetBuffer);

        return true;
    }

    /**
     * Po nawiązaniu połączenia wątek uruchamia pętlę zarządzającą odbieraniem pakietów
     */
    private boolean startSendLoop = false;	// czy uruchomić sendLoop

    public void run() {
        if (startSendLoop) {
            sendLoop();
            return;
        }
        console.log("run()");

        if (socket != null) {
            return;
        }

        try {
            socket = SocketChannel.open();

            socket.socket().setTcpNoDelay(true);
            socket.configureBlocking(true);
            socket.connect(new InetSocketAddress(host, port));
            while (!socket.finishConnect()) {
                Thread.sleep(500);	// moment na uzyskanie połączenia
            }
            socket.socket().setTcpNoDelay(true);

        } catch (SecurityException e) {
            // błąd zabezpieczeń, uruchom obsługę w JS
            listener.call("onConnectionError",
                    new Integer[]{TanksSocket.BLAD_SECURITYEXCEPTION});
        } catch (UnresolvedAddressException e) {
            // Nieznany host
            listener.call("onConnectionError",
                    new Integer[]{TanksSocket.BLAD_UNKNOWNHOST});
        } catch (IOException e) {
            // prawdopodobnie host nie przyjmuje połączenia
            listener.call("onConnectionError",
                    new Integer[]{TanksSocket.BLAD_IOEXCEPTION});
        } catch (Exception e) {
            //wystąpił jakiś inny wyjątek
            listener.call("onConnectionError", null);
        }

        // wyłączenie blokowania połączeń (uruchomione przez pierwszy connect)
        lock = false;

        // jeżeli nie udało się nawiązać połączenia to zakończ
        if (!socket.isConnected()) {
            tanksSocket.unregister();
            return;
        }

        connected = true;
        listener.call("onConnect", null); // wywołaj zdarzenie z javascript

        // utworzenie wątku wysyłającego
        startSendLoop = true;
        new Thread(this).start();

        try {
            receiveLoop();	// wywołanie pętli obsługi połączenia (z SendAndReceive)
        } catch (IOException e) {
        }

        try {
            socket.close();
        } catch (IOException e) {
        }

        console.log("receiveLoop break");
        lock = false;
        listener.call("onDisconnect", null);
        tanksSocket.unregister();
    }
}
