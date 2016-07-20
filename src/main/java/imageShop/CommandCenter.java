package imageShop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by RAM0N on 7/19/16.
 */



public class CommandCenter {

    public static final int MAX_UNDOS = 10;

    private static CommandCenter stateManager;
    private Stage mMainStage, mSaturationStage;
    private ImageView mImageView;
    private Image mImage, mImageUndo;
    private static List<Image> sBackImages;

    // private constructor
    private CommandCenter() {}

    // static method to get instance of the CommandCenter
    public static CommandCenter getInstance() {
        if (stateManager == null) {
            stateManager = new CommandCenter();
            sBackImages = new LinkedList<>();
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

    public void setImageAndRefreshView(Image image) {
        mImageUndo = this.mImage;
        this.mImage = image;
        mImageView.setImage(image);
    }

    public Image getImage() {
        return mImage;
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
