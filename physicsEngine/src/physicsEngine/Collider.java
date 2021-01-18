package physicsEngine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;
import physicsEngine.events.CollisionPointEvent;

public abstract class Collider<T> {
	private Color c;
	private double rotation, mass, velX, velY;
	private boolean outerCollision, innerCollision;
	private ArrayList<T> collisions = new ArrayList<>();
	private ArrayList<Accel> accelerations = new ArrayList<>();
	private ArrayList<Coord> colliderPoints = new ArrayList<>();
	
	public abstract CollisionPointEvent isPointInCollider(double x, double y, Collider<Object> p);
	public abstract ArrayList<Coord> generateCollisionPoints();
	public abstract void rotate(double deg);
	public abstract void draw(Graphics2D g2d, Paint p, boolean fill);
	public abstract void translateX(double amt);
	public abstract void translateY(double amt);
	
	public Collider(Color c, double rotation, double mass) {
		super();
		this.c = c;
		this.rotation = rotation;
		this.mass = mass;
	}

	public void update() {
		//Check for collisions
		//Apply external forces
		//Update position 
		
		//Each Polygon should have a list of collisions for square and points collisions
		//Then you can check through each polygons list of collisions and if they already collided
		//then you dont have to do the calculations again
		this.innerCollision = false;
	}
	
	//converts double array to an integer array
	public static int[] doubleToIntArray(double[] source) {
	    int[] dest = new int[source.length];
	    for(int i = 0; i < source.length; i++) {
	        dest[i] = (int)source[i];
	    }
	    return dest;
	}
	
	//Checks to see if the outer collision box is colliding with any other object
	public ArrayList<CollisionEvent> getOuterCollisions() {
		ArrayList<Collider<Object>> objects = Enviroment.objects; //Can decide what objects should be here for optimization
		ArrayList<CollisionEvent> collisions = new ArrayList<>();
		for(Collider<Object> obj : objects) {
			if(!(obj == this)) {
				@SuppressWarnings({ "unchecked", "rawtypes" })
				CollisionEvent ce = checkBoxCollision((Collider)this, obj);
				if(ce != null)
					collisions.add(ce);
			}
		}
		
		return collisions;
	}
	
	//checks if there is a collision in a rectangle
	public static CollisionEvent checkBoxCollision(Collider<Object> p1, Collider<Object> p2) {
		ArrayList<Coord> coords1 = p1.outerColliderPoints();
		ArrayList<Coord> coords2 = p2.outerColliderPoints();
		boolean collision = true;
		
		for(int i = 0; i < 2; i++) {
			if(coords1.get(0).getX() > coords2.get(1).getX()) collision = false;
			if(coords1.get(0).getY() > coords2.get(2).getY()) collision = false;
			
			if(coords1.get(1).getX() < coords2.get(0).getX()) collision = false;
			if(coords1.get(2).getY() < coords2.get(0).getY()) collision = false;
			
			if(collision) {
				CollisionEvent ce = (CollisionEvent) p1.addCollision(new CollisionEvent(p1, p2));
				p1.addCollision(ce);
				return new CollisionEvent(p1, p2);
			}
			
			ArrayList<Coord> temp = coords1;
			coords1 = coords2;
			coords2 = temp;
			collision = true;
		}
		
		return null;
	}
	
	public ArrayList<Coord> outerColliderPoints() {
		if(this instanceof Circle) return Circle.outerColliderPoints((Circle)this);
		if(this instanceof Polygon) return Polygon.outerColliderPoints(((Polygon) (this)).getCoords());
		return null;
	}
	
	//gets the maximum value from an array
	public static double getMax(double[] points) {
		double max = points[0];
		for(int i = 1; i < points.length; i++)
			if(max < points[i]) max = points[i];
		return max;
	}
	
	//gets the minimum value from an array
	public static double getMin(double[] points) {
		double min = points[0];
		for(int i = 1; i < points.length; i++)
			if(min > points[i]) min = points[i];
		return min;
	}
	
	public Accel sumAccel() {
		double ax = 0;
		double ay = 0;
		for(Accel a : this.accelerations) {
			ax += a.getX();
			ay += a.getY();
		}
		this.accelerations.clear();
		return new Accel(ax, ay);
	}
	
	public Color getC() {
		return c;
	}

	public void setC(Color c) {
		this.c = c;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public double getMass() {
		return mass;
	}

	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public ArrayList<T> getCollisions() {
		return collisions;
	}

	public void setCollisions(ArrayList<T> collisions) {
		this.collisions = collisions;
	}

	public T addCollision(T collision) {
		this.collisions.add(collision);
		return collision;
	}
	
	public void removeCollision(T collision) {
		this.collisions.add(collision);
		this.collisions.remove(collision);
	}
	
	public boolean isOuterCollision() {
		return outerCollision;
	}
	
	public void setOuterCollision(boolean outerCollision) {
		this.outerCollision = outerCollision;
	}
	
	public boolean isInnerCollision() {
		return innerCollision;
	}
	
	public void setInnerCollision(boolean innerCollision) {
		this.innerCollision = innerCollision;
	}
	
	public double getVelX() {
		return velX;
	}
	
	public void setVelX(double acelX) {
		this.velX = acelX;
	}
	
	public double getVelY() {
		return velY;
	}
	
	public void setVelY(double acelY) {
		this.velY = acelY;
	}
	
	public ArrayList<Accel> getAccelerations() {
		return accelerations;
	}
	
	public void setAccelerations(ArrayList<Accel> accelerations) {
		this.accelerations = accelerations;
	}
	
	public void addAcel(Accel a) {
		this.accelerations.add(a);
	}
	
	public ArrayList<Coord> getColliderPoints() {
		return colliderPoints;
	}
	
	public void setColliderPoints(ArrayList<Coord> colliderPoints) {
		this.colliderPoints = colliderPoints;
	}
}
