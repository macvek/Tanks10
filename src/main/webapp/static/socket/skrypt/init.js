function prepare_socket() {
    var appletIsReady = function (applet) {

        if (window.socket && window.socket.connect) {
            if (window.socket.status) {
                return;
            }

            // więc tylko wykonaj połączenie
            window.socket.connect();
            return;
        }
        window.socket = new TanksSocket(applet);
        var socket = window.socket;

        // Przygotuj listener

        var origOnConnect = socket.listener.onConnect;
        socket.listener.onConnect = function () {
            origOnConnect();
            blaster.onConnect = true;
            blaster.timestampCount = 0;
            blaster.noTimestamp = true;
            blaster.findTimestamp();
            blaster.objects = {};
            blaster.spawnTopObjects = {};
            blaster.spawnDownObjects = {};
        };

        var origOnDisconnect = socket.listener.onDisconnect;
        socket.listener.onDisconnect = function () {
            origOnDisconnect();
            $("#backBuffer").css('background-image', 'url("static/panel/grafika/welcome.png")');
            $("#pasekStanu").css({display: "none"});
            window.blaster.onConnect = false;
            window.blaster.haltKeyboard = true;
            alert("onDisconnect()");

        };

        // Dodaj reakcje na wiadomosci

        socket.listener.on("Spawn", blaster.Spawn);
        socket.listener.on("AddEntity", blaster.AddEntity);
        socket.listener.on("Model", function (name, body) {
            var id = body.id;
            var obj;
            if (!id || !(obj = blaster.objects[id]))
                return;

            var color = obj.color;
            var ammo = obj.ammo;
            blaster.AddEntity(name, body);
            obj = blaster.objects[id];
            obj.color = color;
            blaster.Ammo("Ammo", {id: id, ammo: ammo});

        });
        socket.listener.on("RemoveEntity", blaster.RemoveEntity);
        socket.listener.on("YouAre", blaster.YouAre);
        socket.listener.on("Update", blaster.UpdateRect);
        socket.listener.on("SetAttribute", blaster.SetAttribute);
        socket.listener.on("Frame", blaster.ServerFrame);
        socket.listener.on("TabelaWynikow", okienko.zapisanieDanych);
        socket.listener.on("Color", blaster.Color);
        socket.listener.on("Ammo", blaster.Ammo);
        socket.listener.on("TabelaWynikowKoniec", okienko.wyswietlenieDanych);
        socket.listener.on("Say", okienko.wiadomosc);
        socket.listener.on("Ping", function (name, body) {
            socket.send(new window.TanksPacket("Pong", {f: body.f}));
        });
        socket.listener.on("Hello", function (name, body) {
            var type = Number(body.type);
            switch (type) {
                case 2:
                    window.okienko.wczytaj("static/panel/menu/obserwuj.html", "", "obserwujMenu");
                    break;
                case 1:
                case 4:
                    $('#backBuffer').css('background-image', 'url("static/panel/grafika/miastox.png")');
                    $('#pasekStanu').css({display: "block"});
                    window.blaster.haltKeyboard = false;
                    break;
            }
        });

        socket.listener.on("TimeStamp", blaster.TimeStamp);

        socket.connect();

        var console = {write: function (msg) {
                document.getElementById("console").innerHTML += msg + "\n";
            }};
        applet.registerConsole(console);
    };

    appletIsReady(new TanksApplet());
}