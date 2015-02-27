package screens;

import java.util.ArrayList;

import render.TextureOrganizer;
import render.TextureType;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import components.Constants;
import components.NamedMaterial;
import components.Piece;
import components.PieceAndMaterialRepo;
import components.Placeable;

public class GameItemSelection extends ManagingGroup {

	private GameScreen owner;
	
	private Image background;
	private ScrollPane scroll;
	private Table tiles;
	private TextField searchField0, searchField1;
	
	private PieceAndMaterialRepo repo;
	private ArrayList<NamedMaterial> loadedMats;
	private ArrayList<Piece> loadedPieces;
	
	private ViewingMode viewing;
	
	private enum ViewingMode {
		NONE, MATERIAL, PIECE;
	}
	
	public GameItemSelection(GameScreen owner, PieceAndMaterialRepo repo) {
		this.owner = owner;
		this.repo = repo;
		background = new Image(TextureOrganizer.getTextureRegion(TextureType.OVERLAY_BACKGROUND));
		addActorManaged(background, 0.0f, 0.0f, 1.0f, 1.0f);
		
		TextButton leftTitle = new TextButton(Constants.LEFT_TITLE, TextureOrganizer.getSkin());
		leftTitle.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				if (viewing != ViewingMode.MATERIAL) {
					viewing = ViewingMode.MATERIAL;
					fillTable();
				}
			}
		});
		addActorManaged(leftTitle, 0.0f, 0.9f, 0.5f, 0.1f);
		
		TextButton rightTitle = new TextButton(Constants.RIGHT_TITLE, TextureOrganizer.getSkin());
		rightTitle.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				if (viewing != ViewingMode.PIECE) {
					viewing = ViewingMode.PIECE;
					fillTable();
				}
			}
		});
		addActorManaged(rightTitle, 0.5f, 0.9f, 0.5f, 0.1f);
		
		searchField0 = new TextField("", TextureOrganizer.getSkin());
		searchField0.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char c) {
				//doSearch(0, textField.getText());
			}
		});
		addActorManaged(searchField0, 0.0f, 0.8f, 0.5f, 0.1f);
		
		searchField1 = new TextField("", TextureOrganizer.getSkin());
		searchField1.setTextFieldListener(new TextFieldListener() {
			@Override
			public void keyTyped(TextField textField, char c) {
				doSearch(1, textField.getText());
			}
		});
		addActorManaged(searchField1, 0.5f, 0.8f, 0.5f, 0.1f);
		
		tiles = new Table();
		
		scroll = new ScrollPane(tiles, TextureOrganizer.getSkin());
		scroll.setScrollingDisabled(true, false);
		
		float margin = 0.05f;
		addActorManaged(scroll, margin, margin, 1.0f - margin * 2, 0.8f - margin * 2);
		
		this.viewing = ViewingMode.NONE;
		//if (getWidth() != 0) {
			//fillTable();
		//}
		
		this.addListener(new InputListener() {
			@Override
			public boolean keyDown(InputEvent event, int keycode) {
				if (keycode == Input.Keys.ESCAPE) {
					if (getStage().getKeyboardFocus() == searchField0){
						getStage().unfocus(searchField0);
						return true;
					}
					else if (getStage().getKeyboardFocus() == searchField1){
						getStage().unfocus(searchField1);
						return true;
					}
				}
				else if (keycode == Input.Keys.ENTER) {
					if (getStage().getKeyboardFocus() == searchField0){
						getStage().unfocus(searchField0);
						//get text from searchField0
						doSearch(0, searchField0.getText());
						searchField0.setText("");
						return true;
					}
					else if (getStage().getKeyboardFocus() == searchField1){
						getStage().unfocus(searchField1);
						//get text from searchField1
						doSearch(1, searchField1.getText());
						searchField1.setText("");
						return true;
					}
				}
				return false;
			}
		});
		
		this.setVisible(false);
	}
	@Override
	public void resize(float width, float height) {
		super.resize(width, height);
		fillTable();
	}
	
	private class TileListener extends ClickListener {
		private ColoredRectActor owner;
		public TileListener(ColoredRectActor owner) {
			super();
			this.owner = owner;
		}
		@Override
	    public void clicked(InputEvent event, float x, float y) {
			System.out.println("Selected");
			setBarMaterial(owner.getBlock());
		}
	}
	
	//later will take in arguments
	public void fillTable() {
		if (scroll.getWidth() == 0) {
			return;
		}
		ArrayList<Placeable> currentView = new ArrayList<Placeable>();
		if (viewing == ViewingMode.MATERIAL) {
			if (loadedMats == null)
				loadedMats = repo.getAllMats();
			for (NamedMaterial m: loadedMats) {
				currentView.add(m);
			}
		}
		else {
			if (loadedPieces == null)
				loadedPieces = repo.getAllPieces();
			for (Piece m: loadedPieces) {
				currentView.add(m);
			}
		}
		
		tiles.clearChildren();
		float cellWidth = 64.0f;
		int numAcross = (int)(scroll.getWidth() / cellWidth);
		if (numAcross <= 0) {
			numAcross = 1;
		}
		int count = 0;
		boolean showedNew = false;
		Actor tile;
		while (count < currentView.size()) {
			for (int y = 0; y < numAcross; y++) {
				if (!showedNew) {
					tile = new Label("New", TextureOrganizer.getSkin());
					((Label)tile).setAlignment(Align.center);
					tile.addListener(new ClickListener() {
						@Override
					    public void clicked(InputEvent event, float x, float y) {
							makeNew();
						}
					});
					showedNew = true;
				}
				else if (count >= currentView.size()) {
					break;
				}
				else {
					tile = new ColoredRectActor(currentView.get(count++));
					tile.addListener(new TileListener((ColoredRectActor)tile));
				}
				tiles.add(tile).width(cellWidth).height(cellWidth).fill();
			}
			if (count < currentView.size())
				tiles.row();
		}
	}
	public void doSearch(int keyboard, String text) {
		System.out.println(text);
	}
	public void makeNew() {
		//called when we press the "NEW MAT" button
		System.out.println("NEW!");
	}
	public void setBarMaterial(Placeable mat) {
		owner.setBlock(mat);
	}
	@Override
	public void setVisible(boolean visible) {
		if (!visible && getStage() != null) {
			getStage().unfocusAll();
		}
		super.setVisible(visible);
	}
}
