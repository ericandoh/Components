package components;

/*
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
*/

//deprecated
//public class PieceInConstruction {

	/*
	private ArrayList<BasicRenderable> squareModels;
	private ArrayList<BasicBox> boxes;
	
	private Plane flat;
	
	private Vector3 corner0, corner1;
	private int detailSize;
	
	public PieceInConstruction() {
		super(new Vector3());
		squareModels = new ArrayList<BasicRenderable>();
		boxes = new ArrayList<BasicBox>();
		flat = new Plane(new Vector3(0, 1, 0), 0);
		corner0 = new Vector3();
		corner1 = new Vector3();
		detailSize = Integer.MAX_VALUE;
	}
	
	public void addBlock(Vector3 p, int size, Placeable material) {
		
		if (material == null) {
			return;
		}
		Vector3 sqPos = new Vector3();
		Vector3 temp = new Vector3();
		Vector3 mWidth = new Vector3();
		material.getDimension(mWidth, size);	//m'width *tips fedora*
		//see if block is too far from center of piece
		if (boxes.size() > 0) {
			sqPos.set(corner0).add(corner1).scl(0.5f);
			temp.set(p).add(mWidth).add(p).scl(0.5f);
			if (sqPos.dst(temp) >= Constants.MAX_PIECE_RADIUS * Position.getWidth(detailSize)) {
				System.out.println("Too far!");
				return;
			}
		}
		//see if any block already occupies this space
		float sqWidth;
		for (int i = squareModels.size() - 1; i >= 0; i--) {
			sqPos.set(boxes.get(i).getPos());
			sqWidth = Position.getWidth(boxes.get(i).getSize());
			if (p.x < (sqPos.x + sqWidth) && (p.x + mWidth.x) > sqPos.x 
					&& p.y < (sqPos.y + sqWidth) && (p.y + mWidth.y) > sqPos.y 
					&& p.z < (sqPos.z + sqWidth) && (p.z + mWidth.z) > sqPos.z) {
				System.out.println("Square conflicts");
				return;
			}
		}
		
		
		
		ModelInstance instance = material.createModelInstance();
		float width = Position.getWidth(size);
		instance.transform.setToTranslation(p.x, p.y, p.z);
		instance.transform.scale(width, width, width);
		squareModels.add(new BasicRenderable(instance));
		Vector3 blockPos = new Vector3();
		blockPos.set(p);
		boxes.add(new BasicBox(material, size, blockPos));
		if (boxes.size() == 1) {
			pos.set(blockPos);
			corner0.set(blockPos);
			corner1.set(blockPos).add(width);
		}
		else {
			pos.set(Math.min(pos.x, blockPos.x), Math.min(pos.y, blockPos.y), Math.min(pos.z, blockPos.z));
			corner0.set(Math.min(corner0.x, blockPos.x), Math.min(corner0.y, blockPos.y), Math.min(corner0.z, blockPos.z));
			corner1.set(Math.max(corner0.x, blockPos.x + width), Math.max(corner0.y, blockPos.y + width), Math.max(corner0.z, blockPos.z + width));
		}
		detailSize = Math.min(detailSize, size);
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
	}
	public Vector3 findCollision(Ray cameraRay, Vector3 place, int sideSize) {
		Vector3 intersection = new Vector3();
		Vector3 sqPos = new Vector3();
		Vector3 savedInter, savedPos1, savedPos2;
		savedInter = new Vector3();
		savedPos1 = new Vector3();
		savedPos2 = new Vector3();
		float width;
		Vector3 oppositeCorner = new Vector3();
		float minDist = Float.MAX_VALUE;
		float dist;
		float sideWidth = Position.getWidth(sideSize);
		//int useSize = sideSize;
		for (int c = 0; c < squareModels.size(); c++) {
			sqPos.set(boxes.get(c).getPos());
			width = Position.getWidth(boxes.get(c).getSize());
			oppositeCorner.set(sqPos).add(width, width, width);
			if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos, oppositeCorner), intersection)) {
				dist = intersection.dst(cameraRay.origin);
				if (dist < minDist) {
					minDist = dist;
					savedInter.set(intersection);
					savedPos1.set(sqPos);
					savedPos2.set(oppositeCorner);
					//useSize = Math.min(useSize, squareSizes.get(c));
				}
			}
		}
		if (minDist == Float.MAX_VALUE) {
			//return intersection with flat plane
			if (Intersector.intersectRayPlane(cameraRay, flat, place)) {
				dist = place.dst(cameraRay.origin);
				if (dist >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
					return null;
				}
				place.y = Math.max(0.0f, place.y);
			}
			else {
				return null;
			}
		}
		else if (minDist >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
			return null;
		}
		else {
			if (Math.abs(savedInter.x - savedPos1.x) < 0.01) {
				//place.set(savedPos1.x - sideWidth, savedPos1.y, savedPos1.z);
				place.set(savedPos1.x - sideWidth, savedInter.y, savedInter.z);
			}
			else if (Math.abs(savedInter.x - savedPos2.x) < 0.01) {
				place.set(savedPos2.x, savedInter.y, savedInter.z);
			}
			else if (Math.abs(savedInter.y - savedPos1.y) < 0.01) {
				place.set(savedInter.x, savedPos1.y - sideWidth, savedInter.z);
			}
			else if (Math.abs(savedInter.y - savedPos2.y) < 0.01) {
				place.set(savedInter.x, savedPos2.y, savedInter.z);
			}
			else if (Math.abs(savedInter.z - savedPos1.z) < 0.01) {
				place.set(savedInter.x, savedInter.y, savedPos1.z - sideWidth);
			}
			else if (Math.abs(savedInter.z - savedPos2.z) < 0.01) {
				place.set(savedInter.x, savedInter.y, savedPos2.z);
			}
			else {
				System.out.println("Shit went wrong");
			}
		}
		place.x = Position.quantizeValue(place.x, sideSize);
		place.y = Position.quantizeValue(place.y, sideSize);
		place.z = Position.quantizeValue(place.z, sideSize);
		//place.x = (float) (Math.floor(place.x));
		//place.y = (float) (Math.floor(place.y));
		//place.z = (float) (Math.floor(place.z));
		return place;
	}*/
//}
