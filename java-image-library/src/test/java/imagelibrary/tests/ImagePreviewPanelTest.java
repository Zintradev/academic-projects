package imagelibrary.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.awt.image.BufferedImage;
import java.io.File;
import org.junit.jupiter.api.Test;
import imagelibrary.ui.ImagePreviewPanel;

/**
 * Unit tests for the ImagePreviewPanel class.
 */
public class ImagePreviewPanelTest {

    /**
     * Creates a test panel with default settings.
     * @return Configured ImagePreviewPanel for testing
     */
    private ImagePreviewPanel createTestPanel() {
        ImagePreviewPanel panel = new ImagePreviewPanel();
        panel.setOriginalImage(new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB));
        panel.setCurrentZoom(1.0);
        return panel;
    }

    /**
     * Tests zoom operations (zoom in/out functionality).
     */
    @Test
    public void testZoomOperations() {
        ImagePreviewPanel panel = createTestPanel();
        panel.setCurrentZoom(panel.getCurrentZoom() * 1.25);
        assertEquals(1.25, panel.getCurrentZoom(), 0.01);
    }

    /**
     * Tests the image description saving functionality.
     */
    @Test
    public void testSaveDescription() {
        ImagePreviewPanel panel = new ImagePreviewPanel();
        File testFile = new File("test_image.jpg");

        panel.setCurrentImage(testFile);
        panel.getDescriptionArea().setText("Test description");
        panel.saveDescription();

        assertEquals("Test description", 
                   panel.getImageDescriptions().get(testFile.getAbsolutePath()));
        
        panel.setCurrentImage(null);
        panel.saveDescription();
    }
}