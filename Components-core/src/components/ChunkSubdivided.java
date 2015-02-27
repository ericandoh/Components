package components;

public class ChunkSubdivided {
	/*
	private static final int CHUNK_SUBDIVISION = Constants.CHUNK_SUBDIVISION;
	private static final int CHUNK_MAX_SIZE = Constants.CHUNK_MAX_SIZE;
	private static final int CHUNK_GROUND_SIZE = Constants.CHUNK_GROUND_SIZE;
	private static final int CHUNK_RENDER_START = Constants.CHUNK_RENDER_START;
	private static final int CHUNK_RENDER_SIZE = Constants.CHUNK_RENDER_SIZE;
	
	private ArrayList<PieceInstance> pieces;
	private Chunk[][] subChunks;
	//this is real coordinates not scaled/restricted
	private Vector3 pos, temp;
	private int size;
	
	//optimization parameter - if no pieces in subpieces, don't iterate!
	private int totalSubPieces;
	
	//makes a blank chunk
	public Chunk(Vector3 pos, int size) {
		//System.out.printf("Making chunk %f %f %d\n", pos.x, pos.z ,size);
		totalSubPieces = 0;
		this.pos = pos;
		this.temp = new Vector3(0.0f, 0.0f, 0.0f);
		this.size = size;
		pieces = new ArrayList<PieceInstance>();
		if (size == 0) {
			subChunks = null;
		}
		else {
			subChunks = new Chunk[CHUNK_SUBDIVISION][CHUNK_SUBDIVISION];
			for (int i = 0; i < CHUNK_SUBDIVISION; i++) {
				subChunks[i] = new Chunk[CHUNK_SUBDIVISION];
			}
		}
		if (size == CHUNK_MAX_SIZE) {
			float totalWidth = Position.getWidth(CHUNK_MAX_SIZE);
			float totalX = pos.x;
			float totalZ = pos.z;
			float width = Position.getWidth(CHUNK_GROUND_SIZE);
			Vector3 groundPosition;
			for (float xi = 0; xi < totalWidth; xi += width) {
				for (float zi = 0; zi < totalWidth; zi += width) {
					groundPosition = new Vector3(totalX + xi, 0.0f, totalZ + zi);
					this.addPiece(new PieceInstance(MockWorld.groundPiece, groundPosition, CHUNK_GROUND_SIZE));
				}
			}
		}
	}
	
	public int render(Vector3 p, Renderable[] renderables, int count) {
		//if depth == 0, only render pieces here (at that depth)
		//else render pieces here with correct depth, then check subchunks
		//System.out.printf("Rendering at %d %d, size %d\n", (int)this.pos.x, (int)this.pos.y, size);
		if (totalSubPieces > 0) {
			//find chunk that has this player, and render in the 4 chunks corresponding
			//Position scaled = pos.scaleToUnit(size);
			Position.scaleToUnitReal(temp, p, pos, size);
			Chunk c;
			int xoffset = (int) (temp.x + CHUNK_RENDER_START);
			int zoffset = (int) (temp.z + CHUNK_RENDER_START);
			int xstart = Math.max(xoffset, 0);
			int zstart = Math.max(zoffset, 0);
			int xend = Math.min(CHUNK_SUBDIVISION, xoffset + CHUNK_RENDER_SIZE);
			int zend = Math.min(CHUNK_SUBDIVISION, zoffset + CHUNK_RENDER_SIZE);
			for (int x = xstart; x < xend; x++) {
				for (int z = zstart; z < zend; z++) {
					c = subChunks[x][z];
					if (c != null)
						count = c.render(p, renderables, count);
				}
			}
		}
		for (PieceInstance pi: pieces) {
			count = pi.render(p, renderables, count);
		}
		return count;
	}
	public Chunk getChunk(Vector3 p, int targetSize) {
		if (size <= targetSize) {
			return this;
		}
		//Position scaled = pos.scaleToUnit(size);
		Vector3 scaled = Position.scaleToUnit(p, size);
		Chunk c = subChunks[(int)scaled.x][(int)scaled.z];
		if (c == null) {
			makeChunk((int)scaled.x, (int)scaled.z);
			c = subChunks[(int)scaled.x][(int)scaled.z];
		}
		return c.getChunk(p, targetSize);
	}
	public void addPiece(PieceInstance p) {
		int targetSize = p.getMaxSize();
		if (size <= targetSize) {
			//System.out.printf("Added at %d %d, size %d == %d\n", (int)p.getPos().x, (int)p.getPos().z, size, targetSize);
			pieces.add(p);
		}
		else {
			totalSubPieces++;
			//Position scaled = p.getPos().scaleToUnit(size);
			Vector3 scaled = Position.scaleToUnit(p.getPos(), size);
			Chunk c = subChunks[(int)scaled.x][(int)scaled.z];
			if (c == null) {
				makeChunk((int)scaled.x, (int)scaled.z);
				c = subChunks[(int)scaled.x][(int)scaled.z];
			}
			c.addPiece(p);
		}
	}
	public boolean removePiece(PieceInstance p) {
		int targetSize = p.getMaxSize();
		if (size <= targetSize) {
			System.out.printf("Removing at %d %d, size %d\n", (int)pos.x, (int)pos.z, size);
			return pieces.remove(p);
		}
		else {
			//Position scaled = pos.scaleToUnit(size);
			Vector3 scaled = Position.scaleToUnit(p.getPos(), size);
			Chunk c = subChunks[(int)scaled.x][(int)scaled.z];
			if (c == null) {
				makeChunk((int)scaled.x, (int)scaled.z);
				c = subChunks[(int)scaled.x][(int)scaled.z];
			}
			if (c.removePiece(p)) {
				totalSubPieces--;
				return true;
			}
			return false;
		}
	}
	public void makeChunk(int x, int z) {
		Vector3 subChunkPos = new Vector3(pos.x + x * Position.getWidth(size - 1), 0.0f, pos.z + z * Position.getWidth(size-1));
		subChunks[x][z] = new Chunk(subChunkPos, size - 1);
	}*/
}
