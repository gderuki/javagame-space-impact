package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Laser implements RenderAble {

    private static Texture image;

    static {
        image = new Texture(Gdx.files.internal("laser_small.png"));
    }

    private Vector2 position;

    private Rectangle bounds;

    public Laser(float x, float y) {
        position = new Vector2(x, y);
        bounds = new Rectangle();
        bounds.setWidth(32);
        bounds.setHeight(16);
    }

    @Override
    public Texture getTexture() {
        return image;
    }

    @Override
    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getPositionAsRectangle() {
        return new Rectangle(position.x, position.y, bounds.width, bounds.height);
    }
}
