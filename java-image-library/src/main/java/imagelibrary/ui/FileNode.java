package imagelibrary.ui;

/**
 * Represents a file node for graphical interfaces with name, path and expansion state.
 * Used primarily for tree or list views in file browsers.
 */
public class FileNode {
    private String name;
    private String filePath;
    private boolean expanded;

    /**
     * Creates a new file node with specified name and path.
     * @param name The display name of the node
     * @param path The full filesystem path to the file
     */
    public FileNode(String name, String path) {
        this.name = name;
        this.filePath = path;
    }

    /**
     * Gets the display name of the file node.
     * @return The node's display name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the full filesystem path of the file.
     * @return The complete file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets a new display name for the file node.
     * @param name The new display name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets a new filesystem path for the file node.
     * @param filePath The new complete file path
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the string representation of the node (display name only).
     * @return The node's display name
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Checks if the node is currently expanded in the UI.
     * @return true if expanded, false otherwise
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * Sets the expansion state of the node.
     * @param expanded true to mark as expanded, false to collapse
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}