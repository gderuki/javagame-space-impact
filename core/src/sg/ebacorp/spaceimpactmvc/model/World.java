package sg.ebacorp.spaceimpactmvc.model;

import java.util.HashSet;
import java.util.Set;

import sg.ebacorp.spaceimpact.utils.ExecutionState;

public class World {

    ExecutionState executionState = ExecutionState.RUNNING;

    private SpaceShip spaceShip;
    private Set<Enemy> enemies;
    private Set<Laser> lasers;

    public World() {
        createWorld();
    }

    private void createWorld() {
        this.spaceShip = new SpaceShip();
        this.enemies = new HashSet<>();
        this.lasers = new HashSet<>();
    }

    public SpaceShip getPlayer() {
        return spaceShip;
    }

    public void spawnEnemy(float x, float y) {
        Enemy enemy = new Enemy(x, y);
        enemies.add(enemy);
    }

    public Set<Enemy> getEnemies() {
        return enemies;
    }

    public Set<Laser> getLasers() {
        return lasers;
    }

    public void pause() {
        executionState = ExecutionState.PAUSED;
    }

    public void spawnLaser(float x, float y) {
        Laser laser = new Laser(x, y);
        lasers.add(laser);
    }
}
