package net.aclrian.fx;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateienVerwalter;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.Arrays;

public class TestATilePane extends ApplicationTest {

    @Mock
    private DateienVerwalter dv;
    @Mock
    private Messdiener medi1;
    @Mock
    private Messdiener medi2;
    @Mock
    private Messdiener medi3;
    private Pane pane;
    private ATilePane instance;

    @Override
    public void start(Stage stage) {
        medi1 = Mockito.mock(Messdiener.class);
        medi2 = Mockito.mock(Messdiener.class);
        medi3 = Mockito.mock(Messdiener.class);
        dv = Mockito.mock(DateienVerwalter.class);

        pane = new Pane();
        Scene scene = new Scene(pane, 10, 10);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void test() {
        DateienVerwalter.setInstance(dv);
        Mockito.when(dv.getMessdiener()).thenReturn(Arrays.asList(medi1, medi2, medi3));
        Mockito.when(medi1.toString()).thenReturn("z");
        Mockito.when(medi2.toString()).thenReturn("b");
        Mockito.when(medi3.toString()).thenReturn("a");
        Platform.runLater(() -> {
            instance = new ATilePane();
            pane.getChildren().add(instance);
        });
        WaitForAsyncUtils.waitForFxEvents();

        Node n1 = instance.getChildrenUnmodifiable().get(0);
        Assertions.assertThat(n1.getClass()).isEqualTo(ATilePane.ACheckBox.class);
        Node n2 = instance.getChildrenUnmodifiable().get(1);
        Assertions.assertThat(n2.getClass()).isEqualTo(ATilePane.ACheckBox.class);
        Node n3 = instance.getChildrenUnmodifiable().get(2);
        Assertions.assertThat(n3.getClass()).isEqualTo(ATilePane.ACheckBox.class);
        if (n1 instanceof ATilePane.ACheckBox && n2 instanceof ATilePane.ACheckBox && n3 instanceof ATilePane.ACheckBox) {
            Assertions.assertThat(((ATilePane.ACheckBox) n1).getMessdiener()).isSameAs(medi3);
            Assertions.assertThat(((ATilePane.ACheckBox) n2).getMessdiener()).isSameAs(medi2);
            Assertions.assertThat(((ATilePane.ACheckBox) n3).getMessdiener()).isSameAs(medi1);

            Platform.runLater(() -> {
                ((ATilePane.ACheckBox) n1).setSelected(true);
                ((ATilePane.ACheckBox) n3).setSelected(true);
            });
            WaitForAsyncUtils.waitForFxEvents();
            Assertions.assertThat(instance.getSelected()).containsExactlyInAnyOrder(medi3, medi1);

            Platform.runLater(() -> {
                ((ATilePane.ACheckBox) n1).setSelected(false);
                ((ATilePane.ACheckBox) n3).setSelected(false);
                instance.setSelected(Arrays.asList(medi1, medi2, medi3));
            });
            WaitForAsyncUtils.waitForFxEvents();

            Assertions.assertThat(instance.getSelected()).containsExactlyInAnyOrder(medi1, medi2, medi3);

        } else {
            Assertions.fail("no ACheckBox");
        }
    }
}