package physicsEngine;

import java.awt.Color;

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
	}
	
	public void update() {
		//Check for collisions
		//Apply external forces
		//Update position 
	}
	
	public void rotate(int deg) {
		double rad = Math.toRadians(deg);
		for(int i = 0; i < xPoints.length; i++) {
			double x = this.xPoints[i];
			double y = this.yPoints[i];
			this.xPoints[i] = (x * Math.cos(rad)) - (y * Math.sin(rad));
			this.yPoints[i] = (y * Math.cos(rad)) + (x * Math.sin(rad));
		}
	}
	
	public static int[] copyFromDoubleArray(double[] source) {
	    int[] dest = new int[source.length];
	    for(int i = 0; i < source.length; i++) {
	        dest[i] = (int)source[i];
	    }
	    return dest;
	}
	
	public void rotate(double rad) {
		
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
