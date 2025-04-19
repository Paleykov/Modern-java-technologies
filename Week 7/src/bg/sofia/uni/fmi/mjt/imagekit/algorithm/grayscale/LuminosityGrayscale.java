package bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale;

// 0.21 R + 0.72 G + 0.07 B

import java.awt.*;
import java.awt.image.BufferedImage;

public class LuminosityGrayscale implements GrayscaleAlgorithm {
    @Override
    public BufferedImage process(BufferedImage image) {
        if(image==null){
            throw new IllegalArgumentException("Image cannot be null");
        }

        var GrayImage =new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x<GrayImage.getWidth(); ++x){
            for(int y = 0; y<GrayImage.getHeight(); ++y){
                int currRGB = image.getRGB(x,y);

                Color c = new Color(currRGB);
                int gray = (int)(0.21 * c.getRed() + 0.72 * c.getGreen() + 0.07 * c.getBlue());

                Color grayColor = new Color(gray,gray,gray);

                GrayImage.setRGB(x,y, grayColor.getRGB());
            }
        }

        return GrayImage;
    }
}
