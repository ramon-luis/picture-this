package imageShop;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;

import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller has primary logic for application.
 * Structure is:
 *   FXML methods
 *   Initialize
 *   Logic & Helper Methods
 */

public class Controller implements Initializable{

    private enum PenShape {CIRCLE, SQUARE}

    private static final String DROPPER_IMAGE = "/images/dropper.png";
    private static final String BUCKET_IMAGE = "/images/bucket.png";
    private static final double DEFAULT_PEN_SIZE = 20.0;
    private static final double DEFAULT_PEN_PRESSURE = 75.0;
    private static final double DEFAULT_EFFECTS_VALUE = 0.0;

    private static final Circle mCircle = new Circle(11.5);
    private static final Rectangle mSquare = new Rectangle(20.0, 20.0);

    private Color mColor = Color.rgb(25, 150, 255);
    private PenShape mPenShape;
    private int mPenSize;
    private double mPenPressure;
    private Rectangle mRectangle;
    private double xPos, yPos;
    private boolean mIsOpenDialogBox = false;
    private ColorAdjust mColorAdjust = new ColorAdjust();
    private ArrayList<Shape> removeShapes = new ArrayList<>(1000);
    private File mCurrentFile;

    @FXML private AnchorPane mAnchorPane;
    @FXML private ImageView mImageView;
    @FXML private ImageView mSelectionView;

    @FXML private Menu menuOpenRecent;
    @FXML private MenuItem menuRecentFile1;
    @FXML private MenuItem menuRecentFile2;
    @FXML private MenuItem menuRecentFile3;
    @FXML private MenuItem menuRecentFile4;
    @FXML private MenuItem menuRecentFile5;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuStartOver;

    @FXML private ColorPicker cpkColor;
    @FXML private ToggleButton tgbDropper;
    @FXML private ToggleButton tgbBucket;
    @FXML private ToggleButton tgbPen;
    @FXML private ToolBar tbPenDetails;
    @FXML private ToggleButton tgbCircle;
    @FXML private ToggleButton tgbSquare;
    @FXML private Slider sldPenSize;
    @FXML private Button btnResetPenSize;
    @FXML private Slider sldPenPressure;
    @FXML private Button btnResetPenPressure;
    @FXML private Button btnUndo;
    @FXML private Button btnRedo;
    @FXML private Button btnStartOver;

    @FXML private ToggleButton tgbSelectArea;

    @FXML private ToggleButton tgbEffects;
    @FXML private ToolBar tbEffects;
    @FXML private Button btnApplyEffects;
    @FXML private Button btnResetEffects;
    @FXML private Slider sldBrightness;
    @FXML private Button btnResetBrightness;
    @FXML private Slider sldContrast;
    @FXML private Button btnResetContrast;
    @FXML private Slider sldHue;
    @FXML private Button btnResetHue;
    @FXML private Slider sldSaturate;
    @FXML private Button btnResetSaturate;
    @FXML private Button btnGreyscale;
    @FXML private Button btnSepia;
    @FXML private Button btnInvert;
    @FXML private Button btnMonochrome;
    @FXML private Button btnGlow;

    @FXML private ToggleGroup tgTools = new ToggleGroup();
    @FXML private ToggleGroup tgPenShape = new ToggleGroup();

    @FXML private Tooltip ttColorPicker = new Tooltip("choose color");
    @FXML private Tooltip ttDropper = new Tooltip("dropper: pick color");
    @FXML private Tooltip ttBucket = new Tooltip("paint bucket");
    @FXML private Tooltip ttPen = new Tooltip("pen");
    @FXML private Tooltip ttUndo  = new Tooltip("undo");
    @FXML private Tooltip ttRedo = new Tooltip("redo");
    @FXML private Tooltip ttStartOver = new Tooltip("start over");
    @FXML private Tooltip ttSelectArea = new Tooltip("select area");
    @FXML private Tooltip ttEffects = new Tooltip("effects settings");
    @FXML private Tooltip ttGreyscale = new Tooltip("greyscale");
    @FXML private Tooltip ttSepia = new Tooltip("sepia");
    @FXML private Tooltip ttInvert = new Tooltip("invert");
    @FXML private Tooltip ttMonochrome = new Tooltip("monochrome");
    @FXML private Tooltip ttGlow = new Tooltip("glow");


    // **************************************** //
    // **     FXML METHODS FOR MENU BAR      ** //
    // **************************************** //

    @FXML void menuOpenAction(ActionEvent event) {
        openFile();
    }

    @FXML void menuRecentFileAction1(ActionEvent event) {
        File file = CommandCenter.getInstance().getRecentFiles().get(0);
        openImageFile(file);
    }

    @FXML void menuRecentFileAction2(ActionEvent event) {
        File file = CommandCenter.getInstance().getRecentFiles().get(1);
        openImageFile(file);
    }

    @FXML void menuRecentFileAction3(ActionEvent event) {
        File file = CommandCenter.getInstance().getRecentFiles().get(2);
        openImageFile(file);
    }

    @FXML void menuRecentFileAction4(ActionEvent event) {
        File file = CommandCenter.getInstance().getRecentFiles().get(3);
        openImageFile(file);
    }

    @FXML void menuRecentFileAction5(ActionEvent event) {
        File file = CommandCenter.getInstance().getRecentFiles().get(4);
        openImageFile(file);
    }

    @FXML void menuCloseAction(ActionEvent event) {
        CommandCenter.getInstance().closeImage();
        CommandCenter.getInstance().setOriginalImage(getSnapshot());
    }

    @FXML void menuSaveAction(ActionEvent event) {
        save(mCurrentFile);
    }

    @FXML void menuSaveAsAction(ActionEvent event) {
            saveAs();
    }

    @FXML void menuQuitAction() {
        System.exit(0);
    }

    @FXML void menuUndoAction(ActionEvent event) {
       undo();
    }

    @FXML void menuRedoAction(ActionEvent event) {
        redo();
    }

    @FXML void menuStartOverAction(ActionEvent event) {
        startOver();
    }

    @FXML void menuHelpAction() {getHelp();}

    @FXML void menuAboutAction() {getAbout();}

    // *************************** INITIALIZE METHOD BEGINS *************************** //

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        // **************************************** //
        // **                SETUP               ** //
        // **************************************** //

        // take a snapshot to set as initial image
        Image initialImage = getSnapshot();
        mImageView.setImage(initialImage);
        CommandCenter.getInstance().setImageView(mImageView);
        CommandCenter.getInstance().setOriginalImage(initialImage);
        CommandCenter.getInstance().setImageAndView(initialImage);

        // assign toggle groups
        tgbDropper.setToggleGroup(tgTools);
        tgbBucket.setToggleGroup(tgTools);
        tgbPen.setToggleGroup(tgTools);
        tgbCircle.setToggleGroup(tgPenShape);
        tgbSquare.setToggleGroup(tgPenShape);

        // assign default color
        cpkColor.setValue(mColor);

        // hide open recent, hide tools, disable undo/edo
        setPenShapeIcons();
        resetAllMenusAndToolbars();
        assignToolTips();

        // **************************************** //
        // **            MOUSE ACTIONS           ** //
        // **************************************** //

        // mouse enters area
        mImageView.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            setMouseToPointer(mouseEvent);
            if (!mIsOpenDialogBox && !cpkColor.isShowing()) {
                // order matters: from specific to general actions
                if (tgbSelectArea.isSelected()) {
                    setMouseToCross(mouseEvent);
                } else if (tgbEffects.isSelected()) {
                    setMouseToPointer(mouseEvent);
                } else if (tgbDropper.isSelected()) {
                    setMouseToDropper(mouseEvent);
                } else if (tgbBucket.isSelected()) {
                    System.out.println("bucket icon");
                    setMouseToBucket(mouseEvent);
                } else if (tgbPen.isSelected() && mPenShape != null) {
                    setMouseToPenShape(mouseEvent);
                }
            }
            mouseEvent.consume();
        });

        // mouse pressed
        mImageView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            // order matters: from specific to general actions
            if (tgbSelectArea.isSelected()) {
                if (mRectangle != null) {
                    removeSelection();
                    resetEffectsSliders();
                } else if (tgbSelectArea.isSelected()) {
                    startSelection(mouseEvent);
                }
            } else if (tgbEffects.isSelected()) {
                // do nothing
            } else if (tgbDropper.isSelected()) {
                pickColorFromDropper(mouseEvent);
            } else if (tgbBucket.isSelected()) {
                fillFromBucket(mouseEvent);
            } else if (tgbPen.isSelected() && mPenShape != null) {
                drawPen(mouseEvent);
            }
            mouseEvent.consume();
        });

        // mouse dragged
        mImageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            // order matters: from specific to general actions
            if (tgbSelectArea.isSelected()) {
               updateRectangle(mouseEvent);
            } else if (tgbEffects.isSelected()) {
                // do nothing
            } else if (tgbDropper.isSelected()) {
                // do nothing
            } else if (tgbBucket.isSelected()) {
                fillFromBucket(mouseEvent);
            } else if (tgbPen.isSelected() && mPenShape != null) {
                drawPen(mouseEvent);
            }
            mouseEvent.consume();
        });

        // mouse released:
        mImageView.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            // order matters: from specific to general actions
            if (tgbSelectArea.isSelected()) {
                updateRectangle(mouseEvent);
            } else if (tgbEffects.isSelected()) {
                // do nothing
            } else if (tgbDropper.isSelected()) {
                // do nothing
            } else if (tgbBucket.isSelected()) {
                updateImageAndProperties();
            } else if (tgbPen.isSelected() && mPenShape != null) {
                updateImageAndProperties();
            }
            mouseEvent.consume();
        });

        // **************************************** //
        // **            TOGGLE GROUPS           ** //
        // **************************************** //

        // DrawTool toggle group
        tgTools.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            System.out.println("tool selected");
            removeSelection();
            if (newValue == tgbBucket) {
                System.out.println("bucket selected");
                System.out.println("bucket tgb is selected: " + tgbBucket.isSelected());
                hidePenAndEffectsMenu();
                resetEffectsSliders();
                tgbSelectArea.setSelected(false);
                tgbEffects.setSelected(false);
            } else if (newValue == tgbPen) {
                showPenDetails();
                resetEffectsSliders();
                tgbSelectArea.setSelected(false);
                tgbEffects.setSelected(false);
                if (mPenShape == PenShape.SQUARE) {
                    tgbSquare.setSelected(true);
                } else {
                    tgbCircle.setSelected(true);
                }
            } else if (newValue == tgbDropper) {
                hidePenAndEffectsMenu();
                resetEffectsSliders();
                tgbSelectArea.setSelected(false);
                tgbEffects.setSelected(false);
            } else {
                hidePenDetails();
                if (tgTools.getSelectedToggle() != null) {
                    tgTools.getSelectedToggle().setSelected(false);
                }
            }
        });

        // PenShape toggle group
        tgPenShape.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbCircle) {
                mPenShape = PenShape.CIRCLE;
                tgbCircle.setSelected(true);
            } else if (newValue == tgbSquare) {
                mPenShape = PenShape.SQUARE;
                tgbSquare.setSelected(true);
            }
        });

        // **************************************** //
        // **            COLOR PICKER            ** //
        // **************************************** //

        // update color based on color picker menu dropdown
        cpkColor.setOnAction(event -> updateColor());

        // **************************************** //
        // **    CHANGE VALUE FOR PEN SLIDERS    ** //
        // **************************************** //

        // pen pressure slider
        sldPenPressure.valueProperty().addListener((observable, oldValue, newValue) -> {
            mPenPressure = (double) newValue / 100;
        });

        // pen size slider
        sldPenSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            mPenSize = (int) Math.round( (double) newValue);
        });

        // **************************************** //
        // **   RESET BUTTONS FOR PEN SLIDERS    ** //
        // **************************************** //

        btnResetPenSize.setOnAction(event -> sldPenSize.setValue(DEFAULT_PEN_SIZE));
        btnResetPenPressure.setOnAction(event -> sldPenPressure.setValue(DEFAULT_PEN_PRESSURE));

        // **************************************** //
        // **       ADJUST / EFFECTS BUTTON      ** //
        // **************************************** //

        tgbEffects.selectedProperty().addListener((observable, oldValue, newValue) -> {
            showEffectsDetails();
            if (tgTools.getSelectedToggle() != null) {
                tgTools.getSelectedToggle().setSelected(false);
            }
        });

        // **************************************** //
        // **        SELECT AREA BUTTON          ** //
        // **************************************** //

        tgbSelectArea.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hidePenDetails();
            if (tgTools.getSelectedToggle() != null) {
                tgTools.getSelectedToggle().setSelected(false);
            }
        });

        // **************************************** //
        // **  CHANGE VALUE FOR EFFECTS SLIDERS  ** //
        // **************************************** //

        // brightness slider
        sldBrightness.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorAdjustEffect();
        });

        // contrast slider
        sldContrast.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorAdjustEffect();
        });

        // hue slider
        sldHue.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorAdjustEffect();
        });

        // saturate slider
        sldSaturate.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateColorAdjustEffect();
        });

        // **************************************** //
        // **     BUTTONS FOR EFFECTS SLIDERS    ** //
        // **************************************** //

        btnResetBrightness.setOnAction(event -> sldBrightness.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetContrast.setOnAction(event -> sldContrast.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetHue.setOnAction(event -> sldHue.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetSaturate.setOnAction(event -> sldSaturate.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetEffects.setOnAction(event -> resetEffectsSliders());
        btnApplyEffects.setOnAction(event -> applyImageEffects());

        // **************************************** //
        // **        UNDO AND REDO BUTTONS       ** //
        // **************************************** //

        btnUndo.setOnAction(event -> undo());
        btnRedo.setOnAction(event -> redo());
        btnStartOver.setOnAction(event -> startOver());

        // **************************************** //
        // **           FILTER BUTTONS          ** //
        // **************************************** //

        btnSepia.setOnAction(event -> applySepiaFilter());
        btnInvert.setOnAction(event -> applyInvertFilter());
        btnGreyscale.setOnAction(event -> applyGrayscaleFilter());
        btnMonochrome.setOnAction(event -> applyMonoFilter());
        btnGlow.setOnAction(event -> applyGlowFilter());


    }



    // *************************** INITIALIZE METHOD ENDS *************************** //



    // **************************************** //
    // **           FILTER METHODS          ** //
    // **************************************** //

    // grayscale filter
    private void applyGrayscaleFilter() {
        int iMinX = (mRectangle != null) ? (int) mRectangle.getX() : 0;
        int iMinY = (mRectangle != null) ? (int) mRectangle.getY() : 0;
        int iMaxX = (mRectangle != null) ? (int) mRectangle.getWidth() + iMinX : (int) mImageView.getImage().getWidth();
        int iMaxY = (mRectangle != null) ? (int) mRectangle.getHeight() + iMinY: (int) mImageView.getImage().getHeight();
        removeSelection();
        WritableImage grayscaleImage = new WritableImage((int) mImageView.getImage().getWidth(), (int) mImageView.getImage().getHeight());
        PixelWriter pixelWriter = grayscaleImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth(); x++) {
            for (int y = 0; y < mImageView.getImage().getHeight(); y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                Color newColor = currentColor.grayscale();
                if (x > iMinX && x < iMaxX && y > iMinY && y < iMaxY) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, currentColor);
                }
            }
        }
        mImageView.setImage(grayscaleImage);
        updateImageAndProperties();
    }

    // invert filter
    private void applyInvertFilter() {
        int iMinX = (mRectangle != null) ? (int) mRectangle.getX() : 0;
        int iMinY = (mRectangle != null) ? (int) mRectangle.getY() : 0;
        int iMaxX = (mRectangle != null) ? (int) mRectangle.getWidth() + iMinX : (int) mImageView.getImage().getWidth();
        int iMaxY = (mRectangle != null) ? (int) mRectangle.getHeight() + iMinY: (int) mImageView.getImage().getHeight();
        removeSelection();
        WritableImage inverseImage = new WritableImage((int) mImageView.getFitWidth(), (int) mImageView.getFitHeight());
        PixelWriter pixelWriter = inverseImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth(); x++) {
            for (int y = 0; y < mImageView.getImage().getHeight(); y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                Color newColor = currentColor.invert();
                if (x > iMinX && x < iMaxX && y > iMinY && y < iMaxY) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, currentColor);
                }
            }
        }
        mImageView.setImage(inverseImage);
        updateImageAndProperties();
    }

    // sepia filter
    private void applySepiaFilter() {
        SepiaTone sepiaTone = new SepiaTone();
        sepiaTone.setLevel(0.70);
        updateImageEffect(sepiaTone);
    }

    // mono filter
    private void applyMonoFilter() {
        double monoThreshold = 1.0;
        int iMinX = (mRectangle != null) ? (int) mRectangle.getX() : 0;
        int iMinY = (mRectangle != null) ? (int) mRectangle.getY() : 0;
        int iMaxX = (mRectangle != null) ? (int) mRectangle.getWidth() + iMinX : (int) mImageView.getImage().getWidth();
        int iMaxY = (mRectangle != null) ? (int) mRectangle.getHeight() + iMinY: (int) mImageView.getImage().getHeight();
        removeSelection();
        WritableImage monoImage = new WritableImage((int) mImageView.getFitWidth(), (int) mImageView.getFitHeight());
        PixelWriter pixelWriter = monoImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth() - 1; x++) {
            for (int y = 0; y < mImageView.getImage().getHeight() - 1; y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                Color newColor = (currentColor.getRed() + currentColor.getRed() + currentColor.getBlue() < monoThreshold) ?
                        Color.BLACK : Color.WHITE;
                if (x > iMinX && x < iMaxX && y > iMinY && y < iMaxY) {
                    pixelWriter.setColor(x, y, newColor);
                } else {
                    pixelWriter.setColor(x, y, currentColor);
                }
            }
        }
        mImageView.setImage(monoImage);
        updateImageAndProperties();
    }

    private void applyGlowFilter() {
        Glow glow = new Glow();
        glow.setLevel(0.3);
        updateImageEffect(glow);
    }

    private void updateImageEffect(Effect effect) {
        if (mRectangle != null) {
            updateSelectionEffect(effect);
        } else {
            mImageView.setEffect(effect);
            Image updatedImage = getSnapshot();
            mImageView.setEffect(null);
            mImageView.setImage(updatedImage);
        }
        updateImageAndProperties();
    }

    private void updateSelectionEffect(Effect effect) {
        ImageView tempView = new ImageView();
        Image tempImage = getSnapshotForSelection();
        tempView.setImage(tempImage);
        tempView.setEffect(effect);
        mAnchorPane.getChildren().add(tempView);
        tempView.setX(mRectangle.getX());
        tempView.setY(mRectangle.getY());
        Image updatedImage = getSnapshot();
        mAnchorPane.getChildren().remove(tempView);
        mImageView.setImage(updatedImage);
    }

    // **************************************** //
    // **       DRAW & PAINT METHODS         ** //
    // **************************************** //

    // update the color for paint, draw, and pen shapes
    private void updateColor() {
        mColor = cpkColor.getValue();
        updatePenShapesColor();
    }

    // draw with pen tool
    private void drawPen(MouseEvent mouseEvent) {
        // update x and y position from mouse
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();

        // get shape; add to anchor pane and removeShape list
        Shape penShape = getPenShape();
        mAnchorPane.getChildren().add(penShape);
        removeShapes.add(penShape);
    }

    // get the shape of the pen
    private Shape getPenShape() {
        // get the current pen size and pressure
        Shape penShape = null;
        mPenSize = (int) sldPenSize.getValue();
        mPenPressure = sldPenPressure.getValue() / 100.0;

        // update the shape based on pen shape selected
        if (this.mPenShape == PenShape.CIRCLE) {
            penShape = new Circle(xPos, yPos, mPenSize);
        } else if (this.mPenShape == PenShape.SQUARE) {
            penShape = new Rectangle(xPos, yPos, mPenSize, mPenSize);
        }

        // set the color and pressure before returning
        if (mColor != null) {
            penShape.setFill(mColor);
        }

        penShape.setOpacity(mPenPressure);

        return penShape;
    }

    // get the color from image using dropper
    private void pickColorFromDropper(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        Color pixelColor = mImageView.getImage().getPixelReader().getColor((int) xPos, (int) yPos);
        cpkColor.setValue(pixelColor);
        mColor = pixelColor;
    }

    // fill: paint
    private void fillFromBucket(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        Color targetColor = mImageView.getImage().getPixelReader().getColor((int) xPos, (int) yPos);
        updatePixelColorsBFS((int) xPos, (int) yPos, targetColor);
    }

    // check if pixel address is within image
    private boolean isPixelInBounds(int iPixelX, int iPixelY) {
        int iMaxX = (int) mImageView.getImage().getWidth() - 1;
        int iMaxY = (int) mImageView.getImage().getHeight() - 1;
        return (iPixelX >= 0 && iPixelX <= iMaxX) && (iPixelY >= 0 && iPixelY <= iMaxY);
    }

    // check if pixel has matching color
    private boolean isMatchingColorPixel(int iPixelX, int iPixelY, Color targetColor) {
        return mImageView.getImage().getPixelReader().getColor(iPixelX, iPixelY).equals(targetColor);
    }

    // fill across all pixels that touch and are same color (Breadth First Search)
    private void updatePixelColorsBFS(int iPixelX, int iPixelY, Color targetColor) {
        // get max int for a pixel position
        int iMaxX = (int) mImageView.getImage().getWidth() - 1;
        int iMaxY = (int) mImageView.getImage().getHeight() - 1;

        // create boolean list and pair list -> boolean tracks if we have visited this pixel yet, linked list is Queue
        boolean[][] pixelChecked = new boolean[iMaxX + 1][iMaxY + 1]; // default value is false
        LinkedList<Pair<Integer,Integer>> pixelQueue = new LinkedList<>();

        // create a writable image and pixelwriter
        WritableImage image = new WritableImage(mImageView.getImage().getPixelReader(), (int) mImageView.getImage().getWidth(), (int) mImageView.getImage().getHeight());
        PixelWriter pixelWriter = image.getPixelWriter();

        // update & add the first pixel to queue
        Pair<Integer, Integer> thisPixel = new Pair<>(iPixelX, iPixelY);
        pixelChecked[iPixelX][iPixelY] = true;
        pixelQueue.addLast(thisPixel);

        // loop while queue is not empty, i.e. so long as there is valid path forward
        while (!pixelQueue.isEmpty()) {
            // get first element, remove it from list
            Pair<Integer, Integer> currentPixel = pixelQueue.getFirst();
            pixelQueue.removeFirst();
            int x = currentPixel.getKey();
            int y = currentPixel.getValue();

            // check if matching color: yes, then continue
            if (isMatchingColorPixel(x, y, targetColor)) {
                pixelWriter.setColor(x, y, mColor);

                // enqueue 4 toughing elements if they qualify: within bounds and have not yet been checked
                if (isPixelInBounds(x + 1, y) && !pixelChecked[x + 1][y]) {
                    Pair<Integer, Integer> nextPixel = new Pair<>(x + 1, y);
                    pixelChecked[x + 1][y] = true;
                    pixelQueue.addLast(nextPixel);
                }
                if (isPixelInBounds(x - 1, y) && !pixelChecked[x - 1][y]) {
                    Pair<Integer, Integer> nextPixel = new Pair<>(x - 1, y);
                    pixelChecked[x - 1][y] = true;
                    pixelQueue.addLast(nextPixel);
                }
                if (isPixelInBounds(x, y + 1) && !pixelChecked[x][y + 1]) {
                    Pair<Integer, Integer> nextPixel = new Pair<>(x, y + 1);
                    pixelChecked[x][y + 1] = true;
                    pixelQueue.addLast(nextPixel);
                }
                if (isPixelInBounds(x, y - 1) && !pixelChecked[x][y - 1]) {
                    Pair<Integer, Integer> nextPixel = new Pair<>(x, y - 1);
                    pixelChecked[x][y - 1] = true;
                    pixelQueue.addLast(nextPixel);
                }
            }
        }
        mImageView.setImage(image);
    }

    // **************************************** //
    // **   EFFECTS (COLOR ADJUST) METHODS   ** //
    // **************************************** //

    // update the color adjust to current slider settings
    private void updateColorAdjustEffect() {
        // update the color adjust settings
        mColorAdjust.setBrightness(sldBrightness.getValue() / 100.0);
        mColorAdjust.setContrast(sldContrast.getValue() / 100.0);
        mColorAdjust.setHue(sldHue.getValue() / 100.0);
        mColorAdjust.setSaturation(sldSaturate.getValue() / 100.0);

        // apply to selection or entire imageview
        if (mRectangle != null) {
            mImageView.setEffect(null);
            updateColorAdjustEffectForSelection();
        } else {
            mImageView.setEffect(mColorAdjust);
        }
    }

    // update the color adjust to current slider settings for a selection
    private void updateColorAdjustEffectForSelection() {
        // get the selection if does not exist
        if (mSelectionView == null) {
            mSelectionView = new ImageView();
            Image selectionImage = getSnapshotForSelection();
            mSelectionView.setImage(selectionImage);
            mSelectionView.setX(mRectangle.getX());
            mSelectionView.setY(mRectangle.getY());
            mAnchorPane.getChildren().add(mSelectionView);
        }

        // apply to the selection
        mSelectionView.setEffect(mColorAdjust);

    }

    // makes image effects permanent -> updates image and resets sliders
    private void applyImageEffects() {
        if (mSelectionView != null) {
            Image currentImage = getSnapshot();
            mAnchorPane.getChildren().remove(mSelectionView);
            mSelectionView = null;
            mImageView.setImage(currentImage);
            removeSelection();
        }
        updateImageAndProperties();
    }

    // set effects sliders to 0 (base)
    private void resetEffectsSliders() {
        sldBrightness.setValue(DEFAULT_EFFECTS_VALUE);
        sldContrast.setValue(DEFAULT_EFFECTS_VALUE);
        sldHue.setValue(DEFAULT_EFFECTS_VALUE);
        sldSaturate.setValue(DEFAULT_EFFECTS_VALUE);
    }

    // **************************************** //
    // **         SNAPSHOT METHODS           ** //
    // **************************************** //

    // get a "snap" of screen's current image
    private Image getSnapshot() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(mImageView.getViewport());
//            snapshotParameters.setViewport(new Rectangle2D(mImageView.getX(), mImageView.getY(), mImageView.getImage().getWidth(), mImageView.getImage().getHeight()));
        return mAnchorPane.snapshot(snapshotParameters, null);
    }

    // get snapshot from rectangle selection
    private Image getSnapshotForSelection() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        mRectangle.setVisible(false);
        snapshotParameters.setViewport(new Rectangle2D(mRectangle.getX(),mRectangle.getY(),mRectangle.getWidth(), mRectangle.getHeight()));
        return mImageView.snapshot(snapshotParameters, null);
    }

    // update the image and associated properties
    private void updateImageAndProperties() {
        removeSelection();
        CommandCenter.getInstance().storeLastImageAsUndo();
        CommandCenter.getInstance().clearRedoImages();  // new "path" so clear redo images
        disableRedo();
        enableUndo();
        Image currentImage = getSnapshot();
        CommandCenter.getInstance().setImageAndView(currentImage);
        resetEffectsSliders();
        mImageView.setImage(currentImage);
        mAnchorPane.getChildren().removeAll(removeShapes);
        removeShapes.clear();
    }

    // **************************************** //
    // **       MOUSE ICON METHODS           ** //
    // **************************************** //

    // change the mouse icon to cross hairs
    private void setMouseToCross(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setCursor(Cursor.CROSSHAIR);
    }

    // change the mouse icon to pointer (default)
    private void setMouseToPointer(MouseEvent mouseEvent) {
        ((Node) mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
    }

    // change mouse icon to dropper
    private void setMouseToDropper(MouseEvent mouseEvent) {
        Image dropperImage = new Image(DROPPER_IMAGE, 25, 25, false, false);
        double dBottom = dropperImage.getHeight();
        double dLeft = 0;
        ((Node) mouseEvent.getSource()).setCursor(new ImageCursor(dropperImage, dLeft, dBottom));
    }

    // set the mouse icon to the pen shape
    private void setMouseToPenShape(MouseEvent mouseEvent) {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);
        WritableImage snapshot = getPenShape().snapshot(snapshotParameters, null);
        double middleW = (mPenShape == PenShape.CIRCLE) ? snapshot.getWidth() / 2 : 0;
        double middleH = (mPenShape == PenShape.CIRCLE) ? snapshot.getHeight() / 2 : 0;
        ((Node) mouseEvent.getSource()).setCursor(new ImageCursor(snapshot, middleW, middleH));
    }

    // change mouse icon to bucket
    private void setMouseToBucket(MouseEvent mouseEvent) {
        Image dropperImage = new Image(BUCKET_IMAGE, 40, 40, false, false);
        double dBottomish = dropperImage.getHeight() * 0.70;
        double dRightish = dropperImage.getWidth() * 0.90;
        ((Node) mouseEvent.getSource()).setCursor(new ImageCursor(dropperImage, dRightish, dBottomish));
    }

    // **************************************** //
    // **       SELECT AREA METHODS          ** //
    // **************************************** //

    // start a new rectangle selection
    private void startSelection(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        mRectangle = new Rectangle();
        mRectangle.setFill(Color.SNOW);
        mRectangle.setStroke(Color.WHITE);
        mRectangle.setOpacity(0.15);
        mAnchorPane.getChildren().add(mRectangle);
    }

    // remove rectangle selection
    private void removeSelection() {
        mAnchorPane.getChildren().removeAll(mSelectionView);
        mSelectionView = null;
        mAnchorPane.getChildren().remove(mRectangle);
        mRectangle = null;
    }

    // update rectangle: used to invert shape based on mouse position
    private void updateRectangle(MouseEvent mouseEvent) {
        if (mRectangle != null) {
            // natural starting and ending corners
            double dStartX = xPos;
            double dStartY = yPos;
            double dEndX = mouseEvent.getX();
            double dEndY = mouseEvent.getY();

            // check if x and y are inverted
            boolean bIsInvertedH = dStartX > dEndX;
            boolean bIsInvertedV = dStartY > dEndY;

            // set the x, y, width, and height
            double dX = !bIsInvertedH ? dStartX: dEndX;
            double dY = !bIsInvertedV ? dStartY : dEndY;
            double dWidth = !bIsInvertedH ? (dEndX - dStartX) : (dStartX - dEndX);
            double dHeight = !bIsInvertedV ? (dEndY - dStartY) : (dStartY - dEndY);

            // update rectangle
            mRectangle.setX(dX);
            mRectangle.setY(dY);
            mRectangle.setWidth(dWidth);
            mRectangle.setHeight(dHeight);
        }
    }

    // **************************************** //
    // **          FILE METHODS              ** //
    // **************************************** //

    // open an image file
    private void openFile() {
        mIsOpenDialogBox = true;

        // create a new file chooser
        FileChooser fileChooser = new FileChooser();

        // create file extension filters and add them to the file chooser
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        fileChooser.getExtensionFilters().addAll(extFilterPNG, extFilterJPG);

        // open the file choose dialog box and try to update with the selected image
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            openImageFile(file);
            updateRecentFileMenu(file);
            mCurrentFile = file;
        }
        mIsOpenDialogBox = false;
    }

    // save action
    private void save(File file) {
        if (file != null) {
            try {
                String sFileExt = getFileExtension(file);
                Image currentImage = getSnapshot();
                BufferedImage bufferedImage = SwingFXUtils.fromFXImage(currentImage, null);
                ImageIO.write(bufferedImage, sFileExt, file);
                mCurrentFile = file;
            } catch (Exception e) {
                System.out.println("there was an error saving the image file: ");
                System.out.println("  " + e);
            }
        } else {
            saveAs();
        }
    }

    // save an image file using save as
    private void saveAs() {
        mIsOpenDialogBox = true;

        // create a new file chooser
        FileChooser fileChooser = new FileChooser();

        // create file extension filters and add them to the file chooser
//        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterPNG);

        // open the file choose dialog box and try to update with the selected image
        File file = fileChooser.showSaveDialog(null);

        // only try to save if file
        if (file != null) {
            updateRecentFileMenu(file);
            save(file);
        }
        mIsOpenDialogBox = false;
    }

    // http://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
    private String getFileExtension(File file) {
        String sExtension = "";
        String fileName = file.getName();

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            sExtension = fileName.substring(i+1);
        }
        return sExtension;
    }

    // get help support
    private void getHelp() {
        String Help = "https://xkcd.com/979/";
        if(Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().browse(new URI(Help));
            } catch (Exception e) {
                System.out.println("Error opening web browser: ");
                System.out.println("  " + e);
            }
        }
    }

    // get about info
    private void getAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Picture This... " + "\n" + "by Ramon Rodriguez");
        mIsOpenDialogBox = true;
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> mIsOpenDialogBox = false);
    }

    // update the recent file menu
    private void updateRecentFileMenu(File file) {
        // add file to recent file list & update menu
        CommandCenter.getInstance().addRecentFile(file);

        // get the list of recently opened files & put menu placeholders into array
        List<File> recentFiles = CommandCenter.getInstance().getRecentFiles();
        MenuItem[] recentFileMenuItems = {menuRecentFile1, menuRecentFile2, menuRecentFile3, menuRecentFile4, menuRecentFile5};

        // add file name to placeholder
        int iFileCount = recentFiles.size();
        for (int i = 0; i < iFileCount; i++) {
            recentFileMenuItems[i].setText(recentFiles.get(i).getName());
            recentFileMenuItems[i].setVisible(true);
        }

        // set recent file menu as visible
        menuOpenRecent.setVisible(true);
    }

    // hde recent file menu & sub items
    private void hideRecentFileMenu() {
        menuRecentFile1.setVisible(false);
        menuRecentFile2.setVisible(false);
        menuRecentFile3.setVisible(false);
        menuRecentFile4.setVisible(false);
        menuRecentFile5.setVisible(false);
        menuOpenRecent.setVisible(false);
    }

    // try to read an image file
    private void openImageFile(File file) {
        try {
            // read the file & convert to FX Image object type
            BufferedImage bufferedImage = ImageIO.read(file);
            Image newImage = SwingFXUtils.toFXImage(bufferedImage, null);

            resetEffectsSliders();
            removeSelection();

            // update the application to display the image
            mImageView.setImage(newImage);
            Image currentImage = getSnapshot();

            CommandCenter.getInstance().setImageAndView(currentImage);
            CommandCenter.getInstance().setOriginalImage(currentImage);
            enableStartOver();

            // no undo or redo -> new file
            CommandCenter.getInstance().clearUndoImages();
            CommandCenter.getInstance().clearRedoImages();
            resetAllMenusAndToolbars();

        } catch (Exception e) {
            System.out.println("there was an error loading the image file: ");
            System.out.println("  " + e);
        }
    }

    // **************************************** //
    // **      EDIT (UNDO, REDO) METHODS     ** //
    // **************************************** //

    // undo action
    private void undo() {
        if (CommandCenter.getInstance().hasUndoImage()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addRedoImage(currentImage);
            Image undoImage = CommandCenter.getInstance().getUndoImage();
            resetEffectsSliders();
            CommandCenter.getInstance().setImageAndView(undoImage);
            mImageView.setImage(undoImage);
            enableRedo();
            if (!CommandCenter.getInstance().hasUndoImage()) {
                disableUndo();
            }
        }
    }

    // redo action
    private void redo() {
        System.out.println("redo image added");
        if (CommandCenter.getInstance().hasRedoImage()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addUndoImage(currentImage);
            Image redoImage = CommandCenter.getInstance().getRedoImage();
            resetEffectsSliders();
            CommandCenter.getInstance().setImageAndView(redoImage);
            mImageView.setImage(redoImage);
            enableUndo();
            if (!CommandCenter.getInstance().hasRedoImage()) {
                disableRedo();
            }
        }
    }

    // start over with original image
    private void startOver() {
        resetEffectsSliders();
        Image originalImage = CommandCenter.getInstance().getOriginalImage();
        CommandCenter.getInstance().setImageAndView(originalImage);
        mImageView.setImage(originalImage);
        CommandCenter.getInstance().clearRedoImages();
        CommandCenter.getInstance().clearUndoImages();
        disableUndo();
        disableRedo();
    }

    // **************************************** //
    // **     HIDE & SHOW TOOLBAR METHODS    ** //
    // **************************************** //

    private void resetAllMenusAndToolbars() {
        if (tgTools.getSelectedToggle() != null) {
            tgTools.getSelectedToggle().setSelected(false);
        }
        tgbSelectArea.setSelected(false);
        tgbEffects.setSelected(false);
        hideRecentFileMenu();
        hidePenAndEffectsMenu();
        disableRedo();
        disableUndo();
        enableStartOver();
    }

    // show the pen tool details
    private void showPenDetails() {
        hideEffectsDetails();
        tbPenDetails.setManaged(true);
        tbPenDetails.setVisible(true);
        updatePenShapesColor();
    }

    // hide pen details
    private void hidePenDetails() {
        tbPenDetails.setVisible(false);
        tbPenDetails.setManaged(false);
    }

    // show the effects
    private void showEffectsDetails() {
        hidePenDetails();
        tbEffects.setManaged(true);
        tbEffects.setVisible(true);
    }

    // hide the effects
    private void hideEffectsDetails() {
        tbEffects.setVisible(false);
        tbEffects.setManaged(false);
    }

    // hide pen and effects menus
    private void hidePenAndEffectsMenu() {
        hidePenDetails();
        hideEffectsDetails();
    }

    // enable undo buttons
    private void enableUndo() {
        menuUndo.setDisable(false);
        btnUndo.setDisable(false);
    }

    // disable undo buttons
    private void disableUndo() {
        menuUndo.setDisable(true);
        btnUndo.setDisable(true);
    }

    // enable redo buttons
    private void enableRedo() {
        menuRedo.setDisable(false);
        btnRedo.setDisable(false);
    }

    // disable redo buttons
    private void disableRedo() {
        menuRedo.setDisable(true);
        btnRedo.setDisable(true);
    }

    // enable start over buttons
    private void enableStartOver() {
        menuStartOver.setDisable(false);
        btnStartOver.setDisable(false);
    }


    // **************************************** //
    // **       PEN TOOLBAR & TOOLTIPS       ** //
    // **************************************** //

    private void updatePenShapesColor() {
        mCircle.setFill(mColor);
        mSquare.setFill(mColor);
    }

    private void setPenShapeIcons() {
        updatePenShapesColor();
        tgbCircle.setGraphic(mCircle);

        tgbSquare.setGraphic(mSquare);
    }

    private void assignToolTips() {
        cpkColor.setTooltip(ttColorPicker);
        tgbDropper.setTooltip(ttDropper);
        tgbBucket.setTooltip(ttBucket);
        tgbPen.setTooltip(ttPen);
        btnUndo.setTooltip(ttUndo);
        btnRedo.setTooltip(ttRedo);
        btnStartOver.setTooltip(ttStartOver);
        tgbSelectArea.setTooltip(ttSelectArea);
        tgbEffects.setTooltip(ttEffects);
        btnGreyscale.setTooltip(ttGreyscale);
        btnSepia.setTooltip(ttSepia);
        btnInvert.setTooltip(ttInvert);
        btnMonochrome.setTooltip(ttMonochrome);
        btnGlow.setTooltip(ttGlow);
    }

}
