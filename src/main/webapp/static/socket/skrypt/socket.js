
function TanksPacket(name, src) {
    if (null == src)
        src = {};

    src._name = name;
    return src;
}

function Set(iName, iValue) {
    return new TanksPacket("SetAttribute", {name: iName, value: iValue});
}

function TanksSocket(socketApplet) {
    var socket = socketApplet;
    var parent = this;

    this.log = function (msg, code) {
        console.log(msg + " : " + code, "Błąd");
    };
    this.status = false;	// status połączenia

    this.listener = {
        messageLock: false,
        messageBody: {}, // obiekt do którego aplet umieszcza nową wiadomość

        _message: {
            _error: function (messageName) {
                console.log("nieznany pakiet " + messageName);
            }
        },
        // callback jest to : function(messageName,messageBody) ...
        on: function (messageName, callback) {
            if (messageName == null)
                return;

            if (callback instanceof Function) {
                if (this._message[messageName] == undefined)
                    this._message[messageName] = new Array();

                this._message[messageName].push(callback);
            }
        },
        // Aktualna wersja która działa z TanksProtocolPlainJs2
        onMessage: function (args) {
            var newMessage = true;
            var messageName = "";
            var messageBody = {};

            var self = this;
            
            for (i = 0; i < args.length; i++) {
                var arg = args[i];
                if (newMessage) {
                    newMessage = false;
                    messageName = arg;
                    continue;
                }

                if (arg == "\n") {
                    newMessage = true;
                    executePacket(messageName, messageBody);
                    messageName = "";
                    messageBody = {};
                    continue;
                }

                messageBody[arg] = args[i + 1];
                i++;
            }
            
            function executePacket(name, body) {
                var handlers = self._message[name];

                // Nieznany pakiet, uruchom zdarzenie błędu
                if (handlers == undefined) {
                    self._message._error(name, body);
                }
                // Lub wywołaj wszystkie zarejestrowane handlery
                else {
                    for (index in handlers) {
                        handlers[index](name, body);
                    }
                }
            }

        },
        onMessageReset: function () {
            alert("MessageReset");
        },
        onConnect: function () {
            parent.status = true;
        },
        onDisconnect: function () {
            parent.status = false;
        },
        onTimeout: function () {
            okienko.msgBox("Nastąpiło przekroczenie czasu oczekiwania na odpowiedź z serwera");
        },
        onConnectionError: function (errorCode) {
            okienko.msgBox("Nie udało się połączyć z serwerem");
            parent.log("Błąd podczas nawiązywania połączenia:", socket.getErrorMsg(errorCode));
        }
    };

    // Buduje pakiet widoczny dla apletu z obiektu wejściowego i wysyła go
    this.send = function (packet) {
        if (false == parent.status)
            return;

        var msgName = packet._name;
        var msgHead = new Array();
        var msgParams = {};

        // brak nazwy = błąd
        if (msgName == null)
            return;

        for (i in packet) {
            // Omijamy pola, które nie mają być wysłane			
            if (i == "prototype")
                continue;

            if (i[0] == "_")
                continue;

            if (packet[i] instanceof Function)
                continue;

            // zbudowanie pakietu
            msgHead.push(i);

            msgParams[i] = packet[i].toString();
        }

        var out = "name:" + msgName + "\n" + "head:" + msgHead + "\n";

        for (x in msgParams) {
            out += x + ":" + msgParams[x] + "\n";
        }

        // Konstrukcja obiektu i wysłanie go
        socket.send({
            messageName: msgName,
            messageHead: msgHead,
            messageParams: msgParams
        });
    };

    this.connect = function () {
        // zarejestruj listener i podaj dane do połączenia

        var result = socket.register({listener: this.listener});

        if (result !== 0) {
            this.log("Błąd podczas rejestracji połączenia", socket.getErrorMsg(result));
            return;
        }

        // wykonaj połączenie
        result = socket.connect();
        if (result !== 0) {
            this.log("Błąd podczas nawiązywania połączenia", socket.getErrorMsg(result));
            return;
        }

    };

    this.disconnect = function () {
        var result = socket.disconnect();
        if (result) {
            alert(socket.getErrorMsg(result));
        }
    };
}
