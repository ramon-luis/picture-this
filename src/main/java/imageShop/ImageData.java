package imageShop;

import javafx.scene.image.Image;

/**
 * Stores an image with associated variables
 */
public class ImageData {

    private Image mImage;
    private double mBrightness, mContrast, mHue, mSaturation;

    public ImageData(Image image, double dBrightness, double dContrast, double dHue, double dSaturation) {
        this.mImage = image;
        this.mBrightness = dBrightness;
        this.mContrast = dContrast;
        this.mHue = dHue;
        this.mSaturation = dSaturation;
    }

    public Image getImage() {
        return mImage;
    }

    public void setImage(Image image) {
        mImage = image;
    }

    public double getBrightness() {
        return mBrightness;
    }

    public void setBrightness(double brightness) {
        mBrightness = brightness;
    }

    public double getContrast() {
        return mContrast;
    }

    public void setContrast(double contrast) {
        mContrast = contrast;
    }

    public double getHue() {
        return mHue;
    }

    public void setHue(double hue) {
        mHue = hue;
    }

    public double getSaturation() {
        return mSaturation;
    }

    public void setSaturation(double saturation) {
        mSaturation = saturation;
    }

    @Override
    public String toString() {
        return "ImageData{" +
                "mImage=" + mImage +
                ", mBrightness=" + mBrightness +
                ", mContrast=" + mContrast +
                ", mHue=" + mHue +
                ", mSaturation=" + mSaturation +
                '}';
    }
}
