package sg.ebacorp.spaceimpact;

public class RuntimeConfig {
    public final int defaultFPS;
    public final int screenWidth;
    public final int screenHeight;
    public final boolean shouldUseLerp;
    public final boolean isDebug;
    public final boolean useVsync;
    public final float[] backgroundRGB;
    private static RuntimeConfig instance;


    public RuntimeConfig(
            int screenWidth,
            int screenHeight,
            int defaultFPS,
            boolean shouldUseLerp,
            boolean isDebug,
            boolean useVsync,
            float[] backgroundRGB
    ) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.defaultFPS = defaultFPS;
        this.shouldUseLerp = shouldUseLerp;
        this.isDebug = isDebug;
        this.useVsync = useVsync;
        this.backgroundRGB = backgroundRGB;
    }

    public static RuntimeConfig getInstance() {
        if (RuntimeConfig.instance == null) {
            RuntimeConfig.instance =
                    new RuntimeConfig(
                            800,
                            480,
                            60,
                            true,
                            true,
                            false,
                            new float[]{0.698f, 0.741f, 0.31f}
                    );
        }

        return RuntimeConfig.instance;
    }
}
