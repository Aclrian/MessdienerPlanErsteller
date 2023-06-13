package net.aclrian.mpe.controller;

import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import net.aclrian.mpe.utils.*;
import org.junit.*;
import org.mockito.*;
import org.testfx.framework.junit.*;

public class TestConvertController extends ApplicationTest {

    private DateienVerwalter dv;
    private Pane pane;

    @Override
    public void start(Stage stage) {
        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        dv = Mockito.mock(DateienVerwalter.class);
    }

    @Test
    public void testConversion() {

    }

}
