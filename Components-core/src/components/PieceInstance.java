package components;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/*
 * A PieceInstance represents one instance of a piece in the world
 * Holds a reference to the piece
 * If viewer is too far will render a lesser detail model, or nothing
 */

public class PieceInstance extends WorldObject implements Renderable {

	private Piece source;
	protected ModelInstance instance;
	
	protected ArrayList<PieceInstance> subInstances;
	
	// maximum render size at which to render this object
	protected int maxSize;
	protected int renderAtSize;
	private Vector3 temp;
	
	public PieceInstance(Piece src, Vector3 pos, int maxSize) {
		super(pos);
		temp = new Vector3();
		renderAtSize = 0;
		this.source = src;
		this.maxSize = maxSize;
		//System.out.println("Making piece " + src.toString());
		this.instance = src.createModelInstance();
		this.subInstances = new ArrayList<PieceInstance>();
		src.populateSubInstances(this.pos, this.subInstances);
		updateModelPosition();
	}
	
	public PieceInstance(Piece src, Vector3 pos) {
		this(src, pos, Constants.CHUNK_MAX_SIZE);
	}
	public PieceInstance(PieceUnderConstruction src, Vector3 pos) {
		super(pos);
		this.source = src;
	}

	public int render(Vector3 p, Renderable[] renderables, int count, float renderLength, int renderSize) {
		if (count >= renderables.length)
			return count;
		
		temp.set(pos);
		source.getCorner(temp);
		float xdst = Math.max(pos.x - p.x, p.x - temp.x);
		float ydst = Math.max(pos.y - p.y, p.y - temp.y);
		float zdst = Math.max(pos.z - p.z, p.z - temp.z);
		int renderDst = (int) (Math.max(Math.max(xdst, ydst), zdst) / renderLength);
		if (renderDst > renderSize) {
			return count;
		}
		renderables[count++] = this;
		//set model size to render here
		renderAtSize = renderDst;
		for (PieceInstance pi: subInstances) {
			count = pi.render(p, renderables, count, renderLength, renderSize);
		}
		return count;
	}
	public int getMaxSize() {
		return maxSize;
	}
	public void updateModelPosition() {
		instance.transform.setToTranslation(pos.x, pos.y, pos.z);
		instance.transform.rotate(0.0f, 1.0f, 0.0f, (float) (-direction * 180 / Math.PI));
	}

	@Override
	public ModelInstance getModel() {
		//vary returning model depending on renderAtSize
		return instance;
	}
	
	public boolean conflicts(BasicBox box) {
		 return source.conflicts(pos, box);
	}
	public float hits(Ray cameraRay, int sideSize) {
		return source.hits(pos, cameraRay, sideSize);
	}
	public float findCollision(Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		return source.findCollision(pos, cameraRay, place, dim, sideSize);
	}
	@Override
	public String toString() {
		return "PI with " + source.toString() + " at " + pos.toString();
	}
}
