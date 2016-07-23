package imageShop;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

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

    private static final double DOUBLE_THRESHOLD = 0.1;

    private ActionSet mActionSet;
    private Color mColor = Color.rgb(25, 150, 255);
    private DrawTool mDrawTool;
    private PenShape mPenShape;
    private int mPenSize;
    private Rectangle mRectangle;
    private double xPos, yPos, hPos, wPos;
    private boolean mUserIsCurrentlySelecting = false;

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
        if (CommandCenter.getInstance().hasUndoImage()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addRedoImage(currentImage);
            Image undoImage = CommandCenter.getInstance().getUndoImage();
            resetTouchUpSliders();
            CommandCenter.getInstance().setImageAndView(undoImage);
        }
    }

    @FXML void menuRedoAction(ActionEvent event) {
        if (CommandCenter.getInstance().hasRedoImages()) {
            Image currentImage = getSnapshot();
            CommandCenter.getInstance().addUndoImage(currentImage);
            Image redoImage = CommandCenter.getInstance().getRedoImage();
            resetTouchUpSliders();
            CommandCenter.getInstance().setImageAndView(redoImage);
        }
    }



    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        // set a back image
        this.mImageView.setImage(getSnapshot());
        CommandCenter.getInstance().addUndoImage(getSnapshot());
        CommandCenter.getInstance().setImageView(this.mImageView);

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

        // hide open recent - nothing available yet!
        hideRecentFileMenu();

        // hide the tools pain
        hideToolPanel();

        // no action set yet



        // update ActionSet toggle group
        tgActionSet.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbDraw) {
                mActionSet = ActionSet.DRAW;
                if (mDrawTool == DrawTool.BUCKET) {
                    tgbDraw.setSelected(true);
                } else {
                    tgbPen.setSelected(true);
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

        // update DrawTool toggle group
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

        // update PenShape toggle group
        tgPenShape.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == tgbCircle) {
                mPenShape = PenShape.CIRCLE;
            } else if (newValue == tgbSquare) {
                mPenShape = PenShape.SQUARE;
            }
        });

        // update color based on color picker
        cpkColor.setOnAction(event -> mColor = cpkColor.getValue());

        // update size of pen based on slider
        sldPenSize.valueProperty().addListener((observable, oldValue, newValue) -> {
            mPenSize = (int) newValue;
        });


        // **************************************** //
        // **            MOUSE ACTIONS           ** //
        // **************************************** //

        // action when mouse pressed: get x,y of mouse if pressed when using a filter
        mImageView.addEventFilter(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mActionSet == ActionSet.EFFECTS && tgbSelectArea.isSelected()) {
                if (mUserIsCurrentlySelecting) {
                    mAnchorPane.getChildren().remove(mRectangle);
                    mUserIsCurrentlySelecting = false;
                } else {
                    xPos = mouseEvent.getX();
                    yPos = mouseEvent.getY();
                    mRectangle = new Rectangle();
                    mRectangle.setFill(Color.SNOW);
                    mRectangle.setStroke(Color.WHITE);
                    mRectangle.setOpacity(0.15);
                    mAnchorPane.getChildren().add(mRectangle);
                    mUserIsCurrentlySelecting = true;
                }
            }

            mouseEvent.consume();
        });

        // draw if mouse is dragged while pen is selected
        mImageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW && (mPenShape == PenShape.CIRCLE || mPenShape == PenShape.SQUARE)) {
                xPos = mouseEvent.getX();
                yPos = mouseEvent.getY();
                Shape penShape = Controller.this.getPenShapeShape();
                mAnchorPane.getChildren().add(penShape);
                removeShapes.add(penShape);
            } else if (mActionSet == ActionSet.EFFECTS && mUserIsCurrentlySelecting) {
                hPos = mouseEvent.getX();
                wPos = mouseEvent.getY();
                Controller.this.updateRectangle(xPos, yPos, hPos, wPos);
            }
            mouseEvent.consume();
        });

        // action when mouse released:
        mImageView.addEventFilter(MouseEvent.MOUSE_RELEASED, mouseEvent -> {
            if (mActionSet == ActionSet.DRAW && (mPenShape == PenShape.CIRCLE || mPenShape == PenShape.SQUARE)) {
                CommandCenter.getInstance().storeLastImageAsUndo();
                Image currentImage = getSnapshot();
                resetTouchUpSliders();
                CommandCenter.getInstance().setImageAndView(currentImage);
                mAnchorPane.getChildren().removeAll(removeShapes);
                removeShapes.clear();
            } else if (mActionSet == ActionSet.EFFECTS && mUserIsCurrentlySelecting) {

                wPos = mouseEvent.getX();
                hPos = mouseEvent.getY();

                updateRectangle(xPos, yPos, wPos, hPos);

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
        // ** CHANGE VALUE FOR TOUCH UP SLIDERS  ** //
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
        // ** RESET BUTTONS FOR TOUCH UP SLIDERS ** //
        // **************************************** //

        btnResetBrightness.setOnAction(event -> sldBrightness.setValue(0.0));
        btnResetContrast.setOnAction(event -> sldContrast.setValue(0.0));
        btnResetHue.setOnAction(event -> sldHue.setValue(0.0));
        btnResetSaturate.setOnAction(event -> sldSaturate.setValue(0.0));
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
        selection.setEffect(mColorAdjust);
    }

    // http://stackoverflow.com/questions/18260213/how-to-test-if-a-double-is-zero
    private boolean isZero(double value) {
        return value >= -DOUBLE_THRESHOLD && value <= DOUBLE_THRESHOLD;

    }

    // get a "snap" of screen's current image
    private Image getSnapshot() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(new Rectangle2D(0, 0, mImageView.getFitWidth(), mImageView.getFitHeight()));
        return mAnchorPane.snapshot(snapshotParameters, null);
    }

    private Image getSnapshotForSelection() {
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(new Rectangle2D(mRectangle.getX(), mRectangle.getY(), mRectangle.getWidth(), mRectangle.getHeight()));
        return mAnchorPane.snapshot(snapshotParameters, null);
    }

    // set touch up sliders to 0 (base)
    private void resetTouchUpSliders() {
        sldBrightness.setValue(0.0);
        sldContrast.setValue(0.0);
        sldHue.setValue(0.0);
        sldSaturate.setValue(0.0);
    }

    // get the shape of the pen
    private Shape getPenShapeShape() {
        Shape penShape = null;
        if (this.mPenShape == PenShape.CIRCLE) {
            penShape = new Circle(xPos, yPos, mPenSize);
        } else if (this.mPenShape== PenShape.SQUARE) {
            penShape = new Rectangle(xPos, yPos, mPenSize, mPenSize);
        }
        penShape.setFill(mColor);
        return penShape;
    }

    // update rectangle: used to invert shape based on mouse position
    private void updateRectangle(double dStartX, double dStartY, double dEndX, double dEndY) {

        // check if x and y are inverted
        boolean bIsInvertedH = dEndX < dStartX;
        boolean bIsInvertedV = dEndY < dStartY;

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

        menuOpenRecent.setVisible(true);
    }

    private void hideRecentFileMenu() {
        menuRecentFile1.setVisible(false);
        menuRecentFile2.setVisible(false);
        menuRecentFile3.setVisible(false);
        menuRecentFile4.setVisible(false);
        menuRecentFile5.setVisible(false);
        menuOpenRecent.setVisible(false);
    }


    public void openImageFile(File file) {
        try {
            // read the file & convert to FX Image object type
            BufferedImage bufferedImage = ImageIO.read(file);
            Image newImage = SwingFXUtils.toFXImage(bufferedImage, null);

            // update the application to display the image
            CommandCenter.getInstance().setImageAndView(newImage);
        } catch (Exception e) {
            System.out.println("there was an error loading the image file: ");
            System.out.println("  " + e);
        }
    }

    private void hideToolPanel() {
        pnTools.setVisible(false);
//        pnTools.setStyle("-fx-border-color: lightgrey;");
        pnTools.setManaged(false);
    }

    private void hideDraw() {
        grpDraw.setVisible(false);
        grpColor.setVisible(false);
        grpDrawTool.setVisible(false);
        grpPenDetails.setVisible(false);
    }

    private void hideEffects() {
        grpEffects.setVisible(false);
    }

    private void hideFilters() {
        grpFilters.setVisible(false);
    }

    private void showDrawGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpDraw.setVisible(true);
        grpColor.setVisible(true);
        grpDrawTool.setVisible(true);
        hideEffects();
        hideFilters();
    }

    private void showEffectsGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpEffects.setVisible(true);
        hideDraw();
        hideFilters();
    }

    private void showFilterGroup() {
        pnTools.setManaged(true);
        pnTools.setVisible(true);
        grpFilters.setVisible(true);
        hideEffects();
        hideDraw();
    }
}
