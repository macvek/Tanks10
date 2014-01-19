var FRONT = 1 << 0;
var BACK = 1 << 1;
var RIGHT = 1 << 2;
var LEFT = 1 << 3;

var blaster = {
	onConnect:false,
	frontBuffer:null,
	backBuffer:null,
	frontContext:null,
	backContext:null,
	currentFrame:0,	// aktualna klatka, serwer dziala z szybkoscia 100 fps
	previousFrameTime:null,
	frameMilis:0,	// liczba milisekund która nie była większa od frameDelay co klatkę
	serverDelay: 10,
	serverFrame:0,
	frame:0,
	attackState:false,
	mode:0,	// 0 ruch, 1 strzal
	minimalLag:10,	// uzywane w refresh(), liczba klatek opóźnienia która wymusza ustawienie serverFrame
	
	objects: {},	// elementy o symulowanym ruchu
	topObjects: {},	// elementy które sa zawsze na górze
	
	spawnTopObjects: {},
	spawnDownObjects: {},
	
	moj_obiekt: null,
	last_id: 0,
	flags: 0,
	dynamicId: 0,
	
	hp: 0,
	model: null,
	ammo: 0,
	
	// Obsluga pakietów
	
	// Dodaje obiekt, który nie bedzie posiac zadnych akcji (np. jednorazowa animacja)
	Spawn : function(name, body) {
		
		var s;
		
		var model = body.model;
		var x = Number(body.x);
		var y = Number(body.y);
		var id = Number(body.id);
		var dId = "d"+(blaster.dynamicId++);
		
		switch (model) {
			case "Explode9mm" : s = new Explode(AMMO_9MM,dId,x,y); break;
			case "Explode40mm" : s = new Explode(AMMO_40MM,dId,x,y); break;
			case "ExplodeRocket" : s = new Explode(AMMO_ROCKET,dId,x,y); break;
			case "ExplodeTesla" : s = new Explode(AMMO_TESLA,dId,x,y); break;
			case "ExplodeShell" : s = new Explode(AMMO_SHELL,dId,x,y); break;
			
			case "Fire9mm" : s = new Fire(AMMO_9MM,dId,id,x,y); break;
			case "Fire40mm" : s = new Fire(AMMO_40MM,dId,id,x,y); break;
			case "FireRocket" : s = new Fire(AMMO_ROCKET,dId,id,x,y); break;
			case "FireTesla" : s = new Fire(AMMO_TESLA,dId,id,x,y); break;
			case "FireShell" : s = new Fire(AMMO_SHELL,dId,id,x,y); break;
			
			case "SpawnSoldierAnim" : s = new SpawnSoldierAnim(dId,x,y); break;
			case "Explode" : s = new ExplodeSoldier(dId,x,y); break;
			default: return;
		}

		if (model == "SpawnSoldierAnim")
			blaster.spawnDownObjects[dId] = s;
		else
			blaster.spawnTopObjects[dId] = s;
	},
	
	// Dodaje entity
	AddEntity : function(name, body) {
		var x;
		
		var id = Number(body.id);
		var model = body.model;

		switch (model) {
			case "Tiny" : x = new Tiny(); break;
			case "Light" : x = new Light(); break;
			case "Massive" : x = new Massive(); break;
			
			case "9mm" : x = new Ammo9mm(); return;
			case "40mm" : x = new Ammo40mm(); return;
			case "Rocket" : x = new AmmoRocket(); break;
			case "Tesla" : x = new AmmoTesla(); break;
			case "Shell" : x = new AmmoShell(); break;
			
			default:
				return;
		}
		
		blaster.objects[id] = x;
		
		x.id = body.id;
		x.model = body.model;
		
		if (model != "Bullet") {
			blaster.hp = x.maxHp;
			blaster.model = x.model;
			blaster.ammo = x.ammo;
		}
	},
	
	// Usuwa entity
	RemoveEntity : function(name, body) {
		var id = new Number(body.id);
		if (blaster.objects[id] == null)
			return;
		
		delete blaster.objects[id];
	},
	
	// Ustawia obiekt gracza
	YouAre : function(name, body) {
		var x = blaster.objects[ Number(body.id) ];
		
		if (x == null)
			return;
		
		blaster.moj_obiekt = x;
	},
	
	Color: function(name, body) {
		var id = new Number(body.id);
		var color = body.color;
		var value = null;
		
		switch (color) {
			case "czerwony" : value = "red"; break; 
			case "zielony" : value= "green"; break;
			case "niebieski" : value= "blue"; break;
			case "bialy" : value= "white"; break;
			case "zolty" : value= "yellow"; break;
			case "brazowy" : value= "brown"; break;
			case "pomaranczowy" : value= "orange"; break;
		}
		
		blaster.objects[id].color = value;
	},
	
	Ammo: function(name, body) {
		var id = new Number(body.id);
		var ammo = body.ammo;
		
		blaster.objects[id].ammo = ammo;
		Weapon.switchWeapon(blaster.objects[id]);
		
	},	
	
	// Ustawia atrybut
	SetAttribute : function(namep,body) {
		var name = body.name;
		var value = body.value;
		
		if (name == "hp") {
			$("#energia").text(Math.floor(Number(value)));
		}
		
		var x = blaster.moj_obiekt;
		if (!x) {
			x = blaster;
		}
		
		if (x[name] == null) {
			if (blaster[name] != null) {
				x = blaster;
			}
			else
				return;
		}
			
		x[name] = value;
	
	},
	
	// Aktualna klatka
	ServerFrame : function(name, body) {
		var f = Number(body.f);

		blaster.serverFrame = f;
		
		$("#serverFrame").html(blaster.serverFrame);
	},	
	
	// Aktualizacja pozycji
	UpdateRect : function(name,body) {

		var ent = blaster.objects[Number(body.id)];
		if (ent == null)
			return;
		
		ent.sync = {};
		var sync = ent.sync;
		
		sync.x = Number(body.x);
		sync.y = Number(body.y);
		sync.speed = Number(body.s);
		sync.angle = Number(body.a) * Math.PI / 180;
		sync.offset = blaster.frame - blaster.serverFrame;
		
		$("#offset").html(sync.offset);
		
		var where = Number(body.f);
		
		var x=0,y=0;
		
		if ( (where & FRONT) != 0) y=1;
		if ( (where & BACK) != 0) y=-1;
		if ( (where & RIGHT) != 0) x=1;
		if ( (where & LEFT) != 0) x=-1;
		
		sync.change_angle = x;
		sync.change_speed = y;
		
	},
	
	findTimestamp: function() {
		blaster.timestampSent = new Date().getTime();
		window.socket.send(new TanksPacket("TimeStamp"));
	},
	
	noTimestamp:true,
	timestampCount:0,
	timestampLimit:100,
	timestampDelay: 100,
	timestampSent: null,
	timestampLowestLatency:null,
	TimeStamp: function(name,body) {
		if (++blaster.timestampCount > blaster.timestampLimit) {
			
			// osiagnieto limit, ale nadal za duzy lag, wiec bedzie dalej sprawdzal
			if (blaster.timestampLowestLatency > 200) {
				blaster.timestampCount = 0;
				blaster.timestampDelay = 1000;	// co sekunde
				
			}
			else
				return;
		}
		var now = new Date().getTime();
		var time = Number(body.t);

		// zakładamy że leci w obie strony z ta sama szybkoscia
		var latency = Math.ceil(( now - blaster.timestampSent ) / 2);	 
		
		var setStartTime = function() {
			blaster.startTime = now - latency - time;
			blaster.noTimestamp = false;
			blaster.timestampLowestLatency = latency;
		}
		
		if (blaster.noTimestamp) {
			setStartTime();
			
		}else {
			if (latency < blaster.timestampLowestLatency) {
				blaster.timestampLowestLatency = latency;
				setStartTime();
			}
		}
		
		setTimeout(blaster.findTimestamp,blaster.timestampDelay);
	},
	
	// funkcjonalnosci
	
	setName: function(name) {
		window.socket.send(Set("name",name));
	},
	
	setColor: function(color) {
		window.socket.send(Set("color",color));
	},
	
	setModel: function(model) {
		window.socket.send(Set("model",model));
	},
	
	setAmmo: function(ammo) {
		window.socket.send(Set("ammo",ammo));
	},
	
	suicide: function() {
		window.socket.send(Set("Kill","CODE:0xBEEF"));
	},
	
	mousemove: function(x,y) {
		var cursor = blaster.topObjects["cursor"];
		cursor.x = x;
		cursor.y = y;
	},
	
	switchCursor: function() {
		blaster.mode = (blaster.mode + 1) % 2;
		var cursor = blaster.topObjects["cursor"];
		
		if (blaster.mode == 0) {
			cursor.color = cursor.moveColor;
		}
		else 
			cursor.color = cursor.fireColor;
	},
	
	haltKeyboard: true,
	keydown : function(key) {
		var keyCode = key.keyCode;
		var flags = blaster.flags;
		
		if (blaster.haltKeyboard)
			return;
		
		switch (keyCode) {
			case 39 : flags |= RIGHT; break;
			case 37 : flags |= LEFT; break;
			case 38 : flags |= FRONT; break;
			case 40 : flags |= BACK; break;
			case 17 : 
				if (blaster.attackState == false) {
					blaster.attackState = true;
					window.socket.send(new TanksPacket("Attack",{state:1}));
				}
				break;
			default: return;
		}
		
		if (blaster.flags == flags)
			return;
		
		blaster.flags = flags;
		window.socket.send(new TanksPacket("Move",{where:blaster.flags}));
	},
	
	keyup : function(key) {
		
		var keyCode = key.keyCode;
		var flags = 0;
		
		if (window.okienko.chatStatus && keyCode == 13) {
			window.okienko.wyslijChat();
			
			return;
		}
		
		if (blaster.haltKeyboard)
			return;
		
		switch (keyCode) {
			case 13 : okienko.otworzChat(); 
			case 39 : flags = RIGHT; break;
			case 37 : flags = LEFT; break;
			case 38 : flags = FRONT; break;
			case 40 : flags = BACK; break;
			case 17 : 
				if (blaster.attackState == true) {
					blaster.attackState = false;
					window.socket.send(new TanksPacket("Attack",{state:0}));
				}
				break;
			default: return;
		}
	
		blaster.flags = (blaster.flags & ~flags);
		window.socket.send(new TanksPacket("Move",{where:blaster.flags}));
	},
	
	clicked : function() {
		
		var cursor = blaster.topObjects["cursor"];
		cursor.simulate = cursor.pushed;
		cursor.currentFrame = 0;
		
		if (blaster.mode == 0) {
		//	blaster.move(cursor.x, cursor.y);
		}
		else
			//blaster.fire(cursor.x, cursor.y);
			
		return false;
	},
	
	move: function(x,y) {

		var obj = blaster.moj_obiekt;
		if (obj == null)
			return;
		
		obj.nowy_x = x;
		obj.nowy_y = y;
		
		var dlugosc_x = x - obj.x;
		var dlugosc_y = y - obj.y;
		var dlugosc_v = Math.sqrt(Math.pow(dlugosc_x,2) + Math.pow(dlugosc_y,2));
		
		var vX = obj.speed * dlugosc_x / dlugosc_v;
		var vY = obj.speed * dlugosc_y / dlugosc_v;
		
		vX = vX / blaster.serverFps;
		vY = vY / blaster.serverFps;
		
		
		window.socket.send(new TanksPacket("Move",{x:vX,y:vY}));
	},
	
	fire: function(x,y) {

		window.socket.send(new TanksPacket("Attack", {state:1}));
	},
	
	// uruchamiane co 10 ms
	refresh: function() {
		var objects = blaster.objects;
		var topObjects = blaster.topObjects;
		var spawnTopObjects = blaster.spawnTopObjects;
		var spawnDownObjects = blaster.spawnDownObjects;
			
		var now = new Date().getTime();
		var newFrame = Math.floor((now - blaster.startTime) / blaster.serverDelay);
		
		var step = newFrame - blaster.frame;	// ile klatek mineło
		blaster.frame = newFrame;
		$("#clientFrame").html(now+":"+blaster.startTime+":"+blaster.serverDelay+":"+blaster.frame);
		
		// Te obiekty nie sa synchronizowane
		for (i in spawnDownObjects) {
			spawnDownObjects[i].simulate(step);
		}
		
		// Symulacja i synchronizacja
		for (i in objects) {
			var obj = objects[i];
			var sync = obj.sync;
			if (sync != null) {
				var offset = sync.offset;

				delete sync.frame; 

				for (j in sync) {
					obj[j] = sync[j];
				}
				
				obj.simulate(step+offset,true);
				delete obj.sync;
			}
			else {
				obj.simulate(step);
			}
		}
		
		// Te obiekty nie sa synchronizowane
		for (i in spawnTopObjects) {
			spawnTopObjects[i].simulate(step);
		}
		
		// Te obiekty nie sa synchronizowane
		for (i in topObjects) {
			topObjects[i].simulate(step);
		}
	},
	
	draw: function() {
		var ctx = blaster.backContext;
		
		var elements = new Array();
		
		if (blaster.onConnect == true) {
			elements.push(blaster.spawnDownObjects);
			elements.push(blaster.objects);
			elements.push(blaster.spawnTopObjects);			
		}
		
		elements.push(blaster.topObjects);
		
		ctx.clearRect(0,0,640,480);

		for (j in elements) {
			var obj = elements[j];
			for (i in obj) {
				// ustaw obiekt tak, zeby 0,0 bylo srodkiem danego obiektu
				ctx.save();
					ctx.translate(obj[i].x, obj[i].y);
					ctx.rotate(obj[i].rotate);
					ctx.scale(obj[i].scale,obj[i].scale);
					
					obj[i].render(ctx);
				ctx.restore();
			}
		}
	}
}