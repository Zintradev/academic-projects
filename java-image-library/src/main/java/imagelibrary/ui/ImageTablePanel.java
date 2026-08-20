package imagelibrary.ui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.swing.event.TableModelEvent;
import imagelibrary.model.ImageInfo;
import imagelibrary.util.MetadataEditor;
import imagelibrary.analyzer.ImageAnalyzer;

/**
 * Panel that displays a table of image files with their technical specifications.
 * Supports sorting, filtering, and editing of image metadata.
 */
public class ImageTablePanel extends JPanel {
    private JTable imageTable;
    private DefaultTableModel tableModel;
    private File currentFolder;
    private ImageSelectionListener selectionListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final int COL_NAME = 0;
    private static final int COL_WIDTH = 1;
    private static final int COL_HEIGHT = 2;
    private static final int COL_SIZE = 3;
    private static final int COL_DATE = 4;

    private Map<Integer, String> originalNames = new HashMap<>();

    /**
     * Constructs the image table panel with default configuration.
     */
    public ImageTablePanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Image List"));

        tableModel = new DefaultTableModel(new String[] { "Name", "Width", "Height", "Size", "Modified" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == COL_NAME || column == COL_DATE;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 2) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        imageTable = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == COL_DATE) {
                    ((JComponent) c).setToolTipText("Double click on name or date to change its value");
                }
                return c;
            }
        };

        setupTableAppearance();
        setupSelectionListener();
        setupNameEditListener();
        add(new JScrollPane(imageTable), BorderLayout.CENTER);

        imageTable.getColumnModel().getColumn(0).setPreferredWidth(110);
        imageTable.getColumnModel().getColumn(1).setPreferredWidth(10);
        imageTable.getColumnModel().getColumn(2).setPreferredWidth(10);
        imageTable.getColumnModel().getColumn(3).setPreferredWidth(20);
        imageTable.getColumnModel().getColumn(4).setPreferredWidth(110);
    }

    /**
     * Configures the visual appearance of the table.
     */
    private void setupTableAppearance() {
        Color tableHeaderColor = new Color(63, 81, 181);
        Color tableHeaderTextColor = Color.WHITE;
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font titleFont = new Font("Segoe UI", Font.BOLD, 16);

        imageTable.setFont(tableFont);
        imageTable.setRowHeight(30);
        imageTable.setSelectionBackground(new Color(207, 216, 220));
        imageTable.setGridColor(new Color(224, 224, 224));
        imageTable.setShowGrid(true);

        JTableHeader header = imageTable.getTableHeader();
        header.setBackground(tableHeaderColor);
        header.setForeground(tableHeaderTextColor);
        header.setFont(titleFont);
    }

    /**
     * Sets up the selection listener for the table.
     */
    private void setupSelectionListener() {
        imageTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && selectionListener != null && currentFolder != null) {
                int row = imageTable.getSelectedRow();
                if (row >= 0 && row < tableModel.getRowCount()) {
                    String imageName = (String) tableModel.getValueAt(row, 0);
                    File imageFile = new File(currentFolder, imageName);
                    selectionListener.onImageSelected(imageFile);
                }
            }
        });
    }

    /**
     * Sets up the listener for name and date edits.
     */
    private void setupNameEditListener() {
        tableModel.addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                // Validate row index and folder
                if (row < 0 || row >= tableModel.getRowCount() || currentFolder == null)
                    return;

                String originalName = originalNames.get(row);
                String newTableName = (String) tableModel.getValueAt(row, COL_NAME);
                String dateText = (String) tableModel.getValueAt(row, COL_DATE);

                // Handle name change
                if (column == COL_NAME && !originalName.equals(newTableName)) {
                    handleNameChange(row, originalName, newTableName);
                    return;
                }

                // Handle date change
                if (column == COL_DATE) {
                    handleDateChange(originalName, dateText);
                }
            }
        });
    }

    /**
     * Handles image file renaming.
     * @param row The table row being edited
     * @param originalName The original file name
     * @param newName The new file name
     */
    private void handleNameChange(int row, String originalName, String newName) {
        String extension = "";
        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalName.substring(dotIndex);
        }

        final String finalNewName = !extension.isEmpty() && !newName.endsWith(extension) ? 
            newName + extension : newName;

        File originalFile = new File(currentFolder, originalName);
        File newFile = new File(currentFolder, finalNewName);

        try {
            if (!originalFile.exists()) {
                throw new IOException("Original file does not exist.");
            }
            if (newFile.exists()) {
                throw new IOException("A file with that name already exists.");
            }

            boolean renamed = originalFile.renameTo(newFile);

            if (renamed) {
                originalNames.put(row, finalNewName);
                
                if (selectionListener != null) {
                    selectionListener.onImageRenamed(originalFile, newFile);
                }
                
                SwingUtilities.invokeLater(() -> {
                    if (row < tableModel.getRowCount()) {
                        tableModel.setValueAt(finalNewName, row, COL_NAME);
                    }
                });
            } else {
                throw new IOException("Could not rename the file.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error renaming file: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);

            SwingUtilities.invokeLater(() -> {
                tableModel.setValueAt(originalName, row, COL_NAME);
            });
        }
    }

    /**
     * Handles modification date changes.
     * @param fileName The name of the file being modified
     * @param dateText The new date text
     */
    private void handleDateChange(String fileName, String dateText) {
        File originalFile = new File(currentFolder, fileName);
        File tempFile = new File(currentFolder, "temp_" + fileName);
        File finalFile = new File(currentFolder, fileName);

        try {
            LocalDateTime newDate = LocalDateTime.parse(dateText,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            MetadataEditor.updateMetadata(originalFile, tempFile, newDate, -1, -1);

            if (!originalFile.delete()) {
                throw new IOException("Could not delete original file.");
            }

            if (!tempFile.renameTo(finalFile)) {
                throw new IOException("Could not rename temporary file.");
            }

            finalFile.setLastModified(
                    newDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());

            updateWithFolder(currentFolder);
            selectImage(finalFile.getName());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating metadata: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
            updateWithFolder(currentFolder);
        }
    }

    /**
     * Updates the table with images from the specified folder.
     * @param folder The folder containing images to display
     */
    public void updateWithFolder(File folder) {
        currentFolder = folder;
        tableModel.setRowCount(0);
        originalNames.clear();

        if (folder != null && folder.isDirectory()) {
            SwingWorker<ArrayList<ImageInfo>, Void> worker = new SwingWorker<ArrayList<ImageInfo>, Void>() {
                @Override
                protected ArrayList<ImageInfo> doInBackground() throws Exception {
                    return (ArrayList<ImageInfo>) ImageAnalyzer.analyzeFolder(folder);
                }

                @Override
                protected void done() {
                    try {
                        ArrayList<ImageInfo> images = get();
                        images.sort(Comparator.comparing(img -> img.getFile().lastModified()));
                        int rowIndex = 0;
                        for (ImageInfo img : images) {
                            String fechaModificacion = dateFormat.format(new Date(img.getFile().lastModified()));
                            tableModel.addRow(new Object[] { img.getName(), img.getWidth(), img.getHeight(),
                                    img.getFormattedSize(), fechaModificacion });
                            originalNames.put(rowIndex, img.getName());
                            rowIndex++;
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(ImageTablePanel.this,
                                "Error analyzing folder: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * Selects an image in the table by name.
     * @param imageName The name of the image to select
     */
    public void selectImage(String imageName) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(imageName)) {
                imageTable.setRowSelectionInterval(i, i);
                imageTable.scrollRectToVisible(imageTable.getCellRect(i, 0, true));
                break;
            }
        }
    }

    /**
     * Analyzes and displays information about images in the current folder.
     */
    public void analyzeImages() {
        if (currentFolder == null)
            return;

        File[] files = currentFolder.listFiles((dir, name) -> isImageFile(new File(dir, name)));
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            StringBuilder result = new StringBuilder("Images sorted by date:\n");

            for (File f : files) {
                result.append(f.getName()).append(" - ").append(dateFormat.format(new Date(f.lastModified())))
                        .append("\n");
            }
            JOptionPane.showMessageDialog(this, result.toString());
        }
    }

    /**
     * Shows a dialog for filtering images by various criteria.
     */
    public void applyFiltersDialog() {
        if (currentFolder == null) {
            JOptionPane.showMessageDialog(this, "Please load a folder with images first.");
            return;
        }

        String[] filterFields = { "Date (yyyy-MM-dd)", "Size in bytes", "Width (px)", "Height (px)",
                "Modification Date" };
        String selectedField = (String) JOptionPane.showInputDialog(this, "Select the field to filter by:",
                "Filter Images", JOptionPane.QUESTION_MESSAGE, null, filterFields, filterFields[0]);
        if (selectedField == null)
            return;

        String[] filterTypes = { "Greater than", "Less than", "Equal to" };
        String selectedType = (String) JOptionPane.showInputDialog(this, "Select the filter type:",
                "Filter Type", JOptionPane.QUESTION_MESSAGE, null, filterTypes, filterTypes[0]);
        if (selectedType == null)
            return;

        String inputValue = JOptionPane.showInputDialog(this,
                "Enter the value to filter by (" + selectedField + "):");
        if (inputValue == null || inputValue.trim().isEmpty())
            return;
        inputValue = inputValue.trim();

        File[] files = currentFolder.listFiles((dir, name) -> isImageFile(new File(dir, name)));
        if (files == null)
            files = new File[0];

        ArrayList<File> filteredFiles = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (File f : files) {
            try {
                switch (selectedField) {
                case "Date (yyyy-MM-dd)":
                    Date fileDate = new Date(f.lastModified());
                    java.util.Date inputDate = sdf.parse(inputValue);
                    if (applyDateFilter(fileDate, inputDate, selectedType)) {
                        filteredFiles.add(f);
                    }
                    break;

                case "Modification Date":
                    Date modDate = new Date(f.lastModified());
                    java.util.Date filterDate = dateFormat.parse(inputValue);
                    if (applyDateFilter(modDate, filterDate, selectedType)) {
                        filteredFiles.add(f);
                    }
                    break;

                case "Size in bytes":
                    long size = f.length();
                    long sizeInput = Long.parseLong(inputValue);
                    if (applyNumericFilter(size, sizeInput, selectedType)) {
                        filteredFiles.add(f);
                    }
                    break;

                case "Width (px)":
                    BufferedImage imgW = ImageIO.read(f);
                    if (imgW != null) {
                        int width = imgW.getWidth();
                        int widthInput = Integer.parseInt(inputValue);
                        if (applyNumericFilter(width, widthInput, selectedType)) {
                            filteredFiles.add(f);
                        }
                    }
                    break;

                case "Height (px)":
                    BufferedImage imgH = ImageIO.read(f);
                    if (imgH != null) {
                        int height = imgH.getHeight();
                        int heightInput = Integer.parseInt(inputValue);
                        if (applyNumericFilter(height, heightInput, selectedType)) {
                            filteredFiles.add(f);
                        }
                    }
                    break;
                }
            } catch (Exception ex) {
                // Silently ignore errors for individual files
            }
        }

        updateTableWithFilteredFiles(filteredFiles);
    }

    /**
     * Applies date filter comparison.
     * @param fileDate The file's date
     * @param filterDate The filter date
     * @param filterType The type of comparison
     * @return true if the file matches the filter
     */
    private boolean applyDateFilter(Date fileDate, Date filterDate, String filterType) {
        switch (filterType) {
        case "Greater than":
            return fileDate.after(filterDate);
        case "Less than":
            return fileDate.before(filterDate);
        case "Equal to":
            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            return dayFormat.format(fileDate).equals(dayFormat.format(filterDate));
        default:
            return false;
        }
    }

    /**
     * Applies numeric filter comparison.
     * @param value The value to check
     * @param filterValue The filter value
     * @param filterType The type of comparison
     * @return true if the value matches the filter
     */
    private boolean applyNumericFilter(long value, long filterValue, String filterType) {
        switch (filterType) {
        case "Greater than":
            return value > filterValue;
        case "Less than":
            return value < filterValue;
        case "Equal to":
            return value == filterValue;
        default:
            return false;
        }
    }

    /**
     * Updates the table with filtered files.
     * @param filteredFiles The list of files to display
     */
    private void updateTableWithFilteredFiles(ArrayList<File> filteredFiles) {
        tableModel.setRowCount(0);
        for (File f : filteredFiles) {
            try {
                BufferedImage img = ImageIO.read(f);
                if (img != null) {
                    String modificationDate = dateFormat.format(new Date(f.lastModified()));
                    tableModel.addRow(new Object[] { f.getName(), img.getWidth(), img.getHeight(),
                            formatFileSize(f.length()), modificationDate });
                }
            } catch (Exception ex) {
                // Silently ignore errors for individual files
            }
        }
    }

    /**
     * Formats file size in human-readable format.
     * @param size The size in bytes
     * @return Formatted size string
     */
    private String formatFileSize(long size) {
        if (size < 1024)
            return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(1024, exp), pre);
    }

    /**
     * Sets the image selection listener.
     * @param listener The listener to notify of selection events
     */
    public void setImageSelectionListener(ImageSelectionListener listener) {
        this.selectionListener = listener;
    }

    /**
     * Refreshes the display of the specified image.
     * @param imageFile The image file to refresh
     */
    public void refreshCurrentImage(File imageFile) {
        if (currentFolder != null && currentFolder.equals(imageFile.getParentFile())) {
            int selectedRow = imageTable.getSelectedRow();
            updateWithFolder(currentFolder);
            if (selectedRow >= 0) {
                imageTable.setRowSelectionInterval(selectedRow, selectedRow);
            }
        }
    }

    /**
     * Interface for receiving image selection and rename events.
     */
    public interface ImageSelectionListener {
        /**
         * Called when an image is selected.
         * @param imageFile The selected image file
         */
        void onImageSelected(File imageFile);

        /**
         * Called when an image is renamed.
         * @param oldFile The original file
         * @param newFile The renamed file
         */
        void onImageRenamed(File oldFile, File newFile);
    }

    /**
     * Checks if a file is an image by its extension.
     * @param file The file to check
     * @return true if the file is an image
     */
    private static boolean isImageFile(File file) {
        String[] imageExtensions = { "jpg", "jpeg", "png", "gif", "bmp", "tiff", "webp" };
        String fileName = file.getName().toLowerCase();
        for (String ext : imageExtensions) {
            if (fileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
}