package screens;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;

public class ManagingGroup extends Group {

	private HashMap<Actor, float[]> actorRelativeBounds;
	
	public ManagingGroup() {
		super();
		actorRelativeBounds = new HashMap<Actor, float[]>();
	}
	
	public void addActorManaged(Actor actor, float x, float y, float width, float height) {
		float[] arr = new float[4];
		arr[0] = x;
		arr[1] = y;
		arr[2] = width;
		arr[3] = height;
		actorRelativeBounds.put(actor, arr);
		float bWidth = getWidth();
		float bHeight = getHeight();
		actor.setBounds(bWidth*arr[0], bHeight*arr[1], bWidth*arr[2], bHeight*arr[3]);
		if (actor instanceof ManagingGroup) {
			((ManagingGroup)actor).resize(actor.getWidth(), actor.getHeight());
		}
		this.addActor(actor);
	}
	public void resize(float width, float height) {
		for (Map.Entry<Actor, float[]> entry : actorRelativeBounds.entrySet()) {
			Actor key = entry.getKey();
			float[] value = entry.getValue();
			key.setBounds(width * value[0], height * value[1], width * value[2], height * value[3]);
			if (key instanceof ManagingGroup) {
				((ManagingGroup)key).resize(key.getWidth(), key.getHeight());
			}
		}
	}
	public void changeBoundsFor(Actor actor, float x, float y) {
		if (!actorRelativeBounds.containsKey(actor)) {
			return;
		}
		float[] arr = actorRelativeBounds.get(actor);
		arr[0] = x;
		arr[1] = y;
		float bWidth = getWidth();
		float bHeight = getHeight();
		actor.setBounds(bWidth*arr[0], bHeight*arr[1], bWidth*arr[2], bHeight*arr[3]);
		if (actor instanceof ManagingGroup) {
			((ManagingGroup)actor).resize(actor.getWidth(), actor.getHeight());
		}
	}
	public void changeBoundsFor(Actor actor, float x, float y, float width, float height) {
		if (!actorRelativeBounds.containsKey(actor)) {
			return;
		}
		float[] arr = actorRelativeBounds.get(actor);
		arr[0] = x;
		arr[1] = y;
		arr[2] = width;
		arr[3] = height;
		float bWidth = getWidth();
		float bHeight = getHeight();
		actor.setBounds(bWidth*arr[0], bHeight*arr[1], bWidth*arr[2], bHeight*arr[3]);
		if (actor instanceof ManagingGroup) {
			((ManagingGroup)actor).resize(actor.getWidth(), actor.getHeight());
		}
	}
	@Override
	public boolean removeActor(Actor actor) {
		if (actorRelativeBounds.containsKey(actor)) {
			actorRelativeBounds.remove(actor);
		}
		return super.removeActor(actor);
	}
	@Override
	public void clearChildren() {
		actorRelativeBounds = new HashMap<Actor, float[]>();
		super.clearChildren();
	}
}
