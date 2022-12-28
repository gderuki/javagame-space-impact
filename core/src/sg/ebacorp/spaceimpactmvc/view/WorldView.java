package sg.ebacorp.spaceimpactmvc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpactmvc.model.Live;
import sg.ebacorp.spaceimpactmvc.model.RenderAble;
import sg.ebacorp.spaceimpactmvc.model.World;
import sg.ebacorp.spaceimpactmvc.model.XRay;

public class WorldView {

    public static float CAMERA_WIDTH = 10f;
    public static float CAMERA_HEIGHT = 7f;

    private World world;
    public OrthographicCamera cam;
    private SpriteBatch batch;
    private BitmapFont font;
    private float ppuX;
    private float ppuY;

    public WorldView(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        SetCamera(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f);
        batch = new SpriteBatch();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("pixelfont.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 60;
        parameter.borderWidth = 1;
        parameter.color = Color.BLACK;
        parameter.shadowOffsetX = 1;
        parameter.shadowOffsetY = 1;
        parameter.shadowColor = new Color(0, 0.5f, 0, 0.75f);
        BitmapFont font24 = generator.generateFont(parameter); // font size 24 pixels
        generator.dispose();
        font = font24;
    }

    public void SetCamera(float x, float y) {
        this.cam.position.set(x, y, 0);
        this.cam.update();
    }

    public void render() {
        batch.begin();
        if (world.getState() == ExecutionState.NONE) {
            printWelcome();
        } else {
            if (world.getPlayer().getLives() > 0) {
                //render all renderables
                for (RenderAble renderAble : world.getAllRenderAbles()) {
                    batch.draw(renderAble.getTexture(), renderAble.getPosition().x * ppuX, renderAble.getPosition().y * ppuY,
                            renderAble.getBounds().width * ppuX, renderAble.getBounds().height * ppuY);
                }
                renderUI();
            } else {
                printGameOver();
            }
        }
        batch.end();
    }

    private void printWelcome() {
        font.getData().setScale(0.94f);
        font.draw(batch, "SPACE IMPACT", 40, 128);
        font.getData().setScale(0.5f);
        font.draw(batch, "Press [SPACE] to START", 40, 64);
        font.getData().setScale(0.94f);
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
                batch.draw(Live.texture, ppuX * i, 6 * ppuY);
            }
        }
        font.draw(batch, String.format("%05d", world.getPlayer().getScore()), 6 * ppuX, 6.9f * ppuY);
        if (world.getPlayer().getXray() > 0) {
            batch.draw(XRay.xRayImage, 4 * ppuX, 6 * ppuY);
            font.draw(batch, String.valueOf(world.getPlayer().getXray()), 5 * ppuX, 6.9f * ppuY);
        }
    }

    public void setSize(int width, int height) {
        ppuX = (float) width / CAMERA_WIDTH;
        ppuY = (float) height / CAMERA_HEIGHT;
    }
}
