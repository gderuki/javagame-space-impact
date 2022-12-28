package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class RandomPickup implements RenderAble {

    private static Texture randomPickupImage;

    static {
        randomPickupImage = new Texture(Gdx.files.internal("heal.png"));
    }

    private Vector2 position;
    private Rectangle bounds;

    public RandomPickup(float x, float y) {
        position = new Vector2(x, y);
        bounds = new Rectangle();
        bounds.setWidth(1f);
        bounds.setHeight(1f);
    }

    public void moveLeft(float v) {
        position.x = position.x - v;
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
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
        return randomPickupImage;
    }
}
