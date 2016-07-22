package imageShop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by RAM0N on 7/19/16.
 */



public class CommandCenter {

    public static final int MAX_UNDO_COUNT = 20;
    public static final int MAX_RECENT_FILE_COUNT = 5;

    private static CommandCenter stateManager;
    private Stage mMainStage, mSaturationStage;
    private ImageView mImageView;
    private Image mImage;
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

    public void setMainStage(Stage mainStage) {
        this.mMainStage = mainStage;
    }

    public void setImageView(ImageView imageView) {
        this.mImageView = imageView;
    }

    public void closeImage() {
        mImageView.setImage(null);
        sBackImages.clear();  // clear the list of undo images
        sForwardImages.clear();  // clear the list of redo images
    }

    public void setImageAndView(Image image) {
        this.mImage = image;
        this.mImageView.setImage(image);
    }

    public void storeLastImageAsUndo() {
        if (this.mImage != null) {
            sBackImages.push(this.mImage);
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

    public void addRedoImage(Image image) {
        sForwardImages.push(image);
    }

    public Image getRedoImage() {
        if (sForwardImages.isEmpty()) {
            throw  new IndexOutOfBoundsException("cannot get redo image from empty list");
        }
        return sForwardImages.pop();
    }

    public boolean hasRedoImages() {
        return !sForwardImages.isEmpty();
    }


    public void addRecentFile(File file) {
        sRecentFiles.push(file);
    }

    public File getRecentFile() {
        return sRecentFiles.pop();
    }

    public List<File> getRecentFiles() {
        return sRecentFiles.getAsLinkedList();
    }

    public static Image transformSelection(Image imageIn, ColorTransformer f) {
        int width = (int) imageIn.getWidth();
        int height = (int) imageIn.getHeight();
        WritableImage imageOut = new WritableImage(width, height);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                imageOut.getPixelWriter().setColor(x, y,
                        f.apply(x, y, imageIn.getPixelReader().getColor(x, y)));
            }
        }
        return imageOut;
    }


    //    public void undo() {
//        // only undo if there is a history of back images
//        if (!sBackImages.isEmpty()) {
//            // store the current image in forward images for redo
//            Image currentImage = this.mImage;
//            sForwardImages.push(currentImage);
//
//            // update with the undo image
//            Image undoImage = sBackImages.pop();
//            setImageAndView(undoImage);
//        }
//    }
//
//    public void redo() {
//        // only redo if there is a history of forward images
//        if (!sForwardImages.isEmpty()) {
//            // store the current image in back images for undo
//            Image currentImage = this.mImage;
//            sBackImages.push(currentImage);
//
//            // update with the undo image
//            Image redoImage = sForwardImages.pop();
//            setImageAndView(redoImage);
//        }
//    }

}
