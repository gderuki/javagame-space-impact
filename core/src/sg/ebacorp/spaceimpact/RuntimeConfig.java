package sg.ebacorp.spaceimpact;

public class RuntimeConfig {
    public final int defaultFPS;
    public final int screenWidth;
    public final int screenHeight;
    public final boolean shouldUseLerp;
    public final boolean isDebug;
    public final boolean useVsync;

    private static RuntimeConfig instance;

    public RuntimeConfig(int screenWidth, int screenHeight, int defaultFPS, boolean shouldUseLerp, boolean isDebug, boolean useVsync) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.defaultFPS = defaultFPS;
        this.shouldUseLerp = shouldUseLerp;
        this.isDebug = isDebug;
        this.useVsync = useVsync;
    }

    public static RuntimeConfig getInstance() {
        if (RuntimeConfig.instance == null) {
            RuntimeConfig.instance =
                    new RuntimeConfig(
                            800,
                            480,
                            60,
                            false,
                            true,
                            false);
        }

        return RuntimeConfig.instance;
    }
}
