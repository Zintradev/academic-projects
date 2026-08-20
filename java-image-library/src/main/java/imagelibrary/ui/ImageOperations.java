package imagelibrary.ui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * Provides basic image manipulation operations including loading, saving and resizing.
 * Supports common image formats through Java's ImageIO capabilities.
 */
public class ImageOperations {
    
    /**
     * Loads an image from the specified file.
     * @param file The image file to load
     * @return BufferedImage containing the loaded image
     * @throws Exception If the image cannot be read
     */
    public static BufferedImage loadImage(File file) throws Exception {
        return ImageIO.read(file);
    }

    /**
     * Saves an image to the specified file in the given format.
     * @param image The image to save
     * @param file The destination file
     * @param format The image format (e.g., "JPEG", "PNG")
     * @return true if the image was successfully saved
     * @throws Exception If there's an error writing the image
     */
    public static boolean saveImage(BufferedImage image, File file, String format) throws Exception {
        return ImageIO.write(image, format, file);
    }

    /**
     * Resizes an image to the specified dimensions while maintaining aspect ratio.
     * @param original The original image to resize
     * @param newWidth The target width in pixels
     * @param newHeight The target height in pixels
     * @return A new BufferedImage containing the resized image
     */
    public static BufferedImage resizeImage(BufferedImage original, int newWidth, int newHeight) {
        BufferedImage resized = new BufferedImage(newWidth, newHeight, original.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return resized;
    }
}