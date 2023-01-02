package sg.ebacorp.spaceimpactmvc.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import sg.ebacorp.spaceimpact.utils.ExecutionState;

public class World {

    ExecutionState executionState = ExecutionState.NONE;

    private PlayerPawn playerPawn;
    private Set<EnemyPawn> enemies;
    private Set<Laser> lasers;
    private Set<RandomPickup> randomPickups;

    public World() {
        createWorld();
    }

    private void createWorld() {
        this.playerPawn = new PlayerPawn();
        this.enemies = new HashSet<>();
        this.lasers = new HashSet<>();
        this.randomPickups = new HashSet<>();
    }

    public PlayerPawn getPlayer() {
        return playerPawn;
    }

    public void spawnEnemy(float x, float y) {
        EnemyPawn enemyPawn = new EnemyPawn(x, y);
        enemies.add(enemyPawn);
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
}
