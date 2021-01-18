package physicsEngine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;

import physicsEngine.events.CollisionPointEvent;

public class Polygon extends Collider<Object> {
	private ArrayList<Coord> coords = new ArrayList<>();
	
	public Polygon(double[] xPoints, double[] yPoints, double rotation, double mass, Color c) {
		super(c, mass, rotation);
		this.setCoords(Coord.arraysToCoords(xPoints, yPoints));
		rotate(rotation);
	}
	
	public Polygon(ArrayList<Coord> coords, double rotation, double mass, Color c) {
		super(c, mass, rotation);
		this.setCoords(coords);
		rotate(rotation);
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
		return fCoord;
	}
	
	public static ArrayList<Coord> outerColliderPoints(ArrayList<Coord> iCoords) {
		double minX = getMin(Coord.getAllX(iCoords));
		double maxX = getMax(Coord.getAllX(iCoords));
		double minY = getMin(Coord.getAllY(iCoords));
		double maxY = getMax(Coord.getAllY(iCoords));
		return Coord.arraysToCoords(new double[]{minX, maxX, maxX, minX}, new double[]{minY, minY, maxY, maxY});
	}
	
	//Rotate an object
	@Override
	public void rotate(double deg) {
		double rad = Math.toRadians(deg);
		Coord centerCoord = Polygon.findCenter(this.getCoords());
		for(Coord c : this.getCoords()) {
			double x = c.getX();
			double y = c.getY();
			
			x -= centerCoord.getX();
			y -= centerCoord.getY();
			
			c.setX((x * Math.cos(rad)) - (y * Math.sin(rad)) + centerCoord.getX());
			c.setY((y * Math.cos(rad)) + (x * Math.sin(rad)) + centerCoord.getY());
		}
	}
	
	//generates points on a polygons edges
	@Override
	public ArrayList<Coord> generateCollisionPoints() {
		double sensitivity = 40; //How many points to check on a edge	
		ArrayList<Coord> points = new ArrayList<>();
		
		for(int i = 0; i < this.getCoords().size(); i++) {
			double px1, py1, px2, py2;
			double[] xPoints = Coord.getAllX(this.getCoords());
			double[] yPoints = Coord.getAllY(this.getCoords());
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
			double ang = getPointAngles(new Coord(px1, py1), new Coord(px2, py2));
			for(int j = 0; j < sensitivity; j++) 
				if(px2 < px1)
					points.add(new Coord(px1 - Math.cos(ang) * (cp * j), py1 - Math.sin(ang) * (cp * j)));
				 else 
					points.add(new Coord(Math.cos(ang) * (cp * j) + px1, Math.sin(ang) * (cp * j) + py1));
		}
		
		return points;
	}
	
	public static double getPointAngles(Coord c1, Coord c2) {
		return Math.atan((c2.getY() - c1.getY()) / (c2.getX() - c1.getX())); 
	}
	
	//1) Draw a horizontal line to the right of each point and extend it to infinity
	//2) Count the number of times the line intersects with polygon edges.
	//3) A point is inside the polygon if either count of intersections is odd or
	//   point lies on an edge of polygon.  If none of the conditions is true, then 
	//   point lies outside.
	@Override
	public CollisionPointEvent isPointInCollider(double x, double y, Collider<Object> p) {
		int checks = 0;		

		for(int i = 0; i < this.getCoords().size(); i++) {
			double px1, py1, px2, py2;
			double[] xPoints = Coord.getAllX(this.getCoords());
			double[] yPoints = Coord.getAllY(this.getCoords());
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
			return new CollisionPointEvent(this, p, x, y);
		return null;
	}

	@Override
	public void draw(Graphics2D g2d, Paint p, boolean fill) {
		g2d.setPaint(p);
		if(fill)
			g2d.fillPolygon(Polygon.doubleToIntArray(Coord.getAllX(this.getCoords())), Polygon.doubleToIntArray(Coord.getAllY(this.getCoords())), this.getCoords().size());
		else
			g2d.drawPolygon(Polygon.doubleToIntArray(Coord.getAllX(this.getCoords())), Polygon.doubleToIntArray(Coord.getAllY(this.getCoords())), this.getCoords().size());
	}
	
	@Override
	public void translateX(double amt) {
		for(Coord c : this.coords) 
			c.setX(c.getX() + amt);
	}
	
	@Override
	public void translateY(double amt) {
		for(Coord c : this.coords) 
			c.setY(c.getY() + amt);
	}
	
	public ArrayList<Coord> getCoords() {
		return coords;
	}

	public void setCoords(ArrayList<Coord> coords) {
		this.coords = coords;
	}
	
	public boolean addCoord(Coord coord) {
		return this.coords.add(coord);
	}
	
	public boolean removeCoord(Coord coord) {
		return this.coords.remove(coord);
	}
}
