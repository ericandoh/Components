package components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public interface Placeable {

	public TextureRegion getIcon();
	public Vector3 getDimension(Vector3 src, int sideSize);
	public boolean isBox();
	
	public ModelInstance createModelInstance();
}
