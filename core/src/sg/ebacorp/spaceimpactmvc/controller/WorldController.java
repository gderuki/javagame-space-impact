package sg.ebacorp.spaceimpactmvc.controller;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.TimeUtils;
import sg.ebacorp.spaceimpact.utils.ExecutionState;
import sg.ebacorp.spaceimpactmvc.model.AABB;
import sg.ebacorp.spaceimpactmvc.model.Asteroid;
import sg.ebacorp.spaceimpactmvc.model.EnemyPawn;
import sg.ebacorp.spaceimpactmvc.model.Laser;
import sg.ebacorp.spaceimpactmvc.model.RandomPickup;
import sg.ebacorp.spaceimpactmvc.model.ShootSound;
import sg.ebacorp.spaceimpactmvc.model.World;

public class WorldController {
    private static final float DAMP = 0.97f;
    private static final float ACCELERATION = 20f;
    public static final float LASER_SPEED = 1.75f;
    public static final int RANDOM_SPEED = 1;
    public static final float ENEMY_DAMAGED_GRAVITY = -5f;
    public static final int CHASE_ACCELERATION = -20;
    private final long PROJECTILE_SPAWN_RATE_COMPARATOR = 236000000;
    World world;
    long lastEnemySpawnTime = TimeUtils.millis();
    long lastRandomItemSpawnTime = TimeUtils.millis();
    long lastProjectileSpawnTime = TimeUtils.millis();
    long lastAsteroidSpawnTime = TimeUtils.millis();
    private float ppuX = 1;
    private float ppuY = 1;
    private Asteroid.Overlap overlap;
    private int minIteration = 1;
    private int maxIteration = 128;
    private ArrayList<AsteroidPair> asteroidPairs = new ArrayList<>();

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
                //processInputs();
                //updateEnemyPosition(delta);
                processAsteroidInput(delta);
                for (int i = 0; i < 128; i++) {
                    asteroidPairs.clear();
                    updateAsteroidPosition(delta, 128);
                    broadPhase();
                    narrowPhase();
                }
                //updateAsteroidPosition(delta);
                //resolveCollisions();
                //updateRandomItemPosition();
                //spawnEnemies();
                //spawnRandomItems();
                //spawnAsteroids();
                //updateLaserPosition();
                //world.getPlayer().update(delta);
                //world.getPlayer().getVelocity().scl(DAMP);
            } else {
                if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                    world.reset();
                }
            }
        }
    }

    private void broadPhase() {
        ArrayList<Asteroid> asteroids = new ArrayList<>();
        asteroids.addAll(world.getAsteroids());
        for (int i = 0; i < asteroids.size() - 1; i++) {
            Asteroid bodyA = asteroids.get(i);
            AABB bodyA_aabb = bodyA.getAABB();

            for (int j = i + 1; j < asteroids.size(); j++) {
                Asteroid bodyB = asteroids.get(j);
                AABB bodyB_aabb = bodyB.getAABB();

//                if (bodyA.IsStatic && bodyB.IsStatic)
//                {
//                    continue;
//                }

                if (AABB.IntersectAABBs(bodyA_aabb, bodyB_aabb)) {
                    this.asteroidPairs.add(new AsteroidPair(bodyA, bodyB));
                }
                // i j
            }
        }

    }

    private void updateAsteroidPosition(float delta, int i) {
        for (Asteroid asteroid : world.getAsteroids()) {
            asteroid.update(delta, i);
        }
    }

    private void spawnAsteroids() {
        if (TimeUtils.millis() - lastAsteroidSpawnTime > MathUtils.random(1000, 2000)) {
            float y = MathUtils.random(1f, 5f);
            lastAsteroidSpawnTime = TimeUtils.millis();
            world.spawnAsteroid(world.getPlayer().getPosition().x + 15, y, new Vector2(MathUtils.random(-4, -1), 0), false);
        }
    }

    private void resolveCollisions() {
        ArrayList<EnemyPawn> enemies = new ArrayList<>(world.getEnemies());
        for (int i = 0; i < enemies.size(); i++) {
            EnemyPawn enemyPawn = enemies.get(i);
            for (int y = i + 1; y < enemies.size(); y++) {
                EnemyPawn enemyPawn2 = enemies.get(y);
                if (enemyPawn.getPositionAsRectangle().overlaps(enemyPawn2.getPositionAsRectangle())) {
                    // get vector and normalize
                    Vector2 collisionNormal = enemyPawn.getCentralPosition().sub(enemyPawn2.getCentralPosition()).nor();
                    // get relative velocity of 2 enemies
                    Vector2 relativeVelocity = enemyPawn.getVelocity().cpy().sub(enemyPawn2.getVelocity());
                    // project relat iveVelocity on collision normal
                    float dotProduct = collisionNormal.dot(relativeVelocity);
                    // check that relative velocity has close angle to collision normal
                    if (dotProduct < 0) {
                        float newDotProduct = -dotProduct;
                        // convert scalar to vector
                        Vector2 separationVelocity = collisionNormal.scl(newDotProduct);
                        enemyPawn.getVelocity().add(separationVelocity);
                        enemyPawn2.getVelocity().add(separationVelocity.scl(-1));
                    }
                }
            }

        }
    }

    public Asteroid.Overlap getOverlap() {
        return overlap;
    }

    private void narrowPhase() {
//        ArrayList<Asteroid> asteroids = new ArrayList<>();
//        asteroids.addAll(world.getAsteroids());
//        for (int i = 0; i < asteroids.size() - 1; i++) {
//            Asteroid asteroid1 = asteroids.get(i);
//            for (int y = i + 1; y < asteroids.size(); y++) {
//                Asteroid asteroid2 = asteroids.get(y);
//                Asteroid.Overlap intersect = asteroid1.intersect(asteroid2, ppuX, ppuY);
//                if (!intersect.isGap()) {
//                    world.overlap = intersect;
//                    asteroid1.penetrationResolution(intersect, asteroid2, ppuX, ppuY);
//                    asteroid1.collisionResolution(intersect, asteroid2, ppuX, ppuY);
//                }
//            }
//        }
        for (AsteroidPair asteroidPair : this.asteroidPairs) {
            Asteroid o2 = asteroidPair.getO2();
            Asteroid o1 = asteroidPair.getO1();
            Asteroid.Overlap intersect = o1.intersect(o2, 1, 1);
            if (!intersect.isGap()) {
                world.overlap = intersect;
                o1.penetrationResolution(intersect, o2, ppuX, ppuY);
                o1.collisionResolution(intersect, o2, ppuX, ppuY);
            }
        }

    }

    private void penetrationResolution(Asteroid asteroid1, Asteroid asteroid2, Asteroid.Overlap intersect) {

    }

    private void updateRandomItemPosition() {
        Iterator<RandomPickup> iterator = world.getRandomPickups().iterator();
        while (iterator.hasNext()) {
            RandomPickup randomPickup = iterator.next();
            randomPickup.moveLeft(Gdx.graphics.getDeltaTime());
            if (randomPickup.getPosition().x + RANDOM_SPEED < 0) {
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
            laser.getPosition().x += LASER_SPEED * Gdx.graphics.getDeltaTime();
            for (EnemyPawn enemyPawn : world.getEnemies()) {
                if (enemyPawn.getPositionAsRectangle().overlaps(laser.getPositionAsRectangle())) {
                    world.getPlayer().scoreUp();
                    laserIterator.remove();
                    enemyPawn.getAcceleration().y = ENEMY_DAMAGED_GRAVITY;
                }
            }
        }
    }

    private void updateEnemyPosition(float delta) {
        Iterator<EnemyPawn> iterator = world.getEnemies().iterator();
        while (iterator.hasNext()) {
            EnemyPawn enemyPawn = iterator.next();
            enemyPawn.update(delta);
            if (enemyPawn.getPosition().x + enemyPawn.getBounds().width < 0 || enemyPawn.getPosition().y + enemyPawn.getBounds().height < 0) {
                iterator.remove();
            }
            // enemyPawn is closer than 3 game units we should enable acceleration
//            if (enemyPawn.getPosition().dst(world.getPlayer().getPosition()) < 3) {
//                enemyPawn.getAcceleration().x = CHASE_ACCELERATION;
//            }
            if (enemyPawn.getPositionAsRectangle().overlaps(world.getPlayer().getPositionAsRectangle())) {
                world.getPlayer().liveDown();
                iterator.remove();
            }
        }

    }

    private void spawnRandomItems() {
        if (TimeUtils.millis() - lastRandomItemSpawnTime > MathUtils.random(10000, 60000)) {
            float y = MathUtils.random(1f, 5f);
            lastRandomItemSpawnTime = TimeUtils.millis();
            world.spawnRandomItem(world.getPlayer().getPosition().x + 15, y);
        }
    }

    private void spawnEnemies() {
        if (TimeUtils.millis() - lastEnemySpawnTime > MathUtils.random(1000, 2000)) {
            float y = MathUtils.random(1f, 5f);
            lastEnemySpawnTime = TimeUtils.millis();
            //take position of player and spawn enemies on the edge of screen
            world.spawnEnemy(1, 5);
        }
    }

    private void processAsteroidInput(float delta) {
        Asteroid asteroid = world.getPlayerAsteroid();
        asteroid.setGravity(Vector2.Zero);
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            asteroid.getAcceleration().y += 100f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            asteroid.getAcceleration().y -= 100f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            asteroid.getAcceleration().x += 100f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            asteroid.getAcceleration().x -= 100f * delta;
        } else if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            asteroid.setAngleVelocity(asteroid.getAngleVelocity() + 0.01f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            asteroid.setAngleVelocity(asteroid.getAngleVelocity() - 0.01f);
        } else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            asteroid.setAngleVelocity(0);
            asteroid.getVelocity().set(Vector2.Zero);
        } else if (Gdx.input.isKeyPressed(Input.Keys.C)) {
            world.clearAsteroids();
        } else {
            asteroid.getAcceleration().x = 0;
            asteroid.getAcceleration().y = 0;
        }
        //asteroid.getVelocity().scl(0.9f);
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
        world.spawnLaser(world.getPlayer().getPosition().x + world.getPlayer().getBounds().width,
                world.getPlayer().getPosition().y + (world.getPlayer().getBounds().height / 2));
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }

    public void setSize(int width, int height) {
//        ppuX = (float) width / WorldView.VIEWPORT_WIDTH_RATIO;
//        ppuY = (float) height / WorldView.VIEWPORT_HEIGHT_RATIO;

    }

    private static class AsteroidPair {
        private Asteroid o1;
        private Asteroid o2;

        public AsteroidPair(Asteroid o1, Asteroid o2) {
            this.o1 = o1;
            this.o2 = o2;
        }

        public Asteroid getO1() {
            return o1;
        }

        public Asteroid getO2() {
            return o2;
        }
    }
}
