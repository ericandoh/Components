package components;

/*
 * A piece is the unit model (base)
 * It is compromised of potentially other pieces, or 
 */

import java.util.ArrayList;

import render.MeshAndModelBuilder;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class Piece implements Placeable {
	
	public static final int GROUND_ID = 1;
	
	public static int counter = 0;
	
	//list of models for each render distance (0=most detailed, 5+=least detail)
	private Model model;
	
	//name of this piece (for reference)
	private String name;
	//id name of this piece
	private long id;
	
	//boxes and/or pieces in this piece marked with position offsets
	protected ArrayList<BasicBox> boxes;
	
	//only pieces, with position offsets (stored in BasicBox)
	protected ArrayList<BasicBox> pieceBoxes;
	
	protected Vector3 dimensions;
	protected int detailSize;
	
	public Piece(Model p, String name, int size, int xwidth, int ywidth, int zwidth, PieceAndMaterialRepo repo) {
		//your basic piece
		this.model = p;
		this.boxes = new ArrayList<BasicBox>();
		this.boxes.add(new BasicBox(repo.getNamedMaterial(1), size, new Vector3(0, 0, 0)));
		this.dimensions = new Vector3(xwidth, ywidth, zwidth);
		this.detailSize = size;
		this.name = name;
	}
	@SuppressWarnings("unchecked")
	public Piece(ArrayList<BasicBox> boxes, PieceAndMaterialRepo repo) {
		this.boxes = (ArrayList<BasicBox>) boxes.clone();
		this.dimensions = new Vector3();
		updateWidths(true);
		generateModel();
		this.name = "model" + Integer.toString(counter++);
		repo.addPiece(this);
	}
	//use this constructor only for PieceUnderConstruction
	public Piece() {
		this.name = "under construction";
		id = -1;
		boxes = new ArrayList<BasicBox>();
		this.dimensions = new Vector3();
	}
	public void setID(int id) {
		this.id = id;
	}
	public void generateModel() {
		float mWidth = Position.getWidth(detailSize);
		NamedMaterial[][][] materialList = generateBlockForm();
		this.model = MeshAndModelBuilder.makeModelFromMatrix(materialList, (int)mWidth);
	}
	//used to generate models
	//also used for collision between two pieces...?
	public NamedMaterial[][][] generateBlockForm() {
		Vector3 sqPos = new Vector3();
		int scaledSize;
		float mWidth = Position.getWidth(detailSize);
		NamedMaterial[][][] materialList = new NamedMaterial[(int)dimensions.x][(int)dimensions.y][(int)dimensions.z];
		pieceBoxes = new ArrayList<BasicBox>();
		for (int c = 0; c < boxes.size(); c++) {
			//squareModels.get(c).getModel().transform.getTranslation(sqPos);
			if (!boxes.get(c).isBox()) {
				pieceBoxes.add(boxes.get(c));
				continue;
			}
			sqPos.set(boxes.get(c).getPos());
			scaledSize = (int) Position.getWidth(boxes.get(c).getSize()-detailSize);
			for (int xi = 0; xi < scaledSize; xi++) {
				for (int yi = 0; yi < scaledSize; yi++) {
					for (int zi = 0; zi < scaledSize; zi++) {
						materialList[(int)(sqPos.x / mWidth)+xi][(int)(sqPos.y / mWidth) + yi][(int)(sqPos.z / mWidth) + zi] = (NamedMaterial)boxes.get(c).getMat();
					}
				}
			}
		}
		return materialList;
	}
	
	public void populateSubInstances(Vector3 basePos, ArrayList<PieceInstance> subInstances) {
		if (pieceBoxes == null)
			return;
		Vector3 pos;
		for (int d = 0; d < pieceBoxes.size(); d++) {
			pos = new Vector3();
			pos.set(basePos).add(pieceBoxes.get(d).getPos());
			subInstances.add(new PieceInstance((Piece)pieceBoxes.get(d).getMat(), pos));
		}
	}
	
	public void updateWidths(boolean center) {
		int minX, minY, minZ;
		minX = minY = minZ = Integer.MAX_VALUE;
		int maxX, maxY, maxZ;
		maxX = maxY = maxZ = -Integer.MAX_VALUE;
		Vector3 sqPos = new Vector3();
		int scaledSize;
		Vector3 scaledWidth = new Vector3();
		detailSize = Constants.CHUNK_MAX_SIZE;
		for (int c = 0; c < boxes.size(); c++) {
			sqPos.set(boxes.get(c).getPos());
			scaledSize = boxes.get(c).getSize();
			boxes.get(c).getDimension(scaledWidth);
			minX = Math.min((int)sqPos.x, minX);
			minY = Math.min((int)sqPos.y, minY);
			minZ = Math.min((int)sqPos.z, minZ);
			maxX = Math.max((int)(sqPos.x + scaledWidth.x), maxX);
			maxY = Math.max((int)(sqPos.y + scaledWidth.y), maxY);
			maxZ = Math.max((int)(sqPos.z + scaledWidth.z), maxZ);
			if (boxes.get(c).isBox()) {
				detailSize = Math.min(detailSize, scaledSize);
				detailSize = Math.min(detailSize, scaledSize);
				detailSize = Math.min(detailSize, scaledSize);
			}
		}
		dimensions.set(maxX - minX, maxY - minY, maxZ - minZ);
		if (center) {
			if (minX == 0 && minY == 0 && minZ == 0) 
				return;
			Vector3 offsetOrigin = new Vector3(-minX, -minY, -minZ);
			for (int c = 0; c < boxes.size(); c++) {
				boxes.get(c).offset(offsetOrigin);
			}
		}
	}
	
	public boolean conflicts(Vector3 pos, BasicBox box) {
		if (box.isBox()) {
			Vector3 boxDim = new Vector3();
			box.getDimension(boxDim);
			return conflicts(pos, box.getPos(), boxDim);
		}
		else {
			Piece them = (Piece)box.getMat();
			BasicBox movedCopy;
			//iterate through my own boxes
			for (int i = 0; i < boxes.size(); i++) {
				movedCopy = new BasicBox(boxes.get(i));
				movedCopy.offset(pos);
				if (them.conflicts(box.getPos(), movedCopy)) {
					return true;
				}
			}
		}
		return false;
	}
	
	//pos of this pieceinstance, test vector to test against, size of box of test
	public boolean conflicts(Vector3 pos, Vector3 test0, Vector3 mWidth) {
		Vector3 sqPos0 = new Vector3();
		Vector3 sqPos1 = new Vector3();
		Vector3 test1 = new Vector3();
		Vector3 sqWidth = new Vector3();
		test1.set(test0).add(mWidth);
		
		//check if it hits bounding box
		sqPos0.set(pos);
		sqPos1.set(sqPos0).add(dimensions);
		if (	   test0.x < sqPos1.x && test1.x > sqPos0.x 
				&& test0.y < sqPos1.y && test1.y > sqPos0.y 
				&& test0.z < sqPos1.z && test1.z > sqPos0.z  ) {
		}
		else {
			return false;
		}
		BasicBox box;
		Piece them;
		for (int i = boxes.size() - 1; i >= 0; i--) {
			box = boxes.get(i);
			sqPos0.set(box.getPos()).add(pos);
			if (box.isBox()) {
				boxes.get(i).getDimension(sqWidth);
				sqPos1.set(sqPos0).add(sqWidth);
				if (	   test0.x < sqPos1.x && test1.x > sqPos0.x 
						&& test0.y < sqPos1.y && test1.y > sqPos0.y 
						&& test0.z < sqPos1.z && test1.z > sqPos0.z  ) {
					return true;
				}
			}
			else {
				them = (Piece)box.getMat();
				if (them.conflicts(sqPos0, test0, mWidth)) {
					return true;
				}
			}
		}
		return false;
	}
	public float hits(Vector3 pos, Ray cameraRay, int sideSize) {
		if (id == GROUND_ID) {
			return -1.0f;
		}
		Vector3 sqPos0 = new Vector3();
		Vector3 sqPos1 = new Vector3();
		Vector3 intersection = new Vector3();
		float maxDst = Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH;
		float minDst = maxDst;
		//check if it hits bounding box
		sqPos0.set(pos);
		sqPos1.set(sqPos0).add(dimensions);
		if (!Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos0, sqPos1), intersection)) {
			//doesn't even hit the bounds LOL
			return -1.0f;
		}
		//check each box we have here
		Vector3 temp = new Vector3();
		float dst;
		for (int c = 0; c < boxes.size(); c++) {
			sqPos0.set(boxes.get(c).getPos()).add(pos);
			if (boxes.get(c).isBox()) {
				boxes.get(c).getDimension(temp);
				sqPos1.set(sqPos0).add(temp);
				if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos0, sqPos1), intersection)) {
					minDst = Math.min(minDst, intersection.dst(cameraRay.origin));
				}
			}
			else {
				dst = ((Piece)(boxes.get(c).getMat())).hits(sqPos0, cameraRay, sideSize);
				if (dst > 0)
					minDst = Math.min(minDst, dst);
			}
		}
		if (minDst == maxDst) {
			return -1.0f;
		}
		System.out.println("Hit "+this.name + " with dist " + Float.toString(minDst));
		return minDst;
	}
	
	
	public float findCollision(Vector3 pos, Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		if (id == GROUND_ID) {
			return -1.0f;
		}		
		Vector3 intersection = new Vector3();
		Vector3 sqPos0 = new Vector3();
		
		Vector3 sqPos1 = new Vector3();
		sqPos0.set(pos);
		sqPos1.set(sqPos0).add(dimensions);
		if (!Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos0, sqPos1), intersection)) {
			return -1.0f;
		}
		
		Vector3 savedInter, savedPos1, savedPos2;
		boolean alreadySet = false;
		savedInter = new Vector3();
		savedPos1 = new Vector3();
		savedPos2 = new Vector3();
		float width;
		float minDist = Float.MAX_VALUE;
		float dist;
		//int useSize = sideSize;
		for (int c = 0; c < boxes.size(); c++) {
			sqPos0.set(boxes.get(c).getPos()).add(pos);
			if (boxes.get(c).isBox()) {
				width = Position.getWidth(boxes.get(c).getSize());
				sqPos1.set(sqPos0).add(width, width, width);
				if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(sqPos0, sqPos1), intersection)) {
					dist = intersection.dst(cameraRay.origin);
					if (dist < minDist) {
						minDist = dist;
						savedInter.set(intersection);
						savedPos1.set(sqPos0);
						savedPos2.set(sqPos1);
						alreadySet = false;
					}
				}
			}
			else {
				System.out.println("Checking against subpiece");
				dist = ((Piece)(boxes.get(c).getMat())).findCollision(sqPos0, cameraRay, intersection, dim, sideSize);
				if (dist >= 0 && dist < minDist) {
					System.out.println("Subpiece valid");
					minDist = dist;
					savedInter.set(intersection);
					alreadySet = true;
				}
			}
			
			
		}
		if (minDist == Float.MAX_VALUE) {
			//return intersection with flat plane
			return -1.0f;
		}
		else if (minDist >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
			return -1.0f;
		}
		else if (alreadySet) {
			//already calculated position to the side of in a subpiece
			System.out.println("Hit a subpiece LOL");
			place.set(savedInter);
		}
		else {
			System.out.println("Hit a side block");
			if (Math.abs(savedInter.x - savedPos1.x) < 0.01) {
				//place.set(savedPos1.x - sideWidth, savedPos1.y, savedPos1.z);
				place.set(savedPos1.x - dim.x, savedInter.y, savedInter.z);
			}
			else if (Math.abs(savedInter.x - savedPos2.x) < 0.01) {
				place.set(savedPos2.x, savedInter.y, savedInter.z);
			}
			else if (Math.abs(savedInter.y - savedPos1.y) < 0.01) {
				place.set(savedInter.x, savedPos1.y - dim.y, savedInter.z);
			}
			else if (Math.abs(savedInter.y - savedPos2.y) < 0.01) {
				place.set(savedInter.x, savedPos2.y, savedInter.z);
			}
			else if (Math.abs(savedInter.z - savedPos1.z) < 0.01) {
				place.set(savedInter.x, savedInter.y, savedPos1.z - dim.z);
			}
			else if (Math.abs(savedInter.z - savedPos2.z) < 0.01) {
				place.set(savedInter.x, savedInter.y, savedPos2.z);
			}
			else {
				System.out.println("Shit went wrong");
			}
		}
		//place.x = Position.quantizeValue(place.x, sideSize);
		//place.y = Position.quantizeValue(place.y, sideSize);
		//place.z = Position.quantizeValue(place.z, sideSize);
		return minDist;
	}
	
	public void setModel(Model p) {
		model = p;
	}
	public ModelInstance createModelInstance() {
		return new ModelInstance(model);
	}
	public void populateModels(Vector3 off, ArrayList<Model> models, ArrayList<Vector3> pos) {
		Piece m;
		Vector3 p;
		models.add(model);
		pos.add((new Vector3()).add(off));
		if (pieceBoxes != null && pieceBoxes.size() > 0) {
			for (int d = 0; d < pieceBoxes.size(); d++) {
				//add submodel of this model (if any)
				m = (Piece)(pieceBoxes.get(d).getMat());
				p = new Vector3();
				p.set(off).add(pieceBoxes.get(d).getPos());
				m.populateModels(p, models, pos);
			}
		}
	}
	public TextureRegion getIcon() {
		ArrayList<Model> models = new ArrayList<Model>();
		ArrayList<Vector3> pos = new ArrayList<Vector3>();
		populateModels(PieceUnderConstruction.ORIGIN, models, pos);
		return MeshAndModelBuilder.makeIcon(models, pos, dimensions.x, dimensions.y, dimensions.z);
	}
	public Vector3 getDimension(Vector3 src, int sideSize) {
		return src.set(dimensions);
	}
	public boolean isBox() {
		return false;
	}
	
	public String toString() {
		return "Material " + name;
	}
	public void dispose() {
		model.dispose();
	}
	public void getCorner(Vector3 pos) {
		pos.add(dimensions);
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.dispose();
	}
}
