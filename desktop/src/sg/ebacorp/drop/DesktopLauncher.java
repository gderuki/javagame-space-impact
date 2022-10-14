package sg.ebacorp.drop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import sg.ebacorp.spaceimpact.DefaultGame;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setWindowedMode(800, 400);
        config.setForegroundFPS(24);

        // Fullscreen
//        config.setMaximized(true);
//        DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
//        config.setFullscreenMode(displayMode);

        // we can't have simultaneously vsync and fps cap.
        // while fps cap isn't so accurate I'd say fuck it, I must continue with fps cap instead, don't want dem 60fps
        //		config.useVsync(true);
        config.setTitle("Space Impact");
        new Lwjgl3Application(new DefaultGame(), config);
    }
}
