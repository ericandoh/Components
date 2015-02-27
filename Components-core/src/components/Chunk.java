package components;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/*
 * A large area that details a list of pieces being stored here
 * and smaller chunks inside this chunk
 * Pieces will be 
 * Unit of piece storage
 * If far enough, chunk will render as flat plane of items in it
 */

public class Chunk {
	
	//private static final int CHUNK_SUBDIVISION = Constants.CHUNK_SUBDIVISION;
	private static final int CHUNK_MAX_SIZE = Constants.CHUNK_MAX_SIZE;
	private static final int CHUNK_GROUND_SIZE = Constants.CHUNK_GROUND_SIZE;
	//private static final int CHUNK_RENDER_START = Constants.CHUNK_RENDER_START;
	//private static final int CHUNK_RENDER_SIZE = Constants.CHUNK_RENDER_SIZE;
	
	private ArrayList<PieceInstance> pieces;
	//this is real coordinates not scaled/restricted
	//private Vector3 pos, temp;
	//private int size;
	
	//makes a blank chunk
	public Chunk(Vector3 pos) {
		//this.pos = pos;
		//this.temp = new Vector3(0.0f, 0.0f, 0.0f);
		//this.size = size;
		pieces = new ArrayList<PieceInstance>();
		float totalWidth = Position.getWidth(CHUNK_MAX_SIZE);
		float totalX = pos.x;
		float totalZ = pos.z;
		float width = Position.getWidth(CHUNK_GROUND_SIZE);
		Vector3 groundPosition;
		for (float xi = 0; xi < totalWidth; xi += width) {
			for (float zi = 0; zi < totalWidth; zi += width) {
				groundPosition = new Vector3(totalX + xi, 0.0f, totalZ + zi);
				this.addPiece(new PieceInstance(PieceAndMaterialRepo.groundPiece, groundPosition, CHUNK_GROUND_SIZE));
			}
		}
	}
	
	public int render(Vector3 p, Renderable[] renderables, int count, float renderLength, int renderSize) {
		for (PieceInstance pi: pieces) {
			count = pi.render(p, renderables, count, renderLength, renderSize);
		}
		return count;
	}
	public void addPiece(PieceInstance p) {
		pieces.add(p);
	}
	public boolean removePiece(PieceInstance p) {
		return pieces.remove(p);
	}
	public boolean conflicts(BasicBox box) {
		for (PieceInstance pi: pieces) {
			if (pi.conflicts(box)) {
				return true;
			}
		}
		return false;
	}
	public PieceInstance hit(Ray cameraRay, int sideSize, Vector3 vdst) {
		PieceInstance minP = null;
		float minDst = -1.0f;
		float dst;
		
		for (PieceInstance pi: pieces) {
			dst = pi.hits(cameraRay, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
					minP = pi;
				}
			}
		}
		vdst.set(minDst, minDst, minDst);
		return minP;
	}
	public float findCollision(Ray cameraRay, Vector3 place, Vector3 boxDimensions, int sideSize) {
		float minDst = -1.0f;
		float dst;
		Vector3 temp = new Vector3();
		
		for (PieceInstance pi: pieces) {
			dst = pi.findCollision(cameraRay, temp, boxDimensions, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
					place.set(temp);
				}
			}
		}
		return minDst;
	}
}
