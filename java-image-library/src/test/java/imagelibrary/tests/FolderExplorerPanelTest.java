package imagelibrary.tests;

import org.junit.jupiter.api.Test;
import imagelibrary.ui.FileNode;
import imagelibrary.ui.FolderExplorerPanel;
import static org.junit.jupiter.api.Assertions.*;
import javax.swing.tree.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Unit tests for {@link FolderExplorerPanel} class.
 * Tests image file recognition, tree node selection handling, and tree refresh functionality.
 */
public class FolderExplorerPanelTest {

    /**
     * Tests the image file recognition by extension.
     * Verifies both valid image extensions and non-image files.
     */
    @Test
    public void testIsImageFile() {
        
        assertTrue(FolderExplorerPanel.isImageFile(new File("test.jpg")));
        assertTrue(FolderExplorerPanel.isImageFile(new File("test.PNG")));
        assertTrue(FolderExplorerPanel.isImageFile(new File("test.webp")));
        
        assertFalse(FolderExplorerPanel.isImageFile(new File("test.txt")));
        assertFalse(FolderExplorerPanel.isImageFile(new File("test.doc")));
        assertFalse(FolderExplorerPanel.isImageFile(new File("test")));
    }

    /**
     * Tests folder selection notification in the directory tree.
     * Verifies that selection listeners receive proper folder events.
     * @throws IOException if temporary directory creation fails
     */
    @Test
    public void testFolderSelectionNotification() throws IOException {
        FolderExplorerPanel panel = new FolderExplorerPanel();
        File testDir = Files.createTempDirectory("testDir").toFile();
        
        FolderExplorerPanel.SelectionListener mockListener = new FolderExplorerPanel.SelectionListener() {
            @Override public void onFolderSelected(File folder) {
                assertEquals(testDir, folder);
            }
            @Override public void onImageSelected(File imageFile) {
                fail("Image selection should not trigger for folders");
            }
        };
        
        panel.setSelectionListener(mockListener);
        FileNode node = new FileNode(testDir.getName(), testDir.getAbsolutePath());
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(node);
        panel.getFolderExplorer().setSelectionPath(new TreePath(treeNode.getPath()));
        
        testDir.delete();
    }

    /**
     * Tests tree refresh functionality.
     * Verifies that the tree structure updates when filesystem changes occur.
     * @throws IOException if temporary file creation fails
     */
    @Test
    public void testTreeReflectsFilesystemChanges() throws IOException {
        FolderExplorerPanel panel = new FolderExplorerPanel();
        File testDir = Files.createTempDirectory("testRefresh").toFile();
        panel.updateTreeWithFolder(testDir);
        
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.getTreeModel().getRoot();
        int initialCount = root.getChildCount();
        
        File newFile = new File(testDir, "newFile.txt");
        assertTrue(newFile.createNewFile());
        
        try {
            panel.refreshTree();
            root = (DefaultMutableTreeNode) panel.getTreeModel().getRoot();
            assertTrue(root.getChildCount() > initialCount);
        } finally {
            newFile.delete();
            testDir.delete();
        }
    }
}