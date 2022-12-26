package sg.ebacorp.spaceimpactmvc.controller;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.TimeUtils;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpact.utils.RuntimeConfig;
import sg.ebacorp.spaceimpactmvc.model.Enemy;
import sg.ebacorp.spaceimpactmvc.model.Laser;
import sg.ebacorp.spaceimpactmvc.model.World;

public class WorldController {
    private float PLAYER_MOVE_SPEED = 140;
    private final int MOVE_SPEED = 130;
    private final long PROJECTILE_SPAWN_RATE_COMPARATOR = 236000000;
    World world;
    long lastEnemySpawnTime = 0;
    long lastProjectileSpawnTime = 0;

    public WorldController(World world) {
        this.world = world;
    }

    public void update(float delta) {
        processInputs();
        updateEnemyPosition();
        spawnEnemies();
        updateLaserPosition();
    }

    private void updateLaserPosition() {
        Iterator<Laser> laserIterator = world.getLasers().iterator();
        while (laserIterator.hasNext()) {
            Laser laser = laserIterator.next();
            laser.getPosition().x += (MOVE_SPEED * 1.75) * Gdx.graphics.getDeltaTime();
            Iterator<Enemy> enemyIterator = world.getEnemies().iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (enemy.getPosition().overlaps(laser.getPosition())) {
                    world.getPlayer().scoreUp();
                    laserIterator.remove();
                    enemyIterator.remove();
                }
            }
        }
    }

    private void updateEnemyPosition() {
        Iterator<Enemy> iterator = world.getEnemies().iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            enemy.moveLeft(MOVE_SPEED * Gdx.graphics.getDeltaTime());
            if (enemy.getPosition().x + 64 < 0) {
                iterator.remove();
            }
            if (enemy.getPosition().overlaps(world.getPlayer().getPosition())) {
                world.getPlayer().liveDown();
                if (!world.getPlayer().alive()) {
                    world.pause();
                }
            }
        }
    }

    private void spawnEnemies() {
        if (TimeUtils.millis() - lastEnemySpawnTime > MathUtils.random(1000, 10000)) {
            int topBarOffset = (RuntimeConfig.getInstance().screenHeight - 64 - 16);
            float y = MathUtils.random(128, topBarOffset - 64 - 16);
            float x = RuntimeConfig.getInstance().screenWidth + 64;
            lastEnemySpawnTime = TimeUtils.millis();
            world.spawnEnemy(x, y);
        }
    }

    private void processInputs() {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            world.getPlayer().updateUp(PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            world.getPlayer().updateDown(PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            world.getPlayer().updateRight(PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            world.getPlayer().updateLeft(PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && TimeUtils.nanoTime() - lastProjectileSpawnTime > PROJECTILE_SPAWN_RATE_COMPARATOR) {
            //shootSound.play();
            spawnProjectile();
        }
    }

    private void spawnProjectile() {
        world.spawnLaser(world.getPlayer().getX() + 91, world.getPlayer().getY() + 24);
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }
}
