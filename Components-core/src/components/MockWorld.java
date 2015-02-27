package components;

import com.badlogic.gdx.math.Vector3;


//replace this with database connections to find actual world 

public class MockWorld {
	
	private static final int CHUNK_MAX_SIZE = Constants.CHUNK_MAX_SIZE;
	
	private Chunk[][][] chunks;
	
	public MockWorld() {
		chunks = new Chunk[4][100][100];
		for (int i = 0; i < 4; i++) {
			for (int x = 0; x < 100; x++) {
				chunks[i][x] = new Chunk[100];
			}
		}
	}
	
	public Chunk getMegaChunk(Vector3 pos) {
		//Position megaCoords = pos.scaleToCoordinates(CHUNK_MAX_SIZE);
		Vector3 megaCoords = Position.scaleToCoordinates(pos, CHUNK_MAX_SIZE);
		return getMegaChunkByIndices(megaCoords);
	}
	public Chunk getMegaChunkByIndices(Vector3 megaCoords) {
		int quadrant = 0;
		int refX = 0;
		int refZ = 0;
		if (megaCoords.x < 0) {
			quadrant += 2;
			refX = (int)-megaCoords.x;
		}
		else {
			refX = (int)megaCoords.x;
		}
		if (megaCoords.z < 0) {
			quadrant += 1;
			refZ = (int)-megaCoords.z;
		}
		else {
			refZ = (int)megaCoords.z;
		}
		Chunk c = chunks[quadrant][refX][refZ];
		if (c == null) {
			makeChunk(quadrant, refX, refZ, megaCoords.x, megaCoords.z);
			c = chunks[quadrant][refX][refZ];
		}
		return c;
	}
	public void makeChunk(int quad, int refX, int refZ, float x, float z) {
		Vector3 subChunkPos = new Vector3(x * Position.getWidth(CHUNK_MAX_SIZE), 0.0f, z * Position.getWidth(CHUNK_MAX_SIZE));
		chunks[quad][refX][refZ] = new Chunk(subChunkPos);
	}
	public Chunk getChunk(Vector3 pos, int size) {
		Chunk mega = getMegaChunk(pos);
		return mega;
		//return mega.getChunk(pos, size);
	}
	public void addPiece(Piece p, Vector3 pos) {
		PieceInstance instance = new PieceInstance(p, pos);
		Chunk mega = getMegaChunk(pos);
		mega.addPiece(instance);
	}
	
	//unit testing for worlds/chunks
	public static void main(String[] args) {
		int lol = (int)(-1 % 6);
		int lol2 = (int)(-2 % 6);
		System.out.printf("%d %d\n", lol, lol2);
		Vector3 megaCoords = Position.scaleToCoordinates(new Vector3(-200, 0, 5), CHUNK_MAX_SIZE);
		System.out.println(megaCoords);
	}
}
