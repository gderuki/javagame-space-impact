package sg.ebacorp.spaceimpact;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import sg.ebacorp.spaceimpact.utils.ExecutionState;


public class FirstLevelScreen implements Screen {
    final DefaultGame game;
    private final int SCREEN_HEIGHT = 480;// + 64 + 16;
    private final int SCREEN_WIDTH = 800;

    Texture enemyImage;
    Texture projectileImage;
    Texture playerImage;
    Texture randomPickupImage;
    Texture extraLiveImageUI;
    Texture obstacleImage;
    Texture xRayImage;
    Texture shockWaveImage;
    Sound shootSound;
    //    Music rainMusic;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> randomPickups;
    Array<Rectangle> enemies;
    Array<Rectangle> projectiles;
    Array<Rectangle> obstacles;
    long lastEnemySpawnTime;
    //    long lastExtraLifeSpawnTime;
    long lastProjectileSpawnTime;
    long lastObstacleSpawnTime;
    long lastRespawnTime;
    long lastRandomPickupSpawnTime;
    int xRaysCount = 0;
    int shockWaveCount = 0;
    int scoreCount;
    int livesCount = 1;

    /*
     0 - extra health
     1 - xRay
     2 - shockWave
     */
    int lastPickedUpItemType = 0;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MOVE_SPEED = 130;
    @SuppressWarnings("FieldCanBeLocal")
    private final long ENEMY_SPAWN_RATE_COMPARATOR = 1500000000L + 250000000L;
    @SuppressWarnings("FieldCanBeLocal")
    private final long RANDOM_PICKUP_SPAWN_RATE_COMPARATOR = 1500000000L + 250000000L;
    @SuppressWarnings("FieldCanBeLocal")
    private final long OBSTACLE_SPAWN_RATE_COMPARATOR = 1000000000L + 2500000000L;
    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long PROJECTILE_SPAWN_RATE_COMPARATOR = 236000000; // og: 1000000000
    // The more value -> the fewer enemies are being spawned
    private ExecutionState executionState;
    @SuppressWarnings("FieldCanBeLocal")
    private final int TOP_BAR_OFFSET = (SCREEN_HEIGHT - 64 - 16);
    private int randomLUTCounter = 0;
    // make it DOOM
    private final int[] randomLUT = {
            6, 10, 5, 3, 4, 6, 10, 5, 4,
            3, 4, 7, 9, 8, 2, 7, 9, 8,
            2, 6, 10, 5, 3, 4, 7, 9, 8,
            2, 7, 9, 8, 2, 6, 10, 5, 3
    };
    private boolean isBossFight = false;

    public FirstLevelScreen(final DefaultGame gam) {
        this.game = gam;
        initAssets();

        // TODO: get back on this one some time later
        resetLevelState(false);
    }

    private void initAssets() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        enemyImage = new Texture(Gdx.files.internal("enemy_ship.png"));
        projectileImage = new Texture(Gdx.files.internal("laser_small.png"));
        playerImage = new Texture(Gdx.files.internal("ship.png"));
        extraLiveImageUI = new Texture(Gdx.files.internal("extra_life.png"));
        randomPickupImage = new Texture(Gdx.files.internal("heal.png"));
        obstacleImage = new Texture(Gdx.files.internal("obstacle.png"));
        xRayImage = new Texture(Gdx.files.internal("icon_luch.png"));
        shockWaveImage = new Texture(Gdx.files.internal("icon_luch.png"));
        // load the drop sound effect and the rain background "music"
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
    }

    private void spawnPowerUp() {
        Rectangle item = new Rectangle();

        if (obstacles.size > 0) {
            item.y = MathUtils.random(128, TOP_BAR_OFFSET - 64 - 16);
        } else {
            item.y = MathUtils.random(16, TOP_BAR_OFFSET - 64 - 16);
        }

        item.x = SCREEN_WIDTH;
        item.width = 64;
        item.height = 64;
        // we add -> we clean on pickup, we repopulate
        randomPickups.add(item);
        lastRandomPickupSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnObstacle() {
        Rectangle obstacle = new Rectangle();
        obstacle.y = 0;//MathUtils.random(0, TOP_BAR_OFFSET - 64 - 16);
        obstacle.x = SCREEN_WIDTH;
        obstacle.width = 64;
        obstacle.height = 64;
        obstacles.add(obstacle);
        lastObstacleSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnEnemy() {
        Rectangle enemy = new Rectangle();

        if (obstacles.size > 0) {
            enemy.y = MathUtils.random(128, TOP_BAR_OFFSET - 64 - 16);
        } else {
            enemy.y = MathUtils.random(64, TOP_BAR_OFFSET - 64 - 16);
        }

        enemy.x = SCREEN_WIDTH + 64;
        enemy.width = 96;
        enemy.height = 64;
        enemies.add(enemy);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    private void spawnProjectile() {
        Rectangle projectile = new Rectangle();
        projectile.y = player.y + 24;
        projectile.x = player.x + 91;
        projectile.width = 32;
        projectile.height = 16;
        projectiles.add(projectile);
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // Exit first, anything else later xD
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();

        switch (executionState) {
            case RUNNING:
                update();
                break;
            case PAUSED:
                pause();
                break;
        }
    }

    private void update() {
        // for now, I'll leave order as is
        // food for thought: refactor/optimize later if needed
        drawObjects(false);
        processInput();
        playerMovement();

        // enemy business
        if (!isBossFight) {
            spawnEnemyWhenApplicable();

            // obstacles
            spawnObstacleWhenApplicable();

            // random pickups spawn
            spawnRandomPickupWhenApplicable();
        }

        // bullets and enemies go here
        detectCollision();
    }

    private void spawnObstacleWhenApplicable() {
        if (TimeUtils.nanoTime() - lastObstacleSpawnTime
                >
                (OBSTACLE_SPAWN_RATE_COMPARATOR * getNextRandomInteger(false))
        ) {
            spawnObstacle();
        }
    }

    private int getNextRandomInteger(boolean isDebug) {
        // Ssanity check
        if (randomLUTCounter == randomLUT.length) {
            randomLUTCounter = 0;
        }


        // TODO: see why multiplying it more than by two makes such big diff
        return isDebug ? 2 : randomLUT[randomLUTCounter] * 2;
    }


    private void spawnEnemyWhenApplicable() {
        // TODO: make me work daddy
        if (TimeUtils.nanoTime() - lastEnemySpawnTime > (ENEMY_SPAWN_RATE_COMPARATOR - (ENEMY_SPAWN_RATE_COMPARATOR / MathUtils.random(32, 4096))))
            spawnEnemy();
    }

    private void spawnRandomPickupWhenApplicable() {
        if (TimeUtils.nanoTime() - lastRandomPickupSpawnTime > (RANDOM_PICKUP_SPAWN_RATE_COMPARATOR - (RANDOM_PICKUP_SPAWN_RATE_COMPARATOR / MathUtils.random(1980, 4096)))) {
            spawnPowerUp();
        }
    }

    private void playerMovement() {
        if (player.y < 16) player.y = 16;
        if (player.y > TOP_BAR_OFFSET - 64 - 16) {
            player.y = TOP_BAR_OFFSET - 64 - 16;
        }

        if (player.x < 16) player.x = 16;
        if (player.x > SCREEN_WIDTH - 91 - 16) player.x = SCREEN_WIDTH - 91 - 16;
    }

    private void processInput() {
        if (Gdx.input.isKeyPressed(Keys.NUM_9)) {
            livesCount = 0;
            executionState = ExecutionState.PAUSED;
        }

        // The more value -> the fewer enemies are being spawned
        // TOFDO: Move `PLAYER_MOVE_SPEED` to header
        int PLAYER_MOVE_SPEED = 140;
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.y = player.y + (PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.y = player.y - (PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.x = player.x + (PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.x = player.x - (PLAYER_MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }

        // shoot a missile
        if (Gdx.input.isKeyPressed(Keys.SPACE)
                && TimeUtils.nanoTime() - lastProjectileSpawnTime > PROJECTILE_SPAWN_RATE_COMPARATOR
        ) {
            shootSound.play();
            spawnProjectile();
        }

        // add an extra life
        if (Gdx.input.isKeyPressed(Keys.NUM_0)) {
            livesCount = 4;
            scoreCount += 40;
            xRaysCount = 2;
        }
    }

    private void drawObjects(boolean isPaused) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0.698f, 0.741f, 0.31f, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();

        // draw ui
        if (livesCount >= 1 && !isPaused) {
//            game.font.getData().setScale(1.5f);
            // draw live icons here instead of a count...
            drawUI();
//            drawScoreUI();
        } else {
            game.font.getData().setScale(0.94f);
            game.font.draw(game.batch, "GAME OVER!", 40, 128);
            game.font.getData().setScale(0.35f);
            game.font.draw(game.batch, "Press [SPACE] to restart", 40, 64);
            game.font.draw(game.batch, "- or press [ESC] to quit", 40, 32);
            game.font.getData().setScale(0.94f);
        }

        game.batch.draw(playerImage, player.x, player.y);

        // TODO: prototype for blinking functionality
        for (Rectangle enemy : enemies) {
            game.batch.draw(enemyImage, enemy.x, enemy.y);
        }

        for (Rectangle randomItem : randomPickups) {
            game.batch.draw(randomPickupImage, randomItem.x, randomItem.y);
        }

        for (Rectangle projectile : projectiles) {
            game.batch.draw(projectileImage, projectile.x, projectile.y);
        }

        for (Rectangle obstacle : obstacles) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }

        game.batch.end();
    }

    private void drawUI() {
        // extra lives
        if (livesCount == 2) {
            game.batch.draw(
                    extraLiveImageUI,
                    16,
                    TOP_BAR_OFFSET
            );
        } else if (livesCount == 3) {
            game.batch.draw(
                    extraLiveImageUI,
                    16,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImageUI,
                    16 + 8 + 64,
                    TOP_BAR_OFFSET
            );
        } else if (livesCount == 4) {
            game.batch.draw(
                    extraLiveImageUI,
                    16,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImageUI,
                    16 + 8 + 64,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImageUI,
                    16 + 8 + 64 + 8 + 64,
                    TOP_BAR_OFFSET
            );
        }

        // draw power-ups/pick-ups here
        if (xRaysCount > 0) {
            game.batch.draw(
                    xRayImage, // 64x64
                    8 + 32 + 64 + 64 + 64 + 64,
                    TOP_BAR_OFFSET
            );
            game.font.draw(
                    game.batch,
                    String.valueOf(xRaysCount),
                    24 + 32 + 64 + 64 + 64 + 64 + 64,
                    TOP_BAR_OFFSET + 62 // consider having 60 here...
            );
        }

//        if (shockWaveCount > 0) {
//
//            game.batch.draw(
//                    shockWaveImage, // 64x64
//                    16 + 64 + 64 + 64 + 64 + 64,
//                    TOP_BAR_OFFSET
//            );
//            game.font.draw(
//                    game.batch,
//                    String.valueOf(shockWaveCount),
//                    16 + 64 + 64 + 64 + 64 + 64 + 64,
//                    TOP_BAR_OFFSET + 62 // consider having 60 here...
//            );
//        }


        // score
        game.font.draw(
                game.batch,
                formatScore(),
                SCREEN_WIDTH - 256 - 48,
                TOP_BAR_OFFSET + 62 // consider having 60 here...
        );
    }

    private String formatScore() {
        String str = String.valueOf(scoreCount);
        String formattedString = "00000";

        // 0 - def
        switch (String.valueOf(scoreCount).length()) {
            case 1: {
                formattedString = "0000" + str;
                break;
            }
            case 2: {
                formattedString = "000" + str;
                break;
            }
            case 3: {
                formattedString = "00" + str;
                break;
            }
            case 4: {
                formattedString = "0" + str;
                break;
            }
            case 5: {
                formattedString = str;
                break;
            }
        }
        return formattedString;
    }

    // TODO: remove me if needed
    private void detectCollision() {
        // no need for further processing
        if (executionState.equals(ExecutionState.PAUSED)) return;

        for (Rectangle obstacle : obstacles) {
            obstacle.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();

            if (obstacle.x + 64 < 0) obstacles.removeIndex(obstacles.indexOf(obstacle, false));

            if (obstacle.overlaps(player)) {
                lastRespawnTime = TimeUtils.nanoTime();
                // TODO: remember what I meant by next line xD
                // TODO: make me work
                if (livesCount >= 1) {
                    livesCount--;
                    if (TimeUtils.nanoTime() - lastRespawnTime > OBSTACLE_SPAWN_RATE_COMPARATOR) {
                        resetLevelState(true);
                    }
                    executionState = ExecutionState.PAUSED;
                } else {
                    break;
                }
            }
        }

        for (Rectangle projectile : projectiles) {
            projectile.x += (MOVE_SPEED * 1.75) * Gdx.graphics.getDeltaTime();

            for (Rectangle enemy : enemies) {
                if (enemy.overlaps(projectile)) {
                    // for now we'll leave it like that
                    // TODO: make me diverse, based on enemy type
                    scoreCount += 20;

                    enemies.removeIndex(enemies.indexOf(enemy, false));
                    projectiles.removeIndex(projectiles.indexOf(projectile, false));
                }
            }

//            for (Rectangle obstacle : obstacles) {
//                if (projectile.overlaps(obstacle)) {
//                    projectiles.removeIndex(projectiles.indexOf(projectile, false));
//                }
//            }
        }

        Iterator<Rectangle> iterEnemies = enemies.iterator();
        // nope, doesn't work... future me, please check it once you got home
        while (iterEnemies.hasNext()) {
            Rectangle enemy = iterEnemies.next();

            // apply math
            enemy.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();
            if (enemy.x + 64 < 0) iterEnemies.remove();

            if (enemy.overlaps(player)) {
                livesCount--;
                iterEnemies.remove();

                if (livesCount < 1) {
                    executionState = ExecutionState.PAUSED;
                }
            }
        }

        // for now, I'll leave it here
        Iterator<Rectangle> iterRandomPickups = randomPickups.iterator();
        while (iterRandomPickups.hasNext()) {
            Rectangle item = iterRandomPickups.next();
            item.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();

            if (item.x + 64 < 0) iterRandomPickups.remove();

            if (item.overlaps(player)) {
                // todo update this to be random
                float mathRandom = MathUtils.random();
                // TODO: if we have 3 lives -> don't spawn extra life
                if (mathRandom < .4) {
                    if (livesCount <= 3) {
                        livesCount++;
                    }
                    lastPickedUpItemType = 0;
                    scoreCount += 40;
                } else if (mathRandom > .4) {
                    if (xRaysCount < 3) {
                        xRaysCount++;
                    }

                    lastPickedUpItemType = 1;
                    scoreCount += 80;
                } else {
                    // shock wave will spawn only if
                    shockWaveCount++;
                    lastPickedUpItemType = 2;
                    scoreCount += 120;
                }

                iterRandomPickups.remove();
            }
        }

//        Iterator<Rectangle> iterXRayCannons = xRays.iterator();
//        while (iterXRayCannons.hasNext()) {
//            Rectangle cannon = iterXRayCannons.next();
//            cannon.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();
//
//            if (cannon.x + 64 < 0) iterXRayCannons.remove();
//
//            if (cannon.overlaps(player)) {
//                xRaysCount++;
//                iterXRayCannons.remove();
//            }
//        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void show() {
        // start the playback of the background music
        // when the screen is shown
//        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
        if (Gdx.input.isKeyPressed(Keys.SPACE) || Gdx.input.isKeyPressed(Keys.R)) {
            resetLevelState(livesCount >= 1 && scoreCount > 0);
            executionState = ExecutionState.RUNNING;
        }

        drawObjects(true);
    }

    private void resetLevelState(boolean shouldRespawn) {
        getSessionState();

        if (!shouldRespawn) {
            livesCount = 1;
            scoreCount = 0;
        }

        executionState = ExecutionState.RUNNING;
    }

    private void getSessionState() {
        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        // create a Rectangle to logically represent the bucket
        player = new Rectangle();
        player.x = 16; // bottom left corner of the bucket is 20 pixels above
        //noinspection IntegerDivisionInFloatingPointContext
        player.y = SCREEN_HEIGHT / 2 - 16; // center the bucket horizontally
        // the bottom screen edge
        player.width = 91;
        player.height = 64;

        // Init arrays
        randomPickups = new Array<>();
        enemies = new Array<>();
        obstacles = new Array<>();
        projectiles = new Array<>();
//        xRays = new Array<>();

        // INFO: we don't reset other timers here, cause e.g. enemy timer is being reset inside `spawnEnemy()`
        // natural delay
        long initialDelayTime = TimeUtils.nanoTime();
        lastProjectileSpawnTime = initialDelayTime;
        lastRandomPickupSpawnTime = initialDelayTime;
        lastObstacleSpawnTime = initialDelayTime;

        System.out.println(lastRandomPickupSpawnTime);
        System.out.println(lastObstacleSpawnTime);

        // init
        spawnEnemy();
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        enemyImage.dispose();
        projectileImage.dispose();
        playerImage.dispose();
        shootSound.dispose();
        randomPickupImage.dispose();
        extraLiveImageUI.dispose();
        obstacleImage.dispose();
        xRayImage.dispose();
        shockWaveImage.dispose();
    }
}