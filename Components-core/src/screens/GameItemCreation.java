package screens;

import render.MeshAndModelBuilder;
import render.TextureOrganizer;
import render.TextureType;
import screens.GameScreen.WindowMode;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import components.PieceAndMaterialRepo;

public class GameItemCreation extends ManagingGroup {
	
	private GameScreen owner;
	private PieceAndMaterialRepo repo;
	private Image background;
	private BoxPreviewRenderer render;
	private Slider[] sliders;
	
	private int colorSelectionID;
	
	public GameItemCreation(GameScreen owner, PieceAndMaterialRepo repo) {
		this.owner = owner;
		this.repo = repo;
		
		colorSelectionID = 0;
		
		background = new Image(TextureOrganizer.getTextureRegion(TextureType.OVERLAY_BACKGROUND));
		addActorManaged(background, 0.0f, 0.0f, 1.0f, 1.0f);
		
		TextButton backButton = new TextButton("<-", TextureOrganizer.getSkin());
		backButton.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				switchInventory();
			}
		});
		addActorManaged(backButton, 0.0f, 0.9f, 0.1f, 0.1f);
		
		
		TextButton title0 = new TextButton("Diffuse", TextureOrganizer.getSkin());
		title0.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				setColorSelection(0);
			}
		});
		addActorManaged(title0, 0.4f, 0.9f, 0.2f, 0.1f);
		
		TextButton title1 = new TextButton("Ambient", TextureOrganizer.getSkin());
		title1.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				setColorSelection(1);
			}
		});
		addActorManaged(title1, 0.6f, 0.9f, 0.2f, 0.1f);
		
		TextButton title2 = new TextButton("Specular", TextureOrganizer.getSkin());
		title2.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				setColorSelection(2);
			}
		});
		addActorManaged(title2, 0.8f, 0.9f, 0.2f, 0.1f);
		
		render = new BoxPreviewRenderer();
		addActorManaged(render, 0.1f, 0.4f, 0.3f, 0.3f);
		
		sliders = new Slider[3];
		
		sliders[0] = new Slider(0.0f, 1.0f, 0.05f, false, TextureOrganizer.getSkin());
		sliders[0].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setRenderer();
			}
		});
		addActorManaged(sliders[0], 0.5f, 0.7f, 0.4f, 0.1f);
		
		sliders[1] = new Slider(0.0f, 1.0f, 0.05f, false, TextureOrganizer.getSkin());
		sliders[1].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setRenderer();
			}
		});
		addActorManaged(sliders[1], 0.5f, 0.5f, 0.4f, 0.1f);
		
		sliders[2] = new Slider(0.0f, 1.0f, 0.05f, false, TextureOrganizer.getSkin());
		sliders[2].addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				setRenderer();
			}
		});
		addActorManaged(sliders[2], 0.5f, 0.3f, 0.4f, 0.1f);
		
		
		this.setVisible(false);
	}
	public void switchInventory() {
		owner.switchMode(WindowMode.ITEMSELECTION);
	}
	public void setColorSelection(int i) {
		this.colorSelectionID = i;
	}
	public void setRenderer() {
		float r = sliders[0].getValue();
		float g = sliders[1].getValue();
		float b = sliders[2].getValue();
		if (colorSelectionID == 0) {
			render.setDiffuse(r, g, b);
		}
		else if (colorSelectionID == 1) {
			render.setAmbient(r, g, b);
		}
		else {
			render.setSpecular(r, g, b);
		}
		System.out.printf("SEtting %f %f %f\n", r, g, b);
	}
}



class BoxPreviewRenderer extends Image {
	private Model box;
	private Color ambient, diffuse, specular;
	private float shiny;
	
	public BoxPreviewRenderer() {
		super();
		box = MeshAndModelBuilder.createBox(new Material(ColorAttribute.createDiffuse(Color.RED)));
		ambient = new Color();
		diffuse = new Color(Color.RED);
		specular = new Color();
		shiny = 1.0f;
		setMaterial();
	}
	public void setAmbient(float r, float g, float b) {
		ambient.set(r, g, b, 1.0f);
		setMaterial();
	}
	public void setDiffuse(float r, float g, float b) {
		diffuse.set(r, g, b, 1.0f);
		setMaterial();
	}
	public void setSpecular(float r, float g, float b) {
		specular.set(r, g, b, 1.0f);
		setMaterial();
	}
	public void setMaterial() {
		if (box != null)
			box.dispose();
		Material material = new Material(	ColorAttribute.createAmbient(this.ambient), 
				ColorAttribute.createDiffuse(this.diffuse),
				ColorAttribute.createSpecular(this.specular), 
				FloatAttribute.createShininess(this.shiny));
		box = MeshAndModelBuilder.createBox(material);
		TextureRegion tex = MeshAndModelBuilder.makeIcon(box, 1.0f, 1.0f, 1.0f, false);
		this.setDrawable(new TextureRegionDrawable(tex));
	}
}
