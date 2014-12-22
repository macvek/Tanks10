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

import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import tanks10.protocols.TanksProtocolTiger;
import tanks10.world.entity.*;
import tanks10.packets.*;

/**
 * Obsługa świata. worldLogic to główna pętla działania.
 */
public class TanksWorld implements Runnable {

    public static final int TYPE_GOD = 0;		// admin
    public static final int TYPE_SPECTATOR = 1;	// obserwator
    public static final int TYPE_NEWBIE = 2;	// klient, który nie wybrał jeszcze swojej roli
    public static final int TYPE_SOLDIER = 4;	// normalny gracz
    public static final int TYPE_BOX = 8;		// obiekt nie kontrolowany przez gracza
    public static final int UPDATEBUFFER_SIZE = 4096;
    public static final int TMPBUFFER_SIZE = 128;
    public static final int PING_TIMEOUT = 1000;	// wysyłamy pinga co sekunde
    private final static Set<String> colors = new HashSet<>();
    private static double[][] spawnPoints;

    private boolean playerListChanged = false;	// czy w ostatniej klatce zmieniła się liczba graczy
    private static TanksWorld world;			// instancja świata
    private final TanksProtocolTiger protocol = new TanksProtocolTiger();// jest tutaj używany do budowania broadcastowej wiadomości

    // lista obiektów do utworzenia przy kolejnej klatce
    private final Set<Box> toSpawn = new HashSet<>();
    private final Set<Entity> toRemove = new HashSet<>();

    // Ustawiany jako updateBuffer gdy nie ma nic do wysłania
    private final ByteBuffer emptyBuffer = ByteBuffer.allocate(0);

    // Tutaj jest trzymana wiadomość z updatem
    private ByteBuffer updateBuffer = emptyBuffer;

    public static TanksWorld getWorld() {
        return world;
    }

    public TanksWorld() {
        TanksWorld.world = this;

        registerPackets();
        registerWorldBoundaries();
        registerColors();
        registerSpawnPoints();
    }

    private void registerSpawnPoints() {
        spawnPoints = new double[][]{
            {60, 60},
            {300, 60},
            {560, 60},
            {60, 400},
            {300, 400},
            {560, 400}
        };
    }

    private void registerColors() {
        // dostepne kolory
        colors.add("czerwony");
        colors.add("zielony");
        colors.add("niebieski");
        colors.add("bialy");
        colors.add("zolty");
        colors.add("brazowy");
        colors.add("pomaranczowy");
    }

    private void registerWorldBoundaries() {
        entities.add(new StaticBlock(0, 240, 25, 240));
        entities.add(new StaticBlock(640, 240, 20, 240));

        entities.add(new StaticBlock(320, 0, 320, 25));
        entities.add(new StaticBlock(320, 480, 320, 24));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{250, 150},
                new double[]{360, 150}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{360, 150},
                new double[]{413, 204}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{413, 204},
                new double[]{416, 256}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{416, 256},
                new double[]{356, 312}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{356, 312},
                new double[]{248, 316}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{248, 316},
                new double[]{195, 266}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{195, 266},
                new double[]{195, 200}
        ));

        entities.add(new StaticTriangle(
                new double[]{300, 230},
                new double[]{195, 200},
                new double[]{250, 150}
        ));
    }

    private void registerPackets() {
        /* Pakiety */
        // Ogólnego przeznaczenia \\
        TanksProtocolTiger.registerPacket(new Attribute());
        TanksProtocolTiger.registerPacket(new SetAttribute());
        TanksProtocolTiger.registerPacket(new GetAttribute());
        TanksProtocolTiger.registerPacket(new Ping());
        TanksProtocolTiger.registerPacket(new Pong());
        TanksProtocolTiger.registerPacket(new ProtocolSignature());
        TanksProtocolTiger.registerPacket(new Move());
        TanksProtocolTiger.registerPacket(new Attack());
        TanksProtocolTiger.registerPacket(new Spawn());
        TanksProtocolTiger.registerPacket(new Say());

        TanksProtocolTiger.registerPacket(new RekordyWynikow());

        TanksProtocolTiger.registerPacket(new TimeStamp());
    }

    synchronized static public int newWorldId() {
        return world.lastId++;
    }

    public void randomSpawnPoint(Soldier spawner) {
        int index = (int) Math.floor(Math.random() * (spawnPoints.length - 1) + 0.5);
        double[] vector = spawnPoints[index].clone();

        spawner.setVector(vector);

        synchronized (world.entities) {
            for (Soldier s : soldiers) {

                if (s == spawner) {
                    continue;
                }

                if (Box.boundingTest(s, spawner)) {
                    s.takeHp(999, spawner);
                }
            }
        }
    }

    // Utworzenie pojedynczej animacji, nie zapamiętywanej na serwerze (np. eksplozja)
    public static void spawn(String model, double[] vector) {
        world.broadcast(new Spawn(model, vector));
    }

    // Ta animacja jest przyporządkowana do ID, np. animacja strzału
    public static void spawn(String model, int id) {
        world.broadcast(new Spawn(model, id));
    }

    static public void spawnBox(Box box) {
        world.toSpawn.add(box);
    }

    // Tworzy nowego obserwatora
    public static Spectator newSpectator() {
        return world.spawnSpectator(newWorldId());
    }

    // Tworzy nowego entity administratora 
    public static God newGod() {
        return world.spawnGod(newWorldId());
    }

    // Tworzy nowego entity dla nowego połączenia
    public static Newbie newNewbie() {
        return world.spawnNewbie();
    }

    // Tworzy nowego entity dla SOLDATAA!!!!!!
    public static Soldier newSoldier(String model) {
        return world.spawnSoldier(newWorldId(), model);
    }

    // lista z imionami, używana do usunięcia duplikatów
    private final HashSet<String> nameSet = new HashSet<>();

    // sprawdza czy podany kolor jest poprawny
    public static boolean isColorValid(String color) {
        return colors.contains(color);
    }

    // zmiana broni
    public static void changeAmmo(Soldier s, int ammo) {
        int a = s.getAmmo();
        s.setAmmo(ammo);
        if (a == ammo) {
            return;
        }

        world.broadcast(new Ammo(s));
    }

    // zmiana modelu
    public static void changeModel(Entity ent, String model) {
        String m = ent.getModel();
        ent.setModel(model);
        if (m.equals(ent.getModel())) // nie został zmieniony
        {
            return;
        }

        world.broadcast(new Model(ent));
    }

    // zmiana koloru
    public static void changeColor(Entity ent, String color) {
        if (!isColorValid(color)) {
            return;
        }

        ent.setColor(color);
        world.broadcast(new Color(ent));
    }

    // zmiana imienia
    public static void changeName(Entity ent, String name) {
        if (name.length() > 20) {
            name = name.substring(0, 20);
        }

        if (name.equals(ent.getName())) {
            return;
        }

        synchronized (world.entities) {
            if (world.isNameValid(name)) {
                ent.setName(name);
                world.updateNames();
            }
        }
    }

    private void updateNames() {
        // Wywoływane tylko przez onPlayerSpawn, które jest automatycznie synchronizowny po entities
        synchronized (nameSet) {
            nameSet.clear();
            for (Soldier s : soldiers) {
                nameSet.add(s.getName());
            }
            for (Spectator s : spectators) {
                nameSet.add(s.getName());
            }
        }
    }

    // Sprawdza czy gracz może mieć podane imie
    public boolean isNameValid(String name) {
        if (name.length() == 0) {
            return false;
        }

        synchronized (nameSet) {
            return !nameSet.contains(name);
        }
    }

    public static void removeEntity(Entity ent) {
        synchronized(world.toRemove) {
            world.toRemove.add(ent);
        }
    }

    private void removeEntitiesFromList() {
        synchronized (toRemove) {

            for (Entity ent : toRemove) {
                if (ent instanceof Newbie) {
                    final Newbie newbie = (Newbie) ent;
                    synchronized (world.newbies) {
                        world.newbies.remove(newbie);
                    }
                } else if (ent instanceof God) {
                    final God god = (God) ent;
                    synchronized (world.gods) {
                        world.gods.remove(god);
                    }
                } else {

                    // wyślij powiadomienie o usunięciu
                    if (ent.getId() > -1) {
                        world.broadcast(new RemoveEntity(ent));
                    }

                    // odśwież nazwy graczy
                    if (ent.humanControl()) {
                        world.updateNames();
                    }

                    world.entities.remove(ent);

                    if (ent instanceof Soldier) {
                        final Soldier soldier = (Soldier) ent;
                        world.soldiers.remove(soldier);
                    }
                }
            }
            toRemove.clear();
        }
    }

    @Override
    public void run() {
        worldLogic();
    }

    private long prepareFlag = -1;	// na potrzeby prepareUpdateBuffer

    void prepareUpdateBuffer() {
        if (prepareFlag == frame) {
            return;
        }

        prepareFlag = frame;
        updateBuffer = ByteBuffer.allocate(UPDATEBUFFER_SIZE);

        // rozpoczynamy update od informacji o klatkach
        protocol.send(updateBuffer, new Frame(frame));
    }

    private int maxUpdate = 0;	// zapamiętanie największego komunikatu odświeżenia
    private long frame = 0;	// aktualna klatka

    // wykorzystywane przez TimeStamp
    public static long worldStartTime;
    private boolean endOfTheWorld = false;

    public static void end() {
        world.endOfTheWorld = true;
    }

    public static boolean isEndOfTheWorld() {
        return world.endOfTheWorld;
    }

    private static final long SYSTEM_FPS = 100;
    private static final long DELAY = 1000 / SYSTEM_FPS;
    private long sleepTime = DELAY;

    // Główna pętla wątku świata
    public void worldLogic() {
        long updateFrame = 0;

        long now;
        long newFrame;

        boolean updateSent = false;
        ByteBuffer addEntityBuffer = null;

        Long startTime = new Date().getTime();
        worldStartTime = startTime;

        System.out.println("StartTime:" + startTime);

        while (!endOfTheWorld) {
            toSpawn.clear();
            // koncepcja liczenia klatek względem rozpoczęcia działania programu
            now = new Date().getTime();
            newFrame = (int) ((now - startTime) / DELAY);

            adjustSleepTime(newFrame - frame);
            if (newFrame > frame) {
                updateSent = false;
                simulateFrames(newFrame, toRemove);
            }

            // zbudowanie pakietu odświeżającego
            if (updateFrame < frame) {
                updateFrame = frame;

                if (playerListChanged) {
                    addEntityBuffer = ByteBuffer.allocate(UPDATEBUFFER_SIZE);
                }
                scheduleRefreshPackets(addEntityBuffer);
            }

            // wyślijmy czekające pakiety
            if (updateSent == false) {
                sendPendingPackets(addEntityBuffer, now);
                updateSent = true;
                // może się zdarzyć, że gracz dołączy w połowie klatki
                if (addEntityBuffer != null) {
                    playerListChanged = false;
                    addEntityBuffer = null;
                }
                updateBuffer = emptyBuffer;
            }

            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }
            }
        }

        disconnectClients();
    }

    private void adjustSleepTime(long missedFrames) {
        if (missedFrames == 0) {
            sleepTime = DELAY;
        } else {
            sleepTime = 0;
        }
    }

    private void sendPendingPackets(ByteBuffer addEntityBuffer, long now) {
        updateBuffer.limit(updateBuffer.position());
        updateBuffer.position(0);
        if (addEntityBuffer != null) {
            addEntityBuffer.position(0);
        }

        int size = updateBuffer.limit();
        if (size > maxUpdate) {
            maxUpdate = size;
            System.out.println("MaxUpdate:" + size);
        }

        synchronized (entities) {
            for (Entity one : entities) {
                if (one.humanControl() == false) {
                    continue;
                }

                // sprawdza czy doszli nowi gracze i czy ten jest jednym z nich
                if (playerListChanged && one.freshMeat() && addEntityBuffer != null) {
                    one.getProxy().send(new Frame(frame));
                    one.getProxy().sendFromBuffer(addEntityBuffer);
                    one.getProxy().send(new YouAre(one.getId()));
                    one.noFreshMeat();
                }

                one.getProxy().sendFromBuffer(updateBuffer);

                // Wysyłanie pinga
                if (now >= one.getPingTime()) {
                    one.getProxy().send(new Ping(Long.toString(frame)));
                    one.setPingTime(now + PING_TIMEOUT);
                }
            }
        }
    }

    private void scheduleRefreshPackets(ByteBuffer addEntityBuffer) {
        synchronized (entities) {
            for (Entity one : entities) {
                if (playerListChanged && one.getId() > -1) {
                    protocol.send(addEntityBuffer, new AddEntity(one));

                    // jeżeli ten entity jest kierowany przez gracza to utwórz update
                    if (one instanceof Box && one.humanControl()) {
                        protocol.send(addEntityBuffer, new Update((Box) one));

                        if (one instanceof Soldier) {
                            protocol.send(addEntityBuffer, new Color(one));
                            protocol.send(addEntityBuffer, new Ammo((Soldier) one));
                        }
                    }
                }

                // Odświeżenie położenia
                if (one instanceof Box) {
                    Box b = (Box) one;
                    if (b.needsUpdate(frame)) {
                        prepareUpdateBuffer();
                        Packet updatePacket = new Update(b);
                        protocol.send(updateBuffer, updatePacket);
                        if (b.freshMeat() && b instanceof Soldier) {
                            protocol.send(updateBuffer, new Color(b));
                            protocol.send(updateBuffer, new Ammo((Soldier) b));
                        }
                    }
                }
            }
        }
    }

    private void simulateFrames(long newFrame, final Set<Entity> toRemove) {
        synchronized (entities) {
            while (newFrame > frame) {
                frame++;
                Box self, other;

                for (Entity one : entities) {
                    if (one instanceof Box) {
                        self = (Box) one;

                        if (self.simulate()) {	// jeżeli wykonano ruch to sprawdz kolizje
                            for (Entity two : entities) {
                                if (!(two instanceof Box)) {
                                    continue;
                                }

                                other = (Box) two;

                                // nie sprawdzamy samego z soba
                                if (one == two) {
                                    continue;
                                }

                                // zdarzyła się kolizja
                                if (Box.boundingTest(self, other)) {
                                    Box.collision(self, other);
                                    self.onTouch(other);
                                    other.onTrigger(self);
                                }
                            }
                        }
                    }
                    one.think(frame);	// działanie w tej klatce

                    // proźba o usunięcie (nie dotyczy to graczy)
                    if (one.removeMe()) {
                        toRemove.add(one);
                    }
                }

                removeEntitiesFromList();

                // dodawanie nowych elementów
                for (Box one : toSpawn) {
                    entities.add(one);

                    if (one.getId() > -1) {
                        broadcast(new AddEntity(one));
                    }
                }
            }
        }
    }

    private void disconnectClients() {
        synchronized (entities) {
            for (Entity one : entities) {
                if (one instanceof Soldier) {
                    one.getProxy().disconnect();
                }
            }
        }

        synchronized (newbies) {
            for (Entity one : newbies) {
                one.getProxy().disconnect();
            }
        }

        synchronized (gods) {
            for (Entity one : gods) {
                one.getProxy().disconnect();
            }
        }
    }

    // Dla Gods i Newbies są oddzielne kolekcje bo nie wpływają dynamicznie na świat.
    private final HashSet<God> gods = new HashSet<>();

    private God spawnGod(int id) {
        God god = new God(id);
        synchronized (gods) {
            gods.add(god);
        }
        return god;
    }

    final private HashSet<Newbie> newbies = new HashSet<>();

    private Newbie spawnNewbie() {	// tworzy entity dla nowego połączenia
        Newbie newbie = new Newbie();
        synchronized (newbies) {
            newbies.add(newbie);
        }
        return newbie;
    }

    private int lastId = 0;

    // Pozostali (Soldier, Box) są przechowywani w entities i w swoich oddzielnych listach
    final private HashSet<Entity> entities = new HashSet<>();
    final private HashSet<Soldier> soldiers = new HashSet<>();	// oba są synchronizowane po entities
    final private HashSet<Spectator> spectators = new HashSet<>();

    // Wysłanie wiadomości do wszystkich
    private void broadcast(Packet msg) {
        ByteBuffer tmpBuffer = ByteBuffer.allocate(TMPBUFFER_SIZE);
        protocol.send(tmpBuffer, msg);
        tmpBuffer.limit(tmpBuffer.position());
        synchronized (world.entities) {
            for (Soldier s : soldiers) {
                tmpBuffer.position(0);

                try {
                    s.getProxy().sendFromBuffer(tmpBuffer);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (Spectator s : spectators) {
                tmpBuffer.position(0);
                s.getProxy().sendFromBuffer(tmpBuffer);
            }
        }
    }

    // Wysłanie wiadomości do okienka czatu
    public static void say(String msg) {
        world.broadcast(new Say(msg));
    }

    public static void say(Entity ent, String msg) {
        world.broadcast(new Say(ent.getName() + ":" + msg));
    }

    // Zdarzenie eksplozji
    public void checkExplode(double[] vector, double range, double hp, Soldier owner) {
        synchronized (world.entities) {
            for (Soldier s : soldiers) {
                double sVector[] = s.getVector();

                double rx = Math.abs(vector[0] - sVector[0]);
                double ry = Math.abs(vector[1] - sVector[1]);

                double distance = Math.sqrt(rx * rx + ry * ry);

                if (distance > range * 0.5) {
                    continue;
                }

                s.takeHp(hp, owner);
            }
        }
    }

    // Wywoływane kiedy gracz jest tworzony, tutaj też wysłać wszystkim powiadomienie o tym,
    // to już działa zawsze dla Soldiera
    private void onPlayerSpawn(Entity ent) {
        broadcast(new AddEntity(ent));

        playerListChanged = true;
        entities.add(ent);
        updateNames();
    }

    private Spectator spawnSpectator(int id) {
        Spectator spec = new Spectator(id);
        synchronized (entities) {
            //onPlayerSpawn(spec);
            spectators.add(spec);
        }
        return spec;
    }

    private Soldier spawnSoldier(int id, String model) {
        Soldier soldier = new Soldier(id);
        soldier.setModel(model);
        synchronized (entities) {
            onPlayerSpawn(soldier);
            soldiers.add(soldier);
        }
        return soldier;
    }

    public static ScoreBoard[] getScoreBoard() {
        ScoreBoard[] tablica;
        synchronized (world.entities) {
            tablica = new ScoreBoard[world.soldiers.size()];
            int i = 0;
            for (Soldier x : world.soldiers) {
                tablica[i] = x.getScore();
                tablica[i].imie = x.getName();
                i++;
            }

        }

        return tablica;
    }
}
