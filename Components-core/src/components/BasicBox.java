package components;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class BasicBox {
	
	protected Vector3 temp0, temp1, temp2, temp3;
	
	//position relative to whichever piece this is a part of
	protected Vector3 pos, dim;
	private Placeable mat;
	private int size;
	private boolean isBox;
	
	public BasicBox(Placeable mat, int size, Vector3 pos) {
		this.pos = pos;
		this.mat = mat;
		this.size = size;
		this.isBox = this.mat.isBox();
		this.dim = new Vector3();
		this.mat.getDimension(dim, size);
		makeTemporary();
	}
	public BasicBox(BasicBox copy) {
		this.pos = copy.pos;
		this.mat = copy.mat;
		this.size = copy.size;
		this.isBox = copy.isBox;
		this.dim = new Vector3();
		this.mat.getDimension(dim, size);
		makeTemporary();
	}
	public void makeTemporary() {
		temp0 = new Vector3();
		temp1 = new Vector3();
		temp2 = new Vector3();
		temp3 = new Vector3();
	}
	
	public Placeable getMat() {
		return this.mat;
	}
	public int getSize() {
		return this.size;
	}
	public Vector3 getDimension() {
		return this.dim;
	}
	public Vector3 getPos() {
		return pos;
	}
	public Vector3 offset(Vector3 offset) {
		pos.add(offset);
		return pos;
	}
	public boolean isBox() {
		return this.isBox;
	}
	
	public boolean conflicts(Vector3 pos, BasicBox box) {
		if (isBox) {
			//set bounding for me
			temp0.set(pos);
			temp1.set(pos).add(dim);
			//set bounding for basicbox
			temp2.set(box.getPos());
			temp3.set(box.getPos()).add(box.getDimension());
			//check if it hits bounding box
			if (	   temp0.x < temp3.x && temp1.x > temp2.x 
					&& temp0.y < temp3.y && temp1.y > temp2.y 
					&& temp0.z < temp3.z && temp1.z > temp2.z  ) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return ((Piece)this.getMat()).conflicts(pos, box);
		}
	}
	public float hits(Vector3 pos, Ray cameraRay, int sideSize) {
		if (isBox) {
			//set bounding for me
			temp0.set(pos);
			temp1.set(pos).add(dim);
			if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(temp0, temp1), temp2)) {
				//doesn't even hit the bounds LOL
				return temp2.dst(cameraRay.origin);
			}
			else {
				return -1.0f;
			}
		}
		else {
			return ((Piece)this.getMat()).hits(pos, cameraRay, sideSize);
		}
	}
	public float findCollision(Vector3 pos, Ray cameraRay, Vector3 place, Vector3 dim, int sideSize) {
		if (isBox) {
			temp0.set(pos);
			temp1.set(pos).add(dim);
			if (Intersector.intersectRayBounds(cameraRay, new BoundingBox(temp0, temp1), temp2)) {
				//doesn't even hit the bounds LOL
				if (Math.abs(temp2.x - temp0.x) < 0.01) {
					//place.set(savedPos1.x - sideWidth, savedPos1.y, savedPos1.z);
					place.set(temp0.x - dim.x, temp2.y, temp2.z);
				}
				else if (Math.abs(temp2.x - temp1.x) < 0.01) {
					place.set(temp1.x, temp2.y, temp2.z);
				}
				else if (Math.abs(temp2.y - temp0.y) < 0.01) {
					place.set(temp2.x, temp0.y - dim.y, temp2.z);
				}
				else if (Math.abs(temp2.y - temp1.y) < 0.01) {
					place.set(temp2.x, temp1.y, temp2.z);
				}
				else if (Math.abs(temp2.z - temp0.z) < 0.01) {
					place.set(temp2.x, temp2.y, temp0.z - dim.z);
				}
				else if (Math.abs(temp2.z - temp1.z) < 0.01) {
					place.set(temp2.x, temp2.y, temp1.z);
				}
				else {
					System.out.println("Shit went wrong");
				}
				return temp2.dst(cameraRay.origin);
			}
			else {
				return -1.0f;
			}
		}
		else {
			return ((Piece)this.getMat()).findCollision(pos, cameraRay, place, dim, sideSize);
		}
	}
}