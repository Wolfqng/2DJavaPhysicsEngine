package physicsEngine;

import java.awt.Color;
import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;

public class Polygon {
	private double[] xPoints;
	private double[] yPoints;
	private Color c;
	private double rotation;
	private double mass;
	
	public Polygon(double[] xPoints, double[] yPoints, double rotation, double mass, Color c) {
		this.xPoints = xPoints;
		this.yPoints = yPoints;
		this.rotation = rotation;
		this.mass = mass;
		this.c = c;
		rotate(rotation);
	}
	
	public void update() {
		//Check for collisions
		//Apply external forces
		//Update position 
	}
	
	public void rotate(double deg) {
		double rad = Math.toRadians(deg);
		//System.out.println(deg + " === " + rad);
		double[] cenPts = findCenter(this.xPoints, this.yPoints);
		for(int i = 0; i < xPoints.length; i++) {
			double x = this.xPoints[i];
			double y = this.yPoints[i];
			
			x -= cenPts[0];
			y -= cenPts[1];
			
			this.xPoints[i] = (x * Math.cos(rad)) - (y * Math.sin(rad)) + cenPts[0];
			this.yPoints[i] = (y * Math.cos(rad)) + (x * Math.sin(rad)) + cenPts[1];
		}
	}
	
	//converts double array to an integer array
	public static int[] doubleToIntArray(double[] source) {
	    int[] dest = new int[source.length];
	    for(int i = 0; i < source.length; i++) {
	        dest[i] = (int)source[i];
	    }
	    return dest;
	}
	
	//get the theoretical center of any shape, this infers that xPoints and yPoints are of same size
	public static double[] findCenter(double[] xPoints, double[] yPoints) {
		double[] pts = new double[2];
		double minX = getMin(xPoints);
		double maxX = getMax(xPoints);
		double minY = getMin(yPoints);
		double maxY = getMax(yPoints);
		pts[0] = (maxX + minX) / 2;
		pts[1] = (maxY + minY) / 2;
		return pts;
	}
	
	//grabs an outer collider for optimization
	public static double[][] outerColliderPoints(double[] xPoints, double[] yPoints) {
		double[][] coords = new double[2][2];
		double minX = getMin(xPoints);
		double maxX = getMax(xPoints);
		double minY = getMin(yPoints);
		double maxY = getMax(yPoints);
		coords[0] = new double[]{minX, maxX, maxX, minX};
		coords[1] = new double[]{minY, minY, maxY, maxY};
		return coords;
	}
	
	//this is just to make outerColliderPoints easier to access
	public double[][] outerColliderPoints() {
		return outerColliderPoints(this.xPoints, this.yPoints);
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
		double mix = points[0];
		for(int i = 1; i < points.length; i++)
			if(mix > points[i]) mix = points[i];
		return mix;
	}
	
	//Checks to see if the outer collision box is colliding with any other object
	public ArrayList<CollisionEvent> getOuterCollisions() {
		ArrayList<Polygon> objects = Enviroment.objects; //Can decide what to do with objects here
		ArrayList<CollisionEvent> collisions = new ArrayList<>();
		for(Polygon obj : objects) {
			if(!(obj == this)) {
			CollisionEvent ce = checkBoxCollision(this, obj);
			if(ce != null)
				collisions.add(ce);
			}
		}
		
		return collisions;
	}
	
	//checks if there is a collision in a rectangle
	public static CollisionEvent checkBoxCollision(Polygon p1, Polygon p2) {
		double[][] coord1 = p1.outerColliderPoints();
		double[][] coord2 = p2.outerColliderPoints();
		boolean collision = true;
		
		for(int i = 0; i < 2; i++) {
			if(coord1[0][0] > coord2[0][1]) collision = false;
			if(coord1[1][0] > coord2[1][2]) collision = false;
			
			if(coord1[0][1] < coord2[0][0]) collision = false;
			if(coord1[1][2] < coord2[1][0]) collision = false;
			
			if(collision) return new CollisionEvent(p1, p2);
			
			double[][] temp = coord1;
			coord1 = coord2;
			coord2 = temp;
			collision = true;
		}
		
		return null;
	}
	
	public double[] getxPoints() {
		return xPoints;
	}

	public void setxPoints(double[] xPoints) {
		this.xPoints = xPoints;
	}

	public double[] getyPoints() {
		return yPoints;
	}

	public void setyPoints(double[] yPoints) {
		this.yPoints = yPoints;
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
}
