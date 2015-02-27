package screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

import components.Placeable;

/*
 * NO RACISM IMPLIED
 */

public class ColoredRectActor extends Actor {
	
	private Placeable block;
	private TextureRegion renderMe;
	
	public ColoredRectActor() {
		super();
		setColor(1, 1, 1, 0.0f);
	}
	public ColoredRectActor(float r, float g, float b, float alp) {
		super();
		setColor(r, g, b, alp);
	}
	public ColoredRectActor(Placeable block) {
		super();
		setBlock(block);
	}
	public Placeable getPlaceable() {
		return block;
	}
	public void setBlock(Placeable block) {
		this.block = block;
		renderMe = block.getIcon();
	}
	public Placeable getBlock() {
		return block;
	}
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (renderMe == null) {
			return;
		}
		batch.draw(renderMe, getX(), getY(), getOriginX(), getOriginY(), 
				getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
	}
	
}
