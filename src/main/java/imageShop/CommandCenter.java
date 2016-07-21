package imageShop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

/**
 * Created by RAM0N on 7/19/16.
 */



public class CommandCenter {

    public static final int MAX_UNDO_ACTIONS = 20;

    private static CommandCenter stateManager;
    private Stage mMainStage, mSaturationStage;
    private ImageView mImageView;
    private ImageData mImageData;
    private Image mImage;
    private double mBrightness, mContrast, mHue, mSaturation;
    private static ImageDataStack sBackImageData, sForwardImageData;


    // private constructor
    private CommandCenter() {}

    // static method to get instance of the CommandCenter
    public static CommandCenter getInstance() {
        if (stateManager == null) {
            stateManager = new CommandCenter();
            sBackImageData = new ImageDataStack(MAX_UNDO_ACTIONS);
            sForwardImageData = new ImageDataStack(MAX_UNDO_ACTIONS);
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
    }

    public void setImageDataAndRefreshView__OLD(ImageData imageData) {
        if (this.mImageData != null) {
            sBackImageData.push(this.mImageData);  // add current image to back list for undo
        }
        this.mImageData = imageData;
        mImageView.setImage(mImageData.getImage());

    }

    public void updateBackImageData() {
        if (this.mImageData != null) {
            sBackImageData.push(this.mImageData);  // add current image to back list for undo
            System.out.println("back image data added.");
            System.out.println(mImageData);
        }
    }

    public void setImageDataAndRefreshView(ImageData imageData) {
        this.mImageData = imageData;
        mImageView.setImage(mImageData.getImage());
    }

    public void addBackImageData(ImageData imageData) {
        sBackImageData.push(imageData);
        System.out.println("back image data added.");
        System.out.println(mImageData);
    }


    public ImageData getImageData() {
        return mImageData;
    }
    public Image getImage() {
        return mImage;
    }

    public void undo() {
        // only undo if there is a history of back images
        if (!sBackImageData.isEmpty()) {
            sForwardImageData.push(this.mImageData);  // add current images to forward list for redo action
            ImageData undoImageData = sBackImageData.pop(); // pop off most recent image
            this.mImageData = undoImageData;  // update current image
            mImageView.setImage(this.mImageData.getImage());  // set the imageview
        }
    }

    public void redo() {
        // only redo if there is a history of forward images
        if (!sForwardImageData.isEmpty()) {
            sBackImageData.push(this.mImageData);  // add current images to back list for undo action
            ImageData redoImageData = sForwardImageData.pop();  // pop off top image
            this.mImageData = redoImageData;  // update current image
            mImageView.setImage(this.mImageData.getImage());  // set the imageview
        }
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


}
