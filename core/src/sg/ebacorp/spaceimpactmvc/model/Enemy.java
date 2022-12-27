package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Enemy implements RenderAble {

    private static Texture image;

    static {
        image = new Texture(Gdx.files.internal("enemy_ship.png"));
    }

    private Vector2 position;
    private Rectangle bounds;

    public Enemy(float x, float y) {
        position = new Vector2(x, y);
        bounds = new Rectangle();
        bounds.setWidth(96);
        bounds.setHeight(64);
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getPositionAsRectangle() {
        return new Rectangle(position.x, position.y, bounds.width, bounds.height);
    }

    @Override
    public Texture getTexture() {
        return image;
    }

    public void moveLeft(float v) {
        position.x = position.x - v;
    }
}
