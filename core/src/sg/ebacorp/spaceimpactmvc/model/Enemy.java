package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {

    private Texture image;
    private Rectangle position;

    public Enemy(float x, float y) {
        image = new Texture(Gdx.files.internal("enemy_ship.png"));
        position = new Rectangle(x, y, 96, 64);
    }

    public Rectangle getPosition() {
        return position;
    }

    public Texture getImage() {
        return image;
    }

    public void moveLeft(float v) {
        position.x = position.x - v;
    }
}
