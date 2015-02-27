package screens;

import render.TextureOrganizer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class ColorRectActor extends Actor {

	public ColorRectActor(float r, float g, float b, float alp) {
		super();
		setColor(r, g, b, alp);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		//gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		ShapeRenderer shaper = TextureOrganizer.getShapeRenderer();
		shaper.setTransformMatrix(batch.getTransformMatrix());
		shaper.setProjectionMatrix(batch.getProjectionMatrix());
		
		shaper.begin(ShapeType.Filled);
		shaper.setColor(getColor());
		shaper.rect(getX(), getY(), getOriginX(), getOriginY(), getWidth() - 1, getHeight() - 1, getScaleX(), getScaleY(), getRotation());
		shaper.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		batch.begin();
	}
}
