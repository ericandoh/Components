package com.components;

import render.TextureOrganizer;
import screens.*;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.utils.IdentityMap;

public class Components extends Game {
	SpriteBatch batch;
	Texture img;
	
	PerspectiveCamera cam;
	Environment environment;
	
	protected IdentityMap<ScreenType, BasicScreen> screens;
	
	@Override
	public void create () {
		
		
		TextureOrganizer.init();
		
		screens = new IdentityMap<ScreenType, BasicScreen>();
		
		//screens.put(key, value)
		screens.put(ScreenType.START, new StartScreen(this));
		screens.put(ScreenType.GAME, new GameScreen(this));
		//screens.put(key, value)
		switchScreen(ScreenType.GAME, null);
		
		
		/*
		
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		*/
	}
	
	public void switchScreen(ScreenType n, String info) {
		System.out.println("Switching to " + n);
		if (n.equals(ScreenType.EXIT)) {			
			Gdx.app.exit();
			return;
		}
		else if (n.equals("REPLACEME") && info != null) {
			/*
			GameGrid p;
			if (info.substring(0, 4).equals(GameScreen.NEW)) {
				p = new GameGrid(info.substring(4), GameScreen.SAND);
				p.initialize();
			}
			else {
				String prefix = info.substring(0, 4);
				String name = info.substring(4);
				p = IO.readFile(name, prefix);
			}
			((GameScreen)(screens.get(GameScreen.NAME))).setPlayerAndMap(p, 0);*/
		}
		//add handling for if there is extra info needed 
		setScreen(screens.get(n));
	}
	public boolean needsGL20(){
		return true;
	}
	public void addScreen(ScreenType x, BasicScreen v) {
		screens.remove(x);
		screens.put(x, v);
	}
	public void removeScreen(ScreenType x) {
		screens.remove(x);
	}
	
	@Override
	public void dispose() {
		for (Screen screen : screens.values()) {
			screen.dispose();
		}
		TextureOrganizer.dispose();
	}
}
