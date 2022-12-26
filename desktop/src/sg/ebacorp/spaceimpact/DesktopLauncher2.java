package sg.ebacorp.spaceimpact;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import sg.ebacorp.spaceimpact.utils.RuntimeConfig;
import sg.ebacorp.spaceimpactmvc.SpaceImpactGame;

public class DesktopLauncher2 {
    // private final static boolean IS_DEBUG = RuntimeConfig.getInstance().isDebug;
    //private final static boolean USE_VSYNC = RuntimeConfig.getInstance().useVsync;

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        // Fullscreen
//        if (!IS_DEBUG) {
//            config.setMaximized(true);
//            Graphics.DisplayMode displayMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
//            config.setFullscreenMode(displayMode);
//        }
        // we can't have simultaneously vsync and fps cap.
        // while fps cap isn't so accurate I'd say fuck it, I must continue with fps cap instead, don't want dem 60fps
        //		config.useVsync(true);
        config.setTitle("Space Impact");

        // INFO: could be used fps cap OR vsync, using both will not work,
        //  guess, the priority will go to vsync rather than to fps cap.

        config.setWindowedMode(RuntimeConfig.getInstance().screenWidth, RuntimeConfig.getInstance().screenHeight);
        config.useVsync(true);
//
//        if (!USE_VSYNC) {
//            config.setForegroundFPS(RuntimeConfig.getInstance().defaultFPS);
//        }

        // new Lwjgl3Application(new DefaultGame(), config);
        Lwjgl3Application lwjgl3Application = new Lwjgl3Application(new SpaceImpactGame(), config);
        lwjgl3Application.exit();

    }
}
