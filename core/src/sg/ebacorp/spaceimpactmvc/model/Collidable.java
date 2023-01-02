package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.math.Vector2;

public interface Collidable {

    Vector2 getCentralPosition();

    Vector2 getVelocity();
}
