package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

public class XRay {
    public static Texture xRayImage;

    static {
        xRayImage = new Texture(Gdx.files.internal("icon_luch.png"));
    }
}
