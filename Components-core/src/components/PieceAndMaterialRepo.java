package components;

import java.util.ArrayList;

import render.MeshAndModelBuilder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.math.Vector3;

/*
 * Stores various pieces and materials for the entire game
 */

public class PieceAndMaterialRepo {
	
	private static final int CHUNK_GROUND_SIZE = Constants.CHUNK_GROUND_SIZE;
	
	public static Piece groundPiece;
	
	private ArrayList<Piece> loadedPieces;
	private ArrayList<NamedMaterial> materials;
	
	public PieceAndMaterialRepo() {
		loadedPieces = new ArrayList<Piece>();
		materials = new ArrayList<NamedMaterial>();
		materials.add(new NamedMaterial());
		loadOthers();
		
		groundPiece = new Piece(MeshAndModelBuilder.groundPlane, "Ground", CHUNK_GROUND_SIZE, (int)Position.getWidth(CHUNK_GROUND_SIZE), 0, (int)Position.getWidth(CHUNK_GROUND_SIZE), this);
		groundPiece.setID(Piece.GROUND_ID);
		
		//Piece basic = new Piece(MeshAndModelBuilder.simpleBox(), "basic", 0, 1, 1, 1, this);
		//loadedPieces.add(basic);
	}
	public void loadOthers() {
		//make some materials
		NamedMaterial mat = new NamedMaterial(1, "stone", Color.GRAY);
		NamedMaterial stone = new NamedMaterial(2, "grass", Color.GREEN);
		NamedMaterial gold = new NamedMaterial(3, "gold", Color.YELLOW);
		NamedMaterial blood = new NamedMaterial(4, "blood", Color.RED);
		NamedMaterial water = new NamedMaterial(5, "water", Color.BLUE);
		
		materials.add(mat);
		materials.add(stone);
		materials.add(gold);
		materials.add(blood);
		materials.add(water);
		
		//make some pieces
		//Piece basic = new Piece(MeshAndModelBuilder.simpleBox(), "basic", 1, 0, 1);
		//addPiece(basic, new Position(0, 1, 1));
		int[][][] materials = {{{0,0},
								{1,0},
								{0,0}},
							{	{1,0},
								{1,0},
								{1,0}},
							{	{0,0},
								{1,1},
								{0,0}}};
		ArrayList<BasicBox> namedMats = new ArrayList<BasicBox>();
		BasicBox box;
		for (int x = 0; x < materials.length; x++) {
			for (int y = 0; y < materials[x].length; y++) {
				for (int z = 0; z < materials[x][y].length; z++) {
					if (materials[x][y][z] == 0)
						continue;
					box = new BasicBox(this.materials.get(materials[x][y][z]), 0, new Vector3(x, y, z));
					namedMats.add(box);
				}
			}
		}
		Piece advanced = new Piece(namedMats, this);
		//loadedPieces.add(basic);
		loadedPieces.add(advanced);
	}
	public Material getMaterial(int id) {
		if (id >= materials.size())
			return materials.get(0).getMat();
		return materials.get(id).getMat();
	}
	public NamedMaterial getNamedMaterial(int id) {
		if (id >= materials.size())
			return materials.get(0);
		return materials.get(id);
	}
	public ArrayList<NamedMaterial> getAllMats() {
		return materials;
	}
	public ArrayList<Piece> getAllPieces() {
		return loadedPieces;
	}
	public void addPiece(Piece piece) {
		if (piece == null)
			return;
		loadedPieces.add(piece);
	}
}
