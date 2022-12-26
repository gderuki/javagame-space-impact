package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public class ShootSound {
    public static Sound sound;

    static {
        sound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
    }
}
