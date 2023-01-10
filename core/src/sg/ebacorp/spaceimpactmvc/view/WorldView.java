package sg.ebacorp.spaceimpactmvc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpactmvc.model.Asteroid;
import sg.ebacorp.spaceimpactmvc.model.EnemyPawn;
import sg.ebacorp.spaceimpactmvc.model.Live;
import sg.ebacorp.spaceimpactmvc.model.RenderAble;
import sg.ebacorp.spaceimpactmvc.model.World;

public class WorldView {

    public static float VIEWPORT_WIDTH_RATIO = 16f;
    public static float VIEWPORT_HEIGHT_RATIO = 9f;

    public static float VIEWPORT_WIDTH_ABS = 1280;
    public static float VIEWPORT_HEIGHT_ABS = 720;

    private final World world;
    public static OrthographicCamera playerCamera;
    public static SpriteBatch batch;
    private final BitmapFont font;
    public static float ppuX = 1;
    public static float ppuY = 1;
    private ShapeRenderer shapeRenderer = new ShapeRenderer();
    private PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();

    Texture backgroundImage;

    public WorldView(World world) {
        this.world = world;
        playerCamera = new OrthographicCamera();
        playerCamera.setToOrtho(false, VIEWPORT_WIDTH_ABS, VIEWPORT_HEIGHT_ABS);
//        playerCamera.position.x = VIEWPORT_WIDTH_ABS / 2;
//        playerCamera.position.y = VIEWPORT_HEIGHT_ABS / 2;
//        playerCamera.zoom = 0.95f;
//        playerCamera.position.x = 0;
//        playerCamera.position.y = 0;
        playerCamera.update();

        backgroundImage = new Texture(Gdx.files.internal("bg_debug.png"));

        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("pixelfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 10;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
        BitmapFont font24 = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();
        font = font24;
    }

    public void render() {
        batch.begin();
        if (world.getState() == ExecutionState.NONE) {
            printWelcome();
        } else {
            if (world.getPlayer().getLives() > 0) {
                drawDebugMarkers();

                // render all renderables
//                for (RenderAble renderAble : world.getAllRenderAbles()) {
//                    batch.draw(renderAble.getTexture(), renderAble.getPosition().x * ppuX, renderAble.getPosition().y * ppuY,
//                            renderAble.getBounds().width * ppuX, renderAble.getBounds().height * ppuY);
//                }

                renderUI();
            } else {
                printGameOver();
            }
        }
        batch.end();
        playerCamera.update();

        //shapeRenderer.setProjectionMatrix(playerCamera.combined);
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        shapeRenderer.circle(10, 10, 50);
        for (Asteroid asteroid : world.getAsteroids()) {
            if (asteroid.hasCollision()) {
                shapeRenderer.line(new Vector2(10, 10), asteroid.getOverlapAxis().nor().scl(50));
            }
        }
        if (world.overlap != null) {
            shapeRenderer.circle(world.overlap.getCollisionVertex().x, world.overlap.getCollisionVertex().y, 2);
        }
        shapeRenderer.set(ShapeRenderer.ShapeType.Line);
        shapeRenderer.end();

        polygonSpriteBatch.begin();
        //polygonSpriteBatch.setProjectionMatrix(playerCamera.combined);
        for (Asteroid asteroid : world.getAsteroids()) {
            asteroid.draw(polygonSpriteBatch, ppuX, ppuY);
            if (asteroid.hasCollision()) {
                font.draw(polygonSpriteBatch, "ALARMA COLLISION, DEPTH=" + String.valueOf(asteroid.getDepth()), 1, 1);
            } else {
                font.draw(polygonSpriteBatch, "                     ", 1, 1);
            }

        }
        polygonSpriteBatch.end();
    }

    private void drawDebugMarkers() {
        // corners
        batch.draw(backgroundImage, 0, 0);
        batch.draw(backgroundImage, 1280 - 128, 720 - 128);
        batch.draw(backgroundImage, 1280 - 128, 0);
        batch.draw(backgroundImage, 0, 720 - 128);

        // centraal ALAAAAAA
        batch.draw(backgroundImage, 640 - 64, 360 - 64);
    }

    private void printWelcome() {
//        font.getData().setScale(0.94f);
//        font.draw(batch, "SPACE IMPACT", 40, 128);
//        font.getData().setScale(0.5f);
        font.draw(batch, "Press [SPACE] to START", 40, 64);
//        font.getData().setScale(0.94f);
    }

    private void printGameOver() {
        font.getData().setScale(0.94f);
        font.draw(batch, "GAME OVER!", 40, 128);
        font.getData().setScale(0.35f);
        font.draw(batch, "Press [SPACE] to restart", 40, 64);
        font.draw(batch, "- or press [ESC] to quit", 40, 32);
        font.getData().setScale(0.94f);
    }

    private void renderUI() {
        if (world.getPlayer().getLives() > 0) {
            int lives = world.getPlayer().getLives();
            for (int i = 0; i < lives && i < 5; i++) {
                batch.draw(Live.texture, ppuX * i + 16, 8 * ppuY);
            }
        }
        font.draw(batch, String.format("%05d", world.getPlayer().getScore()), 12 * ppuX, 9 * ppuY - 16);
//        if (world.getPlayer().getXray() > 0) {
//            batch.draw(XRay.xRayImage, 4 * ppuX, 6 * ppuY);
//            font.draw(batch, String.valueOf(world.getPlayer().getXray()), 5 * ppuX, 6.9f * ppuY);
//        }
    }

    public void setSize(int width, int height) {
//        ppuX = (float) width / VIEWPORT_WIDTH_RATIO;
//        ppuY = (float) height / VIEWPORT_HEIGHT_RATIO;
    }
}
