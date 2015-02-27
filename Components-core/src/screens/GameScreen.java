package screens;

import render.GameRenderer;
import render.MeshAndModelBuilder;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.components.Components;

import components.Constants;
import components.NamedMaterial;
import components.Piece;
import components.PieceAndMaterialRepo;
import components.PieceInstance;
import components.Placeable;
import components.Position;
import components.World;

public class GameScreen extends BasicScreen implements InputProcessor {
	
	private static final Directions[] DIRECTION_ORDER = 
		{Directions.FORWARD, Directions.BACKWARD, Directions.LEFT, Directions.RIGHT, 
		Directions.UP, Directions.DOWN};
	private static int[] DIRECTION_KEYS = 
		{Input.Keys.W, Input.Keys.S, Input.Keys.A, 
		Input.Keys.D, Input.Keys.SPACE, Input.Keys.SHIFT_LEFT};
	
	private World world;

	private GameRenderer renderer;	
	private ShapeRenderer shapes;
	private InputMultiplexer plexer;
	
	private Stage stage;
	private ManagingGroup boxManager;
	private GameItemBar bottomBar;
	private GameItemSelection selectScreen;
	
	private boolean editting;
	
	private boolean paused;
	private boolean activeScreen;
	
	private boolean[] directionTrackers;
	
	private float degreesPerPixel = 0.5f;
	private float deltaX = 0.0f, deltaY = 0.0f;
	
	private int edittingSize;
	private PieceAndMaterialRepo pmRepo;
	
	public GameScreen(Components owner) {
		super(owner);
		paused = true;
		directionTrackers = new boolean[DIRECTION_ORDER.length];
		for (int i = 0; i < DIRECTION_ORDER.length; i++) {
			directionTrackers[i] = false;
		}
		editting = false;
		edittingSize = 0;
		create();
	}
	
	public void create() {
		Position.setup();
		MeshAndModelBuilder.initialize();
		//make piece and material repo
		pmRepo = new PieceAndMaterialRepo();
		world = new World();
		
		renderer = new GameRenderer(world);
		shapes = new ShapeRenderer();
		
		stage = new Stage();
		
		boxManager = new ManagingGroup();
		boxManager.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		bottomBar = new GameItemBar();
		bottomBar.setVisible(true);
		boxManager.addActorManaged(bottomBar, 0.0f, 0.0f, 1.0f, 0.12f);
		
		selectScreen = new GameItemSelection(this, pmRepo);
		boxManager.addActorManaged(selectScreen, 0.1f, 0.15f, 0.8f, 0.7f);
		
		//setBounds()
		stage.addActor(boxManager);
		
		plexer = new InputMultiplexer();
		plexer.addProcessor(stage);
		plexer.addProcessor(this);
		Gdx.input.setInputProcessor(plexer);
		
		activeScreen(true);
	}
	
	@Override
	public void render(float delta) {
		if (paused)
			return;
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if (activeScreen) {
			for (int i = 0; i < DIRECTION_ORDER.length; i++) {
				if (directionTrackers[i]) 
					renderer.updateCamera(DIRECTION_ORDER[i]);
			}
			renderer.rotateCamera(deltaX, deltaY);
		}
		this.deltaX = 0;
		this.deltaY = 0;
		renderer.render();
		
		shapes.begin(ShapeType.Filled);
		shapes.setColor(Color.BLACK);
		shapes.circle(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2, 5);
		shapes.end();
		
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		boxManager.resize(width, height);
	}
	
	@Override
	public void dispose() {
		//if anything needs disposing of
		MeshAndModelBuilder.disposeModels();
		stage.dispose();
	}
	@Override
	public void show() {
		Gdx.input.setInputProcessor(plexer);
		paused = false;
	}

	@Override
	public void hide() {
		paused = true;
	}
	
	@Override
	public void pause() {
		paused = true;
		activeScreen(false);
	}

	@Override
	public void resume() {
		paused = false;
		activeScreen(true);
	}

	@Override
	public boolean keyDown(int keycode) {
		for (int i = 0; i < DIRECTION_ORDER.length; i++) {
			if (DIRECTION_KEYS[i] == keycode) {
				directionTrackers[i] = true;
				return true;
			}
		}
		if (keycode == Input.Keys.ENTER) {
			if (editting) {
				world.exportBuildToPiece(pmRepo);
				editting = false;
				//bottomBar.setVisible(false);
			}
		}
		else if (keycode == Input.Keys.ESCAPE) {
			if (selectScreen.isVisible()) {
				selectScreen.setVisible(false);
				activeScreen(true);
			}
			else if (editting) {
				editting = false;
				//bottomBar.setVisible(false);
			}
			else {
				//open menu
				if (paused) {
					resume();
				}
				else {
					pause();
				}
			}
		}
		else if (keycode == Input.Keys.I) {
			//open materials + pieces menu
			selectScreen.setVisible(true);
			activeScreen(false);
		}
		else if (keycode == Input.Keys.UP) {
			edittingSize++;
			if (edittingSize >= Constants.CHUNK_MAX_SIZE)
				edittingSize = Constants.CHUNK_MAX_SIZE;
		}
		else if (keycode == Input.Keys.DOWN) {
			edittingSize--;
			if (edittingSize < 0)
				edittingSize = 0;
		}
		else if (keycode == Input.Keys.DEL) {
			//if block selected, delete it!
		}
		else if (keycode >= Input.Keys.NUM_0 && keycode <= Input.Keys.NUM_9) {
			int index = keycode - Input.Keys.NUM_0;
			if (editting)
				bottomBar.setSelector((index - 1) % 10);
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		for (int i = 0; i < DIRECTION_ORDER.length; i++) {
			if (DIRECTION_KEYS[i] == keycode) {
				directionTrackers[i] = false;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		if (!activeScreen) {
			return false;
		}
		if (button == Input.Buttons.LEFT) {
			Ray cameraRay = renderer.getCameraPickRay(screenX, screenY);
			if (editting) {
				//delete block from build if there is one
				world.removeBlock(cameraRay, edittingSize);
			}
			else {
				PieceInstance p = world.hit(cameraRay, edittingSize);
				//select piece + show info about it or shit
				//or at least just highlight it so we can delete
				if (p != null) {
					System.out.println(p.toString());
				}
				else {
					System.out.println("Nothing!");
				}
			}
		}
		else if (button == Input.Buttons.RIGHT) {
			if (!editting) {
				//what do - right clicked when not editting???
				if (bottomBar.isBoxAtSelected()) {
					editting = true;
					System.out.println("Turned on");
					//bottomBar.setVisible(true);
				}
				else {
					Ray cameraRay = renderer.getCameraPickRay(screenX, screenY);
					Vector3 place = new Vector3();
					Vector3 boxDimensions = new Vector3();
					bottomBar.getAtSelected().getDimension(boxDimensions, edittingSize);
					Vector3 addAt = world.findCollision(cameraRay, place, boxDimensions, edittingSize);
					if (addAt != null) {
						System.out.println("Adding a piece?");
						if (bottomBar.getAtSelected() instanceof Piece) {
							world.addPiece((Piece)bottomBar.getAtSelected(), addAt);
						}
					}
				}
			}
			else {
				//find block corresponding to plane
				Ray cameraRay = renderer.getCameraPickRay(screenX, screenY);
				Vector3 place = new Vector3();
				Vector3 boxDimensions = new Vector3();
				bottomBar.getAtSelected().getDimension(boxDimensions, edittingSize);
				Vector3 addAt = world.findCollision(cameraRay, place, boxDimensions, edittingSize);
				if (addAt != null) {
					System.out.println("Add support for adding pieces here");
					//if (bottomBar.getAtSelected() instanceof NamedMaterial)
					world.addBlockToBuild(addAt, edittingSize, bottomBar.getAtSelected());
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		if (!activeScreen)
			return false;
		float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
		float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;
		this.deltaX += deltaX;
		this.deltaY += deltaY;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		//if (editting)
		bottomBar.incrementSelector(amount);
		return false;
	}
	
	public void activeScreen(boolean yes) {
		if (yes) {
			Gdx.input.setCursorCatched(true);
			activeScreen = true;
		}
		else {
			Gdx.input.setCursorCatched(false);
			activeScreen = false;
		}
	}
	public void setBlock(Placeable mat) {
		bottomBar.setAtSelected(mat);
	}
}
