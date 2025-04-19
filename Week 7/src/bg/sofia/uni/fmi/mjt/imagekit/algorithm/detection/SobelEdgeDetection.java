package bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SobelEdgeDetection implements EdgeDetectionAlgorithm {
    private final ImageAlgorithm grayscaleAlgorithm;

    private static final int[][] GX = {
            {-1, 0, 1},
            {-2, 0, 2},
            {-1, 0, 1}
    };

    private static final int[][] GY = {
            {-1, -2, -1},
            { 0,  0,  0},
            { 1,  2,  1}
    };

    public SobelEdgeDetection(ImageAlgorithm grayscaleAlgorithm) {
        this.grayscaleAlgorithm = grayscaleAlgorithm;
    }

    @Override
    public BufferedImage process(BufferedImage image) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }

        BufferedImage grayscale = grayscaleAlgorithm.process(image);

        int width = grayscale.getWidth();
        int height = grayscale.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {

                int gx = 0;
                int gy = 0;

                for (int ky = -1; ky <= 1; ++ky) {
                    for (int kx = -1; kx <= 1; ++kx) {
                        int pixel = new Color(grayscale.getRGB(x + kx, y + ky)).getRed();

                        gx += pixel * GX[ky + 1][kx + 1];
                        gy += pixel * GY[ky + 1][kx + 1];
                    }
                }

                // int magnitude = Math.min(255, Math.abs(gx) + Math.abs(gy));    // -> normal speed, virtually same result
                int magnitude = (int)Math.min(255, Math.sqrt(gx * gx + gy * gy)); // -> very slow, the exact algorithm

                Color edgeColor = new Color(magnitude, magnitude, magnitude);
                result.setRGB(x, y, edgeColor.getRGB());
            }
        }

        return result;
    }

}
