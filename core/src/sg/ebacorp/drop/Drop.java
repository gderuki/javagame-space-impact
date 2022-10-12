package sg.ebacorp.drop;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

public class Drop extends ApplicationAdapter {
    // Assets
    private Texture dropImage;
    private Texture bucketImage;
    private Sound dropSound;
    private Music rainMusic;

    // Utils
    private SpriteBatch batch;
    private OrthographicCamera camera;

    // Objects
    private Rectangle bucket;
    private Array<Rectangle> raindrops;
    private long lastDropTime;
    private int playerCounter; // need ui for that
    @SuppressWarnings("FieldCanBeLocal")
    private final int MOVE_SPEED = 250;

    @Override
    public void create() {
        // load the images for the droplet and the bucket, 64x64 pixels each
        dropImage = new Texture(Gdx.files.internal("drop.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        // load the drop sound effect and the rain background "music"
        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));

        // start the playback of the background music immediately
        rainMusic.setLooping(true);
        rainMusic.play();

        // create the camera and the SpriteBatch
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);
        batch = new SpriteBatch();

        // create a Rectangle to logically represent the bucket
        bucket = new Rectangle();
        bucket.y = 480 / 2 - 64 / 2; // center the bucket horizontally
        bucket.x = 20; // bottom left corner of the bucket is 20 pixels above the bottom screen edge
        bucket.width = 64;
        bucket.height = 64;

        // create the raindrops array and spawn the first raindrop
        raindrops = new Array<>();
        spawnRaindrop();
    }

    private void spawnRaindrop() {
        Rectangle raindrop = new Rectangle();
//        raindrop.y = MathUtils.random(0, 800 - 64);
//        raindrop.x = 480;
        raindrop.y = MathUtils.random(0, 400 - 64);
        raindrop.x = 800;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    @Override
    public void render() {
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
        batch.setProjectionMatrix(camera.combined);

        // begin a new batch and draw the bucket and
        // all drops
        batch.begin();
        batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop : raindrops) {
            batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        batch.end();

        // process user input
        if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.UP)) {
            // TODO: write proper lerp movement
//            bucket.y += MathUtils.lerp(bucket.y, bucket.y + MOVE_SPEED * Gdx.graphics.getDeltaTime(), 1.0f);
            bucket.y = bucket.y + (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }
        if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.DOWN)) {
            // TODO: write proper lerp movement
//            bucket.y -= MathUtils.lerp(bucket.y + MOVE_SPEED * Gdx.graphics.getDeltaTime(), bucket.y, 1.0f);
            bucket.y = bucket.y - (MOVE_SPEED * Gdx.graphics.getDeltaTime());
        }

        // make sure the bucket stays within the screen bounds
        if (bucket.y < 0 + 16) bucket.y = 0 + 16;
        if (bucket.y > 400 - 16) bucket.y = 400 - 16;

        // check if we need to create a new raindrop
        if (TimeUtils.nanoTime() - lastDropTime > 1000000000) spawnRaindrop();

        // move the raindrops, remove any that are beneath the bottom edge of
        // the screen or that hit the bucket. In the latter case we play back
        // a sound effect as well.
        for (Iterator<Rectangle> iter = raindrops.iterator(); iter.hasNext(); ) {
            Rectangle raindrop = iter.next();
            raindrop.x -= 100 * Gdx.graphics.getDeltaTime();
            if (raindrop.x + 64 < 0) iter.remove();
            if (raindrop.overlaps(bucket)) {
                dropSound.play();
                iter.remove();
            }
        }
    }

    @Override
    public void dispose() {
        // dispose of all the native resources
        dropImage.dispose();
        bucketImage.dispose();
        dropSound.dispose();
        rainMusic.dispose();
        batch.dispose();
    }

    // non-precise lerp
    private float inacurateLerp(float a, float b, float f)
    {
        return a + f * (b - a);
    }

    private float preciseLerp(float a, float b, float f)
    {
        return (float) (a * (1.0 - f) + (b * f));
    }
}