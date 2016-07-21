package imageShop;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.MouseEvent;
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
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    private enum Pen {
        CIR, SQR, FIL, OTHER
    }

    private enum FilterStyle {
        SAT, DRK, OTHER
    }

    private int penSize = 50;
    private Pen penStyle = Pen.CIR;
    private FilterStyle mFilterStyle = FilterStyle.DRK;

    private Rectangle mRectangle;
    private double xPos, yPos, hPos, wPos;
    private boolean mUserIsCurrentlySelecting = false;
    private Color mColor = Color.WHITE;


    private ArrayList<Shape> removeShapes = new ArrayList<>(1000);

    @FXML private AnchorPane mAnchorPane;
    @FXML private ImageView mImageView;
    @FXML private ToggleGroup mToggleGroup = new ToggleGroup();
    @FXML private ToggleButton tgbSquare;
    @FXML private ToggleButton tgbCircle;
    @FXML private ToggleButton tgbFilter;
    // ADD MORE SHAPES???

    @FXML private MenuButton menuTouchUp;

    @FXML private ColorPicker cpkColor;
    @FXML private Slider sldSize;
    @FXML private Slider sldBrightness;
    @FXML private Slider sldContrast;
    @FXML private Slider sldHue;
    @FXML private Slider sldSaturate;

    @FXML private Button btnResetBrightness;
    @FXML private Button btnResetContrast;
    @FXML private Button btnResetHue;
    @FXML private Button btnResetSaturate;



    @FXML void menuOpenAction(ActionEvent event) {

        CommandCenter.getInstance().setImageView(this.mImageView);

        // create a new file chooser
        FileChooser fileChooser = new FileChooser();

        // create file extension filters and add them to the file chooser
        FileChooser.ExtensionFilter extFilterJPG = new FileChooser.ExtensionFilter("JPG files (*.jpg)", "*.JPG");
        FileChooser.ExtensionFilter extFilterPNG = new FileChooser.ExtensionFilter("PNG files (*.png)", "*.PNG");
        fileChooser.getExtensionFilters().addAll(extFilterJPG, extFilterPNG);

        // open the file choose dialog box and try to update with the selected image
        File file = fileChooser.showOpenDialog(null);
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            ImageData imageData = new ImageData(image, 0, 0, 0, 0);
            CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
            //CommandCenter.getInstance().setImageDataAndRefreshView__OLD(imageData);
        } catch (Exception e) {
           // Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, e);

            System.out.println("there was an error loading the image file: ");
            System.out.println("  " + e);
        }
    }

    @FXML void menuCloseAction(ActionEvent event) {
        CommandCenter.getInstance().closeImage();
    }

    @FXML void menuSaveAction(ActionEvent event) {
        throw new RuntimeException("TO DO");
    }

    @FXML void menuSaveAsAction(ActionEvent event) {
        throw new RuntimeException("TO DO");
    }

    @FXML void menuQuitAction() {
        System.exit(0);
    }

    @FXML void menuUndoAction(ActionEvent event) {
        CommandCenter.getInstance().undo();
        ImageData imageData = CommandCenter.getInstance().getImageData();
        sldBrightness.setValue(imageData.getBrightness());
        sldContrast.setValue(imageData.getContrast());
        sldHue.setValue(imageData.getHue());
        sldSaturate.setValue(imageData.getSaturation());
    }

    @FXML void menuRedoAction(ActionEvent event) {
        CommandCenter.getInstance().redo();
        ImageData imageData = CommandCenter.getInstance().getImageData();
        sldBrightness.setValue(imageData.getBrightness());
        sldContrast.setValue(imageData.getContrast());
        sldHue.setValue(imageData.getHue());
        sldSaturate.setValue(imageData.getSaturation());
    }

    // undo
    // redo
    // saturate
    // grayscale
    // invert
    // open last


    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {

        // set the toggles
        tgbCircle.setToggleGroup(mToggleGroup);
        tgbSquare.setToggleGroup(mToggleGroup);
        tgbFilter.setToggleGroup(mToggleGroup);
        tgbCircle.setSelected(true);

        // update pen property based on toggle selection
        mToggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (newValue == tgbCircle) {
                    penStyle = Pen.CIR;
                } else if (newValue == tgbSquare) {
                    penStyle = Pen.SQR;
                } else if (newValue == tgbFilter) {
                    penStyle = Pen.FIL;
                } else {
                    penStyle = Pen.CIR;
                }
            }
        });

        // action when mouse pressed: get x,y of mouse if pressed when using a filter
        mImageView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (penStyle == Pen.FIL) {
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
            }
        });

        // action when mouse released:
        mImageView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (penStyle == Pen.CIR || penStyle == Pen.SQR) {
                    ImageData imageData = getImageData();
                    CommandCenter.getInstance().updateBackImageData();
                    CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
                    //CommandCenter.getInstance().setImageDataAndRefreshView__OLD(imageData);
                    mAnchorPane.getChildren().removeAll(removeShapes);
                    removeShapes.clear();

                } else if (penStyle == Pen.FIL && mUserIsCurrentlySelecting) {

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

                    System.out.println("finished rectangle");

                }

                mouseEvent.consume();
            }
        });

        // draw if mouse is dragged while pen is selected
        mImageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if (penStyle == Pen.CIR || penStyle == Pen.SQR) {
                    xPos = mouseEvent.getX();
                    yPos = mouseEvent.getY();
                    Shape penShape = getPenShape();
                    mAnchorPane.getChildren().add(penShape);
                    removeShapes.add(penShape);
                } else if (penStyle == Pen.FIL && mUserIsCurrentlySelecting) {
                    hPos = mouseEvent.getX();
                    wPos = mouseEvent.getY();
                    updateRectangle(xPos, yPos, hPos, wPos);
                }
                mouseEvent.consume();
            }
        });

        // update color based on color picker
        cpkColor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mColor = cpkColor.getValue();
            }
        });

        // update size of pen based on slider
        sldSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double temp = (Double) newValue;
                penSize = (int) Math.round(temp);
            }
        });

        // store the current image when saturation slider is clicked (for undo)
        sldSaturate.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageData imageData = getImageData();
                CommandCenter.getInstance().addBackImageData(imageData);
            }
        });

        // update the image in command center when saturation slider is released
        sldSaturate.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("slider released");
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
            }
        });

        // change the view of the image when slider value is adjusted
        sldSaturate.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double dSliderValue = (Double) newValue / 100;
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setSaturation(dSliderValue);
                mImageView.setEffect(colorAdjust);
            }
        });

        // reset saturation to zero
        btnResetSaturate.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // store the current image
                CommandCenter.getInstance().updateBackImageData();

                // reset to zero and update
                sldSaturate.setValue(0.0);
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);

                // keep menu displayed
                menuTouchUp.show();
            }
        });

        // store the current image when saturation slider is clicked (for undo)
        sldBrightness.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageData imageData = getImageData();
                CommandCenter.getInstance().addBackImageData(imageData);
            }
        });

        // update the image in command center when brightness slider is released
        sldBrightness.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("slider released");
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
            }
        });

        sldBrightness.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double dSliderValue = (Double) newValue / 100;
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setBrightness(dSliderValue);
                mImageView.setEffect(colorAdjust);
            }
        });

        btnResetBrightness.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // store the current image
                CommandCenter.getInstance().updateBackImageData();

                // reset to zero and update
                sldBrightness.setValue(0.0);
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);

                // keep menu displayed
                menuTouchUp.show();
            }
        });

        // store the current image when saturation slider is clicked (for undo)
        sldContrast.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageData imageData = getImageData();
                CommandCenter.getInstance().addBackImageData(imageData);
            }
        });

        // update the image in command center when contrast slider is released
        sldContrast.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("slider released");
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
            }
        });

        sldContrast.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double dSliderValue = (Double) newValue / 100;
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setContrast(dSliderValue);
                mImageView.setEffect(colorAdjust);
            }
        });

        btnResetContrast.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // store the current image
                CommandCenter.getInstance().updateBackImageData();

                // reset to zero and update
                sldContrast.setValue(0.0);
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);

                // keep menu displayed
                menuTouchUp.show();

            }
        });


        // store the current image when saturation slider is clicked (for undo)
        sldHue.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ImageData imageData = getImageData();
                CommandCenter.getInstance().addBackImageData(imageData);
            }
        });

        // update the image in command center when hue slider is released
        sldHue.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                System.out.println("slider released");
                ImageData imageData = getImageData();
                CommandCenter.getInstance().setImageDataAndRefreshView(imageData);
            }
        });

        sldHue.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double dSliderValue = (Double) newValue / 100;
                ColorAdjust colorAdjust = new ColorAdjust();
                colorAdjust.setHue(dSliderValue);
                mImageView.setEffect(colorAdjust);
            }
        });

        btnResetHue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // store the current image
                CommandCenter.getInstance().updateBackImageData();

                // reset to zero and update
                sldHue.setValue(0.0);
                //ImageData imageData = getImageData();
                //CommandCenter.getInstance().setImageDataAndRefreshView(imageData);

                // keep menu displayed
                menuTouchUp.show();

            }
        });


    }

    private ImageData getImageData() {
        // get the image
        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setViewport(new Rectangle2D(0, 0, mImageView.getFitWidth(), mImageView.getFitHeight()));
        Image snapshot = mAnchorPane.snapshot(snapshotParameters, null);

        // get the slider values for image qualities
        double dBrightness = sldBrightness.getValue();
        double dContrast = sldContrast.getValue();
        double dHue = sldHue.getValue();
        double dSaturation = sldSaturate.getValue();

        // return an image data object
        return new ImageData(snapshot, dBrightness, dContrast, dHue, dSaturation);
    }

    // helper method to get the shape of the pen
    private Shape getPenShape() {
        Shape penShape = null;
        if (penStyle == Pen.CIR) {
            penShape = new Circle(xPos, yPos, penSize);
        } else if (penStyle == Pen.SQR) {
            penShape = new Rectangle(xPos, yPos, penSize, penSize);
        }
        penShape.setFill(mColor);
        return penShape;
    }

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


}
