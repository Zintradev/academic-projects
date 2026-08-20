package imagelibrary.util;

import org.apache.commons.imaging.*;
import org.apache.commons.imaging.formats.jpeg.exif.ExifRewriter;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputDirectory;
import org.apache.commons.imaging.formats.tiff.write.TiffOutputSet;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for reading and writing image metadata (EXIF).
 * Supports updating creation dates, dimensions, and descriptions in image files.
 */
public class MetadataEditor {

    /**
     * Updates image metadata including capture date and optional dimensions.
     * @param original The source image file
     * @param destination The destination file for the modified image
     * @param captureDate The new capture date/time to set
     * @param width The image width to set (ignored if <= 0)
     * @param height The image height to set (ignored if <= 0)
     * @throws Exception if there's an error reading or writing the image
     */
    public static void updateMetadata(File original, File destination, LocalDateTime captureDate, 
                                    int width, int height) throws Exception {
        TiffOutputSet outputSet = new TiffOutputSet();

        String exifDate = captureDate.format(DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss"));
        outputSet.getOrCreateExifDirectory().add(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL, exifDate);

        if (width > 0) {
            outputSet.getOrCreateExifDirectory().add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_WIDTH, (short) width);
        }

        if (height > 0) {
            outputSet.getOrCreateExifDirectory().add(ExifTagConstants.EXIF_TAG_EXIF_IMAGE_LENGTH, (short) height);
        }

        byte[] imageBytes = Files.readAllBytes(original.toPath());

        try (FileOutputStream fos = new FileOutputStream(destination)) {
            new ExifRewriter().updateExifMetadataLossless(imageBytes, fos, outputSet);
        }
    }

    /**
     * Reads the EXIF description from an image file.
     * @param imageFile The image file to read
     * @return The description text or empty string if none exists
     * @throws IOException if there's an error reading the file
     * @throws ImageReadException if there's an error parsing the metadata
     */
    public static String readDescription(File imageFile) throws IOException, ImageReadException {
        TiffImageMetadata exif = null;
        try {
            exif = (TiffImageMetadata) Imaging.getMetadata(imageFile);
        } catch (ClassCastException e) {
            System.err.println("No EXIF metadata present");
        }
        
        if (exif != null) {
            TiffField descriptionField = exif.findField(ExifTagConstants.EXIF_TAG_DEVICE_SETTING_DESCRIPTION);
            if (descriptionField != null) {
                return descriptionField.getStringValue();
            }
        }
        return "";
    }

    /**
     * Writes a description to an image file EXIF metadata.
     * @param originalFile The source image file
     * @param destinationFile The destination file for the modified image
     * @param description The description text to write (as bytes)
     * @throws IOException if there's an error reading or writing the files
     * @throws ImageReadException if there's an error reading the metadata
     * @throws ImageWriteException if there's an error writing the metadata
     */
    public static void writeDescription(File originalFile, File destinationFile, byte[] description)
            throws IOException, ImageReadException, ImageWriteException {
        TiffImageMetadata exif = null;
        try {
            exif = (TiffImageMetadata) Imaging.getMetadata(originalFile);
        } catch (ClassCastException e) {
        	System.err.println("No EXIF metadata present");
        }

        TiffOutputSet outputSet = (exif != null) ? exif.getOutputSet() : new TiffOutputSet();
        TiffOutputDirectory exifDirectory = outputSet.getOrCreateExifDirectory();

        exifDirectory.removeField(ExifTagConstants.EXIF_TAG_DEVICE_SETTING_DESCRIPTION);

        exifDirectory.add(ExifTagConstants.EXIF_TAG_DEVICE_SETTING_DESCRIPTION, description);

        try (OutputStream os = new FileOutputStream(destinationFile);
             InputStream is = new FileInputStream(originalFile)) {
            new ExifRewriter().updateExifMetadataLossless(is, os, outputSet);
        }
    }
}