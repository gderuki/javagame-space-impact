package sg.ebacorp.spaceimpactmvc;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;
import sg.ebacorp.spaceimpact.utils.RuntimeConfig;
import sg.ebacorp.spaceimpactmvc.controller.WorldController;
import sg.ebacorp.spaceimpactmvc.model.AABB;
import sg.ebacorp.spaceimpactmvc.model.Asteroid;

public class SpaceDemoScreen implements Screen, InputProcessor {

    public static final int PPU = 60;
    ArrayList<Asteroid> asteroidArrayList = new ArrayList<>();
    private PolygonSpriteBatch polygonSpriteBatch = new PolygonSpriteBatch();
    private OrthographicCamera playerCamera;
    private int spaceHeight = 4000;
    private int getSpaceWidth = 4000;
    private int getSpaceHeightUnit = spaceHeight / PPU;
    private int getSpaceWidthUnit = getSpaceWidth / PPU;
    RandomXS128 random = new RandomXS128();
    private ArrayList<WorldController.AsteroidPair> asteroidPairs = new ArrayList<>();
    Asteroid player;

    @Override
    public void show() {
        player = new Asteroid((int) (spaceHeight / 2f), (int) (getSpaceWidth / 2f));
        playerCamera = new OrthographicCamera();
        playerCamera.setToOrtho(false, RuntimeConfig.getInstance().screenWidth, RuntimeConfig.getInstance().screenHeight);
        playerCamera.position.x = (float) spaceHeight / 2f;
        playerCamera.position.y = (float) getSpaceWidth / 2f;
        Gdx.input.setInputProcessor(this);
        asteroidArrayList = new ArrayList<>();
        asteroidArrayList.add(player);
        for (int i = 1; i < getSpaceHeightUnit + 1; i++) {
            for (int j = 1; j < getSpaceWidthUnit + 1; j++) {
                int positionx = i * PPU;
                int positiony = j * PPU;
                random.setSeed((positionx & 0xFFFFFFFF) << 16 | (positiony & 0xFFFFFFFF));
                if (random(0, 20) == 1) {
                    asteroidArrayList.add(new Asteroid(i * PPU, j * PPU));
                }
            }
        }
    }

    private int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    private float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    private void updateAsteroidPosition(float delta, int i) {
        for (Asteroid asteroid : asteroidArrayList) {
            asteroid.update(delta, i);
        }
    }

    private void broadPhase() {
        Rectangle screenRectangle = getCameraRectangle();
        ArrayList<Asteroid> asteroids = new ArrayList<>();
        asteroids.addAll(asteroidArrayList);
        for (int i = 0; i < asteroids.size() - 1; i++) {

            Asteroid bodyA = asteroids.get(i);
            if (bodyA.getRectangle().overlaps(screenRectangle)) {
                AABB bodyA_aabb = bodyA.getAABB();
                for (int j = i + 1; j < asteroids.size(); j++) {
                    Asteroid bodyB = asteroids.get(j);
                    AABB bodyB_aabb = bodyB.getAABB();
                    if (AABB.IntersectAABBs(bodyA_aabb, bodyB_aabb)) {
                        this.asteroidPairs.add(new WorldController.AsteroidPair(bodyA, bodyB));
                    }
                }
            }
        }

    }

    private Rectangle getCameraRectangle() {
        Rectangle screenRectangle = new Rectangle(
                (playerCamera.position.x / playerCamera.zoom) - (playerCamera.viewportWidth / playerCamera.zoom / 2),
                (playerCamera.position.y / playerCamera.zoom) - (360 / playerCamera.zoom), (float) playerCamera.viewportWidth * playerCamera.zoom,
                (float) playerCamera.viewportHeight * playerCamera.zoom);

        return screenRectangle;
    }

    private void narrowPhase() {
        for (WorldController.AsteroidPair asteroidPair : this.asteroidPairs) {
            Asteroid o2 = asteroidPair.getO2();
            Asteroid o1 = asteroidPair.getO1();
            Asteroid.Overlap intersect = o1.intersect(o2, 1, 1);
            if (!intersect.isGap()) {
                o1.penetrationResolution(intersect, o2, 1, 1);
                o1.collisionResolution(intersect, o2, 1, 1);
            }
        }

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.698f, 0.741f, 0.31f, 1);
        for (int i = 0; i < 16; i++) {
            asteroidPairs.clear();
            updateAsteroidPosition(delta, 16);
            broadPhase();
            narrowPhase();
        }
        Rectangle cameraRectangle = getCameraRectangle();
        polygonSpriteBatch.setProjectionMatrix(playerCamera.combined);
        polygonSpriteBatch.begin();
        for (Asteroid asteroid : asteroidArrayList) {
            if (asteroid.getRectangle().overlaps(cameraRectangle)) {
                asteroid.draw(polygonSpriteBatch, 1, 1);
            }
        }
        polygonSpriteBatch.end();
        moveCamera(delta);
        playerCamera.position.set(player.getPosition().x, player.getPosition().y, 0);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    // Input processor

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    private void moveCamera(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            playerCamera.position.y += 0.1f * PPU;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            playerCamera.position.y -= 0.1f * PPU;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            playerCamera.position.x -= 0.1f * PPU;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            playerCamera.position.x += 0.1f * PPU;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            player.getAcceleration().y += 5f * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.getAcceleration().y -= 5f * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.getAcceleration().x -= 5f * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.getAcceleration().x += 5f * delta;
        }

        playerCamera.update();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {

//        for (Asteroid asteroid : asteroidArrayList) {
//            asteroid.setScale(amountY);
//        }
        playerCamera.zoom += 0.1f * amountY;
        playerCamera.update();

        return false;
    }
}
