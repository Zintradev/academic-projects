package imagelibrary.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * Panel for previewing and editing images with various operations including:
 * - Zooming in/out
 * - Cropping
 * - Resizing
 * - Adjusting brightness, contrast and saturation
 * - Adding descriptions
 * - Saving modified images
 */
public class ImagePreviewPanel extends JPanel {
	
	/**
	 * Custom panel that displays the current image with zoom support.
	 * Shows a placeholder when no image is loaded and handles crop selection visualization.
	 */
    private JPanel imagePreviewPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            
            if (originalImage != null) {
                Image scaledImage = originalImage.getScaledInstance(
                    (int)(originalImage.getWidth() * currentZoom),
                    (int)(originalImage.getHeight() * currentZoom),
                    Image.SCALE_SMOOTH
                );
                g.drawImage(scaledImage, 0, 0, this);
            } else {
                g.setColor(Color.GRAY);
                g.drawString("Preview", getWidth()/2 - 30, getHeight()/2);
            }
            
            if (isCropping && cropRectangle != null) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setColor(new Color(0, 100, 255, 50));
                g2d.fill(cropRectangle);
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(2));
                g2d.draw(cropRectangle);
                g2d.dispose();
            }
        }
    };
    
    private BufferedImage originalImage;
    private double currentZoom = 1.0;
    private JPanel specificationsPanel;
    private JTextArea descriptionArea;
    private JButton saveDescriptionButton;
    private Map<String, String> imageDescriptions = new HashMap<>();
    private File currentImage;
    private Point cropStartPoint;
    private Rectangle cropRectangle;
    private boolean isCropping = false;
    private ImageModifiedListener imageModifiedListener;

    /**
     * Initializes the preview panel with all UI components.
     * Includes:
     * - Image display area
     * - Zoom controls
     * - Editing tools
     * - Image specifications
     * - Description field
     */
    public ImagePreviewPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        imagePreviewPanel.setBackground(new Color(250, 250, 250));
        imagePreviewPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane previewScroll = new JScrollPane(imagePreviewPanel);
        previewScroll.setBorder(null);
        previewScroll.getViewport().setBackground(Color.WHITE);
        
        specificationsPanel = new JPanel();
        specificationsPanel.setLayout(new BoxLayout(specificationsPanel, BoxLayout.Y_AXIS));
        specificationsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Specifications"),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setBorder(BorderFactory.createTitledBorder("Description"));

        saveDescriptionButton = new JButton("Save Description");
        saveDescriptionButton.addActionListener(e -> saveDescription());
        saveDescriptionButton.setBackground(new Color(76, 175, 80));
        saveDescriptionButton.setForeground(Color.WHITE);
        saveDescriptionButton.setFocusPainted(false);
        saveDescriptionButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        saveDescriptionButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        addZoomButtons(controlsPanel);
        addEditButtons(controlsPanel);
        
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        descriptionPanel.add(saveDescriptionButton, BorderLayout.SOUTH);
        
        add(previewScroll, BorderLayout.CENTER);
        add(controlsPanel, BorderLayout.NORTH);
        add(specificationsPanel, BorderLayout.SOUTH);
        add(descriptionPanel, BorderLayout.SOUTH);
    }

    /**
     * Displays an image in the preview panel.
     * @param file The image file to display (can be null to clear)
     */
    public void displayImage(File file) {
        cancelCropping();
        originalImage = null;
        currentImage = file;

        if (file == null || !file.exists()) {
            imagePreviewPanel.repaint();
            return;
        }

        new SwingWorker<BufferedImage, Void>() {
            @Override
            protected BufferedImage doInBackground() throws Exception {
                return ImageIO.read(file);
            }

            @Override
            protected void done() {
                try {
                    originalImage = get();
                    calculateInitialZoom();
                    loadDescriptionForImage(file);
                    imagePreviewPanel.repaint();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ImagePreviewPanel.this, 
                        "Error loading image: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    /**
     * Cancels any active cropping operation.
     */
    private void cancelCropping() {
        isCropping = false;
        cropRectangle = null;
        cropStartPoint = null;
        imagePreviewPanel.setCursor(Cursor.getDefaultCursor());
        
        MouseListener[] mouseListeners = imagePreviewPanel.getMouseListeners();
        for (MouseListener ml : mouseListeners) {
            imagePreviewPanel.removeMouseListener(ml);
        }
        
        MouseMotionListener[] motionListeners = imagePreviewPanel.getMouseMotionListeners();
        for (MouseMotionListener mml : motionListeners) {
            imagePreviewPanel.removeMouseMotionListener(mml);
        }
        
        imagePreviewPanel.repaint();
    }
    
    /**
     * Applies the current zoom factor to the image.
     */
    public void applyZoom() {
        if (originalImage == null) return;
        imagePreviewPanel.repaint();
    }

    /**
     * Formats file size into human-readable string.
     * @param file The file to check size of
     * @return Formatted size string (KB or MB)
     */
    private String getFileSize(File file) {
        long sizeInBytes = file.length();
        double sizeInKB = sizeInBytes / 1024.0;
        double sizeInMB = sizeInKB / 1024.0;
        if (sizeInMB > 1) {
            return String.format("%.2f MB", sizeInMB);
        } else {
            return String.format("%.2f KB", sizeInKB);
        }
    }

    /**
     * Saves the current image description.
     */
    public void saveDescription() {
        if (currentImage != null) {
            String desc = descriptionArea.getText().trim();
            imageDescriptions.put(currentImage.getAbsolutePath(), desc);
            if (!GraphicsEnvironment.isHeadless()) {
                JOptionPane.showMessageDialog(this, "Description saved.");
            }
        }
    }

    /**
     * Loads description for the current image.
     * @param imageFile The image file to load description for
     */
    public void loadDescriptionForImage(File imageFile) {
        String desc = imageDescriptions.get(imageFile.getAbsolutePath());
        descriptionArea.setText(desc != null ? desc : "");
    }

    /**
     * Adds zoom control buttons to a panel.
     * @param panel The panel to add buttons to
     */
    private void addZoomButtons(JPanel panel) {
        JButton btnZoomIn = new JButton("Zoom In (+)");
        JButton btnZoomOut = new JButton("Zoom Out (-)");
        
        btnZoomIn.addActionListener(e -> {
            currentZoom *= 1.25;
            applyZoom();
        });
        
        btnZoomOut.addActionListener(e -> {
            currentZoom *= 0.8;
            applyZoom();
        });
        
        btnZoomIn.setBackground(new Color(63, 81, 181));
        btnZoomIn.setForeground(Color.WHITE);
        btnZoomOut.setBackground(new Color(63, 81, 181));
        btnZoomOut.setForeground(Color.WHITE);
        btnZoomIn.setFocusPainted(false);
        btnZoomOut.setFocusPainted(false);
        
        panel.add(btnZoomIn);
        panel.add(btnZoomOut);
    }

    /**
     * Adds image editing buttons to a panel.
     * @param panel The panel to add buttons to
     */
    private void addEditButtons(JPanel panel) {
        JButton btnCrop = new JButton("Crop");
        JButton btnResize = new JButton("Resize");
        JButton btnAdjust = new JButton("Adjustments");
        JButton btnSave = new JButton("Save");
        
        btnCrop.addActionListener(e -> startCropping());
        btnResize.addActionListener(e -> showResizeDialog());
        btnAdjust.addActionListener(e -> showAdjustmentDialog());
        btnSave.addActionListener(e -> saveImageChanges());
        
        btnCrop.setBackground(new Color(156, 39, 176));
        btnResize.setBackground(new Color(156, 39, 176));
        btnAdjust.setBackground(new Color(156, 39, 176));
        btnSave.setBackground(new Color(46, 125, 50));
        
        for (JButton btn : new JButton[]{btnCrop, btnResize, btnAdjust, btnSave}) {
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
        }
        
        panel.add(btnCrop);
        panel.add(btnResize);
        panel.add(btnAdjust);
        panel.add(btnSave);
    }

    /**
     * Starts image cropping mode.
     */
    private void startCropping() {
        if (originalImage == null) {
            JOptionPane.showMessageDialog(this, "No image selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        isCropping = true;
        cropRectangle = null;
        cropStartPoint = null;
        
        imagePreviewPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        
        MouseAdapter cropAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                cropStartPoint = e.getPoint();
                cropRectangle = new Rectangle(cropStartPoint.x, cropStartPoint.y, 0, 0);
                imagePreviewPanel.repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (cropStartPoint != null) {
                    int width = e.getX() - cropStartPoint.x;
                    int height = e.getY() - cropStartPoint.y;
                    
                    cropRectangle = new Rectangle(
                        Math.min(cropStartPoint.x, e.getX()),
                        Math.min(cropStartPoint.y, e.getY()),
                        Math.abs(width),
                        Math.abs(height)
                    );
                    imagePreviewPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (cropRectangle != null && cropRectangle.width > 10 && cropRectangle.height > 10) {
                    applyCrop();
                }
                cleanupCropping();
            }
        };

        imagePreviewPanel.addMouseListener(cropAdapter);
        imagePreviewPanel.addMouseMotionListener(cropAdapter);
        
        JOptionPane.showMessageDialog(this, 
            "Drag to select the area to crop", 
            "Crop Mode", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Cleans up after cropping operation.
     */
    private void cleanupCropping() {
        isCropping = false;
        imagePreviewPanel.setCursor(Cursor.getDefaultCursor());
        
        MouseListener[] mouseListeners = imagePreviewPanel.getMouseListeners();
        for (MouseListener ml : mouseListeners) {
            imagePreviewPanel.removeMouseListener(ml);
        }
        
        MouseMotionListener[] motionListeners = imagePreviewPanel.getMouseMotionListeners();
        for (MouseMotionListener mml : motionListeners) {
            imagePreviewPanel.removeMouseMotionListener(mml);
        }
        
        imagePreviewPanel.repaint();
    }

    /**
     * Applies the crop to the current image.
     */
    private void applyCrop() {
        if (cropRectangle == null || originalImage == null) return;
        
        try {
            double scale = currentZoom;
            int x = (int)(cropRectangle.x / scale);
            int y = (int)(cropRectangle.y / scale);
            int width = (int)(cropRectangle.width / scale);
            int height = (int)(cropRectangle.height / scale);
            
            x = Math.max(0, Math.min(x, originalImage.getWidth()));
            y = Math.max(0, Math.min(y, originalImage.getHeight()));
            width = Math.min(width, originalImage.getWidth() - x);
            height = Math.min(height, originalImage.getHeight() - y);
            
            if (width <= 0 || height <= 0) return;
            
            BufferedImage cropped = new BufferedImage(width, height, originalImage.getType());
            Graphics2D g = cropped.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, x, y, x + width, y + height, null);
            g.dispose();
            
            originalImage = cropped;
            currentZoom = 1.0;
            
            applyZoom();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error cropping image: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            cropRectangle = null;
            cropStartPoint = null;
        }
    }

    /**
     * Shows dialog for resizing the image.
     */
    private void showResizeDialog() {
        if (originalImage == null || currentImage == null) {
            JOptionPane.showMessageDialog(this, "No image selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Width:"));
        JTextField widthField = new JTextField(String.valueOf(originalImage.getWidth()));
        panel.add(widthField);
        panel.add(new JLabel("Height:"));
        JTextField heightField = new JTextField(String.valueOf(originalImage.getHeight()));
        panel.add(heightField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Resize Image", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int newWidth = Integer.parseInt(widthField.getText());
                int newHeight = Integer.parseInt(heightField.getText());
                
                if (newWidth > 0 && newHeight > 0) {
                    BufferedImage resized = new BufferedImage(newWidth, newHeight, originalImage.getType());
                    Graphics2D g = resized.createGraphics();
                    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
                    g.dispose();
                    
                    originalImage = resized;
                    applyZoom();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter valid numerical values", 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Shows dialog for adjusting image properties.
     */
    private void showAdjustmentDialog() {
        if (originalImage == null) return;
        
        JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Image Adjustments", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel controlPanel = new JPanel(new GridLayout(3, 2));
        
        JSlider brightnessSlider = new JSlider(-100, 100, 0);
        brightnessSlider.setMajorTickSpacing(50);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        
        JSlider contrastSlider = new JSlider(-100, 100, 0);
        contrastSlider.setMajorTickSpacing(50);
        contrastSlider.setPaintTicks(true);
        contrastSlider.setPaintLabels(true);
        
        JSlider saturationSlider = new JSlider(-100, 100, 0);
        saturationSlider.setMajorTickSpacing(50);
        saturationSlider.setPaintTicks(true);
        saturationSlider.setPaintLabels(true);
        
        controlPanel.add(new JLabel("Brightness:"));
        controlPanel.add(brightnessSlider);
        controlPanel.add(new JLabel("Contrast:"));
        controlPanel.add(contrastSlider);
        controlPanel.add(new JLabel("Saturation:"));
        controlPanel.add(saturationSlider);
        
        JLabel previewLabel = new JLabel(new ImageIcon(originalImage));
        
        JButton applyBtn = new JButton("Apply");
        JButton cancelBtn = new JButton("Cancel");
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(applyBtn);
        buttonPanel.add(cancelBtn);
        
        dialog.add(controlPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(previewLabel), BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        ChangeListener sliderListener = e -> {
            BufferedImage adjusted = applyAdjustments(originalImage, 
                    brightnessSlider.getValue(), 
                    contrastSlider.getValue(), 
                    saturationSlider.getValue());
            previewLabel.setIcon(new ImageIcon(adjusted));
        };
        
        brightnessSlider.addChangeListener(sliderListener);
        contrastSlider.addChangeListener(sliderListener);
        saturationSlider.addChangeListener(sliderListener);
        
        applyBtn.addActionListener(e -> {
            originalImage = applyAdjustments(originalImage, 
                    brightnessSlider.getValue(), 
                    contrastSlider.getValue(), 
                    saturationSlider.getValue());
            applyZoom();
            dialog.dispose();
        });
        
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Applies brightness, contrast and saturation adjustments to an image.
     * @param original The original image
     * @param brightness Brightness adjustment (-100 to 100)
     * @param contrast Contrast adjustment (-100 to 100)
     * @param saturation Saturation adjustment (-100 to 100)
     * @return The adjusted image
     */
    private BufferedImage applyAdjustments(BufferedImage original, int brightness, int contrast, int saturation) {
        BufferedImage adjusted = new BufferedImage(
                original.getWidth(), original.getHeight(), original.getType());
        
        for (int y = 0; y < original.getHeight(); y++) {
            for (int x = 0; x < original.getWidth(); x++) {
                Color color = new Color(original.getRGB(x, y));
                
                int r = clamp(color.getRed() + brightness);
                int g = clamp(color.getGreen() + brightness);
                int b = clamp(color.getBlue() + brightness);
                
                float contrastFactor = (259f * (contrast + 255f)) / (255f * (259f - contrast));
                r = clamp((int)(contrastFactor * (r - 128) + 128));
                g = clamp((int)(contrastFactor * (g - 128) + 128));
                b = clamp((int)(contrastFactor * (b - 128) + 128));
                
                if (saturation != 0) {
                    float gray = (r + g + b) / 3f;
                    r = clamp((int)(gray + (r - gray) * (1 + saturation / 100f)));
                    g = clamp((int)(gray + (g - gray) * (1 + saturation / 100f)));
                    b = clamp((int)(gray + (b - gray) * (1 + saturation / 100f)));
                }
                
                adjusted.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }
        
        return adjusted;
    }
    
    /**
     * Calculates initial zoom to fit image in panel.
     */
    public void calculateInitialZoom() {
        int containerWidth = imagePreviewPanel.getParent() != null ? 
            imagePreviewPanel.getParent().getWidth() : 500;
        int containerHeight = imagePreviewPanel.getParent() != null ? 
            imagePreviewPanel.getParent().getHeight() : 400;
        
        double scaleX = (double) containerWidth / originalImage.getWidth();
        double scaleY = (double) containerHeight / originalImage.getHeight();
        currentZoom = Math.min(scaleX, scaleY);
    }

    /**
     * Clamps a value to 0-255 range.
     * @param value The value to clamp
     * @return The clamped value
     */
    private int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    /**
     * Saves changes made to the current image.
     */
    public void saveImageChanges() {
        if (currentImage == null || originalImage == null) {
            JOptionPane.showMessageDialog(this, "No image selected", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
                "Save changes to original image?", 
                "Save Changes", 
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            try {
                String format = currentImage.getName().toLowerCase().endsWith(".png") ? "png" : "jpg";
                ImageIO.write(originalImage, format, currentImage);
                
                if (imageModifiedListener != null) {
                    imageModifiedListener.onImageModified(currentImage);
                }
                
                JOptionPane.showMessageDialog(this, "Changes saved successfully");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error saving image: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Listener interface for image modification events.
     */
    public interface ImageModifiedListener {
        /**
         * Called when an image is modified.
         * @param modifiedImage The modified image file
         */
        void onImageModified(File modifiedImage);
    }

    /**
     * Sets the image modified listener.
     * @param listener The listener to notify of changes
     */
    public void setImageModifiedListener(ImageModifiedListener listener) {
        this.imageModifiedListener = listener;
    }
    
    /**
     * Saves image descriptions to a file.
     * @param file The file to save to
     * @throws IOException If there's an error writing the file
     */
    public void saveDescriptionsToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(imageDescriptions);
        }
    }

    /**
     * Loads image descriptions from a file.
     * @param file The file to load from
     * @throws IOException If there's an error reading the file
     * @throws ClassNotFoundException If the serialized data is invalid
     */
    public void loadDescriptionsFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            imageDescriptions = (Map<String, String>) ois.readObject();
        }
    }
    /**
     * Saves descriptions to disk (alias for saveDescriptionsToFile).
     * @param saveFile The file to save to
     * @throws IOException If there's an error writing the file
     */
    public void saveDescriptionsToDisk(File saveFile) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveFile))) {
            oos.writeObject(imageDescriptions);
        }
    }

    /**
     * Loads descriptions from disk (alias for loadDescriptionsFromFile).
     * @param saveFile The file to load from
     * @throws IOException If there's an error reading the file
     * @throws ClassNotFoundException If the serialized data is invalid
     */
    @SuppressWarnings("unchecked")
    public void loadDescriptionsFromDisk(File saveFile) throws IOException, ClassNotFoundException {
        if (saveFile.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFile))) {
                imageDescriptions.putAll((Map<String, String>) ois.readObject());
            }
        }
    }

    public BufferedImage getOriginalImage() {
        return originalImage;
    }

    public void setOriginalImage(BufferedImage originalImage) {
        this.originalImage = originalImage;
    }

    public double getCurrentZoom() {
        return currentZoom;
    }

    public void setCurrentZoom(double currentZoom) {
        this.currentZoom = currentZoom;
    }

    public JTextArea getDescriptionArea() {
        return descriptionArea;
    }

    public Map<String, String> getImageDescriptions() {
        return imageDescriptions;
    }

    public File getCurrentImage() {
        return currentImage;
    }

    public void setCurrentImage(File currentImage) {
        this.currentImage = currentImage;
    }
}