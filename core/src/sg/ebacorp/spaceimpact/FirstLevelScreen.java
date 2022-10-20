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


public class FirstLevelScreen implements Screen {
    final DefaultGame game;
    private final int SCREEN_HEIGHT = 480;// + 64 + 16;
    private final int SCREEN_WIDTH = 800;

    Texture enemyImage;
    Texture projectileImage;
    Texture playerImage;
    Texture extraLiveImage;
    Texture obstacleImage;
    Sound shootSound;
    //    Music rainMusic;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> extraLives;
    Array<Rectangle> enemies;
    Array<Rectangle> projectiles;
    Array<Rectangle> obstacles;
    long lastEnemySpawnTime;
    long lastExtraLifeSpawnTime;
    long lastProjectileSpawnTime;
    long lastObstacleSpawnTime;
    int scoreCount;
    int livesCount = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MOVE_SPEED = 130;

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long ENEMY_SPAWN_RATE_COMPARATOR = (1000000000 * 2L);

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long PROJECTILE_RATE_COMPARATOR = 180000000; // og: 1000000000

    private ExecutionState executionState;

    @SuppressWarnings("FieldCanBeLocal")
    private final int TOP_BAR_OFFSET = (SCREEN_HEIGHT - 64 - 16);

    public FirstLevelScreen(final DefaultGame gam) {
        this.game = gam;
        initAssets();

        // TODO: get back on this one some time later
        resetLevelState();
    }

    private void initAssets() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        enemyImage = new Texture(Gdx.files.internal("enemy.png"));
        projectileImage = new Texture(Gdx.files.internal("laser_small.png"));
        playerImage = new Texture(Gdx.files.internal("ship.png"));
        extraLiveImage = new Texture(Gdx.files.internal("extra_life.png"));
        obstacleImage = new Texture(Gdx.files.internal("obstacle.png"));

        // load the drop sound effect and the rain background "music"
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
    }

    private void spawnExtraLive() {
        Rectangle extraLive = new Rectangle();
        extraLive.y = MathUtils.random(16, TOP_BAR_OFFSET - 64 - 16);
        extraLive.x = SCREEN_WIDTH - 64;
        extraLive.width = 64;
        extraLive.height = 64;
        // we add -> we clean on pickup, we repopulate
        extraLives.add(extraLive);
        lastExtraLifeSpawnTime = TimeUtils.nanoTime();
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
        enemy.y = MathUtils.random(0, TOP_BAR_OFFSET - 64 - 16);
        enemy.x = SCREEN_WIDTH;
        enemy.width = 64;
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
        spawnEnemyWhenApplicable();

        // obstacles
        spawnObstacleWhenApplicable();

        // extra life business
        spawnExtraLifeWhenApplicable();
//        detectCollisionForExtraLife();

        // bullets and enemies go here
        detectCollision();
    }

    private void spawnObstacleWhenApplicable() {
        if (TimeUtils.nanoTime() - lastObstacleSpawnTime > (ENEMY_SPAWN_RATE_COMPARATOR * 8)) {
            spawnObstacle();
        }
    }


    private void spawnEnemyWhenApplicable() {
        if (TimeUtils.nanoTime() - lastEnemySpawnTime > ENEMY_SPAWN_RATE_COMPARATOR)
            spawnEnemy();
    }

    private void spawnExtraLifeWhenApplicable() {
        if (TimeUtils.nanoTime() - lastExtraLifeSpawnTime > (ENEMY_SPAWN_RATE_COMPARATOR * 4)) {
            spawnExtraLive();
        }
    }

    private void playerMovement() {
        if (player.y < 0 + 16) player.y = 0 + 16;
        if (player.y > TOP_BAR_OFFSET - 64 - 16) {
            player.y = TOP_BAR_OFFSET - 64 - 16;
        }

        if (player.x < 0 + 16) player.x = 0 + 16;
        if (player.x > SCREEN_WIDTH - 91 - 16) player.x = SCREEN_WIDTH - 91 - 16;
    }

    private void processInput() {
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            player.y = player.y + (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            player.y = player.y - (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            player.x = player.x + (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            player.x = player.x - (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }

        // shoot a missile
        if (Gdx.input.isKeyPressed(Keys.SPACE)
                && TimeUtils.nanoTime() - lastProjectileSpawnTime > PROJECTILE_RATE_COMPARATOR
        ) {
            shootSound.play();
            spawnProjectile();
        }

        // add an extra life
        if (Gdx.input.isKeyPressed(Keys.NUM_0)) {
            livesCount = 4;
            scoreCount += MathUtils.random(20, 100);
        }
    }

    private void drawObjects(boolean isPaused) {
        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        if (isPaused) {
            ScreenUtils.clear(0.560f, 0, 0, 1);
        } else {
            // background color
            ScreenUtils.clear(0.549f, 0.730f, 0.504f, 1);
        }

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
            drawExtraLives();
            drawScoreUI();
        } else {
//            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, "GAME OVER!", 40, 70);
//            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, "Press [SPACE] to restart", 40, 40);
        }

        game.batch.draw(playerImage, player.x, player.y);

        // TODO: prototype for blinking functionality
        for (Rectangle enemy : enemies) {
            game.batch.draw(enemyImage, enemy.x, enemy.y);
        }

        for (Rectangle life : extraLives) {
            game.batch.draw(extraLiveImage, life.x, life.y);
        }

        for (Rectangle projectile : projectiles) {
            game.batch.draw(projectileImage, projectile.x, projectile.y);
        }

        for (Rectangle obstacle : obstacles) {
            game.batch.draw(obstacleImage, obstacle.x, obstacle.y);
        }

        game.batch.end();
    }

    private void drawExtraLives() {
        if (livesCount == 2) {
            game.batch.draw(
                    extraLiveImage,
                    16,
                    TOP_BAR_OFFSET
            );
        } else if (livesCount == 3) {
            game.batch.draw(
                    extraLiveImage,
                    16,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImage,
                    16 + 8 + 64,
                    TOP_BAR_OFFSET
            );
        } else if (livesCount == 4) {
            game.batch.draw(
                    extraLiveImage,
                    16,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImage,
                    16 + 8 + 64,
                    TOP_BAR_OFFSET
            );
            game.batch.draw(
                    extraLiveImage,
                    16 + 8 + 64 + 8 + 64,
                    TOP_BAR_OFFSET
            );
        }
    }

    private void drawScoreUI() {
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
        Iterator<Rectangle> iterExtraLives = extraLives.iterator();
        while (iterExtraLives.hasNext()) {
            Rectangle life = iterExtraLives.next();
            life.x -= MOVE_SPEED * Gdx.graphics.getDeltaTime();

            if (life.x + 64 < 0) iterExtraLives.remove();

            if (life.overlaps(player)) {
                if (livesCount <= 3) {
                    livesCount++;
                }

                scoreCount += MathUtils.random(50, 100);

                iterExtraLives.remove();
            }
        }
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
            resetLevelState();

            executionState = ExecutionState.RUNNING;
        }

        drawObjects(true);
    }

    private void resetLevelState() {
        executionState = ExecutionState.RUNNING;

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

        // Extra live pickup part...
        // TODO: make me array based collision detection
        extraLives = new Array<>();

        // create the raindrops array and spawn the first raindrop
        enemies = new Array<>();

        obstacles = new Array<>();

        // init
        spawnEnemy();
        spawnExtraLive();

        // setup projectile
        projectiles = new Array<>();
        lastProjectileSpawnTime = TimeUtils.nanoTime();

        // stats
        livesCount = 1;
        scoreCount = 0;
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
        extraLiveImage.dispose();
        obstacleImage.dispose();
    }
}