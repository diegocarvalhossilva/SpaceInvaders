package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Alien {
	public Vector2 position;
	public Vector2 position_inital;
	public Sprite sprite;
	public boolean Alive = true;
	
	public Alien (Vector2 _position, Texture img, Color color) {
		position = _position;
		position_inital = position;
		sprite = new Sprite(img);
		sprite.setColor(color);
		sprite.setScale(4);
	}
	
	public void Draw (SpriteBatch batch) {
		sprite.setPosition(position.x, position.y);
		sprite.draw(batch);
		
	}
}
