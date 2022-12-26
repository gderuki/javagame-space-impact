package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class Live {

    public static Texture texture;

    static {
        texture = new Texture(Gdx.files.internal("extra_life.png"));
    }
}
