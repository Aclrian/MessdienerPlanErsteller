package sample;


import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;


public class ASlider extends Slider {

    public ASlider(){
        this(2000,2020,"Eintritt");
    }


    private ASlider(double min, double max, String s){
        setMin(min);
        setMax(max);
        setBlockIncrement(1);
        //Pane thumb = (Pane) lookup(".thumb");
        //Label label = new Label();
        //label.textProperty().bind(valueProperty().asString());
        //thumb.getChildren().add(label);
        Tooltip t = new Tooltip(s+": " + getValue());
      setOnMouseEntered(e -> {
            Node node =(Node)e.getSource();
            showTolltipppp1(t,node,e);
        });
        setOnMouseClicked(e -> {
            Node node =(Node)e.getSource();
            showTolltipppp1(t,node,e);
        });
        setOnMouseMoved(e -> {
            Node node =(Node)e.getSource();
            showTolltipppp1(t,node,e);
        });
        setOnMousePressed(e -> {
            Node node =(Node)e.getSource();
            showTolltipppp1(t,node,e);
        });

        setOnMouseDragged(e -> {
            Node node =(Node)e.getSource();
            showTolltipppp1(t,node,e);
        });
        setOnMouseExited(e->t.hide());
        setOnMouseDragExited(e->t.hide());
        setOnMouseDragReleased(e-> t.hide());
        setOnMouseReleased(e->t.hide());
        setTooltip(t);
    }
    public void bind(){
        Pane thumb = (Pane) lookup(".thumb");
        Label label = new Label();
        thumb.getChildren().add(label);
        valueProperty().addListener((observableValue, number, t1) -> label.setText("Eintritt: " + (int) getValue()));
        setValue(getMax());
    }

    private void showTolltipppp1(Tooltip t, Node node, MouseEvent e) {
        double d = getValue();
        t.setText("Eintritt: " + ((int)d));
        t.show(node,// ((Node)e.getTarget()).getScene().getWindow().getX()+slider.lookup(".thumb").getBoundsInLocal().getCenterX()
                e.getScreenX()
                        -(lookup(".thumb").getBoundsInParent().getWidth()/2f),
                ((Node)e.getTarget()).getScene().getWindow().getY()+lookup(".thumb").getBoundsInLocal().getCenterY()-
                        lookup(".thumb").getBoundsInLocal().getHeight());
    }
}
