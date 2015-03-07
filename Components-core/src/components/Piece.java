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
	
	protected Vector3 temp0, temp1, temp2, temp3;
	
	private TextureRegion icon;
	
	public Piece(Model p, String name, int size, int xwidth, int ywidth, int zwidth, PieceAndMaterialRepo repo) {
		//your basic piece
		makeTemporary();
		this.model = p;
		this.boxes = new ArrayList<BasicBox>();
		this.boxes.add(new BasicBox(repo.getNamedMaterial(1), size, new Vector3(0, 0, 0)));
		this.dimensions = new Vector3(xwidth, ywidth, zwidth);
		this.detailSize = size;
		this.name = name;
	}
	@SuppressWarnings("unchecked")
	public Piece(ArrayList<BasicBox> boxes, PieceAndMaterialRepo repo) {
		makeTemporary();
		this.boxes = (ArrayList<BasicBox>) boxes.clone();
		this.dimensions = new Vector3();
		updateWidths(true);
		generateModel();
		this.name = "model" + Integer.toString(counter++);
		repo.addPiece(this);
	}
	//use this constructor only for PieceUnderConstruction
	public Piece() {
		makeTemporary();
		this.name = "under construction";
		id = -1;
		boxes = new ArrayList<BasicBox>();
		this.dimensions = new Vector3();
	}
	public void makeTemporary() {
		temp0 = new Vector3();
		temp1 = new Vector3();
		temp2 = new Vector3();
		temp3 = new Vector3();
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
		Vector3 scaledWidth;
		detailSize = Constants.CHUNK_MAX_SIZE;
		for (int c = 0; c < boxes.size(); c++) {
			sqPos.set(boxes.get(c).getPos());
			scaledWidth = boxes.get(c).getDimension();
			minX = Math.min((int)sqPos.x, minX);
			minY = Math.min((int)sqPos.y, minY);
			minZ = Math.min((int)sqPos.z, minZ);
			maxX = Math.max((int)(sqPos.x + scaledWidth.x), maxX);
			maxY = Math.max((int)(sqPos.y + scaledWidth.y), maxY);
			maxZ = Math.max((int)(sqPos.z + scaledWidth.z), maxZ);
			if (boxes.get(c).isBox()) {
				scaledSize = boxes.get(c).getSize();
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
	
	public boolean conflicts(Vector3 pos, BasicBox target) {
		//see if it hits my overall bounding box
		//set my bounding box
		temp0.set(pos);
		temp1.set(pos).add(dimensions);
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
			temp0.set(boxes.get(i).getPos()).add(pos);
			if (boxes.get(i).conflicts(temp0, target)) {
				return true;
			}
		}
		return false;
	}
	
	public float hits(Vector3 pos, Ray cameraRay, int sideSize) {
		if (id == GROUND_ID) {
			return -1.0f;
		}
		temp0.set(pos);
		temp1.set(pos).add(dimensions);
		
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
			temp0.set(boxes.get(i).getPos()).add(pos);
			dst = boxes.get(i).hits(temp0, cameraRay, sideSize);
			if (dst >= 0) {
				if (minDst < 0 || dst < minDst) {
					minDst = dst;
				}
			}
		}
		return minDst;
	}
	
	
	public float findCollision(Vector3 pos, Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		if (id == GROUND_ID) {
			return -1.0f;
		}
		temp0.set(pos);
		temp1.set(pos).add(dimensions);
		if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(temp0, temp1), temp2)) {
			//do nothing - we have a good chance it might hit us!
		}
		else {
			//doesn't hit our outside box - fail!
			System.out.println("out of bounds");
			return -1.0f;
		}
		
		temp2.scl(0.0f);
		
		float dst;
		float minDst = -1.0f;
		for (int i = 0; i < boxes.size(); i++) {
			temp0.set(boxes.get(i).getPos()).add(pos);
			dst = boxes.get(i).findCollision(temp0, cameraRay, temp1, dim, sideSize);
			if (dst >= 0) {
				System.out.println("Found onet");
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
		if (icon == null) {
			ArrayList<Model> models = new ArrayList<Model>();
			ArrayList<Vector3> pos = new ArrayList<Vector3>();
			populateModels(PieceUnderConstruction.ORIGIN, models, pos);
			icon = MeshAndModelBuilder.makeIcon(models, pos, dimensions.x, dimensions.y, dimensions.z);
		}
		return icon;
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
