package sg.ebacorp.spaceimpactmvc.view;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import sg.ebacorp.spaceimpactmvc.model.Enemy;
import sg.ebacorp.spaceimpactmvc.model.Laser;
import sg.ebacorp.spaceimpactmvc.model.World;

public class WorldView {

    public static float CAMERA_WIDTH = 8f;
    public static float CAMERA_HEIGHT = 5f;

    private World world;
    public OrthographicCamera cam;
    SpriteBatch batch;

    public WorldView(World world) {
        this.world = world;
        this.cam = new OrthographicCamera(CAMERA_WIDTH, CAMERA_HEIGHT);
        SetCamera(CAMERA_WIDTH / 2f, CAMERA_HEIGHT / 2f);
        batch = new SpriteBatch();
    }

    public void SetCamera(float x, float y) {
        this.cam.position.set(x, y, 0);
        this.cam.update();
    }

    public void render() {
        renderPlayer();
        renderEnemies();
        renderLasers();
    }

    private void renderLasers() {
        batch.begin();
        for (Laser laser : world.getLasers()) {
            batch.draw(laser.getImage(), laser.getPosition().getX(), laser.getPosition().getY());
        }
        batch.end();
    }

    private void renderEnemies() {
        batch.begin();
        for (Enemy enemy : world.getEnemies()) {
            batch.draw(enemy.getImage(), enemy.getPosition().getX(), enemy.getPosition().getY());
        }
        batch.end();
    }

    private void renderPlayer() {
        batch.begin();
        batch.draw(world.getPlayer().getImage(), world.getPlayer().getX(), world.getPlayer().getY());
        batch.end();
    }
}
