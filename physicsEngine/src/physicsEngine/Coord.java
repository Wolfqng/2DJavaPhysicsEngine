package physicsEngine;

import java.util.ArrayList;

public class Coord {
	private double x, y;

	public Coord(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public Coord() {
		
	}
	
	public static double[] getAllX(ArrayList<Coord> coords) {
		double[] xPoints = new double[coords.size()];
		for(int i = 0; i < coords.size(); i++) 
			xPoints[i] = coords.get(i).getX();
		
		return xPoints;
	}
	
	public static double[] getAllY(ArrayList<Coord> coords) {
		double[] yPoints = new double[coords.size()];
		for(int i = 0; i < coords.size(); i++) 
			yPoints[i] = coords.get(i).getY();
		
		return yPoints;
	}
	
	public static ArrayList<Coord> arraysToCoords(double[] xPoints, double[] yPoints) {
		ArrayList<Coord> fCoords = new ArrayList<>();
		for(int i = 0; i < xPoints.length; i++) 
			fCoords.add(new Coord(xPoints[i], yPoints[i]));
		return fCoords;
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

	@Override
	public String toString() {
		return "Coord [x=" + x + ", y=" + y + "]";
	}
}
