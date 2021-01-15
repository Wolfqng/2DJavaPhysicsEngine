package physicsEngine.events;

import java.time.LocalDateTime;

import physicsEngine.Collider;

public class CollisionEvent {
	private Collider<Object> p1;
	private Collider<Object> p2;
	private LocalDateTime time;

	public CollisionEvent(Collider<Object> p1, Collider<Object> p2) {
		super();
		this.p1 = p1;
		this.p2 = p2;
		this.time = LocalDateTime.now(); 
	}
	
	public Collider<Object> getP1() {
		return p1;
	}

	public void setP1(Collider<Object> p1) {
		this.p1 = p1;
	}

	public Collider<Object> getP2() {
		return p2;
	}

	public void setP2(Collider<Object> p2) {
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
