package imagelibrary.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

/**
 * Main application window for the Image Library system.
 * Manages the primary UI components including:
 * - Folder explorer panel
 * - Image table panel
 * - Image preview panel
 * Handles file operations, description persistence, and component coordination.
 */
public class MainWindow extends JFrame {
    private static final File DESCRIPTIONS_FILE = new File("image_descriptions.dat");
    private ImageTablePanel imageTablePanel;
    private FolderExplorerPanel folderExplorerPanel;
    private ImagePreviewPanel imagePreviewPanel;

    /**
     * Constructs and displays the main application window.
     * Initializes UI components and loads saved image descriptions.
     */
    public MainWindow() {
        configureWindow();
        initUI(); 
        loadDescriptionsOnStart(); 
        setVisible(true);
    }
    
    /**
     * Configures window properties and behavior.
     * Sets look and feel, title, size, and close operation.
     */
    private void configureWindow() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Image Library");
        setSize(1000, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	saveDescriptionsOnExit();
                dispose();
                System.exit(0);
            }
        });
    }
    
    /**
     * Initializes and arranges the main UI components.
     * Creates split panes for the folder explorer, image table and preview panels.
     */
    private void initUI() {
        setLayout(new BorderLayout());

        imageTablePanel = new ImageTablePanel();
        folderExplorerPanel = new FolderExplorerPanel();
        imagePreviewPanel = new ImagePreviewPanel();

        setupComponentCommunication();

        JSplitPane leftSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, 
                folderExplorerPanel, imageTablePanel);
        leftSplitPane.setDividerLocation(250);
        leftSplitPane.setResizeWeight(0.8);

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, 
                leftSplitPane, imagePreviewPanel);
        mainSplitPane.setResizeWeight(0.9);
        mainSplitPane.setContinuousLayout(true);

        add(mainSplitPane, BorderLayout.CENTER);
        createMenuBar();
    }
    
    /**
     * Loads image descriptions from disk when application starts.
     */
    private void loadDescriptionsOnStart() {
        try {
            imagePreviewPanel.loadDescriptionsFromDisk(DESCRIPTIONS_FILE);
        } catch (Exception e) {
            System.err.println("Error loading descriptions: " + e.getMessage());
        }
    }

    /**
     * Saves image descriptions to disk when application closes.
     */
    private void saveDescriptionsOnExit() {
        try {
            imagePreviewPanel.saveDescriptionsToDisk(DESCRIPTIONS_FILE);
        } catch (IOException ex) {
            System.err.println("Error saving descriptions: " + ex.getMessage());
        }
    }
    

    /**
     * Establishes communication between main components.
     * Sets up listeners for folder selection, image selection and modification events.
     */
    private void setupComponentCommunication() {
        folderExplorerPanel.setSelectionListener(new FolderExplorerPanel.SelectionListener() {
            @Override
            public void onFolderSelected(File folder) {
                imageTablePanel.updateWithFolder(folder);
            }

            @Override
            public void onImageSelected(File imageFile) {
                imageTablePanel.updateWithFolder(imageFile.getParentFile());
                imageTablePanel.selectImage(imageFile.getName());
                imagePreviewPanel.displayImage(imageFile);
            }
        });

        imageTablePanel.setImageSelectionListener(new ImageTablePanel.ImageSelectionListener() {
            @Override
            public void onImageSelected(File imageFile) {
                imagePreviewPanel.displayImage(imageFile);
            }
            
            @Override
            public void onImageRenamed(File oldFile, File newFile) {
                folderExplorerPanel.updateImageName(oldFile, newFile);
            }
        });
        
        imagePreviewPanel.setImageModifiedListener(modifiedImage -> {
            imageTablePanel.refreshCurrentImage(modifiedImage);
            imagePreviewPanel.displayImage(modifiedImage);
        });
    }

    /**
     * Creates the application menu bar with all menu items.
     * Includes file operations and view options.
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem loadFolderItem = new JMenuItem("Load Folder");
        loadFolderItem.addActionListener(e -> folderExplorerPanel.loadImages());
        
        JMenuItem createFolderItem = new JMenuItem("Create Folder");
        createFolderItem.addActionListener(e -> folderExplorerPanel.createFolder());
        
        JMenuItem createImageItem = new JMenuItem("Create Image");
        createImageItem.addActionListener(e -> folderExplorerPanel.createImage());
        
        JMenuItem analyzeImagesItem = new JMenuItem("Analyze Folder");
        analyzeImagesItem.addActionListener(e -> imageTablePanel.analyzeImages());
        
        JMenuItem applyFiltersItem = new JMenuItem("Apply Filters");
        applyFiltersItem.addActionListener(e -> imageTablePanel.applyFiltersDialog());
        
        fileMenu.add(loadFolderItem);
        fileMenu.add(createFolderItem);
        fileMenu.add(createImageItem);
        fileMenu.add(analyzeImagesItem);
        fileMenu.add(applyFiltersItem);
        
        JMenu viewMenu = new JMenu("View");
        JMenuItem toggleViewItem = new JMenuItem("Show Folders Only");
        toggleViewItem.addActionListener(e -> {
            folderExplorerPanel.toggleFolderView();
            toggleViewItem.setText(folderExplorerPanel.isShowFoldersOnly() ? 
                "Show All" : "Show Folders Only");
        });
        viewMenu.add(toggleViewItem);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu); 
        
        setJMenuBar(menuBar);
    }

    /**
     * Application entry point.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow());
    }
}