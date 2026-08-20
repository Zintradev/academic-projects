package imagelibrary.model;

import java.io.File;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

/**
 * Class representing image data and basic file information.
 * Includes EXIF metadata when available.
 */
public class ImageInfo {

    private String name;
    private Path path;
    private int width;
    private int height;
    private LocalDateTime modificationDate;
    private long sizeBytes;
    private LocalDateTime captureDate;
    private String cameraModel;
    private String gpsCoordinates;

    /**
     * Creates an ImageInfo object with basic and EXIF metadata from an image file.
     * @param file Image file to analyze
     * @param width Image width in pixels
     * @param height Image height in pixels
     */
    public ImageInfo(File file, int width, int height) {
        this.name = file.getName();
        this.path = file.toPath();
        this.width = width;
        this.height = height;
        this.modificationDate = LocalDateTime.ofInstant(
            new Date(file.lastModified()).toInstant(), 
            ZoneId.systemDefault()
        );
        this.sizeBytes = file.length();
        try {
            final ImageMetadata metadata = Imaging.getMetadata(file);
            
            if (metadata instanceof JpegImageMetadata) {
                JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;
                TiffImageMetadata exif = jpegMetadata.getExif();

                TiffField dateField = exif.findField(TiffTagConstants.TIFF_TAG_DATE_TIME);
                if (dateField != null) {
                    String dateStr = dateField.getStringValue();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
                    this.captureDate = LocalDateTime.parse(dateStr, formatter);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading metadata of " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Returns the name of the image file.
     * @return The filename including extension
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the path to the image file.
     * @return Path object representing the file location
     */
    public Path getPath() {
        return path;
    }

    /**
     * Returns the image file object.
     * @return File object representing the image
     */
    public File getFile() {
        return path.toFile();
    }

    /**
     * Returns the width of the image in pixels.
     * @return Image width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image in pixels.
     * @return Image height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the last modification date of the file.
     * @return LocalDateTime representing when the file was last modified
     */
    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    /**
     * Returns the file size in bytes.
     * @return Size of the file in bytes
     */
    public long getSizeBytes() {
        return sizeBytes;
    }

    /**
     * Returns formatted size
     * @return Human-readable formatted file size
     */
    public String getFormattedSize() {
        if (sizeBytes < 1024) return sizeBytes + " B";
        int exp = (int) (Math.log(sizeBytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", sizeBytes / Math.pow(1024, exp), pre);
    }
}