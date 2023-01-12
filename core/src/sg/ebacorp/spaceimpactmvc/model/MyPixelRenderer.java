package sg.ebacorp.spaceimpactmvc.model;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class MyPixelRenderer {
//
//    public static final int X_POINTS = 16;
//    public static final int Y_POINTS = 16;

//    public static void generate(Pixmap pixmap) {
//
//        int width = pixmap.getWidth();
//        int height = pixmap.getHeight();
//
//        float distBufferp[] = new float[width * height + 1];
//        float xcoords[] = new float[X_POINTS];
//        float ycoords[] = new float[Y_POINTS];
//        float globalMin = Float.MAX_VALUE;
//        float globalMax = 0;
//
//        for (int i = 0; i < X_POINTS; i++) {
//            xcoords[i] = MathUtils.random(0, width);
//            ycoords[i] = MathUtils.random(0, height);
//        }
//
//        for (int x = 1; x < width + 1; x++) {
//            for (int y = 1; y < height + 1; y++) {
//                float dst = getNearestDistance(x, y, xcoords, ycoords);
//                distBufferp[x * y] = dst * dst;
//                if (dst < globalMin) {
//                    globalMin = dst;
//                }
//                if (dst > globalMax) {
//                    globalMax = dst;
//                }
//            }
//        }
//        for (int pixelx = 1; pixelx < width + 1; pixelx++) {
//            for (int pixely = 1; pixely < height + 1; pixely++) {
//                float j = distBufferp[pixelx * pixely] - globalMin;
//                float denom = globalMax - globalMin;
//                float color = j / denom;
//                int colorToDraw = Color.rgba8888(color, color, color, 1);
//                pixmap.drawPixel(pixelx, pixely, colorToDraw);
//            }
//        }
//    }
//
//    private static float getNearestDistance(int x, int y, float[] xcoords, float[] ycoords) {
//        float mindist = Float.MAX_VALUE;
//        for (int i = 0; i < X_POINTS; i++) {
//            for (int j = 0; j < Y_POINTS; j++) {
//                float dst = new Vector2(x, y).dst(new Vector2(xcoords[i], ycoords[j]));
//                if (dst < mindist) {
//                    mindist = dst;
//                }
//            }
//        }
//        return mindist;
//    }

    public static void generatePerlin(Pixmap pixmap, boolean clouds, float depth) {
        int height = pixmap.getHeight();
        int width = pixmap.getWidth();
        float[][] noise = generateNoise(height, width);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                float color = turbulence((float) i, (float) j, depth, noise, height, width);
                if (clouds) {
                    Color newColor = new Color(0, 0, 0, 1);
                    float[] hsv = new float[3];
                    hsv[0] = 196f;
                    hsv[1] = color / 4f; // light brightness will be from 0 till 0.25
                    hsv[2] = 0.99f;
                    pixmap.drawPixel(i, j, Color.rgba8888(newColor.fromHsv(hsv)));
                } else {
                    pixmap.drawPixel(i, j, Color.rgba8888(color, color, color, 1));
                }
            }
        }
    }

    static float[][] generateNoise(int height, int width) {
        float[][] noise = new float[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                noise[y][x] = MathUtils.random();
            }
        }
        return noise;
    }

    static float smoothNoise(double x, double y, float[][] noise, int height, int width) {
        int noiseWidth = width;
        int noiseHeight = height;
        //get fractional part of x and y
        double fractX = x - Math.floor(x);
        double fractY = y - Math.floor(y);

        //wrap around
        int x1 = ((int) Math.floor(x) + noiseWidth) % noiseWidth;
        int y1 = ((int) Math.floor(y) + noiseHeight) % noiseHeight;

        //neighbor values
        int x2 = (x1 + noiseWidth - 1) % noiseWidth;
        int y2 = (y1 + noiseHeight - 1) % noiseHeight;

        //smooth the noise with bilinear interpolation
        float value = 0.0f;
        value += fractX * fractY * noise[y1][x1];
        value += (1 - fractX) * fractY * noise[y1][x2];
        value += fractX * (1 - fractY) * noise[y2][x1];
        value += (1 - fractX) * (1 - fractY) * noise[y2][x2];

        return value;
    }

    static float turbulence(float x, float y, float size, float[][] noise, int height, int width) {
        float value = 0.0f;
        float initialSize = size;

        while (size >= 1) {
            float v = smoothNoise(x / size, y / size, noise, height, width);
            value += v * size;
            size /= 2.0;
        }
        float v = value / initialSize;
        // normalize values
        return v * 0.5f;
    }
}
