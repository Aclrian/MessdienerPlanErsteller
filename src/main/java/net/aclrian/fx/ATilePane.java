package net.aclrian.fx;

import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.TilePane;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateienVerwalter;

import java.util.ArrayList;
import java.util.List;

public class ATilePane extends TilePane {

    public ATilePane() {
        setAlignment(Pos.CENTER);
        setOrientation(Orientation.VERTICAL);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        setVgap(5d);
            List<Messdiener> medis = DateienVerwalter.getInstance().getMessdiener();
            medis.sort(Messdiener.compForMedis);
            for (Messdiener messdiener : medis) {
                ACheckBox cb = new ACheckBox(messdiener);
                cb.setMaxWidth(Double.MAX_VALUE);
                cb.setStyle("-fx-padding: 0 0 0 10");
                getChildren().add(cb);
            }
    }

    public List<Messdiener> getSelected() {
        ArrayList<Messdiener> rtn = new ArrayList<>();
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof ACheckBox && ((ACheckBox) n).isSelected())
                rtn.add(((ACheckBox) n).getMessdiener());
        }
        return rtn;
    }

    public void setSelected(List<Messdiener> selected) {
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof ACheckBox) {
                for (Messdiener messdiener : selected) {
                    if (messdiener.toString().equals(((ACheckBox) n).getMessdiener().toString())) {
                        ((CheckBox) n).setSelected(true);
                    }
                }
            }

        }
    }

    public static class ACheckBox extends CheckBox {
        private final Messdiener messdiener;

        public ACheckBox(Messdiener m) {
            super(m.makeId());
            messdiener = m;
        }

        public Messdiener getMessdiener() {
            return messdiener;
        }
    }
}
