package sg.ebacorp.spaceimpactmvc.controller;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.TimeUtils;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpact.utils.RuntimeConfig;
import sg.ebacorp.spaceimpactmvc.model.Enemy;
import sg.ebacorp.spaceimpactmvc.model.Laser;
import sg.ebacorp.spaceimpactmvc.model.RandomPickup;
import sg.ebacorp.spaceimpactmvc.model.ShootSound;
import sg.ebacorp.spaceimpactmvc.model.World;

public class WorldController {
    private float PLAYER_MOVE_SPEED = 140;
    private float PLAYER_MOVE_SPEED_UNIT = 8;
    private static final float DAMP = 0.97f;
    private static final float ACCELERATION = 20f;

    private final int MOVE_SPEED = 130;
    private final long PROJECTILE_SPAWN_RATE_COMPARATOR = 236000000;
    World world;
    long lastEnemySpawnTime = TimeUtils.millis();
    long lastRandomItemSpawnTime = TimeUtils.millis();
    long lastProjectileSpawnTime = TimeUtils.millis();

    public WorldController(World world) {
        this.world = world;
    }

    public void update(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
        if (world.getState() == ExecutionState.NONE) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                world.reset();
                world.start();
            }
        } else {
            if (world.getPlayer().alive()) {
                processInputs();
                updateEnemyPosition();
                updateRandomItemPosition();
                spawnEnemies();
                spawnRandomItems();
                updateLaserPosition();
                world.getPlayer().update(delta);
                world.getPlayer().getVelocity().scl(DAMP);
            } else {
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    world.reset();
                }
            }
        }
    }

    private void updateRandomItemPosition() {
        Iterator<RandomPickup> iterator = world.getRandomPickups().iterator();
        while (iterator.hasNext()) {
            RandomPickup randomPickup = iterator.next();
            randomPickup.moveLeft(MOVE_SPEED * Gdx.graphics.getDeltaTime());
            if (randomPickup.getPosition().x + 64 < 0) {
                iterator.remove();
            }
            if (randomPickup.getPositionAsRectangle().overlaps(world.getPlayer().getPositionAsRectangle())) {
                float mathRandom = MathUtils.random();
                if (mathRandom < .4) {
                    if (world.getPlayer().getLives() <= 3) {
                        world.getPlayer().liveUp();
                    }
                    //FIXME figure out what to do with lastPickedUpItemType
                    //lastPickedUpItemType = 0;
                    world.getPlayer().scoreUp();
                    world.getPlayer().scoreUp();
                } else if (mathRandom > .4) {
                    if (world.getPlayer().getXray() < 3) {
                        world.getPlayer().xrayUp();
                    }
                    //FIXME figure out what to do with lastPickedUpItemType
                    //lastPickedUpItemType = 1;
                    world.getPlayer().scoreUp();
                    world.getPlayer().scoreUp();
                    world.getPlayer().scoreUp();
                } else {
                    //FIXME figure out what to do with sockwaves
//                    // shock wave will spawn only if
//                    shockWaveCount++;
//                    lastPickedUpItemType = 2;
//                    scoreCount += 120;
                }
                iterator.remove();
            }
        }
    }

    private void updateLaserPosition() {
        Iterator<Laser> laserIterator = world.getLasers().iterator();
        while (laserIterator.hasNext()) {
            Laser laser = laserIterator.next();
            laser.getPosition().x += (MOVE_SPEED * 1.75) * Gdx.graphics.getDeltaTime();
            Iterator<Enemy> enemyIterator = world.getEnemies().iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                if (enemy.getPositionAsRectangle().overlaps(laser.getPositionAsRectangle())) {
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
            if (enemy.getPositionAsRectangle().overlaps(world.getPlayer().getPositionAsRectangle())) {
                world.getPlayer().liveDown();
                iterator.remove();
            }
        }
    }

    private void spawnRandomItems() {
        if (TimeUtils.millis() - lastRandomItemSpawnTime > MathUtils.random(10000, 60000)) {
            int topBarOffset = (RuntimeConfig.getInstance().screenHeight - 64 - 16);
            float y = MathUtils.random(128, topBarOffset - 64 - 16);
            float x = RuntimeConfig.getInstance().screenWidth;
            lastRandomItemSpawnTime = TimeUtils.millis();
            world.spawnRandomItem(x, y);
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
            world.getPlayer().getAcceleration().y = ACCELERATION;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            world.getPlayer().getAcceleration().y = -ACCELERATION;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            world.getPlayer().getAcceleration().x = ACCELERATION;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            world.getPlayer().getAcceleration().x = -ACCELERATION;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && TimeUtils.nanoTime() - lastProjectileSpawnTime > PROJECTILE_SPAWN_RATE_COMPARATOR) {
            ShootSound.sound.play();
            spawnProjectile();
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.RIGHT) && !Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.UP) &&
                !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            world.getPlayer().clearAcceleration();
        }
    }

    private void spawnProjectile() {
        world.spawnLaser(world.getPlayer().getPosition().x + 91, world.getPlayer().getPosition().y + 24);
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }
}
