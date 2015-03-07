package render;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Vector3;

import components.Constants;
import components.NamedMaterial;
import components.PieceUnderConstruction;
import components.Position;

public class MeshAndModelBuilder {

	public static MeshBuilder meshBuilder;
	public static ModelBuilder modelBuilder;
	
	public static Model simpleBox, groundPlane;
	
	public static ArrayList<Model> allModels;
	
	public static void initialize() {
		meshBuilder = new MeshBuilder();
		modelBuilder = new ModelBuilder();
		allModels = new ArrayList<Model>();
		
		simpleBox = createBox(new Material(ColorAttribute.createDiffuse(Color.GREEN)));
		allModels.add(simpleBox);
		
		float groundWidth = Position.getWidth(Constants.CHUNK_GROUND_SIZE);
		groundPlane = modelBuilder.createRect(
									0.0f, 0.0f, 0.0f, 
									0.0f, 0.0f, groundWidth, 
									groundWidth, 0.0f, groundWidth, 
									groundWidth, 0.0f, 0.0f, 
									0.0f, 1.0f, 0.0f, 
									new Material(ColorAttribute.createDiffuse(Color.GREEN)), 
									Usage.Position | Usage.Normal);
		allModels.add(groundPlane);
	}
	//makes a model of a box of length 1 left aligned to 0,0 (not centered
	public static Model createBox(Material lol) {
		Model box = modelBuilder.createBox(1f, 1f, 1f,
				lol,
				Usage.Position | Usage.Normal);
		
		for (Node n: box.nodes) {
			n.translation.add(0.5f, 0.5f, 0.5f);
		}
		return box;
	}
	public static Model simpleBox() {
		if (modelBuilder == null) {
			initialize();
		}
		return simpleBox;
	}
	public static Model groundPlane() {
		if (modelBuilder == null) {
			initialize();
		}
		return groundPlane;
	}
	public static Model materialBox(Material lol) {
		Model materialModel = createBox(lol);
		allModels.add(materialModel);
		return materialModel;
	}
	private static boolean isValidPoint(Vector3 vp, Vector3 dim) {
		if (vp.x < 0 || vp.y < 0 || vp.z < 0)
			return false;
		if (vp.x >= dim.x || vp.y >= dim.y || vp.z >= dim.z)
			return false;
		return true;
	}
	public static Model makeModelFromMatrix(NamedMaterial[][][] materials, int scale) {
		
		if (meshBuilder == null || modelBuilder == null) {
			initialize();
		}
		
		int numberPlanes = 0;
		
		//final long atr = Usage.Position | Usage.Normal;
		Vector3 vpointedTo = new Vector3();
		Vector3 vcenter = new Vector3();
		Vector3[] vplanes = new Vector3[4];
		for (int i = 0; i < 4; i++) {
			vplanes[i] = new Vector3();
		}
		Vector3[] vdirs = new Vector3[3];
		vdirs[0] = new Vector3(1, 0, 0);
		vdirs[1] = new Vector3(0, 1, 0);
		vdirs[2] = new Vector3(0, 0, 1);
		Vector3[] vnegdirs = new Vector3[3];
		vnegdirs[0] = new Vector3(-1, 0, 0);
		vnegdirs[1] = new Vector3(0, -1, 0);
		vnegdirs[2] = new Vector3(0, 0, -1);
		Vector3 dim = new Vector3(materials.length, materials[0].length, materials[0][0].length);
		
		modelBuilder.begin();
		MeshPartBuilder meshPartBuilder;
		//Color[] colorList = {Color.WHITE, Color.BLUE, Color.GREEN, Color.MAGENTA, Color.PURPLE, Color.RED, Color.YELLOW};
		//meshBuilder.begin(atr);
		//meshBuilder.part("rectangle", GL20.GL_TRIANGLES);
		for (int x = 0; x < dim.x; x++) {
			for (int y = 0; y < dim.y; y++) {
				for (int z = 0; z < dim.z; z++) {
					if (materials[x][y][z] == null)
						continue;
					//look at each of 6 sides, if exposed to air on the top
					//then add a rectangle to our mesh at that point
					vcenter.set(x, y, z);
					for (int mult = -1; mult < 2; mult += 2) {
						for (int axis = 0; axis < 3; axis++) {
							if (mult == 1) {
								vpointedTo.set(vcenter).add(vdirs[axis]);
							}
							else {
								vpointedTo.set(vcenter).add(vnegdirs[axis]);
							}
							if ((!isValidPoint(vpointedTo, dim))
									|| materials[(int) vpointedTo.x][(int) vpointedTo.y][(int) vpointedTo.z] == null) {
								vplanes[0].set(vcenter);
								if (mult == 1) {
									vplanes[0].add(vdirs[axis]);
								}
								vplanes[1].set(vplanes[0]).add(vdirs[(axis + 1) % 3]);
								vplanes[2].set(vplanes[1]).add(vdirs[(axis + 2) % 3]);
								vplanes[3].set(vplanes[0]).add(vdirs[(axis + 2) % 3]);
								
								vplanes[0].scl(scale);
								vplanes[1].scl(scale);
								vplanes[2].scl(scale);
								vplanes[3].scl(scale);
								
								/*System.out.println("A");
								System.out.println(vplanes[0]);
								System.out.println(vplanes[1]);
								System.out.println(vplanes[2]);
								System.out.println(vplanes[3]);*/
								
								numberPlanes++;
								
								meshPartBuilder = modelBuilder.part("part" + Integer.toString(numberPlanes),
															GL20.GL_TRIANGLES,
															Usage.Position | Usage.Normal, 
															materials[x][y][z].getMat());
								
								//new Material(
								//ColorAttribute.createDiffuse(colorList[(int)(Math.random() * colorList.length)]),
								//ColorAttribute.createSpecular(1,1,1,1),
								//FloatAttribute.createShininess(8f))
								
								if (mult == 1) {
									meshPartBuilder.rect(vplanes[0], vplanes[1], vplanes[2], vplanes[3], vdirs[axis]);
									//System.out.println(vdirs[axis]);
								}
								else {
									meshPartBuilder.rect(vplanes[3], vplanes[2], vplanes[1], vplanes[0], vdirs[axis]);
									//System.out.println(vnegdirs[axis]);
								}
								//make a mesh
								//(-1, 0, 0)
								//then (0,0,0)(0,0,1)(0,1,1)(0,1,0)
								//(0, 1, 0)
								//then (0,1,0)(0,1,1)(1,1,1)(1,1,0)
							}
						}
					}
				}
			}
		}
		//Mesh meshPiece = meshBuilder.end();
		
		/*modelBuilder.part("piece", 
				meshPiece, 
				Usage.Position | Usage.Normal | Usage.TextureCoordinates, 
				new Material(
						ColorAttribute.createDiffuse(Color.GREEN),
						ColorAttribute.createSpecular(1,1,1,1),
						FloatAttribute.createShininess(8f))
				);*/
		Model finalPiece = modelBuilder.end();
		//allModels.add(finalPiece);
		System.out.printf("# planes used %d\n", numberPlanes);
		return finalPiece;
	}
	
	private static OrthographicCamera camera = new OrthographicCamera(5, 5);
	private static ModelBatch modelBatch = new ModelBatch();
	private static Environment environment = new Environment();
	
	private static FrameBuffer m_fbo = null;
	private static TextureRegion m_fboRegion = null;

	public static TextureRegion makeIcon(Model model, float xwidth, float ywidth, float zwidth) {
		return makeIcon(model, xwidth, ywidth, zwidth, true);
	}
	
	public static TextureRegion makeIcon(Model model, float xwidth, float ywidth, float zwidth, boolean preserve) {
		ArrayList<Model> models = new ArrayList<Model>();
		models.add(model);
		ArrayList<Vector3> pos = new ArrayList<Vector3>();
		pos.add(PieceUnderConstruction.ORIGIN);
		return makeIcon(models, pos, xwidth, ywidth, zwidth, preserve);
	}
	
	public static TextureRegion makeIcon(ArrayList<Model> model, ArrayList<Vector3> pos, float xwidth, float ywidth, float zwidth) {
		return makeIcon(model, pos, xwidth, ywidth, zwidth, true);
	}
	
	//makes a 2d icon given a Model (to show for display purposes)
	public static TextureRegion makeIcon(ArrayList<Model> model, ArrayList<Vector3> pos, float xwidth, float ywidth, float zwidth, boolean preserve) {
		//taken from http://stackoverflow.com/questions/7551669/libgdx-spritebatch-render-to-texture               
		int width = Constants.ICON_WIDTH;
		int height = Constants.ICON_WIDTH;
		if(m_fbo == null) {
			// m_fboScaler increase or decrease the antialiasing quality
			m_fbo = new FrameBuffer(Format.RGB565, width, height, false);
			environment.clear();
			environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
	        environment.add(new DirectionalLight().set(Color.WHITE, -1f, 0.8f, -0.2f));
		}
		m_fbo.begin();
		camera.viewportWidth = (float) (1.25f*Math.sqrt(Math.pow(xwidth, 2) + Math.pow(ywidth, 2) + Math.pow(zwidth, 2)));
		camera.viewportHeight = camera.viewportWidth;
		camera.position.set(xwidth*2, ywidth*2, zwidth*2);
		//camera.position.set(5, 5, 5);
		camera.near = 0.1f;
		camera.far = camera.viewportWidth * 3.0f;
		//camera.far = 30;
		camera.lookAt(xwidth / 2.0f, ywidth / 2.0f, zwidth / 2.0f);
		//camera.lookAt(0.5f, 0.5f, 0.5f);
		camera.up.set(0, 1, 0);
		camera.update();
		
		ArrayList<ModelInstance> models = new ArrayList<ModelInstance>();
		ModelInstance mi;
		for (int i = 0; i < model.size(); i++) {
			mi = new ModelInstance(model.get(i));
			mi.transform.setToTranslation(pos.get(i));
			models.add(mi);
		}
		
		//ModelInstance mi = new ModelInstance(modelBuilder.createBox(5f, 5f, 5f, 
        //        new Material(ColorAttribute.createDiffuse(Color.GREEN)),
        //        Usage.Position | Usage.Normal));
		
		// this is the main render function
		Gdx.gl.glViewport(0, 0, width, height);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
 
        modelBatch.begin(camera);
        for (int i = 0; i < models.size(); i++) {
        	modelBatch.render(models.get(i), environment);
        }
        modelBatch.end();
        
        /*TextureOrganizer.getShapeRenderer().begin(ShapeType.Filled);
        TextureOrganizer.getShapeRenderer().setColor(Color.RED);
        TextureOrganizer.getShapeRenderer().rect(10, 10, 30, 30);
        TextureOrganizer.getShapeRenderer().end();*/

		if(m_fbo != null)
		{
			m_fbo.end();
			m_fboRegion = new TextureRegion(m_fbo.getColorBufferTexture());
			m_fboRegion.flip(false, true);
			if (preserve) {
				m_fbo = null;
			}
			return m_fboRegion;
		}   
		return null;
	}

	public static void disposeModels() {
		//should be just simplebox
		for (Model m: allModels) {
			m.dispose();
		}
		allModels.clear();
		meshBuilder = null;
		modelBuilder = null;
	}
}
