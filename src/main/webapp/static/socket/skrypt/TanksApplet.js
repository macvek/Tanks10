function ConnectionManager(listener, tanksSocket) {
    this.connected = false;
    this.listener = listener;
    this.lock = false;
    this.tanksSocket = tanksSocket;
    var socket;
    var protocol = new TanksProtocolPlainJs2();
    protocol.listener = listener;
    
    var self = this;

    function run() {
        var receiveBuffer = {text:"",mark:0};
        
        socket = new WebSocket("ws://localhost:8080/TanksServlet/socket/server");
        socket.onopen = function () {
            self.connected = true;
            listener.onConnect();
        };

        socket.onerror = function (evt) {
            listener.onConnectionError(BLAD_IOEXCEPTION);
            self.connected = false;
            console.log(env);
        };

        socket.onclose = function () {
            self.connected = false;
            listener.onDisconnect();
        };
        
        socket.onmessage = function (evt) {
            handleData(evt.data);
        };
        
        function handleData(data) {
            receiveBuffer.text += data;
            protocol.receive(receiveBuffer);
            receiveBuffer.text = receiveBuffer.text.substring(receiveBuffer.mark, receiveBuffer.length);
        }
    }
    
    function send(msg) {
        // Umieść wiadomość w buforze
        var packetBuffer = {buffered:null};
        if (!protocol.send(packetBuffer, msg)) {
            return false;
        }
        
        socket.send(packetBuffer.buffered);

        return true;
    }
    
    this.run = run;
    this.send = send;
}


function TanksApplet() {
    var errorMsg = [
        "Operacja wykonana poprawnie", // kod 0 
        "Nieprawidłowy obiekt konsoli", // kod 1
        "Nieprawidłowy numer portu", // kod 2
        "Nieprawidłowy obiekt listener", // kod 3
        "Błąd podczas konwersji obiektu JS", // kod 4
        "Nie zarejestrowano obiektu kontrolnego", // kod 5
        "Już jest połączenie", // kod 6
        "Obecnie jest wykonywana operacja na połączeniu", // kod 7
        "Nie udało się nawiązać połączenia z hostem", // kod 8
        "Wystąpił wyjątek bezpieczeństwa", // kod 9
        "Nie jesteś połączony z serwerem", // kod 11
    ];

    var BLAD_KONSOLA = 1;
    var BLAD_PORT = 2;
    var BLAD_LISTENER = 3;
    var BLAD_KONWERSJI = 4;
    var BLAD_NIEZAREJESTROWANY = 5;
    var BLAD_JUZPOLACZONY = 6;
    var BLAD_CONNECTIONLOCKED = 7;
    var BLAD_IOEXCEPTION = 8;
    var BLAD_SECURITYEXCEPTION = 9;
    var BLAD_UNKNOWNHOST = 10;
    var BLAD_NIEPOLACZONY = 11;

    var port;
    var host;
    var listener;
    var registered = false;
    var connection;

    this.register = register;
    this.unregister = unregister;
    this.send = send;
    this.connect = connect;
    this.disconnect = disconnect;
    this.getErrorMsg = getErrorMsg;
    this.registerConsole = registerConsole;

    function register(conf) {
        if (!registered) {
            port = conf.port;
            host = conf.host;
            listener = conf.listener;

            if (port === null)
                return BLAD_KONWERSJI;

            if (port < 1024 || port > 65535)
                return BLAD_PORT;

            if (host === null)
                return BLAD_KONWERSJI;

            if (listener === null)
                return BLAD_KONWERSJI;

            if (!checkListener(listener))
                return BLAD_LISTENER;

            registered = true;
            connection = new ConnectionManager(listener, this);
            console.log("register()");
        }
        return 0;
    }

    function unregister() {
        registered = false;
        connection = null;
    }

    function connect() {
        if (!registered)
            return BLAD_NIEZAREJESTROWANY;

        if (connection.connected)
            return BLAD_CONNECTIONLOCKED;

        if (connection.lock)
            return BLAD_JUZPOLACZONY;

        connection.lock = true;
        connection.run();
        console.log("connect()");
        
        return 0;
    }

    function disconnect() {
        connection.close();
        console.log("disconnect()");
    }

    function send(message) {
        if (!registered)
            return BLAD_NIEZAREJESTROWANY;

        if (!connection.connected)
            return BLAD_NIEPOLACZONY;

        if (!connection.send(message))
            return 1;

        return 0;
    }

    function getErrorMsg(errorCode) {
        if (errorCode >= 0 && errorCode < errorMsg.length)
            return errorMsg[errorCode];

        return "Nieznany kod błędu";
    }

    function registerConsole(outObject) {

    }

    function checkListener() {
        if (listener["messageLock"] === null)
            return false;

        if (listener["messageBody"] === null)
            return false;

        if (listener["onMessage"] === null)
            return false;

        if (listener["onMessageReset"] === null)
            return false;

        if (listener["onConnect"] === null)
            return false;

        if (listener["onDisconnect"] === null)
            return false;

        if (listener["onTimeout"] === null)
            return false;

        if (listener["onConnectionError"] === null)
            return false;

        // Ma wszystkie powyższe elemeny więc jest ok
        return true;
    }
}