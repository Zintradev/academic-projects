package imagelibrary;

import java.io.File;
import java.util.Random;
import javax.swing.JOptionPane;
import imagelibrary.ui.MainWindow;
import imagelibrary.util.FolderGenerator;
import imagelibrary.util.ImageCreator;
import imagelibrary.util.ImageCreator.Format;

/**
 * Main class that launches the image library application.
 * Allows creating a folder structure with random images and launching the GUI.
 */
public class Main {

    /**
     * Main of the application.
     * @param args Command-line arguments (unused)
     */
    public static void main(String[] args) {
        int response = JOptionPane.showConfirmDialog(null, "Do you want to create sample folders and images?", "Setup Gallery", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            File baseFolder = new File("sample_gallery");
            
            if (!baseFolder.exists()) {
                baseFolder.mkdir();
            }
            
            Random random = new Random();
            FolderGenerator.generateStructure(baseFolder, 3, 2, random);
            addImagesInFolders(baseFolder, random);
        }

        new MainWindow();
    }

    /**
     * Adds random images in all folders and subfolders of the specified directory.
     * @param folder Base directory where images will be added
     * @param random Random number generator for image creation
     */
    private static void addImagesInFolders(File folder, Random random) {
        if (!folder.isDirectory()) return;
        String imageName = "image_" + random.nextInt(1000) + ".jpg";
        File image = new File(folder, imageName);
        ImageCreator.createRandomImage(image, 200, 200, Format.JPEG, random.nextInt(256));
        File[] subfolders = folder.listFiles(File::isDirectory);
        if (subfolders != null) {
            for (File sub : subfolders) {
                addImagesInFolders(sub, random);
            }
        }
    }
}