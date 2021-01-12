package physicsEngine.events;

import java.time.LocalDateTime;

import physicsEngine.Polygon;

public class CollisionEvent {
	private Polygon p1;
	private Polygon p2;
	private LocalDateTime time;

	public CollisionEvent(Polygon p1, Polygon p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.time = LocalDateTime.now(); 
	}
	
	public Polygon getP1() {
		return p1;
	}

	public void setP1(Polygon p1) {
		this.p1 = p1;
	}

	public Polygon getP2() {
		return p2;
	}

	public void setP2(Polygon p2) {
		this.p2 = p2;
	}

	public LocalDateTime getTime() {
		return time;
	}

	@Override
	public String toString() {
		return "CollisionEvent [p1=" + p1 + ", p2=" + p2 + ", time=" + time + "]";
	}
}
