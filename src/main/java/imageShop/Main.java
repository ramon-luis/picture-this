package imageShop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {



    public static void main(String[] args) {
        launch(args);

    }

    public void start(Stage stage) throws Exception {

        String fxmlFile = "/fxml/imageShop.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent rootNode = loader.load(getClass().getResourceAsStream(fxmlFile));
        Scene scene = new Scene(rootNode, 1300, 800);
        scene.getStylesheets().add("/styles/styles.css");
        stage.setTitle("Picture This");
        stage.setScene(scene);
        stage.show();

        CommandCenter.getInstance().setMainStage(stage);


    }
}
