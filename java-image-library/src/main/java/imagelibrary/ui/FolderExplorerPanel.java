package imagelibrary.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * A panel that displays a folder and image file explorer using a tree view.
 * This component lets users browse directories, select images, and create new folders or images.
 * It shows the contents of a root folder and allows switching between viewing folders only or all files.
 * It also supports selection listeners to notify when a folder or image is selected.
 */
public class FolderExplorerPanel extends JPanel {
    private boolean showFoldersOnly = false;
    private JTree folderExplorer;
    private DefaultTreeModel treeModel;
    private final String rootDirectory = "sample_gallery";
    private File currentFolder;
    private SelectionListener selectionListener;
    private Set<String> expandedPathsSet = new HashSet<>();

    /**
     * Creates a new FolderExplorerPanel that displays a folder tree.
     * It uses a JTree to show the contents of the root directory, allowing the user
     * to navigate folders and select image files. A custom cell renderer sets the names and icons.
     * When a file or folder is selected, it notifies the selection listener if one is set.
     */
    public FolderExplorerPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Folder Explorer"));
        
        File rootFolder = new File(System.getProperty("user.dir"), rootDirectory);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(rootFolder.getName(), rootFolder.getAbsolutePath()));
        addDirectories(root, rootFolder);

        treeModel = new DefaultTreeModel(root);
        folderExplorer = new JTree(treeModel);
        
        folderExplorer.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, 
                    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    if (node.getUserObject() instanceof FileNode) {
                        FileNode fileNode = (FileNode) node.getUserObject();
                        setText(fileNode.getName());
                    }
                }
                if (leaf && ((DefaultMutableTreeNode) value).getUserObject().toString().contains(".")) {
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                }
                return this;
            }
        });

        folderExplorer.addTreeSelectionListener(e -> {
            TreePath path = e.getNewLeadSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof FileNode) {
                    FileNode fileNode = (FileNode) node.getUserObject();
                    File selectedFile = new File(fileNode.getFilePath());

                    if (selectedFile.isFile() && isImageFile(selectedFile)) {
                        currentFolder = selectedFile.getParentFile();
                        if (selectionListener != null) {
                            selectionListener.onImageSelected(selectedFile);
                        }
                    } else if (selectedFile.isDirectory()) {
                        currentFolder = selectedFile;
                        if (selectionListener != null) {
                            selectionListener.onFolderSelected(selectedFile);
                        }
                    }
                }
            }
        });

        add(new JScrollPane(folderExplorer), BorderLayout.CENTER);
    }

    /**
     * Opens a dialog to select and load an image folder.
     * @return The selected folder or null if canceled
     */
    public File loadImages() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            updateTreeWithFolder(selectedFolder);
            return selectedFolder;
        }
        return null;
    }

    /**
     * Creates a new subfolder in the currently selected folder.
     */
    public void createFolder() {
        if (currentFolder == null) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.");
            return;
        }
        
        String name = JOptionPane.showInputDialog(this, "Folder name:");
        if (name != null && !name.isEmpty()) {
            File newFolder = new File(currentFolder, name);
            if (newFolder.mkdir()) {
                DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
                Enumeration<?> e = root.depthFirstEnumeration();
                
                while (e.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                    if (node.getUserObject() instanceof FileNode) {
                        FileNode fileNode = (FileNode) node.getUserObject();
                        if (fileNode.getFilePath().equals(currentFolder.getAbsolutePath())) {
                            FileNode childNode = new FileNode(newFolder.getName(), newFolder.getAbsolutePath());
                            DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
                            treeModel.insertNodeInto(childTreeNode, node, node.getChildCount());
                            
                            TreePath parentPath = new TreePath(node.getPath());
                            if (!folderExplorer.isExpanded(parentPath)) {
                                folderExplorer.expandPath(parentPath);
                            }
                            
                            TreePath newPath = parentPath.pathByAddingChild(childTreeNode);
                            folderExplorer.setSelectionPath(newPath);
                            folderExplorer.scrollPathToVisible(newPath);
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new image in the currently selected folder.
     */
    public void createImage() {
        if (currentFolder == null) {
            JOptionPane.showMessageDialog(this, "Please select a folder first.");
            return;
        }

        SimplePaintDialog paintDialog = new SimplePaintDialog(SwingUtilities.getWindowAncestor(this), 400, 400);
        paintDialog.setVisible(true);

        if (!paintDialog.isSaved()) {
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Image name (without extension):");
        if (name == null || name.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid name. Operation canceled.");
            return;
        }

        File file = new File(currentFolder, name.trim() + ".jpg");

        try {
            ImageIO.write(paintDialog.getImage(), "jpg", file);
            
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
            Enumeration<?> e = root.depthFirstEnumeration();
            
            while (e.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
                if (node.getUserObject() instanceof FileNode) {
                    FileNode fileNode = (FileNode) node.getUserObject();
                    if (fileNode.getFilePath().equals(currentFolder.getAbsolutePath())) {
                        FileNode childNode = new FileNode(file.getName(), file.getAbsolutePath());
                        DefaultMutableTreeNode childTreeNode = new DefaultMutableTreeNode(childNode);
                        treeModel.insertNodeInto(childTreeNode, node, node.getChildCount());
                        
                        TreePath parentPath = new TreePath(node.getPath());
                        TreePath newPath = parentPath.pathByAddingChild(childTreeNode);
                        folderExplorer.setSelectionPath(newPath);
                        folderExplorer.scrollPathToVisible(newPath);
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error creating image: " + ex.getMessage());
        }
    }

    /**
     * Updates the tree structure based on a new root folder.
     * @param folder The new root folder
     */
    public void updateTreeWithFolder(File folder) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(folder.getName(), folder.getAbsolutePath()));
        addDirectories(root, folder);
        treeModel.setRoot(root);
        treeModel.reload();
    }

    /**
     * Expands the tree to a given path.
     * @param path The absolute file path to expand to
     */
    private void expandTreeToPath(String path) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration<?> e = root.depthFirstEnumeration();
        
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() instanceof FileNode) {
                FileNode fileNode = (FileNode) node.getUserObject();
                if (fileNode.getFilePath().equals(path)) {
                    TreePath treePath = new TreePath(node.getPath());
                    folderExplorer.setSelectionPath(treePath);
                    folderExplorer.scrollPathToVisible(treePath);
                    folderExplorer.expandPath(treePath);
                    fileNode.setExpanded(true);
                    break;
                }
            }
        }
    }

    /**
     * Recursively adds subdirectories and valid files to the tree node.
     * @param parentNode The node to which children should be added
     * @param directory The directory to scan
     */
    private void addDirectories(DefaultMutableTreeNode parentNode, File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            boolean containsOnlyImages = true;
            for (File file : files) {
                if (file.isDirectory()) {
                    containsOnlyImages = false;
                    break;
                }
                if (!isImageFile(file)) {
                    containsOnlyImages = false;
                    break;
                }
            }

            for (File file : files) {
                if (!showFoldersOnly || file.isDirectory() || containsOnlyImages) {
                    FileNode childNode = new FileNode(file.getName(), file.getAbsolutePath());
                    DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(childNode);
                    parentNode.add(treeNode);
                    if (file.isDirectory()) {
                        addDirectories(treeNode, file);
                        if (childNode.isExpanded()) {
                            folderExplorer.expandPath(new TreePath(treeNode.getPath()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Sets the selection listener for tree events.
     * @param listener The listener to notify of selection events
     */
    public void setSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Checks if a file is an image based on its extension.
     * @param file The file to check
     * @return true if it is an image file, false otherwise
     */
    public static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp" };
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates an image name in the tree.
     * @param oldFile The original file
     * @param newFile The renamed file
     */
    public void updateImageName(File oldFile, File newFile) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) treeModel.getRoot();
        Enumeration<?> e = root.depthFirstEnumeration();
        
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() instanceof FileNode) {
                FileNode fileNode = (FileNode) node.getUserObject();
                if (fileNode.getFilePath().equals(oldFile.getAbsolutePath())) {
                    fileNode = new FileNode(newFile.getName(), newFile.getAbsolutePath());
                    node.setUserObject(fileNode);
                    treeModel.nodeChanged(node);
                    break;
                }
            }
        }
    }

    /**
     * Refreshes the tree while maintaining current structure.
     */
    public void refreshTree() {
        DefaultMutableTreeNode currentRoot = (DefaultMutableTreeNode) treeModel.getRoot();
        FileNode rootFileNode = (FileNode) currentRoot.getUserObject();
        File currentRootFile = new File(rootFileNode.getFilePath());
        
        DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode(new FileNode(currentRootFile.getName(), currentRootFile.getAbsolutePath()));
        addDirectories(newRoot, currentRootFile);
        treeModel.setRoot(newRoot);
        treeModel.reload();
        expandTreeToPath(currentFolder != null ? currentFolder.getAbsolutePath() : rootDirectory);
    }

    /**
     * Toggles between showing folders only or all files.
     */
    public void toggleFolderView() {
        saveExpansionState();
        showFoldersOnly = !showFoldersOnly;
        File rootFolder = new File(System.getProperty("user.dir"), rootDirectory);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(new FileNode(rootFolder.getName(), rootFolder.getAbsolutePath()));
        addDirectories(root, rootFolder);
        treeModel.setRoot(root);
        treeModel.reload();
        restoreExpansionState(root);
    }

    /**
     * Saves the currently expanded paths in the tree.
     */
    private void saveExpansionState() {
        expandedPathsSet.clear();
        Enumeration<TreePath> expandedPaths = folderExplorer.getExpandedDescendants(new TreePath(treeModel.getRoot()));
        if (expandedPaths != null) {
            while (expandedPaths.hasMoreElements()) {
                TreePath path = expandedPaths.nextElement();
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (node.getUserObject() instanceof FileNode) {
                    FileNode fileNode = (FileNode) node.getUserObject();
                    expandedPathsSet.add(fileNode.getFilePath());
                }
            }
        }
    }

    /**
     * Restores the previously saved expanded tree paths.
     * @param root The root of the tree
     */
    private void restoreExpansionState(DefaultMutableTreeNode root) {
        Enumeration<?> e = root.depthFirstEnumeration();
        while (e.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
            if (node.getUserObject() instanceof FileNode) {
                FileNode fileNode = (FileNode) node.getUserObject();
                if (expandedPathsSet.contains(fileNode.getFilePath())) {
                    folderExplorer.expandPath(new TreePath(node.getPath()));
                }
            }
        }
        if (currentFolder != null) {
            expandTreeToPath(currentFolder.getAbsolutePath());
        }
    }

    /**
     * Checks if only folders are being shown.
     * @return true if only folders are visible, false otherwise
     */
    public boolean isShowFoldersOnly() {
        return showFoldersOnly;
    }

    /**
     * Interface for handling selection events.
     */
    public interface SelectionListener {
        /**
         * Called when a folder is selected.
         * @param folder The selected folder
         */
        void onFolderSelected(File folder);
        
        /**
         * Called when an image file is selected.
         * @param imageFile The selected image file
         */
        void onImageSelected(File imageFile);
    }

    /**
     * Gets the JTree folder explorer component.
     * @return The JTree component
     */
    public JTree getFolderExplorer() {
        return folderExplorer;
    }

    /**
     * Gets the DefaultTreeModel for the folder explorer.
     * @return The tree model
     */
    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }
}
