package sg.ebacorp.drop;

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

import static sg.ebacorp.drop.ExecutionState.PAUSED;


public class FirstLevelScreen implements Screen {
    final DefaultGame game;
    private final int SCREEN_HEIGHT = 480;
    private final int SCREEN_WIDTH = 800;

    Texture enemyImage;
    Texture projectileImage;
    Texture playerImage;
    Sound shootSound;
//    Music rainMusic;
    OrthographicCamera camera;
    Rectangle player;
    Array<Rectangle> enemies;
    Array<Rectangle> projectiles;
    long lastEnemySpawnTime;
    long lastProjectileSpawnTime;
    int scoreCount;
    int livesCount = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MOVE_SPEED = 160;

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long ENEMY_SPAWN_RATE_COMPARATOR = (1000000000 * 2L);

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long PROJECTILE_RATE_COMPARATOR = 180000000; // og: 1000000000

    // TODO: Make me work later
//    private final Blinker blinker;

    private ExecutionState executionState;

    public FirstLevelScreen(final DefaultGame gam) {
        this.game = gam;
        initAssets();

        // TODO: get back on this one some time later
//        blinker = new Blinker();
//        blinker.setBlinking(true);

        resetLevelState();
    }

    private void initAssets() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        enemyImage = new Texture(Gdx.files.internal("enemy.png"));
        projectileImage = new Texture(Gdx.files.internal("laser.png"));
        playerImage = new Texture(Gdx.files.internal("ship.png"));

        // load the drop sound effect and the rain background "music"
        shootSound = Gdx.audio.newSound(Gdx.files.internal("shoot.wav"));
//        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
//        rainMusic.setLooping(true);
    }

    private void spawnEnemy() {
        Rectangle raindrop = new Rectangle();
        raindrop.y = MathUtils.random(0, SCREEN_HEIGHT - 64);
        raindrop.x = SCREEN_WIDTH;
        raindrop.width = 64;
        raindrop.height = 64;
        enemies.add(raindrop);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    private void spawnProjectile() {
        Rectangle projectile = new Rectangle();
        projectile.y = player.y;
        projectile.x = player.x + 64;
        projectile.width = 64;
        projectile.height = 64;
        projectiles.add(projectile);
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // Exit first, anything else later xD
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();

        // TODO: Fix this, somehow this is not working properly
//        if (Gdx.input.isKeyPressed(Keys.P)) {
//            if (executionState == ExecutionState.RUNNING) {
//                executionState = ExecutionState.PAUSED;
//            } else {
//                if (livesCount >= 1) {
//                    executionState = ExecutionState.RUNNING;
//                }
//            }
//        }

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
        checkPlayerBounds();
        spawnEnemyIfApplicable();
        detectCollision();
    }

    private void spawnEnemyIfApplicable() {
        if (TimeUtils.nanoTime() - lastEnemySpawnTime > ENEMY_SPAWN_RATE_COMPARATOR)
            spawnEnemy();
    }

    private void checkPlayerBounds() {
        if (player.y < 0 + 16) player.y = 0 + 16;
        if (player.y > SCREEN_HEIGHT - 64 - 16) player.y = SCREEN_HEIGHT - 64 - 16;

        if (player.x < 0 + 16) player.x = 0 + 16;
        // 800  - screen size
        // 64   - size of a sprite
        // 16   - border
        if (player.x > SCREEN_WIDTH - 64 - 16) player.x = SCREEN_WIDTH - 64 - 16;
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
            livesCount = 3;
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
            ScreenUtils.clear(0.477f, 0.770f, 0.308f, 1);
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
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, "Extra Lives: " + livesCount, 40, 460);
            game.font.draw(game.batch, "Score: " + scoreCount, 680, 460);
        } else {
            game.font.getData().setScale(1.5f);
            game.font.draw(game.batch, "GAME OVER!", 40, 70);
            game.font.getData().setScale(1.0f);
            game.font.draw(game.batch, "Press [SPACE] to restart", 40, 40);
        }

        game.batch.draw(playerImage, player.x, player.y);

        // TODO: prototype for blinking functionality
//        if (!blinker.shouldBlink(Gdx.graphics.getDeltaTime())) {
            for (Rectangle enemy : enemies) {
                game.batch.draw(enemyImage, enemy.x, enemy.y);
            }

            for (Rectangle projectile : projectiles) {
                game.batch.draw(projectileImage, projectile.x, projectile.y);
            }
//        }

        game.batch.end();
    }

    private void detectCollision() {
        Iterator<Rectangle> iterEnemies = enemies.iterator();
        // nope, doesn't work... future me, please check it once you got home
        while (iterEnemies.hasNext() && !executionState.equals(PAUSED)) {
            Rectangle enemy = iterEnemies.next();

            // apply math
            enemy.x -= 120 * Gdx.graphics.getDeltaTime();
            if (enemy.x + 64 < 0)
                iterEnemies.remove();
            // TODO: Rewrite, doesn't work properly
            if (enemy.overlaps(player)) {
                livesCount--;
                iterEnemies.remove();

                if (livesCount < 1) {
                    executionState = PAUSED;
                }
            }
        }

        for (Rectangle projectile : projectiles) {
            projectile.x += 150 * Gdx.graphics.getDeltaTime();
            for (Rectangle enemy : enemies) {
                if (enemy.overlaps(projectile)) {
                    scoreCount++;
//                    livesCount++;
                    enemies.removeIndex(enemies.indexOf(enemy, false));
                    projectiles.removeIndex(projectiles.indexOf(projectile, false));
                }
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
        player.x = 20; // bottom left corner of the bucket is 20 pixels above
        //noinspection IntegerDivisionInFloatingPointContext
        player.y = SCREEN_HEIGHT / 2 - 20; // center the bucket horizontally
        // the bottom screen edge
        player.width = 64;
        player.height = 64;

        // create the raindrops array and spawn the first raindrop
        enemies = new Array<>();

        // init enemies
        spawnEnemy();

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
//        rainMusic.dispose();
    }

}