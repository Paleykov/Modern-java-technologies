package bg.sofia.uni.fmi.mjt.imagekit.filesystem;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

public class LocalFileSystemImageManager implements FileSystemImageManager{

    private boolean isSupportedFormat(String fileName) {
        String name = fileName.toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                name.endsWith(".png") || name.endsWith(".bmp");
    }

    @Override
    public BufferedImage loadImage(File imageFile) throws IOException {
        if(imageFile==null){
            throw new IllegalArgumentException("File is null");
        }

        if (!imageFile.exists() || !imageFile.isFile()) {
            throw new IOException("File does not exist or is not a regular file");
        }

        String name = imageFile.getName().toLowerCase();
        if(!isSupportedFormat(name)){
            throw new IOException("File format not supported");
        }

        try(var input = new FileInputStream(imageFile)){
            BufferedImage image = ImageIO.read(input);
            if (image == null) {
                throw new IOException("Unsupported or corrupted image format");
            }
            return image;
        } catch (IOException e) {
            throw new UncheckedIOException("A problem occurred while reading from a file", e);
        }
    }

    /**
     * Loads all images from the specified directory.
     *
     * @param imagesDirectory the directory containing the images.
     * @return A list of BufferedImages representing the loaded images.
     * @throws IllegalArgumentException if the directory is null.
     * @throws IOException              if the directory does not exist, is not a directory,
     *                                  or contains files that are not in one of the supported formats.
     */
    @Override
    public List<BufferedImage> loadImagesFromDirectory(File imagesDirectory) throws IOException {
        if(imagesDirectory==null){
            throw new IllegalArgumentException("Directory is null");
        }

        if (!imagesDirectory.exists() || !imagesDirectory.isDirectory()){
            throw new IOException("Directory does not exist or is not a Directory");
        }

        File[] allFiles = imagesDirectory.listFiles();
        if (allFiles == null) {
            throw new IOException("Empty directory");
        }

        List<BufferedImage> images = new ArrayList<>();

        for (File file : allFiles) {
            if (file.isFile() && isSupportedFormat(file.getName())){
                images.add(loadImage(file));
            }
        }

        return images;
    }

    /**
     * Saves the given image to the specified file path.
     *
     * @param image     the image to save.
     * @param imageFile the file to save the image to.
     * @throws IllegalArgumentException if the image or file is null.
     * @throws IOException              if the file already exists or the parent directory does not exist.
     */
    @Override
    public void saveImage(BufferedImage image, File imageFile) throws IOException {
        if(image==null){
            throw new IllegalArgumentException("Image is null");
        }

        if(imageFile==null){
            throw new IllegalArgumentException("ImageFile is null");
        }

        if(imageFile.exists()){
            throw new IOException("File already exists");
        }

        File parent = imageFile.getParentFile();
        if (parent != null && !parent.exists()) {
            throw new IOException("Parent directory does not exist for file: " + imageFile.getPath());
        }

        String name = imageFile.getName().toLowerCase();
        String format;

        if (name.endsWith(".png")) {
            format = "png";
        } else if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            format = "jpg";
        } else if (name.endsWith(".bmp")) {
            format = "bmp";
        } else {
            throw new IOException("Unsupported image format: " + name);
        }

        try{
            ImageIO.write(image, format, imageFile);
        } catch (IOException e) {
            throw new IOException("An error occurred while saving the image to file: " + imageFile.getPath(), e);
        }
    }
}
