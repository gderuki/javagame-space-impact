package sg.ebacorp.spaceimpactmvc.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import sg.ebacorp.spaceimpact.utils.ExecutionState;

public class World {

    public Asteroid.Overlap overlap;
    ExecutionState executionState = ExecutionState.NONE;

    private PlayerPawn playerPawn;
    private EnemyPawnPolygon enemyPawnPoligon;
    private Set<EnemyPawn> enemies;
    private Set<Asteroid> asteroids;
    private Set<Laser> lasers;
    private Set<RandomPickup> randomPickups;
    private Asteroid player = new Asteroid(300, 300, false, false, 1, Vector2.Zero);
    private Asteroid rectangle = new Asteroid(550, 100, 800, 50, false, true, 0, new Vector2());

    public World() {
        createWorld();
    }

    private void createWorld() {
        this.enemyPawnPoligon = new EnemyPawnPolygon(new Vector2(500f, 500f));
        this.playerPawn = new PlayerPawn();
        this.enemies = new HashSet<>();
        this.lasers = new HashSet<>();
        this.randomPickups = new HashSet<>();
        this.asteroids = new HashSet<>();

    }

    public Asteroid.Overlap getOverlap() {
        return overlap;
    }

    public Asteroid getPlayerAsteroid() {
        return player;
    }

    public PlayerPawn getPlayer() {
        return playerPawn;
    }

    public void spawnEnemy(float x, float y) {
        EnemyPawn enemyPawn = new EnemyPawn(x, y);
        enemies.add(enemyPawn);
    }

    public void spawnAsteroid(float x, float y, Vector2 speed, boolean rectangle) {
        Asteroid asteroid = new Asteroid(x, y, false, rectangle, 1, speed);
        asteroids.add(asteroid);
    }

    public Set<EnemyPawn> getEnemies() {
        return enemies;
    }

    public Set<Laser> getLasers() {
        return lasers;
    }

    public Set<RandomPickup> getRandomPickups() {
        return randomPickups;
    }

    public void spawnLaser(float x, float y) {
        Laser laser = new Laser(x, y);
        lasers.add(laser);
    }

    public void spawnRandomItem(float x, float y) {
        RandomPickup randomPickup = new RandomPickup(x, y);
        randomPickups.add(randomPickup);
    }

    public void reset() {
        enemies.clear();
        lasers.clear();
        randomPickups.clear();
        playerPawn.init();
    }

    public void start() {
        executionState = ExecutionState.RUNNING;
    }

    public ExecutionState getState() {
        return executionState;
    }

    public ArrayList<RenderAble> getAllRenderAbles() {
        ArrayList<RenderAble> result = new ArrayList<>();
        result.add(playerPawn);
        result.addAll(enemies);
        result.addAll(lasers);
        result.addAll(randomPickups);
        return result;
    }

    public void clearAsteroids() {
        asteroids.clear();
    }

    public Set<Asteroid> getAsteroids() {
        HashSet<Asteroid> asteroids1 = new HashSet<>(asteroids);
        asteroids1.add(player);
        asteroids1.add(rectangle);
        return asteroids1;
    }


}
