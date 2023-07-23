package net.aclrian.mpe.controller;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.utils.Dialogs;
import net.aclrian.mpe.utils.MPELog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class TestInfoController extends ApplicationTest {

    @Mock
    private Dialogs dialog;
    private InfoController instance;

    @Override
    public void start(Stage stage) {
        Pane pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.setResizable(true);
        dialog = Mockito.mock(Dialogs.class);
    }

    @Test
    void test() {
        Dialogs.setDialogs(dialog);
        Platform.runLater(() -> {
            try {
                instance = new InfoController(new Stage());
            } catch (IOException e) {
                Assertions.fail(e.getMessage(), e);
                MPELog.getLogger().error(e.getMessage(), e);
            }
            instance.start();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Node icon = listWindows().get(0).getScene().lookup("#icon");
            if (icon instanceof ImageView) {
                assertThat(((ImageView) icon).getImage()).isNotNull();
            } else {
                Assertions.fail("icon not found");
            }
            Node table = listWindows().get(0).getScene().lookup("#table");
            if (table instanceof TableView) {
                assertThat(((TableView<?>) table).getItems()).isNotNull();
            } else {
                Assertions.fail("table not found");
            }
            Node tools = listWindows().get(0).getScene().lookup("#tools");
            if (tools instanceof ListView) {
                assertThat(((ListView<?>) tools).getItems()).isNotNull();
                assertThat(((ListView<?>) tools).getItems().get(0)).asString().startsWith("Java 11"); // not isEqualTo because of CI Error
            } else {
                Assertions.fail("table not found");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Node table = listWindows().get(0).getScene().lookup("#table");

            if (table instanceof TableView<?>) {
                ((TableView<?>) table).getSelectionModel().select(0);
                table.fireEvent(new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY, 2, false, false, false, false, false, false, false, false, false, false, null));
                Mockito.verify(dialog, Mockito.times(1)).singleSelect(
                        Mockito.anyList(), Mockito.anyString());
            } else {
                Assertions.fail("table not found");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @DisabledIfEnvironmentVariable(named = "JAVA_HOME", matches = ".*hostedtoolcache.*", disabledReason = "should not run on ci")
    @Test
    void testAwtDesktop() {
        Platform.runLater(() -> {
            try {
                instance = new InfoController(new Stage());
            } catch (IOException e) {
                Assertions.fail(e.getMessage(), e);
                MPELog.getLogger().error(e.getMessage(), e);
            }
            instance.start();
        });
        WaitForAsyncUtils.waitForFxEvents();

        Platform.runLater(() -> {
            Node log = listWindows().get(0).getScene().lookup("#log");
            if (log instanceof Button) {
                ((Button) log).fire();
            } else {
                Assertions.fail("log not found");
            }
            Node quellcode = listWindows().get(0).getScene().lookup("#quellcode");
            if (quellcode instanceof Hyperlink) {
                ((Hyperlink) quellcode).fire();
            } else {
                Assertions.fail("quellcode not found");
            }
            Node mpeWebsite = listWindows().get(0).getScene().lookup("#mpeWebsite");
            if (mpeWebsite instanceof Hyperlink) {
                ((Hyperlink) mpeWebsite).fire();
            } else {
                Assertions.fail("mpeWebsite not found");
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }
}