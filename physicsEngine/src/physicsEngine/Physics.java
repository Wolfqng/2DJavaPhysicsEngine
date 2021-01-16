package physicsEngine;

import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;

public abstract class Physics {
	
	public static void applyForces(ArrayList<Collider<Object>> objects, double time) {
		
		for(Collider<Object> obj : objects) {
			checkWallCollisions(obj);
			obj.addAcel(new Accel(0, Enviroment.GRAVITY));
			ArrayList<CollisionEvent> collisions = obj.getOuterCollisions();
			if(!collisions.isEmpty()) {
				obj.setOuterCollision(true); //If there is an outer collision set it to be true
				
				ArrayList<Coord> edgePoints = obj.generateCollisionPoints();
		    	ArrayList<Collider<Object>> inBoxObjects = new ArrayList<>();
		    	for(CollisionEvent ce : collisions) //Add objects that are in obj's box collider to an array to be looped through
		    		inBoxObjects.add(ce.getP2());
		    	
				for(Coord ep : edgePoints) {
					for(Collider<Object> obj2 : inBoxObjects) {
						if(obj == obj2) continue; //Don't check for collisions within self
						CollisionEvent collision = obj2.isPointInCollider(ep.getX(), ep.getY(), obj);
						if(collision != null) 
							obj.setInnerCollision(true);
						
					}//for3
					
				}//for2
				checkWallCollisions(obj);
			}else
				obj.setOuterCollision(false);
			
			sumForces(obj, time);
			obj.translateX(obj.getVelX() * time);
			obj.translateY(obj.getVelY() * time);
			checkWallCollisions(obj);
			//Apply change in acceleration of objects here
		}//for1
		Enviroment.objects = objects;
	}//
	
	private static void sumForces(Collider<Object> c, double time)
	{
		Accel theAccel = c.sumAccel();
		// Apply the resulting change in velocity.
		double vx = c.getVelX() + (theAccel.getX() * time);
		double vy = c.getVelY() + (theAccel.getY() * time);
		c.setVelX(vx);
		c.setVelY(vy);
		//c.applyDrag(1.0 - (time * Main.DRAG));
	}
	
	private static void checkWallCollisions(Collider<Object> c)
	{
		int maxX = Enviroment.WIDTH;
		int maxY = Enviroment.HEIGHT;
		double x = 0;
		double y = 0;
		if(c instanceof Circle) {
			//maxX -= ((Circle)c).getRadius();
			//maxY -= ((Circle)c).getRadius();
			x = ((Circle)c).getX() + ((Circle) c).getRadius();
			y = ((Circle)c).getY() + ((Circle) c).getRadius();
		}
		else {
			//maxX -= Collider.getMax(Coord.getAllX(((Polygon)c).getCoords())) - Collider.getMin(Coord.getAllX(((Polygon)c).getCoords()));
			//maxY -= Collider.getMax(Coord.getAllY(((Polygon)c).getCoords())) - Collider.getMin(Coord.getAllY(((Polygon)c).getCoords()));
			x = Collider.getMax(Coord.getAllX(((Polygon)c).getCoords()));
			y = Collider.getMax(Coord.getAllY(((Polygon)c).getCoords()));
		}
		if(x > maxX) {
			//s.updatePos(s.getX(), maxY);
			c.translateX(-1 * (maxX - x));
			//s.updateVelocity(s.vx(), (s.vy() * -Main.BOUNCE));
		}
		if(y > maxY) {
			c.translateY(maxY - y);
			//s.updateVelocity((s.vx() * -Main.BOUNCE), s.vy());
		}
		if(x < 1) {
			//s.updatePos(1, s.getY());
			c.translateX(maxX - x + 1);
			//s.updateVelocity((s.vx() * -Main.BOUNCE), s.vy());
		}
	}
	
}
