package net.aclrian.fx;


import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import net.aclrian.mpe.messdiener.*;
import net.aclrian.mpe.messdiener.Person;
import net.aclrian.mpe.utils.*;

import java.util.*;

public class ATilePane extends TilePane {

    public ATilePane() {
        super();
        setAlignment(Pos.CENTER);
        setOrientation(Orientation.VERTICAL);
        setMaxHeight(Double.MAX_VALUE);
        setMaxWidth(Double.MAX_VALUE);
        setVgap(5d);
        List<Messdiener>  medis = DateienVerwalter.getInstance().getMessdiener();
        medis.sort(Person.PERSON_COMPARATOR);
        for (Messdiener messdiener : medis) {
            ACheckBox checkBox = new ACheckBox(messdiener);
            checkBox.setMaxWidth(Double.MAX_VALUE);
            checkBox.setStyle("-fx-padding: 0 0 0 10");
            getChildren().add(checkBox);
        }
    }

    public List<Messdiener> getSelected() {
        ArrayList<Messdiener> rtn = new ArrayList<>();
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof ACheckBox acb && acb.isSelected()) {
                rtn.add(acb.getMessdiener());
            }
        }
        return rtn;
    }

    public void setSelected(List<Messdiener> selected) {
        for (Node n : getChildrenUnmodifiable()) {
            if (n instanceof ACheckBox acb) {
                for (Messdiener messdiener : selected) {
                    if (messdiener.toString().equals(acb.getMessdiener().toString())) {
                        ((CheckBox) n).setSelected(true);
                    }
                }
            }

        }
    }

    public static class ACheckBox extends CheckBox {
        private final Messdiener messdiener;

        public ACheckBox(Messdiener m) {
            super(m.toString());
            messdiener = m;
        }

        public Messdiener getMessdiener() {
            return messdiener;
        }
    }
}
