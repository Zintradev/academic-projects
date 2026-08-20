package imagelibrary.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * A simple painting dialog that allows users to draw images.
 * Provides basic drawing tools including color selection and brush size adjustment.
 * The created image can be retrieved after the dialog closes.
 */
public class SimplePaintDialog extends JDialog {
    private BufferedImage canvas;
    private Point lastPoint = null;
    private boolean saved = false;
    private Color brushColor = Color.BLACK;
    private int brushSize = 2;

    /**
     * Creates a new painting dialog.
     * @param owner The parent window
     * @param width The width of the drawing canvas
     * @param height The height of the drawing canvas
     */
    public SimplePaintDialog(Window owner, int width, int height) {
        super(owner, "Paint - Draw your image", ModalityType.APPLICATION_MODAL);
        setSize(width, height);
        setLocationRelativeTo(owner);
        initializeCanvas(width, height);
        setupUI();
    }

    /**
     * Initializes the drawing canvas with a white background.
     * @param width The width of the canvas
     * @param height The height of the canvas
     */
    private void initializeCanvas(int width, int height) {
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);
        g2d.dispose();
    }

    /**
     * Sets up the user interface components including:
     * - Drawing panel
     * - Color selection controls
     * - Brush size controls
     * - Action buttons
     */
    private void setupUI() {
        JPanel paintPanel = createPaintPanel();
        JPanel controlsPanel = createControlsPanel();
        JPanel buttonsPanel = createButtonsPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(controlsPanel, BorderLayout.NORTH);
        getContentPane().add(paintPanel, BorderLayout.CENTER);
        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
        pack();
    }

    /**
     * Creates the main drawing panel with mouse listeners for painting.
     * @return The configured paint panel
     */
    private JPanel createPaintPanel() {
        JPanel paintPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(canvas, 0, 0, null);
            }
        };
        paintPanel.setPreferredSize(new Dimension(getWidth(), getHeight()));

        paintPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastPoint = e.getPoint();
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                lastPoint = null;
            }
        });

        paintPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (lastPoint != null) {
                    Graphics2D g2d = canvas.createGraphics();
                    g2d.setColor(brushColor);
                    g2d.setStroke(new BasicStroke(brushSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.drawLine(lastPoint.x, lastPoint.y, e.getX(), e.getY());
                    g2d.dispose();
                    lastPoint = e.getPoint();
                    paintPanel.repaint();
                }
            }
        });

        return paintPanel;
    }

    /**
     * Creates the controls panel with color picker and brush size selector.
     * @return The configured controls panel
     */
    private JPanel createControlsPanel() {
        JPanel controlsPanel = new JPanel();
        
        JButton btnColor = new JButton("Color");
        btnColor.setBackground(brushColor);
        btnColor.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(this, "Choose brush color", brushColor);
            if (selectedColor != null) {
                brushColor = selectedColor;
                btnColor.setBackground(brushColor);
            }
        });

        Integer[] sizes = {1, 2, 4, 8, 12, 16};
        JComboBox<Integer> sizeCombo = new JComboBox<>(sizes);
        sizeCombo.setSelectedItem(brushSize);
        sizeCombo.addActionListener(e -> {
            brushSize = (Integer) sizeCombo.getSelectedItem();
        });

        controlsPanel.add(new JLabel("Brush:"));
        controlsPanel.add(btnColor);
        controlsPanel.add(new JLabel("Size:"));
        controlsPanel.add(sizeCombo);

        return controlsPanel;
    }

    /**
     * Creates the buttons panel with save and cancel options.
     * @return The configured buttons panel
     */
    private JPanel createButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        
        JButton btnSave = new JButton("Save and Finish");
        btnSave.addActionListener(e -> {
            saved = true;
            dispose();
        });

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(e -> {
            saved = false;
            dispose();
        });

        buttonsPanel.add(btnSave);
        buttonsPanel.add(btnCancel);

        return buttonsPanel;
    }

    /**
     * Gets the drawn image.
     * @return The BufferedImage containing the drawing
     */
    public BufferedImage getImage() {
        return canvas;
    }

    /**
     * Checks if the drawing was saved.
     * @return true if the user clicked save, false if canceled
     */
    public boolean isSaved() {
        return saved;
    }
}