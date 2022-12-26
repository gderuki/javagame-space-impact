package sg.ebacorp.spaceimpactmvc.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpact.utils.RuntimeConfig;
import sg.ebacorp.spaceimpactmvc.model.Enemy;
import sg.ebacorp.spaceimpactmvc.model.Laser;
import sg.ebacorp.spaceimpactmvc.model.Live;
import sg.ebacorp.spaceimpactmvc.model.RandomPickup;
import sg.ebacorp.spaceimpactmvc.model.World;
import sg.ebacorp.spaceimpactmvc.model.XRay;

public class WorldView {

    public static float CAMERA_WIDTH = 8f;
    public static float CAMERA_HEIGHT = 5f;

    private final int TOP_BAR_OFFSET = (RuntimeConfig.getInstance().screenHeight - 64 - 16);

    private World world;
    public OrthographicCamera cam;
    private SpriteBatch batch;
    private BitmapFont font;

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
            font.getData().setScale(0.94f);
            font.draw(batch, "SPACE IMPACT", 40, 128);
            font.getData().setScale(0.5f);
            font.draw(batch, "Press [SPACE] to START", 40, 64);
            font.getData().setScale(0.94f);
        } else {
            if (world.getPlayer().getLives() > 0) {
                renderPlayer();
                renderEnemies();
                renderLasers();
                renderRandomItems();
                renderUI();
            } else {
                font.getData().setScale(0.94f);
                font.draw(batch, "GAME OVER!", 40, 128);
                font.getData().setScale(0.35f);
                font.draw(batch, "Press [SPACE] to restart", 40, 64);
                font.draw(batch, "- or press [ESC] to quit", 40, 32);
                font.getData().setScale(0.94f);
            }
        }
        batch.end();
    }

    private void renderRandomItems() {
        for (RandomPickup randomPickup : world.getRandomPickups()) {
            batch.draw(randomPickup.getImage(), randomPickup.getPosition().getX(), randomPickup.getPosition().getY());
        }
    }

    private void renderUI() {
        if (world.getPlayer().getLives() > 0) {
            int lives = world.getPlayer().getLives();
            for (int i = 0; i < lives && i < 5; i++) {
                batch.draw(Live.texture, (72 * i), TOP_BAR_OFFSET);
            }

        }
        font.draw(batch, String.format("%05d", world.getPlayer().getScore()), RuntimeConfig.getInstance().screenWidth - 256 - 48,
                TOP_BAR_OFFSET + 62);
        if (world.getPlayer().getXray() > 0) {
            batch.draw(XRay.xRayImage, // 64x64
                    8 + 32 + 64 + 64 + 64 + 64, TOP_BAR_OFFSET);
            font.draw(batch, String.valueOf(world.getPlayer().getXray()), 24 + 32 + 64 + 64 + 64 + 64 + 64, TOP_BAR_OFFSET + 62);
        }
    }

    private void renderLasers() {
        for (Laser laser : world.getLasers()) {
            batch.draw(laser.getImage(), laser.getPosition().getX(), laser.getPosition().getY());
        }
    }

    private void renderEnemies() {
        for (Enemy enemy : world.getEnemies()) {
            batch.draw(enemy.getImage(), enemy.getPosition().getX(), enemy.getPosition().getY());
        }
    }

    private void renderPlayer() {
        batch.draw(world.getPlayer().getImage(), world.getPlayer().getPosition().getX(), world.getPlayer().getPosition().getY());
    }
}
