package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Main extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	Texture img_bullet;
	Texture img_alien;
	Player player;
	Alien[] aliens;
	
	private String txtScore;
	private BitmapFont font;
	private Integer score = 0;
	private Music battle;
	private static GameState estadoAtual = GameState.MENU;
	
	int NumWidth_aliens = 11;
	int NumHeight_aliens = 5;
	int spacing_aliens = 40;
	int minX_aliens;
	int minY_aliens;
	int maxX_aliens;
	int maxY_aliens;
	int direction_aliens = 1;
	float speed_aliens = 100;
	
	Vector2 offset_aliens;
	
	@Override
	public void create () {
		
		offset_aliens = Vector2.Zero;
		batch = new SpriteBatch();
		img = new Texture("Player.png");
		img_bullet = new Texture("Bullet.png");
		img_alien = new Texture ("Alien.png");
		player = new Player (img, img_bullet, Color.GREEN);
		aliens = new Alien[NumWidth_aliens * NumHeight_aliens];
		
		battle = Gdx.audio.newMusic(Gdx.files.internal("BattleMusic.mp3"));
	
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonte-pixel.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 22;
		parameter.color = Color.WHITE;
		font = generator.generateFont(parameter);
		
		int i = 0;
		
		for (int y = 0; y < NumHeight_aliens; y++) {
			for (int x = 0; x < NumWidth_aliens; x++) {
				Vector2 position = new Vector2(x*spacing_aliens,y*spacing_aliens);
				position.x += Gdx.graphics.getWidth()/2;
				position.y += Gdx.graphics.getHeight();
				position.x -= (NumWidth_aliens/2) * spacing_aliens;
				position.y -= (NumHeight_aliens) * spacing_aliens;
				aliens[i] = new Alien(position, img_alien, Color.GREEN);
				i++;
			}
		}
		
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
            public boolean keyDown (int keyCode) {

                if(estadoAtual == GameState.MENU && keyCode == Input.Keys.SPACE){
                	estadoAtual = GameState.GAME;
                }
                else if(estadoAtual == GameState.END && keyCode == Input.Keys.SPACE){
                	estadoAtual = GameState.MENU;
                }

                return true;
            }
			
		});
			
	}

	int amount_alive_aliens = 0;

	@Override
	public void render () {
		
		switch (getEstadoAtual()) { /*Define o menu do jogo*/
		case MENU: /*Menu do jogo*/
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			font.draw(batch, "SPACE INVADERS", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 + 100);
			font.draw(batch, "APERTE A TECLA ESPAÇO PARA INICIAR!!", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2);
			batch.end();
			
			break;

		case GAME: /*Tela do jogo*/
			txtScore = "Pontos: " + score; /*Define o texto da pontuação*/
			
			/*Coloca música*/
			battle.setLooping(true);
			battle.play();
			
			float deltaTime = Gdx.graphics.getDeltaTime();
			ScreenUtils.clear(0, 0, 0, 1);
			batch.begin();
			player.Draw(batch);
			font.draw(batch, txtScore, 20, Gdx.graphics.getHeight() - 20); /*Desenha a pontuação na tela*/
			
			for (int i = 0; i < aliens.length; i++) {
				if (aliens[i].Alive) {
					if(player.sprite_bullet.getBoundingRectangle().overlaps(aliens[i].sprite.getBoundingRectangle())) {
						player.position_bullet.y = 1000000;
						aliens[i].Alive = false;
						score++;
						break;
					}
					
				}
				
			}
			minX_aliens = 10000;
			minY_aliens = 10000;
			maxX_aliens = 0;
			minY_aliens = 0;
			amount_alive_aliens = 0;
			
			for (int i = 0; i < aliens.length; i++) {
				
				if (aliens[i].Alive) {
					int IndexX = i%NumWidth_aliens;
					int IndexY = i%NumWidth_aliens;
					
					if(IndexX>maxX_aliens)maxX_aliens = IndexX;
					if(IndexX<minX_aliens)minX_aliens = IndexX;
					if(IndexY>maxY_aliens)maxY_aliens = IndexY;
					if(IndexY<minY_aliens)minY_aliens = IndexY;
					amount_alive_aliens++;
				}
			}
			
			if (amount_alive_aliens == 0) {
				for (int i = 0; i < aliens.length; i++) {
					aliens[i].Alive = true;
				}
				
				offset_aliens = new Vector2(0,0);
			}
			
			offset_aliens.x += direction_aliens* deltaTime* speed_aliens;
			if (aliens[maxX_aliens].position.x>=Gdx.graphics.getWidth()) {
				direction_aliens = -1;
				offset_aliens.y -= aliens[0].sprite.getScaleY()*0.5f;
				speed_aliens += 5;
			}
			
			if (aliens[minX_aliens].position.x<=0) {
				direction_aliens = 1;
				offset_aliens.y -= aliens[0].sprite.getScaleY()*0.5f;
				speed_aliens += 5;
			}
			
			if (aliens[minY_aliens].position.y<=0) {
				battle.stop();
				estadoAtual = GameState.END;
			}
			
			for (int i = 0; i < aliens.length; i++) {
				
				aliens[i].position = new Vector2(aliens[i].position_inital.x + offset_aliens.x, aliens[i].position_inital.y + offset_aliens.y);
				if(aliens[i].Alive) {
					aliens[i].Draw(batch);
					
					if (aliens[i].sprite.getBoundingRectangle().overlaps(player.sprite.getBoundingRectangle())) {
						battle.stop();
						estadoAtual = GameState.END;
					}
				}
			}
			
			batch.end();
			
			break;
			
		case END: /*Tela de derrota*/
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			batch.begin();
			font.draw(batch, "GAMEOVER", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2 + 100);
			font.draw(batch, "APERTE A TECLA ESPAÇO PARA VOLTAR AO MENU!!", Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 2);
			batch.end();
			
			break;
		}
		
		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
		battle.dispose();
	}

	public static GameState getEstadoAtual() {
		return estadoAtual;
	}


}