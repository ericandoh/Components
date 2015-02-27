package screens;

import render.TextureOrganizer;
import render.TextureType;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import components.Constants;
import components.Placeable;

public class GameItemBar extends ManagingGroup {
	
	private static final int NUM_ITEMS_BAR = Constants.NUM_ITEMS_BAR;
	
	private Image background;
	private ColoredRectActor[] itemIcons;
	private Actor selector;
	private int selected;
	private float itemWidth;
	
	public GameItemBar() {
		super();
		
		//background = new BackgroundActor();
		background = new Image(TextureOrganizer.getTextureRegion(TextureType.OVERLAY_BACKGROUND));
		addActorManaged(background, 0.0f, 0.0f, 1.0f, 1.0f);
		
		itemIcons = new ColoredRectActor[NUM_ITEMS_BAR];
		itemWidth = 1.0f / NUM_ITEMS_BAR;
		float xmargin = 0.01f;
		float ymargin = 0.1f;
		
		selected = 0;
		selector = new ColorRectActor(0.3f, 0.3f, 0.3f, 0.4f);
		addActorManaged(selector, 0.0f, 0.0f, itemWidth, 1.0f);
		
		for (int i = 0; i < NUM_ITEMS_BAR; i++) {
			itemIcons[i] = new ColoredRectActor();
			//itemIcons[i].setColor((float)Math.random(), (float)Math.random(), (float)Math.random(), 0.8f);
			addActorManaged(itemIcons[i], itemWidth*i + xmargin, ymargin, itemWidth - 2*xmargin, 1.0f - 2*ymargin);
			//System.out.printf("%f %f %f %f\n", itemWidth*i + xmargin, ymargin, itemWidth - 2*xmargin, 1.0f - 2*ymargin);
		}
	}
	
	@Override
	public void resize(float width, float height) {
		super.resize(width, height);
		float rawX, rawY;
		float rawWidth = itemIcons[0].getWidth();
		float rawHeight = itemIcons[0].getHeight();
		float rawBound = Math.min(rawWidth, rawHeight);
		int roundDetail = Constants.ICON_WIDTH / 4;
		int roundedBound = (int) (Math.floor(rawBound / roundDetail) * roundDetail);
		if (roundedBound == 0) {
			return;
		}
		int addWidth = (int)(rawWidth - roundedBound) / 2;
		int addHeight = (int)(rawHeight - roundedBound) / 2;
		for (int i = 0; i < NUM_ITEMS_BAR; i++) {
			rawX = itemIcons[i].getX();
			rawY = itemIcons[i].getY();
			itemIcons[i].setBounds(rawX + addWidth, rawY + addHeight, roundedBound, roundedBound);
		}
	}
	public void setSelector(int index) {
		//out of bounds?
		if (index < 0 || index >= NUM_ITEMS_BAR)
			return;
		selected = index;
		changeBoundsFor(selector, itemWidth * selected, 0.0f);
	}
	public void incrementSelector(int amount) {
		selected = (((selected + amount) % NUM_ITEMS_BAR) + NUM_ITEMS_BAR) % NUM_ITEMS_BAR;
		changeBoundsFor(selector, itemWidth * selected, 0.0f);
	}
	public boolean isBoxAtSelected() {
		if (getAtSelected() == null)
			return true;
		return getAtSelected().isBox();
	}
	public Placeable getAtSelected() {
		return itemIcons[selected].getBlock();
	}
	public void setAtSelected(Placeable mat) {
		itemIcons[selected].setBlock(mat);
	}
}
