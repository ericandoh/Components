package components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

public class BasicRenderable implements Renderable {

	private ModelInstance model;
	
	public BasicRenderable(ModelInstance model) {
		this.model = model;
	}
	
	@Override
	public ModelInstance getModel() {
		return this.model;
	}
}
