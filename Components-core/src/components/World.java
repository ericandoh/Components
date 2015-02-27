package components;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;


/*
 * Represents a copy of the global world from the database, centered around the player me
 * Collection of a few chunks around the player
 * Load in/load out chunks as necessary
 */

public class World {
	
	public static final Plane FLAT = new Plane(new Vector3(0, 1, 0), 0);
	
	//north, south, east, west
	private static final int CHUNK_MAX_SIZE = Constants.CHUNK_MAX_SIZE;
	private static final int CHUNK_RENDER_SIZE = Constants.CHUNK_RENDER_SIZE;
	private static final int CHUNK_RENDER_START = Constants.CHUNK_RENDER_START;
	
	//4 chunks in north, east, west, south
	//can later make this 8 (for up/down) if we're ambitious about heights
	private Chunk centeredChunk[][];
	private MockWorld source;
	
	private Player me;
	private PieceUnderConstruction buildMe;
	private Vector3 lastFetch;
	//makes a new world centered at 0,0
	//only for testing purposes!
	//other versions will load a "world" from a preexisting database/file
	public World() {
		//replace this
		source = new MockWorld();
		Vector3 playerPos = new Vector3(0.0f, 1.0f, 0.0f);
		lastFetch = new Vector3(-1000.0f, -1000.0f, -1000.0f);
		centeredChunk = new Chunk[CHUNK_RENDER_SIZE][CHUNK_RENDER_SIZE];
		me = new Player(playerPos);
	}
	
	public int render(Renderable[] renderables, float renderLength, int renderSize) {
		//get player's x/y coordinates
		//go through chunks with relevant items with correct depths
		int count = 0;
		for (int x = 0; x < CHUNK_RENDER_SIZE; x++) {
			for (int z = 0; z < CHUNK_RENDER_SIZE; z++) {
				count = centeredChunk[x][z].render(me.getPos(), renderables, count, renderLength, renderSize);
			}
		}
		if (buildMe != null) {
			count = buildMe.render(me.getPos(), renderables, count, renderLength, renderSize);
		}
		return count;
	}
	public void addPiece(Piece p, Vector3 pos) {
		source.addPiece(p, pos);
	}
	public void setPlayerPos(Vector3 position) {
		me.setPosition(position);
		updatePos();
	}
	public void updatePos() {
		//Position megaCoords = me.getPos().scaleToCoordinates(CHUNK_MAX_SIZE);
		Vector3 megaCoords = Position.scaleToCoordinatesRounded(me.getPos(), CHUNK_MAX_SIZE);
		if (!lastFetch.equals(megaCoords)) {
			//load in new chunk
			lastFetch.set(megaCoords);
			//System.out.printf("---> %f %f\n", centeredCoords.x, centeredCoords.z);
			Vector3 pos = new Vector3();
			int xoffset = (int) (megaCoords.x + CHUNK_RENDER_START);
			int zoffset = (int) (megaCoords.z + CHUNK_RENDER_START);
			for (int x = xoffset; x < xoffset + CHUNK_RENDER_SIZE; x++) {
				for (int z = zoffset; z < zoffset + CHUNK_RENDER_SIZE; z++) {
					pos.set(x, 0.0f, z);
					//System.out.printf("-->%d %d \n", x, z);
					centeredChunk[x - xoffset][z - zoffset] = source.getMegaChunkByIndices(pos);
				}
			}
		}
	}
	public void addBlockToBuild(Vector3 p, int size, Placeable material) {
		BasicBox box = new BasicBox(material, size, p);
		if (buildMe != null) {
			if (buildMe.conflicts(box)) {
				System.out.println("Conflicts with another block in build");
				return;
			}
		}
		for (int x = 0; x < CHUNK_RENDER_SIZE; x++) {
			for (int z = 0; z < CHUNK_RENDER_SIZE; z++) {
				if (centeredChunk[x][z].conflicts(box)) {
					System.out.println("Conflicts with another block");
					return;
				}
			}
		}
		if (buildMe == null) {
			buildMe = new PieceUnderConstruction();
		}
		buildMe.addBlock(box);
	}
	public Piece exportBuildToPiece(PieceAndMaterialRepo repo) {
		if (buildMe == null) {
			return null;
		}
		Vector3 replace = new Vector3();
		replace.set(buildMe.getPos());
		Piece build = buildMe.exportToPiece(repo);
		if (build == null) {
			buildMe = null;
			return null;
		}
		buildMe = null;
		addPiece(build, replace);
		return build;
	}
	public Vector3 findCollision(Ray cameraRay, Vector3 place, Vector3 boxDimensions, int sideSize) {
		//if (buildMe == null) {
			//buildMe = new PieceInConstruction();
		//}
		float minDst = -1.0f;
		float dst;

		Vector3 temp = new Vector3();
		
		if (buildMe != null) {
			dst = buildMe.findCollision(cameraRay, temp, boxDimensions, sideSize);
			if (dst >= 0) {
				minDst = dst;
				place.set(temp);
			}
		}
		for (int x = 0; x < CHUNK_RENDER_SIZE; x++) {
			for (int z = 0; z < CHUNK_RENDER_SIZE; z++) {
				dst = centeredChunk[x][z].findCollision(cameraRay, temp, boxDimensions, sideSize);
				if (dst >= 0) {
					System.out.println("Found one!");
					if (minDst < 0 || dst < minDst) {
						minDst = dst;
						place.set(temp);
					}
				}
			}
		}
		if (minDst < 0) {
			System.out.println("No piece selected");
			if (Intersector.intersectRayPlane(cameraRay, FLAT, place)) {
				float dist = place.dst(cameraRay.origin);
				if (dist >= Position.getWidth(sideSize) * Constants.MAX_PLACE_REACH) {
					System.out.println("out of reach");
					return null;
				}
				place.y = Math.max(0.0f, place.y);
			}
			else {
				System.out.println("out of plane");
				return null;
			}
		}
		
		place.x = Position.quantizeValue(place.x, sideSize);
		place.y = Position.quantizeValue(place.y, sideSize);
		place.z = Position.quantizeValue(place.z, sideSize);
		
		if (place.y < 0)
			place.y = 0;
		
		System.out.println("Collision at ");
		System.out.println(place);
		return place;
	}
	public void removeBlock(Ray cameraRay, int sideSize) {
		if (buildMe == null) {
			return;
		}
		buildMe.removeBlock(cameraRay, sideSize);
	}
	public PieceInstance hit(Ray cameraRay, int sideSize) {
		float minDst = -1.0f;
		Vector3 dst = new Vector3();
		PieceInstance p, minP;
		minP = null;
		
		if (buildMe != null) {
			dst.x = buildMe.hits(cameraRay, sideSize);
			if (dst.x >= 0) {
				minDst = dst.x;
				minP = null;
				System.out.println("Hit the editting piece somehow LOL");
			}
		}
		
		for (int x = 0; x < CHUNK_RENDER_SIZE; x++) {
			for (int z = 0; z < CHUNK_RENDER_SIZE; z++) {
				p = centeredChunk[x][z].hit(cameraRay, sideSize, dst);
				if (dst.x >= 0) {
					if (minDst < 0 || dst.x < minDst) {
						minDst = dst.x;
						minP = p;
					}
				}
			}
		}
		return minP;
	}
}
