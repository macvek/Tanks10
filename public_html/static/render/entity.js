var entity = {
		
	simulate : function(step,force){
			//pierwszy update ustawia wartosci wspolrzednych
			this.simulate = this.simulateMe;
			this.render = this.renderMe;
			this.simulate(step,force);
	},
	
	synchronize : function() {},
	
	simulateMe : function(step,force) {
		
		if (this.model == "Bullet") {
			this.rotate += Math.PI/180;
		} 
		
		with(this) {
			if (change_angle != 0 || force) {
				angle += change_angle * rotateSpeed * step;

				direction[0] = -Math.sin(angle);
				direction[1] = Math.cos(angle);
				
				rotate = angle;
			}	
				
			if (change_speed != 0 || force) {
				speed += change_speed * acc * step;
				
				if (speed > maxSpeed)
					speed = maxSpeed;
				
				if (speed < -maxSpeed)
					speed = -maxSpeed;
				
			}else {
				if (speed != 0) {
					var stopAccel = stopAcc * step;
					if (Math.abs(speed) <= stopAccel * 1.5) {
						speed = 0;
					}
					else if (speed < 0)
						speed += stopAccel;
					else {
						speed -= stopAccel;
					}
					
				}
			}
		
			x += direction[0] * speed * step;
			y += direction[1] * speed * step;
		}
		
	},

}