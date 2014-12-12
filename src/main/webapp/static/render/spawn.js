var drawElipse = function(ctx, x, y, r, color) {
    ctx.fillStyle = color;
    ctx.beginPath();
    ctx.arc(x, y, r, 0, Math.PI * 2, false);
    ctx.stroke();
    ctx.fill();
    ctx.closePath();
};

var ExplodeSimulation = {
    explode9mmSimulate: function(step) {
        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
    },
    explode40mmSimulate: function(step) {
        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
    },
    explodeRocketSimulate: function(step) {
        this.animLength = 60;

        var nR = 218;
        var nG = 37;
        var nB = 37;

        var oR = 250;
        var oG = 200;
        var oB = 60;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];

        if ((this.currentFrame >= 0) && (this.currentFrame < 60)) {
            var now1 = this.currentFrame / 60;
            var sin1 = Math.sin(Math.PI * now1);

            this.range1 = sin1 * this.rocketRange;

            var r = Math.floor(oR + sin1 * (nR - oR));
            var g = Math.floor(oG + sin1 * (nG - oG));
            var b = Math.floor(oB + sin1 * (nB - oB));

            this.color1 = "rgba(" + r + "," + g + "," + b + ",0.5)";
        }
        if ((this.currentFrame >= 20) && (this.currentFrame < 50)) {
            var now2 = (this.currentFrame - 30) / 30;
            var sin2 = Math.sin(Math.PI * now2);

            this.range2 = sin2 * this.rocketRange;

            var r = Math.floor(oR + sin2 * (nR - oR));
            var g = Math.floor(oG + sin2 * (nG - oG));
            var b = Math.floor(oB + sin2 * (nB - oB));

            this.color2 = "rgba(" + r + "," + g + "," + b + ",0.5)";
        }
        if ((this.currentFrame >= 30) && (this.currentFrame < 60)) {
            var now3 = (this.currentFrame - 60) / 30;
            var sin3 = Math.sin(Math.PI * now3);

            this.range3 = sin3 * this.rocketRange;

            var r = Math.floor(oR + sin3 * (nR - oR));
            var g = Math.floor(oG + sin3 * (nG - oG));
            var b = Math.floor(oB + sin3 * (nB - oB));

            this.color3 = "rgba(" + r + "," + g + "," + b + ",0.5)";
        }
    },
    explodeTeslaSimulate: function(step) {
        this.animLength = 60;

        var nR = 77;
        var nG = 156;
        var nB = 168;

        var oR = 255;
        var oG = 255;
        var oB = 255;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];

        var now1 = this.currentFrame / 60;
        var sin1 = Math.sin(Math.PI * now1);

        this.range1 = sin1 * this.teslaRange;

        var r = Math.floor(oR + sin1 * (nR - oR));
        var g = Math.floor(oG + sin1 * (nG - oG));
        var b = Math.floor(oB + sin1 * (nB - oB));

        this.color1 = "rgba(" + r + "," + g + "," + b + ",0.5)";
    },
    explodeShellSimulate: function(step) {
        this.animLength = 70;

        var nR = 18;
        var nG = 230;
        var nB = 7;

        var oR = 18;
        var oG = 7;
        var oB = 230;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];

        var now1 = this.currentFrame / 60;
        var sin1 = Math.sin(Math.PI * now1);

        this.range1 = sin1 * this.shellRange;

        var r = Math.floor(oR + sin1 * (nR - oR));
        var g = Math.floor(oG + sin1 * (nG - oG));
        var b = Math.floor(oB + sin1 * (nB - oB));

        this.color1 = "rgba(" + r + "," + g + "," + b + ",0.6)";
    },
    switchExplode: function(explode) {
        if (!explode)
            return;

        switch (explode.type) {
            case AMMO_9MM :
                explode.simulate = this.explode9mmSimulate;
                break;
            case AMMO_40MM :
                explode.simulate = this.explode40mmSimulate;
                break;
            case AMMO_ROCKET :
                explode.simulate = this.explodeRocketSimulate;
                break;
            case AMMO_TESLA :
                explode.simulate = this.explodeTeslaSimulate;
                break;
            case AMMO_SHELL :
                explode.simulate = this.explodeShellSimulate;
                break;
        }
    }

};

var ExplodeRender = {
    explode9mmRender: function(ctx) {
        ctx.strokeStyle = "orange";
        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;
            var y = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;

            ctx.moveTo(0, 0);
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    },
    explode40mmRender: function(ctx) {
        ctx.strokeStyle = "orange";
        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;
            var y = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;

            ctx.moveTo(0, 0);
            ctx.lineTo(x, y);
        }
        ctx.stroke();
    },
    explodeRocketRender: function(ctx) {
        drawElipse(ctx, 0, 0, Math.abs(this.range1 * 0.5), this.color1);
        drawElipse(ctx, 20, 20, Math.abs(this.range2 * 0.5), this.color2);
        drawElipse(ctx, -20, -20, Math.abs(this.range3 * 0.5), this.color3);
    },
    explodeTeslaRender: function(ctx) {
        drawElipse(ctx, 0, 0, this.range1 * 0.5, this.color1);

        ctx.strokeStyle = "#ffffff";
        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() : -Math.random();
            var y = (Math.random() > 0.5) ? Math.random() : -Math.random();

            ctx.moveTo(0, 0);
            ctx.lineTo(x * this.range1 * 0.5, y * this.range1 * 0.5);
        }
        ctx.stroke();
    },
    explodeShellRender: function(ctx) {
        drawElipse(ctx, 0, 0, Math.abs(this.range1 * 0.5), this.color1);
    },
    switchExplode: function(explode) {
        if (!explode)
            return;

        switch (explode.type) {
            case AMMO_9MM :
                explode.render = this.explode9mmRender;
                break;
            case AMMO_40MM :
                explode.render = this.explode40mmRender;
                break;
            case AMMO_ROCKET :
                explode.render = this.explodeRocketRender;
                break;
            case AMMO_TESLA :
                explode.render = this.explodeTeslaRender;
                break;
            case AMMO_SHELL :
                explode.render = this.explodeShellRender;
                break;
        }
    }

};

var Explode = function(type, id, x, y) {

    this.id = id;
    this.type = type;
    this.scale = 1;
    this.rotate = 0;
    this.r = 250;
    this.g = 200;
    this.b = 60;
    this.color1 = "rgb(" + this.r + "," + this.g + "," + this.b + ")";
    this.color2 = "rgb(" + this.r + "," + this.g + "," + this.b + ")";
    this.color3 = "rgb(" + this.r + "," + this.g + "," + this.b + ")";
    this.x = x;
    this.y = y;
    this.currentFrame = 0;
    this.animLength = 50;
    this.radius = 50;
    this.range1 = 1;
    this.range2 = 1;
    this.range3 = 1;


    this.rocketRange = 100;
    this.teslaRange = 100;
    this.shellRange = 60;

    ExplodeSimulation.switchExplode(this);
    ExplodeRender.switchExplode(this);
};

var FireSimulation = {
    fire9mmSimulate: function(step) {
        this.animLength = 20;
        this.x = this.owner.x;
        this.y = this.owner.y;
        this.rotate = this.owner.rotate;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
        var now = this.currentFrame / this.animLength;
    },
    fire40mmSimulate: function(step) {
        this.animLength = 20;
        this.x = this.owner.x;
        this.y = this.owner.y;
        this.rotate = this.owner.rotate;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
        var now = this.currentFrame / this.animLength;
    },
    fireRocketSimulate: function(step) {
        this.animLength = 20;
        this.x = this.owner.x;
        this.y = this.owner.y;
        this.rotate = this.owner.rotate;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
        var now = this.currentFrame / this.animLength;
    },
    fireTeslaSimulate: function(step) {
        this.animLength = 30;

        this.x = this.owner.x;
        this.y = this.owner.y;
        this.rotate = this.owner.rotate;

        var nR = 77;
        var nG = 156;
        var nB = 168;

        var oR = 255;
        var oG = 255;
        var oB = 255;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];

        var now = this.currentFrame / 60;
        var sin = Math.sin(Math.PI * now);

        this.range = sin * 10;

        var r = Math.floor(oR + sin * (nR - oR));
        var g = Math.floor(oG + sin * (nG - oG));
        var b = Math.floor(oB + sin * (nB - oB));

        this.color = "rgba(" + r + "," + g + "," + b + ",0.5)";
    },
    fireShellSimulate: function(step) {
        this.animLength = 30;

        this.x = this.owner.x;
        this.y = this.owner.y;
        this.rotate = this.owner.rotate;

        var nR = 224;
        var nG = 11;
        var nB = 72;

        var oR = 65;
        var oG = 224;
        var oB = 11;

        this.currentFrame += step;
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];

        var now = this.currentFrame / 60;
        var sin = Math.sin(Math.PI * now);

        this.range = sin * 20;

        var r = Math.floor(oR + sin * (nR - oR));
        var g = Math.floor(oG + sin * (nG - oG));
        var b = Math.floor(oB + sin * (nB - oB));

        this.color = "rgba(" + r + "," + g + "," + b + ",0.5)";
    },
    switchFire: function(fire) {
        if (!fire)
            return;

        switch (fire.type) {
            case AMMO_9MM :
                fire.simulate = this.fire9mmSimulate;
                break;
            case AMMO_40MM :
                fire.simulate = this.fire40mmSimulate;
                break;
            case AMMO_ROCKET :
                fire.simulate = this.fireRocketSimulate;
                break;
            case AMMO_TESLA :
                fire.simulate = this.fireTeslaSimulate;
                break;
            case AMMO_SHELL :
                fire.simulate = this.fireShellSimulate;
                break;
        }
    }
};

var FireRender = {
    fire9mmRender: function(ctx) {
        ctx.strokeStyle = "orange";
        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;
            var y = Math.random() * 15 + 5;

            ctx.moveTo(0, 10);
            ctx.lineTo(x, y);

        }
        ctx.stroke();
    },
    fire40mmRender: function(ctx) {
        ctx.strokeStyle = "orange";
        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;
            var y = Math.random() * 15 + 5;

            ctx.moveTo(0, 10);
            ctx.lineTo(x, y);

        }
        ctx.stroke();
    },
    fireRocketRender: function(ctx) {
        ctx.strokeStyle = "orange";
        ctx.fillStyle = "orange";

        ctx.beginPath();
        for (var i = 0; i < 5; i++) {

            var x = (Math.random() > 0.5) ? Math.random() * 10 : -Math.random() * 10;
            var y = Math.random() * 15 + 5;

            ctx.moveTo(0, 17);
            ctx.lineTo(x, 17 + y);
            ctx.lineTo(-x, 17 + y);
            ctx.fill();

        }
        ctx.closePath();
    },
    fireTeslaRender: function(ctx) {
        drawElipse(ctx, 0, 20, this.range, this.color);
    },
    fireShellRender: function(ctx) {
        drawElipse(ctx, 0, 20, this.range, this.color);
    },
    switchFire: function(fire) {
        if (!fire)
            return;

        switch (fire.type) {
            case AMMO_9MM :
                fire.render = this.fire9mmRender;
                break;
            case AMMO_40MM :
                fire.render = this.fire40mmRender;
                break;
            case AMMO_ROCKET :
                fire.render = this.fireRocketRender;
                break;
            case AMMO_TESLA :
                fire.render = this.fireTeslaRender;
                break;
            case AMMO_SHELL :
                fire.render = this.fireShellRender;
                break;
        }
    }
};

var Fire = function(type, id, tankId, x, y) {

    this.id = id;
    this.owner = blaster.objects[tankId];
    this.type = type;
    this.scale = 1;
    this.rotate = 0;
    this.r = Math.floor(Math.random() * 255);
    this.g = Math.floor(Math.random() * 255);
    this.b = Math.floor(Math.random() * 255);
    this.color = "rgb(" + this.r + "," + this.g + "," + this.b + ")";
    this.x = x;
    this.y = y;
    this.currentFrame = 0;
    this.range;

    FireSimulation.switchFire(this);
    FireRender.switchFire(this);
};

var SpawnSoldierAnim = function(id, x, y) {

    this.id = id;
    this.scale = 1;
    this.rotate = 0;
    this.r = Math.floor(Math.random() * 0x88) + 0x87;
    this.g = Math.floor(Math.random() * 0x88) + 0x87;
    this.b = Math.floor(Math.random() * 0x88) + 0x87;
    this.color = "rgb(" + this.r + "," + this.g + "," + this.b + ")";
    this.x = x;
    this.y = y;
    this.currentFrame = 0;
    this.animLength = 50;


    this.simulate = function(step) {
        this.currentFrame += step;

        // animacja trwa animLength klatek
        if (this.currentFrame > this.animLength)
            delete blaster.spawnDownObjects[this.id];

        var now = this.currentFrame / this.animLength;
        this.rotate = Math.sin(Math.PI * now);

    };

    this.render = function(ctx) {
        ctx.fillStyle = this.color;
        ctx.fillRect(-40, -2, 80, 4);
        ctx.fillRect(-2, -40, 4, 80);
    };
};

var ExplodeSoldier = function(id, x, y) {

    this.id = id;
    this.scale = 1;
    this.rotate = 0;
    this.color1 = "rgba(255,86,86,0.5)";
    this.color2 = "rgba(255,236,86,0.5)";
    this.x = x;
    this.y = y;
    this.currentFrame = 0;
    this.animLength = 50;


    this.simulate = function(step) {
        this.currentFrame += step;

        // animacja trwa animLength klatek
        if (this.currentFrame > this.animLength)
            delete blaster.spawnTopObjects[this.id];
    };

    this.render = function(ctx) {

        var r1 = Math.random() * 20 + 10;
        var r2 = Math.random() * 10 + 10;

        drawElipse(ctx, Math.random() * 10 - 5, Math.random() * 10 - 5, r1, this.color1);
        drawElipse(ctx, Math.random() * 10 - 5, Math.random() * 10 - 5, r2, this.color2);
    };
};