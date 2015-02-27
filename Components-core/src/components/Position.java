package components;

import com.badlogic.gdx.math.Vector3;

public class Position {
	
	private static final int CHUNK_SUBDIVISION = Constants.CHUNK_SUBDIVISION;
	private static final int CHUNK_MAX_SIZE = Constants.CHUNK_MAX_SIZE;
	private static float[] SUBDIVISIONS;
	
	public static void setup() {
		//this must be called before each call
		SUBDIVISIONS = new float[CHUNK_MAX_SIZE + 1];
		for (int i = 0; i < CHUNK_MAX_SIZE + 1; i++) {
			SUBDIVISIONS[i] = (float)Math.pow(CHUNK_SUBDIVISION, i);
		}
	}
	
	public static float quantizeValue(float val, int scale) {
		return (float)(Math.floor(val / SUBDIVISIONS[scale]) * (float)SUBDIVISIONS[scale]);
	}
	
	public static void scaleToUnitReal(Vector3 dest, Vector3 pos, Vector3 upper, int scale) {
		dest.x = (float)Math.round((pos.x - upper.x) / SUBDIVISIONS[scale - 1]);
		dest.z = (float)Math.round((pos.z - upper.z) / SUBDIVISIONS[scale - 1]);
	}
	
	//converts real position to position within chunk of scale scale
	//used to locate position within chunks
	public static Vector3 scaleToUnit(Vector3 pos, int scale) {
		return new Vector3((float)(Math.floor(pos.x / SUBDIVISIONS[scale - 1] ) % CHUNK_SUBDIVISION + CHUNK_SUBDIVISION) % CHUNK_SUBDIVISION, 
							pos.y, 
							(float)(Math.floor(pos.z / SUBDIVISIONS[scale - 1]) % CHUNK_SUBDIVISION + CHUNK_SUBDIVISION) % CHUNK_SUBDIVISION);
	}
	//used for finding mega-chunks (which aren't nested in subchunks)
	public static Vector3 scaleToCoordinates(Vector3 pos, int scale) {
		return new Vector3((float)Math.floor(pos.x / SUBDIVISIONS[scale]), 
							(float)pos.y, 
							(float)Math.floor(pos.z / SUBDIVISIONS[scale]));
	}
	//used to approximate player location to a centered chunk
	public static Vector3 scaleToCoordinatesRounded(Vector3 pos, int scale) {
		return new Vector3((float)Math.round(pos.x / SUBDIVISIONS[scale]), 
							(float)0.0f, 
							(float)Math.round(pos.z / SUBDIVISIONS[scale]));
	}
	public static float getWidth(int scale) {
		return SUBDIVISIONS[scale];
	}
	
	public static void main(String[] args) {
		//Position p = new Vector3(23, 0, 17);
		/*System.out.println(p.scaleToUnit(1));
		System.out.println(p.scaleToUnit(2));
		System.out.println(p.scaleToUnit(3));*/
	}
}
