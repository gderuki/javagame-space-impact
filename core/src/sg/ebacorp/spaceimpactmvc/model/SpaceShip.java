package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SpaceShip implements RenderAble {
    private static Texture image;

    static {
        image = new Texture(Gdx.files.internal("ship.png"));
    }

    private Vector2 position;

    private Vector2 acceleration = new Vector2();

    private Vector2 velocity = new Vector2();

    private Rectangle bounds;

    int lives;

    int score = 0;

    int xray = 0;

    public SpaceShip() {
        init();
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    @Override
    public Texture getTexture() {
        return image;
    }

    public void updateUp(float v) {
        position.y = position.y + v;
    }

    public void updateDown(float v) {
        position.y = position.y - v;
    }

    public Vector2 getVelocity() {
        return velocity;
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

    public void init() {
        lives = 2;
        score = 0;
        xray = 0;
        position = new Vector2(10, 10);
        bounds = new Rectangle();
        bounds.setWidth(91);
        bounds.setHeight(64);
    }

    public void clearAcceleration() {
        acceleration.y = 0;
    }

    public Rectangle getPositionAsRectangle() {
        return new Rectangle(position.x, position.y, bounds.width, bounds.height);
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void update(float delta) {
        acceleration.scl(delta);
        velocity.scl(delta);
        velocity.add(acceleration.x, acceleration.y);
        position.add(velocity);
        velocity.scl(1 / delta);
    }
}
