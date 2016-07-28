package imageShop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.util.List;

/**
 * Command Center is structured to manage the "memory" of the application: what is the most recent image,
 * the original image, lists of images for undos, redos, and recent files.
 */



public class CommandCenter {

    public static final int MAX_UNDO_COUNT = 20;
    public static final int MAX_RECENT_FILE_COUNT = 5;

    private static CommandCenter stateManager;
    private ImageView mImageView;
    private Image mImage, mOriginalImage;
    private static MaxSizeStack<Image> sBackImages, sForwardImages;
    private static MaxSizeStack<File> sRecentFiles;


    // private constructor
    private CommandCenter() {}

    // static method to get instance of the CommandCenter
    public static CommandCenter getInstance() {
        if (stateManager == null) {
            stateManager = new CommandCenter();
            sBackImages = new MaxSizeStack<>(MAX_UNDO_COUNT);
            sForwardImages = new MaxSizeStack<>(MAX_UNDO_COUNT);
            sRecentFiles = new MaxSizeStack<>(MAX_RECENT_FILE_COUNT);

        }
        return stateManager;
    }

    public void setImageView(ImageView imageView) {
        mImageView = imageView;
    }

    public void closeImage() {
        mImageView.setImage(null);
        sBackImages.clear();  // clear the list of undo images
        sForwardImages.clear();  // clear the list of redo images
    }

    public void setImageAndView(Image image) {
            mImage = image;
            mImageView.setImage(image);
    }

    public void storeLastImageAsUndo() {
        if (mImage != null) {
            sBackImages.push(mImage);
        }
    }

    public void addUndoImage(Image image) {
        sBackImages.push(image);
    }

    public Image getUndoImage() {
        if (sBackImages.isEmpty()) {
            throw new IndexOutOfBoundsException("cannot get undo image from empty list");
        }
        return sBackImages.pop();
    }

    public boolean hasUndoImage() {
        return !sBackImages.isEmpty();
    }

    public void clearUndoImages() {
        sBackImages.clear();
    }

    public void addRedoImage(Image image) {
        sForwardImages.push(image);
    }

    public Image getRedoImage() {
        if (sForwardImages.isEmpty()) {
            throw  new IndexOutOfBoundsException("cannot get redo image from empty list");
        }
        return sForwardImages.pop();
    }

    public boolean hasRedoImage() {
        return !sForwardImages.isEmpty();
    }

    public void clearRedoImages() {
        sForwardImages.clear();
    }

    public void addRecentFile(File file) {
        sRecentFiles.push(file);
    }

    public List<File> getRecentFiles() {
        return sRecentFiles.getAsLinkedList();
    }

    public Image getOriginalImage() {
        return mOriginalImage;
    }

    public void setOriginalImage(Image originalImage) {
        mOriginalImage = originalImage;
    }

}
