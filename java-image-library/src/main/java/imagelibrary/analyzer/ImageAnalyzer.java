package imagelibrary.analyzer;

import imagelibrary.model.ImageInfo;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for analyzing image files.
 * Supported formats: PNG, JPG, JPEG, GIF, and BMP.
 */
public class ImageAnalyzer {

    private static final String[] SUPPORTED_FORMATS = { "png", "jpg", "jpeg", "gif", "bmp" };

    /**
     * Analyzes all image files in a folder and returns a list of ImageInfo objects.
     * @param folder Directory to analyze
     * @return List of ImageInfo objects for the found images
     */
    public static List<ImageInfo> analyzeFolder(File folder) {
        List<ImageInfo> images = new ArrayList<>();

        if (folder == null || !folder.exists() || !folder.isDirectory()) {
            return images;
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return images;
        }

        for (File file : files) {
            if (file.isFile() && isImage(file)) {
                try {
                    BufferedImage img = ImageIO.read(file);
                    if (img != null) {
                        ImageInfo info = new ImageInfo(file, img.getWidth(), img.getHeight());
                        images.add(info);
                    }
                } catch (IOException e) {
                    System.err.println("Error reading image: " + file.getName());
                }
            }
        }
        return images;
    }

    /**
     * Analyzes a single file and returns its data as an ImageInfo object.
     * @param file Image file to analyze
     * @return ImageInfo object with image data, or null if invalid
     */
    public static ImageInfo analyzeFile(File file) {
        if (file.isFile() && isImage(file)) {
            try {
                BufferedImage img = ImageIO.read(file);
                if (img != null) {
                    return new ImageInfo(file, img.getWidth(), img.getHeight());
                }
            } catch (IOException e) {
                System.err.println("Error reading image: " + file.getName());
            }
        }
        return null;
    }

    /**
     * Checks if a file has a supported image extension.
     * @param file File to verify
     * @return true if the file is a supported image, false otherwise
     */
    private static boolean isImage(File file) {
        String name = file.getName().toLowerCase();
        for (String format : SUPPORTED_FORMATS) {
            if (name.endsWith("." + format)) {
                return true;
            }
        }
        return false;
    }
}