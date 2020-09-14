package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateienVerwalter;
import net.aclrian.mpe.utils.Log;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class TestInfoController extends ApplicationTest {

    @Mock
    DateienVerwalter dv;
    @Mock
    MainController mc;
    private Pane pane;
    private InfoController instance;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 1000, 1000);
        stage.setScene(scene);
        stage.setResizable(true);

        mc = Mockito.mock(MainController.class);
        dv = Mockito.mock(DateienVerwalter.class);
    }

    @Test
    public void test(){
        Platform.runLater(() -> {
            try {
                instance = new InfoController(new Stage());
            } catch (IOException e) {
                Assertions.fail(e.getMessage());
                Log.getLogger().error(e.getMessage(), e);
            }
            instance.start();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Object icon = listWindows().get(0).getScene().lookup("#icon");
            if(icon instanceof ImageView){
                Assertions.assertThat(((ImageView) icon).getImage()).isNotNull();
            } else {
                Assertions.fail("icon not found");
            }
            Object table = listWindows().get(0).getScene().lookup("#table");
            if(table instanceof TableView){
                Assertions.assertThat(((TableView<?>) table).getItems()).isNotNull();
            } else {
                Assertions.fail("table not found");
            }
            Object tools = listWindows().get(0).getScene().lookup("#tools");
            if(tools instanceof ListView){
                Assertions.assertThat(((ListView<?>) tools).getItems()).isNotNull();
                Assertions.assertThat(((ListView<?>) tools).getItems().get(0)).asString().isEqualTo("Java 11");
            } else {
                Assertions.fail("table not found");
            }
        });

        WaitForAsyncUtils.waitForFxEvents();
    }
}