package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Laser {
    private Texture image;
    private Rectangle position;

    public Laser(float x, float y) {
        image = new Texture(Gdx.files.internal("laser_small.png"));
        position = new Rectangle(x, y, 32, 16);
    }

    public Texture getImage() {
        return image;
    }

    public Rectangle getPosition() {
        return position;
    }
}
