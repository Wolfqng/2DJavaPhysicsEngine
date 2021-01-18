package physicsEngine;

import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;
import physicsEngine.events.CollisionPointEvent;

public abstract class Physics {
	
	public static void applyForces(ArrayList<Collider<Object>> objects, double time) {		
		for(Collider<Object> obj : objects) {
			checkObjectCollision(obj);
			obj.addAcel(new Accel(0, Enviroment.GRAVITY));
			sumForces(obj, time);
			if(!checkWallCollisions(obj)) obj.translateX(obj.getVelX() * time); //this if statement might remove x acel so you may need to remove it
			if(!checkWallCollisions(obj)) obj.translateY(obj.getVelY() * time); //if statement needed for not glitching through floor.
		}
	}
	
	public static void sumForces(Collider<Object> c, double time) {
		Accel theAccel = c.sumAccel();
		double vx = c.getVelX() + (theAccel.getX() * time);
		double vy = c.getVelY() + (theAccel.getY() * time);
		c.setVelX(vx);
		c.setVelY(vy);
		//c.applyDrag(1.0 - (time * Main.DRAG));
	}
	
	public static CollisionEvent checkObjectCollision(Collider<Object> c) {
		ArrayList<CollisionEvent> collisions = c.getOuterCollisions();
		if(!collisions.isEmpty()) c.setOuterCollision(true);
		else c.setOuterCollision(false);
		
		ArrayList<Collider<Object>> objects = new ArrayList<>();
		for(CollisionEvent ce : collisions) 
			objects.add(ce.getP2());
		
		for(Collider<Object> obj : objects) {
			ArrayList<Coord> edgePoints = c.generateCollisionPoints();
			for(Coord ep : edgePoints) {
				CollisionPointEvent cpe = obj.isPointInCollider(ep.getX(), ep.getY(), c);
				if(cpe != null) {
					obj.setInnerCollision(true);
					c.setInnerCollision(true);
					return cpe;
				}
			}
			obj.setInnerCollision(false);
		}
		return null;
	}
	
	public static boolean checkWallCollisions(Collider<Object> c)
	{
		int maxX = Enviroment.WIDTH;
		int maxY = Enviroment.HEIGHT;
		double x = 0;
		double y = 0;
		if(c instanceof Circle) {
			x = ((Circle)c).getX() + ((Circle) c).getRadius();
			y = ((Circle)c).getY() + ((Circle) c).getRadius();
		}
		else {
			x = Collider.getMax(Coord.getAllX(((Polygon)c).getCoords()));
			y = Collider.getMax(Coord.getAllY(((Polygon)c).getCoords()));
		}
		
		if(x > maxX) {                        //One of these x statements will not work, will work on later
			c.translateX(-1 * (maxX - x));
			c.setVelX(0);
			return true;
		}
		if(y > maxY) {
			c.translateY(maxY - y);
			c.setVelY(0);
			return true;
		}
		if(x < 1) {
			c.translateX(maxX - x + 1);
			c.setVelX(0);
			return true;
		}
		
		return false;
	}
	
}
