package imagelibrary.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

/**
 * Utility class for creating images with random geometric shapes.
 * Supports multiple image formats and generates images with configurable dimensions.
 */
public class ImageCreator {

    /**
     * Supported image output formats.
     */
    public enum Format { PNG, JPEG, BMP }
    
    /**
     * Creates an image with random geometric shapes and saves it to the specified file.
     * @param outputFile The destination file for the generated image
     * @param width The width of the generated image in pixels
     * @param height The height of the generated image in pixels
     * @param format The output image format (PNG, JPEG, or BMP)
     * @param shapeCount The number of random shapes to generate (3-6)
     * @throws IllegalArgumentException if parameters are invalid
     */
    public static void createRandomImage(File outputFile, int width, int height, Format format, int shapeCount) {
        if (outputFile == null || width <= 0 || height <= 0 || format == null || shapeCount <= 0) {
            throw new IllegalArgumentException("Invalid parameters");
        }

        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        Random rand = new Random();

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        int shapeQuantity = Math.min(3 + rand.nextInt(4), shapeCount);

        for (int i = 0; i < shapeQuantity; i++) {
            drawRandomShape(g, rand, width, height);
        }

        g.dispose();

        saveImage(img, outputFile, format);
    }

    /**
     * Draws a random geometric shape on the graphics context.
     * @param g The graphics context to draw on
     * @param rand Random number generator for shape properties
     * @param maxWidth Maximum x-boundary for shape positioning
     * @param maxHeight Maximum y-boundary for shape positioning
     */
    private static void drawRandomShape(Graphics2D g, Random rand, int maxWidth, int maxHeight) {
        g.setColor(new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)));

        int x = rand.nextInt(maxWidth - 50);
        int y = rand.nextInt(maxHeight - 50);
        int width = 30 + rand.nextInt(70);
        int height = 30 + rand.nextInt(70);

        switch (rand.nextInt(3)) {
            case 0:
                g.fillRect(x, y, width, height);
                break;
            case 1:
                g.fillOval(x, y, width, height);
                break;
            case 2:
                int x2 = x + rand.nextInt(100);
                int y2 = y + rand.nextInt(100);
                g.drawLine(x, y, x2, y2);
                break;
        }
    }

    /**
     * Saves the generated image to a file in the specified format.
     * @param image The image to save
     * @param outputFile The destination file
     * @param format The output image format
     */
    private static void saveImage(BufferedImage image, File outputFile, Format format) {
        try {
            ImageIO.write(image, format.name().toLowerCase(), outputFile);
        } catch (IOException e) {
            System.err.println("Error saving image: " + e.getMessage());
            e.printStackTrace();
        }
    }
}