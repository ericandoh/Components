package components;

/* Class with a bunch of game constants
 * 
 * 
 * Some side notes here/organizing here
 * 
 * -Render offscreen + save to Texture: http://stackoverflow.com/questions/7551669/libgdx-spritebatch-render-to-texture
 * -Jitter lines http://stackoverflow.com/questions/20082233/uneven-edges-and-glitches-where-3d-objects-meet
 */

public class Constants {
	//general
	public static final int VERSION = 1;
	
	//user limitation constants, based on sideSize
	public static final float MAX_PLACE_REACH = 12.0f;
	public static final float MAX_PIECE_RADIUS = 250.0f;
	
	//graphic limitation constants
	public static final int MAX_RENDERABLE_ITEMS = 5000;
	
	//chunk/map related constants
	public static final int CHUNK_SUBDIVISION = 4;
	public static final int CHUNK_MAX_SIZE = 4;				//as power of subdivision
	//this must be smaller than chunk_max_size
	public static final int CHUNK_GROUND_SIZE = 3;
	public static final int CHUNK_RENDER_SIZE = 2;			//how many chunks to render around
	public static final int CHUNK_RENDER_START = -CHUNK_RENDER_SIZE / 2;
	public static final int CHUNK_RENDER_END = CHUNK_RENDER_SIZE + CHUNK_RENDER_START;
	
	//UI constants
	public static final int NUM_ITEMS_BAR = 10;
	public static final String LEFT_TITLE = "Materials";
	public static final String RIGHT_TITLE = "Pieces";
	public static final int ICON_WIDTH = 64;
}
