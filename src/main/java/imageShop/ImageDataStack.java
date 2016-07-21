package imageShop;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Custom stack implementation with max size limit
 * removes oldest elements when adding a new limit brings stack size over limit
 */
public class ImageDataStack {

    // private members
    private LinkedList<ImageData> mImages;
    private int mSizeLimit;

    // constructor
    public ImageDataStack(int iMaxSize) {
        if (iMaxSize < 0) {
            throw new IllegalArgumentException("size limit for an ImageStack cannot be negative");
        }
        mImages = new LinkedList<>();
        mSizeLimit = iMaxSize;
    }

    // push a new image to the front of the linked list
    public void push(ImageData imageData) {
        // new images are added to front of list
        mImages.addFirst(imageData);

        // oldest images at end of list are removed if over size limit
        while (mImages.size() > mSizeLimit) {
            mImages.removeLast();
        }
    }

    // pop most recent image from the front of the linked list
    public ImageData pop() {
        // check if linked list is empty
        if (mImages.isEmpty()) {
            throw new NoSuchElementException("cannot pop an image from empty ImageStack");
        }

        // get most recent image from front of the list & remove it
        ImageData imageData = mImages.getFirst();
        mImages.removeFirst();
        return imageData;
    }

    // check if linked list is empty
    public boolean isEmpty() {
        return mImages.size() == 0;
    }


}
