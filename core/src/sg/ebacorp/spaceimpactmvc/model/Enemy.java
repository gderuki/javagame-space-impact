package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Enemy implements RenderAble {

    private static Texture image;

    static {
        image = new Texture(Gdx.files.internal("enemy_ship.png"));
    }

    private Rectangle position;

    public Enemy(float x, float y) {
        position = new Rectangle(x, y, 96, 64);
    }

    @Override
    public Rectangle getPosition() {
        return position;
    }

    @Override
    public Texture getTexture() {
        return image;
    }

    public void moveLeft(float v) {
        position.x = position.x - v;
    }
}
