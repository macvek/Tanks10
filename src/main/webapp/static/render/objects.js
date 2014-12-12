var AMMO_9MM = 0;
var AMMO_40MM = 1;
var AMMO_ROCKET = 2;
var AMMO_TESLA = 3;
var AMMO_SHELL = 4;

// Kursor myszki
var cursor = function() {
	// domyslnie jest to kursor
	this.scale = 1;
	this.rotate = 0;
	this.color = "#ffffff";
	this.moveColor = "#ffffff";
	this.fireColor = "#ff0000";
	this.x = 320;
	this.y = 240;
	this.currentFrame = 0;
	this.animLength = 200;
	this.stopAcc = 0;
	this.idle = function(step) {
		this.currentFrame += step;
		
		// animacja trwa animLength klatek
		if (this.currentFrame > this.animLength)
			this.currentFrame -= this.animLength;
			
		var now = this.currentFrame / this.animLength;
		this.rotate = Math.sin(Math.PI * 2 * now) * Math.PI; 
		
	}
	
	this.pushed = function(step) {
		this.rotate = 0;
		this.currentFrame += step;
		
		var now = this.currentFrame / 50;	// trwa pól sekundy
		this.scale = Math.sin(Math.PI * now) * 2 + 1;
		
		if (this.currentFrame > 50) {
			this.scale = 1;
			this.simulate = this.idle;
			this.currentFrame = 0;
		}
	}
	
	this.simulate = this.idle;
	
	this.render = function(ctx) {
		ctx.fillStyle = this.color;
		ctx.fillRect(-5,-2,10,4);
		ctx.fillRect(-2,-5,4,10);
	}
}

var Tiny = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 50;
	this.y = 50;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0.1;
	this.maxSpeed = 2;
	this.rotateSpeed = 4.0 * Math.PI / 180;
	this.direction = new Array(0,1);
	this.currentFrame = 0;	// wzgledem animacji
	this.animLength = 200;
	this.color = "#0000ff";
	this.stopAcc = 0.02;
	this.maxHp = 250;
	this.ammo = 0;
	
	this.render = function(){}
	
	Weapon.switchWeapon(this);
	
	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;
		
		ctx.strokeStyle = "#000000";
		var rysujPoklad = function() {
			
			ctx.fillStyle = self.color;
			ctx.beginPath();

			ctx.moveTo(-11,-7);
			ctx.lineTo(11,-7);
			ctx.lineTo(11,7);
			ctx.lineTo(-11,7);
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
			
			ctx.moveTo(-6,-4);
			ctx.lineTo(6,-4);
			ctx.lineTo(6,4);
			ctx.lineTo(-6,4);
			ctx.closePath();
			
			ctx.stroke();
		}
		
		var rysujPlozy = function() {
			
			ctx.fillStyle = "#000000";
			ctx.beginPath();
			
			ctx.moveTo(-14,-8);
			ctx.lineTo(-9,-8);
			ctx.lineTo(-9,8);
			ctx.lineTo(-14,8);
			ctx.closePath();
			
			ctx.moveTo(9,-8);
			ctx.lineTo(14,-8);
			ctx.lineTo(14,8);
			ctx.lineTo(9,8);
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
		}
		
		var rysujBron = self.renderWeapon;
		
		rysujPlozy();
		rysujPoklad();
		rysujBron(ctx);

	}
}

var Light = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 50;
	this.y = 50;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0.05;
	this.maxSpeed = 1.5;
	this.masa = 5;
	this.rotateSpeed = 4.0 * Math.PI / 180;
	this.direction = new Array(0,1);
	this.currentFrame = 0;	// wzgledem animacji
	this.animLength = 200;
	this.color = "#c34113";
	this.stopAcc = 0.01;
	this.maxHp = 250;
	this.ammo = 0;
	
	this.render = function(){}
	
	Weapon.switchWeapon(this);
	
	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;
		
		ctx.strokeStyle = "#000000";
		var rysujPoklad = function() {
			
			ctx.fillStyle = self.color;
			ctx.beginPath();

			ctx.moveTo(-7,-12);
			ctx.lineTo(7,-12);
			ctx.lineTo(5,13);
			ctx.lineTo(-5,13);
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
			
			ctx.moveTo(-4,-8);
			ctx.lineTo(4,-8);
			ctx.lineTo(3,5);
			ctx.lineTo(-3,5);
			ctx.closePath();
			
			ctx.stroke();
		}
		
		var rysujPlozy = function() {
			
			ctx.fillStyle = "#000000";
			ctx.beginPath();
			
			ctx.moveTo(-9,-13);
			ctx.lineTo(-3,-13);
			ctx.lineTo(-3,11);
			ctx.lineTo(-9,11);
			ctx.closePath();
			
			ctx.moveTo(3,-13);
			ctx.lineTo(9,-13);
			ctx.lineTo(9,11);
			ctx.lineTo(3,11);
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
		}
		
		var rysujBron = self.renderWeapon;
		
		rysujPlozy();
		rysujPoklad();
		rysujBron(ctx);

	}
}

var Massive = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 50;
	this.y = 50;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0.025;
	this.maxSpeed = 1;
	this.rotateSpeed = 4.0 * Math.PI / 180;
	this.direction = new Array(0,1);
	this.currentFrame = 0;	// wzgledem animacji
	this.animLength = 200;
	this.color = "#266826";
	this.stopAcc = 0.05;
	this.maxHp = 250;
	this.ammo = 0;
	
	this.render = function(){}
	
	Weapon.switchWeapon(this);
	
	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;
		
		var rysujPoklad = function() {
			
			ctx.strokeStyle = "#000000";
			ctx.fillStyle = self.color;
			ctx.beginPath();

			ctx.moveTo(-12,-13);
			ctx.lineTo(12,-13);
			ctx.lineTo(12,-8);
			ctx.lineTo(10,-8);
			ctx.lineTo(10,8);
			ctx.lineTo(12,8);
			ctx.lineTo(12,13);
			
			ctx.lineTo(-12,13);
			ctx.lineTo(-12,8);
			ctx.lineTo(-10,8);
			ctx.lineTo(-10,-8);
			ctx.lineTo(-12,-8);
			ctx.lineTo(-12,-13);
			
			
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
			
			ctx.moveTo(-7,-7);
			ctx.lineTo(7,-7);
			ctx.lineTo(7,7);
			ctx.lineTo(-7,7);
			ctx.closePath();
			
			ctx.stroke();
		}
		
		var rysujPlozy = function() {
			
			ctx.strokeStyle = "#000000";
			ctx.fillStyle = "#000000";
			ctx.beginPath();
			
			ctx.moveTo(-13,-12);
			ctx.lineTo(-10,-12);
			ctx.lineTo(-10,12);
			ctx.lineTo(-13,12);
			ctx.closePath();
			
			ctx.moveTo(10,-12);
			ctx.lineTo(13,-12);
			ctx.lineTo(13,12);
			ctx.lineTo(10,12);
			ctx.closePath();
			
			ctx.fill();
			ctx.stroke();
		}
		
		var rysujBron = self.renderWeapon;
		
		rysujPlozy();
		rysujPoklad();
		rysujBron(ctx);

	}
}

var Weapon = {
	
	switchWeapon : function(tank) {
		var type = Number(tank.ammo);
		
		alert("! "+type);
		
		if (type == null)
			return;
		
		switch (type) {
			case AMMO_9MM: tank.renderWeapon = this.render9mm; break;
			case AMMO_40MM: tank.renderWeapon = this.render40mm; break;
			case AMMO_ROCKET: tank.renderWeapon = this.renderRocket; break;
			case AMMO_TESLA: tank.renderWeapon = this.renderTesla; break;
			case AMMO_SHELL: tank.renderWeapon = this.renderShell; break;
		}
	},
	
	render9mm : function(ctx) {
		ctx.fillStyle = "#000000";
		ctx.beginPath();
		
		ctx.moveTo(-1,0);
		ctx.lineTo(-1,10);
		ctx.lineTo(1,10);
		ctx.lineTo(1,0);
		
		ctx.closePath();
		ctx.fill();
	},
	
	render40mm : function(ctx) {
		ctx.fillStyle = "#000000";
		ctx.beginPath();
		
		ctx.moveTo(-3,0);
		ctx.lineTo(-3,10);
		ctx.lineTo(-1,10);
		ctx.lineTo(-1,0);

		ctx.moveTo(1,0);
		ctx.lineTo(1,10);
		ctx.lineTo(3,10);
		ctx.lineTo(3,0);
		
		ctx.closePath();
		ctx.fill();
	},
	
	renderRocket : function(ctx) {
		ctx.fillStyle = "#000000";
		ctx.beginPath();
		
		ctx.moveTo(-3,0);
		ctx.lineTo(-3,15);
		ctx.lineTo(3,15);
		ctx.lineTo(3,0);
		
		ctx.closePath();
		ctx.fill();
		
		ctx.fillStyle = "#ff0000";
		ctx.strokeStyle = "#000000";
		ctx.beginPath();
		
		ctx.moveTo(-3,15);
		ctx.lineTo(-5,18);
		ctx.lineTo(5,18);
		ctx.lineTo(3,15);
		
		ctx.closePath();
		ctx.stroke();
		ctx.fill();		
	},
	
	renderTesla : function(ctx) {
		ctx.strokeStyle = "#000000";
		ctx.fillStyle = "#A4D6DE";
		ctx.beginPath();
		
		ctx.moveTo(0,0);
		ctx.lineTo(-10,0);
		ctx.lineTo(-10,3);
		ctx.lineTo(-2,3);
		ctx.lineTo(-2,6);
		
		ctx.lineTo(-7,6);
		ctx.lineTo(-7,9);
		ctx.lineTo(-2,9);
		ctx.lineTo(-2,12);
		
		ctx.lineTo(-3,12);
		ctx.lineTo(-3,15);
		ctx.lineTo(-2,15);
		ctx.lineTo(-2,16);

		ctx.moveTo(0,0);
		ctx.lineTo(10,0);
		ctx.lineTo(10,3);
		ctx.lineTo(2,3);
		ctx.lineTo(2,6);
		
		ctx.lineTo(7,6);
		ctx.lineTo(7,9);
		ctx.lineTo(2,9);
		ctx.lineTo(2,12);
		
		ctx.lineTo(3,12);
		ctx.lineTo(3,15);
		ctx.lineTo(2,15);
		ctx.lineTo(2,16);		
		ctx.closePath();
		ctx.fill();
		
		drawElipse(ctx,0,16,3,"#A4D6DE");		
	},
	
	renderShell : function(ctx) {
		ctx.fillStyle = "#31990E";
		ctx.strokeStyle = "#000000";
		ctx.beginPath();
		
		ctx.moveTo(0,0);
		ctx.lineTo(-4,0);
		ctx.lineTo(-4,12);
		ctx.lineTo(-1,12);
		ctx.lineTo(-1,15);
		ctx.lineTo(-5,15);
		ctx.lineTo(-5,20);
		ctx.lineTo(0,20);
		
		ctx.moveTo(0,0);
		ctx.lineTo(4,0);
		ctx.lineTo(4,12);
		ctx.lineTo(1,12);
		ctx.lineTo(1,15);
		ctx.lineTo(5,15);
		ctx.lineTo(5,20);
		ctx.lineTo(0,20);
		
		ctx.closePath();
		ctx.fill();
		ctx.stroke();			
	}
}

var Ammo9mm = function() {

}

var Ammo40mm = function() {

}

var AmmoRocket = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 0;
	this.y = 0;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0;
	this.maxSpeed = 10;
	this.rotateSpeed = 0;
	this.direction = new Array(0,1);
	this.animLength = 200;
	this.color = "#ffffff";
	this.color2 = "#0000aa";
	this.thrust = new Array(0.2,0.2,0.5,0,0,0);
	this.cannonR = 0;
	this.stopAcc = 0;
	
	this.render = function(){}

	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;

		ctx.fillStyle = self.color;
		ctx.strokeStyle = "#000000";
		var rysujPoklad = function() {
			ctx.beginPath();

			ctx.moveTo(-4,0);
			ctx.lineTo(-2,2);
			ctx.lineTo(-2,6);
			ctx.lineTo(2,6);
			ctx.lineTo(2,2);
			ctx.lineTo(4,0);
			
			ctx.closePath();
			ctx.fill();
			ctx.stroke();
			
			ctx.fillStyle = "#FF002B";
			ctx.beginPath();
			ctx.moveTo(-2,6);
			ctx.lineTo(0,10);
			ctx.lineTo(2,6);
			ctx.closePath();
			ctx.fill();
			ctx.stroke();
		}
		
		var rysujOdrzut = function() {

		}
		rysujPoklad();
	}	
}

var AmmoTesla = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 0;
	this.y = 0;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0;
	this.maxSpeed = 10;
	this.rotateSpeed = 0;
	this.direction = new Array(0,1);
	this.animLength = 200;
	this.color = "#79BDC7";
	this.color2 = "#0000aa";
	this.thrust = new Array(0.2,0.2,0.5,0,0,0);
	this.cannonR = 0;
	this.stopAcc = 0;
	
	this.render = function(){}

	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;
	
		ctx.fillStyle = self.color;
		ctx.strokeStyle = "#000000";
		var rysujPoklad = function() {
			drawElipse(ctx,0,0,5,this.color);
		}
		
		var rysujOdrzut = function() {

		}
		rysujPoklad();
	}	
}

var AmmoShell = function() {
	this.id = null;
	this.scale = 1;
	this.rotate = 0;
	this.speed = 0;
	this.angle = 0;
	this.x = 0;
	this.y = 0;
	this.change_angle = 0;
	this.change_speed = 0;
	this.acc = 0;
	this.maxSpeed = 10;
	this.rotateSpeed = 0;
	this.direction = new Array(0,1);
	this.animLength = 200;
	this.color = "#0BE336";
	this.color2 = "#0000aa";
	this.thrust = new Array(0.2,0.2,0.5,0,0,0);
	this.cannonR = 0;
	this.stopAcc = 0;
	
	this.render = function(){}

	this.simulate = entity.simulate;
	this.synchronize = entity.synchronize;
	this.simulateMe = entity.simulateMe;
	
	this.renderMe = function(ctx) {
		var self = this;

		ctx.fillStyle = self.color;
		ctx.strokeStyle = "#000000";
		var rysujPoklad = function() {
			drawElipse(ctx,0,0,4,this.color);
		}
		rysujPoklad();
	}	
}