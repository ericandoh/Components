package components;

import java.util.HashMap;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class PieceUnderConstruction extends Piece {
	
	public static final Vector3 ORIGIN = new Vector3(0, 0, 0);
	
	
	private Vector3 corner0;
	
	private HashMap<BasicBox, BasicRenderable> boxRenderables;
	protected HashMap<BasicBox, PieceInstance> subInstances;
	
	public PieceUnderConstruction() {
		super();
		this.detailSize = Integer.MAX_VALUE;
		this.corner0 = new Vector3();
		this.boxRenderables = new HashMap<BasicBox, BasicRenderable>();
		this.subInstances = new HashMap<BasicBox, PieceInstance>();
	}
	
	
	public void addBlock(BasicBox addPiece) {
		if (addPiece == null) {
			return;
		}
		System.out.println("Adding piece at ");
		System.out.println(addPiece.getPos());
		
		//Vector3 sqPos = new Vector3();
		//Vector3 temp = new Vector3();
		//Vector3 mWidth = addPiece.getDimension();	
		//m'width *tips fedora*
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
		/*if (this.conflicts(ORIGIN, addPiece)) {
			System.out.println("Square conflicts");
			return;
		}*/
		if (addPiece.isBox()) {
			NamedMaterial addOwner = (NamedMaterial)(addPiece.getMat());
			ModelInstance boxModel = addOwner.createModelInstance();
			boxModel.transform.setToTranslationAndScaling(addPiece.getPos(), addPiece.getDimension());
			BasicRenderable bas = new BasicRenderable(boxModel);
			boxRenderables.put(addPiece, bas);
		}
		else {
			Piece addPieceOwner = (Piece)(addPiece.getMat());
			PieceInstance pi = new PieceInstance(addPieceOwner, addPiece.getPos());
			subInstances.put(addPiece, pi);
		}
		boxes.add(addPiece);
		updateWidths(addPiece);
	}
	
	//only for use by PieceUnderConstruction!
	public void updateWidths(BasicBox box) {
		Vector3 pos = box.getPos();
		if (boxes.size() == 1) {
			corner0.set(pos);
			dimensions.set(box.getDimension());
		}
		else {
			corner0.set(Math.min(corner0.x, pos.x), 
					Math.min(corner0.y, pos.y) , 
					Math.min(corner0.z, pos.z));
			updateWidths(false);
		}
	}
	
	public int render(Vector3 p, Renderable[] renderables, int count, float renderLength, int renderSize) {
		
		if (count + boxRenderables.size() > renderables.length)
			return count;
		for (BasicRenderable bi: boxRenderables.values()) {
			renderables[count++] = bi;
		}
		for (PieceInstance pi: subInstances.values()) {
			count = pi.render(p, renderables, count, renderLength, renderSize);
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
		int removeIndex = -1;
		float minDst = -1;
		float dst;
		for (int c = 0; c < boxes.size(); c++) {
			//sqPos.set(boxes.get(c).getPos());
			dst = boxes.get(c).hits(ORIGIN, cameraRay, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
					removeIndex = c;
				}
			}
		}
		if (removeIndex < 0 || removeIndex >= boxes.size()) {
			return;
		}
		else if (minDst >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
			return;
		}
		BasicBox removeMe = boxes.remove(removeIndex);
		if (removeMe.isBox()) {
			//remove from boxRenderables
			boxRenderables.remove(removeMe);
		}
		else {
			//remove from subInstances
			subInstances.remove(removeMe);
		}
		
		corner0.set(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
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
	
	public boolean conflicts(BasicBox target) {
		//see if it hits my overall bounding box
		//set my bounding box
		temp0.set(corner0);
		temp1.set(corner0).add(dimensions);
		//set their bounding box
		temp2.set(target.getPos());
		temp3.set(target.getPos()).add(target.getDimension());
		//check if it hits bounding box
		if (	   temp0.x < temp3.x && temp1.x > temp2.x 
				&& temp0.y < temp3.y && temp1.y > temp2.y 
				&& temp0.z < temp3.z && temp1.z > temp2.z  ) {
			//do nothing - we have a good chance of conflicting! (but not yet!)
		}
		else {
			return false;
		}
		//iterate through my own boxes
		for (int i = 0; i < boxes.size(); i++) {
			//temp0.set(boxes.get(i).getPos());
			if (boxes.get(i).conflicts(ORIGIN, target)) {
				return true;
			}
		}
		return false;
	}
	
	public float hits(Ray cameraRay, int sideSize) {
		temp0.set(corner0);
		temp1.set(corner0).add(dimensions);
		if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(temp0, temp1), temp2)) {
			//do nothing - we have a good chance it might hit us!
		}
		else {
			//doesn't hit our outside box - fail!
			return -1.0f;
		}
		float dst;
		float minDst = -1.0f;
		for (int i = 0; i < boxes.size(); i++) {
			//temp0.set(boxes.get(i).getPos());
			dst = boxes.get(i).hits(ORIGIN, cameraRay, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
				}
			}
		}
		return minDst;
	}
	
	public float findCollision(Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		temp0.set(corner0);
		temp1.set(corner0).add(dimensions);
		if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(temp0, temp1), temp2)) {
			//do nothing - we have a good chance it might hit us!
		}
		else {
			//doesn't hit our outside box - fail!
			return -1.0f;
		}
		
		temp2.scl(0.0f);
		
		float dst;
		float minDst = -1.0f;
		for (int i = 0; i < boxes.size(); i++) {
			//temp0.set(boxes.get(i).getPos());
			dst = boxes.get(i).findCollision(ORIGIN, cameraRay, temp1, dim, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
					temp2.set(temp1);
				}
			}
		}
		if (minDst >= 0) {
			place.set(temp2);
		}
		return minDst;
	}
}
