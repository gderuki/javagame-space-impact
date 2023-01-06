package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import javafx.scene.layout.BackgroundRepeat;

public class Asteroid implements PolygonRenderAble {

    private Texture texture;

    private float[] vertices;
    private Vector2 position = new Vector2();
    private Vector2 acceleration = new Vector2();
    private Vector2 velocity;
    private float angle = 0;

    public Asteroid(float positionX, float positionY) {
        texture = new Texture(Gdx.files.internal(String.format("asteroid-%d.jpg", MathUtils.random(1, 3))));
        this.velocity = new Vector2(MathUtils.random(-3, -1), 0);
        position.x = positionX;
        position.y = positionY;
        createVertices();
    }

    private void createVertices() {
        int minPoints = 6;
        int maxPoints = 10;
        int points = MathUtils.random(minPoints, maxPoints);
        this.vertices = new float[points * 2];
        float deltaAngle = MathUtils.PI2 / (float) points;
        float angle = 0f;
        float minDist = 12f;
        float maxDist = 24f;
        for (int i = 0; i < points * 2; i = i + 2) {
            float dist = MathUtils.random(minDist, maxDist);
            float x = MathUtils.cos(angle) * dist * 3;
            float y = MathUtils.sin(angle) * dist * 3;
            this.vertices[i] = x;
            this.vertices[i + 1] = y;
            angle += deltaAngle;
        }
    }

    @Override
    public void draw(PolygonSpriteBatch pSB, float ppuX, float ppuY) {
        PolygonSprite polygonSprite = createPolygonSprite(ppuX, ppuY);
        polygonSprite.draw(pSB);
    }

    private PolygonSprite createPolygonSprite(float ppuX, float ppuY) {
        texture.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        TextureRegion region = new TextureRegion(texture);
        PolygonRegion polygonRegion = new PolygonRegion(region, vertices, new EarClippingTriangulator().computeTriangles(vertices).toArray());
        PolygonSprite polygonSprite = new PolygonSprite(polygonRegion);
        polygonSprite.setBounds(position.x * ppuX, position.y * ppuY, 30, 30);
        polygonSprite.rotate(angle);
        angle = angle + 1f;
        return polygonSprite;
    }

    public void update(float delta) {
        acceleration.scl(delta);
        velocity.add(acceleration.x, acceleration.y);
        velocity.scl(delta);
        position.add(velocity);
        velocity.scl(1 / delta);
        acceleration.scl(1 / delta);
    }
}
