package components;

import com.badlogic.gdx.math.Vector3;

public class GroundInstance extends PieceInstance {
	
	
	public GroundInstance(Vector3 pos, int size) {
		super(null, pos);
		
		//generate some flat ground mhmmm
		float width = Position.getWidth(size);
		
		int[][][] materials = new int[(int)width][1][(int)width];
		for (int x = 0; x < width; x++) {
			for (int z = 0; z < width; z++) {
				materials[x][0][z] = 1;
			}
		}
		
	}
}

