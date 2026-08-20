# Image Library Application

[![Java Version](https://img.shields.io/badge/Java-16%2B-orange.svg)](https://www.oracle.com/java/)
[![Build Tool](https://img.shields.io/badge/Build-Maven-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![GUI Library](https://img.shields.io/badge/GUI-Swing-lightblue.svg)](https://docs.oracle.com/javase/tutorial/uiswing/)

A professional, feature-rich Java Swing desktop application designed for importing, exploring, organizing, and editing digital images. The application parses and handles file metadata (including lossless EXIF header modification), provides interactive image manipulations (zooming, cropping, scaling, property adjustments), and integrates a lightweight mock canvas painting editor.

---

## Key Features

- **Folder & Gallery Tree Explorer**: Dynamically navigate directories, view image previews, create subfolders, and paint new images directly into folders.
- **Detailed Metadata View & Editing**: Tabular display of image details (name, width, height, size, modified date) with inline file renaming and modification date editing.
- **Advanced Image Processing**: Fast bilinear image scaling, canvas cropping, and manual sliders for brightness, contrast, and saturation adjustments.
- **EXIF Metadata Integration**: Read and losslessly write EXIF description tags and file attributes.
- **Serialization Persistence**: Descriptions are preserved across sessions in a local binary data store.

---

## Architecture & Technical Highlights

This project demonstrates several advanced software engineering principles and design patterns:

### 1. Model-View-Controller (MVC) Separation
The codebase separates business logic, metadata parsing, and GUI presentation into clean package layers:
- **`imagelibrary.model`**: Defines pure domain models (e.g., `ImageInfo`) which encapsulate details of the images.
- **`imagelibrary.ui`**: Contains modular Swing panels and dialog classes responsible only for rendering.
- **`imagelibrary.analyzer` & `imagelibrary.util`**: Serve as utility and controller layers executing heavy filesystem scans, metadata edits, and image calculations.

### 2. Multi-threaded GUI Tasks (`SwingWorker`)
To prevent desktop interface freezing (blocking the Event Dispatch Thread - EDT) during expensive operations, the application uses Java's `SwingWorker` for background execution:
- **Directory Analysis**: Deep folder scans and image checks run asynchronously, refreshing the GUI only when results are fully parsed.
- **Image Loading**: Fetching large image files from disk is offloaded to background threads.

### 3. Lossless Metadata Manipulation (EXIF)
Using the Apache Commons Imaging library, the system performs lossless updates on JPEG and TIFF headers:
- Uses `ExifRewriter` to update user tags (such as `ExifTagConstants.EXIF_TAG_DEVICE_SETTING_DESCRIPTION`) directly inside the binary file stream without re-encoding the image data, preserving visual quality and compression.

### 4. Custom Swing Canvas Component & Graphics
- Interactive zooming and panning utilizes custom mathematical transformations in `paintComponent` overrides.
- Sub-pixel cropping uses custom mouse gesture dragging overlays to capture user selected rectangular bounds.

---

## Directory Structure

```text
ImageLibrary/
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── imagelibrary/
│   │           ├── Main.java               # App entry point & sandbox generator
│   │           ├── analyzer/
│   │           │   └── ImageAnalyzer.java  # Fast image scanner
│   │           ├── model/
│   │           │   └── ImageInfo.java      # Image domain entity & EXIF model
│   │           ├── ui/
│   │           │   ├── MainWindow.java     # Primary JFrame container
│   │           │   └── *Panel.java         # Modular Swing UI panels
│   │           └── util/
│   │               ├── FolderGenerator.java# Mock directories creator
│   │               └── MetadataEditor.java # Commons Imaging EXIF wrapper
│   │
│   └── test/
│       └── java/
│           └── imagelibrary/
│               └── tests/                  # JUnit 5 integration & unit tests
│
├── pom.xml                                 # Maven project configuration
└── LICENSE                                 # License details
```

---

## Controls & Usage

| Interface Component | Action / Operation | Description |
| :--- | :--- | :--- |
| **Menu: File -> Load Folder** | Click or shortcut | Opens directory chooser to load a new image gallery. |
| **Menu: File -> Create Folder** | Click or shortcut | Prompts for a folder name and creates a directory inside the selection. |
| **Menu: File -> Create Image** | Click or shortcut | Launches the paint dialog to draw and save a custom image. |
| **Menu: View -> Show Folders Only** | Toggle checkbox | Filters out image files from the folder tree explorer. |
| **Image Table List** | Double Click (Name / Date) | Triggers inline editing to rename files or update the modified date in metadata. |
| **Preview Panel: Zoom** | Buttons `Zoom In` / `Zoom Out` | Scales the image display dynamically. |
| **Preview Panel: Crop** | Button `Crop` + Mouse Drag | Restricts the image to the selected rectangular canvas boundaries. |
| **Preview Panel: Adjustments** | Button `Adjustments` + Sliders | Adjusts Brightness, Contrast, and Saturation with live preview. |

---

## Setup & Execution

### Prerequisites
- **Java SE Development Kit (JDK) 16** or higher.
- **Apache Maven 3** build system.

### Build the Project
Compile the classes and packages, and run the JUnit tests:
```bash
mvn clean test
```

### Run the Application
Launch the GUI application using the Maven Exec plugin:
```bash
mvn compile exec:java -Dexec.mainClass="imagelibrary.Main"
```

To bundle the application into a single executable JAR file with all dependencies included:
```bash
mvn package
```
This produces `target/image-library-0.0.1-SNAPSHOT-jar-with-dependencies.jar`, which can be run using:
```bash
java -jar target/image-library-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
