package components;

import render.MeshAndModelBuilder;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Vector3;

public class NamedMaterial implements Placeable {
	
	private static final Vector3 BLOCK_DIMENSION = new Vector3(1, 1, 1);
	
	private String name;
	private Material material;
	private Color ambient, diffuse, specular;
	private float shiny;
	private Model materialBlock;
	private int id;
	
	public NamedMaterial(int id, String name, Color ambient, Color diffuse, Color specular, float shiny) {
		this.id = id;
		this.name = name;
		this.ambient = ambient;
		this.diffuse = diffuse;
		this.specular = specular;
		this.shiny = shiny;
		this.material = new Material(	ColorAttribute.createAmbient(this.ambient), 
										ColorAttribute.createDiffuse(this.diffuse),
										ColorAttribute.createSpecular(this.specular), 
										FloatAttribute.createShininess(this.shiny));
		this.materialBlock = MeshAndModelBuilder.createBox(this.material);
	}
	public NamedMaterial(int id, String name, Color diffuse) {
		this.id = id;
		this.name = name;
		this.diffuse = diffuse;
		this.material = new Material(ColorAttribute.createDiffuse(this.diffuse));
		this.materialBlock = MeshAndModelBuilder.createBox(this.material);
	}
	public NamedMaterial() {
		this(0, "air", Color.WHITE);
	}
	
	public String getName() {
		return name;
	}
	public Material getMat() {
		return material;
	}
	public Color getDiffuse() {
		return diffuse;
	}
	public ModelInstance createModelInstance() {
		return new ModelInstance(materialBlock);
	}
	public TextureRegion getIcon() {
		return MeshAndModelBuilder.makeIcon(materialBlock, 1, 1, 1);
	}
	public Vector3 getDimension(Vector3 src, int sideSize) {
		return src.set(BLOCK_DIMENSION).scl(Position.getWidth(sideSize));
	}
	public boolean isBox() {
		return true;
	}
	public int getID() {
		return id;
	}
}
