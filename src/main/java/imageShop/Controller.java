package imageShop;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.*;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import javafx.scene.paint.Paint;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private enum PenShape {CIRCLE, SQUARE}
    private enum DrawTool {BUCKET, PEN}
    private enum ActionSet {DRAW, EFFECTS, FILTERS}


    private static final String DROPPER_IMAGE = "/images/dropper.png";
    private static final String BUCKET_IMAGE = "/images/bucket.png";
    private static final double DOUBLE_THRESHOLD = 0.1;
    private static final double DEFAULT_PEN_SIZE = 20.0;
    private static final double DEFAULT_PEN_PRESSURE = 75.0;
    private static final double DEFAULT_EFFECTS_VALUE = 0.0;


    private ActionSet mActionSet;
    private Color mColor = Color.rgb(25, 150, 255);
    private DrawTool mDrawTool;
    private PenShape mPenShape;
    private int mPenSize;
    private double mPenPressure;
    private Rectangle mRectangle;
    private double xPos, yPos, hPos, wPos;
    private boolean mUserIsCurrentlySelecting = false;
    private boolean mUserIsPickingColor = false;

    private ColorAdjust mColorAdjust = new ColorAdjust();
    private ArrayList<Shape> removeShapes = new ArrayList<>(1000);

    @FXML private MenuItem menuNew;
    @FXML private MenuItem menuOpen;
    @FXML private Menu menuOpenRecent;
    @FXML private MenuItem menuRecentFile1;
    @FXML private MenuItem menuRecentFile2;
    @FXML private MenuItem menuRecentFile3;
    @FXML private MenuItem menuRecentFile4;
    @FXML private MenuItem menuRecentFile5;
    @FXML private MenuItem menuClose;
    @FXML private MenuItem menuSave;
    @FXML private MenuItem menuSaveAs;
    @FXML private MenuItem menuQuit;
    @FXML private MenuItem menuUndo;
    @FXML private MenuItem menuRedo;
    @FXML private MenuItem menuStartOver;
    @FXML private MenuItem menuHelp;
    @FXML private MenuItem menuAbout;

    @FXML private ToolBar tbMain;
    @FXML private ToggleButton tgbDraw;
    @FXML private ToggleButton tgbEffects;
    @FXML private ToggleButton tgbFilters;
    @FXML private Button btnUndo;
    @FXML private Button btnRedo;
    @FXML private Button btnStartOver;

    @FXML private Pane pnTools;

    @FXML private Group grpDraw;
    @FXML private Group grpColor;
    @FXML private ToggleButton tgbPickColor;
    @FXML private ColorPicker cpkColor;
    @FXML private Group grpDrawTool;
    @FXML private ToggleButton tgbBucket;
    @FXML private ToggleButton tgbPen;
    @FXML private Group grpPenDetails;
    @FXML private ToggleButton tgbCircle;
    @FXML private ToggleButton tgbSquare;
    @FXML private Slider sldPenSize;
    @FXML private Button btnResetPenSize;
    @FXML private Slider sldPenPressure;
    @FXML private Button btnResetPenPressure;

    @FXML private Group grpEffects;
    @FXML private ToggleButton tgbSelectArea;
    @FXML private Button btnResetEffects;
    @FXML private Slider sldBrightness;
    @FXML private Button btnResetBrightness;
    @FXML private Slider sldContrast;
    @FXML private Button btnResetContrast;
    @FXML private Slider sldHue;
    @FXML private Button btnResetHue;
    @FXML private Slider sldSaturate;
    @FXML private Button btnResetSaturate;

    @FXML private Group grpFilters;
    @FXML private Button btnGreyscale;
    @FXML private Button btnSepia;
    @FXML private Button btnInvert;
    @FXML private Button btnMonochrome;

    @FXML private ToggleGroup tgActionSet = new ToggleGroup();
    @FXML private ToggleGroup tgDrawTool = new ToggleGroup();
    @FXML private ToggleGroup tgPenShape = new ToggleGroup();

    @FXML private AnchorPane mAnchorPane;
    @FXML private ImageView mImageView;


    @FXML void menuOpenAction(ActionEvent event) {
        // create a new file chooser
        FileChooser fileChooser = new FileChooser();

        // create file extension filters and add them to the file chooser
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        // open the file choose dialog box and try to update with the selected image
        File file = fileChooser.showOpenDialog(null);
        openImageFile(file);
        updateRecentFileMenu(file);
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
        throw new RuntimeException("TO DO");
    }

    @FXML void menuSaveAsAction(ActionEvent event) {
            // create a new file chooser
            FileChooser fileChooser = new FileChooser();

            // create file extension filters and add them to the file chooser
            FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
            FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
            fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

            // open the file choose dialog box and try to update with the selected image
            File file = fileChooser.showSaveDialog(null);

            if (file != null) {
                try {
                    FileWriter fileWriter = null;
                    fileWriter = new FileWriter(file);
                    fileWriter.write(file.getName());
                    fileWriter.close();
                } catch (Exception e) {
                    System.out.println("there was an error loading the image file: ");
                    System.out.println("  " + e);
                }
            }
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



    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        // **************************************** //
        // **                SETUP               ** //
        // **************************************** //

        // set a back image
        Image initialImage = getSnapshot();
        mImageView.setImage(initialImage);
        CommandCenter.getInstance().setImageView(mImageView);
        CommandCenter.getInstance().setOriginalImage(initialImage);
        CommandCenter.getInstance().setImageAndView(initialImage);

        // assign toggle groups
        tgbDraw.setToggleGroup(tgActionSet);
        tgbEffects.setToggleGroup(tgActionSet);
        tgbFilters.setToggleGroup(tgActionSet);
        tgbBucket.setToggleGroup(tgDrawTool);
        tgbPen.setToggleGroup(tgDrawTool);
        tgbCircle.setToggleGroup(tgPenShape);
        tgbSquare.setToggleGroup(tgPenShape);

        // assign default color
        cpkColor.setValue(mColor);

        // hide open recent, hide tools, disable undo/edo
        hideRecentFileMenu();
        hideToolPanel();
        disableRedo();
        disableUndo();
        enableStartOver();

        // **************************************** //
        // **            TOGGLE GROUPS           ** //
        // **************************************** //

        // ActionSet toggle group
        tgActionSet.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbDraw) {
                mActionSet = ActionSet.DRAW;
                if (mDrawTool == DrawTool.BUCKET) {
                    tgbDraw.setSelected(true);
                } else {
                    tgbPen.setSelected(true);
                    grpPenDetails.setVisible(true);
                }
                showDrawGroup();
            } else if (newValue == tgbEffects) {
                mActionSet = ActionSet.EFFECTS;
                showEffectsGroup();
            } else if (newValue == tgbFilters) {
                mActionSet = ActionSet.FILTERS;
                showFilterGroup();
            }
        });

        // DrawTool toggle group
        tgDrawTool.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbBucket) {
                mDrawTool = DrawTool.BUCKET;
                grpPenDetails.setVisible(false);
            } else if (newValue == tgbPen) {
                mDrawTool = DrawTool.PEN;
                grpPenDetails.setVisible(true);
                if (mPenShape == PenShape.SQUARE) {
                    tgbSquare.setSelected(true);
                } else {
                    tgbCircle.setSelected(true);
                }
            }
        });

        // PenShape toggle group
        tgPenShape.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbCircle) {
                mPenShape = PenShape.CIRCLE;
            } else if (newValue == tgbSquare) {
                mPenShape = PenShape.SQUARE;
            }
        });



        // **************************************** //
        // **            COLOR ACTIONS           ** //
        // **************************************** //

        // update color based on color picker menu dropdown
        cpkColor.setOnAction(event -> mColor = cpkColor.getValue());

        // pick color using dropper
        tgbPickColor.selectedProperty().addListener((observable, oldValue, newValue) -> {
            mUserIsPickingColor = newValue;
        });


        // **************************************** //
        // **            MOUSE ACTIONS           ** //
        // **************************************** //

        // mouse enters area
        mImageView.addEventFilter(MouseEvent.MOUSE_ENTERED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW && mUserIsPickingColor) {
                setMouseToDropper(mouseEvent);
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.BUCKET) {
                setMouseToBucket(mouseEvent);
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.PEN && mPenShape != null) {
                setMouseToPenShape(mouseEvent);
            }
            mouseEvent.consume();
        });

        // mouse pressed
        mImageView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW && mUserIsPickingColor) {
                pickColorFromDropper(mouseEvent);
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.BUCKET) {
                fillFromBucket(mouseEvent);
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.PEN && mPenShape != null) {
                drawPen(mouseEvent);
            } else if (mActionSet == ActionSet.EFFECTS && tgbSelectArea.isSelected()) {
                if (mUserIsCurrentlySelecting) {
                    removeSelection();
                } else {
                    startSelection(mouseEvent);
                }
            }
            mouseEvent.consume();
        });

        // mouse dragged
        mImageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW &&  mDrawTool == DrawTool.PEN && mPenShape != null) {
               drawPen(mouseEvent);
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.BUCKET) {
                fillFromBucket(mouseEvent);
            } else if (mActionSet == ActionSet.EFFECTS && mUserIsCurrentlySelecting) {
                updateRectangle(mouseEvent);
            }
            mouseEvent.consume();
        });

        // mouse released:
        mImageView.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.PEN && mPenShape != null) {
                CommandCenter.getInstance().storeLastImageAsUndo();
                enableUndo();

                Image currentImage = getSnapshot();
                resetEffectsSliders();
                CommandCenter.getInstance().setImageAndView(currentImage);
                mAnchorPane.getChildren().removeAll(removeShapes);
                removeShapes.clear();
            } else if (mActionSet == ActionSet.DRAW && mDrawTool == DrawTool.BUCKET) {
                CommandCenter.getInstance().storeLastImageAsUndo();
                enableUndo();

                Image currentImage = getSnapshot();
                resetEffectsSliders();
                CommandCenter.getInstance().setImageAndView(currentImage);
                //mAnchorPane.getChildren().removeAll(removeShapes);
                //removeShapes.clear();

            } else if (mActionSet == ActionSet.EFFECTS && mUserIsCurrentlySelecting) {
                updateRectangle(mouseEvent);

//                    Image currentImage = CommandCenter.getInstance().getImage();
//                    Image transformImage = currentImage;
//
//                    if (mFilterStyle == FilterStyle.DRK) {
//                        transformImage = CommandCenter.getInstance().transformSelection(currentImage,
//                                (x, y, c) -> (x > xPos && x < wPos) && (y > yPos && y < hPos) ?
//                                        c.deriveColor(0.0, 1.0, 0.5, 1.0) : c);
//                    } else if (mFilterStyle == FilterStyle.SAT) {
//                        transformImage = CommandCenter.getInstance().transformSelection(currentImage,
//                                (x, y, c) -> (x > xPos && x < wPos) && (y > yPos && y < hPos) ?
//                                        c.deriveColor(0.0, 1.0 / 0.1, 1.0, 1.0) : c);
//
//                    }
//                    CommandCenter.getInstance().setImageDataAndRefreshView__OLD(transformImage);

            }
            mouseEvent.consume();
        });

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
            Controller.this.updateColorAdjustEffect();
        });

        // **************************************** //
        // **  RESET BUTTONS FOR EFFECTS SLIDERS ** //
        // **************************************** //

        btnResetBrightness.setOnAction(event -> sldBrightness.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetContrast.setOnAction(event -> sldContrast.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetHue.setOnAction(event -> sldHue.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetSaturate.setOnAction(event -> sldSaturate.setValue(DEFAULT_EFFECTS_VALUE));
        btnResetEffects.setOnAction(event -> resetEffectsSliders());

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
    }


    // grayscale filter
    private void applyGrayscaleFilter() {
        CommandCenter.getInstance().storeLastImageAsUndo();
        enableUndo();
        WritableImage grayscaleImage = new WritableImage((int) mImageView.getImage().getWidth(), (int) mImageView.getImage().getHeight());
        PixelWriter pixelWriter = grayscaleImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth(); x++) {
            for (int y = 0; y < mImageView.getImage().getHeight(); y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                pixelWriter.setColor(x, y, currentColor.grayscale());
            }
        }
        resetEffectsSliders();
        CommandCenter.getInstance().setImageAndView(grayscaleImage);
        mAnchorPane.getChildren().removeAll(removeShapes);
        removeShapes.clear();
    }

    // invert filter
    private void applyInvertFilter() {
        CommandCenter.getInstance().storeLastImageAsUndo();
        enableUndo();
        WritableImage inverseImage = new WritableImage((int) mImageView.getImage().getWidth(), (int) mImageView.getImage().getHeight());
        PixelWriter pixelWriter = inverseImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth(); x++) {
            for (int y = 0; y < mImageView.getImage().getHeight(); y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                pixelWriter.setColor(x, y, currentColor.invert());
            }
        }
        resetEffectsSliders();
        CommandCenter.getInstance().setImageAndView(inverseImage);
        mAnchorPane.getChildren().removeAll(removeShapes);
        removeShapes.clear();
    }

    // sepia filter
    private void applySepiaFilter() {
        CommandCenter.getInstance().storeLastImageAsUndo();
        enableUndo();

        SepiaTone sepiaTone = new SepiaTone();
        sepiaTone.setLevel(0.70);
        mImageView.setEffect(sepiaTone);
        Image currentImage = getSnapshot();

        resetEffectsSliders();

        CommandCenter.getInstance().setImageAndView(currentImage);
        mAnchorPane.getChildren().removeAll(removeShapes);
        removeShapes.clear();
    }

    // sepia filter
    private void applyMonoFilter() {
        double monoThreshold = 1.0;
        CommandCenter.getInstance().storeLastImageAsUndo();
        enableUndo();
        WritableImage monoImage = new WritableImage((int) mImageView.getImage().getWidth(), (int) mImageView.getImage().getHeight());
        PixelWriter pixelWriter = monoImage.getPixelWriter();
        for (int x = 0; x < mImageView.getImage().getWidth(); x++) {
            for (int y = 0; y < mImageView.getImage().getHeight(); y++) {
                Color currentColor = mImageView.getImage().getPixelReader().getColor(x, y);
                Color newColor = (currentColor.getRed() + currentColor.getRed() + currentColor.getBlue() < monoThreshold) ?
                        Color.BLACK : Color.WHITE;

                    pixelWriter.setColor(x, y, newColor);
            }
        }
        resetEffectsSliders();
        CommandCenter.getInstance().setImageAndView(monoImage);
        mAnchorPane.getChildren().removeAll(removeShapes);
        removeShapes.clear();
    }

    // **************************************** //
    // **           HELPER METHODS           ** //
    // **************************************** //

    // update the color adjust to current slider settings
    private void updateColorAdjustEffect() {
        mColorAdjust.setBrightness(sldBrightness.getValue() / 100.0);
        mColorAdjust.setContrast(sldContrast.getValue() / 100.0);
        mColorAdjust.setHue(sldHue.getValue() / 100.0);
        mColorAdjust.setSaturation(sldSaturate.getValue() / 100.0);
        mImageView.setEffect(mColorAdjust);
    }

    private void updateColorAdjustEffectForSelection() {
        mColorAdjust.setBrightness(sldBrightness.getValue() / 100.0);
        mColorAdjust.setContrast(sldContrast.getValue() / 100.0);
        mColorAdjust.setHue(sldHue.getValue() / 100.0);
        mColorAdjust.setSaturation(sldSaturate.getValue() / 100.0);
        Image selectionImage = getSnapshotForSelection();
        ImageView selection = new ImageView();
        selection.setImage(selectionImage);
        selection.setEffect(mColorAdjust);
    }

    // check if double is zero -> used for sliders
    private boolean isZero(double value) {
        return value >= -DOUBLE_THRESHOLD && value <= DOUBLE_THRESHOLD;

    }

    // get a "snap" of screen's current image
    private Image getSnapshot() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        //snapshotParameters.setViewport(new Rectangle2D(0, 0, mImageView.getFitWidth(), mImageView.getFitHeight()));
        return mAnchorPane.snapshot(snapshotParameters, null);
    }

    // get snapshot from rectangle selection
    private Image getSnapshotForSelection() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(new Rectangle2D(mRectangle.getX(), mRectangle.getY(), mRectangle.getWidth(), mRectangle.getHeight()));
        return mAnchorPane.snapshot(snapshotParameters, null);
    }

    // set effects sliders to 0 (base)
    private void resetEffectsSliders() {
        sldBrightness.setValue(DEFAULT_EFFECTS_VALUE);
        sldContrast.setValue(DEFAULT_EFFECTS_VALUE);
        sldHue.setValue(DEFAULT_EFFECTS_VALUE);
        sldSaturate.setValue(DEFAULT_EFFECTS_VALUE);
    }

    // change mouse icon to dropper
    private void setMouseToDropper(MouseEvent mouseEvent) {
        Image dropperImage = new Image(DROPPER_IMAGE, 25, 25, false, false);
        double dBottom = dropperImage.getHeight();
        double dLeft = 0;
        ((Node) mouseEvent.getSource()).setCursor(new ImageCursor(dropperImage, dLeft, dBottom));
    }

    // get the color from image using dropper
    public void pickColorFromDropper(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        Color pixelColor = mImageView.getImage().getPixelReader().getColor((int) xPos, (int) yPos);
        cpkColor.setValue(pixelColor);
        mUserIsPickingColor = false;
        tgbPickColor.setSelected(false);
        ((Node) mouseEvent.getSource()).setCursor(Cursor.DEFAULT);
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
        penShape.setFill(mColor);
        penShape.setOpacity(mPenPressure);
        return penShape;
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

    // change mouse icon to bucket
    private void setMouseToBucket(MouseEvent mouseEvent) {
        Image dropperImage = new Image(BUCKET_IMAGE, 40, 40, false, false);
        double dBottomish = dropperImage.getHeight() * 0.70;
        double dRightish = dropperImage.getWidth() * 0.90;
        ((Node) mouseEvent.getSource()).setCursor(new ImageCursor(dropperImage, dRightish, dBottomish));
    }

    // fill: paint
    private void fillFromBucket(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        Color targetColor = mImageView.getImage().getPixelReader().getColor((int) xPos, (int) yPos);
        fillRecursive((int) xPos, (int) yPos, targetColor);
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

    // recursive call to fill across all pixels that touch and are same color
    private void fillRecursive(int iPixelX, int iPixelY, Color targetColor) {
        if (isPixelInBounds(iPixelX, iPixelY) && isMatchingColorPixel(iPixelX, iPixelY, targetColor)) {


        // TODO

        }
    }

    // start a new rectangle selection
    private void startSelection(MouseEvent mouseEvent) {
        xPos = mouseEvent.getX();
        yPos = mouseEvent.getY();
        mRectangle = new Rectangle();
        mRectangle.setFill(Color.SNOW);
        mRectangle.setStroke(Color.WHITE);
        mRectangle.setOpacity(0.15);
        mAnchorPane.getChildren().add(mRectangle);
        mUserIsCurrentlySelecting = true;
    }

    // remove rectangle selection
    private void removeSelection() {
        mAnchorPane.getChildren().remove(mRectangle);
        mUserIsCurrentlySelecting = false;
    }

    // update rectangle: used to invert shape based on mouse position
    private void updateRectangle(MouseEvent mouseEvent) {
        // update end corner based on mouse
        hPos = mouseEvent.getX();
        wPos = mouseEvent.getY();

        // check if x and y are inverted
        boolean bIsInvertedH = wPos < xPos;
        boolean bIsInvertedV = hPos < yPos;

        // set the x, y, width, and height
        double dX = !bIsInvertedH ? xPos: wPos;
        double dY = !bIsInvertedV ? yPos : hPos;
        double dWidth = !bIsInvertedH ? (wPos - xPos) : (xPos - wPos);
        double dHeight = !bIsInvertedV ? (hPos - yPos) : (yPos - hPos);

        // update rectangle
        mRectangle.setX(dX);
        mRectangle.setY(dY);
        mRectangle.setWidth(dWidth);
        mRectangle.setHeight(dHeight);
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
    public void openImageFile(File file) {
        try {
            // read the file & convert to FX Image object type
            BufferedImage bufferedImage = ImageIO.read(file);
            Image newImage = SwingFXUtils.toFXImage(bufferedImage, null);

            // update the application to display the image
            CommandCenter.getInstance().setImageAndView(newImage);
            CommandCenter.getInstance().setOriginalImage(newImage);
            enableStartOver();

            // no undo or redo -> new file
            CommandCenter.getInstance().clearUndoImages();
            CommandCenter.getInstance().clearRedoImages();
            disableRedo();
            disableUndo();
        } catch (Exception e) {
            System.out.println("there was an error loading the image file: ");
            System.out.println("  " + e);
        }
    }

    // hide the tool panel
    private void hideToolPanel() {
        pnTools.setVisible(false);
        pnTools.setManaged(false);
    }

    // hide the draw tools
    private void hideDraw() {
        grpDraw.setVisible(false);
        grpColor.setVisible(false);
        grpDrawTool.setVisible(false);
        grpPenDetails.setVisible(false);
    }

    // hide the effects
    private void hideEffects() {
        grpEffects.setVisible(false);
    }

    // hide the filters
    private void hideFilters() {
        grpFilters.setVisible(false);
    }

    // show the draw tools
    private void showDrawGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpDraw.setVisible(true);
        grpColor.setVisible(true);
        grpDrawTool.setVisible(true);
        hideEffects();
        hideFilters();
    }

    // show the effects
    private void showEffectsGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpEffects.setVisible(true);
        hideDraw();
        hideFilters();
    }

    // show the filters
    private void showFilterGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpFilters.setVisible(true);
        hideEffects();
        hideDraw();
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

    // disable start over buttons
    private void disableStartOver() {
        menuStartOver.setDisable(true);
        btnStartOver.setDisable(true);
    }

    // undo action
    private void undo() {
        if (CommandCenter.getInstance().hasUndoImage()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addRedoImage(currentImage);
            Image undoImage = CommandCenter.getInstance().getUndoImage();
            resetEffectsSliders();
            CommandCenter.getInstance().setImageAndView(undoImage);
            enableRedo();
            if (!CommandCenter.getInstance().hasUndoImage()) {
                disableUndo();
            }
        }
    }

    // redo action
    private void redo() {
        if (CommandCenter.getInstance().hasRedoImage()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addUndoImage(currentImage);
            Image redoImage = CommandCenter.getInstance().getRedoImage();
            resetEffectsSliders();
            CommandCenter.getInstance().setImageAndView(redoImage);
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

}
