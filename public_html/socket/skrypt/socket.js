
window.socket = {
	send: function(){}
};

var TanksPacket = function(name,src) {
	if ( null == src )
		src = {};
		
	src._name = name;
	return src;
};

var Set = function(iName,iValue) {
	return new TanksPacket("SetAttribute",{name:iName,value:iValue});
};

var TanksApplet = function(appletId, callback) {
	// jest już zrobiony taki aplet
	if (document.getElementById(appletId)) {
		callback(document.getElementById(appletId));
		return;
	}
	
	// nie jest budowane przez obiekty DOM bo to nie jest poprawny html, ale działa
	var insert = '<applet id="'+appletId+'" archive="applet/Tanks10_Applet.jar" code="tanks10.TanksSocket" width="10" height="10" mayscript></applet>';
	$("body").append(insert);
	var loop = function() {
		if (document.getElementById(appletId).register) {
			callback(document.getElementById(appletId));
		}
		else
			setTimeout(loop,100);
	};
	
	loop();
};

var TanksSocket = function(socketApplet) {
	var socket = socketApplet;
	var parent = this;
	
	this.log = function(msg,code) {alert(msg+" : "+code,"Błąd");};
	this.status = false;	// status połączenia
	
	this.listener = {
		messageLock:false,
		messageBody:{},		// obiekt do którego aplet umieszcza nową wiadomość

		_message: {
			_error:function(messageName){ alert("nieznany pakiet "+messageName);}
		},
		
		// callback jest to : function(messageName,messageBody) ...
		on : function(messageName,callback) {
			if (messageName == null)
				return;
				
			if (callback instanceof Function) {
				if ( this._message[messageName] == undefined )
					this._message[messageName] = new Array();
					
				this._message[messageName].push(callback);
			}
		},
		
		// Aktualna wersja która działa z TanksProtocolPlainJs2
		onMessage: function() {
			var newMessage = true;
			var param = true;
			var messageName = "";
			var messageBody = {};
			
			var self = this;
			var executePacket = function(name,body) {
				var handlers = self._message[name];
				
				// Nieznany pakiet, uruchom zdarzenie błędu
				if (handlers == undefined) {
					self._message._error(name,body);
				}
				// Lub wywołaj wszystkie zarejestrowane handlery
				else {
					for (index in handlers) {
						handlers[index](name,body);
					}
				}
			};
			
			for (i=0;i<arguments.length;i++) {
				if (newMessage) {
					newMessage = false;
					messageName = arguments[i];
					continue;
				}
				
				if (arguments[i] == "\n") {
					newMessage = true;
					executePacket(messageName,messageBody);
					messageName = "";
					messageBody = {};
					continue;
				}
				
				messageBody[arguments[i]] = arguments[i+1];
				i++;
			}
			
		},
		
		onMessageReset: function() {
			alert("MessageReset");
		},
		
		onConnect: function(){
			parent.status = true;
		},

		onDisconnect: function(){
			parent.status = false;
		},
		
		onTimeout: function(){
			okienko.msgBox("Nastąpiło przekroczenie czasu oczekiwania na odpowiedź z serwera");
		},

		onConnectionError: function(errorCode){
			okienko.msgBox("Nie udało się połączyć z serwerem");
			parent.log("Błąd podczas nawiązywania połączenia:" ,socket.getErrorMsg(errorCode));
		}
	};

	// Buduje pakiet widoczny dla apletu z obiektu wejściowego i wysyła go
	this.send = function(packet) {
		if (false == parent.status)
			return;

		var msgName = packet._name;
		var msgHead = new Array();
		var msgParams = {};

		// brak nazwy = błąd
		if ( msgName == null )
			return;
		
		for (i in packet) {
			// Omijamy pola, które nie mają być wysłane			
			if ( i == "prototype" )
				continue;
				
			if ( i[0] == "_" )
				continue;
				
			if ( packet[i] instanceof Function )
				continue;
				
			// zbudowanie pakietu
			msgHead.push(i);
			
			msgParams[i] = packet[i].toString();
		}
		
		var out = "name:"+msgName+"\n"+"head:"+msgHead+"\n";
						
		for (x in msgParams) {
			out += x+":"+msgParams[x]+"\n";
		}
		
		// Konstrukcja obiektu i wysłanie go
		socket.send({
			messageName:msgName,
			messageHead:msgHead,
			messageParams:msgParams
		});
	};

	this.connect = function(remoteHost,remotePort) {
		// zarejestruj listener i podaj dane do połączenia
		
		var result = socket.register({host:remoteHost, port:remotePort, listener:this.listener});
		
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
	
	this.disconnect = function() {
		var result = socket.disconnect();
		if (result) {
			alert(socket.getErrorMsg(result));
		}
	};
};
