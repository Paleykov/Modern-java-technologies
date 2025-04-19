package bg.sofia.uni.fmi.mjt.imagekit;

import bg.sofia.uni.fmi.mjt.imagekit.algorithm.ImageAlgorithm;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.detection.SobelEdgeDetection;
import bg.sofia.uni.fmi.mjt.imagekit.algorithm.grayscale.LuminosityGrayscale;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.FileSystemImageManager;
import bg.sofia.uni.fmi.mjt.imagekit.filesystem.LocalFileSystemImageManager;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (IOException e) {
            System.err.println("Error processing image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run() throws IOException {
        System.out.println("Looking for: " + new File("kitten.png").getAbsolutePath());
        FileSystemImageManager fsImageManager = new LocalFileSystemImageManager();

        BufferedImage image = fsImageManager.loadImage(new File("C:\\Users\\Georgi Paleykov\\Desktop\\JAVA STUFF\\Tasks\\Week 7\\kitten.png"));

        ImageAlgorithm grayscaleAlgorithm = new LuminosityGrayscale();
        BufferedImage grayscaleImage = grayscaleAlgorithm.process(image);

        ImageAlgorithm sobelEdgeDetection = new SobelEdgeDetection(grayscaleAlgorithm);
        BufferedImage edgeDetectedImage = sobelEdgeDetection.process(image);

        fsImageManager.saveImage(grayscaleImage, new File("kitten-grayscale.png"));
        fsImageManager.saveImage(edgeDetectedImage, new File("kitten-edge-detected.png"));
    }
}

