package components;

import com.badlogic.gdx.math.Vector3;

public class WorldObject {
	protected Vector3 pos;
	protected float direction;
	
	public WorldObject(Vector3 pos) {
		this.pos = pos;
	}
	public Vector3 getPos() {
		return pos;
	}
}
