package sg.ebacorp.spaceimpact;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

public class MainMenuScreen implements Screen {
    final DefaultGame game;
    OrthographicCamera camera;

    public MainMenuScreen(DefaultGame game) {
        this.game = game;

        Gdx.input.setCursorCatched(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(
                false,
                RuntimeConfig.getInstance().screenWidth,
                RuntimeConfig.getInstance().screenHeight
        );
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Exit first, anything else later xD
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        ScreenUtils.clear(0.549f, 0.730f, 0.504f, 1);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.getData().setScale(1);
        game.font.draw(game.batch, "SPACE IMPACT", 40, 128);
        game.font.getData().setScale(0.5f);
        game.font.draw(game.batch, "Press [SPACE] to START", 40, 64);
        game.font.getData().setScale(1);
//        game.font.draw(game.batch, "Space Impact 2022", 150, 150);
//        game.font.draw(game.batch, "Press [SPACE] to begin", 150, 100);
        game.batch.end();

        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new FirstLevelScreen(game));
            dispose();
        }
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
}
