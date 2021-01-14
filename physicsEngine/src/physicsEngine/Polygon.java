package physicsEngine;

import java.awt.Color;
import java.util.ArrayList;

import physicsEngine.events.CollisionEvent;
import physicsEngine.events.CollisionPointEvent;

public class Polygon {
	//private double[] xPoints;
	//private double[] yPoints;
	ArrayList<Coord> coords = new ArrayList<>();
	private Color c;
	private double rotation;
	private double mass;
	
	public Polygon(double[] xPoints, double[] yPoints, double rotation, double mass, Color c) {
		//this.xPoints = xPoints;
		//this.yPoints = yPoints;
		this.coords = Coord.arraysToCoords(xPoints, yPoints);
		this.rotation = rotation;
		this.mass = mass;
		this.c = c;
		rotate(rotation);
	}
	
	public Polygon(ArrayList<Coord> coords, double rotation, double mass, Color c) {
		this.coords = coords;
		this.rotation = rotation;
		this.mass = mass;
		this.c = c;
		rotate(rotation);//
	}
	
	public void update() {
		//Check for collisions
		//Apply external forces
		//Update position 
	}
	
	public void rotate(double deg) {
		double rad = Math.toRadians(deg);
		Coord centerCoord = findCenter(this.coords);
		for(Coord c : this.coords) {
			double x = c.getX();
			double y = c.getY();
			
			x -= centerCoord.getX();
			y -= centerCoord.getY();
			
			c.setX((x * Math.cos(rad)) - (y * Math.sin(rad)) + centerCoord.getX());
			c.setY((y * Math.cos(rad)) + (x * Math.sin(rad)) + centerCoord.getY());
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
	
	public static Coord findCenter(ArrayList<Coord> coords) {
		Coord fCoord = new Coord();
		double minX = getMin(Coord.getAllX(coords));
		double maxX = getMax(Coord.getAllX(coords));
		double minY = getMin(Coord.getAllY(coords));
		double maxY = getMax(Coord.getAllY(coords));
		fCoord.setX((maxX + minX) / 2);
		fCoord.setY((maxY + minY) / 2);
		return fCoord;//
	}
	
	public static ArrayList<Coord> outerColliderPoints(ArrayList<Coord> iCoords) {
		double minX = getMin(Coord.getAllX(iCoords));
		double maxX = getMax(Coord.getAllX(iCoords));
		double minY = getMin(Coord.getAllY(iCoords));
		double maxY = getMax(Coord.getAllY(iCoords));
		return Coord.arraysToCoords(new double[]{minX, maxX, maxX, minX}, new double[]{minY, minY, maxY, maxY});
	}
	
	public ArrayList<Coord> outerColliderPoints() {
		return outerColliderPoints(this.coords);
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
		ArrayList<Polygon> objects = Enviroment.objects; //Can decide what objects should be here for optimization
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
		ArrayList<Coord> coords1 = p1.outerColliderPoints();
		ArrayList<Coord> coords2 = p2.outerColliderPoints();
		boolean collision = true;
		
		for(int i = 0; i < 2; i++) {
			if(coords1.get(0).getX() > coords2.get(1).getX()) collision = false;
			if(coords1.get(0).getY() > coords2.get(2).getY()) collision = false;
			
			if(coords1.get(1).getX() < coords2.get(0).getX()) collision = false;
			if(coords1.get(2).getY() < coords2.get(0).getY()) collision = false;
			
			if(collision) return new CollisionEvent(p1, p2);
			
			ArrayList<Coord> temp = coords1;
			coords1 = coords2;
			coords2 = temp;
			collision = true;
		}
		
		return null;
	}
	
	//generates points on a polygons edges
	public static ArrayList<Coord> generateCollisionPoints(Polygon p) {
		double sensitivity = 40; //How many points to check on a edge	
		ArrayList<Coord> points = new ArrayList<>();
		
		for(int i = 0; i < p.getCoords().size(); i++) {
			double px1, py1, px2, py2;
			double[] xPoints = Coord.getAllX(p.getCoords());
			double[] yPoints = Coord.getAllY(p.getCoords());
			px1 = xPoints[i];
			py1 = yPoints[i];
			if(i != xPoints.length - 1) {
				px2 = xPoints[i + 1];
				py2 = yPoints[i + 1];
			}else {
				px2 = xPoints[0];
				py2 = yPoints[0];
			}
			
			double edgeLen = Math.hypot(px2 - px1, py2 - py1);
			double cp = edgeLen / sensitivity;
			double ang = getPointAngles(new double[] {px1, py1}, new double[] {px2, py2});
			for(int j = 0; j < sensitivity; j++) 
				if(px2 < px1)
					points.add(new Coord(px1 - Math.cos(ang) * (cp * j), py1 - Math.sin(ang) * (cp * j)));
				 else 
					points.add(new Coord(Math.cos(ang) * (cp * j) + px1, Math.sin(ang) * (cp * j) + py1));
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

		for(int i = 0; i < p.getCoords().size(); i++) {
			double px1, py1, px2, py2;
			double[] xPoints = Coord.getAllX(p.getCoords());
			double[] yPoints = Coord.getAllY(p.getCoords());
			px1 = xPoints[i];
			py1 = yPoints[i];
			if(i != xPoints.length - 1) {
				px2 = xPoints[i + 1];
				py2 = yPoints[i + 1];
			}else {
				px2 = xPoints[0];
				py2 = yPoints[0];
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
	
	public ArrayList<Coord> getCoords() {
		return this.coords;
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
