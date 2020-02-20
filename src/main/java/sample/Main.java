package sample;

import com.jfoenix.controls.JFXToggleButton;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception{
        /*URL url = new File("src/main/java/sample/sample.fxml").toURI().toURL();
        Parent root = FXMLLoader.load(url);
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        Slider s = new */
        //Slider slider = new Slider();
        //slider.setId("custom-slider");

        VBox layout = new VBox();
        layout.setId("base-layout");


        Scene scene = new Scene(layout);
        //URL s = Main.class.getClassLoader().getResource("spasst/src/style.css");
       // System.out.println(s);
       // scene.getStylesheets().add(Main.class.getResource("/style.css").toExternalForm());//s.toURI().getPath());
        ASlider a = new ASlider();//2000,2020, "Eintritt");
        stage.setScene(scene);
        layout.getChildren().setAll(a);
        stage.show();
        a.bind();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
