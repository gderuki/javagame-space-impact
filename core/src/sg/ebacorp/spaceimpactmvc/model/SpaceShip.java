package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class SpaceShip {
    private Texture image;
    private Rectangle position;
    int lives;
    int score = 0;
    int xray = 0;

    public SpaceShip() {
        image = new Texture(Gdx.files.internal("ship.png"));
        position = new Rectangle(10, 10, 91, 64);
        lives = 2;
    }

    public Rectangle getPosition() {
        return position;
    }

    public Texture getImage() {
        return image;
    }

    public float getX() {
        return position.getX();
    }

    public float getY() {
        return position.getY();
    }

    public void updateUp(float v) {
        position.y = position.y + v;
    }

    public void updateDown(float v) {
        position.y = position.y - v;
    }

    public void updateRight(float v) {
        position.x = position.x + v;
    }

    public void updateLeft(float v) {
        position.x = position.x - v;
    }

    public void liveDown() {
        lives -= 1;
    }

    public boolean alive() {
        return lives > 0;
    }

    public void liveUp() {
        lives += 1;
    }

    public void scoreUp() {
        score += 20;
    }

    public int getLives() {
        return lives;
    }

    public int getScore() {
        return score;
    }

    public int getXray() {
        return xray;
    }

    public void xrayUp() {
        xray += 1;
    }
}