package physicsEngine;

import java.awt.Color;
import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;
import physicsEngine.events.CollisionPointEvent;

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
		double min = points[0];
		for(int i = 1; i < points.length; i++)
			if(min > points[i]) min = points[i];
		return min;
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
	
	//generates points on a polygons edges
	public static double[][] generateCollisionPoints(Polygon p) {
		double sensitivity = 40; //How many points to check on a edge
		double[][] points = new double[2][(int)sensitivity * p.getxPoints().length];		
		int index = 0;
		
		for(int i = 0; i < p.getxPoints().length; i++) {
			double px1, py1, px2, py2;
			px1 = p.getxPoints()[i];
			py1 = p.getyPoints()[i];
			if(i != p.getxPoints().length - 1) {
				px2 = p.getxPoints()[i + 1];
				py2 = p.getyPoints()[i + 1];
			}else {
				px2 = p.getxPoints()[0];
				py2 = p.getyPoints()[0];
			}
			
			double edgeLen = Math.hypot(px2 - px1, py2 - py1);
			double cp = edgeLen / sensitivity;
			double ang = getPointAngles(new double[] {px1, py1}, new double[] {px2, py2});
			for(int j = 0; j < sensitivity; j++) {
				if(px2 < px1) {
					points[0][index] = px1 - Math.cos(ang) * (cp * j);
					points[1][index] = py1 - Math.sin(ang) * (cp * j);
				}else {
					points[0][index] = Math.cos(ang) * (cp * j) + px1;
					points[1][index] = Math.sin(ang) * (cp * j) + py1;
				}
				index++;
			}
		}
		
		return points;
	}
	
	//1) Draw a horizontal line to the right of each point and extend it to infinity
	//2) Count the number of times the line intersects with polygon edges.
	//3) A point is inside the polygon if either count of intersections is odd or
	//   point lies on an edge of polygon.  If none of the conditions is true, then 
	//   point lies outside.
	public static CollisionPointEvent isPointInCollider(double x, double y, Polygon p, Polygon p2) {
		int checks = 0;		

			for(int j = 0; j < p.getxPoints().length; j++) {
				double px1, py1, px2, py2;
				px1 = p.getxPoints()[j];
				py1 = p.getyPoints()[j];
				if(j != p.getxPoints().length - 1) {
					px2 = p.getxPoints()[j + 1];
					py2 = p.getyPoints()[j + 1];
				}else {
					px2 = p.getxPoints()[0];
					py2 = p.getyPoints()[0];
				}
				
				double slope = (py2 - py1) / (px2 - px1);
				double intercept = -(slope * px1) + py1; 

				double poi = (y - intercept) / slope;
				
				if(poi < getMax(new double[] {px1, px2}) && poi > getMin(new double[] {px1, px2}) && poi > x) 
					checks++;	
			}
		if(checks % 2 == 1) 
			return new CollisionPointEvent(p, p2, x, y);
		return null;
	}
	
	public static double getPointAngles(double[] points1, double[] points2) {
		return Math.atan((points2[1] - points1[1]) / (points2[0] - points1[0])); 
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
