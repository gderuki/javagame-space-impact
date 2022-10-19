package sg.ebacorp.spaceimpact;

public class RuntimeConfig {
    public final int defaultFPS;
    public final int screenWidth;
    public final int screenHeight;

    private static RuntimeConfig instance;

    public RuntimeConfig(int screenWidth, int screenHeight, int defaultFPS) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.defaultFPS = defaultFPS;
    }

    public static RuntimeConfig getInstance() {
        if (RuntimeConfig.instance == null) {
            RuntimeConfig.instance = new RuntimeConfig(800, 480, 30);
        }

        return RuntimeConfig.instance;
    }
}
