package components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;


public class Player extends WorldObject implements Renderable {

	public Player() {
		super(null);
	}
	public Player(Vector3 pos) {
		super(pos);
	}
	public void setPosition(Vector3 cameraReference) {
		this.pos = cameraReference;
	}

	@Override
	public ModelInstance getModel() {
		return null;
	}

}
