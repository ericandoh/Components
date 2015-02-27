package components;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class BasicBox {
	
	//position relative to whichever piece this is a part of
	protected Vector3 pos;
	private Placeable mat;
	private int size;
	
	public BasicBox(Placeable mat, int size, Vector3 pos) {
		this.pos = pos;
		this.mat = mat;
		this.size = size;
	}
	public BasicBox(BasicBox copy) {
		this.pos = copy.pos;
		this.mat = copy.mat;
		this.size = copy.size;
	}
	
	public Placeable getMat() {
		return this.mat;
	}
	public int getSize() {
		return this.size;
	}
	public Vector3 getDimension(Vector3 src) {
		return this.mat.getDimension(src, this.size);
	}
	public Vector3 getPos() {
		return pos;
	}
	public Vector3 offset(Vector3 offset) {
		pos.add(offset);
		return pos;
	}
	public boolean isBox() {
		return mat.isBox();
	}
	public ModelInstance createModelInstance() {
		ModelInstance mi = this.mat.createModelInstance();
		mi.transform.setToTranslation(pos);
		if (isBox()) {
			float width = Position.getWidth(this.size);
			mi.transform.scale(width, width, width);
		}
		return mi;
	}
}