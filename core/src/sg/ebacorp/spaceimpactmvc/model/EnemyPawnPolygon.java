package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.FloatArray;

public class EnemyPawnPolygon {

    private static Texture texture;

    static {
        texture = new Texture(Gdx.files.internal("snorky.jpg"));
    }

    Vector2 position;
    FloatArray vertices;

    public EnemyPawnPolygon(Vector2 position) {
        this.position = position;
        vertices = new FloatArray(
                new float[] {position.x, position.y + 50, position.x + 100, position.y, position.x + 50, position.y - 50, position.x - 50,
                        position.y - 50, position.x - 100, position.y});
    }

    public Vector2 getPosition() {
        return position;
    }

    public FloatArray getVertices() {
        return vertices;
    }

    public Texture getTexture() {
        return texture;
    }
}
