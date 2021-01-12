package physicsEngine.events;

import physicsEngine.Polygon;

public class CollisionPointEvent extends CollisionEvent {
	private double x, y;
	
	public CollisionPointEvent(Polygon p1, Polygon p2, double x, double y) {
		super(p1, p2);
		this.x = x;
		this.y = y;
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
		return "CollisionPointEvent [getP1()=" + getP1() + ", getP2()=" + getP2()
				+ ", x=" + x + ", y=" + y + ", getTime()=" + getTime() + "]";
	}
}
