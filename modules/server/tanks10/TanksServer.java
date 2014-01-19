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

import java.io.*;
import java.nio.channels.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.*;

import tanks10.world.TanksWorld;

/**
 * Część serwerowa aplikacji. Tworzy socket za pomocą ServerSocketChannel na
 * podanym, w argumentach, hoście i porcie Dla każdego połączenia przydziela
 * obsługujący je wątek
 *
 */
public class TanksServer implements Runnable {

    final int THREADPOOLSIZE = 32;
    final private PrintStream log;
    final private ServerSocketChannel server;
    final private ServerSocket socket;
    final private ExecutorService pool; // zbiór obsługujących wątków

    /**
     * Po zainicjowaniu jest uruchamiany wątek który obsługuje połączenia,
     * tworzony w konstruktorze
     */
    @Override
    public void run() {
        log.println("TanksServer.run()");
        while (true) {
            try {
                log.println("TanksServer: pool.execute()");
                clientHandler = new ClientHandler( // dla klasy obsługującej połączenia
                        server.accept(), // dając jej nowe połączenie
                        log);
                
                pool.execute(clientHandler);			 	// i strumień do logowania

            } catch (IOException e) {
                log.println("TanksServer: pool.shutdown()");
                pool.shutdown();
                break;
            }
        }
    }
    private ClientHandler clientHandler;

    /**
     * Inicjacja i uruchomienie serwera
     *
     * @param log : stream na który mają iść komunikaty
     */
    
    TanksServer(String host, int port, PrintStream log) throws IOException {
        this.log = log;

        server = ServerSocketChannel.open();
        socket = server.socket();

        socket.bind(new InetSocketAddress(host, port));
        log.println("Server socket: " + host + ":" + port);
        pool = Executors.newFixedThreadPool(THREADPOOLSIZE);
        myThread = new Thread(this, "TanksServer");
        myThread.start();
    }
    private final Thread myThread;

    public void stop() {
        try {
            server.close();
            synchronized(server) {   
                server.notifyAll();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Wyświetlenie poprawnej metody wywoływania aplikacji
     */
    public static void usage() {
        System.out.println("java TanksServer HOST PORT");
        System.exit(1);
    }

    /**
     * Pobiera dane z linii poleceń i tworzy instancję klasy
     */
    public static void main(String[] args) {
        System.out.println(new Date().toString() + ": Tanks10 Server. Start.");

        // Przygotuj dane wejściowe
        int port = 4444;
        String host = "localhost";

        if (args.length < 2) {
            usage();
        }

        try {
            host = args[0];
            port = new Integer(args[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            usage();
        }

        TanksServer mainframe = bootstrapServer(host, port);

        // sprawdzenie czy ciągle działa
        while (true) {
            try {
                Thread.sleep(60000);
                System.out.print(".");
                if (TanksWorld.isEndOfTheWorld()) {
                    System.out.println(new Date().toString() + ": Tanks10 Server. Koniec.");
                    mainframe.stop();
                }
            } catch (InterruptedException e) {
            }
        }
    }

    public static TanksServer bootstrapServer(String host, int port) {
        tanksWorldThread = new Thread(new TanksWorld(), "TanksWorld");
        // Uruchomienie silnika gry
        tanksWorldThread.start();
        TanksServer mainframe = null;
        try {
            mainframe = new TanksServer(host, port, System.out);
        } catch (IOException io) {
            System.out.println("Nie udało się uruchomić serwera");
            throw new RuntimeException(io);
        }
        return mainframe;
    }
    private static Thread tanksWorldThread;
}
