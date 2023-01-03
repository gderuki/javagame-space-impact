package sg.ebacorp.spaceimpactmvc;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.PolygonRegion;
import com.badlogic.gdx.graphics.g2d.PolygonSprite;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class NewScreen implements Screen {

    private OrthographicCamera cam;
    private PolygonSpriteBatch pSB;
    private Hexagon hexagon1, hexagon2;
    public static final int HEXAGON_WIDTH = 100;
    public static final int HEXAGON_HEIGHT = (int) (HEXAGON_WIDTH / (Math.sqrt(3) / 2));
//Ratio of width and height of a regular hexagon.

    public NewScreen() {
        this.cam = new OrthographicCamera(800, 800);
        cam.setToOrtho(true);
        cam.position.set(cam.viewportWidth / 2f, cam.viewportHeight / 2f, 0);
        cam.update();

        hexagon1 = new Hexagon(new Vector2(0, 0));
        hexagon2 = new Hexagon(new Vector2(HEXAGON_WIDTH + 2, 0));

        pSB = new PolygonSpriteBatch();
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glClearColor(1, 1, 1, 1);

        cam.update();
        pSB.setProjectionMatrix(cam.combined);

        pSB.begin();
//        hexagon1.draw(pSB);
//        hexagon2.draw(pSB);
        pSB.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    public static class Hexagon {

        private final float rotateAccelearation;
        private float scale;
        private Vector2 pos;
        private float angle;

        public Hexagon(Vector2 pos) {
            this.pos = pos;
            this.angle = 0f;
            this.scale = 0f;
            int random = MathUtils.random(0, 1);
            if (random == 0) {
                this.rotateAccelearation = MathUtils.random(1f, 10f);
            } else {
                this.rotateAccelearation = MathUtils.random(-10f, -1f);
            }
        }

        public PolygonSprite createPolygonSprite() {

            angle = angle + rotateAccelearation;
            scale = scale + 0.01f;
            System.out.println(angle);

            int hexWidth = NewScreen.HEXAGON_WIDTH;
            int hexHeight = NewScreen.HEXAGON_HEIGHT;

            //float sin = MathUtils.sin(angle);
            //float cos = MathUtils.cos(angle);

            float sin = MathUtils.sinDeg(angle);
            float cos = MathUtils.cosDeg(angle);

            Texture texture = new Texture(Gdx.files.internal("snorky.jpg"));
            float texHeight = texture.getHeight();
            //float texDrawWidth = texHeight * ((float) Math.sqrt(3) / 2);
            float texDrawWidth = texture.getWidth();
//
//            float left = -texDrawWidth / 2f;
//            float right = left + texDrawWidth;
//            float bottom = -texHeight / 2f;
//            float top = bottom + texHeight;

            float left = 0;
            float right = left + texDrawWidth;
            float bottom = 0;
            float top = texHeight;

            float[] vertices = {left, bottom, left, top, right, top, right, bottom};

//
//
//            for (int i = 0; i < vertices.length; i = i + 2) {
//                float vertexX = vertices[i];
//                float vertexY = vertices[i + 1];
//                vertices[i] = cos * vertexX - sin * vertexY;
//                vertices[i + 1] = sin * vertexX + cos * vertexY;
//             }
            TextureRegion region = new TextureRegion(texture);
//            region.flip(false, true);
            PolygonRegion polygonRegion = new PolygonRegion(region, vertices, new EarClippingTriangulator().computeTriangles(vertices).toArray());
            PolygonSprite polygonSprite = new PolygonSprite(polygonRegion);
            //polygonSprite.setPosition(pos.x, pos.y);
            polygonSprite.setBounds(pos.x, pos.y, hexWidth, hexHeight);
            //polygonSprite.setBounds(pos.x, pos.y);
            polygonSprite.rotate(angle);
            //polygonSprite.scale(scale);

            return polygonSprite;
        }

        public void draw(PolygonSpriteBatch pSB, float ppuX, float ppuY) {
            PolygonSprite polygonSprite = createPolygonSprite();
            polygonSprite.draw(pSB);
        }

        public void update(Vector2 pos) {
            this.pos.x = pos.x * 50;
            //this.pos.y = pos.y * 50;
        }

        public void setAngle(float delta) {
            //angle = MathUtils.PI /2f * Gdx.graphics.getDeltaTime();
        }
    }
}