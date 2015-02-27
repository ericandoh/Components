package components;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class PieceUnderConstruction extends Piece {
	
	public static final Vector3 ORIGIN = new Vector3(0, 0, 0);
	
	private ArrayList<BasicRenderable> squareModels;
	private Vector3 corner0;
	
	public PieceUnderConstruction() {
		super();
		this.detailSize = Integer.MAX_VALUE;
		this.squareModels = new ArrayList<BasicRenderable>();
		this.corner0 = new Vector3();
	}
	
	
	public void addBlock(BasicBox addPiece) {
		if (addPiece == null) {
			return;
		}
		System.out.println("Adding piece at ");
		System.out.println(addPiece.getPos());
		
		Vector3 sqPos = new Vector3();
		Vector3 temp = new Vector3();
		Vector3 mWidth = new Vector3();
		addPiece.getDimension(mWidth);	//m'width *tips fedora*
		//see if block is too far from center of piece
		/*if (boxes.size() > 0) {
			sqPos.set(myPos).add(myPos).add(xwidth, ywidth, zwidth).scl(0.5f);
			temp.set(addPiece.getPos()).add(addPiece.getPos()).add(mWidth).scl(0.5f);
			if (sqPos.dst(temp) >= Constants.MAX_PIECE_RADIUS * Position.getWidth(detailSize)) {
				System.out.println("Too far!");
				return;
			}
		}*/
		//see if any block already occupies this space
		if (this.conflicts(ORIGIN, addPiece)) {
			System.out.println("Square conflicts");
			return;
		}
		
		ModelInstance instance = addPiece.createModelInstance();
		squareModels.add(new BasicRenderable(instance));
		boxes.add(addPiece);
		updateWidths(addPiece);
	}
	
	//only for use by PieceUnderConstruction!
	public void updateWidths(BasicBox box) {
		Vector3 pos = box.getPos();
		if (boxes.size() == 1) {
			corner0.set(pos);
			box.getDimension(dimensions);
		}
		else {
			corner0.set(Math.min(corner0.x, pos.x), 
					Math.min(corner0.y, pos.y) , 
					Math.min(corner0.z, pos.z));
			updateWidths(false);
		}
	}
	
	public int render(Vector3 p, Renderable[] renderables, int count) {
		if (count + squareModels.size() > renderables.length)
			return count;
		for (int c = 0; c < squareModels.size(); c++) {
			renderables[count++] = squareModels.get(c);
		}
		return count;
	}
	public Piece exportToPiece(PieceAndMaterialRepo repo) {
		if (boxes.size() == 0) {
			return null;
		}
		Piece newPiece = new Piece(boxes, repo);
		return newPiece;
	}
	
	public void removeBlock(Ray cameraRay, int sideSize) {
		Vector3 intersection = new Vector3();
		Vector3 sqPos = new Vector3();
		int removeIndex = -1;
		float width;
		Vector3 oppositeCorner = new Vector3();
		float minDist = Float.MAX_VALUE;
		float dist;
		for (int c = 0; c < squareModels.size(); c++) {
			sqPos.set(boxes.get(c).getPos());
			width = Position.getWidth(boxes.get(c).getSize());
			oppositeCorner.set(sqPos).add(width, width, width);
			if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos, oppositeCorner), intersection)) {
				dist = intersection.dst(cameraRay.origin);
				if (dist < minDist) {
					minDist = dist;
					removeIndex = c;
				}
			}
		}
		if (removeIndex < 0 || removeIndex >= squareModels.size()) {
			return;
		}
		else if (minDist >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
			return;
		}
		squareModels.remove(removeIndex);
		boxes.remove(removeIndex);
		
		if (boxes.size() > 0) {
			Vector3 pos;
			for (int c = 0; c < boxes.size(); c++) {
				pos = boxes.get(c).getPos();
				corner0.set(Math.min(corner0.x, pos.x), Math.min(corner0.y, pos.y) , Math.min(corner0.z, pos.z));
			}
		}
		updateWidths(false);
	}
	public Vector3 getPos() {
		return this.corner0;
	}
	
	@Override
	public boolean conflicts(Vector3 pos, BasicBox box) {
		return super.conflicts(ORIGIN, box);
	}
	@Override
	public boolean conflicts(Vector3 pos, Vector3 test0, Vector3 mWidth) {
		return super.conflicts(ORIGIN, test0, mWidth);
	}
	@Override
	public float hits(Vector3 pos, Ray cameraRay, int sideSize) {
		return super.hits(ORIGIN, cameraRay, sideSize);
	}
	
	@Override
	public float findCollision(Vector3 p, Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		return super.findCollision(ORIGIN, cameraRay, place, dim, sideSize);
	}
}
