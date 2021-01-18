package physicsEngine;

import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;

public abstract class Physics {
	
	public static void applyForces(ArrayList<Collider<Object>> objects, double time) {
		ArrayList<Collider<Object>> fObjects = new ArrayList<>(objects);
		
		for(int i = 0; i < objects.size(); i++) {
			Collider<Object> obj = objects.get(i);
			
			//obj.setInnerCollision(false);
			//checkWallCollisions(obj);
			obj.addAcel(new Accel(0, Enviroment.GRAVITY));
			
			ArrayList<CollisionEvent> collisions = obj.getOuterCollisions();
			if(!collisions.isEmpty()) {
				obj.setOuterCollision(true); //If there is an outer collision set it to be true
				
				obj.setCollisions(new ArrayList<>());
				
		    	ArrayList<Collider<Object>> inBoxObjects = new ArrayList<>();
		    	for(CollisionEvent ce : collisions) //Add objects that are in obj's box collider to an array to be looped through
		    		inBoxObjects.add(ce.getP2());
		    	
		    	ArrayList<Coord> edgePoints = obj.generateCollisionPoints();
				for(Coord ep : edgePoints) {
					for(Collider<Object> obj2 : inBoxObjects) {
						CollisionEvent collision = obj2.isPointInCollider(ep.getX(), ep.getY(), obj);
						if(collision != null) {
							//obj.setInnerCollision(true);
							obj.addCollision(collision);
							//System.out.println(((CollisionPointEvent)collision).getX());
						}
						//System.out.println();
						
					}//for
					
					if(!obj.getCollisions().isEmpty()) {
						obj.setInnerCollision(true); 
						break;
					}
					else obj.setInnerCollision(false);
					
				}//for2
				//checkWallCollisions(obj);
				
				
			}else
				obj.setOuterCollision(false);
			
			sumForces(obj, time);
			if(!checkWallCollisions(obj)) fObjects.get(i).translateX(obj.getVelX() * time); //this if statement might remove x acel so you may need to remove it
			if(!checkWallCollisions(obj)) fObjects.get(i).translateY(obj.getVelY() * time); //needed for not glitching through floor.
			checkWallCollisions(obj);
			//Apply change in acceleration of objects here
		}//for1
		Enviroment.objects = fObjects;
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
	
	private static boolean checkWallCollisions(Collider<Object> c)
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
			return true;
		}
		if(y > maxY) {
			c.translateY(maxY - y);
			//s.updateVelocity((s.vx() * -Main.BOUNCE), s.vy());
			return true;
		}
		if(x < 1) {
			//s.updatePos(1, s.getY());
			c.translateX(maxX - x + 1);
			//s.updateVelocity((s.vx() * -Main.BOUNCE), s.vy());
			return true;
		}
		
		return false;
	}
	
}
