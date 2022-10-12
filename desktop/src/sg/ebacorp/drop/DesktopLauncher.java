package sg.ebacorp.drop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import sg.ebacorp.drop.Drop;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(800, 400);
        config.setForegroundFPS(60);

        // Fullscreen
//        config.setMaximized(true);
//        DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
//        config.setFullscreenMode(displayMode);

        // we can't have simultaneously vsync and fps cap.
        // while fps cap isn't so accurate I'd say fuck it, I must continue with fps cap instead, don't want dem 60fps
        //		config.useVsync(true);
        config.setTitle("Drop");
        new Lwjgl3Application(new Drop(), config);
    }
}
