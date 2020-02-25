package net.aclrian.mpe.controller;

import java.util.ArrayList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import net.aclrian.mpe.messdiener.Messdiener;
import net.aclrian.mpe.utils.DateienVerwalter;

public class Select implements Controller {
	public enum Selecter {
		Messdiener;//, Messe;
	}
	@FXML
	ListView<Label> list;
	private Parent p;
	private Selecter sel;
	private MainController mc;

	public Select(Parent p,Selecter sel, MainController mc) {
		this.p = p;
		this.sel =sel;
		this.mc = mc;

	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		//p.getChildrenUnmodifiable().add(list);
	}

	@Override
	public void afterstartup() {
		if (sel == Selecter.Messdiener) {
			ArrayList<Messdiener> data = DateienVerwalter.dv.getAlleMedisVomOrdnerAlsList();
			for (int i = 0; i < data.size(); i++) {
				list.getItems().add(new Label(data.get(i).toString()));
			}
			list.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent mouseEvent) {
					if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
						if (mouseEvent.getClickCount() == 2) {
							int i = list.getSelectionModel().getSelectedIndex();
							mc.changePane(data.get(i));
						}
					}
				}
			});

		}

	}

	@Override
	public boolean isLocked() {
		return false;
	}

}
