package physicsEngine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.util.ArrayList;

import physicsEngine.events.CollisionPointEvent;

public class Circle extends Collider<Object> {
	private double x, y, radius;
	//For checking for outer collider you can create a polygon and put it into the polygon box collision detection system
	//(If you do so then change the box collision detection to be in Collider and not just Polygon)
	
	public Circle(double x, double y, double radius, Color c, double rotation, double mass) {
		super(c, rotation, mass);
		this.x = x;
		this.y = y;
		this.radius = radius / 2;
	}
	
	public static ArrayList<Coord> outerColliderPoints(Circle c) {
		double x = c.getX();
		double y = c.getY();
		double r = c.getRadius();
		double minX = x - r;
		double maxX = x + r;
		double minY = y - r;
		double maxY = y + r;
		return Coord.arraysToCoords(new double[]{minX, maxX, maxX, minX}, new double[]{minY, minY, maxY, maxY});
	}
	
	@Override
	public CollisionPointEvent isPointInCollider(double x, double y, Collider<Object> p) {
		//(x-center_x)^2 + (y - center_y)^2 < radius^2
		//System.out.println(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2) - Math.pow(this.radius, 2));
		//System.out.println(Math.pow(this.radius, 2));
		if(Math.pow(x - this.x, 2) + Math.pow(y - this.y, 2) < Math.pow(this.radius, 2)) 
			return new CollisionPointEvent(this, p, x, y);
		
		return null;
	}
	
	@Override
	public void rotate(double deg) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public ArrayList<Coord> generateCollisionPoints() {
		double sensitivity = 200;
		double interval = 360 / sensitivity;
		ArrayList<Coord> coords = new ArrayList<>();
		
		for(int i = 0; i < sensitivity; i++) 
			coords.add(new Coord(Math.cos(interval * i) * this.radius + this.x, Math.sin(interval * i) * this.radius + this.y));
		
		return coords;
	}
	
	@Override
	public void draw(Graphics2D g2d, Paint p) {
		if(!this.isDrawn()) {
			g2d.setPaint(p);
			g2d.fillOval((int)(this.x - this.radius), (int)(this.y - this.radius), (int)(this.radius * 2), (int)(this.radius * 2));
		}
		this.setDrawn(true);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getRadius() {
		return radius;
	}

	public void setRadius(double radius) {
		this.radius = radius;
	}
}
