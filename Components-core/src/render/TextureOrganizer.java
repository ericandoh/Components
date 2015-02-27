package render;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/*
 * Imports some textures
 */

public class TextureOrganizer {

	private static HashMap<TextureType, Texture> textures;
	
	private static BitmapFont font;
	private static Skin skin;
	
	private static ShapeRenderer shapeRenderer;
	
	public static void init() {

		System.out.println("We need to make some textures mang!");
		
		textures = new HashMap<TextureType, Texture>();
		
		Texture tex;
		tex = new Texture(Gdx.files.internal("badlogic.jpg"));
		
		tex = new Texture(Gdx.files.internal("screenBG.png"));
		textures.put(TextureType.OVERLAY_BACKGROUND, tex);
		
		font = new BitmapFont();
		//new BitmapFont(FileHandle fontFile)...BMFont file
		
		skin = new Skin(Gdx.files.internal("skins/uiskin.json"));
		
		shapeRenderer = new ShapeRenderer();
	}
	public static TextureRegion getTextureRegion(TextureType type) {
		return new TextureRegion(textures.get(type));
	}
	public static TextureRegion getTextureRegion(TextureType type, int width, int height) {
		return new TextureRegion(textures.get(type), width, height);
	}
	public static TextureRegion getTextureRegion(TextureType type, int x, int y, int width, int height) {
		return new TextureRegion(textures.get(type), x, y, width, height);
	}
	public static BitmapFont getFont() {
		return font;
	}
	public static Skin getSkin() {
		return skin;
	}
	public static ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}
	public static void dispose() {
		for (Texture tex: textures.values()) {
			tex.dispose();
		}
		font.dispose();
	}
}