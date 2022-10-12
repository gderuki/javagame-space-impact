package sg.ebacorp.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import org.w3c.dom.css.Rect;


public class GameScreen implements Screen {
    final Drop game;

    Texture enemyImage;
    Texture projectileImage;
    Texture playerImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> enemies;
    Array<Rectangle> projectiles;
    long lastEnemySpawnTime;
    long lastProjectileSpawnTime;
    int scoreCount;
    int livesCount = 1;

    @SuppressWarnings("FieldCanBeLocal")
    private final int MOVE_SPEED = 250;

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long ENEMY_SPAWN_RATE_COMPARATOR = (1000000000 * 2L);

    // The more value -> the fewer enemies are being spawned
    @SuppressWarnings("FieldCanBeLocal")
    private final long PROJECTILE_RATE_COMPARATOR = 1000000000;

    public GameScreen(final Drop gam) {
        this.game = gam;

        // load the images for the droplet and the bucket, 64x64 pixels each
        enemyImage = new Texture(Gdx.files.internal("droplet.png"));
        projectileImage = new Texture(Gdx.files.internal("fireball.png"));
        playerImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.x = 20; // center the bucket horizontally
        bucket.y = 480 / 2 - 20; // bottom left corner of the bucket is 20 pixels above
        // the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        enemies = new Array<>();
        spawnRaindrop();

        // setup projectile
        projectiles = new Array<>();
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.y = MathUtils.random(0, 480 - 64);
        raindrop.x = 800;
        raindrop.width = 64;
        raindrop.height = 64;
        enemies.add(raindrop);
        lastEnemySpawnTime = TimeUtils.nanoTime();
    }

    private void spawnProjectile() {
        Rectangle projectile = new Rectangle();
        projectile.y = bucket.y;
        projectile.x = bucket.x + 34;
        projectile.width = 64;
        projectile.height = 34;
        projectiles.add(projectile);
        lastProjectileSpawnTime = TimeUtils.nanoTime();
    }

    @Override
    public void render(float delta) {
        // Exit first, anything else later xD
        if (Gdx.input.isKeyPressed(Keys.ESCAPE)) Gdx.app.exit();

        // clear the screen with a dark blue color. The
        // arguments to clear are the red, green
        // blue and alpha component in the range [0,1]
        // of the color to be used to clear the screen.
        ScreenUtils.clear(0, 0, 0.2f, 1);

        // tell the camera to update its matrices.
        camera.update();

        // tell the SpriteBatch to render in the
        // coordinate system specified by the camera.
        game.batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        game.batch.begin();
        if (livesCount >= 1) {
            game.font.draw(game.batch, "Score: " + scoreCount, 340, 470);
        } else {
            game.font.draw(game.batch, "GAME OVER!", 340, 470);
        }

        game.batch.draw(playerImage, bucket.x, bucket.y);
        for (Rectangle enemy : enemies) {
            game.batch.draw(enemyImage, enemy.x, enemy.y);
        }

        for (Rectangle projectile : projectiles) {
            game.batch.draw(projectileImage, projectile.x, projectile.y);
        }


        game.batch.end();

        // process user input
        if (Gdx.input.isKeyPressed(Keys.UP)) {
            bucket.y = bucket.y + (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.DOWN)) {
            bucket.y = bucket.y - (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            bucket.x = bucket.x + (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            bucket.x = bucket.x - (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }

        // shoot a missile
        if (Gdx.input.isKeyPressed(Keys.SPACE)
                && TimeUtils.nanoTime() - lastProjectileSpawnTime > PROJECTILE_RATE_COMPARATOR
        ) {
            spawnProjectile();
        }

        // make sure the bucket stays within the screen bounds
        if (bucket.y < 0 + 16) bucket.y = 0 + 16;
        if (bucket.y > 480 - 64 - 16) bucket.y = 480 - 64 - 16;

        if (bucket.x < 0 + 16) bucket.x = 0 + 16;
        // 800  - screen size
        // 64   - size of a sprite
        // 16   - border
        if (bucket.x > 800 - 64 - 16) bucket.x = 800 - 64 - 16;

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastEnemySpawnTime > ENEMY_SPAWN_RATE_COMPARATOR)
            spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the later case we play back
        // a sound effect as well.
        Iterator<Rectangle> iterProjectiles = projectiles.iterator();
//        while (iterProjectiles.hasNext()) {

//            if (projectile.x + 64 > 800)
//                iterProjectiles.remove();
//
//            Iterator<Rectangle> eIterator = enemies.iterator();
//            while (eIterator.hasNext()) {
//                Rectangle enemy = eIterator.next();
//                if (projectile.overlaps(enemy)) {
//                    iterProjectiles.remove(); break;
//                }
//            }
//        }

        Iterator<Rectangle> iterEnemies = enemies.iterator();
        while (iterEnemies.hasNext()) {
            Rectangle enemy = iterEnemies.next();
            enemy.x -= 100 * Gdx.graphics.getDeltaTime();
            if (enemy.x + 64 < 0)
                iterEnemies.remove();
            // TODO: Rewrite, doesn't work properly
            Iterator<Rectangle> pIterator = projectiles.iterator();
            while (pIterator.hasNext()) {
                Rectangle projectile = pIterator.next();
                projectile.x += 200 * Gdx.graphics.getDeltaTime();

                if (enemy.overlaps(projectile)) {
                    scoreCount++;
                    livesCount++;
                    iterProjectiles.remove();
                    iterEnemies.remove();
                }
            }

            if (enemy.overlaps(bucket)) {
                livesCount--;
                iterEnemies.remove();
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
        rainMusic.play();
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        enemyImage.dispose();
        projectileImage.dispose();
        playerImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
    }

}