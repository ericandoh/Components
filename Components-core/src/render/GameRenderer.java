package render;

import screens.Directions;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import components.Constants;
import components.Renderable;
import components.World;

public class GameRenderer {

	private static final int MAX_RENDERABLE_ITEMS = Constants.MAX_RENDERABLE_ITEMS;
	
	private PerspectiveCamera camera;
	//private CameraInputController control;
	private final Vector3 cameraRotation = new Vector3();
	private final Vector3 cameraUp = new Vector3(0, 1, 0);
	
	private Environment environment;	//replacement for lights
	
	private ModelBatch modelBatch;
	private Renderable[] renderables;
	
	private World world;
	
	public GameRenderer(World world) {
		renderables = new Renderable[MAX_RENDERABLE_ITEMS];
		this.world = world;
		create();
	}
	
	public void create() {
		int width = Gdx.graphics.getWidth();
		int height = Gdx.graphics.getHeight();
		
		modelBatch = new ModelBatch();
		
		camera = new PerspectiveCamera(67, width, height);
		camera.position.set(0.0f, 0.5f, 0.0f);
		camera.near = 0.01f;
		camera.far = 200f;
		camera.lookAt(1.0f, 0.5f, 0.0f);
		camera.update();
		
		this.world.setPlayerPos(camera.position);
		
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.7f, 0.6f, 1f));
        environment.add(new DirectionalLight().set(10.0f, 10.0f, 10.0f, -1f, -0.8f, -0.2f));   
	}
	public void render() {
		
		float renderLength = 24.0f;
		int renderSize = 5;
		
		int renderCount = world.render(renderables, renderLength, renderSize);
		//have camera move when keys pushed (via camera.update)
		//camera.position.set(world.getPlayerPos());
		/*camera.lookAt(camera.position.x + camera.position.y, 0,
				camera.position.z + camera.position.y);
		camera.update();*/
		
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        modelBatch.begin(camera);
        Renderable r;
        ModelInstance mi;
        for (int i = 0; i < renderCount; i++) {
        	r = renderables[i];
        	mi = r.getModel();
        	if (mi != null) {
        		modelBatch.render(mi, environment);
        	}
        }
        modelBatch.end();
	}
	
	public void updateCamera(Directions dir) {
		//float degTurn = 5f;
		float step = 0.5f;
		float minHeight = 0.03f;
		switch (dir) {
			case LEFT:
				//camera.rotateAround(camera.position, new Vector3(0, 1, 0), degTurn);
				camera.translate(camera.direction.z * step, 0,
						-camera.direction.x * step);
				break;
			case RIGHT:
				//camera.rotateAround(camera.position, new Vector3(0, 1, 0), -degTurn);
				camera.translate(-camera.direction.z * step, 0,
						camera.direction.x * step);
				break;
			case FORWARD:
				camera.translate(camera.direction.x * step, 0,
						camera.direction.z * step);
				break;
			case BACKWARD: 
				camera.translate(-camera.direction.x * step, 0,
						-camera.direction.z * step);
				break;
			case UP:
				camera.translate(0, step, 0);
				break;
			case DOWN:
				camera.translate(0, -step, 0);
				if (camera.position.y < minHeight)
					camera.position.y = minHeight;
				break;
			default:
				break;
		}
		camera.update();
		world.updatePos();
	}

	public void rotateCamera(float deltaX, float deltaY) {
		camera.direction.rotate(cameraUp, deltaX);
		cameraRotation.set(camera.direction).crs(camera.up).nor();
		camera.direction.rotate(cameraRotation, deltaY);
		camera.update();
	}
	public Ray getCameraPickRay(int screenX, int screenY) {
		return camera.getPickRay(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
		//return camera.getPickRay(screenX, screenY);
	}
}
