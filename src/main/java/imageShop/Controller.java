package imageShop;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import java.io.IOException;
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

    private double xPos, yPos, hPos, wPos;
    private Color mColor = Color.WHITE;

    private ArrayList<Shape> removeShapes = new ArrayList<>(1000);

    @FXML private AnchorPane mAnchorPane;
    @FXML private ImageView mImageView;
    @FXML private ToggleGroup mToggleGroup = new ToggleGroup();
    @FXML private ToggleButton tgbSquare;
    @FXML private ToggleButton tgbCircle;
    @FXML private ToggleButton tgbFilter;
    // ADD MORE SHAPES???
    @FXML private ColorPicker cpkColor;
    @FXML private Slider sldrSize;

    @FXML void menuOpenAction(ActionEvent event) {
        // is this line needed?
        // CommandCenter.getInstance().setImageView(this.mImageView);

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
            CommandCenter.getInstance().setImageAndRefreshView(SwingFXUtils.toFXImage(bufferedImage, null));
        } catch (IOException e) {
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
                    xPos = mouseEvent.getX();
                    yPos = mouseEvent.getY();
                }

                mouseEvent.consume();
            }
        });

        // action when mouse released:
        mImageView.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (penStyle == Pen.CIR || penStyle == Pen.SQR) {

                    SnapshotParameters snapshotParameters = new SnapshotParameters();
                    snapshotParameters.setViewport(new Rectangle2D(0, 0, mImageView.getFitWidth(), mImageView.getFitHeight()));
                    Image snapshot = mAnchorPane.snapshot(snapshotParameters, null);
                    CommandCenter.getInstance().setImageAndRefreshView(snapshot);
                    mAnchorPane.getChildren().removeAll(removeShapes);
                    removeShapes.clear();
                } else if (penStyle == Pen.FIL) {

                    wPos = mouseEvent.getX();
                    hPos = mouseEvent.getY();

                    Image transformImage;

                    // update to handle filters
                    // pass filter type as param?

                    if (mFilterStyle == FilterStyle.DRK) {

                    } else if (mFilterStyle == FilterStyle.SAT) {

                    }
                }

                mouseEvent.consume();
            }
        });


        mImageView.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

                if (penStyle == Pen.CIR || penStyle == Pen.SQR) {
                    xPos = mouseEvent.getX();
                    yPos = mouseEvent.getY();
                    Shape shape = getShape();
                    mAnchorPane.getChildren().add(shape);
                    removeShapes.add(shape);
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


        sldrSize.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                double temp = (Double) newValue;
                penSize = (int) Math.round(temp);
            }
        });



    }

    // helper method to get the shape of the pen
    private Shape getShape() {
        Shape shape = null;
        if (penStyle == Pen.CIR) {
            shape = new Circle(xPos, yPos, penSize);
        } else if (penStyle == Pen.SQR) {
            shape = new Rectangle(xPos, yPos, penSize, penSize);
        }
        shape.setFill(mColor);
        return shape;
    }


}
