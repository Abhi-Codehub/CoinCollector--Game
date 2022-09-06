package com.coincollector;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.Random;

public class CoinCollector extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	int manState = 0;
	int pause = 0;
	float gravity = 0.2f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;
	BitmapFont font;
	Texture dizzy;
	int score = 0 ;
	int gameState = 0;
	Random random;
	// for coins
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinReactangle = new ArrayList<>();
	Texture coin;
	int coinCount;
	// for bombs
	ArrayList<Integer> bombsXs = new ArrayList<>();
	ArrayList<Integer> bombsYs = new ArrayList<>();
	ArrayList<Rectangle> bombsReactangle = new ArrayList<>();
	Texture bombs;
	int bombsCount;


	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[4];
		man[0] = new Texture("frame-1.png");
		man[1] = new Texture("frame-2.png");
		man[2] = new Texture("frame-3.png");
		man[3] = new Texture("frame-4.png");
		manY = Gdx.graphics.getHeight()/2;
		coin = new Texture("coin.png");
		bombs = new Texture("bomb.png");
		random = new Random();
		dizzy = new Texture("dizzy-1.png");
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);

	}
// generate coin all over the screen
		public void makeCoin(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		coinYs.add((int)height);
		coinXs.add(Gdx.graphics.getWidth());
		}
	public void makeBommb(){
		float height = random.nextFloat() * Gdx.graphics.getHeight();
		bombsYs.add((int)height);
		bombsXs.add(Gdx.graphics.getWidth());
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background,0,0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());

		if (gameState == 1){
			// GAME IS LIVE
			// Bombs
			if (bombsCount < 150){
				bombsCount++;
			} else {
				bombsCount = 0;
				makeBommb();
			}
			bombsReactangle.clear();
			for (int i=0; i<bombsXs.size(); i++)
			{
				batch.draw(bombs,bombsXs.get(i), bombsYs.get(i));
				bombsXs.set(i,bombsXs.get(i) - 18);
				bombsReactangle.add(new Rectangle(bombsXs.get(i), bombsYs.get(i),bombs.getWidth(),bombs.getHeight()));

			}
			//Coins
			if (coinCount < 100){
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}
			coinReactangle.clear();
			for (int i=0; i<coinXs.size(); i++)
			{
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 10);
				coinReactangle.add(new Rectangle(coinXs.get(i), coinYs.get(i),coin.getWidth(),coin.getHeight()));
			}
			if (Gdx.input.justTouched()){
				velocity = -10;
			}
			if( pause < 5){
				pause++;
			} else {
				pause = 0;
				if (manState < 3) {
					manState++;
				} else {
					manState = 0;
				}
			}
			velocity += gravity;
			manY -= velocity;
			//games is live
			if (manY <= 0) {
				manY = 0;
			}
		} else if (gameState == 0){
			if (Gdx.input.justTouched()){
				gameState = 1;
			}
			// waiting to start
		}else if (gameState == 2 ){
			if (Gdx.input.justTouched()) {
				gameState = 1;
				manY = Gdx.graphics.getHeight() / 2;
				score = 0;
				velocity = 0;
				coinXs.clear();
				coinYs.clear();
				coinReactangle.clear();
				coinCount = 0;
				bombsXs.clear();
				bombsYs.clear();
				bombsReactangle.clear();
				bombsCount = 0;
			}
		}

		if (gameState == 2) {
			batch.draw(dizzy, Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		} else {
			batch.draw(man[manState], Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY);
		}
		manRectangle = new Rectangle(Gdx.graphics.getWidth() / 2 - man[manState].getWidth() / 2, manY, man[manState].getWidth(), man[manState].getHeight());

		for(int i = 0; i < coinReactangle.size(); i++){
			if(Intersector.overlaps(manRectangle,coinReactangle.get(i))){
				score++;
				coinReactangle.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}
		for(int i = 0; i<bombsReactangle.size(); i++){
			if(Intersector.overlaps(manRectangle,bombsReactangle.get(i))){
				Gdx.app.log("Bomb!", "Collision!");
				gameState = 2;
			}
		}
		font.draw(batch, String.valueOf(score), 100,200);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
